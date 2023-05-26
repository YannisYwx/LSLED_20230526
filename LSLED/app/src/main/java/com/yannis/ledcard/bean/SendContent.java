package com.yannis.ledcard.bean;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * File:com.ls.yannis.bean.SendContent.java
 *
 * @version V1.0 <描述当前版本功能>
 * Email:923080261@qq.com
 * @Description: ${发送消息的内容}
 * Author Yannis
 * Create on: 2017-07-27 14:27
 */
public class SendContent extends DataSupport implements Serializable {
    private int id;
    private String message;// 待发送的消息
    private int speed;// 速度
    private int mode;// 模式
    private boolean isFlash;// 是否闪屏
    private boolean isReverse;// 是否倒字
    private boolean isMarquee;// 是否跑马灯
    private boolean isSelect; //是否选择
    public SendContent() {

    }

    public SendContent(String message, int speed, int mode, boolean isFlash, boolean isReverse, boolean isMarquee) {
        this.message = message;
        this.speed = speed;
        this.mode = mode;
        this.isFlash = isFlash;
        this.isReverse = isReverse;
        this.isMarquee = isMarquee;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public boolean isFlash() {
        return isFlash;
    }

    public void setFlash(boolean flash) {
        isFlash = flash;
    }

    public boolean isReverse() {
        return isReverse;
    }

    public void setReverse(boolean reverse) {
        isReverse = reverse;
    }

    public boolean isMarquee() {
        return isMarquee;
    }

    public void setMarquee(boolean marquee) {
        isMarquee = marquee;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    @Override
    public String toString() {
        return "SendContent{" +
                "message='" + message + '\'' +
                ", speed=" + speed +
                ", mode=" + mode +
                ", isFlash=" + isFlash +
                ", isReverse=" + isReverse +
                ", isMarquee=" + isMarquee +
                ", isSelect=" + isSelect +
                '}';
    }
}
