package com.example.app.tools;

import java.awt.Point;
import java.awt.Cursor;
import java.awt.event.MouseEvent;

/**
 * Outil par défaut (aucune action)
 */
public class NoTool implements DrawingTool {
    
    @Override
    public void onMousePressed(MouseEvent e, Point imagePoint) {
        // Aucune action
    }
    
    @Override
    public void onMouseDragged(MouseEvent e, Point imagePoint) {
        // Aucune action
    }
    
    @Override
    public void onMouseReleased(MouseEvent e, Point imagePoint) {
        // Aucune action
    }
    
    @Override
    public void onMouseClicked(MouseEvent e, Point imagePoint) {
        // Aucune action
    }
    
    @Override
    public String getToolName() {
        return "Aucun outil";
    }
    
    @Override
    public int getCursorType() {
        return Cursor.DEFAULT_CURSOR;
    }
}
