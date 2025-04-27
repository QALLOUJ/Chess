package controleur;

import modele.*;
import Vue.*;

import java.util.List;

public class ControleurEchiquier implements CaseClickListener {
    private final Jeu jeu;
    private final VueEchiquier vue;
    private Case selection = null;

    public ControleurEchiquier(Jeu jeu, VueEchiquier vue) {
        this.jeu = jeu;
        this.vue = vue;
        vue.addCaseClickListener(this);
        jeu.demarrerChronometre(); // Démarre les chronomètres lorsque le contrôleur est initialisé
    }


    @Override
    public void onCaseClicked(Case c) {
        // 1er clic : sélection de la pièce
        if (selection == null) {
            Piece piece = jeu.getPlateau().getPiece(c);

            // Vérifier si la pièce existe et si c'est la couleur du joueur courant
            if (piece != null && piece.getCouleur().equals(jeu.getJoueurCourant().getCouleur())) {
                selection = c;
                List<Case> acces = piece.dec.getMesCA(); // Liste des cases accessibles
                vue.setDeplacementsPossibles(acces);  // Mettre à jour l'affichage des déplacements possibles
                System.out.println("Pièce sélectionnée : " + piece + " à la position (" + c.getX() + "," + c.getY() + ")");
            } else {
                // Si aucune pièce n'est sélectionnée ou ce n'est pas votre tour
                System.out.println("Aucune pièce sélectionnée ou ce n’est pas votre tour.");
                selection = null;
                vue.setDeplacementsPossibles(List.of());
            }
        }
        // 2e clic : tentative de déplacement
        else {
            // Tenter de déplacer la pièce
            boolean succes = jeu.demandeDeplacementPiece(selection, c);

            if (succes) {
                System.out.println("Déplacement réussi : " + jeu.getPlateau().getPiece(selection) + " de " + selection + " à " + c);
            } else {
                System.out.println("Déplacement invalide de " + jeu.getPlateau().getPiece(selection) + " vers " + c);
            }

            // Réinitialiser tout après le déplacement
            selection = null;
            vue.setDeplacementsPossibles(List.of());
        }

        // Toujours mettre à jour l'affichage après un clic
        vue.mettreAJourAffichage();
    }
}
