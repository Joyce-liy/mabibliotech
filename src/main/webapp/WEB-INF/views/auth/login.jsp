<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!doctype html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Connexion — Bibliothèque Universitaire</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <link href="${ctx}/assets/css/app.css" rel="stylesheet">
    <style>
        * { box-sizing: border-box; }

        body { margin:0; padding:0; font-family:'Inter',sans-serif; }

        .login-wrapper {
            min-height: 100vh;
            display: grid;
            grid-template-columns: 1fr 1fr;
        }

        /* ============ Panneau gauche — image de bibliothèque + overlay animé ============ */
        .login-left {
            position: relative;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: flex-start;
            padding: 3rem 3.5rem;
            overflow: hidden;
            background-image: url('${ctx}/assets/img/library-bg.jpg');
            background-size: cover;
            background-position: center;
        }

        /* Overlay dégradé sombre pour garder le texte lisible */
        .login-left::before {
            content: '';
            position: absolute;
            inset: 0;
            background: linear-gradient(145deg, rgba(15,23,42,.92) 0%, rgba(30,58,138,.85) 55%, rgba(37,87,167,.75) 100%);
            z-index: 1;
        }

        /* Cercles décoratifs flottants */
        .login-left .floating-circle {
            position: absolute;
            border-radius: 50%;
            background: rgba(255,255,255,.06);
            z-index: 2;
            animation: floatCircle 12s ease-in-out infinite;
        }
        .login-left .circle-1 { top:-120px; right:-120px; width:380px; height:380px; animation-delay: 0s; }
        .login-left .circle-2 { bottom:-80px; left:-80px; width:280px; height:280px; animation-delay: 2s; }
        .login-left .circle-3 { top:40%; right:10%; width:140px; height:140px; animation-delay: 4s; background: rgba(255,255,255,.04); }

        @keyframes floatCircle {
            0%, 100% { transform: translate(0,0) scale(1); }
            50% { transform: translate(-15px, 20px) scale(1.06); }
        }

        /* Particules lumineuses */
        .particle {
            position: absolute;
            width: 6px; height: 6px;
            background: rgba(255,255,255,.6);
            border-radius: 50%;
            z-index: 2;
            animation: particleRise linear infinite;
            box-shadow: 0 0 8px rgba(255,255,255,.6);
        }
        @keyframes particleRise {
            0%   { transform: translateY(0) scale(0); opacity: 0; }
            10%  { opacity: .8; transform: translateY(-10px) scale(1); }
            90%  { opacity: .4; }
            100% { transform: translateY(-420px) scale(.3); opacity: 0; }
        }

        /* Contenu texte au-dessus de l'overlay */
        .login-left .content { position: relative; z-index: 3; }

        .login-left .brand-icon {
            width: 52px; height: 52px;
            background: rgba(255,255,255,.15);
            border-radius: 14px;
            display: flex; align-items: center; justify-content: center;
            font-size: 1.5rem; color: #fff;
            margin-bottom: 2rem;
            backdrop-filter: blur(8px);
            border: 1px solid rgba(255,255,255,.2);
            animation: popIn .7s ease both, pulseGlow 3s ease-in-out infinite 1s;
        }

        @keyframes popIn {
            0% { opacity:0; transform: scale(.6) rotate(-10deg); }
            100% { opacity:1; transform: scale(1) rotate(0); }
        }
        @keyframes pulseGlow {
            0%, 100% { box-shadow: 0 0 0 0 rgba(255,255,255,.25); }
            50% { box-shadow: 0 0 0 10px rgba(255,255,255,0); }
        }

        .login-left h2 {
            font-family: 'Inter', sans-serif;
            font-size: 2.1rem;
            font-weight: 700;
            color: #fff;
            line-height: 1.2;
            margin-bottom: 1rem;
            opacity: 0;
            animation: slideUpFade .8s ease forwards .15s;
            text-shadow: 0 4px 24px rgba(0,0,0,.45);
        }

        .login-left p {
            color: rgba(255,255,255,.78);
            font-size: .95rem;
            line-height: 1.7;
            max-width: 380px;
            opacity: 0;
            animation: slideUpFade .8s ease forwards .35s;
        }

        @keyframes slideUpFade {
            0% { opacity: 0; transform: translateY(18px); }
            100% { opacity: 1; transform: translateY(0); }
        }

        .login-left .feature-list {
            margin-top: 2rem;
            display: flex;
            flex-direction: column;
            gap: .75rem;
        }

        .login-left .feature-item {
            display: flex;
            align-items: center;
            gap: .75rem;
            color: rgba(255,255,255,.85);
            font-size: .875rem;
            opacity: 0;
            transform: translateX(-16px);
            animation: slideRightFade .6s ease forwards;
            background: rgba(255,255,255,.05);
            border: 1px solid rgba(255,255,255,.08);
            border-radius: 10px;
            padding: .55rem .8rem;
            backdrop-filter: blur(4px);
            transition: background .25s ease, transform .25s ease;
        }
        .login-left .feature-item:hover {
            background: rgba(255,255,255,.12);
            transform: translateX(4px);
        }
        .login-left .feature-item:nth-child(1) { animation-delay: .55s; }
        .login-left .feature-item:nth-child(2) { animation-delay: .7s; }
        .login-left .feature-item:nth-child(3) { animation-delay: .85s; }
        .login-left .feature-item:nth-child(4) { animation-delay: 1s; }

        @keyframes slideRightFade {
            to { opacity: 1; transform: translateX(0); }
        }

        .login-left .feature-item i {
            width: 28px; height: 28px;
            background: rgba(255,255,255,.15);
            border-radius: 8px;
            display: flex; align-items: center; justify-content: center;
            font-size: .85rem;
            flex-shrink: 0;
        }

        /* ============ Panneau droit — formulaire ============ */
        .login-right {
            background: #f5f7fb;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 2rem;
            position: relative;
        }

        .login-card {
            background: #ffffff;
            border-radius: 16px;
            box-shadow: 0 8px 40px rgba(15,28,46,.1);
            padding: 2.5rem 2rem;
            width: 100%;
            max-width: 400px;
            opacity: 0;
            transform: translateY(24px) scale(.98);
            animation: cardEnter .7s cubic-bezier(.22,1,.36,1) forwards .1s;
        }

        @keyframes cardEnter {
            to { opacity: 1; transform: translateY(0) scale(1); }
        }

        .login-card .card-header-text {
            text-align: center;
            margin-bottom: 2rem;
        }

        .login-card .card-header-text .avatar {
            width: 56px; height: 56px;
            background: #dbeafe;
            border-radius: 14px;
            display: flex; align-items: center; justify-content: center;
            font-size: 1.5rem; color: #2557a7;
            margin: 0 auto 1rem;
            animation: avatarBounce 1.4s ease-in-out infinite;
        }

        @keyframes avatarBounce {
            0%, 100% { transform: translateY(0) rotate(0deg); }
            50% { transform: translateY(-4px) rotate(3deg); }
        }

        .login-card .card-header-text h1 {
            font-size: 1.4rem;
            font-weight: 700;
            color: #182230;
            margin-bottom: .3rem;
        }

        .login-card .card-header-text p {
            color: #667085;
            font-size: .875rem;
            margin: 0;
        }

        .input-group-icon {
            position: relative;
            transition: transform .2s ease;
        }

        .input-group-icon i {
            position: absolute;
            left: .85rem;
            top: 50%;
            transform: translateY(-50%);
            color: #667085;
            font-size: .9rem;
            pointer-events: none;
            z-index: 5;
            transition: color .2s ease;
        }

        .input-group-icon input {
            padding-left: 2.4rem;
            transition: box-shadow .25s ease, border-color .25s ease, transform .15s ease;
        }

        .input-group-icon input:focus {
            border-color: #2557a7;
            box-shadow: 0 0 0 4px rgba(37,87,167,.12);
            transform: translateY(-1px);
        }

        .input-group-icon input:focus + .toggle-password,
        .input-group-icon:has(input:focus) i:first-child {
            color: #2557a7;
        }

        .toggle-password {
            position: absolute;
            right: .85rem;
            top: 50%;
            transform: translateY(-50%);
            background: none;
            border: none;
            color: #667085;
            cursor: pointer;
            padding: 0;
            z-index: 5;
            font-size: .9rem;
            transition: color .2s ease, transform .2s ease;
        }

        .toggle-password:hover { color: #2557a7; transform: translateY(-50%) scale(1.15); }

        .btn-login {
            background: linear-gradient(135deg, #2557a7, #1d4ed8);
            background-size: 200% 200%;
            border: none;
            color: #fff;
            font-weight: 600;
            font-size: .9rem;
            padding: .7rem 1rem;
            border-radius: 8px;
            width: 100%;
            transition: all .25s ease;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: .5rem;
            position: relative;
            overflow: hidden;
        }

        .btn-login:hover {
            background-position: 100% 100%;
            box-shadow: 0 6px 20px rgba(37,87,167,.45);
            transform: translateY(-2px);
            color: #fff;
        }

        .btn-login:active { transform: translateY(0) scale(.98); }

        /* Effet ripple au clic */
        .btn-login .ripple {
            position: absolute;
            border-radius: 50%;
            background: rgba(255,255,255,.5);
            transform: scale(0);
            animation: ripple .6s ease-out;
            pointer-events: none;
        }
        @keyframes ripple {
            to { transform: scale(4); opacity: 0; }
        }

        .divider {
            display: flex;
            align-items: center;
            gap: .75rem;
            margin: 1.25rem 0;
            font-size: .78rem;
            color: #667085;
        }

        .divider::before, .divider::after {
            content: '';
            flex: 1;
            height: 1px;
            background: #d9e1ec;
        }

        .mb-3, .mb-4 {
            opacity: 0;
            animation: slideUpFade .6s ease forwards;
        }
        .mb-3 { animation-delay: .4s; }
        .mb-4 { animation-delay: .5s; }
        .login-card form button[type="submit"] { animation: slideUpFade .6s ease forwards .6s; opacity:0; }

        @media (max-width: 768px) {
            .login-wrapper { grid-template-columns: 1fr; }
            .login-left { display: none; }
            .login-right { background: linear-gradient(145deg, #0f172a, #1e3a8a); }
            .login-card { box-shadow: 0 12px 40px rgba(0,0,0,.25); }
        }
    </style>
</head>
<body>

<div class="login-wrapper">

    <!-- Panneau gauche : image de bibliothèque + overlay animé -->
    <div class="login-left">
        <span class="floating-circle circle-1"></span>
        <span class="floating-circle circle-2"></span>
        <span class="floating-circle circle-3"></span>

        <!-- Particules générées dynamiquement par JS -->
        <div id="particles"></div>

        <div class="content">
            <div class="brand-icon">
                <img src="${ctx}/assets/img/logo.jpg" alt="Logo" style="width:100%;height:100%;object-fit:cover;border-radius:12px;" />
            </div>
            <h2>Bibliothèque<br>Universitaire</h2>
            <p>Gérez vos ouvrages, membres et emprunts depuis une interface centralisée et moderne.</p>

            <div class="feature-list">
                <div class="feature-item">
                    <i class="bi bi-journal-bookmark"></i>
                    <span>Catalogue complet des ouvrages</span>
                </div>
                <div class="feature-item">
                    <i class="bi bi-people"></i>
                    <span>Gestion des membres et abonnements</span>
                </div>
                <div class="feature-item">
                    <i class="bi bi-arrow-left-right"></i>
                    <span>Suivi des emprunts en temps réel</span>
                </div>
                <div class="feature-item">
                    <i class="bi bi-bar-chart-line"></i>
                    <span>Statistiques et analyses détaillées</span>
                </div>
            </div>
        </div>
    </div>

    <!-- Panneau droit -->
    <div class="login-right">
        <div class="login-card">

            <div class="card-header-text">
                <div class="avatar">
                    <i class="bi bi-person-lock"></i>
                </div>
                <h1>Connexion</h1>
                <p>Entrez vos identifiants pour accéder au système</p>
            </div>

            <c:if test="${not empty error}">
                <div class="alert alert-danger d-flex align-items-center gap-2 mb-3" style="font-size:.85rem;">
                    <i class="bi bi-exclamation-circle-fill flex-shrink-0"></i>
                    <c:out value="${error}" />
                </div>
            </c:if>

            <form method="post" action="${ctx}/login" id="loginForm">
                <div class="mb-3">
                    <label class="form-label" for="email">Adresse email</label>
                    <div class="input-group-icon">
                        <i class="bi bi-envelope"></i>
                        <input class="form-control"
                               type="email"
                               id="email"
                               name="email"
                               placeholder="admin@bibliotheque.univ.cm"
                               required
                               autofocus>
                    </div>
                </div>

                <div class="mb-4">
                    <label class="form-label" for="motDePasse">Mot de passe</label>
                    <div class="input-group-icon" style="position:relative;">
                        <i class="bi bi-lock"></i>
                        <input class="form-control"
                               type="password"
                               id="motDePasse"
                               name="motDePasse"
                               placeholder="••••••••"
                               required
                               style="padding-right:2.5rem;">
                        <button type="button" class="toggle-password" onclick="togglePassword()">
                            <i class="bi bi-eye" id="eyeIcon"></i>
                        </button>
                    </div>
                </div>

                <button class="btn-login" type="submit" id="submitBtn">
                    <i class="bi bi-box-arrow-in-right"></i>
                    Se connecter
                </button>
            </form>

            <div class="divider">Système de gestion de bibliothèque</div>

            <p class="text-center mb-0" style="font-size:.78rem;color:#667085;">
                <i class="bi bi-shield-lock me-1"></i>
                Accès réservé au personnel autorisé
            </p>
        </div>
    </div>

</div>

<script>
function togglePassword() {
    const input = document.getElementById('motDePasse');
    const icon = document.getElementById('eyeIcon');
    if (input.type === 'password') {
        input.type = 'text';
        icon.className = 'bi bi-eye-slash';
    } else {
        input.type = 'password';
        icon.className = 'bi bi-eye';
    }
}

// Génération de particules lumineuses flottantes sur le panneau gauche
(function () {
    const container = document.getElementById('particles');
    if (!container) return;
    const total = 22;
    for (let i = 0; i < total; i++) {
        const p = document.createElement('span');
        p.className = 'particle';
        const left = Math.random() * 100;
        const size = 3 + Math.random() * 5;
        const duration = 8 + Math.random() * 10;
        const delay = Math.random() * 10;
        p.style.left = left + '%';
        p.style.bottom = '-10px';
        p.style.width = size + 'px';
        p.style.height = size + 'px';
        p.style.animationDuration = duration + 's';
        p.style.animationDelay = delay + 's';
        container.appendChild(p);
    }
})();

// Effet ripple sur le bouton de connexion
document.getElementById('submitBtn').addEventListener('click', function (e) {
    const btn = this;
    const circle = document.createElement('span');
    const diameter = Math.max(btn.clientWidth, btn.clientHeight);
    circle.style.width = circle.style.height = diameter + 'px';
    circle.style.left = (e.clientX - btn.getBoundingClientRect().left - diameter / 2) + 'px';
    circle.style.top = (e.clientY - btn.getBoundingClientRect().top - diameter / 2) + 'px';
    circle.classList.add('ripple');
    const old = btn.querySelector('.ripple');
    if (old) old.remove();
    btn.appendChild(circle);
});
</script>

</body>
</html>
