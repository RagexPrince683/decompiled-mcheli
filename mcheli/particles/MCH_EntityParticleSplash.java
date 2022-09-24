package mcheli.particles;

import java.util.Random;
import mcheli.wrapper.W_Block;
import mcheli.wrapper.W_McClient;
import mcheli.wrapper.W_WorldFunc;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;


public class MCH_EntityParticleSplash
  extends MCH_EntityParticleBase
{
  public MCH_EntityParticleSplash(World par1World, double x, double y, double z, double mx, double my, double mz)
  {
    super(par1World, x, y, z, mx, my, mz);
    this.particleRed = (this.particleGreen = this.particleBlue = this.rand.nextFloat() * 0.3F + 0.7F);
    setParticleScale(this.rand.nextFloat() * 0.5F + 5.0F);
    setParticleMaxAge((int)(80.0D / (this.rand.nextFloat() * 0.8D + 0.2D)) + 2);
  }
  

  public void onUpdate()
  {
    this.prevPosX = this.posX;
    this.prevPosY = this.posY;
    this.prevPosZ = this.posZ;
    
    if (this.particleAge < this.particleMaxAge)
    {
      setParticleTextureIndex((int)(8.0D * this.particleAge / this.particleMaxAge));
      this.particleAge += 1;
    }
    else
    {
      setDead();
    }
    
    this.motionY -= 0.05999999865889549D;
    Block block = W_WorldFunc.getBlock(this.worldObj, (int)(this.posX + 0.5D), (int)(this.posY + 0.5D), (int)(this.posZ + 0.5D));
    boolean beforeInWater = W_Block.isEqualTo(block, W_Block.getWater());
    
    moveEntity(this.motionX, this.motionY, this.motionZ);
    
    block = W_WorldFunc.getBlock(this.worldObj, (int)(this.posX + 0.5D), (int)(this.posY + 0.5D), (int)(this.posZ + 0.5D));
    boolean nowInWater = W_Block.isEqualTo(block, W_Block.getWater());
    

    if ((this.motionY < -0.6D) && (!beforeInWater) && (nowInWater))
    {
      double p = -this.motionY * 10.0D;
      
      for (int i = 0; i < p; i++)
      {
        this.worldObj.spawnParticle("splash", this.posX + 0.5D + (this.rand.nextDouble() - 0.5D) * 2.0D, this.posY + this.rand.nextDouble(), this.posZ + 0.5D + (this.rand.nextDouble() - 0.5D) * 2.0D, (this.rand.nextDouble() - 0.5D) * 2.0D, 4.0D, (this.rand.nextDouble() - 0.5D) * 2.0D);
        






        this.worldObj.spawnParticle("bubble", this.posX + 0.5D + (this.rand.nextDouble() - 0.5D) * 2.0D, this.posY - this.rand.nextDouble(), this.posZ + 0.5D + (this.rand.nextDouble() - 0.5D) * 2.0D, (this.rand.nextDouble() - 0.5D) * 2.0D, -0.5D, (this.rand.nextDouble() - 0.5D) * 2.0D);


      }
      



    }
    else if (this.onGround)
    {
      setDead();
    }
    this.motionX *= 0.9D;
    this.motionZ *= 0.9D;
  }
  
  public void renderParticle(Tessellator par1Tessellator, float par2, float par3, float par4, float par5, float par6, float par7)
  {
    W_McClient.MOD_bindTexture("textures/particles/smoke.png");
    
    float f6 = this.particleTextureIndexX / 8.0F;
    float f7 = f6 + 0.125F;
    float f8 = 0.0F;
    float f9 = 1.0F;
    float f10 = 0.1F * this.particleScale;
    
    float f11 = (float)(this.prevPosX + (this.posX - this.prevPosX) * par2 - interpPosX);
    float f12 = (float)(this.prevPosY + (this.posY - this.prevPosY) * par2 - interpPosY);
    float f13 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * par2 - interpPosZ);
    float f14 = 1.0F;
    par1Tessellator.setColorRGBA_F(this.particleRed * f14, this.particleGreen * f14, this.particleBlue * f14, this.particleAlpha);
    par1Tessellator.addVertexWithUV(f11 - par3 * f10 - par6 * f10, f12 - par4 * f10, f13 - par5 * f10 - par7 * f10, f7, f9);
    par1Tessellator.addVertexWithUV(f11 - par3 * f10 + par6 * f10, f12 + par4 * f10, f13 - par5 * f10 + par7 * f10, f7, f8);
    par1Tessellator.addVertexWithUV(f11 + par3 * f10 + par6 * f10, f12 + par4 * f10, f13 + par5 * f10 + par7 * f10, f6, f8);
    par1Tessellator.addVertexWithUV(f11 + par3 * f10 - par6 * f10, f12 - par4 * f10, f13 + par5 * f10 - par7 * f10, f6, f9);
  }
}
