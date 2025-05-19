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
    private boolean partieEnCours = true;
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
        this.plateau = new Plateau(8, 8);
        this.joueurBlanc = new Joueur(original.joueurBlanc);
        this.joueurNoir = new Joueur(original.joueurNoir);
        this.joueurCourant = original.joueurCourant.getCouleur().equals("blanc") ? joueurBlanc : joueurNoir;
        this.historique = new ArrayList<>(original.historique);
        this.tempsRestantBlanc = original.tempsRestantBlanc;
        this.tempsRestantNoir = original.tempsRestantNoir;
        this.partieEnCours = original.partieEnCours;
        this.iaActivee = original.iaActivee;
    }

    public void demarrerChronometre() {
        ActionListener actionBlanc = e -> {
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

        ActionListener actionNoir = e -> {
            if (tempsRestantNoir > 0 && partieEnCours) {
                tempsRestantNoir--;
                VueEchiquier.miseAJourChronoNoir(tempsRestantNoir);
                verifierFinPartie();
            } else if (timerNoir != null) {
                timerNoir.stop();
            }
        };

        if (timerBlanc == null) {
            timerBlanc = new Timer(1000, actionBlanc);
        }
        if (timerNoir == null) {
            timerNoir = new Timer(1000, actionNoir);
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

    public boolean demandeDeplacementPiece(Case source, Case arrivee) {
        Piece piece = source.getPiece();
        if (piece == null || !piece.getCouleur().equals(joueurCourant.getCouleur()) || !piece.peutDeplacer(source, arrivee)) {
            return false;
        }

        Piece capture = arrivee.getPiece();

        // Cas spécial pour la prise en passant
        if (piece instanceof Pion && arrivee.estVide()) {
            int dx = arrivee.getX() - source.getX();
            int dy = arrivee.getY() - source.getY();
            if (Math.abs(dx) == 1 && dy == (piece.getCouleur().equals("blanc") ? -1 : 1)) {
                Case casePrise = plateau.getCase(arrivee.getX(), source.getY());
                capture = casePrise.getPiece();
                casePrise.setPiece(null);
            }
        }

        if (capture != null && vueEchiquier != null) {
            vueEchiquier.ajouterCapture(capture);
        }

        Coup coup = new Coup(piece, source, arrivee, capture);
        effectuerDeplacement(source, arrivee, piece);
        historique.add(coup);

        // Gestion du roque (petit et grand)
        if (piece instanceof Roi) {
            int dx = arrivee.getX() - source.getX();
            if (Math.abs(dx) == 2) {
                int ligne = source.getY();
                if (dx > 0) {
                    deplacerTourPourRoque(7, 5, ligne);
                } else {
                    deplacerTourPourRoque(0, 3, ligne);
                }
            }
        }

        // Promotion automatique d'un pion arrivé en bout
        if (piece instanceof Pion) {
            int lignePromotion = piece.getCouleur().equals("blanc") ? 0 : 7;
            if (arrivee.getY() == lignePromotion) {
                String[] options = {"Reine", "Tour", "Fou", "Cavalier"};
                String choix = interfaceUtilisateur.demanderChoixPromotion(options);
                Piece nouvellePiece;
                switch (choix) {
                    case "Tour": nouvellePiece = new Tour(piece.getCouleur()); break;
                    case "Fou": nouvellePiece = new Fou(piece.getCouleur()); break;
                    case "Cavalier": nouvellePiece = new Cavalier(piece.getCouleur()); break;
                    default: nouvellePiece = new Reine(piece.getCouleur()); break;
                }
                arrivee.setPiece(nouvellePiece);
                nouvellePiece.setCase(arrivee);
            }
        }

        plateau.notifierChangement();
        changerTour();
        verifierEchecEtMat(joueurCourant.getCouleur());
        return true;
    }

    private void deplacerTourPourRoque(int colonneDepart, int colonneArrivee, int ligne) {
        Case caseTour = plateau.getCase(colonneDepart, ligne);
        Case caseDestination = plateau.getCase(colonneArrivee, ligne);
        Piece tour = caseTour.getPiece();
        caseTour.setPiece(null);
        caseDestination.setPiece(tour);
        tour.setCase(caseDestination);
        if (tour instanceof Tour) {
            ((Tour) tour).setABouge(true);
        }
    }

    private void effectuerDeplacement(Case source, Case arrivee, Piece piece) {
        arrivee.setPiece(piece);
        source.setPiece(null);
        piece.setCase(arrivee);
        if (piece instanceof Roi) {
            ((Roi) piece).setABouge(true);
        } else if (piece instanceof Tour) {
            ((Tour) piece).setABouge(true);
        }
    }

    public void verifierEchecEtMat(String couleur) {
        if (plateau.estEchecEtMat(couleur)) {
            afficherMessage("Échec et Mat", joueurCourant.getNom() + " a gagné !");
            finPartie(joueurCourant.getNom() + " a gagné par Échec et Mat !");
        } else if (plateau.estPat(couleur)) {
            afficherMessage("Pat", "Match nul : situation de pat.");
            finPartie("Match nul : pat.");
        } else if (plateau.estEnEchec(couleur) && !messageEchecAffiche) {
            afficherMessage("Échec", "Attention : le roi " + couleur + " est en échec !");
            messageEchecAffiche = true;
        } else {
            messageEchecAffiche = false;
        }
    }

    private void afficherMessage(String titre, String contenu) {
        String texte = titre + " : " + contenu;
        if (interfaceUtilisateur != null) {
            interfaceUtilisateur.afficherMessage(texte);
        } else {
            System.out.println(texte);
        }
    }

    public void verifierFinPartie() {
        if (tempsRestantBlanc <= 0) {
            finPartie("Temps écoulé pour les blancs !");
        } else if (tempsRestantNoir <= 0) {
            finPartie("Temps écoulé pour les noirs !");
        }
    }

    public void sauvegarderEnPGN(String chemin, String resultat) {
        PGNExporter.exporter(this, historique, chemin, resultat);
    }

    public void sauvegarderEchiquierEtPGNEnPNG(String nomFichierPNG, String resultat) {
        int largeur = vueEchiquier.getWidth();
        int hauteurEchiquier = vueEchiquier.getHeight();
        int hauteurTexte = 150;
        int hauteurTotale = hauteurEchiquier + hauteurTexte;

        BufferedImage image = new BufferedImage(largeur, hauteurTotale, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        vueEchiquier.paint(g2d);

        String pgn = PGNExporter.getPGNString(this, historique, resultat);
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, hauteurEchiquier, largeur, hauteurTexte);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Monospaced", Font.PLAIN, 14));

        int x = 10;
        int y = hauteurEchiquier + 20;
        for (String ligne : wrapText(pgn, 80)) {
            g2d.drawString(ligne, x, y);
            y += 18;
        }

        g2d.dispose();
        try {
            ImageIO.write(image, "png", new File(nomFichierPNG));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> wrapText(String texte, int longueurMax) {
        List<String> lignes = new ArrayList<>();
        StringBuilder ligneActuelle = new StringBuilder();

        for (String mot : texte.split(" ")) {
            if (ligneActuelle.length() + mot.length() + 1 > longueurMax) {
                lignes.add(ligneActuelle.toString());
                ligneActuelle = new StringBuilder();
            }
            if (!ligneActuelle.isEmpty()) ligneActuelle.append(" ");
            ligneActuelle.append(mot);
        }
        if (!ligneActuelle.isEmpty()) {
            lignes.add(ligneActuelle.toString());
        }

        return lignes;
    }

    public void finPartie(String message) {
        partieEnCours = false;
        if (timerBlanc != null) timerBlanc.stop();
        if (timerNoir != null) timerNoir.stop();

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        new File("sauvegardes").mkdirs();
        new File("captures").mkdirs();

        String fichierPGN = "sauvegardes/partie_" + timestamp + ".pgn";
        String fichierPNG = "captures/echiquier_fin_" + timestamp + ".png";

        String resultat = message.toLowerCase().contains("blanc") ? "1-0" :
                message.toLowerCase().contains("noir") ? "0-1" : "1/2-1/2";

        sauvegarderEnPGN(fichierPGN, resultat);
        sauvegarderEchiquierEtPGNEnPNG(fichierPNG, resultat);

        if (interfaceUtilisateur != null) {
            interfaceUtilisateur.afficherMessage(message);
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
        return historique.isEmpty() ? null : historique.get(historique.size() - 1);
    }

    public boolean isPartieEnCours() {
        return partieEnCours;
    }

    public static void remettreTourBlanc() {
        tourBlanc = true;
    }
}
