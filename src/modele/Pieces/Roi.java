package modele.Pieces;

import modele.Case;
import modele.Piece;
import modele.decorateur.DecRoi;

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
    public String getNom() {
        return "Roi";
    }

    @Override
    public boolean peutDeplacer(Case source, Case arrive) {
        if (dec == null) return false;
        ArrayList<Case> lst = dec.getMesCA();
        return lst.contains(arrive); // Vérifie si la destination est valide
    }
    private boolean aBouge = false;

    public boolean getABouge() {
        return aBouge;
    }
    @Override
    public boolean peutAttaquer(Case source, Case arrive) {
        ArrayList<Case> deplacements = new ArrayList<>();
        Case caseActuelle = getCase();
        int[][] directions = {
                {-1, 0}, {1, 0}, {0, -1}, {0, 1},
                {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
        };
        for (int[] d : directions) {
            Case c = getPlateau().getCaseRelative(caseActuelle, d[0], d[1]);
            if (c != null) deplacements.add(c);
        }
        return deplacements.contains(arrive);
    }


    public void setABouge(boolean b) {
        aBouge = b;
    }

}
