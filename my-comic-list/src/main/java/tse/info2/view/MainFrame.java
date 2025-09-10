package tse.info2.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import java.net.URL;

import com.formdev.flatlaf.FlatLightLaf;
import tse.info2.session.UserSession;

public class MainFrame extends JFrame {
    private JPanel mainContent;
    private JTextField searchField;
    private JComboBox<String> searchTypeComboBox;
    private CardLayout cardLayout;
    
    private LoginForm loginPanel;
    private SignupForm signupPanel;
    private ComicHomePage homePanel;
    private LibraryPage libraryPanel;
    private SearchPage searchPanel;
    
    public MainFrame() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        
        setTitle("Comic Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // Initialiser les panels
        cardLayout = new CardLayout();
        mainContent = new JPanel(cardLayout);
        
        loginPanel = new LoginForm(this);
        signupPanel = new SignupForm(this);
        homePanel = new ComicHomePage(this);
        libraryPanel = new LibraryPage(this);
        searchPanel = new SearchPage(this);
        
        // Ajouter les panels au cardLayout
        mainContent.add(loginPanel, "login");
        mainContent.add(signupPanel, "signup");
        mainContent.add(homePanel, "home");
        mainContent.add(libraryPanel, "library");
        mainContent.add(searchPanel, "search");
        
        // Layout principal
        setLayout(new BorderLayout());
        add(createTopBar(), BorderLayout.NORTH);
        add(mainContent, BorderLayout.CENTER);
        
        // Commencer avec login
        showPanel("login");
    }
    
    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setPreferredSize(new Dimension(getWidth(), 80));
        topBar.setBackground(new Color(30, 38, 48));
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(51, 90, 183)));
        
        // Panel central pour centrer le searchPanel
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerPanel.setOpaque(false);
        
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
        searchPanel.setOpaque(false);

        // Bouton Home avec logo
        JButton homeButton = createHomeButton();
        searchPanel.add(homeButton);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        
        // Barre de recherche et ses composants
        searchField = new JTextField(30);
        searchField.setMaximumSize(new Dimension(300, 30));
        searchTypeComboBox = new JComboBox<>(new String[] { "Character", "Issue" });
        searchTypeComboBox.setMaximumSize(new Dimension(150, 30));
        searchTypeComboBox.setSelectedIndex(0);
        searchTypeComboBox.setBackground(Color.WHITE);
        
        JButton searchButton = new JButton("Search");
        searchButton.setBackground(new Color(0x4CAF50));
        
        // Configurer le bouton de recherche
        searchButton.addActionListener(e -> {
            String query = searchField.getText().trim();
            if (!query.isEmpty()) {
                String searchType = searchTypeComboBox.getSelectedItem().toString();
                getSearchPanel().performSearch(query, searchType);
                showPanel("search");
            }
        });
        
        // Bouton Library
        JButton libraryButton = new JButton("Go to Library");
        libraryButton.setBackground(new Color(0xFF5722));
        libraryButton.addActionListener(e -> {
            // Créer une nouvelle instance de LibraryPage et l'ajouter au cardLayout
            libraryPanel = new LibraryPage(this);
            mainContent.add(libraryPanel, "library");
            showPanel("library");
        });
        
        // Bouton Déconnexion avec gestion du mode invité
        JButton logoutButton = new JButton("Logout");
        if (UserSession.getInstance().isGuest()) {
            logoutButton.setText("Login");
            logoutButton.setBackground(new Color(50, 205, 50)); // Vert pour connexion
        } else {
            logoutButton.setBackground(new Color(0xF44336)); // Rouge pour déconnexion
        }
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setPreferredSize(new Dimension(120, 30));
        logoutButton.addActionListener(e -> {
            if (UserSession.getInstance().isGuest()) {
                showPanel("login"); // Redirection vers login pour les invités
            } else {
                UserSession.getInstance().clearSession(); // Déconnexion pour les utilisateurs
                showPanel("login");
            }
        });
        
        // Ajouter tous les composants au searchPanel
        searchPanel.add(searchField);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(searchTypeComboBox);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(searchButton);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(libraryButton);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(logoutButton);
        
        // Ajouter le searchPanel au centerPanel
        centerPanel.add(searchPanel);
        
        // Ajouter le centerPanel au centre de la topBar
        topBar.add(centerPanel, BorderLayout.CENTER);
        
        // Rendre la topbar visible seulement quand l'utilisateur est connecté
        topBar.setVisible(false);
        
        return topBar;
    }
    
    private JButton createHomeButton() {
        JButton homeButton = new JButton();
        homeButton.setPreferredSize(new Dimension(100, 60));
        homeButton.setFocusPainted(false);
        homeButton.setBorder(BorderFactory.createEmptyBorder());
        homeButton.setContentAreaFilled(false);
        
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/batman_logo.png"));
            Image scaledImage = icon.getImage().getScaledInstance(80, 40, Image.SCALE_SMOOTH);
            homeButton.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image : " + e.getMessage());
        }
        
        homeButton.addActionListener(e -> {
            // Créer une nouvelle instance de ComicHomePage et l'ajouter au cardLayout
            homePanel = new ComicHomePage(this);
            mainContent.add(homePanel, "home");
            showPanel("home");
        });
        
        return homeButton;
    }
    
    public void showPanel(String panelName) {
        // Gérer la visibilité de la topbar
        boolean showTopBar = !panelName.equals("login") && !panelName.equals("signup");
        JPanel topBar = (JPanel) getContentPane().getComponent(0);
        topBar.setVisible(showTopBar);
        
        // Mettre à jour le bouton de déconnexion si la topbar est visible
        if (showTopBar) {
            updateLogoutButton(topBar);
        }
        
        // Afficher le panel demandé
        cardLayout.show(mainContent, panelName);
    }

    private void updateLogoutButton(JPanel topBar) {
        // Trouver le bouton de déconnexion dans la topbar
        JPanel centerPanel = (JPanel) topBar.getComponent(0);
        JPanel searchPanel = (JPanel) centerPanel.getComponent(0);
        
        // Le bouton logout est le dernier composant avant le dernier glue
        Component[] components = searchPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                if (button.getText().equals("Logout") || button.getText().equals("Login")) {
                    // Mettre à jour le bouton selon le statut d'invité
                    if (UserSession.getInstance().isGuest()) {
                        button.setText("Login");
                        button.setForeground(Color.BLACK);
                        button.setBackground(new Color(50, 205, 50)); // Vert pour connexion
                    } else {
                        button.setText("Logout");
                        button.setForeground(Color.BLACK);
                        button.setBackground(new Color(0xF44336)); // Rouge pour déconnexion
                    }
                    button.revalidate();
                    button.repaint();
                    break;
                }
            }
        }
    }
    
    public SearchPage getSearchPanel() {
        return searchPanel;
    }

    public JPanel getMainContent() {
        return mainContent;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
