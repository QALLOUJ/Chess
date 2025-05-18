import modele.*;
import Vue.*;
import controleur.ControleurEchiquier;

import javax.swing.*;

public class Demo {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Plateau plateau = new Plateau(8, 8);
            Jeu jeu = new Jeu(plateau, "Abir", "Chafae", 0);

            VueEchiquier vue = new VueEchiquier(plateau);
            new ControleurEchiquier(jeu, vue);

            // Si VueEchiquier est une JFrame ou dérivée
            vue.setTitle("ChessMaster - Abir vs Chafae");
            vue.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            vue.pack();
            vue.setLocationRelativeTo(null);
            vue.setVisible(true);

            // Choix du scénario de test
            String scenario = "enpassant"; // "enpassant", "promotion", "mat", ou "" pour normal
            initialiserScenario(plateau, scenario);
            vue.mettreAJourAffichage();

            JOptionPane.showMessageDialog(vue,
                    "La partie commence !\nAbir (Blanc) vs Chafae (Noir)\nScénario: " + scenario,
                    "Partie en cours",
                    JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private static void initialiserScenario(Plateau plateau, String scenario) {
        switch (scenario.toLowerCase()) {
            case "roque":
                plateau.initialiserRoqueTest();
                break;
            case "enpassant":
                plateau.initialiserMiseEnPassantTest();
                break;
            case "promotion":
                plateau.initialiserPromotionTest();
                break;
            case "echec":
                plateau.initialiserMatTest();
                break;
            default:
                plateau.initialiserPartie();
                break;
        }
    }
}
