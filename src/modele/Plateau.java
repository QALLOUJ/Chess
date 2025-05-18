package modele;

import modele.Pieces.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class Plateau extends Observable {
    public static int SIZE_X;
    public static int SIZE_Y;
    private final Case[][] cases;

    public Plateau(Plateau original) {
        SIZE_X = 8;
        SIZE_Y = 8;
        cases = new Case[SIZE_X][SIZE_Y];

        for (int x = 0; x < SIZE_X; x++) {
            for (int y = 0; y < SIZE_Y; y++) {
                cases[x][y] = new Case(x, y, this);
                Piece originalPiece = original.cases[x][y].getPiece();
                if (originalPiece != null) {
                    cases[x][y].setPiece(originalPiece.clone());
                }
            }
        }
    }

    public Plateau(int sizeX, int sizeY) {
        SIZE_X = sizeX;
        SIZE_Y = sizeY;
        cases = new Case[SIZE_X][SIZE_Y];

        for (int x = 0; x < SIZE_X; x++) {
            for (int y = 0; y < SIZE_Y; y++) {
                cases[x][y] = new Case(x, y, this);
            }
        }
        initialiserPartie();
    }

    public void initialiserPartie() {
        for (int i = 0; i < SIZE_X; i++) {
            cases[i][1].setPiece(new Pion("noir"));
            cases[i][6].setPiece(new Pion("blanc"));
        }

        cases[0][0].setPiece(new Tour("noir"));
        cases[1][0].setPiece(new Cavalier("noir"));
        cases[2][0].setPiece(new Fou("noir"));
        cases[3][0].setPiece(new Reine("noir"));
        cases[4][0].setPiece(new Roi("noir"));
        cases[5][0].setPiece(new Fou("noir"));
        cases[6][0].setPiece(new Cavalier("noir"));
        cases[7][0].setPiece(new Tour("noir"));

        cases[0][7].setPiece(new Tour("blanc"));
        cases[1][7].setPiece(new Cavalier("blanc"));
        cases[2][7].setPiece(new Fou("blanc"));
        cases[3][7].setPiece(new Reine("blanc"));
        cases[4][7].setPiece(new Roi("blanc"));
        cases[5][7].setPiece(new Fou("blanc"));
        cases[6][7].setPiece(new Cavalier("blanc"));
        cases[7][7].setPiece(new Tour("blanc"));
    }

    public Case getCase(int x, int y) {
        if (x >= 0 && x < SIZE_X && y >= 0 && y < SIZE_Y) {
            return cases[x][y];
        } else {
            throw new IndexOutOfBoundsException("Coordonnées hors limites : (" + x + ", " + y + ")");
        }
    }

    public Case[][] getCases() {
        return cases;
    }

    public Case getCaseRelative(Case source, int dx, int dy) {
        int newX = source.getX() + dx;
        int newY = source.getY() + dy;

        if (newX >= 0 && newX < SIZE_X && newY >= 0 && newY < SIZE_Y) {
            return getCase(newX, newY);
        } else {
            return null;
        }
    }

    public Piece getPiece(Case c) {
        return c.getPiece();
    }

    public void notifierChangement() {
        setChanged();
        notifyObservers();
    }

    public Case getCaseRoi(String couleur) {
        for (int x = 0; x < SIZE_X; x++) {
            for (int y = 0; y < SIZE_Y; y++) {
                Piece piece = cases[x][y].getPiece();
                if (piece instanceof Roi && piece.getCouleur().equals(couleur)) {
                    return cases[x][y];
                }
            }
        }
        return null;
    }

    private Jeu jeu;

    public void setJeu(Jeu jeu) {
        this.jeu = jeu;
    }

    public Jeu getJeu() {
        return this.jeu;
    }

    public boolean estPat(String couleur) {
        if (estEnEchec(couleur)) return false;

        for (int x = 0; x < SIZE_X; x++) {
            for (int y = 0; y < SIZE_Y; y++) {
                Piece piece = cases[x][y].getPiece();
                if (piece != null && piece.getCouleur().equals(couleur) && piece.dec != null) {
                    Case source = cases[x][y];
                    ArrayList<Case> destinations = piece.dec.getMesCA();
                    for (Case cible : destinations) {
                        if (cible == null) continue;

                        Piece pieceCapturee = cible.getPiece();
                        Case ancienneCase = piece.getCase();

                        cible.setPiece(piece);
                        source.setPiece(null);
                        piece.setCase(cible);

                        boolean roiEnEchec = estEnEchec(couleur);

                        source.setPiece(piece);
                        cible.setPiece(pieceCapturee);
                        piece.setCase(ancienneCase);

                        if (!roiEnEchec) return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean estEnEchec(String couleur) {
        Case caseRoi = getCaseRoi(couleur);
        if (caseRoi == null) return false;

        for (int x = 0; x < SIZE_X; x++) {
            for (int y = 0; y < SIZE_Y; y++) {
                Piece piece = cases[x][y].getPiece();
                if (piece != null && !piece.getCouleur().equals(couleur)) {
                    if (piece.peutAttaquer(cases[x][y], caseRoi)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean estEchecEtMat(String couleur) {
        if (!estEnEchec(couleur)) {
            return false;
        }

        for (int x = 0; x < SIZE_X; x++) {
            for (int y = 0; y < SIZE_Y; y++) {
                Piece piece = cases[x][y].getPiece();

                if (piece != null && piece.getCouleur().equals(couleur) && piece.dec != null) {
                    Case source = cases[x][y];
                    ArrayList<Case> destinations = piece.dec.getMesCA();

                    for (Case cible : destinations) {
                        if (cible == null) continue;

                        // Sauvegarde de l'état du plateau
                        Piece pieceCapturee = cible.getPiece();
                        Case ancienneCase = piece.getCase();

                        // Simulation du coup
                        cible.setPiece(piece);
                        source.setPiece(null);
                        piece.setCase(cible);

                        // Vérifier si roi est en échec après ce coup
                        boolean roiEnEchec = estEnEchec(couleur);

                        // Annuler la simulation (restauration)
                        source.setPiece(piece);
                        cible.setPiece(pieceCapturee);
                        piece.setCase(ancienneCase);

                        if (!roiEnEchec) {
                            return false; // Il y a un coup légal possible => pas échec et mat
                        }
                    }
                }
            }
        }

        return true; // aucun coup légal trouvé, donc échec et mat
    }


    public List<Coup> getTousLesCoupsLegauxPour(String couleur) {
        List<Coup> coupsLegaux = new ArrayList<>();

        for (int x = 0; x < SIZE_X; x++) {
            for (int y = 0; y < SIZE_Y; y++) {
                Case source = cases[x][y];
                Piece p = source.getPiece();

                if (p != null && p.getCouleur().equals(couleur)) {
                    for (Case cible : p.getCasesAccessibles()) {
                        if (p.peutDeplacer(source, cible)) {
                            // Simuler le coup
                            Piece pieceCapturee = cible.getPiece();
                            cible.setPiece(p);
                            source.setPiece(null);
                            p.setCase(cible);

                            boolean roiEnEchec = estEnEchec(couleur);

                            // Revenir en arrière
                            source.setPiece(p);
                            cible.setPiece(pieceCapturee);
                            p.setCase(source);

                            if (!roiEnEchec) {
                                coupsLegaux.add(new Coup(p, source, cible, pieceCapturee));
                            }
                        }
                    }
                }
            }
        }

        return coupsLegaux;
    }


    public void appliquerCoup(Coup c) {
        Case depart = c.getCaseDepart();
        Case arrivee = c.getCaseArrivee();
        Piece piece = getCase(depart.getX(), depart.getY()).getPiece();
        if (piece != null) {
            getCase(arrivee.getX(), arrivee.getY()).setPiece(piece);
            getCase(depart.getX(), depart.getY()).setPiece(null);
            piece.setCase(arrivee); // Assure-toi que setCase existe bien dans la classe Piece
        }
    }

    public void reset() {
        // Réinitialise toutes les cases sans pièce
        for (int x = 0; x < SIZE_X; x++) {
            for (int y = 0; y < SIZE_Y; y++) {
                cases[x][y].setPiece(null);
            }
        }

        // Réinitialise les pièces comme au début
        initialiserPartie();

        // Notifie les observateurs d'un changement
        notifierChangement();

    }
    public void setPiece(Piece piece, int ligne, int colonne) {
        Case c = cases[ligne][colonne];
        c.setPiece(piece);
        piece.setCase(c);
    }
    public void reinitialiser() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                cases[i][j].setPiece(null);
            }
        }
    }

    // ♟️ test roque (petit et grand, blanc et noir)
    public void initialiserRoqueTest() {
        reinitialiser();

        // position de départ des blancs
        setPiece(new Roi("blanc"), 4, 7);
        setPiece(new Tour("blanc"), 3, 3);
        setPiece(new Tour("blanc"), 7, 7);

        // position de départ des noirs
        setPiece(new Roi("noir"), 4,0);
        setPiece(new Tour("noir"), 0, 0);
        setPiece(new Tour("noir"), 3, 2);

        // vider les cases entre roi et tours pour permettre le roque
        for (int col = 1; col < 4; col++) {
            cases[7][col].setPiece(null);  // cases b1, c1, d1 libres
            cases[0][col].setPiece(null);  // cases b8, c8, d8 libres
        }
        for (int col = 5; col < 7; col++) {
            cases[7][col].setPiece(null);  // cases f1, g1 libres
            cases[0][col].setPiece(null);  // cases f8, g8 libres
        }
    }

    public void initialiserMiseEnPassantTest() {
        reinitialiser();

        // position initiale :
        // pion blanc en e5 → ligne 4, colonne 4
        setPiece(new Pion("blanc"), 4, 4);

        // pion noir en f7 → ligne 6, colonne 5
        setPiece(new Pion("noir"), 6, 5);

        // simuler déplacement noir de f7 → f5 (ligne 6 → ligne 4)
        Piece pionNoir = cases[6][5].getPiece();
        cases[6][5].setPiece(null);  // retirer de f7
        cases[4][5].setPiece(pionNoir);  // placer en f5
        pionNoir.setCase(cases[4][5]);

        // marquer ce pion comme ayant fait un double pas (si ton code a une variable pour ça)
        pionNoir.setVientDeFaireDoublePas(true); // à toi de définir ça selon ton implémentation

        // maintenant : le pion blanc peut faire la prise en passant sur f6 (ligne 5, colonne 5)
    }





    //test promotion
    public void initialiserPromotionTest() {
        reinitialiser();
        setPiece(new Pion("blanc"), 2, 2); // a7, va en a8 pour promotion
    }


    public void initialiserEchecTest() {
        reinitialiser();

        setPiece(new Roi("noir"), 0, 4);
        setPiece(new Reine("blanc"), 1, 4); // met le roi noir en échec
    }


    public void initialiserMatTest() {
        reinitialiser();

        setPiece(new Roi("noir"), 0, 0);
        setPiece(new Reine("blanc"), 1, 1);
        setPiece(new Roi("blanc"), 7, 7);
    }



}
