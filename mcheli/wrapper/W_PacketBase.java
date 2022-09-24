package mcheli.wrapper;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class W_PacketBase implements IMessage
{
  com.google.common.io.ByteArrayDataInput data;
  
  public W_PacketBase() {}
  
  public byte[] createData()
  {
    return null;
  }
  


  public void fromBytes(ByteBuf buf)
  {
    byte[] dst = new byte[buf.array().length - 1];
    buf.getBytes(0, dst);
    this.data = com.google.common.io.ByteStreams.newDataInput(dst);
  }
  
  public void toBytes(ByteBuf buf) {
    buf.writeBytes(createData());
  }
}
