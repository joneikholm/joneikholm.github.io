import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class MovieApp {

    static Connection connection;
    static JComboBox<Genre> genre;
    static JTextField tekst = new JTextField(25);
    static String[] columns = {"ID", "Name", "Genre"};
    static DefaultTableModel model = new DefaultTableModel(columns,0);

    static {
        String url = "jdbc:mysql://localhost:3306/mydb";
        String user = "root";
        String pw = "password";
        try {
            connection = DriverManager.getConnection(url,user, pw);
            System.out.println("er forbundet til MySQL Server");
        }catch (Exception e){
            System.out.println("fejl " + e.getMessage());
        }
    }


    public static void main(String[] args) {
        System.out.println("hej");
        JFrame frame = new JFrame("Movies");
        frame.setSize(600,400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        loadMovies();
        JTable jTable = new JTable(model);

        JScrollPane jScrollPane = new JScrollPane(jTable);
        JPanel topPanel = new JPanel();
        genre = getGenres();

        JButton pressMe = new JButton("Tilføj film");
        pressMe.addActionListener(e -> insertMovie());
        topPanel.add(pressMe);
        topPanel.add(tekst);
        topPanel.add(genre);
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(jScrollPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private static void loadMovies(){
        String sql = """
                SELECT m.id, m.name AS movie_name, g.name AS genre_name
                FROM movies m
                INNER JOIN genre g
                ON 
                m.genre_id = g.id
                """;
        model.setRowCount(0);
        try(Statement stmt = connection.createStatement()){
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()){
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("movie_name"),
                        rs.getString("genre_name")
                });
            }
        }catch (Exception e){}
    }

    private static JComboBox<Genre> getGenres() {
        JComboBox<Genre> genres = new JComboBox<>();
        // hent rækker fra Genre tabellen
        try(Statement stmt = connection.createStatement()){
            ResultSet rs = stmt.executeQuery("SELECT * FROM genre");
            while(rs.next()){
                genres.addItem(new Genre(rs.getInt("id"), rs.getString("name")));
            }
        }
        catch (Exception e){}
        return genres;
    }

    private static void insertMovie(){
        // hvilken række i Genre er valgt?
        int genreID = ((Genre)genre.getSelectedItem()).id;
        String movieName = tekst.getText();
        String sql = "INSERT INTO movies (name, genre_id) VALUES (?,?)";
        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, movieName);
            ps.setInt(2, genreID);
            ps.executeUpdate();
        }catch (Exception e){}
        loadMovies();
    }
}