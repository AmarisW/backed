/* Copyright (C) 2020-present Linkcld. All rights reserved. */
package com.gqtian.fileStorage.config;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

import cn.hutool.extra.ftp.Ftp;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 👻 配置信息
 *
 * @className SpringFileStorageProperties
 * @author gqtian
 * @date 2025/3/20 11:01
 */
@Data
@Accessors(chain = true)
@ConfigurationProperties(prefix = "file-storage")
public class FileStorageProperties {

    /** 默认存储平台 - 本地路径 */
    private String defaultPlatform = "local";

    /** 华为云 OBS */
    private HuaweiObsConfig huaweiObs = new HuaweiObsConfig();

    /** 阿里云 OSS */
    private AliYunOssConfig aliYunOss = new AliYunOssConfig();

    /** MinIO USS */
    private MinioConfig minio = new MinioConfig();

    /** ftp */
    private FtpOssConfig ftp = new FtpOssConfig();

    /** 本地 */
    private LocalConfig local = new LocalConfig();

    /** 基本的存储平台配置 */
    @Data
    @Accessors(chain = true)
    public static class BaseConfig {

        // OSS的启用状态。true表示启用，false表示禁用。
        private boolean enable;

        /** 存储平台 */
        private String platform = "";

        /** 访问密钥 （用户名） */
        private String accessKey;

        /** 访问密钥 （密码） */
        private String secretKey;

        /** 访问域名 （[ip + 端口] | 域名） */
        private String endPoint;

        /** 存储桶名称 */
        private String bucketName;

        /** 访问域名（访问地址前缀 domain+fileName 为实际的访问地址） */
        private String domain = "";

        /** 存储路径 */
        private String basePath = "";

        /**
         * 未实现厂商自有分片时，是否降级为 S3 协议分片。
         * <p>
         * 要求当前平台 endpoint 支持 S3 API，且 accessKey/secretKey/bucket 可用于 S3 签名。
         */
        private boolean s3MultipartFallback = false;

        /** S3 协议区域（分片降级用），默认 us-east-1 */
        private String region = "us-east-1";

        /**
         * S3 path-style 访问（分片降级用）。
         * MinIO 等兼容端点通常为 true。
         */
        private boolean pathStyleAccess = true;
    }

    /** 本地 local */
    @Data
    @Accessors(chain = true)
    @EqualsAndHashCode(callSuper = true)
    public static class LocalConfig extends BaseConfig {

        /** 自动分片上传阈值，达到此大小则使用分片上传，默认 128MB */
        private int multipartThreshold = 128 * 1024 * 1024;

        /** 自动分片上传时每个分片大小，默认 32MB */
        private int multipartPartSize = 32 * 1024 * 1024;
    }

    /** 华为云 OBS */
    @Data
    @Accessors(chain = true)
    @EqualsAndHashCode(callSuper = true)
    public static class HuaweiObsConfig extends BaseConfig {

        /** 默认Acl */
        private String defaultAcl;

        /** 自动分片上传阈值，达到此大小则使用分片上传，默认 128MB */
        private int multipartThreshold = 128 * 1024 * 1024;

        /** 自动分片上传时每个分片大小，默认 32MB */
        private int multipartPartSize = 32 * 1024 * 1024;
    }

    /** 阿里云 OSS */
    @Data
    @Accessors(chain = true)
    @EqualsAndHashCode(callSuper = true)
    public static class AliYunOssConfig extends BaseConfig {

        /** 自动分片上传阈值，达到此大小则使用分片上传，默认 128MB */
        private int multipartThreshold = 128 * 1024 * 1024;

        /** 自动分片上传时每个分片大小，默认 32MB */
        private int multipartPartSize = 32 * 1024 * 1024;
    }

    /** MinIo */
    @Data
    @Accessors(chain = true)
    @EqualsAndHashCode(callSuper = true)
    public static class MinioConfig extends BaseConfig {

        /** 自动分片上传阈值，达到此大小则使用分片上传，默认 128MB */
        private int multipartThreshold = 128 * 1024 * 1024;

        /** 自动分片上传时每个分片大小，默认 32MB */
        private int multipartPartSize = 32 * 1024 * 1024;
    }

    /** Ftp */
    @Data
    @Accessors(chain = true)
    @EqualsAndHashCode(callSuper = true)
    public static class FtpOssConfig extends BaseConfig {

        /** 编码，默认UTF-8 */
        private Charset charset = StandardCharsets.UTF_8;

        /** 连接超时时长，单位毫秒，默认10秒 {@link org.apache.commons.net.SocketClient#setConnectTimeout(int)} */
        private int connectionTimeout = 10 * 1000;

        /** Socket连接超时时长，单位毫秒，默认10秒 {@link org.apache.commons.net.SocketClient#setSoTimeout(int)} */
        private long soTimeout = 10 * 1000;

        /**
         * 设置服务器语言，默认空，{@link org.apache.commons.net.ftp.FTPClientConfig#setServerLanguageCode(String)}
         */
        private String serverLanguageCode;

        /**
         * 服务器标识，默认空，{@link org.apache.commons.net.ftp.FTPClientConfig#FTPClientConfig(String)}
         * 例如：org.apache.commons.net.ftp.FTPClientConfig.SYST_NT
         */
        private String systemKey;

        /** 是否主动模式，默认被动模式 */
        private Boolean isActive = false;

        /** Client 对象池配置 */
        private CommonClientPoolConfig<Ftp> pool = new CommonClientPoolConfig<>();

        /** 其它自定义配置 */
        private Map<String, Object> attr = new LinkedHashMap<>();
    }

    /** 通用的 Client 对象池配置，详情见 {@link GenericObjectPoolConfig} */
    @Data
    @Accessors(chain = true)
    public static class CommonClientPoolConfig<T> {

        /** 取出对象前进行校验，默认开启 */
        private Boolean testOnBorrow = true;

        /** 空闲检测，默认开启 */
        private Boolean testWhileIdle = true;

        /** 最大总数量，超过此数量会进行阻塞等待，默认 16 */
        private Integer maxTotal = 16;

        /** 最大空闲数量，默认 4 */
        private Integer maxIdle = 4;

        /** 最小空闲数量，默认 1 */
        private Integer minIdle = 1;

        public GenericObjectPoolConfig<T> toGenericObjectPoolConfig() {
            GenericObjectPoolConfig<T> config = new GenericObjectPoolConfig<>();
            config.setTestOnBorrow(testOnBorrow);
            config.setTestWhileIdle(testWhileIdle);
            config.setMaxTotal(maxTotal);
            config.setMinIdle(minIdle);
            config.setMaxIdle(maxIdle);
            return config;
        }
    }
}
