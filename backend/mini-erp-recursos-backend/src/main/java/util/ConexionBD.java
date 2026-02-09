/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;
// El paquete donde guardamos clases de utilidad (no son DAOs ni modelos)

/**
 *
 * @author isierra
 */ 
import java.sql.Connection;
import java.sql.DriverManager;
// Importamos las clases necesarias para conectarnos a la base de datos

public class ConexionBD {

    // URL de conexión a PostgreSQL.
    // Cambia "TU_BASE" por el nombre real de tu base de datos.
    private static final String URL = "jdbc:postgresql://localhost:5432/erp_recursos_db";

    // Usuario de PostgreSQL (por defecto suele ser "postgres")
    private static final String USER = "postgres";

    // Contraseña del usuario de PostgreSQL
    private static final String PASS = "admin";

    // Método estático que devuelve una conexión lista para usar
    public static Connection getConnection() {
        Connection conn = null;

        try {
            Class.forName("org.postgresql.Driver"); // ← IMPORTANTE
            conn = DriverManager.getConnection(URL, USER, PASS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return conn;
    }

}

