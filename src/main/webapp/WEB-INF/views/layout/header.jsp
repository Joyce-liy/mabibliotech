<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!doctype html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><c:out value="${empty pageTitle ? 'Bibliotheque universitaire' : pageTitle}" /></title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <link href="${ctx}/assets/css/app.css" rel="stylesheet">
</head>
<body>
<nav class="navbar topbar px-3">
    <a class="navbar-brand fw-semibold" href="${ctx}/dashboard">
        <i class="bi bi-book-half me-2"></i>Bibliotheque Universitaire
    </a>
    <div class="d-flex align-items-center gap-3">
        <span class="text-muted small">
            <i class="bi bi-person-circle me-1"></i>
            <c:out value="${sessionScope.utilisateur.prenom}" />
            <c:out value="${sessionScope.utilisateur.nom}" />
        </span>
        <a class="btn btn-outline-secondary btn-sm" href="${ctx}/logout">
            <i class="bi bi-box-arrow-right"></i>
        </a>
    </div>
</nav>
