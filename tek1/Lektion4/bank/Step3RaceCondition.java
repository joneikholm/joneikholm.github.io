package bank;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

// =========================================================
// OPGAVE 3: Langsom indbetaling (race condition / lost update)
// =========================================================
// SCENARIE:
// Alice og Bob er forældre. De vil begge overføre penge
// til deres barns konto – samtidig, fra hver sin computer.
//
// OPSÆTNING:
// 1. I IntelliJ: Edit Configurations → Modify options → Allow multiple instances
// 2. Kør denne app TO gange, så du har to vinduer åbne
// 3. Klik "Nulstil konti" i ét af vinduerne
//
// PROBLEMET:
// 1. I vindue 1: vælg Alice, skriv 500, klik "Overfør til Barnet"
// 2. HURTIGT (inden 5 sek): vælg Bob i vindue 2, skriv 500, klik knappen
// 3. Begge læser Barnets saldo: 1000. Begge beregner 1000 + 500 = 1500.
//    Begge skriver 1500. Men saldoen burde være 2000!
//    Én overførsel er gået tabt. Det er en "lost update".
//
// DIN OPGAVE: Ret depositSlow() med en transaktion
// så begge indbetalinger tæller med.
// =========================================================

public class Step3RaceCondition {
    static Connection connection;
    static DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Navn", "Saldo"}, 0);
    static final int BARNET_ID = 3; // Barnets konto-id

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
        JFrame frame = new JFrame("Opgave 3: Race condition (lost update)");
        frame.setSize(550, 350);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(5, 5));

        loadAccounts();
        frame.add(new JScrollPane(new JTable(model)), BorderLayout.CENTER);

        JPanel south = new JPanel();
        south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Overførsel til Barnet (åbn to vinduer!)"));

        JComboBox<Account> parentBox = new JComboBox<>();
        loadParentBox(parentBox);
        JTextField amountField = new JTextField(8);
        JButton depositBtn = new JButton("Overfør til Barnet (langsom)");
        JButton resetBtn = new JButton("Nulstil konti");

        depositBtn.addActionListener(e -> {
            Account parent = (Account) parentBox.getSelectedItem();
            int amount = Integer.parseInt(amountField.getText());
            new Thread(() -> depositSlow(parent.id, parent.name, BARNET_ID, amount)).start();
        });

        resetBtn.addActionListener(e -> resetAccounts());

        panel.add(new JLabel("Forælder:"));
        panel.add(parentBox);
        panel.add(new JLabel("Beløb:"));
        panel.add(amountField);
        panel.add(depositBtn);

        JPanel resetPanel = new JPanel();
        resetPanel.add(resetBtn);

        south.add(panel);
        south.add(resetPanel);
        frame.add(south, BorderLayout.SOUTH);
        frame.setVisible(true);

        // Opdater tabellen automatisk hvert 2. sekund
        new javax.swing.Timer(2000, e -> loadAccounts()).start();
    }

    // =========================================================
    // RET DENNE METODE
    // =========================================================
       static void depositSlow(int parentId, String parentName, int childId, int amount) {
        try (Statement stmt = connection.createStatement()) {

            // 1. Læs Barnets nuværende saldo
            ResultSet rs = stmt.executeQuery(
                    "SELECT saldo FROM account WHERE id = " + childId);
            rs.next();
            int childSaldo = rs.getInt("saldo");
            System.out.println(parentName + " læser Barnets saldo: " + childSaldo
                    + " (venter 5 sekunder...)");

            // Simulerer langsomt netværk
            Thread.sleep(5000);

            // 2. Beregn ny saldo i Java
            int nyChildSaldo = childSaldo + amount;

            // 3. Træk fra forælderens konto
            stmt.executeUpdate(
                    "UPDATE account SET saldo = saldo - " + amount
                            + " WHERE id = " + parentId);

            // 4. Skriv den nye saldo til Barnets konto
            stmt.executeUpdate(
                    "UPDATE account SET saldo = " + nyChildSaldo
                            + " WHERE id = " + childId);
            System.out.println(parentName + " har overført " + amount
                    + " kr til Barnet. Barnets nye saldo: " + nyChildSaldo);

        } catch (Exception e) {
            System.out.println("FEJL: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> loadAccounts());
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

    static void loadParentBox(JComboBox<Account> box) {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT * FROM account WHERE name IN ('Alice', 'Bob')")) {
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