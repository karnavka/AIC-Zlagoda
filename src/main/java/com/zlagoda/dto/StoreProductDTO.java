package com.zlagoda.dto;

public class StoreProductDTO {

    private String upc;
    private String upcProm;
    private String productName;
    private String manufacturer;
    private String categoryName;
    private double sellingPrice;
    private int productsNumber;
    private boolean promotional;
    private String characteristics; // для вікна деталей

    public String getUpc() { return upc; }
    public void setUpc(String upc) { this.upc = upc; }
    public String getUpcProm() { return upcProm; }
    public void setUpcProm(String upcProm) { this.upcProm = upcProm; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public double getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(double sellingPrice) { this.sellingPrice = sellingPrice; }
    public int getProductsNumber() { return productsNumber; }
    public void setProductsNumber(int productsNumber) { this.productsNumber = productsNumber; }
    public boolean isPromotional() { return promotional; }
    public void setPromotional(boolean promotional) { this.promotional = promotional; }
    public String getCharacteristics() { return characteristics; }
    public void setCharacteristics(String characteristics) { this.characteristics = characteristics; }
}