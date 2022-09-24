package mcheli.wrapper;

import net.minecraft.client.audio.SoundManager;
import net.minecraft.entity.Entity;

public class W_SoundManagerFunc
{
  public W_SoundManagerFunc() {}
  
  public static void DEF_playEntitySound(SoundManager sm, String name, Entity entity, float volume, float pitch, boolean par5)
  {
    sm.playSound(new W_Sound(new net.minecraft.util.ResourceLocation(name), volume, pitch, entity.posX, entity.posY, entity.posZ));
  }
  





  public static void MOD_playEntitySound(SoundManager sm, String name, Entity entity, float volume, float pitch, boolean par5)
  {
    DEF_playEntitySound(sm, W_MOD.DOMAIN + ":" + name, entity, volume, pitch, par5);
  }
}
