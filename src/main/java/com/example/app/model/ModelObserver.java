package com.example.app.model;

/**
 * Interface Observer pour le pattern Observer
 * Les classes qui implémentent cette interface seront notifiées
 * des changements dans le modèle
 */
public interface ModelObserver {
    /**
     * Méthode appelée lorsque le modèle change
     */
    void onModelChanged();
}
