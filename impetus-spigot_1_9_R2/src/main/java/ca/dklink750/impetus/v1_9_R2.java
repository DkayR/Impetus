package ca.dklink750.impetus;

import ca.dklink750.impetus.utils.NmsProvider;
import net.minecraft.server.v1_9_R2.ChatComponentText;
import net.minecraft.server.v1_9_R2.PacketPlayOutChat;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class v1_9_R2 extends NmsProvider {


    @Override
    public void sendActionBar(Player player, String message) {
        PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(message), (byte)2);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
    }
}
