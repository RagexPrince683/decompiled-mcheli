package mcheli.wrapper;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class W_NBTTag
{
  public static final int TAG_COMPOUND = 10;
  
  public W_NBTTag() {}
  
  public static NBTTagCompound tagAt(NBTTagList list, int i)
  {
    return list != null ? list.getCompoundTagAt(i) : null;
  }
  



  public static NBTTagList getTagList(NBTTagCompound nbt, String s, int i)
  {
    return nbt.getTagList(s, i);
  }
  



  public static net.minecraft.nbt.NBTTagIntArray newTagIntArray(String s, int[] n)
  {
    return new net.minecraft.nbt.NBTTagIntArray(n);
  }
}
