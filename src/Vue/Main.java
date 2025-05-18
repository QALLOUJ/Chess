package Vue;

import controleur.ControleurConsole;
import controleur.ControleurEchiquier;
import modele.Jeu;
import modele.Plateau;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        // Afficher une boîte de dialogue Swing pour choisir le mode de jeu
        String[] options = {"Mode Console", "Mode Graphique"};
        int choix = JOptionPane.showOptionDialog(
                null,
                "Bienvenue au jeu d'échecs !\nChoisissez le mode de jeu :",
                "Menu de démarrage",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (choix == 0) {
            lancerJeuConsole();
        } else if (choix == 1) {
            lancerNouvellePartie();
        } else {
            System.out.println("Aucun mode sélectionné. Fermeture.");
        }
    }

    private static void lancerJeuConsole() {
        Plateau plateau = new Plateau(8, 8);

        String nomBlanc = JOptionPane.showInputDialog(null, "Nom du joueur Blanc :", "Blanc");
        if (nomBlanc == null || nomBlanc.trim().isEmpty()) nomBlanc = "Blanc";

        String nomNoir = JOptionPane.showInputDialog(null, "Nom du joueur Noir :", "Noir");
        if (nomNoir == null || nomNoir.trim().isEmpty()) nomNoir = "Noir";

        int dureeChrono = 600;
        try {
            String input = JOptionPane.showInputDialog(null, "Durée par joueur (minutes) :", "10");
            int minutes = Integer.parseInt(input);
            if (minutes > 0) dureeChrono = minutes * 60;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Entrée invalide. Durée par défaut : 10 minutes.");
        }

        Jeu jeu = new Jeu(plateau, nomBlanc, nomNoir, dureeChrono);
        VueConsole vue = new VueConsole(new java.util.Scanner(System.in));
        ControleurConsole controleur = new ControleurConsole(jeu, vue);
        controleur.demarrerPartie();
    }

    public static void lancerNouvellePartie() {
        SwingUtilities.invokeLater(() -> {
            try {
                Plateau plateau = new Plateau(8, 8);
                VueGraphique vue = new VueGraphique(plateau);

                vue.afficherMenuDemarrage();

                if (vue.estParametrageValide()) {
                    String nomJoueurBlanc = vue.getNomJoueurBlanc();
                    String nomJoueurNoir = vue.getNomJoueurNoir();
                    int tempsParJoueur = vue.getTempsParJoueur();

                    Jeu jeu = new Jeu(plateau, nomJoueurBlanc, nomJoueurNoir, tempsParJoueur);
                    plateau.setJeu(jeu);
                    jeu.setInterfaceUtilisateur(vue);

                    ControleurEchiquier controleur = new ControleurEchiquier(jeu, vue);
                    vue.setControleur(controleur);

                    vue.setVisible(true);
                    jeu.demarrerChronometre();
                } else {
                    JOptionPane.showMessageDialog(null, "Paramètres invalides. Fermeture.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Erreur : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
