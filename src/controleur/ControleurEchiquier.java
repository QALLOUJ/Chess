package controleur;

import modele.*;
import Vue.*;

import java.util.ArrayList;
import java.util.List;

public class ControleurEchiquier implements CaseClickListener {
    private final Jeu jeu;
    private final VueEchiquier vue;
    private Case selection = null;

    public ControleurEchiquier(Jeu jeu, VueEchiquier vue) {
        this.jeu = jeu;
        this.vue = vue;
        jeu.setVueEchiquier(vue);
        vue.addCaseClickListener(this);
        jeu.demarrerChronometre(); // Démarre les chronomètres lorsque le contrôleur est initialisé
    }

    @Override
    public void onCaseClicked(Case c) {
        // 1er clic : sélection de la pièce
        if (!jeu.isPartieEnCours()) {
            return; // ne rien faire si la partie est terminée
        }

        if (selection == null) {
            Piece piece = jeu.getPlateau().getPiece(c);

            // Vérifier si la pièce existe et si c'est la couleur du joueur courant
            if (piece != null && piece.getCouleur().equals(jeu.getJoueurCourant().getCouleur())) {
                selection = c;

                // récupérer tous les coups possibles bruts
                List<Case> accessibles = piece.dec.getMesCA();
                List<Case> deplacementsLegaux = new ArrayList<>();

                for (Case cible : accessibles) {
                    if (cible == null) continue;

                    Case source = c;
                    Piece sauvegarde = cible.getPiece();
                    Case ancienneCase = piece.getCase();

                    // simulation
                    cible.setPiece(piece);
                    source.setPiece(null);
                    piece.setCase(cible);

                    boolean encoreEnEchec = jeu.getPlateau().estEnEchec(piece.getCouleur());

                    // restauration
                    source.setPiece(piece);
                    cible.setPiece(sauvegarde);
                    piece.setCase(ancienneCase);

                    if (!encoreEnEchec) {
                        deplacementsLegaux.add(cible);
                    }
                }

                vue.setDeplacementsPossibles(deplacementsLegaux); // Montrer uniquement les déplacements légaux
                System.out.println("Pièce sélectionnée : " + piece + " à la position (" + c.getX() + "," + c.getY() + ")");
            } else {
                System.out.println("Aucune pièce sélectionnée ou ce n’est pas votre tour.");
                selection = null;
                vue.setDeplacementsPossibles(List.of());
            }
        }
        // 2e clic : tentative de déplacement
        else {
            boolean succes = jeu.demandeDeplacementPiece(selection, c);

            if (succes) {
                System.out.println("Déplacement réussi : " + jeu.getPlateau().getPiece(selection) + " de " + selection + " à " + c);
            } else {
                System.out.println("Déplacement invalide de " + jeu.getPlateau().getPiece(selection) + " vers " + c);
            }

            selection = null;
            vue.setDeplacementsPossibles(List.of());
        }

        vue.mettreAJourAffichage();
    }
}
