package com.zlagoda.model;

public class Customer_Card {
    private String card_number;
    private String surname;
    private String name;
    private String patronymic;
    private String phone_number;
    private String city;
    private String street;
    private String zip_code;
    private int percent;

    public Customer_Card() {
    }

    public Customer_Card(String card_number, String surname, String name, String patronymic, String phone_number, String city, String street, String zip_code, int percent) {
        this.card_number = card_number;
        this.surname = surname;
        this.name = name;
        this.patronymic = patronymic;
        this.phone_number = phone_number;
        this.city = city;
        this.street = street;
        this.zip_code = zip_code;
        this.percent = percent;
    }

    public String getCard_number() {
        return card_number;
    }

    public void setCard_number(String card_number) {
        this.card_number = card_number;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZip_code() {
        return zip_code;
    }

    public void setZip_code(String zip_code) {
        this.zip_code = zip_code;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }
}