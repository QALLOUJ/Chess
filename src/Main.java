import Vue.VueEchiquier;
import controleur.ControleurEchiquier;
import modele.Jeu;
import modele.Plateau;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {
        // Créer le panneau de saisie avec les champs pour les noms des joueurs et le chrono
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JTextField nomBlancField = new JTextField(20);
        JTextField nomNoirField = new JTextField(20);
        JTextField chronoField = new JTextField(5);

        panel.add(new JLabel("Nom du joueur Blanc :"));
        panel.add(nomBlancField);
        panel.add(new JLabel("Nom du joueur Noir :"));
        panel.add(nomNoirField);
        panel.add(new JLabel("Durée du chrono par joueur (en minutes) :"));
        panel.add(chronoField);

        // Afficher le panneau dans un seul JOptionPane
        int option = JOptionPane.showConfirmDialog(null, panel, "Paramètres de la partie", JOptionPane.OK_CANCEL_OPTION);

        // Si l'utilisateur a cliqué sur OK
        if (option == JOptionPane.OK_OPTION) {
            String nomBlanc = nomBlancField.getText().trim();
            String nomNoir = nomNoirField.getText().trim();
            String chronoInput = chronoField.getText().trim();

            // Validation des noms
            if (nomBlanc.isEmpty()) {
                nomBlanc = "Blanc";
            }
            if (nomNoir.isEmpty()) {
                nomNoir = "Noir";
            }

            // Validation de la durée du chrono
            int dureeMinutes = 5; // valeur par défaut
            try {
                dureeMinutes = Integer.parseInt(chronoInput);
                if (dureeMinutes <= 0) {
                    JOptionPane.showMessageDialog(null, "Valeur invalide. 5 minutes seront utilisées par défaut.");
                    dureeMinutes = 5;
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Entrée invalide. 5 minutes seront utilisées par défaut.");
            }

            int dureeEnSecondes = dureeMinutes * 60;

            // Création du plateau et du jeu
            Plateau plateau = new Plateau(8, 8);
            Jeu jeu = new Jeu(plateau, nomBlanc, nomNoir, dureeEnSecondes);
            plateau.setJeu(jeu); // connecter le plateau au jeu


            // Création de la vue et du contrôleur
            VueEchiquier vue = new VueEchiquier(plateau);
            ControleurEchiquier controleur = new ControleurEchiquier(jeu, vue);

            vue.setVisible(true);
            jeu.demarrerChronometre(); // démarre le chrono après affichage
        }
    }
}
