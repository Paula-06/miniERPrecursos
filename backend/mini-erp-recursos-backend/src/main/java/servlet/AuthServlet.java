/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package servlet;

import controller.UserController;
import model.User;
import util.JsonUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Servlet REST para autenticación.
 *
 * Rutas:
 *  - POST /api/auth/login   → login con JSON { "username", "password" }
 *  - POST /api/auth/logout  → cierra la sesión actual
 *
 * NOTA: El filtro AuthFilter permite /api/auth/login sin sesión,
 *       pero exige sesión para el resto de /api/*.
 */
@WebServlet("/api/auth/*")
public class AuthServlet extends HttpServlet {

    // Usamos el controlador para no hablar directamente con el DAO
    private UserController controller = new UserController();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");

        // pathInfo es la parte después de /api/auth
        // Ej: /login, /logout
        String path = req.getPathInfo();
        if (path == null) path = "";


        if (path.endsWith("/login")) {
            login(req, resp);
        } else if (path.endsWith("/logout")) {
            logout(req, resp);
        } else {
            resp.setStatus(404);
            resp.getWriter().write("{\"error\":\"Ruta no encontrada\"}");
        }

    }

    /**
     * Maneja el login.
     * Lee JSON del body, valida usuario y crea sesión si es correcto.
     */
    private void login(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        // Gson convierte el JSON del body en un objeto LoginBody
        LoginBody body = JsonUtil.gson.fromJson(req.getReader(), LoginBody.class);

        // Validación básica: campos obligatorios
        if (body == null || body.username == null || body.password == null) {
            resp.setStatus(400);
            resp.getWriter().write("{\"error\":\"username y password requeridos\"}");
            return;
        }

 
        // Validamos contra la BD a través del controlador
        User u = controller.validate(body.username, body.password);

        if (u == null) {
            // Credenciales incorrectas
            resp.setStatus(401);
            resp.getWriter().write("{\"error\":\"Credenciales inválidas\"}");
            return;
        }

        // Credenciales correctas → creamos sesión
        HttpSession session = req.getSession(true);
        session.setAttribute("user", u);

        resp.setStatus(200);
        resp.getWriter().write("{\"message\":\"Login correcto\"}");
    }

    /**
     * Maneja el logout.
     * Invalida la sesión actual si existe.
     */
    private void logout(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        resp.setStatus(200);
        resp.getWriter().write("{\"message\":\"Logout correcto\"}");
    }

    /**
     * Clase interna para mapear el JSON de login.
     * Ejemplo de body:
     * { "username": "admin", "password": "admin123" }
     */
    private static class LoginBody {
        String username;
        String password;
    }
}
