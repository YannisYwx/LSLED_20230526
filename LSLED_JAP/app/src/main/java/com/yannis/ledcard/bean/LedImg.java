package com.yannis.ledcard.bean;

/**
 * File:com.ls.yannis.bean.LedImg.java
 *
 * @version V1.0 <描述当前版本功能>
 *          Email:923080261@qq.com
 * @Description: ${led 图片}
 * Author Yannis
 * Create on: 2017-07-27 14:27
 */
public class LedImg {
    private int index;
    private int imgRes;
    private String imgMsg;

    public LedImg(int index, int imgRes, String imgMsg) {
        this.index = index;
        this.imgRes = imgRes;
        this.imgMsg = imgMsg;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getImgRes() {
        return imgRes;
    }

    public void setImgRes(int imgRes) {
        this.imgRes = imgRes;
    }

    public String getImgMsg() {
        return imgMsg;
    }

    public void setImgMsg(String imgMsg) {
        this.imgMsg = imgMsg;
    }
}
