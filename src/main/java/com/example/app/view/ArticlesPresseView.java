package com.example.app.view;

import com.example.app.controller.ApplicationController;
import com.example.app.model.ModelObserver;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

/**
 * Vue principale de l'application
 * Implémente le pattern MVC et Observer
 */
public class ArticlesPresseView extends JFrame implements ModelObserver {
    
    private final ApplicationController controller;
    
    // Composants principaux
    private JMenuBar menuBar;
    private JToolBar toolBar;
    private JScrollPane scrollPane;
    private JPanel statusBar;
    private JPanel sidePanel;
    private ImageDisplayPanel imagePanel;
    private JSplitPane splitPane;
    
    // Labels pour les informations
    private JLabel statusLabel;
    private JLabel imageInfoLabel;
    private JLabel zoomLabel;
    private JLabel zonesCountLabel;
    
    // Composants pour les onglets
    private JTabbedPane tabbedPane;
    
    /**
     * Constructeur principal
     */
    public ArticlesPresseView(ApplicationController controller) {
        this.controller = controller;
        
        // S'enregistrer comme observateur du modèle
        controller.getModel().addObserver(this);
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        
        setTitle("Articles Presse App - Architecture MVC");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        // Mise à jour initiale
        onModelChanged();
    }
    
    @Override
    public void onModelChanged() {
        SwingUtilities.invokeLater(() -> {
            updateImageInfo();
            updateZoomInfo();
            updateSidePanel();
            updateStatus();
            
            if (imagePanel != null) {
                imagePanel.updateDisplay();
            }
        });
    }
    
    /**
     * Initialise tous les composants
     */
    private void initializeComponents() {
        createMenuBar();
        createToolBar();
        
        // Zone d'affichage de l'image
        imagePanel = new ImageDisplayPanel(controller);
        scrollPane = new JScrollPane(imagePanel);
        scrollPane.setPreferredSize(new Dimension(600, 400));
        scrollPane.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
        
        // Améliorer le défilement : vitesse de défilement plus rapide
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(20);
        scrollPane.getVerticalScrollBar().setBlockIncrement(100);
        scrollPane.getHorizontalScrollBar().setBlockIncrement(100);
        
        createSidePanel();
        createStatusBar();
    }
    
    /**
     * Configure la disposition des composants
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(menuBar, BorderLayout.NORTH);
        topPanel.add(toolBar, BorderLayout.SOUTH);
        
        // Créer un JSplitPane pour permettre le redimensionnement du panneau latéral
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, sidePanel);
        splitPane.setResizeWeight(0.8); // 80% pour la zone principale, 20% pour le panneau latéral
        splitPane.setDividerSize(8);
        splitPane.setOneTouchExpandable(true); // Permet de masquer/afficher le panneau latéral
        
        add(topPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
    }
    
    /**
     * Configure les gestionnaires d'événements
     */
    private void setupEventHandlers() {
        // Gestion du zoom avec Ctrl + molette et défilement amélioré
        scrollPane.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.isControlDown() && controller.getModel().hasImage()) {
                    // Zoom avec Ctrl + molette
                    if (e.getWheelRotation() < 0) {
                        controller.zoomIn();
                    } else {
                        controller.zoomOut();
                    }
                } else {
                    // Défilement normal mais plus rapide
                    JScrollBar scrollBar = e.isShiftDown() ? 
                        scrollPane.getHorizontalScrollBar() : 
                        scrollPane.getVerticalScrollBar();
                    
                    int scrollAmount = e.getWheelRotation() * 40; // Augmenter la vitesse de défilement
                    scrollBar.setValue(scrollBar.getValue() + scrollAmount);
                }
            }
        });
        
        // Raccourcis clavier
        setupKeyboardShortcuts();
    }
    
    /**
     * Configure les raccourcis clavier
     */
    private void setupKeyboardShortcuts() {
        JRootPane rootPane = getRootPane();
        InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = rootPane.getActionMap();
        
        // Ctrl+Z pour Undo
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK), "undo");
        actionMap.put("undo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.undo();
            }
        });
        
        // Ctrl+Y pour Redo
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK), "redo");
        actionMap.put("redo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.redo();
            }
        });
        
        // Delete pour supprimer les zones sélectionnées
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "deleteSelected");
        actionMap.put("deleteSelected", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.deleteSelectedZones();
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
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        
        JMenuItem exitItem = new JMenuItem("Quitter");
        exitItem.addActionListener(e -> System.exit(0));
        
        fileMenu.add(openItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        // Menu Édition
        JMenu editMenu = new JMenu("Édition");
        
        JMenuItem undoItem = new JMenuItem("Annuler");
        undoItem.addActionListener(e -> controller.undo());
        undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
        
        JMenuItem redoItem = new JMenuItem("Rétablir");
        redoItem.addActionListener(e -> controller.redo());
        redoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK));
        
        editMenu.add(undoItem);
        editMenu.add(redoItem);
        
        // Menu Outils
        JMenu toolsMenu = new JMenu("Outils");
        
        JMenuItem rectangleItem = new JMenuItem("Délimiter zone rectangulaire");
        rectangleItem.addActionListener(e -> {
            controller.setRectangleSelectionTool();
            updateCursorAndToolbar();
        });
        
        JMenuItem selectItem = new JMenuItem("Sélectionner/Désélectionner zones");
        selectItem.addActionListener(e -> {
            controller.setZoneSelectionTool();
            updateCursorAndToolbar();
        });
        
        toolsMenu.add(rectangleItem);
        toolsMenu.add(selectItem);
        
        // Menu Vue
        JMenu viewMenu = new JMenu("Vue");
        
        JMenuItem zoomInItem = new JMenuItem("Zoom avant");
        zoomInItem.addActionListener(e -> controller.zoomIn());
        
        JMenuItem zoomOutItem = new JMenuItem("Zoom arrière");
        zoomOutItem.addActionListener(e -> controller.zoomOut());
        
        JMenuItem zoomResetItem = new JMenuItem("Zoom 100%");
        zoomResetItem.addActionListener(e -> controller.resetZoom());
        
        viewMenu.add(zoomInItem);
        viewMenu.add(zoomOutItem);
        viewMenu.addSeparator();
        viewMenu.add(zoomResetItem);
        
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(toolsMenu);
        menuBar.add(viewMenu);
        
        setJMenuBar(menuBar);
    }
    
    /**
     * Crée la barre d'outils
     */
    private void createToolBar() {
        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        // Bouton Ouvrir
        JButton openButton = createIconButton("📁", "Ouvrir une image");
        openButton.addActionListener(e -> openImageFile());
        
        // Bouton Undo
        JButton undoButton = createIconButton("↶", "Annuler");
        undoButton.addActionListener(e -> controller.undo());
        
        // Bouton Redo
        JButton redoButton = createIconButton("↷", "Rétablir");
        redoButton.addActionListener(e -> controller.redo());
        
        toolBar.add(openButton);
        toolBar.addSeparator();
        toolBar.add(undoButton);
        toolBar.add(redoButton);
        toolBar.addSeparator();
        
        // Boutons d'outils
        JButton rectangleButton = createIconButton("🟦", "Délimiter zone rectangulaire");
        rectangleButton.addActionListener(e -> {
            controller.setRectangleSelectionTool();
            updateCursorAndToolbar();
        });
        
        JButton selectButton = createIconButton("👆", "Sélectionner/Désélectionner zones");
        selectButton.addActionListener(e -> {
            controller.setZoneSelectionTool();
            updateCursorAndToolbar();
        });
        
        toolBar.add(rectangleButton);
        toolBar.add(selectButton);
        toolBar.addSeparator();
        
        // Boutons de zoom
        JButton zoomInButton = createIconButton("🔍+", "Zoom avant");
        zoomInButton.addActionListener(e -> controller.zoomIn());
        
        JButton zoomOutButton = createIconButton("🔍-", "Zoom arrière");
        zoomOutButton.addActionListener(e -> controller.zoomOut());
        
        JButton zoomResetButton = createIconButton("🔍=", "Zoom 100%");
        zoomResetButton.addActionListener(e -> controller.resetZoom());
        
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
        sidePanel.setMinimumSize(new Dimension(150, 0)); // Taille minimale
        
        // Créer le JTabbedPane
        tabbedPane = new JTabbedPane();
        
        // Onglet Informations
        JPanel infoTab = createInformationsTab();
        tabbedPane.addTab("Informations", infoTab);
        
        // Onglet Images
        JPanel imagesTab = createImagesTab();
        tabbedPane.addTab("Images", imagesTab);
        
        sidePanel.add(tabbedPane, BorderLayout.CENTER);
    }
    
    /**
     * Crée l'onglet Informations
     */
    private JPanel createInformationsTab() {
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
        
        return infoPanel;
    }
    
    /**
     * Crée l'onglet Images
     */
    private JPanel createImagesTab() {
        JPanel imagesPanel = new JPanel(new BorderLayout());
        imagesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Message simple pour l'onglet Images
        JLabel messageLabel = new JLabel("<html><center>Fonctionnalités images<br>à venir prochainement</center></html>");
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setFont(messageLabel.getFont().deriveFont(Font.ITALIC));
        
        // Bouton pour ouvrir une image
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton openImageButton = new JButton("Ouvrir une image...");
        openImageButton.addActionListener(e -> openImageFile());
        buttonPanel.add(openImageButton);
        
        imagesPanel.add(messageLabel, BorderLayout.CENTER);
        imagesPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        return imagesPanel;
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
            if (!controller.loadImage(selectedFile)) {
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors du chargement de l'image: " + selectedFile.getName(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Met à jour les informations de l'image
     */
    private void updateImageInfo() {
        if (controller.getModel().hasImage()) {
            String name = controller.getModel().getCurrentImageName();
            var image = controller.getModel().getCurrentImage();
            imageInfoLabel.setText("<html>" + name + "<br>" + 
                image.getWidth() + "x" + image.getHeight() + "px</html>");
        } else {
            imageInfoLabel.setText("Aucune image");
        }
    }
    
    /**
     * Met à jour les informations de zoom
     */
    private void updateZoomInfo() {
        double zoom = controller.getModel().getZoomFactor();
        zoomLabel.setText("Zoom: " + Math.round(zoom * 100) + "%");
    }
    
    /**
     * Met à jour le panneau latéral
     */
    private void updateSidePanel() {
        int zoneCount = controller.getModel().getZoneCount();
        int selectedCount = controller.getModel().getSelectedZoneCount();
        
        if (selectedCount > 0) {
            zonesCountLabel.setText("Zones: " + zoneCount + " (" + selectedCount + " sélectionnées)");
        } else {
            zonesCountLabel.setText("Zones: " + zoneCount);
        }
    }
    
    /**
     * Met à jour le message de statut
     */
    private void updateStatus() {
        String toolName = controller.getCurrentTool().getToolName();
        statusLabel.setText(" " + toolName);
    }
    
    /**
     * Met à jour le curseur et la barre d'outils selon l'outil actuel
     */
    private void updateCursorAndToolbar() {
        if (imagePanel != null) {
            imagePanel.updateDisplay(); // Ceci mettra à jour le curseur
        }
        updateStatus();
    }
}
