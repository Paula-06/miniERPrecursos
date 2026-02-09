/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAOs;


import util.ConexionBD;
import model.User;

import java.sql.*;


public class UserDAO {

    
    public User validate(String username, String passwordHash) {
        System.out.println("🔥🔥🔥 ENTRA EN VALIDATE() 🔥🔥🔥");
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        // Buscamos un usuario con ese username y ese hash
        String sql = "SELECT * FROM users WHERE username = ? AND password_hash = ?";

        try {
            
            conn = ConexionBD.getConnection();

            // 🔥 TEST DE CONEXIÓN, PÉGALO AQUÍ
            System.out.println("========= TEST CONEXIÓN =========");

            try {
                System.out.println("[DB] URL  = " + conn.getMetaData().getURL());
                System.out.println("[DB] USER = " + conn.getMetaData().getUserName());
            } catch (Exception e) {
                System.out.println("[DB] No se pudo obtener metadata de la conexión");
                e.printStackTrace();
            }

            try (Statement st = conn.createStatement()) {
                ResultSet r = st.executeQuery("SELECT 1");
                if (r.next()) {
                    System.out.println("[DB] SELECT 1 = OK ✓");
                }
            } catch (Exception e) {
                System.out.println("[DB] ERROR ejecutando SELECT 1 ✗");
                e.printStackTrace();
            }

            try (Statement st = conn.createStatement()) {
                ResultSet r = st.executeQuery("SELECT COUNT(*) FROM users");
                if (r.next()) {
                    System.out.println("[DB] Tabla users encontrada. Registros = " + r.getInt(1));
                }
            } catch (Exception e) {
                System.out.println("[DB] ERROR: La tabla 'users' NO existe en esta BD.");
                e.printStackTrace();
            }

            System.out.println("=================================");
            
            
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, username);
            stmt.setString(2, passwordHash);

            rs = stmt.executeQuery();

            if (rs.next()) {
                User u = new User();

                u.setId(rs.getInt("id"));
                u.setUsername(rs.getString("username"));
                u.setPasswordHash(rs.getString("password_hash"));
                u.setRole(rs.getString("role"));
                u.setCreatedAt(rs.getObject("created_at", java.time.LocalDateTime.class));

                return u; // usuario válido
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignored) {}
            try { if (stmt != null) stmt.close(); } catch (Exception ignored) {}
            try { if (conn != null) conn.close(); } catch (Exception ignored) {}
        }

        return null; // usuario no encontrado
    }
}
