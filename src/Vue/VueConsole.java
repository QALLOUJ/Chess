package Vue;

import java.util.*;

public class VueConsole implements InterfaceUtilisateur {
    private final Scanner scanner;

    public VueConsole(Scanner scanner) {
        this.scanner = scanner;
        afficherBienvenue();
    }

    private void afficherBienvenue() {
        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║            Bienvenue au jeu d'échecs           ║");
        System.out.println("╠════════════════════════════════════════════════╣");
        System.out.println("║ Commandes :                                    ║");
        System.out.println("║  - Entrez un coup au format : e2 e4            ║");
        System.out.println("║  - Tapez 'aide' à tout moment pour les règles  ║");
        System.out.println("║  - Tapez 'quitter' pour arrêter la partie      ║");
        System.out.println("╚════════════════════════════════════════════════╝");
        System.out.println();
    }

    @Override
    public void afficherMessage(String message) {
        System.out.println("\n>> " + message + "\n");
    }

    @Override
    public void afficherPlateau(String texte) {
        System.out.println(texte);
    }

    @Override
    public void miseAJourChrono(String couleur, int temps) {
        // Ne rien faire en mode console : suppression de l'affichage du chrono
    }


    @Override
    public String demanderCoup() {
        while (true) {
            System.out.print("Entrez votre coup (ex : e2 e4) ou 'aide' : ");
            String input = scanner.nextLine().trim().toLowerCase();

            if (input.equals("aide")) {
                afficherAide();
            } else if (input.equals("quitter")) {
                afficherMessage("Merci d'avoir joué. À bientôt !");
                System.exit(0);
            } else if (input.matches("^[a-h][1-8] [a-h][1-8]$")) {
                return input;
            } else {
                afficherMessage("Format invalide. Essayez : e2 e4");
            }
        }
    }

    @Override
    public String demanderChoixPromotion(String[] options) {
        System.out.println("Promotion : choisissez une pièce parmi :");
        for (String opt : options) {
            System.out.println("- " + opt);
        }
        String choix;
        do {
            System.out.print("Votre choix : ");
            choix = scanner.nextLine().trim().toUpperCase();
        } while (!List.of(options).contains(choix));
        return choix;
    }

    private void afficherAide() {
        System.out.println("\n=== AIDE ===");
        System.out.println("Format des coups : e2 e4 déplace une pièce de e2 vers e4.");
        System.out.println("Commandes :");
        System.out.println("  - 'aide' : afficher ce message");
        System.out.println("  - 'quitter' : quitter la partie");
        System.out.println("\nRègles simplifiées :");
        System.out.println("  - Le but est de mettre le roi adverse en échec et mat.");
        System.out.println("  - Vous ne pouvez pas jouer un coup qui met votre roi en échec.");
        System.out.println("  - Promotion : un pion atteignant la dernière rangée est promu.");
        System.out.println("=====================\n");
    }
}
