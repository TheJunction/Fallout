/*
 * Copyright (c) 2016 CubeXMC. All Rights Reserved.
 * Created by PantherMan594.
 */

package net.cubexmc.fallout.settlement;

import net.cubexmc.fallout.Fallout;
import net.minecraft.server.v1_9_R1.EntityInsentient;
import net.minecraft.server.v1_9_R1.PathEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Created by David on 3/28.
 *
 * @author David
 */
public class SettlerManager {
    private Map<UUID, Settler> settlers;

    public SettlerManager() {
        settlers = new HashMap<>();
        Bukkit.getScheduler().runTaskTimer(Fallout.getInstance(), () -> {
            for (Settler settler : settlers.values()) {
                String[] target = settler.getTarget().split(";");
                switch (settler.getTask()) {
                    case HARVEST:
                        List<Material> crops = new ArrayList<>();
                        crops.add(Material.WHEAT);
                        crops.add(Material.POTATO_ITEM);
                        crops.add(Material.CARROT_ITEM);
                        crops.add(Material.BEETROOT);
                        crops.add(Material.MELON);
                        crops.add(Material.PUMPKIN);
                        int radius = Integer.valueOf(target[0]);
                        for (int x = -radius; x <= radius; x++) {
                            for (int y = -radius; y <= radius; y++) {
                                for (int z = -radius; z <= radius; z++) {
                                    Location loc = settler.getLoc().getBlock().getRelative(x, y, z).getLocation();
                                    if (loc.getBlock().getType() == Material.valueOf(target[1])) {
                                        Collection<ItemStack> drops = loc.getBlock().getDrops();
                                        Iterator<ItemStack> dropIterator = drops.iterator();
                                        boolean found = false;
                                        while (dropIterator.hasNext() && !found) {
                                            found = crops.contains(dropIterator.next().getType());
                                        }
                                        if (found) {
                                            EntityInsentient nmsEntity = ((EntityInsentient) settler.getEntity());
                                            PathEntity path = nmsEntity.getNavigation().a(loc.getX(), loc.getY(), loc.getZ());
                                            nmsEntity.getNavigation().a(path, 1.0);
                                        }
                                    }
                                }
                            }
                        }
                }
            }
        }, 0, 20);
    }

    public Map<UUID, Settler> getSettlers() {
        return settlers;
    }

    public void addSettler(Settler settler) {
        settlers.put(settler.getUuid(), settler);
    }

    public enum Occupation {
        FARMER,
        MINER,
        BUILDER,
        MECHANIC,
        NONE
    }

    public enum Task {
        HARVEST,    //radius;blockTypeToHarvest
        WATER,
        PLANT,
        MINE,
        BUILD,
        RUN,
        NONE
    }

    public enum Crops {
        WHEAT,
        POTATO,
        CARROT,
        BEETROOT,
        MELON,
        PUMPKIN,
        SUGARCANE
    }
}
