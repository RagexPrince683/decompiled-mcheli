package mcheli;

import java.io.PrintWriter;

public class MCH_OutputFile
{
  public java.io.File file;
  public PrintWriter pw;
  
  public MCH_OutputFile()
  {
    this.file = null;
    this.pw = null;
  }
  
  public boolean open(String path)
  {
    close();
    this.file = new java.io.File(path);
    try
    {
      this.pw = new PrintWriter(this.file);

    }
    catch (java.io.FileNotFoundException e)
    {
      return false;
    }
    return true;
  }
  
  public boolean openUTF8(String path)
  {
    close();
    this.file = new java.io.File(path);
    try
    {
      this.pw = new PrintWriter(new java.io.OutputStreamWriter(new java.io.FileOutputStream(this.file), "UTF-8"));
    }
    catch (Exception e)
    {
      e.printStackTrace();
      return false;
    }
    return true;
  }
  
  public void writeLine(String s)
  {
    if ((this.pw != null) && (s != null))
    {
      this.pw.println(s);
    }
  }
  
  public void close()
  {
    if (this.pw != null) this.pw.close();
    this.pw = null;
  }
}
