package Vue;

import Vue.CaseClickListener;
import modele.*;
import modele.Pieces.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

public class VueEchiquier extends JFrame implements Observer {
    private Plateau plateau;
    private JLabel[][] tabJLabel;
    private final int pxCase = 50, pyCase = 50;
    private final int sizeX, sizeY;
    private static JLabel labelChronoBlanc;
    private static JLabel labelChronoNoir;
    private static JLabel labelTour;
    private final List<CaseClickListener> listeners = new ArrayList<>();

    private List<Case> deplacementsPossibles = new ArrayList<>();
    private List<Piece> piecesCapturesBlanc = new ArrayList<>();
    private List<Piece> piecesCapturesNoir = new ArrayList<>();

    // Icônes
    private ImageIcon icoRoiBlanc, icoRoiNoir, icoReineBlanc, icoReineNoir,
            icoTourBlanc, icoTourNoir, icoFouBlanc, icoFouNoir,
            icoCavalierBlanc, icoCavalierNoir, icoPionBlanc, icoPionNoir;

    public VueEchiquier(Plateau plateau) {
        this.plateau = plateau;
        this.sizeX = plateau.SIZE_X;
        this.sizeY = plateau.SIZE_Y;

        plateau.addObserver(this);
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

    private ImageIcon chargerIcone(String path) {
        ImageIcon icon = new ImageIcon(path);
        Image img = icon.getImage().getScaledInstance(pxCase, pyCase, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    private void placerLesComposantsGraphiques() {
        setTitle("Jeu d'Échecs");
        setResizable(false);
        setSize(sizeX * pxCase, sizeY * pyCase + 120);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Panel pour la grille de jeu
        JPanel grille = new JPanel(new GridLayout(sizeY, sizeX));
        tabJLabel = new JLabel[sizeX][sizeY];

        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                JLabel jlab = new JLabel();
                jlab.setOpaque(true);
                final int xx = x, yy = y;

                jlab.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        notifyCaseClicked(plateau.getCase(xx, yy));
                    }
                });

                Color color = (x + y) % 2 == 0 ? new Color(245, 245, 220) : new Color(200, 100, 110);
                jlab.setBackground(color);

                tabJLabel[x][y] = jlab;
                grille.add(jlab);
            }
        }

        // Panel pour les chronomètres et les noms
        JPanel panelChronos = new JPanel();
        panelChronos.setLayout(new FlowLayout(FlowLayout.CENTER));

        // Initialisation des chronomètres et noms
        labelChronoBlanc = new JLabel("Blanc: 05:00");
        labelChronoNoir = new JLabel("Noir: 05:00");
        labelTour = new JLabel("C'est au tour du joueur Blanc");

        panelChronos.add(labelChronoBlanc);
        panelChronos.add(labelChronoNoir);
        panelChronos.add(labelTour);

        // Panel pour les pièces capturées
        JPanel panelCaptures = new JPanel();
        panelCaptures.setLayout(new GridLayout(1, 2));

        // Label pour les pièces capturées
        JLabel labelCapturesBlanc = new JLabel("Captures Blanc : ");
        JLabel labelCapturesNoir = new JLabel("Captures Noir : ");
        panelCaptures.add(labelCapturesBlanc);
        panelCaptures.add(labelCapturesNoir);

        // Ajout des panels à la fenêtre
        this.add(grille, BorderLayout.CENTER);
        this.add(panelChronos, BorderLayout.NORTH);
        this.add(panelCaptures, BorderLayout.SOUTH);
    }

    public void setDeplacementsPossibles(List<Case> cases) {
        this.deplacementsPossibles = cases;
    }

    public void update(Observable o, Object arg) {
        mettreAJourAffichage();
    }

    public void mettreAJourAffichage() {
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                Case c = plateau.getCase(x, y);
                Piece piece = c.getPiece();
                JLabel label = tabJLabel[x][y];

                if (piece != null) {
                    String couleur = piece.getCouleur();
                    if (piece instanceof Roi)
                        label.setIcon(couleur.equals("blanc") ? icoRoiBlanc : icoRoiNoir);
                    else if (piece instanceof Reine)
                        label.setIcon(couleur.equals("blanc") ? icoReineBlanc : icoReineNoir);
                    else if (piece instanceof Tour)
                        label.setIcon(couleur.equals("blanc") ? icoTourBlanc : icoTourNoir);
                    else if (piece instanceof Fou)
                        label.setIcon(couleur.equals("blanc") ? icoFouBlanc : icoFouNoir);
                    else if (piece instanceof Cavalier)
                        label.setIcon(couleur.equals("blanc") ? icoCavalierBlanc : icoCavalierNoir);
                    else if (piece instanceof Pion)
                        label.setIcon(couleur.equals("blanc") ? icoPionBlanc : icoPionNoir);
                } else {
                    label.setIcon(null);
                }

                // coloration
                if (deplacementsPossibles.contains(c)) {
                    label.setBackground(new Color(255, 255, 153));
                } else {
                    label.setBackground((x + y) % 2 == 0 ? new Color(245, 245, 220) : new Color(200, 100, 110));
                }
            }
        }
        // Mise à jour des pièces capturées
        mettreAJourCaptures();
    }

    private void mettreAJourCaptures() {
        String capturesBlanc = "Captures Blanc: ";
        for (Piece piece : piecesCapturesBlanc) {
            capturesBlanc += piece.getNom() + " ";
        }

        String capturesNoir = "Captures Noir: ";
        for (Piece piece : piecesCapturesNoir) {
            capturesNoir += piece.getNom() + " ";
        }

        // Mettre à jour les labels de captures
        ((JLabel) ((JPanel) this.getContentPane().getComponent(2)).getComponent(0)).setText(capturesBlanc);
        ((JLabel) ((JPanel) this.getContentPane().getComponent(2)).getComponent(1)).setText(capturesNoir);
    }

    // Méthodes pour mettre à jour les chronomètres
    public static void miseAJourChronoBlanc(int temps) {
        int minutes = temps / 60;
        int secondes = temps % 60;
        String tempsFormatte = String.format("%02d:%02d", minutes, secondes);
        labelChronoBlanc.setText("Blanc: " + tempsFormatte);
    }

    public static void miseAJourChronoNoir(int temps) {
        int minutes = temps / 60;
        int secondes = temps % 60;
        String tempsFormatte = String.format("%02d:%02d", minutes, secondes);
        labelChronoNoir.setText("Noir: " + tempsFormatte);
    }

    public static void changerTour(String joueur) {
        labelTour.setText("C'est au tour du joueur " + joueur);
    }

    public void ajouterCapture(Piece piece) {
        if (piece.getCouleur().equals("blanc")) {
            piecesCapturesBlanc.add(piece);
        } else {
            piecesCapturesNoir.add(piece);
        }
    }
}
