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
    private JLabel labelChronoBlanc;  // plus static
    private JLabel labelChronoNoir;   // plus static
    private JLabel labelTour;          // plus static
    private final List<CaseClickListener> listeners = new ArrayList<>();

    private final JPanel panelCapturesBlanc = new JPanel();
    private final JPanel panelCapturesNoir = new JPanel();

    private List<Case> deplacementsPossibles = new ArrayList<>();

    // Icônes des pièces
    private ImageIcon icoRoiBlanc, icoRoiNoir, icoReineBlanc, icoReineNoir,
            icoTourBlanc, icoTourNoir, icoFouBlanc, icoFouNoir,
            icoCavalierBlanc, icoCavalierNoir, icoPionBlanc, icoPionNoir;

    // Labels noms joueurs (à initialiser dynamiquement)
    private JLabel labelNomBlanc;
    private JLabel labelNomNoir;

    public VueEchiquier(Plateau plateau, String nomBlanc, String nomNoir) {
        this.plateau = plateau;
        this.sizeX = Plateau.SIZE_X;
        this.sizeY = Plateau.SIZE_Y;

        chargerLesIcones();

        // Initialiser labels noms joueurs
        labelNomBlanc = new JLabel("Blanc : " + nomBlanc);
        labelNomNoir = new JLabel("Noir : " + nomNoir);

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

        JPanel panelNomCapturesNoir = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        panelNomCapturesNoir.setPreferredSize(new Dimension(250, 40));
        panelCapturesNoir.setLayout(new FlowLayout(FlowLayout.LEFT));
        panelNomCapturesNoir.add(labelNomNoir);
        panelNomCapturesNoir.add(panelCapturesNoir);

        labelNomNoir.setPreferredSize(new Dimension(150, 30));
        labelNomNoir.setToolTipText(labelNomNoir.getText());

        JPanel panelNomCapturesBlanc = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panelNomCapturesBlanc.setPreferredSize(new Dimension(250, 40));
        panelCapturesBlanc.setLayout(new FlowLayout(FlowLayout.LEFT));
        panelNomCapturesBlanc.add(labelNomBlanc);
        panelNomCapturesBlanc.add(panelCapturesBlanc);

        labelNomBlanc.setPreferredSize(new Dimension(150, 30));
        labelNomBlanc.setToolTipText(labelNomBlanc.getText());

        JPanel panelChronos = new JPanel(new FlowLayout(FlowLayout.CENTER));
        labelChronoBlanc = new JLabel("Blanc: 05:00");
        labelChronoNoir = new JLabel("Noir: 05:00");
        labelTour = new JLabel("C'est au tour du joueur Blanc");
        panelChronos.add(labelChronoBlanc);
        panelChronos.add(labelChronoNoir);
        panelChronos.add(labelTour);

        this.setLayout(new BorderLayout());
        this.add(panelNomCapturesNoir, BorderLayout.NORTH);
        this.add(grille, BorderLayout.CENTER);
        this.add(panelNomCapturesBlanc, BorderLayout.SOUTH);
        this.add(panelChronos, BorderLayout.PAGE_START);

        setSize(sizeX * pxCase, sizeY * pyCase + 150);
        setLocationRelativeTo(null);
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

    public void miseAJourChronoBlanc(int temps) {
        labelChronoBlanc.setText("Blanc: " + formatTemps(temps));
    }

    public void miseAJourChronoNoir(int temps) {
        labelChronoNoir.setText("Noir: " + formatTemps(temps));
    }

    private String formatTemps(int temps) {
        int minutes = temps / 60;
        int secondes = temps % 60;
        return String.format("%02d:%02d", minutes, secondes);
    }

    public void changerTour(String joueur) {
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
        labelIcone.setPreferredSize(new Dimension(20, 20)); // taille réduite comme sur chess.com

        if (piece.getCouleur().equals("blanc")) {
            panelCapturesNoir.add(labelIcone);
        } else {
            panelCapturesBlanc.add(labelIcone);
        }
        revalidate();
        repaint();
    }
}
