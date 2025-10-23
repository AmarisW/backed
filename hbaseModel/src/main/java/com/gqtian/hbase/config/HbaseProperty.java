/** Copyright (c) 2020 linkcld.com to Present. All rights reserved. */
package com.gqtian.hbase.config;

import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * Create on 2021/9/7 11:01
 *
 * @author zhangdz
 */
@Configuration
@ConditionalOnExpression("${hbase.enabled:true}")
@ConfigurationProperties(prefix = "hbase.zookeeper")
@Data
public class HbaseProperty
{
    
    private String quorum;
    
    private Integer port;
    
    private String znodeParent;
    
    private Integer poolSize;
    
    private String userName;
    
    private String password;
    
    @Value("${hbase.hadoop.user}")
    private String hadoopUser;
    
    public Properties getProperties()
    {
        Properties properties = new Properties();
        if (StringUtils.isNotBlank(this.getQuorum()))
        {
            properties.setProperty("hbase.zookeeper.quorum", this.getQuorum());
        }
        
        if (this.getPort() != null)
        {
            properties.setProperty("hbase.zookeeper.property.clientPort", this.getPort().toString());
        }
        
        if (StringUtils.isNotBlank(this.getZnodeParent()))
        {
            properties.setProperty("zookeeper.znode.parent", this.getZnodeParent());
        }
        
        if (this.getPoolSize() != null)
        {
            properties.setProperty("hbase.client.ipc.pool.size", this.getPoolSize().toString());
        }
        if (this.getUserName() != null)
        {
            properties.setProperty("hbase.client.username", this.getUserName());
        }
        if (this.getPassword() != null)
        {
            properties.setProperty("hbase.client.password", this.getPassword());
        }
        return properties;
    }
    
}
