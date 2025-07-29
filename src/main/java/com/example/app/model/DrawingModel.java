package com.example.app.model;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Modèle principal de l'application de dessin
 * Implémente le pattern Observer pour notifier les changements
 */
public class DrawingModel {
    private BufferedImage currentImage;
    private double zoomFactor = 1.0;
    private List<Zone> zones = new ArrayList<>();
    private List<Zone> selectedZones = new ArrayList<>();
    private List<ModelObserver> observers = new ArrayList<>();
    private String currentImageName = "";
    
    // Observer Pattern
    public void addObserver(ModelObserver observer) {
        observers.add(observer);
    }
    
    public void removeObserver(ModelObserver observer) {
        observers.remove(observer);
    }
    
    private void notifyObservers() {
        for (ModelObserver observer : observers) {
            observer.onModelChanged();
        }
    }
    
    // Gestion de l'image
    public void setCurrentImage(BufferedImage image, String imageName) {
        this.currentImage = image;
        this.currentImageName = imageName;
        this.zones.clear();
        this.selectedZones.clear();
        this.zoomFactor = 1.0;
        notifyObservers();
    }
    
    public BufferedImage getCurrentImage() {
        return currentImage;
    }
    
    public String getCurrentImageName() {
        return currentImageName;
    }
    
    // Gestion du zoom
    public void setZoomFactor(double zoomFactor) {
        this.zoomFactor = Math.max(0.1, Math.min(5.0, zoomFactor));
        notifyObservers();
    }
    
    public double getZoomFactor() {
        return zoomFactor;
    }
    
    public void zoomIn() {
        setZoomFactor(zoomFactor * 1.2);
    }
    
    public void zoomOut() {
        setZoomFactor(zoomFactor / 1.2);
    }
    
    public void resetZoom() {
        setZoomFactor(1.0);
    }
    
    // Gestion des zones
    public void addZone(Zone zone) {
        zones.add(zone);
        notifyObservers();
    }
    
    public void removeZone(Zone zone) {
        zones.remove(zone);
        selectedZones.remove(zone);
        notifyObservers();
    }
    
    public void removeSelectedZones() {
        zones.removeAll(selectedZones);
        selectedZones.clear();
        notifyObservers();
    }
    
    public List<Zone> getZones() {
        return new ArrayList<>(zones);
    }
    
    // Gestion de la sélection
    public void selectZone(Zone zone) {
        if (!selectedZones.contains(zone)) {
            selectedZones.add(zone);
            notifyObservers();
        }
    }
    
    public void deselectZone(Zone zone) {
        if (selectedZones.remove(zone)) {
            notifyObservers();
        }
    }
    
    public void clearSelection() {
        if (!selectedZones.isEmpty()) {
            selectedZones.clear();
            notifyObservers();
        }
    }
    
    public List<Zone> getSelectedZones() {
        return new ArrayList<>(selectedZones);
    }
    
    public boolean isZoneSelected(Zone zone) {
        return selectedZones.contains(zone);
    }
    
    // Méthodes utilitaires
    public Zone getZoneAt(Point point) {
        for (Zone zone : zones) {
            if (zone.contains(point)) {
                return zone;
            }
        }
        return null;
    }
    
    public boolean hasImage() {
        return currentImage != null;
    }
    
    public int getZoneCount() {
        return zones.size();
    }
    
    public int getSelectedZoneCount() {
        return selectedZones.size();
    }
}
