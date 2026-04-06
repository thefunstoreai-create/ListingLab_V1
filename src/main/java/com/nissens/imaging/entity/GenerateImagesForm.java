package com.nissens.imaging.entity;

public class GenerateImagesForm {

    private Integer imageCount = 1;
    private StylePreset stylePreset = StylePreset.WHOLESALE;

    public Integer getImageCount() {
        return imageCount;
    }

    public void setImageCount(Integer imageCount) {
        this.imageCount = imageCount;
    }

    public StylePreset getStylePreset() {
        return stylePreset;
    }

    public void setStylePreset(StylePreset stylePreset) {
        this.stylePreset = stylePreset;
    }
}