package mcheli.weapon;

import java.util.Random;
import mcheli.wrapper.W_Entity;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class MCH_EntityA10 extends W_Entity
{
  public static final int DATAWT_NAME = 29;
  public final int DESPAWN_COUNT = 70;
  public int despawnCount = 0;
  public Entity shootingAircraft;
  public Entity shootingEntity;
  public int shotCount = 0;
  public int direction = 0;
  
  public int power;
  public float acceleration;
  public int explosionPower;
  public boolean isFlaming;
  public String name;
  public MCH_WeaponInfo weaponInfo;
  static int snd_num = 0;
  
  public MCH_EntityA10(World world)
  {
    super(world);
    
    this.ignoreFrustumCheck = true;
    this.preventEntitySpawning = false;
    setSize(5.0F, 3.0F);
    this.yOffset = (this.height / 2.0F);
    this.motionX = 0.0D;
    this.motionY = 0.0D;
    this.motionZ = 0.0D;
    this.power = 32;
    this.acceleration = 4.0F;
    this.explosionPower = 1;
    this.isFlaming = false;
    this.shootingEntity = null;
    this.shootingAircraft = null;
    this.isImmuneToFire = true;
    
    this.renderDistanceWeight *= 10.0D;
  }
  
  public MCH_EntityA10(World world, double x, double y, double z) {
    this(world);
    this.lastTickPosX = (this.prevPosX = this.posX = x);
    this.lastTickPosY = (this.prevPosY = this.posY = y);
    this.lastTickPosZ = (this.prevPosZ = this.posZ = z);
  }
  
  protected boolean canTriggerWalking() { return false; }
  
  protected void entityInit()
  {
    getDataWatcher().addObject(29, String.valueOf(""));
  }
  
  public void setWeaponName(String s)
  {
    if ((s != null) && (!s.isEmpty()))
    {
      this.weaponInfo = MCH_WeaponInfoManager.get(s);
      if ((this.weaponInfo != null) && (!this.worldObj.isRemote))
      {
        getDataWatcher().updateObject(29, String.valueOf(s));
      }
    }
  }
  
  public String getWeaponName() {
    return getDataWatcher().getWatchableObjectString(29);
  }
  
  public MCH_WeaponInfo getInfo() {
    return this.weaponInfo;
  }
  


  public AxisAlignedBB getCollisionBox(Entity par1Entity)
  {
    return par1Entity.boundingBox;
  }
  
  public AxisAlignedBB getBoundingBox() { return this.boundingBox; }
  
  public boolean canBePushed() {
    return false;
  }
  
  public boolean attackEntityFrom(DamageSource par1DamageSource, float par2) { return false; }
  
  public boolean canBeCollidedWith() {
    return false;
  }
  
  public void setDead()
  {
    super.setDead();
  }
  


  public void onUpdate()
  {
    super.onUpdate();
    
    if (!this.isDead)
    {
      this.despawnCount += 1;
    }
    
    if (this.weaponInfo == null)
    {
      setWeaponName(getWeaponName());
      if (this.weaponInfo == null)
      {
        setDead();
        return;
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
    
    if (!this.isDead)
    {
      if (this.despawnCount <= 20)
      {

        this.motionY = -0.3D;
      }
      else
      {
        setPosition(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        this.motionY += 0.02D;
      }
    }
  }
  
  public boolean isRender()
  {
    return this.despawnCount > 20;
  }
  

  private void onUpdate_Client()
  {
    this.shotCount += 4;
  }
  

  private void onUpdate_Server()
  {
    if (!this.isDead)
    {
      if (this.despawnCount > 70)
      {
        setDead();


      }
      else if ((this.despawnCount > 0) && (this.shotCount < 40))
      {
        for (int i = 0; i < 2; i++)
        {
          shotGAU8(true, this.shotCount);
          this.shotCount += 1;
        }
        if (this.shotCount == 38)
        {
          mcheli.wrapper.W_WorldFunc.MOD_playSoundEffect(this.worldObj, this.posX, this.posY, this.posZ, "gau-8_snd", 150.0F, 1.0F);
        }
      }
    }
  }
  


  protected void shotGAU8(boolean playSound, int cnt)
  {
    float yaw = 90 * this.direction;
    float pitch = 30.0F;
    double x = this.posX;
    double y = this.posY;
    double z = this.posZ;
    double tX = this.rand.nextDouble() - 0.5D;
    double tY = -2.6D;
    double tZ = this.rand.nextDouble() - 0.5D;
    if (this.direction == 0) { tZ += 10.0D;z += cnt * 0.6D; }
    if (this.direction == 1) { tX -= 10.0D;x -= cnt * 0.6D; }
    if (this.direction == 2) { tZ -= 10.0D;z -= cnt * 0.6D; }
    if (this.direction == 3) { tX += 10.0D;x += cnt * 0.6D; }
    double dist = net.minecraft.util.MathHelper.sqrt_double(tX * tX + tY * tY + tZ * tZ);
    tX = tX * 4.0D / dist;
    tY = tY * 4.0D / dist;
    tZ = tZ * 4.0D / dist;
    
    MCH_EntityBullet e = new MCH_EntityBullet(this.worldObj, x, y, z, tX, tY, tZ, yaw, pitch, this.acceleration);
    e.setName(getWeaponName());
    e.explosionPower = (this.shotCount % 4 == 0 ? this.explosionPower : 0);
    e.setPower(this.power);
    e.shootingEntity = this.shootingEntity;
    e.shootingAircraft = this.shootingAircraft;
    
    this.worldObj.spawnEntityInWorld(e);
  }
  



  protected void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
  {
    par1NBTTagCompound.setString("WeaponName", getWeaponName());
  }
  



  protected void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
  {
    this.despawnCount = 200;
    if (par1NBTTagCompound.hasKey("WeaponName"))
    {
      setWeaponName(par1NBTTagCompound.getString("WeaponName")); }
  }
  
  @cpw.mods.fml.relauncher.SideOnly(cpw.mods.fml.relauncher.Side.CLIENT)
  public float getShadowSize() {
    return 10.0F;
  }
}
