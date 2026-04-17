package com.zlagoda.dao;

import com.zlagoda.model.Store_Product;
import com.zlagoda.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// тут не всі запити є, але більшість
// я потім додам до запитів join, щоб нам виводилася назва товару завжди з таблиці Product

public class Store_ProductDAO {

    public void addStoreProduct(Store_Product storeProduct) throws SQLException {
        String sql = "INSERT INTO Store_Product (upc, upc_prom, id_product, selling_price, products_number, promotional_product) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, storeProduct.getUPC());

            if (storeProduct.getUPC_prom() == null || storeProduct.getUPC_prom().isEmpty()) {
                statement.setNull(2, Types.VARCHAR);
            } else {
                statement.setString(2, storeProduct.getUPC_prom());
            }
            statement.setInt(3, storeProduct.getId_product());
            statement.setDouble(4, storeProduct.getSelling_price());
            statement.setInt(5, storeProduct.getProducts_number());
            statement.setBoolean(6, storeProduct.isPromotional_product());

            statement.executeUpdate();
        }
    }

    public void updateStoreProduct(Store_Product storeProduct) throws SQLException {
        String sql = "UPDATE Store_Product SET upc_prom = ?, selling_price = ?, products_number = ?, promotional_product = ? WHERE upc = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            if (storeProduct.getUPC_prom() == null || storeProduct.getUPC_prom().isEmpty()) {
                statement.setNull(1, Types.VARCHAR);
            } else {
                statement.setString(1, storeProduct.getUPC_prom());
            }
            statement.setDouble(2, storeProduct.getSelling_price());
            statement.setInt(3, storeProduct.getProducts_number());
            statement.setBoolean(4, storeProduct.isPromotional_product());
            statement.setString(5, storeProduct.getUPC());

            statement.executeUpdate();
        }
    }

    public void deleteStoreProduct(String upc) throws SQLException {
        String sql = "DELETE FROM Store_Product WHERE upc = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, upc);
            statement.executeUpdate();
        }
    }

    public List<Store_Product> getAllStoreProductsOrderByName() throws SQLException {
        List<Store_Product> list = new ArrayList<>();
        String sql = "SELECT sp.* FROM Store_Product sp " +
                "JOIN Product p ON sp.id_product = p.id_product " +
                "ORDER BY p.name";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                list.add(mapStoreProduct(rs));
            }
        }
        return list;
    }

    public List<Store_Product> getAllStoreProductsOrderByNumber() throws SQLException {
        List<Store_Product> list = new ArrayList<>();
        String sql = "SELECT * FROM Store_Product ORDER BY products_number";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                list.add(mapStoreProduct(rs));
            }
        }
        return list;
    }

    // дописати
    public Store_Product getStoreProductByUPC(String upc) throws SQLException {
        String sql = "SELECT upc, selling_price, products_number FROM Store_Product WHERE upc = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, upc);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    Store_Product sp = new Store_Product();
                    sp.setUPC(rs.getString("upc"));
                    sp.setSelling_price(rs.getDouble("selling_price"));
                    sp.setProducts_number(rs.getInt("products_number"));
                    return sp;
                }
            }
        }
        return null;
    }

    public List<Store_Product> getPromotionalProductsOrderByName() throws SQLException {
        String sql = "SELECT sp.* FROM Store_Product sp " +
                "JOIN Product p ON sp.id_product = p.id_product " +
                "WHERE sp.promotional_product = TRUE " +
                "ORDER BY p.name";
        return executeListQuery(sql);
    }

    public List<Store_Product> getPromotionalProductsOrderByNumber() throws SQLException {
        String sql = "SELECT sp.* FROM Store_Product sp " +
                "JOIN Product p ON sp.id_product = p.id_product " +
                "WHERE sp.promotional_product = TRUE " +
                "ORDER BY sp.products_number";
        return executeListQuery(sql);
    }

    public List<Store_Product> getNonPromotionalProductsOrderByName() throws SQLException {
        String sql = "SELECT sp.* FROM Store_Product sp " +
                "JOIN Product p ON sp.id_product = p.id_product " +
                "WHERE sp.promotional_product = FALSE " +
                "ORDER BY p.name";
        return executeListQuery(sql);
    }

    public List<Store_Product> getNonPromotionalProductsOrderByNumber() throws SQLException {
        String sql = "SELECT sp.* FROM Store_Product sp " +
                "JOIN Product p ON sp.id_product = p.id_product " +
                "WHERE sp.promotional_product = FALSE " +
                "ORDER BY sp.products_number";
        return executeListQuery(sql);
    }

    private List<Store_Product> executeListQuery(String sql) throws SQLException {
        List<Store_Product> list = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                list.add(mapStoreProduct(rs));
            }
        }
        return list;
    }

    private Store_Product mapStoreProduct(ResultSet rs) throws SQLException {
        Store_Product sp = new Store_Product();
        sp.setUPC(rs.getString("upc"));
        sp.setUPC_prom(rs.getString("upc_prom"));
        sp.setId_product(rs.getInt("id_product"));
        sp.setSelling_price(rs.getDouble("selling_price"));
        sp.setProducts_number(rs.getInt("products_number"));
        sp.setPromotional_product(rs.getBoolean("promotional_product"));
        return sp;
    }
}