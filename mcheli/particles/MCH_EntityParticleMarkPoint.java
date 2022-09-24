package mcheli.particles;

import mcheli.MCH_Lib;
import mcheli.multiplay.MCH_GuiTargetMarker;
import mcheli.wrapper.W_Reflection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class MCH_EntityParticleMarkPoint
  extends MCH_EntityParticleBase
{
  final Team taem;
  
  public MCH_EntityParticleMarkPoint(World par1World, double x, double y, double z, Team team)
  {
    super(par1World, x, y, z, 0.0D, 0.0D, 0.0D);
    setParticleMaxAge(30);
    this.taem = team;
  }
  

  public void onUpdate()
  {
    this.prevPosX = this.posX;
    this.prevPosY = this.posY;
    this.prevPosZ = this.posZ;
    
    EntityPlayer player = Minecraft.getMinecraft().thePlayer;
    if (player == null)
    {
      setDead();
    }
    else if ((player.getTeam() == null) && (this.taem != null))
    {
      setDead();
    }
    else if ((player.getTeam() != null) && (!player.isOnTeam(this.taem)))
    {
      setDead();
    }
  }
  
  public void setDead()
  {
    super.setDead();
    MCH_Lib.DbgLog(true, "MCH_EntityParticleMarkPoint.setDead : " + this, new Object[0]);
  }
  
  public int getFXLayer()
  {
    return 3;
  }
  
  public void renderParticle(Tessellator par1Tessellator, float par2, float par3, float par4, float par5, float par6, float par7)
  {
    GL11.glPushMatrix();
    
    Minecraft mc = Minecraft.getMinecraft();
    EntityPlayer player = mc.thePlayer;
    if (player == null) { return;
    }
    double ix = interpPosX;
    double iy = interpPosY;
    double iz = interpPosZ;
    
    if ((mc.gameSettings.thirdPersonView > 0) && (mc.renderViewEntity != null))
    {
      Entity viewer = mc.renderViewEntity;
      double dist = W_Reflection.getThirdPersonDistance();
      float yaw = mc.gameSettings.thirdPersonView != 2 ? -viewer.rotationYaw : -viewer.rotationYaw;
      float pitch = mc.gameSettings.thirdPersonView != 2 ? -viewer.rotationPitch : -viewer.rotationPitch;
      Vec3 v = MCH_Lib.RotVec3(0.0D, 0.0D, -dist, yaw, pitch);
      if (mc.gameSettings.thirdPersonView == 2)
      {
        v.xCoord = (-v.xCoord);
        v.yCoord = (-v.yCoord);
        v.zCoord = (-v.zCoord);
      }
      Vec3 vs = Vec3.createVectorHelper(viewer.posX, viewer.posY + viewer.getEyeHeight(), viewer.posZ);
      MovingObjectPosition mop = mc.renderViewEntity.worldObj.rayTraceBlocks(vs.addVector(0.0D, 0.0D, 0.0D), vs.addVector(v.xCoord, v.yCoord, v.zCoord));
      

      double block_dist = dist;
      if ((mop != null) && (mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK))
      {
        block_dist = vs.distanceTo(mop.hitVec) - 0.4D;
        if (block_dist < 0.0D) { block_dist = 0.0D;
        }
      }
      GL11.glTranslated(v.xCoord * (block_dist / dist), v.yCoord * (block_dist / dist), v.zCoord * (block_dist / dist));
      ix += v.xCoord * (block_dist / dist);
      iy += v.yCoord * (block_dist / dist);
      iz += v.zCoord * (block_dist / dist);
    }
    
    double px = (float)(this.prevPosX + (this.posX - this.prevPosX) * par2 - ix);
    double py = (float)(this.prevPosY + (this.posY - this.prevPosY) * par2 - iy);
    double pz = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * par2 - iz);
    double scale = Math.sqrt(px * px + py * py + pz * pz) / 10.0D;
    if (scale < 1.0D)
    {
      scale = 1.0D;
    }
    
    MCH_GuiTargetMarker.addMarkEntityPos(100, this, px / scale, py / scale, pz / scale, false);
    
    GL11.glPopMatrix();
  }
}
