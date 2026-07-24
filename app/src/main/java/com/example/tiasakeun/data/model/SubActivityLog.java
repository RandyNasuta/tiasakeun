package com.example.tiasakeun.data.model;

public class SubActivityLog {
    private Long id;
    private Long value;
    private String logDate;

    private Long subActivityId;
    private String subActivityName;
    private String categoryType;
    private String unitName;

    public SubActivityLog() {}

    public SubActivityLog(Long id, Long value, String logDate, Long subActivityId, String subActivityName, String categoryType, String unitName) {
        this.id = id;
        this.value = value;
        this.logDate = logDate;
        this.subActivityId = subActivityId;
        this.subActivityName = subActivityName;
        this.categoryType = categoryType;
        this.unitName = unitName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public String getLogDate() {
        return logDate;
    }

    public void setLogDate(String logDate) {
        this.logDate = logDate;
    }

    public Long getSubActivityId() {
        return subActivityId;
    }

    public void setSubActivityId(Long subActivityId) {
        this.subActivityId = subActivityId;
    }

    public String getSubActivityName() {
        return subActivityName;
    }

    public void setSubActivityName(String subActivityName) {
        this.subActivityName = subActivityName;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    @Override
    public String toString() {
        return "SubActivityLog{" +
                "id=" + id +
                ", value=" + value +
                ", logDate='" + logDate + '\'' +
                ", subActivityId=" + subActivityId +
                ", subActivityName='" + subActivityName + '\'' +
                ", categoryType='" + categoryType + '\'' +
                ", unitName='" + unitName + '\'' +
                '}';
    }
}
