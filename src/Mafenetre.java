import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Mafenetre extends JFrame {
    public Mafenetre() {
        build();
    }

    void build() {
        JPanel jp = new JPanel(new GridLayout(8, 8));
        setContentPane(jp);

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                JButton caseBouton = new JButton();
                caseBouton.setOpaque(true);
                caseBouton.setBorderPainted(false);

                if ((i + j) % 2 == 0) {
                    caseBouton.setBackground(Color.WHITE); // cases blanches
                } else {
                    caseBouton.setBackground(Color.GRAY); // cases noires
                }

                // ajouter un écouteur de souris
                caseBouton.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        caseBouton.setBackground(Color.RED); // devient rouge au clic
                    }
                });

                jp.add(caseBouton);
            }
        }

        setTitle("Échiquier");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Mafenetre::new);
    }
}
