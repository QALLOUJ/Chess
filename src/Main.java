import modele.Jeu;

import javax.swing.*;

import modele.*;

public class Main {
    public static void main(String[] args) {
        // Création du modèle
        Plateau plateau = new Plateau(8, 8);

        // Ajout de quelques pièces
        plateau.getCase(4, 0).setPiece(new Roi("blanc"));
        plateau.getCase(4, 7).setPiece(new Roi("noir"));
        plateau.getCase(3, 0).setPiece(new Reine("blanc"));
        plateau.getCase(3, 7).setPiece(new Reine("noir"));

        plateau.getCase(0, 1).setPiece(new Pion("blanc"));
        plateau.getCase(1, 1).setPiece(new Pion("blanc"));
        plateau.getCase(2, 1).setPiece(new Pion("blanc"));
        plateau.getCase(3, 1).setPiece(new Pion("blanc"));
        plateau.getCase(4, 1).setPiece(new Pion("blanc"));
        plateau.getCase(5, 1).setPiece(new Pion("blanc"));
        plateau.getCase(6, 1).setPiece(new Pion("blanc"));
        plateau.getCase(7, 1).setPiece(new Pion("blanc"));

        plateau.getCase(0, 6).setPiece(new Pion("noir"));
        plateau.getCase(1, 6).setPiece(new Pion("noir"));
        plateau.getCase(2, 6).setPiece(new Pion("noir"));
        plateau.getCase(3, 6).setPiece(new Pion("noir"));
        plateau.getCase(4, 6).setPiece(new Pion("noir"));
        plateau.getCase(5, 6).setPiece(new Pion("noir"));
        plateau.getCase(6, 6).setPiece(new Pion("noir"));
        plateau.getCase(7, 6).setPiece(new Pion("noir"));

        plateau.getCase(2, 0).setPiece(new Fou("blanc"));
        plateau.getCase(5, 0).setPiece(new Fou("blanc"));
        plateau.getCase(2, 7).setPiece(new Fou("noir"));
        plateau.getCase(5, 7).setPiece(new Fou("noir"));

        plateau.getCase(1, 0).setPiece(new Cavalier("blanc"));
        plateau.getCase(6, 0).setPiece(new Cavalier("blanc"));
        plateau.getCase(1, 7).setPiece(new Cavalier("noir"));
        plateau.getCase(6, 7).setPiece(new Cavalier("noir"));

        plateau.getCase(0, 0).setPiece(new Tour("blanc"));
        plateau.getCase(7, 0).setPiece(new Tour("blanc"));
        plateau.getCase(0, 7).setPiece(new Tour("noir"));
        plateau.getCase(7, 7).setPiece(new Tour("noir"));

        // Création du jeu
        Jeu jeu = new Jeu(plateau);

        // Lancement de l'interface graphique
        javax.swing.SwingUtilities.invokeLater(() -> {
            Control ctrl = new Control(jeu);
            ctrl.setVisible(true);
        });
    }
}
