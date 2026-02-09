/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servlet;

import controller.EmployeeController;
import controller.AssetController;
import controller.AssignmentController;
import util.JsonUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 * API REST de estadísticas para el dashboard.
 *
 * GET /api/stats
 *
 * Devuelve JSON:
 * {
 *   "employees": 10,
 *   "assets": 20,
 *   "assignedAssets": 3,
 *   "availableAssets": 17
 * }
 *
 * IMPORTANTE:
 *  - En tu base de datos NO existe un campo "status" en assignments.
 *  - Un activo está asignado si existe una fila en assignments con end_date IS NULL.
 *  - Por eso, assignedAssets debe calcularse desde la tabla assignments, NO desde assets.
 */
@WebServlet("/api/stats")
public class StatsServlet extends HttpServlet {

    private EmployeeController employeeController     = new EmployeeController();
    private AssetController assetController           = new AssetController();
    private AssignmentController assignmentController = new AssignmentController();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");

        // Total de empleados
        int employees = employeeController.getAll(null, null).size();

        // Total de activos
        int assets = assetController.getAll(null, null).size();

        /**
         * ACTIVOS ASIGNADOS (CORRECTO SEGÚN TU BBDD Y LA PRÁCTICA)
         *
         * Un activo está asignado si existe una fila en assignments con end_date = NULL.
         * Por tanto, NO debemos mirar asset.status, sino contar asignaciones activas.
         *
         * Este método debe existir en AssignmentController y llamar al DAO:
         * SELECT COUNT(*) FROM assignments WHERE end_date IS NULL;
         */
        int assignedAssets = assignmentController.countActiveAssignments();

        /**
         * ACTIVOS DISPONIBLES
         *
         * Si un activo está asignado, no está disponible.
         * Por tanto:
         * disponibles = total activos - activos asignados
         */
        int availableAssets = assets - assignedAssets;

        // Construimos la respuesta JSON
        StatsResponse stats = new StatsResponse();
        stats.employees       = employees;
        stats.assets          = assets;
        stats.assignedAssets  = assignedAssets;
        stats.availableAssets = availableAssets;

        resp.setStatus(200);
        resp.getWriter().write(JsonUtil.gson.toJson(stats));
    }

    /**
     * Clase interna para mapear la respuesta JSON de stats.
     */
    private static class StatsResponse {
        int employees;
        int assets;
        int assignedAssets;
        int availableAssets;
    }
}

