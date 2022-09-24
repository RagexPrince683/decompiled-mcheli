package mcheli.lweapon;

import com.google.common.io.ByteArrayDataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import mcheli.MCH_Packet;

public class MCH_PacketLightWeaponPlayerControl
  extends MCH_Packet
{
  public boolean useWeapon;
  public int useWeaponOption1;
  public int useWeaponOption2;
  public double useWeaponPosX;
  public double useWeaponPosY;
  public double useWeaponPosZ;
  public int cmpReload;
  public int camMode;
  
  public MCH_PacketLightWeaponPlayerControl()
  {
    this.useWeapon = false;
    this.useWeaponOption1 = 0;
    this.useWeaponOption2 = 0;
    this.useWeaponPosX = 0.0D;
    this.useWeaponPosY = 0.0D;
    this.useWeaponPosZ = 0.0D;
    this.cmpReload = 0;
    this.camMode = 0;
  }
  
  public int getMessageID() { return 536936464; }
  


  public void readData(ByteArrayDataInput data)
  {
    try
    {
      this.useWeapon = (data.readByte() != 0);
      if (this.useWeapon)
      {
        this.useWeaponOption1 = data.readInt();
        this.useWeaponOption2 = data.readInt();
        this.useWeaponPosX = data.readDouble();
        this.useWeaponPosY = data.readDouble();
        this.useWeaponPosZ = data.readDouble();
      }
      this.cmpReload = data.readByte();
      this.camMode = data.readByte();
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
      dos.writeByte(this.useWeapon ? 1 : 0);
      if (this.useWeapon)
      {
        dos.writeInt(this.useWeaponOption1);
        dos.writeInt(this.useWeaponOption2);
        dos.writeDouble(this.useWeaponPosX);
        dos.writeDouble(this.useWeaponPosY);
        dos.writeDouble(this.useWeaponPosZ);
      }
      dos.writeByte(this.cmpReload);
      dos.writeByte(this.camMode);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
}
