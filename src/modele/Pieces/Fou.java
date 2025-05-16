package modele.Pieces;

import modele.Case;
import modele.Piece;
import modele.decorateur.DecFou;

import java.util.ArrayList;

public class Fou extends Piece {
    public Fou(String couleur) {
        super(couleur);
    }

    @Override
    public void setCase(Case c) {
        super.setCase(c);
        if (c != null) {
            this.dec = new DecFou(this, c.getPlateau());
        } else {
            this.dec = null;
        }
    }
    @Override
    public String getNom() {
        return "Fou";
    }
    @Override
    public boolean peutDeplacer(Case source, Case arrive) {
        if (dec == null) return false;
        ArrayList<Case> lst = dec.getMesCA();
        return lst.contains(arrive);
    }
    @Override
    public Piece clone() {
        return new Fou(this.couleur);
    }
}
