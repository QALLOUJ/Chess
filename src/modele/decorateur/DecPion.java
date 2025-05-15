package modele.decorateur;

import modele.Case;
import modele.Piece;
import modele.Plateau;

import java.util.ArrayList;

public  class DecPion extends DesCasesAccessibles {

    public DecPion(Piece piece, Plateau plateau) {
        super(piece, plateau);
    }

    @Override
    public ArrayList<Case> getMesCA() {
        ArrayList<Case> lst = new ArrayList<>();

        int dir = piece.getCouleur().equals("blanc") ? -1 : 1;

        Case caseActuelle = piece.getCase();
        if (caseActuelle == null) return lst;

        // 1. avancer d’une case
        Case caseDevant = plateau.getCaseRelative(caseActuelle, 0, dir);
        if (caseDevant != null && caseDevant.estVide()) {
            lst.add(caseDevant);

            // 2. avancer de deux cases si en position initiale
            if ((piece.getCouleur().equals("blanc") && caseActuelle.getY() == 6)
                    || (piece.getCouleur().equals("noir") && caseActuelle.getY() == 1)) {

                Case deuxCasesDevant = plateau.getCaseRelative(caseActuelle, 0, 2 * dir);
                if (deuxCasesDevant != null && deuxCasesDevant.estVide()) {
                    lst.add(deuxCasesDevant);
                }
            }
        }

        // 3. captures diagonales classiques
        for (int dx = -1; dx <= 1; dx += 2) {
            Case capture = plateau.getCaseRelative(caseActuelle, dx, dir);
            if (capture != null && capture.contientEnnemi(piece)) {
                lst.add(capture);
            }
        }

        // 4. prise en passant
        // Récupérer le dernier coup depuis le jeu
        modele.Jeu jeu = piece.getPlateau().getJeu(); // tu dois ajouter un getter `getJeu()` dans Plateau
        if (jeu != null && !jeu.getHistorique().isEmpty()) {
            modele.Coup dernierCoup = jeu.getDernierCoup();

            Piece pieceJouee = dernierCoup.getPieceJouee();

            if (pieceJouee instanceof modele.Pieces.Pion) {
                int fromY = dernierCoup.getCaseDepart().getY();
                int toY = dernierCoup.getCaseArrivee().getY();

                // double-pas vertical d’un pion ennemi juste avant
                if (Math.abs(toY - fromY) == 2 && !pieceJouee.getCouleur().equals(piece.getCouleur())) {
                    int px = dernierCoup.getCaseArrivee().getX();
                    int py = dernierCoup.getCaseArrivee().getY();

                    // si la pièce actuelle est à côté de ce pion
                    if (Math.abs(px - caseActuelle.getX()) == 1 && py == caseActuelle.getY()) {
                        Case caseEnPassant = plateau.getCase(px, py + dir);
                        if (caseEnPassant != null && caseEnPassant.estVide()) {
                            lst.add(caseEnPassant);
                        }
                    }
                }
            }
        }

        return lst;
    }

}

