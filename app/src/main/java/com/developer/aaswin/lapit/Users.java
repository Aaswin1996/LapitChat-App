package com.developer.aaswin.lapit;

/**
 * Created by aaswin on 24/1/18.
 */

public class Users {
    private String name;
    private String Image;
    private String status;
    private String image_thumbnail;

    public Users() {
    }

    public Users(String name, String image, String status, String image_thumbnail) {
        this.name = name;
        Image = image;
        this.status = status;
        this.image_thumbnail = image_thumbnail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage_thumbnail() {
        return image_thumbnail;
    }

    public void setImage_thumbnail(String image_thumbnail) {
        this.image_thumbnail = image_thumbnail;
    }
}
