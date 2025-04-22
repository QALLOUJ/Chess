package modele;

import java.util.ArrayList;
import java.util.Observable;

public class Plateau extends Observable {
    public static int SIZE_X;
    public static int SIZE_Y;
    private final Case[][] cases;

    // Constructeur pour initialiser le plateau avec une taille
    public Plateau(int sizeX, int sizeY) {
        this.SIZE_X = sizeX;
        this.SIZE_Y = sizeY;
        cases = new Case[SIZE_X][SIZE_Y];

        for (int x = 0; x < SIZE_X; x++) {
            for (int y = 0; y < SIZE_Y; y++) {
                cases[x][y] = new Case(x, y, this);
            }
        }
        initialiserPartie();
    }

    // Initialiser les pièces pour une nouvelle partie
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

    // Méthode pour obtenir la case d'une coordonnée donnée
    public Case getCase(int x, int y) {
        if (x >= 0 && x < SIZE_X && y >= 0 && y < SIZE_Y) {
            return cases[x][y];
        } else {
            throw new IndexOutOfBoundsException("Coordonnées hors limites : (" + x + ", " + y + ")");
        }
    }
    public Case[][] getCases() { return cases; }

    // Méthode pour obtenir une case relative à une autre
    public Case getCaseRelative(Case source, int dx, int dy) {
        int newX = source.getX() + dx;
        int newY = source.getY() + dy;

        if (newX >= 0 && newX < SIZE_X && newY >= 0 && newY < SIZE_Y) {
            return getCase(newX, newY);
        } else {
            return null;
        }
    }

    // Récupérer la pièce d'une case donnée
    public Piece getPiece(Case c) {
        return c.getPiece();
    }

    // Méthode pour notifier un changement dans l'état du plateau
    public void notifierChangement() {
        setChanged();
        notifyObservers();
    }

    // 1. Trouver le roi d'une couleur
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

    // 2. Vérifier si le roi est en échec
    public boolean estEnEchec(String couleur) {
        Case caseRoi = getCaseRoi(couleur);
        if (caseRoi == null) return false;

        for (int x = 0; x < SIZE_X; x++) {
            for (int y = 0; y < SIZE_Y; y++) {
                Piece piece = cases[x][y].getPiece();
                if (piece != null && !piece.getCouleur().equals(couleur)) {
                    if (piece.peutDeplacer(cases[x][y], caseRoi)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // 3. Vérifier s’il y a échec et mat
    public boolean estEchecEtMat(String couleur) {
        if (!estEnEchec(couleur)) return false;

        // tester tous les coups possibles
        for (int x1 = 0; x1 < SIZE_X; x1++) {
            for (int y1 = 0; y1 < SIZE_Y; y1++) {
                Piece piece = cases[x1][y1].getPiece();
                if (piece != null && piece.getCouleur().equals(couleur)) {
                    Case source = cases[x1][y1];
                    ArrayList<Case> destinations = piece.dec.getMesCA(); // positions accessibles
                    for (Case arrivee : destinations) {
                        Piece sauvegarde = arrivee.getPiece();

                        // simulation du déplacement
                        arrivee.setPiece(piece);
                        source.setPiece(null);
                        piece.setCase(arrivee);

                        boolean toujoursEnEchec = estEnEchec(couleur);

                        // annuler le déplacement
                        source.setPiece(piece);
                        arrivee.setPiece(sauvegarde);
                        piece.setCase(source);

                        if (!toujoursEnEchec) {
                            return false; // il existe au moins un coup pour sortir d’échec
                        }
                    }
                }
            }
        }
        return true; // aucun coup possible pour sortir d’échec
    }
}
