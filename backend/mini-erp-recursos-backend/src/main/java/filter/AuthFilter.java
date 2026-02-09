/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;
import java.io.IOException;

@WebFilter("/api/*")
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                     FilterChain chain) throws IOException, ServletException {

    HttpServletRequest req  = (HttpServletRequest) request;
    HttpServletResponse resp = (HttpServletResponse) response;

    String path = req.getRequestURI();
    System.out.println("[AUTH] PATH = " + path);

    // Permitir login sin sesión
    if (path.endsWith("/api/auth/login")) {
        System.out.println("[AUTH] Permitiendo /api/auth/login sin sesión");
        chain.doFilter(request, response);
        return;
    }

    // Permitir health y stats sin sesión
    if (path.endsWith("/api/health") || path.endsWith("/api/stats")) {
        chain.doFilter(request, response);
        return;
    }


    HttpSession session = req.getSession(false);

    if (session == null || session.getAttribute("user") == null) {
        resp.setContentType("application/json");
        resp.setStatus(401);
        resp.getWriter().write("{\"error\":\"No autenticado\"}");
        return;
    }

    chain.doFilter(request, response);
}

}
