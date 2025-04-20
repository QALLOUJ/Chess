package modele;

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

        // Vérifier toutes les directions
        for (int[] direction : directions) {
            int dx = direction[0];
            int dy = direction[1];


            int x = caseActuelle.getX();
            int y = caseActuelle.getY();

            while (true) {
                x += dx;
                y += dy;


                Case caseCourante = plateau.getCase(x, y);
                if (caseCourante == null) break;

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
