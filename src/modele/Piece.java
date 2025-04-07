package modele;

public abstract class Piece {
    private final String couleur;  // La couleur de la pièce (blanche ou noire)

    // Constructeur pour initialiser la couleur de la pièce
    public Piece(String couleur) {
        this.couleur = couleur;
    }

    // Getter pour la couleur
    public String getCouleur() {
        return couleur;
    }

    // Méthode abstraite pour le déplacement de la pièce
    // Cette méthode devra être implémentée dans chaque sous-classe pour définir les règles de déplacement de chaque type de pièce
    public abstract boolean peutDeplacer(Case caseDepart, Case caseArrivee);

    // Méthode pour obtenir une représentation sous forme de chaîne de caractères de la pièce
    @Override
    public String toString() {
        return getClass().getSimpleName() + " " + couleur;
    }
}
