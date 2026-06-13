<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../layout/header.jsp" %>
<%@ include file="../layout/sidebar.jsp" %>

<!-- UI enhancements: animations and reveal behavior -->
<style>
:root{--accent:#2557a7;--muted:#6b7280}
.page-card{transition:transform .35s ease,box-shadow .35s ease;border-radius:10px}
.page-card:hover{transform:translateY(-6px);box-shadow:0 18px 36px rgba(15,28,46,.06)}
.table-hover tbody tr{transition:transform .2s ease,background .25s ease}
.table-hover tbody tr:hover{transform:translateX(8px);background:linear-gradient(90deg,rgba(37,87,167,.03),rgba(29,78,216,.02))}
.reveal{opacity:0;transform:translateY(14px);transition:opacity .6s ease,transform .6s ease}
.reveal.visible{opacity:1;transform:translateY(0)}
.table-footer{display:flex;justify-content:space-between;align-items:center;padding:.75rem 0}
</style>

<div class="d-flex flex-wrap justify-content-between align-items-center mb-3 gap-2">
    <div>
        <h1 class="h3 mb-1">Penalites</h1>
        <p class="text-muted mb-0">Retards calcules a 50 FCFA par jour.</p>
    </div>
    <div class="btn-group">
        <a class="btn btn-outline-secondary" href="${ctx}/penalites">Toutes</a>
        <a class="btn btn-outline-warning" href="${ctx}/penalites?payee=false">Impayees</a>
    </div>
</div>

<div class="page-card p-0">
    <div class="table-responsive">
        <table class="table table-hover mb-0">
            <thead class="table-light">
            <tr>
                <th>Membre</th>
                <th>Ouvrage</th>
                <th>Jours</th>
                <th>Montant</th>
                <th>Statut</th>
                <th class="text-end">Paiement</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="penalite" items="${penalites}">
                <tr>
                    <td><c:out value="${penalite.emprunt.membre.carteNumero}" /></td>
                    <td><c:out value="${penalite.emprunt.ouvrage.titre}" /></td>
                    <td>${penalite.joursRetard}</td>
                    <td>${penalite.montantFcfa} FCFA</td>
                    <td>
                        <span class="badge ${penalite.payee ? 'text-bg-success' : 'text-bg-warning'}">
                            ${penalite.payee ? 'Payee' : 'Impayee'}
                        </span>
                    </td>
                    <td class="text-end">
                        <c:if test="${not penalite.payee}">
                            <form class="d-flex justify-content-end align-items-center gap-2" method="post"
                                  action="${ctx}/penalites/payer/${penalite.id}">
                                <input class="form-control form-control-sm" style="max-width: 120px;" type="number"
                                       name="montant" min="1" step="0.01" value="${penalite.montantFcfa}" required>
                                <select class="form-select form-select-sm" style="max-width: 150px;" name="moyen" required>
                                    <option value="ESPECES">Especes</option>
                                    <option value="CARTE_BANCAIRE">CB</option>
                                    <option value="MOBILE_MONEY">Mobile money</option>
                                    <option value="VIREMENT">Virement</option>
                                </select>
                                <input class="form-control form-control-sm" style="max-width: 170px;" type="text"
                                       name="reference" placeholder="Reference recu" required>
                                <button class="btn btn-sm btn-outline-success" type="submit" title="Enregistrer le paiement">
                                    <i class="bi bi-check2"></i>
                                </button>
                            </form>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty penalites}">
                <tr><td colspan="6" class="text-center text-muted py-4">Aucune penalite trouvee.</td></tr>
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

<script>
// Reveal cards and animate table rows
document.addEventListener('DOMContentLoaded', function(){
  document.querySelectorAll('.page-card, .table-responsive table tbody tr').forEach((el,i)=>{
    el.classList.add('reveal');
    setTimeout(()=>el.classList.add('visible'), 80 + i*30);
  });
});
</script>
