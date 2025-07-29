# Application ArticlesPresse - Architecture MVC avec Design Patterns

## Vue d'ensemble

Cette application permet d'annoter des articles de presse en délimitant des zones rectangulaires sur des images. Elle a été refactorisée pour utiliser une architecture propre basée sur plusieurs Design Patterns.

## Architecture

L'application utilise une architecture **MVC (Model-View-Controller)** avec les Design Patterns suivants :

### 1. **Pattern MVC**
- **Model** (`DrawingModel`) : Gère les données (image, zones, sélection, zoom)
- **View** (`ArticlesPresseView`, `ImageDisplayPanel`) : Interface utilisateur
- **Controller** (`ApplicationController`) : Logique de contrôle et coordination

### 2. **Pattern Observer**
- `ModelObserver` : Interface pour les observateurs
- `DrawingModel` : Notifie les changements aux observateurs
- `ArticlesPresseView` : S'enregistre comme observateur du modèle

### 3. **Pattern Command**
- `Command` : Interface pour encapsuler les actions
- `AddZoneCommand` : Commande pour ajouter une zone
- `DeleteSelectedZonesCommand` : Commande pour supprimer les zones sélectionnées
- `CommandManager` : Gestionnaire pour undo/redo

### 4. **Pattern Strategy**
- `DrawingTool` : Interface pour les outils de dessin
- `RectangleSelectionTool` : Outil pour créer des zones rectangulaires
- `ZoneSelectionTool` : Outil pour sélectionner/désélectionner des zones
- `NoTool` : Outil par défaut (aucune action)

## Prérequis
- Java 11 ou supérieur
- JDK installé et configuré dans PATH

## Structure du projet
```
JavaV2/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/app/
│   │   │       ├── Main.java                          # Point d'entrée
│   │   │       ├── ArticlesPresse.java               # Ancienne version
│   │   │       ├── model/
│   │   │       │   ├── DrawingModel.java             # Modèle principal
│   │   │       │   ├── ModelObserver.java            # Interface Observer
│   │   │       │   └── Zone.java                     # Classe Zone
│   │   │       ├── controller/
│   │   │       │   └── ApplicationController.java     # Contrôleur
│   │   │       ├── view/
│   │   │       │   ├── ArticlesPresseView.java       # Vue principale
│   │   │       │   └── ImageDisplayPanel.java        # Composant image
│   │   │       ├── commands/
│   │   │       │   ├── Command.java                  # Interface Command
│   │   │       │   ├── AddZoneCommand.java           # Commande ajout
│   │   │       │   ├── DeleteSelectedZonesCommand.java # Commande suppression
│   │   │       │   └── CommandManager.java          # Gestionnaire undo/redo
│   │   │       └── tools/
│   │   │           ├── DrawingTool.java              # Interface Strategy
│   │   │           ├── RectangleSelectionTool.java   # Outil création zone
│   │   │           ├── ZoneSelectionTool.java        # Outil sélection
│   │   │           └── NoTool.java                   # Outil par défaut
│   │   └── resources/
│   └── test/
│       └── java/
│           └── com/example/app/
├── lib/                    # Dépendances externes
├── bin/                    # Fichiers compilés
├── docs/                   # Documentation
├── compile.bat            # Script de compilation
├── run.bat               # Script d'exécution
└── README.md
```

## Fonctionnalités

### Gestion d'images
- Ouverture de fichiers image (JPG, PNG, GIF, BMP, TIFF)
- Zoom avant/arrière avec la molette (Ctrl + molette)
- Reset du zoom à 100%

### Interface utilisateur améliorée
- **Panneau latéral redimensionnable** : Peut être agrandi/réduit en glissant le séparateur
- **Onglets dans le panneau latéral** :
  - **Onglet Informations** : Affiche les détails de l'image courante, le zoom et le nombre de zones
  - **Onglet Images** : Historique des 10 dernières images ouvertes avec accès rapide par double-clic
- **Bouton de masquage rapide** : Double-clic sur le séparateur pour masquer/afficher le panneau
- **Défilement amélioré** : Vitesse de défilement augmentée (Shift + molette pour défilement horizontal)
- **Curseur adaptatif** : Le curseur change immédiatement selon l'outil sélectionné
- **Menu contextuel amélioré** : Le clic droit sur une zone sélectionnée affiche le menu sans désélectionner

### Gestion des zones
- Création de zones rectangulaires par glisser-déposer
- Sélection/désélection des zones existantes
- Sélection multiple avec Ctrl
- Suppression des zones sélectionnées

### Historique des actions
- **Undo** (Ctrl+Z) : Annuler la dernière action
- **Redo** (Ctrl+Y) : Rétablir la dernière action annulée
- Suppression avec la touche Delete

## Compilation
```cmd
compile.bat
```

## Exécution
```cmd
run.bat
```

## Tests
```cmd
test.bat
```

## Avantages de cette architecture

1. **Séparation des responsabilités** : Chaque classe a une responsabilité unique
2. **Extensibilité** : Facile d'ajouter de nouveaux outils ou commandes
3. **Maintenabilité** : Code organisé et modulaire
4. **Testabilité** : Chaque composant peut être testé indépendamment
5. **Réutilisabilité** : Les patterns peuvent être réutilisés dans d'autres projets
6. **Undo/Redo** : Système d'historique complet avec le pattern Command

## Design Patterns utilisés

- **MVC** : Séparation modèle/vue/contrôleur
- **Observer** : Notification automatique des changements
- **Command** : Encapsulation des actions avec undo/redo
- **Strategy** : Changement d'outils à l'exécution

Cette architecture rend l'application robuste, maintenable et facilement extensible.
