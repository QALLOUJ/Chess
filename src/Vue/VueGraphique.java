package Vue;



import Vue.VueEchiquier;
import modele.Plateau;

import javax.swing.*;

public class VueGraphique extends VueEchiquier {

    public VueGraphique(Plateau plateau) {
        super(plateau);
        setVisible(false); // Fenêtre cachée au départ
        afficherMenuDemarrage();
        if (estParametrageValide()) {
            setVisible(true); // Afficher seulement après paramétrage
        } else {
            dispose(); // Fermer si annulation
        }
    }

    @Override
    public String demanderChoixPromotion(String[] options) {
        return (String) JOptionPane.showInputDialog(
                this,
                "Promotion : choisissez une pièce",
                "Promotion",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );
    }

    @Override
    public void afficherPlateau(String plateauTexte) {
        // Vous pouvez l'ignorer si vous avez un affichage graphique,
        // mais voici une implémentation console pour du debug éventuel :
        System.out.println(plateauTexte);
    }

    @Override
    public void miseAJourChrono(String nomJoueur, int tempsRestant) {
        if (nomJoueur.equalsIgnoreCase("blanc")) {
            miseAJourChronoBlanc(tempsRestant);
        } else if (nomJoueur.equalsIgnoreCase("noir")) {
            miseAJourChronoNoir(tempsRestant);
        }
    }
    @Override
    public String demanderCoup() {
        // Si tu gères les coups via clics, cette méthode peut rester inutilisée.
        throw new UnsupportedOperationException("Le coup est géré par l'interface graphique.");
    }

}
