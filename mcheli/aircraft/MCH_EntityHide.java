package mcheli.aircraft;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.List;
import mcheli.wrapper.W_Entity;
import mcheli.wrapper.W_Lib;
import mcheli.wrapper.W_WorldFunc;
import net.minecraft.block.Block;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;








public class MCH_EntityHide
  extends W_Entity
{
  private MCH_EntityAircraft ac;
  private Entity user;
  private int paraPosRotInc;
  private double paraX;
  private double paraY;
  private double paraZ;
  private double paraYaw;
  private double paraPitch;
  @SideOnly(Side.CLIENT)
  private double velocityX;
  @SideOnly(Side.CLIENT)
  private double velocityY;
  @SideOnly(Side.CLIENT)
  private double velocityZ;
  
  public MCH_EntityHide(World par1World)
  {
    super(par1World);
    
    setSize(1.0F, 1.0F);
    
    this.preventEntitySpawning = true;
    this.yOffset = (this.height / 2.0F);
    this.user = null;
    this.motionX = (this.motionY = this.motionZ = 0.0D);
  }
  
  public MCH_EntityHide(World par1World, double x, double y, double z) {
    this(par1World);
    this.posX = x;
    this.posY = y;
    this.posZ = z;
  }
  
  protected void entityInit()
  {
    super.entityInit();
    createRopeIndex(-1);
    getDataWatcher().addObject(31, new Integer(0));
  }
  
  public void setParent(MCH_EntityAircraft ac, Entity user, int ropeIdx)
  {
    this.ac = ac;
    setRopeIndex(ropeIdx);
    this.user = user;
  }
  
  protected boolean canTriggerWalking() { return false; }
  public AxisAlignedBB getCollisionBox(Entity par1Entity) { return par1Entity.boundingBox; }
  public AxisAlignedBB getBoundingBox() { return this.boundingBox; }
  public boolean canBePushed() { return true; }
  public double getMountedYOffset() { return this.height * 0.0D - 0.3D; }
  public boolean attackEntityFrom(DamageSource par1DamageSource, float par2) { return false; }
  public boolean canBeCollidedWith() { return !this.isDead; }
  protected void writeEntityToNBT(NBTTagCompound nbt) {}
  protected void readEntityFromNBT(NBTTagCompound nbt) {}
  @SideOnly(Side.CLIENT)
  public float getShadowSize() { return 0.0F; }
  public boolean interactFirst(EntityPlayer par1EntityPlayer) { return false; }
  
  public void createRopeIndex(int defaultValue)
  {
    getDataWatcher().addObject(30, new Integer(defaultValue));
  }
  
  public int getRopeIndex() {
    return getDataWatcher().getWatchableObjectInt(30);
  }
  
  public void setRopeIndex(int value) {
    getDataWatcher().updateObject(30, new Integer(value));
  }
  
  @SideOnly(Side.CLIENT)
  public void setPositionAndRotation2(double par1, double par3, double par5, float par7, float par8, int par9)
  {
    this.paraPosRotInc = (par9 + 10);
    
    this.paraX = par1;
    this.paraY = par3;
    this.paraZ = par5;
    this.paraYaw = par7;
    this.paraPitch = par8;
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
  
  public void setDead()
  {
    super.setDead();
  }
  
  public void onUpdate()
  {
    super.onUpdate();
    


    if ((this.user != null) && (!this.worldObj.isRemote))
    {
      if (this.ac != null)
      {
        getDataWatcher().updateObject(31, new Integer(this.ac.getEntityId()));
      }
      this.user.mountEntity(this);
      this.user = null;
    }
    
    if ((this.ac == null) && (this.worldObj.isRemote))
    {
      int id = getDataWatcher().getWatchableObjectInt(31);
      if (id > 0)
      {
        Entity entity = this.worldObj.getEntityByID(id);
        if ((entity instanceof MCH_EntityAircraft))
        {
          this.ac = ((MCH_EntityAircraft)entity);
        }
      }
    }
    
    this.prevPosX = this.posX;
    this.prevPosY = this.posY;
    this.prevPosZ = this.posZ;
    
    this.fallDistance = 0.0F;
    if (this.riddenByEntity != null)
    {
      this.riddenByEntity.fallDistance = 0.0F;
    }
    
    if (this.ac != null)
    {
      if (!this.ac.isRepelling())
      {
        setDead();
      }
      int id = getRopeIndex();
      if (id >= 0)
      {
        Vec3 v = this.ac.getRopePos(id);
        this.posX = v.xCoord;
        this.posZ = v.zCoord;
      }
    }
    
    setPosition(this.posX, this.posY, this.posZ);
    
    if (this.worldObj.isRemote)
    {
      onUpdateClient();
    }
    else
    {
      onUpdateServer();
    }
  }
  
  public void onUpdateClient()
  {
    if (this.paraPosRotInc > 0)
    {
      double x = this.posX + (this.paraX - this.posX) / this.paraPosRotInc;
      double y = this.posY + (this.paraY - this.posY) / this.paraPosRotInc;
      double z = this.posZ + (this.paraZ - this.posZ) / this.paraPosRotInc;
      double yaw = MathHelper.wrapAngleTo180_double(this.paraYaw - this.rotationYaw);
      this.rotationYaw = ((float)(this.rotationYaw + yaw / this.paraPosRotInc));
      this.rotationPitch = ((float)(this.rotationPitch + (this.paraPitch - this.rotationPitch) / this.paraPosRotInc));
      
      this.paraPosRotInc -= 1;
      
      setPosition(x, y, z);
      setRotation(this.rotationYaw, this.rotationPitch);
      
      if (this.riddenByEntity != null)
      {
        setRotation(this.riddenByEntity.prevRotationYaw, this.rotationPitch);
      }
    }
    else
    {
      setPosition(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
      
      this.motionX *= 0.99D;
      this.motionY *= 0.95D;
      this.motionZ *= 0.99D;
    }
  }
  
  public void onUpdateServer()
  {
    this.motionY -= (this.onGround ? 0.01D : 0.03D);
    
    if (this.onGround)
    {
      onGroundAndDead();
      return;
    }
    
    moveEntity(this.motionX, this.motionY, this.motionZ);
    
    this.motionY *= 0.9D;
    this.motionX *= 0.95D;
    this.motionZ *= 0.95D;
    
    int id = getRopeIndex();
    if ((this.ac != null) && (id >= 0))
    {
      Vec3 v = this.ac.getRopePos(id);
      if (Math.abs(this.posY - v.yCoord) > Math.abs(this.ac.ropesLength) + 5.0F)
      {
        onGroundAndDead();
      }
    }
    
    if ((this.riddenByEntity != null) && (this.riddenByEntity.isDead))
    {
      this.riddenByEntity = null;
      setDead();
    }
  }
  

  public List getCollidingBoundingBoxes(Entity par1Entity, AxisAlignedBB par2AxisAlignedBB)
  {
    ArrayList collidingBoundingBoxes = new ArrayList();
    collidingBoundingBoxes.clear();
    int i = MathHelper.floor_double(par2AxisAlignedBB.minX);
    int j = MathHelper.floor_double(par2AxisAlignedBB.maxX + 1.0D);
    int k = MathHelper.floor_double(par2AxisAlignedBB.minY);
    int l = MathHelper.floor_double(par2AxisAlignedBB.maxY + 1.0D);
    int i1 = MathHelper.floor_double(par2AxisAlignedBB.minZ);
    int j1 = MathHelper.floor_double(par2AxisAlignedBB.maxZ + 1.0D);
    
    for (int k1 = i; k1 < j; k1++)
    {
      for (int l1 = i1; l1 < j1; l1++)
      {
        if (this.worldObj.blockExists(k1, 64, l1))
        {
          for (int i2 = k - 1; i2 < l; i2++)
          {
            Block block = W_WorldFunc.getBlock(this.worldObj, k1, i2, l1);
            
            if (block != null)
            {
              block.addCollisionBoxesToList(this.worldObj, k1, i2, l1, par2AxisAlignedBB, collidingBoundingBoxes, par1Entity);
            }
          }
        }
      }
    }
    
    double d0 = 0.25D;
    List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(par1Entity, par2AxisAlignedBB.expand(d0, d0, d0));
    
    for (int j2 = 0; j2 < list.size(); j2++)
    {
      Entity entity = (Entity)list.get(j2);
      
      if ((!W_Lib.isEntityLivingBase(entity)) && 
        (!(entity instanceof MCH_EntitySeat)) && 
        (!(entity instanceof MCH_EntityHitBox)))
      {
        AxisAlignedBB axisalignedbb1 = entity.getBoundingBox();
        
        if ((axisalignedbb1 != null) && (axisalignedbb1.intersectsWith(par2AxisAlignedBB)))
        {
          collidingBoundingBoxes.add(axisalignedbb1);
        }
        
        axisalignedbb1 = par1Entity.getCollisionBox(entity);
        
        if ((axisalignedbb1 != null) && (axisalignedbb1.intersectsWith(par2AxisAlignedBB)))
        {
          collidingBoundingBoxes.add(axisalignedbb1);
        }
      }
    }
    return collidingBoundingBoxes;
  }
  
  public void moveEntity(double par1, double par3, double par5) {
    this.worldObj.theProfiler.startSection("move");
    this.ySize *= 0.4F;
    double d3 = this.posX;
    double d4 = this.posY;
    double d5 = this.posZ;
    
    double d6 = par1;
    double d7 = par3;
    double d8 = par5;
    AxisAlignedBB axisalignedbb = this.boundingBox.copy();
    

    List list = getCollidingBoundingBoxes(this, this.boundingBox.addCoord(par1, par3, par5));
    
    for (int i = 0; i < list.size(); i++)
    {
      par3 = ((AxisAlignedBB)list.get(i)).calculateYOffset(this.boundingBox, par3);
    }
    
    this.boundingBox.offset(0.0D, par3, 0.0D);
    
    if ((!this.field_70135_K) && (d7 != par3))
    {
      par5 = 0.0D;
      par3 = 0.0D;
      par1 = 0.0D;
    }
    
    boolean flag1 = (this.onGround) || ((d7 != par3) && (d7 < 0.0D));
    

    for (int j = 0; j < list.size(); j++)
    {
      par1 = ((AxisAlignedBB)list.get(j)).calculateXOffset(this.boundingBox, par1);
    }
    
    this.boundingBox.offset(par1, 0.0D, 0.0D);
    
    if ((!this.field_70135_K) && (d6 != par1))
    {
      par5 = 0.0D;
      par3 = 0.0D;
      par1 = 0.0D;
    }
    
    for (int j = 0; j < list.size(); j++)
    {
      par5 = ((AxisAlignedBB)list.get(j)).calculateZOffset(this.boundingBox, par5);
    }
    
    this.boundingBox.offset(0.0D, 0.0D, par5);
    
    if ((!this.field_70135_K) && (d8 != par5))
    {
      par5 = 0.0D;
      par3 = 0.0D;
      par1 = 0.0D;
    }
    





    if ((this.stepHeight > 0.0F) && (flag1) && (this.ySize < 0.05F) && ((d6 != par1) || (d8 != par5)))
    {
      double d12 = par1;
      double d10 = par3;
      double d11 = par5;
      par1 = d6;
      par3 = this.stepHeight;
      par5 = d8;
      AxisAlignedBB axisalignedbb1 = this.boundingBox.copy();
      this.boundingBox.setBB(axisalignedbb);
      
      list = getCollidingBoundingBoxes(this, this.boundingBox.addCoord(d6, par3, d8));
      
      for (int k = 0; k < list.size(); k++)
      {
        par3 = ((AxisAlignedBB)list.get(k)).calculateYOffset(this.boundingBox, par3);
      }
      
      this.boundingBox.offset(0.0D, par3, 0.0D);
      
      if ((!this.field_70135_K) && (d7 != par3))
      {
        par5 = 0.0D;
        par3 = 0.0D;
        par1 = 0.0D;
      }
      
      for (int k = 0; k < list.size(); k++)
      {
        par1 = ((AxisAlignedBB)list.get(k)).calculateXOffset(this.boundingBox, par1);
      }
      
      this.boundingBox.offset(par1, 0.0D, 0.0D);
      
      if ((!this.field_70135_K) && (d6 != par1))
      {
        par5 = 0.0D;
        par3 = 0.0D;
        par1 = 0.0D;
      }
      
      for (int k = 0; k < list.size(); k++)
      {
        par5 = ((AxisAlignedBB)list.get(k)).calculateZOffset(this.boundingBox, par5);
      }
      
      this.boundingBox.offset(0.0D, 0.0D, par5);
      
      if ((!this.field_70135_K) && (d8 != par5))
      {
        par5 = 0.0D;
        par3 = 0.0D;
        par1 = 0.0D;
      }
      
      if ((!this.field_70135_K) && (d7 != par3))
      {
        par5 = 0.0D;
        par3 = 0.0D;
        par1 = 0.0D;
      }
      else
      {
        par3 = -this.stepHeight;
        
        for (int k = 0; k < list.size(); k++)
        {
          par3 = ((AxisAlignedBB)list.get(k)).calculateYOffset(this.boundingBox, par3);
        }
        
        this.boundingBox.offset(0.0D, par3, 0.0D);
      }
      
      if (d12 * d12 + d11 * d11 >= par1 * par1 + par5 * par5)
      {
        par1 = d12;
        par3 = d10;
        par5 = d11;
        this.boundingBox.setBB(axisalignedbb1);
      }
    }
    
    this.worldObj.theProfiler.endSection();
    this.worldObj.theProfiler.startSection("rest");
    this.posX = ((this.boundingBox.minX + this.boundingBox.maxX) / 2.0D);
    this.posY = (this.boundingBox.minY + this.yOffset - this.ySize);
    this.posZ = ((this.boundingBox.minZ + this.boundingBox.maxZ) / 2.0D);
    this.isCollidedHorizontally = ((d6 != par1) || (d8 != par5));
    this.isCollidedVertically = (d7 != par3);
    this.onGround = ((d7 != par3) && (d7 < 0.0D));
    this.isCollided = ((this.isCollidedHorizontally) || (this.isCollidedVertically));
    updateFallState(par3, this.onGround);
    
    if (d6 != par1)
    {
      this.motionX = 0.0D;
    }
    
    if (d7 != par3)
    {
      this.motionY = 0.0D;
    }
    
    if (d8 != par5)
    {
      this.motionZ = 0.0D;
    }
    
    double d12 = this.posX - d3;
    double d10 = this.posY - d4;
    double d11 = this.posZ - d5;
    
    try
    {
      doBlockCollisions();
    }
    catch (Throwable throwable)
    {
      CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Checking entity tile collision");
      CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being checked for collision");
      addEntityCrashInfo(crashreportcategory);
      throw new ReportedException(crashreport);
    }
    
    this.worldObj.theProfiler.endSection();
  }
  
  public void onGroundAndDead()
  {
    this.posY += 0.5D;
    updateRiderPosition();
    setDead();
  }
  
  public void _updateRiderPosition() {
    if (this.riddenByEntity != null)
    {
      double x = -Math.sin(this.rotationYaw * 3.141592653589793D / 180.0D) * 0.1D;
      double z = Math.cos(this.rotationYaw * 3.141592653589793D / 180.0D) * 0.1D;
      this.riddenByEntity.setPosition(this.posX + x, this.posY + getMountedYOffset() + this.riddenByEntity.getYOffset(), this.posZ + z);
    }
  }
}
