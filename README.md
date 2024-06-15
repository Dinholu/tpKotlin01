# QuizApp

## Description
QuizApp est une application mobile de quiz qui permet aux utilisateurs de tester leurs connaissances sur diverses catégories de sujets. L'application comprend une fonctionnalité de login, quatre fragments principaux pour naviguer entre les différentes sections du quiz, et un leaderboard pour afficher les meilleurs scores.

## Fonctionnalités
- **Login** : Authentification des utilisateurs pour accéder à l'application.
- **Categories** : Choix de la catégorie de quiz.
- **Quiz** : Questionnaire avec des questions relatives à la catégorie choisie.
- **Score** : Affichage du score à la fin de chaque partie.
- **Leaderboard** : Affichage des meilleurs scores.

## Structure de l'application
- **LoginActivity** : Activité de connexion.
- **Fragments** :
  - `CategoriesFragment` : Sélection de la catégorie de quiz.
  - `QuizFragment` : Questionnaire de quiz.
  - `ScoreFragment` : Affichage du score final.
  - `LeaderboardFragment` : Tableau des meilleurs scores.

## Installation
1. Clonez le dépôt :
   ```bash
   git clone https://github.com/votre-utilisateur/quizapp.git
    Ouvrez le projet avec Android Studio.
    Compilez et exécutez l'application sur un appareil ou un émulateur Android.
   ```

## Utilisation

   Ouvrez l'application et connectez-vous avec vos identifiants.
    Sélectionnez une catégorie de quiz dans le CategoriesFragment.
    Répondez aux questions du quiz dans le QuizFragment.
    Consultez votre score dans le ScoreFragment à la fin de la partie.
    Visualisez les meilleurs scores dans le LeaderboardFragment.

## Technologies utilisées

- Langage : Kotlin/Java
- Architecture : MVVM (Model-View-ViewModel)
- Base de données : Room
- API : https://quizzapi.jomoreschi.fr/
- Authentification : Firebase Authentication
- Interface utilisateur : Android Jetpack Components (Navigation, LiveData, ViewModel)

Contributeurs

