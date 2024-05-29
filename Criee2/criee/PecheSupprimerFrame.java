package criee;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class PecheSupprimerFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JComboBox<String> pecheComboBox;
    private DatabaseConnection databaseConnection;
    private Connection connection;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                DatabaseConnection databaseConnection = new DatabaseConnection("jdbc:mysql://localhost", "root", "", "bdd_criee");
                PecheSupprimerFrame frame = new PecheSupprimerFrame(databaseConnection);
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public PecheSupprimerFrame(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
        this.connection = databaseConnection.getConnection();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblNewLabel = new JLabel("Suppression pêche");
        lblNewLabel.setForeground(new Color(22, 100, 95));
        lblNewLabel.setFont(new Font("Rokkitt", Font.BOLD, 18));
        lblNewLabel.setBounds(110, 30, 220, 20);
        contentPane.add(lblNewLabel);

        pecheComboBox = new JComboBox<>();
        pecheComboBox.setFont(new Font("Montserrat", Font.PLAIN, 14));
        pecheComboBox.setBounds(73, 100, 300, 22);
        contentPane.add(pecheComboBox);

        JButton supprimerButton = new JButton("Supprimer");
        supprimerButton.setFont(new Font("Montserrat", Font.BOLD, 14));
        supprimerButton.setBounds(168, 150, 112, 30);
        contentPane.add(supprimerButton);

        chargerListePeche();

        supprimerButton.addActionListener(e -> confirmerSuppression());

        contentPane.setBackground(new Color(236, 240, 245));
    }

    private void chargerListePeche() {
        try {
            String query = "SELECT idbateau, datePeche FROM peche";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int idBateau = resultSet.getInt("idbateau");
                String datePeche = resultSet.getString("datePeche");
                String item = "Bateau:" + idBateau + ", Date:" + datePeche;
                pecheComboBox.addItem(item);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void confirmerSuppression() {
        ImageIcon icon = new ImageIcon(getClass().getResource("/images/logoCriee.png"));
        Image image = icon.getImage();
        Image newImage = image.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        ImageIcon newIcon = new ImageIcon(newImage);
        int choix = JOptionPane.showConfirmDialog(null, "Voulez-vous vraiment supprimer cette pêche?", "Confirmation de suppression", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, newIcon);
        if (choix == JOptionPane.YES_OPTION) {
            supprimerPeche();
        }
    }

    private void supprimerPeche() {
        try {
            String selectedPeche = (String) pecheComboBox.getSelectedItem();
            String[] parts = selectedPeche.split(", Date:");
            int idBateau = Integer.parseInt(parts[0].replace("Bateau:", "").trim());
            String datePeche = parts[1].trim();

            // Vérification des valeurs récupérées
            System.out.println("Suppression de la pêche pour le bateau ID: " + idBateau + " à la date: " + datePeche);

            String deleteQuery = "DELETE FROM peche WHERE idbateau=? AND datePeche=?";
            PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery);
            deleteStatement.setInt(1, idBateau);
            deleteStatement.setString(2, datePeche);

            int rowsAffected = deleteStatement.executeUpdate();
            System.out.println("Nombre de lignes supprimées: " + rowsAffected);

            pecheComboBox.removeAllItems();
            chargerListePeche();

            JOptionPane.showMessageDialog(null, "Pêche supprimée avec succès!");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
