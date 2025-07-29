package com.example.app.commands;

import java.util.Stack;

/**
 * Gestionnaire de commandes pour implémenter undo/redo
 */
public class CommandManager {
    private final Stack<Command> undoStack = new Stack<>();
    private final Stack<Command> redoStack = new Stack<>();
    private final int maxCommands;
    
    public CommandManager(int maxCommands) {
        this.maxCommands = maxCommands;
    }
    
    public CommandManager() {
        this(50); // Par défaut, 50 commandes maximum
    }
    
    /**
     * Exécute une commande et l'ajoute à la pile d'undo
     */
    public void executeCommand(Command command) {
        command.execute();
        
        undoStack.push(command);
        redoStack.clear(); // Vider la pile de redo
        
        // Limiter la taille de la pile d'undo
        while (undoStack.size() > maxCommands) {
            undoStack.remove(0);
        }
    }
    
    /**
     * Annule la dernière commande
     */
    public boolean undo() {
        if (!undoStack.isEmpty()) {
            Command command = undoStack.pop();
            command.undo();
            redoStack.push(command);
            return true;
        }
        return false;
    }
    
    /**
     * Refait la dernière commande annulée
     */
    public boolean redo() {
        if (!redoStack.isEmpty()) {
            Command command = redoStack.pop();
            command.execute();
            undoStack.push(command);
            return true;
        }
        return false;
    }
    
    /**
     * Vérifie si undo est possible
     */
    public boolean canUndo() {
        return !undoStack.isEmpty();
    }
    
    /**
     * Vérifie si redo est possible
     */
    public boolean canRedo() {
        return !redoStack.isEmpty();
    }
    
    /**
     * Retourne la description de la prochaine commande à annuler
     */
    public String getUndoDescription() {
        return undoStack.isEmpty() ? null : undoStack.peek().getDescription();
    }
    
    /**
     * Retourne la description de la prochaine commande à refaire
     */
    public String getRedoDescription() {
        return redoStack.isEmpty() ? null : redoStack.peek().getDescription();
    }
    
    /**
     * Efface l'historique des commandes
     */
    public void clear() {
        undoStack.clear();
        redoStack.clear();
    }
}
