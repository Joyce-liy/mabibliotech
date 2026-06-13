<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../layout/header.jsp" %>
<%@ include file="../layout/sidebar.jsp" %>

<div class="d-flex flex-wrap justify-content-between align-items-center mb-3 gap-2">
    <div>
        <h1 class="h3 mb-1">Membres</h1>
        <p class="text-muted mb-0">Etudiants, enseignants et lecteurs externes.</p>
    </div>
    <div class="d-flex gap-2">
        <a class="btn btn-outline-secondary" href="${ctx}/export/csv/membres"><i class="bi bi-filetype-csv"></i> CSV</a>
        <a class="btn btn-primary" href="${ctx}/membres/nouveau"><i class="bi bi-person-plus"></i> Ajouter</a>
    </div>
</div>

<div class="page-card p-3 mb-3">
    <form class="row g-2" method="get" action="${ctx}/membres">
        <div class="col-md-6">
            <input class="form-control" type="search" name="q" value="${q}" placeholder="Nom, prenom ou numero de carte">
        </div>
        <div class="col-md-4">
            <select class="form-select" name="type">
                <option value="">Tous les types</option>
                <c:forEach var="t" items="${types}">
                    <option value="${t}" ${t == type ? 'selected' : ''}>${t}</option>
                </c:forEach>
            </select>
        </div>
        <div class="col-md-2 d-grid">
            <button class="btn btn-outline-primary" type="submit"><i class="bi bi-search"></i> Rechercher</button>
        </div>
    </form>
</div>

<div class="page-card p-0">
    <div class="table-responsive">
        <table class="table table-hover mb-0">
            <thead class="table-light">
            <tr>
                <th>Carte</th>
                <th>Nom</th>
                <th>Type</th>
                <th>Contact</th>
                <th>Expiration</th>
                <th>Statut</th>
                <th class="text-end">Actions</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="membre" items="${membres}">
                <tr>
                    <td><strong><c:out value="${membre.carteNumero}" /></strong></td>
                    <td><c:out value="${membre.nom}" /> <c:out value="${membre.prenom}" /></td>
                    <td>${membre.typeMembre}</td>
                    <td>
                        <div><c:out value="${membre.telephone}" /></div>
                        <div class="small text-muted"><c:out value="${membre.email}" /></div>
                    </td>
                    <td>${membre.dateExpirationCarte}</td>
                    <td>
                        <span class="badge ${membre.actif ? 'text-bg-success' : 'text-bg-secondary'}">
                            ${membre.actif ? 'Actif' : 'Inactif'}
                        </span>
                        <c:if test="${membre.bloque}">
                            <span class="badge text-bg-danger ms-1">Bloque</span>
                        </c:if>
                    </td>
                    <td class="text-end">
                        <a class="btn btn-sm btn-outline-secondary" href="${ctx}/export/pdf/membre?id=${membre.id}"><i class="bi bi-file-earmark-pdf"></i></a>
                        <a class="btn btn-sm btn-outline-success" href="${ctx}/membres/transactions/${membre.id}" title="Historique des paiements"><i class="bi bi-clock-history"></i></a>
                        <a class="btn btn-sm btn-outline-primary" href="${ctx}/membres/modifier/${membre.id}"><i class="bi bi-pencil"></i></a>
                        <c:if test="${isAdmin and membre.bloque}">
                            <form class="d-inline" method="post" action="${ctx}/membres/debloquer/${membre.id}"
                                  data-confirm="Debloquer ce membre manuellement ?">
                                <button class="btn btn-sm btn-outline-warning" type="submit" title="Debloquer">
                                    <i class="bi bi-unlock"></i>
                                </button>
                            </form>
                        </c:if>
                        <form class="d-inline" method="post" action="${ctx}/membres/supprimer/${membre.id}" data-confirm="Desactiver ce membre ?">
                            <button class="btn btn-sm btn-outline-danger" type="submit"><i class="bi bi-trash"></i></button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty membres}">
                <tr><td colspan="7" class="text-center text-muted py-4">Aucun membre trouve.</td></tr>
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
