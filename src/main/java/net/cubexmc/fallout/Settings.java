/*
 * Copyright (c) 2015 David Shen. All Rights Reserved.
 * Created by PantherMan594.
 */

package net.cubexmc.fallout;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by David on 12/23.
 *
 * @author David
 */
public class Settings {

    public void load(UUID uuid) {
        boolean isNew = false;
        if (!Fallout.getInstance().getDataFolder().exists()) {
            if (!Fallout.getInstance().getDataFolder().mkdir()) {
                Bukkit.getLogger().warning("Unable to create config folder!");
            }
        }
        if (!new File(Fallout.getInstance().getDataFolder() + File.separator + "playerdata").exists()) {
            if (!new File(Fallout.getInstance().getDataFolder() + File.separator + "playerdata").mkdir()) {
                Bukkit.getLogger().warning("Unable to create playerdata folder!");
            }
        }
        File f = new File(Fallout.getInstance().getDataFolder() + File.separator + "playerdata" + File.separator + uuid.toString() + ".yml");
        try {
            isNew = f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int claimLevel = 3;
        double radLevel = 0;
        int lLevel = 1; //max 50
        int iLevel = 1; //max 9
        int eLevel = 1; //max 15
        int sLevel = 1; //max 15
        ItemStack[] inv = new ItemStack[45];
        HashMap<String, Location> locs = new HashMap<>();
        if (!isNew) {
            FileConfiguration con = YamlConfiguration.loadConfiguration(f);
            claimLevel = con.getInt("claimLevels");
            radLevel = con.getDouble("stats.radLevel");
            lLevel = con.getInt("stats.luck");
            iLevel = con.getInt("stats.intelligence");
            eLevel = con.getInt("stats.endurance");
            sLevel = con.getInt("stats.strength");
            for (int i = 0; i < 18; i++) {
                if (con.contains("locs." + i + ".name")) {
                    String name = con.getString("locs." + i + ".name");
                    String world = con.getString("locs." + i + ".world");
                    int  x = con.getInt("locs." + i + ".x");
                    int y = con.getInt("locs." + i + ".y");
                    int z = con.getInt("locs." + i + ".z");
                    Location location = new Location(Bukkit.getWorld(world), x, y, z);
                    locs.put(name, location);
                }
            }
            List<?> itemList = con.getList("items");
            if (itemList != null && !itemList.isEmpty()) {
                inv = itemList.toArray(new ItemStack[itemList.size()]);
            }
        }
        Fallout.getInstance().getStats().setClaimLevel(uuid, claimLevel);
        Fallout.getInstance().getStats().setRadLevel(uuid, radLevel);
        Fallout.getInstance().getStats().setLLevel(uuid, lLevel);
        Fallout.getInstance().getStats().setILevel(uuid, iLevel);
        Fallout.getInstance().getStats().setELevel(uuid, eLevel);
        Fallout.getInstance().getStats().setSLevel(uuid, sLevel);
        Fallout.getInstance().getGui().setInv(uuid, inv);
        Fallout.getInstance().getGui().setLoc(uuid, locs);
    }

    public void save(UUID uuid) {
        File f = new File(Fallout.getInstance().getDataFolder() + File.separator + "playerdata" + File.separator + uuid.toString() + ".yml");
        FileConfiguration con = YamlConfiguration.loadConfiguration(f);
        con.set("claimLevels", Fallout.getInstance().getStats().getClaimLevel(uuid));
        con.set("stats.radLevel", Fallout.getInstance().getStats().getRadLevel(uuid));
        con.set("stats.luck", Fallout.getInstance().getStats().getLLevel(uuid));
        con.set("stats.intelligence", Fallout.getInstance().getStats().getILevel(uuid));
        con.set("stats.endurance", Fallout.getInstance().getStats().getELevel(uuid));
        con.set("stats.strength", Fallout.getInstance().getStats().getSLevel(uuid));
        if (Fallout.getInstance().getGui().getInvs().containsKey(uuid)) {
            ItemStack[] items = Fallout.getInstance().getGui().getInvs().get(uuid);
            con.set("items", items);
        }
        if (Fallout.getInstance().getGui().getLocs().containsKey(uuid)) {
            int i = 0;
            for (String name : Fallout.getInstance().getGui().getLocs().get(uuid).keySet()) {
                Location loc = Fallout.getInstance().getGui().getLocs().get(uuid).get(name);
                con.set("locs." + i + ".name", name);
                con.set("locs." + i + ".world", loc.getWorld().getName());
                con.set("locs." + i + ".x", loc.getBlockX());
                con.set("locs." + i + ".y", loc.getBlockY());
                con.set("locs." + i + ".z", loc.getBlockZ());
                i++;
            }
        }
        try {
            con.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
