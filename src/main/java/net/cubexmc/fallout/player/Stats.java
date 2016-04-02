/*
 * Copyright (c) 2016 CubeXMC. All Rights Reserved.
 * Created by PantherMan594.
 */

package net.cubexmc.fallout.player;

import net.cubexmc.fallout.Fallout;
import net.cubexmc.fallout.player.ActionBar;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

/**
 * Created by David on 12/23.
 *
 * @author David
 */
public class Stats {
    private Map<UUID, Integer> claimLevel;
    private List<UUID> radZone;
    private List<UUID> radResist;
    private Map<UUID, Double> rLevel;
    private Map<UUID, Integer> lLevel;
    private Map<UUID, Integer> iLevel;
    private Map<UUID, Integer> eLevel;
    private Map<UUID, Integer> sLevel;
    private List<UUID> strResist;
    private Map<UUID, Integer> xpLevels;
    private List<UUID> powerSuit;

    public Stats() {
        claimLevel = new HashMap<>();
        radZone = new ArrayList<>();
        radResist = new ArrayList<>();
        rLevel = new HashMap<>();
        lLevel = new HashMap<>();
        iLevel = new HashMap<>();
        eLevel = new HashMap<>();
        sLevel = new HashMap<>();
        strResist = new ArrayList<>();
        xpLevels = new HashMap<>();
        powerSuit = new ArrayList<>();
        Bukkit.getScheduler().runTaskTimer(Fallout.getInstance(), () -> {
            if (!Bukkit.getOnlinePlayers().isEmpty()) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.setFoodLevel(19);
                    final UUID uuid = p.getUniqueId();
                    String bar = ChatColor.GREEN + "HP ";
                    for (int i = 0; i < 20; i++) {
                        if (i > p.getHealth()) {
                            if (i <= p.getMaxHealth()) {
                                bar += ChatColor.DARK_GREEN;
                            } else {
                                bar += ChatColor.RED;
                            }
                        }
                        bar += "⬛";
                    }
                    new ActionBar(p, bar);
                    if (p.getInventory().getHelmet() != null && p.getInventory().getHelmet().getItemMeta().getDisplayName() != null && p.getInventory().getHelmet().getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Power Helmet") && p.getInventory().getChestplate() != null && p.getInventory().getChestplate().getItemMeta().getDisplayName() != null && p.getInventory().getChestplate().getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Power Chestplate") && p.getInventory().getLeggings() != null && p.getInventory().getLeggings().getItemMeta().getDisplayName() != null && p.getInventory().getLeggings().getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Power Leggings") && p.getInventory().getBoots() != null && p.getInventory().getBoots().getItemMeta().getDisplayName() != null && p.getInventory().getBoots().getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Power Boots")) {
                        if (!getPowerSuit(uuid)) {
                            setPowerSuit(uuid, true);
                            p.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                            p.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                            p.removePotionEffect(PotionEffectType.SPEED);
                            p.removePotionEffect(PotionEffectType.JUMP);
                            p.removePotionEffect(PotionEffectType.SLOW_DIGGING);
                            p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 4, true));
                            p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 4, true));
                            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2, true));
                            p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 1, true));
                            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, 1, true));
                        }
                    } else if (getPowerSuit(uuid)) {
                        setPowerSuit(uuid, false);
                        p.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                        p.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                        p.removePotionEffect(PotionEffectType.SPEED);
                        p.removePotionEffect(PotionEffectType.JUMP);
                        p.removePotionEffect(PotionEffectType.SLOW_DIGGING);
                    }
                    if (getRadZone(uuid)) {
                        double addRad;
                        String biomeName = p.getLocation().getBlock().getBiome().name();
                        if (biomeName.contains("DESERT") || biomeName.contains("BEACHES") || biomeName.contains("STONE") || biomeName.contains("MESA") || biomeName.contains("SWAMPLAND") || biomeName.contains("SAVANNA")) {
                            addRad = Math.pow(16 - getELevel(uuid), 2) / 1200.0;
                        } else if (biomeName.contains("COLD") || biomeName.contains("TAIGA") || biomeName.contains("ICE") || biomeName.contains("MUSHROOM")) {
                            addRad = 0;
                        } else {
                            addRad = Math.pow(16 - getELevel(uuid), 2) / 3200.0;
                        }

                        if (getRadResist(uuid)) {
                            addRad = addRad / 10.0;
                        } else if (powerSuit.contains(uuid)) {
                            addRad = addRad / 5.0;
                        } else if (p.getInventory().getHelmet() != null && p.getInventory().getHelmet().getType() == Material.GLASS) {
                            addRad = addRad * 3.0 / 5.0;
                        }
                        if (p.getWorld().getTime() >= 12000 && p.getWorld().getTime() <= 24000) {
                            addRad = addRad * 2.0 / 3.0;
                        }
                        if (addRad > 0) {
                            setRadLevel(uuid, getRadLevel(uuid) + addRad);
                        }
                    }

                    if (p.getLocation().getBlock().getType() == Material.WATER || p.getLocation().getBlock().getType() == Material.STATIONARY_WATER) {
                        double addRad = Math.pow(16 - getELevel(uuid), 2) / 100.0;
                        if (getRadResist(p.getUniqueId())) {
                            addRad = addRad / 20.0;
                        } else if (getPowerSuit(uuid)) {
                            addRad = addRad / 10.0;
                        }
                        if (addRad > 0) {
                            setRadLevel(p.getUniqueId(), getRadLevel(p.getUniqueId()) + addRad);
                        }
                    }
                }
            }
        }, 0, 20);
        Bukkit.getScheduler().runTaskTimer(Fallout.getInstance(), () -> Bukkit.getOnlinePlayers().stream().filter(p -> getPowerSuit(p.getUniqueId())).forEach(p -> {
            ItemStack chest = p.getInventory().getChestplate();
            chest.setDurability((short) (chest.getDurability() + 1));
            if (chest.getDurability() >= chest.getType().getMaxDurability()) {
                chest.setType(Material.AIR);
            }
        }), 0, 100);
    }

    public void setClaimLevel(UUID uuid, int level) {
        claimLevel.put(uuid, level);
    }

    public int getClaimLevel(UUID uuid) {
        return claimLevel.containsKey(uuid) ? claimLevel.get(uuid) : 0;
    }

    public void setRadZone(UUID uuid, boolean set) {
        if (set && !getRadZone(uuid)) {
            radZone.add(uuid);
        } else if (!set && getRadZone(uuid)) {
            radZone.remove(uuid);
        }
    }

    private boolean getRadZone(UUID uuid) {
        return radZone.contains(uuid);
    }

    public void setRadLevel(UUID uuid, double level) {
        Player p = Bukkit.getPlayer(uuid);
        if (p.getLevel() < 4 && getClaimLevel(uuid) >= 0) {
            level = 0;
        }
        rLevel.put(uuid, level);
        int newLvls = (int) (20 - getRadLevel(uuid));
        if (newLvls <= 2 && Fallout.getInstance().getPlayerGui().getInvs().containsKey(uuid)) {
            List<ItemStack> inv = new ArrayList(Arrays.asList(Fallout.getInstance().getPlayerGui().getInvs().get(uuid)));
            if (inv != null) {
                for (int i = 9; i < inv.size(); i++) {
                    ItemStack stack = inv.get(i);
                    if (stack != null && stack.getType() == Material.POTION && stack.getItemMeta().getDisplayName() != null && stack.getItemMeta().getDisplayName().equals(ChatColor.RED + "Rad Away")) {
                        inv.set(i, new ItemStack(Material.AIR));
                        setRadLevel(uuid, 0);
                        newLvls = 20;
                        Bukkit.getPlayer(uuid).closeInventory();
                        Fallout.getInstance().getPlayerGui().setInv(uuid, inv.toArray(new ItemStack[54]));
                        Bukkit.getPlayer(uuid).sendMessage(ChatColor.GREEN + "Pip-Boy: " + ChatColor.GRAY + "You were about to die to radiation, so I consumed a Rad Away for you. Be more careful next time!");
                        break;
                    }
                }
            }
        }
        if (newLvls == 0) {
            EntityDamageEvent ede = new EntityDamageEvent(p, EntityDamageEvent.DamageCause.STARVATION, Short.MAX_VALUE);
            Bukkit.getPluginManager().callEvent(ede);
            p.damage(Short.MAX_VALUE);
            if (p.getHealth() > 0) {
                p.setHealth(0);
            }
        } else {
            p.setFoodLevel(19);
            if (p.getHealth() > newLvls) {
                p.setHealth(newLvls);
            }
            if (p.getMaxHealth() != newLvls) {
                p.setMaxHealth(newLvls);
                p.damage(0);
            }
        }
    }

    public double getRadLevel(UUID uuid) {
        return rLevel.containsKey(uuid) ? rLevel.get(uuid) : 0;
    }

    public void setRadResist(UUID uuid, boolean set) {
        if (set && !getRadResist(uuid)) {
            radResist.add(uuid);
        } else if (!set && getRadResist(uuid)) {
            radResist.remove(uuid);
        }
    }

    private boolean getRadResist(UUID uuid) {
        return radResist.contains(uuid);
    }

    public void setLLevel(UUID uuid, int level) {
        lLevel.put(uuid, level);
    }

    public int getLLevel(UUID uuid) {
        return lLevel.containsKey(uuid) ? lLevel.get(uuid) : 1;
    }

    public void setILevel(UUID uuid, int level) {
        iLevel.put(uuid, level);
    }

    public int getILevel(UUID uuid) {
        return iLevel.containsKey(uuid) ? iLevel.get(uuid) : 1;
    }

    public void setELevel(UUID uuid, int level) {
        eLevel.put(uuid, level);
    }

    public int getELevel(UUID uuid) {
        return eLevel.containsKey(uuid) ? eLevel.get(uuid) : 1;
    }

    // 9 10 11 12 13 14 15 16 17
    //18 19 20 21 22 23 24 25 26
    //27 28 29 30 31 32 33 34 35
    public void setSLevel(UUID uuid, int level) {
        sLevel.put(uuid, level);
        Bukkit.getPlayer(uuid).addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 999999, ((int) (level / 3.0)) - 1, true));
    }

    public int getSLevel(UUID uuid) {
        return sLevel.containsKey(uuid) ? sLevel.get(uuid) : 1;
    }

    public void setStrResist(UUID uuid, boolean set) {
        if (set) {
            strResist.add(uuid);
        } else {
            strResist.remove(uuid);
        }
    }

    public boolean getStrResist(UUID uuid) {
        return getPowerSuit(uuid) || strResist.contains(uuid);
    }

    public void setXpLevel(UUID uuid, Integer level) {
        xpLevels.put(uuid, level);
    }

    public Integer getXpLevel(UUID uuid) {
        return xpLevels.containsKey(uuid) ? xpLevels.get(uuid) : 0;
    }

    public boolean getPowerSuit(UUID uuid) {
        return powerSuit.contains(uuid);
    }

    public void setPowerSuit(UUID uuid, boolean set) {
        if (set) {
            powerSuit.add(uuid);
        } else {
            powerSuit.remove(uuid);
        }
    }

    public void claimLevels(Player p, String attr) {
        UUID uuid = p.getUniqueId();
        if (Fallout.getInstance().getStats().getClaimLevel(uuid) >= 1) {
            boolean max = false;
            switch (attr) {
                case "luck":
                    if (getLLevel(uuid) >= 50) {
                        max = true;
                    } else {
                        setLLevel(uuid, getLLevel(uuid) + 1);
                    }
                    break;
                case "intelligence":
                    if (getILevel(uuid) >= 9) {
                        max = true;
                    } else {
                        setILevel(uuid, getILevel(uuid) + 1);
                    }
                    break;
                case "endurance":
                    if (getELevel(uuid) >= 15) {
                        max = true;
                    } else {
                        setELevel(uuid, getELevel(uuid) + 1);
                    }
                    break;
                case "strength":
                    if (getSLevel(uuid) >= 15) {
                        max = true;
                    } else {
                        setSLevel(uuid, getSLevel(uuid) + 1);
                    }
                    break;
            }
            if (max) {
                p.sendMessage(ChatColor.GREEN + "Pip-Boy: " + ChatColor.GRAY + "Your " + attr + " is at max value!");
            } else {
                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10, 1);
                Fallout.getInstance().getStats().setClaimLevel(uuid, Fallout.getInstance().getStats().getClaimLevel(uuid) - 1);
                p.openInventory(Fallout.getInstance().getPlayerGui().openLies(p));
            }
        } else {
            p.sendMessage(ChatColor.GREEN + "Pip-Boy: " + ChatColor.GRAY + "You don't have enough xp levels to claim.");
        }
    }
}
