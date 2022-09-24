package mcheli.weapon;

import net.minecraft.world.World;

public class MCH_EntityRocket extends MCH_EntityBaseBullet
{
  public MCH_EntityRocket(World par1World)
  {
    super(par1World);
  }
  


  public MCH_EntityRocket(World par1World, double posX, double posY, double posZ, double targetX, double targetY, double targetZ, float yaw, float pitch, double acceleration)
  {
    super(par1World, posX, posY, posZ, targetX, targetY, targetZ, yaw, pitch, acceleration);
  }
  

  public void onUpdate()
  {
    super.onUpdate();
    
    onUpdateBomblet();
    

    if (this.isBomblet <= 0)
    {
      if ((getInfo() != null) && (!getInfo().disableSmoke))
      {
        spawnParticle(getInfo().trajectoryParticleName, 3, 5.0F * getInfo().smokeSize * 0.5F);
      }
    }
  }
  
  public void sprinkleBomblet()
  {
    if (!this.worldObj.isRemote)
    {
      MCH_EntityRocket e = new MCH_EntityRocket(this.worldObj, this.posX, this.posY, this.posZ, this.motionX, this.motionY, this.motionZ, this.rotationYaw, this.rotationPitch, this.acceleration);
      


      e.setName(getName());
      
      e.setParameterFromWeapon(this, this.shootingAircraft, this.shootingEntity);
      
      float MOTION = getInfo().bombletDiff;
      float RANDOM = 1.2F;
      e.motionX += (this.rand.nextFloat() - 0.5D) * MOTION;
      e.motionY += (this.rand.nextFloat() - 0.5D) * MOTION;
      e.motionZ += (this.rand.nextFloat() - 0.5D) * MOTION;
      e.setBomblet();
      
      this.worldObj.spawnEntityInWorld(e);
    }
  }
  

  public MCH_BulletModel getDefaultBulletModel()
  {
    return MCH_DefaultBulletModels.Rocket;
  }
}
