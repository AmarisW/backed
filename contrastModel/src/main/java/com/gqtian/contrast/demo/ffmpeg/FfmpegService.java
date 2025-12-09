package com.gqtian.contrast.demo.ffmpeg;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.stereotype.Service;

import com.gqtian.fileStorage.domain.FileStorageInfo;
import com.gqtian.fileStorage.service.FileStorageService;

import cn.hutool.core.date.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 🤨 视频功能测试
 * @className FfmpegService
 * @author gqtian
 * @date 2025/11/28 14:31
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FfmpegService {

    private static final String RTSP_TRANSPORT = "rtsp_transport";
    private static final String TCP = "tcp";
    private static final String IMAGE_FORMAT = "png";

    private final FileStorageService fileStorageService;

    /**
     * 开始截图并上传到文件存储
     * @param url 视频流地址（支持RTSP、HTTP等协议）
     */
    public void startCapture(String url) {
        try (FFmpegFrameGrabber grabber = FFmpegFrameGrabber.createDefault(url);
             Java2DFrameConverter converter = new Java2DFrameConverter()) {
            
            // 配置RTSP流读取协议为TCP（避免UDP丢包）
            grabber.setOption(RTSP_TRANSPORT, TCP);
            grabber.startUnsafe(false);

            Frame frame = grabber.grabImage();
            if (frame == null) {
                log.warn("未能从视频流中抓取到帧：{}", url);
                return;
            }

            // 转换帧为图片并上传
            BufferedImage image = converter.getBufferedImage(frame);
            String timestamp = DateUtil.format(DateUtil.date(), "yyyyMMddHHmmss");
            uploadImage(image, timestamp);
            
            log.info("截图成功：{}", timestamp + "图片." + IMAGE_FORMAT);
        } catch (Exception e) {
            log.error("截图异常，视频地址：{}", url, e);
        }
    }

    /**
     * 上传图片到文件存储
     */
    private void uploadImage(BufferedImage image, String timestamp) throws Exception {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, IMAGE_FORMAT, outputStream);
            
            FileStorageInfo fileInfo = FileStorageInfo.builder()
                    .filename(timestamp + "图片." + IMAGE_FORMAT)
                    .path(timestamp + "/")
                    .content(new ByteArrayInputStream(outputStream.toByteArray()))
                    .build();
            
            fileStorageService.upload(fileInfo);
        }
    }
}
