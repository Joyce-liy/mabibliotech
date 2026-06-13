<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../layout/header.jsp" %>
<%@ include file="../layout/sidebar.jsp" %>

<div class="d-flex flex-wrap justify-content-between align-items-center mb-3 gap-2">
    <div>
        <h1 class="h3 mb-1">Ouvrages</h1>
        <p class="text-muted mb-0">Catalogue, disponibilites et localisation.</p>
    </div>
    <div class="d-flex gap-2">
        <a class="btn btn-outline-secondary" href="${ctx}/export/csv/ouvrages"><i class="bi bi-filetype-csv"></i> CSV</a>
        <a class="btn btn-primary" href="${ctx}/ouvrages/nouveau"><i class="bi bi-plus-lg"></i> Ajouter</a>
    </div>
</div>

<div class="page-card p-3 mb-3">
    <form class="row g-2" method="get" action="${ctx}/ouvrages">
        <div class="col-md-6">
            <input class="form-control" type="search" name="q" value="${q}" placeholder="Titre, auteur, ISBN, categorie">
        </div>
        <div class="col-md-4">
            <select class="form-select" name="categorie">
                <option value="">Toutes les categories</option>
                <c:forEach var="cat" items="${categories}">
                    <option value="${cat}" ${cat == categorie ? 'selected' : ''}><c:out value="${cat}" /></option>
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
                <th style="width:60px">Photo</th>
                <th>Titre</th>
                <th>Auteur</th>
                <th>Categorie</th>
                <th>Stock</th>
                <th>Localisation</th>
                <th class="text-end">Actions</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="ouvrage" items="${ouvrages}">
                <tr>
                    <td>
                        <c:choose>
                            <c:when test="${not empty ouvrage.photoCouverture}">
                                <img src="${ctx}/uploads/photos/${ouvrage.photoCouverture}"
                                     alt="Couverture"
                                     style="width:45px; height:58px; object-fit:cover; border-radius:4px; box-shadow:0 1px 4px rgba(0,0,0,.15);">
                            </c:when>
                            <c:otherwise>
                                <div style="width:45px; height:58px; background:#f0f2f7; border-radius:4px; display:flex; align-items:center; justify-content:center; color:#adb5bd;">
                                    <i class="bi bi-book" style="font-size:1.2rem;"></i>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <strong><c:out value="${ouvrage.titre}" /></strong>
                        <div class="small text-muted"><c:out value="${ouvrage.isbn}" /></div>
                    </td>
                    <td><c:out value="${ouvrage.auteur}" /></td>
                    <td><c:out value="${ouvrage.categorie}" /></td>
                    <td>
                        <span class="badge ${ouvrage.exemplairesDispo > 0 ? 'badge-status-rendu' : 'badge-status-en-retard'}">
                            ${ouvrage.exemplairesDispo}/${ouvrage.exemplairesTotal}
                        </span>
                    </td>
                    <td><c:out value="${ouvrage.localisation}" /></td>
                    <td class="text-end">
                        <a class="btn btn-sm btn-outline-primary" href="${ctx}/ouvrages/modifier/${ouvrage.id}">
                            <i class="bi bi-pencil"></i>
                        </a>
                        <form class="d-inline" method="post" action="${ctx}/ouvrages/supprimer/${ouvrage.id}"
                              data-confirm="Supprimer cet ouvrage ?">
                            <button class="btn btn-sm btn-outline-danger" type="submit">
                                <i class="bi bi-trash"></i>
                            </button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty ouvrages}">
                <tr><td colspan="7" class="text-center text-muted py-4">Aucun ouvrage trouve.</td></tr>
            </c:if>
            </tbody>
        </table>
    </div>

    <div class="table-footer">
        <span class="table-info">
            Page ${currentPage} sur ${totalPages}
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
</div>

<%@ include file="../layout/footer.jsp" %>
