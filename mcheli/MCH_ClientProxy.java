package mcheli;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import mcheli.aircraft.MCH_AircraftInfo;
import mcheli.aircraft.MCH_EntityAircraft;
import mcheli.aircraft.MCH_EntityHide;
import mcheli.aircraft.MCH_EntitySeat;
import mcheli.aircraft.MCH_SoundUpdater;
import mcheli.block.MCH_DraftingTableRenderer;
import mcheli.command.MCH_GuiTitle;
import mcheli.container.MCH_EntityContainer;
import mcheli.debug.MCH_RenderTest;
import mcheli.gltd.MCH_ItemGLTDRender;
import mcheli.gltd.MCH_RenderGLTD;
import mcheli.helicopter.MCH_EntityHeli;
import mcheli.helicopter.MCH_HeliInfo;
import mcheli.helicopter.MCH_HeliInfoManager;
import mcheli.helicopter.MCH_RenderHeli;
import mcheli.hud.MCH_HudManager;
import mcheli.lweapon.MCH_ItemLightWeaponRender;
import mcheli.mob.MCH_EntityGunner;
import mcheli.mob.MCH_RenderGunner;
import mcheli.multiplay.MCH_MultiplayClient;
import mcheli.parachute.MCH_EntityParachute;
import mcheli.parachute.MCH_RenderParachute;
import mcheli.particles.MCH_ParticlesUtil;
import mcheli.plane.MCP_EntityPlane;
import mcheli.plane.MCP_PlaneInfo;
import mcheli.plane.MCP_PlaneInfoManager;
import mcheli.tank.MCH_RenderTank;
import mcheli.tank.MCH_TankInfo;
import mcheli.tank.MCH_TankInfoManager;
import mcheli.throwable.MCH_EntityThrowable;
import mcheli.throwable.MCH_RenderThrowable;
import mcheli.throwable.MCH_ThrowableInfo;
import mcheli.tool.MCH_ItemRenderWrench;
import mcheli.uav.MCH_EntityUavStation;
import mcheli.uav.MCH_RenderUavStation;
import mcheli.vehicle.MCH_EntityVehicle;
import mcheli.vehicle.MCH_VehicleInfo;
import mcheli.vehicle.MCH_VehicleInfoManager;
import mcheli.weapon.MCH_BulletModel;
import mcheli.weapon.MCH_EntityA10;
import mcheli.weapon.MCH_EntityAAMissile;
import mcheli.weapon.MCH_EntityASMissile;
import mcheli.weapon.MCH_EntityBullet;
import mcheli.weapon.MCH_EntityMarkerRocket;
import mcheli.weapon.MCH_EntityTorpedo;
import mcheli.weapon.MCH_EntityTvMissile;
import mcheli.weapon.MCH_RenderASMissile;
import mcheli.weapon.MCH_RenderBomb;
import mcheli.weapon.MCH_RenderBullet;
import mcheli.weapon.MCH_RenderNone;
import mcheli.weapon.MCH_RenderTvMissile;
import mcheli.weapon.MCH_WeaponInfo;
import mcheli.weapon.MCH_WeaponInfoManager;
import mcheli.wrapper.W_Item;
import mcheli.wrapper.W_McClient;
import mcheli.wrapper.W_MinecraftForgeClient;
import mcheli.wrapper.W_Reflection;
import mcheli.wrapper.W_TickRegistry;
import mcheli.wrapper.modelloader.W_ModelCustom;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.common.MinecraftForge;

public class MCH_ClientProxy extends MCH_CommonProxy
{
  public String lastLoadHUDPath = "";
  
  public MCH_ClientProxy() {}
  
  public String getDataDir() {
    return Minecraft.getMinecraft().mcDataDir.getPath();
  }
  


  public void registerRenderer()
  {
    RenderingRegistry.registerEntityRenderingHandler(MCH_EntitySeat.class, new MCH_RenderTest(0.0F, 0.0F, 0.0F, "seat"));
    RenderingRegistry.registerEntityRenderingHandler(MCH_EntityHeli.class, new MCH_RenderHeli());
    RenderingRegistry.registerEntityRenderingHandler(MCP_EntityPlane.class, new mcheli.plane.MCP_RenderPlane());
    RenderingRegistry.registerEntityRenderingHandler(mcheli.tank.MCH_EntityTank.class, new MCH_RenderTank());
    RenderingRegistry.registerEntityRenderingHandler(mcheli.gltd.MCH_EntityGLTD.class, new MCH_RenderGLTD());
    RenderingRegistry.registerEntityRenderingHandler(mcheli.chain.MCH_EntityChain.class, new mcheli.chain.MCH_RenderChain());
    RenderingRegistry.registerEntityRenderingHandler(MCH_EntityParachute.class, new MCH_RenderParachute());
    RenderingRegistry.registerEntityRenderingHandler(MCH_EntityContainer.class, new mcheli.container.MCH_RenderContainer());
    RenderingRegistry.registerEntityRenderingHandler(MCH_EntityVehicle.class, new mcheli.vehicle.MCH_RenderVehicle());
    RenderingRegistry.registerEntityRenderingHandler(MCH_EntityUavStation.class, new MCH_RenderUavStation());
    RenderingRegistry.registerEntityRenderingHandler(mcheli.weapon.MCH_EntityCartridge.class, new mcheli.weapon.MCH_RenderCartridge());
    RenderingRegistry.registerEntityRenderingHandler(MCH_EntityHide.class, new MCH_RenderNull());
    
    RenderingRegistry.registerEntityRenderingHandler(MCH_ViewEntityDummy.class, new MCH_RenderNull());
    
    RenderingRegistry.registerEntityRenderingHandler(mcheli.weapon.MCH_EntityRocket.class, new MCH_RenderBullet());
    RenderingRegistry.registerEntityRenderingHandler(MCH_EntityTvMissile.class, new MCH_RenderTvMissile());
    RenderingRegistry.registerEntityRenderingHandler(MCH_EntityBullet.class, new MCH_RenderBullet());
    RenderingRegistry.registerEntityRenderingHandler(MCH_EntityA10.class, new mcheli.weapon.MCH_RenderA10());
    RenderingRegistry.registerEntityRenderingHandler(MCH_EntityAAMissile.class, new mcheli.weapon.MCH_RenderAAMissile());
    RenderingRegistry.registerEntityRenderingHandler(MCH_EntityASMissile.class, new MCH_RenderASMissile());
    RenderingRegistry.registerEntityRenderingHandler(mcheli.weapon.MCH_EntityATMissile.class, new MCH_RenderTvMissile());
    RenderingRegistry.registerEntityRenderingHandler(MCH_EntityTorpedo.class, new MCH_RenderBullet());
    RenderingRegistry.registerEntityRenderingHandler(mcheli.weapon.MCH_EntityBomb.class, new MCH_RenderBomb());
    RenderingRegistry.registerEntityRenderingHandler(MCH_EntityMarkerRocket.class, new MCH_RenderBullet());
    RenderingRegistry.registerEntityRenderingHandler(mcheli.weapon.MCH_EntityDispensedItem.class, new MCH_RenderNone());
    
    RenderingRegistry.registerEntityRenderingHandler(mcheli.flare.MCH_EntityFlare.class, new mcheli.flare.MCH_RenderFlare());
    
    RenderingRegistry.registerEntityRenderingHandler(MCH_EntityThrowable.class, new MCH_RenderThrowable());
    
    RenderingRegistry.registerEntityRenderingHandler(MCH_EntityGunner.class, new MCH_RenderGunner());
    
    W_MinecraftForgeClient.registerItemRenderer(MCH_MOD.itemJavelin, new MCH_ItemLightWeaponRender());
    W_MinecraftForgeClient.registerItemRenderer(MCH_MOD.itemStinger, new MCH_ItemLightWeaponRender());
    W_MinecraftForgeClient.registerItemRenderer(MCH_MOD.invisibleItem, new MCH_InvisibleItemRender());
    
    W_MinecraftForgeClient.registerItemRenderer(MCH_MOD.itemGLTD, new MCH_ItemGLTDRender());
    W_MinecraftForgeClient.registerItemRenderer(MCH_MOD.itemWrench, new MCH_ItemRenderWrench());
    W_MinecraftForgeClient.registerItemRenderer(MCH_MOD.itemRangeFinder, new mcheli.tool.rangefinder.MCH_ItemRenderRangeFinder());
  }
  


  public void registerBlockRenderer()
  {
    ClientRegistry.bindTileEntitySpecialRenderer(mcheli.block.MCH_DraftingTableTileEntity.class, new MCH_DraftingTableRenderer());
    W_MinecraftForgeClient.registerItemRenderer(W_Item.getItemFromBlock(MCH_MOD.blockDraftingTable), new mcheli.block.MCH_DraftingTableItemRender());
  }
  

  public void registerModels()
  {
    MCH_ModelManager.setForceReloadMode(true);
    
    mcheli.aircraft.MCH_RenderAircraft.debugModel = MCH_ModelManager.load("box");
    
    MCH_ModelManager.load("a-10");
    
    MCH_RenderGLTD.model = MCH_ModelManager.load("gltd");
    
    MCH_ModelManager.load("chain");
    
    MCH_ModelManager.load("container");
    
    MCH_ModelManager.load("parachute1");
    MCH_ModelManager.load("parachute2");
    
    MCH_ModelManager.load("lweapons", "fim92");
    MCH_ModelManager.load("lweapons", "fgm148");
    
    for (String s : MCH_RenderUavStation.MODEL_NAME)
    {
      MCH_ModelManager.load(s);
    }
    
    MCH_ModelManager.load("wrench");
    
    MCH_ModelManager.load("rangefinder");
    

    MCH_HeliInfoManager.getInstance(); for (String key : MCH_HeliInfoManager.map.keySet())
    {
      registerModelsHeli(key, false);
    }
    

    for (String key : MCP_PlaneInfoManager.map.keySet())
    {
      registerModelsPlane(key, false);
    }
    

    MCH_TankInfoManager.getInstance(); for (String key : MCH_TankInfoManager.map.keySet())
    {
      registerModelsTank(key, false);
    }
    

    for (String key : MCH_VehicleInfoManager.map.keySet())
    {
      registerModelsVehicle(key, false);
    }
    

    registerModels_Bullet();
    
    mcheli.weapon.MCH_DefaultBulletModels.Bullet = loadBulletModel("bullet");
    mcheli.weapon.MCH_DefaultBulletModels.AAMissile = loadBulletModel("aamissile");
    mcheli.weapon.MCH_DefaultBulletModels.ATMissile = loadBulletModel("asmissile");
    mcheli.weapon.MCH_DefaultBulletModels.ASMissile = loadBulletModel("asmissile");
    mcheli.weapon.MCH_DefaultBulletModels.Bomb = loadBulletModel("bomb");
    mcheli.weapon.MCH_DefaultBulletModels.Rocket = loadBulletModel("rocket");
    mcheli.weapon.MCH_DefaultBulletModels.Torpedo = loadBulletModel("torpedo");
    

    for (MCH_ThrowableInfo wi : mcheli.throwable.MCH_ThrowableInfoManager.getValues())
    {
      wi.model = MCH_ModelManager.load("throwable", wi.name);
    }
    
    MCH_ModelManager.load("blocks", "drafting_table");
  }
  

  public static void registerModels_Bullet()
  {
    for (MCH_WeaponInfo wi : MCH_WeaponInfoManager.getValues())
    {
      IModelCustom m = null;
      if (!wi.bulletModelName.isEmpty())
      {
        m = MCH_ModelManager.load("bullets", wi.bulletModelName);
        if (m != null)
        {
          wi.bulletModel = new MCH_BulletModel(wi.bulletModelName, m);
        }
      }
      if (!wi.bombletModelName.isEmpty())
      {
        m = MCH_ModelManager.load("bullets", wi.bombletModelName);
        if (m != null)
        {
          wi.bombletModel = new MCH_BulletModel(wi.bombletModelName, m);
        }
      }
      if ((wi.cartridge != null) && (!wi.cartridge.name.isEmpty()))
      {
        wi.cartridge.model = MCH_ModelManager.load("bullets", wi.cartridge.name);
        if (wi.cartridge.model == null)
        {
          wi.cartridge = null;
        }
      }
    }
  }
  
  public void registerModelsHeli(String name, boolean reload)
  {
    MCH_ModelManager.setForceReloadMode(reload);
    
    MCH_HeliInfo info = (MCH_HeliInfo)MCH_HeliInfoManager.map.get(name);
    info.model = MCH_ModelManager.load("helicopters", info.name);
    
    for (MCH_HeliInfo.Rotor rotor : info.rotorList)
    {
      rotor.model = loadPartModel("helicopters", info.name, info.model, rotor.modelName);
      System.out.println(info.name);
      System.out.println(rotor.modelName);
      System.out.println(rotor.model);
    }
    
    registerCommonPart("helicopters", info);
    
    MCH_ModelManager.setForceReloadMode(false);
  }
  
  public void registerModelsPlane(String name, boolean reload) {
    MCH_ModelManager.setForceReloadMode(reload);
    
    MCP_PlaneInfo info = (MCP_PlaneInfo)MCP_PlaneInfoManager.map.get(name);
    info.model = MCH_ModelManager.load("planes", info.name);
    for (MCH_AircraftInfo.DrawnPart n : info.nozzles)
    {
      n.model = loadPartModel("planes", info.name, info.model, n.modelName);
    }
    for (MCP_PlaneInfo.Rotor r : info.rotorList)
    {
      r.model = loadPartModel("planes", info.name, info.model, r.modelName);
      for (MCP_PlaneInfo.Blade b : r.blades)
      {
        b.model = loadPartModel("planes", info.name, info.model, b.modelName);
      }
    }
    for (MCP_PlaneInfo.Wing w : info.wingList)
    {
      w.model = loadPartModel("planes", info.name, info.model, w.modelName);
      if (w.pylonList != null)
      {
        for (MCP_PlaneInfo.Pylon p : w.pylonList)
        {
          p.model = loadPartModel("planes", info.name, info.model, p.modelName);
        }
      }
    }
    
    registerCommonPart("planes", info);
    
    MCH_ModelManager.setForceReloadMode(false);
  }
  
  public void registerModelsVehicle(String name, boolean reload) {
    MCH_ModelManager.setForceReloadMode(reload);
    
    MCH_VehicleInfo info = (MCH_VehicleInfo)MCH_VehicleInfoManager.map.get(name);
    info.model = MCH_ModelManager.load("vehicles", info.name);
    
    for (MCH_VehicleInfo.VPart vp : info.partList)
    {
      vp.model = loadPartModel("vehicles", info.name, info.model, vp.modelName);
      if (vp.child != null)
      {
        registerVCPModels(info, vp);
      }
    }
    registerCommonPart("vehicles", info);
    
    MCH_ModelManager.setForceReloadMode(false);
  }
  
  public void registerModelsTank(String name, boolean reload) {
    MCH_ModelManager.setForceReloadMode(reload);
    
    MCH_TankInfo info = (MCH_TankInfo)MCH_TankInfoManager.map.get(name);
    info.model = MCH_ModelManager.load("tanks", info.name);
    
    registerCommonPart("tanks", info);
    
    MCH_ModelManager.setForceReloadMode(false);
  }
  
  private MCH_BulletModel loadBulletModel(String name)
  {
    IModelCustom m = MCH_ModelManager.load("bullets", name);
    return m != null ? new MCH_BulletModel(name, m) : null;
  }
  
  private IModelCustom loadPartModel(String path, String name, IModelCustom body, String part)
  {
    if ((body instanceof W_ModelCustom))
    {
      if (((W_ModelCustom)body).containsPart("$" + part))
      {
    	  
        return null;
      }
    }
    IModelCustom test = MCH_ModelManager.load(path, name);
    return test;
  }
  
  private void registerCommonPart(String path, MCH_AircraftInfo info)
  {
    for (MCH_AircraftInfo.Hatch h : info.hatchList)
    {
      h.model = loadPartModel(path, info.name, info.model, h.modelName);
    }
    for (MCH_AircraftInfo.Camera c : info.cameraList)
    {
      c.model = loadPartModel(path, info.name, info.model, c.modelName);
    }
    for (MCH_AircraftInfo.Throttle c : info.partThrottle)
    {
      c.model = loadPartModel(path, info.name, info.model, c.modelName);
    }
    for (MCH_AircraftInfo.RotPart c : info.partRotPart)
    {
      c.model = loadPartModel(path, info.name, info.model, c.modelName);
    }
    for (MCH_AircraftInfo.PartWeapon p : info.partWeapon)
    {
      p.model = loadPartModel(path, info.name, info.model, p.modelName);
      for (MCH_AircraftInfo.PartWeaponChild wc : p.child)
      {
        wc.model = loadPartModel(path, info.name, info.model, wc.modelName);
      }
    }
    for (MCH_AircraftInfo.Canopy c : info.canopyList)
    {
      c.model = loadPartModel(path, info.name, info.model, c.modelName);
    }
    for (MCH_AircraftInfo.DrawnPart n : info.landingGear)
    {
      n.model = loadPartModel(path, info.name, info.model, n.modelName);
    }
    for (MCH_AircraftInfo.WeaponBay w : info.partWeaponBay)
    {
      w.model = loadPartModel(path, info.name, info.model, w.modelName);
    }
    for (MCH_AircraftInfo.CrawlerTrack c : info.partCrawlerTrack)
    {
      c.model = loadPartModel(path, info.name, info.model, c.modelName);
    }
    for (MCH_AircraftInfo.TrackRoller c : info.partTrackRoller)
    {
      c.model = loadPartModel(path, info.name, info.model, c.modelName);
    }
    for (MCH_AircraftInfo.PartWheel c : info.partWheel)
    {
      c.model = loadPartModel(path, info.name, info.model, c.modelName);
    }
    for (MCH_AircraftInfo.PartWheel c : info.partSteeringWheel)
    {
      c.model = loadPartModel(path, info.name, info.model, c.modelName);
    }
  }
  
  private void registerVCPModels(MCH_VehicleInfo info, MCH_VehicleInfo.VPart vp)
  {
    for (MCH_VehicleInfo.VPart vcp : vp.child)
    {
      vcp.model = loadPartModel("vehicles", info.name, info.model, vcp.modelName);
      if (vcp.child != null)
      {
        registerVCPModels(info, vcp);
      }
    }
  }
  

  public void registerClientTick()
  {
    Minecraft mc = Minecraft.getMinecraft();
    MCH_ClientCommonTickHandler.instance = new MCH_ClientCommonTickHandler(mc, MCH_MOD.config);
    W_TickRegistry.registerTickHandler(MCH_ClientCommonTickHandler.instance, Side.CLIENT);
  }
  
  public boolean isRemote() {
    return true;
  }
  
  public String side() { return "Client"; }
  

  public MCH_SoundUpdater CreateSoundUpdater(MCH_EntityAircraft aircraft)
  {
    if ((aircraft == null) || (!aircraft.worldObj.isRemote)) return null;
    return new MCH_SoundUpdater(Minecraft.getMinecraft(), aircraft, Minecraft.getMinecraft().thePlayer);
  }
  

  public void registerSounds()
  {
    W_McClient.addSound("alert.ogg");
    W_McClient.addSound("locked.ogg");
    W_McClient.addSound("gltd.ogg");
    W_McClient.addSound("zoom.ogg");
    W_McClient.addSound("ng.ogg");
    W_McClient.addSound("a-10_snd.ogg");
    W_McClient.addSound("gau-8_snd.ogg");
    W_McClient.addSound("hit.ogg");
    W_McClient.addSound("helidmg.ogg");
    W_McClient.addSound("heli.ogg");
    W_McClient.addSound("plane.ogg");
    W_McClient.addSound("plane_cc.ogg");
    W_McClient.addSound("plane_cv.ogg");
    W_McClient.addSound("chain.ogg");
    W_McClient.addSound("chain_ct.ogg");
    W_McClient.addSound("eject_seat.ogg");
    
    W_McClient.addSound("fim92_snd.ogg");
    W_McClient.addSound("fim92_reload.ogg");
    W_McClient.addSound("lockon.ogg");
    
    for (MCH_WeaponInfo info : MCH_WeaponInfoManager.getValues())
    {
      W_McClient.addSound(info.soundFileName + ".ogg");
    }
    
    for (MCH_AircraftInfo info : MCP_PlaneInfoManager.map.values())
    {
      if (!info.soundMove.isEmpty()) W_McClient.addSound(info.soundMove + ".ogg");
    }
    for (MCH_AircraftInfo info : MCH_HeliInfoManager.map.values())
    {
      if (!info.soundMove.isEmpty()) W_McClient.addSound(info.soundMove + ".ogg");
    }
    for (MCH_AircraftInfo info : MCH_TankInfoManager.map.values())
    {
      if (!info.soundMove.isEmpty()) W_McClient.addSound(info.soundMove + ".ogg");
    }
    for (MCH_AircraftInfo info : MCH_VehicleInfoManager.map.values())
    {
      if (!info.soundMove.isEmpty()) { W_McClient.addSound(info.soundMove + ".ogg");
      }
    }
  }
  
  public void loadConfig(String fileName)
  {
    this.lastConfigFileName = fileName;
    
    this.config = new MCH_Config(Minecraft.getMinecraft().mcDataDir.getPath(), "/" + fileName);
    this.config.load();
    this.config.write();
  }
  

  public void reconfig()
  {
    MCH_Lib.DbgLog(false, "MCH_ClientProxy.reconfig()", new Object[0]);
    loadConfig(this.lastConfigFileName);
    MCH_ClientCommonTickHandler.instance.updatekeybind(this.config);
  }
  
  public void loadHUD(String path)
  {
    this.lastLoadHUDPath = path;
    MCH_HudManager.load(path);
  }
  
  public void reloadHUD() {
    loadHUD(this.lastLoadHUDPath);
  }
  
  public Entity getClientPlayer()
  {
    return Minecraft.getMinecraft().thePlayer;
  }
  


  public void init()
  {
    MinecraftForge.EVENT_BUS.register(new MCH_ParticlesUtil());
    MinecraftForge.EVENT_BUS.register(new MCH_ClientEventHook());
  }
  
  public void setCreativeDigDelay(int n)
  {
    W_Reflection.setCreativeDigSpeed(n);
  }
  
  public boolean isFirstPerson()
  {
    return Minecraft.getMinecraft().gameSettings.thirdPersonView == 0;
  }
  
  public int getNewRenderType()
  {
    return RenderingRegistry.getNextAvailableRenderId();
  }
  
  public boolean isSinglePlayer() { return Minecraft.getMinecraft().isSingleplayer(); }
  
  public void readClientModList()
  {
    try
    {
      Minecraft mc = Minecraft.getMinecraft();
      MCH_MultiplayClient.readModList(mc.getSession().getPlayerID(), mc.getSession().getUsername());
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public void printChatMessage(IChatComponent chat, int showTime, int pos)
  {
    ((MCH_GuiTitle)MCH_ClientCommonTickHandler.instance.gui_Title).setupTitle(chat, showTime, pos);
  }
  
  public void hitBullet()
  {
    MCH_ClientCommonTickHandler.instance.gui_Common.hitBullet();
  }
  
  public void clientLocked()
  {
    MCH_ClientCommonTickHandler.isLocked = true;
  }
}
