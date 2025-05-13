package Vue;

import modele.*;
import modele.Pieces.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

public class VueEchiquier extends JFrame implements Observer {
    private final Plateau plateau;
    private JLabel[][] tabJLabel;
    private final int pxCase = 50, pyCase = 50;
    private final int sizeX, sizeY;
    private static JLabel labelChronoBlanc;
    private static JLabel labelChronoNoir;
    private static JLabel labelTour;
    private final List<CaseClickListener> listeners = new ArrayList<>();
    private JPanel panelCapturesBlanc;
    private JPanel panelCapturesNoir;

    private List<Case> deplacementsPossibles = new ArrayList<>();
    private final List<Piece> piecesCapturesBlanc = new ArrayList<>();
    private final List<Piece> piecesCapturesNoir = new ArrayList<>();

    // Icônes
    private ImageIcon icoRoiBlanc, icoRoiNoir, icoReineBlanc, icoReineNoir,
            icoTourBlanc, icoTourNoir, icoFouBlanc, icoFouNoir,
            icoCavalierBlanc, icoCavalierNoir, icoPionBlanc, icoPionNoir;

    public VueEchiquier(Plateau plateau) {
        this.plateau = plateau;
        this.sizeX = Plateau.SIZE_X;
        this.sizeY = Plateau.SIZE_Y;
        panelCapturesBlanc = new JPanel();
        panelCapturesNoir = new JPanel();

        // Pour une meilleure organisation
        panelCapturesBlanc.setLayout(new FlowLayout(FlowLayout.LEFT));
        panelCapturesNoir.setLayout(new FlowLayout(FlowLayout.LEFT));

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

    private ImageIcon getIconPourPiece(Piece piece) {
        if (piece == null) {
            return null;
        }

        if (piece instanceof Roi) {
            return piece.getCouleur().equals("blanc") ? icoRoiBlanc : icoRoiNoir;
        } else if (piece instanceof Reine) {
            return piece.getCouleur().equals("blanc") ? icoReineBlanc : icoReineNoir;
        } else if (piece instanceof Tour) {
            return piece.getCouleur().equals("blanc") ? icoTourBlanc : icoTourNoir;
        } else if (piece instanceof Fou) {
            return piece.getCouleur().equals("blanc") ? icoFouBlanc : icoFouNoir;
        } else if (piece instanceof Cavalier) {
            return piece.getCouleur().equals("blanc") ? icoCavalierBlanc : icoCavalierNoir;
        } else if (piece instanceof Pion) {
            return piece.getCouleur().equals("blanc") ? icoPionBlanc : icoPionNoir;
        } else {
            return null;
        }
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

        // Ajout des panels de captures
        panelCaptures.add(panelCapturesBlanc);
        panelCaptures.add(panelCapturesNoir);
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
                    if (piece instanceof Roi) {
                        label.setIcon(couleur.equals("blanc") ? icoRoiBlanc : icoRoiNoir);
                    } else if (piece instanceof Reine) {
                        label.setIcon(couleur.equals("blanc") ? icoReineBlanc : icoReineNoir);
                    } else if (piece instanceof Tour) {
                        label.setIcon(couleur.equals("blanc") ? icoTourBlanc : icoTourNoir);
                    } else if (piece instanceof Fou) {
                        label.setIcon(couleur.equals("blanc") ? icoFouBlanc : icoFouNoir);
                    } else if (piece instanceof Cavalier) {
                        label.setIcon(couleur.equals("blanc") ? icoCavalierBlanc : icoCavalierNoir);
                    } else if (piece instanceof Pion) {
                        label.setIcon(couleur.equals("blanc") ? icoPionBlanc : icoPionNoir);
                    }
                } else {
                    label.setIcon(null);
                }

                // Coloration de la case
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
        StringBuilder capturesBlanc = new StringBuilder("Captures Blanc: ");
        for (Piece piece : piecesCapturesBlanc) {
            capturesBlanc.append(piece.getNom()).append(" ");
        }

        StringBuilder capturesNoir = new StringBuilder("Captures Noir: ");
        for (Piece piece : piecesCapturesNoir) {
            capturesNoir.append(piece.getNom()).append(" ");
        }

        // Mettre à jour les labels de captures
        ((JLabel) ((JPanel) this.getContentPane().getComponent(2)).getComponent(0)).setText(capturesBlanc.toString());
        ((JLabel) ((JPanel) this.getContentPane().getComponent(2)).getComponent(1)).setText(capturesNoir.toString());
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
        // Utilise la méthode pour récupérer l'icône de la pièce capturée
        ImageIcon icone = getIconPourPiece(piece);

        // Crée un JLabel avec l'icône récupérée
        JLabel labelIcone = new JLabel(icone);
        labelIcone.setPreferredSize(new Dimension(32, 32)); // Ajuste la taille de l'icône capturée

        // Ajoute le label à la bonne liste de captures en fonction de la couleur de la pièce
        if (piece.getCouleur().equals("blanc")) {
            panelCapturesNoir.add(labelIcone); // Pièce blanche capturée => joueur noir a capturé
        } else {
            panelCapturesBlanc.add(labelIcone); // Pièce noire capturée => joueur blanc a capturé
        }

        // Rafraîchit la vue
        revalidate();
        repaint();
    }
}
