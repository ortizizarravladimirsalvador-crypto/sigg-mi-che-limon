<%-- 
    Document   : login
    Created on : 23 may. 2026, 6:04:45 a. m.
    Author     : FRANCK
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">

    <!-- Título que aparece en la pestaña del navegador -->
    <title>SIGG Mi Che Limón | Login</title>

    <!-- Permite que la pantalla se adapte a celulares, tablets y laptops -->
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <!-- Bootstrap local: framework CSS para estilos rápidos y responsive -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css">

    <!-- Hoja de estilos propia del sistema SIGG -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sigg.css">
</head>

<!-- Clase personalizada que aplica el fondo visual del login -->
<body class="sigg-login-body">

    <!-- Contenedor principal que centra la tarjeta de login en toda la pantalla -->
    <main class="container-fluid min-vh-100 d-flex align-items-center justify-content-center">

        <!-- Tarjeta principal del login: divide la pantalla en panel de marca y formulario -->
        <div class="row login-card shadow-lg">

            <!-- Panel izquierdo: identidad visual del sistema -->
            <section class="col-md-6 login-brand-panel d-none d-md-flex flex-column justify-content-between">
                <div>
                    <!-- Insignia visual del sistema -->
                    <div class="brand-badge mb-4">
                        SIGG
                    </div>

                    <!-- Nombre del restaurante / sistema -->
                    <h1 class="fw-bold display-6 mb-3">Mi Che Limón</h1>

                    <!-- Nombre formal del sistema -->
                    <p class="lead mb-4">
                        Sistema Integral de Gestión Gastronómica
                    </p>

                    <!-- Descripción resumida de los módulos principales -->
                    <p class="text-muted">
                        Comandas digitales, control de mesas, cocina sincronizada,
                        stock diario de platos, caja y reportes.
                    </p>
                </div>

                <!-- Texto inferior del panel izquierdo -->
                <div class="small text-muted">
                    Proyecto académico | Curso Integrador I
                </div>
            </section>

            <!-- Panel derecho: formulario de inicio de sesión -->
            <section class="col-md-6 p-5 bg-white">
                <div class="mb-4">
                    <h2 class="fw-bold">Inicio de sesión</h2>
                    <p class="text-muted mb-0">
                        Accede según tu rol: administrador, mesero, cocina o cajero.
                    </p>
                </div>

                <%-- 
                    Si el Servlet envía un atributo llamado "error",
                    se muestra una alerta indicando que las credenciales son incorrectas.
                --%>
                <% if (request.getAttribute("error") != null) { %>
                    <div class="alert alert-danger" role="alert">
                        <%= request.getAttribute("error") %>
                    </div>
                <% } %>

                <!-- Formulario que envía usuario y contraseña al LoginServlet mediante POST -->
                <form method="post" action="${pageContext.request.contextPath}/login">

                    <!-- Campo para ingresar el usuario -->
                    <div class="mb-3">
                        <label for="usuario" class="form-label">Usuario</label>
                        <input type="text"
                               class="form-control form-control-lg"
                               id="usuario"
                               name="usuario"
                               placeholder="Ejemplo: admin"
                               required>
                    </div>

                    <!-- Campo para ingresar la contraseña -->
                    <div class="mb-4">
                        <label for="password" class="form-label">Contraseña</label>
                        <input type="password"
                               class="form-control form-control-lg"
                               id="password"
                               name="password"
                               placeholder="Ejemplo: 1234"
                               required>
                    </div>

                    <!-- Botón que envía el formulario -->
                    <button type="submit" class="btn btn-success btn-lg w-100">
                        Ingresar
                    </button>
                </form>

                <!-- Bloque temporal para recordar las credenciales de prueba -->
                <div class="mt-4 p-3 rounded demo-access">
                    <p class="mb-1 fw-semibold">Acceso temporal de prueba:</p>
                    <p class="mb-0 small">
                        Usuario: <strong>admin</strong> | Contraseña: <strong>1234</strong>
                    </p>
                </div>
            </section>

        </div>
    </main>

    <!-- JavaScript de Bootstrap para componentes interactivos -->
    <script src="${pageContext.request.contextPath}/assets/js/bootstrap.bundle.min.js"></script>
</body>
</html>