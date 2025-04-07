package modele;

public class Cavalier extends Piece {

    public Cavalier(String couleur) {
        super(couleur);
    }

    @Override
    public boolean peutDeplacer(Case caseDepart, Case caseArrivee) {
        int dx = Math.abs(caseDepart.getX() - caseArrivee.getX());
        int dy = Math.abs(caseDepart.getY() - caseArrivee.getY());
        return (dx == 2 && dy == 1) || (dx == 1 && dy == 2);
    }
}
