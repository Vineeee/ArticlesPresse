package com.example.app.model;

import java.awt.Point;

/**
 * Représente une zone rectangulaire dans l'image
 * Classe immutable pour assurer la cohérence des données
 */
public class Zone {
    public final int x, y, width, height;
    
    public Zone(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    /**
     * Vérifie si un point est contenu dans cette zone
     */
    public boolean contains(Point point) {
        return point.x >= x && point.x <= x + width &&
               point.y >= y && point.y <= y + height;
    }
    
    /**
     * Retourne les coordonnées du centre de la zone
     */
    public Point getCenter() {
        return new Point(x + width / 2, y + height / 2);
    }
    
    /**
     * Retourne la surface de la zone
     */
    public int getArea() {
        return width * height;
    }
    
    @Override
    public String toString() {
        return String.format("Zone [x=%d, y=%d, w=%d, h=%d]", x, y, width, height);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Zone zone = (Zone) obj;
        return x == zone.x && y == zone.y && width == zone.width && height == zone.height;
    }
    
    @Override
    public int hashCode() {
        return x * 31 + y * 31 + width * 31 + height;
    }
}
