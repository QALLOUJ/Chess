package Vue;

import controleur.ControleurEchiquier;
import modele.Jeu;
import modele.Plateau;

public class Main {
    public static void main(String[] args) {
        // Création du plateau de jeu
        Plateau plateau = new Plateau(8, 8);

        // Création de la vue (le menu de démarrage est géré dans le constructeur)
        VueEchiquier vue = new VueEchiquier(plateau);

        // Récupération des paramètres de la partie depuis la vue
        String nomJoueurBlanc = vue.getNomJoueurBlanc();
        String nomJoueurNoir = vue.getNomJoueurNoir();
        int tempsParJoueur = vue.getTempsParJoueur();

        // Création de l'objet Jeu avec les paramètres
        Jeu jeu = new Jeu(plateau, nomJoueurBlanc, nomJoueurNoir, tempsParJoueur);
        plateau.setJeu(jeu); // Lier le jeu au plateau

        // Création du contrôleur
        ControleurEchiquier controleur = new ControleurEchiquier(jeu, vue);

        // Affichage de la vue
        vue.setVisible(true);

        // Lancer le chronomètre
        jeu.demarrerChronometre();
    }
    public static void lancerNouvellePartie() {
        // Création du plateau de jeu
        Plateau plateau = new Plateau(8, 8);

        // Création de la vue (le menu de démarrage est géré dans le constructeur)
        VueEchiquier vue = new VueEchiquier(plateau);

        // Récupération des paramètres de la partie depuis la vue
        String nomJoueurBlanc = vue.getNomJoueurBlanc();
        String nomJoueurNoir = vue.getNomJoueurNoir();
        int tempsParJoueur = vue.getTempsParJoueur();

        // Création de l'objet Jeu avec les paramètres
        Jeu jeu = new Jeu(plateau, nomJoueurBlanc, nomJoueurNoir, tempsParJoueur);
        plateau.setJeu(jeu); // Lier le jeu au plateau

        // Création du contrôleur
        ControleurEchiquier controleur = new ControleurEchiquier(jeu, vue);

        // Associer le contrôleur à la vue si nécessaire
        vue.setControleur(controleur);

        // Affichage de la vue
        vue.setVisible(true);

        // Lancer le chronomètre
        jeu.demarrerChronometre();
    }

}
