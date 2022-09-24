package mcheli.wrapper;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityEvent.CanUpdate;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;

public class W_EventHook
{
  @SubscribeEvent
  public void onEvent_entitySpawn(EntityJoinWorldEvent event)
  {
    entitySpawn(event);
  }
  
  public void entitySpawn(EntityJoinWorldEvent event) {}
  
  @SubscribeEvent
  public void onEvent_livingHurtEvent(LivingHurtEvent event)
  {
    livingHurtEvent(event);
  }
  
  public void livingHurtEvent(LivingHurtEvent event) {}
  
  @SubscribeEvent
  public void onEvent_livingAttackEvent(LivingAttackEvent event)
  {
    livingAttackEvent(event);
  }
  
  public void livingAttackEvent(LivingAttackEvent event) {}
  
  @SubscribeEvent
  public void onEvent_entityInteractEvent(EntityInteractEvent event)
  {
    entityInteractEvent(event);
  }
  
  public void entityInteractEvent(EntityInteractEvent event) {}
  
  @SubscribeEvent
  public void onEvent_entityCanUpdate(EntityEvent.CanUpdate event)
  {
    entityCanUpdate(event);
  }
  
  public void entityCanUpdate(EntityEvent.CanUpdate event) {}
  
  @SubscribeEvent
  public void onEvent_commandEvent(CommandEvent event)
  {
    commandEvent(event);
  }
  
  public void commandEvent(CommandEvent event) {}
}