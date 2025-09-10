package tse.info2.view;

import tse.info2.controller.AuthenticationController;
import tse.info2.service.AuthenticationService;
import tse.info2.session.UserSession;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class SignupForm extends JPanel {
    private static AuthenticationService authService = new AuthenticationService();
    private static AuthenticationController authController = new AuthenticationController(authService);
    private MainFrame mainFrame;

    public SignupForm(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());

        // Top Bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setPreferredSize(new Dimension(getWidth(), 80));
        topBar.setBackground(new Color(30, 38, 48));
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(51, 90, 183)));

        // Logo in the top bar
        ImageIcon logoIcon = new ImageIcon("./my-comic-list/src/main/resources/batman_logo.png");
        Image scaledImage = logoIcon.getImage().getScaledInstance(80, 40, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledImage));
        logoLabel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 0));
        topBar.add(logoLabel, BorderLayout.WEST);

        // Main content panel
        JPanel mainContent = new JPanel(new GridBagLayout());
        mainContent.setOpaque(false);

        // Content panel for title and form
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        // Title
        JLabel titleLabel = new JLabel("Sign Up");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        contentPanel.add(titleLabel);

        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username field
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        usernameLabel.setForeground(new Color(240, 242, 242));
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(usernameLabel, gbc);

        JTextField usernameField = new JTextField(15);
        usernameField.setBorder(new LineBorder(new Color(240, 242, 242), 1, true));
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        // Password field
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

        // Confirm password field
        JLabel confirmPasswordLabel = new JLabel("Confirm password");
        confirmPasswordLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        confirmPasswordLabel.setForeground(new Color(240, 242, 242));
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(confirmPasswordLabel, gbc);

        JPasswordField confirmPasswordField = new JPasswordField(15);
        confirmPasswordField.setBorder(new LineBorder(new Color(240, 242, 242), 1, true));
        gbc.gridx = 1;
        formPanel.add(confirmPasswordField, gbc);

        // Sign up button
        JButton signUpButton = new JButton("Sign Up");
        signUpButton.setBackground(new Color(51, 90, 183));
        signUpButton.setForeground(Color.WHITE);
        signUpButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        signUpButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        signUpButton.setFocusPainted(false);
        signUpButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(signUpButton, gbc);

        // Sign up button
        signUpButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            boolean isRegistered = authController.register(username, password, confirmPassword);

            if (isRegistered) {
                JOptionPane.showMessageDialog(this, "Registration successful! Welcome, " + username);
                mainFrame.showPanel("login");
            } else {
                String errorMessage = authController.getErrorMessage();
                JOptionPane.showMessageDialog(this, errorMessage);
            }
        });

        // Link for users who already have an account
        JLabel loginLabel = new JLabel("<html><u>Already have an account? Log in</u></html>");
        loginLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        loginLabel.setForeground(new Color(240, 242, 242));
        loginLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        loginLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                mainFrame.showPanel("login");
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(loginLabel, gbc);

        // Link for guest users
        JLabel guestLabel = new JLabel("<html><u>Login as guest</u></html>");
        guestLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        guestLabel.setForeground(new Color(240, 242, 242));
        guestLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        guestLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                UserSession.getInstance().setAsGuest();
                mainFrame.showPanel("home");
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(guestLabel, gbc);

        contentPanel.add(formPanel);
        mainContent.add(contentPanel);

        // Add main components
        add(topBar, BorderLayout.NORTH);
        add(mainContent, BorderLayout.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Panel width and height
        int width = getWidth();
        int height = getHeight();

        // Gradient colors
        Color startColor = new Color(51, 90, 183); // Blue
        Color endColor = new Color(30, 38, 48);   // Dark gray

        // Create linear gradient
        GradientPaint gradient = new GradientPaint(0, 0, startColor, 0, height, endColor);
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, width, height); // Fill the entire panel with the gradient
    }
}