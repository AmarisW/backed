package com.gqtian.fileStorage.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gqtian.common.globalProcess.GlobalException;
import com.gqtian.fileStorage.domain.FileStorageInfo;
import com.gqtian.fileStorage.domain.UploadParam;
import com.gqtian.fileStorage.service.FileStorageService;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 👻 文件控制器
 *
 * @className TestController
 * @author gqtian
 * @date 2025/8/15 00:00
 */
@Slf4j
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {
    
    private final FileStorageService fileStorageService;
    
    /**
     * 读取相对路径下面的文件信息
     */
    @RequestMapping("/readFile")
    public void readFile() {
        InputStream inputStream = null;
        try {
            // 读取本地文件
            ClassPathResource classPathResource = new ClassPathResource("file/hello.txt");
            inputStream = classPathResource.getInputStream();
            String result = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            log.info("读取文件内容：{}", result);
        }
        catch (Exception e) {
            log.error("读取文件失败", e);
        }
        finally {
            try {
                assert inputStream != null;
                inputStream.close();
            }
            catch (Exception e) {
                log.error("文件流关闭失败", e);
            }
        }
        
    }
    
    /**
     * 上传文件附件 （文件参数）
     *
     * @param uploadParam 上传参数
     * @return 上传结果
     */
    @RequestMapping("/UploadParam")
    public boolean UploadParam(@ModelAttribute
    UploadParam uploadParam) {
        FileStorageInfo fileInfo = FileStorageInfo.builder()
            .content(uploadParam.getFile())
            .filename(uploadParam.getFile().getOriginalFilename())
            .originalFilename(uploadParam.getFile().getOriginalFilename())
            .size(uploadParam.getFile().getSize())
            .fileType(uploadParam.getFile().getContentType())
            .platform(uploadParam.getPlatform())
            .build();
        return fileStorageService.upload(fileInfo);
    }
    
    /**
     * 文件上传
     *
     * @param platform 上传的平台
     * @return 上传结果
     */
    @RequestMapping("/upload")
    public String upload(@RequestParam
    String platform) {
        try {
            // 时间字符串 yyyy-MM-dd HH:mm:ss
            String format = DateUtil.format(new Date(), "yyyyMMddHHmmss");
            String path = format + StrUtil.SLASH;
            ClassPathResource imageResource = new ClassPathResource("file/test.jpg");
            // 图片资源 - 流
            FileStorageInfo imageStreamBuild = FileStorageInfo.builder()
                .filename(format + "图片流.jpg")
                .platform(platform)
                .path(path)
                .content(imageResource.getInputStream())
                .build();
            fileStorageService.upload(imageStreamBuild);
            
            // 图片资源 - 字节
            FileStorageInfo imageByteBuild = FileStorageInfo.builder()
                .filename(format + "图片字节.jpg")
                .platform(platform)
                .path(path)
                .content(toByteArray(imageResource.getInputStream()))
                .build();
            fileStorageService.upload(imageByteBuild);
            
            ClassPathResource videoResource = new ClassPathResource("file/test.mp4");
            
            // 视频资源 - 流
            FileStorageInfo videoStreamBuild = FileStorageInfo.builder()
                .path(path)
                .platform(platform)
                .filename(format + "视频流.mp4")
                .content(videoResource.getInputStream())
                .build();
            fileStorageService.upload(videoStreamBuild);
            
            // 视频资源 - 字节
            FileStorageInfo videoByteBuild = FileStorageInfo.builder()
                .platform(platform)
                .path(path)
                .filename(format + "视频字节.mp4")
                .content(toByteArray(videoResource.getInputStream()))
                .build();
            fileStorageService.upload(videoByteBuild);
        }
        catch (Exception e) {
            log.error("文件上传失败：{}", e.getMessage(), e);
            throw new GlobalException(e.getMessage());
        }
        return "测试文件上传成功";
    }
    
    /** 删除指定文件 */
    @RequestMapping("/delete")
    public String delete(@RequestBody
    FileStorageInfo fileStorageInfo) {
        try {
            return fileStorageService.delete(fileStorageInfo) ? "删除成功" : "删除失败";
        }
        catch (Exception e) {
            log.error("文件删除失败：{}", e.getMessage(), e);
            throw new GlobalException(e.getMessage());
        }
    }
    
    /** 获取预览地址 */
    @RequestMapping("/preview")
    public String preview(@RequestBody
    FileStorageInfo fileStorageInfo) {
        try {
            return fileStorageService.getPreviewUrl(fileStorageInfo);
        }
        catch (Exception e) {
            log.error("文件预览失败：{}", e.getMessage(), e);
            throw new GlobalException("文件预览失败!");
        }
    }
    
    /**
     * 将输入流转换为字节数组
     *
     * @param in 输入流
     */
    private byte[] toByteArray(InputStream in)
        throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n;
        while ((n = in.read(buffer)) != -1) {
            out.write(buffer, 0, n);
        }
        return out.toByteArray();
    }
    
}
