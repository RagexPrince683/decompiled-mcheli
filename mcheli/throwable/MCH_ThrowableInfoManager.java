package mcheli.throwable;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import mcheli.MCH_InputFile;
import mcheli.MCH_Lib;
import net.minecraft.item.Item;

public class MCH_ThrowableInfoManager
{
  private static MCH_ThrowableInfoManager instance = new MCH_ThrowableInfoManager();
  private static HashMap<String, MCH_ThrowableInfo> map = new java.util.LinkedHashMap();
  
  private MCH_ThrowableInfoManager() {}
  
  public static boolean load(String path)
  {
    path = path.replace('\\', '/');
    File dir = new File(path);
    File[] files = dir.listFiles(new java.io.FileFilter()
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
    for (File f : files)
    {
      MCH_InputFile inFile = new MCH_InputFile();
      

      int line = 0;
      try
      {
        String name = f.getName().toLowerCase();
        name = name.substring(0, name.length() - 4);
        if (map.containsKey(name))
        {
































          inFile.close();
        }
        else if (inFile.openUTF8(f))
        {
          MCH_ThrowableInfo info = new MCH_ThrowableInfo(name);
          String str;
          while ((str = inFile.br.readLine()) != null)
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
        }
      }
      catch (IOException e)
      {
        if (line > 0) MCH_Lib.Log("### Load failed %s : line=%d", new Object[] { f.getName(), Integer.valueOf(line) }); else
          MCH_Lib.Log("### Load failed %s", new Object[] { f.getName() });
        e.printStackTrace();
      }
      finally
      {
        inFile.close();
      }
    }
    
    MCH_Lib.Log("Read %d throwable", new Object[] { Integer.valueOf(map.size()) });
    
    return map.size() > 0;
  }
  
  public static MCH_ThrowableInfo get(String name)
  {
    return (MCH_ThrowableInfo)map.get(name);
  }
  
  public static MCH_ThrowableInfo get(Item item) {
    for (MCH_ThrowableInfo info : map.values())
    {
      if (info.item == item)
      {
        return info;
      }
    }
    return null;
  }
  
  public static boolean contains(String name) {
    return map.containsKey(name);
  }
  
  public static java.util.Set<String> getKeySet() {
    return map.keySet();
  }
  
  public static java.util.Collection<MCH_ThrowableInfo> getValues() {
    return map.values();
  }
}
