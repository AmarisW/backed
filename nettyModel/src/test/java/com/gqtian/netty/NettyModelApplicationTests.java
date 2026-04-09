package com.gqtian.netty;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

@SpringBootTest
class NettyModelApplicationTests {

    public static void main(String[] args) throws Exception {
        Network network;
        network = Network.builder().ip("192.168.2.206").port(2019).build();
        // 读取本地文件
        ClassPathResource classPathResource = new ClassPathResource("file/2019/1412P19Jtb.txt");
        InputStream inputStream = classPathResource.getInputStream();
        String msg = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        NettyClientTest nettyClient = new NettyClientTest();
        nettyClient.runClient(network.getIp(), network.getPort(), msg.replace(" ", ""));
    }
}
