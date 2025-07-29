package com.example.app.tools;

import com.example.app.model.DrawingModel;
import com.example.app.model.Zone;
import com.example.app.commands.CommandManager;
import com.example.app.commands.AddZoneCommand;
import java.awt.Point;
import java.awt.Cursor;
import java.awt.event.MouseEvent;

/**
 * Outil pour créer des zones rectangulaires
 */
public class RectangleSelectionTool implements DrawingTool {
    private final DrawingModel model;
    private final CommandManager commandManager;
    private Point startPoint;
    private Point currentPoint;
    private boolean drawing = false;
    
    public RectangleSelectionTool(DrawingModel model, CommandManager commandManager) {
        this.model = model;
        this.commandManager = commandManager;
    }
    
    @Override
    public void onMousePressed(MouseEvent e, Point imagePoint) {
        if (!model.hasImage()) return;
        
        // Ignorer le clic droit pour cet outil
        if (e.getButton() == MouseEvent.BUTTON3) return;
        
        startPoint = new Point(imagePoint);
        currentPoint = new Point(imagePoint);
        drawing = true;
    }
    
    @Override
    public void onMouseDragged(MouseEvent e, Point imagePoint) {
        if (drawing) {
            currentPoint = new Point(imagePoint);
            // Le composant d'affichage devra se redessiner pour montrer le rectangle temporaire
        }
    }
    
    @Override
    public void onMouseReleased(MouseEvent e, Point imagePoint) {
        if (!drawing || startPoint == null) return;
        
        currentPoint = new Point(imagePoint);
        
        int x = Math.min(startPoint.x, currentPoint.x);
        int y = Math.min(startPoint.y, currentPoint.y);
        int width = Math.abs(currentPoint.x - startPoint.x);
        int height = Math.abs(currentPoint.y - startPoint.y);
        
        // Ne créer la zone que si elle a une taille minimale
        if (width > 5 && height > 5) {
            Zone newZone = new Zone(x, y, width, height);
            AddZoneCommand command = new AddZoneCommand(model, newZone);
            commandManager.executeCommand(command);
        }
        
        drawing = false;
        startPoint = null;
        currentPoint = null;
    }
    
    @Override
    public void onMouseClicked(MouseEvent e, Point imagePoint) {
        // Pas d'action spéciale pour les clics simples
    }
    
    @Override
    public String getToolName() {
        return "Délimitation de zone rectangulaire";
    }
    
    @Override
    public int getCursorType() {
        return Cursor.CROSSHAIR_CURSOR;
    }
    
    /**
     * Retourne le point de départ du rectangle en cours de création
     */
    public Point getStartPoint() {
        return startPoint == null ? null : new Point(startPoint);
    }
    
    /**
     * Retourne le point actuel du rectangle en cours de création
     */
    public Point getCurrentPoint() {
        return currentPoint == null ? null : new Point(currentPoint);
    }
    
    /**
     * Indique si un rectangle est en cours de création
     */
    public boolean isDrawing() {
        return drawing;
    }
}
