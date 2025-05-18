package modele.ia;

import modele.Coup;
import modele.Jeu;
import modele.Plateau;

import java.util.List;

public class IAJoueurMinimax {
    private static final int PROFONDEUR_MAX = 3;

    public Coup meilleurCoup(Jeu jeu, String couleurIA) {
        int meilleureValeur = Integer.MIN_VALUE;
        Coup meilleurCoup = null;

        List<Coup> coups = jeu.getPlateau().getTousLesCoupsLegauxPour(couleurIA);
        for (Coup coup : coups) {
            Jeu copie = new Jeu(jeu); // Copie de l'état actuel du jeu
            copie.getPlateau().appliquerCoup(coup);
            int valeur = minimax(copie, PROFONDEUR_MAX - 1, false, couleurIA);
            if (valeur > meilleureValeur) {
                meilleureValeur = valeur;
                meilleurCoup = coup;
            }
        }

        return meilleurCoup;
    }

    private int minimax(Jeu jeu, int profondeur, boolean estMax, String couleurIA) {
        String couleurActuelle = estMax ? couleurIA : couleurAdverse(couleurIA);

        if (profondeur == 0 || jeu.getPlateau().estEchecEtMat(couleurActuelle) || jeu.getPlateau().estPat(couleurActuelle)) {
            return evaluerPlateau(jeu.getPlateau(), couleurIA);
        }

        List<Coup> coups = jeu.getPlateau().getTousLesCoupsLegauxPour(couleurActuelle);

        if (estMax) {
            int maxEval = Integer.MIN_VALUE;
            for (Coup coup : coups) {
                Jeu copie = new Jeu(jeu);
                copie.getPlateau().appliquerCoup(coup);
                int eval = minimax(copie, profondeur - 1, false, couleurIA);
                maxEval = Math.max(maxEval, eval);
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Coup coup : coups) {
                Jeu copie = new Jeu(jeu);
                copie.getPlateau().appliquerCoup(coup);
                int eval = minimax(copie, profondeur - 1, true, couleurIA);
                minEval = Math.min(minEval, eval);
            }
            return minEval;
        }
    }

    private int evaluerPlateau(Plateau plateau, String couleurIA) {
        int score = 0;
        for (int x = 0; x < Plateau.SIZE_X; x++) {
            for (int y = 0; y < Plateau.SIZE_Y; y++) {
                if (plateau.getCase(x, y).getPiece() != null) {
                    int valeur = plateau.getCase(x, y).getPiece().getValeur();
                    if (plateau.getCase(x, y).getPiece().getCouleur().equals(couleurIA)) {
                        score += valeur;
                    } else {
                        score -= valeur;
                    }
                }
            }
        }
        return score;
    }

    private String couleurAdverse(String couleur) {
        return couleur.equals("blanc") ? "noir" : "blanc";
    }
}
