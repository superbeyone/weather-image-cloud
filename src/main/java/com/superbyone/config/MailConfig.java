package com.superbyone.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author Mr.superbeyone
 * @project load-weather-image
 * @className MailConfig
 * @description
 * @date 2024-02-24 17:03
 **/

@Data
@Configuration
@ConfigurationProperties(prefix = "mail")
public class MailConfig {

    private String host;

    private boolean sslEnable;

    private int socketFactoryPort;

    private String title;

    private String from;

    private String pass;
    private List<String> tos;
}
