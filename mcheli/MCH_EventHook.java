package mcheli;

import java.util.List;
import java.util.UUID;
import mcheli.aircraft.MCH_AircraftInfo;
import mcheli.aircraft.MCH_EntityAircraft;
import mcheli.aircraft.MCH_EntitySeat;
import mcheli.aircraft.MCH_ItemAircraft;
import mcheli.chain.MCH_ItemChain;
import mcheli.weapon.MCH_EntityBaseBullet;
import mcheli.wrapper.W_Entity;
import mcheli.wrapper.W_EntityPlayer;
import mcheli.wrapper.W_EventHook;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityEvent.CanUpdate;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;

public class MCH_EventHook extends W_EventHook
{
  public MCH_EventHook() {}
  
  public void commandEvent(CommandEvent event)
  {
    mcheli.command.MCH_Command.onCommandEvent(event);
  }
  
  public void entitySpawn(EntityJoinWorldEvent event)
  {
    if ((mcheli.wrapper.W_Lib.isEntityLivingBase(event.entity)) && (!W_EntityPlayer.isPlayer(event.entity)))
    {
      event.entity.renderDistanceWeight *= MCH_Config.MobRenderDistanceWeight.prmDouble;
    }
    else if ((event.entity instanceof MCH_EntityAircraft))
    {
      MCH_EntityAircraft aircraft = (MCH_EntityAircraft)event.entity;
      if (!aircraft.worldObj.isRemote)
      {


        if (!aircraft.isCreatedSeats())
        {
          aircraft.createSeats(UUID.randomUUID().toString());

        }
        

      }
      


    }
    else if (W_EntityPlayer.isPlayer(event.entity))
    {

      Entity e = event.entity;
      boolean b = Float.isNaN(e.rotationPitch);
      b |= Float.isNaN(e.prevRotationPitch);
      b |= Float.isInfinite(e.rotationPitch);
      b |= Float.isInfinite(e.prevRotationPitch);
      if (b)
      {
        MCH_Lib.Log(event.entity, "### EntityJoinWorldEvent Error:Player invalid rotation pitch(" + e.rotationPitch + ")", new Object[0]);
        e.rotationPitch = 0.0F;
        e.prevRotationPitch = 0.0F;
      }
      
      b = Float.isInfinite(e.rotationYaw);
      b |= Float.isInfinite(e.prevRotationYaw);
      b |= Float.isNaN(e.rotationYaw);
      b |= Float.isNaN(e.prevRotationYaw);
      if (b)
      {
        MCH_Lib.Log(event.entity, "### EntityJoinWorldEvent Error:Player invalid rotation yaw(" + e.rotationYaw + ")", new Object[0]);
        e.rotationYaw = 0.0F;
        e.prevRotationYaw = 0.0F;
      }
      
      if ((!e.worldObj.isRemote) && ((event.entity instanceof EntityPlayerMP)))
      {
        MCH_Lib.DbgLog(false, "EntityJoinWorldEvent:" + event.entity, new Object[0]);
        MCH_PacketNotifyServerSettings.send((EntityPlayerMP)event.entity);
      }
    }
  }
  
  public void livingAttackEvent(LivingAttackEvent event)
  {
    MCH_EntityAircraft ac = getRiddenAircraft(event.entity);
    if (ac == null) return;
    if (ac.getAcInfo() == null) return;
    if (ac.isDestroyed()) return;
    if (ac.getAcInfo().damageFactor > 0.0F) { return;
    }
    Entity attackEntity = event.source.getEntity();
    if (attackEntity == null)
    {
      event.setCanceled(true);



    }
    else if (W_Entity.isEqual(attackEntity, event.entity))
    {
      event.setCanceled(true);




    }
    else if (ac.isMountedEntity(attackEntity))
    {
      event.setCanceled(true);

    }
    else
    {
      MCH_EntityAircraft atkac = getRiddenAircraft(attackEntity);
      if (W_Entity.isEqual(atkac, ac))
      {
        event.setCanceled(true);
      }
    }
  }
  






  public void livingHurtEvent(LivingHurtEvent event)
  {
    MCH_EntityAircraft ac = getRiddenAircraft(event.entity);
    if (ac == null) return;
    if (ac.getAcInfo() == null) return;
    if (ac.isDestroyed()) { return;
    }
    Entity attackEntity = event.source.getEntity();
    if (attackEntity == null)
    {
      ac.attackEntityFrom(event.source, event.ammount * 2.0F);
      event.ammount *= ac.getAcInfo().damageFactor;



    }
    else if (W_Entity.isEqual(attackEntity, event.entity))
    {
      ac.attackEntityFrom(event.source, event.ammount * 2.0F);
      event.ammount *= ac.getAcInfo().damageFactor;




    }
    else if (ac.isMountedEntity(attackEntity))
    {
      event.ammount = 0.0F;
      event.setCanceled(true);

    }
    else
    {
      MCH_EntityAircraft atkac = getRiddenAircraft(attackEntity);
      if (W_Entity.isEqual(atkac, ac))
      {
        event.ammount = 0.0F;
        event.setCanceled(true);
      }
      else
      {
        ac.attackEntityFrom(event.source, event.ammount * 2.0F);
        event.ammount *= ac.getAcInfo().damageFactor;
      }
    }
  }
  


  public MCH_EntityAircraft getRiddenAircraft(Entity entity)
  {
    MCH_EntityAircraft ac = null;
    Entity ridden = entity.ridingEntity;
    

    if ((ridden instanceof MCH_EntityAircraft))
    {
      ac = (MCH_EntityAircraft)ridden;

    }
    else if ((ridden instanceof MCH_EntitySeat))
    {
      ac = ((MCH_EntitySeat)ridden).getParent();
    }
    

    if (ac == null)
    {

      List list = entity.worldObj.getEntitiesWithinAABB(MCH_EntityAircraft.class, entity.boundingBox.expand(50.0D, 50.0D, 50.0D));
      
      if (list != null)
      {
        for (int i = 0; i < list.size(); i++)
        {
          MCH_EntityAircraft tmp = (MCH_EntityAircraft)list.get(i);
          if (tmp.isMountedEntity(entity))
          {
            return tmp;
          }
        }
      }
    }
    return ac;
  }
  
  public void entityInteractEvent(EntityInteractEvent event)
  {
    ItemStack item = event.entityPlayer.getHeldItem();
    if (item == null) { return;
    }
    if ((item.getItem() instanceof MCH_ItemChain))
    {
      MCH_ItemChain.interactEntity(item, event.target, event.entityPlayer, event.entityPlayer.worldObj);
      

      event.setCanceled(true);
    }
    else if ((item.getItem() instanceof MCH_ItemAircraft))
    {
      ((MCH_ItemAircraft)item.getItem()).rideEntity(item, event.target, event.entityPlayer);
    }
  }
  
  public void entityCanUpdate(EntityEvent.CanUpdate event)
  {
    if ((event.entity instanceof MCH_EntityBaseBullet))
    {
      MCH_EntityBaseBullet bullet = (MCH_EntityBaseBullet)event.entity;
      bullet.setDead();
    }
  }
}
