<%-- 
    Document   : mesas
    Created on : 24 may. 2026, 5:51:58 p. m.
    Author     : FRANCK
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">

    <!-- Título mostrado en la pestaña del navegador -->
    <title>SIGG Mi Che Limón | Mesas</title>

    <!-- Permite diseño adaptable a diferentes pantallas -->
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <!-- Bootstrap local -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css">

    <!-- Estilos personalizados del sistema -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sigg.css">
</head>

<body class="sigg-app-body">

    <!-- Layout general del sistema interno -->
    <div class="sigg-layout">

        <!-- Menú lateral reutilizado del dashboard -->
        <aside class="sigg-sidebar">
            <div class="sigg-sidebar-brand">
                <div class="sigg-logo">SIGG</div>
                <div>
                    <strong>Mi Che Limón</strong>
                    <span>Sistema gastronómico</span>
                </div>
            </div>

            <!-- Menú principal. En esta pantalla Mesas queda activo. -->
            <nav class="sigg-menu">
                <a href="${pageContext.request.contextPath}/dashboard">Dashboard</a>
                <a class="active" href="${pageContext.request.contextPath}/mesas">Mesas</a>
                <a href="${pageContext.request.contextPath}/pedidos">Pedidos</a>
                <a href="${pageContext.request.contextPath}/cocina">Cocina</a>
                <a href="${pageContext.request.contextPath}/stock">Stock</a>
                <a href="#">Ventas y caja</a>
                <a href="#">Reportes</a>
                <a href="#">Usuarios</a>
            </nav>

            <div class="sigg-sidebar-footer">
                <a href="${pageContext.request.contextPath}/login">Cerrar sesión</a>
            </div>
        </aside>

        <!-- Contenido principal de la pantalla -->
        <main class="sigg-main">

            <!-- Encabezado superior -->
            <header class="sigg-topbar">
                <div>
                    <p class="text-muted mb-1">Gestión operativa</p>
                    <h1 class="h3 fw-bold mb-0">Gestión de mesas</h1>
                </div>

                <div class="sigg-user-pill">
                    Administrador
                </div>
            </header>

            <!-- Descripción de la pantalla -->
            <section class="sigg-hero-card mb-4">
                <div>
                    <h2 class="h4 fw-bold">Estado y asignación</h2>
                    <p class="text-muted mb-0">
                        Visualiza las mesas libres, ocupadas y reservadas para organizar mejor la atención del restaurante.
                    </p>
                </div>

                <button class="btn btn-success">
                    + Nueva mesa
                </button>
            </section>

            <!-- Indicadores principales de mesas -->
            <section class="row g-3 mb-4">
                <div class="col-md-4">
                    
                    <!-- Tarjeta resumen para mesas libres -->
                    <div class="sigg-kpi-card kpi-success">
                        <span>Mesas libres</span>
                        <strong>${mesasLibres}</strong>
                    </div>
                </div>

                <div class="col-md-4">
                    <!-- Tarjeta resumen para mesas ocupadas -->
                    <div class="sigg-kpi-card kpi-warning">
                        <span>Mesas ocupadas</span>
                        <strong>${mesasOcupadas}</strong>
                    </div>
                </div>

                <div class="col-md-4">
                    <!-- Tarjeta resumen para mesas reservadas -->
                    <div class="sigg-kpi-card kpi-reserved">
                        <span>Mesas reservadas</span>
                        <strong>${mesasReservadas}</strong>
                    </div>
                </div>
            </section>

            <!-- Panel principal con tarjetas de mesas -->
            <section class="sigg-panel">
                <div class="sigg-panel-header">
                    <h3 class="h5 fw-bold mb-0">Mesas del restaurante</h3>

                    <!-- Leyenda visual de estados -->
                    <div class="sigg-status-legend">
                        <!-- Leyenda específica del módulo Mesas -->
                        <span><i class="mesa-dot libre"></i> Libre</span>
                        <span><i class="mesa-dot ocupada"></i> Ocupada</span>
                        <span><i class="mesa-dot reservada"></i> Reservada</span>
                    </div>
                </div>

                <!-- Grilla responsive de mesas -->
                <div class="mesa-grid">

                    <!-- Recorre la lista enviada desde MesaServlet -->
                    <c:forEach var="mesa" items="${mesas}">

                        <!-- Agrega una clase visual según el estado de la mesa -->
                        <article class="mesa-card estado-${mesa.estado.toLowerCase()}">

                            <div class="mesa-card-header">
                                <strong>Mesa ${mesa.numeroMesa}</strong>

                                <!-- Muestra etiqueta de estado con color según corresponda -->
                                <c:choose>
                                    <c:when test="${mesa.estado == 'LIBRE'}">
                                        <span class="badge badge-mesa badge-mesa-libre">Libre</span>
                                    </c:when>
                                    <c:when test="${mesa.estado == 'OCUPADA'}">
                                        <span class="badge badge-mesa badge-mesa-ocupada">Ocupada</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge badge-mesa badge-mesa-reservada">Reservada</span>
                                    </c:otherwise>
                                </c:choose>
                            </div>

                            <div class="mesa-icon">
                                🍽️
                            </div>

                            <p class="text-muted mb-3">
                                Capacidad: ${mesa.capacidad} personas
                            </p>

                            <!-- Acciones visuales. Más adelante conectaremos cada botón con Servlets reales. -->
                            <div class="mesa-actions">
                                <c:choose>
                                    <c:when test="${mesa.estado == 'LIBRE'}">
                                    <a href="${pageContext.request.contextPath}/pedidos" class="btn btn-success btn-sm w-100">
                                        Registrar pedido
                                    </a>
                                    </c:when>
                                    <c:when test="${mesa.estado == 'OCUPADA'}">
                                        <button class="btn btn-outline-success btn-sm w-100">Ver pedido</button>
                                    </c:when>
                                    <c:otherwise>
                                        <button class="btn btn-outline-secondary btn-sm w-100">Ver reserva</button>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </article>

                    </c:forEach>
                </div>
            </section>

        </main>
    </div>

    <!-- JavaScript de Bootstrap -->
    <script src="${pageContext.request.contextPath}/assets/js/bootstrap.bundle.min.js"></script>
</body>
</html>