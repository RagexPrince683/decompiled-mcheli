package mcheli;

import java.io.File;



public class MCH_BaseInfo
{
  public String filePath;
  
  public MCH_BaseInfo() {}
  
  public boolean toBool(String s) { return s.equalsIgnoreCase("true"); }
  
  public boolean toBool(String s, boolean defaultValue) {
    if (s.equalsIgnoreCase("true")) return true;
    if (s.equalsIgnoreCase("false")) return false;
    return defaultValue;
  }
  
  public float toFloat(String s) { return Float.parseFloat(s); }
  
  public float toFloat(String s, float min, float max) {
    float f = Float.parseFloat(s);
    return f > max ? max : f < min ? min : f; }
  
  public double toDouble(String s) { return Double.parseDouble(s); }
  
  public net.minecraft.util.Vec3 toVec3(String x, String y, String z)
  {
    return net.minecraft.util.Vec3.createVectorHelper(toDouble(x), toDouble(y), toDouble(z));
  }
  
  public int toInt(String s) { return Integer.parseInt(s); }
  
  public int toInt(String s, int min, int max) {
    int f = Integer.parseInt(s);
    return f > max ? max : f < min ? min : f;
  }
  
  public int hex2dec(String s)
  {
    if ((!s.startsWith("0x")) && (!s.startsWith("0X")) && (s.indexOf(0) != 35))
    {
      return (int)(Long.decode("0x" + s).longValue() & 0xFFFFFFFF);
    }
    return (int)(Long.decode(s).longValue() & 0xFFFFFFFF);
  }
  
  public String[] splitParam(String data)
  {
    return data.split("\\s*,\\s*");
  }
  
  public String[] splitParamSlash(String data) {
    return data.split("\\s*/\\s*");
  }
  
  public boolean isValidData() throws Exception
  {
    return true;
  }
  


  public void loadItemData(String item, String data) {}
  

  public void loadItemData(int fileLine, String item, String data) {}
  

  public void preReload() {}
  

  public void postReload() {}
  

  public boolean canReloadItem(String item)
  {
    return false;
  }
  
  public boolean reload()
  {
    return reload(this);
  }
  
  private boolean reload(MCH_BaseInfo info)
  {
    int line = 0;
    MCH_InputFile inFile = new MCH_InputFile();
    
    java.io.BufferedReader br = null;
    File f = new File(info.filePath);
    try
    {
      if (inFile.openUTF8(f))
      {
        info.preReload();
        
        String str;
        while ((str = inFile.br.readLine()) != null)
        {
          line++;
          str = str.trim();
          int eqIdx = str.indexOf('=');
          if ((eqIdx >= 0) && 
            (str.length() > eqIdx + 1))
          {
            String item = str.substring(0, eqIdx).trim().toLowerCase();
            if (info.canReloadItem(item))
            {
              info.loadItemData(item, str.substring(eqIdx + 1).trim()); }
          }
        }
        line = 0;
        
        info.isValidData();
        
        info.postReload();
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
    return true;
  }
}
