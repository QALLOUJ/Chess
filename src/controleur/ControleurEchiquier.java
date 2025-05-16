package controleur;

import modele.*;
import Vue.*;
import modele.ia.IAJoueurMinimax;

import java.util.ArrayList;
import java.util.List;

public class ControleurEchiquier implements VueEchiquier.CaseClickListener {
    private final Jeu jeu;
    private final VueEchiquier vue;
    private Case selection = null;
    private final boolean vsIA;
    private final IAJoueurMinimax ia;


    public ControleurEchiquier(Jeu jeu, VueEchiquier vue) {
        this.jeu = jeu;
        this.vue = vue;
        this.vsIA = vue.isVsIA();
        this.ia = new IAJoueurMinimax();
        jeu.setVueEchiquier(vue);
        vue.addCaseClickListener(this);
        jeu.demarrerChronometre();
        jeu.setIaActivee(vsIA);
// Démarre les chronomètres lorsque le contrôleur est initialisé
    }

    @Override
    public void onCaseClicked(Case c) {
        if (!jeu.isPartieEnCours()) return;
        if (jeu.isTourIA()) {
            return;
        }




        // 1er clic : sélection
        if (selection == null) {
            Piece piece = jeu.getPlateau().getPiece(c);

            if (piece != null && piece.getCouleur().equals(jeu.getJoueurCourant().getCouleur())) {
                selection = c;

                // Récupération des cases accessibles (simulation pour légalité)
                List<Case> accessibles = piece.dec.getMesCA();
                List<Case> deplacementsLegaux = new ArrayList<>();

                for (Case cible : accessibles) {
                    if (cible == null) continue;

                    Piece sauvegarde = cible.getPiece();
                    Case ancienneCase = piece.getCase();

                    // Simulation du déplacement
                    cible.setPiece(piece);
                    c.setPiece(null);
                    piece.setCase(cible);

                    boolean encoreEnEchec = jeu.getPlateau().estEnEchec(piece.getCouleur());

                    // Restauration
                    c.setPiece(piece);
                    cible.setPiece(sauvegarde);
                    piece.setCase(ancienneCase);

                    if (!encoreEnEchec) {
                        deplacementsLegaux.add(cible);
                    }
                }

                vue.setDeplacementsPossibles(deplacementsLegaux);
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
                System.out.println("Déplacement réussi : " + jeu.getPlateau().getPiece(c) + " de " + selection + " à " + c);
                vue.mettreAJourAffichage();
                VueEchiquier.changerTour(jeu.getJoueurCourant().getNom());

                // 🎯 Si mode vs IA et c’est le tour de l’IA
                if (vsIA && jeu.getJoueurCourant().getCouleur().equals("noir")) {
                    // Lancer le tour de l'IA avec un petit délai pour l'effet
                    new javax.swing.Timer(800, e -> {
                        jouerTourIA();
                        ((javax.swing.Timer) e.getSource()).stop();
                    }).start();
                }
            } else {
                System.out.println("Déplacement invalide de " + jeu.getPlateau().getPiece(selection) + " vers " + c);
            }

            selection = null;
            vue.setDeplacementsPossibles(List.of());
        }

        vue.mettreAJourAffichage();
    }

    private void jouerTourIA() {
        if (!jeu.isPartieEnCours()) return;

        String couleurIA = jeu.getJoueurCourant().getCouleur();
        Coup coup = ia.meilleurCoup(jeu, couleurIA);

        if (coup != null) {
            System.out.println("IA veut jouer : " + coup.getCaseDepart() + " -> " + coup.getCaseArrivee());
            boolean succes = jeu.demandeDeplacementPiece(coup.getCaseDepart(), coup.getCaseArrivee());
            if (succes) {
                System.out.println("IA a joué son coup avec succès.");
                vue.mettreAJourAffichage();
                VueEchiquier.changerTour(jeu.getJoueurCourant().getNom());
            } else {
                System.out.println("IA : déplacement invalide.");
            }
        } else {
            System.out.println("IA ne trouve pas de coup valide.");
        }
    }


}
