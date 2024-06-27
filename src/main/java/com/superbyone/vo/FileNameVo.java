package com.superbyone.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Mr.superbeyone
 * @project weather-image-cloud
 * @className FileNameVo
 * @description
 * @date 2024-02-28 09:55
 **/

@Data
public class FileNameVo implements Serializable {

    private Long name;

    private String fileName;

    private String path;
}
