package mcheli.weapon;

import mcheli.wrapper.ChatMessageComponent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IChatComponent;

public class MCH_DummyEntityPlayer extends mcheli.wrapper.W_EntityPlayer
{
  public MCH_DummyEntityPlayer(net.minecraft.world.World p_i45324_1_, EntityPlayer player)
  {
    super(p_i45324_1_, player);
  }
  
  public void addChatMessage(IChatComponent var1) {}
  
  public boolean canCommandSenderUseCommand(int var1, String var2) { return false; }
  
  public net.minecraft.util.ChunkCoordinates getPlayerCoordinates() { return null; }
  
  public void sendChatToPlayer(ChatMessageComponent chatmessagecomponent) {}
}
