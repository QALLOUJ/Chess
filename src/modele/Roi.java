package modele;

import java.util.ArrayList;

public class Roi extends Piece {

    public Roi(String couleur) {
        super(couleur); // Le constructeur appelle le constructeur de la classe parent Piece
    }

    @Override
    public void setCase(Case c) {
        super.setCase(c);
        if (c != null) {
            this.dec = new DecRoi(this, c.getPlateau()); // Définir le déplaceur pour le roi
        } else {
            this.dec = null;
        }
    }

    @Override
    public boolean peutDeplacer(Case source, Case arrive) {
        if (dec == null) return false;
        ArrayList<Case> lst = dec.getMesCA();
        return lst.contains(arrive); // Vérifie si la destination est valide
    }
}
