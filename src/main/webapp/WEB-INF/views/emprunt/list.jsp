<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../layout/header.jsp" %>
<%@ include file="../layout/sidebar.jsp" %>

<div class="d-flex flex-wrap justify-content-between align-items-center mb-3 gap-2">
    <div>
        <h1 class="h3 mb-1">Emprunts</h1>
        <p class="text-muted mb-0">Prets en cours, retours et retards.</p>
    </div>
    <div class="d-flex gap-2">
        <a class="btn btn-outline-secondary" href="${ctx}/export/csv/emprunts"><i class="bi bi-filetype-csv"></i> CSV</a>
        <a class="btn btn-primary" href="${ctx}/emprunts/nouveau"><i class="bi bi-plus-lg"></i> Nouvel emprunt</a>
    </div>
</div>

<div class="page-card p-3 mb-3">
    <form class="row g-2" method="get" action="${ctx}/emprunts">
        <div class="col-md-10">
            <select class="form-select" name="statut">
                <option value="">Tous les statuts</option>
                <c:forEach var="s" items="${statuts}">
                    <option value="${s}" ${s == statut ? 'selected' : ''}>${s}</option>
                </c:forEach>
            </select>
        </div>
        <div class="col-md-2 d-grid">
            <button class="btn btn-outline-primary" type="submit"><i class="bi bi-funnel"></i> Filtrer</button>
        </div>
    </form>
</div>

<div class="page-card p-0">
    <div class="table-responsive">
        <table class="table table-hover mb-0">
            <thead class="table-light">
            <tr>
                <th>Membre</th>
                <th>Ouvrage</th>
                <th>Emprunt</th>
                <th>Retour prevu</th>
                <th>Statut</th>
                <th class="text-end">Actions</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="emprunt" items="${emprunts}">
                <c:set var="badgeClass" value="badge-status-en-cours" />
                <c:if test="${emprunt.statut == 'RENDU'}"><c:set var="badgeClass" value="badge-status-rendu" /></c:if>
                <c:if test="${emprunt.statut == 'EN_RETARD'}"><c:set var="badgeClass" value="badge-status-en-retard" /></c:if>
                <tr>
                    <td>
                        <strong><c:out value="${emprunt.membre.carteNumero}" /></strong>
                        <div class="small text-muted"><c:out value="${emprunt.membre.nom}" /> <c:out value="${emprunt.membre.prenom}" /></div>
                    </td>
                    <td><c:out value="${emprunt.ouvrage.titre}" /></td>
                    <td>${emprunt.dateEmprunt}</td>
                    <td>${emprunt.dateRetourPrevue}</td>
                    <td><span class="badge ${badgeClass}">${emprunt.statut}</span></td>
                    <td class="text-end">
                        <a class="btn btn-sm btn-outline-secondary" href="${ctx}/export/pdf/emprunt?id=${emprunt.id}"><i class="bi bi-receipt"></i></a>
                        <c:if test="${emprunt.statut != 'RENDU'}">
                            <a class="btn btn-sm btn-outline-success" href="${ctx}/emprunts/retour/${emprunt.id}"><i class="bi bi-check2-circle"></i></a>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty emprunts}">
                <tr><td colspan="6" class="text-center text-muted py-4">Aucun emprunt trouve.</td></tr>
            </c:if>
            </tbody>
        </table>
    </div>
</div>

<div class="table-footer">
    <span class="table-info">
        Affichage de ${totalItems == 0 ? 0 : (currentPage - 1) * pageSize + 1}
        a ${currentPage * pageSize > totalItems ? totalItems : currentPage * pageSize}
        sur ${totalItems} dossiers
    </span>
    <ul class="pagination mb-0">
        <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
            <a class="page-link" href="?page=${currentPage - 1}"><i class="bi bi-chevron-left"></i></a>
        </li>
        <c:forEach begin="1" end="${totalPages}" var="p">
            <li class="page-item ${p == currentPage ? 'active' : ''}">
                <a class="page-link" href="?page=${p}">${p}</a>
            </li>
        </c:forEach>
        <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
            <a class="page-link" href="?page=${currentPage + 1}"><i class="bi bi-chevron-right"></i></a>
        </li>
    </ul>
</div>

<%@ include file="../layout/footer.jsp" %>
