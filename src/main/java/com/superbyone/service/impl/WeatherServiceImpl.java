package com.superbyone.service.impl;

import com.superbyone.config.WeatherConfig;
import com.superbyone.service.WeatherService;
import com.superbyone.vo.FileNameVo;
import com.superbyone.vo.ImageVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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

        File imageRootDir = new File(weatherConfig.getSaveRootPath(), weatherConfig.getImgDir());
        File dayImageDir = new File(imageRootDir, year + File.separator + month + File.separator + day);

        List<ImageVo> imageListOfToday = getImageListByDate(dayImageDir);
        
        List<ImageVo> imageList = new ArrayList<>(imageListOfToday);

        assembleYesterdayData(date, imageListOfToday, imageRootDir, imageList);
        
        ImageVo middleRangeImage = getMiddleRangeImage(dayImageDir);
        if (middleRangeImage != null) {
            imageList.add(middleRangeImage);
        }
        return imageList;
    }

    private void assembleYesterdayData(String date, List<ImageVo> imageListOfToday, File imageRootDir, List<ImageVo> imageList) {
        if(imageListOfToday.size() < 7){
            LocalDate yesterday = LocalDate.parse(date).plusDays(-1);

            File file = new File(imageRootDir, getNum(yesterday.getYear())
                    + File.separator + getNum(yesterday.getMonthValue())
                    + File.separator + getNum(yesterday.getDayOfMonth()));

            List<ImageVo> imageListByDate = getImageListByDate(file);
            if(!CollectionUtils.isEmpty(imageListByDate)){
                List<String> nameList = imageListOfToday.stream().map(ImageVo::getHour).collect(Collectors.toList());
                List<ImageVo> yeaterDayDataList = imageListByDate.stream().filter(r -> !nameList.contains(r.getHour())).collect(Collectors.toList());
                imageList.addAll(yeaterDayDataList);
            }
        }
    }

    private String getNum(int num){
        if(num>0 &&num<10){
            return "0" + num;
        }
        return num + "";
    }

    private ImageVo getMiddleRangeImage(File dayImageDir) {
        String fileName = weatherConfig.getMiddleRange().getFileName();
        File imageFile = new File(dayImageDir, fileName);
        if (imageFile.exists()) {
            ImageVo imageVo = new ImageVo();
            imageVo.setHour(StringUtils.substringBefore(fileName, "."));
            imageVo.setFileName(fileName);
            String path = getPath(imageFile);
            imageVo.setPath(path);
            return imageVo;
        }
        return null;
    }


    private List<ImageVo> getImageListByDate(File dayImageDir) {


        Map<Long, FileNameVo> fileNameMap = new TreeMap<>();
        if (dayImageDir.isDirectory() && dayImageDir.exists()) {
            File[] imageFiles = dayImageDir.listFiles();

            assert imageFiles != null;
            for (File file : imageFiles) {

                //文件全名
                String fileName = file.getName();
                if(StringUtils.equals(fileName,weatherConfig.getMiddleRange().getFileName())){
                    continue;
                }
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
                    String path = getPath(file);
                    fileNameVo.setPath(path);
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
            imageVo.setPath(fileNameVo.getPath());
            imageVos.add(imageVo);
        }

        return imageVos;
    }

    private String getPath(File file) {
        String path = StringUtils.substringAfter(file.getAbsolutePath(), weatherConfig.getSaveRootPath());
        path = StringUtils.replace(path, "\\", "/");
        return path;
    }
}
