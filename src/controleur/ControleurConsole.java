package controleur;

import modele.*;
import Vue.*;

import java.util.List;

public class ControleurConsole {
    private final Jeu jeu;
    private final VueConsole vue;

    // constructeur du contrôleur, on lie le jeu à la vue console
    public ControleurConsole(Jeu jeu, VueConsole vue) {
        this.jeu = jeu;
        this.vue = vue;
        jeu.demarrerChronometre(); // démarrer le chrono dès le lancement du jeu
    }

    // boucle principale du jeu — gère les tours jusqu'à la fin
    public void demarrerPartie() {
        boolean partieFinie = false;

        while (!partieFinie && jeu.isPartieEnCours()) {
            // affichage du plateau à chaque tour
            vue.afficherPlateau(jeu.getPlateau().toString());

            String joueurActuel = jeu.getJoueurCourant().getNom();
            vue.afficherMessage("Tour du joueur " + joueurActuel);

            // demander le coup à jouer, sous forme de chaîne comme "e2 e4"
            String coup = vue.demanderCoup();
            String[] parts = coup.trim().split("\\s+");
            if (parts.length != 2) {
                vue.afficherMessage("Format invalide. Veuillez saisir un coup au format : 'e2 e4'");
                continue;
            }

            // récupération des cases source et destination à partir de la notation
            Case depart = jeu.getPlateau().getCaseDepuisNotation(parts[0]);
            Case arrivee = jeu.getPlateau().getCaseDepuisNotation(parts[1]);

            // on vérifie que les cases existent bien
            if (depart == null || arrivee == null) {
                vue.afficherMessage("Coordonnées invalides.");
                continue;
            }

            Piece piece = depart.getPiece();
            if (piece == null) {
                vue.afficherMessage("Aucune pièce à la case de départ.");
                continue;
            }

            // s'assurer que le joueur joue bien ses propres pièces
            if (!piece.getCouleur().equals(jeu.getJoueurCourant().getCouleur())) {
                vue.afficherMessage("La pièce sélectionnée n'appartient pas au joueur " + joueurActuel);
                continue;
            }

            // on teste si le déplacement est légal et ne met pas le roi en danger
            if (!estDeplacementValide(piece, depart, arrivee)) {
                vue.afficherMessage("Coup invalide, déplacement interdit ou met le roi en échec.");
                continue;
            }

            // si tout est bon, on tente d'exécuter le coup
            boolean succes = jeu.demandeDeplacementPiece(depart, arrivee);

            if (!succes) {
                vue.afficherMessage("Le coup n'a pas pu être joué, essayez à nouveau.");
                continue;
            }

            // on réaffiche le plateau après le déplacement
            vue.afficherPlateau(jeu.getPlateau().toString());

            // maintenant on vérifie si l'adversaire est en échec et mat ou en pat
            String couleurAdverse = jeu.getJoueurCourant().getCouleur().equals("blanc") ? "noir" : "blanc";

            if (jeu.getPlateau().estEchecEtMat(couleurAdverse)) {
                vue.afficherMessage("Échec et mat ! Le joueur " + joueurActuel + " a gagné.");
                partieFinie = true;
            } else if (jeu.getPlateau().estPat(couleurAdverse)) {
                vue.afficherMessage("Partie nulle par pat.");
                partieFinie = true;
            } else {
                jeu.changerTour(); // sinon, on passe au joueur suivant
            }
        }

        // une fois la boucle terminée, on affiche que la partie est finie
        vue.afficherMessage("Fin de la partie.");
    }

    // méthode pour vérifier la validité d’un coup, en simulant le déplacement
    private boolean estDeplacementValide(Piece piece, Case depart, Case arrivee) {
        List<Case> accessibles = piece.getCasesAccessibles();
        if (!accessibles.contains(arrivee)) return false;

        // on simule le déplacement pour vérifier si le roi serait en danger
        Piece sauvegarde = arrivee.getPiece();
        arrivee.setPiece(piece);
        depart.setPiece(null);
        piece.setCase(arrivee);

        boolean roiEnEchec = piece.getPlateau().estEnEchec(piece.getCouleur());

        // on restaure l’état initial pour ne rien perturber
        depart.setPiece(piece);
        arrivee.setPiece(sauvegarde);
        piece.setCase(depart);

        // on retourne vrai si le roi reste en sécurité
        return !roiEnEchec;
    }

}
