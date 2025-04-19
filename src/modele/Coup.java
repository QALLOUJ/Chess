package modele;

public class Coup {
    private Case depart;
    private Case arrivee;
    private Piece piece;
    private Piece pieceCapturee;
    private String typeSpecial; // "promotion", "roque", "prise en passant", etc.

    public Coup(Case depart, Case arrivee, Piece piece, Piece pieceCapturee, String typeSpecial) {
        this.depart = depart;
        this.arrivee = arrivee;
        this.piece = piece;
        this.pieceCapturee = pieceCapturee;
        this.typeSpecial = typeSpecial;
    }

    public Case getDepart() {
        return depart;
    }

    public Case getArrivee() {
        return arrivee;
    }

    public Piece getPiece() {
        return piece;
    }

    public Piece getPieceCapturee() {
        return pieceCapturee;
    }

    public String getTypeSpecial() {
        return typeSpecial;
    }

    @Override
    public String toString() {
        return piece + " de " + "(" + depart.getX() + "," + depart.getY() + ")" +
                " à " + "(" + arrivee.getX() + "," + arrivee.getY() + ")" +
                (pieceCapturee != null ? ", capture " + pieceCapturee : "") +
                (typeSpecial != null ? " [" + typeSpecial + "]" : "");
    }
}
