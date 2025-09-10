package tse.info2.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import tse.info2.model.Issue;
import tse.info2.model.Personnage;
import tse.info2.model.Power;

public class PersonnageDetailsPanel extends JPanel {

    private SearchPage searchPage;

    public PersonnageDetailsPanel(Personnage personnage, SearchPage searchPage) {
        this.searchPage = searchPage;
        // Créer le panneau principal pour contenir tous les composants
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(null); // Positionnement absolu
        contentPanel.setPreferredSize(new Dimension(1280, 4000)); // Ajuster la hauteur pour permettre le défilement

        Color darkBlue = new Color(10, 12, 30);
        Color white = Color.WHITE;

        contentPanel.setBackground(darkBlue);

        
        // Titre du personnage
        JLabel nameLabel = new JLabel("Character: " + personnage.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 30));
        nameLabel.setForeground(white);
        nameLabel.setBounds(470, 50, 600, 50);
        contentPanel.setComponentZOrder(nameLabel, 0); // Met le titre au-dessus de l'image floue

        // Description du personnage
        JTextArea descriptionArea = new JTextArea(personnage.getDescription());
        descriptionArea.setFont(new Font("Arial", Font.ITALIC, 18));
        descriptionArea.setForeground(white);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setLineWrap(true);
        descriptionArea.setEditable(false);

        // Supprimer la bordure et définir l'arrière-plan comme transparent
        descriptionArea.setOpaque(false);
        descriptionArea.setBorder(null);

        descriptionArea.setBounds(450, 120, 500, 150);
        contentPanel.add(descriptionArea);
        contentPanel.setComponentZOrder(descriptionArea, 0); // Met la description au-dessus de l'image floue

        
        JButton backButton = createRoundedButton("Back to list", white, darkBlue);
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBackground(new Color(0x3F51B5));
        backButton.setForeground(Color.WHITE);
        backButton.setBounds(450,310 , 150, 40);
        backButton.setFocusPainted(false);
        backButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        backButton.addActionListener(e -> {
            if (searchPage != null) {
                searchPage.updateResultsDisplay();
            }
        });
        contentPanel.add(backButton);
        contentPanel.setComponentZOrder(backButton, 0); // Met le bouton au-dessus de l'image floue




        try {
            URL imageUrl = new URL(personnage.getImage());
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
        detailsPanel.setBounds(100, 420, 1300, 900);

     // Section Powers
        JLabel powersLabel = new JLabel("Powers:");
        powersLabel.setFont(new Font("Arial", Font.BOLD, 20));
        powersLabel.setForeground(new Color(0xe4f1fe));
        powersLabel.setBounds(50, 20, 200, 30); // Coordonnées relatives à detailsPanel
        detailsPanel.add(powersLabel);

        int yPositionPowers = 50; // Position verticale initiale pour les pouvoirs, relative au cadre
        if (personnage.getPowers() != null && !personnage.getPowers().isEmpty()) {
            for (Power power : personnage.getPowers()) {
                JLabel powerLabel = new JLabel("- " + power.getName());
                powerLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                powerLabel.setForeground(white);
                powerLabel.setBounds(70, yPositionPowers, 400, 20); // Coordonnées relatives au cadre
                detailsPanel.add(powerLabel); // Ajouter au detailsPanel
                yPositionPowers += 30; // Espacement entre chaque pouvoir
            }
        } else {
            JLabel noPowersLabel = new JLabel("No powers available.");
            noPowersLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            noPowersLabel.setForeground(new Color(180, 180, 180));
            noPowersLabel.setBounds(40, yPositionPowers, 400, 20); // Coordonnées relatives au cadre
            detailsPanel.add(noPowersLabel); // Ajouter au detailsPanel
            yPositionPowers += 30;
        }
        
        
        // Section Appearances
        JLabel appearancesLabel = new JLabel("Appearances:");
        appearancesLabel.setFont(new Font("Arial", Font.BOLD, 20));
        appearancesLabel.setForeground(new Color(0xe4f1fe));
        appearancesLabel.setBounds(700, 20, 200, 30); // Aligner en haut à droite de "Powers"
        detailsPanel.add(appearancesLabel);

        int yPositionAppearances = 50; // Position verticale initiale pour les apparitions
        if (personnage.getAppearances() != null && !personnage.getAppearances().isEmpty()) {
            int count = 0; // Limiter à 15 apparitions
            for (Issue appearance : personnage.getAppearances()) {
                if (count >= 15) {
                    break; // Limiter à 15
                }
                JLabel appearanceLabel = new JLabel("- " + appearance.getName());
                appearanceLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                appearanceLabel.setForeground(white);
                appearanceLabel.setBounds(720, yPositionAppearances, 600, 20); // Relative à "Appearances"
                detailsPanel.add(appearanceLabel);
                yPositionAppearances += 30; // Espacement entre chaque apparition
                count++;
            }
        } else {
            JLabel noAppearancesLabel = new JLabel("No appearances available.");
            noAppearancesLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            noAppearancesLabel.setForeground(new Color(180, 180, 180));
            noAppearancesLabel.setBounds(720, yPositionAppearances, 400, 20);
            detailsPanel.add(noAppearancesLabel);
            yPositionAppearances += 30;
        }

        // Ajouter le panneau au parent (déjà présent dans votre code)
        contentPanel.add(detailsPanel);

        // Réorganiser les couches pour s'assurer que detailsPanel est au-dessus
        contentPanel.setComponentZOrder(detailsPanel, 0); // Met detailsPanel tout en haut

       

        // Encapsuler le panneau principal dans un JScrollPane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Vitesse de défilement
        scrollPane.setBounds(0, 0, 1280, 720); // Taille de la fenêtre visible

        // Ajouter le JScrollPane à la page principale
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
    }

    public PersonnageDetailsPanel() {
        // Créer un panneau de contenu avec la même structure
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(null);
        Color darkBlue = new Color(10, 12, 30);
        Color white = Color.WHITE;
        contentPanel.setBackground(darkBlue);
        contentPanel.setPreferredSize(new Dimension(1280, 4000));

        // Titre "Chargement..."
        JLabel loadingLabel = new JLabel("Chargement des détails du personnage...");
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
        detailsPanel.setBounds(100, 420, 1300, 900);

        // Ajouter les sections en cours de chargement
        addLoadingSection(detailsPanel, "Powers", 50, 50);
        addLoadingSection(detailsPanel, "Appearances", 700, 50);

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

    private JButton createRoundedButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.setColor(fgColor);
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int textHeight = fm.getAscent();
                g2.drawString(getText(), (getWidth() - textWidth) / 2, (getHeight() + textHeight) / 2 - 2);
                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
            }

            @Override
            public void setContentAreaFilled(boolean b) {
            }
        };
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        return button;
    }
}
