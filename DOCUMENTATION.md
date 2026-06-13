# 📚 Documentation de l’Application Bibliothèque Universitaire

Version: 1.0.0
Stack: Jakarta EE 10, Java 17, Hibernate 6, MySQL 8, Maven
Date: Juin 2026

---

## 🎯 Objectif du projet

Cette application permet de gérer une bibliothèque universitaire de façon complète et structurée. Elle couvre :

- la gestion des ouvrages,
- la gestion des membres,
- le suivi des emprunts et des retours,
- le calcul et la gestion des pénalités de retard,
- l’export de données et la génération de documents,
- l’envoi de rappels SMS,
- l’affichage de statistiques et d’indicateurs de performance,
- l’authentification des utilisateurs avec rôles.

Ce projet est conçu pour les bibliothécaires, les gestionnaires de bibliothèque et les utilisateurs autorisés.

---

## 🏗️ Architecture MVC

L’application utilise le modèle **MVC** (Modèle-Vue-Contrôleur) pour séparer les responsabilités :

- **Vue** : pages JSP qui affichent les informations et présentent des formulaires.
- **Contrôleur** : servlets qui reçoivent les requêtes HTTP, orchestrent la logique métier et choisissent la vue de sortie.
- **Modèle** : services et entités qui définissent la logique métier et le modèle de données.

Cette architecture garantit une application claire, maintenable et facile à faire évoluer.

### Diagramme MVC

Le diagramme suivant illustre le flux principal de l’application :

- la vue envoie la requête au contrôleur,
- le contrôleur appelle le service métier,
- le service utilise les DAO pour accéder à la base,
- les résultats sont renvoyés au contrôleur,
- le contrôleur transmet les données à la vue.

Vous trouverez le diagramme source PlantUML dans : `docs/mvc.puml`.

---

## 🧱 Modèle de données (MCD / MLD)

Le modèle de données est composé de cinq entités principales : **Utilisateur**, **Membre**, **Ouvrage**, **Emprunt** et **Pénalité**.

### MCD (Modèle Conceptuel de Données)

Le MCD représente les concepts métiers et leurs relations :

- un **membre** peut enregistrer plusieurs **emprunts**,
- un **ouvrage** peut être emprunté plusieurs fois,
- un **utilisateur** (bibliothécaire ou admin) peut enregistrer plusieurs emprunts,
- un **emprunt** peut générer au plus une **pénalité**.

Le diagramme ERD PlantUML correspondant se trouve dans : `docs/erd.puml`.

### MLD (Modèle Logique de Données)

Le MLD précise les attributs et les clés de chaque entité :

- **Utilisateur** : nom, prénom, email, mot de passe, rôle, actif, date de création.
- **Membre** : numéro de carte, nom, prénom, type de membre, téléphone, email, date d’expiration, photo, date d’inscription.
- **Ouvrage** : ISBN, titre, auteur, éditeur, année, catégorie, exemplaires total/disponible, localisation, photo, date d’ajout.
- **Emprunt** : date d’emprunt, date de retour prévue, date de retour réelle, statut, références vers membre, ouvrage et bibliothécaire.
- **Pénalité** : jours de retard, montant, statut payé, date de calcul, référence vers emprunt.

Le diagramme de classes PlantUML pour le MLD se trouve dans : `docs/uml.puml`.

---

## 📌 Description des entités

### Utilisateur

Gère les personnes autorisées à utiliser l’interface administrative. Chaque utilisateur possède un rôle et un état actif/inactif.

### Membre

Représente un membre de la bibliothèque. Sa carte peut expirer et il peut être bloqué s’il a des pénalités impayées.

### Ouvrage

Représente un livre ou une ressource. Le système suit le nombre total d’exemplaires et le nombre disponible.

### Emprunt

Représente l’acte d’emprunter un ouvrage par un membre. La durée autorisée dépend du type de membre.

### Pénalité

Représente le retard de retour et le montant facturé. Une pénalité est liée à un emprunt unique.

---

## 🧠 Règles métier principales

### Emprunts

- le membre doit être actif,
- la carte ne doit pas être expirée,
- aucun retard impayé ne doit être en cours,
- l’ouvrage doit être disponible.

### Retours

- la date réelle est enregistrée,
- le stock d’exemplaires disponibles est mis à jour,
- si le retour est après la date prévue, une pénalité est créée.

### Pénalités

- le montant est calculé selon les jours de retard,
- la pénalité est marquée payée lorsque le paiement est enregistré.

---

## 🔧 Composants techniques

- **DAO** : accès aux données via JPA/Hibernate,
- **Service** : validation et règles métier,
- **Servlet** : gestion des requêtes HTTP,
- **JSP** : interaction utilisateur et affichage,
- **JPA/Hibernate** : mapping objet-relationnel,
- **MySQL** : base de données relationnelle.

---

## 📂 Fichiers PlantUML

- `docs/mvc.puml` : diagramme MVC,
- `docs/erd.puml` : diagramme ERD / MCD,
- `docs/uml.puml` : diagramme de classes / MLD.

Ces fichiers peuvent être rendus avec PlantUML pour obtenir des schémas visuels.

---

## ✅ Conclusion

Cette documentation décrit ton application en insistant sur son architecture MVC, son modèle de données conceptuel et logique, et le rôle des composants principaux.

Elle inclut les diagrammes PlantUML nécessaires pour visualiser le MCD, le MLD et le flux MVC de l’application.
