<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../layout/header.jsp" %>
<%@ include file="../layout/sidebar.jsp" %>

<h1 class="h3 mb-3">Confirmer le retour</h1>

<div class="page-card p-3">
    <dl class="row mb-0">
        <dt class="col-sm-3">Membre</dt>
        <dd class="col-sm-9"><c:out value="${emprunt.membre.carteNumero}" /> - <c:out value="${emprunt.membre.nom}" /> <c:out value="${emprunt.membre.prenom}" /></dd>
        <dt class="col-sm-3">Ouvrage</dt>
        <dd class="col-sm-9"><c:out value="${emprunt.ouvrage.titre}" /></dd>
        <dt class="col-sm-3">Retour prevu</dt>
        <dd class="col-sm-9">${emprunt.dateRetourPrevue}</dd>
        <dt class="col-sm-3">Retard</dt>
        <dd class="col-sm-9">${joursRetard} jour(s)</dd>
        <dt class="col-sm-3">Penalite estimee</dt>
        <dd class="col-sm-9">${montantPenalite} FCFA</dd>
    </dl>
    <div class="d-flex gap-2 mt-3">
        <form method="post" action="${ctx}/emprunts/retour/${emprunt.id}">
            <button class="btn btn-success" type="submit"><i class="bi bi-check2-circle"></i> Confirmer</button>
        </form>
        <a class="btn btn-outline-secondary" href="${ctx}/emprunts">Annuler</a>
    </div>
</div>

<%@ include file="../layout/footer.jsp" %>
