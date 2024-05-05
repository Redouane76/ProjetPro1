package criee;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;

public class PecheFrame extends JFrame {

    private static DatabaseConnection databaseConnection;

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    databaseConnection = new DatabaseConnection("jdbc:mysql://localhost", "root", "", "bdd_criee");
                    PecheFrame frame = new PecheFrame(databaseConnection);
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public PecheFrame(DatabaseConnection databaseConnection) {
        Connection connection = databaseConnection.getConnection();

        getContentPane().setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel titreLabel = new JLabel("Création pêche");
        titreLabel.setForeground(new Color(186, 85, 211));
        titreLabel.setBounds(167, 24, 155, 25);
        contentPane.add(titreLabel);

        JLabel bateauLabel = new JLabel("Bateau:");
        bateauLabel.setBounds(43, 85, 46, 14);
        contentPane.add(bateauLabel);

        JComboBox<String> bateauComboBox = new JComboBox<>();
        bateauComboBox.setBounds(185, 81, 118, 22);
        contentPane.add(bateauComboBox);

        // Exécuter une requête SQL pour récupérer la liste des bateaux
        try {
            String query = "SELECT id, nom FROM bateau";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            // Parcourir le résultat et ajouter chaque bateau à la ComboBox
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nom = resultSet.getString("nom");
                bateauComboBox.addItem(id + ": " + nom);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        JButton validerButton = new JButton("Valider");
        validerButton.setBounds(158, 171, 89, 23);
        contentPane.add(validerButton);

        DateFormat shortDateFormat = DateFormat.getDateTimeInstance(
                DateFormat.SHORT,
                DateFormat.SHORT);
        Date todaysDate = new Date();
        JLabel dateLabel = new JLabel("Date: " + shortDateFormat.format(todaysDate));

        dateLabel.setBounds(10, 236, 237, 14);
        contentPane.add(dateLabel);

        // Écouteur d'événements pour le bouton "Valider"
        validerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Récupérer l'ID du bateau sélectionné
                String selectedBateau = (String) bateauComboBox.getSelectedItem();
                int idBateau = Integer.parseInt(selectedBateau.split(":")[0].trim());

                // Récupérer la date actuelle
                Date datePeche = new Date(); 

                // Formatter la date au format souhaité
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String formattedDate = dateFormat.format(datePeche);

                // Exécuter une requête SQL pour insérer une nouvelle pêche
                try {
                    String insertQuery = "INSERT INTO peche (idbateau, datePeche) VALUES (?, ?)";
                    PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
                    insertStatement.setInt(1, idBateau);
                    insertStatement.setString(2, formattedDate);
                    insertStatement.executeUpdate();


                    System.out.println("Nouvelle pêche créée avec succès!");

                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
}
