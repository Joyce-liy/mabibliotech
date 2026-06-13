<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<div class="app-shell">
    <aside class="sidebar">
        <nav class="nav flex-column gap-1">
            <a class="nav-link" href="${ctx}/dashboard"><i class="bi bi-speedometer2"></i> Tableau de bord</a>
            <a class="nav-link" href="${ctx}/ouvrages"><i class="bi bi-journal-bookmark"></i> Ouvrages</a>
            <a class="nav-link" href="${ctx}/membres"><i class="bi bi-people"></i> Membres</a>
            <a class="nav-link" href="${ctx}/emprunts"><i class="bi bi-arrow-left-right"></i> Emprunts</a>
            <a class="nav-link" href="${ctx}/penalites"><i class="bi bi-cash-coin"></i> Penalites</a>
            <a class="nav-link" href="${ctx}/transactions"><i class="bi bi-clock-history"></i> Transactions</a>
            <a class="nav-link" href="${ctx}/statistiques"><i class="bi bi-bar-chart"></i> Statistiques</a>
        </nav>
    </aside>
    <main class="content">
        <c:if test="${not empty sessionScope.flashMessage}">
            <div class="alert alert-${empty sessionScope.flashType ? 'info' : sessionScope.flashType} alert-dismissible fade show" role="alert">
                <c:out value="${sessionScope.flashMessage}" />
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Fermer"></button>
            </div>
            <c:remove var="flashMessage" scope="session" />
            <c:remove var="flashType" scope="session" />
        </c:if>
