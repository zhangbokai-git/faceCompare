package org.jeecg.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author: zc
 * @date: 2019年12月13日15:23:49
 * @description: keycloak 配置类
 */

@Component
@ConfigurationProperties(prefix = "keycloak-master",ignoreUnknownFields = false)
@Data
public class KeyCloakConfig {

    private String authServerUrl;

    private String realm;

    private String username;

    private String password;

    private String clientId;

}
