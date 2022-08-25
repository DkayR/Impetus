package ca.dklink750.impetus;

import ca.dklink750.impetus.utils.NmsProvider;
import net.minecraft.server.v1_12_R1.ChatComponentText;
import net.minecraft.server.v1_12_R1.ChatMessageType;
import net.minecraft.server.v1_12_R1.PacketPlayOutChat;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class v1_12_R1 extends NmsProvider {
    @Override
    public void sendActionBar(Player player, String message) {
        PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(message), ChatMessageType.GAME_INFO);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
    }
}
