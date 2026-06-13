<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../layout/header.jsp" %>
<%@ include file="../layout/sidebar.jsp" %>

<h1 class="h3 mb-3">Nouvel emprunt</h1>

<c:if test="${not empty error}">
    <div class="alert alert-danger"><c:out value="${error}" /></div>
</c:if>

<c:set var="hasBlockedMembers" value="false" />
<c:forEach var="membre" items="${membres}">
    <c:if test="${membre.bloque}">
        <c:set var="hasBlockedMembers" value="true" />
    </c:if>
</c:forEach>
<c:if test="${hasBlockedMembers}">
    <div class="alert alert-warning">
        Certains membres sont bloques et ne peuvent pas etre selectionnes pour un nouvel emprunt.
    </div>
</c:if>

<div class="page-card p-3">
    <form method="post" action="${ctx}/emprunts/sauvegarder" class="row g-3">
        <div class="col-md-6">
            <label class="form-label" for="membreId">Membre</label>
            <select class="form-select" id="membreId" name="membreId" required>
                <option value="">Selectionner un membre</option>
                <c:forEach var="membre" items="${membres}">
                    <option value="${membre.id}" data-type="${membre.typeMembre}" ${membre.bloque ? 'disabled' : ''}>
                        ${membre.carteNumero} - <c:out value="${membre.nom}" /> <c:out value="${membre.prenom}" /> (${membre.typeMembre})
                        <c:if test="${membre.bloque}"> - BLOQUE</c:if>
                    </option>
                </c:forEach>
            </select>
        </div>
        <div class="col-md-6">
            <label class="form-label" for="ouvrageId">Ouvrage disponible</label>
            <select class="form-select" id="ouvrageId" name="ouvrageId" required>
                <option value="">Selectionner un ouvrage</option>
                <c:forEach var="ouvrage" items="${ouvrages}">
                    <option value="${ouvrage.id}">
                        <c:out value="${ouvrage.titre}" /> - <c:out value="${ouvrage.auteur}" /> (${ouvrage.exemplairesDispo} dispo)
                    </option>
                </c:forEach>
            </select>
        </div>
        <div class="col-md-6">
            <label class="form-label" for="dateEmprunt">Date d'emprunt</label>
            <input type="date" class="form-control" id="dateEmprunt" name="dateEmprunt" required>
        </div>
        <div class="col-md-6">
            <label class="form-label" for="retourPrevu">Retour prevu</label>
            <input type="date" class="form-control" id="retourPrevu" name="retourPrevu" required>
        </div>
        <div class="col-12 d-flex gap-2">
            <button class="btn btn-primary" type="submit"><i class="bi bi-save"></i> Enregistrer</button>
            <a class="btn btn-outline-secondary" href="${ctx}/emprunts">Annuler</a>
        </div>
    </form>
</div>

<script>
    const membreSelect = document.getElementById("membreId");
    const dateEmpruntInput = document.getElementById("dateEmprunt");
    const retourPrevuInput = document.getElementById("retourPrevu");
    
    // 1. Initialiser le champ Date d'emprunt avec la date du jour par défaut (au format YYYY-MM-DD)
    const aujourdhui = new Date();
    dateEmpruntInput.value = aujourdhui.toISOString().slice(0, 10);

    const daysByType = { ETUDIANT: 14, ENSEIGNANT: 30, EXTERNE: 7 };

    // 2. Fonction qui calcule dynamiquement la date de retour conseillée
    function refreshReturnDate() {
        const type = membreSelect.selectedOptions[0]?.dataset.type;
        const dateChoisieStr = dateEmpruntInput.value;

        if (!type || !dateChoisieStr) {
            retourPrevuInput.value = "";
            return;
        }

        // Calcule le retour en fonction de la date d'emprunt saisie
        const date = new Date(dateChoisieStr);
        date.setDate(date.getDate() + daysByType[type]);
        retourPrevuInput.value = date.toISOString().slice(0, 10);
    }

    // Écouteurs d'événements pour mettre à jour la date automatiquement...
    membreSelect.addEventListener("change", refreshReturnDate);
    dateEmpruntInput.addEventListener("change", refreshReturnDate);
    
    // ...Mais comme le champ 'retourPrevuInput' n'a plus l'attribut 'readonly',
    // tu restes libre d'y cliquer et d'y forcer la date que tu veux pour tes tests !
</script>

<%@ include file="../layout/footer.jsp" %>
