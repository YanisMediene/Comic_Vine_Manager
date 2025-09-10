package tse.info2.view;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.sql.SQLException;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import tse.info2.database.UserIssueDAO;
import tse.info2.model.Auteur;
import tse.info2.model.ComicObject;
import tse.info2.model.Genre;
import tse.info2.model.Issue;
import tse.info2.model.Location;
import tse.info2.model.Personnage;
import tse.info2.model.StoryArc;
import tse.info2.model.Team;
// Ajouter l'import pour UserSession
import tse.info2.session.UserSession;
import tse.info2.util.ApiClient;

// Importer les classes nécessaires en haut du fichier
import tse.info2.service.UserService;
import tse.info2.controller.UserController;
import tse.info2.database.*;

public class IssueDetailsPanel extends JPanel {
    private UserController userController;
    // Ajouter ces variables pour tracker la position Y de chaque colonne
    private int leftColumnY = 100;    // Pour la colonne de gauche
    private int rightColumnY = 100;   // Pour la colonne de droite

    private SearchPage searchPage;
    private ComicHomePage homePage;
    private MainFrame mainFrame; // Ajouter cette variable d'instance

    public IssueDetailsPanel(Issue issue, SearchPage searchPage, LibraryPage libraryPage) throws IOException, InterruptedException {
        this.searchPage = searchPage;
        this.homePage = null;
        this.mainFrame = null; // Initialiser à null quand on vient de SearchPage
        // Initialiser les DAOs et services nécessaires
        VolumeDAO volumeDAO = new VolumeDAO();
        IssueDAO issueDAO = new IssueDAO();
        UserIssueDAO userIssueDAO = new UserIssueDAO();
        AuteurDAO auteurDAO = new AuteurDAO();
        GenreDAO genreDAO = new GenreDAO();
        PersonnageDAO personnageDAO = new PersonnageDAO();
        PowerDAO powerDAO = new PowerDAO();
        LocationDAO locationDAO = new LocationDAO();
        TeamDAO teamDAO = new TeamDAO();
        ComicObjectDAO comicObjectDAO = new ComicObjectDAO();
        StoryArcDAO storyArcDAO = new StoryArcDAO();
        ApiClient apiClient = new ApiClient();

        UserService userService = new UserService(volumeDAO, issueDAO, userIssueDAO, auteurDAO, 
            genreDAO, personnageDAO, apiClient, powerDAO, locationDAO, teamDAO, 
            comicObjectDAO, storyArcDAO);
        
        this.userController = new UserController(userService);

        // Set the layout with padding for spacing
    	
    	
        JPanel contentPanel = new JPanel();

        contentPanel.setLayout(null); // Positionnement absolu
        Color darkBlue = new Color(10, 12, 30);
        Color white = Color.WHITE;
        Color lightRed = new Color(255, 85, 85);


        contentPanel.setBackground(darkBlue);
        contentPanel.setPreferredSize(new Dimension(1280, 4000)); // Taille suffisante pour activer le défilement
 
        
        // Titre du personnage
        JLabel nameLabel = new JLabel("Issue: " + issue.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 30));
        nameLabel.setForeground(white);
        nameLabel.setBounds(470, 50, 600, 50);
        contentPanel.setComponentZOrder(nameLabel, 0); // Met le titre au-dessus de l'image floue
        
        
        JLabel issueNumberLabel = new JLabel("Issue Number: " + issue.getIssueNumber());
        issueNumberLabel.setFont(new Font("Arial", Font.PLAIN, 16));  // Smaller font for number
        issueNumberLabel.setForeground(new Color(180,180,180));  // Dark gray for readability
        issueNumberLabel.setBounds(480, 90, 500, 30); 
        contentPanel.setComponentZOrder(issueNumberLabel, 0); // Met le titre au-dessus de l'image floue



        JLabel coverDateLabel = new JLabel("Cover Date: " + issue.getCoverDate());
        coverDateLabel.setFont(new Font("Arial", Font.PLAIN, 16));  // Font styling
        coverDateLabel.setForeground(new Color(180,180,180));
        coverDateLabel.setBounds(480, 120, 400, 30); 
        contentPanel.setComponentZOrder(coverDateLabel, 0); // Met le titre au-dessus de l'image floue


        JLabel VolumeLabel = new JLabel("Volume: " + issue.getVolume().getName());
        VolumeLabel.setFont(new Font("Arial", Font.PLAIN, 16));  // Font styling
        VolumeLabel.setForeground(new Color(180,180,180));
        VolumeLabel.setBounds(480, 150, 400, 30); 
        contentPanel.setComponentZOrder(VolumeLabel, 0); // Met le titre au-dessus de l'image floue


        


     // Description du personnage
        JLabel descriptionLabel = new JLabel("<html><body style='width: 750px;'>" + issue.getDescription() + "</body></html>");
        descriptionLabel.setFont(new Font("Arial", Font.ITALIC, 18));
        descriptionLabel.setForeground(white);

        // Supprimer la bordure et définir l'arrière-plan comme transparent
        descriptionLabel.setOpaque(false);
        descriptionLabel.setVerticalAlignment(SwingConstants.TOP); // Alignement vertical du texte

        // Encapsuler le JLabel dans un JScrollPane
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionLabel);
        descriptionScrollPane.setBounds(450, 190, 980, 150); // Position et taille du slider
        descriptionScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); // Barre de défilement verticale
        descriptionScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // Pas de défilement horizontal
        descriptionScrollPane.setOpaque(false);
        descriptionScrollPane.getViewport().setOpaque(false); // Fond transparent pour correspondre à votre design
        descriptionScrollPane.setBorder(null); // Supprimer les bordures par défaut

        // Ajouter le JScrollPane au contentPanel
        contentPanel.add(descriptionScrollPane);
        contentPanel.setComponentZOrder(descriptionScrollPane, 0); // Met la description au-dessus de l'image floue

        
        
        
        // Add a button to add to librar
           JButton addToLibraryButton = createRoundedButton("Add to Library", white, darkBlue);
           addToLibraryButton.setFont(new Font("Arial", Font.BOLD, 14));  // Button font styling
           addToLibraryButton.setBackground(new Color(0x4CAF50));  // Green color
           addToLibraryButton.setForeground(Color.WHITE);  // White text
           addToLibraryButton.setFocusPainted(false);
           addToLibraryButton.setBounds(460, 360, 150, 40); 

           addToLibraryButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));  // Button padding
           addToLibraryButton.setCursor(new Cursor(Cursor.HAND_CURSOR));  // Hand cursor on hover
           addToLibraryButton.setAlignmentX(Component.CENTER_ALIGNMENT);  // Center the button

           // Action to add the comic to the library
           addToLibraryButton.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent e) {
                   try {
                       int currentUserId = UserSession.getInstance().getUserId();
                       
                       if (currentUserId == -1) {
                           JOptionPane.showMessageDialog(IssueDetailsPanel.this,
                                   "Veuillez vous connecter pour ajouter des comics à votre bibliothèque.",
                                   "Non connecté",
                                   JOptionPane.WARNING_MESSAGE);
                           return;
                       }

                       boolean added = userController.addIssueToFavorites(currentUserId, issue.getApi_detail_url());
                       
                       if (added) {
                           JOptionPane.showMessageDialog(IssueDetailsPanel.this,
                                   "Comic ajouté à votre bibliothèque !",
                                   "Succès",
                                   JOptionPane.INFORMATION_MESSAGE);

                           if (libraryPage != null) {
                               libraryPage.reloadLibraryIssues();
                           }
                       } else {
                           JOptionPane.showMessageDialog(IssueDetailsPanel.this,
                                   "Ce comic est déjà dans votre bibliothèque.",
                                   "Info",
                                   JOptionPane.INFORMATION_MESSAGE);
                       }
                   } catch (Exception ex) {
                       JOptionPane.showMessageDialog(IssueDetailsPanel.this,
                               "Erreur lors de l'ajout du comic à votre bibliothèque. Veuillez réessayer.",
                               "Erreur",
                               JOptionPane.ERROR_MESSAGE);
                       ex.printStackTrace();
                   }
               }
           });




           // Add the button to the panel
           contentPanel.add(addToLibraryButton);

           // Add a stylish "Back to list" button
           JButton backButton = createRoundedButton("Back to list",lightRed, white);
           backButton.setFont(new Font("Arial", Font.BOLD, 14));  // Button font styling
           backButton.setBackground(new Color(0x3F51B5));  // Stylish blue color
           backButton.setForeground(Color.WHITE);  // White text
           backButton.setBounds(700, 360, 150, 40); 

           backButton.setFocusPainted(false);
           backButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));  // Button padding
           backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));  // Hand cursor on hover
           backButton.setAlignmentX(Component.CENTER_ALIGNMENT);  // Center the button

           // Action to go back to the list
           backButton.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent e) {
                   if (searchPage != null) {
                       // Retour à la liste de recherche
                       searchPage.updateResultsDisplay();
                   } else if (mainFrame != null) {
                       // Retour à la page d'accueil
                       mainFrame.showPanel("home");
                   }
               }
           });

           contentPanel.add(backButton); // Add the back button

           // Mouse wheel listener to navigate back
           addMouseWheelListener(e -> {
               if (e.getWheelRotation() < 0) {  // Scrolling up
                   searchPage.updateResultsDisplay();
               }
           });

           // Adding some space below the panel
           add(Box.createVerticalStrut(20));
           
           
           
           
        

        // Image label with better spacing
       
        
        
        try {
            URL imageUrl = new URL(issue.getImage());
            BufferedImage image = ImageIO.read(imageUrl);

            // Créer un JPanel personnalisé pour gérer l'image de fond floue
            JPanel layeredPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;

                    // Appliquer un effet flou sur l'image en arrière-plan
                    int panelWidth = getWidth();
                    int panelHeight = getHeight();

                    Image scaledBackground = image.getScaledInstance(panelWidth, panelHeight, Image.SCALE_SMOOTH);
                    BufferedImage blurredImage = new BufferedImage(panelWidth, panelHeight, BufferedImage.TYPE_INT_ARGB);

                    Graphics2D gBlur = blurredImage.createGraphics();
                    gBlur.drawImage(scaledBackground, 0, 0, null);
                    gBlur.dispose();

                    // Simuler un effet de flou (léger)
                    for (int i = 0; i < 5; i++) {
                        gBlur = blurredImage.createGraphics();
                        gBlur.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                        gBlur.drawImage(blurredImage, -5, 0, null);
                        gBlur.drawImage(blurredImage, 5, 0, null);
                        gBlur.drawImage(blurredImage, 0, -5, null);
                        gBlur.drawImage(blurredImage, 0, 5, null);
                        gBlur.dispose();
                    }

                    g2d.drawImage(blurredImage, 0, 0, null);

                    // Dessiner un rectangle semi-transparent par-dessus pour assombrir l'arrière-plan
                    g2d.setColor(new Color(0, 0, 0, 180)); // Noir avec opacité
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            };

            layeredPanel.setLayout(null); // Positionnement absolu
            layeredPanel.setBounds(0, 0, 1550, 470); // Dimensions du panneau principal
            contentPanel.add(layeredPanel); // Ajouter le panneau flou

            // Ajouter l'image claire par-dessus
            ImageIcon icon = new ImageIcon(image.getScaledInstance(400, 400, Image.SCALE_SMOOTH));
            JLabel imageLabel = new JLabel(icon);
            imageLabel.setBounds(60, 50, 350, 360); // Position de l'image claire
            layeredPanel.add(imageLabel);

            // Ajouter des informations textuelles





        } catch (IOException e) {
            e.printStackTrace();
        }
        
        
        
        
        
        
        


        // Adding labels
        
        
        
        // Cadre arrondi pour les détails
        JPanel detailsPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Dessiner le fond avec des coins arrondis
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                // Dessiner la bordure avec des coins arrondis
                g2.setColor(getForeground());
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 20, 20); // Ajuster les dimensions pour éviter les débordements
            }

            @Override
            protected void paintBorder(Graphics g) {
                // Désactiver la peinture de la bordure par défaut
            }
        };
        detailsPanel.setOpaque(false); // Empêche la peinture d'un arrière-plan rectangulaire par défaut
        detailsPanel.setLayout(null); // Positionnement manuel
        detailsPanel.setBackground(new Color(10, 12, 30)); // Couleur semi-transparente
        //detailsPanel.setForeground(Color.WHITE); // Couleur de la bordure
        detailsPanel.setBounds(100, 420, 1300, 4000);

        displayCharacters(detailsPanel, issue);
        displayAuthors(detailsPanel, issue);
        leftColumnY = Math.max(leftColumnY, rightColumnY) + 50; // Réaligner les colonnes si nécessaire
        displayTeams(detailsPanel, issue);
        displayLocations(detailsPanel, issue);
        displayConcepts(detailsPanel, issue);
        displayObjects(detailsPanel, issue);
        displayStoryArcs(detailsPanel, issue);

        // Ajouter le panneau au parent (déjà présent dans votre code)
        contentPanel.add(detailsPanel);

        // Réorganiser les couches pour s'assurer que detailsPanel est au-dessus
        contentPanel.setComponentZOrder(detailsPanel, 0); // Met detailsPanel tout en haut

       

        
        
        
        
    
        
     // Add the content panel to a JScrollPane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Smooth scrolling
        scrollPane.setBounds(0, 0, 1280, 720); // Taille visible de la fenêtre

        // Add the JScrollPane to this panel
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
    }
    
    // Ajouter un constructeur supplémentaire pour la page d'accueil
    public IssueDetailsPanel(Issue issue, MainFrame mainFrame, LibraryPage libraryPage) throws IOException, InterruptedException {
        this.searchPage = null;
        this.homePage = null;
        this.mainFrame = mainFrame; // Initialiser avec le mainFrame passé en paramètre
        // Le reste du code du constructeur est identique, mais copié ici
        // Initialiser les DAOs et services nécessaires
        VolumeDAO volumeDAO = new VolumeDAO();
        IssueDAO issueDAO = new IssueDAO();
        UserIssueDAO userIssueDAO = new UserIssueDAO();
        AuteurDAO auteurDAO = new AuteurDAO();
        GenreDAO genreDAO = new GenreDAO();
        PersonnageDAO personnageDAO = new PersonnageDAO();
        PowerDAO powerDAO = new PowerDAO();
        LocationDAO locationDAO = new LocationDAO();
        TeamDAO teamDAO = new TeamDAO();
        ComicObjectDAO comicObjectDAO = new ComicObjectDAO();
        StoryArcDAO storyArcDAO = new StoryArcDAO();
        ApiClient apiClient = new ApiClient();

        UserService userService = new UserService(volumeDAO, issueDAO, userIssueDAO, auteurDAO, 
            genreDAO, personnageDAO, apiClient, powerDAO, locationDAO, teamDAO, 
            comicObjectDAO, storyArcDAO);
        
        this.userController = new UserController(userService);

        // Set the layout with padding for spacing
    	
    	
        JPanel contentPanel = new JPanel();

        contentPanel.setLayout(null); // Positionnement absolu
        Color darkBlue = new Color(10, 12, 30);
        Color white = Color.WHITE;
        Color lightRed = new Color(255, 85, 85);


        contentPanel.setBackground(darkBlue);
        contentPanel.setPreferredSize(new Dimension(1280, 4000)); // Taille suffisante pour activer le défilement
 
        
        // Titre du personnage
        JLabel nameLabel = new JLabel("Issue: " + issue.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 30));
        nameLabel.setForeground(white);
        nameLabel.setBounds(470, 50, 600, 50);
        contentPanel.setComponentZOrder(nameLabel, 0); // Met le titre au-dessus de l'image floue
        
        
        JLabel issueNumberLabel = new JLabel("Issue Number: " + issue.getIssueNumber());
        issueNumberLabel.setFont(new Font("Arial", Font.PLAIN, 16));  // Smaller font for number
        issueNumberLabel.setForeground(new Color(180,180,180));  // Dark gray for readability
        issueNumberLabel.setBounds(480, 90, 500, 30); 
        contentPanel.setComponentZOrder(issueNumberLabel, 0); // Met le titre au-dessus de l'image floue



        JLabel coverDateLabel = new JLabel("Cover Date: " + issue.getCoverDate());
        coverDateLabel.setFont(new Font("Arial", Font.PLAIN, 16));  // Font styling
        coverDateLabel.setForeground(new Color(180,180,180));
        coverDateLabel.setBounds(480, 120, 400, 30); 
        contentPanel.setComponentZOrder(coverDateLabel, 0); // Met le titre au-dessus de l'image floue


        JLabel VolumeLabel = new JLabel("Volume: " + issue.getVolume().getName());
        VolumeLabel.setFont(new Font("Arial", Font.PLAIN, 16));  // Font styling
        VolumeLabel.setForeground(new Color(180,180,180));
        VolumeLabel.setBounds(480, 150, 400, 30); 
        contentPanel.setComponentZOrder(VolumeLabel, 0); // Met le titre au-dessus de l'image floue


        


     // Description du personnage
        JLabel descriptionLabel = new JLabel("<html><body style='width: 750px;'>" + issue.getDescription() + "</body></html>");
        descriptionLabel.setFont(new Font("Arial", Font.ITALIC, 18));
        descriptionLabel.setForeground(white);

        // Supprimer la bordure et définir l'arrière-plan comme transparent
        descriptionLabel.setOpaque(false);
        descriptionLabel.setVerticalAlignment(SwingConstants.TOP); // Alignement vertical du texte

        // Encapsuler le JLabel dans un JScrollPane
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionLabel);
        descriptionScrollPane.setBounds(450, 190, 980, 150); // Position et taille du slider
        descriptionScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); // Barre de défilement verticale
        descriptionScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // Pas de défilement horizontal
        descriptionScrollPane.setOpaque(false);
        descriptionScrollPane.getViewport().setOpaque(false); // Fond transparent pour correspondre à votre design
        descriptionScrollPane.setBorder(null); // Supprimer les bordures par défaut

        // Ajouter le JScrollPane au contentPanel
        contentPanel.add(descriptionScrollPane);
        contentPanel.setComponentZOrder(descriptionScrollPane, 0); // Met la description au-dessus de l'image floue

        
        
        
        // Add a button to add to librar
           JButton addToLibraryButton = createRoundedButton("Add to Library", white, darkBlue);
           addToLibraryButton.setFont(new Font("Arial", Font.BOLD, 14));  // Button font styling
           addToLibraryButton.setBackground(new Color(0x4CAF50));  // Green color
           addToLibraryButton.setForeground(Color.WHITE);  // White text
           addToLibraryButton.setFocusPainted(false);
           addToLibraryButton.setBounds(460, 360, 150, 40); 

           addToLibraryButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));  // Button padding
           addToLibraryButton.setCursor(new Cursor(Cursor.HAND_CURSOR));  // Hand cursor on hover
           addToLibraryButton.setAlignmentX(Component.CENTER_ALIGNMENT);  // Center the button

           // Action to add the comic to the library
           addToLibraryButton.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent e) {
                   try {
                       int currentUserId = UserSession.getInstance().getUserId();
                       
                       if (currentUserId == -1) {
                           JOptionPane.showMessageDialog(IssueDetailsPanel.this,
                                   "Veuillez vous connecter pour ajouter des comics à votre bibliothèque.",
                                   "Non connecté",
                                   JOptionPane.WARNING_MESSAGE);
                           return;
                       }

                       boolean added = userController.addIssueToFavorites(currentUserId, issue.getApi_detail_url());
                       
                       if (added) {
                           JOptionPane.showMessageDialog(IssueDetailsPanel.this,
                                   "Comic ajouté à votre bibliothèque !",
                                   "Succès",
                                   JOptionPane.INFORMATION_MESSAGE);

                           if (libraryPage != null) {
                               libraryPage.reloadLibraryIssues();
                           }
                       } else {
                           JOptionPane.showMessageDialog(IssueDetailsPanel.this,
                                   "Ce comic est déjà dans votre bibliothèque.",
                                   "Info",
                                   JOptionPane.INFORMATION_MESSAGE);
                       }
                   } catch (Exception ex) {
                       JOptionPane.showMessageDialog(IssueDetailsPanel.this,
                               "Erreur lors de l'ajout du comic à votre bibliothèque. Veuillez réessayer.",
                               "Erreur",
                               JOptionPane.ERROR_MESSAGE);
                       ex.printStackTrace();
                   }
               }
           });




           // Add the button to the panel
           contentPanel.add(addToLibraryButton);

           // Add a stylish "Back to list" button
           JButton backButton = createRoundedButton("Back to list",lightRed, white);
           backButton.setFont(new Font("Arial", Font.BOLD, 14));  // Button font styling
           backButton.setBackground(new Color(0x3F51B5));  // Stylish blue color
           backButton.setForeground(Color.WHITE);  // White text
           backButton.setBounds(700, 360, 150, 40); 

           backButton.setFocusPainted(false);
           backButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));  // Button padding
           backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));  // Hand cursor on hover
           backButton.setAlignmentX(Component.CENTER_ALIGNMENT);  // Center the button

           // Action to go back to the list
           backButton.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent e) {
                   if (searchPage != null) {
                       // Retour à la liste de recherche
                       searchPage.updateResultsDisplay();
                   } else if (mainFrame != null) {
                       // Retour à la page d'accueil
                       mainFrame.showPanel("home");
                   }
               }
           });

           contentPanel.add(backButton); // Add the back button

           // Mouse wheel listener to navigate back
           addMouseWheelListener(e -> {
               if (e.getWheelRotation() < 0) {  // Scrolling up
                   searchPage.updateResultsDisplay();
               }
           });

           // Adding some space below the panel
           add(Box.createVerticalStrut(20));
           
           
           
           
        

        // Image label with better spacing
       
        
        
        try {
            URL imageUrl = new URL(issue.getImage());
            BufferedImage image = ImageIO.read(imageUrl);

            // Créer un JPanel personnalisé pour gérer l'image de fond floue
            JPanel layeredPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;

                    // Appliquer un effet flou sur l'image en arrière-plan
                    int panelWidth = getWidth();
                    int panelHeight = getHeight();

                    Image scaledBackground = image.getScaledInstance(panelWidth, panelHeight, Image.SCALE_SMOOTH);
                    BufferedImage blurredImage = new BufferedImage(panelWidth, panelHeight, BufferedImage.TYPE_INT_ARGB);

                    Graphics2D gBlur = blurredImage.createGraphics();
                    gBlur.drawImage(scaledBackground, 0, 0, null);
                    gBlur.dispose();

                    // Simuler un effet de flou (léger)
                    for (int i = 0; i < 5; i++) {
                        gBlur = blurredImage.createGraphics();
                        gBlur.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                        gBlur.drawImage(blurredImage, -5, 0, null);
                        gBlur.drawImage(blurredImage, 5, 0, null);
                        gBlur.drawImage(blurredImage, 0, -5, null);
                        gBlur.drawImage(blurredImage, 0, 5, null);
                        gBlur.dispose();
                    }

                    g2d.drawImage(blurredImage, 0, 0, null);

                    // Dessiner un rectangle semi-transparent par-dessus pour assombrir l'arrière-plan
                    g2d.setColor(new Color(0, 0, 0, 180)); // Noir avec opacité
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            };

            layeredPanel.setLayout(null); // Positionnement absolu
            layeredPanel.setBounds(0, 0, 1550, 470); // Dimensions du panneau principal
            contentPanel.add(layeredPanel); // Ajouter le panneau flou

            // Ajouter l'image claire par-dessus
            ImageIcon icon = new ImageIcon(image.getScaledInstance(400, 400, Image.SCALE_SMOOTH));
            JLabel imageLabel = new JLabel(icon);
            imageLabel.setBounds(60, 50, 350, 360); // Position de l'image claire
            layeredPanel.add(imageLabel);

            // Ajouter des informations textuelles





        } catch (IOException e) {
            e.printStackTrace();
        }
        
        
        
        
        
        
        


        // Adding labels
        
        
        
        // Cadre arrondi pour les détails
        JPanel detailsPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Dessiner le fond avec des coins arrondis
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                // Dessiner la bordure avec des coins arrondis
                g2.setColor(getForeground());
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 20, 20); // Ajuster les dimensions pour éviter les débordements
            }

            @Override
            protected void paintBorder(Graphics g) {
                // Désactiver la peinture de la bordure par défaut
            }
        };
        detailsPanel.setOpaque(false); // Empêche la peinture d'un arrière-plan rectangulaire par défaut
        detailsPanel.setLayout(null); // Positionnement manuel
        detailsPanel.setBackground(new Color(10, 12, 30)); // Couleur semi-transparente
        //detailsPanel.setForeground(Color.WHITE); // Couleur de la bordure
        detailsPanel.setBounds(100, 420, 1300, 4000);

        displayCharacters(detailsPanel, issue);
        displayAuthors(detailsPanel, issue);
        leftColumnY = Math.max(leftColumnY, rightColumnY) + 50; // Réaligner les colonnes si nécessaire
        displayTeams(detailsPanel, issue);
        displayLocations(detailsPanel, issue);
        displayConcepts(detailsPanel, issue);
        displayObjects(detailsPanel, issue);
        displayStoryArcs(detailsPanel, issue);

        // Ajouter le panneau au parent (déjà présent dans votre code)
        contentPanel.add(detailsPanel);

        // Réorganiser les couches pour s'assurer que detailsPanel est au-dessus
        contentPanel.setComponentZOrder(detailsPanel, 0); // Met detailsPanel tout en haut

       

        
        
        
        
    
        
     // Add the content panel to a JScrollPane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Smooth scrolling
        scrollPane.setBounds(0, 0, 1280, 720); // Taille visible de la fenêtre

        // Add the JScrollPane to this panel
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
    }

    private JButton createRoundedButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30); // Rayon de 20 pour les bords arrondis
                g2.setColor(fgColor);
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int textHeight = fm.getAscent();
                g2.drawString(getText(), (getWidth() - textWidth) / 2, (getHeight() + textHeight) / 2 - 2);
                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                // Ne dessine aucune bordure
            }

            @Override
            public void setContentAreaFilled(boolean b) {
                // Empêche le remplissage par défaut
            }
        };
        button.setForeground(fgColor); // Couleur du texte
        button.setFocusPainted(false); // Désactive l'effet de focus
        button.setBorderPainted(false); // Désactive la bordure par défaut
        button.setFont(new Font("Arial", Font.BOLD, 14)); // Police du bouton
        return button;
    }

    public IssueDetailsPanel() {
        // Créer un panneau de contenu avec la même structure
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(null);
        Color darkBlue = new Color(10, 12, 30);
        Color white = Color.WHITE;
        contentPanel.setBackground(darkBlue);
        contentPanel.setPreferredSize(new Dimension(1280, 4000));

        // Titre "Chargement..."
        JLabel loadingLabel = new JLabel("Chargement des détails...");
        loadingLabel.setFont(new Font("Arial", Font.BOLD, 30));
        loadingLabel.setForeground(white);
        loadingLabel.setBounds(470, 50, 600, 50);
        contentPanel.add(loadingLabel);

        // Panneau pour les détails en cours de chargement
        JPanel detailsPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(getForeground());
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 20, 20);
            }
        };
        detailsPanel.setOpaque(false);
        detailsPanel.setLayout(null);
        detailsPanel.setBackground(new Color(10, 12, 30));
        detailsPanel.setBounds(100, 420, 1300, 4000);

        // Ajouter les sections en cours de chargement
        addLoadingSection(detailsPanel, "Characters", 50, 100);
        addLoadingSection(detailsPanel, "Auteurs", 700, 50);
        addLoadingSection(detailsPanel, "Teams", 100, 700);
        addLoadingSection(detailsPanel, "Locations", 700, 700);
        addLoadingSection(detailsPanel, "Concepts", 1000, 1000);
        addLoadingSection(detailsPanel, "Objects", 700, 1000);
        addLoadingSection(detailsPanel, "Story Arcs", 100, 1500);

        contentPanel.add(detailsPanel);

        // Ajouter le panneau dans un JScrollPane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBounds(0, 0, 1280, 720);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
    }

    private void addLoadingSection(JPanel panel, String title, int x, int y) {
        // Titre de la section
        JLabel titleLabel = new JLabel(title + ":");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(new Color(0xFFFFFF));
        titleLabel.setBounds(x, y, 200, 30);
        panel.add(titleLabel);

        // Message de chargement
        JLabel loadingLabel = new JLabel("Chargement en cours...");
        loadingLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        loadingLabel.setForeground(new Color(180, 180, 180));
        loadingLabel.setBounds(x + 20, y + 50, 400, 20);
        panel.add(loadingLabel);

        // Indicateur de chargement
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setBounds(x + 20, y + 80, 200, 20);
        progressBar.setBackground(new Color(30, 38, 48));
        progressBar.setForeground(new Color(51, 90, 183));
        panel.add(progressBar);
    }

    private void displayCharacters(JPanel detailsPanel, Issue issue) {
        JLabel charactersLabel = new JLabel("Characters:");
        charactersLabel.setFont(new Font("Arial", Font.BOLD, 16));
        charactersLabel.setForeground(new Color(0xFFFFFF));
        charactersLabel.setBounds(100, leftColumnY, 200, 30);
        detailsPanel.add(charactersLabel);
        leftColumnY += 50;  // Espace après le titre

        if (issue.getPersonnages() != null && !issue.getPersonnages().isEmpty()) {
            for (Personnage personnage : issue.getPersonnages()) {
                JLabel nameLabel = new JLabel(personnage.getName());
                nameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                nameLabel.setForeground(new Color(200, 200, 200));
                nameLabel.setBounds(200, leftColumnY, 300, 20);
                detailsPanel.add(nameLabel);

                try {
                    if (personnage.getImage() != null) {
                        URL imageUrl = new URL(personnage.getImage());
                        ImageIcon icon = new ImageIcon(new ImageIcon(imageUrl).getImage()
                            .getScaledInstance(50, 50, Image.SCALE_SMOOTH));
                        JLabel imageLabel = new JLabel(icon);
                        imageLabel.setBounds(120, leftColumnY - 10, 50, 50);
                        detailsPanel.add(imageLabel);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                leftColumnY += 70;  // Espace pour le prochain personnage
            }
        } else {
            JLabel noCharactersLabel = new JLabel("No characters available.");
            noCharactersLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            noCharactersLabel.setForeground(new Color(180, 180, 180));
            noCharactersLabel.setBounds(120, leftColumnY, 400, 20);
            detailsPanel.add(noCharactersLabel);
            leftColumnY += 40;  // Espace après le message
        }

        leftColumnY += 30;  // Espace supplémentaire après la section
    }

    private void displayAuthors(JPanel detailsPanel, Issue issue) {
        JLabel authorsLabel = new JLabel("Auteurs:");
        authorsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        authorsLabel.setForeground(new Color(0xFFFFFF));
        authorsLabel.setBounds(700, rightColumnY, 200, 30);
        detailsPanel.add(authorsLabel);
        rightColumnY += 50;  // Espace après le titre

        if (issue.getAuteurs() != null && !issue.getAuteurs().isEmpty()) {
            for (Auteur auteur : issue.getAuteurs()) {
                JLabel nameLabel = new JLabel(auteur.getName() + " (" + auteur.getRole() + ")");
                nameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                nameLabel.setForeground(new Color(200, 200, 200));
                nameLabel.setBounds(800, rightColumnY, 400, 20);
                detailsPanel.add(nameLabel);

                try {
                    if (auteur.getImage() != null) {
                        URL imageUrl = new URL(auteur.getImage());
                        ImageIcon icon = new ImageIcon(new ImageIcon(imageUrl).getImage()
                            .getScaledInstance(50, 50, Image.SCALE_SMOOTH));
                        JLabel imageLabel = new JLabel(icon);
                        imageLabel.setBounds(720, rightColumnY - 10, 50, 50);
                        detailsPanel.add(imageLabel);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                rightColumnY += 70;  // Espace pour le prochain auteur
            }
        } else {
            JLabel noAuthorsLabel = new JLabel("No authors available.");
            noAuthorsLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            noAuthorsLabel.setForeground(new Color(180, 180, 180));
            noAuthorsLabel.setBounds(720, rightColumnY, 400, 20);
            detailsPanel.add(noAuthorsLabel);
            rightColumnY += 40;  // Espace après le message
        }

        rightColumnY += 30;  // Espace supplémentaire après la section
    }

    private void displayTeams(JPanel detailsPanel, Issue issue) {
        JLabel teamsLabel = new JLabel("Teams:");
        teamsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        teamsLabel.setForeground(new Color(0xFFFFFF));
        teamsLabel.setBounds(100, leftColumnY, 200, 30);
        detailsPanel.add(teamsLabel);
        leftColumnY += 50;  // Espace après le titre

        if (issue.getTeams() != null && !issue.getTeams().isEmpty()) {
            for (Team team : issue.getTeams()) {
                JLabel nameLabel = new JLabel(team.getName());
                nameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                nameLabel.setForeground(new Color(200, 200, 200));
                nameLabel.setBounds(200, leftColumnY, 300, 20);
                detailsPanel.add(nameLabel);

                try {
                    if (team.getImage() != null) {
                        URL imageUrl = new URL(team.getImage());
                        ImageIcon icon = new ImageIcon(new ImageIcon(imageUrl).getImage()
                            .getScaledInstance(50, 50, Image.SCALE_SMOOTH));
                        JLabel imageLabel = new JLabel(icon);
                        imageLabel.setBounds(120, leftColumnY - 10, 50, 50);
                        detailsPanel.add(imageLabel);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                leftColumnY += 70;  // Espace pour la prochaine équipe
            }
        } else {
            JLabel noTeamsLabel = new JLabel("No teams available.");
            noTeamsLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            noTeamsLabel.setForeground(new Color(180, 180, 180));
            noTeamsLabel.setBounds(120, leftColumnY, 400, 20);
            detailsPanel.add(noTeamsLabel);
            leftColumnY += 40;  // Espace après le message
        }

        leftColumnY += 30;  // Espace supplémentaire après la section
    }

    private void displayLocations(JPanel detailsPanel, Issue issue) {
        JLabel locationsLabel = new JLabel("Locations:");
        locationsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        locationsLabel.setForeground(new Color(0xFFFFFF));
        locationsLabel.setBounds(700, rightColumnY, 200, 30);
        detailsPanel.add(locationsLabel);
        rightColumnY += 50;  // Espace après le titre

        if (issue.getLocations() != null && !issue.getLocations().isEmpty()) {
            for (Location location : issue.getLocations()) {
                JLabel nameLabel = new JLabel(location.getName());
                nameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                nameLabel.setForeground(new Color(200, 200, 200));
                nameLabel.setBounds(800, rightColumnY, 400, 20);
                detailsPanel.add(nameLabel);

                try {
                    if (location.getImage() != null) {
                        URL imageUrl = new URL(location.getImage());
                        ImageIcon icon = new ImageIcon(new ImageIcon(imageUrl).getImage()
                            .getScaledInstance(50, 50, Image.SCALE_SMOOTH));
                        JLabel imageLabel = new JLabel(icon);
                        imageLabel.setBounds(720, rightColumnY - 10, 50, 50);
                        detailsPanel.add(imageLabel);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                rightColumnY += 70;  // Espace pour le prochain lieu
            }
        } else {
            JLabel noLocationsLabel = new JLabel("No locations available.");
            noLocationsLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            noLocationsLabel.setForeground(new Color(180, 180, 180));
            noLocationsLabel.setBounds(720, rightColumnY, 400, 20);
            detailsPanel.add(noLocationsLabel);
            rightColumnY += 40;  // Espace après le message
        }

        rightColumnY += 30;  // Espace supplémentaire après la section
    }

    private void displayConcepts(JPanel detailsPanel, Issue issue) {
        JLabel conceptsLabel = new JLabel("Concepts:");
        conceptsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        conceptsLabel.setForeground(new Color(0xFFFFFF));
        conceptsLabel.setBounds(100, leftColumnY, 200, 30);
        detailsPanel.add(conceptsLabel);
        leftColumnY += 50;  // Espace après le titre

        if (issue.getGenres() != null && !issue.getGenres().isEmpty()) {
            for (Genre genre : issue.getGenres()) {
                JLabel nameLabel = new JLabel(genre.getName());
                nameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                nameLabel.setForeground(new Color(200, 200, 200));
                nameLabel.setBounds(200, leftColumnY, 300, 20);
                detailsPanel.add(nameLabel);

                try {
                    if (genre.getImage() != null) {
                        URL imageUrl = new URL(genre.getImage());
                        ImageIcon icon = new ImageIcon(new ImageIcon(imageUrl).getImage()
                            .getScaledInstance(50, 50, Image.SCALE_SMOOTH));
                        JLabel imageLabel = new JLabel(icon);
                        imageLabel.setBounds(120, leftColumnY - 10, 50, 50);
                        detailsPanel.add(imageLabel);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                leftColumnY += 70;  // Espace pour le prochain concept
            }
        } else {
            JLabel noConceptsLabel = new JLabel("No concepts available.");
            noConceptsLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            noConceptsLabel.setForeground(new Color(180, 180, 180));
            noConceptsLabel.setBounds(120, leftColumnY, 400, 20);
            detailsPanel.add(noConceptsLabel);
            leftColumnY += 40;  // Espace après le message
        }

        leftColumnY += 30;  // Espace supplémentaire après la section
    }

    private void displayObjects(JPanel detailsPanel, Issue issue) {
        JLabel objectsLabel = new JLabel("Objects:");
        objectsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        objectsLabel.setForeground(new Color(0xFFFFFF));
        objectsLabel.setBounds(700, rightColumnY, 200, 30);
        detailsPanel.add(objectsLabel);
        rightColumnY += 50;  // Espace après le titre

        if (issue.getObjects() != null && !issue.getObjects().isEmpty()) {
            for (ComicObject object : issue.getObjects()) {
                JLabel nameLabel = new JLabel(object.getName());
                nameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                nameLabel.setForeground(new Color(200, 200, 200));
                nameLabel.setBounds(800, rightColumnY, 400, 20);
                detailsPanel.add(nameLabel);

                try {
                    if (object.getImage() != null) {
                        URL imageUrl = new URL(object.getImage());
                        ImageIcon icon = new ImageIcon(new ImageIcon(imageUrl).getImage()
                            .getScaledInstance(50, 50, Image.SCALE_SMOOTH));
                        JLabel imageLabel = new JLabel(icon);
                        imageLabel.setBounds(720, rightColumnY - 10, 50, 50);
                        detailsPanel.add(imageLabel);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                rightColumnY += 70;  // Espace pour le prochain objet
            }
        } else {
            JLabel noObjectsLabel = new JLabel("No objects available.");
            noObjectsLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            noObjectsLabel.setForeground(new Color(180, 180, 180));
            noObjectsLabel.setBounds(720, rightColumnY, 400, 20);
            detailsPanel.add(noObjectsLabel);
            rightColumnY += 40;  // Espace après le message
        }

        rightColumnY += 30;  // Espace supplémentaire après la section
    }

    private void displayStoryArcs(JPanel detailsPanel, Issue issue) {
        JLabel storyArcsLabel = new JLabel("Story Arcs:");
        storyArcsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        storyArcsLabel.setForeground(new Color(0xFFFFFF));
        storyArcsLabel.setBounds(100, leftColumnY, 200, 30);
        detailsPanel.add(storyArcsLabel);
        leftColumnY += 50;  // Espace après le titre

        if (issue.getStoryArcs() != null && !issue.getStoryArcs().isEmpty()) {
            for (StoryArc storyArc : issue.getStoryArcs()) {
                JLabel nameLabel = new JLabel(storyArc.getName());
                nameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                nameLabel.setForeground(new Color(200, 200, 200));
                nameLabel.setBounds(200, leftColumnY, 300, 20);
                detailsPanel.add(nameLabel);

                try {
                    if (storyArc.getImage() != null) {
                        URL imageUrl = new URL(storyArc.getImage());
                        ImageIcon icon = new ImageIcon(new ImageIcon(imageUrl).getImage()
                            .getScaledInstance(50, 50, Image.SCALE_SMOOTH));
                        JLabel imageLabel = new JLabel(icon);
                        imageLabel.setBounds(120, leftColumnY - 10, 50, 50);
                        detailsPanel.add(imageLabel);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                leftColumnY += 70;  // Espace pour le prochain story arc
            }
        } else {
            JLabel noStoryArcsLabel = new JLabel("No story arcs available.");
            noStoryArcsLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            noStoryArcsLabel.setForeground(new Color(180, 180, 180));
            noStoryArcsLabel.setBounds(120, leftColumnY, 400, 20);
            detailsPanel.add(noStoryArcsLabel);
            leftColumnY += 40;  // Espace après le message
        }

        leftColumnY += 30;  // Espace supplémentaire après la section
    }
}
