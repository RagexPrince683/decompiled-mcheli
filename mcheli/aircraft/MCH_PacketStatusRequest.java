package mcheli.aircraft;

import com.google.common.io.ByteArrayDataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import mcheli.MCH_Packet;
import mcheli.wrapper.W_Entity;
import mcheli.wrapper.W_Network;
import net.minecraft.world.World;

public class MCH_PacketStatusRequest
  extends MCH_Packet
{
  public int entityID_AC;
  
  public MCH_PacketStatusRequest()
  {
    this.entityID_AC = -1;
  }
  
  public int getMessageID() { return 536875104; }
  


  public void readData(ByteArrayDataInput data)
  {
    try
    {
      this.entityID_AC = data.readInt();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  

  public void writeData(DataOutputStream dos)
  {
    try
    {
      dos.writeInt(this.entityID_AC);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  
  public static void requestStatus(MCH_EntityAircraft ac)
  {
    if (ac.worldObj.isRemote)
    {
      MCH_PacketStatusRequest s = new MCH_PacketStatusRequest();
      s.entityID_AC = W_Entity.getEntityId(ac);
      W_Network.sendToServer(s);
    }
  }
}
