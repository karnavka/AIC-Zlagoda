package com.zlagoda.dao;

import com.zlagoda.model.Customer_Card;
import com.zlagoda.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Customer_CardDAO {

    public void addCustomerCard(Customer_Card card) throws SQLException {
        String sql = "INSERT INTO Customer_Card " +
                "(card_number, surname, name, patronymic, phone_number, city, street, zip_code, percent) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, card.getCard_number());
            statement.setString(2, card.getSurname());
            statement.setString(3, card.getName());
            statement.setString(4, card.getPatronymic());
            statement.setString(5, card.getPhone_number());
            statement.setString(6, card.getCity());
            statement.setString(7, card.getStreet());
            statement.setString(8, card.getZip_code());
            statement.setInt(9, card.getPercent());

            statement.executeUpdate();
        }
    }

    public void updateCustomerCard(Customer_Card card) throws SQLException {
        String sql = "UPDATE Customer_Card SET " +
                "surname = ?, name = ?, patronymic = ?, phone_number = ?, city = ?, street = ?, zip_code = ?, percent = ? " +
                "WHERE card_number = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, card.getSurname());
            statement.setString(2, card.getName());
            statement.setString(3, card.getPatronymic());
            statement.setString(4, card.getPhone_number());
            statement.setString(5, card.getCity());
            statement.setString(6, card.getStreet());
            statement.setString(7, card.getZip_code());
            statement.setInt(8, card.getPercent());
            statement.setString(9, card.getCard_number());

            statement.executeUpdate();
        }
    }

    public void deleteCustomerCard(String card_number) throws SQLException {
        String sql = "DELETE FROM Customer_Card WHERE card_number = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, card_number);
            statement.executeUpdate();
        }
    }

    public List<Customer_Card> getAllCustomerCardsOrderBySurname() throws SQLException {
        List<Customer_Card> list = new ArrayList<>();

        String sql = "SELECT * FROM Customer_Card ORDER BY surname";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                Customer_Card card = new Customer_Card();

                card.setCard_number(rs.getString("card_number"));
                card.setSurname(rs.getString("surname"));
                card.setName(rs.getString("name"));
                card.setPatronymic(rs.getString("patronymic"));
                card.setPhone_number(rs.getString("phone_number"));
                card.setCity(rs.getString("city"));
                card.setStreet(rs.getString("street"));
                card.setZip_code(rs.getString("zip_code"));
                card.setPercent(rs.getInt("percent"));

                list.add(card);
            }
        }
        return list;
    }

    public List<Customer_Card> getCustomerCardsByPercent(int percent) throws SQLException {
        List<Customer_Card> list = new ArrayList<>();

        String sql = "SELECT * FROM Customer_Card WHERE percent = ? ORDER BY surname";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, percent);

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Customer_Card card = new Customer_Card();

                card.setCard_number(rs.getString("card_number"));
                card.setSurname(rs.getString("surname"));
                card.setName(rs.getString("name"));
                card.setPatronymic(rs.getString("patronymic"));
                card.setPhone_number(rs.getString("phone_number"));
                card.setCity(rs.getString("city"));
                card.setStreet(rs.getString("street"));
                card.setZip_code(rs.getString("zip_code"));
                card.setPercent(rs.getInt("percent"));

                list.add(card);
            }
        }
        return list;
    }

    public List<Customer_Card> searchCustomerCardsBySurname(String surname) throws SQLException {
        List<Customer_Card> list = new ArrayList<>();

        String sql = "SELECT * FROM Customer_Card WHERE surname LIKE ? ORDER BY surname";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, surname + "%");

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Customer_Card card = new Customer_Card();

                card.setCard_number(rs.getString("card_number"));
                card.setSurname(rs.getString("surname"));
                card.setName(rs.getString("name"));
                card.setPatronymic(rs.getString("patronymic"));
                card.setPhone_number(rs.getString("phone_number"));
                card.setCity(rs.getString("city"));
                card.setStreet(rs.getString("street"));
                card.setZip_code(rs.getString("zip_code"));
                card.setPercent(rs.getInt("percent"));

                list.add(card);
            }
        }
        return list;
    }
}