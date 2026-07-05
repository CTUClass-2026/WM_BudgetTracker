package com.mycompany.personalfinancetrackerctu;

public class Category {
    private int id;
    private String name;
    private String colorHex;

    public Category(int id, String name, String colorHex) {
        this.id = id;
        this.name = name;
        this.colorHex = colorHex;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }   

    public String getName() {
        return name;
    }

    public String getColorHex() {
        return colorHex;
    }

    @Override
    public String toString() {
        return name;
    }
}