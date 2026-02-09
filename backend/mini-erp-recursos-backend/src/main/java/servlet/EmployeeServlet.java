/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package servlet;

import controller.EmployeeController;
import model.Employee;
import util.JsonUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.servlet.ServletException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * API REST de empleados.
 *
 * Endpoints:
 *  - GET    /api/employees?search=&department=   → lista JSON
 *  - POST   /api/employees                       → crear empleado
 *  - GET    /api/employees/{id}                  → obtener uno
 *  - PUT    /api/employees/{id}                  → actualizar
 *  - DELETE /api/employees/{id}                  → eliminar
 *
 * Validaciones mínimas:
 *  - name, surname, email obligatorios
 *  - si id no existe → 404
 */
@WebServlet("/api/employees/*")
public class EmployeeServlet extends HttpServlet {

    private EmployeeController controller = new EmployeeController();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        String path = req.getPathInfo(); // parte después de /api/employees

        // GET /api/employees → lista
        if (path == null || path.equals("/")) {
            String search = req.getParameter("search");
            String department = req.getParameter("department");

            List<Employee> list = controller.getAll(search, department);

            resp.setStatus(200);
            resp.getWriter().write(JsonUtil.gson.toJson(list));
            return;
        }

        // GET /api/employees/{id}
        try {
            int id = Integer.parseInt(path.substring(1)); // quitamos la barra inicial
            Employee emp = controller.getById(id);

            if (emp == null) {
                resp.setStatus(404);
                resp.getWriter().write("{\"error\":\"Empleado no encontrado\"}");
                return;
            }

            resp.setStatus(200);
            resp.getWriter().write(JsonUtil.gson.toJson(emp));

        } catch (NumberFormatException e) {
            resp.setStatus(400);
            resp.getWriter().write("{\"error\":\"ID inválido\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("application/json");

        // Gson convierte el JSON del body en un Employee
        Employee emp = JsonUtil.gson.fromJson(req.getReader(), Employee.class);

        // Validaciones mínimas
        if (emp.getName() == null || emp.getSurname() == null || emp.getEmail() == null) {
            resp.setStatus(400);
            resp.getWriter().write("{\"error\":\"name, surname y email son obligatorios\"}");
            return;
        }

        // Campos de auditoría
        emp.setCreatedAt(LocalDateTime.now());
        emp.setUpdatedAt(LocalDateTime.now());

        controller.create(emp);

        resp.setStatus(201);
        resp.getWriter().write("{\"message\":\"Empleado creado\"}");
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("application/json");

        String path = req.getPathInfo();
        if (path == null || path.equals("/")) {
            resp.setStatus(400);
            resp.getWriter().write("{\"error\":\"ID requerido\"}");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(path.substring(1));
        } catch (NumberFormatException e) {
            resp.setStatus(400);
            resp.getWriter().write("{\"error\":\"ID inválido\"}");
            return;
        }

        // Comprobamos si existe
        Employee existing = controller.getById(id);
        if (existing == null) {
            resp.setStatus(404);
            resp.getWriter().write("{\"error\":\"Empleado no encontrado\"}");
            return;
        }

        // Leemos el body con los nuevos datos
        Employee body = JsonUtil.gson.fromJson(req.getReader(), Employee.class);

        existing.setName(body.getName());
        existing.setSurname(body.getSurname());
        existing.setEmail(body.getEmail());
        existing.setDepartment(body.getDepartment());
        existing.setActive(body.isActive());
        existing.setUpdatedAt(LocalDateTime.now());

        controller.update(existing);

        resp.setStatus(200);
        resp.getWriter().write("{\"message\":\"Empleado actualizado\"}");
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("application/json");

        String path = req.getPathInfo();
        if (path == null || path.equals("/")) {
            resp.setStatus(400);
            resp.getWriter().write("{\"error\":\"ID requerido\"}");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(path.substring(1));
        } catch (NumberFormatException e) {
            resp.setStatus(400);
            resp.getWriter().write("{\"error\":\"ID inválido\"}");
            return;
        }

        boolean deleted = controller.delete(id);

        if (!deleted) {
            resp.setStatus(404);
            resp.getWriter().write("{\"error\":\"Empleado no encontrado\"}");
            return;
        }

        resp.setStatus(200);
        resp.getWriter().write("{\"message\":\"Empleado eliminado\"}");
    }
}
