package modele;

public class Cavalier extends Piece {
    public Cavalier(String couleur) {
        super(couleur);
    }

    @Override
    public boolean peutDeplacer(Case caseDepart, Case caseArrivee) {
        int dx = Math.abs(caseArrivee.getX() - caseDepart.getX());
        int dy = Math.abs(caseArrivee.getY() - caseDepart.getY());

        // mouvement en L : 2 cases dans une direction, 1 dans l'autre
        if ((dx == 2 && dy == 1) || (dx == 1 && dy == 2)) {
            Piece cible = caseArrivee.getPiece();
            return (cible == null || !cible.getCouleur().equals(getCouleur()));
        }
        return false;
    }
}
