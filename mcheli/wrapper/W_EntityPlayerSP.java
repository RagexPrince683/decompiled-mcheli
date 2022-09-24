package mcheli.wrapper;

import net.minecraft.client.entity.EntityPlayerSP;

public class W_EntityPlayerSP
{
  public W_EntityPlayerSP() {}
  
  public static void closeScreen(net.minecraft.entity.Entity p) {
    if ((p instanceof EntityPlayerSP))
    {
      ((EntityPlayerSP)p).closeScreen();
    }
  }
}
