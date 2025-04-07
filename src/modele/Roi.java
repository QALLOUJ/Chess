package modele;

public class Roi extends Piece {
    public Roi(String couleur) {
        super(couleur);
    }

    @Override
    public boolean peutDeplacer(Case caseDepart, Case caseArrivee) {
        return false;
    }

}
