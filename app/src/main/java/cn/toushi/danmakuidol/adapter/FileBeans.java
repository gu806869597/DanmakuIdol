package cn.toushi.danmakuidol.adapter;

import android.graphics.Bitmap;

/**
 * 作者： jimhao
 * 创建于： 2017/3/5
 * 包名： cn.toushi.danmakuidol
 * 文档描述： 文件实体
 */

public class FileBeans {

    public Long id ;

    public String name ;

    public String path ;

    public String TIME ;

    public Bitmap fileImg ;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTIME() {
        return TIME;
    }

    public void setTIME(String TIME) {
        this.TIME = TIME;
    }

    public Bitmap getFileImg() {
        return fileImg;
    }

    public void setFileImg(Bitmap fileImg) {
        this.fileImg = fileImg;
    }
}
