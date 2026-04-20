package com.zlagoda.dao;

import com.zlagoda.dto.StoreProductDTO;
import com.zlagoda.model.Store_Product;
import com.zlagoda.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    public Store_Product getStoreProductByUPC(String upc) throws SQLException {
        String sql = "SELECT upc, id_product, selling_price, products_number FROM Store_Product WHERE upc = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, upc);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    Store_Product sp = new Store_Product();
                    sp.setUPC(rs.getString("upc"));
                    sp.setId_product(rs.getInt("id_product"));
                    sp.setSelling_price(rs.getDouble("selling_price"));
                    sp.setProducts_number(rs.getInt("products_number"));
                    return sp;
                }
            }
        }
        return null;
    }

    public Store_Product getFullStoreProductByUPC(String upc) throws SQLException {
        String sql = "SELECT * FROM Store_Product WHERE upc = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, upc);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapStoreProduct(rs);
                }
            }
        }
        return null;
    }

    public List<StoreProductDTO> getProductsByCategory(String categoryName) throws SQLException {
        String sql = "SELECT sp.*, p.name, p.manufacturer, p.characteristics, c.name AS category_name " +
                "FROM Store_Product sp " +
                "JOIN Product p ON sp.id_product = p.id_product " +
                "JOIN Category c ON p.category_number = c.category_number " +
                "WHERE c.name = ? " +
                "ORDER BY p.name";

        List<StoreProductDTO> list = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, categoryName);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    list.add(mapToDto(rs));
                }
            }
        }
        return list;
    }

    public List<StoreProductDTO> getAllStoreProductsOrderByName() throws SQLException {
        String sql = "SELECT sp.*, p.name, p.manufacturer, p.characteristics, c.name AS category_name " +
                "FROM Store_Product sp " +
                "JOIN Product p ON sp.id_product = p.id_product " +
                "JOIN Category c ON p.category_number = c.category_number " +
                "ORDER BY p.name";
        return executeDtoQuery(sql);
    }

    public List<StoreProductDTO> getAllStoreProductsOrderByNumber() throws SQLException {
        String sql = "SELECT sp.*, p.name, p.manufacturer, p.characteristics, c.name AS category_name " +
                "FROM Store_Product sp " +
                "JOIN Product p ON sp.id_product = p.id_product " +
                "JOIN Category c ON p.category_number = c.category_number " +
                "ORDER BY sp.products_number DESC";
        return executeDtoQuery(sql);
    }

    public List<StoreProductDTO> getPromotionalProductsOrderByNumber() throws SQLException {
        String sql = "SELECT sp.*, p.name, p.manufacturer, p.characteristics, c.name AS category_name " +
                "FROM Store_Product sp " +
                "JOIN Product p ON sp.id_product = p.id_product " +
                "JOIN Category c ON p.category_number = c.category_number " +
                "WHERE sp.promotional_product = TRUE " +
                "ORDER BY sp.products_number DESC";
        return executeDtoQuery(sql);
    }

    public List<StoreProductDTO> getNonPromotionalProductsOrderByNumber() throws SQLException {
        String sql = "SELECT sp.*, p.name, p.manufacturer, p.characteristics, c.name AS category_name " +
                "FROM Store_Product sp " +
                "JOIN Product p ON sp.id_product = p.id_product " +
                "JOIN Category c ON p.category_number = c.category_number " +
                "WHERE sp.promotional_product = FALSE " +
                "ORDER BY sp.products_number DESC";
        return executeDtoQuery(sql);
    }

    public List<StoreProductDTO> getPromotionalProductsOrderByName() throws SQLException {
        String sql = "SELECT sp.*, p.name, p.manufacturer, p.characteristics, c.name AS category_name " +
                "FROM Store_Product sp " +
                "JOIN Product p ON sp.id_product = p.id_product " +
                "JOIN Category c ON p.category_number = c.category_number " +
                "WHERE sp.promotional_product = TRUE " +
                "ORDER BY p.name";
        return executeDtoQuery(sql);
    }

    public List<StoreProductDTO> getNonPromotionalProductsOrderByName() throws SQLException {
        String sql = "SELECT sp.*, p.name, p.manufacturer, p.characteristics, c.name AS category_name " +
                "FROM Store_Product sp " +
                "JOIN Product p ON sp.id_product = p.id_product " +
                "JOIN Category c ON p.category_number = c.category_number " +
                "WHERE sp.promotional_product = FALSE " +
                "ORDER BY p.name";
        return executeDtoQuery(sql);
    }

    public List<StoreProductDTO> searchByProductName(String name) throws SQLException {
        String sql = "SELECT sp.*, p.name, p.manufacturer, p.characteristics, c.name AS category_name " +
                "FROM Store_Product sp " +
                "JOIN Product p ON sp.id_product = p.id_product " +
                "JOIN Category c ON p.category_number = c.category_number " +
                "WHERE p.name LIKE ? " +
                "ORDER BY p.name";

        List<StoreProductDTO> list = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "%" + name + "%");
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    list.add(mapToDto(rs));
                }
            }
        }
        return list;
    }

    public List<StoreProductDTO> searchByProductNameOrderByNumber(String name) throws SQLException {
        String sql = "SELECT sp.*, p.name, p.manufacturer, p.characteristics, c.name AS category_name " +
                "FROM Store_Product sp " +
                "JOIN Product p ON sp.id_product = p.id_product " +
                "JOIN Category c ON p.category_number = c.category_number " +
                "WHERE p.name LIKE ? " +
                "ORDER BY sp.products_number DESC";

        List<StoreProductDTO> list = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "%" + name + "%");
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    list.add(mapToDto(rs));
                }
            }
        }
        return list;
    }

    private List<StoreProductDTO> executeDtoQuery(String sql) throws SQLException {
        List<StoreProductDTO> list = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                list.add(mapToDto(rs));
            }
        }
        return list;
    }

    private StoreProductDTO mapToDto(ResultSet rs) throws SQLException {
        StoreProductDTO dto = new StoreProductDTO();
        dto.setUpc(rs.getString("upc"));
        dto.setUpcProm(rs.getString("upc_prom"));
        dto.setProductName(rs.getString("name"));
        dto.setManufacturer(rs.getString("manufacturer"));
        dto.setCategoryName(rs.getString("category_name"));
        dto.setSellingPrice(rs.getDouble("selling_price"));
        dto.setProductsNumber(rs.getInt("products_number"));
        dto.setPromotional(rs.getBoolean("promotional_product"));
        dto.setCharacteristics(rs.getString("characteristics"));
        return dto;
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
    public boolean existsByProductId(int idProduct) throws SQLException {
        String sql = "SELECT 1 FROM Store_Product WHERE id_product = ? LIMIT 1";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, idProduct);

            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        }
    }
}