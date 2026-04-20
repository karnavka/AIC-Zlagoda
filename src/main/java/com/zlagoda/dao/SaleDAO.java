package com.zlagoda.dao;

import com.zlagoda.model.Sale;
import com.zlagoda.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SaleDAO {

    public void addSale(Sale sale) throws SQLException {

        String sql = "INSERT INTO Sale (UPC, check_number, product_number, selling_price) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, sale.getUPC());
            ps.setString(2, sale.getCheck_number());
            ps.setInt(3, sale.getProduct_number());
            ps.setDouble(4, sale.getSelling_price());

            ps.executeUpdate();
        }
    }

    public void updateSale(Sale sale) throws SQLException {

        String sql = "UPDATE Sale SET product_number = ?, selling_price = ? " +
                "WHERE UPC = ? AND check_number = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sale.getProduct_number());
            ps.setDouble(2, sale.getSelling_price());
            ps.setString(3, sale.getUPC());
            ps.setString(4, sale.getCheck_number());

            ps.executeUpdate();
        }
    }

    public void deleteSale(String upc, String checkNumber) throws SQLException {

        String sql = "DELETE FROM Sale WHERE UPC = ? AND check_number = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, upc);
            ps.setString(2, checkNumber);

            ps.executeUpdate();
        }
    }

    public List<Sale> getSalesByCheck(String checkNumber) throws SQLException {

        List<Sale> list = new ArrayList<>();

        String sql = "SELECT UPC, check_number, product_number, selling_price " +
                "FROM Sale WHERE check_number = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, checkNumber);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Sale sale = new Sale();

                sale.setUPC(rs.getString("UPC"));
                sale.setCheck_number(rs.getString("check_number"));
                sale.setProduct_number(rs.getInt("product_number"));
                sale.setSelling_price(rs.getDouble("selling_price"));

                list.add(sale);
            }
        }
        return list;
    }

    public int getTotalSoldByProductId(int productId, java.time.LocalDate dateFrom, java.time.LocalDate dateTo) throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT COALESCE(SUM(s.product_number), 0) AS total_sold " +
                        "FROM Sale s " +
                        "JOIN Store_Product sp ON s.UPC = sp.UPC " +
                        "JOIN Receipt r ON s.check_number = r.check_number " +
                        "WHERE sp.id_product = ?"
        );

        if (dateFrom != null) {
            sql.append(" AND DATE(r.print_date) >= ?");
        }
        if (dateTo != null) {
            sql.append(" AND DATE(r.print_date) <= ?");
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            ps.setInt(paramIndex++, productId);

            if (dateFrom != null) {
                ps.setDate(paramIndex++, Date.valueOf(dateFrom));
            }
            if (dateTo != null) {
                ps.setDate(paramIndex++, Date.valueOf(dateTo));
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total_sold");
                }
            }
        }

        return 0;
    }

    public int getTotalSoldByUpc(String upc, java.time.LocalDate dateFrom, java.time.LocalDate dateTo) throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT COALESCE(SUM(s.product_number), 0) AS total_sold " +
                        "FROM Sale s " +
                        "JOIN Receipt r ON s.check_number = r.check_number " +
                        "WHERE s.UPC = ?"
        );

        if (dateFrom != null) {
            sql.append(" AND DATE(r.print_date) >= ?");
        }
        if (dateTo != null) {
            sql.append(" AND DATE(r.print_date) <= ?");
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            ps.setString(paramIndex++, upc);

            if (dateFrom != null) {
                ps.setDate(paramIndex++, Date.valueOf(dateFrom));
            }
            if (dateTo != null) {
                ps.setDate(paramIndex++, Date.valueOf(dateTo));
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total_sold");
                }
            }
        }

        return 0;
    }
}