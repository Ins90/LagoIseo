package it.inserrafesta.iseomap;

import android.graphics.Bitmap;

public class ServiceItem {
    private int imageId;
    private String title;

    public ServiceItem(int image, String title) {
        super();
        this.imageId = image;
        this.title = title;
    }

    public int getImage() {
        return imageId;
    }

    public void setImage(int imageId) {
        this.imageId = imageId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}