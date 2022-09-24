package mcheli.command;

import com.google.common.io.ByteArrayDataInput;
import mcheli.aircraft.MCH_EntityAircraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class MCH_CommandPacketHandler
{
  public MCH_CommandPacketHandler() {}
  
  public static void onPacketTitle(EntityPlayer player, ByteArrayDataInput data)
  {
    if ((player == null) || (!player.worldObj.isRemote)) { return;
    }
    MCH_PacketTitle req = new MCH_PacketTitle();
    req.readData(data);
    
    mcheli.MCH_MOD.proxy.printChatMessage(req.chatComponent, req.showTime, req.position);
  }
  

  public static void onPacketSave(EntityPlayer player, ByteArrayDataInput data)
  {
    if ((player == null) || (player.worldObj.isRemote)) { return;
    }
    MCH_PacketCommandSave req = new MCH_PacketCommandSave();
    req.readData(data);
    
    MCH_EntityAircraft ac = MCH_EntityAircraft.getAircraft_RiddenOrControl(player);
    if (ac != null)
    {
      ac.setCommand(req.str, player);
    }
  }
}
