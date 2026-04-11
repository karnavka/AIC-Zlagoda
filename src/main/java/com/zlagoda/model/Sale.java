package com.zlagoda.model;

public class Sale {
    private String check_number;
    private String UPC;
    private int product_number;
    private double selling_price;

    public Sale() {
    }

    public Sale(String check_number, String UPC, int product_number, double selling_price) {
        this.check_number = check_number;
        this.UPC = UPC;
        this.product_number = product_number;
        this.selling_price = selling_price;
    }

    public String getCheck_number() {
        return check_number;
    }

    public void setCheck_number(String check_number) {
        this.check_number = check_number;
    }

    public String getUPC() {
        return UPC;
    }

    public void setUPC(String UPC) {
        this.UPC = UPC;
    }

    public int getProduct_number() {
        return product_number;
    }

    public void setProduct_number(int product_number) {
        this.product_number = product_number;
    }

    public double getSelling_price() {
        return selling_price;
    }

    public void setSelling_price(double selling_price) {
        this.selling_price = selling_price;
    }
}