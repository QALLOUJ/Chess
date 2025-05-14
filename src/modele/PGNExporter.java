package modele;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class PGNExporter {

    public static void exporter(Jeu jeu, List<Coup> historique, String nomFichier, String resultat) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomFichier))) {

            // Écriture de l'en-tête PGN
            writer.write("[Event \"Partie d'échecs\"]\n");
            writer.write("[Site \"Local\"]\n");
            writer.write("[Date \"" + LocalDate.now() + "\"]\n");
            writer.write("[White \"" + jeu.getJoueurBlanc().getNom() + "\"]\n");
            writer.write("[Black \"" + jeu.getJoueurNoir().getNom() + "\"]\n");
            writer.write("[Result \"" + resultat + "\"]\n\n");

            // Écriture des coups
            int numCoup = 1;
            for (int i = 0; i < historique.size(); i++) {
                if (i % 2 == 0) {
                    writer.write(numCoup + ". ");
                    numCoup++;
                }

                Coup coup = historique.get(i);
                String notation = genererNotation(coup);
                writer.write(notation + " ");
            }

            writer.write(resultat + "\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String genererNotation(Coup coup) {
        Piece piece = coup.getPieceJouee();
        String nom = piece.getNom();

        // Notation PGN standard : K (roi), Q (reine), R (tour), B (fou), N (cavalier)
        String prefixe = switch (nom) {
            case "Roi" -> "K";
            case "Dame" -> "Q";
            case "Tour" -> "R";
            case "Fou" -> "B";
            case "Cavalier" -> "N";
            default -> ""; // pion = pas de lettre
        };

        String colonneDepart = String.valueOf((char) ('a' + coup.getCaseDepart().getX()));
        String ligneDepart = String.valueOf(8 - coup.getCaseDepart().getY());
        String colonneArrivee = String.valueOf((char) ('a' + coup.getCaseArrivee().getX()));
        String ligneArrivee = String.valueOf(8 - coup.getCaseArrivee().getY());

        String capture = coup.getPieceCapturee() != null ? "x" : "";

        // Gestion des coups spéciaux (roque, promotion, en passant)
        if (coup.getPieceCapturee() != null && coup.getPieceJouee().getNom().equals("Pion")) {
            capture = "x";
        }

        // Cas pour la promotion (ex. "e8=Q")
        if (piece.getNom().equals("Pion") && (ligneArrivee.equals("1") || ligneArrivee.equals("8"))) {
            return colonneDepart + capture + colonneArrivee + ligneArrivee + "=" + "Q"; // Promotion à Dame par défaut
        }

        // Roque
        if (piece.getNom().equals("Roi") && Math.abs(coup.getCaseDepart().getX() - coup.getCaseArrivee().getX()) > 1) {
            return "O-O"; // Petit roque
        }
        if (piece.getNom().equals("Roi") && Math.abs(coup.getCaseDepart().getX() - coup.getCaseArrivee().getX()) < -1) {
            return "O-O-O"; // Grand roque
        }

        // En passant (par exemple, "exd6")
        if (capture.equals("x") && coup.getPieceCapturee() != null) {
            return colonneDepart + capture + colonneArrivee + ligneArrivee;
        }

        return prefixe + (prefixe.equals("") && capture.equals("x") ? colonneDepart : "")
                + capture + colonneArrivee + ligneArrivee;
    }
}
