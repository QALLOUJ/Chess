package modele;

public class Joueur {
    private String nom;
    private String couleur; // "blanc" ou "noir"

    public Joueur(String nom, boolean estBlanc) {
        this.nom = nom;
        this.couleur = estBlanc ? "blanc" : "noir";
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCouleur() {
        return couleur;
    }

    public boolean estBlanc() {
        return "blanc".equals(couleur);
    }
    public Joueur(Joueur autre) {
        this.nom = autre.nom;
        this.couleur = autre.couleur;
    }

}
