<%-- 
    Document   : stock
    Created on : 24 may. 2026, 6:24:15 p. m.
    Author     : FRANCK
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">

    <!-- Título mostrado en la pestaña del navegador -->
    <title>SIGG Mi Che Limón | Stock</title>

    <!-- Permite que la pantalla sea adaptable -->
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <!-- Bootstrap local -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css">

    <!-- Estilos personalizados del sistema -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sigg.css">
</head>

<body class="sigg-app-body">

    <!-- Layout general de la aplicación interna -->
    <div class="sigg-layout">

        <!-- Menú lateral principal -->
        <aside class="sigg-sidebar">
            <div class="sigg-sidebar-brand">
                <div class="sigg-logo">SIGG</div>
                <div>
                    <strong>Mi Che Limón</strong>
                    <span>Sistema gastronómico</span>
                </div>
            </div>

            <!-- Menú de navegación. Stock queda activo en esta pantalla. -->
            <nav class="sigg-menu">
                <a href="${pageContext.request.contextPath}/dashboard">Dashboard</a>
                <a href="${pageContext.request.contextPath}/mesas">Mesas</a>
                <a href="${pageContext.request.contextPath}/pedidos">Pedidos</a>
                <a href="${pageContext.request.contextPath}/cocina">Cocina</a>
                <a class="active" href="${pageContext.request.contextPath}/stock">Stock</a>
                <a href="#">Ventas y caja</a>
                <a href="#">Reportes</a>
                <a href="#">Usuarios</a>
            </nav>

            <div class="sigg-sidebar-footer">
                <a href="${pageContext.request.contextPath}/login">Cerrar sesión</a>
            </div>
        </aside>

        <!-- Contenido principal -->
        <main class="sigg-main">

            <!-- Encabezado superior -->
            <header class="sigg-topbar">
                <div>
                    <p class="text-muted mb-1">Control operativo</p>
                    <h1 class="h3 fw-bold mb-0">Stock diario de platos</h1>
                </div>

                <div class="sigg-user-pill">
                    Administrador
                </div>
            </header>

            <!-- Bloque descriptivo del módulo -->
            <section class="sigg-hero-card mb-4">
                <div>
                    <h2 class="h4 fw-bold">Disponibilidad de platos por jornada</h2>
                    <p class="text-muted mb-0">
                        Define y supervisa cuántos platos quedan disponibles durante el día para evitar sobreventas.
                    </p>
                </div>

                <!-- Inicia la jornada de stock del día actual -->
                <form method="post" action="${pageContext.request.contextPath}/stock">
                    <input type="hidden" name="accion" value="iniciarJornada">

                    <button type="submit" class="btn btn-success">
                        Iniciar jornada
                    </button>
                </form>
            </section>

            <!-- Indicadores principales de stock -->
            <section class="row g-3 mb-4">
                <div class="col-md-4">
                    <!-- Tarjeta resumen para platos disponibles -->
                    <div class="sigg-kpi-card kpi-success">
                        <span>Platos disponibles</span>
                        <strong>${platosDisponibles}</strong>
                    </div>
                </div>

                <div class="col-md-4">
                    <!-- Tarjeta resumen para platos en estado crítico -->
                    <div class="sigg-kpi-card kpi-warning">
                        <span>Stock crítico</span>
                        <strong>${platosCriticos}</strong>
                    </div>
                </div>

                <div class="col-md-4">
                    <!-- Tarjeta resumen para platos agotados -->
                    <div class="sigg-kpi-card kpi-danger">
                        <span>Platos agotados</span>
                        <strong>${platosAgotados}</strong>
                    </div>
                </div>
            </section>
                    
                    <!-- Mensajes temporales del módulo Stock -->
                    <c:if test="${not empty mensajeExito}">
                        <div class="alert alert-success" role="alert">
                            ${mensajeExito}
                        </div>
                    </c:if>

                    <c:if test="${not empty mensajeError}">
                        <div class="alert alert-danger" role="alert">
                            ${mensajeError}
                        </div>
                    </c:if>

                    <!-- Aviso cuando aún no existe stock para la fecha actual -->
                    <c:if test="${jornadaIniciada == false}">
                        <div class="alert alert-warning" role="alert">
                            No se ha iniciado la jornada de stock para hoy.
                            Presiona <strong>Iniciar jornada</strong> para cargar las cantidades base del restaurante.
                        </div>
                    </c:if>

            <!-- Panel principal de stock -->
            <section class="sigg-panel">

                <div class="sigg-panel-header">
                    <h3 class="h5 fw-bold mb-0">Platos registrados</h3>

                    <!-- Leyenda específica del módulo Stock -->
                    <div class="sigg-status-legend">
                        <span><i class="stock-dot disponible"></i> Disponible</span>
                        <span><i class="stock-dot critico"></i> Crítico</span>
                        <span><i class="stock-dot agotado"></i> Agotado</span>
                    </div>
                </div>

                <!-- Recorre la lista de stock enviada desde StockServlet -->
                <!-- Stock diario agrupado por categoría -->
                <div class="categoria-productos-lista">

                    <c:forEach var="grupo" items="${categoriasStock}">

                        <!-- Bloque visual de categoría -->
                        <section class="stock-categoria ${grupo.claseCss}">

                            <div class="stock-categoria-header">
                                <div>
                                    <span class="categoria-label">Categoría</span>
                                    <h4>${grupo.nombreCategoria}</h4>
                                </div>

                                <span class="categoria-count">
                                    ${grupo.cantidadProductos} producto(s)
                                </span>
                            </div>

                            <div class="stock-lista">

                                <c:forEach var="item" items="${grupo.productos}">

                                    <!-- Tarjeta de stock por producto -->
                                    <article class="stock-card stock-${item.claseEstado}">

                                        <div class="stock-card-header">
                                            <div>
                                                <h4 class="h6 fw-bold mb-1">
                                                    ${item.plato.nombrePlato}
                                                </h4>

                                                <p class="text-muted mb-0">
                                                    ${item.plato.categoria}
                                                    · S/ ${item.plato.precio}
                                                    · ${item.plato.tiempoPreparacion} min
                                                    · ${item.plato.unidadMedida}
                                                </p>
                                            </div>

                                            <c:choose>
                                                <c:when test="${item.estado == 'DISPONIBLE'}">
                                                    <span class="badge badge-stock badge-stock-disponible">Disponible</span>
                                                </c:when>
                                                <c:when test="${item.estado == 'CRITICO'}">
                                                    <span class="badge badge-stock badge-stock-critico">Crítico</span>
                                                </c:when>
                                                <c:when test="${item.estado == 'AGOTADO'}">
                                                    <span class="badge badge-stock badge-stock-agotado">Agotado</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge badge-stock badge-stock-inactivo">Inactivo</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>

                                        <div class="stock-metrics">
                                            <div>
                                                <span>Inicial</span>
                                                <strong>${item.cantidadInicial}</strong>
                                            </div>

                                            <div>
                                                <span>Disponible</span>
                                                <strong>${item.cantidadDisponible}</strong>
                                            </div>

                                            <div>
                                                <span>Vendidos</span>
                                                <strong>${item.cantidadVendida}</strong>
                                            </div>
                                        </div>

                                        <div class="stock-progress">
                                            <div class="stock-bar stock-bar-${item.claseEstado}"
                                                 style="width: ${item.porcentajeDisponible}%;">
                                                ${item.porcentajeDisponible}%
                                            </div>
                                        </div>

                                    </article>

                                </c:forEach>

                            </div>

                        </section>

                    </c:forEach>

                </div>
            </section>

        </main>
    </div>

    <!-- JavaScript de Bootstrap -->
    <script src="${pageContext.request.contextPath}/assets/js/bootstrap.bundle.min.js"></script>
</body>
</html>