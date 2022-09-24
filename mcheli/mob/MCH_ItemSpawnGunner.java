package mcheli.mob;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import java.util.UUID;
import mcheli.aircraft.MCH_AircraftInfo;
import mcheli.aircraft.MCH_EntityAircraft;
import mcheli.aircraft.MCH_EntitySeat;
import mcheli.wrapper.W_Item;
import mcheli.wrapper.W_WorldFunc;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;



public class MCH_ItemSpawnGunner
  extends W_Item
{
  public int primaryColor = 16777215;
  public int secondaryColor = 16777215;
  public int targetType = 0;
  
  @SideOnly(Side.CLIENT)
  private IIcon theIcon;
  
  public MCH_ItemSpawnGunner()
  {
    this.maxStackSize = 1;
    setCreativeTab(CreativeTabs.tabTransport);
  }
  




  public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player)
  {
    float f = 1.0F;
    float pitch = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f;
    float yaw = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f;
    double dx = player.prevPosX + (player.posX - player.prevPosX) * f;
    double dy = player.prevPosY + (player.posY - player.prevPosY) * f + 1.62D - player.yOffset;
    double dz = player.prevPosZ + (player.posZ - player.prevPosZ) * f;
    Vec3 vec3 = Vec3.createVectorHelper(dx, dy, dz);
    float f3 = MathHelper.cos(-yaw * 0.017453292F - 3.1415927F);
    float f4 = MathHelper.sin(-yaw * 0.017453292F - 3.1415927F);
    float f5 = -MathHelper.cos(-pitch * 0.017453292F);
    float f6 = MathHelper.sin(-pitch * 0.017453292F);
    float f7 = f4 * f5;
    float f8 = f3 * f5;
    double d3 = 5.0D;
    Vec3 vec31 = vec3.addVector(f7 * d3, f6 * d3, f8 * d3);
    List list = world.getEntitiesWithinAABB(MCH_EntityGunner.class, player.boundingBox.expand(5.0D, 5.0D, 5.0D));
    Entity target = null;
    for (int i = 0; i < list.size(); i++)
    {
      MCH_EntityGunner gunner = (MCH_EntityGunner)list.get(i);
      if (gunner.boundingBox.calculateIntercept(vec3, vec31) != null)
      {
        if ((target == null) || (player.getDistanceSqToEntity(gunner) < player.getDistanceSqToEntity(target)))
        {
          target = gunner;
        }
      }
    }
    
    if (target == null)
    {
      list = world.getEntitiesWithinAABB(MCH_EntitySeat.class, player.boundingBox.expand(5.0D, 5.0D, 5.0D));
      for (int i = 0; i < list.size(); i++)
      {
        MCH_EntitySeat seat = (MCH_EntitySeat)list.get(i);
        if ((seat.getParent() != null) && (seat.getParent().getAcInfo() != null) && (seat.boundingBox.calculateIntercept(vec3, vec31) != null))
        {



          if ((target == null) || (player.getDistanceSqToEntity(seat) < player.getDistanceSqToEntity(target)))
          {
            if ((seat.riddenByEntity instanceof MCH_EntityGunner))
            {
              target = seat.riddenByEntity;
            }
            else
            {
              target = seat;
            }
          }
        }
      }
    }
    
    if (target == null)
    {
      list = world.getEntitiesWithinAABB(MCH_EntityAircraft.class, player.boundingBox.expand(5.0D, 5.0D, 5.0D));
      for (int i = 0; i < list.size(); i++)
      {
        MCH_EntityAircraft ac = (MCH_EntityAircraft)list.get(i);
        if ((!ac.isUAV()) && (ac.getAcInfo() != null) && (ac.boundingBox.calculateIntercept(vec3, vec31) != null))
        {



          if ((target == null) || (player.getDistanceSqToEntity(ac) < player.getDistanceSqToEntity(target)))
          {
            if ((ac.getRiddenByEntity() instanceof MCH_EntityGunner))
            {
              target = ac.getRiddenByEntity();
            }
            else
            {
              target = ac;
            }
          }
        }
      }
    }
    
    if ((target instanceof MCH_EntityGunner))
    {
      target.interactFirst(player);
      return itemStack;
    }
    
    if ((this.targetType == 1) && (!world.isRemote) && (player.getTeam() == null))
    {
      player.addChatMessage(new ChatComponentText("You are not on team."));
      return itemStack;
    }
    
    if (target == null)
    {
      if (!world.isRemote)
      {
        player.addChatMessage(new ChatComponentText("Right click to seat."));
      }
      return itemStack;
    }
    

    if (!world.isRemote)
    {
      MCH_EntityGunner gunner = new MCH_EntityGunner(world, target.posX, target.posY, target.posZ);
      gunner.rotationYaw = (((MathHelper.floor_double(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 0x3) - 1) * 90);
      gunner.isCreative = player.capabilities.isCreativeMode;
      gunner.targetType = this.targetType;
      gunner.ownerUUID = player.getUniqueID().toString();
      ScorePlayerTeam team = world.getScoreboard().getPlayersTeam(player.getDisplayName());
      if (team != null)
      {
        gunner.setTeamName(team.getRegisteredName());
      }
      world.spawnEntityInWorld(gunner);
      gunner.mountEntity(target);
      W_WorldFunc.MOD_playSoundAtEntity(gunner, "wrench", 1.0F, 3.0F);
      MCH_EntityAircraft ac = (target instanceof MCH_EntityAircraft) ? (MCH_EntityAircraft)target : ((MCH_EntitySeat)target).getParent();
      player.addChatMessage(new ChatComponentText("The gunner was put on " + EnumChatFormatting.GOLD + ac.getAcInfo().displayName + EnumChatFormatting.RESET + " seat " + (ac.getSeatIdByEntity(gunner) + 1) + " by " + ScorePlayerTeam.formatPlayerName(player.getTeam(), player.getDisplayName())));
    }
    



    if (!player.capabilities.isCreativeMode)
    {
      itemStack.stackSize -= 1;
    }
    
    return itemStack;
  }
  
  @SideOnly(Side.CLIENT)
  public int getColorFromItemStack(ItemStack itemStack, int layer)
  {
    return layer == 0 ? this.primaryColor : this.secondaryColor;
  }
  
  @SideOnly(Side.CLIENT)
  public boolean requiresMultipleRenderPasses()
  {
    return true;
  }
  
  @SideOnly(Side.CLIENT)
  public IIcon getIconFromDamageForRenderPass(int p_77618_1_, int p_77618_2_)
  {
    return p_77618_2_ > 0 ? this.theIcon : super.getIconFromDamageForRenderPass(p_77618_1_, p_77618_2_);
  }
  
  @SideOnly(Side.CLIENT)
  public void registerIcons(IIconRegister icon)
  {
    super.registerIcons(icon);
    this.theIcon = icon.registerIcon(getIconString() + "_overlay");
  }
}
