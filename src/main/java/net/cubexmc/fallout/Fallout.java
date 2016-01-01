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
        }
        return false;
    }

    public ItemStack getBook() {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bm = (BookMeta) book.getItemMeta();
        bm.setTitle("Fallout Handbook");
        bm.setAuthor("Vault Boy");
        bm.addPage("Fallout Handbook\n-------------------\nPage 2: Section 1\nPip-Boy™\nPage 10: Section 2\nRadiation\nPage 15: Section 3\nSettlements\nPage 18: Section 4\nCrafting");
        bm.addPage("I : Pip-Boy™\n-------------------\nPip-Boy™ is the latest\nand greatest portable\ncomputer. With it users\ncan access their\nL.I.E.S. stats, equip\narmor, set waypoint,\nview a map, listen to\nmusic, and much more!");
        bm.addPage("I.1 : Access\n-------------------\nUsers can access\ntheir Pip-Boy™ by\nclicking the item in their\ninventory.");
        bm.addPage("I.2 : Status Menu\n-------------------\nThe status menu has\nmany useful tools\nwithin it. In the status\nmenu, users can view\ntheir health, caps,\nweight, radiation\nlevels, and potion\neffects.\nThere are also slots\nfor equiping and\nviewing armor.");
        bm.addPage("I.3 : L.I.E.S Menu\n-------------------\nL.I.E.S. are very\nimportant to the\naverage survivor in\nthe wasteland. Using\nthe Pip-Boy™ users\ncan level up and\nmanage their L.I.E.S.\nThey simply need to\nclick the stat they\nwould like to level up.");
        bm.addPage("I.4 : Inventory Menu\n-------------------\nIn this menu, users\ncan access their\ninventory and\narrange items inside\nof it. Upgrade\nstrength for more\nPip-Boy™ storage\nwithout slowness.\nThere is also a\ncrafting option for\nthe user's comfort.");
        bm.addPage("I.5: Data Menu\n-------------------\nUsers can save\ncoordinates to\nanywhere they want\nusing the waypoints\ntool in this menu.\nUser's may also view\ntheir settlement's\ndata and information.");
        bm.addPage("I.6 : Map Menu\n-------------------\nThe map menu displays\na map of settlement's\nwithin your area.\nAlthough this\nadvanced map can\ndetermine what land\nhas been claimed, it\ncannot detect players\nin the area.");
        bm.addPage("I.7 : Radio\n-------------------\nIn the Radio Menu,\nusers can view their\nsettlements messages\nand play the lastest\nhits in music; including\nCat, Strad, and even\nthe classic; Blocks!");
        bm.addPage("II : Radiation\n-------------------\nThis is a post-\napocalyptic world, in\nthe aftermath of a\nnuclear war. Radiation\nfrom this nuclear\nfallout affects\neverything, forcing\nsurvivors to work to\nprotect from radiation\nand enemy\nsettlements.");
        bm.addPage("II.1 : Sources\n-------------------\nRadiation is most harsh\nin hot climates, so it is\nbest to stay out of\nhot biomes and under-\nground, out of direct\naccess to the sun.\nTaking a dive in water\ncan be deadly! Food\nfrom animals is also\naffected, so use\ncaution");
        bm.addPage("II.2a : Prevention\n-------------------\nA pill of Rad-X is most\nhelpful in reducing\nradiation, doing so by\n90%. A suit of power\narmor provides\nprotection from 80% of\nthe radiation, as well\nas increased\nresistance to damage\nand strength in\nattacks.");
        bm.addPage("II.2b : Prevention\n-------------------\nIncreasing endurance\ncan be a good\nlong-term way to\nprotect against\nradiation, providing\nprotection and a\nresistance effect.\nFinally, a glass helmet\nis a cheap, way to get\nsome (40%) radiation\nprotection.");
        bm.addPage("II.3 : Treatment\n-------------------\nAt this moment, there\nis only one way to\ntreat radiation, by\nusing rad aways. This\nremoves all acquired\nradiation, but does not\nheal on its own.");
        bm.addPage("III : Settlements\n-------------------\nCreate or join a\nsettlement to work with\nyour friends to make\nthe most powerful\nteam, while destroying\nthe others!");
        bm.addPage("III.1 : Mechanics\n-------------------\nTo claim land, a\nsettlement must have\nenough power. Power\ncan be gained with\nonline members (max 10\neach), lost with\ndeaths. If a settlement\nhas more land than\npower, it can be\nunclaimed. Otherwise,\nTNT and creepers!");
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
