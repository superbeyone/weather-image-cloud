package com.superbyone.config;

import lombok.Data;
import lombok.ToString;

/**
 * @author Mr.superbeyone
 * @project load-weather-image
 * @className ImageCutRange
 * @description
 * @date 2024-02-24 16:11
 **/

@Data
@ToString
public class ImgCutRange {

    private boolean needCut = true;
    private int x;

    private int y;

    private int width;

    private int height;
    
}
