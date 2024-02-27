package com.superbyone.job;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.extra.mail.Mail;
import cn.hutool.extra.mail.MailAccount;
import com.sun.mail.util.MailSSLSocketFactory;
import com.superbyone.config.MailConfig;
import com.superbyone.config.WeatherConfig;
import com.superbyone.utils.WeatherUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.StringJoiner;

/**
 * @author Mr.superbeyone
 * @project load-weather-image
 * @className WeatherJob
 * @description
 * @date 2024-02-23 19:42
 **/

@Slf4j
@Component
public class WeatherJob implements InitializingBean {

    @Resource
    WeatherConfig weatherConfig;

    @Resource
    MailConfig mailConfig;

    @Override
    public void afterPropertiesSet() throws Exception {
        MailAccount mailAccount = initMailAccount();
        //启动时，先调用一次
        doJob(mailAccount);
        
        //开启定时任务，定时调用
        startJob(mailAccount);
        
        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }


    public void startJob(MailAccount mailAccount) {
        CronUtil.schedule(weatherConfig.getCron(), (Task) () -> doJob(mailAccount));
    }
    

    private void doJob(MailAccount mailAccount) {
        try {

            List<String> tosList = mailConfig.getTos();
            String[] toArray = tosList.toArray(new String[tosList.size()]);

            String rootPath = weatherConfig.getSaveRootPath();

            //保存抓取的图片
            String imgRootPath = rootPath + File.separator + "image";
            boolean needCut = weatherConfig.getImgCutRange().isNeedCut();
            //保存裁剪的图片
            String cutRootPath = rootPath + File.separator + "cropImage";
            if (needCut) {
                new File(cutRootPath).mkdirs();
            }
            new File(imgRootPath).mkdirs();

            StringJoiner contentBuilder = new StringJoiner("<br/> <br/> <hr/>");

            boolean isFailed = false;

            Random random = new Random();

            //待抓取的网页路径 (解析html, 以获取图片路径)
            List<String> pageUrlList = WeatherUtil.getPageUrlList(weatherConfig.getBaseUrl(), weatherConfig.getUrlClassName());
            for (String pageUrl : pageUrlList) {
                //待保存的图片路径
                List<String> imageUrlList = WeatherUtil.getImageUrl(pageUrl, weatherConfig.getImgIdName());
                if (CollectionUtils.isEmpty(imageUrlList)) {
                    isFailed = true;
                    //解析html，获取图片路径失败，需要发送通知
                    String content = "抓取 <a target='_blank' href ='" + pageUrl + "'> 页面图片 </a> 失败，地址 [ " + pageUrl + " ]";
                    contentBuilder.add(content);
                }
                //一般情况下只有一张图片
                for (String imgUrl : imageUrlList) {
                    try {
                        //存储的图片文件 (已将图片保存到本地)
                        File srcImageFile = WeatherUtil.saveImage(imgRootPath, imgUrl);
                        if (srcImageFile != null && needCut) {
                            //裁剪图片数据
                            String cutFile = StringUtils.replace(srcImageFile.getAbsolutePath(), imgRootPath, cutRootPath);
                            WeatherUtil.cutImage(srcImageFile, new File(cutFile), weatherConfig.getImgCutRange());
                        }
                    } catch (Exception e) {
                        log.error("save image error ", e);
                        isFailed = true;
                        contentBuilder.add(e.getMessage());
                    }
                }

                //为了避免接口请求过于频繁
                Thread.sleep(5000 + random.nextInt(2000));
            }

            if (isFailed) {
                sendEmail(mailAccount, mailConfig.getTitle(), contentBuilder.toString(), toArray);
            }
        } catch (Exception e) {
            log.error("do job error ", e);
        }
    }
    

    private MailAccount initMailAccount() throws Exception {
        MailAccount mailAccount = new MailAccount();
        mailAccount.setAuth(true)
                .setSslEnable(mailConfig.isSslEnable())
                .setHost(mailConfig.getHost())
                .setFrom(mailConfig.getFrom())
                .setPass(mailConfig.getPass())
                .setCharset(StandardCharsets.UTF_8);


        MailSSLSocketFactory sf = new MailSSLSocketFactory();
        sf.setTrustAllHosts(true);
        mailAccount.setCustomProperty("mail.smtp.ssl.socketFactory", sf);

        return mailAccount;
    }


    private void sendEmail(MailAccount mailAccount, String title, String content, String... tos) {
        title = title + LocalDateTimeUtil.format(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss");
        Mail.create(mailAccount)
                .setTitle(title)
                .setTos(tos)
                .setContent(content, true)
                .send();
    }


}
