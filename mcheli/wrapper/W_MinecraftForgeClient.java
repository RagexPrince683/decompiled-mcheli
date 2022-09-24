package mcheli.wrapper;

import net.minecraftforge.client.IItemRenderer;

public class W_MinecraftForgeClient
{
  public W_MinecraftForgeClient() {}
  
  public static void registerItemRenderer(net.minecraft.item.Item item, IItemRenderer renderer) {
    if (item != null)
    {

      net.minecraftforge.client.MinecraftForgeClient.registerItemRenderer(item, renderer);
    }
  }
}
