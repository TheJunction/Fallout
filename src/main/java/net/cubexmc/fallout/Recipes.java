/*
 * Copyright (c) 2015 David Shen. All Rights Reserved.
 * Created by PantherMan594.
 */

package net.cubexmc.fallout;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

/**
 * Created by David on 12/24.
 *
 * @author David
 */
public class Recipes {

    public Recipes() {
        ItemStack rAStack = new ItemStack(Material.POTION, 1, (short) 8227);
        ItemMeta meta = rAStack.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Rad Away");
        meta.setLore(Arrays.asList("", ChatColor.GRAY + "Removes all radiation."));
        rAStack.setItemMeta(meta);
        ShapedRecipe radAway = new ShapedRecipe(rAStack);
        radAway.shape("PPP", "PMP", "PIP");
        radAway.setIngredient('P', Material.PAPER);
        radAway.setIngredient('M', Material.MILK_BUCKET);
        radAway.setIngredient('I', Material.IRON_INGOT);
        Fallout.getInstance().getServer().addRecipe(radAway);

        ItemStack rXStack = new ItemStack(Material.POTION, 1, (short) 8193);
        meta = rXStack.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Rad-X");
        meta.setLore(Arrays.asList("", ChatColor.GRAY + "Increased resistance to radiation for 5 minutes."));
        rXStack.setItemMeta(meta);
        ShapedRecipe radX = new ShapedRecipe(rXStack);
        radX.shape(" R ", "RSR", " I ");
        radX.setIngredient('R', Material.REDSTONE_BLOCK);
        radX.setIngredient('S', Material.SUGAR);
        radX.setIngredient('I', Material.IRON_BLOCK);
        Fallout.getInstance().getServer().addRecipe(radX);

        ItemStack nCStack = new ItemStack(Material.POTION, 1, (short) 8203);
        meta = nCStack.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Nuka-Cola");
        meta.setLore(Arrays.asList("", ChatColor.GRAY + "An invigorating drink. May be a little too strong."));
        nCStack.setItemMeta(meta);
        ShapedRecipe nukaCola = new ShapedRecipe(nCStack);
        nukaCola.shape(" A ", "sWS", " g ");
        nukaCola.setIngredient('A', Material.APPLE);
        nukaCola.setIngredient('s', Material.FERMENTED_SPIDER_EYE);
        nukaCola.setIngredient('W', Material.POTION);
        nukaCola.setIngredient('S', Material.SUGAR);
        nukaCola.setIngredient('g', Material.GOLDEN_CARROT);
        Fallout.getInstance().getServer().addRecipe(nukaCola);

        ItemStack fCStack = new ItemStack(Material.NETHER_STAR);
        meta = fCStack.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_GREEN + "Energized Fusion Core");
        meta.setLore(Arrays.asList("", ChatColor.GRAY + "For powering the power armor."));
        fCStack.setItemMeta(meta);
        ShapedRecipe fusionCore = new ShapedRecipe(fCStack);
        fusionCore.shape(" E ", "DFG", " R ");
        fusionCore.setIngredient('E', Material.EMERALD_BLOCK);
        fusionCore.setIngredient('D', Material.DIAMOND_BLOCK);
        fusionCore.setIngredient('F', Material.NETHER_STAR);
        fusionCore.setIngredient('G', Material.GOLD_BLOCK);
        fusionCore.setIngredient('R', Material.REDSTONE_BLOCK);
        Fallout.getInstance().getServer().addRecipe(fusionCore);

        ItemStack pHStack = new ItemStack(Material.CHAINMAIL_HELMET);
        meta = pHStack.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Power Helmet");
        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 8, true);
        meta.addEnchant(Enchantment.DURABILITY, 3, true);
        pHStack.setItemMeta(meta);
        ShapedRecipe powerHelmet = new ShapedRecipe(pHStack);
        powerHelmet.shape("GdG", "I I", "   ");
        powerHelmet.setIngredient('G', Material.GOLD_BLOCK);
        powerHelmet.setIngredient('d', Material.DIAMOND_HELMET);
        powerHelmet.setIngredient('I', Material.IRON_BLOCK);
        Fallout.getInstance().getServer().addRecipe(powerHelmet);

        ItemStack pCStack = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
        meta = pCStack.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Power Chestplate");
        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 8, true);
        meta.addEnchant(Enchantment.DURABILITY, 3, true);
        pCStack.setItemMeta(meta);
        ShapedRecipe powerChestplate = new ShapedRecipe(pCStack);
        powerChestplate.shape("I I", "GEG", "RdR");
        powerChestplate.setIngredient('I', Material.IRON_BLOCK);
        powerChestplate.setIngredient('G', Material.GOLD_BLOCK);
        powerChestplate.setIngredient('E', Material.NETHER_STAR);
        powerChestplate.setIngredient('R', Material.REDSTONE_BLOCK);
        powerChestplate.setIngredient('d', Material.DIAMOND_CHESTPLATE);
        Fallout.getInstance().getServer().addRecipe(powerChestplate);

        ItemStack pLStack = new ItemStack(Material.CHAINMAIL_LEGGINGS);
        meta = pLStack.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Power Leggings");
        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 8, true);
        meta.addEnchant(Enchantment.DURABILITY, 3, true);
        pLStack.setItemMeta(meta);
        ShapedRecipe powerLeggings = new ShapedRecipe(pLStack);
        powerLeggings.shape("IdI", "R R", "G G");
        powerLeggings.setIngredient('I', Material.IRON_BLOCK);
        powerLeggings.setIngredient('d', Material.DIAMOND_LEGGINGS);
        powerLeggings.setIngredient('R', Material.REDSTONE_BLOCK);
        powerLeggings.setIngredient('G', Material.GOLD_BLOCK);
        Fallout.getInstance().getServer().addRecipe(powerLeggings);

        ItemStack pBStack = new ItemStack(Material.CHAINMAIL_BOOTS);
        meta = pBStack.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Power Boots");
        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 8, true);
        meta.addEnchant(Enchantment.DURABILITY, 3, true);
        pBStack.setItemMeta(meta);
        ShapedRecipe powerBoots = new ShapedRecipe(pBStack);
        powerBoots.shape("   ", "IdI", "R R");
        powerBoots.setIngredient('I', Material.IRON_BLOCK);
        powerBoots.setIngredient('d', Material.DIAMOND_BOOTS);
        powerBoots.setIngredient('R', Material.REDSTONE_BLOCK);
        Fallout.getInstance().getServer().addRecipe(powerBoots);
    }
}
