/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAOs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;
import model.Employee;

import util.ConexionBD;


/**
 *
 * @author isierra
 */

public class EmployeeDAO {

    public List<Employee> findAll(String search, String department) {
        List<Employee> empleados = new ArrayList<>();

        // Aquí irán la conexión, la query, el resultset, etc.


        //VARIABLES
        Connection conn = null; //la conexión a la base de datos
        PreparedStatement stmt = null; //la consulta SQL preparada - preparar la query.
        ResultSet rs = null; //las filas que devuelve la base de datos

//        String sql = "SELECT * FROM employees";//query base (consulta a la bbdd)
        
        //query dinamica
        String sql = "SELECT * FROM employees WHERE 1=1";

        if (search != null && !search.isEmpty()) {
            sql += " AND (name ILIKE ? OR surname ILIKE ?)";
        }

        if (department != null && !department.isEmpty()) {
            sql += " AND department = ?";
        }


    
        //preparación de la PreparedStatement
        try {
            conn = ConexionBD.getConnection(); // Abrimos conexión
            
            
//            stmt = conn.prepareStatement(sql); // Preparamos la query 
//            rs = stmt.executeQuery(); // Ejecutamos la query y obtenemos las filas

            //versión con parámetros
            stmt = conn.prepareStatement(sql);

            int index = 1;

            if (search != null && !search.isEmpty()) {
                stmt.setString(index++, "%" + search + "%");
                stmt.setString(index++, "%" + search + "%");
            }

            if (department != null && !department.isEmpty()) {
                stmt.setString(index++, department);
            }

            rs = stmt.executeQuery();


            while (rs.next()) { //avanza fila por fila
                Employee emp = new Employee();

                emp.setId(rs.getInt("id"));
                emp.setName(rs.getString("name"));
                emp.setSurname(rs.getString("surname"));
                emp.setEmail(rs.getString("email"));
                emp.setDepartment(rs.getString("department"));
                emp.setActive(rs.getBoolean("active"));
                emp.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
                emp.setUpdatedAt(rs.getObject("updated_at", LocalDateTime.class));

                empleados.add(emp);
            }
        
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return empleados;
    }
    
    
    
    
    
    
    public Employee findById(int id) {

        // Este objeto será el que devolvamos al final.
        // Si no se encuentra ningún empleado, se devolverá null.
        Employee emp = null;

        // Objetos necesarios para trabajar con JDBC
        Connection conn = null;        // Conexión a la base de datos
        PreparedStatement stmt = null; // Consulta SQL preparada
        ResultSet rs = null;           // Resultado de la consulta (las filas)

        // Consulta SQL para buscar un empleado por su ID
        // El ? indica que luego asignaremos un valor (el id)
        String sql = "SELECT * FROM employees WHERE id = ?";

        try {
            // 1. Abrimos la conexión con la base de datos
            conn = ConexionBD.getConnection();

            // 2. Preparamos la consulta SQL
            stmt = conn.prepareStatement(sql);

            // 3. Asignamos el valor del parámetro (el ID del empleado)
            // El primer ? recibe el valor de "id"
            stmt.setInt(1, id);

            // 4. Ejecutamos la consulta y obtenemos el resultado
            rs = stmt.executeQuery();

            // 5. Como buscamos un solo empleado, usamos IF en lugar de WHILE
            if (rs.next()) {
                // Si existe una fila, creamos el objeto Employee
                emp = new Employee();

                // 6. Rellenamos el objeto con los datos de la fila
                emp.setId(rs.getInt("id"));
                emp.setName(rs.getString("name"));
                emp.setSurname(rs.getString("surname"));
                emp.setEmail(rs.getString("email"));
                emp.setDepartment(rs.getString("department"));
                emp.setActive(rs.getBoolean("active"));

                // Para columnas tipo timestamp → LocalDateTime
                emp.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
                emp.setUpdatedAt(rs.getObject("updated_at", LocalDateTime.class));
            }

        } catch (Exception e) {
            // Si ocurre un error, lo mostramos en consola
            e.printStackTrace();

        } finally {
            // 7. Cerramos los recursos en orden inverso
            try {
                if (rs != null) rs.close();     // Cerrar ResultSet
                if (stmt != null) stmt.close(); // Cerrar PreparedStatement
                if (conn != null) conn.close(); // Cerrar conexión
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 8. Devolvemos el empleado encontrado (o null si no existe)
        return emp;
    }

    

    
    public boolean create(Employee emp) {

        Connection conn = null;
        PreparedStatement stmt = null;

        // Consulta SQL para insertar un nuevo empleado
        String sql = "INSERT INTO employees (name, surname, email, department, active, created_at, updated_at) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            // 1. Abrimos conexión
            conn = ConexionBD.getConnection();

            // 2. Preparamos la consulta
            stmt = conn.prepareStatement(sql);

            // 3. Asignamos los valores a los ?
            stmt.setString(1, emp.getName());
            stmt.setString(2, emp.getSurname());
            stmt.setString(3, emp.getEmail());
            stmt.setString(4, emp.getDepartment());
            stmt.setBoolean(5, emp.isActive());
            stmt.setObject(6, emp.getCreatedAt());
            stmt.setObject(7, emp.getUpdatedAt());

            // 4. Ejecutamos la inserción
            int filas = stmt.executeUpdate();

            // Si filas > 0, se insertó correctamente
            return filas > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;

        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    
    
    
    
    
    public boolean update(Employee emp) {

        Connection conn = null;
        PreparedStatement stmt = null;

        // Consulta SQL para actualizar un empleado por su ID
        String sql = "UPDATE employees SET name = ?, surname = ?, email = ?, department = ?, active = ?, updated_at = ? "
                   + "WHERE id = ?";

        try {
            // 1. Abrimos conexión
            conn = ConexionBD.getConnection();

            // 2. Preparamos la consulta
            stmt = conn.prepareStatement(sql);

            // 3. Asignamos los valores a los ?
            stmt.setString(1, emp.getName());
            stmt.setString(2, emp.getSurname());
            stmt.setString(3, emp.getEmail());
            stmt.setString(4, emp.getDepartment());
            stmt.setBoolean(5, emp.isActive());
            stmt.setObject(6, emp.getUpdatedAt());

            // El último parámetro es el ID del empleado a actualizar
            stmt.setInt(7, emp.getId());

            // 4. Ejecutamos la actualización
            int filas = stmt.executeUpdate();

            return filas > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;

        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    
    
    
    
    
    
    public boolean delete(int id) {

        Connection conn = null;
        PreparedStatement stmt = null;

        // Consulta SQL para borrar un empleado por su ID
        String sql = "DELETE FROM employees WHERE id = ?";

        try {
            // 1. Abrimos conexión
            conn = ConexionBD.getConnection();

            // 2. Preparamos la consulta
            stmt = conn.prepareStatement(sql);

            // 3. Asignamos el ID al ?
            stmt.setInt(1, id);

            // 4. Ejecutamos el borrado
            int filas = stmt.executeUpdate();

            return filas > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;

        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    
}
