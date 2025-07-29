package com.example.app.view;

import com.example.app.controller.ApplicationController;
import com.example.app.model.Zone;
import com.example.app.tools.RectangleSelectionTool;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

/**
 * Composant pour afficher l'image et gérer les interactions
 */
public class ImageDisplayPanel extends JPanel {
    private final ApplicationController controller;
    
    public ImageDisplayPanel(ApplicationController controller) {
        this.controller = controller;
        setBackground(Color.LIGHT_GRAY);
        
        setupMouseListeners();
    }
    
    /**
     * Configure les listeners de souris
     */
    private void setupMouseListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Gérer le clic droit en priorité pour le menu contextuel
                if (e.getButton() == MouseEvent.BUTTON3 && 
                    controller.getModel().getSelectedZoneCount() > 0) {
                    showContextMenu(e.getPoint());
                    return; // Ne pas traiter cet événement avec l'outil
                }
                
                Point imagePoint = screenToImageCoordinates(e.getPoint());
                controller.getCurrentTool().onMousePressed(e, imagePoint);
                updateCursor();
                repaint();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                // Ignorer le clic droit pour les outils
                if (e.getButton() == MouseEvent.BUTTON3) return;
                
                Point imagePoint = screenToImageCoordinates(e.getPoint());
                controller.getCurrentTool().onMouseReleased(e, imagePoint);
                repaint();
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                // Le menu contextuel est déjà géré dans mousePressed
                // Ne traiter ici que les clics gauches
                if (e.getButton() != MouseEvent.BUTTON1) return;
                
                Point imagePoint = screenToImageCoordinates(e.getPoint());
                controller.getCurrentTool().onMouseClicked(e, imagePoint);
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point imagePoint = screenToImageCoordinates(e.getPoint());
                controller.getCurrentTool().onMouseDragged(e, imagePoint);
                repaint();
            }
        });
    }
    
    /**
     * Met à jour le curseur selon l'outil actuel
     */
    private void updateCursor() {
        int cursorType = controller.getCurrentTool().getCursorType();
        setCursor(Cursor.getPredefinedCursor(cursorType));
    }
    
    /**
     * Convertit les coordonnées écran en coordonnées image
     */
    private Point screenToImageCoordinates(Point screenPoint) {
        double zoom = controller.getModel().getZoomFactor();
        int imageX = (int)(screenPoint.x / zoom);
        int imageY = (int)(screenPoint.y / zoom);
        return new Point(imageX, imageY);
    }
    
    /**
     * Convertit les coordonnées image en coordonnées écran
     */
    private Point imageToScreenCoordinates(Point imagePoint) {
        double zoom = controller.getModel().getZoomFactor();
        int screenX = (int)(imagePoint.x * zoom);
        int screenY = (int)(imagePoint.y * zoom);
        return new Point(screenX, screenY);
    }
    
    /**
     * Affiche le menu contextuel pour les zones sélectionnées
     */
    private void showContextMenu(Point point) {
        JPopupMenu popup = new JPopupMenu();
        
        JMenuItem deleteItem = new JMenuItem("Supprimer zone(s) sélectionnée(s)");
        deleteItem.addActionListener(e -> controller.deleteSelectedZones());
        
        popup.add(deleteItem);
        popup.show(this, point.x, point.y);
    }
    
    /**
     * Met à jour l'affichage du composant
     */
    public void updateDisplay() {
        BufferedImage image = controller.getModel().getCurrentImage();
        if (image != null) {
            double zoom = controller.getModel().getZoomFactor();
            int scaledWidth = (int)(image.getWidth() * zoom);
            int scaledHeight = (int)(image.getHeight() * zoom);
            setPreferredSize(new Dimension(scaledWidth, scaledHeight));
            revalidate();
        }
        updateCursor();
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        BufferedImage image = controller.getModel().getCurrentImage();
        if (image == null) return;
        
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
                           RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        
        double zoom = controller.getModel().getZoomFactor();
        int scaledWidth = (int)(image.getWidth() * zoom);
        int scaledHeight = (int)(image.getHeight() * zoom);
        
        // Dessiner l'image
        g2d.drawImage(image, 0, 0, scaledWidth, scaledHeight, null);
        
        // Dessiner les zones existantes
        drawZones(g2d);
        
        // Dessiner la zone en cours de création (si applicable)
        drawCurrentDrawing(g2d);
        
        g2d.dispose();
    }
    
    /**
     * Dessine toutes les zones
     */
    private void drawZones(Graphics2D g2d) {
        var zones = controller.getModel().getZones();
        for (int i = 0; i < zones.size(); i++) {
            Zone zone = zones.get(i);
            boolean isSelected = controller.getModel().isZoneSelected(zone);
            Color color = isSelected ? Color.RED : Color.BLUE;
            drawZone(g2d, zone, color, i + 1);
        }
    }
    
    /**
     * Dessine une zone avec numérotation
     */
    private void drawZone(Graphics2D g2d, Zone zone, Color color, int number) {
        double zoom = controller.getModel().getZoomFactor();
        
        int x = (int)(zone.x * zoom);
        int y = (int)(zone.y * zoom);
        int width = (int)(zone.width * zoom);
        int height = (int)(zone.height * zoom);
        
        // Dessiner le rectangle
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(x, y, width, height);
        
        // Numérotation
        g2d.setColor(Color.WHITE);
        g2d.fillOval(x - 10, y - 10, 20, 20);
        g2d.setColor(color);
        g2d.drawOval(x - 10, y - 10, 20, 20);
        
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        FontMetrics fm = g2d.getFontMetrics();
        String numberStr = String.valueOf(number);
        int textX = x - fm.stringWidth(numberStr) / 2;
        int textY = y + fm.getAscent() / 2 - 2;
        g2d.drawString(numberStr, textX, textY);
    }
    
    /**
     * Dessine la zone en cours de création (rectangle temporaire)
     */
    private void drawCurrentDrawing(Graphics2D g2d) {
        if (controller.getCurrentTool() instanceof RectangleSelectionTool) {
            RectangleSelectionTool rectTool = (RectangleSelectionTool) controller.getCurrentTool();
            
            if (rectTool.isDrawing()) {
                Point start = rectTool.getStartPoint();
                Point current = rectTool.getCurrentPoint();
                
                if (start != null && current != null) {
                    double zoom = controller.getModel().getZoomFactor();
                    
                    int x = (int)(Math.min(start.x, current.x) * zoom);
                    int y = (int)(Math.min(start.y, current.y) * zoom);
                    int width = (int)(Math.abs(current.x - start.x) * zoom);
                    int height = (int)(Math.abs(current.y - start.y) * zoom);
                    
                    g2d.setColor(Color.GREEN);
                    g2d.setStroke(new BasicStroke(2));
                    g2d.drawRect(x, y, width, height);
                }
            }
        }
    }
}
