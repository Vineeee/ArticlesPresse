package com.example.app.controller;

import com.example.app.model.DrawingModel;
import com.example.app.commands.CommandManager;
import com.example.app.commands.DeleteSelectedZonesCommand;
import com.example.app.tools.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Contrôleur principal de l'application
 * Coordonne les interactions entre le modèle, la vue et les outils
 */
public class ApplicationController {
    private final DrawingModel model;
    private final CommandManager commandManager;
    private DrawingTool currentTool;
    
    // Outils disponibles
    private final NoTool noTool;
    private final RectangleSelectionTool rectangleSelectionTool;
    private final ZoneSelectionTool zoneSelectionTool;
    
    public ApplicationController() {
        this.model = new DrawingModel();
        this.commandManager = new CommandManager();
        
        // Initialiser les outils
        this.noTool = new NoTool();
        this.rectangleSelectionTool = new RectangleSelectionTool(model, commandManager);
        this.zoneSelectionTool = new ZoneSelectionTool(model);
        
        // Outil par défaut
        this.currentTool = noTool;
    }
    
    // Getters pour le modèle et les gestionnaires
    public DrawingModel getModel() {
        return model;
    }
    
    public CommandManager getCommandManager() {
        return commandManager;
    }
    
    // Gestion des outils
    public DrawingTool getCurrentTool() {
        return currentTool;
    }
    
    public void setNoTool() {
        this.currentTool = noTool;
    }
    
    public void setRectangleSelectionTool() {
        this.currentTool = rectangleSelectionTool;
    }
    
    public void setZoneSelectionTool() {
        this.currentTool = zoneSelectionTool;
    }
    
    public RectangleSelectionTool getRectangleSelectionTool() {
        return rectangleSelectionTool;
    }
    
    // Actions de l'application
    public boolean loadImage(File file) {
        try {
            BufferedImage image = ImageIO.read(file);
            if (image != null) {
                model.setCurrentImage(image, file.getName());
                commandManager.clear(); // Effacer l'historique lors du chargement d'une nouvelle image
                return true;
            }
        } catch (IOException e) {
            // L'erreur sera gérée par la vue
        }
        return false;
    }
    
    public void zoomIn() {
        model.zoomIn();
    }
    
    public void zoomOut() {
        model.zoomOut();
    }
    
    public void resetZoom() {
        model.resetZoom();
    }
    
    public void deleteSelectedZones() {
        if (model.getSelectedZoneCount() > 0) {
            DeleteSelectedZonesCommand command = new DeleteSelectedZonesCommand(model);
            commandManager.executeCommand(command);
        }
    }
    
    public void undo() {
        commandManager.undo();
    }
    
    public void redo() {
        commandManager.redo();
    }
    
    public boolean canUndo() {
        return commandManager.canUndo();
    }
    
    public boolean canRedo() {
        return commandManager.canRedo();
    }
    
    public String getUndoDescription() {
        return commandManager.getUndoDescription();
    }
    
    public String getRedoDescription() {
        return commandManager.getRedoDescription();
    }
}
