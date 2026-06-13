<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../layout/header.jsp" %>
<%@ include file="../layout/sidebar.jsp" %>

<style>
:root{--accent:#2563eb}
.mf-card{transition:transform .3s ease,box-shadow .3s ease}
.mf-card:hover{transform:translateY(-6px);box-shadow:0 18px 36px rgba(15,28,46,.06)}
.mf-input:focus{box-shadow:0 10px 30px rgba(37,99,235,.06);border-color:var(--accent)}
.reveal{opacity:0;transform:translateY(12px);transition:opacity .5s ease,transform .5s ease}
.reveal.visible{opacity:1;transform:translateY(0)}
</style>

<div class="mf-header mf-anim">
  <h1 class="mf-title">
    <c:choose>
      <c:when test="${empty membre.id}">👤 Nouveau membre</c:when>
      <c:otherwise>✏️ Modifier le membre</c:otherwise>
    </c:choose>
  </h1>
  <p class="mf-sub"><c:out value="${pageTitle}" /></p>
</div>

<c:if test="${not empty error}">
  <div class="err-alert mf-anim">
    <i class="bi bi-exclamation-triangle-fill"></i>
    <c:out value="${error}" />
  </div>
</c:if>

<form method="post" action="${ctx}/membres/sauvegarder" enctype="multipart/form-data" id="membreForm">
  <input type="hidden" name="id" value="${membre.id}">

  <div class="mf-layout mf-anim">

    <!-- Panel avatar -->
    <div class="mf-avatar-panel">
      <div class="mf-avatar-ring" id="avatarRing" onclick="document.getElementById('photoInput').click()">
        <img id="avatarPreview" src="" alt="Photo membre">
        <span id="avatarInitials">?</span>
        <input type="file" name="photo" id="photoInput" accept="image/jpeg,image/png"
               onchange="previewPhoto(event)" style="display:none;">
      </div>
      <div class="mf-avatar-name" id="avatarName">—</div>
      <div class="mf-avatar-type" id="avatarType">Type</div>
      <hr style="margin:1rem 0;border-color:#f1f5f9;">
      <div style="font-size:.78rem;color:#94a3b8;line-height:1.5;">
        Cliquez sur l'avatar<br>pour changer la photo
      </div>
    </div>

    <!-- Formulaire principal -->
    <div class="mf-card">
      <div class="row g-3">

        <div class="col-12">
          <div class="mf-section-title">
            <i class="bi bi-person-badge"></i> Identifiant & carte
          </div>
        </div>

        <div class="col-md-4">
          <label class="mf-label" for="carteNumero">N° de carte <span class="required">*</span></label>
          <input class="mf-input" id="carteNumero" name="carteNumero" maxlength="20"
                 value="${membre.carteNumero}" required placeholder="MB-2025-0001">
        </div>
        <div class="col-md-4">
          <label class="mf-label" for="typeMembre">Type <span class="required">*</span></label>
          <select class="mf-select" id="typeMembre" name="typeMembre" required onchange="updateAvatarType()">
            <c:forEach var="t" items="${types}">
              <option value="${t}" ${t == membre.typeMembre ? 'selected' : ''}>${t}</option>
            </c:forEach>
          </select>
        </div>
        <div class="col-md-4">
          <label class="mf-label" for="dateExpirationCarte">Expiration carte</label>
          <div class="expiry-field">
            <input class="mf-input" id="dateExpirationCarte" name="dateExpirationCarte"
                   type="date" value="${membre.dateExpirationCarte}" style="padding-right:36px;">
            <i class="bi bi-calendar3 expiry-icon"></i>
          </div>
        </div>

        <div class="col-12" style="margin-top:.5rem;">
          <div class="mf-section-title">
            <i class="bi bi-person"></i> État civil
          </div>
        </div>

        <div class="col-md-6">
          <label class="mf-label" for="nom">Nom <span class="required">*</span></label>
          <input class="mf-input" id="nom" name="nom" maxlength="100"
                 value="${membre.nom}" required placeholder="Nom de famille"
                 oninput="updateAvatarName()">
        </div>
        <div class="col-md-6">
          <label class="mf-label" for="prenom">Prénom <span class="required">*</span></label>
          <input class="mf-input" id="prenom" name="prenom" maxlength="100"
                 value="${membre.prenom}" required placeholder="Prénom(s)"
                 oninput="updateAvatarName()">
        </div>

        <div class="col-12" style="margin-top:.5rem;">
          <div class="mf-section-title">
            <i class="bi bi-telephone"></i> Contact
          </div>
        </div>

        <div class="col-md-6">
          <label class="mf-label" for="telephone">Téléphone</label>
          <input class="mf-input" id="telephone" name="telephone" maxlength="20"
                 value="${membre.telephone}" placeholder="+237 6XX XXX XXX">
        </div>
        <div class="col-md-6">
          <label class="mf-label" for="email">Email</label>
          <input class="mf-input" id="email" name="email" type="email" maxlength="150"
                 value="${membre.email}" placeholder="prenom.nom@exemple.cm">
        </div>

        <div class="col-12" style="margin-top:.5rem;">
          <div class="mf-switch-wrap">
            <div>
              <div class="mf-switch-label">Compte actif</div>
              <div class="mf-switch-desc">Permet à ce membre d'emprunter des ouvrages</div>
            </div>
            <div class="form-check form-switch mb-0">
              <input class="form-check-input" type="checkbox" role="switch"
                     id="actif" name="actif" value="true"
                     ${empty membre.actif || membre.actif ? 'checked' : ''}>
            </div>
          </div>
        </div>

        <div class="col-12">
          <div class="btn-row">
            <button class="btn-primary-mf" type="submit">
              <i class="bi bi-save2"></i> Enregistrer
            </button>
            <a class="btn-sec-mf" href="${ctx}/membres">Annuler</a>
          </div>
        </div>
      </div>
    </div>
  </div>
</form>

<script>
function updateAvatarName(){
  var nom=(document.getElementById('nom').value||'').trim();
  var pre=(document.getElementById('prenom').value||'').trim();
  var full=pre?pre+' '+nom:nom;
  document.getElementById('avatarName').textContent=full||'—';
  var init=(pre?pre[0]:'')+(nom?nom[0]:'');
  document.getElementById('avatarInitials').textContent=init.toUpperCase()||'?';
}
function updateAvatarType(){
  var v=document.getElementById('typeMembre').value||'Type';
  document.getElementById('avatarType').textContent=v;
}
function previewPhoto(e){
  var file=e.target.files[0]; if(!file)return;
  var reader=new FileReader();
  reader.onload=function(ev){
    var img=document.getElementById('avatarPreview');
    img.src=ev.target.result; img.style.display='block';
    document.getElementById('avatarInitials').style.display='none';
  };
  reader.readAsDataURL(file);
}
document.addEventListener('DOMContentLoaded',function(){
  updateAvatarName(); updateAvatarType();
});
</script>

<%@ include file="../layout/footer.jsp" %>

<script>
document.addEventListener('DOMContentLoaded', function(){
  document.querySelectorAll('.mf-card, .mf-avatar-panel, .mf-input').forEach((el,i)=>{
    el.classList.add('reveal');
    setTimeout(()=>el.classList.add('visible'), 80 + i*30);
  });
});
</script>