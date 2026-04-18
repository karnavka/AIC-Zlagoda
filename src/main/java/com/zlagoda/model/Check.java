package com.zlagoda.model;

import java.time.LocalDateTime;

public class Check {
    private String check_number;
    private String id_employee;
    private String card_number;
    private LocalDateTime print_date;
    private double sum_total;
    private double vat;

    public Check() {
    }

    public Check(String check_number, String id_employee, String card_number, LocalDateTime print_date, double sum_total, double vat) {
        this.check_number = check_number;
        this.id_employee = id_employee;
        this.card_number = card_number;
        this.print_date = print_date;
        this.sum_total = sum_total;
        this.vat = vat;
    }

    public String getCheck_number() {
        return check_number;
    }

    public void setCheck_number(String check_number) {
        this.check_number = check_number;
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

    public LocalDateTime getPrint_date() {
        return print_date;
    }

    public void setPrint_date(LocalDateTime print_date) {
        this.print_date = print_date;
    }

    public double getSum_total() {
        return sum_total;
    }

    public void setSum_total(double sum_total) {
        this.sum_total = sum_total;
    }

    public double getVat() {
        return vat;
    }

    public void setVat(double vat) {
        this.vat = vat;
    }
}