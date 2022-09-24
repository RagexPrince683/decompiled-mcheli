package mcheli.container;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import mcheli.MCH_Config;
import mcheli.MCH_ConfigPrm;
import mcheli.MCH_Lib;
import mcheli.MCH_MOD;
import mcheli.aircraft.MCH_EntityAircraft;
import mcheli.aircraft.MCH_EntitySeat;
import mcheli.aircraft.MCH_IEntityCanRideAircraft;
import mcheli.aircraft.MCH_SeatRackInfo;
import mcheli.multiplay.MCH_Multiplay;
import mcheli.wrapper.W_AxisAlignedBB;
import mcheli.wrapper.W_Block;
import mcheli.wrapper.W_Blocks;
import mcheli.wrapper.W_EntityContainer;
import mcheli.wrapper.W_WorldFunc;
import net.minecraft.block.material.Material;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class MCH_EntityContainer extends W_EntityContainer implements MCH_IEntityCanRideAircraft
{
  private boolean isBoatEmpty;
  private double speedMultiplier;
  private int boatPosRotationIncrements;
  private double boatX;
  private double boatY;
  private double boatZ;
  private double boatYaw;
  private double boatPitch;
  @SideOnly(Side.CLIENT)
  private double velocityX;
  @SideOnly(Side.CLIENT)
  private double velocityY;
  @SideOnly(Side.CLIENT)
  private double velocityZ;
  
  public MCH_EntityContainer(World par1World)
  {
    super(par1World);
    this.speedMultiplier = 0.07D;
    this.preventEntitySpawning = true;
    setSize(2.0F, 1.0F);
    this.yOffset = (this.height / 2.0F);
    this.stepHeight = 0.6F;
    this.isImmuneToFire = true;
    this.renderDistanceWeight = 2.0D;
  }
  
  public MCH_EntityContainer(World par1World, double par2, double par4, double par6)
  {
    this(par1World);
    setPosition(par2, par4 + this.yOffset, par6);
    this.motionX = 0.0D;
    this.motionY = 0.0D;
    this.motionZ = 0.0D;
    this.prevPosX = par2;
    this.prevPosY = par4;
    this.prevPosZ = par6;
  }
  




  protected boolean canTriggerWalking()
  {
    return false;
  }
  
  protected void entityInit()
  {
    this.dataWatcher.addObject(17, new Integer(0));
    this.dataWatcher.addObject(18, new Integer(1));
    this.dataWatcher.addObject(19, new Integer(0));
  }
  




  public AxisAlignedBB getCollisionBox(Entity par1Entity)
  {
    return par1Entity.boundingBox;
  }
  



  public AxisAlignedBB getBoundingBox()
  {
    return this.boundingBox;
  }
  



  public boolean canBePushed()
  {
    return true;
  }
  

  public int getSizeInventory()
  {
    return 54;
  }
  
  public String getInvName()
  {
    return "Container " + super.getInvName();
  }
  



  public double getMountedYOffset()
  {
    return -0.3D;
  }
  



  public boolean attackEntityFrom(DamageSource ds, float damage)
  {
    if (isEntityInvulnerable())
    {
      return false;
    }
    
    if ((this.worldObj.isRemote) || (this.isDead))
    {
      return false;
    }
    
    damage = MCH_Config.applyDamageByExternal(this, ds, damage);
    
    if (!MCH_Multiplay.canAttackEntity(ds, this))
    {
      return false;
    }
    
    if (((ds.getEntity() instanceof EntityPlayer)) && (ds.getDamageType().equalsIgnoreCase("player")))
    {
      MCH_Lib.DbgLog(this.worldObj, "MCH_EntityContainer.attackEntityFrom:damage=%.1f:%s", new Object[] { Float.valueOf(damage), ds.getDamageType() });
      W_WorldFunc.MOD_playSoundAtEntity(this, "hit", 1.0F, 1.3F);
      setDamageTaken(getDamageTaken() + (int)(damage * 20.0F));
    }
    else
    {
      return false;
    }
    setForwardDirection(-getForwardDirection());
    setTimeSinceHit(10);
    setBeenAttacked();
    boolean flag = ((ds.getEntity() instanceof EntityPlayer)) && (((EntityPlayer)ds.getEntity()).capabilities.isCreativeMode);
    
    if ((flag) || (getDamageTaken() > 40.0F))
    {
      if (!flag)
      {
        dropItemWithOffset(MCH_MOD.itemContainer, 1, 0.0F);
      }
      
      setDead();
    }
    return true;
  }
  




  @SideOnly(Side.CLIENT)
  public void performHurtAnimation()
  {
    setForwardDirection(-getForwardDirection());
    setTimeSinceHit(10);
    setDamageTaken(getDamageTaken() * 11);
  }
  



  public boolean canBeCollidedWith()
  {
    return !this.isDead;
  }
  





  @SideOnly(Side.CLIENT)
  public void setPositionAndRotation2(double par1, double par3, double par5, float par7, float par8, int par9)
  {
    this.boatPosRotationIncrements = (par9 + 10);
    
    this.boatX = par1;
    this.boatY = par3;
    this.boatZ = par5;
    this.boatYaw = par7;
    this.boatPitch = par8;
    this.motionX = this.velocityX;
    this.motionY = this.velocityY;
    this.motionZ = this.velocityZ;
  }
  




  @SideOnly(Side.CLIENT)
  public void setVelocity(double par1, double par3, double par5)
  {
    this.velocityX = (this.motionX = par1);
    this.velocityY = (this.motionY = par3);
    this.velocityZ = (this.motionZ = par5);
  }
  



  public void onUpdate()
  {
    super.onUpdate();
    
    if (getTimeSinceHit() > 0)
    {
      setTimeSinceHit(getTimeSinceHit() - 1);
    }
    
    if (getDamageTaken() > 0.0F)
    {
      setDamageTaken(getDamageTaken() - 1);
    }
    
    this.prevPosX = this.posX;
    this.prevPosY = this.posY;
    this.prevPosZ = this.posZ;
    byte b0 = 5;
    double d0 = 0.0D;
    
    for (int i = 0; i < b0; i++)
    {
      double d1 = this.boundingBox.minY + (this.boundingBox.maxY - this.boundingBox.minY) * (i + 0) / b0 - 0.125D;
      double d2 = this.boundingBox.minY + (this.boundingBox.maxY - this.boundingBox.minY) * (i + 1) / b0 - 0.125D;
      AxisAlignedBB axisalignedbb = W_AxisAlignedBB.getAABB(this.boundingBox.minX, d1, this.boundingBox.minZ, this.boundingBox.maxX, d2, this.boundingBox.maxZ);
      
      if (this.worldObj.isAABBInMaterial(axisalignedbb, Material.water))
      {
        d0 += 1.0D / b0;
      }
      else if (this.worldObj.isAABBInMaterial(axisalignedbb, Material.lava))
      {
        d0 += 1.0D / b0;
      }
    }
    

    double d3 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
    


    if (d3 > 0.2625D)
    {
      double d4 = Math.cos(this.rotationYaw * 3.141592653589793D / 180.0D);
      double d1 = Math.sin(this.rotationYaw * 3.141592653589793D / 180.0D);
    }
    



    if (this.worldObj.isRemote)
    {
      if (this.boatPosRotationIncrements > 0)
      {
        double d4 = this.posX + (this.boatX - this.posX) / this.boatPosRotationIncrements;
        double d5 = this.posY + (this.boatY - this.posY) / this.boatPosRotationIncrements;
        double d11 = this.posZ + (this.boatZ - this.posZ) / this.boatPosRotationIncrements;
        double d10 = MathHelper.wrapAngleTo180_double(this.boatYaw - this.rotationYaw);
        this.rotationYaw = ((float)(this.rotationYaw + d10 / this.boatPosRotationIncrements));
        this.rotationPitch = ((float)(this.rotationPitch + (this.boatPitch - this.rotationPitch) / this.boatPosRotationIncrements));
        this.boatPosRotationIncrements -= 1;
        setPosition(d4, d5, d11);
        setRotation(this.rotationYaw, this.rotationPitch);
      }
      else
      {
        double d4 = this.posX + this.motionX;
        double d5 = this.posY + this.motionY;
        double d11 = this.posZ + this.motionZ;
        setPosition(d4, d5, d11);
        
        if (this.onGround)
        {
          float groundSpeed = 0.9F;
          this.motionX *= 0.8999999761581421D;
          
          this.motionZ *= 0.8999999761581421D;
        }
        
        this.motionX *= 0.99D;
        this.motionY *= 0.95D;
        this.motionZ *= 0.99D;
      }
    }
    else
    {
      if (d0 < 1.0D)
      {
        double d4 = d0 * 2.0D - 1.0D;
        this.motionY += 0.04D * d4;
      }
      else
      {
        if (this.motionY < 0.0D)
        {
          this.motionY /= 2.0D;
        }
        
        this.motionY += 0.007D;
      }
      
      double d4 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
      
      if (d4 > 0.35D)
      {
        double d5 = 0.35D / d4;
        this.motionX *= d5;
        this.motionZ *= d5;
        d4 = 0.35D;
      }
      
      if ((d4 > d3) && (this.speedMultiplier < 0.35D))
      {
        this.speedMultiplier += (0.35D - this.speedMultiplier) / 35.0D;
        
        if (this.speedMultiplier > 0.35D)
        {
          this.speedMultiplier = 0.35D;
        }
      }
      else
      {
        this.speedMultiplier -= (this.speedMultiplier - 0.07D) / 35.0D;
        
        if (this.speedMultiplier < 0.07D)
        {
          this.speedMultiplier = 0.07D;
        }
      }
      
      if (this.onGround)
      {
        float groundSpeed = 0.9F;
        this.motionX *= 0.8999999761581421D;
        
        this.motionZ *= 0.8999999761581421D;
      }
      
      moveEntity(this.motionX, this.motionY, this.motionZ);
      
      this.motionX *= 0.99D;
      this.motionY *= 0.95D;
      this.motionZ *= 0.99D;
      
      this.rotationPitch = 0.0F;
      double d5 = this.rotationYaw;
      double d11 = this.prevPosX - this.posX;
      double d10 = this.prevPosZ - this.posZ;
      
      if (d11 * d11 + d10 * d10 > 0.001D)
      {
        d5 = (float)(Math.atan2(d10, d11) * 180.0D / 3.141592653589793D);
      }
      
      double d12 = MathHelper.wrapAngleTo180_double(d5 - this.rotationYaw);
      
      if (d12 > 5.0D)
      {
        d12 = 5.0D;
      }
      
      if (d12 < -5.0D)
      {
        d12 = -5.0D;
      }
      
      this.rotationYaw = ((float)(this.rotationYaw + d12));
      setRotation(this.rotationYaw, this.rotationPitch);
      
      if (!this.worldObj.isRemote)
      {
        List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(0.2D, 0.0D, 0.2D));
        

        if ((list != null) && (!list.isEmpty()))
        {
          for (int l = 0; l < list.size(); l++)
          {
            Entity entity = (Entity)list.get(l);
            
            if ((entity.canBePushed()) && ((entity instanceof MCH_EntityContainer)))
            {
              entity.applyEntityCollision(this);
            }
          }
        }
        
        if (MCH_Config.Collision_DestroyBlock.prmBool)
        {
          for (int l = 0; l < 4; l++)
          {
            int i1 = MathHelper.floor_double(this.posX + (l % 2 - 0.5D) * 0.8D);
            int j1 = MathHelper.floor_double(this.posZ + (l / 2 - 0.5D) * 0.8D);
            
            for (int k1 = 0; k1 < 2; k1++)
            {
              int l1 = MathHelper.floor_double(this.posY) + k1;
              
              if (W_WorldFunc.isEqualBlock(this.worldObj, i1, l1, j1, W_Block.getSnowLayer()))
              {
                this.worldObj.setBlockToAir(i1, l1, j1);
              }
              else if (W_WorldFunc.isEqualBlock(this.worldObj, i1, l1, j1, W_Blocks.waterlily))
              {
                W_WorldFunc.destroyBlock(this.worldObj, i1, l1, j1, true);
              }
            }
          }
        }
      }
    }
  }
  



  protected void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
  {
    super.writeEntityToNBT(par1NBTTagCompound);
  }
  



  protected void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
  {
    super.readEntityFromNBT(par1NBTTagCompound);
  }
  
  @SideOnly(Side.CLIENT)
  public float getShadowSize()
  {
    return 2.0F;
  }
  



  public boolean interactFirst(EntityPlayer player)
  {
    if (player != null)
    {
      openInventory(player);
    }
    return true;
  }
  



  public void setDamageTaken(int par1)
  {
    this.dataWatcher.updateObject(19, Integer.valueOf(par1));
  }
  



  public int getDamageTaken()
  {
    return this.dataWatcher.getWatchableObjectInt(19);
  }
  



  public void setTimeSinceHit(int par1)
  {
    this.dataWatcher.updateObject(17, Integer.valueOf(par1));
  }
  



  public int getTimeSinceHit()
  {
    return this.dataWatcher.getWatchableObjectInt(17);
  }
  



  public void setForwardDirection(int par1)
  {
    this.dataWatcher.updateObject(18, Integer.valueOf(par1));
  }
  



  public int getForwardDirection()
  {
    return this.dataWatcher.getWatchableObjectInt(18);
  }
  

  public boolean canRideAircraft(MCH_EntityAircraft ac, int seatID, MCH_SeatRackInfo info)
  {
    for (String s : info.names)
    {
      if (s.equalsIgnoreCase("container"))
      {
        return (ac.ridingEntity == null) && (this.ridingEntity == null);
      }
    }
    return false;
  }
  
  public boolean isSkipNormalRender()
  {
    return this.ridingEntity instanceof MCH_EntitySeat;
  }
}
