/*
 * Copyright (c) 2016 CubeXMC. All Rights Reserved.
 * Created by PantherMan594.
 */

package net.cubexmc.fallout;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Created by David on 12/29.
 *
 * @author David
 */
public class ActionBar {
    private Player p;
    private String msg;

    public ActionBar(Player p, String msg) {
        this.p = p;
        this.msg = msg;

        IChatBaseComponent icbc = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + msg + "\"}");
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutChat(icbc, (byte) 2));
    }

    public Player getPlayer() {
        return p;
    }

    public String getMsg() {
        return msg;
    }
}
