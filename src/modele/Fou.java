package modele;

public class Fou extends Piece {

    public Fou(String couleur) {
        super(couleur);
    }

    @Override
    public boolean peutDeplacer(Case caseDepart, Case caseArrivee) {
        return Math.abs(caseDepart.getX() - caseArrivee.getX()) ==
                Math.abs(caseDepart.getY() - caseArrivee.getY());
    }
}
