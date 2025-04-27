package modele.Pieces;

import modele.Case;
import modele.Piece;
import modele.decorateur.DecCavalier;

import java.util.ArrayList;

public class Cavalier extends Piece {
    public Cavalier(String couleur) {
        super(couleur);
    }

    @Override
    public void setCase(Case c) {
        super.setCase(c);
        if (c != null) {
            this.dec = new DecCavalier(this, c.getPlateau());
        } else {
            this.dec = null;
        }
    }
    @Override
    public String getNom() {
        return "Cavalier";
    }
    @Override
    public boolean peutDeplacer(Case source, Case arrive) {
        if (dec == null) return false;
        ArrayList<Case> lst = dec.getMesCA();
        return lst.contains(arrive);
    }
}
