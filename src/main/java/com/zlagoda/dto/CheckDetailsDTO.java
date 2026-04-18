package com.zlagoda.dto;

import java.time.LocalDateTime;

public class CheckDetailsDTO {

    private String check_number;
    private LocalDateTime print_date;
    private String id_employee;
    private String card_number;
    private String UPC;
    private String name;
    private int product_number;
    private double selling_price;

    public String getCheck_number() {
        return check_number;
    }

    public void setCheck_number(String check_number) {
        this.check_number = check_number;
    }

    public LocalDateTime getPrint_date() {
        return print_date;
    }

    public void setPrint_date(LocalDateTime print_date) {
        this.print_date = print_date;
    }

    public String getId_employee() {
        return id_employee;
    }

    public void setId_employee(String id_employee) {
        this.id_employee = id_employee;
    }

    public String getCard_number() {
        return card_number;
    }

    public void setCard_number(String card_number) {
        this.card_number = card_number;
    }

    public String getUPC() {
        return UPC;
    }

    public void setUPC(String UPC) {
        this.UPC = UPC;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getProduct_number() {
        return product_number;
    }

    public void setProduct_number(int products_number) {
        this.product_number = products_number;
    }

    public double getSelling_price() {
        return selling_price;
    }

    public void setSelling_price(double selling_price) {
        this.selling_price = selling_price;
    }
}