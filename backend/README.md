 SunuXam - Backend API REST (Spring Boot 3)
Moteur Métier • Sécurité JWT • Calcul Délibération & Persistance
Java Spring Boot Spring Security PostgreSQL Swagger OpenAPI


Serveur applicatif d'entreprise en charge de l'authentification sécurisée (JWT), de la gestion des concours, de l'instruction des candidatures numériques, de la saisie des notes et du calcul automatisé des délibérations officielles.
 Sommaire
 Architecture & Couches Logiques
 Sécurité & Authentification JWT
 Logique Métier & Algorithme de Délibération
 Endpoints de l'API REST
 Structure du Code Source
 Architecture & Couches Logiques
L'application suit une architecture en couches (Layered Architecture) conforme aux meilleures pratiques Spring :

graph TD
Client["Client Web (Angular 18+)"] -->|HTTP REST / Multipart| Security["Spring Security & JwtAuthenticationFilter"]
Security --> Controller["Controllers REST (@RestController)"]
Controller --> Service["Services Métier (@Service)"]
Service --> Repository["Repositories (@Repository - Spring Data JPA)"]
Service --> Storage["Service de Stockage Fichiers (uploads/)"]
Repository --> Database[("PostgreSQL / H2 Memory")]
Contrôleurs REST : Exposition d'endpoints clairs et typés via des DTOs validés (@Valid).
Services Métier : Encapsulation des règles de gestion, algorithmes de délibération et transactions ACID (@Transactional).
Gestion Globale des Erreurs : Interception centralisée par GlobalExceptionHandler retournant des réponses standardisées conformes au format RFC Problem Details.
Service d'Upload Local : Stockage sécurisé des pièces jointes (CV, diplômes, photos d'identité) avec génération d'URLs relatives servies via /api/files/**.
 Sécurité & Authentification JWT
Authentification sans état (Stateless) via token JWT signé cryptographiquement (JJWT 0.12.6).
Hachage robuste des mots de passe avec l'algorithme BCryptPasswordEncoder.
Contrôle d'accès basé sur les rôles (RBAC) :
ROLE_CANDIDAT : Dépôt et consultation de son propre dossier, téléchargement de son relevé certifié.
ROLE_ADMIN : Accès complet au backoffice, validation/rejet des dossiers, saisie des épreuves et publication officielle.
 Logique Métier & Algorithme de Délibération
Le moteur de calcul applique les règles réglementaires de la fonction publique :

Moyenne Pondérée par Candidat :
Classement au Mérite : Tri automatique décroissant des moyennes obtenues.
Attribution des Mentions et Seuils :
Les
premiers candidats sont classés ADMIS (
).
Les candidats suivants atteignant la moyenne minimale réglementaire sont classés sur LISTE_D_ATTENTE.
Les autres candidats sont classés NON_ADMIS.
Confidentialité : Les résultats ne sont visibles du grand public et des candidats qu'après le déclenchement explicite de l'action de publication officielle par le président du jury.
 Endpoints de l'API REST
Domaine	Méthode	URI	Accès	Description
Auth	POST	/api/auth/register	Public	Inscription d'un nouveau candidat
Auth	POST	/api/auth/login	Public	Connexion et génération du token JWT
Concours	GET	/api/concours	Public	Liste des concours ouverts & publiés
Concours	GET	/api/concours/{id}	Public	Fiche détaillée d'un concours et ses épreuves
Candidature	POST	/api/candidatures	CANDIDAT	Dépôt multipart (CV, Diplôme, Photo)
Candidature	GET	/api/candidatures/my	CANDIDAT	Historique personnel de candidatures
Résultats	GET	/api/resultats/concours/{id}	Public	Liste officielle des admis et mentions
Admin	GET	/api/admin/dashboard	ADMIN	Statistiques globales et métriques
Admin	PUT	/api/admin/candidatures/{id}/statut	ADMIN	Validation ou rejet avec motif
Admin	POST	/api/admin/notes/bulk	ADMIN	Saisie matricielle des notes des candidats
Admin	POST	/api/admin/deliberation/simuler	ADMIN	Simulation du classement et des moyennes
Admin	POST	/api/admin/deliberation/publier	ADMIN	Publication officielle des résultats
Fichiers	GET	/api/files/{filename}	Public / Auth	Visualisation d'un document justificatif



Structure du Code Source
backend/
├── src/main/java/com/opticoncours/
│   ├── config/              # Configuration Security, OpenAPI, CORS & Seeder
│   ├── controller/          # Contrôleurs REST documentés Swagger
│   ├── dto/                 # Request & Response Data Transfer Objects
│   ├── entity/              # Entités relationnelles JPA / Hibernate
│   ├── exception/           # Exceptions métier et GlobalExceptionHandler
│   ├── repository/          # Interfaces Spring Data JPA
│   ├── security/            # JwtUtils, JwtAuthenticationFilter, UserDetailsService
│   └── service/             # Services métier et logique transactionnelle
├── src/main/resources/
│   └── application.yml      # Configuration Spring Boot, JPA, JWT et chemins
├── uploads/                 # Dossier local de stockage des pièces jointes
└── pom.xml                  # Descripteur de projet Maven et dépendances
SunuXam • Projet de Licence 3 Génie Logiciel 2026