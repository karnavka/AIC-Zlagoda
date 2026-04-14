package com.zlagoda.dao;

import com.zlagoda.model.Category;
import com.zlagoda.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    public void addCategory(Category category) throws SQLException {
        String sql = "INSERT INTO Category (category_number, name) VALUES (?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, category.getCategory_number());
            statement.setString(2, category.getName());

            statement.executeUpdate();
        }
    }

    public void updateCategory(Category category) throws SQLException {
        String sql = "UPDATE category SET name = ? WHERE category_number = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, category.getName());
            statement.setInt(2, category.getCategory_number());

            statement.executeUpdate();
        }
    }

    public void deleteCategory(int category_number) throws SQLException {
        String sql = "DELETE FROM Category WHERE category_number = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, category_number);
            statement.executeUpdate();
        }
    }

    public List<Category> getAllCategoriesOrderByName() throws SQLException {
        List<Category> list = new ArrayList<>();

        String sql = "SELECT * FROM category ORDER BY name";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);

             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                Category category = new Category();
                category.setCategory_number(rs.getInt("category_number"));
                category.setName(rs.getString("name"));

                list.add(category);
            }
        }
        return list;
    }
}