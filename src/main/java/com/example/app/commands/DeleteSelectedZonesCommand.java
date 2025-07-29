package com.example.app.commands;

import com.example.app.model.DrawingModel;
import com.example.app.model.Zone;
import java.util.List;
import java.util.ArrayList;

/**
 * Commande pour supprimer les zones sélectionnées
 */
public class DeleteSelectedZonesCommand implements Command {
    private final DrawingModel model;
    private final List<Zone> deletedZones;
    
    public DeleteSelectedZonesCommand(DrawingModel model) {
        this.model = model;
        this.deletedZones = new ArrayList<>(model.getSelectedZones());
    }
    
    @Override
    public void execute() {
        model.removeSelectedZones();
    }
    
    @Override
    public void undo() {
        for (Zone zone : deletedZones) {
            model.addZone(zone);
            model.selectZone(zone);
        }
    }
    
    @Override
    public String getDescription() {
        return "Supprimer " + deletedZones.size() + " zone(s)";
    }
}
