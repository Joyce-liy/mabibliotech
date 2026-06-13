# Bibliotheque Universitaire

Application web Java EE/Jakarta EE pour la gestion d'une bibliotheque universitaire: catalogue, membres, emprunts, retours, penalites, exports CSV/PDF, upload de photos, statistiques et rappels SMS.

## Stack

- Java 17+
- Jakarta Servlet 6 / JSP 3
- JPA/Hibernate 6
- MySQL 8
- Maven
- iText 7, OpenCSV, jBCrypt
- Tomcat 10.1 ou WildFly compatible Jakarta EE

## Demarrage

1. Creer la base MySQL:

```sql
SOURCE sql/create_tables.sql;
SOURCE sql/data_initial.sql;
```

2. Adapter la connexion dans `src/main/resources/db.properties`:

```properties
db.url=jdbc:mysql://localhost:3306/bibliotheque_universitaire?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.user=root
db.password=
```

3. Compiler:

```bash
mvn clean package
```

4. Deployer `target/bibliotheque-universitaire.war` dans Tomcat 10.1 ou WildFly.

5. Connexion initiale:

```text
Email: admin@bibliotheque.univ.cm
Mot de passe: admin123
```

L'application cree aussi ce compte au demarrage si la table `utilisateur` est vide.

## Fonctionnalites livrees

- Authentification BCrypt et sessions.
- Filtres `AuthFilter` et `EncodingFilter`.
- CRUD ouvrages et membres.
- Creation d'emprunt avec controles: membre actif, carte valide, penalites impayees, stock disponible.
- Retour d'ouvrage avec generation automatique de penalite a 50 FCFA/jour.
- Paginaton, recherche et filtres sur les listes.
- Exports CSV pour ouvrages, membres et emprunts.
- Exports PDF pour carte membre, recu d'emprunt et rapport mensuel.
- Dashboard statistiques: compteurs, top ouvrages, evolution mensuelle, taux de retard.
- Upload images JPG/PNG jusqu'a 2 Mo.
- Pages d'erreur 403, 404, 500.
- Service SMS centralise; par defaut il journalise les messages pour rester deployable sans modem. Le point d'integration est `SmsService.send(...)`.

## Structure

```text
src/main/java/cm/edu/bibliotheque/
  entity/       Entites JPA
  dao/          Acces donnees
  service/      Logique metier
  servlet/      Controleurs MVC
  filter/       Securite et encodage
  util/         JPA, BCrypt, pagination, upload
src/main/webapp/WEB-INF/views/
  layout/       Header, sidebar, footer
  auth/         Connexion
  ouvrage/      Catalogue
  membre/       Membres
  emprunt/      Emprunts et retours
  penalite/     Penalites
  statistique/  Dashboard
sql/            Scripts MySQL
docs/           Diagrammes PlantUML
```

## Tests

```bash
mvn test
```

Les tests couvrent le calcul des dates de retour par type de membre et la verification BCrypt du compte initial.
