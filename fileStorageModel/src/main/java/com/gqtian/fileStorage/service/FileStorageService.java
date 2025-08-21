/* Copyright (C) 2020-present Linkcld. All rights reserved. */
package com.gqtian.fileStorage.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import com.gqtian.common.globalProcess.GlobalException;
import com.gqtian.fileStorage.config.FileStorageProperties;
import com.gqtian.fileStorage.domain.FileStorageInfo;
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
     * 上传文件到存储平台（优化资源关闭）
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
            // 1. 将内容转换为流（自动处理资源）
            contentToStream(fileStorageInfo);
            
            // 2. 获取存储平台接口
            FileStorageInterface fileStorageInterface = getFileStorage(fileStorageInfo.getPlatform());
            
            // 3. 执行上传
            if (fileStorageInterface.upload(fileStorageInfo)) {
                log.info("【文件服务】文件 {} 上传成功 | 耗时: {}ms",
                    fileStorageInfo.getFilename(),
                    System.currentTimeMillis() - startTime);
                return true;
            }
            else {
                log.error("【文件服务】文件 {} 上传失败 | 耗时: {}ms",
                    fileStorageInfo.getFilename(),
                    System.currentTimeMillis() - startTime);
                return false;
            }
            
        }
        catch (Exception e) {
            log.error("【文件服务】文件 {} 上传失败 | 耗时: {}ms | 错误: {}",
                fileStorageInfo.getFilename(),
                System.currentTimeMillis() - startTime,
                e.getMessage());
            throw new GlobalException("【文件服务】文件上传失败", e);
        }
        
    }
    
    /**
     * 将文件内容转换为流（优化资源安全）
     */
    public void contentToStream(FileStorageInfo fileStorageInfo)
        throws IOException {
        Object content = fileStorageInfo.getContent();
        
        if (content instanceof byte[]) {
            // 字节数组无需关闭
            fileStorageInfo.setInputStream(new ByteArrayInputStream((byte[])content));
        }
        else if (content instanceof MultipartFile file) {
            // 读取MultipartFile到字节数组（自动关闭原始流）
            try (InputStream srcStream = file.getInputStream()) {
                byte[] bytes = IoUtil.readBytes(srcStream);
                fileStorageInfo.setInputStream(new ByteArrayInputStream(bytes));
            }
        }
        else if (content instanceof InputStream) {
            // 直接使用外部传入的流（需调用方保证关闭）
            fileStorageInfo.setInputStream((InputStream)content);
        }
        else {
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
        }
        catch (Exception e) {
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
        }
        catch (Exception e) {
            log.error("【文件服务】文件删除失败", e);
            throw new GlobalException("【文件服务】文件删除失败");
        }
    }
    
    /** 销毁所有存储平台的连接 */
    public void destroy() {
        for (FileStorageInterface fileStorageInterface : fileStorageInterfaceList) {
            try {
                fileStorageInterface.close();
                log.info("【文件服务】销毁存储平台 {} 成功", fileStorageInterface.getPlatform());
            }
            catch (Exception e) {
                log.error("【文件服务】销毁存储平台 {} 失败，{}", fileStorageInterface.getPlatform(), e.getMessage(), e);
            }
        }
    }
    
    public static void main(String[] args) {
        
    }
}
