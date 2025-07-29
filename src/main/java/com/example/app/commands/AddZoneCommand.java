package com.example.app.commands;

import com.example.app.model.DrawingModel;
import com.example.app.model.Zone;

/**
 * Commande pour ajouter une zone
 */
public class AddZoneCommand implements Command {
    private final DrawingModel model;
    private final Zone zone;
    
    public AddZoneCommand(DrawingModel model, Zone zone) {
        this.model = model;
        this.zone = zone;
    }
    
    @Override
    public void execute() {
        model.addZone(zone);
    }
    
    @Override
    public void undo() {
        model.removeZone(zone);
    }
    
    @Override
    public String getDescription() {
        return "Ajouter zone " + zone.toString();
    }
}
