/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAOs;

import util.ConexionBD;
import model.Asset;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;



public class AssetDAO {

    
    public List<Asset> findAll(String search, String status) {
        List<Asset> assets = new ArrayList<>();

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT * FROM assets WHERE 1=1";

        if (search != null && !search.isEmpty()) {
            sql += " AND (type ILIKE ? OR brand ILIKE ? OR model ILIKE ? OR serial ILIKE ?)";
        }

        if (status != null && !status.isEmpty()) {
            sql += " AND status = ?";
        }

        try {
            conn = ConexionBD.getConnection();
            stmt = conn.prepareStatement(sql);

            int index = 1;

            if (search != null && !search.isEmpty()) {
                String like = "%" + search + "%";
                stmt.setString(index++, like);
                stmt.setString(index++, like);
                stmt.setString(index++, like);
                stmt.setString(index++, like);
            }

            if (status != null && !status.isEmpty()) {
                stmt.setString(index++, status);
            }

            rs = stmt.executeQuery();

            while (rs.next()) {
                Asset a = new Asset();

                a.setId(rs.getInt("id"));
                a.setType(rs.getString("type"));
                a.setBrand(rs.getString("brand"));
                a.setModel(rs.getString("model"));
                a.setSerial(rs.getString("serial"));
                a.setStatus(rs.getString("status"));
                a.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
                a.setUpdatedAt(rs.getObject("updated_at", LocalDateTime.class));

                assets.add(a);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignored) {}
            try { if (stmt != null) stmt.close(); } catch (Exception ignored) {}
            try { if (conn != null) conn.close(); } catch (Exception ignored) {}
        }

        return assets;
    }

    
    public List<Asset> available() {
        return findAll("", "EN_STOCK");
    }

    
    public Asset findById(int id) {
        Asset a = null;

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT * FROM assets WHERE id = ?";

        try {
            conn = ConexionBD.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);

            rs = stmt.executeQuery();

            if (rs.next()) {
                a = new Asset();

                a.setId(rs.getInt("id"));
                a.setType(rs.getString("type"));
                a.setBrand(rs.getString("brand"));
                a.setModel(rs.getString("model"));
                a.setSerial(rs.getString("serial"));
                a.setStatus(rs.getString("status"));
                a.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
                a.setUpdatedAt(rs.getObject("updated_at", LocalDateTime.class));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignored) {}
            try { if (stmt != null) stmt.close(); } catch (Exception ignored) {}
            try { if (conn != null) conn.close(); } catch (Exception ignored) {}
        }

        return a;
    }

    
    public boolean create(Asset a) {
        Connection conn = null;
        PreparedStatement stmt = null;

        String sql = "INSERT INTO assets (type, brand, model, serial, status, created_at, updated_at) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            conn = ConexionBD.getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, a.getType());
            stmt.setString(2, a.getBrand());
            stmt.setString(3, a.getModel());
            stmt.setString(4, a.getSerial());
            stmt.setString(5, a.getStatus());
            stmt.setObject(6, a.getCreatedAt());
            stmt.setObject(7, a.getUpdatedAt());

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;

        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception ignored) {}
            try { if (conn != null) conn.close(); } catch (Exception ignored) {}
        }
    }

   
    
    public boolean update(Asset a) {
        Connection conn = null;
        PreparedStatement stmt = null;

        String sql = "UPDATE assets SET type = ?, brand = ?, model = ?, serial = ?, status = ?, updated_at = ? "
                   + "WHERE id = ?";

        try {
            conn = ConexionBD.getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, a.getType());
            stmt.setString(2, a.getBrand());
            stmt.setString(3, a.getModel());
            stmt.setString(4, a.getSerial());
            stmt.setString(5, a.getStatus());
            stmt.setObject(6, a.getUpdatedAt());
            stmt.setInt(7, a.getId());

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;

        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception ignored) {}
            try { if (conn != null) conn.close(); } catch (Exception ignored) {}
        }
    }

    
    
    
    public boolean delete(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;

        String sql = "DELETE FROM assets WHERE id = ?";

        try {
            conn = ConexionBD.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;

        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception ignored) {}
            try { if (conn != null) conn.close(); } catch (Exception ignored) {}
        }
    }
}
