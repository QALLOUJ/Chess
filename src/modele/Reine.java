package modele;

public class Reine extends Piece {

    public Reine(String couleur) {
        super(couleur);
    }

    @Override
    public boolean peutDeplacer(Case caseDepart, Case caseArrivee) {
        int dx = Math.abs(caseDepart.getX() - caseArrivee.getX());
        int dy = Math.abs(caseDepart.getY() - caseArrivee.getY());
        return dx == dy || caseDepart.getX() == caseArrivee.getX() || caseDepart.getY() == caseArrivee.getY();
    }
}
