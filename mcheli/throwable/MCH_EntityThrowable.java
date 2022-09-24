package mcheli.throwable;

import java.util.Random;
import mcheli.MCH_Color;
import mcheli.MCH_Lib;
import mcheli.particles.MCH_ParticleParam;
import mcheli.wrapper.W_WorldFunc;
import net.minecraft.block.material.Material;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class MCH_EntityThrowable extends EntityThrowable
{
  private static final int DATAID_NAME = 31;
  private int countOnUpdate;
  private MCH_ThrowableInfo throwableInfo;
  public double boundPosX;
  public double boundPosY;
  public double boundPosZ;
  public MovingObjectPosition lastOnImpact;
  public int noInfoCount;
  
  public MCH_EntityThrowable(World par1World)
  {
    super(par1World);
    init();
  }
  
  public MCH_EntityThrowable(World par1World, EntityLivingBase par2EntityLivingBase, float acceleration)
  {
    super(par1World, par2EntityLivingBase);
    this.motionX *= acceleration;
    this.motionY *= acceleration;
    this.motionZ *= acceleration;
    
    init();
  }
  
  public MCH_EntityThrowable(World par1World, double par2, double par4, double par6)
  {
    super(par1World, par2, par4, par6);
    init();
  }
  

  public MCH_EntityThrowable(World p_i1777_1_, double x, double y, double z, float yaw, float pitch)
  {
    this(p_i1777_1_);
    setSize(0.25F, 0.25F);
    setLocationAndAngles(x, y, z, yaw, pitch);
    this.posX -= MathHelper.cos(this.rotationYaw / 180.0F * 3.1415927F) * 0.16F;
    this.posY -= 0.10000000149011612D;
    this.posZ -= MathHelper.sin(this.rotationYaw / 180.0F * 3.1415927F) * 0.16F;
    setPosition(this.posX, this.posY, this.posZ);
    this.yOffset = 0.0F;
    float f = 0.4F;
    this.motionX = (-MathHelper.sin(this.rotationYaw / 180.0F * 3.1415927F) * MathHelper.cos(this.rotationPitch / 180.0F * 3.1415927F) * f);
    this.motionZ = (MathHelper.cos(this.rotationYaw / 180.0F * 3.1415927F) * MathHelper.cos(this.rotationPitch / 180.0F * 3.1415927F) * f);
    this.motionY = (-MathHelper.sin((this.rotationPitch + func_70183_g()) / 180.0F * 3.1415927F) * f);
    setThrowableHeading(this.motionX, this.motionY, this.motionZ, func_70182_d(), 1.0F);
  }
  


  public void init()
  {
    this.lastOnImpact = null;
    this.countOnUpdate = 0;
    setInfo(null);
    this.noInfoCount = 0;
    
    getDataWatcher().addObject(31, new String(""));
  }
  
  public void setDead()
  {
    String s = getInfo() != null ? getInfo().name : "null";
    MCH_Lib.DbgLog(this.worldObj, "MCH_EntityThrowable.setDead(%s)", new Object[] { s });
    
    super.setDead();
  }
  

  public void onUpdate()
  {
    this.boundPosX = this.posX;
    this.boundPosY = this.posY;
    this.boundPosZ = this.posZ;
    
    if (getInfo() != null)
    {
      net.minecraft.block.Block block = W_WorldFunc.getBlock(this.worldObj, (int)(this.posX + 0.5D), (int)this.posY, (int)(this.posZ + 0.5D));
      Material mat = W_WorldFunc.getBlockMaterial(this.worldObj, (int)(this.posX + 0.5D), (int)this.posY, (int)(this.posZ + 0.5D));
      if ((block != null) && (mat == Material.water))
      {
        this.motionY += getInfo().gravityInWater;
      }
      else
      {
        this.motionY += getInfo().gravity;
      }
    }
    
    super.onUpdate();
    
    if (this.lastOnImpact != null)
    {
      boundBullet(this.lastOnImpact);
      setPosition(this.boundPosX + this.motionX, this.boundPosY + this.motionY, this.boundPosZ + this.motionZ);
      


      this.lastOnImpact = null;
    }
    
    this.countOnUpdate += 1;
    
    if (this.countOnUpdate >= 2147483632)
    {
      setDead();
      return;
    }
    
    if (getInfo() == null)
    {
      String s = getDataWatcher().getWatchableObjectString(31);
      if (!s.isEmpty())
      {
        setInfo(MCH_ThrowableInfoManager.get(s));
      }
      
      if (getInfo() == null)
      {
        this.noInfoCount += 1;
        if (this.noInfoCount > 10)
        {
          setDead();
        }
        return;
      }
    }
    
    if (this.isDead) { return;
    }
    if (!this.worldObj.isRemote)
    {
      if (this.countOnUpdate == getInfo().timeFuse)
      {
        if (getInfo().explosion > 0)
        {
          mcheli.MCH_Explosion.newExplosion(this.worldObj, null, null, this.posX, this.posY, this.posZ, getInfo().explosion, getInfo().explosion, true, true, false, true, 0);
          

          setDead();
          return;
        }
      }
      
      if (this.countOnUpdate >= getInfo().aliveTime)
      {
        setDead();

      }
      

    }
    else if (this.countOnUpdate >= getInfo().timeFuse)
    {
      if (getInfo().explosion <= 0)
      {




        for (int i = 0; i < getInfo().smokeNum; i++)
        {
          float y = getInfo().smokeVelocityVertical >= 0.0F ? 0.2F : -0.2F;
          
          float r = getInfo().smokeColor.r * 0.9F + this.rand.nextFloat() * 0.1F;
          float g = getInfo().smokeColor.g * 0.9F + this.rand.nextFloat() * 0.1F;
          float b = getInfo().smokeColor.b * 0.9F + this.rand.nextFloat() * 0.1F;
          if (getInfo().smokeColor.r == getInfo().smokeColor.g)
          {
            g = r;
          }
          if (getInfo().smokeColor.r == getInfo().smokeColor.b)
          {
            b = r;
          }
          if (getInfo().smokeColor.g == getInfo().smokeColor.b)
          {
            b = g;
          }
          
          spawnParticle("explode", 4, getInfo().smokeSize + this.rand.nextFloat() * getInfo().smokeSize / 3.0F, r, g, b, getInfo().smokeVelocityHorizontal * (this.rand.nextFloat() - 0.5F), getInfo().smokeVelocityVertical * this.rand.nextFloat(), getInfo().smokeVelocityHorizontal * (this.rand.nextFloat() - 0.5F));
        }
      }
    }
  }
  







  public void spawnParticle(String name, int num, float size, float r, float g, float b, float mx, float my, float mz)
  {
    if (this.worldObj.isRemote)
    {
      if ((name.isEmpty()) || (num < 1)) return;
      double x = (this.posX - this.prevPosX) / num;
      double y = (this.posY - this.prevPosY) / num;
      double z = (this.posZ - this.prevPosZ) / num;
      for (int i = 0; i < num; i++)
      {
        MCH_ParticleParam prm = new MCH_ParticleParam(this.worldObj, "smoke", this.prevPosX + x * i, 1.0D + this.prevPosY + y * i, this.prevPosZ + z * i);
        
        prm.setMotion(mx, my, mz);
        prm.size = size;
        prm.setColor(1.0F, r, g, b);
        prm.isEffectWind = true;
        prm.toWhite = true;
        
        mcheli.particles.MCH_ParticlesUtil.spawnParticle(prm);
      }
    }
  }
  

  protected float getGravityVelocity()
  {
    return 0.0F;
  }
  
  public void boundBullet(MovingObjectPosition m)
  {
    float bound = getInfo().bound;
    
    switch (m.sideHit)
    {
    case 0: 
    case 1: 
      this.motionX *= 0.8999999761581421D;
      this.motionZ *= 0.8999999761581421D;
      this.boundPosY = m.hitVec.yCoord;
      if (((m.sideHit == 0) && (this.motionY > 0.0D)) || ((m.sideHit == 1) && (this.motionY < 0.0D)))
      {
        this.motionY = (-this.motionY * bound);
      }
      else
      {
        this.motionY = 0.0D;
      }
      break;
    case 2: 
      if (this.motionZ > 0.0D) { this.motionZ = (-this.motionZ * bound);
      }
      break;
    case 3: 
      if (this.motionZ < 0.0D) { this.motionZ = (-this.motionZ * bound);
      }
      break;
    case 4: 
      if (this.motionX > 0.0D) { this.motionX = (-this.motionX * bound);
      }
      break;
    case 5: 
      if (this.motionX < 0.0D) { this.motionX = (-this.motionX * bound);
      }
      
      break;
    }
    
  }
  

  protected void onImpact(MovingObjectPosition m)
  {
    if (getInfo() != null)
    {
      this.lastOnImpact = m;
    }
  }
  
  public MCH_ThrowableInfo getInfo()
  {
    return this.throwableInfo;
  }
  
  public void setInfo(MCH_ThrowableInfo info)
  {
    this.throwableInfo = info;
    if (info != null)
    {
      if (!this.worldObj.isRemote)
      {
        getDataWatcher().updateObject(31, new String(info.name));
      }
    }
  }
}
