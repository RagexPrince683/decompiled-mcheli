package mcheli;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.File;
import java.util.HashMap;
import java.util.Random;
import mcheli.wrapper.W_ModelBase;
import mcheli.wrapper.W_ResourcePath;
import mcheli.wrapper.modelloader.W_ModelCustom;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.client.model.IModelCustom;

@SideOnly(Side.CLIENT)
public class MCH_ModelManager
  extends W_ModelBase
{
  private static MCH_ModelManager instance = new MCH_ModelManager();
  
  private static HashMap<String, IModelCustom> map;
  private static ModelRenderer defaultModel;
  private static boolean forceReloadMode = false;
  
  private MCH_ModelManager()
  {
    map = new HashMap<String, IModelCustom>();
    defaultModel = null;
    defaultModel = new ModelRenderer(this, 0, 0);
    defaultModel.addBox(-5.0F, -5.0F, -5.0F, 10, 10, 10, 0.0F);
  }
  
  public static void setForceReloadMode(boolean b)
  {
    forceReloadMode = b;
  }
  
  public static IModelCustom load(String path, String name)
  {
    if ((name == null) || (name.isEmpty())) return null;
    return load(path + "/" + name);
  }
  

  public static IModelCustom load(String name)
  {
    if ((name == null) || (name.isEmpty()))
    {
      return null;
    }
    

    IModelCustom obj = (IModelCustom)map.get(name);
    if (obj != null)
    {
      if (forceReloadMode)
      {
        map.remove(name);
      }
      else
      {
        return obj;
      }
    }
    
    IModelCustom model = null;
    try
    {
      String filePathMqo = "/assets/mcheli/models/" + name + ".mqo";
      String filePathObj = "/assets/mcheli/models/" + name + ".obj";
      String filePathTcn = "/assets/mcheli/models/" + name + ".tcn";
      if (new File(MCH_MOD.sourcePath + filePathMqo).exists())
      {
        filePathMqo = W_ResourcePath.getModelPath() + "models/" + name + ".mqo";
        model = W_ModelBase.loadModel(filePathMqo);
      }
      else if (new File(MCH_MOD.sourcePath + filePathObj).exists())
      {
        filePathObj = W_ResourcePath.getModelPath() + "models/" + name + ".obj";
        model = W_ModelBase.loadModel(filePathObj);
      }
      else if (new File(MCH_MOD.sourcePath + filePathTcn).exists())
      {
        filePathTcn = W_ResourcePath.getModelPath() + "models/" + name + ".tcn";
        model = W_ModelBase.loadModel(filePathTcn);

      }
      

    }
    catch (Exception e)
    {

      e.printStackTrace();
      model = null;
    }
    if (model != null)
    {
      map.put(name, model);
      return model;
    }
    return null;
  }
  

























































  public static void render(String path, String name)
  {
    render(path + "/" + name);
  }
  
  public static void render(String name)
  {
    // Byte code:
    //   0: getstatic 29	mcheli/MCH_ModelManager:map	Ljava/util/HashMap;
    //   3: aload_0
    //   4: invokevirtual 80	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   7: checkcast 82	net/minecraftforge/client/model/IModelCustom
    //   10: astore_1
    //   11: aload_1
    //   12: ifnull +12 -> 24
    //   15: aload_1
    //   16: invokeinterface 138 1 0
    //   21: goto +9 -> 30
    //   24: getstatic 31	mcheli/MCH_ModelManager:defaultModel	Lnet/minecraft/client/model/ModelRenderer;
    //   27: ifnull +3 -> 30
    //   30: return
    // Line number table:
    //   Java source line #168	-> byte code offset #0
    //   Java source line #169	-> byte code offset #11
    //   Java source line #171	-> byte code offset #15
    //   Java source line #173	-> byte code offset #24
    //   Java source line #177	-> byte code offset #30
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	31	0	name	String
    //   10	6	1	model	IModelCustom
  }
  
  public static void renderPart(String name, String partName)
  {
    IModelCustom model = (IModelCustom)map.get(name);
    if (model != null)
    {
      model.renderPart(partName);
    }
  }
  
  public static void renderLine(String path, String name, int startLine, int maxLine)
  {
    IModelCustom model = (IModelCustom)map.get(path + "/" + name);
    if ((model instanceof W_ModelCustom))
    {
      ((W_ModelCustom)model).renderAllLine(startLine, maxLine);
    }
  }
  
  public static void render(String path, String name, int startFace, int maxFace) {
    IModelCustom model = (IModelCustom)map.get(path + "/" + name);
    if ((model instanceof W_ModelCustom))
    {
      ((W_ModelCustom)model).renderAll(startFace, maxFace);
    }
  }
  
  public static int getVertexNum(String path, String name) {
    IModelCustom model = (IModelCustom)map.get(path + "/" + name);
    if ((model instanceof W_ModelCustom))
    {
      return ((W_ModelCustom)model).getVertexNum();
    }
    return 0;
  }
  
  public static W_ModelCustom get(String path, String name)
  {
    IModelCustom model = (IModelCustom)map.get(path + "/" + name);
    if ((model instanceof W_ModelCustom))
    {
      return (W_ModelCustom)model;
    }
    return null;
  }
  
  private static Random rand = new Random();
  
  public static W_ModelCustom getRandome() {
    int size = map.size();
    int idx; int index; for (int i = 0; i < 10; i++)
    {
      idx = 0;
      index = rand.nextInt(size);
      for (IModelCustom model : map.values())
      {
        if ((idx >= index) && ((model instanceof W_ModelCustom)))
        {
          return (W_ModelCustom)model;
        }
        idx++;
      }
    }
    return null;
  }
  
  public static boolean containsModel(String path, String name)
  {
    return containsModel(path + "/" + name);
  }
  
  public static boolean containsModel(String name) {
    return map.containsKey(name);
  }
}
