package modele.ia;

import modele.Coup;
import modele.Jeu;
import modele.Plateau;


import java.util.List;

public class IAJoueurMinimax {

    private static final int PROFONDEUR = 3;

    /**
     * Trouve le meilleur coup pour la couleur de l'IA en utilisant Minimax.
     */
    public Coup meilleurCoup(Jeu jeu, String couleurIA) {
        int maxEval = Integer.MIN_VALUE;
        Coup meilleurCoup = null;

        // Récupère les coups possibles via le plateau
        List<Coup> coupsPossibles = jeu.getPlateau().getTousLesCoupsPossiblesPour(couleurIA);
        for (Coup coup : coupsPossibles) {
            Jeu copieJeu = new Jeu(jeu); // constructeur copie de Jeu à créer
            copieJeu.getPlateau().appliquerCoup(coup);

            int eval = minimax(copieJeu, PROFONDEUR - 1, false, couleurIA);
            if (eval > maxEval) {
                maxEval = eval;
                meilleurCoup = coup;
            }
        }
        return meilleurCoup;
    }

    /**
     * Minimax récursif.
     */
    private int minimax(Jeu jeu, int profondeur, boolean estMax, String couleurIA) {
        String couleurCourante = estMax ? couleurIA : (couleurIA.equals("blanc") ? "noir" : "blanc");

        if (profondeur == 0 ||
                jeu.getPlateau().estEchecEtMat(couleurCourante) ||
                jeu.getPlateau().estPat(couleurCourante)) {
            return evaluerPlateau(jeu.getPlateau(), couleurIA);
        }

        List<Coup> coups = jeu.getPlateau().getTousLesCoupsPossiblesPour(couleurCourante);

        if (coups.isEmpty()) {
            return evaluerPlateau(jeu.getPlateau(), couleurIA);
        }

        if (estMax) {
            int maxEval = Integer.MIN_VALUE;
            for (Coup coup : coups) {
                Jeu copieJeu = new Jeu(jeu);
                copieJeu.demandeDeplacementPiece(coup.getCaseDepart(), coup.getCaseArrivee());
                int eval = minimax(copieJeu, profondeur - 1, false, couleurIA);
                maxEval = Math.max(maxEval, eval);
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Coup coup : coups) {
                Jeu copieJeu = new Jeu(jeu);
                copieJeu.demandeDeplacementPiece(coup.getCaseDepart(), coup.getCaseArrivee());
                int eval = minimax(copieJeu, profondeur - 1, true, couleurIA);
                minEval = Math.min(minEval, eval);
            }
            return minEval;
        }
    }

    /**
     * Évalue le plateau.
     */
    private int evaluerPlateau(Plateau plateau, String couleurIA) {
        int score = 0;
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                modele.Piece p = plateau.getCase(x, y).getPiece();
                if (p != null) {
                    int valeur = getValeurPiece(p);
                    score += p.getCouleur().equals(couleurIA) ? valeur : -valeur;
                }
            }
        }
        return score;
    }

    /**
     * Valeur heuristique d'une pièce.
     */
    private int getValeurPiece(modele.Piece p) {
        if (p instanceof modele.Pieces.Pion) return 10;
        if (p instanceof modele.Pieces.Cavalier || p instanceof modele.Pieces.Fou) return 30;
        if (p instanceof modele.Pieces.Tour) return 50;
        if (p instanceof modele.Pieces.Reine) return 90;
        if (p instanceof modele.Pieces.Roi) return 900;
        return 0;
    }
}
