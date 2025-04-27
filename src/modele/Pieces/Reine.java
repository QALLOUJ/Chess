package modele.Pieces;

import modele.Case;
import modele.Piece;
import modele.decorateur.DecReine;

import java.util.ArrayList;

public class Reine extends Piece {

    public Reine(String couleur) {
        super(couleur);
    }

    @Override
    public void setCase(Case c) {
        super.setCase(c);
        if (c != null) {
            this.dec = new DecReine(this, c.getPlateau());
        } else {
            this.dec = null;
        }
    }
    @Override
    public String getNom() {
        return "Reine";
    }

    @Override
    public boolean peutDeplacer(Case source, Case arrive) {
        if (dec == null) return false;
        ArrayList<Case> lst = dec.getMesCA();
        return lst.contains(arrive);
    }
}
