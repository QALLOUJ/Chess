package modele;

import modele.Pieces.*;
import modele.decorateur.DesCasesAccessibles;

import java.util.ArrayList;
import java.util.List;

public abstract class Piece {
    public final String couleur;
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


    public List<Case> getCasesAccessibles() {
        if (dec == null) return new ArrayList<>();
        return new ArrayList<>(dec.getMesCA());
    }



    @Override
    public String toString() {
        return getClass().getSimpleName() + " " + couleur;
    }

    public abstract boolean peutDeplacer(Case source, Case arrive);
    public int getValeur() {
        if (this instanceof Reine) return 9;
        if (this instanceof Tour) return 5;
        if (this instanceof Fou) return 3;
        if (this instanceof Cavalier) return 3;
        if (this instanceof Pion) return 1;
        return 0; // Roi non compté dans le matériel
    }
    public abstract Piece clone();
}
