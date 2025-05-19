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

    // constructeur principal du contrôleur
    public ControleurEchiquier(Jeu jeu, VueEchiquier vue) {
        this.jeu = jeu;
        this.vue = vue;
        this.vsIA = vue.isVsIA(); // savoir si la partie se joue contre l'IA
        this.ia = new IAJoueurMinimax(); // création de l'IA
        jeu.setVueEchiquier(vue); // on relie la vue au modèle
        vue.addCaseClickListener(this); // on ajoute l'écouteur d'événements
        jeu.demarrerChronometre(); // on lance le chronomètre dès le début
        jeu.setIaActivee(vsIA); // on active l'IA si besoin
    }

    // méthode appelée lorsqu'on clique sur une case du plateau
    @Override
    public void onCaseClicked(Case c) {
        // si la partie est terminée ou c’est le tour de l’IA, on ne fait rien
        if (!jeu.isPartieEnCours()) return;
        if (jeu.isTourIA()) return;

        // première étape : sélection d'une pièce
        if (selection == null) {
            Piece piece = jeu.getPlateau().getPiece(c);

            // on vérifie que la pièce sélectionnée appartient bien au joueur
            if (piece != null && piece.getCouleur().equals(jeu.getJoueurCourant().getCouleur())) {
                selection = c;

                // on récupère les déplacements possibles de cette pièce
                List<Case> accessibles = piece.dec.getMesCA();
                List<Case> deplacementsLegaux = new ArrayList<>();

                for (Case cible : accessibles) {
                    if (cible == null) continue;

                    Piece sauvegarde = cible.getPiece();
                    Case ancienneCase = piece.getCase();

                    // on simule le déplacement pour voir s’il met en échec
                    cible.setPiece(piece);
                    c.setPiece(null);
                    piece.setCase(cible);

                    boolean encoreEnEchec = jeu.getPlateau().estEnEchec(piece.getCouleur());

                    // on restaure l’état précédent
                    c.setPiece(piece);
                    cible.setPiece(sauvegarde);
                    piece.setCase(ancienneCase);

                    if (!encoreEnEchec) {
                        deplacementsLegaux.add(cible); // déplacement autorisé si pas d’échec
                    }
                }

                // on affiche les déplacements légaux dans la vue
                vue.setDeplacementsPossibles(deplacementsLegaux);

            } else {
                // si la sélection est invalide, on réinitialise
                selection = null;
                vue.setDeplacementsPossibles(List.of());
            }
        }
        // deuxième clic : tentative de déplacement vers une case cible
        else {
            boolean succes = jeu.demandeDeplacementPiece(selection, c);

            if (succes) {
                // si le coup est valide, on met à jour la vue
                vue.mettreAJourAffichage();
                vue.changerTour(jeu.getJoueurCourant().getNom());

                // si c'est le tour de l'IA maintenant, elle joue après un petit délai
                if (vsIA && jeu.getJoueurCourant().getCouleur().equals("noir")) {
                    new javax.swing.Timer(800, e -> {
                        jouerTourIA();
                        ((javax.swing.Timer) e.getSource()).stop();
                    }).start();
                }
            } else {
                // coup invalide, on n’affiche rien de spécial mais on pourrait ajouter un message ici
            }

            // on réinitialise la sélection quelle que soit l'issue
            selection = null;
            vue.setDeplacementsPossibles(List.of());
        }

        // mise à jour visuelle à la fin de l'interaction
        vue.mettreAJourAffichage();
    }

    // méthode permettant à l'IA de jouer son tour
    private void jouerTourIA() {
        if (!jeu.isPartieEnCours()) return;

        new Thread(() -> {
            String couleurIA = jeu.getJoueurCourant().getCouleur();

            // on peut prévenir le joueur si l'IA est en échec
            if (jeu.getPlateau().estEnEchec(couleurIA)) {
                try {
                    javax.swing.SwingUtilities.invokeAndWait(() -> {
                        vue.afficherAlerte("Échec détecté pour l'IA, attention !");
                    });
                } catch (Exception e) {
                    e.printStackTrace(); // en cas d'erreur d'exécution graphique
                }
            }

            // l’IA choisit le meilleur coup possible
            Coup coup = ia.meilleurCoup(jeu, couleurIA);

            if (coup != null) {
                boolean succes = jeu.demandeDeplacementPiece(coup.getCaseDepart(), coup.getCaseArrivee());

                if (succes) {
                    // si le coup est réussi, on met à jour l’interface
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        vue.mettreAJourAffichage();
                        vue.mettreAJourTour(jeu.getJoueurCourant().getNom());
                    });
                } else {
                    // le coup a échoué, mais on ne fait rien pour le moment
                }
            } else {
                // aucun coup possible (probablement pat ou échec et mat)
            }
        }).start();
    }

}
