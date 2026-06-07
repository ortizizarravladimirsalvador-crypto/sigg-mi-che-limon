package pe.edu.utp.sigg.controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * LoginServlet es el controlador encargado del inicio de sesión.
 *
 * En la arquitectura MVC:
 * - El Servlet actúa como Controlador.
 * - login.jsp actúa como Vista.
 * - Más adelante UsuarioService y UsuarioDAO validarán datos contra MySQL.
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    /**
     * Método que se ejecuta cuando el usuario entra a /login desde el navegador.
     *
     * Su función es mostrar la pantalla de login ubicada dentro de WEB-INF.
     * Se usa forward porque el usuario no debe acceder directamente al JSP.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp")
               .forward(request, response);
    }

    /**
     * Método que se ejecuta cuando el usuario envía el formulario de login.
     *
     * Por ahora valida credenciales temporales:
     * usuario: admin
     * contraseña: 1234
     *
     * Más adelante esta validación se reemplazará por UsuarioService + UsuarioDAO + MySQL.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Captura los datos enviados desde el formulario login.jsp.
        String usuario = request.getParameter("usuario");
        String password = request.getParameter("password");

        // Validación temporal para probar el flujo de autenticación.
        if ("admin".equals(usuario) && "1234".equals(password)) {

            // Si las credenciales son correctas, redirige al dashboard.
            // Por ahora genera 404 porque todavía no hemos creado /dashboard.
            response.sendRedirect(request.getContextPath() + "/dashboard");

        } else {

            // Si las credenciales son incorrectas, envía un mensaje de error a la vista.
            request.setAttribute("error", "Usuario o contraseña incorrectos.");

            // Vuelve a mostrar el login con el mensaje de error.
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp")
                   .forward(request, response);
        }
    }
}