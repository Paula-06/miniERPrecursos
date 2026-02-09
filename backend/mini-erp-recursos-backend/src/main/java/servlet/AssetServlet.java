/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servlet;

import controller.AssetController;
import model.Asset;
import util.JsonUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.servlet.ServletException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * API REST de activos.
 *
 * Endpoints:
 *  - GET    /api/assets?search=&status=
 *  - POST   /api/assets
 *  - GET    /api/assets/{id}
 *  - PUT    /api/assets/{id}
 *  - DELETE /api/assets/{id}
 *  - GET    /api/assets/available
 *
 * Validaciones mínimas:
 *  - serial obligatorio y único (si duplicado → 409)
 *  - status ∈ {EN_STOCK, ASIGNADO, BAJA}
 */
@WebServlet("/api/assets/*")
public class AssetServlet extends HttpServlet {

    private AssetController controller = new AssetController();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        String path = req.getPathInfo();

        // GET /api/assets/available
        if ("/available".equals(path)) {
            List<Asset> list = controller.available();
            resp.setStatus(200);
            resp.getWriter().write(JsonUtil.gson.toJson(list));
            return;
        }

        // GET /api/assets → lista con filtros
        if (path == null || path.equals("/")) {
            String search = req.getParameter("search");
            String status = req.getParameter("status");

            List<Asset> list = controller.getAll(search, status);

            resp.setStatus(200);
            resp.getWriter().write(JsonUtil.gson.toJson(list));
            return;
        }

        // GET /api/assets/{id}
        try {
            int id = Integer.parseInt(path.substring(1));
            Asset asset = controller.getById(id);

            if (asset == null) {
                resp.setStatus(404);
                resp.getWriter().write("{\"error\":\"Activo no encontrado\"}");
                return;
            }

            resp.setStatus(200);
            resp.getWriter().write(JsonUtil.gson.toJson(asset));

        } catch (NumberFormatException e) {
            resp.setStatus(400);
            resp.getWriter().write("{\"error\":\"ID inválido\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("application/json");

        Asset asset = JsonUtil.gson.fromJson(req.getReader(), Asset.class);

        // Validación: serial obligatorio
        if (asset.getSerial() == null || asset.getSerial().isEmpty()) {
            resp.setStatus(400);
            resp.getWriter().write("{\"error\":\"serial es obligatorio\"}");
            return;
        }

        // Validación: status válido
        if (!isValidStatus(asset.getStatus())) {
            resp.setStatus(400);
            resp.getWriter().write("{\"error\":\"status inválido\"}");
            return;
        }

        asset.setCreatedAt(LocalDateTime.now());
        asset.setUpdatedAt(LocalDateTime.now());

        // El DAO debe devolver false si hay conflicto de serial
        boolean created = controller.create(asset);

        if (!created) {
            resp.setStatus(409);
            resp.getWriter().write("{\"error\":\"serial duplicado\"}");
            return;
        }

        resp.setStatus(201);
        resp.getWriter().write("{\"message\":\"Activo creado\"}");
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

        Asset existing = controller.getById(id);
        if (existing == null) {
            resp.setStatus(404);
            resp.getWriter().write("{\"error\":\"Activo no encontrado\"}");
            return;
        }

        Asset body = JsonUtil.gson.fromJson(req.getReader(), Asset.class);

        existing.setType(body.getType());
        existing.setBrand(body.getBrand());
        existing.setModel(body.getModel());
        existing.setSerial(body.getSerial());
        existing.setStatus(body.getStatus());
        existing.setUpdatedAt(LocalDateTime.now());

        if (!isValidStatus(existing.getStatus())) {
            resp.setStatus(400);
            resp.getWriter().write("{\"error\":\"status inválido\"}");
            return;
        }

        boolean updated = controller.update(existing);

        if (!updated) {
            // Puede ser por serial duplicado
            resp.setStatus(409);
            resp.getWriter().write("{\"error\":\"serial duplicado\"}");
            return;
        }

        resp.setStatus(200);
        resp.getWriter().write("{\"message\":\"Activo actualizado\"}");
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
            resp.getWriter().write("{\"error\":\"Activo no encontrado\"}");
            return;
        }

        resp.setStatus(200);
        resp.getWriter().write("{\"message\":\"Activo eliminado\"}");
    }

    /**
     * Comprueba que el estado es uno de los permitidos.
     */
    private boolean isValidStatus(String status) {
        if (status == null) return false;
        return status.equals("EN_STOCK") ||
               status.equals("ASIGNADO") ||
               status.equals("BAJA");
    }
}

