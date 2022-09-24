package mcheli.weapon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;





@SideOnly(Side.CLIENT)
public class MCH_RenderNone
  extends MCH_RenderBulletBase
{
  public MCH_RenderNone() {}
  
  public void renderBullet(Entity entity, double posX, double posY, double posZ, float yaw, float partialTickTime) {}
  
  protected ResourceLocation getEntityTexture(Entity entity)
  {
    return TEX_DEFAULT;
  }
}
