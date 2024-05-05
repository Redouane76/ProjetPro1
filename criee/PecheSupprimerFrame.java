package criee;


import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;


public class PecheSupprimerFrame extends JFrame {


    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JComboBox<String> pecheComboBox;
    private DatabaseConnection databaseConnection;


    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    DatabaseConnection databaseConnection = new DatabaseConnection("jdbc:mysql://localhost", "root", "", "bdd_criee");
                    PecheSupprimerFrame frame = new PecheSupprimerFrame(databaseConnection);
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public PecheSupprimerFrame(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        JLabel lblNewLabel = new JLabel("Suppression pêche");
        lblNewLabel.setForeground(new Color(186, 85, 211));
        lblNewLabel.setBounds(154, 32, 157, 14);
        contentPane.add(lblNewLabel);
        pecheComboBox = new JComboBox<>();
        pecheComboBox.setBounds(73, 106, 300, 22);
        contentPane.add(pecheComboBox);
        JButton supprimerButton = new JButton("Supprimer");
        supprimerButton.setBounds(188, 150, 112, 23);
        contentPane.add(supprimerButton);
        // Charger la liste des pêches au démarrage de la fenêtre
        chargerListePeche();
        // Écouteur d'événements pour le bouton "Supprimer"
        supprimerButton.addActionListener(e -> supprimerPeche());
    }


    // Méthode pour charger la liste des pêches depuis la base de données
    private void chargerListePeche() {
        Connection connection = null;
        try {
            connection = databaseConnection.getConnection();
            String query = "SELECT idbateau, datePeche FROM peche";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            // Remplir la JComboBox avec les pêches
            while (resultSet.next()) {
                int idBateau = resultSet.getInt("idbateau");
                String datePeche = resultSet.getString("datePeche");
                String item = " Bateau:" + idBateau + ", Date " + datePeche;
                pecheComboBox.addItem(item);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }


    // Méthode pour supprimer la pêche sélectionnée
    private void supprimerPeche() {
        Connection connection = null;
        try {
            connection = databaseConnection.getConnection();
            // Récupérer l'ID du bateau de la pêche sélectionnée
            String selectedPeche = (String) pecheComboBox.getSelectedItem();
            String[] parts = selectedPeche.split("\\s+"); // Séparer la chaîne par les espaces
            int idBateau = Integer.parseInt(parts[1].replace("Bateau:", "").replace(",", "").trim()); // Supprimer "Bateau:" et la virgule de la valeur
            // Exécuter la requête SQL pour supprimer la pêche
            String deleteQuery = "DELETE FROM peche WHERE idbateau=?";
            PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery);
            deleteStatement.setInt(1, idBateau);
            deleteStatement.executeUpdate();
            // Rafraîchir la liste après la suppression
            pecheComboBox.removeAllItems();
            chargerListePeche();
            // Afficher un message de succès (à adapter selon vos besoins)
            System.out.println("Pêche supprimée avec succès!");
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
