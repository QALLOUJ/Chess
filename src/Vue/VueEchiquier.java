package Vue;

import modele.*;
import modele.Pieces.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class VueEchiquier extends JFrame {
    private final Plateau plateau;
    private JLabel[][] tabJLabel;
    private final int pxCase = 50, pyCase = 50;
    private final int sizeX, sizeY;
    private static JLabel labelChronoBlanc;
    private static JLabel labelChronoNoir;
    private static JLabel labelTour;
    private final List<CaseClickListener> listeners = new ArrayList<>();
    private final JPanel panelCapturesBlanc = new JPanel();
    private final JPanel panelCapturesNoir = new JPanel();

    private List<Case> deplacementsPossibles = new ArrayList<>();

    // Icônes
    private ImageIcon icoRoiBlanc, icoRoiNoir, icoReineBlanc, icoReineNoir,
            icoTourBlanc, icoTourNoir, icoFouBlanc, icoFouNoir,
            icoCavalierBlanc, icoCavalierNoir, icoPionBlanc, icoPionNoir;

    public VueEchiquier(Plateau plateau) {
        this.plateau = plateau;
        this.sizeX = Plateau.SIZE_X;
        this.sizeY = Plateau.SIZE_Y;

        chargerLesIcones();
        placerLesComposantsGraphiques();
        mettreAJourAffichage();
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

    private ImageIcon chargerIcone(String path) {
        ImageIcon icon = new ImageIcon(path);
        if (icon.getImageLoadStatus() != MediaTracker.COMPLETE) {
            System.out.println("Erreur de chargement de l'image: " + path);
        }
        Image img = icon.getImage().getScaledInstance(pxCase, pyCase, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    private void placerLesComposantsGraphiques() {
        setTitle("Jeu d'Échecs");
        setResizable(false);
        setSize(sizeX * pxCase, sizeY * pyCase + 120);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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

                Color color = (x + y) % 2 == 0 ? new Color(245, 245, 220) : new Color(200, 100, 110);
                jlab.setBackground(color);
                jlab.setBorder(BorderFactory.createLineBorder(Color.BLACK));

                tabJLabel[x][y] = jlab;
                grille.add(jlab);
            }
        }

        JPanel panelChronos = new JPanel(new FlowLayout(FlowLayout.CENTER));
        labelChronoBlanc = new JLabel("Blanc: 05:00");
        labelChronoNoir = new JLabel("Noir: 05:00");
        labelTour = new JLabel("C'est au tour du joueur Blanc");
        panelChronos.add(labelChronoBlanc);
        panelChronos.add(labelChronoNoir);
        panelChronos.add(labelTour);

        JPanel panelCaptures = new JPanel(new GridLayout(1, 2));

        JPanel blocBlanc = new JPanel(new BorderLayout());
        blocBlanc.add(new JLabel("Captures Blanc:"), BorderLayout.NORTH);
        panelCapturesBlanc.setLayout(new FlowLayout(FlowLayout.LEFT));
        blocBlanc.add(panelCapturesBlanc, BorderLayout.CENTER);

        JPanel blocNoir = new JPanel(new BorderLayout());
        blocNoir.add(new JLabel("Captures Noir:"), BorderLayout.NORTH);
        panelCapturesNoir.setLayout(new FlowLayout(FlowLayout.LEFT));
        blocNoir.add(panelCapturesNoir, BorderLayout.CENTER);

        panelCaptures.add(blocBlanc);
        panelCaptures.add(blocNoir);

        this.add(grille, BorderLayout.CENTER);
        this.add(panelChronos, BorderLayout.NORTH);
        this.add(panelCaptures, BorderLayout.SOUTH);
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

                if (deplacementsPossibles.contains(c)) {
                    label.setBackground(new Color(255, 255, 153));
                } else {
                    label.setBackground((x + y) % 2 == 0 ? new Color(245, 245, 220) : new Color(200, 100, 110));
                }
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
}
