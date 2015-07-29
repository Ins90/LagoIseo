package it.inserrafesta.iseomap;

import android.graphics.Bitmap;

public class ServiceItem {
    private Bitmap image;
    private String title;

    public ServiceItem(Bitmap image, String title) {
        super();
        this.image = image;
        this.title = title;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap imageId) {
        this.image = imageId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}