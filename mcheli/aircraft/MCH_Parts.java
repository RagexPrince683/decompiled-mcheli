package mcheli.aircraft;

import net.minecraft.entity.Entity;

public class MCH_Parts { public final Entity parent;
  public final net.minecraft.entity.DataWatcher dataWatcher;
  public final int shift;
  public final int dataIndex;
  public final String partName;
  
  public class Sound { public Sound() {}
    public String name = "";
    public float volume = 1.0F;
    public float pitch = 1.0F;
    
    public void setPrm(String n, float v, float p) {
      this.name = n;
      this.volume = v;
      this.pitch = p;
    }
  }
  






  public float prevRotation = 0.0F;
  public float rotation = 0.0F;
  public float rotationMax = 90.0F;
  public float rotationInv = 1.0F;
  
  public Sound soundStartSwichOn = new Sound();
  public Sound soundEndSwichOn = new Sound();
  public Sound soundSwitching = new Sound();
  public Sound soundStartSwichOff = new Sound();
  public Sound soundEndSwichOff = new Sound();
  
  private boolean status = false;
  
  public MCH_Parts(Entity parent, int shiftBit, int dataWatcherIndex, String name)
  {
    this.parent = parent;
    this.dataWatcher = parent.getDataWatcher();
    this.shift = shiftBit;
    this.dataIndex = dataWatcherIndex;
    this.status = ((getDataWatcherValue() & 1 << this.shift) != 0);
    this.partName = name;
  }
  
  public int getDataWatcherValue()
  {
    return this.dataWatcher.getWatchableObjectInt(this.dataIndex);
  }
  
  public void setStatusServer(boolean stat)
  {
    setStatusServer(stat, true);
  }
  
  public void setStatusServer(boolean stat, boolean playSound) {
    if (!this.parent.worldObj.isRemote)
    {
      if (getStatus() != stat)
      {
        mcheli.MCH_Lib.DbgLog(false, "setStatusServer(ID=%d %s :%s -> %s)", new Object[] { Integer.valueOf(this.shift), this.partName, getStatus() ? "ON" : "OFF", stat ? "ON" : "OFF" });
        

        updateDataWatcher(stat);
        playSound(this.soundSwitching);
        if (!stat)
        {
          playSound(this.soundStartSwichOff);
        }
        else
        {
          playSound(this.soundStartSwichOn);
        }
        update();
      }
    }
  }
  
  protected void updateDataWatcher(boolean stat)
  {
    int currentStatus = this.dataWatcher.getWatchableObjectInt(this.dataIndex);
    int mask = 1 << this.shift;
    if (!stat)
    {
      this.dataWatcher.updateObject(this.dataIndex, Integer.valueOf(currentStatus & (mask ^ 0xFFFFFFFF)));
    }
    else
    {
      this.dataWatcher.updateObject(this.dataIndex, Integer.valueOf(currentStatus | mask));
    }
    this.status = stat;
  }
  
  public boolean getStatus()
  {
    return this.status;
  }
  


  public boolean isOFF()
  {
    return (!this.status) && (this.rotation <= 0.02F);
  }
  
  public boolean isON() {
    return (this.status == true) && (this.rotation >= this.rotationMax - 0.02F);
  }
  
  public void updateStatusClient(int statFromDataWatcher)
  {
    if (this.parent.worldObj.isRemote)
    {
      this.status = ((statFromDataWatcher & 1 << this.shift) != 0);
    }
  }
  
  public void update()
  {
    this.prevRotation = this.rotation;
    
    if (getStatus())
    {
      if (this.rotation < this.rotationMax)
      {
        this.rotation += this.rotationInv;
        if (this.rotation >= this.rotationMax)
        {
          playSound(this.soundEndSwichOn);
        }
        
      }
      
    }
    else if (this.rotation > 0.0F)
    {
      this.rotation -= this.rotationInv;
      if (this.rotation <= 0.0F)
      {
        playSound(this.soundEndSwichOff);
      }
    }
  }
  


  public void forceSwitch(boolean onoff)
  {
    updateDataWatcher(onoff);
    this.rotation = (this.prevRotation = this.rotationMax);
  }
  
  public float getFactor()
  {
    if (this.rotationMax > 0.0F)
    {
      return this.rotation / this.rotationMax;
    }
    return 0.0F;
  }
  
  public void playSound(Sound snd)
  {
    if ((!snd.name.isEmpty()) && (!this.parent.worldObj.isRemote))
    {
      mcheli.wrapper.W_WorldFunc.MOD_playSoundAtEntity(this.parent, snd.name, snd.volume, snd.pitch);
    }
  }
}
