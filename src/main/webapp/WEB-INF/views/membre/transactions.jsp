<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../layout/header.jsp" %>
<%@ include file="../layout/sidebar.jsp" %>

<div class="d-flex flex-wrap justify-content-between align-items-center mb-3 gap-2">
    <div>
        <h1 class="h3 mb-1">Historique des paiements</h1>
        <p class="text-muted mb-0">
            <c:out value="${membre.carteNumero}" /> -
            <c:out value="${membre.nom}" /> <c:out value="${membre.prenom}" />
        </p>
    </div>
    <a class="btn btn-outline-secondary" href="${ctx}/membres"><i class="bi bi-arrow-left"></i> Retour</a>
</div>

<div class="page-card p-0">
    <div class="table-responsive">
        <table class="table table-hover mb-0">
            <thead class="table-light">
            <tr>
                <th>Date</th>
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
                <tr><td colspan="6" class="text-center text-muted py-4">Aucune transaction.</td></tr>
            </c:if>
            </tbody>
        </table>
    </div>
</div>

<%@ include file="../layout/footer.jsp" %>
