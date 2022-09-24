package mcheli.aircraft;

import java.util.ArrayList;
import java.util.List;
import mcheli.MCH_Config;
import mcheli.MCH_ConfigPrm;
import mcheli.MCH_Lib;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

public class MCH_BoundingBox
{
  public final AxisAlignedBB boundingBox;
  public final double offsetX;
  public final double offsetY;
  public final double offsetZ;
  public final float width;
  public final float height;
  public Vec3 rotatedOffset;
  public List<Vec3> pos = new ArrayList();
  public final float damegeFactor;
  
  public MCH_BoundingBox(double x, double y, double z, float w, float h, float df) {
    this.offsetX = x;
    this.offsetY = y;
    this.offsetZ = z;
    this.width = w;
    this.height = h;
    this.damegeFactor = df;
    this.boundingBox = AxisAlignedBB.getBoundingBox(x - w / 2.0F, y - h / 2.0F, z - w / 2.0F, x + w / 2.0F, y + h / 2.0F, z + w / 2.0F);
    updatePosition(0.0D, 0.0D, 0.0D, 0.0F, 0.0F, 0.0F);
  }
  
  public void add(double x, double y, double z)
  {
    this.pos.add(0, Vec3.createVectorHelper(x, y, z));
    while (this.pos.size() > MCH_Config.HitBoxDelayTick.prmInt + 2)
    {
      this.pos.remove(MCH_Config.HitBoxDelayTick.prmInt + 2);
    }
  }
  
  public MCH_BoundingBox copy()
  {
    return new MCH_BoundingBox(this.offsetX, this.offsetY, this.offsetZ, this.width, this.height, this.damegeFactor);
  }
  
  public void updatePosition(double posX, double posY, double posZ, float yaw, float pitch, float roll)
  {
    Vec3 v = Vec3.createVectorHelper(this.offsetX, this.offsetY, this.offsetZ);
    this.rotatedOffset = MCH_Lib.RotVec3(v, -yaw, -pitch, -roll);
    
    add(posX + this.rotatedOffset.xCoord, posY + this.rotatedOffset.yCoord, posZ + this.rotatedOffset.zCoord);
    
    int index = MCH_Config.HitBoxDelayTick.prmInt;
    Vec3 cp = index + 0 < this.pos.size() ? (Vec3)this.pos.get(index + 0) : (Vec3)this.pos.get(this.pos.size() - 1);
    Vec3 pp = index + 1 < this.pos.size() ? (Vec3)this.pos.get(index + 1) : (Vec3)this.pos.get(this.pos.size() - 1);
    
    double sx = (this.width + Math.abs(cp.xCoord - pp.xCoord)) / 2.0D;
    double sy = (this.height + Math.abs(cp.yCoord - pp.yCoord)) / 2.0D;
    double sz = (this.width + Math.abs(cp.zCoord - pp.zCoord)) / 2.0D;
    double x = (cp.xCoord + pp.xCoord) / 2.0D;
    double y = (cp.yCoord + pp.yCoord) / 2.0D;
    double z = (cp.zCoord + pp.zCoord) / 2.0D;
    
    this.boundingBox.setBounds(x - sx, y - sy, z - sz, x + sx, y + sy, z + sz);
  }
}
