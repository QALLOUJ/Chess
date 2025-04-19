package controleur;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Observable;
import java.util.Observer;
import javax.swing.*;


import modele.*;


/** Cette classe a deux fonctions :
 *  (1) Vue : proposer une représentation graphique de l'application (cases graphiques, etc.)
 *  (2) Controleur : écouter les évènements clavier et déclencher le traitement adapté sur le modèle (clic position départ -> position arrivée pièce))
 *
 */
public class Control extends JFrame implements Observer, javax.naming.ldap.Control {
    private Plateau plateau; // référence sur une classe de modèle : permet d'accéder aux données du modèle pour le rafraichissement, permet de communiquer les actions clavier (ou souris)
    private Jeu jeu;
    private final int sizeX; // taille de la grille affichée
    private final int sizeY;
    private static final int pxCase = 50; // nombre de pixel par case
    // icones affichées dans la grille
    private ImageIcon icoRoiBlanc, icoRoiNoir;
    private ImageIcon icoReineBlanc, icoReineNoir;
    private ImageIcon icoTourBlanc, icoTourNoir;
    private ImageIcon icoFouBlanc, icoFouNoir;
    private ImageIcon icoCavalierBlanc, icoCavalierNoir;
    private ImageIcon icoPionBlanc, icoPionNoir;



    private Case caseClic1; // mémorisation des cases cliquées
    private Case caseClic2;


    private JLabel[][] tabJLabel; // cases graphique (au moment du rafraichissement, chaque case va être associée à une icône, suivant ce qui est présent dans le modèle)


    public Control(Jeu _jeu) {
        jeu = _jeu;
        plateau = jeu.getPlateau();
        sizeX = plateau.SIZE_X;
        sizeY = plateau.SIZE_Y;



        chargerLesIcones();
        placerLesComposantsGraphiques();

        plateau.addObserver(this);

        mettreAJourAffichage();

    }
    private Case selection = null;

    public void caseCliquee(Case c) {
        // Vérifier si aucune pièce n'est actuellement sélectionnée (premier clic)
        if (selection == null) {
            // 1er clic : on sélectionne la pièce
            Piece piece = jeu.getPlateau().getPiece(c);

            // Vérifier si la case contient une pièce et si la couleur de la pièce correspond à celle du joueur courant
            if (piece != null && piece.getCouleur().equals(jeu.getJoueurCourant().getCouleur())) {
                selection = c;
                System.out.println("Pièce sélectionnée : " + piece);
            }
        } else {
            // 2e clic : tentative de déplacement
            Piece pieceSelectionnee = jeu.getPlateau().getPiece(selection);
            Piece pieceCible = jeu.getPlateau().getPiece(c);

            // Vérifier si le déplacement est valide
            if (jeu.demandeDeplacementPiece(selection, c)) {
                System.out.println("Déplacement réussi : " + pieceSelectionnee + " de " + selection + " à " + c);
            } else {
                System.out.println("Déplacement invalide.");
            }

            // Réinitialiser la sélection après un déplacement
            selection = null;
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


    private ImageIcon chargerIcone(String urlIcone) {
        BufferedImage image = null;

        ImageIcon icon = new ImageIcon(urlIcone);

        // Redimensionner l'icône
        Image img = icon.getImage().getScaledInstance(pxCase, pxCase, Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(img);

        return resizedIcon;
    }


    private void placerLesComposantsGraphiques() {
        setTitle("Jeu d'Échecs");
        setResizable(false);
        setSize(sizeX * pxCase, sizeX * pxCase);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // permet de terminer l'application à la fermeture de la fenêtre

        JComponent grilleJLabels = new JPanel(new GridLayout(sizeY, sizeX)); // grilleJLabels va contenir les cases graphiques et les positionner sous la forme d'une grille


        tabJLabel = new JLabel[sizeX][sizeY];

        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                JLabel jlab = new JLabel();

                tabJLabel[x][y] = jlab; // on conserve les cases graphiques dans tabJLabel pour avoir un accès pratique à celles-ci (voir mettreAJourAffichage() )

                final int xx = x; // permet de compiler la classe anonyme ci-dessous
                final int yy = y;
                // écouteur de clics
                jlab.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (caseClic1 == null) {
                            // Sélectionner la pièce à déplacer
                            caseClic1 = plateau.getCases()[xx][yy];

                            // Vérifier que la pièce appartient au joueur actif
                            if (caseClic1.getPiece() != null && caseClic1.getPiece().getCouleur().equals(jeu.getJoueurCourant().getCouleur())){


                            // La pièce appartient au joueur actif, donc on permet la sélection
                            } else {
                                caseClic1 = null; // Réinitialiser si la case n'a pas de pièce ou appartient à l'adversaire
                            }
                        } else {
                            // Déplacer la pièce si elle est valide
                            caseClic2 = plateau.getCases()[xx][yy];

                            // Vérifier si le déplacement est valide pour la pièce
                            if (caseClic1 != caseClic2 && caseClic1.getPiece().peutDeplacer(caseClic1, caseClic2)) {
                                // Effectuer le déplacement
                                jeu.demandeDeplacementPiece(caseClic1, caseClic2);


                                // Réinitialiser les cases sélectionnées
                                caseClic1 = null;
                                caseClic2 = null;
                            }
                        }
                    }
                });


                jlab.setOpaque(true);

                if ((y%2 == 0 && x%2 == 0) || (y%2 != 0 && x%2 != 0)) {
                    tabJLabel[x][y].setBackground(new Color(50, 50, 110));
                } else {
                    tabJLabel[x][y].setBackground(new Color(150, 150, 210));
                }

                grilleJLabels.add(jlab);
            }
        }
        add(grilleJLabels);
    }


    /**
     * Il y a une grille du côté du modèle ( jeu.getGrille() ) et une grille du côté de la vue (tabJLabel)
     */
    private void mettreAJourAffichage() {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Case c = plateau.getCase(x, y);
                Piece piece = c.getPiece();

                if (piece != null) {
                    String couleur = piece.getCouleur();

                    if (piece instanceof Roi) {
                        tabJLabel[x][y].setIcon(couleur.equals("blanc") ? icoRoiBlanc : icoRoiNoir);
                    } else if (piece instanceof Reine) {
                        tabJLabel[x][y].setIcon(couleur.equals("blanc") ? icoReineBlanc : icoReineNoir);
                    } else if (piece instanceof Tour) {
                        tabJLabel[x][y].setIcon(couleur.equals("blanc") ? icoTourBlanc : icoTourNoir);
                    } else if (piece instanceof Fou) {
                        tabJLabel[x][y].setIcon(couleur.equals("blanc") ? icoFouBlanc : icoFouNoir);
                    } else if (piece instanceof Cavalier) {
                        tabJLabel[x][y].setIcon(couleur.equals("blanc") ? icoCavalierBlanc : icoCavalierNoir);
                    } else if (piece instanceof Pion) {
                        tabJLabel[x][y].setIcon(couleur.equals("blanc") ? icoPionBlanc : icoPionNoir);
                    }
                } else {
                    tabJLabel[x][y].setIcon(null);
                }
            }
        }
    }


    @Override
    public void update(Observable o, Object arg) {
        mettreAJourAffichage();
        /*

        // récupérer le processus graphique pour rafraichir
        // (normalement, à l'inverse, a l'appel du modèle depuis le contrôleur, utiliser un autre processus, voir classe Executor)


        SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        mettreAJourAffichage();
                    }
                }); 
        */

    }

    @Override
    public String getID() {
        return "";
    }

    @Override
    public boolean isCritical() {
        return false;
    }

    @Override
    public byte[] getEncodedValue() {
        return new byte[0];
    }
}
