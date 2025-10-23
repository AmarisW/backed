/** Copyright (c) 2020 linkcld.com to Present. All rights reserved. */
package com.gqtian.hbase.config;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.security.UserGroupInformation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

/**
 * Create on 2021/9/7 10:03
 *
 * @author zhangdz
 */
@org.springframework.context.annotation.Configuration
@ConditionalOnExpression("${hbase.enabled:true}")
@Slf4j
@Order(100)
public class HbaseConfig {

    public static Configuration configuration;

    public static Connection connection;

    public static Admin admin;

    public HbaseConfig() {}

    public synchronized static void init(HbaseProperty hbaseProperty) {
        configuration = HBaseConfiguration.create();
        configuration.set(HConstants.ZOOKEEPER_QUORUM, hbaseProperty.getQuorum());
        configuration.set(HConstants.ZOOKEEPER_CLIENT_PORT, hbaseProperty.getPort().toString());
        configuration.set(HConstants.ZOOKEEPER_ZNODE_PARENT, hbaseProperty.getZnodeParent());
        configuration.set(HConstants.HBASE_CLIENT_IPC_POOL_SIZE, hbaseProperty.getPoolSize().toString());
        // configuration.set(HConstants.CONNECTION_IMPL, AliHBaseUEClusterConnection.class.getName());
        if (!StringUtils.isEmpty(hbaseProperty.getUserName())) {
            configuration.set(HConstants.USERNAME, hbaseProperty.getUserName());
        }
        if (!StringUtils.isEmpty(hbaseProperty.getPassword())) {
            configuration.set(HConstants.PASSWORD, hbaseProperty.getPassword());
        }
        try {
            if (!StringUtils.isEmpty(hbaseProperty.getHadoopUser())) {
                UserGroupInformation userGroupInformation = UserGroupInformation.createRemoteUser("hbase");
                connection = ConnectionFactory.createConnection(configuration, User.create(userGroupInformation));
            } else {
                connection = ConnectionFactory.createConnection(configuration);
            }
            admin = connection.getAdmin();
            log.info("hbase 连接成功");
        } catch (IOException e) {
            log.error("hbase 连接失败,errMsg:{}", e.getMessage(), e);
        }
    }

    /**
     * 操作数据库之后，关闭连接
     */
    @PreDestroy
    public void close() {
        try {
            if (admin != null) {
                admin.close(); // 退出用户
            }
            if (null != connection) {
                connection.close(); // 关闭连接
            }
        } catch (IOException e) {
            log.error("连接关闭失败,errMsg:{}", e.getMessage(), e);
        }
    }
}
