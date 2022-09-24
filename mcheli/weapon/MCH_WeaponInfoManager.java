package mcheli.weapon;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import mcheli.MCH_CommonProxy;
import mcheli.MCH_Lib;
import mcheli.MCH_MOD;
import mcheli.wrapper.W_Item;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;


public class MCH_WeaponInfoManager
{
  private static MCH_WeaponInfoManager instance = new MCH_WeaponInfoManager();
  private static HashMap<String, MCH_WeaponInfo> map;
  private static String lastPath;
  
  private MCH_WeaponInfoManager()
  {
    map = new HashMap();
  }
  
  public static boolean reload()
  {
    boolean ret = false;
    try
    {
      map.clear();
      ret = load(lastPath);
      setRoundItems();
      MCH_MOD.proxy.registerModels();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return ret;
  }
  
  public static boolean load(String path)
  {
    lastPath = path;
    
    path = path.replace('\\', '/');
    File dir = new File(path);
    File[] files = dir.listFiles(new FileFilter()
    {

      public boolean accept(File pathname)
      {
        String s = pathname.getName().toLowerCase();
        return (pathname.isFile()) && (s.length() >= 5) && (s.substring(s.length() - 4).compareTo(".txt") == 0);
      }
    });
    

    if ((files == null) || (files.length <= 0))
    {
      return false;
    }
    File[] arr$ = files;int len$ = arr$.length; for (int i$ = 0; i$ < len$;) { File f = arr$[i$];
      
      BufferedReader br = null;
      int line = 0;
      try
      {
        String name = f.getName().toLowerCase();
        name = name.substring(0, name.length() - 4);
        if (map.containsKey(name))
        {













          try
          {













            if (br != null) br.close();
          }
          catch (Exception e) {}
        }
        else
        {
          br = new BufferedReader(new FileReader(f));
          
          MCH_WeaponInfo info = new MCH_WeaponInfo(name);
          String str;
          while ((str = br.readLine()) != null)
          {
            line++;
            str = str.trim();
            int eqIdx = str.indexOf('=');
            if ((eqIdx >= 0) && 
              (str.length() > eqIdx + 1))
            {
              info.loadItemData(str.substring(0, eqIdx).trim().toLowerCase(), str.substring(eqIdx + 1).trim());
            }
          }
          

          info.checkData();
          
          map.put(name, info);
          






          try
          {
            if (br != null) br.close();
          }
          catch (Exception e) {}
        }
        i$++;














      }
      catch (IOException e)
      {














        if (line > 0) MCH_Lib.Log("### Load failed %s : line=%d", new Object[] { f.getName(), Integer.valueOf(line) }); else
          MCH_Lib.Log("### Load failed %s", new Object[] { f.getName() });
        e.printStackTrace();
      }
      finally {
        try {
          if (br != null) br.close();
        }
        catch (Exception e) {}
      }
    }
    MCH_Lib.Log("[mcheli] Read %d weapons", new Object[] { Integer.valueOf(map.size()) });
    
    return map.size() > 0;
  }
  

  public static void setRoundItems()
  {
    for (MCH_WeaponInfo w : map.values())
    {
      for (MCH_WeaponInfo.RoundItem r : w.roundItems)
      {
        Item item = W_Item.getItemByName(r.itemName);
        r.itemStack = new ItemStack(item, 1, r.damage);
      }
    }
  }
  
  public static MCH_WeaponInfo get(String name)
  {
    return (MCH_WeaponInfo)map.get(name);
  }
  
  public static boolean contains(String name) {
    return map.containsKey(name);
  }
  
  public static Set<String> getKeySet() {
    return map.keySet();
  }
  
  public static Collection<MCH_WeaponInfo> getValues() {
    return map.values();
  }
}
