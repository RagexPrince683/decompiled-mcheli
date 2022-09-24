package mcheli.weapon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import java.util.Random;
import mcheli.MCH_Explosion;
import mcheli.MCH_Explosion.ExplosionResult;
import mcheli.MCH_Lib;
import mcheli.aircraft.MCH_EntityAircraft;
import mcheli.aircraft.MCH_EntitySeat;
import mcheli.particles.MCH_ParticleParam;
import mcheli.particles.MCH_ParticlesUtil;
import mcheli.wrapper.W_Entity;
import mcheli.wrapper.W_WorldFunc;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public abstract class MCH_EntityBaseBullet extends W_Entity
{
  public static final int DATAWT_RESERVE1 = 26;
  public static final int DATAWT_TARGET_ENTITY = 27;
  public static final int DATAWT_MARKER_STAT = 28;
  public static final int DATAWT_NAME = 29;
  public static final int DATAWT_BULLET_MODEL = 30;
  public static final int DATAWT_BOMBLET_FLAG = 31;
  public Entity shootingEntity;
  public Entity shootingAircraft;
  private int countOnUpdate = 0;
  
  public int explosionPower;
  
  public int explosionPowerInWater;
  private int power;
  public double acceleration;
  public double accelerationFactor;
  public Entity targetEntity;
  public int piercing;
  public int delayFuse;
  public int sprinkleTime;
  public byte isBomblet;
  private MCH_WeaponInfo weaponInfo;
  private MCH_BulletModel model;
  public double prevPosX2;
  public double prevPosY2;
  public double prevPosZ2;
  public double prevMotionX;
  public double prevMotionY;
  public double prevMotionZ;
  
  public MCH_EntityBaseBullet(World par1World)
  {
    super(par1World);
    setSize(1.0F, 1.0F);
    this.prevRotationYaw = this.rotationYaw;
    this.prevRotationPitch = this.rotationPitch;
    this.targetEntity = null;
    setPower(1);
    this.acceleration = 1.0D;
    this.accelerationFactor = 1.0D;
    this.piercing = 0;
    this.explosionPower = 0;
    this.explosionPowerInWater = 0;
    this.delayFuse = 0;
    this.sprinkleTime = 0;
    this.isBomblet = -1;
    this.weaponInfo = null;
    this.ignoreFrustumCheck = true;
    if (par1World.isRemote)
    {
      this.model = null;
    }
  }
  




  public MCH_EntityBaseBullet(World par1World, double px, double py, double pz, double mx, double my, double mz, float yaw, float pitch, double acceleration)
  {
    this(par1World);
    setSize(1.0F, 1.0F);
    setLocationAndAngles(px, py, pz, yaw, pitch);
    setPosition(px, py, pz);
    this.prevRotationYaw = yaw;
    this.prevRotationPitch = pitch;
    this.yOffset = 0.0F;
    

    if (acceleration > 3.9D)
    {
      acceleration = 3.9D;
    }
    
    double d = net.minecraft.util.MathHelper.sqrt_double(mx * mx + my * my + mz * mz);
    this.motionX = (mx * acceleration / d);
    this.motionY = (my * acceleration / d);
    this.motionZ = (mz * acceleration / d);
    this.prevMotionX = this.motionX;
    this.prevMotionY = this.motionY;
    this.prevMotionZ = this.motionZ;
    this.acceleration = acceleration;
  }
  
  public void setLocationAndAngles(double par1, double par3, double par5, float par7, float par8)
  {
    super.setLocationAndAngles(par1, par3, par5, par7, par8);
    this.prevPosX2 = par1;
    this.prevPosY2 = par3;
    this.prevPosZ2 = par5;
  }
  
  public void setPositionAndRotation(double p_70080_1_, double p_70080_3_, double p_70080_5_, float yaw, float pitch)
  {
    super.setPositionAndRotation(p_70080_1_, p_70080_3_, p_70080_5_, yaw, pitch);
  }
  
  protected void setRotation(float p_70101_1_, float p_70101_2_)
  {
    super.setRotation(p_70101_1_, this.rotationPitch);
  }
  
  @SideOnly(Side.CLIENT)
  public void setPositionAndRotation2(double p_70056_1_, double p_70056_3_, double p_70056_5_, float p_70056_7_, float p_70056_8_, int p_70056_9_)
  {
    setPosition(p_70056_1_, (p_70056_3_ + this.posY * 2.0D) / 3.0D, p_70056_5_);
    


    setRotation(p_70056_7_, p_70056_8_);
  }
  

  protected void entityInit()
  {
    super.entityInit();
    getDataWatcher().addObject(27, Integer.valueOf(0));
    getDataWatcher().addObject(29, String.valueOf(""));
    getDataWatcher().addObject(30, String.valueOf(""));
    getDataWatcher().addObject(31, Byte.valueOf((byte)0));
  }
  
  public void setName(String s)
  {
    if ((s != null) && (!s.isEmpty()))
    {
      this.weaponInfo = MCH_WeaponInfoManager.get(s);
      if (this.weaponInfo != null)
      {
        if (!this.worldObj.isRemote)
        {
          getDataWatcher().updateObject(29, String.valueOf(s));
        }
        onSetWeasponInfo();
      }
    }
  }
  
  public String getName()
  {
    return getDataWatcher().getWatchableObjectString(29);
  }
  
  public MCH_WeaponInfo getInfo()
  {
    return this.weaponInfo;
  }
  
  public void onSetWeasponInfo()
  {
    if (!this.worldObj.isRemote)
    {
      this.isBomblet = 0;
    }
    if (getInfo().bomblet > 0)
    {
      this.sprinkleTime = getInfo().bombletSTime;
    }
    
    this.piercing = getInfo().piercing;
    
    if ((this instanceof MCH_EntityBullet))
    {
      if (getInfo().acceleration > 4.0F)
      {
        this.accelerationFactor = (getInfo().acceleration / 4.0F);
      }
    }
    else if ((this instanceof MCH_EntityRocket))
    {

      if ((this.isBomblet == 0) && (getInfo().acceleration > 4.0F))
      {
        this.accelerationFactor = (getInfo().acceleration / 4.0F);
      }
    }
  }
  
  public void setDead()
  {
    super.setDead();
  }
  



  public void setBomblet()
  {
    this.isBomblet = 1;
    this.sprinkleTime = 0;
    this.dataWatcher.updateObject(31, Byte.valueOf((byte)1));
  }
  
  public byte getBomblet()
  {
    return this.dataWatcher.getWatchableObjectByte(31);
  }
  
  public void setTargetEntity(Entity entity)
  {
    this.targetEntity = entity;
    if (!this.worldObj.isRemote)
    {
      if ((this.targetEntity instanceof net.minecraft.entity.player.EntityPlayerMP))
      {
        MCH_Lib.DbgLog(this.worldObj, "MCH_EntityBaseBullet.setTargetEntity alert" + this.targetEntity + " / " + this.targetEntity.ridingEntity, new Object[0]);
        if ((this.targetEntity.ridingEntity != null) && (!(this.targetEntity.ridingEntity instanceof MCH_EntityAircraft)) && (!(this.targetEntity.ridingEntity instanceof MCH_EntitySeat)))
        {


          W_WorldFunc.MOD_playSoundAtEntity(this.targetEntity, "alert", 2.0F, 1.0F);
        }
      }
      if (entity != null)
      {
        getDataWatcher().updateObject(27, Integer.valueOf(W_Entity.getEntityId(entity)));
      }
      else
      {
        getDataWatcher().updateObject(27, Integer.valueOf(0));
      }
    }
  }
  
  public int getTargetEntityID()
  {
    if (this.targetEntity != null) return W_Entity.getEntityId(this.targetEntity);
    return getDataWatcher().getWatchableObjectInt(27);
  }
  
  public MCH_BulletModel getBulletModel()
  {
    if (getInfo() == null) return null;
    if (this.isBomblet < 0) { return null;
    }
    if (this.model == null)
    {
      if (this.isBomblet == 1)
      {
        this.model = getInfo().bombletModel;
      }
      else
      {
        this.model = getInfo().bulletModel;
      }
      if (this.model == null)
      {
        this.model = getDefaultBulletModel();
      }
    }
    
    return this.model;
  }
  
  public abstract MCH_BulletModel getDefaultBulletModel();
  
  public void sprinkleBomblet() {}
  
  public void spawnParticle(String name, int num, float size) {
    if (this.worldObj.isRemote)
    {
      if ((name.isEmpty()) || (num < 1) || (num > 50)) { return;
      }
      double x = (this.posX - this.prevPosX) / num;
      double y = (this.posY - this.prevPosY) / num;
      double z = (this.posZ - this.prevPosZ) / num;
      double x2 = (this.prevPosX - this.prevPosX2) / num;
      double y2 = (this.prevPosY - this.prevPosY2) / num;
      double z2 = (this.prevPosZ - this.prevPosZ2) / num;
      
      if (name.equals("explode"))
      {
        for (int i = 0; i < num; i++)
        {
          MCH_ParticleParam prm = new MCH_ParticleParam(this.worldObj, "smoke", (this.prevPosX + x * i + (this.prevPosX2 + x2 * i)) / 2.0D, (this.prevPosY + y * i + (this.prevPosY2 + y2 * i)) / 2.0D, (this.prevPosZ + z * i + (this.prevPosZ2 + z2 * i)) / 2.0D);
          


          prm.size = (size + this.rand.nextFloat());
          MCH_ParticlesUtil.spawnParticle(prm);
        }
        
      }
      else {
        for (int i = 0; i < num; i++)
        {
          MCH_ParticlesUtil.DEF_spawnParticle(name, (this.prevPosX + x * i + (this.prevPosX2 + x2 * i)) / 2.0D, (this.prevPosY + y * i + (this.prevPosY2 + y2 * i)) / 2.0D, (this.prevPosZ + z * i + (this.prevPosZ2 + z2 * i)) / 2.0D, 0.0D, 0.0D, 0.0D, 50.0F);
        }
      }
    }
  }
  



  public void DEF_spawnParticle(String name, int num, float size)
  {
    if (this.worldObj.isRemote)
    {
      if ((name.isEmpty()) || (num < 1) || (num > 50)) return;
      double x = (this.posX - this.prevPosX) / num;
      double y = (this.posY - this.prevPosY) / num;
      double z = (this.posZ - this.prevPosZ) / num;
      double x2 = (this.prevPosX - this.prevPosX2) / num;
      double y2 = (this.prevPosY - this.prevPosY2) / num;
      double z2 = (this.prevPosZ - this.prevPosZ2) / num;
      for (int i = 0; i < num; i++)
      {
        MCH_ParticlesUtil.DEF_spawnParticle(name, (this.prevPosX + x * i + (this.prevPosX2 + x2 * i)) / 2.0D, (this.prevPosY + y * i + (this.prevPosY2 + y2 * i)) / 2.0D, (this.prevPosZ + z * i + (this.prevPosZ2 + z2 * i)) / 2.0D, 0.0D, 0.0D, 0.0D, 150.0F);
      }
    }
  }
  




  public int getCountOnUpdate()
  {
    return this.countOnUpdate;
  }
  
  public void clearCountOnUpdate()
  {
    this.countOnUpdate = 0;
  }
  




  @SideOnly(Side.CLIENT)
  public boolean isInRangeToRenderDist(double par1)
  {
    double d1 = this.boundingBox.getAverageEdgeLength() * 4.0D;
    d1 *= 64.0D;
    return par1 < d1 * d1;
  }
  
  public void setParameterFromWeapon(MCH_WeaponBase w, Entity entity, Entity user)
  {
    this.explosionPower = w.explosionPower;
    this.explosionPowerInWater = w.explosionPowerInWater;
    setPower(w.power);
    this.piercing = w.piercing;
    this.shootingAircraft = entity;
    this.shootingEntity = user;
  }
  
  public void setParameterFromWeapon(MCH_EntityBaseBullet b, Entity entity, Entity user) {
    this.explosionPower = b.explosionPower;
    this.explosionPowerInWater = b.explosionPowerInWater;
    setPower(b.getPower());
    this.piercing = b.piercing;
    this.shootingAircraft = entity;
    this.shootingEntity = user;
  }
  
  public void setMotion(double targetX, double targetY, double targetZ)
  {
    double d6 = net.minecraft.util.MathHelper.sqrt_double(targetX * targetX + targetY * targetY + targetZ * targetZ);
    this.motionX = (targetX * this.acceleration / d6);
    this.motionY = (targetY * this.acceleration / d6);
    this.motionZ = (targetZ * this.acceleration / d6);
  }
  
  public boolean usingFlareOfTarget(Entity entity)
  {
    if (getCountOnUpdate() % 3 == 0)
    {
      List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, entity.boundingBox.expand(15.0D, 15.0D, 15.0D));
      

      for (int i = 0; i < list.size(); i++)
      {
        if (((Entity)list.get(i)).getEntityData().getBoolean("FlareUsing"))
        {
          return true;
        }
      }
    }
    return false;
  }
  
  public void guidanceToTarget(double targetPosX, double targetPosY, double targetPosZ)
  {
    guidanceToTarget(targetPosX, targetPosY, targetPosZ, 1.0F);
  }
  
  public void guidanceToTarget(double targetPosX, double targetPosY, double targetPosZ, float accelerationFactor) {
    double tx = targetPosX - this.posX;
    double ty = targetPosY - this.posY;
    double tz = targetPosZ - this.posZ;
    
    double d = net.minecraft.util.MathHelper.sqrt_double(tx * tx + ty * ty + tz * tz);
    double mx = tx * this.acceleration / d;
    double my = ty * this.acceleration / d;
    double mz = tz * this.acceleration / d;
    
    this.motionX = ((this.motionX * 6.0D + mx) / 7.0D);
    this.motionY = ((this.motionY * 6.0D + my) / 7.0D);
    this.motionZ = ((this.motionZ * 6.0D + mz) / 7.0D);
    
    double a = (float)Math.atan2(this.motionZ, this.motionX);
    this.rotationYaw = ((float)(a * 180.0D / 3.141592653589793D) - 90.0F);
    
    double r = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
    this.rotationPitch = (-(float)(Math.atan2(this.motionY, r) * 180.0D / 3.141592653589793D));
  }
  
  public boolean checkValid()
  {
    if ((this.shootingEntity == null) && (this.shootingAircraft == null)) { return false;
    }
    



    Entity shooter = ((this.shootingAircraft == null) || (!this.shootingAircraft.isDead)) || (this.shootingEntity != null) ? this.shootingEntity : this.shootingAircraft;
    
    double x = this.posX - shooter.posX;
    
    double z = this.posZ - shooter.posZ;
    return (x * x + z * z < 3.38724E7D) && (this.posY > -10.0D);
  }
  
  public float getGravity()
  {
    return getInfo() != null ? getInfo().gravity : 0.0F;
  }
  
  public float getGravityInWater() {
    return getInfo() != null ? getInfo().gravityInWater : 0.0F;
  }
  




  public void onUpdate()
  {
    if ((this.worldObj.isRemote) && (this.countOnUpdate == 0))
    {
      int tgtEttId = getTargetEntityID();
      if (tgtEttId > 0)
      {
        setTargetEntity(this.worldObj.getEntityByID(tgtEttId));
      }
    }
    
    if ((!this.worldObj.isRemote) && (getCountOnUpdate() % 20 == 19) && ((this.targetEntity instanceof net.minecraft.entity.player.EntityPlayerMP)))
    {
      MCH_Lib.DbgLog(this.worldObj, "MCH_EntityBaseBullet.onUpdate alert" + this.targetEntity + " / " + this.targetEntity.ridingEntity, new Object[0]);
      if ((this.targetEntity.ridingEntity != null) && (!(this.targetEntity.ridingEntity instanceof MCH_EntityAircraft)) && (!(this.targetEntity.ridingEntity instanceof MCH_EntitySeat)))
      {


        W_WorldFunc.MOD_playSoundAtEntity(this.targetEntity, "alert", 2.0F, 1.0F);
      }
    }
    
    this.prevMotionX = this.motionX;
    this.prevMotionY = this.motionY;
    this.prevMotionZ = this.motionZ;
    
    this.countOnUpdate += 1;
    if (this.countOnUpdate > 10000000)
    {
      clearCountOnUpdate();
    }
    
    this.prevPosX2 = this.prevPosX;
    this.prevPosY2 = this.prevPosY;
    this.prevPosZ2 = this.prevPosZ;
    super.onUpdate();
    
    if ((this.prevMotionX != this.motionX) || (this.prevMotionY != this.motionY) || (this.prevMotionZ != this.motionZ))
    {
      if (this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ > 0.1D)
      {
        double a = (float)Math.atan2(this.motionZ, this.motionX);
        this.rotationYaw = ((float)(a * 180.0D / 3.141592653589793D) - 90.0F);
        
        double r = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
        this.rotationPitch = (-(float)(Math.atan2(this.motionY, r) * 180.0D / 3.141592653589793D));
      }
    }
    
    if (getInfo() == null)
    {
      if (this.countOnUpdate < 2)
      {
        setName(getName());
      }
      else
      {
        MCH_Lib.Log(this, "##### MCH_EntityBaseBullet onUpdate() Weapon info null %d, %s, Name=%s", new Object[] { Integer.valueOf(W_Entity.getEntityId(this)), getEntityName(), getName() });
        
        setDead();
        return;
      }
      if (getInfo() == null) { return;
      }
    }
    if ((getInfo().bound <= 0.0F) && (this.onGround))
    {
      this.motionX *= 0.9D;
      
      this.motionZ *= 0.9D;
    }
    
    if (this.worldObj.isRemote)
    {
      if (this.isBomblet < 0)
      {
        this.isBomblet = getBomblet();
      }
    }
    
    if (!this.worldObj.isRemote)
    {
      if (((int)this.posY <= 255) && (!this.worldObj.blockExists((int)this.posX, (int)this.posY, (int)this.posZ)))
      {
        if (getInfo().delayFuse > 0)
        {
          if (this.delayFuse == 0)
          {
            this.delayFuse = getInfo().delayFuse;
          }
        }
        else
        {
          setDead();
          return;
        }
      }
      
      if (this.delayFuse > 0)
      {
        this.delayFuse -= 1;
        if (this.delayFuse == 0)
        {
          onUpdateTimeout();
          setDead();
          return;
        }
      }
      
      if (!checkValid())
      {
        setDead();
        return;
      }
      
      if ((getInfo().timeFuse > 0) && (getCountOnUpdate() > getInfo().timeFuse))
      {
        onUpdateTimeout();
        setDead();
        return;
      }
      
      if (getInfo().explosionAltitude > 0)
      {

        if (MCH_Lib.getBlockIdY(this, 3, -getInfo().explosionAltitude) != 0)
        {
          MovingObjectPosition mop = new MovingObjectPosition((int)this.posX, (int)this.posY, (int)this.posZ, 0, Vec3.createVectorHelper(this.posX, this.posY, this.posZ));
          
          onImpact(mop, 1.0F);
        }
      }
    }
    
    if (!isInWater())
    {
      this.motionY += getGravity();
    }
    else
    {
      this.motionY += getGravityInWater();
    }
    if (!this.isDead)
    {
      onUpdateCollided();
    }
    
    this.posX += this.motionX * this.accelerationFactor;
    this.posY += this.motionY * this.accelerationFactor;
    this.posZ += this.motionZ * this.accelerationFactor;
    
    if (this.worldObj.isRemote)
    {
      updateSplash();
    }
    
    if (isInWater())
    {
      float f3 = 0.25F;
      this.worldObj.spawnParticle("bubble", this.posX - this.motionX * f3, this.posY - this.motionY * f3, this.posZ - this.motionZ * f3, this.motionX, this.motionY, this.motionZ);
    }
    




    setPosition(this.posX, this.posY, this.posZ);
  }
  
  public void updateSplash()
  {
    if (getInfo() == null) return;
    if (getInfo().power <= 0) { return;
    }
    
    if (!W_WorldFunc.isBlockWater(this.worldObj, (int)(this.prevPosX + 0.5D), (int)(this.prevPosY + 0.5D), (int)(this.prevPosZ + 0.5D)))
    {
      if (W_WorldFunc.isBlockWater(this.worldObj, (int)(this.posX + 0.5D), (int)(this.posY + 0.5D), (int)(this.posZ + 0.5D)))
      {
        double x = this.posX - this.prevPosX;
        double y = this.posY - this.prevPosY;
        double z = this.posZ - this.prevPosZ;
        double d = Math.sqrt(x * x + y * y + z * z);
        

        if (d <= 0.15D) { return;
        }
        x /= d;
        y /= d;
        z /= d;
        double px = this.prevPosX;
        double py = this.prevPosY;
        double pz = this.prevPosZ;
        for (int i = 0; i <= d; i++)
        {
          px += x;
          py += y;
          pz += z;
          if (W_WorldFunc.isBlockWater(this.worldObj, (int)(px + 0.5D), (int)(py + 0.5D), (int)(pz + 0.5D)))
          {
            float pwr = getInfo().power < 20 ? getInfo().power : 20.0F;
            int n = this.rand.nextInt(1 + (int)pwr / 3) + (int)pwr / 2 + 1;
            
            pwr *= 0.03F;
            
            for (int j = 0; j < n; j++)
            {
              MCH_ParticleParam prm = new MCH_ParticleParam(this.worldObj, "splash", px, py + 0.5D, pz, pwr * (this.rand.nextDouble() - 0.5D) * 0.3D, pwr * (this.rand.nextDouble() * 0.5D + 0.5D) * 1.8D, pwr * (this.rand.nextDouble() - 0.5D) * 0.3D, pwr * 5.0F);
              




              MCH_ParticlesUtil.spawnParticle(prm);
            }
            break;
          }
        }
      }
    }
  }
  
  public void onUpdateTimeout()
  {
    if (isInWater())
    {
      if (this.explosionPowerInWater > 0)
      {
        newExplosion(this.posX, this.posY, this.posZ, this.explosionPowerInWater, this.explosionPowerInWater, true);
      }
      

    }
    else if (this.explosionPower > 0)
    {
      newExplosion(this.posX, this.posY, this.posZ, this.explosionPower, getInfo().explosionBlock, false);

    }
    else if (this.explosionPower < 0)
    {
      playExplosionSound();
    }
  }
  

  public void onUpdateBomblet()
  {
    if (!this.worldObj.isRemote)
    {
      if ((this.sprinkleTime > 0) && (!this.isDead))
      {
        this.sprinkleTime -= 1;
        if (this.sprinkleTime == 0)
        {
          for (int i = 0; i < getInfo().bomblet; i++)
          {
            sprinkleBomblet();
          }
          
          setDead();
        }
      }
    }
  }
  
  public void boundBullet(int sideHit)
  {
    switch (sideHit)
    {
    case 0: 
      if (this.motionY > 0.0D) this.motionY = (-this.motionY * getInfo().bound);
      break;
    case 1: 
      if (this.motionY < 0.0D) this.motionY = (-this.motionY * getInfo().bound);
      break;
    case 2: 
      if (this.motionZ > 0.0D) this.motionZ = (-this.motionZ * getInfo().bound); else
        this.posZ += this.motionZ;
      break;
    case 3: 
      if (this.motionZ < 0.0D) this.motionZ = (-this.motionZ * getInfo().bound); else
        this.posZ += this.motionZ;
      break;
    case 4: 
      if (this.motionX > 0.0D) this.motionX = (-this.motionX * getInfo().bound); else
        this.posX += this.motionX;
      break;
    case 5: 
      if (this.motionX < 0.0D) this.motionX = (-this.motionX * getInfo().bound); else
        this.posX += this.motionX;
      break;
    }
    if (getInfo().bound <= 0.0F)
    {
      this.motionX *= 0.25D;
      this.motionY *= 0.25D;
      this.motionZ *= 0.25D;
    }
  }
  
  protected void onUpdateCollided()
  {
    float damageFator = 1.0F;
    double mx = this.motionX * this.accelerationFactor;
    double my = this.motionY * this.accelerationFactor;
    double mz = this.motionZ * this.accelerationFactor;
    

    MovingObjectPosition m = null;
    for (int i = 0; i < 5; i++)
    {
      Vec3 vec3 = W_WorldFunc.getWorldVec3(this.worldObj, this.posX, this.posY, this.posZ);
      Vec3 vec31 = W_WorldFunc.getWorldVec3(this.worldObj, this.posX + mx, this.posY + my, this.posZ + mz);
      m = W_WorldFunc.clip(this.worldObj, vec3, vec31);
      
      boolean continueClip = false;
      
      if ((this.shootingEntity != null) && (mcheli.wrapper.W_MovingObjectPosition.isHitTypeTile(m)))
      {
        net.minecraft.block.Block block = W_WorldFunc.getBlock(this.worldObj, m.blockX, m.blockY, m.blockZ);
        if (mcheli.MCH_Config.bulletBreakableBlocks.contains(block))
        {
          W_WorldFunc.destroyBlock(this.worldObj, m.blockX, m.blockY, m.blockZ, true);
          continueClip = true;
        }
      }
      
      if (!continueClip) {
        break;
      }
    }
    

    Vec3 vec3 = W_WorldFunc.getWorldVec3(this.worldObj, this.posX, this.posY, this.posZ);
    Vec3 vec31 = W_WorldFunc.getWorldVec3(this.worldObj, this.posX + mx, this.posY + my, this.posZ + mz);
    
    if (getInfo().delayFuse > 0)
    {
      if (m != null)
      {
        boundBullet(m.sideHit);
        if (this.delayFuse == 0)
        {
          this.delayFuse = getInfo().delayFuse;
        }
      }
      return;
    }
    
    if (m != null)
    {
      vec31 = W_WorldFunc.getWorldVec3(this.worldObj, m.hitVec.xCoord, m.hitVec.yCoord, m.hitVec.zCoord);
    }
    
    Entity entity = null;
    List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.addCoord(mx, my, mz).expand(21.0D, 21.0D, 21.0D));
    double d0 = 0.0D;
    
    for (int j = 0; j < list.size(); j++)
    {
      Entity entity1 = (Entity)list.get(j);
      

      if (canBeCollidedEntity(entity1))
      {
        float f = 0.3F;
        AxisAlignedBB axisalignedbb = entity1.boundingBox.expand(f, f, f);
        MovingObjectPosition m1 = axisalignedbb.calculateIntercept(vec3, vec31);
        
        if (m1 != null)
        {
          double d1 = vec3.distanceTo(m1.hitVec);
          
          if ((d1 < d0) || (d0 == 0.0D))
          {
            entity = entity1;
            d0 = d1;
          }
        }
      }
    }
    











    if (entity != null)
    {
      m = new MovingObjectPosition(entity);
    }
    
    if (m != null)
    {
      onImpact(m, damageFator);
    }
  }
  

  public boolean canBeCollidedEntity(Entity entity)
  {
    if ((entity instanceof mcheli.chain.MCH_EntityChain)) { return false;
    }
    if (!entity.canBeCollidedWith()) { return false;
    }
    if ((entity instanceof MCH_EntityBaseBullet))
    {
      if (this.worldObj.isRemote) { return false;
      }
      MCH_EntityBaseBullet blt = (MCH_EntityBaseBullet)entity;
      if (W_Entity.isEqual(blt.shootingAircraft, this.shootingAircraft)) return false;
      if (W_Entity.isEqual(blt.shootingEntity, this.shootingEntity)) { return false;
      }
    }
    if ((entity instanceof MCH_EntitySeat)) return false;
    if ((entity instanceof mcheli.aircraft.MCH_EntityHitBox)) { return false;
    }
    if (W_Entity.isEqual(entity, this.shootingEntity)) return false;
    if ((this.shootingAircraft instanceof MCH_EntityAircraft))
    {

      if (W_Entity.isEqual(entity, this.shootingAircraft)) { return false;
      }
      if (((MCH_EntityAircraft)this.shootingAircraft).isMountedEntity(entity)) return false;
    }
    for (String s : mcheli.MCH_Config.IgnoreBulletHitList)
    {
      if (entity.getClass().getName().toLowerCase().indexOf(s.toLowerCase()) >= 0) return false;
    }
    return true;
  }
  
  public void notifyHitBullet()
  {
    if (((this.shootingAircraft instanceof MCH_EntityAircraft)) && (mcheli.wrapper.W_EntityPlayer.isPlayer(this.shootingEntity)))
    {
      mcheli.aircraft.MCH_PacketNotifyHitBullet.send((MCH_EntityAircraft)this.shootingAircraft, (net.minecraft.entity.player.EntityPlayer)this.shootingEntity);
    }
    if (mcheli.wrapper.W_EntityPlayer.isPlayer(this.shootingEntity))
    {
      mcheli.aircraft.MCH_PacketNotifyHitBullet.send(null, (net.minecraft.entity.player.EntityPlayer)this.shootingEntity);
    }
  }
  



  protected void onImpact(MovingObjectPosition m, float damageFactor)
  {
    if (!this.worldObj.isRemote)
    {

      if (m.entityHit != null)
      {
        onImpactEntity(m.entityHit, damageFactor);
        this.piercing = 0;
      }
      
      float expPower = this.explosionPower * damageFactor;
      float expPowerInWater = this.explosionPowerInWater * damageFactor;
      
      double dx = 0.0D;
      double dy = 0.0D;
      double dz = 0.0D;
      

      if (this.piercing > 0)
      {
        this.piercing -= 1;
        if (expPower > 0.0F)
        {
          newExplosion(m.hitVec.xCoord + dx, m.hitVec.yCoord + dy, m.hitVec.zCoord + dz, 1.0F, 1.0F, false);
        }
        
      }
      else
      {
        if (expPowerInWater == 0.0F)
        {
          if (getInfo().isFAE)
          {
            newFAExplosion(this.posX, this.posY, this.posZ, expPower, getInfo().explosionBlock);
          }
          else if (expPower > 0.0F)
          {
            newExplosion(m.hitVec.xCoord + dx, m.hitVec.yCoord + dy, m.hitVec.zCoord + dz, expPower, getInfo().explosionBlock, false);





          }
          else if (expPower < 0.0F)
          {
            playExplosionSound();

          }
          


        }
        else if (m.entityHit != null)
        {
          if (isInWater())
          {
            newExplosion(m.hitVec.xCoord + dx, m.hitVec.yCoord + dy, m.hitVec.zCoord + dz, expPowerInWater, expPowerInWater, true);

          }
          else
          {
            newExplosion(m.hitVec.xCoord + dx, m.hitVec.yCoord + dy, m.hitVec.zCoord + dz, expPower, getInfo().explosionBlock, false);
          }
          

        }
        else if ((isInWater()) || (MCH_Lib.isBlockInWater(this.worldObj, m.blockX, m.blockY, m.blockZ)))
        {
          newExplosion(m.blockX, m.blockY, m.blockZ, expPowerInWater, expPowerInWater, true);

        }
        else if (expPower > 0.0F)
        {
          newExplosion(m.hitVec.xCoord + dx, m.hitVec.yCoord + dy, m.hitVec.zCoord + dz, expPower, getInfo().explosionBlock, false);

        }
        else if (expPower < 0.0F)
        {
          playExplosionSound();
        }
        

        setDead();
      }
    }
    else if ((getInfo() != null) && ((getInfo().explosion == 0) || (getInfo().modeNum >= 2)))
    {
      if (mcheli.wrapper.W_MovingObjectPosition.isHitTypeTile(m))
      {
        float p = getInfo().power;
        for (int i = 0; i < p / 3.0F; i++)
        {
          MCH_ParticlesUtil.spawnParticleTileCrack(this.worldObj, m.blockX, m.blockY, m.blockZ, m.hitVec.xCoord + (this.rand.nextFloat() - 0.5D) * p / 10.0D, m.hitVec.yCoord + 0.1D, m.hitVec.zCoord + (this.rand.nextFloat() - 0.5D) * p / 10.0D, -this.motionX * p / 2.0D, p / 2.0F, -this.motionZ * p / 2.0D);
        }
      }
    }
  }
  






  public void onImpactEntity(Entity entity, float damageFactor)
  {
    if (!entity.isDead)
    {
      MCH_Lib.DbgLog(this.worldObj, "MCH_EntityBaseBullet.onImpactEntity:Damage=%d:" + entity.getClass(), new Object[] { Integer.valueOf(getPower()) });
      

      MCH_Lib.applyEntityHurtResistantTimeConfig(entity);
      
      DamageSource ds = DamageSource.causeThrownDamage(this, this.shootingEntity);
      float damage = mcheli.MCH_Config.applyDamageVsEntity(entity, ds, getPower() * damageFactor);
      damage *= (getInfo() != null ? getInfo().getDamageFactor(entity) : 1.0F);
      entity.attackEntityFrom(ds, damage);
      



      if (((this instanceof MCH_EntityBullet)) && ((entity instanceof net.minecraft.entity.passive.EntityVillager)))
      {
        if ((this.shootingEntity != null) && ((this.shootingEntity.ridingEntity instanceof MCH_EntitySeat)))
        {
          mcheli.MCH_Achievement.addStat(this.shootingEntity, mcheli.MCH_Achievement.aintWarHell, 1);
        }
      }
      
      if (!entity.isDead) {}
    }
    



    notifyHitBullet();
  }
  


  public void newFAExplosion(double x, double y, double z, float exp, float expBlock)
  {
    MCH_Explosion.ExplosionResult result = MCH_Explosion.newExplosion(this.worldObj, this, this.shootingEntity, x, y, z, exp, expBlock, true, true, getInfo().flaming, false, 15);
    

    if ((result != null) && (result.hitEntity))
    {
      notifyHitBullet();
    }
  }
  
  public void newExplosion(double x, double y, double z, float exp, float expBlock, boolean inWater) {
    MCH_Explosion.ExplosionResult result;
    if (!inWater)
    {
      result = MCH_Explosion.newExplosion(this.worldObj, this, this.shootingEntity, x, y, z, exp, expBlock, this.rand.nextInt(3) == 0, true, getInfo().flaming, true, 0, getInfo() != null ? getInfo().damageFactor : null);

    }
    else
    {

      result = MCH_Explosion.newExplosionInWater(this.worldObj, this, this.shootingEntity, x, y, z, exp, expBlock, this.rand.nextInt(3) == 0, true, getInfo().flaming, true, 0, getInfo() != null ? getInfo().damageFactor : null);
    }
    


    if ((result != null) && (result.hitEntity))
    {
      notifyHitBullet();
    }
  }
  
  public void playExplosionSound()
  {
    MCH_Explosion.playExplosionSound(this.worldObj, this.posX, this.posY, this.posZ);
  }
  



  public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
  {
    par1NBTTagCompound.setTag("direction", newDoubleNBTList(new double[] { this.motionX, this.motionY, this.motionZ }));
    par1NBTTagCompound.setString("WeaponName", getName());
  }
  













  public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
  {
    setDead();
  }
  



  public boolean canBeCollidedWith()
  {
    return true;
  }
  
  public float getCollisionBorderSize()
  {
    return 1.0F;
  }
  

  public boolean attackEntityFrom(DamageSource ds, float par2)
  {
    if (isEntityInvulnerable()) { return false;
    }
    if (!this.worldObj.isRemote)
    {
      if ((par2 > 0.0F) && (ds.getDamageType().equalsIgnoreCase("thrown")))
      {
        setBeenAttacked();
        MovingObjectPosition m = new MovingObjectPosition((int)(this.posX + 0.5D), (int)(this.posY + 0.5D), (int)(this.posZ + 0.5D), 0, Vec3.createVectorHelper(this.posX + 0.5D, this.posY + 0.5D, this.posZ + 0.5D));
        




        onImpact(m, 1.0F);
        return true;
      }
    }
    
    return false;
  }
  
  @SideOnly(Side.CLIENT)
  public float getShadowSize()
  {
    return 0.0F;
  }
  



  public float getBrightness(float par1)
  {
    return 1.0F;
  }
  
  @SideOnly(Side.CLIENT)
  public int getBrightnessForRender(float par1)
  {
    return 15728880;
  }
  
  public int getPower()
  {
    return this.power;
  }
  
  public void setPower(int power)
  {
    this.power = power;
  }
}
