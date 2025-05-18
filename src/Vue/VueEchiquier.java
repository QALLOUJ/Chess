package Vue;

import controleur.ControleurEchiquier;
import modele.*;
import modele.Pieces.*;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class VueEchiquier extends JFrame {
    private final Plateau plateau;
    private JLabel[][] tabJLabel;
    private final int pxCase = 64, pyCase = 64;
    private final int sizeX, sizeY;
    private static JLabel labelChronoBlanc;
    private static JLabel labelChronoNoir;
    private static JLabel labelTour;
    private String nomJoueurBlanc = "Blanc";
    private String nomJoueurNoir = "Noir";
    private int tempsParJoueur = 300; // en secondes

    private final List<CaseClickListener> listeners = new ArrayList<>();
    private final JPanel panelCapturesBlanc = new JPanel();
    private final JPanel panelCapturesNoir = new JPanel();
    private List<Case> deplacementsPossibles = new ArrayList<>();
    private ImageIcon icoRoiBlanc, icoRoiNoir, icoReineBlanc, icoReineNoir,
            icoTourBlanc, icoTourNoir, icoFouBlanc, icoFouNoir,
            icoCavalierBlanc, icoCavalierNoir, icoPionBlanc, icoPionNoir;
    private boolean vsIA = false;

    public VueEchiquier(Plateau plateau) {
        this.plateau = plateau;
        this.sizeX = Plateau.SIZE_X;
        this.sizeY = Plateau.SIZE_Y;
        chargerLesIcones();
        initFenetre();
        afficherMenuDemarrage();
        mettreAJourAffichage();
    }

    private void initFenetre() {
        setTitle("Jeu d'Échecs");
        setResizable(false);
        setSize(sizeX * pxCase + 300, sizeY * pyCase + 220);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Grille de l'échiquier
        JPanel grille = new JPanel(new GridLayout(sizeY, sizeX));
        tabJLabel = new JLabel[sizeX][sizeY];

        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                JLabel jlab = new JLabel();
                jlab.setOpaque(true);
                jlab.setHorizontalAlignment(SwingConstants.CENTER);
                final int xx = x, yy = y;
                jlab.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        notifyCaseClicked(plateau.getCase(xx, yy));
                    }
                });
                jlab.setBackground((x + y) % 2 == 0 ? new Color(245, 245, 220) : new Color(200, 100, 110));
                jlab.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
                tabJLabel[x][y] = jlab;
                grille.add(jlab);
            }
        }

        // Panel en haut : chronos + tour
        JPanel panelHaut = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 10));
        labelChronoBlanc = new JLabel("Blanc: 05:00");
        labelChronoNoir = new JLabel("Noir: 05:00");
        labelTour = new JLabel("C'est au tour du joueur Blanc");

        Font fontChrono = new Font("Verdana", Font.BOLD, 14);
        labelChronoBlanc.setFont(fontChrono);
        labelChronoNoir.setFont(fontChrono);
        labelTour.setFont(new Font("Arial", Font.BOLD, 16));

        panelHaut.add(labelChronoBlanc);
        panelHaut.add(labelTour);
        panelHaut.add(labelChronoNoir);

        // Panels des captures
        JPanel panelCaptures = new JPanel(new GridLayout(1, 2, 10, 0));
        panelCapturesBlanc.setLayout(new FlowLayout(FlowLayout.LEFT));
        panelCapturesBlanc.setBorder(BorderFactory.createTitledBorder("Captures Blanc"));
        panelCapturesNoir.setLayout(new FlowLayout(FlowLayout.LEFT));
        panelCapturesNoir.setBorder(BorderFactory.createTitledBorder("Captures Noir"));
        panelCaptures.add(panelCapturesBlanc);
        panelCaptures.add(panelCapturesNoir);

        // Bouton Recommencer
        JButton btnRecommencer = new JButton("Recommencer");
        btnRecommencer.addActionListener(e -> {
            int option = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment recommencer la partie ?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                reinitialiserPlateau();
                reinitialiserChronos(tempsParJoueur); //  ici on utilise la bonne valeur
            }
        });

        // Bouton Retour au menu
        JButton btnRetourMenu = new JButton("Retour au menu");
        btnRetourMenu.setFont(new Font("Arial", Font.PLAIN, 12));
        btnRetourMenu.addActionListener(e -> {
            int option = JOptionPane.showConfirmDialog(this, "Voulez-vous revenir au menu principal ? La partie actuelle sera perdue.", "Retour au menu", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                this.dispose(); // Ferme complètement la fenêtre actuelle
               Main.lancerNouvellePartie(); // Lance une nouvelle partie à partir de zéro
            }
        });


        // Panel bas : captures + bouton centré en dessous
        JPanel panelBas = new JPanel(new BorderLayout());
        panelBas.add(panelCaptures, BorderLayout.CENTER);

        JPanel panelBouton = new JPanel();
        panelBouton.add(btnRecommencer);
        panelBouton.add(btnRetourMenu);
        panelBas.add(panelBouton, BorderLayout.SOUTH);

        // Layout principal
        this.setLayout(new BorderLayout(10, 10));
        this.add(grille, BorderLayout.CENTER);
        this.add(panelHaut, BorderLayout.NORTH);
        this.add(panelBas, BorderLayout.SOUTH);
    }

    private void afficherMenuDemarrage() {
        JDialog dialog = new JDialog(this, "Paramètres de la partie", true);
        dialog.setSize(400, 350);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);

        // Titre
        JLabel label = new JLabel("Paramètres de la partie", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        dialog.add(label, BorderLayout.NORTH);

        // Panneau central avec champs de saisie
        JPanel panelCentre = new JPanel();
        panelCentre.setLayout(new BoxLayout(panelCentre, BoxLayout.Y_AXIS));
        panelCentre.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        JTextField nomBlancField = new JTextField(20);
        JTextField nomNoirField = new JTextField(20);
        JTextField chronoField = new JTextField(5);

        panelCentre.add(new JLabel("Nom du joueur Blanc :"));
        panelCentre.add(nomBlancField);
        panelCentre.add(Box.createVerticalStrut(10));
        panelCentre.add(new JLabel("Nom du joueur Noir :"));
        panelCentre.add(nomNoirField);
        panelCentre.add(Box.createVerticalStrut(10));
        panelCentre.add(new JLabel("Durée du chrono par joueur (en minutes) :"));
        panelCentre.add(chronoField);

        dialog.add(panelCentre, BorderLayout.CENTER);

        // Boutons pour choisir le mode de jeu
        JPanel panelBas = new JPanel(new GridLayout(2, 1, 10, 10));
        panelBas.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        JButton btnJoueurVsJoueur = new JButton("Joueur vs Joueur");
        JButton btnJoueurVsIA = new JButton("Joueur vs IA");

        ActionListener startGameListener = e -> {
            // Récupération et validation des champs
            String nomBlanc = nomBlancField.getText().trim();
            String nomNoir = nomNoirField.getText().trim();
            String chronoInput = chronoField.getText().trim();

            if (nomBlanc.isEmpty()) nomBlanc = "Blanc";
            if (nomNoir.isEmpty()) nomNoir = "Noir";

            int dureeMinutes = 5;
            try {
                dureeMinutes = Integer.parseInt(chronoInput);
                if (dureeMinutes <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Entrée invalide pour le chrono. 5 minutes seront utilisées par défaut.");
                dureeMinutes = 5;
            }
            int dureeEnSecondes = dureeMinutes * 60;

            // Affectation des valeurs et démarrage
            this.nomJoueurBlanc = nomBlanc;
            this.nomJoueurNoir = nomNoir;
            this.tempsParJoueur = dureeEnSecondes;
            vsIA = (e.getSource() == btnJoueurVsIA);

            dialog.dispose();
            changerTour("blanc");
        };

        btnJoueurVsJoueur.addActionListener(startGameListener);
        btnJoueurVsIA.addActionListener(startGameListener);

        panelBas.add(btnJoueurVsJoueur);
        panelBas.add(btnJoueurVsIA);

        dialog.add(panelBas, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }


    public boolean isVsIA() {
        return vsIA;
    }

    public void addCaseClickListener(CaseClickListener listener) {
        listeners.add(listener);
    }

    private void notifyCaseClicked(Case c) {
        for (CaseClickListener l : listeners) {
            l.onCaseClicked(c);
        }
    }

    private void chargerLesIcones() {
        icoRoiBlanc = chargerIcone("Images/white-king.png");
        icoRoiNoir = chargerIcone("Images/black-king.png");
        icoReineBlanc = chargerIcone("Images/white-queen.png");
        icoReineNoir = chargerIcone("Images/black-queen.png");
        icoTourBlanc = chargerIcone("Images/white-rook.png");
        icoTourNoir = chargerIcone("Images/black-rook.png");
        icoFouBlanc = chargerIcone("Images/white-bishop.png");
        icoFouNoir = chargerIcone("Images/black-bishop.png");
        icoCavalierBlanc = chargerIcone("Images/white-knight.png");
        icoCavalierNoir = chargerIcone("Images/black-knight.png");
        icoPionBlanc = chargerIcone("Images/white-pawn.png");
        icoPionNoir = chargerIcone("Images/black-pawn.png");
    }

    private ImageIcon chargerIcone(String path) {
        ImageIcon icon = new ImageIcon(path);
        if (icon.getImageLoadStatus() != MediaTracker.COMPLETE) {
            System.err.println("Erreur de chargement de l'image: " + path);
        }
        Image img = icon.getImage().getScaledInstance(pxCase, pyCase, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    private ImageIcon getIconPourPiece(Piece piece) {
        if (piece == null) return null;
        return switch (piece.getNom()) {
            case "Roi" -> piece.getCouleur().equals("blanc") ? icoRoiBlanc : icoRoiNoir;
            case "Reine" -> piece.getCouleur().equals("blanc") ? icoReineBlanc : icoReineNoir;
            case "Tour" -> piece.getCouleur().equals("blanc") ? icoTourBlanc : icoTourNoir;
            case "Fou" -> piece.getCouleur().equals("blanc") ? icoFouBlanc : icoFouNoir;
            case "Cavalier" -> piece.getCouleur().equals("blanc") ? icoCavalierBlanc : icoCavalierNoir;
            case "Pion" -> piece.getCouleur().equals("blanc") ? icoPionBlanc : icoPionNoir;
            default -> null;
        };
    }

    public void setDeplacementsPossibles(List<Case> cases) {
        this.deplacementsPossibles = cases;
    }

    public void mettreAJourAffichage() {
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                Case c = plateau.getCase(x, y);
                JLabel label = tabJLabel[x][y];
                label.setIcon(getIconPourPiece(c.getPiece()));
                label.setBackground(deplacementsPossibles.contains(c) ? new Color(255, 255, 153) :
                        (x + y) % 2 == 0 ? new Color(245, 245, 220) : new Color(200, 100, 110));
            }
        }
        revalidate();
        repaint();
    }

    public static void miseAJourChronoBlanc(int temps) {
        labelChronoBlanc.setText("Blanc: " + formatTemps(temps));
    }

    public static void miseAJourChronoNoir(int temps) {
        labelChronoNoir.setText("Noir: " + formatTemps(temps));
    }

    private static String formatTemps(int temps) {
        int minutes = temps / 60;
        int secondes = temps % 60;
        return String.format("%02d:%02d", minutes, secondes);
    }

    public void changerTour(String joueur) {
        if (joueur.equalsIgnoreCase("blanc")) {
            labelTour.setText("C'est au tour du joueur " + nomJoueurBlanc);
        } else if (joueur.equalsIgnoreCase("noir")) {
            labelTour.setText("C'est au tour du joueur " + nomJoueurNoir);
        } else {
            labelTour.setText("C'est au tour du joueur " + joueur);
        }
    }

    public void desactiverPlateau() {
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                tabJLabel[x][y].setEnabled(false);
            }
        }
    }

    public void ajouterCapture(Piece piece) {
        JLabel labelIcone = new JLabel(getIconPourPiece(piece));
        labelIcone.setPreferredSize(new Dimension(32, 32));
        if (piece.getCouleur().equals("blanc")) {
            panelCapturesNoir.add(labelIcone);
        } else {
            panelCapturesBlanc.add(labelIcone);
        }
        panelCapturesBlanc.revalidate();
        panelCapturesNoir.revalidate();
        panelCapturesBlanc.repaint();
        panelCapturesNoir.repaint();
    }

    private void reinitialiserPlateau() {
        plateau.reset();  // doit remettre les pièces à leur place d'origine
        mettreAJourAffichage();
        panelCapturesBlanc.removeAll();
        panelCapturesNoir.removeAll();
        panelCapturesBlanc.revalidate();
        panelCapturesNoir.revalidate();
        panelCapturesBlanc.repaint();
        panelCapturesNoir.repaint();
        Jeu.remettreTourBlanc();  // ou autre logique de tour
    }


    public String getNomJoueurBlanc() {
        return nomJoueurBlanc;
    }

    public String getNomJoueurNoir() {
        return nomJoueurNoir;
    }

    public int getTempsParJoueur() {
        return tempsParJoueur;
    }
    // Méthode pour afficher une alerte pop-up
    public void afficherAlerte(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public void afficherMessage(String message) {
        System.out.println(message);
    }
        // Mise à jour affichage tour joueur
        public void mettreAJourTour(String nomJoueur) {
            // Exemple : labelTour.setText("Tour de : " + nomJoueur);
        }

    public void setControleur(ControleurEchiquier controleur) {
    }

    // Interface pour écouter les clics sur une case
    public interface CaseClickListener {
        void onCaseClicked(Case c);
    }
    public static void reinitialiserChronos(int tempsInitial) {
        miseAJourChronoBlanc(tempsInitial);
        miseAJourChronoNoir(tempsInitial);
    }

}
