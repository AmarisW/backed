package com.gqtian.contrast.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.gqtian.common.globalProcess.GlobalException;
import com.gqtian.contrast.demo.ffmpeg.FfmpegService;
import com.gqtian.fileStorage.domain.FileStorageInfo;
import com.gqtian.fileStorage.domain.UploadParam;
import com.gqtian.fileStorage.service.FileStorageService;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/***👻文件控制器**

@className TestController*
@author gqtian*
@date 2025/8/15 00:00*/
@Slf4j
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;
    private final FfmpegService ffmpegService;

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
        } catch (Exception e) {
            log.error("读取文件失败", e);
        } finally {
            try {
                assert inputStream != null;
                inputStream.close();
            } catch (Exception e) {
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
    @RequestMapping("/uploadParam")
    public boolean UploadParam(@ModelAttribute UploadParam uploadParam) {
        FileStorageInfo fileInfo = FileStorageInfo.builder().content(uploadParam.getFile())
            .filename(uploadParam.getFile().getOriginalFilename())
            .originalFilename(uploadParam.getFile().getOriginalFilename()).size(uploadParam.getFile().getSize())
            .fileType(uploadParam.getFile().getContentType()).platform(uploadParam.getPlatform()).build();
        return fileStorageService.upload(fileInfo);
    }

    /**
    * 文件上传
    *
    * @param platform 上传的平台
    * @return 上传结果
    */
    @RequestMapping("/upload")
    public String upload(@RequestParam String platform) {
        try {
            // 时间字符串 yyyy-MM-dd HH:mm:ss
            String format = DateUtil.format(new Date(), "yyyyMMddHHmmss");
            String path = format + StrUtil.SLASH;
            ClassPathResource imageResource = new ClassPathResource("file/test.jpg");
            // 图片资源 - 流
            FileStorageInfo imageStreamBuild = FileStorageInfo.builder().filename(format + "图片流.jpg").platform(platform)
                .path(path).content(imageResource.getInputStream()).build();
            fileStorageService.upload(imageStreamBuild);

            // 图片资源 - 字节
            FileStorageInfo imageByteBuild = FileStorageInfo.builder().filename(format + "图片字节.jpg").platform(platform)
                .path(path).content(toByteArray(imageResource.getInputStream())).build();
            fileStorageService.upload(imageByteBuild);

            ClassPathResource videoResource = new ClassPathResource("file/test.mp4");

            // 视频资源 - 流
            FileStorageInfo videoStreamBuild = FileStorageInfo.builder().path(path).platform(platform)
                .filename(format + "视频流.mp4").content(videoResource.getInputStream()).build();
            fileStorageService.upload(videoStreamBuild);

            // 视频资源 - 字节
            FileStorageInfo videoByteBuild = FileStorageInfo.builder().platform(platform).path(path)
                .filename(format + "视频字节.mp4").content(toByteArray(videoResource.getInputStream())).build();
            fileStorageService.upload(videoByteBuild);
        } catch (Exception e) {
            log.error("文件上传失败：{}", e.getMessage(), e);
            throw new GlobalException(e.getMessage());
        }
        return "测试文件上传成功";
    }

    /** 删除指定文件 */
    @RequestMapping("/delete")
    public String delete(@RequestBody FileStorageInfo fileStorageInfo) {
        try {
            return fileStorageService.delete(fileStorageInfo) ? "删除成功" : "删除失败";
        } catch (Exception e) {
            log.error("文件删除失败：{}", e.getMessage(), e);
            throw new GlobalException(e.getMessage());
        }
    }

    /** 获取预览地址 */
    @RequestMapping("/preview")
    public String preview(@RequestBody FileStorageInfo fileStorageInfo) {
        try {
            return fileStorageService.getPreviewUrl(fileStorageInfo);
        } catch (Exception e) {
            log.error("文件预览失败：{}", e.getMessage(), e);
            throw new GlobalException("文件预览失败!");
        }
    }

    /**
     * 抓拍视频帧数做封面
     */
    @RequestMapping("/capture")
    public String capture() {
        try {
            ffmpegService.startCapture("https://yzzy.play-cdn19.com/20231110/7920_14890de3/index.m3u8");
            return "视频帧数抓取成功";
        } catch (Exception e) {
            log.error("视频帧数抓取失败：{}", e.getMessage(), e);
            throw new GlobalException("视频帧数抓取失败!");
        }
    }

    /**
     * 👻 同时上传JSON参数和多附件
     * <p>
     * 使用 @RequestPart 同时接收 JSON 参数和多个文件
     * </p>
     *
     * @param uploadParam 上传参数（JSON格式）
     * @param files       多个附件文件（多文件上传）
     * @return 上传结果
     */
    @RequestMapping("/uploadWithJsonAndFiles")
    public String uploadWithJsonAndFiles(@RequestPart("uploadParam") UploadParam uploadParam,
        @RequestPart("files") List<MultipartFile> files) {
        try {
            log.info("上传请求 - 平台：{}，文件数量：{}", uploadParam.getPlatform(), files.size());

            StringBuilder resultMsg = new StringBuilder();
            for (int i = 0; i < files.size(); i++) {
                MultipartFile file = files.get(i);
                log.info("文件{}-文件名：{}，大小：{}字节，类型：{}",
                    i + 1, file.getOriginalFilename(), file.getSize(), file.getContentType());

                FileStorageInfo fileInfo = FileStorageInfo.builder()
                    .content(file.getBytes())
                    .filename(file.getOriginalFilename())
                    .originalFilename(file.getOriginalFilename())
                    .size(file.getSize())
                    .fileType(file.getContentType())
                    .platform(uploadParam.getPlatform())
                    .build();

                boolean success = fileStorageService.upload(fileInfo);
                resultMsg.append("文件").append(i + 1).append("(").append(file.getOriginalFilename()).append(")：")
                    .append(success ? "上传成功" : "上传失败").append("；");
            }

            return resultMsg.toString();
        } catch (Exception e) {
            log.error("多文件上传失败：{}", e.getMessage(), e);
            throw new GlobalException("多文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 👻 同时上传头像、个人证件照（多个）和个人信息
     * <p>
     * 使用 @RequestPart 分别接收不同类型的参数和文件
     * </p>
     *
     * @param personalInfo 个人信息的JSON参数
     * @param avatar      头像文件（单张）
     * @param idPhotos    个人证件照（多张）
     * @return 上传结果
     */
    @RequestMapping("/uploadPersonalInfo")
    public String uploadPersonalInfo(@RequestPart("personalInfo") UploadParam personalInfo,
        @RequestPart("avatar") MultipartFile avatar,
        @RequestPart("idPhotos") List<MultipartFile> idPhotos) {
        try {
            log.info("个人信息上传请求 - 平台：{}", personalInfo.getPlatform());
            log.info("头像信息 - 文件名：{}，大小：{}字节，类型：{}",
                avatar.getOriginalFilename(), avatar.getSize(), avatar.getContentType());
            log.info("证件照数量：{}", idPhotos.size());

            StringBuilder resultMsg = new StringBuilder();

            // 1. 上传头像
            FileStorageInfo avatarInfo = FileStorageInfo.builder()
                .content(avatar.getBytes())
                .filename("avatar_" + avatar.getOriginalFilename())
                .originalFilename(avatar.getOriginalFilename())
                .size(avatar.getSize())
                .fileType(avatar.getContentType())
                .platform(personalInfo.getPlatform())
                .path("personal/avatar/")
                .build();
            boolean avatarSuccess = fileStorageService.upload(avatarInfo);
            resultMsg.append("头像：").append(avatarSuccess ? "上传成功" : "上传失败").append("；");

            // 2. 上传证件照（多张）
            for (int i = 0; i < idPhotos.size(); i++) {
                MultipartFile idPhoto = idPhotos.get(i);
                log.info("证件照{}-文件名：{}，大小：{}字节，类型：{}",
                    i + 1, idPhoto.getOriginalFilename(), idPhoto.getSize(), idPhoto.getContentType());

                FileStorageInfo idPhotoInfo = FileStorageInfo.builder()
                    .content(idPhoto.getBytes())
                    .filename("idphoto_" + i + 1 + "_" + idPhoto.getOriginalFilename())
                    .originalFilename(idPhoto.getOriginalFilename())
                    .size(idPhoto.getSize())
                    .fileType(idPhoto.getContentType())
                    .platform(personalInfo.getPlatform())
                    .path("personal/idphoto/")
                    .build();

                boolean photoSuccess = fileStorageService.upload(idPhotoInfo);
                resultMsg.append("证件照").append(i + 1).append("：")
                    .append(photoSuccess ? "上传成功" : "上传失败").append("；");
            }

            log.info("个人信息上传完成：{}", resultMsg);
            return resultMsg.toString();
        } catch (Exception e) {
            log.error("个人信息上传失败：{}", e.getMessage(), e);
            throw new GlobalException("个人信息上传失败：" + e.getMessage());
        }
    }

    /**
     * 👻 同时上传JSON参数和单个附件（简化版）
     *
     * @param uploadParam 上传参数（JSON格式）
     * @param file        单个附件文件
     * @return 上传结果
     */
    @RequestMapping("/uploadWithJsonAndFile")
    public String uploadWithJsonAndFile(@RequestPart("uploadParam") UploadParam uploadParam,
        @RequestPart("file") MultipartFile file) {
        try {
            log.info("上传请求 - 平台：{}，文件名：{}，大小：{}字节",
                uploadParam.getPlatform(), file.getOriginalFilename(), file.getSize());

            FileStorageInfo fileInfo = FileStorageInfo.builder()
                .content(file.getBytes())
                .filename(file.getOriginalFilename())
                .originalFilename(file.getOriginalFilename())
                .size(file.getSize())
                .fileType(file.getContentType())
                .platform(uploadParam.getPlatform())
                .build();

            boolean success = fileStorageService.upload(fileInfo);
            return success ? "上传成功" : "上传失败";
        } catch (Exception e) {
            log.error("文件上传失败：{}", e.getMessage(), e);
            throw new GlobalException("文件上传失败：" + e.getMessage());
        }
    }

    /**
    * 将输入流转换为字节数组
    *
    * @param in 输入流 
    */
    private byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n;
        while ((n = in.read(buffer)) != -1) {
            out.write(buffer, 0, n);
        }
        return out.toByteArray();
    }

}
