package modele.decorateur;

import modele.Case;
import modele.Piece;
import modele.Plateau;

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

        for (int[] direction : directions) {
            int dx = direction[0];
            int dy = direction[1];


            Case caseCourante = plateau.getCaseRelative(caseActuelle, dx, dy);
            if (caseCourante != null) {
                if (caseCourante.estVide()) {
                    lst.add(caseCourante);
                } else if (caseCourante.contientEnnemi(piece)) {
                    lst.add(caseCourante);
                }
            }
        }

        return lst;
    }
}
