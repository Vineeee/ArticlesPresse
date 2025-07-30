package com.example.app;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * Application ArticlesPresseApp
 * Application pour annoter des articles de presse avec délimitation de zones
 */

public class ArticlesPresse extends JFrame {
    
    public enum Tool {
        NONE, RECTANGLE_SELECTION, ZONE_SELECTION
    }
    
    // Composants principaux
    private JMenuBar menuBar;
    private JToolBar toolBar;
    private JScrollPane scrollPane;
    private JPanel statusBar;
    private JPanel sidePanel;
    private ImagePanel imagePanel;
    
    // État de l'application
    private Tool currentTool = Tool.NONE;
    private BufferedImage currentImage;
    private double zoomFactor = 1.0;
    private List<Zone> zones = new ArrayList<>();
    private List<Zone> selectedZones = new ArrayList<>();
    
    // Labels pour les informations
    private JLabel statusLabel;
    private JLabel imageInfoLabel;
    private JLabel zoomLabel;
    private JLabel zonesCountLabel;
    
    /**
     * Constructeur principal
     */
    public ArticlesPresse() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        
        setTitle("Articles Presse App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
    }
    
    /**
     * Initialise tous les composants
     */
    private void initializeComponents() {
        // Barre de menu
        createMenuBar();
        
        // Barre d'outils avec icônes
        createToolBarWithIcons();
        
        // Zone d'affichage de l'image
        imagePanel = new ImagePanel();
        scrollPane = new JScrollPane(imagePanel);
        scrollPane.setPreferredSize(new Dimension(600, 400));
        
        // Centrer l'image dans le scroll pane
        scrollPane.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
        
        // Panneau latéral
        createSidePanel();
        
        // Barre de statut
        createStatusBar();
    }
    
    /**
     * Configure la disposition des composants
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Assurer que la toolbar soit sous le menu
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(menuBar, BorderLayout.NORTH);
        topPanel.add(toolBar, BorderLayout.SOUTH);
        
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
        add(sidePanel, BorderLayout.EAST);
    }
    
    /**
     * Configure les gestionnaires d'événements
     */
    private void setupEventHandlers() {
        // Gestion du zoom avec Ctrl + molette
        scrollPane.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.isControlDown() && currentImage != null) {
                    Point mousePos = e.getPoint();
                    
                    if (e.getWheelRotation() < 0) {
                        // Zoom avant
                        zoomIn();
                    } else {
                        // Zoom arrière
                        zoomOut();
                    }
                    
                    // Centrer le zoom sur la position de la souris
                    centerZoomOnPoint(mousePos);
                }
            }
        });
    }
    
    /**
     * Crée la barre de menu
     */
    private void createMenuBar() {
        menuBar = new JMenuBar();
        
        // Menu Fichier
        JMenu fileMenu = new JMenu("Fichier");
        
        JMenuItem openItem = new JMenuItem("Ouvrir...");
        openItem.addActionListener(e -> openImageFile());
        
        JMenuItem exitItem = new JMenuItem("Quitter");
        exitItem.addActionListener(e -> System.exit(0));
        
        fileMenu.add(openItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        // Menu Outils
        JMenu toolsMenu = new JMenu("Outils");
        
        JMenuItem rectangleItem = new JMenuItem("Délimiter zone rectangulaire");
        rectangleItem.addActionListener(e -> setCurrentTool(Tool.RECTANGLE_SELECTION));
        
        JMenuItem selectItem = new JMenuItem("Sélectionner/Désélectionner zones");
        selectItem.addActionListener(e -> setCurrentTool(Tool.ZONE_SELECTION));
        
        toolsMenu.add(rectangleItem);
        toolsMenu.add(selectItem);
        
        // Menu Vue
        JMenu viewMenu = new JMenu("Vue");
        
        JMenuItem zoomInItem = new JMenuItem("Zoom avant");
        zoomInItem.addActionListener(e -> zoomIn());
        
        JMenuItem zoomOutItem = new JMenuItem("Zoom arrière");
        zoomOutItem.addActionListener(e -> zoomOut());
        
        JMenuItem zoomResetItem = new JMenuItem("Zoom 100%");
        zoomResetItem.addActionListener(e -> resetZoom());
        
        viewMenu.add(zoomInItem);
        viewMenu.add(zoomOutItem);
        viewMenu.addSeparator();
        viewMenu.add(zoomResetItem);
        
        menuBar.add(fileMenu);
        menuBar.add(toolsMenu);
        menuBar.add(viewMenu);
        
        setJMenuBar(menuBar);
    }
    
    /**
     * Crée la barre d'outils avec des icônes
     */
    private void createToolBarWithIcons() {
        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        // Bouton Ouvrir avec icône
        JButton openButton = createIconButton("📁", "Ouvrir une image");
        openButton.addActionListener(e -> openImageFile());
        
        // Bouton Zone rectangulaire avec icône
        JButton rectangleButton = createIconButton("🟦", "Délimiter zone rectangulaire");
        rectangleButton.addActionListener(e -> setCurrentTool(Tool.RECTANGLE_SELECTION));
        
        // Bouton Sélection avec icône
        JButton selectButton = createIconButton("👆", "Sélectionner/Désélectionner zones");
        selectButton.addActionListener(e -> setCurrentTool(Tool.ZONE_SELECTION));
        
        // Séparateur
        toolBar.addSeparator();
        
        // Bouton Zoom avant avec icône
        JButton zoomInButton = createIconButton("🔍+", "Zoom avant");
        zoomInButton.addActionListener(e -> zoomIn());
        
        // Bouton Zoom arrière avec icône
        JButton zoomOutButton = createIconButton("🔍-", "Zoom arrière");
        zoomOutButton.addActionListener(e -> zoomOut());
        
        // Bouton Reset zoom avec icône
        JButton zoomResetButton = createIconButton("🔍=", "Zoom 100%");
        zoomResetButton.addActionListener(e -> resetZoom());
        
        toolBar.add(openButton);
        toolBar.addSeparator();
        toolBar.add(rectangleButton);
        toolBar.add(selectButton);
        toolBar.addSeparator();
        toolBar.add(zoomInButton);
        toolBar.add(zoomOutButton);
        toolBar.add(zoomResetButton);
    }
    
    /**
     * Crée un bouton avec icône et tooltip
     */
    private JButton createIconButton(String icon, String tooltip) {
        JButton button = new JButton(icon);
        button.setToolTipText(tooltip);
        button.setPreferredSize(new Dimension(40, 40));
        button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        button.setFocusPainted(false);
        return button;
    }
    
    /**
     * Crée le panneau latéral
     */
    private void createSidePanel() {
        sidePanel = new JPanel(new BorderLayout());
        sidePanel.setPreferredSize(new Dimension(200, 0));
        sidePanel.setBorder(BorderFactory.createTitledBorder("Informations"));
        
        JPanel infoPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        imageInfoLabel = new JLabel("Aucune image");
        zoomLabel = new JLabel("Zoom: 100%");
        zonesCountLabel = new JLabel("Zones: 0");
        
        infoPanel.add(new JLabel("Image:"));
        infoPanel.add(imageInfoLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(zoomLabel);
        infoPanel.add(zonesCountLabel);
        
        sidePanel.add(infoPanel, BorderLayout.NORTH);
    }
    
    /**
     * Crée la barre de statut
     */
    private void createStatusBar() {
        statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createLoweredBevelBorder());
        
        statusLabel = new JLabel(" Prêt");
        statusBar.add(statusLabel, BorderLayout.WEST);
    }
    
    /**
     * Ouvre un fichier image
     */
    private void openImageFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter(
            "Images (*.jpg, *.jpeg, *.png, *.tiff, *.gif, *.bmp)", 
            "jpg", "jpeg", "png", "tiff", "gif", "bmp"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            loadImage(selectedFile);
        }
    }
    
    /**
     * Charge une image dans l'application
     */
    private void loadImage(File file) {
        try {
            currentImage = ImageIO.read(file);
            if (currentImage != null) {
                zones.clear();
                selectedZones.clear();
                zoomFactor = 1.0;
                
                imagePanel.updateImage();
                centerImage();
                updateImageInfo(file.getName());
                updateStatus("Image chargée: " + file.getName());
                updateSidePanel();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors du chargement de l'image: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Centre l'image dans le viewport
     */
    private void centerImage() {
        if (currentImage != null && imagePanel != null) {
            SwingUtilities.invokeLater(() -> {
                JViewport viewport = scrollPane.getViewport();
                Dimension viewSize = viewport.getExtentSize();
                Dimension panelSize = imagePanel.getPreferredSize();
                
                int x = Math.max(0, (panelSize.width - viewSize.width) / 2);
                int y = Math.max(0, (panelSize.height - viewSize.height) / 2);
                
                viewport.setViewPosition(new Point(x, y));
            });
        }
    }
    
    /**
     * Centre le zoom sur un point spécifique
     */
    private void centerZoomOnPoint(Point mousePos) {
        SwingUtilities.invokeLater(() -> {
            JViewport viewport = scrollPane.getViewport();
            Point viewPos = viewport.getViewPosition();
            Dimension viewSize = viewport.getExtentSize();
            
            // Calculer la nouvelle position pour centrer sur le point de la souris
            int newX = (int)(mousePos.x * zoomFactor - viewSize.width / 2.0);
            int newY = (int)(mousePos.y * zoomFactor - viewSize.height / 2.0);
            
            newX = Math.max(0, Math.min(newX, imagePanel.getWidth() - viewSize.width));
            newY = Math.max(0, Math.min(newY, imagePanel.getHeight() - viewSize.height));
            
            viewport.setViewPosition(new Point(newX, newY));
        });
    }
    
    /**
     * Définit l'outil actuel
     */
    private void setCurrentTool(Tool tool) {
        currentTool = tool;
        selectedZones.clear();
        
        switch (tool) {
            case RECTANGLE_SELECTION:
                updateStatus("Outil actif: Délimitation de zone rectangulaire");
                break;
            case ZONE_SELECTION:
                updateStatus("Outil actif: Sélection/Désélection de zones");
                break;
            default:
                updateStatus("Aucun outil sélectionné");
                break;
        }
    }
    
    /**
     * Zoom avant
     */
    private void zoomIn() {
        if (currentImage != null) {
            zoomFactor = Math.min(zoomFactor * 1.2, 5.0);
            imagePanel.updateImage();
            updateZoomInfo();
            updateStatus("Zoom: " + Math.round(zoomFactor * 100) + "%");
        }
    }
    
    /**
     * Zoom arrière
     */
    private void zoomOut() {
        if (currentImage != null) {
            zoomFactor = Math.max(zoomFactor / 1.2, 0.1);
            imagePanel.updateImage();
            updateZoomInfo();
            updateStatus("Zoom: " + Math.round(zoomFactor * 100) + "%");
        }
    }
    
    /**
     * Reset du zoom à 100%
     */
    private void resetZoom() {
        if (currentImage != null) {
            zoomFactor = 1.0;
            imagePanel.updateImage();
            centerImage();
            updateZoomInfo();
            updateStatus("Zoom remis à 100%");
        }
    }
    
    /**
     * Met à jour les informations de l'image
     */
    private void updateImageInfo(String filename) {
        if (currentImage != null) {
            imageInfoLabel.setText("<html>" + filename + "<br>" + 
                currentImage.getWidth() + "x" + currentImage.getHeight() + "px</html>");
        }
    }
    
    /**
     * Met à jour les informations de zoom
     */
    private void updateZoomInfo() {
        zoomLabel.setText("Zoom: " + Math.round(zoomFactor * 100) + "%");
    }
    
    /**
     * Met à jour le panneau latéral
     */
    private void updateSidePanel() {
        zonesCountLabel.setText("Zones: " + zones.size());
        updateZoomInfo();
    }
    
    /**
     * Met à jour le message de statut
     */
    private void updateStatus(String message) {
        statusLabel.setText(" " + message);
    }
    
    /**
     * Classe interne pour gérer l'affichage de l'image
     */
    private class ImagePanel extends JPanel {
        private Point startPoint;
        private Point endPoint;
        private boolean drawing = false;
        
        public ImagePanel() {
            setBackground(Color.LIGHT_GRAY);
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (currentImage == null) return;
                    
                    Point imagePoint = screenToImageCoordinates(e.getPoint());
                    
                    if (currentTool == Tool.RECTANGLE_SELECTION) {
                        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                        startPoint = imagePoint;
                        drawing = true;
                    } else if (currentTool == Tool.ZONE_SELECTION) {
                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        handleZoneSelection(imagePoint);
                    } else {
                        setCursor(Cursor.getDefaultCursor());
                    }
                }
                
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (currentImage == null || !drawing) return;
                    
                    if (currentTool == Tool.RECTANGLE_SELECTION && startPoint != null) {
                        Point imagePoint = screenToImageCoordinates(e.getPoint());
                        endPoint = imagePoint;
                        
                        int x = Math.min(startPoint.x, endPoint.x);
                        int y = Math.min(startPoint.y, endPoint.y);
                        int width = Math.abs(endPoint.x - startPoint.x);
                        int height = Math.abs(endPoint.y - startPoint.y);
                        
                        if (width > 5 && height > 5) {
                            Zone newZone = new Zone(x, y, width, height);
                            zones.add(newZone);
                            updateStatus("Zone créée: " + newZone.toString());
                            updateSidePanel();
                        }
                        
                        drawing = false;
                        startPoint = null;
                        endPoint = null;
                        repaint();
                    }
                }
                
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON3 && !selectedZones.isEmpty()) {
                        showContextMenu(e.getPoint());
                    }
                }
            });
            
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (drawing && currentTool == Tool.RECTANGLE_SELECTION) {
                        endPoint = screenToImageCoordinates(e.getPoint());
                        repaint();
                    }
                }
            });
        }
        
        /**
         * Convertit les coordonnées écran en coordonnées image
         */
        private Point screenToImageCoordinates(Point screenPoint) {
            int imageX = (int)(screenPoint.x / zoomFactor);
            int imageY = (int)(screenPoint.y / zoomFactor);
            return new Point(imageX, imageY);
        }
        
        /**
         * Convertit les coordonnées image en coordonnées écran
         */
        private Point imageToScreenCoordinates(Point imagePoint) {
            int screenX = (int)(imagePoint.x * zoomFactor);
            int screenY = (int)(imagePoint.y * zoomFactor);
            return new Point(screenX, screenY);
        }
        
        /**
         * Gère la sélection des zones
         */
        private void handleZoneSelection(Point imagePoint) {
            for (Zone zone : zones) {
                if (zone.contains(imagePoint)) {
                    if (selectedZones.contains(zone)) {
                        selectedZones.remove(zone);
                        updateStatus("Zone désélectionnée: " + zone.toString());
                    } else {
                        selectedZones.add(zone);
                        updateStatus("Zone sélectionnée: " + zone.toString());
                    }
                    repaint();
                    return;
                }
            }
        }
        
        /**
         * Affiche le menu contextuel
         */
        private void showContextMenu(Point point) {
            JPopupMenu popup = new JPopupMenu();
            
            JMenuItem deleteItem = new JMenuItem("Supprimer zone(s) sélectionnée(s)");
            deleteItem.addActionListener(e -> deleteSelectedZones());
            
            popup.add(deleteItem);
            popup.show(this, point.x, point.y);
        }
        
        /**
         * Supprime les zones sélectionnées
         */
        private void deleteSelectedZones() {
            zones.removeAll(selectedZones);
            updateStatus("Zones supprimées: " + selectedZones.size());
            selectedZones.clear();
            updateSidePanel();
            repaint();
        }
        
        /**
         * Met à jour l'affichage de l'image
         */
        public void updateImage() {
            if (currentImage != null) {
                int scaledWidth = (int)(currentImage.getWidth() * zoomFactor);
                int scaledHeight = (int)(currentImage.getHeight() * zoomFactor);
                setPreferredSize(new Dimension(scaledWidth, scaledHeight));
                revalidate();
                repaint();
            }
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            if (currentImage != null) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
                                   RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                
                int scaledWidth = (int)(currentImage.getWidth() * zoomFactor);
                int scaledHeight = (int)(currentImage.getHeight() * zoomFactor);
                
                g2d.drawImage(currentImage, 0, 0, scaledWidth, scaledHeight, null);
                
                // Dessiner les zones existantes
                for (int i = 0; i < zones.size(); i++) {
                    Zone zone = zones.get(i);
                    Color color = selectedZones.contains(zone) ? Color.RED : Color.BLUE;
                    drawZone(g2d, zone, color, i + 1);
                }
                
                // Dessiner la zone en cours de création
                if (drawing && startPoint != null && endPoint != null) {
                    int x = (int)(Math.min(startPoint.x, endPoint.x) * zoomFactor);
                    int y = (int)(Math.min(startPoint.y, endPoint.y) * zoomFactor);
                    int width = (int)(Math.abs(endPoint.x - startPoint.x) * zoomFactor);
                    int height = (int)(Math.abs(endPoint.y - startPoint.y) * zoomFactor);
                    
                    g2d.setColor(Color.GREEN);
                    g2d.setStroke(new BasicStroke(2));
                    g2d.drawRect(x, y, width, height);
                }
                
                g2d.dispose();
            }
        }
        
        /**
         * Dessine une zone avec numérotation
         */
        private void drawZone(Graphics2D g2d, Zone zone, Color color, int number) {
            int x = (int)(zone.x * zoomFactor);
            int y = (int)(zone.y * zoomFactor);
            int width = (int)(zone.width * zoomFactor);
            int height = (int)(zone.height * zoomFactor);
            
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRect(x, y, width, height);
            
            // Numérotation
            g2d.setColor(Color.WHITE);
            g2d.fillRect(x - 10, y - 10, 20, 20);
            g2d.setColor(color);
            g2d.drawRect(x - 10, y - 10, 20, 20);
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            FontMetrics fm = g2d.getFontMetrics();
            String numberStr = String.valueOf(number);
            int textX = x - fm.stringWidth(numberStr) / 2;
            int textY = y + fm.getAscent() / 2 - 2;
            g2d.drawString(numberStr, textX, textY);
        }
    }

    public class Zone {
        public final int x, y, width, height;
        
        public Zone(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
        
        public boolean contains(Point point) {
            return point.x >= x && point.x <= x + width &&
                    point.y >= y && point.y <= y + height;
        }
        
        @Override
        public String toString() {
            return String.format("Zone [x=%d, y=%d, w=%d, h=%d]", x, y, width, height);
        }
    }

}
