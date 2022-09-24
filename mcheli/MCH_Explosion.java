package mcheli;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import mcheli.particles.MCH_ParticleParam;
import mcheli.particles.MCH_ParticlesUtil;
import mcheli.weapon.MCH_EntityBaseBullet;
import mcheli.wrapper.W_Block;
import mcheli.wrapper.W_ChunkPosition;
import mcheli.wrapper.W_Entity;
import mcheli.wrapper.W_WorldFunc;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class MCH_Explosion extends Explosion
{
  public final int field_77289_h = 16;
  public World world;
  private static Random explosionRNG = new Random();
  public java.util.Map field_77288_k = new java.util.HashMap();
  public boolean isDestroyBlock;
  public int countSetFireEntity;
  public boolean isPlaySound;
  public boolean isInWater;
  ExplosionResult result;
  public EntityPlayer explodedPlayer;
  public float explosionSizeBlock;
  public MCH_DamageFactor damageFactor = null;
  
  public class ExplosionResult
  {
    public boolean hitEntity;
    
    public ExplosionResult() {
      this.hitEntity = false;
    }
  }
  

  public MCH_Explosion(World par1World, Entity exploder, Entity player, double x, double y, double z, float size)
  {
    super(par1World, exploder, x, y, z, size);
    this.world = par1World;
    this.isDestroyBlock = false;
    this.explosionSizeBlock = size;
    this.countSetFireEntity = 0;
    this.isPlaySound = true;
    this.isInWater = false;
    this.result = null;
    this.explodedPlayer = ((player instanceof EntityPlayer) ? (EntityPlayer)player : null);
  }
  
  public boolean isRemote() { return this.world.isRemote; }
  

  public void doExplosionA()
  {
    HashSet hashset = new HashSet();
    






    for (int i = 0;; i++) { getClass(); if (i >= 16)
        break;
      for (int j = 0;; j++) { getClass(); if (j >= 16)
          break;
        for (int k = 0;; k++) { getClass(); if (k >= 16)
            break;
          if (i != 0) { getClass(); if ((i != 16 - 1) && (j != 0)) { getClass(); if ((j != 16 - 1) && (k != 0)) { getClass(); if (k != 16 - 1) continue;
              } } }
          getClass();double d3 = i / (16.0F - 1.0F) * 2.0F - 1.0F;
          getClass();double d4 = j / (16.0F - 1.0F) * 2.0F - 1.0F;
          getClass();double d5 = k / (16.0F - 1.0F) * 2.0F - 1.0F;
          double d6 = Math.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
          d3 /= d6;
          d4 /= d6;
          d5 /= d6;
          float f1 = this.explosionSizeBlock * (0.7F + this.world.rand.nextFloat() * 0.6F);
          double d0 = this.explosionX;
          double d1 = this.explosionY;
          double d2 = this.explosionZ;
          
          for (float f2 = 0.3F; f1 > 0.0F; f1 -= 0.22500001F)
          {
            int l = MathHelper.floor_double(d0);
            int i1 = MathHelper.floor_double(d1);
            int j1 = MathHelper.floor_double(d2);
            int k1 = W_WorldFunc.getBlockId(this.world, l, i1, j1);
            
            if (k1 > 0)
            {
              Block block = W_WorldFunc.getBlock(this.world, l, i1, j1);
              float f3;
              if (this.exploder != null)
              {
                f3 = W_Entity.getBlockExplosionResistance(this.exploder, this, this.world, l, i1, j1, block);
              }
              else
              {
                f3 = block.getExplosionResistance(this.exploder, this.world, l, i1, j1, this.explosionX, this.explosionY, this.explosionZ);
              }
              
              if (this.isInWater)
              {
                f3 *= (this.world.rand.nextFloat() * 0.2F + 0.2F);
              }
              
              f1 -= (f3 + 0.3F) * 0.3F;
            }
            
            if ((f1 > 0.0F) && ((this.exploder == null) || (W_Entity.shouldExplodeBlock(this.exploder, this, this.world, l, i1, j1, k1, f1))))
            {
              hashset.add(new ChunkPosition(l, i1, j1));
            }
            
            d0 += d3 * 0.30000001192092896D;
            d1 += d4 * 0.30000001192092896D;
            d2 += d5 * 0.30000001192092896D;
          }
        }
      }
    }
    

    float f = this.explosionSize;
    int i;
    this.affectedBlockPositions.addAll(hashset);
    this.explosionSize *= 2.0F;
    i = MathHelper.floor_double(this.explosionX - this.explosionSize - 1.0D);
    int j = MathHelper.floor_double(this.explosionX + this.explosionSize + 1.0D);
    int k = MathHelper.floor_double(this.explosionY - this.explosionSize - 1.0D);
    int l1 = MathHelper.floor_double(this.explosionY + this.explosionSize + 1.0D);
    int i2 = MathHelper.floor_double(this.explosionZ - this.explosionSize - 1.0D);
    int j2 = MathHelper.floor_double(this.explosionZ + this.explosionSize + 1.0D);
    List list = this.world.getEntitiesWithinAABBExcludingEntity(this.exploder, mcheli.wrapper.W_AxisAlignedBB.getAABB(i, k, i2, j, l1, j2));
    Vec3 vec3 = W_WorldFunc.getWorldVec3(this.world, this.explosionX, this.explosionY, this.explosionZ);
    

    this.exploder = this.explodedPlayer;
    
    for (int k2 = 0; k2 < list.size(); k2++)
    {
      Entity entity = (Entity)list.get(k2);
      double d7 = entity.getDistance(this.explosionX, this.explosionY, this.explosionZ) / this.explosionSize;
      
      if (d7 <= 1.0D)
      {
        double d0 = entity.posX - this.explosionX;
        double d1 = entity.posY + entity.getEyeHeight() - this.explosionY;
        double d2 = entity.posZ - this.explosionZ;
        double d8 = MathHelper.sqrt_double(d0 * d0 + d1 * d1 + d2 * d2);
        
        if (d8 != 0.0D)
        {
          d0 /= d8;
          d1 /= d8;
          d2 /= d8;
          double d9 = getBlockDensity(vec3, entity.boundingBox);
          double d10 = (1.0D - d7) * d9;
          float damage = (int)((d10 * d10 + d10) / 2.0D * 8.0D * this.explosionSize + 1.0D);
          if ((damage > 0.0F) && (this.result != null))
          {
            if ((!(entity instanceof net.minecraft.entity.item.EntityItem)) && (!(entity instanceof net.minecraft.entity.item.EntityExpBottle)) && (!(entity instanceof net.minecraft.entity.item.EntityXPOrb)) && (!W_Entity.isEntityFallingBlock(entity)))
            {





              if (((entity instanceof MCH_EntityBaseBullet)) && ((this.exploder instanceof EntityPlayer)))
              {

                if (!W_Entity.isEqual(((MCH_EntityBaseBullet)entity).shootingEntity, this.exploder))
                {
                  this.result.hitEntity = true;
                  MCH_Lib.DbgLog(this.world, "MCH_Explosion.doExplosionA:Damage=%.1f:HitEntityBullet=" + entity.getClass(), new Object[] { Float.valueOf(damage) });
                }
                
              }
              else
              {
                MCH_Lib.DbgLog(this.world, "MCH_Explosion.doExplosionA:Damage=%.1f:HitEntity=" + entity.getClass(), new Object[] { Float.valueOf(damage) });
                this.result.hitEntity = true;
              }
            }
          }
          
          MCH_Lib.applyEntityHurtResistantTimeConfig(entity);
          
          DamageSource ds = DamageSource.setExplosionSource(this);
          damage = MCH_Config.applyDamageVsEntity(entity, ds, damage);
          damage *= (this.damageFactor != null ? this.damageFactor.getDamageFactor(entity) : 1.0F);
          W_Entity.attackEntityFrom(entity, ds, damage);
          



          double d11 = net.minecraft.enchantment.EnchantmentProtection.func_92092_a(entity, d10);
          if (!(entity instanceof MCH_EntityBaseBullet))
          {
            entity.motionX += d0 * d11 * 0.4D;
            entity.motionY += d1 * d11 * 0.1D;
            entity.motionZ += d2 * d11 * 0.4D;
          }
          
          if ((entity instanceof EntityPlayer))
          {
            this.field_77288_k.put((EntityPlayer)entity, W_WorldFunc.getWorldVec3(this.world, d0 * d10, d1 * d10, d2 * d10));
          }
          
          if ((damage > 0.0F) && (this.countSetFireEntity > 0))
          {
            double fireFactor = 1.0D - d8 / this.explosionSize;
            if (fireFactor > 0.0D)
            {
              entity.setFire((int)(fireFactor * this.countSetFireEntity));
            }
          }
        }
      }
    }
    
    this.explosionSize = f;
  }
  
  private double getBlockDensity(Vec3 vec3, AxisAlignedBB p_72842_2_)
  {
    double d0 = 1.0D / ((p_72842_2_.maxX - p_72842_2_.minX) * 2.0D + 1.0D);
    double d1 = 1.0D / ((p_72842_2_.maxY - p_72842_2_.minY) * 2.0D + 1.0D);
    double d2 = 1.0D / ((p_72842_2_.maxZ - p_72842_2_.minZ) * 2.0D + 1.0D);
    
    if ((d0 >= 0.0D) && (d1 >= 0.0D) && (d2 >= 0.0D))
    {
      int i = 0;
      int j = 0;
      
      for (float f = 0.0F; f <= 1.0F; f = (float)(f + d0))
      {
        for (float f1 = 0.0F; f1 <= 1.0F; f1 = (float)(f1 + d1))
        {
          for (float f2 = 0.0F; f2 <= 1.0F; f2 = (float)(f2 + d2))
          {
            double d3 = p_72842_2_.minX + (p_72842_2_.maxX - p_72842_2_.minX) * f;
            double d4 = p_72842_2_.minY + (p_72842_2_.maxY - p_72842_2_.minY) * f1;
            double d5 = p_72842_2_.minZ + (p_72842_2_.maxZ - p_72842_2_.minZ) * f2;
            
            if (this.world.func_147447_a(Vec3.createVectorHelper(d3, d4, d5), vec3, false, true, false) == null)
            {
              i++;
            }
            
            j++;
          }
        }
      }
      
      return i / j;
    }
    

    return 0.0D;
  }
  


  public void doExplosionB(boolean par1)
  {
    if (this.isPlaySound)
    {
      W_WorldFunc.DEF_playSoundEffect(this.world, this.explosionX, this.explosionY, this.explosionZ, "random.explode", 4.0F, (1.0F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F) * 0.7F);
    }
    










    if (this.isSmoking)
    {
      Iterator iterator = this.affectedBlockPositions.iterator();
      
      while (iterator.hasNext())
      {
        ChunkPosition chunkposition = (ChunkPosition)iterator.next();
        int i = W_ChunkPosition.getChunkPosX(chunkposition);
        int j = W_ChunkPosition.getChunkPosY(chunkposition);
        int k = W_ChunkPosition.getChunkPosZ(chunkposition);
        int l = W_WorldFunc.getBlockId(this.world, i, j, k);
        
        if ((l > 0) && (this.isDestroyBlock) && (this.explosionSizeBlock > 0.0F) && (MCH_Config.Explosion_DestroyBlock.prmBool))
        {
          Block block = W_Block.getBlockById(l);
          
          if (block.canDropFromExplosion(this))
          {
            block.dropBlockAsItemWithChance(this.world, i, j, k, this.world.getBlockMetadata(i, j, k), 1.0F / this.explosionSizeBlock, 0);
          }
          
          block.onBlockExploded(this.world, i, j, k, this);
        }
      }
    }
    
    if ((this.isFlaming) && (MCH_Config.Explosion_FlamingBlock.prmBool))
    {
      Iterator iterator = this.affectedBlockPositions.iterator();
      
      while (iterator.hasNext())
      {
        ChunkPosition chunkposition = (ChunkPosition)iterator.next();
        int i = W_ChunkPosition.getChunkPosX(chunkposition);
        int j = W_ChunkPosition.getChunkPosY(chunkposition);
        int k = W_ChunkPosition.getChunkPosZ(chunkposition);
        int l = W_WorldFunc.getBlockId(this.world, i, j, k);
        Block b = W_WorldFunc.getBlock(this.world, i, j - 1, k);
        
        if ((l == 0) && (b != null) && (b.isOpaqueCube()) && (explosionRNG.nextInt(3) == 0))
        {
          W_WorldFunc.setBlock(this.world, i, j, k, mcheli.wrapper.W_Blocks.fire);
        }
      }
    }
  }
  
  public ExplosionResult newExplosionResult()
  {
    return new ExplosionResult();
  }
  


























  public static ExplosionResult newExplosion(World w, Entity entityExploded, Entity player, double x, double y, double z, float size, float sizeBlock, boolean playSound, boolean isSmoking, boolean isFlaming, boolean isDestroyBlock, int countSetFireEntity)
  {
    return newExplosion(w, entityExploded, player, x, y, z, size, sizeBlock, playSound, isSmoking, isFlaming, isDestroyBlock, countSetFireEntity, null);
  }
  










  public static ExplosionResult newExplosion(World w, Entity entityExploded, Entity player, double x, double y, double z, float size, float sizeBlock, boolean playSound, boolean isSmoking, boolean isFlaming, boolean isDestroyBlock, int countSetFireEntity, MCH_DamageFactor df)
  {
    if (w.isRemote) { return null;
    }
    MCH_Explosion exp = new MCH_Explosion(w, entityExploded, player, x, y, z, size);
    
    exp.isSmoking = w.getGameRules().getGameRuleBooleanValue("mobGriefing");
    exp.isFlaming = isFlaming;
    exp.isDestroyBlock = isDestroyBlock;
    exp.explosionSizeBlock = sizeBlock;
    exp.countSetFireEntity = countSetFireEntity;
    exp.isPlaySound = playSound;
    exp.isInWater = false;
    exp.result = exp.newExplosionResult();
    exp.damageFactor = df;
    
    exp.doExplosionA();
    exp.doExplosionB(true);
    
    MCH_PacketEffectExplosion.ExplosionParam param = MCH_PacketEffectExplosion.create();
    param.exploderID = W_Entity.getEntityId(entityExploded);
    param.posX = x;
    param.posY = y;
    param.posZ = z;
    param.size = size;
    param.inWater = false;
    MCH_PacketEffectExplosion.send(param);
    
    return exp.result;
  }
  












  public static ExplosionResult newExplosionInWater(World w, Entity entityExploded, Entity player, double x, double y, double z, float size, float sizeBlock, boolean playSound, boolean isSmoking, boolean isFlaming, boolean isDestroyBlock, int countSetFireEntity, MCH_DamageFactor df)
  {
    if (w.isRemote) { return null;
    }
    MCH_Explosion exp = new MCH_Explosion(w, entityExploded, player, x, y, z, size);
    
    exp.isSmoking = w.getGameRules().getGameRuleBooleanValue("mobGriefing");
    exp.isFlaming = isFlaming;
    exp.isDestroyBlock = isDestroyBlock;
    exp.explosionSizeBlock = sizeBlock;
    exp.countSetFireEntity = countSetFireEntity;
    exp.isPlaySound = playSound;
    exp.isInWater = true;
    exp.result = exp.newExplosionResult();
    exp.damageFactor = df;
    
    exp.doExplosionA();
    exp.doExplosionB(true);
    
    MCH_PacketEffectExplosion.ExplosionParam param = MCH_PacketEffectExplosion.create();
    param.exploderID = W_Entity.getEntityId(entityExploded);
    param.posX = x;
    param.posY = y;
    param.posZ = z;
    param.size = size;
    param.inWater = true;
    MCH_PacketEffectExplosion.send(param);
    
    return exp.result;
  }
  
  public static void playExplosionSound(World w, double x, double y, double z)
  {
    Random rand = new Random();
    W_WorldFunc.DEF_playSoundEffect(w, x, y, z, "random.explode", 4.0F, (1.0F + (rand.nextFloat() - rand.nextFloat()) * 0.2F) * 0.7F);
  }
  



  public static void effectExplosion(World world, Entity exploder, double explosionX, double explosionY, double explosionZ, float explosionSize, boolean isSmoking)
  {
    List affectedBlockPositions = new ArrayList();
    int field_77289_h = 16;
    
    float f = explosionSize;
    HashSet hashset = new HashSet();
    






    for (int i = 0; i < 16; i++)
    {
      for (int j = 0; j < 16; j++)
      {
        for (int k = 0; k < 16; k++)
        {
          if ((i == 0) || (i == 15) || (j == 0) || (j == 15) || (k == 0) || (k == 15))
          {
            double d3 = i / 15.0F * 2.0F - 1.0F;
            double d4 = j / 15.0F * 2.0F - 1.0F;
            double d5 = k / 15.0F * 2.0F - 1.0F;
            double d6 = Math.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
            d3 /= d6;
            d4 /= d6;
            d5 /= d6;
            float f1 = explosionSize * (0.7F + world.rand.nextFloat() * 0.6F);
            double d0 = explosionX;
            double d1 = explosionY;
            double d2 = explosionZ;
            
            for (float f2 = 0.3F; f1 > 0.0F; f1 -= f2 * 0.75F)
            {
              int l = MathHelper.floor_double(d0);
              int i1 = MathHelper.floor_double(d1);
              int j1 = MathHelper.floor_double(d2);
              int k1 = W_WorldFunc.getBlockId(world, l, i1, j1);
              
              if (k1 > 0)
              {
                Block block = W_Block.getBlockById(k1);
                float f3 = block.getExplosionResistance(exploder, world, l, i1, j1, explosionX, explosionY, explosionZ);
                f1 -= (f3 + 0.3F) * f2;
              }
              
              if (f1 > 0.0F)
              {
                hashset.add(new ChunkPosition(l, i1, j1));
              }
              
              d0 += d3 * f2;
              d1 += d4 * f2;
              d2 += d5 * f2;
            }
          }
        }
      }
    }
    
    affectedBlockPositions.addAll(hashset);
    


    if ((explosionSize >= 2.0F) && (isSmoking))
    {
      MCH_ParticlesUtil.DEF_spawnParticle("hugeexplosion", explosionX, explosionY, explosionZ, 1.0D, 0.0D, 0.0D, 10.0F);

    }
    else
    {

      MCH_ParticlesUtil.DEF_spawnParticle("largeexplode", explosionX, explosionY, explosionZ, 1.0D, 0.0D, 0.0D, 10.0F);
    }
    






    if (isSmoking)
    {
      Iterator iterator = affectedBlockPositions.iterator();
      
      int cnt = 0;
      int flareCnt = (int)explosionSize;
      while (iterator.hasNext())
      {
        ChunkPosition chunkposition = (ChunkPosition)iterator.next();
        int i = W_ChunkPosition.getChunkPosX(chunkposition);
        int j = W_ChunkPosition.getChunkPosY(chunkposition);
        int k = W_ChunkPosition.getChunkPosZ(chunkposition);
        int l = W_WorldFunc.getBlockId(world, i, j, k);
        
        cnt++;
        

        double d0 = i + world.rand.nextFloat();
        double d1 = j + world.rand.nextFloat();
        double d2 = k + world.rand.nextFloat();
        double mx = d0 - explosionX;
        double my = d1 - explosionY;
        double mz = d2 - explosionZ;
        double d6 = MathHelper.sqrt_double(mx * mx + my * my + mz * mz);
        mx /= d6;
        my /= d6;
        mz /= d6;
        double d7 = 0.5D / (d6 / explosionSize + 0.1D);
        d7 *= (world.rand.nextFloat() * world.rand.nextFloat() + 0.3F);
        mx *= d7 * 0.5D;
        my *= d7 * 0.5D;
        mz *= d7 * 0.5D;
        
        double px = (d0 + explosionX * 1.0D) / 2.0D;
        double py = (d1 + explosionY * 1.0D) / 2.0D;
        double pz = (d2 + explosionZ * 1.0D) / 2.0D;
        
        double r = 3.141592653589793D * world.rand.nextInt(360) / 180.0D;
        if ((explosionSize >= 4.0F) && (flareCnt > 0))
        {
          double a = Math.min(explosionSize / 12.0F, 0.6D) * (0.5F + world.rand.nextFloat() * 0.5F);
          world.spawnEntityInWorld(new mcheli.flare.MCH_EntityFlare(world, px, py + 2.0D, pz, Math.sin(r) * a, (1.0D + my / 5.0D) * a, Math.cos(r) * a, 2.0F, 0));
          






          flareCnt--;
        }
        boolean ret;
        if (cnt % 4 == 0)
        {
          float bdf = Math.min(explosionSize / 3.0F, 2.0F) * (0.5F + world.rand.nextFloat() * 0.5F);
          ret = MCH_ParticlesUtil.spawnParticleTileDust(world, (int)(px + 0.5D), (int)(py - 0.5D), (int)(pz + 0.5D), px, py + 1.0D, pz, Math.sin(r) * bdf, 0.5D + my / 5.0D * bdf, Math.cos(r) * bdf, Math.min(explosionSize / 2.0F, 3.0F) * (0.5F + world.rand.nextFloat() * 0.5F));
        }
        








        int es = (int)(explosionSize >= 4.0F ? explosionSize : 4.0F);
        if ((explosionSize <= 1.0F) || (cnt % es == 0))
        {
          if (world.rand.nextBoolean())
          {
            my *= 3.0D;
            mx *= 0.1D;
            mz *= 0.1D;
          }
          else
          {
            my *= 0.2D;
            mx *= 3.0D;
            mz *= 3.0D;
          }
          

          MCH_ParticleParam prm = new MCH_ParticleParam(world, "explode", px, py, pz, mx, my, mz, explosionSize < 8.0F ? explosionSize * 2.0F : explosionSize < 2.0F ? 2.0F : 16.0F);
          


          prm.r = (prm.g = prm.b = 0.3F + world.rand.nextFloat() * 0.4F);
          prm.r += 0.1F;
          prm.g += 0.05F;
          prm.b += 0.0F;
          prm.age = (10 + world.rand.nextInt(30)); MCH_ParticleParam 
            tmp1231_1229 = prm;tmp1231_1229.age = ((int)(tmp1231_1229.age * (explosionSize < 6.0F ? explosionSize : 6.0F)));
          prm.age = (prm.age * 2 / 3);
          prm.diffusible = true;
          
          MCH_ParticlesUtil.spawnParticle(prm);
        }
      }
    }
  }
  






  public static void DEF_effectExplosion(World world, Entity exploder, double explosionX, double explosionY, double explosionZ, float explosionSize, boolean isSmoking)
  {
    List affectedBlockPositions = new ArrayList();
    int field_77289_h = 16;
    
    float f = explosionSize;
    HashSet hashset = new HashSet();
    






    for (int i = 0; i < 16; i++)
    {
      for (int j = 0; j < 16; j++)
      {
        for (int k = 0; k < 16; k++)
        {
          if ((i == 0) || (i == 15) || (j == 0) || (j == 15) || (k == 0) || (k == 15))
          {
            double d3 = i / 15.0F * 2.0F - 1.0F;
            double d4 = j / 15.0F * 2.0F - 1.0F;
            double d5 = k / 15.0F * 2.0F - 1.0F;
            double d6 = Math.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
            d3 /= d6;
            d4 /= d6;
            d5 /= d6;
            float f1 = explosionSize * (0.7F + world.rand.nextFloat() * 0.6F);
            double d0 = explosionX;
            double d1 = explosionY;
            double d2 = explosionZ;
            
            for (float f2 = 0.3F; f1 > 0.0F; f1 -= f2 * 0.75F)
            {
              int l = MathHelper.floor_double(d0);
              int i1 = MathHelper.floor_double(d1);
              int j1 = MathHelper.floor_double(d2);
              int k1 = W_WorldFunc.getBlockId(world, l, i1, j1);
              
              if (k1 > 0)
              {
                Block block = W_Block.getBlockById(k1);
                float f3 = block.getExplosionResistance(exploder, world, l, i1, j1, explosionX, explosionY, explosionZ);
                f1 -= (f3 + 0.3F) * f2;
              }
              
              if (f1 > 0.0F)
              {
                hashset.add(new ChunkPosition(l, i1, j1));
              }
              
              d0 += d3 * f2;
              d1 += d4 * f2;
              d2 += d5 * f2;
            }
          }
        }
      }
    }
    
    affectedBlockPositions.addAll(hashset);
    


    if ((explosionSize >= 2.0F) && (isSmoking))
    {
      MCH_ParticlesUtil.DEF_spawnParticle("hugeexplosion", explosionX, explosionY, explosionZ, 1.0D, 0.0D, 0.0D, 10.0F);
    }
    else
    {
      MCH_ParticlesUtil.DEF_spawnParticle("largeexplode", explosionX, explosionY, explosionZ, 1.0D, 0.0D, 0.0D, 10.0F);
    }
    




    if (isSmoking)
    {
      Iterator iterator = affectedBlockPositions.iterator();
      
      while (iterator.hasNext())
      {
        ChunkPosition chunkposition = (ChunkPosition)iterator.next();
        int i = W_ChunkPosition.getChunkPosX(chunkposition);
        int j = W_ChunkPosition.getChunkPosY(chunkposition);
        int k = W_ChunkPosition.getChunkPosZ(chunkposition);
        int l = W_WorldFunc.getBlockId(world, i, j, k);
        


        double d0 = i + world.rand.nextFloat();
        double d1 = j + world.rand.nextFloat();
        double d2 = k + world.rand.nextFloat();
        double d3 = d0 - explosionX;
        double d4 = d1 - explosionY;
        double d5 = d2 - explosionZ;
        double d6 = MathHelper.sqrt_double(d3 * d3 + d4 * d4 + d5 * d5);
        d3 /= d6;
        d4 /= d6;
        d5 /= d6;
        double d7 = 0.5D / (d6 / explosionSize + 0.1D);
        d7 *= (world.rand.nextFloat() * world.rand.nextFloat() + 0.3F);
        d3 *= d7;
        d4 *= d7;
        d5 *= d7;
        MCH_ParticlesUtil.DEF_spawnParticle("explode", (d0 + explosionX * 1.0D) / 2.0D, (d1 + explosionY * 1.0D) / 2.0D, (d2 + explosionZ * 1.0D) / 2.0D, d3, d4, d5, 10.0F);
        




        MCH_ParticlesUtil.DEF_spawnParticle("smoke", d0, d1, d2, d3, d4, d5, 10.0F);
      }
    }
  }
  



  public static void effectExplosionInWater(World world, Entity exploder, double explosionX, double explosionY, double explosionZ, float explosionSize, boolean isSmoking)
  {
    if (explosionSize <= 0.0F) return;
    int range = (int)(explosionSize + 0.5D) / 1;
    int ex = (int)(explosionX + 0.5D);
    int ey = (int)(explosionY + 0.5D);
    int ez = (int)(explosionZ + 0.5D);
    


    for (int y = -range; y <= range; y++)
    {
      if (ey + y >= 1)
      {
        for (int x = -range; x <= range; x++)
        {
          for (int z = -range; z <= range; z++)
          {
            int d = x * x + y * y + z * z;
            if (d < range * range)
            {
              if (W_Block.isEqualTo(W_WorldFunc.getBlock(world, ex + x, ey + y, ez + z), W_Block.getWater()))
              {
                int n = explosionRNG.nextInt(2);
                for (int i = 0; i < n; i++)
                {
                  MCH_ParticleParam prm = new MCH_ParticleParam(world, "splash", ex + x, ey + y, ez + z, x / range * (explosionRNG.nextFloat() - 0.2D), 1.0D - Math.sqrt(x * x + z * z) / range + explosionRNG.nextFloat() * 0.4D * range * 0.4D, z / range * (explosionRNG.nextFloat() - 0.2D), explosionRNG.nextInt(range) * 3 + range);
                  




                  MCH_ParticlesUtil.spawnParticle(prm);
                }
              }
            }
          }
        }
      }
    }
  }
}
