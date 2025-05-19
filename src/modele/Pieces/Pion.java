package modele.Pieces;

import modele.Case;
import modele.Piece;
import modele.decorateur.DecPion;

import java.util.ArrayList;

public class Pion extends Piece {

    public Pion(String couleur) {
        super(couleur); // appel au constructeur de la classe mère
    }

    @Override
    public void setCase(Case c) {
        super.setCase(c);
        // on attache un décorateur spécial pour gérer les déplacements du pion
        if (c != null) {
            this.dec = new DecPion(this, c.getPlateau());
        } else {
            this.dec = null;
        }
    }

    @Override
    public String getNom() {
        return "Pion";
    }

    // vérifie si un déplacement est autorisé en consultant les cases accessibles
    @Override
    public boolean peutDeplacer(Case source, Case arrive) {
        if (dec == null) return false;
        ArrayList<Case> lst = dec.getMesCA(); // liste des cases accessibles
        return lst.contains(arrive);
    }

    // permet de cloner la pièce (utile pour les simulations de jeu comme dans minimax)
    @Override
    public Piece clone() {
        return new Pion(this.couleur);
    }
}
