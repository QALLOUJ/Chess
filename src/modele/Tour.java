package modele;



public class Tour extends Piece {

    public Tour(String couleur) {
        super(couleur);
    }

    @Override
    public boolean peutDeplacer(Case caseDepart, Case caseArrivee) {
        return caseDepart.getX() == caseArrivee.getX() || caseDepart.getY() == caseArrivee.getY();
    }
}
