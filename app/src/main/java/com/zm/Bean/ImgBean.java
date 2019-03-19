package com.zm.Bean;


import java.util.ArrayList;

public class ImgBean {
    ArrayList< byte[]> bytes;
    int width=0;
    int height=0;

    public ArrayList<byte[]> getBytes() {
        return bytes;
    }

    public void setBytes(ArrayList<byte[]> bytes) {
        this.bytes = bytes;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
