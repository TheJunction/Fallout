/*
 * Copyright (c) 2016 CubeXMC. All Rights Reserved.
 * Created by PantherMan594.
 */

package net.cubexmc.fallout;

import com.earth2me.essentials.api.Economy;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.ps.PS;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;

import java.util.*;

/**
 * Created by David on 12/26.
 *
 * @author David
 */
public class Gui {
    private Map<UUID, ItemStack[]> invs;
    private Map<UUID, HashMap<String, Location>> locs;
    private List<ItemStack> menu;

    public Gui() {
        invs = new HashMap<>();
        locs = new HashMap<>();
        menu = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            String name = "";
            switch (i) {
                case 0:
                    name = "STATUS";
                    break;
                case 1:
                    name = "LIES";
                    break;
                case 2:
                    name = "INV";
                    break;
                case 3:
                    name = "DATA";
                    break;
                case 4:
                    name = "MAP";
                    break;
                case 5:
                    name = "RADIO";
                    break;
            }
            ItemStack item = new ItemStack(Material.BARRIER);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(name);
            meta.setLore(Fallout.getInstance().getIdentifier());
            item.setItemMeta(meta);
            menu.add(item);
        }
        ItemStack item = new ItemStack(Material.WORKBENCH);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("Workbench");
        meta.setLore(Fallout.getInstance().getIdentifier());
        item.setItemMeta(meta);
        menu.add(item);

        item = new ItemStack(Material.BARRIER);
        meta = item.getItemMeta();
        meta.setLore(Fallout.getInstance().getIdentifier());
        item.setItemMeta(meta);
        menu.add(item);

        item = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bm = (BookMeta) item.getItemMeta();
        bm.setTitle("Fallout Handbook");
        bm.setAuthor("Vault Boy");
        bm.setDisplayName("Fallout Guide");
        bm.setLore(Fallout.getInstance().getIdentifier());
        item.setItemMeta(meta);
        menu.add(item);
    }

    public void setInv(UUID uuid, ItemStack[] inv) {
        this.invs.put(uuid, inv);
    }


    public Map<UUID, ItemStack[]> getInvs() {
        return invs;
    }

    public void setLoc(UUID uuid, HashMap<String, Location> loc) {
        locs.put(uuid, loc);
    }

    public Map<UUID, HashMap<String, Location>> getLocs() {
        return locs;
    }

    public Inventory openStatus(Player p) {
        UUID uuid = p.getUniqueId();
        p.getInventory().getItem(0).setDurability((short) 0);
        Inventory resInv = Bukkit.createInventory(null, 54, "STATUS");
        for (int i = 0; i < 9; i++) {
            resInv.setItem(i, menu.get(i));
        }

        ItemStack caps = new ItemStack(Material.INK_SACK, 1, (short) 8);
        ItemMeta meta = caps.getItemMeta();
        try {
            meta.setDisplayName(Economy.getMoney(p.getName()) + " Caps");
        } catch (Exception e)  {
            e.printStackTrace();
        }
        meta.setLore(Fallout.getInstance().getIdentifier());
        caps.setItemMeta(meta);

        ItemStack level = new ItemStack(Material.EXP_BOTTLE);
        meta = level.getItemMeta();
        meta.setDisplayName("Level " + p.getLevel());
        meta.setLore(Fallout.getInstance().getIdentifier());
        level.setItemMeta(meta);

        short durability = (short) (Material.GOLD_HELMET.getMaxDurability() * (20 - p.getHealth()) / 20);
        ItemStack health = new ItemStack(Material.GOLD_HELMET, 1, durability);
        meta = health.getItemMeta();
        meta.setDisplayName("Health " + p.getHealth() + "/20");
        meta.setLore(Fallout.getInstance().getIdentifier());
        health.setItemMeta(meta);

        double radiation = Fallout.getInstance().getStats().getRadLevel(uuid);
        durability = (short) (Material.GOLD_CHESTPLATE.getMaxDurability() * radiation / 20);
        ItemStack rad = new ItemStack(Material.GOLD_CHESTPLATE, 1, durability);
        meta = rad.getItemMeta();
        meta.setDisplayName("Radiation: " + radiation + "/20");
        meta.setLore(Fallout.getInstance().getIdentifier());
        rad.setItemMeta(meta);

        double invWeight = getInvWeight(p);
        double maxWeight = Fallout.getInstance().getStats().getSLevel(uuid) * 5;
        if (invWeight >= maxWeight) {
            durability = Material.GOLD_LEGGINGS.getMaxDurability();
        } else {
            durability = (short) (Material.GOLD_LEGGINGS.getMaxDurability() * invWeight / maxWeight);
        }
        ItemStack weight = new ItemStack(Material.GOLD_LEGGINGS, 1, durability);
        meta = weight.getItemMeta();
        meta.setDisplayName("Weight: " + invWeight + "/" + maxWeight);
        meta.setLore(Fallout.getInstance().getIdentifier());
        weight.setItemMeta(meta);

        ItemStack effects = new ItemStack(Material.GOLD_BOOTS);
        meta = effects.getItemMeta();
        meta.setDisplayName("Status Effects");
        List<String> lore = new ArrayList<>();
        for (PotionEffect pE : p.getActivePotionEffects()) {
            lore.add(ChatColor.GRAY + pE.getType().getName() + " " + pE.getAmplifier() + " (" + pE.getDuration() + ")");
        }
        lore.add(Fallout.getInstance().getIdentifier().get(0));
        meta.setLore(lore);
        effects.setItemMeta(meta);

        resInv.setItem(21, caps);
        resInv.setItem(22, p.getInventory().getHelmet());
        resInv.setItem(23, level);
        resInv.setItem(30, health);
        resInv.setItem(31, p.getInventory().getChestplate());
        resInv.setItem(32, rad);
        resInv.setItem(39, weight);
        resInv.setItem(40, p.getInventory().getLeggings());
        resInv.setItem(41, effects);
        resInv.setItem(49, p.getInventory().getBoots());
        return resInv;
    }

    public Inventory openLies(Player p) {
        UUID uuid = p.getUniqueId();
        p.getInventory().getItem(0).setDurability((short) 1);
        Inventory resInv = Bukkit.createInventory(null, 54, "LIES");
        for (int i = 0; i < 9; i++) {
            resInv.setItem(i, menu.get(i));
        }

        ItemStack luck = new ItemStack(Material.RABBIT_FOOT);
        ItemMeta meta = luck.getItemMeta();
        meta.setDisplayName("Luck");
        meta.setLore(Arrays.asList(Fallout.getInstance().getIdentifier().get(0), "Increase for a higher chance of keeping", "your inventory and LIES on death, also", "increases chance of successful CATS hit."));
        luck.setItemMeta(meta);

        ItemStack intelligence = new ItemStack(Material.APPLE);
        meta = intelligence.getItemMeta();
        meta.setDisplayName("Intelligence");
        meta.setLore(Arrays.asList(Fallout.getInstance().getIdentifier().get(0), "Increase to unlock new crafting", "recipes and to use more complex items."));
        intelligence.setItemMeta(meta);

        ItemStack endurance = new ItemStack(Material.BEDROCK);
        meta = endurance.getItemMeta();
        meta.setDisplayName("Endurance");
        meta.setLore(Arrays.asList(Fallout.getInstance().getIdentifier().get(0), "Increase for a higher resistance from radiation."));
        endurance.setItemMeta(meta);

        ItemStack strength = new ItemStack(Material.BONE);
        meta = strength.getItemMeta();
        meta.setDisplayName("Strength");
        meta.setLore(Arrays.asList(Fallout.getInstance().getIdentifier().get(0), "Increase to carry more items in", "inventory without slowing down."));
        strength.setItemMeta(meta);

        int lvl = Fallout.getInstance().getStats().getLLevel(uuid);
        ItemStack luckL = new ItemStack(Material.EXP_BOTTLE, lvl);
        meta = luckL.getItemMeta();
        meta.setDisplayName("Level " + lvl);
        meta.setLore(Fallout.getInstance().getIdentifier());
        luckL.setItemMeta(meta);

        lvl = Fallout.getInstance().getStats().getILevel(uuid);
        ItemStack intelligenceL = new ItemStack(Material.EXP_BOTTLE, lvl);
        meta = intelligenceL.getItemMeta();
        meta.setDisplayName("Level " + lvl);
        meta.setLore(Fallout.getInstance().getIdentifier());
        intelligenceL.setItemMeta(meta);

        lvl = Fallout.getInstance().getStats().getELevel(uuid);
        ItemStack enduranceL = new ItemStack(Material.EXP_BOTTLE, lvl);
        meta = enduranceL.getItemMeta();
        meta.setDisplayName("Level " + lvl);
        meta.setLore(Fallout.getInstance().getIdentifier());
        enduranceL.setItemMeta(meta);

        lvl = Fallout.getInstance().getStats().getSLevel(uuid);
        ItemStack strengthL = new ItemStack(Material.EXP_BOTTLE, lvl);
        meta = strengthL.getItemMeta();
        meta.setDisplayName("Level " + lvl);
        meta.setLore(Fallout.getInstance().getIdentifier());
        strengthL.setItemMeta(meta);

        int xpLvl = p.getLevel();
        lvl = xpLvl;
        if (xpLvl > 64) {
            xpLvl = 64;
        }
        ItemStack level = new ItemStack(Material.EXP_BOTTLE, xpLvl);
        meta = level.getItemMeta();
        meta.setDisplayName("Level " + lvl + " (" + Fallout.getInstance().getStats().getClaimLevel(uuid) + ") to claim.");
        meta.setLore(Fallout.getInstance().getIdentifier());
        level.setItemMeta(meta);

        resInv.setItem(19, luck);
        resInv.setItem(21, intelligence);
        resInv.setItem(23, endurance);
        resInv.setItem(25, strength);
        resInv.setItem(28, luckL);
        resInv.setItem(30, intelligenceL);
        resInv.setItem(32, enduranceL);
        resInv.setItem(34, strengthL);
        resInv.setItem(53, level);
        return resInv;
    }

    public Inventory openInv(Player p) {
        UUID uuid = p.getUniqueId();
        Inventory resInv = Bukkit.createInventory(null, 54, "INV");
        if (invs.containsKey(uuid)) {
            resInv.setContents(invs.get(uuid));
        }


        p.getInventory().getItem(0).setDurability((short) 2);
        for (int i = 0; i < 9; i++) {
            resInv.setItem(i, menu.get(i));
        }
        return resInv;
    }

    public Inventory openData(Player p) {
        UUID uuid = p.getUniqueId();
        p.getInventory().getItem(0).setDurability((short) 3);
        Inventory resInv = Bukkit.createInventory(null, 54, "DATA");
        for (int i = 0; i < 9; i++) {
            resInv.setItem(i, menu.get(i));
        }

        ItemStack save = new ItemStack(Material.COMPASS);
        ItemMeta meta = save.getItemMeta();
        meta.setDisplayName("Save Location");
        meta.setLore(Fallout.getInstance().getIdentifier());
        save.setItemMeta(meta);

        ItemStack delete = new ItemStack(Material.LAVA_BUCKET);
        meta = delete.getItemMeta();
        meta.setDisplayName("Delete Location");
        meta.setLore(Fallout.getInstance().getIdentifier());
        delete.setItemMeta(meta);

        resInv.setItem(11, save);
        resInv.setItem(15, delete);

        if (Fallout.getInstance().getGui().getLocs().containsKey(uuid)) {
            int i = 18;
            for (String name : Fallout.getInstance().getGui().getLocs().get(uuid).keySet()) {
                if (i < 36) {
                    Location loc = Fallout.getInstance().getGui().getLocs().get(uuid).get(name);
                    ItemStack locItem = new ItemStack(Material.MAP);
                    meta = locItem.getItemMeta();
                    meta.setDisplayName(name);
                    meta.setLore(Arrays.asList(Fallout.getInstance().getIdentifier().get(0), "World: " + loc.getWorld().getName(), "XYZ: " + loc.getBlockX() + " / " + loc.getBlockY() + " / " + loc.getBlockZ()));
                    locItem.setItemMeta(meta);
                    resInv.setItem(i, locItem);
                    i++;
                }
            }
        }

        Faction faction = MPlayer.get(uuid).getFaction();
        ItemStack item = new ItemStack(Material.EMPTY_MAP);
        meta = item.getItemMeta();
        meta.setDisplayName("Land: " + faction.getLandCount());
        meta.setLore(Fallout.getInstance().getIdentifier());
        item.setItemMeta(meta);
        resInv.setItem(36, item);

        item = new ItemStack(Material.STONE_SWORD);
        meta = item.getItemMeta();
        meta.setDisplayName("Power: " + faction.getPowerRounded());
        meta.setLore(Fallout.getInstance().getIdentifier());
        item.setItemMeta(meta);
        resInv.setItem(37, item);

        item = new ItemStack(Material.IRON_SWORD);
        meta = item.getItemMeta();
        meta.setDisplayName("Max Power: " + faction.getPowerMax());
        meta.setLore(Fallout.getInstance().getIdentifier());
        item.setItemMeta(meta);
        resInv.setItem(38, item);

        int i = 38;
        for (MPlayer mP : faction.getMPlayersWhereOnline(true)) {
            if (i < 54) {
                resInv.setItem(i, getFacHead(mP, "Online"));
                i++;
            }
        }
        for (MPlayer mP : faction.getMPlayersWhereOnline(false)) {
            if (i < 54) {
                resInv.setItem(i, getFacHead(mP, "Offline"));
                i++;
            }
        }
        return resInv;
    }

    private ItemStack getFacHead(MPlayer mP, String online) {
        int num = 0;
        switch (mP.getRole().getValue()) {
            case 45:
                num = 1;
                break;
            case 50:
                num = 2;
                break;
            case 60:
                num = 3;
                break;
            case 70:
                num = 4;
                break;
        }
        ItemStack item = new ItemStack(Material.SKULL_ITEM, num, (short) 3);
        SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
        skullMeta.setOwner(mP.getName());
        skullMeta.setDisplayName(mP.getName());
        skullMeta.setLore(Arrays.asList(Fallout.getInstance().getIdentifier().get(0), online));
        item.setItemMeta(skullMeta);
        return item;
    }

    public Inventory openMap(Player p) {
        UUID uuid = p.getUniqueId();
        p.getInventory().getItem(0).setDurability((short) 4);
        Inventory resInv = Bukkit.createInventory(null, 54, "MAP (" + p.getLocation().getChunk().getX() + "," + p.getLocation().getChunk().getZ()+ ") ");
        for (int i = 0; i < 9; i++) {
            resInv.setItem(i, menu.get(i));
        }

        List<Material> matMapKey = Arrays.asList(Material.STAINED_GLASS, Material.WOOL, Material.STAINED_CLAY, Material.STAINED_GLASS_PANE, Material.CARPET, Material.BANNER);
        int memberBlock = 0;
        int allyBlock = 0;
        int truceBlock = 0;
        int neutralBlock = 0;
        int enemyBlock = 0;
        HashMap<Faction, Material> facBlock = new HashMap<>();
        HashMap<Faction, Short> facColor = new HashMap<>();
        for (int z = 0; z < 5; z++) {
            for (int x = 0; x < 9; x++) {
                Faction thisFaction = BoardColl.get().getFactionAt(PS.valueOf(p.getLocation().getBlock().getLocation().add(-64 + 16 * x, 0, -32 + 16 * z)));
                Material blockType = Material.BARRIER;
                short color = 0;
                if (z == 2 && x == 4) {
                    blockType = Material.WOOL;
                    color = 3;
                } else if (facBlock.containsKey(thisFaction)) {
                    blockType = facBlock.get(thisFaction);
                    color = facColor.get(thisFaction);
                } else if (!thisFaction.isNone()){
                    int rel = thisFaction.getRelationTo(MPlayer.get(uuid).getFaction()).getValue();
                    switch (rel) {
                        case 40:
                            color = 10;
                            blockType = matMapKey.get(allyBlock++);
                            break;
                        case 30:
                            color = 2;
                            blockType = matMapKey.get(truceBlock++);
                            break;
                        case 20:
                            color = 0;
                            blockType = matMapKey.get(neutralBlock++);
                            break;
                        case 10:
                            color = 14;
                            blockType = matMapKey.get(enemyBlock++);
                            break;
                        default:
                            if (rel > 44) {
                                color = 5;
                                blockType = matMapKey.get(memberBlock++);
                            }
                    }
                    facBlock.put(thisFaction, blockType);
                    facColor.put(thisFaction, color);
                    if (blockType == Material.BANNER) {
                        color = (short) (15 - color);
                    }
                }
                ItemStack item = new ItemStack(blockType, 1, color);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(thisFaction.getColorTo(MPlayer.get(uuid).getFaction()) + thisFaction.getName());
                meta.setLore(Fallout.getInstance().getIdentifier());
                item.setItemMeta(meta);
                resInv.setItem(z * 9 + 9 + x, item);
            }
        }
        return resInv;
    }

    public Inventory openRadio(Player p) {
        UUID uuid = p.getUniqueId();
        p.getInventory().getItem(0).setDurability((short) 5);
        Inventory resInv = Bukkit.createInventory(null, 54, "RADIO");
        for (int i = 0; i < 9; i++) {
            resInv.setItem(i, menu.get(i));
        }

        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("Faction motd:");
        meta.setLore(Arrays.asList(Fallout.getInstance().getIdentifier().get(0), MPlayer.get(uuid).getFaction().getMotd()));
        item.setItemMeta(meta);
        resInv.setItem(9, item);
        Map<Integer, String> discNames = new HashMap<>();
        discNames.put(2256, "Sixty Minute Man" + ChatColor.DARK_RED + "*");
        discNames.put(2257, "Uranium Fever");
        discNames.put(2258, "Mighty, Mighty Man");
        discNames.put(2259, "Pistol Packin' Mama" + ChatColor.RED + "-");
        discNames.put(2260, "Crawl Out Through the Fallout");
        discNames.put(2261, "Atom Bomb Baby" + ChatColor.RED + "-");
        discNames.put(2262, "Personality");
        discNames.put(2263, "Worry Worry Worry");
        discNames.put(2264, "Uranium Rock");
        discNames.put(2265, "The Wanderer");
        discNames.put(2266, "One More Tomorrow");
        discNames.put(2267, "Rocket 69");
        int j = 38;
        int i = 2256;
        while (i < 2268) {
            if (j < 43 || j > 45) {
                ItemStack disc = new ItemStack(Material.getMaterial(i));
                meta = disc.getItemMeta();
                meta.setDisplayName("Play " + ChatColor.BLUE + discNames.get(i));
                meta.setLore(Fallout.getInstance().getIdentifier());
                disc.setItemMeta(meta);
                resInv.setItem(j, disc);
                i++;
            }
            j++;
        }
        return resInv;
    }

    public double getInvWeight(Player p) {
        double invWeight = 0;
        for (ItemStack stack : Fallout.getInstance().getGui().getInvs().get(p.getUniqueId())) {
            if (stack != null && stack.getMaxStackSize() > 0) {
                invWeight += stack.getAmount() / ((double) stack.getMaxStackSize());
            }
        }
        for (ItemStack stack : p.getInventory().getContents()) {
            if (stack != null && stack.getMaxStackSize() > 0) {
                invWeight++;
            }
        }
        for (ItemStack stack : p.getInventory().getArmorContents()) {
            if (stack != null && stack.getMaxStackSize() > 0) {
                invWeight++;
            }
        }
        return invWeight;
    }
}
