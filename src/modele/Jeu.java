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
            System.out.println("Aucune pièce à déplacer !");
            return false;
        }

        if (!piece.getCouleur().equals(joueurCourant.getCouleur())) {
            System.out.println("Ce n'est pas votre tour !");
            return false;
        }

        if (!piece.peutDeplacer(source, arrive)) {
            System.out.println("Déplacement invalide pour cette pièce.");
            return false;
        }


        arrive.setPiece(piece);
        source.setPiece(null);


        changerTour();


        plateau.notifierChangement();

        System.out.println("Déplacement effectué de " + source + " vers " + arrive);
        return true;
    }
}
