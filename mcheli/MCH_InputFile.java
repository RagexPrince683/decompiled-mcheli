package mcheli;

import java.io.File;

public class MCH_InputFile
{
  public File file;
  public java.io.BufferedReader br;
  
  public MCH_InputFile()
  {
    this.file = null;
    this.br = null;
  }
  
  public boolean open(String path)
  {
    close();
    this.file = new File(path);
    String filePath = this.file.getAbsolutePath();
    try {
      this.br = new java.io.BufferedReader(new java.io.FileReader(this.file));
    } catch (java.io.FileNotFoundException e) {
      MCH_Lib.DbgLog(true, "FILE open failed MCH_InputFile.open:" + filePath, new Object[0]);
      e.printStackTrace();
      return false;
    }
    return true;
  }
  
  public boolean openUTF8(File file)
  {
    return openUTF8(file.getPath());
  }
  
  public boolean openUTF8(String path) {
    close();
    this.file = new File(path);
    try
    {
      this.br = new java.io.BufferedReader(new java.io.InputStreamReader(new java.io.FileInputStream(this.file), "UTF-8"));
    }
    catch (Exception e)
    {
      e.printStackTrace();
      return false;
    }
    return true;
  }
  
  public String readLine()
  {
    try {
      return this.br != null ? this.br.readLine() : null;
    }
    catch (java.io.IOException e) {}
    
    return null;
  }
  

  public void close()
  {
    try
    {
      if (this.br != null) { this.br.close();
      }
    }
    catch (java.io.IOException e) {}
    
    this.br = null;
  }
}
