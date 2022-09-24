package mcheli.block;

import java.util.List;
import java.util.Map;
import mcheli.MCH_IRecipeList;
import mcheli.MCH_ItemRecipe;
import mcheli.MCH_Lib;
import mcheli.MCH_MOD;
import mcheli.helicopter.MCH_HeliInfoManager;
import mcheli.tank.MCH_TankInfoManager;
import mcheli.vehicle.MCH_VehicleInfoManager;
import mcheli.wrapper.W_Block;
import mcheli.wrapper.W_EntityPlayer;
import mcheli.wrapper.W_WorldFunc;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class MCH_DraftingTableGuiContainer extends Container
{
  public final EntityPlayer player;
  public final int posX;
  public final int posY;
  public final int posZ;
  public final int outputSlotIndex;
  private IInventory outputSlot = new InventoryCraftResult();
  
  public MCH_DraftingTableGuiContainer(EntityPlayer player, int posX, int posY, int posZ)
  {
    this.player = player;
    this.posX = posX;
    this.posY = posY;
    this.posZ = posZ;
    

    for (int y = 0; y < 3; y++)
    {
      for (int x = 0; x < 9; x++)
      {
        addSlotToContainer(new Slot(player.inventory, 9 + x + y * 9, 30 + x * 18, 140 + y * 18));
      }
    }
    

    for (int x = 0; x < 9; x++)
    {
      addSlotToContainer(new Slot(player.inventory, x, 30 + x * 18, 198));
    }
    
    this.outputSlotIndex = this.inventoryItemStacks.size();
    Slot a = new Slot(this.outputSlot, this.outputSlotIndex, 178, 90)
    {
      public boolean isItemValid(ItemStack par1ItemStack) { return false; }
    };
    addSlotToContainer(a);
    
    MCH_Lib.DbgLog(player.worldObj, "MCH_DraftingTableGuiContainer.MCH_DraftingTableGuiContainer", new Object[0]);
  }
  

  public void detectAndSendChanges()
  {
    super.detectAndSendChanges();
  }
  

  public boolean canInteractWith(EntityPlayer player)
  {
    Block block = W_WorldFunc.getBlock(player.worldObj, this.posX, this.posY, this.posZ);
    
    if ((W_Block.isEqual(block, MCH_MOD.blockDraftingTable)) || (W_Block.isEqual(block, MCH_MOD.blockDraftingTableLit)))
    {
      return player.getDistanceSq(this.posX, this.posY, this.posZ) <= 144.0D;
    }
    
    return false;
  }
  

  public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex)
  {
    ItemStack itemstack = null;
    Slot slot = (Slot)this.inventorySlots.get(slotIndex);
    
    if ((slot != null) && (slot.getHasStack()))
    {
      ItemStack itemstack1 = slot.getStack();
      itemstack = itemstack1.copy();
      
      if (slotIndex == this.outputSlotIndex)
      {
        if (!mergeItemStack(itemstack1, 0, 36, true))
        {
          return null;
        }
        
        slot.onSlotChange(itemstack1, itemstack);
      }
      else
      {
        return null;
      }
      
      if (itemstack1.stackSize == 0)
      {
        slot.putStack((ItemStack)null);
      }
      else
      {
        slot.onSlotChanged();
      }
      
      if (itemstack1.stackSize == itemstack.stackSize)
      {
        return null;
      }
      
      slot.onPickupFromSlot(player, itemstack1);
    }
    
    return itemstack;
  }
  
  public void onContainerClosed(EntityPlayer player)
  {
    super.onContainerClosed(player);
    
    if (!player.worldObj.isRemote)
    {
      ItemStack itemstack = getSlot(this.outputSlotIndex).getStack();
      
      if (itemstack != null)
      {
        W_EntityPlayer.dropPlayerItemWithRandomChoice(player, itemstack, false, false);
      }
    }
    
    MCH_Lib.DbgLog(player.worldObj, "MCH_DraftingTableGuiContainer.onContainerClosed", new Object[0]);
  }
  
  public void createRecipeItem(Item outputItem, Map<Item, Integer> map)
  {
    boolean isCreativeMode = this.player.capabilities.isCreativeMode;
    if ((getSlot(this.outputSlotIndex).getHasStack()) && (!isCreativeMode))
    {
      MCH_Lib.DbgLog(this.player.worldObj, "MCH_DraftingTableGuiContainer.createRecipeItem:OutputSlot is not empty", new Object[0]);
      return;
    }
    if (outputItem == null)
    {
      MCH_Lib.DbgLog(this.player.worldObj, "Error:MCH_DraftingTableGuiContainer.createRecipeItem:outputItem = null", new Object[0]);
      return;
    }
    if ((map == null) || (map.size() <= 0))
    {
      MCH_Lib.DbgLog(this.player.worldObj, "Error:MCH_DraftingTableGuiContainer.createRecipeItem:map is null : " + map, new Object[0]);
      return;
    }
    
    ItemStack itemStack = new ItemStack(outputItem);
    boolean result = false;
    IRecipe recipe = null;
    MCH_IRecipeList[] recipeLists = { MCH_ItemRecipe.getInstance(), MCH_HeliInfoManager.getInstance(), mcheli.plane.MCP_PlaneInfoManager.getInstance(), MCH_VehicleInfoManager.getInstance(), MCH_TankInfoManager.getInstance() };
    





    for (MCH_IRecipeList rl : recipeLists)
    {
      int index = searchRecipeFromList(rl, itemStack);
      if (index >= 0)
      {
        recipe = isValidRecipe(rl, itemStack, index, map);
        break;
      }
    }
    if (recipe != null)
    {
      if ((isCreativeMode) || (MCH_Lib.canPlayerCreateItem(recipe, this.player.inventory)))
      {
        for (Item key : map.keySet())
        {
          for (int i = 0; i < ((Integer)map.get(key)).intValue(); i++)
          {
            if (!isCreativeMode)
            {
              W_EntityPlayer.consumeInventoryItem(this.player, key);
            }
            getSlot(this.outputSlotIndex).putStack(recipe.getRecipeOutput().copy());
            result = true;
          }
        }
      }
    }
    
    MCH_Lib.DbgLog(this.player.worldObj, "MCH_DraftingTableGuiContainer:Result=" + result + ":Recipe=" + recipe + " :" + outputItem.getUnlocalizedName() + ": map=" + map, new Object[0]);
  }
  


  public IRecipe isValidRecipe(MCH_IRecipeList list, ItemStack itemStack, int startIndex, Map<Item, Integer> map)
  {
    for (int index = startIndex; (index >= 0) && (index < list.getRecipeListSize()); index++)
    {
      IRecipe recipe = list.getRecipe(index);
      if (itemStack.isItemEqual(recipe.getRecipeOutput()))
      {
        Map<Item, Integer> mapRecipe = MCH_Lib.getItemMapFromRecipe(recipe);
        boolean isEqual = true;
        for (Item key : map.keySet())
        {
          if ((!mapRecipe.containsKey(key)) || (mapRecipe.get(key) != map.get(key)))
          {
            isEqual = false;
            break;
          }
        }
        if (isEqual)
        {
          return recipe;
        }
      }
      else
      {
        return null;
      }
    }
    return null;
  }
  
  public int searchRecipeFromList(MCH_IRecipeList list, ItemStack item)
  {
    for (int i = 0; i < list.getRecipeListSize(); i++)
    {
      if (list.getRecipe(i).getRecipeOutput().isItemEqual(item))
      {
        return i;
      }
    }
    return -1;
  }
}
