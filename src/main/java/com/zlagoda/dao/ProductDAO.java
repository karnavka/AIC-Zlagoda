package com.zlagoda.dao;

import com.zlagoda.dto.ProductCatalogDTO;
import com.zlagoda.model.Product;
import com.zlagoda.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    public void addProduct(Product product) throws SQLException {
        String sql = "INSERT INTO Product " +
                "(id_product, name, manufacturer, characteristics, category_number) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, product.getId_product());
            statement.setString(2, product.getName());
            statement.setString(3, product.getManufacturer());
            statement.setString(4, product.getCharacteristics());
            statement.setInt(5, product.getCategory_number());

            statement.executeUpdate();
        }
    }

    public void updateProduct(Product product) throws SQLException {
        String sql = "UPDATE Product SET " +
                "name = ?, manufacturer = ?, characteristics = ?, category_number = ? " +
                "WHERE id_product = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, product.getName());
            statement.setString(2, product.getManufacturer());
            statement.setString(3, product.getCharacteristics());
            statement.setInt(4, product.getCategory_number());
            statement.setInt(5, product.getId_product());

            statement.executeUpdate();
        }
    }

    public void deleteProduct(int id_product) throws SQLException {
        String sql = "DELETE FROM Product WHERE id_product = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id_product);
            statement.executeUpdate();
        }
    }

    public List<Product> getAllProductsOrderByName() throws SQLException {
        List<Product> list = new ArrayList<>();

        String sql = "SELECT * FROM Product ORDER BY name";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                Product product = new Product();

                product.setId_product(rs.getInt("id_product"));
                product.setName(rs.getString("name"));
                product.setManufacturer(rs.getString("manufacturer"));
                product.setCharacteristics(rs.getString("characteristics"));
                product.setCategory_number(rs.getInt("category_number"));

                list.add(product);
            }
        }
        return list;
    }

    public Product getProductById(int id) throws SQLException {
        String sql = "SELECT * FROM Product WHERE id_product = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    Product product = new Product();
                    product.setId_product(rs.getInt("id_product"));
                    product.setName(rs.getString("name"));
                    product.setManufacturer(rs.getString("manufacturer"));
                    product.setCharacteristics(rs.getString("characteristics"));
                    product.setCategory_number(rs.getInt("category_number"));
                    return product;
                }
            }
        }
        return null;
    }

    public List<Product> getProductsByCategoryName(String categoryName) throws SQLException {
        List<Product> list = new ArrayList<>();

        String sql = "SELECT p.* " +
                     "FROM Product p " +
                     "JOIN Category c ON p.category_number = c.category_number " +
                     "WHERE c.name = ? " +
                     "ORDER BY p.name";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, categoryName);

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Product product = new Product();

                product.setId_product(rs.getInt("id_product"));
                product.setName(rs.getString("name"));
                product.setManufacturer(rs.getString("manufacturer"));
                product.setCharacteristics(rs.getString("characteristics"));
                product.setCategory_number(rs.getInt("category_number"));

                list.add(product);
            }
        }
        return list;
    }

    public List<Product> searchProductsByName(String name) throws SQLException {
        List<Product> list = new ArrayList<>();

        String sql = "SELECT * FROM Product WHERE name LIKE ? ORDER BY name";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, name + "%");

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Product product = new Product();

                product.setId_product(rs.getInt("id_product"));
                product.setName(rs.getString("name"));
                product.setManufacturer(rs.getString("manufacturer"));
                product.setCharacteristics(rs.getString("characteristics"));
                product.setCategory_number(rs.getInt("category_number"));

                list.add(product);
            }
        }
        return list;
    }
    public List<ProductCatalogDTO> getAllProductsWithCategoryName() throws SQLException {
        List<ProductCatalogDTO> list = new ArrayList<>();

        String sql = "SELECT p.id_product, p.name, p.manufacturer, p.characteristics, " +
                "p.category_number, c.name AS category_name " +
                "FROM Product p " +
                "JOIN Category c ON p.category_number = c.category_number " +
                "ORDER BY p.name";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                ProductCatalogDTO dto = new ProductCatalogDTO();
                dto.setIdProduct(rs.getInt("id_product"));
                dto.setProductName(rs.getString("name"));
                dto.setManufacturer(rs.getString("manufacturer"));
                dto.setCharacteristics(rs.getString("characteristics"));
                dto.setCategoryNumber(rs.getInt("category_number"));
                dto.setCategoryName(rs.getString("category_name"));
                list.add(dto);
            }
        }

        return list;
    }
}