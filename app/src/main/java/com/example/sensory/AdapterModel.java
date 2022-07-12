package com.example.sensory;

public class AdapterModel {
    private final String title;
    private String value;
    private final Integer imgResourceId;

    public AdapterModel(String title, String value, Integer imgResourceId) {
        this.title = title;
        this.value = value;
        this.imgResourceId = imgResourceId;
    }

    public String getTitle() {
        return title;
    }

    public String getValue() {
        return value;
    }

    public Integer getImg() {
        return imgResourceId;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
