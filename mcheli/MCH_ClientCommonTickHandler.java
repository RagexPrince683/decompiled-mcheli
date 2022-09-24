package mcheli;

import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;

import mcheli.aircraft.MCH_AircraftInfo;
import mcheli.aircraft.MCH_AircraftInfo.CameraPosition;
import mcheli.aircraft.MCH_EntityAircraft;
import mcheli.aircraft.MCH_EntitySeat;
import mcheli.aircraft.MCH_SeatInfo;
import mcheli.command.MCH_GuiTitle;
import mcheli.gltd.MCH_ClientGLTDTickHandler;
import mcheli.gltd.MCH_EntityGLTD;
import mcheli.gltd.MCH_GuiGLTD;
import mcheli.gui.MCH_Gui;
import mcheli.helicopter.MCH_EntityHeli;
import mcheli.lweapon.MCH_ClientLightWeaponTickHandler;
import mcheli.multiplay.MCH_GuiTargetMarker;
import mcheli.plane.MCP_ClientPlaneTickHandler;
import mcheli.plane.MCP_EntityPlane;
import mcheli.tank.MCH_ClientTankTickHandler;
import mcheli.tank.MCH_EntityTank;
import mcheli.tool.MCH_ClientToolTickHandler;
import mcheli.tool.MCH_GuiWrench;
import mcheli.tool.MCH_ItemWrench;
import mcheli.tool.rangefinder.MCH_GuiRangeFinder;
import mcheli.uav.MCH_EntityUavStation;
import mcheli.vehicle.MCH_ClientVehicleTickHandler;
import mcheli.weapon.MCH_EntityTvMissile;
import mcheli.weapon.MCH_WeaponInfo;
import mcheli.weapon.MCH_WeaponSet;
import mcheli.wrapper.W_Lib;
import mcheli.wrapper.W_McClient;
import mcheli.wrapper.W_Reflection;
import mcheli.wrapper.W_TickHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MouseHelper;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.Display;

@SideOnly(cpw.mods.fml.relauncher.Side.CLIENT)
public class MCH_ClientCommonTickHandler extends W_TickHandler
{
  public static MCH_ClientCommonTickHandler instance;
  public MCH_GuiCommon gui_Common;
  public MCH_Gui gui_Heli;
  public MCH_Gui gui_Plane;
  public MCH_Gui gui_Tank;
  public MCH_Gui gui_GLTD;
  public MCH_Gui gui_Vehicle;
  public MCH_Gui gui_LWeapon;
  public MCH_Gui gui_Wrench;
  public MCH_Gui gui_EMarker;
  public MCH_Gui gui_SwnGnr;
  public MCH_Gui gui_RngFndr;
  public MCH_Gui gui_Title;
  public MCH_Gui[] guis;
  public MCH_Gui[] guiTicks;
  public MCH_ClientTickHandlerBase[] ticks;
  public MCH_Key[] Keys;
  public MCH_Key KeyCamDistUp;
  public MCH_Key KeyCamDistDown;
  public MCH_Key KeyScoreboard;
  public MCH_Key KeyMultiplayManager;
  public static int cameraMode = 0;
  public static MCH_EntityAircraft ridingAircraft = null;
  
  public static boolean isDrawScoreboard = false;
  
  public static int sendLDCount = 0;
  
  public static boolean isLocked = false;
  public static int lockedSoundCount = 0;
  int debugcnt;
  
  public MCH_ClientCommonTickHandler(Minecraft minecraft, MCH_Config config) {
    super(minecraft);
    
    this.gui_Common = new MCH_GuiCommon(minecraft);
    this.gui_Heli = new mcheli.helicopter.MCH_GuiHeli(minecraft);
    this.gui_Plane = new mcheli.plane.MCP_GuiPlane(minecraft);
    this.gui_Tank = new mcheli.tank.MCH_GuiTank(minecraft);
    this.gui_GLTD = new MCH_GuiGLTD(minecraft);
    this.gui_Vehicle = new mcheli.vehicle.MCH_GuiVehicle(minecraft);
    this.gui_LWeapon = new mcheli.lweapon.MCH_GuiLightWeapon(minecraft);
    this.gui_Wrench = new MCH_GuiWrench(minecraft);
    this.gui_SwnGnr = new mcheli.mob.MCH_GuiSpawnGunner(minecraft);
    this.gui_RngFndr = new MCH_GuiRangeFinder(minecraft);
    this.gui_EMarker = new MCH_GuiTargetMarker(minecraft);
    this.gui_Title = new MCH_GuiTitle(minecraft);
    this.guis = new MCH_Gui[] { this.gui_RngFndr, this.gui_LWeapon, this.gui_Heli, this.gui_Plane, this.gui_Tank, this.gui_GLTD, this.gui_Vehicle };
    

    this.guiTicks = new MCH_Gui[] { this.gui_Common, this.gui_Heli, this.gui_Plane, this.gui_Tank, this.gui_GLTD, this.gui_Vehicle, this.gui_LWeapon, this.gui_Wrench, this.gui_SwnGnr, this.gui_RngFndr, this.gui_EMarker, this.gui_Title };
    



    this.ticks = new MCH_ClientTickHandlerBase[] { new mcheli.helicopter.MCH_ClientHeliTickHandler(minecraft, config), new MCP_ClientPlaneTickHandler(minecraft, config), new MCH_ClientTankTickHandler(minecraft, config), new MCH_ClientGLTDTickHandler(minecraft, config), new MCH_ClientVehicleTickHandler(minecraft, config), new MCH_ClientLightWeaponTickHandler(minecraft, config), new mcheli.aircraft.MCH_ClientSeatTickHandler(minecraft, config), new MCH_ClientToolTickHandler(minecraft, config) };
    









    updatekeybind(config);
  }
  
  public void updatekeybind(MCH_Config config)
  {
    this.KeyCamDistUp = new MCH_Key(MCH_Config.KeyCameraDistUp.prmInt);
    this.KeyCamDistDown = new MCH_Key(MCH_Config.KeyCameraDistDown.prmInt);
    this.KeyScoreboard = new MCH_Key(MCH_Config.KeyScoreboard.prmInt);
    this.KeyMultiplayManager = new MCH_Key(MCH_Config.KeyMultiplayManager.prmInt);
    this.Keys = new MCH_Key[] { this.KeyCamDistUp, this.KeyCamDistDown, this.KeyScoreboard, this.KeyMultiplayManager };
    



    for (MCH_ClientTickHandlerBase t : this.ticks)
    {
      t.updateKeybind(config); }
  }
  
  public String getLabel() {
    return null;
  }
  

  public void onTick()
  {
    
    
    for (MCH_Key k : this.Keys) { k.update();
    }
    EntityPlayer player = this.mc.thePlayer;
    if ((player != null) && (this.mc.currentScreen == null))
    {
      if (MCH_ServerSettings.enableCamDistChange)
      {
        if ((this.KeyCamDistUp.isKeyDown()) || (this.KeyCamDistDown.isKeyDown()))
        {
          int camdist = (int)W_Reflection.getThirdPersonDistance();
          if ((this.KeyCamDistUp.isKeyDown()) && (camdist < 72))
          {
            camdist += 4;
            if (camdist > 72) camdist = 72;
            W_Reflection.setThirdPersonDistance(camdist);
          }
          else if (this.KeyCamDistDown.isKeyDown())
          {
            camdist -= 4;
            if (camdist < 4) camdist = 4;
            W_Reflection.setThirdPersonDistance(camdist);
          }
        }
      }
      
      if ((this.mc.currentScreen == null) && ((!this.mc.isSingleplayer()) || (MCH_Config.DebugLog)))
      {
        isDrawScoreboard = this.KeyScoreboard.isKeyPress();
        
        if ((!isDrawScoreboard) && (this.KeyMultiplayManager.isKeyDown()))
        {



          MCH_PacketIndOpenScreen.send(5);
        }
      }
    }
    

    if (sendLDCount < 10)
    {
      sendLDCount += 1;
    }
    else
    {
      mcheli.multiplay.MCH_MultiplayClient.sendImageData();
      sendLDCount = 0;
    }
    
    boolean inOtherGui = this.mc.currentScreen != null;
    
    for (MCH_ClientTickHandlerBase t : this.ticks)
    {
      t.onTick(inOtherGui);
    }
    
    for (MCH_Gui g : this.guiTicks)
    {
      g.onTick();
    }
    
    MCH_EntityAircraft ac = MCH_EntityAircraft.getAircraft_RiddenOrControl(player);
    if ((player != null) && (ac != null) && (!ac.isDestroyed()))
    {
      if ((isLocked) && (lockedSoundCount == 0))
      {
        isLocked = false;
        lockedSoundCount = 20;
        MCH_ClientTickHandlerBase.playSound("locked");
      }
    }
    else
    {
      lockedSoundCount = 0;
      isLocked = false;
    }
    if (lockedSoundCount > 0)
    {
      lockedSoundCount -= 1;
    }
  }
  
  public void onTickPre() {
    if ((this.mc.thePlayer != null) && (this.mc.theWorld != null))
    {
      onTick();
    }
  }
  
  public void onTickPost()
  {
    if ((this.mc.thePlayer != null) && (this.mc.theWorld != null))
    {
      MCH_GuiTargetMarker.onClientTick();
    }
  }
  
  private static double prevMouseDeltaX;
  private static double prevMouseDeltaY;
  private static double mouseDeltaX = 0.0D;
  private static double mouseDeltaY = 0.0D;
  private static double mouseRollDeltaX = 0.0D;
  private static double mouseRollDeltaY = 0.0D;
  private static boolean isRideAircraft = false;
  private static float prevTick = 0.0F;
  
  public static double getCurrentStickX()
  {
    return mouseRollDeltaX;
  }
  
  public static double getCurrentStickY() {
    double inv = 1.0D;
    if (Minecraft.getMinecraft().gameSettings.invertMouse)
    {
      inv = -inv;
    }
    if (MCH_Config.InvertMouse.prmBool)
    {
      inv = -inv;
    }
    return mouseRollDeltaY * inv;
  }
  
  public static double getMaxStickLength() {
    return 40.0D;
  }
  
  public void updateMouseDelta(boolean stickMode, float partialTicks)
  {
    prevMouseDeltaX = mouseDeltaX;
    prevMouseDeltaY = mouseDeltaY;
    mouseDeltaX = 0.0D;
    mouseDeltaY = 0.0D;
    
    if ((this.mc.inGameHasFocus) && (Display.isActive()) && (this.mc.currentScreen == null))
    {
      if (stickMode)
      {
        if (Math.abs(mouseRollDeltaX) < getMaxStickLength() * 0.2D)
        {
          mouseRollDeltaX *= (1.0F - 0.15F * partialTicks);
        }
        if (Math.abs(mouseRollDeltaY) < getMaxStickLength() * 0.2D)
        {
          mouseRollDeltaY *= (1.0F - 0.15F * partialTicks);
        }
      }
      
      this.mc.mouseHelper.mouseXYChange();
      float f1 = this.mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
      float f2 = f1 * f1 * f1 * 8.0F;
      
      double ms = MCH_Config.MouseSensitivity.prmDouble * 0.1D;
      
      mouseDeltaX = ms * this.mc.mouseHelper.deltaX * f2;
      mouseDeltaY = ms * this.mc.mouseHelper.deltaY * f2;
      
      byte inv = 1;
      
      if (this.mc.gameSettings.invertMouse)
      {
        inv = -1;
      }
      if (MCH_Config.InvertMouse.prmBool)
      {
        inv = (byte)(inv * -1);
      }
      
      mouseRollDeltaX += mouseDeltaX;
      mouseRollDeltaY += mouseDeltaY * inv;
      


      double dist = mouseRollDeltaX * mouseRollDeltaX + mouseRollDeltaY * mouseRollDeltaY;
      if (dist > 1.0D)
      {
        dist = MathHelper.sqrt_double(dist);
        double d = dist;
        if (d > getMaxStickLength()) { d = getMaxStickLength();
        }
        mouseRollDeltaX /= dist;
        mouseRollDeltaY /= dist;
        mouseRollDeltaX *= d;
        mouseRollDeltaY *= d;
      }
    }
  }
  

  public void onRenderTickPre(float partialTicks)
  {
    
    if (!MCH_ServerSettings.enableDebugBoundingBox)
    {
      net.minecraft.client.renderer.entity.RenderManager.debugBoundingBox = false;
    }
    
    MCH_ClientEventHook.haveSearchLightAircraft.clear();
    if ((this.mc != null) && (this.mc.theWorld != null)) {
      for (Object o : Minecraft.getMinecraft().theWorld.loadedEntityList)
      {
        if ((o instanceof MCH_EntityAircraft))
        {
          if (((MCH_EntityAircraft)o).haveSearchLight())
          {
            MCH_ClientEventHook.haveSearchLightAircraft.add((MCH_EntityAircraft)o);
          }
        }
      }
    }
    if (W_McClient.isGamePaused()) return;
    EntityPlayer player = this.mc.thePlayer;
    if (player == null) { return;
    }
    ItemStack currentItemstack = player.getCurrentEquippedItem();
    if ((currentItemstack != null) && ((currentItemstack.getItem() instanceof MCH_ItemWrench)))
    {
      if (player.getItemInUseCount() > 0)
      {
        W_Reflection.setItemRendererProgress(1.0F);
      }
    }
    
    ridingAircraft = MCH_EntityAircraft.getAircraft_RiddenOrControl(player);
    if (ridingAircraft != null)
    {
      cameraMode = ridingAircraft.getCameraMode(player);


    }
    else if ((player.ridingEntity instanceof MCH_EntityGLTD))
    {
      MCH_EntityGLTD gltd = (MCH_EntityGLTD)player.ridingEntity;
      cameraMode = gltd.camera.getMode(0);
    }
    else
    {
      cameraMode = 0;
    }
    

    MCH_EntityAircraft ac = null;
    if (((player.ridingEntity instanceof MCH_EntityHeli)) || ((player.ridingEntity instanceof MCP_EntityPlane)) || ((player.ridingEntity instanceof MCH_EntityTank)))
    {


      ac = (MCH_EntityAircraft)player.ridingEntity;
    }
    else if ((player.ridingEntity instanceof MCH_EntityUavStation))
    {
      ac = ((MCH_EntityUavStation)player.ridingEntity).getControlAircract();
    }
    else if ((player.ridingEntity instanceof mcheli.vehicle.MCH_EntityVehicle))
    {
      MCH_EntityAircraft vehicle = (MCH_EntityAircraft)player.ridingEntity;
      vehicle.setupAllRiderRenderPosition(partialTicks, player);
    }
    
    boolean stickMode = false;
    if ((ac instanceof MCH_EntityHeli))
    {
      stickMode = MCH_Config.MouseControlStickModeHeli.prmBool;
    }
    if ((ac instanceof MCP_EntityPlane))
    {
      stickMode = MCH_Config.MouseControlStickModePlane.prmBool;
    }
    
    for (int i = 0; (i < 10) && (prevTick > partialTicks); i++)
    {
      prevTick -= 1.0F;
    }
    
    if ((ac != null) && (ac.canMouseRot()))
    {
      if (!isRideAircraft)
      {
        ac.onInteractFirst(player);
      }
      isRideAircraft = true;
      
      updateMouseDelta(stickMode, partialTicks);
      
      boolean fixRot = false;
      float fixYaw = 0.0F;
      float fixPitch = 0.0F;
      MCH_SeatInfo seatInfo = ac.getSeatInfo(player);
      if ((seatInfo != null) && (seatInfo.fixRot) && (ac.getIsGunnerMode(player)) && (!ac.isGunnerLookMode(player)))
      {
        fixRot = true;
        fixYaw = seatInfo.fixYaw;
        fixPitch = seatInfo.fixPitch;
        mouseRollDeltaX *= 0.0D;
        mouseRollDeltaY *= 0.0D;
        mouseDeltaX *= 0.0D;
        mouseDeltaY *= 0.0D;
      }
      else if (ac.isPilot(player))
      {
        MCH_AircraftInfo.CameraPosition cp = ac.getCameraPosInfo();
        if (cp != null)
        {
          fixYaw = cp.yaw;
          fixPitch = cp.pitch;
        }
      }
      
      if (ac.getAcInfo() == null)
      {
        player.setAngles((float)mouseDeltaX, (float)mouseDeltaY);
      }
      else
      {
        ac.setAngles(player, fixRot, fixYaw, fixPitch, (float)(mouseDeltaX + prevMouseDeltaX) / 2.0F, (float)(mouseDeltaY + prevMouseDeltaY) / 2.0F, (float)mouseRollDeltaX, (float)mouseRollDeltaY, partialTicks - prevTick);
      }
      





      ac.setupAllRiderRenderPosition(partialTicks, player);
      
      double dist = MathHelper.sqrt_double(mouseRollDeltaX * mouseRollDeltaX + mouseRollDeltaY * mouseRollDeltaY);
      

      if ((!stickMode) || (dist < getMaxStickLength() * 0.1D))
      {
        mouseRollDeltaX *= 0.95D;
        mouseRollDeltaY *= 0.95D;
      }
      
      float roll = MathHelper.wrapAngleTo180_float(ac.getRotRoll());
      float yaw = MathHelper.wrapAngleTo180_float(ac.getRotYaw() - player.rotationYaw);
      roll *= MathHelper.cos((float)(yaw * 3.141592653589793D / 180.0D));
      if ((ac.getTVMissile() != null) && (W_Lib.isClientPlayer(ac.getTVMissile().shootingEntity)) && (ac.getIsGunnerMode(player)))
      {


        roll = 0.0F;
      }
      W_Reflection.setCameraRoll(roll);
      correctViewEntityDummy(player);
    }
    else
    {
      MCH_EntitySeat seat = (player.ridingEntity instanceof MCH_EntitySeat) ? (MCH_EntitySeat)player.ridingEntity : null;
      if ((seat != null) && (seat.getParent() != null))
      {
        updateMouseDelta(stickMode, partialTicks);
        
        ac = seat.getParent();
        
        boolean fixRot = false;
        MCH_SeatInfo seatInfo = ac.getSeatInfo(player);
        if ((seatInfo != null) && (seatInfo.fixRot) && (ac.getIsGunnerMode(player)) && (!ac.isGunnerLookMode(player)))
        {
          fixRot = true;
          mouseRollDeltaX *= 0.0D;
          mouseRollDeltaY *= 0.0D;
          mouseDeltaX *= 0.0D;
          mouseDeltaY *= 0.0D;
        }
        
        Vec3 v = Vec3.createVectorHelper(mouseDeltaX, mouseRollDeltaY, 0.0D);
        mcheli.wrapper.W_Vec3.rotateAroundZ((float)(ac.calcRotRoll(partialTicks) / 180.0F * 3.141592653589793D), v);
        
        MCH_WeaponSet ws = ac.getCurrentWeapon(player);
        mouseDeltaY *= ((ws != null) && (ws.getInfo() != null) ? ws.getInfo().cameraRotationSpeedPitch : 1.0D);
        
        player.setAngles((float)mouseDeltaX, (float)mouseDeltaY);
        
        float y = ac.getRotYaw();
        float p = ac.getRotPitch();
        float r = ac.getRotRoll();
        ac.setRotYaw(ac.calcRotYaw(partialTicks));
        ac.setRotPitch(ac.calcRotPitch(partialTicks));
        ac.setRotRoll(ac.calcRotRoll(partialTicks));
        
        float revRoll = 0.0F;
        if (fixRot)
        {
          player.rotationYaw = (ac.getRotYaw() + seatInfo.fixYaw);
          player.rotationPitch = (ac.getRotPitch() + seatInfo.fixPitch);
          if (player.rotationPitch > 90.0F)
          {
            player.prevRotationPitch -= (player.rotationPitch - 90.0F) * 2.0F;
            player.rotationPitch -= (player.rotationPitch - 90.0F) * 2.0F;
            player.prevRotationYaw += 180.0F;
            player.rotationYaw += 180.0F;
            revRoll = 180.0F;
          }
          else if (player.rotationPitch < -90.0F)
          {
            player.prevRotationPitch -= (player.rotationPitch - 90.0F) * 2.0F;
            player.rotationPitch -= (player.rotationPitch - 90.0F) * 2.0F;
            player.prevRotationYaw += 180.0F;
            player.rotationYaw += 180.0F;
            revRoll = 180.0F;
          }
        }
        
        ac.setupAllRiderRenderPosition(partialTicks, player);
        ac.setRotYaw(y);
        ac.setRotPitch(p);
        ac.setRotRoll(r);
        
        mouseRollDeltaX *= 0.9D;
        mouseRollDeltaY *= 0.9D;
        
        float roll = MathHelper.wrapAngleTo180_float(ac.getRotRoll());
        float yaw = MathHelper.wrapAngleTo180_float(ac.getRotYaw() - player.rotationYaw);
        roll *= MathHelper.cos((float)(yaw * 3.141592653589793D / 180.0D));
        if ((ac.getTVMissile() != null) && (W_Lib.isClientPlayer(ac.getTVMissile().shootingEntity)) && (ac.getIsGunnerMode(player)))
        {


          roll = 0.0F;
        }
        W_Reflection.setCameraRoll(roll + revRoll);
        correctViewEntityDummy(player);
      }
      else
      {
        if (isRideAircraft)
        {
          W_Reflection.setCameraRoll(0.0F);
          isRideAircraft = false;
        }
        mouseRollDeltaX = 0.0D;
        mouseRollDeltaY = 0.0D;
      }
    }
    
    if (ac != null)
    {
      if ((ac.getSeatIdByEntity(player) == 0) && (!ac.isDestroyed()))
      {
        ac.lastRiderYaw = player.rotationYaw;
        ac.prevLastRiderYaw = player.prevRotationYaw;
        ac.lastRiderPitch = player.rotationPitch;
        ac.prevLastRiderPitch = player.prevRotationPitch;
      }
      
      ac.updateWeaponsRotation();
    }
    
    Entity de = MCH_ViewEntityDummy.getInstance(player.worldObj);
    if (de != null)
    {
      de.rotationYaw = player.rotationYaw;
      de.prevRotationYaw = player.prevRotationYaw;
      
      if (ac != null)
      {
        MCH_WeaponSet wi = ac.getCurrentWeapon(player);
        if ((wi != null) && (wi.getInfo() != null) && (wi.getInfo().fixCameraPitch))
        {
          de.rotationPitch = (de.prevRotationPitch = 0.0F);
        }
      }
    }
    
    prevTick = partialTicks;
  }
  
  public void correctViewEntityDummy(Entity entity)
  {
    Entity de = MCH_ViewEntityDummy.getInstance(entity.worldObj);
    if (de != null)
    {
      if (de.rotationYaw - de.prevRotationYaw > 180.0F)
      {
        de.prevRotationYaw += 360.0F;
      }
      else if (de.rotationYaw - de.prevRotationYaw < -180.0F)
      {
        de.prevRotationYaw -= 360.0F;
      }
    }
  }
  

  public void onPlayerTickPre(EntityPlayer player)
  {
    if (player.worldObj.isRemote)
    {
      ItemStack currentItemstack = player.getCurrentEquippedItem();
      if ((currentItemstack != null) && ((currentItemstack.getItem() instanceof MCH_ItemWrench)))
      {
        if ((player.getItemInUseCount() > 0) && (player.getItemInUse() != currentItemstack))
        {
          int maxdm = currentItemstack.getMaxDamage();
          int dm = currentItemstack.getItemDamage();
          if ((dm <= maxdm) && (dm > 0))
          {
            player.setItemInUse(currentItemstack, player.getItemInUseCount());
          }
        }
      }
    }
  }
  

  public void onPlayerTickPost(EntityPlayer player) {}
  
  public void onRenderTickPost(float partialTicks)
  {
    if (this.mc.thePlayer != null)
    {
      MCH_ClientTickHandlerBase.applyRotLimit(this.mc.thePlayer);
      

      Entity e = MCH_ViewEntityDummy.getInstance(this.mc.thePlayer.worldObj);
      




      if (e != null)
      {
        e.rotationPitch = this.mc.thePlayer.rotationPitch;
        e.rotationYaw = this.mc.thePlayer.rotationYaw;
        e.prevRotationPitch = this.mc.thePlayer.prevRotationPitch;
        e.prevRotationYaw = this.mc.thePlayer.prevRotationYaw;
      }
    }
    
    if ((this.mc.currentScreen == null) || ((this.mc.currentScreen instanceof net.minecraft.client.gui.GuiChat)) || (this.mc.currentScreen.getClass().toString().indexOf("GuiDriveableController") >= 0))
    {


      for (MCH_Gui gui : this.guis)
      {
        if (drawGui(gui, partialTicks)) {
          break;
        }
      }
      

      drawGui(this.gui_Common, partialTicks);
      drawGui(this.gui_Wrench, partialTicks);
      drawGui(this.gui_SwnGnr, partialTicks);
      drawGui(this.gui_EMarker, partialTicks);
      
      if (isDrawScoreboard)
      {
        mcheli.multiplay.MCH_GuiScoreboard.drawList(this.mc, this.mc.fontRenderer, false);
      }
      
      drawGui(this.gui_Title, partialTicks);
    }
  }
  
  public boolean drawGui(MCH_Gui gui, float partialTicks)
  {
    if (gui.isDrawGui(this.mc.thePlayer))
    {
      gui.drawScreen(0, 0, partialTicks);
      return true;
    }
    return false;
  }
}
