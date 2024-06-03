package criee;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LotFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JComboBox<String> comboBoxIdBateau;
    private JComboBox<String> comboBoxIdEspece;
    private JComboBox<String> comboBoxIdTaille;
    private JComboBox<String> comboBoxIdPresentation;
    private JComboBox<String> comboBoxIdQualite;
    private JTextField textFieldPoidsBrutLot;
    private JTextField textFieldPrixEnchere;
    private JTextField textFieldPrixPlancher;
    private JTextField textFieldPrixDepart;
    private JTextField textFieldCodeEtat;
    private JComboBox<String> comboBoxDateEnchere;
    private JComboBox<String> comboBoxHeureDebutEnchere; // Ajout de la JComboBox pour l'heure de début d'enchère

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    LotFrame frame = new LotFrame();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public LotFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 500); // Augmentation de la taille de la fenêtre
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        JPanel panel = new JPanel();
        contentPane.add(panel, BorderLayout.CENTER);
        panel.setLayout(new GridLayout(0, 2, 10, 10));

        // Ajout des champs pour les détails du lot
        JLabel lblIdBateau = new JLabel("ID Bateau:");
        panel.add(lblIdBateau);

        comboBoxIdBateau = new JComboBox<>();
        panel.add(comboBoxIdBateau);

        JLabel lblIdEspece = new JLabel("ID Espèce:");
        panel.add(lblIdEspece);

        comboBoxIdEspece = new JComboBox<>();
        panel.add(comboBoxIdEspece);

        JLabel lblIdTaille = new JLabel("ID Taille:");
        panel.add(lblIdTaille);

        comboBoxIdTaille = new JComboBox<>();
        panel.add(comboBoxIdTaille);

        JLabel lblIdPresentation = new JLabel("ID Présentation:");
        panel.add(lblIdPresentation);

        comboBoxIdPresentation = new JComboBox<>();
        panel.add(comboBoxIdPresentation);

        JLabel lblIdQualite = new JLabel("ID Qualité:");
        panel.add(lblIdQualite);

        comboBoxIdQualite = new JComboBox<>();
        panel.add(comboBoxIdQualite);

        JLabel lblPoidsBrutLot = new JLabel("Poids Brut Lot:");
        panel.add(lblPoidsBrutLot);

        textFieldPoidsBrutLot = new JTextField();
        panel.add(textFieldPoidsBrutLot);
        textFieldPoidsBrutLot.setColumns(10);

        JLabel lblPrixEnchere = new JLabel("Prix Enchère:");
        panel.add(lblPrixEnchere);

        textFieldPrixEnchere = new JTextField();
        panel.add(textFieldPrixEnchere);
        textFieldPrixEnchere.setColumns(10);

        JLabel lblPrixPlancher = new JLabel("Prix Plancher:");
        panel.add(lblPrixPlancher);

        textFieldPrixPlancher = new JTextField();
        panel.add(textFieldPrixPlancher);
        textFieldPrixPlancher.setColumns(10);

        JLabel lblPrixDepart = new JLabel("Prix Départ:");
        panel.add(lblPrixDepart);

        textFieldPrixDepart = new JTextField();
        panel.add(textFieldPrixDepart);
        textFieldPrixDepart.setColumns(10);

        JLabel lblCodeEtat = new JLabel("Code État:");
        panel.add(lblCodeEtat);

        textFieldCodeEtat = new JTextField();
        panel.add(textFieldCodeEtat);
        textFieldCodeEtat.setColumns(10);

        // Ajout de la JComboBox pour la date d'enchère
        panel.add(new JLabel("Date d'enchère:"));
        comboBoxDateEnchere = new JComboBox<>();
        panel.add(comboBoxDateEnchere);

        // Ajout de la JComboBox pour l'heure de début d'enchère
        panel.add(new JLabel("Heure de début d'enchère:"));
        comboBoxHeureDebutEnchere = new JComboBox<>();
        panel.add(comboBoxHeureDebutEnchere);

        JButton btnAjouterLot = new JButton("Ajouter Lot");
        btnAjouterLot.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (confirmDialog()) {
                    ajouterLot();
                }
            }
        });
        contentPane.add(btnAjouterLot, BorderLayout.SOUTH);

        // Ajout du panneau pour le titre et la date
        JPanel panelTitreDate = new JPanel();
        panelTitreDate.setLayout(new BorderLayout());
        JLabel lblTitre = new JLabel("Création lot pêche du jour", SwingConstants.CENTER);
        lblTitre.setFont(new Font("Serif", Font.BOLD, 16));
        panelTitreDate.add(lblTitre, BorderLayout.NORTH);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        JLabel lblDate = new JLabel("Date : " + sdf.format(new Date()), SwingConstants.CENTER);
        panelTitreDate.add(lblDate, BorderLayout.SOUTH);

        contentPane.add(panelTitreDate, BorderLayout.NORTH);

        remplirComboBox();
    }

    private void remplirComboBox() {
        try {
            DatabaseConnection databaseConnection = new DatabaseConnection("jdbc:mysql://localhost", "root", "", "bdd_criee");
            Connection conn = databaseConnection.getConnection();

            // Remplissage des comboBox avec les valeurs de la base de données
            remplirComboBoxAvecDonnees(conn, "SELECT id FROM bateau", comboBoxIdBateau);
            remplirComboBoxAvecDonnees(conn, "SELECT id FROM espece", comboBoxIdEspece);
            remplirComboBoxAvecDonnees(conn, "SELECT id FROM taille", comboBoxIdTaille);
            remplirComboBoxAvecDonnees(conn, "SELECT id FROM presentation", comboBoxIdPresentation);
            remplirComboBoxAvecDonnees(conn, "SELECT id FROM qualite", comboBoxIdQualite);

            // Remplissage de la JComboBox avec les dates d'enchère disponibles
            remplirComboBoxAvecDonnees(conn, "SELECT DISTINCT dateEnchere FROM lot", comboBoxDateEnchere);

            // Ajout de l'option null dans la JComboBox pour la date d'enchère
            comboBoxDateEnchere.addItem(null); // Ajoute une option vide

            // Remplissage de la JComboBox avec les heures de début d'enchère disponibles
            remplirComboBoxAvecDonnees(conn, "SELECT DISTINCT HeureDebutEnchere FROM lot", comboBoxHeureDebutEnchere);

            // Ajout de l'option null dans la JComboBox pour l'heure de début d'enchère
            comboBoxHeureDebutEnchere.addItem(null); // Ajoute une option vide

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void remplirComboBoxAvecDonnees(Connection conn, String query, JComboBox<String> comboBox) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(query);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            comboBox.addItem(rs.getString(1));
        }
        rs.close();
        pstmt.close();
    }

    private void ajouterLot() {
        try {
            int idBateau = Integer.parseInt((String) comboBoxIdBateau.getSelectedItem());
            int idEspece = Integer.parseInt((String) comboBoxIdEspece.getSelectedItem());
            int idTaille = Integer.parseInt((String) comboBoxIdTaille.getSelectedItem());
            String idPresentation = (String) comboBoxIdPresentation.getSelectedItem();
            String idQualite = (String) comboBoxIdQualite.getSelectedItem();
            float poidsBrutLot = Float.parseFloat(textFieldPoidsBrutLot.getText());
            float prixPlancher = Float.parseFloat(textFieldPrixPlancher.getText());
            float prixDepart = Float.parseFloat(textFieldPrixDepart.getText());
            String codeEtat = textFieldCodeEtat.getText();

            // Récupérez la date d'enchère sélectionnée depuis la JComboBox
            String dateEnchere = (String) comboBoxDateEnchere.getSelectedItem();
            java.sql.Date dateEnchereSQL = null;
            if (dateEnchere != null && !dateEnchere.isEmpty()) {
                dateEnchereSQL = java.sql.Date.valueOf(dateEnchere);
            }

            // Récupérez l'heure de début d'enchère sélectionnée depuis la JComboBox
            String heureDebutEnchere = (String) comboBoxHeureDebutEnchere.getSelectedItem();

            // Connexion à la base de données et insertion du lot
            DatabaseConnection databaseConnection = new DatabaseConnection("jdbc:mysql://localhost", "root", "", "bdd_criee");
            Connection conn = databaseConnection.getConnection();
            String query = "INSERT INTO lot (idBateau, datePeche, idEspece, idTaille, idPresentation, idQualite, poidsBrutLot, prixEnchere, dateEnchere, HeureDebutEnchere, prixPlancher, prixDepart, codeEtat) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, idBateau);
            pstmt.setDate(2, java.sql.Date.valueOf(java.time.LocalDate.now())); // Utilisation de la date actuelle pour datePeche
            pstmt.setInt(3, idEspece);
            pstmt.setInt(4, idTaille);
            pstmt.setString(5, idPresentation);
            pstmt.setString(6, idQualite);
            pstmt.setFloat(7, poidsBrutLot);

            // Gérez la valeur null pour le prix d'enchère
            String prixEnchere = textFieldPrixEnchere.getText();
            if (prixEnchere.isEmpty()) {
                pstmt.setNull(8, java.sql.Types.FLOAT);
            } else {
                pstmt.setFloat(8, Float.parseFloat(prixEnchere));
            }

            // Gérez la valeur null pour la date d'enchère
            if (dateEnchereSQL == null) {
                pstmt.setNull(9, java.sql.Types.DATE);
            } else {
                pstmt.setDate(9, dateEnchereSQL);
            }

            // Gérez la valeur null pour l'heure de début d'enchère
            if (heureDebutEnchere == null || heureDebutEnchere.isEmpty()) {
                pstmt.setNull(10, java.sql.Types.TIME);
            } else {
                pstmt.setString(10, heureDebutEnchere);
            }

            pstmt.setFloat(11, prixPlancher);
            pstmt.setFloat(12, prixDepart);
            pstmt.setString(13, codeEtat);

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Le lot a été ajouté avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
            }
            pstmt.close();
            conn.close();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer des valeurs numériques valides pour les champs appropriés.", "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de l'insertion dans la base de données.", "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private boolean confirmDialog() {
        int option = JOptionPane.showConfirmDialog(this, "Êtes-vous sûr de vouloir ajouter ce lot ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        return option == JOptionPane.YES_OPTION;
    }
}

