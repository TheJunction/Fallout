/*
 * Copyright (c) 2016 CubeXMC. All Rights Reserved.
 * Created by PantherMan594.
 */

package net.cubexmc.fallout;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

/**
 * Created by David on 12/23.
 *
 * @author David
 */
public class Listeners implements Listener {
    private List<UUID> saveLoc;
    private List<UUID> deleteLoc;
    private Map<UUID, Location> dieLoc;

    public Listeners() {
        saveLoc = new ArrayList<>();
        deleteLoc = new ArrayList<>();
        dieLoc = new HashMap<>();
    }

    @EventHandler
    public void join(PlayerJoinEvent e) {
        Fallout.getInstance().getSettings().load(e.getPlayer().getUniqueId());
        Fallout.getInstance().getStats().setXpLevel(e.getPlayer().getUniqueId(), e.getPlayer().getLevel());
        e.getPlayer().getInventory().setItem(0, Fallout.getInstance().getPip());
        e.getPlayer().setResourcePack("http://cubexmc.net/files/Fallout.zip");
    }

    @EventHandler
    public void quit(PlayerQuitEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        Fallout.getInstance().getSettings().save(uuid);
        saveLoc.remove(uuid);
        deleteLoc.remove(uuid);
        dieLoc.remove(uuid);
        Fallout.getInstance().getGui().getLocs().remove(uuid);
        Fallout.getInstance().getGui().getInvs().remove(uuid);
        Fallout.getInstance().getStats().setRadZone(uuid, false);
        Fallout.getInstance().getStats().setStrResist(uuid, false);
        Fallout.getInstance().getStats().setRadResist(uuid, false);
        Fallout.getInstance().getStats().setPowerSuit(uuid, false);
    }

    @EventHandler
    public void move(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        if (!e.isCancelled()) {
            if (e.getTo().getBlock().getType() == Material.PORTAL) {
                Bukkit.getServer().dispatchCommand(p, "rtp");
            }
            if (Fallout.getInstance().getStats().getPowerSuit(uuid)) {
                p.getWorld().playEffect(p.getLocation(), Effect.SMOKE, 5);
                p.getWorld().playEffect(p.getLocation(), Effect.MOBSPAWNER_FLAMES, 1);
            }
            p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, Fallout.getInstance().getStats().getSLevel(uuid) / 7, true));
            p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, Fallout.getInstance().getStats().getELevel(uuid) / 7, true));
            boolean seeSky = true;
            for (int y = 250; y > e.getTo().getBlockY(); y--) {
                Location checkLoc = e.getTo().getBlock().getLocation();
                checkLoc.setY(y);
                if (checkLoc.getBlock() != null && checkLoc.getBlock().getType() != Material.AIR) {
                    seeSky = false;
                    break;
                }
            }
            if ((seeSky || e.getTo().getY() >= 60) && p.getGameMode() == GameMode.SURVIVAL) {
                Fallout.getInstance().getStats().setRadZone(uuid, true);
            } else {
                Fallout.getInstance().getStats().setRadZone(uuid, false);
            }
            double invWeight = Fallout.getInstance().getGui().getInvWeight(p);
            if (!Fallout.getInstance().getStats().getStrResist(uuid) && invWeight - 6 >= Fallout.getInstance().getStats().getSLevel(uuid) * 5) {
                p.setWalkSpeed(0.125f);
            } else {
                p.setWalkSpeed(0.175f);
            }
        }
    }

    @EventHandler
    public void sprint(PlayerToggleSprintEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        double invWeight = Fallout.getInstance().getGui().getInvWeight(p);
        if (!Fallout.getInstance().getStats().getStrResist(uuid) && invWeight >= Fallout.getInstance().getStats().getSLevel(uuid) * 5) {
            p.setFoodLevel(6);
        } else {
            p.setFoodLevel(19);
        }
    }

    @EventHandler
    public void sneak(PlayerToggleSneakEvent e) {
        Player p = e.getPlayer();
        if (!p.isSneaking() && p.getItemInHand().getType() != Material.AIR && p.getItemInHand().getItemMeta().getDisplayName() != null && p.getItemInHand().getItemMeta().getDisplayName().equals("Pip-Boy")) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 999999, 0, true));
        } else {
            p.removePotionEffect(PotionEffectType.NIGHT_VISION);
        }
    }

    @EventHandler
    public void inv(InventoryClickEvent e) {
        if (e.getCurrentItem() != null) {
            if (e.getCurrentItem().getItemMeta() != null && e.getCurrentItem().getItemMeta().getLore() != null && !e.getCurrentItem().getItemMeta().getLore().isEmpty() && e.getCurrentItem().getItemMeta().getLore().contains(Fallout.getInstance().getIdentifier().get(0))) {
                e.setCancelled(true);
                Player p = (Player) e.getWhoClicked();
                if (saveLoc.contains(p.getUniqueId()) && e.getInventory().getType() == InventoryType.ANVIL && e.getCurrentItem().getType() == Material.NAME_TAG) {
                    HashMap<String, Location> locs = Fallout.getInstance().getGui().getLocs().get(p.getUniqueId());
                    locs.put(e.getCurrentItem().getItemMeta().getDisplayName(), p.getLocation());
                    Fallout.getInstance().getGui().setLoc(p.getUniqueId(), locs);
                    e.getInventory().clear();
                    p.giveExpLevels(-1);
                    ((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutCloseWindow(1));
                    saveLoc.remove(p.getUniqueId());
                }
                if (e.getCurrentItem().getItemMeta().getDisplayName() != null) {
                    ItemStack cursor = p.getItemOnCursor();
                    p.setItemOnCursor(new ItemStack(Material.AIR));
                    switch (e.getCurrentItem().getItemMeta().getDisplayName()) {
                        case "Pip-Boy":
                            if (e.getClick() == ClickType.DOUBLE_CLICK) {
                                boolean openInv = false;
                                if (e.getInventory().getName() != null && e.getInventory().getName().equals("INV")) {
                                    p.openInventory(Fallout.getInstance().getGui().openStatus(p));
                                    openInv = true;
                                }
                                for (int i = 9; i < 36; i++) {
                                    if (p.getInventory().getItem(i) != null) {
                                        addItem(p.getUniqueId(), p.getInventory().getItem(i));
                                        p.getInventory().remove(p.getInventory().getItem(i));
                                    }
                                }
                                if (openInv) {
                                    p.openInventory(Fallout.getInstance().getGui().openInv(p));
                                }
                            }
                            break;
                        case "STATUS":
                            p.openInventory(Fallout.getInstance().getGui().openStatus(p));
                            break;
                        case "LIES":
                            p.openInventory(Fallout.getInstance().getGui().openLies(p));
                            break;
                        case "INV":
                            p.openInventory(Fallout.getInstance().getGui().openInv(p));
                            break;
                        case "DATA":
                            p.openInventory(Fallout.getInstance().getGui().openData(p));
                            break;
                        case "MAP":
                            p.openInventory(Fallout.getInstance().getGui().openMap(p));
                            break;
                        case "RADIO":
                            p.openInventory(Fallout.getInstance().getGui().openRadio(p));
                            break;
                        case "Save Location":
                            EntityPlayer entityPlayer = ((CraftPlayer) p).getHandle();
                            FakeAnvil fakeAnvil = new FakeAnvil(entityPlayer);
                            int containerId = entityPlayer.nextContainerCounter();

                            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutOpenWindow(containerId, "minecraft:anvil", new ChatMessage("Repairing"), 0));

                            entityPlayer.activeContainer = fakeAnvil;
                            entityPlayer.activeContainer.windowId = containerId;
                            entityPlayer.activeContainer.addSlotListener(entityPlayer);
                            entityPlayer.activeContainer = fakeAnvil;
                            entityPlayer.activeContainer.windowId = containerId;

                            Inventory save = fakeAnvil.getBukkitView().getTopInventory();
                            ItemStack item = new ItemStack(Material.NAME_TAG);
                            ItemMeta meta = item.getItemMeta();
                            meta.setLore(Fallout.getInstance().getIdentifier());
                            item.setItemMeta(meta);
                            save.setItem(0, item);
                            saveLoc.add(p.getUniqueId());
                            p.giveExpLevels(1);
                            break;
                        case "Delete Location":
                            deleteLoc.add(p.getUniqueId());
                            p.sendMessage(ChatColor.GREEN + "Pip-Boy: " + ChatColor.GRAY + "Click the location you want to delete.");
                            break;
                        case "Workbench":
                            p.openWorkbench(null, true);
                            break;
                        case "Fallout Guide":
                            p.getInventory().addItem(Fallout.getInstance().getBook());
                            break;
                        default:
                            if (e.getCurrentItem().getItemMeta().getDisplayName().startsWith("Level ")) {
                                switch (e.getSlot()) {
                                    case 28:
                                        Fallout.getInstance().getStats().claimLevels(p, "luck");
                                        break;
                                    case 30:
                                        Fallout.getInstance().getStats().claimLevels(p, "intelligence");
                                        break;
                                    case 32:
                                        Fallout.getInstance().getStats().claimLevels(p, "endurance");
                                        break;
                                    case 34:
                                        Fallout.getInstance().getStats().claimLevels(p, "strength");
                                        break;
                                }
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().startsWith("Play " + ChatColor.BLUE)) {
                                ((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutWorldEvent(1005, new BlockPosition(p.getLocation().getBlockX(), p.getLocation().getBlockY(), p.getLocation().getBlockZ()), e.getCurrentItem().getTypeId(), false));
                            } else if (deleteLoc.contains(p.getUniqueId()) && e.getInventory().getName().equals("DATA") && e.getCurrentItem().getItemMeta().getLore() != null && e.getCurrentItem().getItemMeta().getLore().get(1).contains("World: ")) {
                                HashMap<String, Location> locs = Fallout.getInstance().getGui().getLocs().get(p.getUniqueId());
                                locs.remove(e.getCurrentItem().getItemMeta().getDisplayName());
                                Fallout.getInstance().getGui().setLoc(p.getUniqueId(), locs);
                                p.openInventory(Fallout.getInstance().getGui().openData(p));
                            }
                    }
                    if (cursor != null) {
                        p.setItemOnCursor(cursor);
                    }
                }
            }
        }
    }

    @EventHandler
    public void close(final InventoryCloseEvent e) {
        final Player p = (Player) e.getPlayer();
        UUID uuid = p.getUniqueId();
        deleteLoc.remove(uuid);
        if (saveLoc.contains(uuid) && e.getInventory().getType() == InventoryType.ANVIL) {
            if (e.getInventory().getContents() != null) {
                e.getInventory().clear();
            }
            p.giveExpLevels(-1);
            saveLoc.remove(uuid);
        } else if (e.getInventory().getName().equals("STATUS")) {
            p.getInventory().setHelmet(e.getInventory().getItem(22));
            p.getInventory().setChestplate(e.getInventory().getItem(31));
            p.getInventory().setLeggings(e.getInventory().getItem(40));
            p.getInventory().setBoots(e.getInventory().getItem(49));
        } else if (e.getInventory().getName().equals("INV")) {
            Fallout.getInstance().getGui().setInv(uuid, e.getInventory().getContents());
        }
        Bukkit.getScheduler().runTaskLater(Fallout.getInstance(), new Runnable() {
            @Override
            public void run() {
                int dura = p.getInventory().getItem(0).getDurability();
                if (dura != 9) {
                    List<String> names = new ArrayList<>();
                    names.add("STATUS");
                    names.add("LIES");
                    names.add("INV");
                    names.add("DATA");
                    names.add("MAP");
                    names.add("RADIO");
                    if (p.getOpenInventory().getTopInventory().getName() == null || !p.getOpenInventory().getTopInventory().getName().startsWith(names.get(dura))) {
                        p.getInventory().getItem(0).setDurability((short) 9);
                    }
                }
            }
        }, 5);
    }

    @EventHandler
    public void interact(PlayerInteractEvent e) {
        if ((e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) && e.getItem() != null && e.getItem().getItemMeta().getDisplayName() != null && e.getItem().getItemMeta().getDisplayName().equals("Pip-Boy") && e.getItem().getItemMeta().getLore() != null && e.getItem().getItemMeta().getLore().get(0).equals(Fallout.getInstance().getIdentifier().get(0))) {
            if (e.getPlayer().isSneaking()) {
                e.getPlayer().openInventory(Fallout.getInstance().getGui().openInv(e.getPlayer()));
            } else {
                e.getPlayer().openInventory(Fallout.getInstance().getGui().openStatus(e.getPlayer()));
            }
        }
    }

    @EventHandler
    public void craft(PrepareItemCraftEvent e) {
        if (e.getInventory().getResult() != null && e.getInventory().getResult().getItemMeta().getDisplayName() != null && e.getInventory().getResult().getItemMeta().getDisplayName().contains(ChatColor.GOLD + "Power ")) {
            ItemStack item = e.getInventory().getResult();
            item.addUnsafeEnchantment(org.bukkit.enchantments.Enchantment.DURABILITY, 10);
            if (e.getInventory().getResult().getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Power Chestplate")) {
                if (e.getInventory().getMatrix()[4].getItemMeta().getDisplayName() != null && e.getInventory().getMatrix()[4].getItemMeta().getDisplayName().equals(ChatColor.DARK_GREEN + "Energized Fusion Core")) {
                    item.addUnsafeEnchantment(org.bukkit.enchantments.Enchantment.PROTECTION_ENVIRONMENTAL, 8);
                } else {
                    e.getInventory().setResult(new ItemStack(Material.AIR));
                }
            }
        } else {
            HashMap<Material, Integer> ingrLvl = new HashMap<>();
            ingrLvl.put(Material.COBBLESTONE, 2);
            ingrLvl.put(Material.IRON_INGOT, 3);
            ingrLvl.put(Material.DIAMOND, 7);
            if (ingrLvl.containsKey(e.getInventory().getResult().getType())) {
                if (Fallout.getInstance().getStats().getILevel(e.getViewers().get(0).getUniqueId()) < ingrLvl.get(e.getInventory().getResult().getType())) {
                    e.getViewers().get(0).sendMessage(ChatColor.RED + "You are not intelligent enough to craft this item.");
                    e.getInventory().setResult(new ItemStack(Material.AIR));
                }
            }
            HashMap<String, Integer> craftLvl = new HashMap<>();
            craftLvl.put(ChatColor.RED + "Rad Away", 4);
            craftLvl.put(ChatColor.BLUE + "Spawn Trader", 5);
            craftLvl.put(ChatColor.RED + "Rad-X", 6);
            craftLvl.put(ChatColor.DARK_GREEN + "Energized Fusion Core", 8);
            craftLvl.put(ChatColor.GOLD + "Power Helmet", 9);
            craftLvl.put(ChatColor.GOLD + "Power Chestplate", 9);
            craftLvl.put(ChatColor.GOLD + "Power Leggings", 9);
            craftLvl.put(ChatColor.GOLD + "Power Boots", 9);
            if (e.getInventory().getResult().getItemMeta() != null && e.getInventory().getResult().getItemMeta().getDisplayName() != null && craftLvl.containsKey(e.getInventory().getResult().getItemMeta().getDisplayName())) {
                if (Fallout.getInstance().getStats().getILevel(e.getViewers().get(0).getUniqueId()) < craftLvl.get(e.getInventory().getResult().getItemMeta().getDisplayName())) {
                    e.getViewers().get(0).sendMessage(ChatColor.RED + "You are not intelligent enough to craft this item.");
                    e.getInventory().setResult(new ItemStack(Material.AIR));
                }
            }
        }
    }

    @EventHandler
    public void attack(EntityDamageByEntityEvent e) {
        String bloatFlyBase = ChatColor.DARK_GREEN + "Bloat" + ChatColor.BLACK + "FLY";
        List<String> cancels = Arrays.asList(bloatFlyBase, ChatColor.BLACK + "Black " + bloatFlyBase);
        if (e.getEntity() instanceof Player && e.getDamager().getCustomName() != null && cancels.contains(e.getDamager().getCustomName())) {
            e.setDamage(0);
        }
    }

    @EventHandler
    public void damage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player && ((Player) e.getEntity()).getHealth() - e.getDamage() <= 0) {
            dieLoc.put(e.getEntity().getUniqueId(), e.getEntity().getLocation());
        } else if (e.getEntity() instanceof Player){
            ((Player) e.getEntity()).setFoodLevel(19);
        }
    }

    @EventHandler
    public void die(PlayerDeathEvent e) {
        e.setKeepLevel(true);
        final Player p = e.getEntity();
        final Location loc = p.getLocation();
        final UUID uuid = p.getUniqueId();
        if (Fallout.getInstance().getStats().getRadLevel(uuid) >= 19.5) {
            e.setDeathMessage(e.getEntity().getName() + " was killed by " + ChatColor.GREEN + "radiation" + ChatColor.WHITE + ".");
        }
        Fallout.getInstance().getStats().setRadLevel(uuid, 0);
        Fallout.getInstance().getStats().setRadZone(uuid, false);
        e.getEntity().setMaxHealth(20);
        if (!dieLoc.containsKey(uuid)) {
            dieLoc.put(e.getEntity().getUniqueId(), e.getEntity().getLocation());
        }
        double rand = Math.random() * 50;
        if (rand > Fallout.getInstance().getStats().getLLevel(e.getEntity().getUniqueId()) && dieLoc.containsKey(e.getEntity().getUniqueId())) {
            e.setKeepInventory(false);
            e.getDrops().remove(0);
            if (rand > Fallout.getInstance().getStats().getLLevel(e.getEntity().getUniqueId()) / 2.0) {
                rand = rand * 4 / 50.0;
                if (rand < 1) {
                    Fallout.getInstance().getStats().setLLevel(e.getEntity().getUniqueId(), Fallout.getInstance().getStats().getLLevel(e.getEntity().getUniqueId()) - 1);
                } else if (rand < 2) {
                    Fallout.getInstance().getStats().setILevel(e.getEntity().getUniqueId(), Fallout.getInstance().getStats().getILevel(e.getEntity().getUniqueId()) - 1);
                } else if (rand < 3) {
                    Fallout.getInstance().getStats().setELevel(e.getEntity().getUniqueId(), Fallout.getInstance().getStats().getELevel(e.getEntity().getUniqueId()) - 1);
                } else {
                    Fallout.getInstance().getStats().setSLevel(e.getEntity().getUniqueId(), Fallout.getInstance().getStats().getSLevel(e.getEntity().getUniqueId()) - 1);
                }
            }
            Bukkit.getScheduler().runTaskLater(Fallout.getInstance(), new Runnable() {
                @Override
                public void run() {
                    for (ItemStack item : Fallout.getInstance().getGui().getInvs().get(uuid)) {
                        if (item != null && !(item.getItemMeta().getLore() != null && item.getItemMeta().getLore().contains(Fallout.getInstance().getIdentifier().get(0)))) {
                            loc.getWorld().dropItemNaturally(loc, item);
                        }
                    }
                    Fallout.getInstance().getGui().setInv(uuid, new ItemStack[54]);
                }
            }, 20);
        } else {
            e.setKeepInventory(true);
        }
        Bukkit.getScheduler().runTaskLater(Fallout.getInstance(), new Runnable() {
            @Override
            public void run() {
                ((CraftPlayer) p).getHandle().playerConnection.a(new PacketPlayInClientCommand(PacketPlayInClientCommand.EnumClientCommand.PERFORM_RESPAWN));
            }
        }, 10);
    }

    @EventHandler
    public void respawn(PlayerRespawnEvent e) {
        e.getPlayer().getInventory().setItem(0, Fallout.getInstance().getPip());
    }

    @EventHandler
    public void hunger(FoodLevelChangeEvent e) {
        UUID uuid = e.getEntity().getUniqueId();
        boolean cancel = false;
        int diff = e.getFoodLevel() - ((Player) e.getEntity()).getFoodLevel();
        if (diff > 0) {
            Double newHealth = e.getEntity().getHealth() + diff / 2.0;
            if (newHealth <= e.getEntity().getMaxHealth()) {
                e.getEntity().setHealth(newHealth);
            } else {
                e.getEntity().setHealth(e.getEntity().getMaxHealth());
            }
            cancel = true;
        }
        if (e.getEntity().hasPotionEffect(PotionEffectType.HUNGER)) {
            Fallout.getInstance().getStats().setRadLevel(uuid, Fallout.getInstance().getStats().getRadLevel(uuid) + 1.5);
            e.getEntity().removePotionEffect(PotionEffectType.HUNGER);
        }
        int newLvls = (int) (20 - Fallout.getInstance().getStats().getRadLevel(uuid));
        if (newLvls == 0) {
            EntityDamageEvent ede = new EntityDamageEvent(e.getEntity(), EntityDamageEvent.DamageCause.STARVATION, Short.MAX_VALUE);
            Bukkit.getPluginManager().callEvent(ede);
            e.getEntity().damage(Short.MAX_VALUE);
            if (e.getEntity().getHealth() > 0) {
                e.getEntity().setHealth(0);
            }
        } else {
            if (newLvls <= 2 && Fallout.getInstance().getGui().getInvs().containsKey(uuid)) {
                List<ItemStack> inv = new ArrayList(Arrays.asList(Fallout.getInstance().getGui().getInvs().get(uuid)));
                if (inv != null) {
                    for (int i = 9; i < inv.size(); i++) {
                        ItemStack stack = inv.get(i);
                        if (stack != null && stack.getType() == Material.POTION && stack.getItemMeta().getDisplayName() != null && stack.getItemMeta().getDisplayName().equals(ChatColor.RED + "Rad Away")) {
                            inv.set(i, new ItemStack(Material.AIR));
                            Fallout.getInstance().getStats().setRadLevel(uuid, 0);
                            newLvls = 20;
                            Fallout.getInstance().getGui().setInv(uuid, inv.toArray(new ItemStack[54]));
                            e.getEntity().sendMessage(ChatColor.GREEN + "Pip-Boy: " + ChatColor.GRAY + "You were about to die to radiation, so I consumed a Rad Away for you. Be more careful next time!");
                            break;
                        }
                    }
                }
            }
            e.setFoodLevel(19);
            if (e.getEntity().getHealth() > newLvls) {
                e.getEntity().setHealth(newLvls);
            }
            if (e.getEntity().getMaxHealth() != newLvls) {
                e.getEntity().setMaxHealth(newLvls);
                e.getEntity().damage(0);
            }
        }
        if (cancel) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void eat(PlayerItemConsumeEvent e) {
        final UUID uuid = e.getPlayer().getUniqueId();
        if (e.getItem().getType() == Material.MILK_BUCKET) {
            Fallout.getInstance().getStats().setRadResist(uuid, false);
            Fallout.getInstance().getStats().setStrResist(uuid, false);
        }
        if (e.getItem().getType().name().toLowerCase().startsWith("raw ") || (e.getItem().getItemMeta() != null && e.getItem().getItemMeta().getDisplayName() != null && e.getItem().getItemMeta().getDisplayName().toLowerCase().startsWith("raw "))) {
            Fallout.getInstance().getStats().setRadLevel(uuid, Fallout.getInstance().getStats().getRadLevel(uuid) + 1.5);
        }
        if (e.getItem().getItemMeta() != null && e.getItem().getItemMeta().getDisplayName() != null && e.getItem().getItemMeta().getDisplayName().length() > 6 && e.getItem().getItemMeta().getLore() != null) {
            boolean custom = true;
            switch (e.getItem().getItemMeta().getDisplayName().substring(1)) {
                case "cRad Away":
                    Fallout.getInstance().getStats().setRadLevel(uuid, 0);
                    break;
                case "cRad-X":
                    Fallout.getInstance().getStats().setRadResist(uuid, true);
                    Bukkit.getScheduler().runTaskLaterAsynchronously(Fallout.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            Fallout.getInstance().getStats().setRadResist(uuid, false);
                        }
                    }, 6000);
                    break;
                case "aNuka-Cola":
                    e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 3600, 3, true));
                    e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 3600, 3, true));
                    e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 3600, 1, true));
                    Fallout.getInstance().getStats().setStrResist(uuid, true);
                    Bukkit.getScheduler().runTaskLaterAsynchronously(Fallout.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            Fallout.getInstance().getStats().setStrResist(uuid, false);
                        }
                    }, 3600);
                    break;
                default:
                    custom = false;
            }
            if (custom) {
                e.setCancelled(true);
                e.getPlayer().setItemInHand(null);
            }
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent e) {
        if (e.getEntity() instanceof PigZombie) {
            PigZombie pigzombie = (PigZombie) e.getEntity();
            pigzombie.setAngry(true);
        }
    }

    @EventHandler
    public void achievement(PlayerAchievementAwardedEvent e) {
        if (!e.isCancelled()) {
            int lvl = e.getPlayer().getLevel();
            if (lvl <= 16) {
                e.getPlayer().giveExp(lvl + 4);
            } else if (lvl <= 31) {
                e.getPlayer().giveExp((int) Math.round(2.5 * lvl - 19));
            } else {
                e.getPlayer().giveExp((int) Math.round(4.5 * lvl - 79));
            }
        }
    }

    @EventHandler
    public void drop(PlayerDropItemEvent e) {
        if (e.getItemDrop().getItemStack().getItemMeta().getLore() != null && !e.getItemDrop().getItemStack().getItemMeta().getLore().isEmpty() && e.getItemDrop().getItemStack().getItemMeta().getLore().contains(Fallout.getInstance().getIdentifier().get(0))) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void xp(PlayerExpChangeEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        e.setAmount(e.getAmount() / 3);
        int diff = e.getPlayer().getLevel() - Fallout.getInstance().getStats().getXpLevel(uuid);
        if (diff != 0 && e.getPlayer().getGameMode() == GameMode.SURVIVAL) {
            Fallout.getInstance().getStats().setXpLevel(uuid, e.getPlayer().getLevel());
            int newClaims = Fallout.getInstance().getStats().getClaimLevel(uuid) + diff;
            Fallout.getInstance().getStats().setClaimLevel(uuid, newClaims);
        }
    }

    @EventHandler
    public void pack(final PlayerResourcePackStatusEvent e) {
        if (e.getStatus() == PlayerResourcePackStatusEvent.Status.DECLINED) {
            Bukkit.getScheduler().runTaskLater(Fallout.getInstance(), new Runnable() {
                @Override
                public void run() {
                    e.getPlayer().kickPlayer(ChatColor.RED + "You must accept the resource pack to play!\nTry setting server resource packs to \"Enabled\"\nby editing the server on the multiplayer menu.");
                }
            }, 20);
        }
    }

    public boolean addItem(UUID uuid, ItemStack stack) {
        List<ItemStack> inv = Arrays.asList(Fallout.getInstance().getGui().getInvs().get(uuid));
        if (inv != null) {
            for (int j = 9; j < 54; j++) {
                ItemStack item = inv.get(j);
                if (item == null || item.getType() == Material.AIR) {
                    inv.set(j, stack);
                    ItemStack[] invArray = inv.toArray(new ItemStack[inv.size()]);
                    Fallout.getInstance().getGui().setInv(uuid, invArray);
                    return true;
                } else if (item != null && item.isSimilar(stack)) {
                    int amt = item.getAmount() + stack.getAmount();
                    int max = item.getMaxStackSize();
                    if (amt > max) {
                        item.setAmount(max);
                        stack.setAmount(amt - max);
                    } else {
                        item.setAmount(amt);
                        ItemStack[] invArray = inv.toArray(new ItemStack[inv.size()]);
                        Fallout.getInstance().getGui().setInv(uuid, invArray);
                        return true;
                    }
                }
            }
        } else {
            inv = new ArrayList<>();
            inv.set(0, stack);
            ItemStack[] invArray = inv.toArray(new ItemStack[inv.size()]);
            Fallout.getInstance().getGui().setInv(uuid, invArray);
            return true;
        }
        Location loc = Bukkit.getPlayer(uuid).getLocation();
        loc.getWorld().dropItem(loc, stack);
        Bukkit.getPlayer(uuid).sendMessage(ChatColor.GREEN + "Pip-Boy: " + ChatColor.GRAY + "Your Pip-Boy inventory is full!");
        return false;
    }

    public final class FakeAnvil extends ContainerAnvil {

        public FakeAnvil(EntityHuman entityHuman) {
            super(entityHuman.inventory, entityHuman.world, new BlockPosition(0,0,0), entityHuman);
        }


        @Override
        public boolean a(EntityHuman entityHuman) {
            return true;
        }
    }
}
