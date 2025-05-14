package modele;

import modele.decorateur.DesCasesAccessibles;

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
        if (c != null) {
            this.plateau = c.getPlateau(); // ← AJOUT OBLIGATOIRE !!!
        }
    }

    public boolean peutAttaquer(Case source, Case arrive) {

        return peutDeplacer(source, arrive);
    }


    public Plateau getPlateau() {
        return plateau;
    }
    public abstract String getNom();


    @Override
    public String toString() {
        return getClass().getSimpleName() + " " + couleur;
    }

    public abstract boolean peutDeplacer(Case source, Case arrive);

}
