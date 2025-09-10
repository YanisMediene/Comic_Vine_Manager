package tse.info2.view;

import javax.swing.*;

import tse.info2.controller.FollowUpController;
import tse.info2.model.Issue;
import tse.info2.model.Volume;
import tse.info2.session.UserSession;

import java.awt.*;
import java.util.Map;
import java.util.List;
import java.net.URL;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import tse.info2.util.ApiClient;
import tse.info2.controller.RecommendationController;

public class RecommendationPanel extends JPanel {
    private SearchPage parentPage;
    private FollowUpController followUpController;
    private ApiClient apiClient = new ApiClient();
    private LibraryPage libraryPage; // Ajouter cette référence
    private RecommendationController recommendationController; // Ajouter cette ligne

    public RecommendationPanel(SearchPage parentPage) {
        this.parentPage = parentPage;
        this.followUpController = new FollowUpController();
        this.libraryPage = parentPage.getLibraryPage(); // Récupérer la référence de LibraryPage
        this.recommendationController = new RecommendationController(); // Initialiser le controller
        setLayout(new BorderLayout());
        
        // Panneau principal avec défilement vertical
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);

        // Section 1: Recommandations générales
        JPanel generalPanel = createSectionPanel("Latest Additions", 
            "Discover the most recent comics added to our collection");
        mainPanel.add(generalPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Section 2: Recommandations personnalisées
        JPanel personalizedPanel = createSectionPanel("Personalized Recommendations", 
            "Based on your reading preferences and genres");
        mainPanel.add(personalizedPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Section 3: Suivi des séries
        JPanel seriesPanel = createSeriesFollowUpPanel();
        mainPanel.add(seriesPanel);

        // Ajouter le panneau principal dans un JScrollPane
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createSectionPanel(String title, String subtitle) {
        JPanel sectionPanel = new JPanel();
        sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));
        sectionPanel.setBackground(Color.WHITE);
        sectionPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0x333333));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        subtitleLabel.setForeground(new Color(0x666666));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel comicsContainer = new JPanel();
        comicsContainer.setLayout(new BoxLayout(comicsContainer, BoxLayout.X_AXIS));
        comicsContainer.setBackground(Color.WHITE);
        comicsContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (title.equals("Latest Additions")) {
            try {
                List<Issue> latestIssues = apiClient.getLatestIssues(5);
                for (Issue issue : latestIssues) {
                    comicsContainer.add(createLatestIssuePreview(issue));
                    comicsContainer.add(Box.createHorizontalStrut(20));
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                JLabel errorLabel = new JLabel("Unable to load latest issues");
                errorLabel.setForeground(Color.RED);
                comicsContainer.add(errorLabel);
            }
        } else if (title.equals("Personalized Recommendations")) {
            try {
                // Récupérer l'ID de l'utilisateur connecté
                int userId = UserSession.getInstance().getUserId();
                if (userId != -1) {
                    // Récupérer les recommandations personnalisées
                    List<Issue> recommendedIssues = recommendationController.getRecommendations(userId);
                    if (!recommendedIssues.isEmpty()) {
                        for (Issue issue : recommendedIssues) {
                            comicsContainer.add(createLatestIssuePreview(issue));
                            comicsContainer.add(Box.createHorizontalStrut(20));
                        }
                    } else {
                        JLabel noRecommendationsLabel = new JLabel("No personalized recommendations available yet");
                        noRecommendationsLabel.setForeground(new Color(0x666666));
                        comicsContainer.add(noRecommendationsLabel);
                    }
                } else {
                    JLabel loginPromptLabel = new JLabel("Please login to see personalized recommendations");
                    loginPromptLabel.setForeground(new Color(0x666666));
                    comicsContainer.add(loginPromptLabel);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JLabel errorLabel = new JLabel("Unable to load recommendations");
                errorLabel.setForeground(Color.RED);
                comicsContainer.add(errorLabel);
            }
        } else {
            // Code existant pour les autres sections
            for (int i = 0; i < 5; i++) {
                comicsContainer.add(createComicPreview("Comic " + (i + 1)));
                if (i < 4) comicsContainer.add(Box.createHorizontalStrut(20));
            }
        }

        sectionPanel.add(titleLabel);
        sectionPanel.add(Box.createVerticalStrut(5));
        sectionPanel.add(subtitleLabel);
        sectionPanel.add(Box.createVerticalStrut(20));
        sectionPanel.add(comicsContainer);

        return sectionPanel;
    }

    private JPanel createSeriesFollowUpPanel() {
        JPanel sectionPanel = new JPanel();
        sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));
        sectionPanel.setBackground(Color.WHITE);
        sectionPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        sectionPanel.setAlignmentX(Component.LEFT_ALIGNMENT); // Ajouter cet alignement

        JLabel titleLabel = new JLabel("Series Follow-up");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0x333333));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Continue reading your favorite series");
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        subtitleLabel.setForeground(new Color(0x666666));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Conteneur pour les séries
        JPanel seriesContainer = new JPanel();
        seriesContainer.setLayout(new BoxLayout(seriesContainer, BoxLayout.Y_AXIS));
        seriesContainer.setBackground(Color.WHITE);
        seriesContainer.setAlignmentX(Component.LEFT_ALIGNMENT); // Ajouter cet alignement

        // Récupérer les séries à suivre
        Map<Volume, List<Issue>> followUpMap = followUpController.getSeriesFollowUp(
            UserSession.getInstance().getUserId()
        );

        if (followUpMap != null && !followUpMap.isEmpty()) {
            for (Map.Entry<Volume, List<Issue>> entry : followUpMap.entrySet()) {
                JPanel previewPanel = createSeriesPreview(entry.getKey(), entry.getValue());
                previewPanel.setPreferredSize(new Dimension(getWidth(), 250)); // Définir une hauteur fixe
                previewPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250)); // Limiter la hauteur maximale
                seriesContainer.add(previewPanel);
                seriesContainer.add(Box.createVerticalStrut(20));
            }
        } else {
            JLabel noSeriesLabel = new JLabel("No series to follow up yet");
            noSeriesLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // Aligner à gauche
            seriesContainer.add(noSeriesLabel);
        }

        sectionPanel.add(titleLabel);
        sectionPanel.add(Box.createVerticalStrut(5));
        sectionPanel.add(subtitleLabel);
        sectionPanel.add(Box.createVerticalStrut(20));
        sectionPanel.add(seriesContainer);

        return sectionPanel;
    }

    private JPanel createSeriesPreview(Volume volume, List<Issue> nextIssues) {
        JPanel seriesPanel = new JPanel();
        seriesPanel.setLayout(new BorderLayout(10, 10));
        seriesPanel.setBackground(Color.WHITE);
        seriesPanel.setBorder(BorderFactory.createLineBorder(new Color(0xEEEEEE)));
        seriesPanel.setPreferredSize(new Dimension(getWidth(), 300));

        // En-tête avec le titre de la série
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        JLabel seriesTitle = new JLabel(volume.getName());
        seriesTitle.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(seriesTitle, BorderLayout.NORTH);
        
        // Panel pour les prochains numéros avec scroll horizontal
        JPanel issuesPanel = new JPanel();
        // Utiliser GridLayout avec 1 ligne et espacement uniforme
        GridLayout gridLayout = new GridLayout(1, 0, 10, 0);
        issuesPanel.setLayout(gridLayout);
        issuesPanel.setBackground(Color.WHITE);
        
        for (Issue issue : nextIssues) {
            try {
                // Création d'un panel pour chaque issue
                JPanel issuePanel = new JPanel();
                issuePanel.setLayout(new BoxLayout(issuePanel, BoxLayout.Y_AXIS));
                issuePanel.setBackground(Color.WHITE);
                issuePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

                // Image de l'issue
                URL imageUrl = new URL(issue.getImage());
                BufferedImage image = ImageIO.read(imageUrl);
                ImageIcon icon = new ImageIcon(image.getScaledInstance(120, 180, Image.SCALE_SMOOTH));
                JLabel imageLabel = new JLabel(icon);
                imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

                // Numéro de l'issue
                JLabel issueNumberLabel = new JLabel("<html><body style='width: 100px; text-align: center'>" 
                    + issue.getName() + " #" + issue.getIssueNumber() + "</body></html>");
                issueNumberLabel.setFont(new Font("Arial", Font.BOLD, 14));
                issueNumberLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

                // Ajouter le listener de clic
                issuePanel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        showIssueDetails(issue);
                    }
                    
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        issuePanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                        issuePanel.setBorder(BorderFactory.createLineBorder(new Color(0x4CAF50), 2));
                    }
                    
                    @Override
                    public void mouseExited(MouseEvent e) {
                        issuePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                    }
                });

                issuePanel.add(imageLabel);
                issuePanel.add(Box.createVerticalStrut(5));
                issuePanel.add(issueNumberLabel);

                issuesPanel.add(issuePanel);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Ajout d'un scroll horizontal pour les issues
        JScrollPane scrollPane = new JScrollPane(issuesPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);

        seriesPanel.add(headerPanel, BorderLayout.NORTH);
        seriesPanel.add(scrollPane, BorderLayout.CENTER);

        return seriesPanel;
    }

    private void showIssueDetails(Issue issue) {
        try {
            issue = apiClient.getIssuesDetails(issue);
            
            // Créer un panneau pour afficher les détails avec la référence à LibraryPage
            IssueDetailsPanel detailsPanel = new IssueDetailsPanel(issue, parentPage, libraryPage);
            
            // Mettre à jour le contenu du panneau principal
            this.removeAll();
            this.setLayout(new BorderLayout());
            this.add(detailsPanel, BorderLayout.CENTER);
            this.revalidate();
            this.repaint();
        } catch (IOException | InterruptedException e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de la récupération des détails", 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private JPanel createComicPreview(String title) {
        JPanel comicPanel = new JPanel();
        comicPanel.setLayout(new BoxLayout(comicPanel, BoxLayout.Y_AXIS));
        comicPanel.setBackground(Color.WHITE);
        comicPanel.setBorder(BorderFactory.createLineBorder(new Color(0xEEEEEE)));
        comicPanel.setPreferredSize(new Dimension(150, 250));

        // Placeholder pour l'image
        JPanel imagePanel = new JPanel();
        imagePanel.setPreferredSize(new Dimension(140, 200));
        imagePanel.setBackground(new Color(0xEEEEEE));
        imagePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Titre du comic
        JLabel titleLabel = new JLabel(title);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        comicPanel.add(imagePanel);
        comicPanel.add(Box.createVerticalStrut(10));
        comicPanel.add(titleLabel);

        return comicPanel;
    }

    private JPanel createLatestIssuePreview(Issue issue) {
        JPanel issuePanel = new JPanel();
        issuePanel.setLayout(new BoxLayout(issuePanel, BoxLayout.Y_AXIS));
        issuePanel.setBackground(Color.WHITE);
        issuePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        try {
            // Image de l'issue
            URL imageUrl = new URL(issue.getImage());
            BufferedImage image = ImageIO.read(imageUrl);
            ImageIcon icon = new ImageIcon(image.getScaledInstance(120, 180, Image.SCALE_SMOOTH));
            JLabel imageLabel = new JLabel(icon);
            imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Titre de l'issue
            JLabel titleLabel = new JLabel("<html><body style='width: 100px'>" + issue.getName() + "</body></html>");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Ajouter le listener de clic
            issuePanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    showIssueDetails(issue);
                }
                
                @Override
                public void mouseEntered(MouseEvent e) {
                    issuePanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    issuePanel.setBorder(BorderFactory.createLineBorder(new Color(0x4CAF50), 2));
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    issuePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                }
            });

            issuePanel.add(imageLabel);
            issuePanel.add(Box.createVerticalStrut(5));
            issuePanel.add(titleLabel);

        } catch (IOException e) {
            e.printStackTrace();
            JLabel errorLabel = new JLabel("Error loading image");
            errorLabel.setForeground(Color.RED);
            issuePanel.add(errorLabel);
        }

        return issuePanel;
    }
}
