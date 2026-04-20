package com.zlagoda.dao;

import com.zlagoda.model.Category;
import com.zlagoda.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
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
        String sql = "UPDATE Category SET name = ? WHERE category_number = ?";

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

        String sql = "SELECT * FROM Category ORDER BY name";

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


    public List<CategoryStat> getSoldProductsNumberByCategory(LocalDate start, LocalDate end) throws SQLException {
        List<CategoryStat> list = new ArrayList<>();
        String sql = "SELECT cat.category_number, cat.name, SUM(s.product_number) AS total_number " +
                "FROM Sale s " +
                "JOIN Store_Product sp ON s.UPC = sp.UPC " +
                "JOIN Product p ON sp.id_product = p.id_product " +
                "JOIN Category cat ON p.category_number = cat.category_number " +
                "JOIN Check c ON s.check_number = c.check_number " +
                "WHERE c.print_date BETWEEN ? AND ? " +
                "GROUP BY cat.category_number, cat.name";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setDate(1, Date.valueOf(start));
            statement.setDate(2, Date.valueOf(end));

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    list.add(new CategoryStat(
                            rs.getInt("category_number"),
                            rs.getString("name"),
                            rs.getInt("total_number")
                    ));
                }
            }
        }
        return list;
    }

    public List<Category> getCategoriesWithNoPurchases(LocalDate start, LocalDate end) throws SQLException {
        List<Category> list = new ArrayList<>();

        String sql = "SELECT cat.* FROM Category cat WHERE NOT EXISTS ( " +
                "SELECT * FROM Product p WHERE p.category_number = cat.category_number " +
                "AND NOT EXISTS ( " +
                "SELECT * FROM Store_Product sp " +
                "JOIN Sale s ON sp.UPC = s.UPC " +
                "JOIN Check ch ON s.check_number = ch.check_number " +
                "WHERE sp.id_product = p.id_product " +
                "AND ch.print_date BETWEEN ? AND ? " +
                ") " +
                ")";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setDate(1, Date.valueOf(start));
            statement.setDate(2, Date.valueOf(end));

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Category category = new Category();
                    category.setCategory_number(rs.getInt("category_number"));
                    category.setName(rs.getString("name"));

                    list.add(category);
                }
            }
        }
        return list;
    }


    public static class CategoryStat {
        private final int categoryNumber;
        private final String name;
        private final int totalNumber;

        public CategoryStat(int categoryNumber, String name, int totalNumber) {
            this.categoryNumber = categoryNumber;
            this.name = name;
            this.totalNumber = totalNumber;
        }

        public int getCategoryNumber() { return categoryNumber; }
        public String getName() { return name; }
        public int getTotalNumber() { return totalNumber; }
    }

    public boolean hasProductsInCategory(int categoryNumber) throws SQLException {
        String sql = "SELECT 1 FROM Product WHERE category_number = ? LIMIT 1";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, categoryNumber);

            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        }
    }
    // Касир для Олі в EmployeeDAO
    /*
    public List<EmployeeStat> getEmployeeSalesStats(LocalDate start, LocalDate end) throws SQLException {
        List<EmployeeStat> list = new ArrayList<>();
        String sql = "SELECT e.id_employee, e.surname, e.name, SUM(s.product_number) AS total_products, SUM(ch.sum) AS total_sales " +
                "FROM Employee e " +
                "JOIN Check ch ON e.id_employee = ch.id_employee " +
                "JOIN Sale s ON ch.check_number = s.check_number " +
                "WHERE ch.print_date BETWEEN ? AND ? " +
                "GROUP BY e.id_employee, e.surname, e.name " +
                "ORDER BY total_sales DESC";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setDate(1, Date.valueOf(start));
            statement.setDate(2, Date.valueOf(end));

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    list.add(new EmployeeStat(
                            rs.getString("id_employee"),
                            rs.getString("surname"),
                            rs.getString("name"),
                            rs.getInt("total_products"),
                            rs.getBigDecimal("total_sales").doubleValue()
                    ));
                }
            }
        }
        return list;
    }


    public List<Employee> getEmployeesWorkingEveryDay(LocalDate start, LocalDate end) throws SQLException {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT e.id_employee, e.surname, e.name " +
                "FROM Employee e " +
                "WHERE NOT EXISTS ( " +
                "SELECT DISTINCT ch.print_date " +
                "FROM Check ch " +
                "WHERE ch.print_date BETWEEN ? AND ? " +
                "AND NOT EXISTS ( " +
                "SELECT * " +
                "FROM Check ch2 " +
                "WHERE ch2.id_employee = e.id_employee " +
                "AND ch2.print_date = ch.print_date " +
                ") " +
                ")";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setDate(1, Date.valueOf(start));
            statement.setDate(2, Date.valueOf(end));

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Employee employee = new Employee();
                    employee.setId_employee(rs.getString("id_employee"));
                    employee.setSurname(rs.getString("surname"));
                    employee.setName(rs.getString("name"));

                    list.add(employee);
                }
            }
        }
        return list;
    }


    public static class EmployeeStat {
        private final String id_employee;
        private final String surname;
        private final String name;
        private final int total_products;
        private final double total_sales;

        public EmployeeStat(String id_employee, String surname, String name, int total_products, double total_sales) {
            this.id_employee = id_employee;
            this.surname = surname;
            this.name = name;
            this.total_products = total_products;
            this.total_sales = total_sales;
        }

        public String getId_employee() { return id_employee; }
        public String getSurname() { return surname; }
        public String getName() { return name; }
        public int getTotal_products() { return total_products; }
        public double getTotal_sales() { return total_sales; }
    }
    */

}