<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../layout/header.jsp" %>
<%@ include file="../layout/sidebar.jsp" %>

<div class="d-flex flex-wrap justify-content-between align-items-center mb-3 gap-2">
    <div>
        <h1 class="h3 mb-1">Transactions</h1>
        <p class="text-muted mb-0">Historique des paiements de penalites.</p>
    </div>
    <a class="btn btn-outline-secondary" href="${ctx}/penalites"><i class="bi bi-cash-coin"></i> Penalites</a>
</div>

<div class="page-card p-0">
    <div class="table-responsive">
        <table class="table table-hover mb-0">
            <thead class="table-light">
            <tr>
                <th>Date</th>
                <th>Membre</th>
                <th>Montant</th>
                <th>Penalite</th>
                <th>Ouvrage</th>
                <th>Moyen</th>
                <th>Reference</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="t" items="${transactions}">
                <tr>
                    <td><c:out value="${t.datePaiementFormatee}" /></td>
                    <td>
                        <strong><c:out value="${t.membre.carteNumero}" /></strong>
                        <div class="small text-muted">
                            <c:out value="${t.membre.nom}" /> <c:out value="${t.membre.prenom}" />
                        </div>
                    </td>
                    <td>${t.montant} FCFA</td>
                    <td>
                        <c:choose>
                            <c:when test="${not empty t.penalite}">#${t.penalite.id}</c:when>
                            <c:otherwise>-</c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${not empty t.penalite}">
                                <c:out value="${t.penalite.emprunt.ouvrage.titre}" />
                            </c:when>
                            <c:otherwise>-</c:otherwise>
                        </c:choose>
                    </td>
                    <td><c:out value="${t.moyen}" /></td>
                    <td><c:out value="${t.reference}" /></td>
                </tr>
            </c:forEach>
            <c:if test="${empty transactions}">
                <tr><td colspan="7" class="text-center text-muted py-4">Aucune transaction.</td></tr>
            </c:if>
            </tbody>
        </table>
    </div>
</div>

<div class="table-footer">
    <span class="table-info">
        Affichage de ${totalItems == 0 ? 0 : (currentPage - 1) * pageSize + 1}
        a ${currentPage * pageSize > totalItems ? totalItems : currentPage * pageSize}
        sur ${totalItems} transactions
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
