/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import  DAOs.EmployeeDAO;
import model.Employee;
import java.util.List;

/**
 * Controlador para la lógica de empleados.
 * Actúa como intermediario entre los servlets y el DAO.
 */
public class EmployeeController {

    private EmployeeDAO dao = new EmployeeDAO();

    public List<Employee> getAll(String search, String department) {
        return dao.findAll(search, department);
    }

    public Employee getById(int id) {
        return dao.findById(id);
    }

    public boolean create(Employee emp) {
        return dao.create(emp);
    }

    public boolean update(Employee emp) {
        return dao.update(emp);
    }

    public boolean delete(int id) {
        return dao.delete(id);
    }
}
