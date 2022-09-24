package mcheli.weapon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcheli.MCH_ModelManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;





@SideOnly(Side.CLIENT)
public class MCH_RenderA10
  extends MCH_RenderBulletBase
{
  public MCH_RenderA10()
  {
    this.shadowSize = 10.5F;
  }
  







  public void renderBullet(Entity e, double posX, double posY, double posZ, float par8, float tickTime)
  {
    if (!(e instanceof MCH_EntityA10)) { return;
    }
    if (!((MCH_EntityA10)e).isRender()) { return;
    }
    GL11.glPushMatrix();
    GL11.glTranslated(posX, posY, posZ);
    
    float yaw = -(e.prevRotationYaw + (e.rotationYaw - e.prevRotationYaw) * tickTime);
    float pitch = -(e.prevRotationPitch + (e.rotationPitch - e.prevRotationPitch) * tickTime);
    GL11.glRotatef(yaw, 0.0F, 1.0F, 0.0F);
    GL11.glRotatef(pitch, 1.0F, 0.0F, 0.0F);
    

    bindTexture("textures/bullets/a10.png");
    MCH_ModelManager.render("a-10");
    
    GL11.glPopMatrix();
  }
  
  protected ResourceLocation getEntityTexture(Entity entity)
  {
    return TEX_DEFAULT;
  }
}
