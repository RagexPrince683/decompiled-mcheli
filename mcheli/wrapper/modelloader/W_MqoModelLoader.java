package mcheli.wrapper.modelloader;

import java.net.URL;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.client.model.IModelCustomLoader;
import net.minecraftforge.client.model.ModelFormatException;

public class W_MqoModelLoader
  implements IModelCustomLoader
{
  public W_MqoModelLoader() {}
  
  public String getType()
  {
    return "Metasequoia model";
  }
  
  private static final String[] types = { "mqo" };
  
  public String[] getSuffixes()
  {
    return types;
  }
  
  public IModelCustom loadInstance(ResourceLocation resource)
    throws ModelFormatException
  {
	W_MetasequoiaObject obj = new W_MetasequoiaObject(resource);
    return obj;
  }
  
  public IModelCustom loadInstance(String resourceName, URL resource)
    throws ModelFormatException
  {
    return new W_MetasequoiaObject(resourceName, resource);
  }
}
