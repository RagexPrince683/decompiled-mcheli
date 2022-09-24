package mcheli.aircraft;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcheli.MCH_Lib;
import mcheli.mob.MCH_ItemSpawnGunner;
import mcheli.tool.MCH_ItemWrench;
import mcheli.wrapper.W_Entity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class MCH_EntitySeat
  extends W_Entity
{
  public String parentUniqueID;
  private MCH_EntityAircraft parent;
  public int seatID;
  public int parentSearchCount;
  protected Entity lastRiddenByEntity;
  public static final float BB_SIZE = 1.0F;
  
  public MCH_EntitySeat(World world)
  {
    super(world);
    setSize(1.0F, 1.0F);
    this.yOffset = 0.0F;
    this.motionX = 0.0D;
    this.motionY = 0.0D;
    this.motionZ = 0.0D;
    this.seatID = -1;
    setParent(null);
    this.parentSearchCount = 0;
    this.lastRiddenByEntity = null;
    this.ignoreFrustumCheck = true;
    this.isImmuneToFire = true;
  }
  
  public MCH_EntitySeat(World world, double x, double y, double z) {
    this(world);
    setPosition(x, y + 1.0D, z);
    this.prevPosX = x;
    this.prevPosY = (y + 1.0D);
    this.prevPosZ = z;
  }
  
  protected boolean canTriggerWalking() { return false; }
  


  public AxisAlignedBB getCollisionBox(Entity par1Entity)
  {
    return par1Entity.boundingBox;
  }
  
  public AxisAlignedBB getBoundingBox() { return this.boundingBox; }
  
  public boolean canBePushed() {
    return false;
  }
  
  public double getMountedYOffset() { return -0.3D; }
  

  public boolean attackEntityFrom(DamageSource par1DamageSource, float par2)
  {
    if (getParent() != null)
    {
      return getParent().attackEntityFrom(par1DamageSource, par2);
    }
    return false;
  }
  
  public boolean canBeCollidedWith() {
    return !this.isDead;
  }
  




  @SideOnly(Side.CLIENT)
  public void setPositionAndRotation2(double par1, double par3, double par5, float par7, float par8, int par9) {}
  




  public void setDead()
  {
    super.setDead();
  }
  

  public void onUpdate()
  {
    super.onUpdate();
    

    this.fallDistance = 0.0F;
    if (this.riddenByEntity != null)
    {
      this.riddenByEntity.fallDistance = 0.0F;
    }
    

    if ((this.lastRiddenByEntity == null) && (this.riddenByEntity != null))
    {
      if (getParent() != null)
      {
        MCH_Lib.DbgLog(this.worldObj, "MCH_EntitySeat.onUpdate:SeatID=%d", new Object[] { Integer.valueOf(this.seatID), this.riddenByEntity.toString() });
        getParent().onMountPlayerSeat(this, this.riddenByEntity);
      }
    }
    else if ((this.lastRiddenByEntity != null) && (this.riddenByEntity == null))
    {
      if (getParent() != null)
      {
        MCH_Lib.DbgLog(this.worldObj, "MCH_EntitySeat.onUpdate:SeatID=%d", new Object[] { Integer.valueOf(this.seatID), this.lastRiddenByEntity.toString() });
        getParent().onUnmountPlayerSeat(this, this.lastRiddenByEntity);
      }
    }
    

    if (this.worldObj.isRemote)
    {
      onUpdate_Client();

    }
    else
    {
      onUpdate_Server();
    }
    
    this.lastRiddenByEntity = this.riddenByEntity;
  }
  


  private void onUpdate_Client()
  {
    checkDetachmentAndDelete();
  }
  


  private void onUpdate_Server()
  {
    checkDetachmentAndDelete();
    
    if ((this.riddenByEntity != null) && (this.riddenByEntity.isDead))
    {
      this.riddenByEntity = null;
    }
  }
  

  public void updateRiderPosition()
  {
    updatePosition();
  }
  
  public void updatePosition() {
    Entity ridEnt = this.riddenByEntity;
    if (ridEnt != null)
    {
      ridEnt.setPosition(this.posX, this.posY, this.posZ);
      ridEnt.motionX = (ridEnt.motionY = ridEnt.motionZ = 0.0D);
    }
  }
  
  public void updateRotation(float yaw, float pitch) {
    Entity ridEnt = this.riddenByEntity;
    if (ridEnt != null)
    {
      ridEnt.rotationYaw = yaw;
      ridEnt.rotationPitch = pitch;
    }
  }
  



  protected void checkDetachmentAndDelete()
  {
    if ((!this.isDead) && ((this.seatID < 0) || (getParent() == null) || (getParent().isDead)))
    {

      if ((getParent() != null) && (getParent().isDead)) { this.parentSearchCount = 100000000;
      }
      
      if (this.parentSearchCount >= 1200)
      {
        setDead();
        if (!this.worldObj.isRemote)
        {
          if (this.riddenByEntity != null) this.riddenByEntity.mountEntity(null);
        }
        setParent(null);
        
        MCH_Lib.DbgLog(this.worldObj, "[Error]?????????????????????? seat=%d, parentUniqueID=%s", new Object[] { Integer.valueOf(this.seatID), this.parentUniqueID });
      }
      else
      {
        this.parentSearchCount += 1;
      }
    }
    else
    {
      this.parentSearchCount = 0;
    }
  }
  




  protected void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
  {
    par1NBTTagCompound.setInteger("SeatID", this.seatID);
    par1NBTTagCompound.setString("ParentUniqueID", this.parentUniqueID);
  }
  



  protected void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
  {
    this.seatID = par1NBTTagCompound.getInteger("SeatID");
    this.parentUniqueID = par1NBTTagCompound.getString("ParentUniqueID");
  }
  
  @SideOnly(Side.CLIENT)
  public float getShadowSize()
  {
    return 0.0F;
  }
  
  public boolean canRideMob(Entity entity)
  {
    if ((getParent() == null) || (this.seatID < 0)) { return false;
    }
    if ((getParent().getSeatInfo(this.seatID + 1) instanceof MCH_SeatRackInfo))
    {
      return false;
    }
    
    return true;
  }
  
  public boolean isGunnerMode()
  {
    if (this.riddenByEntity != null)
    {
      if (getParent() != null)
      {
        return getParent().getIsGunnerMode(this.riddenByEntity);
      }
    }
    return false;
  }
  



  public boolean interactFirst(EntityPlayer player)
  {
    if ((getParent() == null) || (getParent().isDestroyed()))
    {
      return false;
    }
    
    if (!getParent().checkTeam(player))
    {
      return false;
    }
    
    ItemStack itemStack = player.getCurrentEquippedItem();
    if ((itemStack != null) && ((itemStack.getItem() instanceof MCH_ItemWrench)))
    {
      return getParent().interactFirst(player);
    }
    if ((itemStack != null) && ((itemStack.getItem() instanceof MCH_ItemSpawnGunner)))
    {
      return getParent().interactFirst(player);
    }
    
    if (this.riddenByEntity != null)
    {


      return false;
    }
    
    if (player.ridingEntity != null)
    {

      return false;
    }
    
    if (!canRideMob(player))
    {
      return false;
    }
    
    player.mountEntity(this);
    
    return true;
  }
  
  public MCH_EntityAircraft getParent() {
    return this.parent;
  }
  
  public void setParent(MCH_EntityAircraft parent) {
    this.parent = parent;
    if (this.riddenByEntity != null)
    {
      MCH_Lib.DbgLog(this.worldObj, "MCH_EntitySeat.setParent:SeatID=%d %s : " + getParent(), new Object[] { Integer.valueOf(this.seatID), this.riddenByEntity.toString() });
      
      if (getParent() != null)
      {
        getParent().onMountPlayerSeat(this, this.riddenByEntity);
      }
    }
  }
}
