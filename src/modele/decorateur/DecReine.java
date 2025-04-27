package modele.decorateur;

import modele.Case;
import modele.Piece;
import modele.Plateau;

import java.util.ArrayList;

public class DecReine extends DesCasesAccessibles {

    public DecReine(Piece piece, Plateau plateau) {
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

            int x = caseActuelle.getX();
            int y = caseActuelle.getY();

            while (true) {
                x += dx;
                y += dy;

                // vérifie les limites du plateau
                if (x < 0 || x >= plateau.SIZE_X || y < 0 || y >= plateau.SIZE_Y) break;

                Case caseCourante = plateau.getCase(x, y);

                if (caseCourante.estVide()) {
                    lst.add(caseCourante);
                } else if (caseCourante.contientEnnemi(piece)) {
                    lst.add(caseCourante);
                    break;
                } else {
                    break;
                }
            }
        }

        return lst;
    }
}
