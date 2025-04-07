package modele;

public class Jeu {
    private Plateau plateau;

    public Jeu(Plateau plateau) {
        this.plateau = plateau;
    }

    public Plateau getPlateau() {
        return plateau;
    }

    // Méthode pour effectuer un déplacement
    public void demandeDeplacementPiece(Case caseClic1, Case caseClic2) {
        Piece pieceDepart = caseClic1.getPiece();
        if (pieceDepart != null && pieceDepart.peutDeplacer(caseClic1, caseClic2)) {
            // Déplacer la pièce
            caseClic2.setPiece(pieceDepart);
            caseClic1.setPiece(null); // La case de départ est vide après le déplacement

            // Notifier les observateurs (ici la vue) pour mettre à jour l'affichage
            plateau.setChanged();
            plateau.notifyObservers();
        }
    }
}
