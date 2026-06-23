package com.example.tiasakeun.data.model;

public class Schedule {
    private int id;
    private String type;

    public Schedule(int id, String type) {
        this.id = id;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "id=" + id +
                ", type='" + type + '\'' +
                '}';
    }
}
