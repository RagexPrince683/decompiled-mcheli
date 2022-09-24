package mcheli.block;

import mcheli.MCH_Lib;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;






public class MCH_DraftingTableTileEntity
  extends TileEntity
{
  public MCH_DraftingTableTileEntity() {}
  
  public int getBlockMetadata()
  {
    if (this.blockMetadata == -1)
    {
      this.blockMetadata = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
      
      MCH_Lib.DbgLog(this.worldObj, "MCH_DraftingTableTileEntity.getBlockMetadata : %d(0x%08X)", new Object[] { Integer.valueOf(this.blockMetadata), Integer.valueOf(this.blockMetadata) });
    }
    

    return this.blockMetadata;
  }
}
