<%-- 
    Document   : dashboard
    Created on : 23 may. 2026, 7:00:36 a. m.
    Author     : FRANCK
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">

    <!-- Título mostrado en la pestaña del navegador -->
    <title>SIGG Mi Che Limón | Dashboard</title>

    <!-- Permite diseño adaptable a distintos tamaños de pantalla -->
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <!-- Bootstrap local -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css">

    <!-- Estilos personalizados del sistema -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sigg.css">
</head>

<body class="sigg-app-body">

    <!-- Contenedor general del sistema interno -->
    <div class="sigg-layout">

        <!-- Barra lateral de navegación principal -->
        <aside class="sigg-sidebar">

            <!-- Marca del sistema en el menú lateral -->
            <div class="sigg-sidebar-brand">
                <div class="sigg-logo">SIGG</div>
                <div>
                    <strong>Mi Che Limón</strong>
                    <span>Sistema gastronómico</span>
                </div>
            </div>

            <!-- Menú de módulos principales -->
            <nav class="sigg-menu">
                <a class="active" href="${pageContext.request.contextPath}/dashboard">Dashboard</a>
                <a href="${pageContext.request.contextPath}/mesas">Mesas</a>
                <a href="${pageContext.request.contextPath}/pedidos">Pedidos</a>
                <a href="${pageContext.request.contextPath}/cocina">Cocina</a>
                <a href="${pageContext.request.contextPath}/stock">Stock</a>
                <a href="#">Ventas y caja</a>
                <a href="#">Reportes</a>
                <a href="#">Usuarios</a>
            </nav>

            <!-- Acción temporal de cierre de sesión -->
            <div class="sigg-sidebar-footer">
                <a href="${pageContext.request.contextPath}/login">Cerrar sesión</a>
            </div>
        </aside>

        <!-- Área principal de contenido -->
        <main class="sigg-main">

            <!-- Encabezado superior del dashboard -->
            <header class="sigg-topbar">
                <div>
                    <p class="text-muted mb-1">Domingo, jornada actual</p>
                    <h1 class="h3 fw-bold mb-0">Dashboard</h1>
                </div>

                <!-- Usuario temporal; luego vendrá desde sesión -->
                <div class="sigg-user-pill">
                    Administrador
                </div>
            </header>

            <!-- Mensaje de bienvenida -->
            <section class="sigg-hero-card mb-4">
                <div>
                    <h2 class="h4 fw-bold">Bienvenido, Administrador</h2>
                    <p class="text-muted mb-0">
                        Supervisa pedidos, mesas, cocina, ventas y disponibilidad de platos desde un solo panel.
                    </p>
                </div>

                <a class="btn btn-success" href="${pageContext.request.contextPath}/pedidos">
                    Registrar pedido
                </a>
            </section>

            <!-- Indicadores principales del sistema -->
            <section class="row g-3 mb-4">

                <!-- Tarjeta de ventas del día -->
                <div class="col-md-3">
                    <!-- KPI de ventas del día: verde porque representa ingreso positivo -->
                    <div class="sigg-kpi-card kpi-success">
                        <span>Ventas de hoy</span>
                        <strong>${ventasHoy}</strong>
                    </div>
                </div>

                <!-- Tarjeta de pedidos activos -->
                <div class="col-md-3">
                    <!-- KPI de pedidos activos: azul porque representa actividad operativa -->
                    <div class="sigg-kpi-card kpi-info">
                        <span>Pedidos activos</span>
                        <strong>${pedidosActivos}</strong>
                    </div>
                </div>

                <!-- Tarjeta de mesas ocupadas -->
                <div class="col-md-3">
                    <!-- KPI de mesas ocupadas: naranja porque indica mesas actualmente en atención -->
                    <div class="sigg-kpi-card kpi-warning">
                        <span>Mesas ocupadas</span>
                        <strong>${mesasOcupadas}</strong>
                    </div>
                </div>

                <!-- Tarjeta de platos críticos -->
                <div class="col-md-3">
                    <!-- KPI de platos críticos: naranja porque representa advertencia operativa -->
                    <div class="sigg-kpi-card kpi-warning">
                        <span>Platos críticos</span>
                        <strong>${platosCriticos}</strong>
                    </div>
                </div>
            </section>

            <!-- Zona inferior del dashboard -->
            <section class="row g-4">

                <!-- Panel de pedidos en cola -->
                <div class="col-lg-7">
                    <div class="sigg-panel">
                        <div class="sigg-panel-header">
                            <h3 class="h5 fw-bold mb-0">Cola de pedidos</h3>
                            <span class="badge text-bg-success">En vivo</span>
                        </div>

                        <div class="sigg-order-item">
                            <div>
                                <strong>P-102 · Mesa 4</strong>
                                <p class="text-muted mb-0">Ceviche clásico · Arroz con mariscos</p>
                            </div>
                            <span class="badge text-bg-warning">En preparación</span>
                        </div>

                        <div class="sigg-order-item">
                            <div>
                                <strong>P-103 · Mesa 2</strong>
                                <p class="text-muted mb-0">Jalea mixta · Limonada frozen</p>
                            </div>
                            <span class="badge text-bg-secondary">Pendiente</span>
                        </div>
                    </div>
                </div>

                <!-- Panel de stock crítico -->
                <div class="col-lg-5">
                    <div class="sigg-panel">
                        <div class="sigg-panel-header">
                            <h3 class="h5 fw-bold mb-0">Stock crítico</h3>
                            <a href="${pageContext.request.contextPath}/stock" class="small">Gestionar stock</a>
                        </div>

                        <!-- Mensaje dinámico según la cantidad de platos críticos -->
                        <c:choose>
                            <c:when test="${platosCriticos > 0}">
                                <p class="text-muted mb-0">
                                    Hay <strong>${platosCriticos}</strong> plato(s) con stock crítico. Revisa la disponibilidad antes de registrar nuevos pedidos.
                                </p>
                            </c:when>
                            <c:otherwise>
                                <p class="text-muted mb-0">
                                    Sin alertas críticas por el momento.
                                </p>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </section>

        </main>
    </div>

    <!-- JavaScript de Bootstrap -->
    <script src="${pageContext.request.contextPath}/assets/js/bootstrap.bundle.min.js"></script>
</body>
</html>
