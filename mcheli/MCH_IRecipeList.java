package mcheli;

import net.minecraft.item.crafting.IRecipe;

public abstract interface MCH_IRecipeList
{
  public abstract int getRecipeListSize();
  
  public abstract IRecipe getRecipe(int paramInt);
}
