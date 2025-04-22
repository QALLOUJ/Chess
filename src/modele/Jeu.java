package modele;

public class Jeu {
    private Joueur joueurBlanc;
    private Joueur joueurNoir;
    private Joueur joueurCourant;
    private Plateau plateau;

    public Jeu(Plateau plateau) {
        this.plateau = plateau;
        this.joueurBlanc = new Joueur("Blanc", true);
        this.joueurNoir = new Joueur("Noir", false);
        this.joueurCourant = joueurBlanc;
    }

    public Plateau getPlateau() {
        return plateau;
    }

    public Joueur getJoueurCourant() {
        return joueurCourant;
    }

    public void changerTour() {
        joueurCourant = (joueurCourant == joueurBlanc) ? joueurNoir : joueurBlanc;
    }


    public boolean demandeDeplacementPiece(Case source, Case arrive) {
        Piece piece = source.getPiece();
        if (piece == null) {

            return false;
        }

        if (!piece.getCouleur().equals(joueurCourant.getCouleur())) {

            return false;
        }

        if (!piece.peutDeplacer(source, arrive)) {

            return false;
        }


        arrive.setPiece(piece);
        source.setPiece(null);


        changerTour();


        plateau.notifierChangement();


        return true;
    }
}
