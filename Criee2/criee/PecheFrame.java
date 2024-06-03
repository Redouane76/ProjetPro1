package criee;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

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

        setTitle("Création pêche du jour");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new GridBagLayout());
        contentPane.setBackground(new Color(236, 240, 245)); // #ecf0f5
        setContentPane(contentPane);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel titreLabel = new JLabel("Création pêche du jour");
        titreLabel.setForeground(new Color(22, 100, 95)); // #16645F
        titreLabel.setFont(new Font("Rokkitt", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        contentPane.add(titreLabel, gbc);

        JLabel bateauLabel = new JLabel("Bateau:");
        bateauLabel.setFont(new Font("Montserrat", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        contentPane.add(bateauLabel, gbc);

        JComboBox<String> bateauComboBox = new JComboBox<>();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        contentPane.add(bateauComboBox, gbc);

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
        validerButton.setFont(new Font("Montserrat", Font.BOLD, 14));
        validerButton.setBackground(new Color(0, 140, 186)); // #008CBA
        validerButton.setForeground(Color.BLACK);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        contentPane.add(validerButton, gbc);

        DateFormat shortDateFormat = DateFormat.getDateTimeInstance(
                DateFormat.SHORT,
                DateFormat.SHORT);
        Date todaysDate = new Date();
        JLabel dateLabel = new JLabel("Date: " + shortDateFormat.format(todaysDate));
        dateLabel.setFont(new Font("Montserrat", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        contentPane.add(dateLabel, gbc);

        // Charger l'icône personnalisée et la redimensionner
        ImageIcon customIcon = null;
        try {
            java.net.URL imgURL = getClass().getResource("/images/logoCriee.png");
            if (imgURL != null) {
                BufferedImage originalImage = ImageIO.read(imgURL);
                Image resizedImage = originalImage.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                customIcon = new ImageIcon(resizedImage);
                System.out.println("Image found: " + imgURL.getPath());
            } else {
                System.err.println("Couldn't find file: logoCriee.png");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Écouteur d'événements pour le bouton "Valider"
        ImageIcon finalCustomIcon = customIcon;
        validerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                        null, 
                        "Voulez-vous vraiment confirmer?", 
                        "Confirmation", 
                        JOptionPane.YES_NO_OPTION, 
                        JOptionPane.QUESTION_MESSAGE, 
                        finalCustomIcon
                );
                if (confirm == JOptionPane.YES_OPTION) {
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
            }
        });
    }
}
