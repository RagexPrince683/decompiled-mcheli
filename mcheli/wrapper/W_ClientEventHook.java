package mcheli.wrapper;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderLivingEvent.Post;
import net.minecraftforge.client.event.RenderLivingEvent.Pre;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.event.world.WorldEvent.Unload;

public class W_ClientEventHook
{
  public W_ClientEventHook() {}
  
  @SubscribeEvent
  public void onEvent_MouseEvent(net.minecraftforge.client.event.MouseEvent event)
  {
    mouseEvent(event);
  }
  
  public void mouseEvent(net.minecraftforge.client.event.MouseEvent event) {}
  
  @SubscribeEvent
  public void onEvent_renderLivingEventSpecialsPre(RenderLivingEvent.Specials.Pre event)
  {
    renderLivingEventSpecialsPre(event);
  }
  
  public void renderLivingEventSpecialsPre(RenderLivingEvent.Specials.Pre event) {}
  
  @SubscribeEvent
  public void onEvent_renderLivingEventSpecialsPost(RenderLivingEvent.Specials.Post event)
  {
    renderLivingEventSpecialsPost(event);
  }
  

  public void renderLivingEventSpecialsPost(RenderLivingEvent.Specials.Post event) {}
  
  @SubscribeEvent
  public void onEvent_renderLivingEventPre(RenderLivingEvent.Pre event)
  {
    renderLivingEventPre(event);
  }
  
  public void renderLivingEventPre(RenderLivingEvent.Pre event) {}
  
  @SubscribeEvent
  public void onEvent_renderLivingEventPost(RenderLivingEvent.Post event)
  {
    renderLivingEventPost(event);
  }
  

  public void renderLivingEventPost(RenderLivingEvent.Post event) {}
  
  @SubscribeEvent
  public void onEvent_renderPlayerPre(RenderPlayerEvent.Pre event)
  {
    renderPlayerPre(event);
  }
  
  public void renderPlayerPre(RenderPlayerEvent.Pre event) {}
  
  @SubscribeEvent
  public void Event_renderPlayerPost(RenderPlayerEvent.Post event)
  {
    renderPlayerPost(event);
  }
  

  public void renderPlayerPost(RenderPlayerEvent.Post event) {}
  

  @SubscribeEvent
  public void onEvent_WorldEventUnload(WorldEvent.Unload event)
  {
    worldEventUnload(event);
  }
  
  public void worldEventUnload(WorldEvent.Unload event) {}
  
  @SubscribeEvent
  public void onEvent_EntityJoinWorldEvent(EntityJoinWorldEvent event)
  {
    entityJoinWorldEvent(event);
  }
  
  public void entityJoinWorldEvent(EntityJoinWorldEvent event) {}
}
