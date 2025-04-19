package modele;

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


        Case caseDevant = plateau.getCaseRelative(caseActuelle, 0, dir);
        if (caseDevant != null && caseDevant.estVide()) {
            lst.add(caseDevant);


            if ((piece.getCouleur().equals("blanc") && caseActuelle.getY() == 6)
                    || (piece.getCouleur().equals("noir") && caseActuelle.getY() == 1)) {

                Case deuxCasesDevant = plateau.getCaseRelative(caseActuelle, 0, 2 * dir);
                if (deuxCasesDevant != null && deuxCasesDevant.estVide()) {
                    lst.add(deuxCasesDevant);
                }
            }
        }


        Case captureGauche = plateau.getCaseRelative(caseActuelle, -1, dir);
        if (captureGauche != null && captureGauche.contientEnnemi(piece)) {
            lst.add(captureGauche);
        }


        Case captureDroite = plateau.getCaseRelative(caseActuelle, 1, dir);
        if (captureDroite != null && captureDroite.contientEnnemi(piece)) {
            lst.add(captureDroite);
        }



        return lst;
    }
}

