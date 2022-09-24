package mcheli.helicopter;

import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import mcheli.aircraft.MCH_Blade;
import mcheli.aircraft.MCH_EntityAircraft;
import mcheli.aircraft.MCH_RenderAircraft;
import mcheli.aircraft.MCH_Rotor;
import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

@SideOnly(cpw.mods.fml.relauncher.Side.CLIENT)
public class MCH_RenderHeli extends MCH_RenderAircraft
{
  public MCH_RenderHeli()
  {
    this.shadowSize = 2.0F;
  }
  









  
  public void renderAircraft(MCH_EntityAircraft entity, double posX, double posY, double posZ, float yaw, float pitch, float roll, float tickTime)
  {
	
	MCH_EntityHeli heli;
    MCH_HeliInfo heliInfo = null;
    if ((entity != null) && ((entity instanceof MCH_EntityHeli)))
    {
      heli = (MCH_EntityHeli)entity;
      heliInfo = heli.getHeliInfo();
      if (heliInfo != null) {}
    }
    else
    {
      return;
    }
    
    
    renderDebugHitBox(heli, posX, posY, posZ, yaw, pitch);
    renderDebugPilotSeat(heli, posX, posY, posZ, yaw, pitch, roll);
    

    GL11.glTranslated(posX, posY, posZ);
    

    GL11.glRotatef(yaw, 0.0F, -1.0F, 0.0F);
    GL11.glRotatef(pitch, 1.0F, 0.0F, 0.0F);
    GL11.glRotatef(roll, 0.0F, 0.0F, 1.0F);
    

    bindTexture("textures/helicopters/" + heli.getTextureName() + ".png", heli);
    
    renderBody(heliInfo.model);
    

    drawModelBlade(heli, heliInfo, tickTime);
  }
  
  public void drawModelBlade(MCH_EntityHeli heli, MCH_HeliInfo info, float tickTime)
  {
	  
    for (int i = 0; (i < heli.rotors.length) && (i < info.rotorList.size()); i++)
    {
      MCH_HeliInfo.Rotor rotorInfo = (MCH_HeliInfo.Rotor)info.rotorList.get(i);
      MCH_Rotor rotor = heli.rotors[i];
      GL11.glPushMatrix();

      if (rotorInfo.oldRenderMethod)
      {
        GL11.glTranslated(rotorInfo.pos.xCoord, rotorInfo.pos.yCoord, rotorInfo.pos.zCoord);
      }
      
      for (MCH_Blade b : rotor.blades)
      {
    	
        GL11.glPushMatrix();
        
        float rot = b.getRotation();
        float prevRot = b.getPrevRotation();
        if (rot - prevRot < -180.0F) { prevRot -= 360.0F;
        } else if (prevRot - rot < -180.0F) { prevRot += 360.0F;
        }
        
        if (!rotorInfo.oldRenderMethod)
        {
          GL11.glTranslated(rotorInfo.pos.xCoord, rotorInfo.pos.yCoord, rotorInfo.pos.zCoord);
        }
        
        GL11.glRotatef(prevRot + (rot - prevRot) * tickTime, (float)rotorInfo.rot.xCoord, (float)rotorInfo.rot.yCoord, (float)rotorInfo.rot.zCoord);
        


        if (!rotorInfo.oldRenderMethod)
        {
          GL11.glTranslated(-rotorInfo.pos.xCoord, -rotorInfo.pos.yCoord, -rotorInfo.pos.zCoord);
        }
        
        renderPart(rotorInfo.model, info.model, rotorInfo.modelName);
        

        GL11.glPopMatrix();
      }
      GL11.glPopMatrix();
    }
  }
  
  protected net.minecraft.util.ResourceLocation getEntityTexture(Entity entity)
  {
    return TEX_DEFAULT;
  }
}
