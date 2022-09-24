package mcheli.aircraft;

public abstract interface MCH_IEntityCanRideAircraft
{
  public abstract boolean isSkipNormalRender();
  
  public abstract boolean canRideAircraft(MCH_EntityAircraft paramMCH_EntityAircraft, int paramInt, MCH_SeatRackInfo paramMCH_SeatRackInfo);
}
