package com.example.app.commands;

/**
 * Interface Command pour le pattern Command
 * Permet d'encapsuler les actions et d'implémenter undo/redo
 */
public interface Command {
    /**
     * Exécute la commande
     */
    void execute();
    
    /**
     * Annule la commande
     */
    void undo();
    
    /**
     * Retourne la description de la commande
     */
    String getDescription();
}
