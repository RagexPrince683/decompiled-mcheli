package mcheli.wrapper;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import java.util.List;
import java.util.Queue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.NetworkSystem;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;

public class W_Reflection
{
  public W_Reflection() {}
  
  public static RenderManager getRenderManager(Render render)
  {
    try
    {
      return (RenderManager)ObfuscationReflectionHelper.getPrivateValue(Render.class, render, new String[] { "renderManager", "renderManager" });
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return null;
  }
  
  public static void restoreDefaultThirdPersonDistance()
  {
    setThirdPersonDistance(4.0F);
  }
  
  public static void setThirdPersonDistance(float dist)
  {
    if (dist < 0.1D) return;
    try
    {
      Minecraft mc = Minecraft.getMinecraft();
      ObfuscationReflectionHelper.setPrivateValue(EntityRenderer.class, mc.entityRenderer, Float.valueOf(dist), new String[] { "thirdPersonDistance", "thirdPersonDistance" });
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public static float getThirdPersonDistance()
  {
    try
    {
      Minecraft mc = Minecraft.getMinecraft();
      return ((Float)ObfuscationReflectionHelper.getPrivateValue(EntityRenderer.class, mc.entityRenderer, new String[] { "thirdPersonDistance", "thirdPersonDistance" })).floatValue();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return 4.0F;
  }
  
  public static void setCameraRoll(float roll)
  {
    try
    {
      roll = MathHelper.wrapAngleTo180_float(roll);
      
      Minecraft mc = Minecraft.getMinecraft();
      ObfuscationReflectionHelper.setPrivateValue(EntityRenderer.class, Minecraft.getMinecraft().entityRenderer, Float.valueOf(roll), new String[] { "camRoll", "camRoll" });
      
      ObfuscationReflectionHelper.setPrivateValue(EntityRenderer.class, Minecraft.getMinecraft().entityRenderer, Float.valueOf(roll), new String[] { "prevCamRoll", "prevCamRoll" });
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public static float getPrevCameraRoll()
  {
    try {
      Minecraft mc = Minecraft.getMinecraft();
      return ((Float)ObfuscationReflectionHelper.getPrivateValue(EntityRenderer.class, Minecraft.getMinecraft().entityRenderer, new String[] { "prevCamRoll", "prevCamRoll" })).floatValue();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return 0.0F;
  }
  
  public static void restoreCameraZoom()
  {
    setCameraZoom(1.0F);
  }
  
  public static void setCameraZoom(float zoom)
  {
    try {
      Minecraft mc = Minecraft.getMinecraft();
      ObfuscationReflectionHelper.setPrivateValue(EntityRenderer.class, mc.entityRenderer, Float.valueOf(zoom), new String[] { "cameraZoom", "cameraZoom" });

    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  


  public static void setItemRenderer(ItemRenderer r)
  {
	 Minecraft mc;
    try
    {
      mc = Minecraft.getMinecraft();
    }
    catch (Exception e)
    {
      
      







      e.printStackTrace();
    }
  }
  
  public static void setCreativeDigSpeed(int n)
  {
    try
    {
      Minecraft mc = Minecraft.getMinecraft();
      ObfuscationReflectionHelper.setPrivateValue(net.minecraft.client.multiplayer.PlayerControllerMP.class, mc.playerController, Integer.valueOf(n), new String[] { "blockHitDelay", "blockHitDelay" });
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public static ItemRenderer getItemRenderer()
  {
    return Minecraft.getMinecraft().entityRenderer.itemRenderer;
  }
  
  public static void setItemRenderer_ItemToRender(ItemStack itemToRender)
  {
    try
    {
      ObfuscationReflectionHelper.setPrivateValue(ItemRenderer.class, getItemRenderer(), itemToRender, new String[] { "itemToRender", "itemToRender" });
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public static ItemStack getItemRenderer_ItemToRender()
  {
    try
    {
      return (ItemStack)ObfuscationReflectionHelper.getPrivateValue(ItemRenderer.class, getItemRenderer(), new String[] { "itemToRender", "itemToRender" });

    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return null;
  }
  
  public static void setItemRendererProgress(float equippedProgress)
  {
    try
    {
      ObfuscationReflectionHelper.setPrivateValue(ItemRenderer.class, getItemRenderer(), Float.valueOf(equippedProgress), new String[] { "equippedProgress", "equippedProgress" });

    }
    catch (Exception e)
    {

      e.printStackTrace();
    }
  }
  
  public static void setBoundingBox(Entity entity, AxisAlignedBB bb)
  {
    try
    {
      ObfuscationReflectionHelper.setPrivateValue(Entity.class, entity, bb, new String[] { "boundingBox", "boundingBox" });
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public static List getNetworkManagers()
  {
    try
    {
      return (List)ObfuscationReflectionHelper.getPrivateValue(NetworkSystem.class, MinecraftServer.getServer().func_147137_ag(), new String[] { "networkManagers", "networkManagers" });

    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return null;
  }
  
  public static Queue getReceivedPacketsQueue(NetworkManager nm)
  {
    try
    {
      return (Queue)ObfuscationReflectionHelper.getPrivateValue(NetworkManager.class, nm, new String[] { "receivedPacketsQueue", "receivedPacketsQueue" });

    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return null;
  }
  
  public static Queue getSendPacketsQueue(NetworkManager nm)
  {
    try
    {
      return (Queue)ObfuscationReflectionHelper.getPrivateValue(NetworkManager.class, nm, new String[] { "outboundPacketsQueue", "outboundPacketsQueue" });

    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return null;
  }
}
