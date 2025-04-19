package modele;

public class Reine extends Piece {

    public Reine(String couleur) {
        super(couleur);
    }

    @Override
    public boolean peutDeplacer(Case from, Case to) {
        int dx = Math.abs(from.getX() - to.getX());
        int dy = Math.abs(from.getY() - to.getY());

        // La Reine peut se déplacer horizontalement, verticalement ou en diagonale
        if (dx == 0 || dy == 0 || dx == dy) {
            // Vérifie si la case de destination est vide ou contient une pièce ennemie
            if (to.getPiece() == null || !to.getPiece().getCouleur().equals(this.getCouleur())) {
                return true;
            }
        }
        return false;
    }
}
