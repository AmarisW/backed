/* Copyright (C) 2020-present Linkcld. All rights reserved. */
package com.gqtian.fileStorage.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

import com.aliyun.oss.OSS;
import com.gqtian.common.globalProcess.GlobalException;
import com.gqtian.fileStorage.config.FileStorageProperties;
import com.gqtian.fileStorage.config.FileStorageProperties.AliYunOssConfig;
import com.gqtian.fileStorage.config.FileStorageProperties.FtpOssConfig;
import com.gqtian.fileStorage.config.FileStorageProperties.HuaweiObsConfig;
import com.gqtian.fileStorage.config.FileStorageProperties.LocalConfig;
import com.gqtian.fileStorage.config.FileStorageProperties.MinioConfig;
import com.gqtian.fileStorage.enums.PlatformEnum;
import com.gqtian.fileStorage.service.factory.AliYunOssClientFactory;
import com.gqtian.fileStorage.service.factory.FtpClientFactory;
import com.gqtian.fileStorage.service.factory.HuaweiObsClientFactory;
import com.gqtian.fileStorage.service.factory.MinioOssClientFactory;
import com.gqtian.fileStorage.service.platform.AliyunOssPlatFrom;
import com.gqtian.fileStorage.service.platform.FtpPlatFrom;
import com.gqtian.fileStorage.service.platform.HuaweiObsPlatFrom;
import com.gqtian.fileStorage.service.platform.LocalPlatFrom;
import com.gqtian.fileStorage.service.platform.MinioOssPlatFrom;
import com.gqtian.fileStorage.utils.Tools;
import com.obs.services.ObsClient;

import cn.hutool.extra.ftp.Ftp;
import io.minio.MinioClient;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

/** 文件存储服务构建器 */
@Slf4j
@Getter
@Setter
@Accessors(chain = true)
public class FileStorageServiceBuilder {
    
    /** 配置参数 */
    private FileStorageProperties properties;
    
    /** Client 工厂列表 */
    private List<FileStorageClientFactory<?>> clientFactoryList = new ArrayList<>();
    
    /** 文件存储平台列表 */
    private List<FileStorageInterface> fileStorageList = new ArrayList<>();
    
    /**
     * 构造函数
     *
     * @param properties 配置参数
     */
    public FileStorageServiceBuilder(FileStorageProperties properties)
    {
        this.properties = properties;
    }
    
    /**
     * 创建一个 FileStorageService 的构造器
     *
     * @param properties 配置参数
     * @return FileStorageServiceBuilder 实例
     */
    public static FileStorageServiceBuilder create(FileStorageProperties properties) {
        return new FileStorageServiceBuilder(properties);
    }
    
    /**
     * 获取或创建指定存储平台的 Client 工厂对象
     *
     * @param platform 平台名称
     * @param factoryList 工厂列表
     * @param defaultSupplier 默认的工厂供应商
     * @param <Client> 客户端类型
     * @return FileStorageClientFactory 实例
     */
    public static <Client> FileStorageClientFactory<Client> getFactory(String platform,
        List<FileStorageClientFactory<?>> factoryList, Supplier<FileStorageClientFactory<Client>> defaultSupplier) {
        
        // TODO 可扩展
        if (factoryList != null) {
            for (FileStorageClientFactory<?> factory : factoryList) {
                if (Objects.equals(platform, factory.getPlatform())) {
                    try {
                        return Tools.cast(factory);
                    }
                    catch (Exception e) {
                        throw new GlobalException("获取 FileStorageClientFactory 失败，类型不匹配，platform：" + platform, e);
                    }
                }
            }
        }
        
        // 返回默认的 FileStorageClientFactory
        return defaultSupplier.get();
    }
    
    /**
     * 创建存储平台时的依赖检查
     *
     * @param object 配置对象
     * @param platformEnum 平台枚举
     */
    public static void buildFileStorageDetect(Object object, PlatformEnum platformEnum) {
        if (Objects.equals(null, object)) {
            return;
        }
        if (doesNotExistClass(platformEnum.getClassPath())) {
            throw new GlobalException("检测到【" + platformEnum.getName() + "】配置，但是没有找到对应的依赖类：【"
                + platformEnum.getClassPath() + "】，所以无法加载此存储平台！");
        }
    }
    
    /**
     * 判断是否没有引入指定 Class
     *
     * @param classNames 类名数组
     * @return 如果没有引入指定的类返回 true，否则返回 false
     */
    public static boolean doesNotExistClass(String... classNames) {
        try {
            for (String className : classNames) {
                Class.forName(className);
            }
            return false;
        }
        catch (ClassNotFoundException e) {
            return true;
        }
    }
    
    /**
     * 创建 FileStorageService 实例
     *
     * @return FileStorageService 实例
     */
    public FileStorageService build() {
        if (properties == null) {
            throw new GlobalException("properties 不能为 null");
        }
        // 初始化各个存储平台
        // 阿里云
        fileStorageList.add(buildAliyunOssFileStorage(properties.getAliYunOss(), clientFactoryList));
        // Minio
        fileStorageList.add(buildMinioFileStorage(properties.getMinio(), clientFactoryList));
        // 华为Obs
        fileStorageList.add(buildHuaweiObsFileStorage(properties.getHuaweiObs(), clientFactoryList));
        // ftp
        fileStorageList.add(buildFtpFileStorage(properties.getFtp(), clientFactoryList));
        // local
        fileStorageList.add(buildLocalFileStorage(properties.getLocal()));
        // 构建 FileStorageService
        FileStorageService fileStorageService = new FileStorageService();
        fileStorageService.setProperties(properties);
        // 去除所有 null 对象
        fileStorageList.removeIf(Objects::isNull);
        fileStorageService.setFileStorageInterfaceList(new CopyOnWriteArrayList<>(fileStorageList));
        return fileStorageService;
    }
    
    private FileStorageInterface buildLocalFileStorage(LocalConfig local) {
        // 检查 FTP 配置是否为空或未启用，如果任一条件满足，则记录日志并返回 null
        if (Objects.equals(local, null) || !local.isEnable()) {
            log.info("未启用本地存储平台：{}", local.getPlatform());
            return null;
        }
        log.info("加载本地存储平台：{}，平台地址：{}", local.getPlatform(), local.getEndPoint());
        return new LocalPlatFrom(local);
    }
    
    /**
     * 构建 FTP 文件存储实例
     *
     * @param ftp FTP 配置信息，包含连接和认证所需的信息
     * @param clientFactoryList 文件存储客户端工厂列表，用于创建特定平台的客户端
     * @return 返回 FTP 文件存储实例，如果未启用或配置无效则返回 null
     */
    private FileStorageInterface buildFtpFileStorage(FtpOssConfig ftp,
        List<FileStorageClientFactory<?>> clientFactoryList) {
        // 检查 FTP 配置是否为空或未启用，如果任一条件满足，则记录日志并返回 null
        if (Objects.equals(ftp, null) || !ftp.isEnable()) {
            log.info("未启用 FTP 存储平台：{}", ftp.getPlatform());
            return null;
        }
        // 如果 FTP 存储平台已启用，记录加载信息并继续创建实例
        log.info("加载 FTP 存储平台：{}，平台地址：{}", ftp.getPlatform(), ftp.getEndPoint());
        // 根据平台类型获取或创建对应的客户端工厂
        FileStorageClientFactory<Ftp> clientFactory =
            getFactory(ftp.getPlatform(), clientFactoryList, () -> new FtpClientFactory(ftp));
        // 使用配置信息和客户端工厂创建并返回 FTP 文件存储实例
        return new FtpPlatFrom(ftp, clientFactory);
    }
    
    /**
     * 构建华为 OBS 文件存储实例
     *
     * @param huaweiObs 华为 OBS 配置信息，包含连接和认证所需的信息
     * @param clientFactoryList 文件存储客户端工厂列表，用于创建特定平台的客户端
     * @return 返回华为 OBS 文件存储实例，如果未启用或配置无效则返回 null
     */
    private FileStorageInterface buildHuaweiObsFileStorage(HuaweiObsConfig huaweiObs,
        List<FileStorageClientFactory<?>> clientFactoryList) {
        // 检查华为 OBS 配置是否为空或未启用，如果任一条件满足，则记录日志并返回 null
        if (Objects.equals(huaweiObs, null) || !huaweiObs.isEnable()) {
            log.info("未启用华为 OBS 存储平台：{}", huaweiObs.getPlatform());
            return null;
        }
        // 如果华为 OBS 存储平台已启用，记录加载信息并继续创建实例
        // buildFileStorageDetect(huaweiObs, PlatformEnum.HUAWEI_OBS);
        log.info("加载华为 OBS 存储平台：{}，平台地址：{}", huaweiObs.getPlatform(), huaweiObs.getEndPoint());
        // 根据平台类型获取或创建对应的客户端工厂
        FileStorageClientFactory<ObsClient> clientFactory =
            getFactory(huaweiObs.getPlatform(), clientFactoryList, () -> new HuaweiObsClientFactory(huaweiObs));
        // 使用配置信息和客户端工厂创建并返回华为 OBS 文件存储实例
        return new HuaweiObsPlatFrom(huaweiObs, clientFactory);
    }
    
    /**
     * 构建 Minio 文件存储实例
     *
     * @param minio Minio 配置信息，包含连接和认证所需的信息
     * @param clientFactoryList 文件存储客户端工厂列表，用于创建特定平台的客户端
     * @return 返回 Minio 文件存储实例，如果未启用或配置无效则返回 null
     */
    private FileStorageInterface buildMinioFileStorage(MinioConfig minio,
        List<FileStorageClientFactory<?>> clientFactoryList) {
        // 检查 Minio 配置是否为空或未启用，如果任一条件满足，则记录日志并返回 null
        if (Objects.equals(minio, null) || !minio.isEnable()) {
            log.info("未启用 Minio 存储平台：{}", minio.getPlatform());
            return null;
        }
        // 如果 Minio 存储平台已启用，记录加载信息并继续创建实例
        // buildFileStorageDetect(minio, PlatformEnum.MINIO);
        log.info("加载 Minio 存储平台：{}，平台地址：{}", minio.getPlatform(), minio.getEndPoint());
        // 根据平台类型获取或创建对应的客户端工厂
        FileStorageClientFactory<MinioClient> clientFactory =
            getFactory(minio.getPlatform(), clientFactoryList, () -> new MinioOssClientFactory(minio));
        // 使用配置信息和客户端工厂创建并返回 Minio 文件存储实例
        return new MinioOssPlatFrom(minio, clientFactory);
    }
    
    /**
     * 构建阿里云 OSS 文件存储
     *
     * @param aliYunOssConfig 阿里云 OSS 配置
     * @param clientFactoryList Client 工厂列表
     * @return AliyunOssPlatFrom 实例
     */
    public static AliyunOssPlatFrom buildAliyunOssFileStorage(AliYunOssConfig aliYunOssConfig,
        List<FileStorageClientFactory<?>> clientFactoryList) {
        if (Objects.equals(aliYunOssConfig, null) || !aliYunOssConfig.isEnable()) {
            log.info("未启用阿里云 OSS 存储平台：{}", aliYunOssConfig.getPlatform());
            return null;
        }
        // buildFileStorageDetect(aliYunOssConfig, PlatformEnum.ALIYUN_OSS);
        log.info("加载阿里云 OSS 存储平台：{}，平台地址：{}", aliYunOssConfig.getPlatform(), aliYunOssConfig.getEndPoint());
        FileStorageClientFactory<OSS> clientFactory = getFactory(aliYunOssConfig.getPlatform(),
            clientFactoryList,
            () -> new AliYunOssClientFactory(aliYunOssConfig));
        return new AliyunOssPlatFrom(aliYunOssConfig, clientFactory);
    }
}
