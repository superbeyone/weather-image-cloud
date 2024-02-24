package com.superbyone.utils;

import cn.hutool.core.img.ImgUtil;
import com.superbyone.config.ImgCutRange;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.*;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Mr.superbeyone
 * @project load-weather-image
 * @className WeatherUtil
 * @description
 * @date 2024-02-23 18:23
 **/
@Slf4j
public class WeatherUtil {

//    private static final String A_HREF_CLASS_NAME = ".container .row .col-xs-10 .bgwhite .p-wrap .p-nav.nav2 ul li a";
//    private static final String IMG_CLASS_NAME = "#imgpath";

    public static List<String> getPageUrlList(String baseUrl, String urlClassName) {
        List<String> urlList = new ArrayList<>();
        try {

            Document doc = Jsoup.connect(baseUrl).get();
            // 使用选择器语法查找元素
            Elements newsHeadlines = doc.select(urlClassName);
            for (Element headline : newsHeadlines) {
                // 获取元素的链接地址
                String href = headline.absUrl("href");
                // 打印
                urlList.add(href);
            }
        } catch (Exception e) {
            log.error("getPageUrlList error ", e);
        }
        return urlList;
    }


    public static List<String> getImageUrl(String url, String imageIdName) throws Exception {
        List<String> urlList = new ArrayList<>();

        Document doc = Jsoup.connect(url).get();
        log.info(doc.title());
        // 使用选择器语法查找元素
        Elements newsHeadlines = doc.select(imageIdName);

        for (Element newsHeadline : newsHeadlines) {
            String imgPath = newsHeadline.attr("src");
            log.info("url: {} \n imgPath: {}", url, imgPath);
            urlList.add(imgPath);
        }

        return urlList;
    }

    public static File saveImage(String rootPath, String imgUrl) {

        String fileNameStr = StringUtils.substringAfterLast(imgUrl, "/");
        String fileName = StringUtils.substringBefore(fileNameStr, "?");
        log.info("saveImage fileName: {}, imgUrl {}", fileName, imgUrl);

        String year = StringUtils.substringBetween(imgUrl, "product/", "/");
        File root = new File(rootPath, year);
        if (!root.exists()) {
            root.mkdirs();
        }
        File file = new File(root, fileName);

        if (file.exists()) {
            log.info("{} 文件已存在，url {}", fileName, imgUrl);
            return null;
        }
        byte[] buffer = new byte[4096];
        int bytesRead;
        try (BufferedOutputStream outputStream = new BufferedOutputStream(Files.newOutputStream(file.toPath()))) {
            URL url = new URL(imgUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = connection.getInputStream();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
        } catch (Exception e) {
            log.error("saveImage error ", e);
            throw new RuntimeException("存储图片[ <a target='_blank' href ='" + imgUrl + "'>" + fileName + "</a> ]失败，<br/>图片地址: [ " + imgUrl + " ]");
        }
        return file;
    }

    public static void cutImage(File srcImage, File destImage, ImgCutRange imageCutRange) {
        Rectangle rectangle = new Rectangle(imageCutRange.getX(), imageCutRange.getY(), imageCutRange.getWidth(), imageCutRange.getHeight());
        File parentFile = destImage.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        ImgUtil.cut(srcImage, destImage, rectangle);
    }
    

}
