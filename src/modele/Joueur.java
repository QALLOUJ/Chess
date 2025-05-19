package modele;

/**
 * Représente un joueur d'échecs avec un nom et une couleur (blanc ou noir).
 */
public class Joueur {
    private String nom;
    private String couleur; // Peut être "blanc" ou "noir"

    /**
     * Initialise un joueur avec son nom et sa couleur selon un booléen.
     *
     * @param nom Nom du joueur
     * @param estBlanc Si vrai, le joueur joue les blancs ; sinon les noirs
     */
    public Joueur(String nom, boolean estBlanc) {
        this.nom = nom;
        this.couleur = estBlanc ? "blanc" : "noir";
    }

    /**
     * Renvoie le nom du joueur.
     */
    public String getNom() {
        return nom;
    }

    /**
     * Modifie le nom du joueur.
     */
    public void setNom(String nouveauNom) {
        this.nom = nouveauNom;
    }

    /**
     * Récupère la couleur actuelle du joueur.
     *
     * @return "blanc" ou "noir"
     */
    public String getCouleur() {
        return couleur;
    }

    /**
     * Vérifie si ce joueur joue avec les pièces blanches.
     */
    public boolean estBlanc() {
        return "blanc".equalsIgnoreCase(couleur);
    }

    /**
     * Constructeur de copie : duplique les propriétés d’un autre joueur.
     */
    public Joueur(Joueur autreJoueur) {
        this.nom = autreJoueur.nom;
        this.couleur = autreJoueur.couleur;
    }
}
