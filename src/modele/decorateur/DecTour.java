package modele.decorateur;

import modele.Case;
import modele.Piece;
import modele.Plateau;

import java.util.ArrayList;

public class DecTour extends DesCasesAccessibles {

    public DecTour(Piece piece, Plateau plateau) {
        super(piece, plateau);
    }

    @Override
    public ArrayList<Case> getMesCA() {
        ArrayList<Case> lst = new ArrayList<>();

        Case caseActuelle = piece.getCase();
        if (caseActuelle == null) return lst;

        int[][] directions = {
                {1, 0},
                {-1, 0},
                {0, 1},
                {0, -1}
        };

        for (int[] dir : directions) {
            int dx = dir[0];
            int dy = dir[1];
            Case next = plateau.getCaseRelative(caseActuelle, dx, dy);
            while (next != null) {
                if (next.estVide()) {
                    lst.add(next);
                } else {
                    if (next.contientEnnemi(piece)) {
                        lst.add(next);
                    }
                    break;
                }
                next = plateau.getCaseRelative(next, dx, dy);
            }
        }

        return lst;
    }
}
