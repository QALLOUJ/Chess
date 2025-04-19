package modele;

import java.util.ArrayList;

public abstract class DesCasesAccessibles {
    protected Piece piece;
    protected Plateau plateau;

    public DesCasesAccessibles(Piece piece, Plateau plateau) {
        this.piece = piece;
        this.plateau = plateau;
    }

    public abstract ArrayList<Case> getMesCA();


}
