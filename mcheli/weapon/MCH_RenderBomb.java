package mcheli.weapon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;






@SideOnly(Side.CLIENT)
public class MCH_RenderBomb
  extends MCH_RenderBulletBase
{
  public MCH_RenderBomb()
  {
    this.shadowSize = 0.0F;
  }
  






  public void renderBullet(Entity entity, double posX, double posY, double posZ, float yaw, float partialTickTime)
  {
    if (!(entity instanceof MCH_EntityBomb)) { return;
    }
    MCH_EntityBomb bomb = (MCH_EntityBomb)entity;
    if (bomb.getInfo() == null) { return;
    }
    GL11.glPushMatrix();
    
    GL11.glTranslated(posX, posY, posZ);
    GL11.glRotatef(-entity.rotationYaw, 0.0F, 1.0F, 0.0F);
    GL11.glRotatef(-entity.rotationPitch, -1.0F, 0.0F, 0.0F);
    
    if ((bomb.isBomblet > 0) || (bomb.getInfo().bomblet <= 0) || (bomb.getInfo().bombletSTime > 0))
    {




      renderModel(bomb);
    }
    
    GL11.glPopMatrix();
  }
  
  protected ResourceLocation getEntityTexture(Entity entity)
  {
    return TEX_DEFAULT;
  }
}
