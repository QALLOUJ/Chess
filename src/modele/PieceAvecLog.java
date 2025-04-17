package modele;

public class PieceAvecLog extends DecorateurPiece {
    public PieceAvecLog(Piece piece) {
        super(piece);
    }

    @Override
    public boolean peutDeplacer(Case caseDepart, Case caseArrivee) {
        System.out.println("📢 Déplacement tenté : " + piece.getClass().getSimpleName() +
                " (" + piece.getCouleur() + ") de [" + caseDepart.getX() + "," + caseDepart.getY() +
                "] vers [" + caseArrivee.getX() + "," + caseArrivee.getY() + "]");

        return super.peutDeplacer(caseDepart, caseArrivee);
    }
}
