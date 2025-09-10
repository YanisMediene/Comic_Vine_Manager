package tse.info2.view;

import tse.info2.session.UserSession;
import tse.info2.database.UserDAO;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.SQLException;

import tse.info2.controller.AuthenticationController;
import tse.info2.controller.UserController;
import tse.info2.model.User;
import tse.info2.service.AuthenticationService;

public class LoginForm extends JPanel {
    private static AuthenticationService authService = new AuthenticationService();
    private static AuthenticationController authController = new AuthenticationController(authService);
    private static UserDAO userDAO = new UserDAO(); // Ajout de l'instance UserDAO
    private MainFrame mainFrame;

    public LoginForm(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());

        // Top Bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setPreferredSize(new Dimension(getWidth(), 80));
        topBar.setBackground(new Color(30, 38, 48));
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(51, 90, 183)));

        // Logo dans la top bar
        ImageIcon logoIcon = new ImageIcon("./my-comic-list/src/main/resources/batman_logo.png");
        Image scaledImage = logoIcon.getImage().getScaledInstance(80, 40, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledImage));
        logoLabel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 0));
        topBar.add(logoLabel, BorderLayout.WEST);

        // Panneau principal pour le contenu
        JPanel mainContent = new JPanel(new GridBagLayout());
        mainContent.setOpaque(false);

        // Panel pour le titre et le formulaire
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        // Titre
        JLabel titleLabel = new JLabel("Login");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        contentPanel.add(titleLabel);

        // Formulaire
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Labels et champs de saisie
        JLabel usernameLabel = new JLabel("Nom d'utilisateur");
        usernameLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        usernameLabel.setForeground(new Color(240, 242, 242));
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(usernameLabel, gbc);

        JTextField usernameField = new JTextField(15);
        usernameField.setBorder(new LineBorder(new Color(240, 242, 242), 1, true));
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        passwordLabel.setForeground(new Color(240, 242, 242));
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(passwordLabel, gbc);

        JPasswordField passwordField = new JPasswordField(15);
        passwordField.setBorder(new LineBorder(new Color(240, 242, 242), 1, true));
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        // Bouton "Se connecter"
        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(51, 90, 183));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        loginButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        loginButton.setFocusPainted(false);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(loginButton, gbc);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            handleLogin(username, password);
        });

        // Ajouter un lien pour s'inscrire
        JLabel registerLabel = new JLabel("<html><u>Don't have an account? Sign up</u></html>");
        registerLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        registerLabel.setForeground(new Color(240, 242, 242));
        registerLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Ajouter un lien pour se connecter en tant qu'invité
        JLabel guestLabel = new JLabel("<html><u>Login as guest</u></html>");
        guestLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        guestLabel.setForeground(new Color(240, 242, 242));
        guestLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        registerLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                mainFrame.showPanel("signup");
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        formPanel.add(registerLabel, gbc);

        guestLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                UserSession.getInstance().setAsGuest();
                mainFrame.showPanel("home");
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(guestLabel, gbc);

        contentPanel.add(formPanel);
        mainContent.add(contentPanel);

        // Ajout des composants principaux
        add(topBar, BorderLayout.NORTH);
        add(mainContent, BorderLayout.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Largeur et hauteur du panneau
        int width = getWidth();
        int height = getHeight();

        // Couleurs du dégradé
        Color startColor = new Color(51, 90, 183); // Bleu
        Color endColor = new Color(30, 38, 48);   // Gris foncé

        // Création du dégradé linéaire
        GradientPaint gradient = new GradientPaint(0, 0, startColor, 0, height, endColor);
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, width, height); // Remplir tout le panneau avec le dégradé
    }

    private void handleLogin(String username, String password) {
        boolean authenticated = authController.login(username, password);
        if (authenticated) {
            try {
                User user = userDAO.getUser(username);
                if (user != null) {
                    UserSession.getInstance().setUser(user);
                    mainFrame.showPanel("home"); // Naviguer vers la page d'accueil
                } else {
                    JOptionPane.showMessageDialog(this, "Error retrieving user data");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Login failed. Please check your details.");
        }
    }
}
