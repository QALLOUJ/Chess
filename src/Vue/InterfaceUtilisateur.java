package Vue;



public interface InterfaceUtilisateur {
    void afficherMessage(String message);

    String demanderCoup();

    String demanderChoixPromotion(String[] options);
    void miseAJourChrono(String couleur, int temps);
    void afficherPlateau(String texte);
}

