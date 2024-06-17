![OIG4-removebg-preview](https://github.com/Dinholu/tpKotlin01/assets/77453475/85781590-7e7d-4aad-9217-ff68b4ace0d8)

# QuizApp

## Description
QuizApp est une application mobile de quiz qui permet aux utilisateurs de tester leurs connaissances sur diverses catégories de sujets. L'application comprend une fonctionnalité de login, quatre fragments principaux pour naviguer entre les différentes sections du quiz, et un leaderboard pour afficher les meilleurs scores.

## Fonctionnalités
### **Login** : Authentification des utilisateurs pour accéder à l'application.

Sur la page de login l'utilisateur peut se connecter en utilisant un pseudonyme de 1 à 5 caractères. Si l'utilisateur ne donne pas de pseudonyme un toast lui previendra qu'il ne pourra pas se connecter. 
Une fois connecté le pseudo est enregistré dans les `SharedPreferences`. 

### **Categories** : Choix de la catégorie de quiz.

Sur la page des catégories, un message de bienvenue est affiché. Plusieurs thèmes sont chargés de la base de données. Chaque theme charge sa propre icone. Une fois cliqué sur un theme cela fait un appel à l'API `https://quizzapi.jomoreschi.fr/`. 

### **Quiz** : Questionnaire avec des questions relatives à la catégorie choisie.

10 questions sont récupérées de l'API et s'affiche tour à tour. Les boutons sont générés aléatoirement pour éviter que les réponses soient toujours à la meme place. Le joueur à 10 sec pour répondre : 
- S'il se trompe la bonne réponse s'affiche et clignote en vert, sa réponse devient rouge
- S'il répond correctement sa réponse devient vert et son score s'incrémente
- S'il ne repond pas dans les temps la reponse s'affiche en vert clignotant.
Une fois la reponse affichée, après un délai de 2 sec, la question suivante apparait.

Une fois les 10 questions repondues le score s'enregistre en base de données. Le joueur peut changer l'orientation du telephone cela n'affecte pas le jeu. De plus à tout moment il peut retourner a la page des catégories et cela mettra un terme au jeu. 

### **Score** : Affichage du score à la fin de chaque partie.

Une fois le jeu terminé le score est affiché ainsi que les meilleures scores de la catégorie jouée. Le joueur peut alors retourner aux catégories pour rejouer.

### **Leaderboard** : Affichage des meilleurs scores.
Sur la page `LeaderBoard` le joueur peut voir les 3 premiers meilleures scores de toutes les catégories. 

### **Deconnecter** 
Le joueur peut enfin se déconnecter afin de laisser un autre joueur s'amuser. 

## Structure de l'application
- **LoginActivity** : Activité de connexion.
- **MainActivity** : Activité principal contenant plusieurs fragments:
  - **Fragments** :
    - `CategoriesFragment` : Sélection de la catégorie de quiz.
    - `QuizFragment` : Questionnaire de quiz.
    - `ScoreFragment` : Affichage du score final.
    - `LeaderboardFragment` : Tableau des meilleurs scores.

## Installation
1. Installation developpement 

a. Clonez le dépôt :
   ```bash
   git clone https://github.com/Dinholu/quizapp.git
   ```
b. Ouvrez le projet avec Android Studio.
c. Compilez et exécutez l'application sur un appareil ou un émulateur Android.

2. Installation de l'APK
   Installer l'APK de release sur votre téléphone


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
- Authentification : SharedPreferences
- Interface utilisateur : Android Jetpack Components (Navigation, LiveData, ViewModel)

## Contributeurs
[@ShortLegsFox](https://github.com/ShortLegsFox) alias Ian Bellot <br>
[@Dinholu](https://github.com/Dinholu) alias Alizée HETT
