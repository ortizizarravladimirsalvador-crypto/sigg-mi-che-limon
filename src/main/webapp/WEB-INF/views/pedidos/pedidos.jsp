<%-- 
    Document   : pedidos
    Created on : 25 may. 2026, 11:03:01 a. m.
    Author     : FRANCK
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">

    <!-- Título mostrado en la pestaña del navegador -->
    <title>SIGG Mi Che Limón | Pedidos</title>

    <!-- Permite que la pantalla se adapte a diferentes tamaños -->
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <!-- Bootstrap local -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css">

    <!-- Estilos personalizados del sistema -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sigg.css">
</head>

<body class="sigg-app-body">

    <!-- Layout general del sistema interno -->
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

            <!-- Menú de navegación. Pedidos queda activo en esta pantalla. -->
            <nav class="sigg-menu">
                <a href="${pageContext.request.contextPath}/dashboard">Dashboard</a>
                <a href="${pageContext.request.contextPath}/mesas">Mesas</a>
                <a class="active" href="${pageContext.request.contextPath}/pedidos">Pedidos</a>
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

        <!-- Contenido principal -->
        <main class="sigg-main">

            <!-- Encabezado superior -->
            <header class="sigg-topbar">
                <div>
                    <p class="text-muted mb-1">Comandas digitales</p>
                    <h1 class="h3 fw-bold mb-0">Registro de pedidos</h1>
                </div>

                <div class="sigg-user-pill">
                    Mesero
                </div>
            </header>

            <!-- Bloque de explicación del módulo -->
            <section class="sigg-hero-card mb-4">
                <div>
                    <h2 class="h4 fw-bold">Nuevo pedido</h2>
                    <p class="text-muted mb-0">
                        Selecciona una mesa libre, revisa los platos disponibles y prepara la comanda para enviarla a cocina.
                    </p>
                </div>

                <span class="badge badge-stock badge-stock-disponible">
                    ${totalPlatosDisponibles} platos disponibles
                </span>
            </section>
                <!-- Mensajes temporales enviados desde PedidoServlet -->
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

            <!-- Distribución principal: catálogo de platos + resumen del pedido -->
            <section class="pedido-layout">

                <!-- Columna izquierda: selección de mesa y platos -->
                <div class="pedido-catalogo">

                    <!-- Panel para seleccionar mesa -->
                    <div class="sigg-panel mb-4">
                        <div class="sigg-panel-header">
                            <h3 class="h5 fw-bold mb-0">Seleccionar mesa</h3>
                            <a href="${pageContext.request.contextPath}/mesas" class="small">Ver mesas</a>
                        </div>

                        
                        <!-- Selector de mesas libres. Su valor se copiará a los formularios de agregar plato. -->
                        <select id="idMesaSelect" class="form-select form-select-lg">
                            <option value="0" ${mesaSeleccionada == 0 ? 'selected' : ''}>
                                Elige una mesa libre
                            </option>

                            <c:forEach var="mesa" items="${mesasLibres}">
                                <option value="${mesa.idMesa}" ${mesa.idMesa == mesaSeleccionada ? 'selected' : ''}>
                                    Mesa ${mesa.numeroMesa} · Capacidad ${mesa.capacidad} personas
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <!-- Panel de platos disponibles -->
                    <div class="sigg-panel">
                        <div class="sigg-panel-header">
                            <h3 class="h5 fw-bold mb-0">Platos disponibles</h3>

                            <!-- Leyenda breve del módulo -->
                            <div class="sigg-status-legend">
                                <span><i class="stock-dot disponible"></i> Disponible</span>
                                <span><i class="stock-dot critico"></i> Poco stock</span>
                            </div>
                        </div>

                        <!-- Grilla de platos para agregar al pedido -->
                        <!-- Lista de productos agrupados por categoría -->
                        <div class="categoria-productos-lista">

                            <c:forEach var="grupo" items="${categoriasPlatos}">

                                <!-- Bloque visual de categoría -->
                                <section class="pedido-categoria ${grupo.claseCss}">

                                    <div class="pedido-categoria-header">
                                        <div>
                                            <span class="categoria-label">Categoría</span>
                                            <h4>${grupo.nombreCategoria}</h4>
                                        </div>

                                        <span class="categoria-count">
                                            ${grupo.cantidadProductos} producto(s)
                                        </span>
                                    </div>

                                    <!-- Grilla de productos de la categoría -->
                                    <div class="plato-grid">

                                        <c:forEach var="item" items="${grupo.productos}">

                                            <!-- Tarjeta de producto. El borde depende del estado del stock. -->
                                            <article class="plato-card stock-${item.claseEstado}">

                                                <div class="plato-card-header">
                                                    <div>
                                                        <h4 class="h6 fw-bold mb-1">${item.plato.nombrePlato}</h4>
                                                        <p class="text-muted mb-0">
                                                            ${item.plato.categoria}
                                                        </p>
                                                    </div>

                                                    <!-- Precio del producto -->
                                                    <strong class="plato-precio">
                                                        S/ ${item.plato.precio}
                                                    </strong>
                                                </div>

                                                <!-- Datos operativos del producto -->
                                                <div class="plato-meta">
                                                    <span>${item.plato.tiempoPreparacion} min</span>
                                                    <span>
                                                        Stock: ${item.cantidadDisponible}
                                                        <small>${item.plato.unidadMedida}</small>
                                                    </span>
                                                </div>

                                                <!-- Formulario que agrega el producto al pedido temporal -->
                                                <form method="post" action="${pageContext.request.contextPath}/pedidos">
                                                    <input type="hidden" name="accion" value="agregar">
                                                    <input type="hidden" name="idPlato" value="${item.plato.idPlato}">
                                                    <input type="hidden" name="idMesa" class="mesa-hidden" value="${mesaSeleccionada}">

                                                    <button type="submit" class="btn btn-outline-success w-100">
                                                        + Agregar
                                                    </button>
                                                </form>

                                            </article>

                                        </c:forEach>

                                    </div>
                                </section>

                            </c:forEach>

                        </div>
                    </div>
                </div>

                <!-- Columna derecha: resumen del pedido actual -->
                <aside class="pedido-resumen">

                    <div class="sigg-panel pedido-resumen-card">

                        <div class="sigg-panel-header">
                            <h3 class="h5 fw-bold mb-0">Pedido actual</h3>
                            <!-- Limpia el pedido temporal almacenado en sesión -->
                            <form method="post" action="${pageContext.request.contextPath}/pedidos">
                                <input type="hidden" name="accion" value="limpiar">
                                <button type="submit" class="btn btn-link btn-sm text-danger p-0">
                                    Limpiar
                                </button>
                            </form>
                        </div>

                        <!-- Lista o estado vacío del pedido actual -->
                        <c:choose>
                            <c:when test="${empty pedidoItems}">
                                <div class="pedido-empty">
                                    <div class="pedido-empty-icon">🧾</div>
                                    <p class="fw-semibold mb-1">Aún no hay platos agregados</p>
                                    <p class="text-muted small mb-0">
                                        Selecciona una mesa y agrega platos desde el catálogo.
                                    </p>
                                </div>
                            </c:when>

                            <c:otherwise>
                                <div class="pedido-items-list">
                                    <c:forEach var="pedidoItem" items="${pedidoItems}">
                                        <div class="pedido-item-row">
                                            <div>
                                                <strong>${pedidoItem.nombrePlato}</strong>
                                                <p class="text-muted small mb-0">
                                                    Cantidad: ${pedidoItem.cantidad} · S/ ${pedidoItem.precio}
                                                </p>
                                            </div>
                                            <strong>S/ ${pedidoItem.subtotal}</strong>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:otherwise>
                        </c:choose>

                        <!-- Métricas temporales del pedido -->
                        <div class="pedido-summary-row">
                            <span>Ítems</span>
                            <strong>${itemsPedido}</strong>
                        </div>

                        <div class="pedido-summary-row">
                            <span>Tiempo estimado</span>
                            <strong>${tiempoEstimado} min</strong>
                        </div>

                        <div class="pedido-summary-row">
                            <span>Subtotal</span>
                            <strong>S/ ${subtotal}</strong>
                        </div>

                        <!-- 
                            Formulario final del pedido.
                            Incluye observaciones + acción enviar a cocina.
                            Así garantizamos que las observaciones sí lleguen al PedidoServlet.
                        -->
                        <form method="post" action="${pageContext.request.contextPath}/pedidos">

                            <input type="hidden" name="accion" value="enviarCocina">
                            <input type="hidden" name="idMesa" class="mesa-hidden" value="${mesaSeleccionada}">

                            <!-- Observaciones del cliente -->
                            <div class="mb-3">
                                <label class="form-label">Observaciones</label>
                                <textarea class="form-control"
                                          name="observaciones"
                                          rows="3"
                                          placeholder="Ejemplo: sin ají, poca sal, servir primero las bebidas..."></textarea>
                            </div>

                            <button type="submit" class="btn btn-success btn-lg w-100">
                                Enviar a cocina
                            </button>
                        </form>

                        <p class="text-muted small mt-3 mb-0">
                            En la siguiente etapa este botón registrará el pedido,
                            descontará stock y lo enviará al módulo Cocina.
                        </p>
                    </div>

                </aside>

            </section>

        </main>
    </div>

    <!-- JavaScript de Bootstrap -->
    <script src="${pageContext.request.contextPath}/assets/js/bootstrap.bundle.min.js"></script>
    <script>
        /*
         * Sincroniza la mesa seleccionada con todos los formularios de platos.
         * Así, cuando el usuario presiona "+ Agregar", también se envía la mesa elegida.
         */
        const mesaSelect = document.getElementById("idMesaSelect");
        const mesaHiddenInputs = document.querySelectorAll(".mesa-hidden");

        function sincronizarMesaSeleccionada() {
            mesaHiddenInputs.forEach(input => {
                input.value = mesaSelect.value;
            });
        }

        mesaSelect.addEventListener("change", sincronizarMesaSeleccionada);
        sincronizarMesaSeleccionada();
    </script>
</body>
</html>