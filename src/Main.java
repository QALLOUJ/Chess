import controleur.Control;
import modele.Jeu;
import modele.Plateau;

public class Main {

    public static void main(String[] args) {

        Plateau plateau = new Plateau(8, 8);


        Jeu jeu = new Jeu(plateau);


        Control controleur = new Control(jeu);

        
        controleur.setVisible(true);
    }
}
