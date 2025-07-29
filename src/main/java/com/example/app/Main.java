package com.example.app;

import com.example.app.controller.ApplicationController;
import com.example.app.view.ArticlesPresseView;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Classe principale de l'application
 * Utilise maintenant l'architecture MVC avec Design Patterns
 */
public class Main {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Utiliser le look and feel par défaut
            }
            
            // Création du contrôleur (qui crée le modèle)
            ApplicationController controller = new ApplicationController();
            
            // Création de la vue (qui s'enregistre comme observateur du modèle)
            ArticlesPresseView view = new ArticlesPresseView(controller);
            
            // Affichage de l'application
            view.setVisible(true);
        });
    }
}
