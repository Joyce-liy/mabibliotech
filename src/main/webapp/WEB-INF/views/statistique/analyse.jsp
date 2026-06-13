<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="../layout/header.jsp" %>
<%@ include file="../layout/sidebar.jsp" %>

<!-- Page Header -->
<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
    <div>
        <h1 class="h3 mb-1">Analyses &amp; Statistiques</h1>
        <p class="text-muted mb-0 small">Analyse détaillée de l'activité de la bibliothèque universitaire.</p>
    </div>
    <form method="get" action="${ctx}/statistiques" class="d-flex align-items-center gap-2 bg-white border rounded px-3 py-2" style="box-shadow:var(--shadow-sm);">
        <label class="form-label mb-0 small text-muted fw-semibold text-nowrap">Filtrer par année :</label>
        <input type="number" name="annee" value="${annee}" min="2020" max="2030"
               class="form-control form-control-sm" style="width:90px;">
        <button type="submit" class="btn btn-primary btn-sm px-3">
            <i class="bi bi-funnel"></i> Filtrer
        </button>
    </form>
</div>

<!-- KPI Cards -->
<div class="row g-3 mb-4">
    <div class="col-md-4">
        <div class="page-card p-3">
            <div class="d-flex justify-content-between align-items-start">
                <div>
                    <div class="text-muted text-uppercase fw-semibold mb-2" style="font-size:.7rem;letter-spacing:.08em;">Ouvrages référencés</div>
                    <div style="font-size:2.2rem;font-weight:700;color:var(--app-primary);line-height:1;">${stats.totalOuvrages}</div>
                </div>
                <div style="width:44px;height:44px;background:var(--app-primary-soft);border-radius:10px;display:flex;align-items:center;justify-content:center;">
                    <i class="bi bi-journal-bookmark" style="font-size:1.3rem;color:var(--app-primary);"></i>
                </div>
            </div>
        </div>
    </div>
    <div class="col-md-4">
        <div class="page-card p-3">
            <div class="d-flex justify-content-between align-items-start">
                <div>
                    <div class="text-muted text-uppercase fw-semibold mb-2" style="font-size:.7rem;letter-spacing:.08em;">Emprunts en cours</div>
                    <div style="font-size:2.2rem;font-weight:700;color:var(--app-success);line-height:1;">${stats.empruntsEnCours}</div>
                </div>
                <div style="width:44px;height:44px;background:var(--app-success-soft);border-radius:10px;display:flex;align-items:center;justify-content:center;">
                    <i class="bi bi-arrow-left-right" style="font-size:1.3rem;color:var(--app-success);"></i>
                </div>
            </div>
        </div>
    </div>
    <div class="col-md-4">
        <div class="page-card p-3">
            <div class="d-flex justify-content-between align-items-start">
                <div>
                    <div class="text-muted text-uppercase fw-semibold mb-2" style="font-size:.7rem;letter-spacing:.08em;">Recettes pénalités impayées</div>
                    <div style="font-size:2.2rem;font-weight:700;color:var(--app-danger);line-height:1;">
                        <fmt:formatNumber value="${stats.montantPenalitesImpayees}" type="number" maxFractionDigits="0"/> FCFA
                    </div>
                </div>
                <div style="width:44px;height:44px;background:var(--app-danger-soft);border-radius:10px;display:flex;align-items:center;justify-content:center;">
                    <i class="bi bi-cash-coin" style="font-size:1.3rem;color:var(--app-danger);"></i>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Graphique + Taux retard -->
<div class="row g-3 mb-4">
    <!-- Graphique évolution -->
    <div class="col-lg-8">
        <div class="page-card p-3 h-100">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h6 class="mb-0 fw-semibold">
                    <i class="bi bi-bar-chart-line me-1 text-primary"></i>
                    Courbe d'activité des emprunts (${annee})
                </h6>
                <span class="badge badge-status-en-cours">Mensuel</span>
            </div>
            <div style="position:relative;height:300px;">
                <canvas id="evolutionChart"></canvas>
            </div>
        </div>
    </div>

    <!-- Taux de retard par profil -->
    <div class="col-lg-4">
        <div class="page-card p-3 h-100">
            <h6 class="fw-semibold mb-3">
                <i class="bi bi-percent me-1 text-warning"></i>
                Taux de retard par Profil
            </h6>

            <div class="text-center p-3 mb-3 rounded" style="background:#fffbeb;">
                <span class="small text-muted d-block mb-1">Moyenne Globale des litiges</span>
                <span style="font-size:1.5rem;font-weight:700;color:var(--app-warning);">
                    ${stats.empruntsEnRetard} en souffrance
                </span>
            </div>

            <div class="d-flex flex-column gap-3">
                <c:forEach var="item" items="${tauxRetard}">
                    <div>
                        <div class="d-flex justify-content-between align-items-center mb-1">
                            <span class="fw-semibold small"><c:out value="${item.categorie}" /></span>
                            <span class="small fw-semibold" style="color:var(--app-warning);">
                                <fmt:formatNumber value="${item.taux}" maxFractionDigits="1"/>% retards
                            </span>
                        </div>
                        <div class="progress rounded-pill" style="height:8px;background:var(--app-line);">
                            <div class="progress-bar rounded-pill"
                                 style="width:${item.taux}%;background:var(--app-warning);"
                                 role="progressbar"
                                 aria-valuenow="${item.taux}" aria-valuemin="0" aria-valuemax="100"></div>
                        </div>
                        <div class="d-flex justify-content-between mt-1" style="font-size:.73rem;color:var(--app-muted);">
                            <span>${item.totalEmprunts} emprunts au total</span>
                            <span>${item.totalRetards} retards</span>
                        </div>
                    </div>
                </c:forEach>
                <c:if test="${empty tauxRetard}">
                    <div class="text-center text-muted py-3 small">Aucune donnée disponible</div>
                </c:if>
            </div>
        </div>
    </div>
</div>

<!-- Top 10 Ouvrages -->
<div class="page-card p-0 mb-4">
    <div class="p-3 border-bottom d-flex justify-content-between align-items-center">
        <h6 class="mb-0 fw-semibold">
            <i class="bi bi-trophy me-1 text-warning"></i>
            Palmarès — Top 10 ouvrages les plus empruntés
        </h6>
        <span class="badge badge-status-rendu fw-semibold">Indicateur de Performance</span>
    </div>
    <div class="table-responsive">
        <table class="table table-hover mb-0">
            <thead class="table-light">
                <tr>
                    <th style="width:60px;">Rang</th>
                    <th>Titre</th>
                    <th>Auteur</th>
                    <th>Catégorie</th>
                    <th class="text-center">Emprunts</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="ouvrage" items="${topOuvrages}" varStatus="s">
                    <tr>
                        <td>
                            <c:choose>
                                <c:when test="${s.index == 0}">
                                    <span style="font-size:1.2rem;">1</span>
                                </c:when>
                                <c:when test="${s.index == 1}">
                                    <span style="font-size:1.2rem;">2</span>
                                </c:when>
                                <c:when test="${s.index == 2}">
                                    <span style="font-size:1.2rem;">3</span>
                                </c:when>
                                <c:otherwise>
                                   <span style="font-size:1.2rem;">4</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td class="fw-semibold"><c:out value="${ouvrage.titre}" /></td>
                        <td class="text-muted"><c:out value="${ouvrage.auteur}" /></td>
                        <td>
                            <span class="badge badge-status-en-cours"><c:out value="${ouvrage.categorie}" /></span>
                        </td>
                        <td class="text-center">
                            <span class="fw-bold" style="color:var(--app-primary);font-size:1rem;">${ouvrage.nbEmprunts}</span>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty topOuvrages}">
                    <tr>
                        <td colspan="5" class="text-center text-muted py-4">
                            <i class="bi bi-inbox" style="font-size:1.5rem;opacity:.3;display:block;margin-bottom:.5rem;"></i>
                            Aucune donnée d'emprunt enregistrée.
                        </td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>
</div>

<!-- Chart.js -->
<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
<script>
document.addEventListener("DOMContentLoaded", function () {
    const labels = [];
    const data = [];

    <c:forEach var="d" items="${evolution}">
        labels.push("${d.nomMois}");
        data.push(${d.nbEmprunts});
    </c:forEach>

    if (labels.length === 0) {
        ["Jan","Fév","Mar","Avr","Mai","Jun","Jul","Aoû","Sep","Oct","Nov","Déc"]
            .forEach(m => { labels.push(m); data.push(0); });
    }

    new Chart(document.getElementById("evolutionChart"), {
        type: "bar",
        data: {
            labels: labels,
            datasets: [{
                label: "Emprunts",
                data: data,
                backgroundColor: "rgba(37,87,167,0.8)",
                borderRadius: 6,
                borderSkipped: false,
                barPercentage: 0.6
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { display: false } },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: { stepSize: 1, font: { size: 11 } },
                    grid: { color: "#f1f5f9" }
                },
                x: {
                    ticks: { font: { size: 11 } },
                    grid: { display: false }
                }
            }
        }
    });
});
</script>

<%@ include file="../layout/footer.jsp" %>