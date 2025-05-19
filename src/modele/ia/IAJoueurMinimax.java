package modele.ia;

import modele.Coup;
import modele.Jeu;
import modele.Plateau;

import java.util.List;

public class IAJoueurMinimax {
    private static final int PROFONDEUR_MAX = 3; // profondeur maximale de recherche

    // méthode principale pour obtenir le meilleur coup à jouer pour l’IA
    public Coup meilleurCoup(Jeu jeu, String couleurIA) {
        int meilleureValeur = Integer.MIN_VALUE;
        Coup meilleurCoup = null;

        // récupérer tous les coups possibles pour l’IA
        List<Coup> coups = jeu.getPlateau().getTousLesCoupsLegauxPour(couleurIA);
        for (Coup coup : coups) {
            // simuler une copie du jeu et appliquer le coup
            Jeu copie = new Jeu(jeu);
            copie.getPlateau().appliquerCoup(coup);

            // évaluer le coup en appelant l'algorithme minimax
            int valeur = minimax(copie, PROFONDEUR_MAX - 1, false, couleurIA);

            // si cette valeur est meilleure, on garde ce coup
            if (valeur > meilleureValeur) {
                meilleureValeur = valeur;
                meilleurCoup = coup;
            }
        }

        return meilleurCoup;
    }

    // algorithme minimax récursif
    private int minimax(Jeu jeu, int profondeur, boolean estMax, String couleurIA) {
        // déterminer la couleur du joueur actuel (IA ou adversaire)
        String couleurActuelle = estMax ? couleurIA : couleurAdverse(couleurIA);

        // cas d’arrêt : profondeur atteinte, échec et mat ou pat
        if (profondeur == 0 || jeu.getPlateau().estEchecEtMat(couleurActuelle) || jeu.getPlateau().estPat(couleurActuelle)) {
            return evaluerPlateau(jeu.getPlateau(), couleurIA);
        }

        // générer les coups légaux pour le joueur actuel
        List<Coup> coups = jeu.getPlateau().getTousLesCoupsLegauxPour(couleurActuelle);

        // si c’est l’IA qui joue, on cherche le maximum
        if (estMax) {
            int maxEval = Integer.MIN_VALUE;
            for (Coup coup : coups) {
                Jeu copie = new Jeu(jeu);
                copie.getPlateau().appliquerCoup(coup);
                int eval = minimax(copie, profondeur - 1, false, couleurIA);
                maxEval = Math.max(maxEval, eval);
            }
            return maxEval;
        }
        // sinon, l’adversaire joue, on cherche à minimiser l’évaluation
        else {
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

    // évalue l’état du plateau en fonction des pièces en jeu
    private int evaluerPlateau(Plateau plateau, String couleurIA) {
        int score = 0;
        for (int x = 0; x < Plateau.SIZE_X; x++) {
            for (int y = 0; y < Plateau.SIZE_Y; y++) {
                if (plateau.getCase(x, y).getPiece() != null) {
                    int valeur = plateau.getCase(x, y).getPiece().getValeur();
                    if (plateau.getCase(x, y).getPiece().getCouleur().equals(couleurIA)) {
                        score += valeur; // ajouter la valeur des pièces IA
                    } else {
                        score -= valeur; // soustraire la valeur des pièces adverses
                    }
                }
            }
        }
        return score;
    }

    // retourne la couleur opposée
    private String couleurAdverse(String couleur) {
        return couleur.equals("blanc") ? "noir" : "blanc";
    }
}
