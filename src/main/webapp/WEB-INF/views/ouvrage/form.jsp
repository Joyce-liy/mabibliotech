<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../layout/header.jsp" %>
<%@ include file="../layout/sidebar.jsp" %>

<style>
:root{--accent:#2557a7;--muted:#6b7280}
.page-card{transition:transform .3s ease,box-shadow .3s ease;border-radius:12px}
.page-card:hover{transform:translateY(-6px);box-shadow:0 18px 36px rgba(15,28,46,.06)}
.form-control{transition:box-shadow .15s ease,border-color .15s ease}
.form-control:focus{box-shadow:0 8px 24px rgba(37,87,167,.06);border-color:var(--accent)}
.reveal{opacity:0;transform:translateY(12px);transition:opacity .5s ease,transform .5s ease}
.reveal.visible{opacity:1;transform:translateY(0)}
</style>

<h1 class="h3 mb-3"><c:out value="${pageTitle}" /></h1>

<c:if test="${not empty error}">
    <div class="alert alert-danger"><c:out value="${error}" /></div>
</c:if>

<div class="page-card p-3">
    <form method="post" action="${ctx}/ouvrages/sauvegarder" enctype="multipart/form-data" class="row g-3">
        <input type="hidden" name="id" value="${ouvrage.id}">
        <div class="col-md-4">
            <label class="form-label" for="isbn">ISBN</label>
            <input class="form-control" id="isbn" name="isbn" maxlength="20" value="${ouvrage.isbn}">
        </div>
        <div class="col-md-8">
            <label class="form-label" for="titre">Titre</label>
            <input class="form-control" id="titre" name="titre" maxlength="200" value="${ouvrage.titre}" required>
        </div>
        <div class="col-md-6">
            <label class="form-label" for="auteur">Auteur</label>
            <input class="form-control" id="auteur" name="auteur" maxlength="150" value="${ouvrage.auteur}" required>
        </div>
        <div class="col-md-6">
            <label class="form-label" for="editeur">Editeur</label>
            <input class="form-control" id="editeur" name="editeur" maxlength="100" value="${ouvrage.editeur}">
        </div>
        <div class="col-md-3">
            <label class="form-label" for="anneeEdition">Annee</label>
            <input class="form-control" id="anneeEdition" name="anneeEdition" type="number" min="1000" max="2100" value="${ouvrage.anneeEdition}">
        </div>
        <div class="col-md-3">
            <label class="form-label" for="categorie">Categorie</label>
            <input class="form-control" id="categorie" name="categorie" maxlength="80" value="${ouvrage.categorie}">
        </div>
        <div class="col-md-3">
            <label class="form-label" for="exemplairesTotal">Total</label>
            <input class="form-control" id="exemplairesTotal" name="exemplairesTotal" type="number" min="1" value="${empty ouvrage.exemplairesTotal ? 1 : ouvrage.exemplairesTotal}" required>
        </div>
        <div class="col-md-3">
            <label class="form-label" for="exemplairesDispo">Disponibles</label>
            <input class="form-control" id="exemplairesDispo" name="exemplairesDispo" type="number" min="0" value="${empty ouvrage.exemplairesDispo ? 1 : ouvrage.exemplairesDispo}" required>
        </div>
        <div class="col-md-6">
            <label class="form-label" for="localisation">Localisation</label>
            <input class="form-control" id="localisation" name="localisation" maxlength="50" value="${ouvrage.localisation}">
        </div>
        <div class="col-md-6">
            <label class="form-label" for="photoCouverture">Couverture JPG/PNG</label>
            <input class="form-control" id="photoCouverture" name="photoCouverture" type="file" accept="image/jpeg,image/png">
        </div>
        <div class="col-12 d-flex gap-2">
            <button class="btn btn-primary" type="submit"><i class="bi bi-save"></i> Enregistrer</button>
            <a class="btn btn-outline-secondary" href="${ctx}/ouvrages">Annuler</a>
        </div>
    </form>
</div>

<%@ include file="../layout/footer.jsp" %>

<script>
document.addEventListener('DOMContentLoaded', function(){
  document.querySelectorAll('.page-card, .form-control').forEach((el,i)=>{
    el.classList.add('reveal');
    setTimeout(()=>el.classList.add('visible'), 100 + i*20);
  });
});
</script>
