package mcheli.wrapper;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class W_PacketDummy
  implements IMessage
{
  public W_PacketDummy() {}
  
  public void fromBytes(ByteBuf buf) {}
  
  public void toBytes(ByteBuf buf) {}
}
