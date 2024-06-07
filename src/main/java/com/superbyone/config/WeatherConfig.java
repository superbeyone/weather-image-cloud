package com.superbyone.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Mr.superbeyone
 * @project load-weather-image
 * @className WeatherConfig
 * @description
 * @date 2024-02-24 16:08
 **/

@Data
@Configuration
@ConfigurationProperties(prefix = "weather")
public class WeatherConfig {

    private String cron;

    private String baseUrl;

    private String saveRootPath;

    private String urlClassName;
    private String imgIdName;
    private ImgCropRange imgCropRange;

    private MiddleRange middleRange;

    private String imgDir = "image";
    private String imgCropDir = "cropImage";


    public String getImgIdName() {
        return "#" + imgIdName;
    }
}
