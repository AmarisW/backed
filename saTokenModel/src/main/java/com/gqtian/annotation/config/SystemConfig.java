package com.gqtian.annotation.config;

import org.springframework.context.annotation.Configuration;

import cn.dev33.satoken.strategy.SaStrategy;
import cn.dev33.satoken.util.SaFoxUtil;
import jakarta.annotation.PostConstruct;

/**
 * 🔔 系统配置类
 *
 * @author GQTIAN
 * @date 2025/7/24 17:15
 **/
@Configuration
public class SystemConfig {

    /**
     * 重写 Sa-Token 框架内部算法策略 
     */
    @PostConstruct
    public void rewriteSaStrategy() {
        // 重写 Token 生成策略 例子 gfuPSwZsnUhwgz08GTCH4wOgasWtc3odP4HLwXJ7NDGOximTvT4OlW19zeLH
        SaStrategy.instance.createToken = (loginId, loginType) -> {
            // 随机60位长度字符串
            return SaFoxUtil.getRandomString(60);
        };
    }
}