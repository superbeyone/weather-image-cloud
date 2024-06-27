package com.superbyone.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author Mr.superbeyone
 * @project weather-image-cloud
 * @className ImageVo
 * @description
 * @date 2024-02-28 09:12
 **/

@Data
@ToString
public class ImageVo implements Serializable {

    private static final long serialVersionUID = 3721591139124064264L;

    private String hour;

    private String fileName;

    private String path;
    
}
