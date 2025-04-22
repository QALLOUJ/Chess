package modele;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Jeu {
    private Joueur joueurBlanc;
    private Joueur joueurNoir;
    private Joueur joueurCourant;
    private Plateau plateau;
    private ArrayList<Coup> historique = new ArrayList<>();


    public Jeu(Plateau plateau) {
        this.plateau = plateau;
        this.joueurBlanc = new Joueur("Blanc", true);
        this.joueurNoir = new Joueur("Noir", false);
        this.joueurCourant = joueurBlanc;
    }

    public Plateau getPlateau() {
        return plateau;
    }

    public Joueur getJoueurCourant() {
        return joueurCourant;
    }

    public void changerTour() {
        joueurCourant = (joueurCourant == joueurBlanc) ? joueurNoir : joueurBlanc;
    }


    public boolean demandeDeplacementPiece(Case source, Case arrive) {

        Piece piece = source.getPiece();
        if (piece == null) {
            return false; // Si aucune pièce à déplacer
        }

        if (!piece.getCouleur().equals(joueurCourant.getCouleur())) {
            return false;
        }

        if (!piece.peutDeplacer(source, arrive)) {
            return false;
        }

        Piece pieceCapturee = arrive.getPiece();

        Coup coup = new Coup(piece, source, arrive, pieceCapturee);
        System.out.println(coup);

        arrive.setPiece(piece);
        source.setPiece(null);

        piece.setCase(arrive);

        historique.add(coup);

        String gagnant = joueurCourant.getNom();

        changerTour();


        plateau.notifierChangement();


        String couleurAdverse = joueurCourant.getCouleur(); // joueurCourant est maintenant l'adversaire

        if (plateau.estEnEchec(couleurAdverse)) {

            JPanel panel = new JPanel();
            panel.setBackground(new Color(255, 192, 203)); // Rose clair un peu plus doux
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));


            JLabel label = new JLabel("<html><div style='text-align: center;'>"
                    + "<h1 style='color: white; font-size: 18px;'>♔ <b>Échec, attention !</b> ♚</h1>"
                    + "<p style='color: white; font-size: 14px;'><strong>Le roi de " + couleurAdverse + " est en échec !</strong></p>"
                    + "</div></html>");
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setAlignmentX(JComponent.CENTER_ALIGNMENT);

            panel.add(Box.createVerticalStrut(20));
            panel.add(label);
            panel.add(Box.createVerticalStrut(20));


            JOptionPane.showMessageDialog(null,
                    panel,
                    "Attention : Échec",
                    JOptionPane.INFORMATION_MESSAGE);
        }


        if (plateau.estEchecEtMat(couleurAdverse)) {

            JPanel panel = new JPanel();
            panel.setBackground(new Color(255, 192, 203)); // Rose clair un peu plus doux
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));


            JLabel label = new JLabel("<html><div style='text-align: center;'>"
                    + "<h1 style='color: white; font-size: 18px;'>♔ <b>Échec et Mat</b> ♚</h1>"
                    + "<p style='color: white; font-size: 14px;'><strong>" + gagnant + "</strong> a remporté la partie !</p>"
                    + "</div></html>");
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setAlignmentX(JComponent.CENTER_ALIGNMENT);

            panel.add(Box.createVerticalStrut(20));
            panel.add(label);
            panel.add(Box.createVerticalStrut(20));


            JOptionPane.showMessageDialog(null,
                    panel,
                    "Fin de la partie",
                    JOptionPane.INFORMATION_MESSAGE);

            return false;
        }

        return true;
    }
}