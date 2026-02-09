/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servlet;

import controller.AssignmentController;
import controller.AssetController;
import controller.EmployeeController;
import model.Assignment;
import model.Asset;
import model.Employee;
import util.JsonUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.List;

/**
 * API REST de asignaciones.
 *
 * Endpoints:
 *  - GET  /api/employees/{id}/assignments
 *  - POST /api/assignments
 *  - POST /api/assignments/{id}/return
 *
 * Reglas:
 *  - 201 Created si asigna
 *  - 409 Conflict si el activo ya estaba asignado
 *  - 404 si empleado o activo no existe
 *  - Al asignar → assets.status = ASIGNADO
 *  - Al devolver → assets.status = EN_STOCK
 */
@WebServlet({"/api/employees/assignments/*", "/api/assignments/*"})
public class AssignmentServlet extends HttpServlet {

    private AssignmentController assignmentController = new AssignmentController();
    private EmployeeController employeeController   = new EmployeeController();
    private AssetController assetController         = new AssetController();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {

        resp.setContentType("application/json");
        String uri = req.getRequestURI();
        String[] parts = uri.split("/");

        // GET /api/employees/{id}/assignments
        if (uri.matches(".*/api/employees/assignments/\\d+$")) {

            // Buscar el índice donde está el ID según la nueva ruta:
            // /api/employees/assignments/{id}
            int idIndex = -1;

            for (int i = 0; i < parts.length; i++) {
                // Ahora buscamos "assignments" porque el ID viene después
                if (parts[i].equals("assignments")) {
                    idIndex = i + 1;   // El ID está justo después de "assignments"
                    break;
                }
            }

            // Validar que el índice existe y no se sale del array
            if (idIndex == -1 || idIndex >= parts.length) {
                resp.setStatus(400);
                resp.getWriter().write("{\"error\":\"ID inválido\"}");
                return;
            }

            int id;
            try {
                // Intentar convertir la parte encontrada en número
                id = Integer.parseInt(parts[idIndex]);
            } catch (NumberFormatException e) {
                resp.setStatus(400);
                resp.getWriter().write("{\"error\":\"ID inválido\"}");
                return;
            }


            Employee emp = employeeController.getById(id);
            if (emp == null) {
                resp.setStatus(404);
                resp.getWriter().write("{\"error\":\"Empleado no encontrado\"}");
                return;
            }

            List<Assignment> list = assignmentController.findByEmployeeId(id);

            resp.setStatus(200);
            resp.getWriter().write(JsonUtil.gson.toJson(list));
            return;
        }

        resp.setStatus(404);
        resp.getWriter().write("{\"error\":\"Ruta no encontrada\"}");
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("application/json");
        String uri = req.getRequestURI();

        // POST /api/assignments
        if (uri.endsWith("/api/assignments")) {
            createAssignment(req, resp);
            return;
        }

        // POST /api/assignments/{id}/return
        if (uri.contains("/api/assignments/") && uri.endsWith("/return")) {
            String[] parts = uri.split("/");
            int id;
            try {
                id = Integer.parseInt(parts[parts.length - 2]);
            } catch (NumberFormatException e) {
                resp.setStatus(400);
                resp.getWriter().write("{\"error\":\"ID inválido\"}");
                return;
            }

            returnAssignment(id, resp);
            return;
        }

        resp.setStatus(404);
        resp.getWriter().write("{\"error\":\"Ruta no encontrada\"}");
    }

    /**
     * Crea una nueva asignación.
     * Body: { "employeeId": 1, "assetId": 10 }
     */
    private void createAssignment(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        AssignmentBody body = JsonUtil.gson.fromJson(req.getReader(), AssignmentBody.class);

        if (body == null || body.employeeId == null || body.assetId == null) {
            resp.setStatus(400);
            resp.getWriter().write("{\"error\":\"employeeId y assetId son obligatorios\"}");
            return;
        }

        // Comprobar que existen empleado y activo
        Employee emp = employeeController.getById(body.employeeId);
        if (emp == null) {
            resp.setStatus(404);
            resp.getWriter().write("{\"error\":\"Empleado no encontrado\"}");
            return;
        }

        Asset asset = assetController.getById(body.assetId);
        if (asset == null) {
            resp.setStatus(404);
            resp.getWriter().write("{\"error\":\"Activo no encontrado\"}");
            return;
        }

        // Comprobar si el activo ya está asignado (status ASIGNADO)
        if ("ASIGNADO".equals(asset.getStatus())) {
            resp.setStatus(409);
            resp.getWriter().write("{\"error\":\"El activo ya está asignado\"}");
            return;
        }

        // Crear asignación
        boolean ok = assignmentController.assign(body.employeeId, body.assetId);

        if (!ok) {
            // Por si el DAO detecta conflicto adicional
            resp.setStatus(409);
            resp.getWriter().write("{\"error\":\"No se pudo asignar el activo\"}");
            return;
        }

        // Actualizar estado del activo a ASIGNADO
        asset.setStatus("ASIGNADO");
        assetController.update(asset);

        resp.setStatus(201);
        resp.getWriter().write("{\"message\":\"Activo asignado\"}");
    }

    /**
     * Marca una asignación como devuelta.
     * POST /api/assignments/{id}/return
     */
    private void returnAssignment(int assignmentId, HttpServletResponse resp)
            throws IOException {

        // Primero marcamos la asignación como devuelta (end_date = NOW())
        boolean ok = assignmentController.returnAsset(assignmentId);

        if (!ok) {
            resp.setStatus(404);
            resp.getWriter().write("{\"error\":\"Asignación no encontrada\"}");
            return;
        }

        // Aquí, idealmente, el DAO de Assignment debería permitir obtener el asset_id
        // asociado a esa asignación para poder cambiar el estado del activo.
        // Suponiendo que ya lo tienes resuelto en tu DAO, aquí solo dejamos el comentario.

        // Ejemplo (si tuvieras un método findById en AssignmentDAO):
        // Assignment a = assignmentController.getById(assignmentId);
        // Asset asset = assetController.getById(a.getAssetId());
        // asset.setStatus("EN_STOCK");
        // assetController.update(asset);

        resp.setStatus(200);
        resp.getWriter().write("{\"message\":\"Activo devuelto\"}");
    }

    /**
     * Clase interna para mapear el JSON de creación de asignación.
     */
    private static class AssignmentBody {
        Integer employeeId;
        Integer assetId;
    }
}
