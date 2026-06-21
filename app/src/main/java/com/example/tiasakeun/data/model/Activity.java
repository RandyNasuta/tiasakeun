package com.example.tiasakeun.data.model;

public class Activity {
    private int id;
    private int userId;
    private int typeId;
    private int scheduleId;
    private String title;
    private int currentValue;
    private int targetValue;
    private int notification;
    private int imageResourceId;

    public Activity(int id, int userId, int typeId, int scheduleId, String title, int currentValue, int targetValue, int notification, int imageResourceId) {
        this.id = id;
        this.userId = userId;
        this.typeId = typeId;
        this.scheduleId = scheduleId;
        this.title = title;
        this.currentValue = currentValue;
        this.targetValue = targetValue;
        this.notification = notification;
        this.imageResourceId = imageResourceId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(int currentValue) {
        this.currentValue = currentValue;
    }

    public int getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(int targetValue) {
        this.targetValue = targetValue;
    }

    public int getNotification() {
        return notification;
    }

    public void setNotification(int notification) {
        this.notification = notification;
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
                ", userId=" + userId +
                ", typeId=" + typeId +
                ", scheduleId=" + scheduleId +
                ", title='" + title + '\'' +
                ", currentValue=" + currentValue +
                ", targetValue=" + targetValue +
                ", notification=" + notification +
                ", imageResourceId=" + imageResourceId +
                '}';
    }
}
