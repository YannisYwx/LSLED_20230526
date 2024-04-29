package com.yannis.ledcard.bean;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * @author : Yannis.Ywx
 * @createTime : 2018/11/22 19:34
 * @email : 923080261@qq.com
 * @description : 图片信息需要
 */
public class LEDBmp extends DataSupport implements Serializable {
    private int id;
    /**
     * 内容00011010111
     */
    private String content;
    /**
     * 点阵大小
     */
    private int matrix;
    /**
     * 文件目录
     */
    private String filePath;
    /**
     * ResourceId
     */
    private int resourceID;

    private String resourceName;

    public LEDBmp(String content, int matrix, String filePath) {
        this.content = content;
        this.matrix = matrix;
        this.filePath = filePath;
        this.resourceID = -1;
    }

    public LEDBmp(String content, int matrix, int resourceID) {
        this.content = content;
        this.matrix = matrix;
        this.filePath = null;
        this.resourceID = resourceID;
    }

    public int getResourceID() {
        return resourceID;
    }

    public void setResourceID(int resourceID) {
        this.resourceID = resourceID;
    }

    public LEDBmp(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getMatrix() {
        return matrix;
    }

    public void setMatrix(int matrix) {
        this.matrix = matrix;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    @Override
    public String toString() {
        return "LEDBmp{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", matrix=" + matrix +
                ", filePath='" + filePath + '\'' +
                ", resourceID ='" + resourceID +
                '}';
    }
}
