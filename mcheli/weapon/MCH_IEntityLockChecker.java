package mcheli.weapon;

import net.minecraft.entity.Entity;

public abstract interface MCH_IEntityLockChecker
{
  public abstract boolean canLockEntity(Entity paramEntity);
}
