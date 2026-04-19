package com.zlagoda.service;

import com.zlagoda.dao.Store_ProductDAO;
import com.zlagoda.model.Store_Product;
import java.sql.SQLException;

public class StoreProductService {

    private Store_ProductDAO storeProductDAO = new Store_ProductDAO();

    // не впевнена чи треба нам такий метод (як ми реалізовуємо поставки?)
    // можливо для цього вистачить і одного методу update з дао
    public void updateProductPriceAndNumber(String upc, int addedNumber, double newPrice) throws SQLException {
        Store_Product product = storeProductDAO.getFullStoreProductByUPC(upc);

        if (product != null) {
            product.setSelling_price(newPrice);
            product.setProducts_number(product.getProducts_number() + addedNumber);

            storeProductDAO.updateStoreProduct(product);
        }
    }

    public void makePromotional(String upc, String promoUpc) throws SQLException {
        Store_Product mainProduct = storeProductDAO.getFullStoreProductByUPC(upc);

        if (mainProduct != null && !mainProduct.isPromotional_product()) {

            Store_Product promoProduct = new Store_Product();
            promoProduct.setUPC(promoUpc);
            promoProduct.setId_product(mainProduct.getId_product());
            promoProduct.setSelling_price(mainProduct.getSelling_price() * 0.8);
            promoProduct.setProducts_number(mainProduct.getProducts_number());
            promoProduct.setPromotional_product(true);
            promoProduct.setUPC_prom(null);

            storeProductDAO.addStoreProduct(promoProduct);

            mainProduct.setUPC_prom(promoUpc);
            mainProduct.setProducts_number(0);

            storeProductDAO.updateStoreProduct(mainProduct);
        }
    }
}