package com.example.tiasakeun.data.model;

public class Activity {
    private Long id;
    private Long typeId;
    private String title;;
    private int imageResourceId;

    public Activity() {
    }

    public Activity(Long id, Long typeId, String title, int imageResourceId) {
        this.id = id;
        this.typeId = typeId;
        this.title = title;
        this.imageResourceId = imageResourceId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public void setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }

    @Override
    public String toString() {
        return "Activity{" +
                "id=" + id +
                ", typeId=" + typeId +
                ", title='" + title + '\'' +
                ", imageResourceId=" + imageResourceId +
                '}';
    }
}
