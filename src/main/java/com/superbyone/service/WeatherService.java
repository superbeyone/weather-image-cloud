package com.superbyone.service;

import com.superbyone.vo.ImageVo;

import java.util.List;

/**
 * @author Mr.superbeyone
 * @project weather-image-cloud
 * @className WeatherService
 * @description
 * @date 2024-02-28 09:14
 **/

public interface WeatherService {


    public List<ImageVo> getImageListByDate(String date);

}
