package modele;

import Vue.InterfaceUtilisateur;
import modele.Pieces.*;

import Vue.VueEchiquier;

import java.util.List;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    private boolean messageEchecAffiche = false;
    private static boolean tourBlanc = true;
    private InterfaceUtilisateur interfaceUtilisateur;




    public Jeu(Plateau plateau, String nomBlanc, String nomNoir, int dureeChronoEnSecondes) {
        this.plateau = plateau;
        this.joueurBlanc = new Joueur(nomBlanc, true);
        this.joueurNoir = new Joueur(nomNoir, false);
        this.joueurCourant = joueurBlanc;
        this.tempsRestantBlanc = dureeChronoEnSecondes;
        this.tempsRestantNoir = dureeChronoEnSecondes;
    }

    public Jeu(Jeu original) {
        this.plateau = new Plateau(8,8); // copie profonde à implémenter dans Plateau
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
                if (interfaceUtilisateur != null) {
                    interfaceUtilisateur.miseAJourChrono("blanc", tempsRestantBlanc);
                    interfaceUtilisateur.miseAJourChrono("noir", tempsRestantNoir);
                }

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

    public void setInterfaceUtilisateur(InterfaceUtilisateur ui) {
        this.interfaceUtilisateur = ui;
    }

    public boolean isTourIA() {
        return iaActivee && joueurCourant.getCouleur().equals("noir");
    }
    public void setIaActivee(boolean active) {
        this.iaActivee = active;
    }

    public void changerTour() {
        if (!partieEnCours) return;

        if (joueurCourant == joueurBlanc) {
            if (timerBlanc != null) timerBlanc.stop();
            if (timerNoir != null) timerNoir.start();
            joueurCourant = joueurNoir;
        } else {
            if (timerNoir != null) timerNoir.stop();
            if (timerBlanc != null) timerBlanc.start();
            joueurCourant = joueurBlanc;
        }
        tourBlanc = !tourBlanc;
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
                String choix = interfaceUtilisateur.demanderChoixPromotion(options);


                Piece nouvellePiece;
                switch (choix) {
                    case "Reine":
                        nouvellePiece = new Reine(piece.getCouleur());
                        break;
                    case "Tour":
                        nouvellePiece = new Tour(piece.getCouleur());
                        break;
                    case "Fou":
                        nouvellePiece = new Fou(piece.getCouleur());
                        break;
                    case "Cavalier":
                        nouvellePiece = new Cavalier(piece.getCouleur());
                        break;
                    default:
                        nouvellePiece = new Reine(piece.getCouleur());
                        break;
                }

                arrive.setPiece(nouvellePiece);
                nouvellePiece.setCase(arrive);
            }
        }


        plateau.notifierChangement();
        changerTour();
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

    public void verifierEchecEtMat(String couleur) {
        if (plateau.estEchecEtMat(couleur)) {
            afficherMessage("Échec et Mat", joueurCourant.getNom() + " a remporté la partie !");
            finPartie(joueurCourant.getNom() + " a gagné par Échec et Mat !");
            return;
        } else if (plateau.estPat(couleur)) {
            afficherMessage("Pat", "La partie est nulle par pat.");
            finPartie("Partie nulle par pat.");
            return;
        }

        if (plateau.estEnEchec(couleur)) {
            if (!messageEchecAffiche) {
                afficherMessage("Échec, attention !", "Le roi de " + couleur + " est en échec !");
                messageEchecAffiche = true;
            }
        } else {
            messageEchecAffiche = false;
        }
    }



    private void afficherMessage(String titre, String message) {
        String messageComplet = titre + " : " + message;
        if (interfaceUtilisateur != null) {
            interfaceUtilisateur.afficherMessage(messageComplet);
        } else {
            // fallback console
            System.out.println(messageComplet);
        }
    }

    public void verifierFinPartie() {
        if (tempsRestantBlanc <= 0) {
            finPartie("Le joueur noir a gagné, temps écoulé !");
        } else if (tempsRestantNoir <= 0) {
            finPartie("Le joueur blanc a gagné, temps écoulé !");
        }
    }

    public void sauvegarderEnPGN(String chemin, String resultat) {
        PGNExporter.exporter(this, historique, chemin, resultat);
    }
    public void sauvegarderEchiquierEtPGNEnPNG(String nomFichierPNG, String resultat) {
        int largeur = vueEchiquier.getWidth();
        int hauteurEchiquier = vueEchiquier.getHeight();
        int hauteurPGN = 150; // Ajuste si nécessaire
        int hauteurTotale = hauteurEchiquier + hauteurPGN;

        // Créer une image avec de l'espace pour le PGN
        BufferedImage image = new BufferedImage(largeur, hauteurTotale, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        // Dessiner l'échiquier
        vueEchiquier.paint(g2d);

        // Récupérer le texte PGN
        String pgnText = PGNExporter.getPGNString(this, historique, resultat);

        // Définir une police lisible
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Monospaced", Font.PLAIN, 14));

        // Fond noir sous l’échiquier pour le texte
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, hauteurEchiquier, largeur, hauteurPGN);

        // Dessiner le texte PGN
        g2d.setColor(Color.WHITE);
        int x = 10;
        int y = hauteurEchiquier + 20;
        for (String line : wrapText(pgnText, 80)) {
            g2d.drawString(line, x, y);
            y += 18;
        }

        g2d.dispose();

        try {
            ImageIO.write(image, "png", new File(nomFichierPNG));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour couper le texte PGN en lignes de taille raisonnable
    private List<String> wrapText() {
        return wrapText(null, 0);
    }

    // Méthode pour couper le texte PGN en lignes de taille raisonnable
    private List<String> wrapText(String text, int maxLineLength) {
        List<String> lines = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();

        for (String word : text.split(" ")) {
            if (currentLine.length() + word.length() + 1 > maxLineLength) {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder();
            }
            if (!currentLine.isEmpty()) {
                currentLine.append(" ");
            }
            currentLine.append(word);
        }
        if (!currentLine.isEmpty()) {
            lines.add(currentLine.toString());
        }

        return lines;
    }



    public void finPartie(String message) {
        partieEnCours = false;
        if (timerBlanc != null) timerBlanc.stop();
        if (timerNoir != null) timerNoir.stop();

        //  Sauvegarde PGN
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String nomFichierPGN = "sauvegardes/partie_" + timestamp + ".pgn";
        new File("sauvegardes").mkdirs(); // Crée le dossier si besoin

        String resultat;
        if (message.toLowerCase().contains("blanc")) {
            resultat = "1-0";
        } else if (message.toLowerCase().contains("noir")) {
            resultat = "0-1";
        } else {
            resultat = "1/2-1/2";
        }

        sauvegarderEnPGN(nomFichierPGN, resultat);

        //  Sauvegarde image PNG
        String nomFichierPNG = "captures/echiquier_fin_" + timestamp + ".png";
        new File("captures").mkdirs();
        sauvegarderEchiquierEtPGNEnPNG(nomFichierPNG, resultat); // À condition que cette méthode prenne un nom de fichier

        //  Affichage message fin de partie
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.BLACK);

        JLabel titre = new JLabel("<html><div style='text-align: center;'><h2 style='color:white;'>" + message + "</h2></div></html>");
        titre.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        panel.add(Box.createVerticalStrut(20));
        panel.add(titre);
        panel.add(Box.createVerticalStrut(20));

        if (interfaceUtilisateur != null) {
            interfaceUtilisateur.afficherMessage( message);
        }

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

    public static void remettreTourBlanc() {
        tourBlanc = true;
    }
}
