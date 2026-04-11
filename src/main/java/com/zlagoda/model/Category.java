package com.zlagoda.model;

public class Category {
    private int category_number;
    private String name;

    public Category() {
    }

    public Category(int category_number, String name) {
        this.category_number = category_number;
        this.name = name;
    }

    public int getCategory_number() {
        return category_number;
    }

    public void setCategory_number(int category_number) {
        this.category_number = category_number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}