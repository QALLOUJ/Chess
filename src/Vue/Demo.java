package Vue;
import modele.*;
import Vue.*;
import controleur.ControleurEchiquier;
import javax.swing.*;

public class Demo {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Plateau plateau = new Plateau(8, 8);
            Jeu jeu = new Jeu(plateau, "Abir", "Chafae", 0);
            VueEchiquier vue = new VueGraphique (plateau);
            new ControleurEchiquier(jeu, vue);
            vue.setTitle("ChessMaster - Abir vs Chafae");
            vue.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            vue.pack();
            vue.setLocationRelativeTo(null);
            vue.setVisible(true);
            // Choix du scénario de test
            String scenario = "enpassant";// "enpassant", "promotion", "mat", ou "" pour normal
            //String scenario = "roque";
            //String scenario = "promotion";
            //String scenario = "echec";
            //String scenario = "Mat";
            initialiserScenario(plateau, scenario);
            vue.mettreAJourAffichage();
            jeu.verifierEchecEtMat("blanc");
            jeu.verifierEchecEtMat("noir");
            Case source = plateau.getCase(6, 5);
            Case arrivee = plateau.getCase(4, 5);
            jeu.demandeDeplacementPiece(source, arrivee);



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
            case "Mat":
                plateau.initialiserMatTest();
                break;
            case "echec":
                plateau.initialiserEchecTest();
                break;
            default:
                plateau.initialiserPartie();
                break;
        }
    }

}
