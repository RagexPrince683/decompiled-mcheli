package mcheli.aircraft;

import java.util.List;
import mcheli.MCH_Achievement;
import mcheli.MCH_Config;
import mcheli.MCH_ConfigPrm;
import mcheli.wrapper.W_EntityPlayer;
import mcheli.wrapper.W_Item;
import mcheli.wrapper.W_MovingObjectPosition;
import mcheli.wrapper.W_WorldFunc;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public abstract class MCH_ItemAircraft extends W_Item
{
  public MCH_ItemAircraft(int i)
  {
    super(i);
  }
  
  private static boolean isRegistedDispenseBehavior = false;
  
  public static void registerDispenseBehavior(Item item)
  {
    if (isRegistedDispenseBehavior == true) { return;
    }
    
    BlockDispenser.dispenseBehaviorRegistry.putObject(item, new MCH_ItemAircraftDispenseBehavior());
  }
  
  public abstract MCH_AircraftInfo getAircraftInfo();
  
  public abstract MCH_EntityAircraft createAircraft(World paramWorld, double paramDouble1, double paramDouble2, double paramDouble3, ItemStack paramItemStack);
  
  public MCH_EntityAircraft onTileClick(ItemStack itemStack, World world, float rotationYaw, int x, int y, int z) {
    MCH_EntityAircraft ac = createAircraft(world, x + 0.5F, y + 1.0F, z + 0.5F, itemStack);
    if (ac == null)
    {
      return null;
    }
    
    ac.initRotationYaw(((MathHelper.floor_double(rotationYaw * 4.0F / 360.0F + 0.5D) & 0x3) - 1) * 90);
    
    if (!world.getCollidingBoundingBoxes(ac, ac.boundingBox.expand(-0.1D, -0.1D, -0.1D)).isEmpty())
    {
      return null;
    }
    
    return ac;
  }
  
  public String toString()
  {
    MCH_AircraftInfo info = getAircraftInfo();
    if (info != null)
    {
      return super.toString() + "(" + info.getDirectoryName() + ":" + info.name + ")";
    }
    

    return super.toString() + "(null)";
  }
  




  public ItemStack onItemRightClick(ItemStack par1ItemStack, World world, EntityPlayer player)
  {
    float f = 1.0F;
    float f1 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f;
    float f2 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f;
    double d0 = player.prevPosX + (player.posX - player.prevPosX) * f;
    double d1 = player.prevPosY + (player.posY - player.prevPosY) * f + 1.62D - player.yOffset;
    double d2 = player.prevPosZ + (player.posZ - player.prevPosZ) * f;
    Vec3 vec3 = W_WorldFunc.getWorldVec3(world, d0, d1, d2);
    float f3 = MathHelper.cos(-f2 * 0.017453292F - 3.1415927F);
    float f4 = MathHelper.sin(-f2 * 0.017453292F - 3.1415927F);
    float f5 = -MathHelper.cos(-f1 * 0.017453292F);
    float f6 = MathHelper.sin(-f1 * 0.017453292F);
    float f7 = f4 * f5;
    float f8 = f3 * f5;
    double d3 = 5.0D;
    Vec3 vec31 = vec3.addVector(f7 * d3, f6 * d3, f8 * d3);
    MovingObjectPosition mop = W_WorldFunc.clip(world, vec3, vec31, true);
    
    if (mop == null)
    {
      return par1ItemStack;
    }
    
    Vec3 vec32 = player.getLook(f);
    boolean flag = false;
    float f9 = 1.0F;
    List list = world.getEntitiesWithinAABBExcludingEntity(player, player.boundingBox.addCoord(vec32.xCoord * d3, vec32.yCoord * d3, vec32.zCoord * d3).expand(f9, f9, f9));
    

    for (int i = 0; i < list.size(); i++)
    {
      Entity entity = (Entity)list.get(i);
      
      if (entity.canBeCollidedWith())
      {
        float f10 = entity.getCollisionBorderSize();
        AxisAlignedBB axisalignedbb = entity.boundingBox.expand(f10, f10, f10);
        
        if (axisalignedbb.isVecInside(vec3))
        {
          flag = true;
        }
      }
    }
    
    if (flag)
    {
      return par1ItemStack;
    }
    

    if (W_MovingObjectPosition.isHitTypeTile(mop))
    {
      if (MCH_Config.PlaceableOnSpongeOnly.prmBool)
      {
        MCH_AircraftInfo acInfo = getAircraftInfo();
        if ((acInfo != null) && (acInfo.isFloat) && (!acInfo.canMoveOnGround))
        {
          if (world.getBlock(mop.blockX, mop.blockY - 1, mop.blockZ) != Blocks.sponge)
          {
            return par1ItemStack;
          }
          for (int x = -1; x <= 1; x++)
          {
            for (int z = -1; z <= 1; z++)
            {
              if (world.getBlock(mop.blockX + x, mop.blockY, mop.blockZ + z) != Blocks.water)
              {
                return par1ItemStack;
              }
            }
          }
        }
        else
        {
          Block block = world.getBlock(mop.blockX, mop.blockY, mop.blockZ);
          if (!(block instanceof net.minecraft.block.BlockSponge))
          {
            return par1ItemStack;
          }
        }
      }
      spawnAircraft(par1ItemStack, world, player, mop.blockX, mop.blockY, mop.blockZ);
    }
    

    return par1ItemStack;
  }
  
  public MCH_EntityAircraft spawnAircraft(ItemStack itemStack, World world, EntityPlayer player, int x, int y, int z)
  {
    MCH_EntityAircraft ac = onTileClick(itemStack, world, player.rotationYaw, x, y, z);
    
    if (ac != null)
    {
      if ((ac.getAcInfo() != null) && (ac.getAcInfo().creativeOnly) && (!player.capabilities.isCreativeMode))
      {
        return null;
      }
      
      if (ac.isUAV())
      {
        if (world.isRemote)
        {
          if (ac.isSmallUAV())
          {
            W_EntityPlayer.addChatMessage(player, "Please use the UAV station OR Portable Controller");
          }
          else
          {
            W_EntityPlayer.addChatMessage(player, "Please use the UAV station");
          }
        }
        ac = null;
      }
      else
      {
        if (!world.isRemote)
        {
          ac.getAcDataFromItem(itemStack);
          world.spawnEntityInWorld(ac);
          

          MCH_Achievement.addStat(player, MCH_Achievement.welcome, 1);
        }
        
        if (!player.capabilities.isCreativeMode)
        {
          itemStack.stackSize -= 1;
        }
      }
    }
    
    return ac;
  }
  
  public void rideEntity(ItemStack item, Entity target, EntityPlayer player)
  {
    if (!MCH_Config.PlaceableOnSpongeOnly.prmBool)
    {
      if (((target instanceof EntityMinecartEmpty)) && (target.riddenByEntity == null))
      {
        MCH_EntityAircraft ac = spawnAircraft(item, player.worldObj, player, (int)target.posX, (int)target.posY + 2, (int)target.posZ);
        
        if ((!player.worldObj.isRemote) && (ac != null))
        {
          ac.mountEntity(target);
        }
      }
    }
  }
}
