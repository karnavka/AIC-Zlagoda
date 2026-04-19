package com.zlagoda.dao;

import com.zlagoda.dto.CheckDetailsDTO;
import com.zlagoda.model.Check;
import com.zlagoda.util.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
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

    // ще видалення ! --додалааа

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

        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();

        String sql = """
        SELECT *
        FROM Receipt
        WHERE id_employee = ?
          AND print_date >= ?
          AND print_date < ?
        ORDER BY print_date DESC
    """;

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

    public List<CheckDetailsDTO> getCheckDetails(String checkNumber) throws SQLException {

        List<CheckDetailsDTO> list = new ArrayList<>();

        String sql = "SELECT * FROM check_details_view WHERE check_number = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, checkNumber);
            ResultSet rs = ps.executeQuery();

            List<CheckDetailsDTO> result = new ArrayList<>();

            while (rs.next()) {
                CheckDetailsDTO dto = new CheckDetailsDTO();

                dto.setCheck_number(rs.getString("check_number"));
                dto.setPrint_date(rs.getTimestamp("print_date").toLocalDateTime());
                dto.setId_employee(rs.getString("id_employee"));
                dto.setCard_number(rs.getString("card_number"));
                dto.setUPC(rs.getString("UPC"));
                dto.setName(rs.getString("product_name"));
                dto.setProduct_number(rs.getInt("product_number"));
                dto.setSelling_price(rs.getDouble("selling_price"));

                result.add(dto);
            }
            return result;
        }
    }
    public boolean existsByCheckNumber(String checkNumber) throws SQLException {
        String sql = "SELECT 1 FROM Receipt WHERE check_number = ? LIMIT 1";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, checkNumber);

            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        }
    }
    public Check getCheckByNumberAndEmployee(String checkNumber, String employeeId) throws SQLException {
        String sql = """
        SELECT *
        FROM Receipt
        WHERE check_number = ? AND id_employee = ?
    """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, checkNumber);
            statement.setString(2, employeeId);

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                Check check = new Check();
                check.setCheck_number(rs.getString("check_number"));
                check.setId_employee(rs.getString("id_employee"));
                check.setCard_number(rs.getString("card_number"));
                check.setPrint_date(rs.getTimestamp("print_date").toLocalDateTime());
                check.setSum_total(rs.getDouble("sum_total"));
                check.setVat(rs.getDouble("vat"));
                return check;
            }
        }

        return null;
    }
    public void deleteCheck(String checkNumber) throws SQLException {
        String deleteSalesSql = "DELETE FROM Sale WHERE check_number = ?";
        String deleteReceiptSql = "DELETE FROM Receipt WHERE check_number = ?";

        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false);

            try (PreparedStatement salesStmt = connection.prepareStatement(deleteSalesSql);
                 PreparedStatement receiptStmt = connection.prepareStatement(deleteReceiptSql)) {

                salesStmt.setString(1, checkNumber);
                salesStmt.executeUpdate();

                receiptStmt.setString(1, checkNumber);
                receiptStmt.executeUpdate();
            }

            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (connection != null) {
                connection.setAutoCommit(true);
                connection.close();
            }
        }
    }
}