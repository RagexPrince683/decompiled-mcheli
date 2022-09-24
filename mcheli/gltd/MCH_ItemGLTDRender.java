package mcheli.gltd;

import mcheli.wrapper.W_McClient;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;

public class MCH_ItemGLTDRender implements net.minecraftforge.client.IItemRenderer
{
  public MCH_ItemGLTDRender() {}
  
  public boolean handleRenderType(ItemStack item, IItemRenderer.ItemRenderType type)
  {
    return (type == IItemRenderer.ItemRenderType.EQUIPPED) || (type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON) || (type == IItemRenderer.ItemRenderType.ENTITY);
  }
  





  public boolean shouldUseRenderHelper(IItemRenderer.ItemRenderType type, ItemStack item, IItemRenderer.ItemRendererHelper helper)
  {
    return type == IItemRenderer.ItemRenderType.ENTITY;
  }
  

  public void renderItem(IItemRenderer.ItemRenderType type, ItemStack item, Object... data)
  {
    GL11.glPushMatrix();
    
    GL11.glEnable(2884);
    
    W_McClient.MOD_bindTexture("textures/gltd.png");
    
    switch (type)
    {
    case ENTITY: 
      GL11.glEnable(32826);
      GL11.glEnable(2903);
      GL11.glScalef(1.0F, 1.0F, 1.0F);
      MCH_RenderGLTD.model.renderAll();
      GL11.glDisable(32826);
      break;
    case EQUIPPED: 
      GL11.glEnable(32826);
      GL11.glEnable(2903);
      GL11.glTranslatef(0.0F, 0.005F, -0.165F);
      GL11.glRotatef(-10.0F, 0.0F, 0.0F, 1.0F);
      GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
      MCH_RenderGLTD.model.renderAll();
      GL11.glDisable(32826);
      break;
    case EQUIPPED_FIRST_PERSON: 
      GL11.glEnable(32826);
      GL11.glEnable(2903);
      GL11.glTranslatef(0.3F, 0.5F, -0.5F);
      GL11.glScalef(0.5F, 0.5F, 0.5F);
      GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
      GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
      MCH_RenderGLTD.model.renderAll();
      GL11.glDisable(32826);
      break;
    }
    
    



    GL11.glPopMatrix();
  }
}
