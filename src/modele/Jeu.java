package modele;

import modele.Pieces.Roi;
import modele.Pieces.Tour;
import modele.Pieces.Pion;

import Vue.VueEchiquier;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Jeu {
    private final Joueur joueurBlanc;
    private final Joueur joueurNoir;
    private Joueur joueurCourant;
    private final Plateau plateau;
    private ArrayList<Coup> historique = new ArrayList<>();
    private Timer timerBlanc;
    private Timer timerNoir;
    private int tempsRestantBlanc;
    private int tempsRestantNoir;
    private boolean partieEnCours = true; // Pour savoir si la partie est encore en cours
    private VueEchiquier vueEchiquier;
    private boolean iaActivee = false;


    public Jeu(Plateau plateau, String nomBlanc, String nomNoir, int dureeChronoEnSecondes) {
        this.plateau = plateau;
        this.joueurBlanc = new Joueur(nomBlanc, true);
        this.joueurNoir = new Joueur(nomNoir, false);
        this.joueurCourant = joueurBlanc;
        this.tempsRestantBlanc = dureeChronoEnSecondes;
        this.tempsRestantNoir = dureeChronoEnSecondes;
    }

    public Jeu(Jeu original) {
        this.plateau = new Plateau(original.getPlateau()); // copie profonde à implémenter dans Plateau
        this.joueurBlanc = new Joueur(original.joueurBlanc);
        this.joueurNoir = new Joueur(original.joueurNoir);
        this.joueurCourant = original.joueurCourant.getCouleur().equals("blanc") ? joueurBlanc : joueurNoir;
        this.historique = new ArrayList<>(original.historique); // copie superficielle, faire profonde si nécessaire
        this.tempsRestantBlanc = original.tempsRestantBlanc;
        this.tempsRestantNoir = original.tempsRestantNoir;
        this.partieEnCours = original.partieEnCours;
        this.iaActivee = original.iaActivee;
    }

    public void demarrerChronometre() {
        ActionListener actionListenerBlanc = e -> {
            if (tempsRestantBlanc > 0 && partieEnCours) {
                tempsRestantBlanc--;
                VueEchiquier.miseAJourChronoBlanc(tempsRestantBlanc);
                verifierFinPartie();
            } else if (timerBlanc != null) {
                timerBlanc.stop();
            }
        };

        ActionListener actionListenerNoir = e -> {
            if (tempsRestantNoir > 0 && partieEnCours) {
                tempsRestantNoir--;
                VueEchiquier.miseAJourChronoNoir(tempsRestantNoir);
                verifierFinPartie();
            } else if (timerNoir != null) {
                timerNoir.stop();
            }
        };

        if (timerBlanc == null) {
            timerBlanc = new Timer(1000, actionListenerBlanc);
        }
        if (timerNoir == null) {
            timerNoir = new Timer(1000, actionListenerNoir);
        }

        timerBlanc.start();
    }

    public Plateau getPlateau() {
        return plateau;
    }

    public Joueur getJoueurCourant() {
        return joueurCourant;
    }

    public boolean isTourIA() {
        return iaActivee && joueurCourant.getCouleur().equals("noir");
    }
    public void setIaActivee(boolean active) {
        this.iaActivee = active;
    }

    public void changerTour() {
        if (joueurCourant == joueurBlanc) {
            if (timerBlanc != null) timerBlanc.stop();
            if (timerNoir != null) timerNoir.start();
            joueurCourant = joueurNoir;
        } else {
            if (timerNoir != null) timerNoir.stop();
            if (timerBlanc != null) timerBlanc.start();
            joueurCourant = joueurBlanc;
        }
    }

    public boolean demandeDeplacementPiece(Case source, Case arrive) {
        Piece piece = source.getPiece();
        if (piece == null || !piece.getCouleur().equals(joueurCourant.getCouleur()) || !piece.peutDeplacer(source, arrive)) {
            return false;
        }

        Piece pieceCapturee = arrive.getPiece();

        // Prise en passant
        if (piece instanceof Pion && arrive.estVide()) {
            int dx = arrive.getX() - source.getX();
            int dy = arrive.getY() - source.getY();
            if (Math.abs(dx) == 1 && dy == (piece.getCouleur().equals("blanc") ? -1 : 1)) {
                Case casePionPris = plateau.getCase(arrive.getX(), source.getY());
                pieceCapturee = casePionPris.getPiece();
                casePionPris.setPiece(null);
            }
        }

        // Afficher la pièce capturée (prise en passant ou normale)
        if (pieceCapturee != null && vueEchiquier != null) {
            vueEchiquier.ajouterCapture(pieceCapturee);
        }

        Coup coup = new Coup(piece, source, arrive, pieceCapturee);
        effectuerDeplacement(source, arrive, piece);

        historique.add(coup);

        // Gestion du roque
        if (piece instanceof Roi) {
            int dx = arrive.getX() - source.getX();
            if (Math.abs(dx) == 2) {
                int y = source.getY();
                if (dx > 0) { // Petit roque
                    Case caseTour = plateau.getCase(7, y);
                    Case caseTourDest = plateau.getCase(5, y);
                    Piece tour = caseTour.getPiece();
                    caseTourDest.setPiece(tour);
                    caseTour.setPiece(null);
                    tour.setCase(caseTourDest);
                    ((Tour) tour).setABouge(true);
                } else { // Grand roque
                    Case caseTour = plateau.getCase(0, y);
                    Case caseTourDest = plateau.getCase(3, y);
                    Piece tour = caseTour.getPiece();
                    caseTourDest.setPiece(tour);
                    caseTour.setPiece(null);
                    tour.setCase(caseTourDest);
                    ((Tour) tour).setABouge(true);
                }
            }
        }

        // Promotion du pion
        if (piece instanceof Pion) {
            int lignePromotion = piece.getCouleur().equals("blanc") ? 0 : 7;
            if (arrive.getY() == lignePromotion) {
                String[] options = {"Reine", "Tour", "Fou", "Cavalier"};
                String choix = (String) JOptionPane.showInputDialog(
                        null,
                        "Choisissez une pièce pour la promotion :",
                        "Promotion du pion",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        options,
                        options[0]
                );

                Piece nouvellePiece;
                switch (choix) {
                    case "Reine":
                        nouvellePiece = new modele.Pieces.Reine(piece.getCouleur());
                        break;
                    case "Tour":
                        nouvellePiece = new modele.Pieces.Tour(piece.getCouleur());
                        break;
                    case "Fou":
                        nouvellePiece = new modele.Pieces.Fou(piece.getCouleur());
                        break;
                    case "Cavalier":
                        nouvellePiece = new modele.Pieces.Cavalier(piece.getCouleur());
                        break;
                    default:
                        nouvellePiece = new modele.Pieces.Reine(piece.getCouleur());
                        break;
                }

                arrive.setPiece(nouvellePiece);
                nouvellePiece.setCase(arrive);
            }
        }

        changerTour();
        plateau.notifierChangement();

        verifierEchecEtMat(joueurCourant.getCouleur());

        return true;
    }

    private void effectuerDeplacement(Case source, Case arrive, Piece piece) {
        arrive.setPiece(piece);
        source.setPiece(null);
        piece.setCase(arrive);
        if (piece instanceof Roi) {
            ((Roi) piece).setABouge(true);
        } else if (piece instanceof Tour) {
            ((Tour) piece).setABouge(true);
        }
    }

    private void verifierEchecEtMat(String couleur) {
        if (plateau.estEnEchec(couleur)) {
            afficherMessage("Échec, attention !", "Le roi de " + couleur + " est en échec !");
        }

        if (plateau.estEchecEtMat(couleur)) {
            afficherMessage("Échec et Mat", joueurCourant.getNom() + " a remporté la partie !");
            finPartie(joueurCourant.getNom() + " a gagné par Échec et Mat !");
        } else if (plateau.estPat(couleur)) {
            afficherMessage("Pat", "La partie est nulle par pat.");
            finPartie("Partie nulle par pat.");
        }
    }

    private void afficherMessage(String titre, String message) {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(255, 192, 203));
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

    public void verifierFinPartie() {
        if (tempsRestantBlanc <= 0) {
            finPartie("Le joueur noir a gagné, temps écoulé !");
        } else if (tempsRestantNoir <= 0) {
            finPartie("Le joueur blanc a gagné, temps écoulé !");
        }
    }

    public void finPartie(String message) {
        partieEnCours = false;
        if (timerBlanc != null) timerBlanc.stop();
        if (timerNoir != null) timerNoir.stop();

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.BLACK);

        JLabel titre = new JLabel("<html><div style='text-align: center;'><h2 style='color:white;'>" + message + "</h2></div></html>");
        titre.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        panel.add(Box.createVerticalStrut(20));
        panel.add(titre);
        panel.add(Box.createVerticalStrut(20));

        JOptionPane.showMessageDialog(null, panel, "🎉 Fin de la Partie 🎉", JOptionPane.INFORMATION_MESSAGE);

        if (vueEchiquier != null) {
            vueEchiquier.setEnabled(false);
            vueEchiquier.setFocusable(false);
            vueEchiquier.desactiverPlateau();
        }
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

    public ArrayList<Coup> getHistorique() {
        return historique;
    }

    public Coup getDernierCoup() {
        if (!historique.isEmpty()) {
            return historique.get(historique.size() - 1);
        }
        return null;
    }

    public boolean isPartieEnCours() {
        return partieEnCours;
    }
}
