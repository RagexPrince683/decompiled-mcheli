package mcheli.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import mcheli.wrapper.W_GuiButton;
import net.minecraft.client.Minecraft;







public class MCH_GuiList
  extends W_GuiButton
{
  public List<MCH_GuiListItem> listItems;
  public MCH_GuiSliderVertical scrollBar;
  public final int maxRowNum;
  public MCH_GuiListItem lastPushItem;
  
  public MCH_GuiList(int id, int maxRow, int posX, int posY, int w, int h, String name)
  {
    super(id, posX, posY, w, h, "");
    this.maxRowNum = (maxRow > 0 ? maxRow : 1);
    this.listItems = new ArrayList();
    this.scrollBar = new MCH_GuiSliderVertical(0, posX + w - 20, posY, 20, h, name, 0.0F, 0.0F, 0.0F, 1.0F);
    this.lastPushItem = null;
  }
  
  public void drawButton(Minecraft mc, int x, int y)
  {
    if (isVisible())
    {
      drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, -2143272896);
      
      this.scrollBar.drawButton(mc, x, y);
      
      for (int i = 0; i < this.maxRowNum; i++)
      {
        if (i + getStartRow() >= this.listItems.size())
          break;
        MCH_GuiListItem item = (MCH_GuiListItem)this.listItems.get(i + getStartRow());
        
        item.draw(mc, x, y, this.xPosition, this.yPosition + 5 + 20 * i);
      }
    }
  }
  
  public void addItem(MCH_GuiListItem item)
  {
    this.listItems.add(item);
    int listNum = this.listItems.size();
    this.scrollBar.valueMax = (listNum > this.maxRowNum ? listNum - this.maxRowNum : 0.0F);
  }
  
  public MCH_GuiListItem getItem(int i) {
    return i < getItemNum() ? (MCH_GuiListItem)this.listItems.get(i) : null;
  }
  
  public int getItemNum() {
    return this.listItems.size();
  }
  
  public void scrollUp(float a)
  {
    if (isVisible())
    {
      this.scrollBar.scrollUp(a);
    }
  }
  
  public void scrollDown(float a) {
    if (isVisible())
    {
      this.scrollBar.scrollDown(a);
    }
  }
  
  public int getStartRow() {
    int startRow = (int)this.scrollBar.getSliderValue();
    return startRow >= 0 ? startRow : 0;
  }
  
  protected void mouseDragged(Minecraft mc, int x, int y)
  {
    if (isVisible())
    {
      this.scrollBar.mouseDragged(mc, x, y);
    }
  }
  
  public boolean mousePressed(Minecraft mc, int x, int y)
  {
    boolean b = false;
    if (isVisible())
    {
      b |= this.scrollBar.mousePressed(mc, x, y);
      
      for (int i = 0; i < this.maxRowNum; i++)
      {
        if (i + getStartRow() >= this.listItems.size())
          break;
        MCH_GuiListItem item = (MCH_GuiListItem)this.listItems.get(i + getStartRow());
        
        if (item.mousePressed(mc, x, y))
        {
          this.lastPushItem = item;
          b = true;
        }
      }
    }
    return b;
  }
  
  public void mouseReleased(int x, int y)
  {
    if (isVisible())
    {
      this.scrollBar.mouseReleased(x, y);
      MCH_GuiListItem item; for (Iterator i$ = this.listItems.iterator(); i$.hasNext(); item.mouseReleased(x, y)) item = (MCH_GuiListItem)i$.next();
    }
  }
}
