package com.gqtian.netty.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gqtian.netty.server.NettyServer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 👻 Netty控制器
 * 
 * @className NettyWeb
 * @author gqtian
 * @date 2025/8/7 13:23
 */
@Slf4j
@RestController
@RequestMapping("/server")
@RequiredArgsConstructor
public class NettyServerWeb {

    private final NettyServer nettyServer;

    /**
     * 启动服务端
     */
    @RequestMapping("/start")
    public void startServer() {
        nettyServer.runServer();
    }

    /**
     * 关闭服务端
     */
    @RequestMapping("/stop")
    public void stopServer() {
        nettyServer.stopServer();
    }

}
