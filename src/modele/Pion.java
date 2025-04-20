package modele;

import java.util.ArrayList;

public class Pion extends Piece {
    public Pion(String couleur) {
        super(couleur); // doit toujours être en premier
    }


    @Override

    public void setCase(Case c) {
        super.setCase(c);
        if (c != null) {
            this.dec = new DecPion(this, c.getPlateau());
        } else {
            this.dec = null;
        }
    }


    @Override
    public boolean peutDeplacer(Case source, Case arrive) {
        if (dec == null) return false;
        ArrayList<Case> lst = dec.getMesCA();
        return lst.contains(arrive);
    }
}

        /*
        int dx = caseArrivee.getX() - caseDepart.getX();
        int dy = caseArrivee.getY() - caseDepart.getY();

        Piece pieceCible = caseArrivee.getPiece();
        String couleur = getCouleur();

        // Pion blanc (avance vers le haut du plateau)
        if (couleur.equals("blanc")) {
            if (dx == 0 && dy == 1 && pieceCible == null) return true; // avancer d’une case
            if (dx == 0 && dy == 2 && caseDepart.getY() == 1 && pieceCible == null) return true; // premier déplacement
            if (Math.abs(dx) == 1 && dy == 1 && pieceCible != null && !pieceCible.getCouleur().equals(couleur)) return true; // prise diagonale
        }

        // Pion noir (avance vers le bas)
        if (couleur.equals("noir")) {
            if (dx == 0 && dy == -1 && pieceCible == null) return true;
            if (dx == 0 && dy == -2 && caseDepart.getY() == 6 && pieceCible == null) return true;
            if (Math.abs(dx) == 1 && dy == -1 && pieceCible != null && !pieceCible.getCouleur().equals(couleur)) return true;
        }

        return false;
    }
}
/*
         */