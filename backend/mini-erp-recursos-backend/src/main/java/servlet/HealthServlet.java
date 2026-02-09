
package servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

@WebServlet(name = "HealthServlet", urlPatterns = {"/api/health"})
public class HealthServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String dbUrl  = getServletContext().getInitParameter("db.url");
        String dbUser = getServletContext().getInitParameter("db.user");
        String dbPass = getServletContext().getInitParameter("db.pass");

        if (dbUrl == null || dbUrl.isBlank()) {
            dbUrl = "jdbc:postgresql://localhost:5432/mini_erp";
            dbUser = (dbUser == null) ? "postgres" : dbUser;
            dbPass = (dbPass == null) ? "postgres" : dbPass;
        }

        boolean dbOk = false;
        String dbError = "";

        try (Connection con = DriverManager.getConnection(dbUrl, dbUser, dbPass);
             PreparedStatement ps = con.prepareStatement("SELECT 1")) {
            ps.execute();
            dbOk = true;
        } catch (Exception e) {
            dbOk = false;
            dbError = e.getMessage();
        }

        resp.setContentType("application/json; charset=UTF-8");

        if (dbOk) {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("{\"status\":\"UP\",\"db\":\"UP\"}");
        } else {
            resp.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            String safe = dbError.replace("\"", "\\\"");
            resp.getWriter().write("{\"status\":\"DEGRADED\",\"db\":\"DOWN\",\"error\":\"" + safe + "\"}");
        }
    }
}

