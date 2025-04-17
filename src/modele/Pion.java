package modele;

public class Pion extends Piece {
    public Pion(String couleur) {
        super(couleur);
    }

    @Override
    public boolean peutDeplacer(Case caseDepart, Case caseArrivee) {
        int dx = caseArrivee.getX() - caseDepart.getX();
        int dy = caseArrivee.getY() - caseDepart.getY();

        Piece pieceCible = caseArrivee.getPiece();
        String couleur = getCouleur();

        // Pion blanc (avance vers le haut du plateau)
        if (couleur.equals("blanc")) {
            if (dx == 0 && dy == 1 && pieceCible == null) return true; // avancer d’une case
            if (dx == 0 && dy == 2 && caseDepart.getY() == 1 && pieceCible == null) return true; // premier déplacement
            if (Math.abs(dx) == 1 && dy == 1 && pieceCible != null && !pieceCible.getCouleur().equals(couleur)) return true; // prise diagonale
        }

        // Pion noir (avance vers le bas)
        if (couleur.equals("noir")) {
            if (dx == 0 && dy == -1 && pieceCible == null) return true;
            if (dx == 0 && dy == -2 && caseDepart.getY() == 6 && pieceCible == null) return true;
            if (Math.abs(dx) == 1 && dy == -1 && pieceCible != null && !pieceCible.getCouleur().equals(couleur)) return true;
        }

        return false;
    }
}
