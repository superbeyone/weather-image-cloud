package com.superbyone.service.impl;

import com.superbyone.config.WeatherConfig;
import com.superbyone.service.WeatherService;
import com.superbyone.vo.FileNameVo;
import com.superbyone.vo.ImageVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author Mr.superbeyone
 * @project weather-image-cloud
 * @className WeatherServiceImpl
 * @description
 * @date 2024-02-28 09:14
 **/

@Slf4j
@Service
public class WeatherServiceImpl implements WeatherService {

    @Resource
    WeatherConfig weatherConfig;

    @Override
    public List<ImageVo> getImageListByDate(String date) {
        String[] dateArray = StringUtils.split(date, "-");
        if (dateArray.length != 3) {
            String format = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            dateArray = StringUtils.split(format, "-");
        }
        String year = dateArray[0];
        String month = dateArray[1];
        String day = dateArray[2];
        return getImageListByDate(year, month, day);
    }


    private List<ImageVo> getImageListByDate(String year, String month, String day) {

        File imageRootDir = new File(weatherConfig.getSaveRootPath(), weatherConfig.getImgDir());
        File dayImageDir = new File(imageRootDir, year + File.separator + month + File.separator + day);

        Map<Long, FileNameVo> fileNameMap = new TreeMap<>();
        if (dayImageDir.isDirectory() && dayImageDir.exists()) {
            File[] imageFiles = dayImageDir.listFiles();

            assert imageFiles != null;
            for (File file : imageFiles) {

                //文件全名
                String fileName = file.getName();
                //去除文件后缀,只有最后的日期字符串的文件名
                String name = StringUtils.substring(fileName, fileName.lastIndexOf("_") + 1, fileName.lastIndexOf("."));
                //截取文件名最后5位

                long nameLong = Long.parseLong(name);

                String hourStr = StringUtils.substring(name, name.length() - 5);
                long hour = Long.parseLong(hourStr) / 100;


                //获取同一个key下最大的那个文件
                FileNameVo fileNameVoInMap = fileNameMap.get(hour);
                if (fileNameVoInMap == null || fileNameVoInMap.getName() < nameLong) {
                    FileNameVo fileNameVo = new FileNameVo();
                    fileNameVo.setName(nameLong);
                    fileNameVo.setFileName(fileName);
                    fileNameMap.put(hour, fileNameVo);
                }


            }

        }
        List<ImageVo> imageVos = new ArrayList<>();

        for (Map.Entry<Long, FileNameVo> entry : fileNameMap.entrySet()) {
            Long key = entry.getKey();
            FileNameVo fileNameVo = entry.getValue();

            ImageVo imageVo = new ImageVo();
            imageVo.setHour(key + "");
            imageVo.setFileName(fileNameVo.getFileName());

            imageVos.add(imageVo);
        }

        return imageVos;
    }
}
