/*
 * Copyright (c) 2016 CubeXMC. All Rights Reserved.
 * Created by PantherMan594.
 */

package net.cubexmc.fallout.settlement;

import org.bukkit.Location;
import org.bukkit.entity.Villager;

import java.util.UUID;

/**
 * Created by David on 3/28.
 *
 * @author David
 */
public class Settler {
    private String name;
    private UUID uuid;
    private SettlerManager.Occupation occupation;
    private SettlerManager.Task task;
    private String target;
    private Villager entity;

    public Settler(String name, Location loc, SettlerManager.Occupation occupation, SettlerManager.Task task, String target) {
        this.name = name;
        this.occupation = occupation;
        this.task = task;
        this.target = target;
        entity = loc.getWorld().spawn(loc, Villager.class);
        uuid = entity.getUniqueId();
        entity.setCustomName(getName());
        entity.setCustomNameVisible(true);
        switch(occupation) {
            case FARMER:
                entity.setProfession(Villager.Profession.FARMER);
                break;
            case MINER:
            case BUILDER:
            case MECHANIC:
                entity.setProfession(Villager.Profession.BLACKSMITH);
                break;
            case NONE:
                entity.setProfession(Villager.Profession.LIBRARIAN);
                break;
        }
    }

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Location getLoc() {
        return entity.getLocation();
    }

    public SettlerManager.Occupation getOccupation() {
        return occupation;
    }

    public SettlerManager.Task getTask() {
        return task;
    }

    public String getTarget() {
        return target;
    }

    public Villager getEntity() {
        return entity;
    }
}
