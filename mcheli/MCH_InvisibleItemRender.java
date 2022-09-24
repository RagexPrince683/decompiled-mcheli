package mcheli;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;

public class MCH_InvisibleItemRender implements IItemRenderer
{
  public MCH_InvisibleItemRender() {}
  
  public boolean handleRenderType(ItemStack item, IItemRenderer.ItemRenderType type)
  {
    return (type == IItemRenderer.ItemRenderType.EQUIPPED) || (type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON);
  }
  

  public boolean shouldUseRenderHelper(IItemRenderer.ItemRenderType type, ItemStack item, IItemRenderer.ItemRendererHelper helper) { return false; }
  
  public boolean useCurrentWeapon() { return false; }
  
  public void renderItem(IItemRenderer.ItemRenderType type, ItemStack item, Object... data) {}
}
