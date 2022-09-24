package mcheli.wrapper;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;


public class W_Particle
{
  public W_Particle() {}
  
  public static String getParticleTileCrackName(World w, int blockX, int blockY, int blockZ)
  {
    Block block = w.getBlock(blockX, blockY, blockZ);
    if (block.getMaterial() != Material.air)
    {
      return "blockcrack_" + Block.getIdFromBlock(block) + "_" + w.getBlockMetadata(blockX, blockY, blockZ);
    }
    












    return "";
  }
  


  public static String getParticleTileDustName(World w, int blockX, int blockY, int blockZ)
  {
    Block block = w.getBlock(blockX, blockY, blockZ);
    if (block.getMaterial() != Material.air)
    {
      return "blockdust_" + Block.getIdFromBlock(block) + "_" + w.getBlockMetadata(blockX, blockY, blockZ);
    }
    












    return "";
  }
}
