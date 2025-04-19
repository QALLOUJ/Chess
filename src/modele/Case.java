package modele;

public class Case {
    private int x;
    private int y;
    private Piece piece;
    private Plateau plateau; // Référence au plateau

    public Case(int x, int y, Plateau plateau) {
        this.x = x;
        this.y = y;
        this.plateau = plateau;
        this.piece = null;
    }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Piece getPiece() {
        return piece;
    }


    public void setPiece(Piece piece) {
        this.piece = piece;
        if (piece != null) {
            piece.setCase(this); // Met à jour la case dans la pièce
        }
    }

    public boolean estVide() {
        return piece == null;
    }

    public boolean contientEnnemi(Piece autrePiece) {
        return piece != null && !piece.getCouleur().equals(autrePiece.getCouleur());
    }


    public Plateau getPlateau() {
        return plateau;
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}
