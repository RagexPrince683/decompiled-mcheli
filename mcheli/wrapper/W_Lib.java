package mcheli.wrapper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public class W_Lib
{
  public W_Lib() {}
  
  public static boolean isEntityLivingBase(Entity entity)
  {
    return entity instanceof EntityLivingBase;
  }
  




  public static EntityLivingBase castEntityLivingBase(Object entity)
  {
    return (EntityLivingBase)entity;
  }
  



  public static Class getEntityLivingBaseClass()
  {
    return EntityLivingBase.class;
  }
  


  public static double getEntityMoveDist(Entity entity)
  {
    if (entity == null) { return 0.0D;
    }
    return (entity instanceof EntityLivingBase) ? ((EntityLivingBase)entity).moveForward : 0.0D;
  }
  


  public static boolean isClientPlayer(Entity entity)
  {
    if ((entity instanceof net.minecraft.entity.player.EntityPlayer))
    {
      if (entity.worldObj.isRemote)
      {
        return W_Entity.isEqual(mcheli.MCH_MOD.proxy.getClientPlayer(), entity);
      }
    }
    return false;
  }
  
  public static boolean isFirstPerson()
  {
    return mcheli.MCH_MOD.proxy.isFirstPerson();
  }
}
