package bank;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

// =========================================================
// OPGAVE 2: Overfør penge (atomicitet)
// =========================================================
// 1. Kør appen og overfør 500 kr fra Alice til Bob. Det virker.
// 2. Klik "Nulstil konti".
// 3. Fjern kommentaren fra "throw"-linjen i transfer().
// 4. Overfør 500 kr fra Alice til Bob igen.
// 5. Alice har mistet 500 kr, men Bob har IKKE fået dem!
//    Pengene er forsvundet ud af systemet.
//
// DIN OPGAVE: Wrap begge UPDATEs i en transaktion,
// så enten sker BEGGE – eller INGEN af dem.
// Brug: setAutoCommit(false), commit(), rollback(), setAutoCommit(true)
// =========================================================

public class Step2Transfer {
    static Connection connection;
    static DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Navn", "Saldo"}, 0);

    static {
        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/bankdb", "root", "password");
            System.out.println("Forbundet til databasen");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Opgave 2: Overfør penge");
        frame.setSize(500, 350);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(5, 5));

        loadAccounts();
        frame.add(new JScrollPane(new JTable(model)), BorderLayout.CENTER);

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Overfør penge"));

        JComboBox<Account> fromBox = new JComboBox<>();
        JComboBox<Account> toBox = new JComboBox<>();
        JTextField amountField = new JTextField(8);
        JButton transferBtn = new JButton("Overfør");
        JButton resetBtn = new JButton("Nulstil konti");

        transferBtn.addActionListener(e -> {
            Account from = (Account) fromBox.getSelectedItem();
            Account to = (Account) toBox.getSelectedItem();
            int amount = Integer.parseInt(amountField.getText());
            transfer(from.id, to.id, amount);
        });

        resetBtn.addActionListener(e -> resetAccounts());

        panel.add(new JLabel("Fra:"));
        panel.add(fromBox);
        panel.add(new JLabel("Til:"));
        panel.add(toBox);
        panel.add(new JLabel("Beløb:"));
        panel.add(amountField);
        panel.add(transferBtn);
        panel.add(resetBtn);

        frame.add(panel, BorderLayout.SOUTH);

        loadAccountBox(fromBox);
        loadAccountBox(toBox);
        // Vælg Bob som default i "Til"-boksen
        if (toBox.getItemCount() > 1) toBox.setSelectedIndex(1);
        frame.setVisible(true);
    }

    // =========================================================
    // RET DENNE METODE
    // =========================================================
    static void transfer(int fromId, int toId, int amount) {
        try (Statement stmt = connection.createStatement()) {

            stmt.executeUpdate(
                    "UPDATE account SET saldo = saldo - " + amount
                            + " WHERE id = " + fromId);
            System.out.println("Trukket " + amount + " fra konto " + fromId);

            // Fjern kommentaren herunder for at simulere en fejl:
            if (true) throw new RuntimeException("Strømmen gik!");

            stmt.executeUpdate(
                    "UPDATE account SET saldo = saldo + " + amount
                            + " WHERE id = " + toId);
            System.out.println("Lagt " + amount + " til konto " + toId);

        } catch (Exception e) {
            System.out.println("FEJL: " + e.getMessage());
        }
        loadAccounts();
    }



    // =========================================================
    // HJÆLPEMETODER (rør ikke disse)
    // =========================================================

    static void loadAccounts() {
        model.setRowCount(0);
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM account")) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("saldo")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void loadAccountBox(JComboBox<Account> box) {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM account")) {
            while (rs.next()) {
                box.addItem(new Account(rs.getInt("id"), rs.getString("name")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void resetAccounts() {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("UPDATE account SET saldo = 1000");
        } catch (Exception e) {
            e.printStackTrace();
        }
        loadAccounts();
        System.out.println("Konti nulstillet til 1000 kr");
    }
}