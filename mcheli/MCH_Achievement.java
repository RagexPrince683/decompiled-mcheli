package mcheli;

import java.util.HashMap;
import mcheli.aircraft.MCH_AircraftInfo;
import mcheli.helicopter.MCH_HeliInfo;
import mcheli.helicopter.MCH_HeliInfoManager;
import mcheli.vehicle.MCH_VehicleInfoManager;
import mcheli.wrapper.W_Achievement;
import mcheli.wrapper.W_Item;
import mcheli.wrapper.W_LanguageRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.stats.Achievement;
import net.minecraft.world.World;
import net.minecraftforge.common.AchievementPage;

public class MCH_Achievement
{
  public static Achievement welcome = null;
  public static Achievement supplyFuel = null;
  public static Achievement supplyAmmo = null;
  public static Achievement aintWarHell = null;
  public static Achievement reliefSupplies = null;
  public static Achievement rideValkyries = null;
  
  public MCH_Achievement() {}
  
  public static void PreInit() { Item item = getAnyAircraftIcon("ah-64");
    
    int BC = 1;
    int BR = 1;
    
    String name = "McHeliWelcome";
    welcome = W_Achievement.registerAchievement("mcheli" + name, name, 1, 1, item, null);
    W_LanguageRegistry.addNameForObject(welcome, "en_US", "Welcome to MC Helicopter MOD", name, "Put the helicopter");
    W_LanguageRegistry.addNameForObject(welcome, "ja_JP", "MC Helicopter MOD ?????", name, "?????????");
    
    name = "McHeliSupplyFuel";
    supplyFuel = W_Achievement.registerAchievement("mcheli" + name, name, -1, 1, MCH_MOD.itemFuel, null);
    W_LanguageRegistry.addNameForObject(supplyFuel, "en_US", "Refueling", name, "Refuel aircraft");
    W_LanguageRegistry.addNameForObject(supplyFuel, "ja_JP", "????", name, "?????");
    
    item = getAircraftIcon("ammo_box");
    name = "McHeliSupplyAmmo";
    supplyAmmo = W_Achievement.registerAchievement("mcheli" + name, name, 3, 1, item, null);
    W_LanguageRegistry.addNameForObject(supplyAmmo, "en_US", "Supply ammo", name, "Supply ammo to the aircraft");
    W_LanguageRegistry.addNameForObject(supplyAmmo, "ja_JP", "????", name, "?????");
    
    item = getAircraftIcon("uh-1c");
    name = "McHeliRideValkyries";
    rideValkyries = W_Achievement.registerAchievement("mcheli" + name, name, -1, 3, item, null);
    W_LanguageRegistry.addNameForObject(rideValkyries, "en_US", "Ride Of The Valkyries", name, "?");
    W_LanguageRegistry.addNameForObject(rideValkyries, "ja_JP", "?????????", name, "?");
    
    item = getAircraftIcon("mh-60l_dap");
    name = "McHeliAintWarHell";
    aintWarHell = W_Achievement.registerAchievement("mcheli" + name, name, 3, 3, item, null);
    W_LanguageRegistry.addNameForObject(aintWarHell, "en_US", "Ain't war hell?", name, "?");
    W_LanguageRegistry.addNameForObject(aintWarHell, "ja_JP", "??????????", name, "?");
    
    item = MCH_MOD.itemContainer;
    name = "McHeliReliefSupplies";
    reliefSupplies = W_Achievement.registerAchievement("mcheli" + name, name, -1, -1, item, null);
    W_LanguageRegistry.addNameForObject(reliefSupplies, "en_US", "Relief supplies", name, "Drop a container");
    W_LanguageRegistry.addNameForObject(reliefSupplies, "ja_JP", "????", name, "???????");
    

    Achievement[] achievements = { welcome, supplyFuel, supplyAmmo, aintWarHell, rideValkyries, reliefSupplies };
    AchievementPage.registerAchievementPage(new AchievementPage("MC Helicopter", achievements));
  }
  
  public static Item getAircraftIcon(String defaultIconAircraft)
  {
    Item item = W_Item.getItemByName("stone");
    MCH_AircraftInfo info = MCH_HeliInfoManager.get(defaultIconAircraft);
    if ((info != null) && (info.getItem() != null))
    {
      return info.getItem();
    }
    
    info = mcheli.plane.MCP_PlaneInfoManager.get(defaultIconAircraft);
    if ((info != null) && (info.getItem() != null))
    {
      return info.getItem();
    }
    
    info = MCH_VehicleInfoManager.get(defaultIconAircraft);
    if ((info != null) && (info.getItem() != null))
    {
      return info.getItem();
    }
    
    return item;
  }
  
  public static Item getAnyAircraftIcon(String defaultIconAircraft)
  {
    Item item = W_Item.getItemByName("stone");
    if (MCH_HeliInfoManager.map.size() > 0)
    {
      MCH_HeliInfo info = MCH_HeliInfoManager.get(defaultIconAircraft);
      if ((info != null) && (info.item != null))
      {
        item = info.item;
      }
      else
      {
        for (MCH_HeliInfo i : MCH_HeliInfoManager.map.values())
        {
          if (i.item != null)
          {
            item = i.item;
            break;
          }
        }
      }
    }
    return item;
  }
  
  public static void addStat(Entity player, Achievement a, int i)
  {
    if (a != null)
    {
      if (((player instanceof EntityPlayer)) && (!player.worldObj.isRemote))
      {
        ((EntityPlayer)player).addStat(a, i);
      }
    }
  }
}
