package modele;

import Vue.VueEchiquier;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Jeu {
    private Joueur joueurBlanc;
    private Joueur joueurNoir;
    private Joueur joueurCourant;
    private Plateau plateau;
    private ArrayList<Coup> historique = new ArrayList<>();
    private Timer timerBlanc;
    private Timer timerNoir;
    private int tempsRestantBlanc;
    private int tempsRestantNoir;
    private boolean partieEnCours = true;// Pour savoir si la partie est encore en cours
    private VueEchiquier vueEchiquier;

    public Jeu(Plateau plateau, String nomBlanc, String nomNoir, int dureeChronoEnSecondes) {
        this.plateau = plateau;
        this.joueurBlanc = new Joueur(nomBlanc, true);
        this.joueurNoir = new Joueur(nomNoir, false);
        this.joueurCourant = joueurBlanc;

        this.tempsRestantBlanc = dureeChronoEnSecondes;
        this.tempsRestantNoir = dureeChronoEnSecondes;
    }

    public void demarrerChronometre() {
        ActionListener actionListenerBlanc = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tempsRestantBlanc > 0 && partieEnCours) {
                    tempsRestantBlanc--;
                    VueEchiquier.miseAJourChronoBlanc(tempsRestantBlanc); // Met à jour l'affichage du chrono Blanc
                } else {
                    timerBlanc.stop();
                }
            }
        };

        ActionListener actionListenerNoir = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tempsRestantNoir > 0 && partieEnCours) {
                    tempsRestantNoir--;
                    VueEchiquier.miseAJourChronoNoir(tempsRestantNoir); // Met à jour l'affichage du chrono Noir
                } else {
                    timerNoir.stop();
                }
            }
        };

        // Initialisation des timers
        timerBlanc = new Timer(1000, actionListenerBlanc); // 1000 ms = 1 seconde
        timerNoir = new Timer(1000, actionListenerNoir);

        // Lancer le timer pour Blanc en premier
        timerBlanc.start();
    }

    public Plateau getPlateau() {
        return plateau;
    }

    public Joueur getJoueurCourant() {
        return joueurCourant;
    }

    public void changerTour() {
        if (joueurCourant == joueurBlanc) {
            timerBlanc.stop();
            timerNoir.start();
            joueurCourant = joueurNoir; // <-- AJOUT ICI
        } else {
            timerNoir.stop();
            timerBlanc.start();
            joueurCourant = joueurBlanc; // <-- AJOUT ICI
        }
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