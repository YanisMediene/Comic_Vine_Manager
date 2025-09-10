# Comic Collection Manager

## Description
Comic Collection Manager est une application Java desktop conçue pour la gestion et l'organisation des collections de bandes dessinées. Elle permet aux utilisateurs de découvrir, suivre et gérer efficacement leur bibliothèque de comics tout en bénéficiant de recommandations personnalisées.

## Fonctionnalités Principales

### Pour les Utilisateurs Inscrits
- Recherche avancée de comics et de personnages
- Gestion complète de la bibliothèque personnelle
- Suivi de l'état de lecture (non commencé, en cours, terminé)
- Marquage des achats
- Recommandations personnalisées :
  - Basées sur les comics terminés
  - Fondées sur la bibliothèque existante
  - Suggestions générales (nouveautés et populaires)

### Pour les Utilisateurs Invités
- Recherche de comics et de personnages
- Découverte du catalogue
- Consultation des informations détaillées

## Prérequis
- Java JDK 11 ou supérieur
- MySQL 8.0 ou supérieur
- Maven 3.6 ou supérieur
- Une clé API Comic Vine valide

## Installation

### 1. Configuration de la Base de Données
```sql
CREATE DATABASE comics_db;
USE comics_db;
-- Exécutez le script SQL fourni comics_db.sql pour créer les tables
```

### 2. Configuration de l'Application
1. Clonez le repository :
```bash
git clone [https://devops.telecomste.fr/prinfo/2024-25/info2.git]
cd info2
```

2. Configurez le fichier `src/main/resources/application.properties` :
```properties
# Database Configuration
db.url=jdbc:mysql://localhost:3306/comicsdb
db.username=votre_username
db.password=votre_password

# API Configuration
comicvine.api.key=votre_cle_api
comicvine.api.url=https://comicvine.gamespot.com/api
```

### 3. Installation des Dépendances
```bash
mvn clean install
```

## Lancement de l'Application
```bash
mvn exec:java -Dexec.mainClass="tse.info2.Main"
```

## Structure du Projet
```
src/
├── main/
│   ├── java/tse/info2/
│   │   ├── controller/    # Contrôleurs de l'application
│   │   ├── model/         # Modèles de données
│   │   ├── database/      # Classes DAO
│   │   ├── service/       # Services métier
│   │   ├── session/       # Session utilisateur
│   │   ├── util/          # Classes utilitaires
│   │   └── view/          # Interface utilisateur
│   └── resources/         # Ressources et configurations
└── test/
│   └── java/tse/info2/    # Tests unitaires
```

## Technologies Utilisées

### Backend
- Java
- MySQL (Base de données)
- Comic Vine API

### Interface Utilisateur
- Java Swing
- FlatLaf (Look and Feel moderne)
- AWT

### Sécurité
- bcrypt (Hachage des mots de passe)

### Outils de Développement
- Maven
- JUnit & JUnit Jupiter
- Git & GitLab

## Fonctionnalités Détaillées

### Système de Recherche
L'application propose un système de recherche basique :
1. **Recherche simple** :
   - Titre du comic
   - Nom des personnages

### Système de Recommandation
L'application utilise trois types d'algorithmes de recommandation :
1. **Recommandations générales** basées sur les sorties récentes
2. **Recommandations personnalisé** basées sur la bibliothèque
3. **Recommandations de suivi** pour les séries

### Gestion de la Bibliothèque
- Ajout/Suppression de comics
- Tri et filtrage
- Suivi de lecture
- Marquage des achats

### Sécurité
- Authentification utilisateur
- Sessions sécurisées
- Mots de passe hashés avec bcrypt

### Améliorations Futures

#### Système de Recherche Avancé
1. **Critères de recherche additionnels** :
   - Auteurs et artistes
   - Année de publication
   - Genre

2. **Filtres de recherche** :
   - Par éditeur
   - Par série
   - Par état de lecture
   - Par statut d'achat

3. **Recherche instantanée** avec suggestions en temps réel

#### Fonctionnalités Sociales
1. **Système de Notation et Avis** :
   - Notes sur 5 étoiles
   - Commentaires et critiques
   - Partage des avis

2. **Communauté** :
   - Profils utilisateurs personnalisables
   - Partage de collections
   - Forums de discussion
   - Groupes de lecture

#### Fonctionnalités de Gestion Avancée
1. **Statistiques Personnelles** :
   - Temps de lecture
   - Genres préférés
   - Historique d'achats
   - Visualisation des données

2. **Organisation Avancée** :
   - Tags personnalisés
   - Collections thématiques
   - Listes de souhaits
   - Export/Import de bibliothèque

#### Intégrations Externes
1. **Connexion avec les Plateformes** :
   - Import depuis Goodreads
   - Synchronisation avec des applications mobiles
   - Intégration des prix des revendeurs

2. **Notifications** :
   - Alertes de sorties
   - Rappels de lecture
   - Notifications de prix

## Tests
Exécution des tests unitaires :
```bash
mvn test
```

## Contribution
1. Fork le projet
2. Créez votre branche de fonctionnalité (`git checkout -b feature/AmazingFeature`)
3. Committez vos changements (`git commit -m 'Add some AmazingFeature'`)
4. Push vers la branche (`git push origin feature/AmazingFeature`)
5. Ouvrez une Pull Request

## Licence
Ce projet est sous licence MIT - voir le fichier LICENSE.md pour plus de détails.

## Équipe
- BENZHA Marieme
- EL ATMIOUI Nassim
- FUSERO Clement
- MARDOUME Boutaina
- MEDIENE Yanis

## Encadrement
- M. REMY Girodon       # Java Expert
- M. ZAGARRIO Romain    # Product Owner
- M. ZEGHDALLOU Mehdi   # Coach Scrum

## Support
Pour toute question ou problème, veuillez ouvrir un ticket dans la section "Issues" du repository GitLab.