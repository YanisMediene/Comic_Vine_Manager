package tse.info2.view;

import tse.info2.session.UserSession;
import tse.info2.database.UserIssueDAO.IssueStatus;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.net.URL;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;

import tse.info2.database.AuteurDAO;
import tse.info2.database.ComicObjectDAO;
import tse.info2.database.GenreDAO;
import tse.info2.database.PersonnageDAO;
import tse.info2.database.StoryArcDAO;
import tse.info2.database.TeamDAO;
import tse.info2.database.UserIssueDAO;
import tse.info2.model.Auteur;
import tse.info2.model.ComicObject;
import tse.info2.model.Genre;
import tse.info2.model.Issue;
import tse.info2.model.Personnage;
import tse.info2.model.StoryArc;
import tse.info2.model.Team;


public class LibraryPage extends JPanel {

    private JPanel issuesPanel; // Panel to display issues
    private JPanel detailsPanel; // Panel to display issue details
    private JScrollPane scrollPane; // Scrollable view for issues
    private List<Issue> libraryIssues; // List of issues in the library

    private SearchPage parentPage; // Reference to the existing SearchPage
    private ComicHomePage comicHomePage;
    private MainFrame mainFrame;

    private boolean showOnlyFavorites = false;
    private boolean showOnlyPurchased = false;
    private String selectedReadingStatus = "All"; // Default: show all comics

    // Constructor 1: Accepts a SearchPage reference
    public LibraryPage(SearchPage parentPage) {
        this.parentPage = parentPage; // Save the reference to the parent page
        this.comicHomePage = null; // No reference to ComicHomePage
        initialize();
        reloadLibraryIssues();
    }

    // Constructor 2: Accepts a ComicHomePage reference
    public LibraryPage(ComicHomePage comicHomePage) {
        this.comicHomePage = comicHomePage; // Save the reference to ComicHomePage
        this.parentPage = null; // No reference to SearchPage
        initialize();
        reloadLibraryIssues();
    }

    // Constructor 3: Accepts a MainFrame reference
    public LibraryPage(MainFrame mainFrame) {
        this.mainFrame = mainFrame; // Save the reference to MainFrame
        initialize();
        reloadLibraryIssues();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setBackground(new Color(10, 10, 30)); // Set a consistent background color

        // Main content panel for toolbar and issues
        JPanel mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new BorderLayout());
        mainContentPanel.setBackground(new Color(10, 10, 30)); // Consistent dark blue background

        // Add the toolbar at the top of the main content panel
        JPanel toolbar = createToolbar();
        mainContentPanel.add(toolbar, BorderLayout.NORTH);

        // Scrollable issues panel
        issuesPanel = new JPanel();
        issuesPanel.setLayout(new GridLayout(0, 4, 10, 10)); // 4 columns with spacing
        issuesPanel.setBackground(new Color(10, 10, 30)); // Really dark blue background

        JScrollPane issuesScrollPane = new JScrollPane(issuesPanel);
        issuesScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        issuesScrollPane.getViewport().setBackground(new Color(10, 10, 30)); // Ensure consistent dark blue
        mainContentPanel.add(issuesScrollPane, BorderLayout.CENTER);

        // Add the main content panel (with toolbar and issues) to the LibraryPage
        add(mainContentPanel, BorderLayout.CENTER);

        // Details panel (hidden by default)
        detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(50, 50, 70)),
            "Details",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            new Font("Arial", Font.BOLD, 14),
            Color.WHITE
        ));
        detailsPanel.setBackground(new Color(10, 10, 30)); // Really dark blue
        detailsPanel.setVisible(false);

        // Add the details panel to the right side
        add(detailsPanel, BorderLayout.EAST);

        // Load library issues from the database
        libraryIssues = loadIssuesFromDatabase();
        displayLibraryItems();
    }


    private JPanel createTopBar() {
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

        // Search bar, library button, and logout button centered
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
        searchPanel.setOpaque(false);

        // Left spacing
        searchPanel.add(Box.createHorizontalGlue());

        // Search bar
        JTextField searchField = new JTextField(30);
        searchField.setMaximumSize(new Dimension(300, 30));
        searchPanel.add(searchField);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0))); // Spacing between components

        // Search button
        JButton searchButton = new JButton("Search");
        searchButton.setPreferredSize(new Dimension(100, 30));
        searchButton.setBackground(new Color(0x4CAF50));
        searchButton.setForeground(Color.WHITE);
        searchPanel.add(searchButton);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0))); // Spacing between components

        // Library button
        JButton libraryButton = new JButton("Go to Library");
        libraryButton.setPreferredSize(new Dimension(150, 30));
        libraryButton.setBackground(new Color(0xFF5722));
        libraryButton.setForeground(Color.WHITE);
        libraryButton.setFocusPainted(false);
        libraryButton.addActionListener(e -> {
            if (comicHomePage != null) {
                comicHomePage.showLibraryPage(); // Navigate back to LibraryPage
            }
        });
        searchPanel.add(libraryButton);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0))); // Spacing between components

        // Logout button
        JButton logoutButton = new JButton("Logout");
        if (UserSession.getInstance().isGuest()) {
            logoutButton.setText("Login");
            logoutButton.setBackground(new Color(50, 205, 50));
        } else {
            logoutButton.setBackground(new Color(0xF44336));
        }
        logoutButton.setPreferredSize(new Dimension(120, 30));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> {
            UserSession.getInstance().clearSession();
            mainFrame.showPanel("login");
        });
        searchPanel.add(logoutButton);

        // Right spacing
        searchPanel.add(Box.createHorizontalGlue());

        // Add search panel to the top bar
        topBar.add(searchPanel, BorderLayout.CENTER);

        return topBar;
    }


 // Toolbar with Sort and Filter options
    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.setBackground(new Color(30, 30, 60)); // Really dark blue background

        // Sort Options Dropdown
        JComboBox<String> sortDropdown = new JComboBox<>(new String[]{"Sort by Title", "Sort by Issue Number", "Sort by Cover Date"});
        styleDropdown(sortDropdown); // Apply consistent styling to the dropdown
        sortDropdown.setPreferredSize(new Dimension(180, 40)); // Larger dropdown

        sortDropdown.addActionListener(e -> {
            int selectedIndex = sortDropdown.getSelectedIndex();
            sortIssues(selectedIndex); // Call sortIssues based on selection
        });
        toolbar.add(sortDropdown);

        // Favorites Filter Toggle
        JToggleButton favoritesButton = new JToggleButton("Show Favorites Only");
        styleToggleButton(favoritesButton, new Color(20, 20, 50), Color.WHITE, 16); // Dark blue background
        favoritesButton.setPreferredSize(new Dimension(180, 40)); // Larger button
        favoritesButton.addActionListener(e -> {
            showOnlyFavorites = favoritesButton.isSelected();
            reloadLibraryIssues();

            // Change text color based on selected state
            if (favoritesButton.isSelected()) {
                favoritesButton.setBackground(new Color(173, 216, 230)); // Light blue
                favoritesButton.setForeground(new Color(30, 30, 60)); // Dark blue text
            } else {
                favoritesButton.setBackground(new Color(20, 20, 50)); // Dark blue background
                favoritesButton.setForeground(Color.WHITE); // White text
            }
        });
        toolbar.add(favoritesButton);

        // Purchase Filter Toggle
        JToggleButton purchaseButton = new JToggleButton("Show Purchased Only");
        styleToggleButton(purchaseButton, new Color(20, 20, 50), Color.WHITE, 16); // Dark blue background
        purchaseButton.setPreferredSize(new Dimension(180, 40)); // Larger button
        purchaseButton.addActionListener(e -> {
            showOnlyPurchased = purchaseButton.isSelected();
            reloadLibraryIssues();

            // Change text color based on selected state
            if (purchaseButton.isSelected()) {
                purchaseButton.setBackground(new Color(173, 216, 230)); // Light blue
                purchaseButton.setForeground(new Color(30, 30, 60)); // Dark blue text
            } else {
                purchaseButton.setBackground(new Color(20, 20, 50)); // Dark blue background
                purchaseButton.setForeground(Color.WHITE); // White text
            }
        });
        toolbar.add(purchaseButton);

        // Reading Status Filter
        JComboBox<String> readingStatusCombo = new JComboBox<>(new String[]{"All", "Not Started", "In Progress", "Completed"});
        styleDropdown(readingStatusCombo); // Apply consistent styling to the dropdown
        readingStatusCombo.setPreferredSize(new Dimension(180, 40)); // Larger dropdown
        readingStatusCombo.addActionListener(e -> {
            selectedReadingStatus = (String) readingStatusCombo.getSelectedItem();
            reloadLibraryIssues();
        });
        toolbar.add(readingStatusCombo);

        return toolbar;
    }

    // Style dropdown consistently
    private void styleDropdown(JComboBox<String> dropdown) {
        dropdown.setBackground(new Color(30, 30, 60)); // Dark blue dropdown background
        dropdown.setForeground(Color.WHITE); // White text
        dropdown.setFont(new Font("Arial", Font.BOLD, 14)); // Bold font
        dropdown.setFocusable(false); // Remove focus border
        dropdown.setBorder(BorderFactory.createLineBorder(new Color(173, 216, 230), 2)); // Light blue border
        dropdown.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setBackground(isSelected ? new Color(20, 20, 50) : new Color(30, 30, 60)); // Darker blue for selected, dark blue for others
                label.setForeground(Color.WHITE); // White text for all items
                return label;
            }
        });
    }

    // Style toggle buttons consistently
    private void styleToggleButton(JToggleButton button, Color background, Color foreground, int fontSize) {
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFont(new Font("Arial", Font.BOLD, fontSize));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(173, 216, 230), 2)); // Light blue border
        button.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Change cursor to hand on hover
    }



  



    // Load issues from the database
    private List<Issue> loadIssuesFromDatabase() {
        UserIssueDAO dao = new UserIssueDAO();
        try {
            List<Issue> issues = dao.getUserIssues(getCurrentUserId());
            return issues.stream()
                .filter(issue -> {
                    try {
                        IssueStatus status = dao.getIssueStatus(getCurrentUserId(), issue.getId());
                        boolean meetsConditions = true;

                        if (showOnlyFavorites) {
                            meetsConditions = meetsConditions && status != null && status.favoris.equals("YES");
                        }

                        if (showOnlyPurchased) {
                            meetsConditions = meetsConditions && status != null && status.acheter.equals("YES");
                        }

                        if (!selectedReadingStatus.equals("All")) {
                            meetsConditions = meetsConditions && status != null && status.avancement.equals(selectedReadingStatus);
                        }

                        return meetsConditions;
                    } catch (SQLException e) {
                        e.printStackTrace();
                        return false;
                    }
                })
                .collect(Collectors.toList());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading issues from database.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return List.of();
        }
    }









 // Display library items in the issues panel
    private void displayLibraryItems() {
        issuesPanel.removeAll();
        
        
        // Add top margin to the issuesPanel
        issuesPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0)); // Top margin of 20 pixels
        
        if (libraryIssues.isEmpty()) {
            JLabel emptyLabel = new JLabel("No issues to display!");
            emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            emptyLabel.setForeground(Color.WHITE); // White text
            issuesPanel.setBackground(new Color(10, 10, 30)); // Dark blue background
            issuesPanel.add(emptyLabel);
        } else {
            for (Issue issue : libraryIssues) {
                issuesPanel.add(createItemPanel(issue));
            }
        }
        issuesPanel.revalidate();
        issuesPanel.repaint();
    }

    private void addIssueToLibrary() {
        // Navigate back to the SearchPage
        this.setVisible(false); // Hide the current LibraryPage
        parentPage.setVisible(true); // Show the existing SearchPage

        // Ensure that LibraryPage updates when revisited
        reloadLibraryIssues();
    }

    // Method to reload issues from the database and update the display
    public void reloadLibraryIssues() {
        libraryIssues = loadIssuesFromDatabase(); // Reload issues from the database
        displayLibraryItems(); // Refresh the issues panel
    }

    // Remove a selected issue from the library
    private void removeSelectedIssue() {
        if (libraryIssues.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No issues to remove.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] issueTitles = libraryIssues.stream().map(Issue::getName).toArray(String[]::new);
        String selectedIssue = (String) JOptionPane.showInputDialog(this, "Select an issue to remove:",
                "Remove Comic", JOptionPane.PLAIN_MESSAGE, null, issueTitles, issueTitles[0]);

        if (selectedIssue != null) {
            libraryIssues.removeIf(issue -> issue.getName().equals(selectedIssue));
            displayLibraryItems();
            JOptionPane.showMessageDialog(this, "Issue removed from your library.", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Sort the issues based on the selected criterion
    private void sortIssues(int sortIndex) {
        switch (sortIndex) {
            case 0: // Sort by Title
                libraryIssues.sort(Comparator.comparing(Issue::getName));
                break;
            case 1: // Sort by Issue Number
                libraryIssues.sort(Comparator.comparingInt(issue -> Integer.parseInt(issue.getIssueNumber())));
                break;
            case 2: // Sort by Cover Date
                libraryIssues.sort(Comparator.comparing(Issue::getCoverDate));
                break;
        }
        displayLibraryItems();
    }

    private JPanel createItemPanel(Issue issue) {
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS)); // Vertical stacking of image and details
        itemPanel.setBackground(new Color(10, 10, 30)); // Darker blue background
        itemPanel.setPreferredSize(new Dimension(200, 300)); // Set consistent size for each item

        // Panel for image and title
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS)); // Use BoxLayout for tight alignment
        contentPanel.setBackground(new Color(10, 10, 30)); // Dark blue

        try {
            // Load image and title
            URL imageUrl = URI.create(issue.getImage()).toURL();
            ImageIcon icon = new ImageIcon(ImageIO.read(imageUrl).getScaledInstance(150, 200, Image.SCALE_SMOOTH));
            JLabel imageLabel = new JLabel(icon);
            imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align the image
            contentPanel.add(imageLabel);

            JLabel titleLabel = new JLabel(issue.getName());
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align the title directly below the image
            titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
            titleLabel.setForeground(Color.WHITE); // White text
            titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0)); // Minimal spacing
            contentPanel.add(titleLabel);

            // Add contentPanel to itemPanel
            itemPanel.add(contentPanel);

            // Panel for dropdown menus
            JPanel statusPanel = new JPanel(new GridLayout(3, 1, 5, 5));
            statusPanel.setBackground(new Color(40, 40, 70)); // Darker blue
            statusPanel.setMaximumSize(new Dimension(200, 90)); // Limit height of the status section

            // Retrieve current statuses
            UserIssueDAO dao = new UserIssueDAO();
            IssueStatus status = dao.getIssueStatus(getCurrentUserId(), issue.getId());

            // Dropdown for favorites status
            String[] favorisOptions = { "Not Favorite", "Favorite" };
            JComboBox<String> favorisCombo = new JComboBox<>(favorisOptions);
            favorisCombo.setBackground(new Color(30, 30, 60)); // Dark blue
            favorisCombo.setForeground(Color.WHITE);
            favorisCombo.setPreferredSize(new Dimension(120, 25)); // Restrict size
            favorisCombo.setSelectedItem(status != null && "YES".equals(status.favoris) ? "Favorite" : "Not favorite");
            favorisCombo.addActionListener(e -> {
                try {
                    String newStatus = favorisCombo.getSelectedItem().toString().equals("Favorite") ? "YES" : "NO";
                    dao.updateUserIssueFavoris(getCurrentUserId(), issue.getId(), newStatus);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la mise à jour du statut favori", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            });

            // Dropdown for purchase status
            String[] achatOptions = { "Not Purchased", "Purchased" };
            JComboBox<String> achatCombo = new JComboBox<>(achatOptions);
            achatCombo.setBackground(new Color(30, 30, 60)); // Dark blue
            achatCombo.setForeground(Color.WHITE);
            achatCombo.setPreferredSize(new Dimension(120, 25)); // Restrict size
            achatCombo.setSelectedItem(status != null && "YES".equals(status.acheter) ? "Purchased" : "Not Purchased");
            achatCombo.addActionListener(e -> {
                try {
                    String newStatus = achatCombo.getSelectedItem().toString().equals("Purchased") ? "YES" : "NO";
                    dao.updateUserIssueAchat(getCurrentUserId(), issue.getId(), newStatus);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la mise à jour du statut d'achat", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            });

            // Dropdown for reading progress
            String[] lectureOptions = { "Not Started", "In Progress", "Completed" };
            JComboBox<String> lectureCombo = new JComboBox<>(lectureOptions);
            lectureCombo.setBackground(new Color(30, 30, 60)); // Dark blue
            lectureCombo.setForeground(Color.WHITE);
            lectureCombo.setPreferredSize(new Dimension(120, 25)); // Restrict size
            lectureCombo.setSelectedItem(status != null ? status.avancement : "Not Started");
            lectureCombo.addActionListener(e -> {
                try {
                    String newStatus = lectureCombo.getSelectedItem().toString();
                    dao.updateUserIssueProgress(getCurrentUserId(), Integer.parseInt(issue.getId()), newStatus);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la mise à jour du statut de lecture", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            });

            // Add dropdowns to the status panel
            statusPanel.add(favorisCombo);
            statusPanel.add(achatCombo);
            statusPanel.add(lectureCombo);

            // Add statusPanel to itemPanel
            itemPanel.add(statusPanel);

            // Add click listener for showing details
            itemPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    displayIssueDetails(issue); // Show details of the clicked issue
                }
            });

        } catch (Exception e) {
            System.out.println("Error loading image: " + e.getMessage());
        }

        return itemPanel;
    }



    
    
    
    
    
    
    
    

    private void displayIssueDetails(Issue issue) {
        long startTime = System.currentTimeMillis(); // Start timing

        detailsPanel.removeAll();
        detailsPanel.setLayout(new BorderLayout());
        detailsPanel.setBackground(new Color(25, 25, 75)); // Unified dark blue background

        // Main Content Panel with GridBagLayout
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridBagLayout());
        contentPanel.setBackground(new Color(25, 25, 75)); // Unified dark blue background
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Padding between elements
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Close button at the top
        JPanel closePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        closePanel.setBackground(new Color(25, 25, 75));
        JButton closeButton = new JButton("X");
        closeButton.setFont(new Font("Arial", Font.BOLD, 14));
        closeButton.setForeground(Color.RED); // Red close button
        closeButton.setFocusPainted(false);
        closeButton.setBackground(new Color(30, 30, 80));
        closeButton.addActionListener(e -> detailsPanel.setVisible(false));
        closePanel.add(closeButton);
        detailsPanel.add(closePanel, BorderLayout.NORTH);

        // Title Section
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(new Color(25, 25, 75)); // Unified dark blue background
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding

        JLabel titleLabel = new JLabel(issue.getName() != null ? issue.getName() : "No Title");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Larger title
        titleLabel.setForeground(new Color(173, 216, 230)); // Light blue title
        titlePanel.add(titleLabel);

        JLabel numberLabel = new JLabel("Issue Number: " + (issue.getIssueNumber() != null ? issue.getIssueNumber() : "Unknown"));
        numberLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        numberLabel.setForeground(Color.WHITE); // White text
        titlePanel.add(numberLabel);

        JLabel dateLabel = new JLabel("Cover Date: " + (issue.getCoverDate() != null ? issue.getCoverDate() : "Unknown"));
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        dateLabel.setForeground(Color.WHITE); // White text
        titlePanel.add(dateLabel);

        contentPanel.add(titlePanel, gbc);

        // Description Section
        gbc.gridy++;
        gbc.gridwidth = 2;

        JPanel descriptionPanel = new JPanel();
        descriptionPanel.setLayout(new BoxLayout(descriptionPanel, BoxLayout.Y_AXIS));
        descriptionPanel.setBackground(new Color(25, 25, 75)); // Unified dark blue background
        descriptionPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(173, 216, 230)), // Light blue border
            "Description", 0, 0, new Font("Arial", Font.BOLD, 16), new Color(173, 216, 230) // Light blue title
        ));

        JTextArea descriptionLabel = new JTextArea("Loading..."); // Placeholder text
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        descriptionLabel.setForeground(Color.WHITE); // White text
        descriptionLabel.setBackground(new Color(25, 25, 75));
        descriptionLabel.setLineWrap(true); // Wrap long text
        descriptionLabel.setWrapStyleWord(true); // Wrap at word boundaries
        descriptionLabel.setEditable(false); // Non-editable text
        descriptionPanel.add(descriptionLabel);

        contentPanel.add(descriptionPanel, gbc);

        // Wrap content in a JScrollPane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); // Enable horizontal scrolling

        detailsPanel.add(scrollPane, BorderLayout.CENTER);

        // Delete Button at the Bottom
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(new Color(25, 25, 75));

        JButton deleteButton = new JButton("Delete");
        deleteButton.setFont(new Font("Arial", Font.BOLD, 14));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setBackground(Color.RED);
        deleteButton.setFocusPainted(false);
        deleteButton.setPreferredSize(new Dimension(120, 40));
        deleteButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this comic?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    UserIssueDAO dao = new UserIssueDAO();
                    boolean success = dao.deleteUserIssue(getCurrentUserId(), issue.getId());

                    if (success) {
                        libraryIssues.remove(issue); // Remove the issue from the in-memory list
                        displayLibraryItems(); // Refresh the issues panel
                        detailsPanel.setVisible(false); // Hide the details panel
                        JOptionPane.showMessageDialog(this, "Comic removed from your library.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to remove the comic from the database.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error occurred while removing the comic.", "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        bottomPanel.add(deleteButton);
        detailsPanel.add(bottomPanel, BorderLayout.SOUTH);

        detailsPanel.setVisible(true);
        detailsPanel.revalidate();
        detailsPanel.repaint();

        // Asynchronous loading using SwingWorker
        SwingWorker<Void, Runnable> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                // Update the description asynchronously
                publish(() -> descriptionLabel.setText(issue.getDescription() != null ? issue.getDescription().replaceAll("<[^>]*>", "") : "No Description"));

                // Add Characters Section
                publish(() -> addSection(contentPanel, gbc, "Characters", () -> {
                    try {
                        PersonnageDAO personnageDAO = new PersonnageDAO();
                        List<Personnage> characters = personnageDAO.getCharactersByIssueId(issue.getId());
                        return characters != null && !characters.isEmpty()
                            ? characters.stream()
                                .map(character -> new DataItem("- " + character.getName(), character.getImage()))
                                .collect(Collectors.toList())
                            : List.of(new DataItem("No characters available", null));
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        return List.of(new DataItem("Error loading characters", null));
                    }
                }));

                // Add Authors Section
                publish(() -> addSection(contentPanel, gbc, "Authors", () -> {
                    try {
                        AuteurDAO auteurDAO = new AuteurDAO();
                        List<Auteur> authors = auteurDAO.getAuthorsByIssueId(issue.getId());
                        return authors != null && !authors.isEmpty()
                            ? authors.stream()
                                .map(author -> new DataItem("- " + author.getRole() + ": " + author.getName(), author.getImage()))
                                .collect(Collectors.toList())
                            : List.of(new DataItem("No authors available", null));
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        return List.of(new DataItem("Error loading authors", null));
                    }
                }));

                // Add Teams Section
                publish(() -> addSection(contentPanel, gbc, "Teams", () -> {
                    try {
                        TeamDAO teamDAO = new TeamDAO();
                        List<Team> teams = teamDAO.getTeamsByIssueId(issue.getId());
                        return teams != null && !teams.isEmpty()
                            ? teams.stream()
                                .map(team -> new DataItem("- " + team.getName(), team.getImage()))
                                .collect(Collectors.toList())
                            : List.of(new DataItem("No teams available", null));
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        return List.of(new DataItem("Error loading teams", null));
                    }
                }));

                // Add Genres Section
                publish(() -> addSection(contentPanel, gbc, "Genres", () -> {
                    try {
                        GenreDAO genreDAO = new GenreDAO();
                        List<Genre> genres = genreDAO.getGenresByIssueId(issue.getId());
                        return genres != null && !genres.isEmpty()
                            ? genres.stream()
                                .map(genre -> new DataItem("- " + genre.getName(), genre.getImage()))
                                .collect(Collectors.toList())
                            : List.of(new DataItem("No genres available", null));
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        return List.of(new DataItem("Error loading genres", null));
                    }
                }));

                // Add Story Arcs Section
                publish(() -> addSection(contentPanel, gbc, "Story Arcs", () -> {
                    try {
                        StoryArcDAO storyArcDAO = new StoryArcDAO();
                        List<StoryArc> storyArcs = storyArcDAO.getStoryArcsByIssueId(issue.getId());
                        return storyArcs != null && !storyArcs.isEmpty()
                            ? storyArcs.stream()
                                .map(arc -> new DataItem("- " + arc.getName(), arc.getImage()))
                                .collect(Collectors.toList())
                            : List.of(new DataItem("No story arcs available", null));
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        return List.of(new DataItem("Error loading story arcs", null));
                    }
                }));

                // Add Comic Objects Section
                publish(() -> addSection(contentPanel, gbc, "Comic Objects", () -> {
                    try {
                        ComicObjectDAO comicObjectDAO = new ComicObjectDAO();
                        List<ComicObject> comicObjects = comicObjectDAO.getComicObjectsByIssueId(issue.getId());
                        return comicObjects != null && !comicObjects.isEmpty()
                            ? comicObjects.stream()
                                .map(object -> new DataItem("- " + object.getName(), object.getImage()))
                                .collect(Collectors.toList())
                            : List.of(new DataItem("No comic objects available", null));
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        return List.of(new DataItem("Error loading comic objects", null));
                    }
                }));


                return null;
            }

            @Override
            protected void process(List<Runnable> chunks) {
                for (Runnable task : chunks) {
                    task.run(); // Execute each UI update on the Event Dispatch Thread
                }
            }

            @Override
            protected void done() {
                long endTime = System.currentTimeMillis(); // End timing
                System.out.println("Time to load all sections asynchronously: " + (endTime - startTime) + " ms");
                contentPanel.revalidate();
                contentPanel.repaint();
            }
        };

        worker.execute(); // Start the SwingWorker
    }





    // Helper method to add sections
    private void addSection(JPanel contentPanel, GridBagConstraints gbc, String title, DataProviderWithImage dataProvider) {
        gbc.gridy++;
        gbc.gridwidth = 2;

        JPanel sectionPanel = createBlockSection(title, dataProvider);
        contentPanel.add(sectionPanel, gbc);

        contentPanel.revalidate();
        contentPanel.repaint();
    }



    private JPanel createBlockSection(String title, DataProviderWithImage dataProvider) {
        JPanel blockPanel = new JPanel();
        blockPanel.setLayout(new GridBagLayout());
        blockPanel.setBackground(new Color(25, 25, 75)); // Unified dark blue background
        blockPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(173, 216, 230)), // Light blue border
            title, 0, 0, new Font("Arial", Font.BOLD, 14), new Color(173, 216, 230) // Light blue title
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding
        gbc.anchor = GridBagConstraints.WEST;

        List<DataItem> data = dataProvider.getData();
        int row = 0;
        for (DataItem item : data) {
            gbc.gridx = 0;
            gbc.gridy = row;

            // Load and display image
            if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
                try {
                    URL imageUrl = new URL(item.getImageUrl());
                    ImageIcon icon = new ImageIcon(imageUrl);
                    Image scaledImage = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH); // Resize
                    JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
                    blockPanel.add(imageLabel, gbc);
                } catch (Exception e) {
                    e.printStackTrace(); // Log error
                }
            }

            // Display text
            gbc.gridx = 1;
            JLabel textLabel = new JLabel(item.getText());
            textLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            textLabel.setForeground(Color.WHITE); // White text
            blockPanel.add(textLabel, gbc);

            row++;
        }

        return blockPanel;
    }

    /**
     * Functional interface for lazy data loading with image support
     */
    @FunctionalInterface
    private interface DataProviderWithImage {
        List<DataItem> getData();
    }

    /**
     * Class representing a data item with text and an optional image URL
     */
    private static class DataItem {
        private final String text;
        private final String imageUrl;

        public DataItem(String text, String imageUrl) {
            this.text = text;
            this.imageUrl = imageUrl;
        }

        public String getText() {
            return text;
        }

        public String getImageUrl() {
            return imageUrl;
        }
    }





    private int getCurrentUserId() {
        return UserSession.getInstance().getUserId();
    }
}