package modele;

public abstract class Piece {
    private final String couleur;
    private Case c;
    public DesCasesAccessibles dec;
    private Plateau plateau;

    // Constructeur pour initialiser la couleur de la pièce
    public Piece(String couleur) {
        this.couleur = couleur;
    }

    // Getter pour la couleur
    public String getCouleur() {
        return couleur;
    }

    public Case getCase() {
        return c;
    }

    public void setCase(Case c) {
        this.c = c;
    }

    public Plateau getPlateau() {
        return plateau;
    }


    @Override
    public String toString() {
        return getClass().getSimpleName() + " " + couleur;
    }

    public abstract boolean peutDeplacer(Case source, Case arrive);

}
