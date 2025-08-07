package com.gqtian.netty.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author gqtian
 * @description: netty配置信息
 * @date 2018-12-27 12:20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "netty")
public class NettyConfig {


  /**
   * 主线程最大线程数
   */
  private Integer bossMaxThreadCount = 4;

  /**
   * 工作线程最大线程数
   */
  private Integer workMaxThreadCount = 8;

  /**
   * 数据包最大长度
   */
  private Integer maxFrameLength = 655350;

  /**
   * 单节点最大连接数
   */
  private Integer maxConnectNum;

  /**
   * 读事件空闲时间 秒
   */
  private Integer readerIdleTimeSeconds = 60;

  /**
   * 写事件空闲时间 秒
   */
  private Integer writerIdleTimeSeconds = 60;

  /**
   * 读写都空闲事件 秒
   */
  private Integer allIdleTimeSeconds = 60;


  /**
   * netty server 配置
   */
  private NettyServerConfig server;


  /**
   * netty client 配置
   */
  private NettyClientConfig client;


  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class NettyServerConfig {

    /**
     * netty server port
     */
    private Integer port;

  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class NettyClientConfig {

    /**
     * 服务端连接地址
     */
    private String serverIp;

    /**
     * 服务端连接端口
     */
    private Integer serverPort;

  }

}
