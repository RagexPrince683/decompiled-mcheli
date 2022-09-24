package mcheli.gltd;

import com.google.common.io.ByteArrayDataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import mcheli.MCH_Packet;

public class MCH_PacketGLTDPlayerControl
  extends MCH_Packet
{
  public byte switchCameraMode;
  public byte switchWeapon;
  public boolean useWeapon;
  public int useWeaponOption1;
  public int useWeaponOption2;
  public double useWeaponPosX;
  public double useWeaponPosY;
  public double useWeaponPosZ;
  public boolean unmount;
  
  public MCH_PacketGLTDPlayerControl()
  {
    this.switchCameraMode = -1;
    this.switchWeapon = -1;
    this.useWeapon = false;
    this.useWeaponOption1 = 0;
    this.useWeaponOption2 = 0;
    this.useWeaponPosX = 0.0D;
    this.useWeaponPosY = 0.0D;
    this.useWeaponPosZ = 0.0D;
    this.unmount = false;
  }
  
  public int getMessageID() { return 536887312; }
  


  public void readData(ByteArrayDataInput data)
  {
    try
    {
      this.switchCameraMode = data.readByte();
      this.switchWeapon = data.readByte();
      this.useWeapon = (data.readByte() != 0);
      if (this.useWeapon)
      {
        this.useWeaponOption1 = data.readInt();
        this.useWeaponOption2 = data.readInt();
        this.useWeaponPosX = data.readDouble();
        this.useWeaponPosY = data.readDouble();
        this.useWeaponPosZ = data.readDouble();
      }
      this.unmount = (data.readByte() != 0);
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
      dos.writeByte(this.switchCameraMode);
      dos.writeByte(this.switchWeapon);
      dos.writeByte(this.useWeapon ? 1 : 0);
      if (this.useWeapon)
      {
        dos.writeInt(this.useWeaponOption1);
        dos.writeInt(this.useWeaponOption2);
        dos.writeDouble(this.useWeaponPosX);
        dos.writeDouble(this.useWeaponPosY);
        dos.writeDouble(this.useWeaponPosZ);
      }
      dos.writeByte(this.unmount ? 1 : 0);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
}
