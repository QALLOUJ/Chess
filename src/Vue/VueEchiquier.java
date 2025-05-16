package Vue;

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

        JPanel panelCaptures = new JPanel(new GridLayout(1, 2, 10, 0));
        panelCapturesBlanc.setLayout(new FlowLayout(FlowLayout.LEFT));
        panelCapturesBlanc.setBorder(BorderFactory.createTitledBorder("Captures Blanc"));
        panelCapturesNoir.setLayout(new FlowLayout(FlowLayout.LEFT));
        panelCapturesNoir.setBorder(BorderFactory.createTitledBorder("Captures Noir"));
        panelCaptures.add(panelCapturesBlanc);
        panelCaptures.add(panelCapturesNoir);

        JButton btnRecommencer = new JButton("Recommencer");
        btnRecommencer.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnRecommencer.addActionListener(e -> {
            int option = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment recommencer la partie ?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                this.dispose();
                new VueEchiquier(new Plateau(8,8)).setVisible(true);
            }
        });
        JPanel panelDroite = new JPanel(new BorderLayout());
        panelDroite.add(btnRecommencer, BorderLayout.NORTH);

        this.setLayout(new BorderLayout(10, 10));
        this.add(grille, BorderLayout.CENTER);
        this.add(panelHaut, BorderLayout.NORTH);
        this.add(panelCaptures, BorderLayout.SOUTH);
        this.add(panelDroite, BorderLayout.EAST);
    }

    private void afficherMenuDemarrage() {
        JDialog dialog = new JDialog(this, "Choix du mode de jeu", true);
        dialog.setSize(350, 230);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);

        JLabel label = new JLabel("Choisissez le mode de jeu :", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        dialog.add(label, BorderLayout.NORTH);

        JPanel panelBoutons = new JPanel(new GridLayout(2, 1, 10, 10));
        JButton btnJoueurVsJoueur = new JButton("Joueur vs Joueur");
        JButton btnJoueurVsIA = new JButton("Joueur vs IA");

        btnJoueurVsJoueur.setFont(new Font("Arial", Font.PLAIN, 16));
        btnJoueurVsIA.setFont(new Font("Arial", Font.PLAIN, 16));

        btnJoueurVsJoueur.addActionListener(e -> {
            vsIA = false;
            dialog.dispose();
            changerTour("Blanc");
        });

        btnJoueurVsIA.addActionListener(e -> {
            vsIA = true;
            dialog.dispose();
            changerTour("Blanc");
        });

        panelBoutons.add(btnJoueurVsJoueur);
        panelBoutons.add(btnJoueurVsIA);
        dialog.add(panelBoutons, BorderLayout.CENTER);
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

    public static void changerTour(String joueur) {
        labelTour.setText("C'est au tour du joueur " + joueur);
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
        revalidate();
        repaint();
    }

    public interface CaseClickListener {
        void onCaseClicked(Case c);
    }
}
