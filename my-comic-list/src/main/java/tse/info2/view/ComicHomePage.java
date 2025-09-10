package tse.info2.view;

import com.formdev.flatlaf.FlatLightLaf;
import tse.info2.session.UserSession;
import tse.info2.util.ApiClient;
import tse.info2.controller.RecommendationController;
import tse.info2.model.Issue;
import tse.info2.model.Volume;
import tse.info2.controller.FollowUpController;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class ComicHomePage extends JPanel {
    private ApiClient apiClient = new ApiClient();
    private RecommendationController recommendationController = new RecommendationController();
    private FollowUpController followUpController = new FollowUpController();
    private MainFrame mainFrame;

    public ComicHomePage(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());

        // Main content panel
        JPanel mainContent = new JPanel(new GridBagLayout());
        mainContent.setOpaque(false);

        // Add scrollable pane
        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        add(scrollPane, BorderLayout.CENTER);

        // Display recommendations
        displayRecommendations(mainContent);
    }

    private void displayRecommendations(JPanel mainContent) {
        JPanel recommendationsPanel = new JPanel();
        recommendationsPanel.setLayout(new BoxLayout(recommendationsPanel, BoxLayout.Y_AXIS));
        recommendationsPanel.setOpaque(false);

        // General recommendations
        recommendationsPanel.add(createSectionPanel("Latest Additions", "Discover the most recent comics added to our collection"));

        // Personalized recommendations
        if (UserSession.getInstance().isGuest()) {
            recommendationsPanel.add(createGuestMessagePanel("Personalized Recommendations", "Please login to see personalized recommendations"));
        } else {
            recommendationsPanel.add(createSectionPanel("Personalized Recommendations", "Based on your reading preferences and genres"));
        }

        // Series follow-up
        if (UserSession.getInstance().isGuest()) {
            recommendationsPanel.add(createGuestMessagePanel("Series Follow-up", "Please login to see your series follow-up"));
        } else {
            recommendationsPanel.add(createSeriesFollowUpPanel());
        }

        mainContent.add(recommendationsPanel);
    }

    
    public void showLibraryPage() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (parentFrame != null) {
            parentFrame.setContentPane(new LibraryPage(this)); // Pass the current ComicHomePage instance
            parentFrame.revalidate();
            parentFrame.repaint();
        }
    }


    private JPanel createGuestMessagePanel(String title, String message) {
        JPanel sectionPanel = new JPanel();
        sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));
        sectionPanel.setOpaque(false);
        sectionPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        messageLabel.setForeground(Color.LIGHT_GRAY);
        messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        sectionPanel.add(titleLabel);
        sectionPanel.add(Box.createVerticalStrut(5));
        sectionPanel.add(messageLabel);

        return sectionPanel;
    }

    private JPanel createSectionPanel(String title, String subtitle) {
        JPanel sectionPanel = new JPanel();
        sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));
        sectionPanel.setOpaque(false);
        sectionPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        subtitleLabel.setForeground(Color.LIGHT_GRAY);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel comicsContainer = new JPanel();
        comicsContainer.setLayout(new BoxLayout(comicsContainer, BoxLayout.X_AXIS));
        comicsContainer.setOpaque(false);
        comicsContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (title.equals("Latest Additions")) {
            try {
                List<Issue> latestIssues = apiClient.getLatestIssues(5);
                for (Issue issue : latestIssues) {
                    comicsContainer.add(createIssuePreview(issue));
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
                int userId = UserSession.getInstance().getUserId();
                if (userId != -1) {
                    List<Issue> recommendedIssues = recommendationController.getRecommendations(userId);
                    if (!recommendedIssues.isEmpty()) {
                        for (Issue issue : recommendedIssues) {
                            comicsContainer.add(createIssuePreview(issue));
                            comicsContainer.add(Box.createHorizontalStrut(20));
                        }
                    } else {
                        JLabel noRecommendationsLabel = new JLabel("No personalized recommendations available yet");
                        noRecommendationsLabel.setForeground(Color.LIGHT_GRAY);
                        comicsContainer.add(noRecommendationsLabel);
                    }
                } else {
                    JLabel loginPromptLabel = new JLabel("Please login to see personalized recommendations");
                    loginPromptLabel.setForeground(Color.LIGHT_GRAY);
                    comicsContainer.add(loginPromptLabel);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JLabel errorLabel = new JLabel("Unable to load recommendations");
                errorLabel.setForeground(Color.RED);
                comicsContainer.add(errorLabel);
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
        sectionPanel.setOpaque(false);
        sectionPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel titleLabel = new JLabel("Series Follow-up");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Continue reading your favorite series");
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        subtitleLabel.setForeground(Color.LIGHT_GRAY);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel seriesContainer = new JPanel();
        seriesContainer.setLayout(new BoxLayout(seriesContainer, BoxLayout.Y_AXIS));
        seriesContainer.setOpaque(false);
        seriesContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        Map<Volume, List<Issue>> followUpMap = followUpController.getSeriesFollowUp(UserSession.getInstance().getUserId());

        if (followUpMap != null && !followUpMap.isEmpty()) {
            for (Map.Entry<Volume, List<Issue>> entry : followUpMap.entrySet()) {
                JPanel previewPanel = createSeriesPreview(entry.getKey(), entry.getValue());
                previewPanel.setPreferredSize(new Dimension(getWidth(), 300));
                previewPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
                previewPanel.setOpaque(false);
                seriesContainer.add(previewPanel);
                seriesContainer.add(Box.createVerticalStrut(20));
            }
        } else {
            JLabel noSeriesLabel = new JLabel("No series to follow up yet");
            noSeriesLabel.setForeground(Color.LIGHT_GRAY);
            noSeriesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
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
        seriesPanel.setLayout(new BoxLayout(seriesPanel, BoxLayout.Y_AXIS));
        seriesPanel.setOpaque(false);
        seriesPanel.setPreferredSize(new Dimension(getWidth(), 300));

        JLabel seriesTitle = new JLabel(volume.getName());
        seriesTitle.setFont(new Font("Arial", Font.BOLD, 18));
        seriesTitle.setForeground(Color.WHITE);
        seriesTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel issuesPanel = new JPanel();
        issuesPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        issuesPanel.setOpaque(false);

        for (Issue issue : nextIssues) {
            try {
                JPanel issuePanel = new JPanel();
                issuePanel.setLayout(new BoxLayout(issuePanel, BoxLayout.Y_AXIS));
                issuePanel.setOpaque(false);
                issuePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                issuePanel.setPreferredSize(new Dimension(150, 240)); // Adjust height to accommodate title

                URL imageUrl = new URL(issue.getImage());
                BufferedImage image = ImageIO.read(imageUrl);
                ImageIcon icon = new ImageIcon(image.getScaledInstance(120, 180, Image.SCALE_SMOOTH));
                JLabel imageLabel = new JLabel(icon);
                imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

                JLabel issueNumberLabel = new JLabel("<html><body style='width: 120px; text-align: center'>" + issue.getName() + " #" + issue.getIssueNumber() + "</body></html>");
                issueNumberLabel.setFont(new Font("Arial", Font.BOLD, 14));
                issueNumberLabel.setForeground(Color.WHITE);
                issueNumberLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

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

        seriesPanel.add(seriesTitle);
        seriesPanel.add(Box.createVerticalStrut(10));
        seriesPanel.add(issuesPanel);

        return seriesPanel;
    }

    private void showIssueDetails(Issue issue) {
        try {
            // Créer d'abord un panel de chargement
            IssueDetailsPanel loadingPanel = new IssueDetailsPanel();
            mainFrame.getMainContent().add(loadingPanel, "issueDetails");
            mainFrame.showPanel("issueDetails");

            Thread loadingThread = new Thread(() -> {
                try {
                    Issue basicIssue = apiClient.getIssuesDetails(issue);
                    Issue fullIssue = apiClient.getCompleteIssueDetails(basicIssue.getApi_detail_url());
                    
                    SwingUtilities.invokeLater(() -> {
                        try {
                            // Passer mainFrame au lieu de searchPage
                            IssueDetailsPanel detailsPanel = new IssueDetailsPanel(fullIssue, mainFrame, null);
                            mainFrame.getMainContent().add(detailsPanel, "issueDetails");
                            mainFrame.showPanel("issueDetails");
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                            showError("Error displaying issue details");
                        }
                    });
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                    SwingUtilities.invokeLater(() -> showError("Error fetching issue details"));
                }
            });
            loadingThread.start();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Error initializing details view");
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private JPanel createIssuePreview(Issue issue) {
        JPanel issuePanel = new JPanel();
        issuePanel.setLayout(new BoxLayout(issuePanel, BoxLayout.Y_AXIS));
        issuePanel.setOpaque(false);
        issuePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        try {
            URL imageUrl = new URL(issue.getImage());
            BufferedImage image = ImageIO.read(imageUrl);
            ImageIcon icon = new ImageIcon(image.getScaledInstance(120, 180, Image.SCALE_SMOOTH));
            JLabel imageLabel = new JLabel(icon);
            imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel titleLabel = new JLabel("<html><body style='width: 100px'>" + issue.getName() + "</body></html>");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
            titleLabel.setForeground(Color.WHITE);
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        int width = getWidth();
        int height = getHeight();

        Color startColor = new Color(51, 90, 183);
        Color endColor = new Color(30, 38, 48);

        GradientPaint gradient = new GradientPaint(0, 0, startColor, 0, height, endColor);
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, width, height);
    }
}