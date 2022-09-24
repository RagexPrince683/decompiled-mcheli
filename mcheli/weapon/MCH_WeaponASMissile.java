package mcheli.weapon;

import mcheli.wrapper.W_WorldFunc;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class MCH_WeaponASMissile extends MCH_WeaponBase
{
  public MCH_WeaponASMissile(World w, Vec3 v, float yaw, float pitch, String nm, MCH_WeaponInfo wi)
  {
    super(w, v, yaw, pitch, nm, wi);
    this.acceleration = 3.0F;
    this.explosionPower = 9;
    this.power = 40;
    this.interval = 65186;
    if (w.isRemote) this.interval -= 10;
  }
  
  public boolean isCooldownCountReloadTime()
  {
    return true;
  }
  

  public void update(int countWait)
  {
    super.update(countWait);
  }
  
  public boolean shot(MCH_WeaponParam prm)
  {
    float yaw = prm.user.rotationYaw;
    float pitch = prm.user.rotationPitch;
    
    double tX = -MathHelper.sin(yaw / 180.0F * 3.1415927F) * MathHelper.cos(pitch / 180.0F * 3.1415927F);
    
    double tZ = MathHelper.cos(yaw / 180.0F * 3.1415927F) * MathHelper.cos(pitch / 180.0F * 3.1415927F);
    
    double tY = -MathHelper.sin(pitch / 180.0F * 3.1415927F);
    
    double dist = MathHelper.sqrt_double(tX * tX + tY * tY + tZ * tZ);
    

    if (this.worldObj.isRemote)
    {
      tX = tX * 200.0D / dist;
      tY = tY * 200.0D / dist;
      tZ = tZ * 200.0D / dist;
    }
    else
    {
      tX = tX * 250.0D / dist;
      tY = tY * 250.0D / dist;
      tZ = tZ * 250.0D / dist;
    }
    


    Vec3 src = W_WorldFunc.getWorldVec3(this.worldObj, prm.entity.posX, prm.entity.posY + 1.62D, prm.entity.posZ);
    Vec3 dst = W_WorldFunc.getWorldVec3(this.worldObj, prm.entity.posX + tX, prm.entity.posY + 1.62D + tY, prm.entity.posZ + tZ);
    MovingObjectPosition m = W_WorldFunc.clip(this.worldObj, src, dst);
    
    if ((m != null) && (mcheli.wrapper.W_MovingObjectPosition.isHitTypeTile(m)) && (!mcheli.MCH_Lib.isBlockInWater(this.worldObj, m.blockX, m.blockY, m.blockZ)))
    {


      if (!this.worldObj.isRemote)
      {
        MCH_EntityASMissile e = new MCH_EntityASMissile(this.worldObj, prm.posX, prm.posY, prm.posZ, tX, tY, tZ, yaw, pitch, this.acceleration);
        
        e.setName(this.name);
        
        e.setParameterFromWeapon(this, prm.entity, prm.user);
        
        e.targetPosX = m.hitVec.xCoord;
        e.targetPosY = m.hitVec.yCoord;
        e.targetPosZ = m.hitVec.zCoord;
        
        this.worldObj.spawnEntityInWorld(e);
        
        playSound(prm.entity);
      }
      
      return true;
    }
    return false;
  }
}
