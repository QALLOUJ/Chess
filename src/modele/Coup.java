package modele;

public class Coup {
    private final Piece pieceJouee;
    private final Case caseDepart;
    private final Case caseArrivee;
    private final Piece pieceCapturee;

    public Coup(Piece pieceJouee, Case caseDepart, Case caseArrivee, Piece pieceCapturee) {
        this.pieceJouee = pieceJouee;
        this.caseDepart = caseDepart;
        this.caseArrivee = caseArrivee;
        this.pieceCapturee = pieceCapturee;
    }
    public Piece getPieceJouee() {
        return pieceJouee;
    }

    public Case getCaseDepart() {
        return caseDepart;
    }

    public Case getCaseArrivee() {
        return caseArrivee;
    }




    public Piece getPieceCapturee() {
        return pieceCapturee;
    }
}
