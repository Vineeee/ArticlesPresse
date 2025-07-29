package com.example.app.tools;

import com.example.app.model.DrawingModel;
import com.example.app.model.Zone;
import java.awt.Point;
import java.awt.Cursor;
import java.awt.event.MouseEvent;

/**
 * Outil pour sélectionner/désélectionner des zones
 */
public class ZoneSelectionTool implements DrawingTool {
    private final DrawingModel model;
    
    public ZoneSelectionTool(DrawingModel model) {
        this.model = model;
    }
    
    @Override
    public void onMousePressed(MouseEvent e, Point imagePoint) {
        if (!model.hasImage()) return;
        
        // Ignorer le clic droit pour éviter de désélectionner
        if (e.getButton() == MouseEvent.BUTTON3) return;
        
        Zone clickedZone = model.getZoneAt(imagePoint);
        if (clickedZone != null) {
            if (model.isZoneSelected(clickedZone)) {
                model.deselectZone(clickedZone);
            } else {
                // Si Ctrl n'est pas enfoncé, vider la sélection précédente
                if (!e.isControlDown()) {
                    model.clearSelection();
                }
                model.selectZone(clickedZone);
            }
        } else {
            // Clic dans le vide : vider la sélection si Ctrl n'est pas enfoncé
            if (!e.isControlDown()) {
                model.clearSelection();
            }
        }
    }
    
    @Override
    public void onMouseDragged(MouseEvent e, Point imagePoint) {
        // Pas d'action pour le glissement avec cet outil
    }
    
    @Override
    public void onMouseReleased(MouseEvent e, Point imagePoint) {
        // Pas d'action pour le relâchement avec cet outil
    }
    
    @Override
    public void onMouseClicked(MouseEvent e, Point imagePoint) {
        // La logique de sélection est déjà gérée dans onMousePressed
    }
    
    @Override
    public String getToolName() {
        return "Sélection/Désélection de zones";
    }
    
    @Override
    public int getCursorType() {
        return Cursor.DEFAULT_CURSOR;
    }
}
