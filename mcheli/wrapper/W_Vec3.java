package mcheli.wrapper;

import net.minecraft.util.Vec3;

public class W_Vec3
{
  public W_Vec3() {}
  
  public static void rotateAroundZ(float par1, Vec3 vOut)
  {
    float f1 = net.minecraft.util.MathHelper.cos(par1);
    float f2 = net.minecraft.util.MathHelper.sin(par1);
    double d0 = vOut.xCoord * f1 + vOut.yCoord * f2;
    double d1 = vOut.yCoord * f1 - vOut.xCoord * f2;
    double d2 = vOut.zCoord;
    vOut.xCoord = d0;
    vOut.yCoord = d1;
    vOut.zCoord = d2;
  }
}
