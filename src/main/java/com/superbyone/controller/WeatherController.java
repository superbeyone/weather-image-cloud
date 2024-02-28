package com.superbyone.controller;

import com.superbyone.service.WeatherService;
import com.superbyone.vo.ImageVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author Mr.superbeyone
 * @project weather-image-cloud
 * @className WeatherController
 * @description
 * @date 2024-02-28 09:09
 **/

@Slf4j
@RestController
@RequestMapping("/weather/precipitation")
public class WeatherController {

    @Resource
    WeatherService weatherService;

    @RequestMapping("/getImages")
    public List<ImageVo> getImageListByDate(@RequestParam(required = false) String date){

        if(StringUtils.isBlank(date)){
            date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        return weatherService.getImageListByDate(date);
    }
}
