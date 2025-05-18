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
    }

    @Override
    public void onCaseClicked(Case c) {
        if (!jeu.isPartieEnCours()) return;
        if (jeu.isTourIA()) return;

        // 1er clic : sélection
        if (selection == null) {
            Piece piece = jeu.getPlateau().getPiece(c);

            if (piece != null && piece.getCouleur().equals(jeu.getJoueurCourant().getCouleur())) {
                selection = c;

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

            } else {

                selection = null;
                vue.setDeplacementsPossibles(List.of());
            }
        }
        // 2e clic : tentative de déplacement
        else {
            boolean succes = jeu.demandeDeplacementPiece(selection, c);

            if (succes) {

                vue.mettreAJourAffichage();
                vue.mettreAJourTour(jeu.getJoueurCourant().getNom());

                if (vsIA && jeu.getJoueurCourant().getCouleur().equals("noir")) {
                    new javax.swing.Timer(800, e -> {
                        jouerTourIA();
                        ((javax.swing.Timer) e.getSource()).stop();
                    }).start();
                }
            } else {

            }

            selection = null;
            vue.setDeplacementsPossibles(List.of());
        }

        vue.mettreAJourAffichage();
    }

    private void jouerTourIA() {
        if (!jeu.isPartieEnCours()) return;

        new Thread(() -> {
            String couleurIA = jeu.getJoueurCourant().getCouleur();

            if (jeu.getPlateau().estEnEchec(couleurIA)) {
                try {
                    javax.swing.SwingUtilities.invokeAndWait(() -> {
                        vue.afficherAlerte("Échec détecté pour l'IA, attention !");
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Coup coup = ia.meilleurCoup(jeu, couleurIA);

            if (coup != null) {

                boolean succes = jeu.demandeDeplacementPiece(coup.getCaseDepart(), coup.getCaseArrivee());

                if (succes) {


                    javax.swing.SwingUtilities.invokeLater(() -> {
                        vue.mettreAJourAffichage();
                        vue.mettreAJourTour(jeu.getJoueurCourant().getNom());
                    });
                } else {

                }
            } else {

            }
        }).start();
    }

}
