package mcheli;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventBus;
import mcheli.aircraft.MCH_EntityAircraft;
import mcheli.aircraft.MCH_SoundUpdater;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IChatComponent;






public class MCH_CommonProxy
{
  public MCH_CommonProxy() {}
  
  public String getDataDir() { return MinecraftServer.getServer().getFolderName(); }
  
  public void registerRenderer() {}
  
  public void registerBlockRenderer() {}
  
  public void registerModels() {}
  
  public void registerModelsHeli(String name, boolean reload) {}
  public void registerModelsPlane(String name, boolean reload) {}
  public void registerModelsVehicle(String name, boolean reload) {}
  public void registerModelsTank(String name, boolean reload) {}
  public void registerClientTick() {}
  public void registerServerTick() { FMLCommonHandler.instance().bus().register(new MCH_ServerTickHandler()); }
  
  public boolean isRemote() { return false; }
  public String side() { return "Server"; }
  public MCH_SoundUpdater CreateSoundUpdater(MCH_EntityAircraft aircraft) { return null; }
  

  public MCH_Config config = null;
  public String lastConfigFileName;
  
  public void registerSounds() {}
  
  public void loadConfig(String fileName) { this.lastConfigFileName = fileName;
    
    this.config = new MCH_Config("./", fileName);
    
    this.config.load();
    this.config.write();
  }
  
  public void reconfig() {
    MCH_Lib.DbgLog(false, "MCH_CommonProxy.reconfig()", new Object[0]);
    loadConfig(this.lastConfigFileName);
  }
  
  public void save() {
    MCH_Lib.DbgLog(false, "MCH_CommonProxy.save()", new Object[0]);
    this.config.write();
  }
  
  public void loadHUD(String path) {}
  
  public void reloadHUD() {}
  
  public Entity getClientPlayer() {
    return null;
  }
  
  public void setCreativeDigDelay(int n) {}
  
  public void init() {}
  
  public boolean isFirstPerson() { return false; }
  
  public int getNewRenderType() { return -1; }
  
  public boolean isSinglePlayer() { return MinecraftServer.getServer().isSinglePlayer(); }
  
  public void readClientModList() {}
  
  public void printChatMessage(IChatComponent chat, int showTime, int pos) {}
  
  public void hitBullet() {}
  
  public void clientLocked() {}
}
