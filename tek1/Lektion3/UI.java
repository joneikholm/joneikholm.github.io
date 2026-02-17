import javax.swing.*;
import java.awt.*;

public class UI {
    public static void main(String[] args) {
        System.out.println("hej");
        JFrame frame = new JFrame("Movies");
        frame.setSize(800,400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        // Liste
        DefaultListModel<String> listModel = new DefaultListModel<>();
        listModel.addElement("Dune");
        listModel.addElement("Inception");
        JList<String> movieList = new JList<>(listModel);

        JPanel panel = new JPanel();
        JButton pressMe = new JButton("Tilføj film");
        JTextField tekst = new JTextField(45);
        pressMe.addActionListener(e ->{
            System.out.println("indsæt ny film..." + e.toString());
        });
        panel.add(pressMe);
        panel.add(tekst);
        panel.add(movieList);
        frame.add(panel, BorderLayout.NORTH);
        frame.setVisible(true);
    }
}
