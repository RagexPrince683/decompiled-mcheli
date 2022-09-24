package mcheli;

import java.io.File;

public class MCH_SoundsJson
{
  public MCH_SoundsJson() {}
  
  public static boolean update(String path) {
    boolean result = true;
    path = path.replace('\\', '/');
    File dir = new File(path + "sounds");
    File[] files = dir.listFiles(new java.io.FileFilter()
    {

      public boolean accept(File pathname)
      {
        String s = pathname.getName().toLowerCase();
        return (pathname.isFile()) && (s.length() >= 5) && (s.substring(s.length() - 4).compareTo(".ogg") == 0);

      }
      

    });
    int cnt = 0;
    
    java.io.PrintWriter pw = null;
    
    try
    {
      File file = new File(path + "sounds.json");
      
      pw = new java.io.PrintWriter(file);
      
      pw.println("{");
      java.util.LinkedHashMap<String, java.util.ArrayList<String>> map;
      if (files != null)
      {
        map = new java.util.LinkedHashMap();
        for (File f : files)
        {
          String name = f.getName().toLowerCase();
          int ei = name.lastIndexOf(".");
          name = name.substring(0, ei);
          String key = name;
          char c = key.charAt(key.length() - 1);
          if ((c >= '0') && (c <= '9'))
          {
            key = key.substring(0, key.length() - 1);
          }
          
          if (!map.containsKey(key))
          {
            map.put(key, new java.util.ArrayList());
          }
          ((java.util.ArrayList)map.get(key)).add(name);
        }
        for (String key : map.keySet())
        {
          cnt++;
          java.util.ArrayList<String> list = (java.util.ArrayList)map.get(key);
          String line = "";
          
          line = "\"" + key + "\": {\"category\": \"master\",\"sounds\": [";
          for (int fi = 0; fi < list.size(); fi++)
          {
            line = line + (fi > 0 ? "," : "") + "\"" + (String)list.get(fi) + "\"";
          }
          line = line + "]}";
          
          if (cnt < map.size()) line = line + ",";
          pw.println(line);
        }
      }
      
      pw.println("}");
      pw.println("");
      
      result = true;
    }
    catch (java.io.IOException e)
    {
      result = false;
      e.printStackTrace();
    }
    finally
    {
      if (pw != null)
      {
        pw.close();
      }
    }
    
    if (result)
    {
      MCH_Lib.Log("Update sounds.json. %d sounds", new Object[] { Integer.valueOf(cnt) });
    }
    else
    {
      MCH_Lib.Log("Failed sounds.json update! %d sounds", new Object[] { Integer.valueOf(cnt) });
    }
    return result;
  }
}
