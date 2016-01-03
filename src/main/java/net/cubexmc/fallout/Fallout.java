/*
 * Copyright (c) 2016 CubeXMC. All Rights Reserved.
 * Created by PantherMan594.
 */

package net.cubexmc.fallout;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by David on 12/23.
 *
 * @author David
 */
public class Fallout extends JavaPlugin {
    public static Fallout instance;
    private Settings settings;
    private Stats stats;
    private Gui gui;
    private List<String> identifier;
    private ItemStack pip;

    public static Fallout getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        Bukkit.getServer().getPluginManager().registerEvents(new Listeners(), this);
        Bukkit.getScheduler().cancelTasks(this);
        instance = this;
        identifier = new ArrayList<>();
        identifier.add(ChatColor.BLACK + "*");
        gui = new Gui();
        stats = new Stats();
        settings = new Settings();
        pip = new ItemStack(Material.INK_SACK, 1, (short) 9);
        ItemMeta meta = pip.getItemMeta();
        meta.setDisplayName("Pip-Boy");
        meta.setLore(Arrays.asList(Fallout.getInstance().getIdentifier().get(0), "Right click to open your Pip-Boy!", "Shift right click to open your Pip-Boy inventory!", "Double click item in inventory", "  to transfer items to Pip-Boy!"));
        pip.setItemMeta(meta);
        for (Player p : Bukkit.getOnlinePlayers()) {
            settings.load(p.getUniqueId());
            stats.setXpLevel(p.getUniqueId(), p.getLevel());
            p.getInventory().setItem(0, pip);
        }
        new Recipes();
    }

    @Override
    public void onDisable() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            settings.save(p.getUniqueId());
        }
    }

    public boolean onCommand(CommandSender se, Command c, String lbl, String[] args) {
        if (se instanceof Player) {
            Player p = (Player) se;
            if (lbl.equalsIgnoreCase("pip")) {
                p.getInventory().setItem(0, getPip());
                p.openInventory(Fallout.getInstance().getGui().openStatus(p));
            }
        }
        if (lbl.equalsIgnoreCase("handbook")) {
            if (args.length == 0 && se instanceof Player) {
                ((Player) se).getInventory().addItem(getBook());
            } else if (args.length == 1 && Bukkit.getPlayer(args[0]) != null) {
                Bukkit.getPlayer(args[0]).getInventory().addItem(getBook());
            }
        } else if (lbl.equalsIgnoreCase("giveradiation") && !(se instanceof Player)) {
            UUID uuid = Bukkit.getPlayer(args[0]).getUniqueId();
            getStats().setRadLevel(uuid, getStats().getRadLevel(uuid) + Integer.valueOf(args[1]));
        }
        return false;
    }

    public ItemStack getBook() {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bm = (BookMeta) book.getItemMeta();
        bm.setTitle("Fallout Handbook");
        bm.setAuthor("Vault Boy");
        bm.addPage("Fallout Handbook\n-------------------\nPage 2: Section 1\n  Pip-Boy™\nPage 10: Section 2\n  Radiation\nPage 15: Section 3\n  Settlements\nPage 18: Section 4\n  Crafting");
        bm.addPage("I : Pip-Boy™\n-------------------Pip-Boy™ is the latest and greatest portable computer. With it users can access their L.I.E.S. stats, equip armor, set waypoint, view a map, listen to music, and much more! Shift while holding to turn on your flashlight.");
        bm.addPage("I.1 : Access\n-------------------Users can access their Pip-Boy™ by clicking the item in their inventory.");
        bm.addPage("I.2 : Status Menu\n-------------------The status menu has many useful tools within it. In the status menu, users can view their health, caps, weight, radiation levels, and potion effects. There are also slots for equiping and viewing armor.");
        bm.addPage("I.3 : L.I.E.S Menu\n-------------------L.I.E.S. are very important to the average survivor in the wasteland. Using the Pip-Boy™ users can level up and manage their L.I.E.S. They simply need to click the stat they would like to level up.");
        bm.addPage("I.4 : Inventory Menu\n-------------------In this menu, users can access their inventory and arrange items inside of it. Upgrade strength for more Pip-Boy™ storage without slowness. There is also a crafting option for the user's comfort.");
        bm.addPage("I.5: Data Menu\n-------------------Users can save coordinates to anywhere they want using the waypoints tool in this menu. User's may also view their settlement's data and information.");
        bm.addPage("I.6 : Map Menu\n-------------------The map menu displays a map of settlement's within your area. Although this advanced map can determine what land has been claimed, it cannot detect players in the area.");
        bm.addPage("I.7 : Radio\n-------------------In the Radio Menu, users can view their settlements messages and play the lastest hits in Fallout music! (Some may not be appropriate for all ages.)");
        bm.addPage("II : Radiation\n-------------------This is a post- apocalyptic world, in the aftermath of a nuclear war. Radiation from this nuclear fallout affects everything, forcing survivors above 3 xp levels to work to protect from radiation and enemy settlements.");
        bm.addPage("II.1 : Sources\n-------------------Radiation is most harsh in hot climates, so it is best to stay out of hot biomes and under- ground, out of direct access to the sun. Taking a dive in water can be deadly! Food from animals is also affected, so use caution.");
        bm.addPage("II.2a : Prevention\n-------------------A pill of Rad-X is most helpful in reducing radiation, doing so by 90%. A suit of power armor provides protection from 80% of the radiation, as well as increased resistance to damage and strength in attacks.");
        bm.addPage("II.2b : Prevention\n-------------------Increasing endurance can be a good long- term way to protect against radiation, providing protection and a resistance effect. Finally, a glass helmet is a cheap, way to get some (40%) radiation protection.");
        bm.addPage("II.3 : Treatment\n-------------------At this moment, there is only one way to treat radiation, by using rad aways. This removes all acquired radiation, but does not heal on its own.");
        bm.addPage("III : Settlements\n-------------------Create or join a settlement to work with your friends to make the most powerful team, while destroying the others!");
        bm.addPage("III.1 : Mechanics\n-------------------To claim land, a settlement must have enough power. Power can be gained with online members (max 10 each), lost with deaths. If a settlement has more land than power, it can be unclaimed. Otherwise, TNT and creepers!");
        bm.addPage("III.2 : Quick Commands\n-------------------\nSettlement help: /s\n(look into claiming and\nmoney)\nTeleport randomly:\n/rtp\nGo back to spawn:\n/spawn\nSpend vote points:\n/shop\nWarps: /warp");
        bm.addPage("IV.1 : Rad Away\n-------------------\nPPP \nPMP\nPNP\nP = paper\nM = milk bucket\nN = iron ingot");
        bm.addPage("IV.2 : Rad-X\n-------------------\n  R\nRSR\n  N\nR = redstone block\nS = sugar\nN = iron block");
        bm.addPage("IV.3 : Nuka-Cola\n-------------------\n  A\nSWs\n  G\nA = apple\nS = fermented spider\neye\nW = water bottle\ns = sugar\nG = golden carrot");
        bm.addPage("IV.3 : Energized Fusion\nCore\n-------------------\n  E\nDRG\n  R\nE = emerald block\nD = diamond block\nR = raw fusion core\nG = gold block\nR = redstone block");
        bm.addPage("IV.4 : Power Helmet\n-------------------\nGDG\nB  B\nG = gold block\nD = diamond helmet\nB = iron block");
        bm.addPage("IV.3 : Power Chestplate\n-------------------\nB  B\nGEG \nRDR \nB = iron block\nG = gold block\nE = energized fusion\ncore\nR = redstone block\nD = diamond chestplate");
        bm.addPage("IV.3 : Power Leggings\n-------------------\nBDB\nR  R\nG  G\nB = iron block\nD = diamond leggings\nR = redstone block\nG = gold block");
        bm.addPage("IV.3 : Power Boots\n-------------------\nBDB\nR  R\nB = iron block\nD = diamond boots\nR = redstone block");
        book.setItemMeta(bm);
        return book;
    }

    public Settings getSettings() {
        return settings;
    }

    public Stats getStats() {
        return stats;
    }

    public Gui getGui() {
        return gui;
    }

    public List<String> getIdentifier() {
        return identifier;
    }

    public ItemStack getPip() {
        return pip;
    }
}
