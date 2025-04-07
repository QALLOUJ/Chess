package modele;

public class Pion extends Piece {

    public Pion(String couleur) {
        super(couleur);
    }

    @Override
    public boolean peutDeplacer(Case caseDepart, Case caseArrivee) {
        int dx = caseArrivee.getX() - caseDepart.getX();
        int dy = caseArrivee.getY() - caseDepart.getY();

        // Si blanc, avance vers le haut (y décroissant), sinon vers le bas (y croissant)
        if (getCouleur().equalsIgnoreCase("blanc")) {
            return dx == 0 && dy == -1;
        } else {
            return dx == 0 && dy == 1;
        }
    }
}
