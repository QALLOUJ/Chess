package modele.decorateur;

import modele.Case;
import modele.Piece;
import modele.Plateau;
import modele.Pieces.Roi;
import modele.Pieces.Tour;

import java.util.ArrayList;

public class DecRoi extends DesCasesAccessibles {

    public DecRoi(Piece piece, Plateau plateau) {
        super(piece, plateau);
    }

    @Override
    public ArrayList<Case> getMesCA() {
        ArrayList<Case> lst = new ArrayList<>();
        Case caseActuelle = piece.getCase();
        if (caseActuelle == null) return lst;

        int[][] directions = {
                {-1, 0}, {1, 0}, {0, -1}, {0, 1},
                {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
        };

        // déplacements normaux du roi
        for (int[] direction : directions) {
            int dx = direction[0];
            int dy = direction[1];
            Case caseCourante = plateau.getCaseRelative(caseActuelle, dx, dy);
            if (caseCourante != null) {
                if (caseCourante.estVide() || caseCourante.contientEnnemi(piece)) {
                    lst.add(caseCourante);
                }
            }
        }

        // ajout des cases pour le roque
        Roi roi = (Roi) piece;
        if (!roi.getABouge() && !plateau.estEnEchec(roi.getCouleur())) {
            int y = caseActuelle.getY();

            // petit roque (côté roi)
            Case caseTourPetit = plateau.getCase(7, y);
            if (caseTourPetit != null && caseTourPetit.getPiece() instanceof Tour) {
                Tour tourPetit = (Tour) caseTourPetit.getPiece();
                if (!tourPetit.getABouge()) {
                    Case case1 = plateau.getCase(5, y);
                    Case case2 = plateau.getCase(6, y);
                    if (case1.estVide() && case2.estVide()
                            && !estCaseAttaquee(case1, roi.getCouleur())
                            && !estCaseAttaquee(case2, roi.getCouleur())) {
                        lst.add(case2); // case où le roi doit se déplacer pour roque petit
                    }
                }
            }

            // grand roque (côté dame)
            Case caseTourGrand = plateau.getCase(0, y);
            if (caseTourGrand != null && caseTourGrand.getPiece() instanceof Tour) {
                Tour tourGrand = (Tour) caseTourGrand.getPiece();
                if (!tourGrand.getABouge()) {
                    Case case1 = plateau.getCase(1, y);
                    Case case2 = plateau.getCase(2, y);
                    Case case3 = plateau.getCase(3, y);
                    if (case1.estVide() && case2.estVide() && case3.estVide()
                            && !estCaseAttaquee(case2, roi.getCouleur())
                            && !estCaseAttaquee(case3, roi.getCouleur())) {
                        lst.add(case2); // case où le roi doit se déplacer pour roque grand
                    }
                }
            }
        }

        return lst;
    }

    // méthode pour vérifier si une case est attaquée par l'adversaire
    private boolean estCaseAttaquee(Case c, String couleur) {
        for (int x = 0; x < Plateau.SIZE_X; x++) {
            for (int y = 0; y < Plateau.SIZE_Y; y++) {
                Piece p = plateau.getCase(x, y).getPiece();
                if (p != null && !p.getCouleur().equals(couleur)) {
                    if (p.peutAttaquer(plateau.getCase(x, y), c)) {

                        return true;
                    }
                }
            }
        }
        return false;
    }
}
