/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAOs;


import util.ConexionBD;
import model.Assignment;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class AssignmentDAO {

    
    public List<Assignment> findByEmployeeId(int employeeId) {
        List<Assignment> list = new ArrayList<>();

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT * FROM assignments WHERE employee_id = ?";

        try {
            conn = ConexionBD.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, employeeId);

            rs = stmt.executeQuery();

            while (rs.next()) {
                Assignment a = new Assignment();

                a.setId(rs.getInt("id"));
                a.setEmployeeId(rs.getInt("employee_id"));
                a.setAssetId(rs.getInt("asset_id"));
                a.setStartDate(rs.getObject("start_date", LocalDateTime.class));
                a.setEndDate(rs.getObject("end_date", LocalDateTime.class));
                a.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));

                list.add(a);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignored) {}
            try { if (stmt != null) stmt.close(); } catch (Exception ignored) {}
            try { if (conn != null) conn.close(); } catch (Exception ignored) {}
        }

        return list;
    }

    
    public boolean assign(int employeeId, int assetId) {
        Connection conn = null;
        PreparedStatement stmt = null;

        String sql = "INSERT INTO assignments (employee_id, asset_id) VALUES (?, ?)";

        try {
            conn = ConexionBD.getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, employeeId);
            stmt.setInt(2, assetId);

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;

        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception ignored) {}
            try { if (conn != null) conn.close(); } catch (Exception ignored) {}
        }
    }

    
    public boolean returnAsset(int assignmentId) {
        Connection conn = null;
        PreparedStatement stmt = null;

        String sql = "UPDATE assignments SET end_date = NOW() WHERE id = ?";

        try {
            conn = ConexionBD.getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, assignmentId);

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;

        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception ignored) {}
            try { if (conn != null) conn.close(); } catch (Exception ignored) {}
        }
    }
    
    
    
    public int countActive() {
        String sql = "SELECT COUNT(*) FROM assignments WHERE end_date IS NULL";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;

        } catch (SQLException e) {
            e.printStackTrace();
            
            return 0;
        }
    }

}
