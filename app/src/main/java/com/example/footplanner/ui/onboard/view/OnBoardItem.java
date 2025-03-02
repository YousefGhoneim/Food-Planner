package com.example.footplanner.ui.onboard.view;

public class OnBoardItem {
    private int image;
    private String desc;

    public OnBoardItem(int image,  String desc) {
        this.image = image;
        this.desc = desc;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
