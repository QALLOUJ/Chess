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
        SIZE_X = SIZE_X;
        SIZE_Y = SIZE_Y;
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

    public List<Coup> getTousLesCoupsPossiblesPour(String couleur) {
        List<Coup> coups = new ArrayList<>();

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Case c = cases[x][y];
                Piece p = c.getPiece();
                if (p != null && p.getCouleur().equals(couleur)) {
                    for (Case cible : p.getCasesAccessibles()) {
                        if (p.peutDeplacer(c, cible)) {
                            coups.add(new Coup(p, c, cible, cible.getPiece()));
                        }
                    }
                }
            }
        }
        return coups;
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
}
