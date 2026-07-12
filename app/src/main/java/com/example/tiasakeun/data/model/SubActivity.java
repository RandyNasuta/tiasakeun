package com.example.tiasakeun.data.model;

public class SubActivity {
    private long id;
    private long activityId;
    private long scheduleId;;
    private String title;
    private long targetValue;
    private String dateActivity;
    private int notification;
    private int isCompleted;

    private int currentValue;
    private String unitName;
    private String scheduleType;

    public SubActivity() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getActivityId() {
        return activityId;
    }

    public void setActivityId(long activityId) {
        this.activityId = activityId;
    }

    public long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(long targetValue) {
        this.targetValue = targetValue;
    }

    public String getDateActivity() {
        return dateActivity;
    }

    public void setDateActivity(String dateActivity) {
        this.dateActivity = dateActivity;
    }

    public int getNotification() {
        return notification;
    }

    public void setNotification(int notification) {
        this.notification = notification;
    }

    public int getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(int isCompleted) {
        this.isCompleted = isCompleted;
    }

    public int getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(int currentValue) {
        this.currentValue = currentValue;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(String scheduleType) {
        this.scheduleType = scheduleType;
    }

    @Override
    public String toString() {
        return "SubActivity{" +
                "id=" + id +
                ", activityId=" + activityId +
                ", scheduleId=" + scheduleId +
                ", title='" + title + '\'' +
                ", targetValue=" + targetValue +
                ", dateActivity='" + dateActivity + '\'' +
                ", notification=" + notification +
                ", isCompleted=" + isCompleted +
                '}';
    }


}
