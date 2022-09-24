package mcheli;

import java.io.File;

public abstract class MCH_InfoManagerBase
{
  public MCH_InfoManagerBase() {}
  
  public abstract MCH_BaseInfo newInfo(String paramString);
  
  public abstract java.util.Map getMap();
  
  public boolean load(String path, String type)
  {
    path = path.replace('\\', '/');
    File dir = new File(path + type);
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
      int line = 0;
      MCH_InputFile inFile = new MCH_InputFile();
      
      java.io.BufferedReader br = null;
      try
      {
        String name = f.getName().toLowerCase();
        name = name.substring(0, name.length() - 4);
        if (getMap().containsKey(name))
        {

































          inFile.close();
        }
        else if (inFile.openUTF8(f))
        {
          MCH_BaseInfo info = newInfo(name);
          info.filePath = f.getCanonicalPath();
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
          
          line = 0;
          
          if (info.isValidData())
          {
            getMap().put(name, info);
          }
        }
      }
      catch (Exception e)
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
    
    MCH_Lib.Log("Read %d %s", new Object[] { Integer.valueOf(getMap().size()), type });
    return getMap().size() > 0;
  }
}
