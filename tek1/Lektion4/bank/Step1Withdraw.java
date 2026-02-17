package bank;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

// =========================================================
// OPGAVE 1: Hæv penge (rollback)
// =========================================================
// 1. Kør appen og hæv 200 kr fra Alice. Det virker fint.
// 2. Klik "Nulstil konti".
// 3. Fjern kommentaren fra "throw"-linjen i withdraw().
// 4. Kør igen og hæv 200 kr fra Alice.
// 5. Der står "FEJL" i konsollen – men pengene er væk alligevel!
//
// DIN OPGAVE: Ret withdraw() så pengene IKKE forsvinder ved fejl.
// Brug: setAutoCommit(false), commit(), rollback(), setAutoCommit(true)
// =========================================================

public class Step1Withdraw {
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
        JFrame frame = new JFrame("Opgave 1: Hæv penge");
        frame.setSize(450, 350);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(5, 5));

        loadAccounts();
        frame.add(new JScrollPane(new JTable(model)), BorderLayout.CENTER);

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Hæv penge"));

        JComboBox<Account> accountBox = new JComboBox<>();
        JTextField amountField = new JTextField(8);
        JButton withdrawBtn = new JButton("Hæv");
        JButton resetBtn = new JButton("Nulstil konti");

        withdrawBtn.addActionListener(e -> {
            Account account = (Account) accountBox.getSelectedItem();
            int amount = Integer.parseInt(amountField.getText());
            withdraw(account.id, amount);
        });

        resetBtn.addActionListener(e -> resetAccounts());

        panel.add(new JLabel("Konto:"));
        panel.add(accountBox);
        panel.add(new JLabel("Beløb:"));
        panel.add(amountField);
        panel.add(withdrawBtn);
        panel.add(resetBtn);

        frame.add(panel, BorderLayout.SOUTH);

        loadAccountBox(accountBox);
        frame.setVisible(true);
    }

    // =========================================================
    // RET DENNE METODE
    // =========================================================

    static void withdraw(int accountId, int amount) {
        try (Statement stmt = connection.createStatement()) {
            connection.setAutoCommit(false);
            ResultSet rs = stmt.executeQuery( // 1. Læs den nuværende saldo
                    "SELECT saldo FROM account WHERE id = " + accountId);
            rs.next();
            int saldo = rs.getInt("saldo");
            if (saldo < amount) { // 2. Beregn ny saldo i Java
                System.out.println("Ikke nok penge! Saldo: " + saldo);
                return;
            }
            int nySaldo = saldo - amount;
            // 3. Skriv den nye saldo til databasen
            stmt.executeUpdate(
                    "UPDATE account SET saldo = " + nySaldo
                            + " WHERE id = " + accountId);
            // Fjern kommentaren herunder for at simulere en fejl:
            //if (true) throw new RuntimeException("Fejl i systemet!");
            connection.commit();
            System.out.println("Værsgo, her er: " + amount + " kr. Ny saldo: " + nySaldo);
        } catch (Exception e) {
            try {connection.rollback();}catch (Exception e2){}
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