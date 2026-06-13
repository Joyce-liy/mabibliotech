<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../layout/header.jsp" %>
<%@ include file="../layout/sidebar.jsp" %>

<!-- Styles d'amélioration et animations (injection locale) -->
<style>
:root{
  --accent:#2557a7; --accent-2:#1d4ed8; --muted:#6b7280;
}
/* Layout improvements */
.page-card{transition:transform .45s cubic-bezier(.2,.9,.3,1), box-shadow .35s ease; border-radius:12px}
.page-card:hover{transform:translateY(-6px); box-shadow:0 20px 40px rgba(15,28,46,.09)}
.stat-card{background:linear-gradient(90deg,rgba(37,87,167,.06),rgba(29,78,216,.03)); border:1px solid rgba(37,87,167,.06)}
.stat-card span{text-transform:uppercase; font-size:.75rem; color:var(--muted)}
.stat-card .h3{font-weight:800; color:var(--accent)}

/* Animated counters */
.counter{font-size:1.9rem; font-weight:800; color:var(--accent)}
.counter small{display:block;font-size:.8rem;color:var(--muted)}

/* Header area */
.page-header-animated{display:flex;align-items:center;gap:1rem;padding:.75rem;border-radius:10px;background:linear-gradient(90deg,rgba(37,87,167,.06),rgba(29,78,216,.03));box-shadow:inset 0 -1px 0 rgba(255,255,255,.02)}
.page-header-animated .title{font-weight:700;font-size:1.1rem}

/* Top ouvrages table interactive */
.table-sm tbody tr{transition:background .25s ease, transform .2s ease}
.table-sm tbody tr:hover{background:linear-gradient(90deg, rgba(37,87,167,.03), rgba(29,78,216,.02)); transform:translateX(6px)}

/* Small badges and pill */
.badge-cta{background:linear-gradient(90deg,var(--accent),var(--accent-2)); color:#fff; border-radius:12px;padding:.45rem .7rem}

/* Reveal on scroll */
.reveal{opacity:0; transform:translateY(18px); transition:opacity .6s ease, transform .6s ease}
.reveal.visible{opacity:1; transform:translateY(0)}

/* Responsive tweaks */
@media(max-width:992px){ .page-header-animated{flex-direction:column;align-items:flex-start} }
</style>

<div class="d-flex flex-wrap justify-content-between align-items-center mb-3 gap-2">
    <div style="display:flex;align-items:center;gap:12px;">
        <img src="${ctx}/assets/img/logo.jpg" alt="Logo" style="width:56px;height:56px;border-radius:10px;object-fit:cover;box-shadow:0 6px 18px rgba(37,87,167,.12);" />
        <div>
            <div style="display:flex;align-items:center;gap:12px;">
                <h1 class="h3 mb-1" style="margin:0;"><c:out value="${pageTitle}" /></h1>
                <span class="badge-cta" style="font-size:.8rem;">En ligne</span>
            </div>
            <div style="display:flex;align-items:center;gap:10px;margin-top:4px;">
                <p class="text-muted mb-0" style="margin:0;font-size:.95rem;">Synthèse des activités de la bibliothèque.</p>
                <small id="lastSync" class="text-muted" style="margin-left:8px;font-size:.8rem;">Mise à jour: --</small>
            </div>
        </div>
    </div>
    <form method="post" action="${ctx}/sms/rappels">
        <button class="btn btn-outline-primary" type="submit">
            <i class="bi bi-send"></i> Rappels J-2
        </button>
    </form>
</div>

<div class="row g-3 mb-4">
    <div class="col-md-3"><div class="page-card stat-card p-3"><span class="text-muted">Ouvrages</span><div class="h3 mb-0">${stats.totalOuvrages}</div></div></div>
    <div class="col-md-3"><div class="page-card stat-card p-3"><span class="text-muted">Membres actifs</span><div class="h3 mb-0">${stats.membresActifs}</div></div></div>
    <div class="col-md-3"><div class="page-card stat-card p-3"><span class="text-muted">Emprunts en cours</span><div class="h3 mb-0">${stats.empruntsEnCours}</div></div></div>
    <div class="col-md-3"><div class="page-card stat-card p-3"><span class="text-muted">Retards</span><div class="h3 mb-0">${stats.empruntsEnRetard}</div></div></div>
</div>

<c:if test="${not empty membresBloques}">
    <div class="page-card p-3 mb-3">
        <div class="d-flex justify-content-between align-items-center mb-2">
            <h2 class="h5 mb-0 text-danger">Membres bloques</h2>
            <a class="btn btn-sm btn-outline-secondary" href="${ctx}/membres"><i class="bi bi-people"></i></a>
        </div>
        <div class="table-responsive">
            <table class="table table-sm">
                <thead><tr><th>Carte</th><th>Nom</th><th>Contact</th></tr></thead>
                <tbody>
                <c:forEach var="membre" items="${membresBloques}">
                    <tr>
                        <td><c:out value="${membre.carteNumero}" /></td>
                        <td><c:out value="${membre.nom}" /> <c:out value="${membre.prenom}" /></td>
                        <td><c:out value="${membre.telephone}" /></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</c:if>

<div class="row g-3">
    <div class="col-lg-6">
        <div class="page-card p-3 h-100">
            <div class="d-flex justify-content-between align-items-center mb-2">
                <h2 class="h5 mb-0">Top ouvrages</h2>
                <a class="btn btn-sm btn-outline-secondary" href="${ctx}/export/pdf/rapport"><i class="bi bi-file-earmark-pdf"></i></a>
            </div>
            <div class="table-responsive">
                <table class="table table-sm">
                    <thead><tr><th>Titre</th><th>Categorie</th><th>Emprunts</th></tr></thead>
                    <tbody>
                    <c:forEach var="item" items="${topOuvrages}">
                        <tr>
                            <td><c:out value="${item.titre}" /></td>
                            <td><c:out value="${item.categorie}" /></td>
                            <td>${item.nbEmprunts}</td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
    <div class="col-lg-6">
        <div class="page-card p-3 h-100">
            <h2 class="h5 mb-2">Evolution mensuelle</h2>
            <div class="table-responsive">
                <table class="table table-sm">
                    <thead><tr><th>Mois</th><th>Emprunts</th></tr></thead>
                    <tbody>
                    <c:forEach var="item" items="${evolution}">
                        <tr><td><c:out value="${item.nomMois}" /></td><td>${item.nbEmprunts}</td></tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<c:if test="${not empty tauxRetard}">
    <div class="page-card p-3 mt-3">
        <h2 class="h5 mb-2">Taux de retard par categorie</h2>
        <div class="table-responsive">
            <table class="table table-sm">
                <thead><tr><th>Categorie</th><th>Total</th><th>Retards</th><th>Taux</th></tr></thead>
                <tbody>
                <c:forEach var="item" items="${tauxRetard}">
                    <tr>
                        <td><c:out value="${item.categorie}" /></td>
                        <td>${item.totalEmprunts}</td>
                        <td>${item.nbRetards}</td>
                        <td>${item.tauxRetardPct}%</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</c:if>

<c:if test="${not empty retards}">
    <div class="page-card p-3 mt-3">
        <h2 class="h5 mb-2 text-danger">Alertes retards</h2>
        <div class="table-responsive">
            <table class="table table-sm">
                <thead><tr><th>Membre</th><th>Ouvrage</th><th>Retour prevu</th></tr></thead>
                <tbody>
                <c:forEach var="emprunt" items="${retards}">
                    <tr>
                        <td><c:out value="${emprunt.membre.carteNumero}" /></td>
                        <td><c:out value="${emprunt.ouvrage.titre}" /></td>
                        <td>${emprunt.dateRetourPrevue}</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</c:if>

<!-- Add interactive scripts just before footer include -->
<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
<script>
// Reveal on scroll and counter animation
(function(){
  function animateCounter(el){
    const target = +el.dataset.target || +el.textContent || 0;
    const duration = 1200; // ms
    let start = 0; const step = Math.max(1, Math.floor(target/ (duration/16)));
    let raf;
    function run(){
      start += step; if(start >= target) { el.textContent = target; cancelAnimationFrame(raf); return; }
      el.textContent = start; raf = requestAnimationFrame(run);
    }
    run();
  }

  const io = new IntersectionObserver(entries => {
    entries.forEach(ent => {
      if(ent.isIntersecting){
        ent.target.classList.add('visible');
        const counters = ent.target.querySelectorAll('.counter[data-target]');
        counters.forEach(c=>{ if(!c.dataset.animated){ c.dataset.animated = '1'; animateCounter(c); } });
      }
    });
  }, {threshold:0.15});

  document.querySelectorAll('.page-card').forEach(card=>io.observe(card));

  // Initialize small sparkline charts in stat cards if canvas present
  document.querySelectorAll('.sparkline').forEach(canvas=>{
    try{
      const labels = (canvas.dataset.labels||'').split(',').filter(Boolean);
      const data = (canvas.dataset.values||'').split(',').map(v=>+v||0);
      new Chart(canvas.getContext('2d'),{type:'line',data:{labels:labels.length?labels:[''],datasets:[{data:data,fill:false,borderColor:'#2563eb',tension:0.4,pointRadius:0}]},options:{responsive:true,maintainAspectRatio:false,plugins:{legend:{display:false}},scales:{x:{display:false},y:{display:false}}}});
    }catch(e){console.warn('sparkline',e)}
  });

})();

// Optional: animate table rows sequentially
(function(){
  document.querySelectorAll('.table-sm tbody tr').forEach((tr,i)=>{
    tr.style.transitionDelay = (i*30)+'ms';
    tr.classList.add('reveal');
    setTimeout(()=>tr.classList.add('visible'), 120 + i*30);
  });
})();

// Affiche l'heure de mise à jour (locale) à côté du titre
 (function(){
   try{
     const el = document.getElementById('lastSync');
     if(el){
       const now = new Date();
       const opts = { year:'numeric', month:'short', day:'2-digit', hour:'2-digit', minute:'2-digit' };
       el.textContent = 'Mise à jour: ' + now.toLocaleString(undefined, opts);
     }
   }catch(e){/* ignore */}
 })();
</script>

<%@ include file="../layout/footer.jsp" %>
