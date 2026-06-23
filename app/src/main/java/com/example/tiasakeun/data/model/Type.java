package com.example.tiasakeun.data.model;

public class Type {
    private int id;
    private String category;
    private String unitName;

    public Type(int id, String category, String unitName) {
        this.id = id;
        this.category = category;
        this.unitName = unitName;
    }

    public int getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public String getUnitName() {
        return unitName;
    }

    @Override
    public String toString() {
        return "Type{" +
                "id=" + id +
                ", category='" + category + '\'' +
                ", unitName='" + unitName + '\'' +
                '}';
    }
}
