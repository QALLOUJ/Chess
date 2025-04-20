package modele;

import java.util.ArrayList;

public class DecCavalier extends DesCasesAccessibles {

    public DecCavalier(Piece piece, Plateau plateau) {
        super(piece, plateau);
    }

    @Override
    public ArrayList<Case> getMesCA() {
        ArrayList<Case> lst = new ArrayList<>();

        Case caseActuelle = piece.getCase();
        if (caseActuelle == null) return lst;

        int[][] mouvements = {
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };

        for (int[] m : mouvements) {
            Case caseDestination = plateau.getCaseRelative(caseActuelle, m[0], m[1]);
            if (caseDestination != null && !caseDestination.contientAllie(piece)) {
                lst.add(caseDestination);
            }
        }

        return lst;
    }
}
