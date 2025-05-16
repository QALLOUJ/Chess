package modele.Pieces;

import modele.Case;
import modele.Piece;
import modele.decorateur.DecTour;

import java.util.ArrayList;

public class Tour extends Piece {

    public Tour(String couleur) {
        super(couleur);
    }

    @Override
    public void setCase(Case c) {
        super.setCase(c);
        if (c != null) {
            this.dec = new DecTour(this, c.getPlateau());
        } else {
            this.dec = null;
        }
    }
    @Override
    public String getNom() {
        return "Tour";
    }

    @Override
    public boolean peutDeplacer(Case source, Case arrive) {
        if (dec == null) return false;
        ArrayList<Case> lst = dec.getMesCA();
        return lst.contains(arrive);
    }
    private boolean aBouge = false;

    public boolean getABouge() {
        return aBouge;
    }

    public void setABouge(boolean b) {
        aBouge = b;
    }
    @Override
    public Piece clone() {
        return new Tour(this.couleur);
    }
}

