package com.gqtian.netty;

import com.gqtian.common.config.LaunchApplicationContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


@Slf4j
@EnableCaching // 开启缓存
@SpringBootApplication
public class NettyModelApplication {

  public static void main(String[] args) {
    LaunchApplicationContext.launch(NettyModelApplication.class, args);
  }
}