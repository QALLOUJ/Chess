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
    private boolean partieEnCours = true; // Pour savoir si la partie est encore en cours
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
                    verifierFinPartie();
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
                    verifierFinPartie();
                } else {
                    timerNoir.stop();
                }
            }
        };

        if (timerBlanc == null) {
            timerBlanc = new Timer(1000, actionListenerBlanc); // 1000 ms = 1 seconde
        }
        if (timerNoir == null) {
            timerNoir = new Timer(1000, actionListenerNoir);
        }

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
        // Arrêter le timer du joueur courant et démarrer celui du joueur adverse
        if (joueurCourant == joueurBlanc) {
            timerBlanc.stop();
            timerNoir.start();
            joueurCourant = joueurNoir;
        } else {
            timerNoir.stop();
            timerBlanc.start();
            joueurCourant = joueurBlanc;
        }
    }

    public boolean demandeDeplacementPiece(Case source, Case arrive) {
        Piece piece = source.getPiece();
        if (piece == null || !piece.getCouleur().equals(joueurCourant.getCouleur()) || !piece.peutDeplacer(source, arrive)) {
            return false; // Valider que la pièce peut être déplacée
        }

        Piece pieceCapturee = arrive.getPiece();
        if (pieceCapturee != null && vueEchiquier != null) {
            vueEchiquier.ajouterCapture(pieceCapturee);
        }

        Coup coup = new Coup(piece, source, arrive, pieceCapturee);
        effectuerDeplacement(source, arrive, piece);

        historique.add(coup);
        changerTour();
        plateau.notifierChangement();

        String couleurAdverse = joueurCourant.getCouleur(); // Adversaire après changement de tour
        verifierEchecEtMat(couleurAdverse);

        return true;
    }

    private void effectuerDeplacement(Case source, Case arrive, Piece piece) {
        arrive.setPiece(piece);
        source.setPiece(null);
        piece.setCase(arrive);
    }

    private void verifierEchecEtMat(String couleurAdverse) {
        if (plateau.estEnEchec(couleurAdverse)) {
            afficherMessage("Échec, attention !", "Le roi de " + couleurAdverse + " est en échec !");
        }

        if (plateau.estEchecEtMat(couleurAdverse)) {
            afficherMessage("Échec et Mat", joueurCourant.getNom() + " a remporté la partie !");
            finPartie(joueurCourant.getNom() + " a gagné par Échec et Mat !");
        }
    }

    private void afficherMessage(String titre, String message) {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(255, 192, 203)); // Rose clair un peu plus doux
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel label = new JLabel("<html><div style='text-align: center;'>"
                + "<h1 style='color: white; font-size: 18px;'>" + titre + "</h1>"
                + "<p style='color: white; font-size: 14px;'><strong>" + message + "</strong></p>"
                + "</div></html>");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setAlignmentX(JComponent.CENTER_ALIGNMENT);

        panel.add(Box.createVerticalStrut(20));
        panel.add(label);
        panel.add(Box.createVerticalStrut(20));

        JOptionPane.showMessageDialog(null, panel, titre, JOptionPane.INFORMATION_MESSAGE);
    }

    // Vérifier si le temps est écoulé pour un joueur
    public void verifierFinPartie() {
        if (tempsRestantBlanc <= 0) {
            finPartie("Le joueur noir a gagné, temps écoulé !");
        } else if (tempsRestantNoir <= 0) {
            finPartie("Le joueur blanc a gagné, temps écoulé !");
        }
    }

    // Fin de la partie
    public void finPartie(String message) {
        // Afficher une boîte de dialogue pour annoncer la fin de la partie
        JOptionPane.showMessageDialog(null, message, "Fin de la partie", JOptionPane.INFORMATION_MESSAGE);
        partieEnCours = false; // Mettre à jour l'état de la partie pour arrêter le jeu
        timerBlanc.stop();
        timerNoir.stop();
        sauvegarderEnPGN("partie.pgn", "0-1");  // 1-0 pour blanc, 0-1 pour noir, 1/2-1/2 pour nul

    }

    public void setVueEchiquier(VueEchiquier vueEchiquier) {
        this.vueEchiquier = vueEchiquier;
    }

    public Joueur getJoueurNoir() {
        return joueurNoir;
    }

    public Joueur getJoueurBlanc() {
        return joueurBlanc;
    }
    public void sauvegarderEnPGN(String chemin, String resultat) {
        modele.PGNExporter.exporter(this, historique, chemin, resultat);
    }

}
