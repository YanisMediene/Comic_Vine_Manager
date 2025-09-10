package tse.info2.view;

import javax.swing.*;

import com.formdev.flatlaf.FlatLightLaf;

import tse.info2.model.Issue;
import tse.info2.model.Personnage;
import tse.info2.util.ApiClient;
import tse.info2.view.PersonnageDetailsPanel;
import tse.info2.session.UserSession;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.net.URL;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class SearchPage extends JPanel {
    private MainFrame mainFrame;
    private JTextField searchField;
    private JButton searchButton;
    private LibraryPage libraryPage; // Instance of LibraryPage
    private JComboBox<String> searchTypeComboBox;
    private JPanel contentPanel; // Utilisez un JPanel pour les résultats
    private ApiClient apiClient;
    private int currentPage = 1; // Page actuelle
    private static final int RESULTS_PER_PAGE = 10; // Nombre d'éléments par page
    private List<Issue> currentIssues = new ArrayList<>();  // Initialiser la liste
    private List<Personnage> currentPersonnages = new ArrayList<>();  // Initialiser aussi celle-ci si elle existe
    private RecommendationPanel recommendationPanel;
    private String lastSearchType; // Ajouter cette variable

    public SearchPage(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.apiClient = new ApiClient();
        setLayout(new BorderLayout());
        
        // Initialiser searchTypeComboBox ici
        searchTypeComboBox = new JComboBox<>(new String[] { "Character", "Issue" });
        
        contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BorderLayout());
        add(contentPanel, BorderLayout.CENTER);
        
        // Supprimer cette ligne qui ajoutait la toolbar
        // add(createToolbar(), BorderLayout.NORTH);
    }

    public void performSearch(String query, String searchType) {
        this.lastSearchType = searchType; // Sauvegarder le type de recherche
        this.currentPage = 1; // Réinitialiser la page à 1 pour chaque nouvelle recherche
        try {
            if ("Issue".equals(searchType)) {
                currentIssues = apiClient.searchIssuesWithLimit(query, 20);
                displayIssues();
            } else if ("Character".equals(searchType)) {
                currentPersonnages = apiClient.searchPersonnagesWithLimit(query, 20);
                displayPersonnages();
            }
        } catch (IOException | InterruptedException ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la récupération des données", "Erreur", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void displayIssues() {
        int startIndex = (currentPage - 1) * RESULTS_PER_PAGE;
        int endIndex = Math.min(currentIssues.size(), currentPage * RESULTS_PER_PAGE);

        // Panneau pour afficher les résultats
        JPanel resultPanel = new JPanel();
        Color darkBlue = new Color(10, 12, 30);
        resultPanel.setBackground(darkBlue); // Définir la couleur de fond bleu foncé
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Marges pour les résultats

        if (currentIssues.isEmpty()) {
            JLabel noResultsLabel = new JLabel("Aucun numéro trouvé.");
            noResultsLabel.setFont(new Font("Arial", Font.BOLD, 18));
            noResultsLabel.setForeground(Color.WHITE);
            noResultsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            resultPanel.add(noResultsLabel);
        } else {
            for (int i = startIndex; i < endIndex; i++) {
                Issue issue = currentIssues.get(i);

                // Panneau pour chaque issue
                JPanel issuePanel = new JPanel(new BorderLayout(20, 10));
                issuePanel.setOpaque(false); // Transparence pour voir le fond
                issuePanel.setBorder(BorderFactory.createLineBorder(new Color(211, 211, 211), 1)); // Bordure verte

                try {
                    // Charger l'image de l'issue
                    URL imageUrl = new URL(issue.getImage());
                    BufferedImage image = ImageIO.read(imageUrl);
                    ImageIcon icon = new ImageIcon(image.getScaledInstance(120, 160, Image.SCALE_SMOOTH));

                    JLabel imageLabel = new JLabel(icon);
                    issuePanel.add(imageLabel, BorderLayout.WEST); // Ajouter l'image à gauche

                    // Texte avec le nom et les informations de l'issue
                    JPanel textPanel = new JPanel();
                    textPanel.setOpaque(false); // Transparence
                    textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
                    textPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

                    JLabel issueNameLabel = new JLabel(issue.getName());
                    issueNameLabel.setFont(new Font("Arial", Font.BOLD, 16));
                    issueNameLabel.setForeground(new Color(211, 211, 211)); // Gris clair


                    JLabel issueNumberLabel = new JLabel("Numéro : " + issue.getIssueNumber());
                    issueNumberLabel.setForeground(Color.WHITE);

                    JLabel issueDateLabel = new JLabel("Date : " + issue.getCoverDate());
                    issueDateLabel.setForeground(Color.LIGHT_GRAY);

                    textPanel.add(issueNameLabel);
                    textPanel.add(issueNumberLabel);
                    textPanel.add(issueDateLabel);

                    issuePanel.add(textPanel, BorderLayout.CENTER);

                    // Ajout d'un effet au survol
                    issuePanel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            displayIssueDetails(issue); // Afficher les détails
                        }

                        @Override
                        public void mouseEntered(MouseEvent e) {
                            issuePanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                            issuePanel.setBorder(BorderFactory.createLineBorder(new Color(0xFF9800), 2)); // Bordure orange au survol
                        }

                        @Override
                        public void mouseExited(MouseEvent e) {
                            issuePanel.setBorder(BorderFactory.createLineBorder(new Color(211, 211, 211), 1)); // Bordure grise
                        }
                    });

                } catch (IOException e) {
                    System.out.println("Erreur de chargement de l'image pour " + issue.getName() + ": " + e.getMessage());
                }

                resultPanel.add(issuePanel);
                resultPanel.add(Box.createVerticalStrut(10)); // Espacement entre les issues
            }
        }

        // Ajout des résultats au panneau central
        contentPanel.removeAll();
        JScrollPane scrollPane = new JScrollPane(resultPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null); // Supprimer les bordures pour un rendu propre
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Défilement fluide

        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Ajouter les contrôles de pagination
        JPanel paginationPanel = new JPanel();
        paginationPanel.setBackground(new Color(30, 38, 48));
        JButton prevButton = new JButton("Previous");
        JButton nextButton = new JButton("Next");

        // Activer/désactiver les boutons selon la page actuelle
        prevButton.setEnabled(currentPage > 1);
        boolean hasMoreResults = currentPage * RESULTS_PER_PAGE < currentIssues.size();
        nextButton.setEnabled(hasMoreResults);

        prevButton.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                displayIssues();
            }
        });

        nextButton.addActionListener(e -> {
            if (hasMoreResults) {
                currentPage++;
                displayIssues();
            }
        });

        paginationPanel.add(prevButton);
        paginationPanel.add(nextButton);

        contentPanel.add(paginationPanel, BorderLayout.SOUTH);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    
    
    private void displayIssueDetails(Issue issue) {
        // Créer et afficher d'abord le panneau de chargement
        IssueDetailsPanel loadingPanel = new IssueDetailsPanel();
        contentPanel.removeAll();
        contentPanel.add(loadingPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();

        // Lancer le chargement des détails en arrière-plan
        SwingWorker<Issue, Void> worker = new SwingWorker<Issue, Void>() {
            @Override
            protected Issue doInBackground() throws Exception {
                return apiClient.getCompleteIssueDetails(issue.getApi_detail_url());
            }

            @Override
            protected void done() {
                try {
                    Issue completeIssue = get();
                    IssueDetailsPanel detailsPanel = new IssueDetailsPanel(completeIssue, SearchPage.this, libraryPage);
                    contentPanel.removeAll();
                    contentPanel.add(detailsPanel, BorderLayout.CENTER);
                    contentPanel.revalidate();
                    contentPanel.repaint();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(SearchPage.this, 
                        "Error fetching issue details: " + e.getMessage(), 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }


    private void displayPersonnages() {
        int startIndex = (currentPage - 1) * RESULTS_PER_PAGE;
        int endIndex = Math.min(currentPersonnages.size(), currentPage * RESULTS_PER_PAGE);

        // Panneau pour afficher les résultats
        JPanel resultPanel = new JPanel();
        Color darkBlue = new Color(10, 12, 30);
        resultPanel.setBackground(darkBlue); // Définir la couleur de fond bleu foncé
        resultPanel.setOpaque(true); // Rendre le panneau opaque pour afficher la couleur
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Marges pour le contenu

        if (currentPersonnages.isEmpty()) {
            JLabel noResultsLabel = new JLabel("Aucun personnage trouvé.");
            noResultsLabel.setFont(new Font("Arial", Font.BOLD, 18));
            noResultsLabel.setForeground(Color.WHITE);
            noResultsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            resultPanel.add(noResultsLabel);
        } else {
            for (int i = startIndex; i < endIndex; i++) {
                Personnage personnage = currentPersonnages.get(i);

                // Panneau pour chaque personnage
                JPanel personnagePanel = new JPanel(new BorderLayout(20, 10));
                personnagePanel.setOpaque(false); // Transparence pour voir le fond
                personnagePanel.setBorder(BorderFactory.createLineBorder(new Color(211, 211, 211), 1)); // Bordure grise

                try {
                    // Charger l'image du personnage
                    URL imageUrl = new URL(personnage.getImage());
                    BufferedImage image = ImageIO.read(imageUrl);
                    ImageIcon icon = new ImageIcon(image.getScaledInstance(120, 160, Image.SCALE_SMOOTH));

                    JLabel imageLabel = new JLabel(icon);
                    personnagePanel.add(imageLabel, BorderLayout.WEST); // Ajouter l'image à gauche

                    // Texte avec le nom du personnage
                    JPanel textPanel = new JPanel();
                    textPanel.setOpaque(false); // Transparence
                    textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
                    textPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

                    JLabel personnageNameLabel = new JLabel(personnage.getName());
                    personnageNameLabel.setFont(new Font("Arial", Font.BOLD, 16));
                    personnageNameLabel.setForeground(new Color(211, 211, 211)); // Gris



                    textPanel.add(personnageNameLabel);

                    personnagePanel.add(textPanel, BorderLayout.CENTER);

                    // Ajouter un effet interactif au survol
                    personnagePanel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            displayPersonnageDetails(personnage); // Afficher les détails
                        }

                        @Override
                        public void mouseEntered(MouseEvent e) {
                            personnagePanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                            personnagePanel.setBorder(BorderFactory.createLineBorder(new Color(0xFF9800), 2)); // Bordure orange au survol
                        }

                        @Override
                        public void mouseExited(MouseEvent e) {
                            personnagePanel.setBorder(BorderFactory.createLineBorder(new Color(211, 211, 211), 1)); // Bordure verte normale
                        }
                    });

                } catch (IOException e) {
                    System.out.println("Erreur de chargement de l'image pour " + personnage.getName() + ": " + e.getMessage());
                }

                resultPanel.add(personnagePanel);
                resultPanel.add(Box.createVerticalStrut(10)); // Espacement entre les panneaux des personnages
            }
        }

        // Ajout des résultats au panneau central
        contentPanel.removeAll();
        JScrollPane scrollPane = new JScrollPane(resultPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Ajouter les contrôles de pagination
        JPanel paginationPanel = new JPanel();
        paginationPanel.setBackground(new Color(30, 38, 48));
        JButton prevButton = new JButton("Previous");
        JButton nextButton = new JButton("Next");

        // Activer/désactiver les boutons selon la page actuelle
        prevButton.setEnabled(currentPage > 1);
        boolean hasMoreResults = currentPage * RESULTS_PER_PAGE < currentPersonnages.size();
        nextButton.setEnabled(hasMoreResults);

        prevButton.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                displayPersonnages();
            }
        });

        nextButton.addActionListener(e -> {
            if (hasMoreResults) {
                currentPage++;
                displayPersonnages();
            }
        });

        paginationPanel.add(prevButton);
        paginationPanel.add(nextButton);

        contentPanel.add(paginationPanel, BorderLayout.SOUTH);
        contentPanel.revalidate();
        contentPanel.repaint();
    }


    private void displayPersonnageDetails(Personnage personnage) {
        // Créer et afficher d'abord le panneau de chargement
        PersonnageDetailsPanel loadingPanel = new PersonnageDetailsPanel();
        contentPanel.removeAll();
        contentPanel.add(loadingPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();

        // Lancer le chargement des détails en arrière-plan
        SwingWorker<Personnage, Void> worker = new SwingWorker<Personnage, Void>() {
            @Override
            protected Personnage doInBackground() throws Exception {
                return apiClient.getPersonnagesDetails(personnage);
            }

            @Override
            protected void done() {
                try {
                    Personnage completePersonnage = get();
                    PersonnageDetailsPanel detailsPanel = new PersonnageDetailsPanel(completePersonnage, SearchPage.this);
                    contentPanel.removeAll();
                    contentPanel.add(detailsPanel, BorderLayout.CENTER);
                    contentPanel.revalidate();
                    contentPanel.repaint();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(SearchPage.this, 
                        "Error fetching character details: " + e.getMessage(), 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void addPaginationControls() {
        // Create a pagination panel
        JPanel paginationPanel = new JPanel();
        paginationPanel.setBackground(new Color(30, 38, 48));  // grey background
        JButton prevButton = new JButton("Previous");
        JButton nextButton = new JButton("Next");

        // Enable or disable buttons based on the current page
        prevButton.setEnabled(currentPage > 1); // Disable if on first page
        boolean hasMoreResults = currentPage * RESULTS_PER_PAGE < (currentIssues.size() > 0 ? currentIssues.size() : currentPersonnages.size());
        nextButton.setEnabled(hasMoreResults); // Disable if on last page

        // Add action listeners for buttons
        prevButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentPage > 1) {
                    currentPage--;  // Decrease the current page
                    updateResultsDisplay();  // Refresh the results display
                }
            }
        });

        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (hasMoreResults) {
                    currentPage++;  // Increase the current page
                    updateResultsDisplay();  // Refresh the results display
                }
            }
        });

        // Add the buttons to the pagination panel
        paginationPanel.add(prevButton);
        paginationPanel.add(nextButton);

        // Add pagination panel to contentPanel (inside the BorderLayout)
        contentPanel.add(paginationPanel, BorderLayout.SOUTH);  // Ensure pagination is at the bottom
        contentPanel.revalidate();  // Revalidate the panel to update the layout
        contentPanel.repaint();  // Repaint to reflect changes
    }
    public void updateResultsDisplay() {
        // Utiliser le dernier type de recherche effectué
        if (lastSearchType != null) {
            if ("Issue".equals(lastSearchType)) {
                displayIssues();
            } else {
                displayPersonnages();
            }
        } else if (searchTypeComboBox != null) {
            // Fallback sur la sélection actuelle du combobox si aucune recherche n'a été faite
            String selectedType = (String) searchTypeComboBox.getSelectedItem();
            if ("Issue".equals(selectedType)) {
                displayIssues();
            } else {
                displayPersonnages();
            }
        } else {
            // Fallback par défaut si rien d'autre n'est disponible
            displayPersonnages();
        }
    }

    private void performSearch() {
        String query = searchField.getText().trim();
        String searchType = searchTypeComboBox.getSelectedItem().toString();
        if (!query.isEmpty()) {
            performSearch(query, searchType);
        }
    }

    private void openLibraryPage() {
        LibraryPage libraryPage = new LibraryPage(this); // Pass the current SearchPage instance
        this.setVisible(false); // Hide SearchPage
        libraryPage.setVisible(true); // Show LibraryPage
    }

    public LibraryPage getLibraryPage() {
        return libraryPage;
    }

}