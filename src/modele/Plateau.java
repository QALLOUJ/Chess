package modele;

import java.util.Observable;

public class Plateau extends Observable {
    public final int SIZE_X;
    public final int SIZE_Y;
    private final Case[][] cases; // Stocke les cases de l'échiquier

    public Plateau(int sizeX, int sizeY) {
        this.SIZE_X = sizeX;
        this.SIZE_Y = sizeY;
        cases = new Case[SIZE_X][SIZE_Y];

        // Initialisation des cases
        for (int x = 0; x < SIZE_X; x++) {
            for (int y = 0; y < SIZE_Y; y++) {
                cases[x][y] = new Case(x, y); // Assure-toi que la classe Case a un constructeur adapté
            }
        }
    }

    public Case[][] getCases() {
        return cases;
    }

    public Case getCase(int x, int y) {
        if (x >= 0 && x < SIZE_X && y >= 0 && y < SIZE_Y) {
            return cases[x][y];
        } else {
            throw new IndexOutOfBoundsException("Coordonnées hors limites : (" + x + ", " + y + ")");
        }
    }

    // Méthode pour signaler que l'état du modèle a changé
    public void setChanged() {
        super.setChanged(); // Appel à la méthode de la classe Observable
    }
}
