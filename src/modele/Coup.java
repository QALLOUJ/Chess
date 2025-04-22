package modele;

public class Coup {
    private Piece pieceJouee;
    private Case caseDepart;
    private Case caseArrivee;
    private Piece pieceCapturee;

    public Coup(Piece pieceJouee, Case caseDepart, Case caseArrivee, Piece pieceCapturee) {
        this.pieceJouee = pieceJouee;
        this.caseDepart = caseDepart;
        this.caseArrivee = caseArrivee;
        this.pieceCapturee = pieceCapturee;
    }

    @Override
    public String toString() {
        String info = "Coup joué : " + pieceJouee.getClass().getSimpleName()
                + " de (" + caseDepart.getX() + "," + caseDepart.getY() + ")"
                + " à (" + caseArrivee.getX() + "," + caseArrivee.getY() + ")";
        if (pieceCapturee != null) {
            info += " en capturant un " + pieceCapturee.getClass().getSimpleName();
        }
        return info;
    }

    public Piece getPieceCapturee() {
        return pieceCapturee;
    }
}
