package modele;

import java.util.ArrayList;

public class DecFou extends DesCasesAccessibles {

    public DecFou(Piece piece, Plateau plateau) {
        super(piece, plateau);
    }

    @Override
    public ArrayList<Case> getMesCA() {
        ArrayList<Case> lst = new ArrayList<>();
        Case caseActuelle = piece.getCase();
        if (caseActuelle == null) return lst;


        int[][] directions = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}}; // haut-gauche, haut-droit, bas-gauche, bas-droit

        for (int[] direction : directions) {
            int dx = direction[0];
            int dy = direction[1];


            Case caseCourante = caseActuelle;
            while (true) {
                caseCourante = plateau.getCaseRelative(caseCourante, dx, dy);
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
