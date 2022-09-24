package mcheli.uav;

import com.google.common.io.ByteArrayDataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import mcheli.MCH_Packet;

public class MCH_UavPacketStatus
  extends MCH_Packet
{
  public byte posUavX;
  public byte posUavY;
  public byte posUavZ;
  public boolean continueControl;
  
  public MCH_UavPacketStatus()
  {
    this.posUavX = 0;
    this.posUavY = 0;
    this.posUavZ = 0;
    this.continueControl = false;
  }
  
  public int getMessageID() { return 537133072; }
  


  public void readData(ByteArrayDataInput data)
  {
    try
    {
      this.posUavX = data.readByte();
      this.posUavY = data.readByte();
      this.posUavZ = data.readByte();
      this.continueControl = (data.readByte() != 0);
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
      dos.writeByte(this.posUavX);
      dos.writeByte(this.posUavY);
      dos.writeByte(this.posUavZ);
      dos.writeByte(this.continueControl ? 1 : 0);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
}
