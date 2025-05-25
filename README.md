# Jeu d'Échecs - Projet JAVA 🎓

👉 [consulter le rapport ](https://github.com/QALLOUJ/Chess/blob/master1/chess.pdf)

Projet réalisé dans le cadre de la 3ᵉ année d’ingénierie informatique à Polytech Lyon.  
Développement d’un jeu d’échecs complet en Java, intégrant une interface graphique (Swing) et un mode console.

## 📁 Structure du projet

```
├── src/
│   ├── controleur/          # Contrôleurs MVC
│   ├── modele/              # Logique métier (pièces, plateau, règles, IA)
│   │   ├── ia/              # Intelligence artificielle (Minimax)
│   │   └── decorateur/      # Design Pattern Décorateur
│   └── Vue/                 # Interfaces utilisateur (console + graphique)
│
├── Main.java                # Lancement du jeu
├── Demo.java                # Classe de test de scénarios (prise en passant, roque, etc.)
├── sauvegardes/            # Fichiers PGN des parties sauvegardées
```

## 🧠 Fonctionnalités

- ♟ Plateau 8x8 et placement initial
- ➡️ Déplacements classiques
- ♜ Roque (court et long)
- 🥷 Prise en passant
- 👑 Promotion
- ♚ Détection d’échec et mat
- 🤖 Joueur contre IA (Minimax)
- 💾 Sauvegarde au format PGN
- 🖼️ Capture PNG de l’échiquier

## 🎮 Modes disponibles

- **Interface Graphique (Swing)** : intuitive, clics sur les pièces
- **Console (Terminal)** : saisie manuelle, utile pour tests/débogage

## ▶️ Lancement

```bash
# Compilation
javac src/**/*.java

# Exécution
java Main
```

Une interface s’affiche pour choisir :
- Interface graphique ou console
- Joueur vs Joueur ou Joueur vs IA
- Noms des joueurs et durée du chrono

## ⚙️ Démonstration

Pour tester rapidement des fonctionnalités précises (ex: roque, prise en passant), exécute :

```bash
java Demo
```

## 💡 Améliorations envisagées

- Personnalisation de la profondeur de l’IA
- IA plus intelligente (évaluation plus poussée)
- Calcul des fins de partie par répétition(une hashmap, avec pour clef la position de jeu...)
- Éditeur PNG : permettre l’édition d’une partie PNG (commentaires, etc.).
- Vue 3D.
- Connecteur réseau à un serveur d’échecs.
- Interface graphique plus ergonomique

## 👥 Auteurs

- **Chafae QALLOUJ**
- **Abir HANNED**
- [Polytech Lyon - 3A Informatique](https://www.polytech-lyon.fr)

---

> Ce projet a été l’occasion de mettre en œuvre l’architecture MVC, la programmation orientée objet, les design patterns, ainsi qu’une interface graphique riche avec Swing.
>
---
Je vous remercie d'avoir pris le temps de visiter notre projet 💙. N'hésitez pas à explorer les différentes fonctionnalités et à consulter le rapport pour plus de détails. Votre retour est toujours apprécié !
