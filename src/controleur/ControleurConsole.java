package controleur;

import modele.*;
import Vue.*;

import java.util.List;

public class ControleurConsole {
    private final Jeu jeu;
    private final VueConsole vue;

    public ControleurConsole(Jeu jeu, VueConsole vue) {
        this.jeu = jeu;
        this.vue = vue;
        jeu.demarrerChronometre();
    }

    public void demarrerPartie() {
        boolean partieFinie = false;

        while (!partieFinie && jeu.isPartieEnCours()) {
            vue.afficherPlateau(jeu.getPlateau().toString());


            String joueurActuel = jeu.getJoueurCourant().getNom();
            vue.afficherMessage("Tour du joueur " + joueurActuel);

            String coup = vue.demanderCoup();
            String[] parts = coup.trim().split("\\s+");
            if (parts.length != 2) {
                vue.afficherMessage("Format invalide. Veuillez saisir un coup au format : 'e2 e4'");
                continue;
            }

            Case depart = jeu.getPlateau().getCaseDepuisNotation(parts[0]);
            Case arrivee = jeu.getPlateau().getCaseDepuisNotation(parts[1]);

            if (depart == null || arrivee == null) {
                vue.afficherMessage("Coordonnées invalides.");
                continue;
            }

            Piece piece = depart.getPiece();
            if (piece == null) {
                vue.afficherMessage("Aucune pièce à la case de départ.");
                continue;
            }
            if (!piece.getCouleur().equals(jeu.getJoueurCourant().getCouleur())) {
                vue.afficherMessage("La pièce sélectionnée n'appartient pas au joueur " + joueurActuel);
                continue;
            }

            if (!estDeplacementValide(piece, depart, arrivee)) {
                vue.afficherMessage("Coup invalide, déplacement interdit ou met le roi en échec.");
                continue;
            }

            boolean succes = jeu.demandeDeplacementPiece(depart, arrivee);

            if (!succes) {
                vue.afficherMessage("Le coup n'a pas pu être joué, essayez à nouveau.");
                continue;
            }

            vue.afficherPlateau(jeu.getPlateau().toString());


            // Vérifier fin de partie pour le joueur adverse
            String couleurAdverse = jeu.getJoueurCourant().getCouleur().equals("blanc") ? "noir" : "blanc";

            if (jeu.getPlateau().estEchecEtMat(couleurAdverse)) {
                vue.afficherMessage("Échec et mat ! Le joueur " + joueurActuel + " a gagné.");
                partieFinie = true;
            } else if (jeu.getPlateau().estPat(couleurAdverse)) {
                vue.afficherMessage("Partie nulle par pat.");
                partieFinie = true;
            } else {
                jeu.changerTour();
            }
        }

        vue.afficherMessage("Fin de la partie.");
    }


    private boolean estDeplacementValide(Piece piece, Case depart, Case arrivee) {
        List<Case> accessibles = piece.getCasesAccessibles();
        if (!accessibles.contains(arrivee)) return false;

        // Simulation du déplacement
        Piece sauvegarde = arrivee.getPiece();
        arrivee.setPiece(piece);
        depart.setPiece(null);
        piece.setCase(arrivee);

        boolean roiEnEchec = piece.getPlateau().estEnEchec(piece.getCouleur());

        // Restauration
        depart.setPiece(piece);
        arrivee.setPiece(sauvegarde);
        piece.setCase(depart);

        return !roiEnEchec;
    }

}
