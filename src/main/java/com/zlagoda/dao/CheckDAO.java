package com.zlagoda.dao;

import com.zlagoda.model.Check;
import com.zlagoda.util.DatabaseConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CheckDAO {

    public void addCheck(Check check) throws SQLException {
        String sql = "INSERT INTO Receipt " +
                "(check_number, id_employee, card_number, print_date, sum_total, vat) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, check.getCheck_number());
            statement.setString(2, check.getId_employee());

            if (check.getCard_number() != null) {
                statement.setString(3, check.getCard_number());
            } else {
                statement.setNull(3, Types.VARCHAR);
            }

            statement.setTimestamp(4, Timestamp.valueOf(check.getPrint_date()));
            statement.setDouble(5, check.getSum_total());
            statement.setDouble(6, check.getVat());

            statement.executeUpdate();
        }
    }

    public void updateCheck(Check check) throws SQLException {
        String sql = "UPDATE Receipt SET " +
                "id_employee = ?, card_number = ?, print_date = ?, sum_total = ?, vat = ? " +
                "WHERE check_number = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, check.getId_employee());

            if (check.getCard_number() != null) {
                statement.setString(2, check.getCard_number());
            } else {
                statement.setNull(2, Types.VARCHAR);
            }

            statement.setTimestamp(3, Timestamp.valueOf(check.getPrint_date()));
            statement.setDouble(4, check.getSum_total());
            statement.setDouble(5, check.getVat());
            statement.setString(6, check.getCheck_number());

            statement.executeUpdate();
        }
    }

    // ще видалення !

    public List<Check> getChecksByPeriod(LocalDateTime start, LocalDateTime end) throws SQLException {
        List<Check> list = new ArrayList<>();

        String sql = "SELECT * FROM Receipt WHERE print_date BETWEEN ? AND ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setTimestamp(1, Timestamp.valueOf(start));
            statement.setTimestamp(2, Timestamp.valueOf(end));

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Check check = new Check();

                check.setCheck_number(rs.getString("check_number"));
                check.setId_employee(rs.getString("id_employee"));
                check.setCard_number(rs.getString("card_number"));
                check.setPrint_date(rs.getTimestamp("print_date").toLocalDateTime());
                check.setSum_total(rs.getDouble("sum_total"));
                check.setVat(rs.getDouble("vat"));

                list.add(check);
            }
        }
        return list;
    }

    public List<Check> getChecksByEmployeeAndPeriod(String id_employee, LocalDateTime start, LocalDateTime end) throws SQLException {
        List<Check> list = new ArrayList<>();

        String sql = "SELECT * FROM Receipt WHERE id_employee = ? AND print_date BETWEEN ? AND ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, id_employee);
            statement.setTimestamp(2, Timestamp.valueOf(start));
            statement.setTimestamp(3, Timestamp.valueOf(end));

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Check check = new Check();

                check.setCheck_number(rs.getString("check_number"));
                check.setId_employee(rs.getString("id_employee"));
                check.setCard_number(rs.getString("card_number"));
                check.setPrint_date(rs.getTimestamp("print_date").toLocalDateTime());
                check.setSum_total(rs.getDouble("sum_total"));
                check.setVat(rs.getDouble("vat"));

                list.add(check);
            }
        }
        return list;
    }

    public List<Check> getTodayChecksByEmployee(String id_employee) throws SQLException {
        List<Check> list = new ArrayList<>();

        String sql = "SELECT * FROM Receipt WHERE id_employee = ? AND DATE(print_date) = CURRENT_DATE";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, id_employee);

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Check check = new Check();

                check.setCheck_number(rs.getString("check_number"));
                check.setId_employee(rs.getString("id_employee"));
                check.setCard_number(rs.getString("card_number"));
                check.setPrint_date(rs.getTimestamp("print_date").toLocalDateTime());
                check.setSum_total(rs.getDouble("sum_total"));
                check.setVat(rs.getDouble("vat"));

                list.add(check);
            }
        }
        return list;
    }

    public double getCheckSumByPeriod(LocalDateTime start, LocalDateTime end) throws SQLException {
        String sql = "SELECT SUM(sum_total) FROM Receipt WHERE print_date BETWEEN ? AND ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setTimestamp(1, Timestamp.valueOf(start));
            statement.setTimestamp(2, Timestamp.valueOf(end));

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        return 0;
    }

    public double getCheckSumByEmployeeAndPeriod(String id_employee, LocalDateTime start, LocalDateTime end) throws SQLException {
        String sql = "SELECT SUM(sum_total) FROM Receipt WHERE id_employee = ? AND print_date BETWEEN ? AND ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, id_employee);
            statement.setTimestamp(2, Timestamp.valueOf(start));
            statement.setTimestamp(3, Timestamp.valueOf(end));

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        return 0;
    }
}