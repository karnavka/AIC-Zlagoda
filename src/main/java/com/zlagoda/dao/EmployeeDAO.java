package com.zlagoda.dao;

import com.zlagoda.model.Employee;
import com.zlagoda.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    public void addEmployee(Employee employee) throws SQLException {
        String sql = "INSERT INTO Employee " +
                "(id_employee, surname, name, patronymic, role, salary, date_of_birth, date_of_start, phone_number, city, street, zip_code) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, employee.getId_employee());
            statement.setString(2, employee.getSurname());
            statement.setString(3, employee.getName());
            statement.setString(4, employee.getPatronymic());
            statement.setString(5, employee.getRole());
            statement.setDouble(6, employee.getSalary());
            statement.setDate(7, java.sql.Date.valueOf(employee.getDate_of_birth()));
            statement.setDate(8, java.sql.Date.valueOf(employee.getDate_of_start()));
            statement.setString(9, employee.getPhone_number());
            statement.setString(10, employee.getCity());
            statement.setString(11, employee.getStreet());
            statement.setString(12, employee.getZip_code());

            statement.executeUpdate();
        }
    }

    public void updateEmployee(Employee employee) throws SQLException {
        String sql = "UPDATE Employee SET " +
                "surname = ?, name = ?, patronymic = ?, role = ?, salary = ?, " +
                "date_of_birth = ?, date_of_start = ?, phone_number = ?, city = ?, street = ?, zip_code = ? " +
                "WHERE id_employee = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, employee.getSurname());
            statement.setString(2, employee.getName());
            statement.setString(3, employee.getPatronymic());
            statement.setString(4, employee.getRole());
            statement.setDouble(5, employee.getSalary());
            statement.setObject(6, employee.getDate_of_birth());
            statement.setObject(7, employee.getDate_of_start());
            statement.setString(8, employee.getPhone_number());
            statement.setString(9, employee.getCity());
            statement.setString(10, employee.getStreet());
            statement.setString(11, employee.getZip_code());
            statement.setString(12, employee.getId_employee());

            statement.executeUpdate();
        }
    }

    public void deleteEmployee(String id_employee) throws SQLException {
        String sql = "DELETE FROM Employee WHERE id_employee = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, id_employee);
            statement.executeUpdate();
        }
    }

    public List<Employee> getAllEmployeesOrderBySurname() throws SQLException {
        List<Employee> list = new ArrayList<>();

        String sql = "SELECT * FROM Employee ORDER BY surname";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                Employee employee = new Employee();

                employee.setId_employee(rs.getString("id_employee"));
                employee.setSurname(rs.getString("surname"));
                employee.setName(rs.getString("name"));
                employee.setPatronymic(rs.getString("patronymic"));
                employee.setRole(rs.getString("role"));
                employee.setSalary(rs.getDouble("salary"));
                employee.setDate_of_birth(rs.getDate("date_of_birth").toLocalDate());
                employee.setDate_of_start(rs.getDate("date_of_start").toLocalDate());
                employee.setPhone_number(rs.getString("phone_number"));
                employee.setCity(rs.getString("city"));
                employee.setStreet(rs.getString("street"));
                employee.setZip_code(rs.getString("zip_code"));

                list.add(employee);
            }
        }
        return list;
    }

    public Employee getEmployeeById(String id_employee) throws SQLException {
        String sql = "SELECT * FROM Employee WHERE id_employee = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, id_employee);

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                Employee employee = new Employee();

                employee.setId_employee(rs.getString("id_employee"));
                employee.setSurname(rs.getString("surname"));
                employee.setName(rs.getString("name"));
                employee.setPatronymic(rs.getString("patronymic"));
                employee.setRole(rs.getString("role"));
                employee.setSalary(rs.getDouble("salary"));
                employee.setDate_of_birth(rs.getDate("date_of_birth").toLocalDate());
                employee.setDate_of_start(rs.getDate("date_of_start").toLocalDate());
                employee.setPhone_number(rs.getString("phone_number"));
                employee.setCity(rs.getString("city"));
                employee.setStreet(rs.getString("street"));
                employee.setZip_code(rs.getString("zip_code"));

                return employee;
            }
        }
        return null;
    }

    public List<Employee> getEmployeesByRole(String role) throws SQLException {
        List<Employee> list = new ArrayList<>();

        String sql = "SELECT * FROM Employee WHERE role = ? ORDER BY surname";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, role);

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Employee employee = new Employee();

                employee.setId_employee(rs.getString("id_employee"));
                employee.setSurname(rs.getString("surname"));
                employee.setName(rs.getString("name"));
                employee.setPatronymic(rs.getString("patronymic"));
                employee.setRole(rs.getString("role"));
                employee.setSalary(rs.getDouble("salary"));
                employee.setDate_of_birth(rs.getDate("date_of_birth").toLocalDate());
                employee.setDate_of_start(rs.getDate("date_of_start").toLocalDate());
                employee.setPhone_number(rs.getString("phone_number"));
                employee.setCity(rs.getString("city"));
                employee.setStreet(rs.getString("street"));
                employee.setZip_code(rs.getString("zip_code"));

                list.add(employee);
            }
        }
        return list;
    }

    public List<Employee> searchEmployeeContactsBySurname(String surname) throws SQLException {
        List<Employee> list = new ArrayList<>();

        String sql = "SELECT phone_number, city, street, zip_code FROM Employee WHERE surname LIKE ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, surname + "%");

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Employee employee = new Employee();

                employee.setPhone_number(rs.getString("phone_number"));
                employee.setCity(rs.getString("city"));
                employee.setStreet(rs.getString("street"));
                employee.setZip_code(rs.getString("zip_code"));

                list.add(employee);
            }
        }
        return list;
    }
}