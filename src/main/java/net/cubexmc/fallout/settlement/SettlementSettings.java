/*
 * Copyright (c) 2016 CubeXMC. All Rights Reserved.
 * Created by PantherMan594.
 */

package net.cubexmc.fallout.settlement;

import com.massivecraft.factions.entity.Faction;
import net.cubexmc.fallout.Fallout;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by David on 1/28.
 *
 * @author David
 */
class SettlementSettings {

    void load(Faction fac) {
        boolean isNew = false;
        if (!Fallout.getInstance().getDataFolder().exists()) {
            if (!Fallout.getInstance().getDataFolder().mkdir()) {
                Bukkit.getLogger().warning("Unable to create config folder!");
            }
        }
        if (!new File(Fallout.getInstance().getDataFolder() + File.separator + "settlementdata").exists()) {
            if (!new File(Fallout.getInstance().getDataFolder() + File.separator + "settlementdata").mkdir()) {
                Bukkit.getLogger().warning("Unable to create settlementdata folder!");
            }
        }
        File f = new File(Fallout.getInstance().getDataFolder() + File.separator + "settlementdata" + File.separator + fac.getId() + ".yml");
        try {
            isNew = f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!isNew) {
            FileConfiguration con = YamlConfiguration.loadConfiguration(f);
        }
    }

    public void save(UUID uuid) {
        File f = new File(Fallout.getInstance().getDataFolder() + File.separator + "settlementdata" + File.separator + uuid.toString() + ".yml");
        FileConfiguration con = YamlConfiguration.loadConfiguration(f);
        con.set("claimLevels", Fallout.getInstance().getStats().getClaimLevel(uuid));
        con.set("stats.radLevel", Fallout.getInstance().getStats().getRadLevel(uuid));
        con.set("stats.luck", Fallout.getInstance().getStats().getLLevel(uuid));
        con.set("stats.intelligence", Fallout.getInstance().getStats().getILevel(uuid));
        con.set("stats.endurance", Fallout.getInstance().getStats().getELevel(uuid));
        con.set("stats.strength", Fallout.getInstance().getStats().getSLevel(uuid));
        if (Fallout.getInstance().getPlayerGui().getInvs().containsKey(uuid)) {
            ItemStack[] items = Fallout.getInstance().getPlayerGui().getInvs().get(uuid);
            con.set("items", items);
        }
        if (Fallout.getInstance().getPlayerGui().getLocs().containsKey(uuid)) {
            int i = 0;
            for (String name : Fallout.getInstance().getPlayerGui().getLocs().get(uuid).keySet()) {
                Location loc = Fallout.getInstance().getPlayerGui().getLocs().get(uuid).get(name);
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
