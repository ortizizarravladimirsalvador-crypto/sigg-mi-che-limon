<%-- 
    Document   : cocina
    Created on : 26 may. 2026, 4:30:29 p. m.
    Author     : FRANCK
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">

    <!-- Título mostrado en la pestaña del navegador -->
    <title>SIGG Mi Che Limón | Cocina</title>

    <!-- Diseño adaptable -->
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <!-- Bootstrap local -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css">

    <!-- Estilos personalizados -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sigg.css">
</head>

<body class="sigg-app-body">

    <!-- Layout general del sistema -->
    <div class="sigg-layout">

        <!-- Menú lateral -->
        <aside class="sigg-sidebar">
            <div class="sigg-sidebar-brand">
                <div class="sigg-logo">SIGG</div>
                <div>
                    <strong>Mi Che Limón</strong>
                    <span>Sistema gastronómico</span>
                </div>
            </div>

            <!-- Cocina queda activo en esta pantalla -->
            <nav class="sigg-menu">
                <a href="${pageContext.request.contextPath}/dashboard">Dashboard</a>
                <a href="${pageContext.request.contextPath}/mesas">Mesas</a>
                <a href="${pageContext.request.contextPath}/pedidos">Pedidos</a>
                <a class="active" href="${pageContext.request.contextPath}/cocina">Cocina</a>
                <a href="${pageContext.request.contextPath}/stock">Stock</a>
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

            <!-- Encabezado -->
            <header class="sigg-topbar">
                <div>
                    <p class="text-muted mb-1">Área de preparación</p>
                    <h1 class="h3 fw-bold mb-0">Pantalla de cocina</h1>
                </div>

                <div class="sigg-user-pill">
                    Cocina
                </div>
            </header>

            <!-- Bloque de descripción -->
            <section class="sigg-hero-card mb-4">
                <div>
                    <h2 class="h4 fw-bold">Pedidos en cola</h2>
                    <p class="text-muted mb-0">
                        Visualiza los pedidos enviados por el mesero y organiza la preparación según orden de llegada.
                    </p>
                </div>

                <span class="badge badge-stock badge-stock-disponible">
                    ${totalPedidos} pedido(s)
                </span>
            </section>

            <!-- Mensaje de confirmación del módulo de cocina al recibir pedido -->
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

            <!-- Panel principal de cocina -->
            <section class="sigg-panel">

                <div class="sigg-panel-header">
                    <h3 class="h5 fw-bold mb-0">Cola de preparación</h3>

                    <div class="sigg-status-legend">
                        <span><i class="pedido-dot pendiente"></i> Pendiente</span>
                        <span><i class="pedido-dot preparacion"></i> En preparación</span>
                        <span><i class="pedido-dot listo"></i> Listo</span>
                    </div>
                </div>

                <!-- Si no hay pedidos, se muestra estado vacío -->
                <c:choose>
                    <c:when test="${empty pedidosCocina}">
                        <div class="pedido-empty">
                            <div class="pedido-empty-icon">👨‍🍳</div>
                            <p class="fw-semibold mb-1">No hay pedidos en cocina</p>
                            <p class="text-muted small mb-0">
                                Los pedidos enviados desde el módulo Pedidos aparecerán aquí.
                            </p>
                        </div>
                    </c:when>

                    <c:otherwise>
                        <div class="cocina-grid">

                            <!-- Recorre los pedidos enviados a cocina -->
                            <c:forEach var="pedido" items="${pedidosCocina}">
                                <article class="cocina-card pedido-${pedido.claseEstado}">

                                    <div class="cocina-card-header">
                                        <div>
                                            <h4 class="h5 fw-bold mb-1">
                                                ${pedido.codigoPedido} · ${pedido.nombreMesa}
                                            </h4>
                                            <p class="text-muted mb-0">
                                                Tiempo estimado: ${pedido.tiempoEstimado} min · Total: S/ ${pedido.subtotal}
                                            </p>
                                        </div>

                                        <span class="badge badge-pedido badge-pedido-pendiente">
                                            Pendiente
                                        </span>
                                    </div>

                                    <!-- Lista de platos del pedido -->
                                    <div class="cocina-items">
                                        <c:forEach var="item" items="${pedido.items}">
                                            <div class="cocina-item-row">
                                                <span>${item.nombrePlato}</span>
                                                <strong>x${item.cantidad}</strong>
                                            </div>
                                        </c:forEach>
                                    </div>

                                    <!-- Observaciones del pedido -->
                                    <c:if test="${not empty pedido.observaciones}">
                                        <div class="cocina-observacion">
                                            <strong>Observación:</strong>
                                            <p class="mb-0">${pedido.observaciones}</p>
                                        </div>
                                    </c:if>

                                    <!-- Acciones visuales. Se conectarán en el siguiente paso. -->
                                    <div class="cocina-actions">
                                        <!-- Acciones reales de cocina contra MySQL -->
                                        <div class="d-flex justify-content-end gap-2 mt-3">

                                            <!-- Solo permite iniciar preparación si el pedido está pendiente -->
                                            <c:if test="${pedido.estado == 'PENDIENTE'}">
                                                <form method="post" action="${pageContext.request.contextPath}/cocina">
                                                    <input type="hidden" name="accion" value="iniciarPreparacion">
                                                    <input type="hidden" name="idPedido" value="${pedido.idPedido}">

                                                    <button type="submit" class="btn btn-outline-success">
                                                        Iniciar preparación
                                                    </button>
                                                </form>
                                            </c:if>

                                            <!-- Permite marcar listo si está pendiente o en preparación -->
                                            <c:if test="${pedido.estado == 'PENDIENTE' || pedido.estado == 'EN_PREPARACION'}">
                                                <form method="post" action="${pageContext.request.contextPath}/cocina">
                                                    <input type="hidden" name="accion" value="marcarListo">
                                                    <input type="hidden" name="idPedido" value="${pedido.idPedido}">

                                                    <button type="submit" class="btn btn-success">
                                                        Marcar listo
                                                    </button>
                                                </form>
                                            </c:if>

                                            <!-- Si ya está listo, muestra estado informativo -->
                                            <c:if test="${pedido.estado == 'LISTO'}">
                                                <span class="badge bg-success px-3 py-2">
                                                    Listo para caja
                                                </span>
                                            </c:if>

                                        </div>
                                    </div>

                                </article>
                            </c:forEach>

                        </div>
                    </c:otherwise>
                </c:choose>
            </section>

        </main>
    </div>

    <!-- Bootstrap -->
    <script src="${pageContext.request.contextPath}/assets/js/bootstrap.bundle.min.js"></script>
</body>
</html>