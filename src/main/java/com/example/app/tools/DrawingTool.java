package com.example.app.tools;

import java.awt.Point;
import java.awt.event.MouseEvent;

/**
 * Interface Strategy pour les outils de dessin
 */
public interface DrawingTool {
    /**
     * Appelé lors du clic de souris
     */
    void onMousePressed(MouseEvent e, Point imagePoint);
    
    /**
     * Appelé lors du glissement de souris
     */
    void onMouseDragged(MouseEvent e, Point imagePoint);
    
    /**
     * Appelé lors du relâchement de souris
     */
    void onMouseReleased(MouseEvent e, Point imagePoint);
    
    /**
     * Appelé lors du clic simple
     */
    void onMouseClicked(MouseEvent e, Point imagePoint);
    
    /**
     * Retourne le nom de l'outil
     */
    String getToolName();
    
    /**
     * Retourne le curseur à utiliser pour cet outil
     */
    int getCursorType();
}
