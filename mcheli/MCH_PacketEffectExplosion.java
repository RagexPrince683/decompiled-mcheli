package mcheli;

import com.google.common.io.ByteArrayDataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import mcheli.wrapper.W_Network;





public class MCH_PacketEffectExplosion
  extends MCH_Packet
{
  ExplosionParam prm = new ExplosionParam();
  





  public MCH_PacketEffectExplosion() {}
  




  public int getMessageID()
  {
    return 268437520;
  }
  

  public void readData(ByteArrayDataInput data)
  {
    try
    {
      this.prm.posX = data.readDouble();
      this.prm.posY = data.readDouble();
      this.prm.posZ = data.readDouble();
      this.prm.size = data.readFloat();
      this.prm.exploderID = data.readInt();
      this.prm.inWater = (data.readByte() != 0);
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
      dos.writeDouble(this.prm.posX);
      dos.writeDouble(this.prm.posY);
      dos.writeDouble(this.prm.posZ);
      dos.writeFloat(this.prm.size);
      dos.writeInt(this.prm.exploderID);
      dos.writeByte(this.prm.inWater ? 1 : 0);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  
  public static ExplosionParam create()
  {
    return new MCH_PacketEffectExplosion().aaa();
  }
  
  private ExplosionParam aaa() { return new ExplosionParam(); }
  
  public static void send(ExplosionParam param)
  {
    if (param != null)
    {
      MCH_PacketEffectExplosion s = new MCH_PacketEffectExplosion();
      s.prm = param;
      W_Network.sendToAllPlayers(s);
    }
  }
  
  public class ExplosionParam
  {
    public double posX;
    public double posY;
    public double posZ;
    public float size;
    public int exploderID;
    public boolean inWater;
    
    public ExplosionParam() {}
  }
}
