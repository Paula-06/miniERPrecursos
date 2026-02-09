/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

@WebListener
public class AppStartupSmokeTest implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();
        
        try {
            Class.forName("org.postgresql.Driver");
            ctx.log("[SMOKETEST][DB] PostgreSQL driver loaded manually.");
        } catch (ClassNotFoundException e) {
            ctx.log("[SMOKETEST][DB] ERROR loading PostgreSQL driver: " + e.getMessage());
        }

        String dbUrl  = getParam(ctx, "db.url", "jdbc:postgresql://localhost:5432/mini_erp");
        String dbUser = getParam(ctx, "db.user", "postgres");
        String dbPass = getParam(ctx, "db.pass", "postgres");

        String baseUrl = getParam(ctx, "smoketest.baseUrl", "http://localhost:8080" + ctx.getContextPath());
        boolean restEnabled = Boolean.parseBoolean(getParam(ctx, "smoketest.rest.enabled", "true"));

        ctx.log("[SMOKETEST] ===== Startup Smoke Test (NO AUTH) =====");

        testDatabase(ctx, dbUrl, dbUser, dbPass);

        if (restEnabled) {
            new Thread(() -> {
                sleep(1200);
                testRest(ctx, baseUrl);
            }, "rest-smoketest-noauth").start();
        }

        ctx.log("[SMOKETEST] ========================================");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {}

    private void testDatabase(ServletContext ctx, String url, String user, String pass) {
        ctx.log("[SMOKETEST][DB] Testing connection: " + url);

        try (Connection con = DriverManager.getConnection(url, user, pass);
             PreparedStatement ps = con.prepareStatement("SELECT 1")) {

            ps.execute();
            ctx.log("[SMOKETEST][DB] OK (SELECT 1)");

        } catch (Exception e) {
            ctx.log("[SMOKETEST][DB] FAILED: " + e.getMessage(), e);
        }
    }

    private void testRest(ServletContext ctx, String baseUrl) {
        ctx.log("[SMOKETEST][REST] Base URL: " + baseUrl);

        SimpleHttpClient client = new SimpleHttpClient();

        String[] endpoints = {
                "/api/health",
                "/api/stats",
                "/api/employees"
        };

        for (String ep : endpoints) {
            try {
                SimpleHttpClient.HttpResult res = client.get(baseUrl + ep);
                ctx.log("[SMOKETEST][REST] GET " + ep + " -> HTTP " + res.status + " Body: " + res.bodyPreview());
            } catch (Exception e) {
                ctx.log("[SMOKETEST][REST] FAILED " + ep + ": " + e.getMessage());
            }
        }
    }

    private static String getParam(ServletContext ctx, String name, String defaultValue) {
        String v = ctx.getInitParameter(name);
        return (v == null || v.isBlank()) ? defaultValue : v.trim();
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }

    static class SimpleHttpClient {

        HttpResult get(String url) throws IOException {
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(4000);
            con.setReadTimeout(7000);
            con.setRequestProperty("Accept", "application/json");

            int status = con.getResponseCode();
            String body = readBody(con);
            return new HttpResult(status, body);
        }

        private String readBody(HttpURLConnection con) throws IOException {
            InputStream is = (con.getResponseCode() >= 200 && con.getResponseCode() < 400)
                    ? con.getInputStream()
                    : con.getErrorStream();

            if (is == null) return "";
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line).append("\n");
                return sb.toString().trim();
            }
        }

        static class HttpResult {
            final int status;
            final String body;

            HttpResult(int status, String body) {
                this.status = status;
                this.body = body;
            }

            String bodyPreview() {
                return body.length() <= 250 ? body : body.substring(0, 250) + "...";
            }
        }
    }
}
