import Vue.VueEchiquier;
import Vue.VueConsole;
import controleur.ControleurEchiquier;
import modele.Jeu;
import modele.Plateau;
import javax.swing.*;
public class Main {
    public static void main(String[] args) {
        // Choix du mode de jeu
        String[] options = {"Console", "Graphique"};
        int choix = JOptionPane.showOptionDialog(
                null,
                "Choisissez le mode de jeu",
                "Mode",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choix == 0) {
            // Mode console
            Plateau plateau = new Plateau(8, 8);
            Jeu jeu = new Jeu(plateau, "Blanc", "Noir", 300); // noms et temps par défaut
            plateau.setJeu(jeu);

            VueConsole vueConsole = new VueConsole(jeu);
            vueConsole.lancer();  // démarre la boucle console
        } else if (choix == 1) {
            // Mode graphique
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

            int option = JOptionPane.showConfirmDialog(null, panel, "Paramètres de la partie", JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                String nomBlanc = nomBlancField.getText().trim();
                String nomNoir = nomNoirField.getText().trim();
                String chronoInput = chronoField.getText().trim();

                if (nomBlanc.isEmpty()) nomBlanc = "Blanc";
                if (nomNoir.isEmpty()) nomNoir = "Noir";

                int dureeMinutes = 5;
                try {
                    dureeMinutes = Integer.parseInt(chronoInput);
                    if (dureeMinutes <= 0) {
                        JOptionPane.showMessageDialog(null, "Durée invalide. 5 minutes utilisées.");
                        dureeMinutes = 5;
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Entrée invalide. 5 minutes utilisées.");
                }

                int dureeEnSecondes = dureeMinutes * 60;

                Plateau plateau = new Plateau(8, 8);
                Jeu jeu = new Jeu(plateau, nomBlanc, nomNoir, dureeEnSecondes);
                plateau.setJeu(jeu);

                VueEchiquier vue = new VueEchiquier(plateau, nomBlanc, nomNoir);
                new ControleurEchiquier(jeu, vue);

                vue.setVisible(true);
                jeu.demarrerChronometre();
            }
        }
    }

}
