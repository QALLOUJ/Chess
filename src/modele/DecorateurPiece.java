package modele;

public abstract class DecorateurPiece extends Piece {
    protected Piece piece;

    public DecorateurPiece(Piece piece) {
        super(piece.getCouleur()); // On garde la même couleur que la pièce originale
        this.piece = piece;
    }
    public Piece getPieceOriginale() {
        return piece;
    }


    @Override
    public boolean peutDeplacer(Case caseDepart, Case caseArrivee) {
        return piece.peutDeplacer(caseDepart, caseArrivee);
    }

    @Override
    public String toString() {
        return piece.toString(); // Pour garder l’affichage de la pièce d’origine
    }
}
