/* Copyright (C) 2020-present Linkcld. All rights reserved. */
package com.gqtian.fileStorage.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import com.gqtian.common.globalProcess.GlobalException;
import com.gqtian.fileStorage.config.FileStorageProperties;
import com.gqtian.fileStorage.domain.FileStorageInfo;
import com.gqtian.fileStorage.domain.MultipartInitResult;
import com.gqtian.fileStorage.domain.MultipartPartETag;
import com.gqtian.fileStorage.utils.Tools;

import cn.hutool.core.io.IoUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/** 用来处理文件存储，对接多个平台 */
@Slf4j
@Data
public class FileStorageService {

    private FileStorageProperties properties;

    private CopyOnWriteArrayList<FileStorageInterface> fileStorageInterfaceList;

    /**
     * 获取对应的存储平台
     *
     * @param platform 存储平台名称
     * @return 对应的文件存储平台实例，如果找不到则返回null
     */
    public <T extends FileStorageInterface> T getFileStorage(String platform) {
        FileStorageInterface platformFileStorageInterface = null;
        // 未传入平台时 使用默认平台
        if (StringUtils.isEmpty(platform)) {
            platform = properties.getDefaultPlatform();
        }
        if (CollectionUtils.isEmpty(fileStorageInterfaceList)) {
            throw new GlobalException("【文件服务】未配置存储平台或平台未启用，请检查配置文件参数信息！");
        }

        for (FileStorageInterface fileStorageInterface : fileStorageInterfaceList) {
            if (fileStorageInterface.getPlatform().equalsIgnoreCase(platform)) {
                platformFileStorageInterface = fileStorageInterface;
                // 找到匹配平台 直接结束循环
                break;
            }
        }
        if (platformFileStorageInterface == null) {
            log.error("【文件服务】平台编码：{} 未匹配到存储平台实现，请检查配置文件参数信息！", platform);
            throw new GlobalException("【文件服务】平台编码：" + platform + "未匹配到存储平台实现，请检查配置文件参数信息！");
        }
        return Tools.cast(platformFileStorageInterface);
    }

    /**
     * 上传文件到存储平台（优化资源关闭）。
     * <p>
     * 若平台启用了 S3 分片降级（{@code s3ProtocolSupport != null}），则本次上传走 S3 协议分片，
     * 并在执行过程中打印 {@code [S3降级]} 日志；否则走各平台自有直传。
     *
     * @param fileStorageInfo 文件信息对象
     * @return 上传成功返回true，否则false
     */
    public boolean upload(FileStorageInfo fileStorageInfo) {
        if (fileStorageInfo == null) {
            log.error("【文件服务】文件信息为空");
            return false;
        }

        long startTime = System.currentTimeMillis();
        try {
            // 1. 将内容转换为流，并尽量回填 size 便于日志与分片
            contentToStream(fileStorageInfo);
            fillSizeIfAbsent(fileStorageInfo);

            // 2. 获取存储平台
            FileStorageInterface storage = getFileStorage(fileStorageInfo.getPlatform());
            String platform = storage.getPlatform();
            Long size = fileStorageInfo.getSize();

            // 3. 已挂载 S3 降级：upload 执行时直接走 S3 分片，并打执行日志
            if (storage.s3ProtocolSupport() != null) {
                log.info(
                    "【文件服务】[S3降级] 执行上传 | platform={} | file={} | path={} | size={}B | partSize={}B",
                    platform, fileStorageInfo.getFilename(), fileStorageInfo.getPath(), size,
                    storage.s3ProtocolSupport().getMultipartPartSize());
                boolean success = uploadByMultipart(fileStorageInfo, 0);
                log.info("【文件服务】[S3降级] 上传{} | platform={} | file={} | size={}B | 耗时: {}ms",
                    success ? "成功" : "失败", platform, fileStorageInfo.getFilename(), size,
                    System.currentTimeMillis() - startTime);
                return success;
            }

            // 4. 平台自有直传
            log.info("【文件服务】执行上传 | 方式=平台直传 | platform={} | file={} | path={} | size={}B", platform,
                fileStorageInfo.getFilename(), fileStorageInfo.getPath(), size);
            if (storage.upload(fileStorageInfo)) {
                log.info("【文件服务】上传成功 | 方式=平台直传 | platform={} | file={} | 耗时: {}ms", platform,
                    fileStorageInfo.getFilename(), System.currentTimeMillis() - startTime);
                return true;
            }
            log.error("【文件服务】上传失败 | 方式=平台直传 | platform={} | file={} | 耗时: {}ms", platform,
                fileStorageInfo.getFilename(), System.currentTimeMillis() - startTime);
            return false;

        } catch (Exception e) {
            log.error("【文件服务】上传失败 | file={} | 耗时: {}ms | 错误: {}", fileStorageInfo.getFilename(),
                System.currentTimeMillis() - startTime, e.getMessage(), e);
            throw new GlobalException("【文件服务】文件上传失败", e);
        }
    }

    /**
     * 根据 content / inputStream 尽量填充 size（不影响业务字段已有值）。
     */
    private void fillSizeIfAbsent(FileStorageInfo fileStorageInfo) {
        if (fileStorageInfo.getSize() != null && fileStorageInfo.getSize() >= 0) {
            return;
        }
        Object content = fileStorageInfo.getContent();
        if (content instanceof byte[] bytes) {
            fileStorageInfo.setSize((long)bytes.length);
            return;
        }
        if (content instanceof MultipartFile file) {
            fileStorageInfo.setSize(file.getSize());
            return;
        }
        try {
            if (fileStorageInfo.getInputStream() != null) {
                int available = fileStorageInfo.getInputStream().available();
                if (available > 0) {
                    fileStorageInfo.setSize((long)available);
                }
            }
        } catch (Exception ignored) {
            // size 仅用于日志与策略，解析失败不阻断上传
        }
    }

    /**
     * 将文件内容转换为流（优化资源安全）
     */
    public void contentToStream(FileStorageInfo fileStorageInfo) throws IOException {
        Object content = fileStorageInfo.getContent();

        if (content instanceof byte[]) {
            // 字节数组无需关闭
            fileStorageInfo.setInputStream(new ByteArrayInputStream((byte[])content));
        } else if (content instanceof MultipartFile file) {
            // 读取MultipartFile到字节数组（自动关闭原始流）
            try (InputStream srcStream = file.getInputStream()) {
                byte[] bytes = IoUtil.readBytes(srcStream);
                fileStorageInfo.setInputStream(new ByteArrayInputStream(bytes));
            }
        } else if (content instanceof InputStream) {
            // 直接使用外部传入的流（需调用方保证关闭）
            fileStorageInfo.setInputStream((InputStream)content);
        } else {
            throw new GlobalException("【文件服务】不支持的文件类型: " + content.getClass().getSimpleName());
        }
    }

    /**
     * 获取文件预览地址
     *
     * @return 文件预览地址
     */
    public String getPreviewUrl(FileStorageInfo fileStorageInfo) {
        try {
            FileStorageInterface fileStorageInterface = getFileStorage(fileStorageInfo.getPlatform());
            return fileStorageInterface.getPreviewUrl(fileStorageInfo);
        } catch (Exception e) {
            log.error("【文件服务】文件：{} 文件预览失败：{}", fileStorageInfo.getFilename(), e.getMessage(), e);
            throw new GlobalException("【文件服务】文件：" + fileStorageInfo.getFilename() + "文件预览失败!");
        }
    }

    /**
     * 删除文件
     *
     * @return 删除成功返回true，否则返回false
     */
    public boolean delete(FileStorageInfo fileStorageInfo) {
        try {
            FileStorageInterface fileStorageInterface = getFileStorage(fileStorageInfo.getPlatform());
            return fileStorageInterface.delete(fileStorageInfo);
        } catch (Exception e) {
            log.error("【文件服务】文件删除失败", e);
            throw new GlobalException("【文件服务】文件删除失败");
        }
    }

    // ========================= 分片上传 =========================

    /**
     * 初始化分片上传。
     *
     * @param fileStorageInfo 文件信息（platform / path / filename 或 fileType）
     * @return 分片会话信息（含 uploadId）
     */
    public MultipartInitResult initiateMultipartUpload(FileStorageInfo fileStorageInfo) {
        FileStorageInterface storage = getFileStorage(fileStorageInfo.getPlatform());
        if (!storage.supportMultipartUpload()) {
            throw storage.unsupportedMultipart();
        }
        String mode = resolveMultipartMode(storage);
        log.info("【文件服务】分片初始化 | platform={} | mode={} | file={} | path={}", storage.getPlatform(), mode,
            fileStorageInfo.getFilename(), fileStorageInfo.getPath());
        // 将 init 解析出的文件名回写，便于后续 part/complete 使用同一 key
        MultipartInitResult result = storage.initiateMultipartUpload(fileStorageInfo);
        if (StringUtils.isNotBlank(result.getFilename())
            && StringUtils.isBlank(fileStorageInfo.getFilename())) {
            fileStorageInfo.setFilename(result.getFilename());
        }
        log.info("【文件服务】分片初始化完成 | platform={} | mode={} | uploadId={} | objectKey={}", storage.getPlatform(),
            mode, result.getUploadId(), result.getObjectKey());
        return result;
    }

    /**
     * 上传单个分片。
     * <p>
     * 分片数据优先取 {@code partData}；若为空则从 fileStorageInfo.content / inputStream 转换。
     *
     * @param fileStorageInfo 文件定位信息（需与 init 的 path、filename 一致）
     * @param uploadId 分片会话 ID
     * @param partNumber 分片序号，从 1 开始
     * @param partData 分片流（可空，空则走 contentToStream）
     * @param partSize 分片字节数
     * @return 分片 ETag
     */
    public MultipartPartETag uploadPart(FileStorageInfo fileStorageInfo, String uploadId, int partNumber,
        InputStream partData, long partSize) {
        FileStorageInterface storage = getFileStorage(fileStorageInfo.getPlatform());
        if (!storage.supportMultipartUpload()) {
            throw storage.unsupportedMultipart();
        }
        String mode = resolveMultipartMode(storage);
        log.info("【文件服务】分片上传 | platform={} | mode={} | partNumber={} | partSize={}B | uploadId={}",
            storage.getPlatform(), mode, partNumber, partSize, uploadId);
        try {
            InputStream dataStream = partData;
            if (dataStream == null) {
                contentToStream(fileStorageInfo);
                dataStream = fileStorageInfo.getInputStream();
            }
            MultipartPartETag partETag =
                storage.uploadPart(fileStorageInfo, uploadId, partNumber, dataStream, partSize);
            log.info("【文件服务】分片上传完成 | platform={} | mode={} | partNumber={} | etag={}", storage.getPlatform(),
                mode, partNumber, partETag.getEtag());
            return partETag;
        } catch (GlobalException e) {
            throw e;
        } catch (Exception e) {
            log.error("【文件服务】分片上传失败 | mode={} | partNumber={} | uploadId={}", mode, partNumber, uploadId, e);
            throw new GlobalException("【文件服务】分片上传失败", e);
        }
    }

    /**
     * 合并分片完成上传。
     *
     * @param fileStorageInfo 文件定位信息
     * @param uploadId 分片会话 ID
     * @param parts 已上传分片 ETag 列表
     * @return 是否成功
     */
    public boolean completeMultipartUpload(FileStorageInfo fileStorageInfo, String uploadId,
        List<MultipartPartETag> parts) {
        FileStorageInterface storage = getFileStorage(fileStorageInfo.getPlatform());
        if (!storage.supportMultipartUpload()) {
            throw storage.unsupportedMultipart();
        }
        String mode = resolveMultipartMode(storage);
        int partCount = parts == null ? 0 : parts.size();
        log.info("【文件服务】分片合并 | platform={} | mode={} | uploadId={} | parts={}", storage.getPlatform(), mode,
            uploadId, partCount);
        boolean success = storage.completeMultipartUpload(fileStorageInfo, uploadId, parts);
        log.info("【文件服务】分片合并完成 | platform={} | mode={} | uploadId={} | success={}", storage.getPlatform(),
            mode, uploadId, success);
        return success;
    }

    /**
     * 取消分片上传。
     *
     * @param fileStorageInfo 文件定位信息
     * @param uploadId 分片会话 ID
     * @return 是否成功
     */
    public boolean abortMultipartUpload(FileStorageInfo fileStorageInfo, String uploadId) {
        FileStorageInterface storage = getFileStorage(fileStorageInfo.getPlatform());
        if (!storage.supportMultipartUpload()) {
            throw storage.unsupportedMultipart();
        }
        String mode = resolveMultipartMode(storage);
        log.info("【文件服务】分片取消 | platform={} | mode={} | uploadId={}", storage.getPlatform(), mode, uploadId);
        boolean success = storage.abortMultipartUpload(fileStorageInfo, uploadId);
        log.info("【文件服务】分片取消完成 | platform={} | mode={} | uploadId={} | success={}", storage.getPlatform(),
            mode, uploadId, success);
        return success;
    }

    /**
     * 服务端将整文件按固定分片大小自动分片上传（适合后端持有完整流的大文件场景）。
     * <p>
     * 失败时会尝试 abort 已创建的分片会话。
     *
     * @param fileStorageInfo 文件信息（需 content 或 inputStream，建议设置 size）
     * @param partSize 分片大小；&lt;=0 时使用平台默认（MinIO 配置 multipartPartSize）
     * @return 是否成功
     */
    public boolean uploadByMultipart(FileStorageInfo fileStorageInfo, long partSize) {
        if (fileStorageInfo == null) {
            log.error("【文件服务】文件信息为空");
            return false;
        }
        FileStorageInterface storage = getFileStorage(fileStorageInfo.getPlatform());
        if (!storage.supportMultipartUpload()) {
            throw storage.unsupportedMultipart();
        }

        long resolvedPartSize = partSize > 0 ? partSize : resolveDefaultPartSize(storage);
        if (resolvedPartSize <= 0) {
            throw new GlobalException("【文件服务】分片大小无效，请指定 partSize 或配置 multipartPartSize");
        }

        String mode = resolveMultipartMode(storage);
        MultipartInitResult initResult = null;
        List<MultipartPartETag> uploadedParts = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        log.info("【文件服务】自动分片上传开始 | platform={} | mode={} | file={} | partSize={}B", storage.getPlatform(),
            mode, fileStorageInfo.getFilename(), resolvedPartSize);
        try {
            contentToStream(fileStorageInfo);
            initResult = initiateMultipartUpload(fileStorageInfo);
            // 保证后续 complete 使用同一 filename
            fileStorageInfo.setFilename(initResult.getFilename());

            try (InputStream inputStream = fileStorageInfo.getInputStream()) {
                byte[] buffer = new byte[(int)Math.min(resolvedPartSize, Integer.MAX_VALUE)];
                int partNumber = 1;
                int read;
                while ((read = readFully(inputStream, buffer)) > 0) {
                    ByteArrayInputStream partStream = new ByteArrayInputStream(buffer, 0, read);
                    MultipartPartETag partETag =
                        storage.uploadPart(fileStorageInfo, initResult.getUploadId(), partNumber, partStream, read);
                    uploadedParts.add(partETag);
                    partNumber++;
                }
            }

            if (uploadedParts.isEmpty()) {
                throw new GlobalException("【文件服务】文件内容为空，无法分片上传");
            }

            boolean success =
                completeMultipartUpload(fileStorageInfo, initResult.getUploadId(), uploadedParts);
            log.info("【文件服务】自动分片上传{} | platform={} | mode={} | file={} | parts={} | 耗时: {}ms",
                success ? "成功" : "失败", storage.getPlatform(), mode, fileStorageInfo.getFilename(),
                uploadedParts.size(), System.currentTimeMillis() - startTime);
            return success;
        } catch (Exception e) {
            log.error("【文件服务】自动分片上传失败 | file={}", fileStorageInfo.getFilename(), e);
            if (initResult != null && StringUtils.isNotBlank(initResult.getUploadId())) {
                try {
                    abortMultipartUpload(fileStorageInfo, initResult.getUploadId());
                } catch (Exception abortEx) {
                    log.warn("【文件服务】自动分片失败后 abort 异常 | uploadId={}", initResult.getUploadId(), abortEx);
                }
            }
            if (e instanceof GlobalException globalException) {
                throw globalException;
            }
            throw new GlobalException("【文件服务】自动分片上传失败", e);
        }
    }

    /**
     * 尽量读满 buffer，返回实际读取字节数；到流末尾且无数据时返回 -1。
     */
    private int readFully(InputStream inputStream, byte[] buffer) throws IOException {
        int offset = 0;
        while (offset < buffer.length) {
            int read = inputStream.read(buffer, offset, buffer.length - offset);
            if (read < 0) {
                return offset == 0 ? -1 : offset;
            }
            offset += read;
        }
        return offset;
    }

    /** 从具体平台实现上读取默认分片大小 */
    private long resolveDefaultPartSize(FileStorageInterface storage) {
        if (storage instanceof com.gqtian.fileStorage.service.platform.MinioOssPlatFrom minio) {
            return minio.getMultipartPartSize();
        }
        // 走 S3 协议降级的平台
        if (storage.s3ProtocolSupport() != null) {
            return storage.s3ProtocolSupport().getMultipartPartSize();
        }
        return 0;
    }

    /**
     * 解析分片实现方式，便于日志区分。
     * <ul>
     *   <li>S3降级：平台未自有分片，走 {@code s3ProtocolSupport}</li>
     *   <li>平台自有：如 MinIO 自有 multipart 实现</li>
     * </ul>
     */
    private String resolveMultipartMode(FileStorageInterface storage) {
        return storage.s3ProtocolSupport() != null ? "S3降级" : "平台自有";
    }

    /** 销毁所有存储平台的连接 */
    public void destroy() {
        for (FileStorageInterface fileStorageInterface : fileStorageInterfaceList) {
            try {
                fileStorageInterface.close();
                log.info("【文件服务】销毁存储平台 {} 成功", fileStorageInterface.getPlatform());
            } catch (Exception e) {
                log.error("【文件服务】销毁存储平台 {} 失败，{}", fileStorageInterface.getPlatform(), e.getMessage(), e);
            }
        }
    }
}
