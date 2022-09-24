package mcheli.wrapper;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;



public abstract class W_TickHandler
  implements ITickHandler
{
  protected Minecraft mc;
  
  static enum TickType
  {
    RENDER,  CLIENT;
    
    private TickType() {}
  }
  
  public W_TickHandler(Minecraft m)
  {
    this.mc = m;
  }
  
  public void onPlayerTickPre(EntityPlayer player) {}
  
  public void onPlayerTickPost(EntityPlayer player) {}
  
  public void onRenderTickPre(float partialTicks) {}
  
  public void onRenderTickPost(float partialTicks) {}
  
  public void onTickPre() {}
  
  public void onTickPost() {}
  
  @SubscribeEvent
  public void onPlayerTickEvent(TickEvent.PlayerTickEvent event) {
    if (event.phase == TickEvent.Phase.START)
    {
      onPlayerTickPre(event.player);
    }
    if (event.phase == TickEvent.Phase.END)
    {
      onPlayerTickPost(event.player);
    }
  }
  
  @SubscribeEvent
  public void onClientTickEvent(TickEvent.ClientTickEvent event)
  {
    if (event.phase == TickEvent.Phase.START)
    {
      onTickPre();
    }
    if (event.phase == TickEvent.Phase.END)
    {
      onTickPost();
    }
  }
  

  @SubscribeEvent
  @SideOnly(Side.CLIENT)
  public void onRenderTickEvent(TickEvent.RenderTickEvent event)
  {
    if (event.phase == TickEvent.Phase.START)
    {
      onRenderTickPre(event.renderTickTime);
    }
    if (event.phase == TickEvent.Phase.END)
    {
      onRenderTickPost(event.renderTickTime);
    }
  }
}
