package mcheli.aircraft;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import mcheli.MCH_Achievement;
import mcheli.MCH_Camera;
import mcheli.MCH_Color;
import mcheli.MCH_CommonProxy;
import mcheli.MCH_Config;
import mcheli.MCH_ConfigPrm;
import mcheli.MCH_Explosion;
import mcheli.MCH_Lib;
import mcheli.MCH_LowPassFilterFloat;
import mcheli.MCH_MOD;
import mcheli.MCH_Math;
import mcheli.MCH_Math.FMatrix;
import mcheli.MCH_Math.FVector3D;
import mcheli.MCH_Queue;
import mcheli.MCH_Vector2;
import mcheli.MCH_ViewEntityDummy;
import mcheli.chain.MCH_EntityChain;
import mcheli.command.MCH_Command;
import mcheli.flare.MCH_Flare;
import mcheli.mob.MCH_EntityGunner;
import mcheli.mob.MCH_ItemSpawnGunner;
import mcheli.multiplay.MCH_Multiplay;
import mcheli.parachute.MCH_EntityParachute;
import mcheli.particles.MCH_ParticleParam;
import mcheli.particles.MCH_ParticlesUtil;
import mcheli.tool.MCH_ItemWrench;
import mcheli.uav.MCH_EntityUavStation;
import mcheli.weapon.MCH_EntityTvMissile;
import mcheli.weapon.MCH_IEntityLockChecker;
import mcheli.weapon.MCH_WeaponBase;
import mcheli.weapon.MCH_WeaponCreator;
import mcheli.weapon.MCH_WeaponDummy;
import mcheli.weapon.MCH_WeaponInfo;
import mcheli.weapon.MCH_WeaponInfo.MuzzleFlash;
import mcheli.weapon.MCH_WeaponInfo.RoundItem;
import mcheli.weapon.MCH_WeaponParam;
import mcheli.weapon.MCH_WeaponSet;
import mcheli.weapon.MCH_WeaponSmoke;
import mcheli.wrapper.W_AxisAlignedBB;
import mcheli.wrapper.W_Block;
import mcheli.wrapper.W_Blocks;
import mcheli.wrapper.W_Entity;
import mcheli.wrapper.W_EntityContainer;
import mcheli.wrapper.W_EntityPlayer;
import mcheli.wrapper.W_EntityRenderer;
import mcheli.wrapper.W_Item;
import mcheli.wrapper.W_Lib;
import mcheli.wrapper.W_NBTTag;
import mcheli.wrapper.W_Reflection;
import mcheli.wrapper.W_WorldFunc;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public abstract class MCH_EntityAircraft extends W_EntityContainer
		implements MCH_IEntityLockChecker, MCH_IEntityCanRideAircraft, IEntityAdditionalSpawnData {
	private static final int DATAWT_ID_DAMAGE = 19;
	private static final int DATAWT_ID_TYPE = 20;
	private static final int DATAWT_ID_TEXTURE_NAME = 21;
	private static final int DATAWT_ID_UAV_STATION = 22;
	private static final int DATAWT_ID_STATUS = 23;
	private static final int CMN_ID_FLARE = 0;
	private static final int CMN_ID_FREE_LOOK = 1;
	private static final int CMN_ID_RELOADING = 2;
	private static final int CMN_ID_INGINITY_AMMO = 3;
	private static final int CMN_ID_INGINITY_FUEL = 4;
	private static final int CMN_ID_RAPELLING = 5;
	private static final int CMN_ID_SEARCHLIGHT = 6;
	private static final int CMN_ID_CNTRL_LEFT = 7;
	private static final int CMN_ID_CNTRL_RIGHT = 8;
	private static final int CMN_ID_CNTRL_UP = 9;
	private static final int CMN_ID_CNTRL_DOWN = 10;
	private static final int CMN_ID_CNTRL_BRAKE = 11;
	private static final int CMN_ID_GUNNER = 12;
	private static final int DATAWT_ID_USE_WEAPON = 24;
	private static final int DATAWT_ID_FUEL = 25;
	private static final int DATAWT_ID_ROT_ROLL = 26;
	private static final int DATAWT_ID_COMMAND = 27;
	private static final int DATAWT_ID_THROTTLE = 29;
	protected static final int DATAWT_ID_FOLD_STAT = 30;
	protected static final int DATAWT_ID_PART_STAT = 31;
	protected static final int PART_ID_CANOPY = 0;
	protected static final int PART_ID_NOZZLE = 1;
	protected static final int PART_ID_LANDINGGEAR = 2;
	protected static final int PART_ID_WING = 3;
	protected static final int PART_ID_HATCH = 4;
	public static final byte LIMIT_GROUND_PITCH = 40;
	public static final byte LIMIT_GROUND_ROLL = 40;
	public boolean isRequestedSyncStatus;
	private MCH_AircraftInfo acInfo;
	private int commonStatus;
	private Entity[] partEntities;
	private MCH_EntityHitBox pilotSeat;
	private MCH_EntitySeat[] seats;
	private MCH_SeatInfo[] seatsInfo;
	private String commonUniqueId;
	private int seatSearchCount;
	protected double velocityX;
	protected double velocityY;
	protected double velocityZ;
	public boolean keepOnRideRotation;
	protected int aircraftPosRotInc;
	protected double aircraftX;
	protected double aircraftY;
	protected double aircraftZ;
	protected double aircraftYaw;
	protected double aircraftPitch;
	public boolean aircraftRollRev;
	public boolean aircraftRotChanged;
	public float rotationRoll;
	public float prevRotationRoll;
	private double currentThrottle;
	private double prevCurrentThrottle;
	public double currentSpeed;
	public int currentFuel;
	public float throttleBack = 0.0F;

	public double beforeHoverThrottle;

	public int waitMountEntity = 0;

	public boolean throttleUp = false;
	public boolean throttleDown = false;
	public boolean moveLeft = false;
	public boolean moveRight = false;

	public MCH_LowPassFilterFloat lowPassPartialTicks;

	private MCH_Radar entityRadar;

	private int radarRotate;

	private MCH_Flare flareDv;

	private int currentFlareIndex;

	protected MCH_WeaponSet[] weapons;

	protected int[] currentWeaponID;

	public float lastRiderYaw;

	public float prevLastRiderYaw;

	public float lastRiderPitch;
	public float prevLastRiderPitch;
	protected MCH_WeaponSet dummyWeapon;
	protected int useWeaponStat;
	protected int hitStatus;
	protected final MCH_SoundUpdater soundUpdater;
	protected Entity lastRiddenByEntity;
	protected Entity lastRidingEntity;

	protected class UnmountReserve {
		final Entity entity;
		final double posX;
		final double posY;
		final double posZ;
		int cnt;

		public UnmountReserve(Entity e, double x, double y, double z) {
			this.cnt = 5;
			this.entity = e;
			this.posX = x;
			this.posY = y;
			this.posZ = z;
		}
	}

	public List<UnmountReserve> listUnmountReserve = new ArrayList();

	private int countOnUpdate;

	private MCH_EntityChain towChainEntity;

	private MCH_EntityChain towedChainEntity;

	public MCH_Camera camera;

	private int cameraId;

	protected boolean isGunnerMode = false;
	protected boolean isGunnerModeOtherSeat = false;
	private boolean isHoveringMode = false;
	public static final int CAMERA_PITCH_MIN = -30;
	public static final int CAMERA_PITCH_MAX = 70;
	private MCH_EntityTvMissile TVmissile;
	protected boolean isGunnerFreeLookMode = false;

	public final MCH_MissileDetector missileDetector;

	public int serverNoMoveCount = 0;

	public int repairCount;

	public int beforeDamageTaken;

	public int timeSinceHit;
	private int despawnCount;
	public float rotDestroyedYaw;
	public float rotDestroyedPitch;
	public float rotDestroyedRoll;
	public int damageSinceDestroyed;
	public boolean isFirstDamageSmoke = true;
	public Vec3[] prevDamageSmokePos = new Vec3[0];

	private MCH_EntityUavStation uavStation;

	public boolean cs_dismountAll;

	public boolean cs_heliAutoThrottleDown;

	public boolean cs_planeAutoThrottleDown;

	public boolean cs_tankAutoThrottleDown;

	public MCH_Parts partHatch;

	public MCH_Parts partCanopy;

	public MCH_Parts partLandingGear;

	public double prevRidingEntityPosX;

	public double prevRidingEntityPosY;

	public double prevRidingEntityPosZ;

	public boolean canRideRackStatus;

	private int modeSwitchCooldown;

	public MCH_BoundingBox[] extraBoundingBox;

	public float lastBBDamageFactor;

	private final MCH_AircraftInventory inventory;
	private double fuelConsumption;
	private int fuelSuppliedCount;
	private int supplyAmmoWait;
	private boolean beforeSupplyAmmo;
	public WeaponBay[] weaponBays;
	public float[] rotPartRotation;
	public float[] prevRotPartRotation;
	public float[] rotCrawlerTrack = new float[2];

	public float[] prevRotCrawlerTrack = new float[2];

	public float[] throttleCrawlerTrack = new float[2];

	public float[] rotTrackRoller = new float[2];

	public float[] prevRotTrackRoller = new float[2];

	public float rotWheel = 0.0F;
	public float prevRotWheel = 0.0F;
	public float rotYawWheel = 0.0F;
	public float prevRotYawWheel = 0.0F;

	private boolean isParachuting;

	public float ropesLength = 0.0F;

	private MCH_Queue<Vec3> prevPosition;

	private int tickRepelling;

	private int lastUsedRopeIndex;

	private boolean dismountedUserCtrl;

	public float lastSearchLightYaw;
	public float lastSearchLightPitch;
	public float rotLightHatch = 0.0F;
	public float prevRotLightHatch = 0.0F;

	public int recoilCount = 0;
	public float recoilYaw = 0.0F;
	public float recoilValue = 0.0F;

	public int brightnessHigh = 240;
	public int brightnessLow = 240;

	public final HashMap<Entity, Integer> noCollisionEntities = new HashMap();

	private double lastCalcLandInDistanceCount;

	private double lastLandInDistance;
	public float thirdPersonDist = 4.0F;

	public Entity lastAttackedEntity = null;

	public class WeaponBay {
		public float rot;
		public float prevRot;

		public WeaponBay() {
			this.rot = 0.0F;
			this.prevRot = 0.0F;
		}
	}

	public MCH_EntityAircraft(World world) {
		super(world);
		MCH_Lib.DbgLog(world, "MCH_EntityAircraft : " + toString(), new Object[0]);

		this.isRequestedSyncStatus = false;
		setAcInfo(null);

		this.dropContentsWhenDead = false;

		this.ignoreFrustumCheck = true;

		this.flareDv = new MCH_Flare(world, this);
		this.currentFlareIndex = 0;

		this.entityRadar = new MCH_Radar(world);
		this.radarRotate = 0;

		this.currentWeaponID = new int[0];

		this.aircraftPosRotInc = 0;
		this.aircraftX = 0.0D;
		this.aircraftY = 0.0D;
		this.aircraftZ = 0.0D;
		this.aircraftYaw = 0.0D;
		this.aircraftPitch = 0.0D;

		this.currentSpeed = 0.0D;
		setCurrentThrottle(0.0D);
		this.currentFuel = 0;

		this.cs_dismountAll = false;
		this.cs_heliAutoThrottleDown = true;
		this.cs_planeAutoThrottleDown = false;

		this.renderDistanceWeight = (2.0D * MCH_Config.RenderDistanceWeight.prmDouble);

		setCommonUniqueId("");
		this.seatSearchCount = 0;
		this.seatsInfo = null;
		this.seats = new MCH_EntitySeat[0];

		this.pilotSeat = new MCH_EntityHitBox(world, this, 1.0F, 1.0F);
		this.pilotSeat.parent = this;
		this.partEntities = new Entity[] { this.pilotSeat };

		setTextureName("");

		this.camera = new MCH_Camera(world, this, this.posX, this.posY, this.posZ);
		setCameraId(0);

		this.lastRiddenByEntity = null;
		this.lastRidingEntity = null;

		this.soundUpdater = MCH_MOD.proxy.CreateSoundUpdater(this);

		this.countOnUpdate = 0;

		setTowChainEntity(null);

		this.dummyWeapon = new MCH_WeaponSet(
				new MCH_WeaponDummy(this.worldObj, Vec3.createVectorHelper(0.0D, 0.0D, 0.0D), 0.0F, 0.0F, "", null));
		this.useWeaponStat = 0;
		this.hitStatus = 0;

		this.repairCount = 0;
		this.beforeDamageTaken = 0;
		this.timeSinceHit = 0;
		setDespawnCount(0);

		this.missileDetector = new MCH_MissileDetector(this, world);

		this.uavStation = null;

		this.modeSwitchCooldown = 0;
		this.partHatch = null;
		this.partCanopy = null;
		this.partLandingGear = null;

		this.weaponBays = new WeaponBay[0];

		this.rotPartRotation = new float[0];
		this.prevRotPartRotation = new float[0];

		this.lastRiderYaw = 0.0F;
		this.prevLastRiderYaw = 0.0F;
		this.lastRiderPitch = 0.0F;
		this.prevLastRiderPitch = 0.0F;

		this.rotationRoll = 0.0F;
		this.prevRotationRoll = 0.0F;

		this.lowPassPartialTicks = new MCH_LowPassFilterFloat(10);

		this.extraBoundingBox = new MCH_BoundingBox[0];
		W_Reflection.setBoundingBox(this, new MCH_AircraftBoundingBox(this));
		this.lastBBDamageFactor = 1.0F;

		this.inventory = new MCH_AircraftInventory(this);

		this.fuelConsumption = 0.0D;
		this.fuelSuppliedCount = 0;

		this.canRideRackStatus = false;

		this.isParachuting = false;
		this.prevPosition = new MCH_Queue(10, Vec3.createVectorHelper(0.0D, 0.0D, 0.0D));

		this.lastSearchLightYaw = (this.lastSearchLightPitch = 0.0F);
	}

	protected void entityInit() {
		super.entityInit();
		getDataWatcher().addObject(20, "");
		getDataWatcher().addObject(19, new Integer(0));
		getDataWatcher().addObject(23, new Integer(0));
		getDataWatcher().addObject(24, new Integer(0));
		getDataWatcher().addObject(25, new Integer(0));
		getDataWatcher().addObject(21, "");
		getDataWatcher().addObject(22, new Integer(0));
		getDataWatcher().addObject(26, new Short((short) 0));
		getDataWatcher().addObject(27, new String(""));
		getDataWatcher().addObject(29, new Integer(0));
		getDataWatcher().addObject(31, new Integer(0));

		if (!this.worldObj.isRemote) {
			setCommonStatus(3, MCH_Config.InfinityAmmo.prmBool);
			setCommonStatus(4, MCH_Config.InfinityFuel.prmBool);
			setGunnerStatus(true);
		}

		getEntityData().setString("EntityType", getEntityType());
	}

	public float getServerRoll() {
		return getDataWatcher().getWatchableObjectShort(26);
	}

	public float getRotYaw() {
		return this.rotationYaw;
	}

	public float getRotPitch() {
		return this.rotationPitch;
	}

	public float getRotRoll() {
		return this.rotationRoll;
	}

	public void setRotYaw(float f) {
		this.rotationYaw = f;
	}

	public void setRotPitch(float f) {
		this.rotationPitch = f;
	}

	public void setRotPitch(float f, String msg) {
		setRotPitch(f);
	}

	public void setRotRoll(float f) {
		this.rotationRoll = f;
	}

	public void applyOnGroundPitch(float factor) {
		if (getAcInfo() != null) {
			float ogp = getAcInfo().onGroundPitch;
			float pitch = getRotPitch();
			pitch -= ogp;
			pitch *= factor;
			pitch += ogp;
			setRotPitch(pitch, "applyOnGroundPitch");
		}
		setRotRoll(getRotRoll() * factor);
	}

	public float calcRotYaw(float partialTicks) {
		return this.prevRotationYaw + (getRotYaw() - this.prevRotationYaw) * partialTicks;
	}

	public float calcRotPitch(float partialTicks) {
		return this.prevRotationPitch + (getRotPitch() - this.prevRotationPitch) * partialTicks;
	}

	public float calcRotRoll(float partialTicks) {
		return this.prevRotationRoll + (getRotRoll() - this.prevRotationRoll) * partialTicks;
	}

	protected void setRotation(float y, float p) {
		setRotYaw(y % 360.0F);
		setRotPitch(p % 360.0F);
	}

	public boolean isInfinityAmmo(Entity player) {
		return (isCreative(player)) || (getCommonStatus(3));
	}

	public boolean isInfinityFuel(Entity player, boolean checkOtherSeet) {
		if ((isCreative(player)) || (getCommonStatus(4))) {
			return true;
		}
		if (checkOtherSeet) {
			for (MCH_EntitySeat seat : getSeats()) {
				if (seat != null) {
					if (isCreative(seat.riddenByEntity))
						return true;
				}
			}
		}
		return false;
	}

	public void setCommand(String s, EntityPlayer player) {
		if (!this.worldObj.isRemote) {
			if (MCH_Command.canUseCommand(player)) {
				setCommandForce(s);
			}
		}
	}

	public void setCommandForce(String s) {
		if (!this.worldObj.isRemote) {
			getDataWatcher().updateObject(27, s);
		}
	}

	public String getCommand() {
		return getDataWatcher().getWatchableObjectString(27);
	}

	public String getKindName() {
		return "";
	}

	public String getEntityType() {
		return "";
	}

	public void setTypeName(String s) {
		String beforeType = getTypeName();
		if ((s != null) && (!s.isEmpty())) {
			if (s.compareTo(beforeType) != 0) {
				getDataWatcher().updateObject(20, String.valueOf(s));
				changeType(s);
				initRotationYaw(getRotYaw());
			}
		}
	}

	public String getTypeName() {
		return getDataWatcher().getWatchableObjectString(20);
	}

	public abstract void changeType(String paramString);

	public boolean isTargetDrone() {
		return (getAcInfo() != null) && (getAcInfo().isTargetDrone);
	}

	public boolean isUAV() {
		return (getAcInfo() != null) && (getAcInfo().isUAV);
	}

	public boolean isSmallUAV() {
		return (getAcInfo() != null) && (getAcInfo().isSmallUAV);
	}

	public boolean isAlwaysCameraView() {
		return (getAcInfo() != null) && (getAcInfo().alwaysCameraView);
	}

	public void setUavStation(MCH_EntityUavStation uavSt) {
		this.uavStation = uavSt;
		if (!this.worldObj.isRemote) {
			if (uavSt != null) {
				getDataWatcher().updateObject(22, Integer.valueOf(W_Entity.getEntityId(uavSt)));
			} else {
				getDataWatcher().updateObject(22, Integer.valueOf(0));
			}
		}
	}

	public float getStealth() {
		return getAcInfo() != null ? getAcInfo().stealth : 0.0F;
	}

	public MCH_AircraftInventory getGuiInventory() {
		return this.inventory;
	}

	public void openGui(EntityPlayer player) {
		if (!this.worldObj.isRemote) {
			player.openGui(MCH_MOD.instance, 1, this.worldObj, (int) this.posX, (int) this.posY, (int) this.posZ);
		}
	}

	public MCH_EntityUavStation getUavStation() {
		return isUAV() ? this.uavStation : null;
	}

	public static MCH_EntityAircraft getAircraft_RiddenOrControl(Entity rider) {
		if (rider != null) {
			if ((rider.ridingEntity instanceof MCH_EntityAircraft)) {
				return (MCH_EntityAircraft) rider.ridingEntity;
			}
			if ((rider.ridingEntity instanceof MCH_EntitySeat)) {
				return ((MCH_EntitySeat) rider.ridingEntity).getParent();
			}
			if ((rider.ridingEntity instanceof MCH_EntityUavStation)) {
				MCH_EntityUavStation uavStation = (MCH_EntityUavStation) rider.ridingEntity;
				return uavStation.getControlAircract();
			}
		}
		return null;
	}

	public boolean isCreative(Entity entity) {
		if (((entity instanceof EntityPlayer)) && (((EntityPlayer) entity).capabilities.isCreativeMode)) {
			return true;
		}
		if (((entity instanceof MCH_EntityGunner)) && (((MCH_EntityGunner) entity).isCreative)) {
			return true;
		}
		return false;
	}

	public Entity getRiddenByEntity() {
		if (isUAV()) {
			if (this.uavStation != null) {
				return this.uavStation.riddenByEntity;
			}
		}
		return this.riddenByEntity;
	}

	public boolean getCommonStatus(int bit) {
		return (this.commonStatus >> bit & 0x1) != 0;
	}

	public void setCommonStatus(int bit, boolean b) {
		setCommonStatus(bit, b, false);
	}

	public void setCommonStatus(int bit, boolean b, boolean writeClient) {
		if ((!this.worldObj.isRemote) || (writeClient)) {
			int bofore = this.commonStatus;
			int mask = 1 << bit;
			if (b) {
				this.commonStatus |= mask;
			} else {
				this.commonStatus &= (mask ^ 0xFFFFFFFF);
			}
			if (bofore != this.commonStatus) {
				MCH_Lib.DbgLog(this.worldObj, "setCommonStatus : %08X -> %08X ",
						new Object[] { Integer.valueOf(getDataWatcher().getWatchableObjectInt(23)),
								Integer.valueOf(this.commonStatus) });

				getDataWatcher().updateObject(23, Integer.valueOf(this.commonStatus));
			}
		}
	}

	public double getThrottle() {
		return 0.05D * getDataWatcher().getWatchableObjectInt(29);
	}

	public void setThrottle(double t) {
		int n = (int) (t * 20.0D);

		if ((n == 0) && (t > 0.0D)) {
			n = 1;
		}
		getDataWatcher().updateObject(29, Integer.valueOf(n));
	}

	public int getMaxHP() {
		return getAcInfo() != null ? getAcInfo().maxHp : 100;
	}

	public int getHP() {
		return getMaxHP() - getDamageTaken() >= 0 ? getMaxHP() - getDamageTaken() : 0;
	}

	public void setDamageTaken(int par1) {
		if (par1 < 0) {
			par1 = 0;
		}
		if (par1 > getMaxHP()) {
			par1 = getMaxHP();
		}
		getDataWatcher().updateObject(19, Integer.valueOf(par1));
	}

	public int getDamageTaken() {
		return getDataWatcher().getWatchableObjectInt(19);
	}

	public void destroyAircraft() {
		setSearchLight(false);
		switchHoveringMode(false);
		switchGunnerMode(false);
		for (int i = 0; i < getSeatNum() + 1; i++) {
			Entity e = getEntityBySeatId(i);
			if ((e instanceof EntityPlayer)) {
				switchCameraMode((EntityPlayer) e, 0);
			}
		}

		if (isTargetDrone()) {
			setDespawnCount(20 * MCH_Config.DespawnCount.prmInt / 10);
		} else {
			setDespawnCount(20 * MCH_Config.DespawnCount.prmInt);
		}
		this.rotDestroyedPitch = (this.rand.nextFloat() - 0.5F);
		this.rotDestroyedRoll = ((this.rand.nextFloat() - 0.5F) * 0.5F);
		this.rotDestroyedYaw = 0.0F;

		if ((isUAV()) && (getRiddenByEntity() != null)) {
			getRiddenByEntity().mountEntity(null);
		}

		if (!this.worldObj.isRemote) {
			ejectSeat(getRiddenByEntity());
			Entity entity = getEntityBySeatId(1);
			if (entity != null) {
				ejectSeat(entity);
			}

			float dmg = MCH_Config.KillPassengersWhenDestroyed.prmBool ? 100000.0F : 0.001F;
			DamageSource dse = DamageSource.generic;
			if (this.worldObj.difficultySetting.getDifficultyId() == 0) {
				if ((this.lastAttackedEntity instanceof EntityPlayer)) {
					dse = DamageSource.causePlayerDamage((EntityPlayer) this.lastAttackedEntity);
				}

			} else {
				dse = DamageSource.setExplosionSource(
						new Explosion(this.worldObj, this.lastAttackedEntity, this.posX, this.posY, this.posZ, 1.0F));
			}
			if (this.riddenByEntity != null) {
				this.riddenByEntity.attackEntityFrom(dse, dmg);
			}
			for (MCH_EntitySeat seat : getSeats()) {
				if ((seat != null) && (seat.riddenByEntity != null)) {
					seat.riddenByEntity.attackEntityFrom(dse, dmg);
				}
			}
		}
	}

	public boolean isDestroyed() {
		return getDespawnCount() > 0;
	}

	public int getDespawnCount() {
		return this.despawnCount;
	}

	public void setDespawnCount(int despawnCount) {
		this.despawnCount = despawnCount;
	}

	public boolean isEntityRadarMounted() {
		return getAcInfo() != null ? getAcInfo().isEnableEntityRadar : false;
	}

	public boolean canFloatWater() {
		return (getAcInfo() != null) && (getAcInfo().isFloat) && (!isDestroyed());
	}

	@SideOnly(Side.CLIENT)
	public int getBrightnessForRender(float par1) {
		if ((haveSearchLight()) && (isSearchLightON())) {
			return 15728880;
		}

		int i = MathHelper.floor_double(this.posX);
		int j = MathHelper.floor_double(this.posZ);

		if (this.worldObj.blockExists(i, 0, j)) {
			double d0 = (this.boundingBox.maxY - this.boundingBox.minY) * 0.66D;
			float fo = getAcInfo() != null ? getAcInfo().submergedDamageHeight : 0.0F;
			if (canFloatWater()) {
				fo = getAcInfo().floatOffset;
				if (fo < 0.0F)
					fo = -fo;
				fo += 1.0F;
			}
			int k = MathHelper.floor_double(this.posY + fo - this.yOffset + d0);
			int val = this.worldObj.getLightBrightnessForSkyBlocks(i, k, j, 0);
			int low = val & 0xFFFF;
			int high = val >> 16 & 0xFFFF;

			if (high < this.brightnessHigh) {
				if ((this.brightnessHigh > 0) && (getCountOnUpdate() % 2 == 0)) {
					this.brightnessHigh -= 1;
				}
			} else if (high > this.brightnessHigh) {
				this.brightnessHigh += 4;
				if (this.brightnessHigh > 240) {
					this.brightnessHigh = 240;
				}
			}

			return this.brightnessHigh << 16 | low;
		}

		return 0;
	}

	public MCH_AircraftInfo.CameraPosition getCameraPosInfo() {
		if (getAcInfo() == null) {
			return null;
		}
		Entity player = MCH_Lib.getClientPlayer();
		int sid = getSeatIdByEntity(player);

		if ((sid == 0) && (canSwitchCameraPos())) {
			if ((getCameraId() > 0) && (getCameraId() < getAcInfo().cameraPosition.size())) {
				return (MCH_AircraftInfo.CameraPosition) getAcInfo().cameraPosition.get(getCameraId());
			}
		}

		if ((sid > 0) && (sid < getSeatsInfo().length) && (getSeatsInfo()[sid].invCamPos)) {
			return getSeatsInfo()[sid].getCamPos();
		}

		return (MCH_AircraftInfo.CameraPosition) getAcInfo().cameraPosition.get(0);
	}

	public int getCameraId() {
		return this.cameraId;
	}

	public void setCameraId(int cameraId) {
		MCH_Lib.DbgLog(true, "MCH_EntityAircraft.setCameraId %d -> %d",
				new Object[] { Integer.valueOf(this.cameraId), Integer.valueOf(cameraId) });
		this.cameraId = cameraId;
	}

	public boolean canSwitchCameraPos() {
		return getCameraPosNum() >= 2;
	}

	public int getCameraPosNum() {
		if (getAcInfo() != null) {
			return getAcInfo().cameraPosition.size();
		}
		return 1;
	}

	public void onAcInfoReloaded() {
		if (getAcInfo() == null) {
			return;
		}
		setSize(getAcInfo().bodyWidth, getAcInfo().bodyHeight);
	}

	public void writeSpawnData(ByteBuf buffer) {
		if (getAcInfo() != null) {
			buffer.writeFloat(getAcInfo().bodyHeight);
			buffer.writeFloat(getAcInfo().bodyWidth);
			buffer.writeFloat(getAcInfo().thirdPersonDist);
			byte[] name = getTypeName().getBytes();
			buffer.writeShort(name.length);
			buffer.writeBytes(name);
		} else {
			buffer.writeFloat(this.height);
			buffer.writeFloat(this.width);
			buffer.writeFloat(4.0F);
			buffer.writeShort(0);
		}
	}

	public void readSpawnData(ByteBuf data) {
		try {
			float height = data.readFloat();
			float width = data.readFloat();
			setSize(width, height);
			this.thirdPersonDist = data.readFloat();
			int len = data.readShort();
			if (len > 0) {
				byte[] dst = new byte[len];
				data.readBytes(dst);
				changeType(new String(dst));
			}
		} catch (Exception e) {
			MCH_Lib.Log(this, "readSpawnData error!", new Object[0]);
			e.printStackTrace();
		}
	}

	protected void readEntityFromNBT(NBTTagCompound nbt) {
		setDespawnCount(nbt.getInteger("AcDespawnCount"));
		setTextureName(nbt.getString("TextureName"));
		setCommonUniqueId(nbt.getString("AircraftUniqueId"));
		setRotRoll(nbt.getFloat("AcRoll"));
		this.prevRotationRoll = getRotRoll();

		this.prevLastRiderYaw = (this.lastRiderYaw = nbt.getFloat("AcLastRYaw"));
		this.prevLastRiderPitch = (this.lastRiderPitch = nbt.getFloat("AcLastRPitch"));

		setPartStatus(nbt.getInteger("PartStatus"));

		setTypeName(nbt.getString("TypeName"));

		super.readEntityFromNBT(nbt);
		getGuiInventory().readEntityFromNBT(nbt);

		setCommandForce(nbt.getString("AcCommand"));

		setGunnerStatus(nbt.getBoolean("AcGunnerStatus"));

		setFuel(nbt.getInteger("AcFuel"));

		int[] wa_list = nbt.getIntArray("AcWeaponsAmmo");
		for (int i = 0; i < wa_list.length; i++) {
			getWeapon(i).setRestAllAmmoNum(wa_list[i]);
			getWeapon(i).reloadMag();
		}

		if (getDespawnCount() > 0) {
			setDamageTaken(getMaxHP());
		} else if (nbt.hasKey("AcDamage")) {
			setDamageTaken(nbt.getInteger("AcDamage"));
		}

		if ((haveSearchLight()) && (nbt.hasKey("SearchLight"))) {
			setSearchLight(nbt.getBoolean("SearchLight"));
		}

		this.dismountedUserCtrl = nbt.getBoolean("AcDismounted");
	}

	protected void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setString("TextureName", getTextureName());
		nbt.setString("AircraftUniqueId", getCommonUniqueId());
		nbt.setString("TypeName", getTypeName());
		nbt.setInteger("PartStatus", getPartStatus() & getLastPartStatusMask());
		nbt.setInteger("AcFuel", getFuel());
		nbt.setInteger("AcDespawnCount", getDespawnCount());
		nbt.setFloat("AcRoll", getRotRoll());
		nbt.setBoolean("SearchLight", isSearchLightON());
		nbt.setFloat("AcLastRYaw", getLastRiderYaw());
		nbt.setFloat("AcLastRPitch", getLastRiderPitch());
		nbt.setString("AcCommand", getCommand());
		if (!nbt.hasKey("AcGunnerStatus")) {
			setGunnerStatus(true);
		}
		nbt.setBoolean("AcGunnerStatus", getGunnerStatus());

		super.writeEntityToNBT(nbt);
		getGuiInventory().writeEntityToNBT(nbt);

		int[] wa_list = new int[getWeaponNum()];
		for (int i = 0; i < wa_list.length; i++) {
			wa_list[i] = (getWeapon(i).getRestAllAmmoNum() + getWeapon(i).getAmmoNum());
		}
		nbt.setTag("AcWeaponsAmmo", W_NBTTag.newTagIntArray("AcWeaponsAmmo", wa_list));
		nbt.setInteger("AcDamage", getDamageTaken());
		nbt.setBoolean("AcDismounted", this.dismountedUserCtrl);
	}

	public boolean attackEntityFrom(DamageSource damageSource, float org_damage) {
		float damage = org_damage;
		float damageFactor = this.lastBBDamageFactor;
		this.lastBBDamageFactor = 1.0F;

		if (isEntityInvulnerable())
			return false;
		if (this.isDead)
			return false;
		if (this.timeSinceHit > 0)
			return false;
		String dmt = damageSource.getDamageType();
		if (dmt.equalsIgnoreCase("inFire"))
			return false;
		if (dmt.equalsIgnoreCase("cactus"))
			return false;
		if (this.worldObj.isRemote) {
			return true;
		}
		damage = MCH_Config.applyDamageByExternal(this, damageSource, damage);

		if ((getAcInfo() != null) && (getAcInfo().invulnerable)) {
			damage = 0.0F;
		}

		if (damageSource == DamageSource.outOfWorld) {
			setDead();
		}

		if (!MCH_Multiplay.canAttackEntity(damageSource, this)) {
			return false;
		}

		if (dmt.equalsIgnoreCase("lava")) {
			damage *= (this.rand.nextInt(8) + 2);
			this.timeSinceHit = 2;
		}

		if (dmt.startsWith("explosion")) {
			this.timeSinceHit = 1;

		} else if (isMountedEntity(damageSource.getEntity())) {
			return false;
		}

		if (dmt.equalsIgnoreCase("onFire")) {
			this.timeSinceHit = 10;
		}

		boolean isCreative = false;
		boolean isSneaking = false;
		Entity entity = damageSource.getEntity();
		if ((entity instanceof EntityLivingBase)) {
			this.lastAttackedEntity = entity;
		}
		boolean isDamegeSourcePlayer = false;
		boolean playDamageSound = false;
		if ((entity instanceof EntityPlayer)) {
			EntityPlayer player = (EntityPlayer) entity;

			isCreative = player.capabilities.isCreativeMode;
			isSneaking = player.isSneaking();

			if (dmt.equalsIgnoreCase("player")) {

				if (isCreative) {
					isDamegeSourcePlayer = true;
				} else if ((getAcInfo() != null) && (!getAcInfo().creativeOnly)) {
					if (!MCH_Config.PreventingBroken.prmBool) {

						if (MCH_Config.BreakableOnlyPickaxe.prmBool) {

							if ((player.getCurrentEquippedItem() != null)
									&& ((player.getCurrentEquippedItem().getItem() instanceof ItemPickaxe))) {

								isDamegeSourcePlayer = true;
							}

						} else {
							isDamegeSourcePlayer = !isRidePlayer();
						}
					}
				}
			}
			W_WorldFunc.MOD_playSoundAtEntity(this, "hit", damage > 0.0F ? 1.0F : 0.5F, 1.0F);
		} else {
			playDamageSound = true;
		}

		if (!isDestroyed()) {
			if (!isDamegeSourcePlayer) {
				MCH_AircraftInfo acInfo = getAcInfo();
				if (acInfo != null) {
					if ((!dmt.equalsIgnoreCase("lava")) && (!dmt.equalsIgnoreCase("onFire"))) {
						if (damage > acInfo.armorMaxDamage) {
							damage = acInfo.armorMaxDamage;
						}

						if (damageFactor <= 1.0F) {
							damage *= damageFactor;
						}
						damage *= acInfo.armorDamageFactor;

						damage -= acInfo.armorMinDamage;

						if (damage <= 0.0F) {
							MCH_Lib.DbgLog(this.worldObj,
									"MCH_EntityAircraft.attackEntityFrom:no damage=%.1f -> %.1f(factor=%.2f):%s",
									new Object[] { Float.valueOf(org_damage), Float.valueOf(damage),
											Float.valueOf(damageFactor), dmt });
							return false;
						}

						if (damageFactor > 1.0F) {
							damage *= damageFactor;
						}
					}
				}
				MCH_Lib.DbgLog(this.worldObj, "MCH_EntityAircraft.attackEntityFrom:damage=%.1f(factor=%.2f):%s",
						new Object[] { Float.valueOf(damage), Float.valueOf(damageFactor), dmt });
				setDamageTaken(getDamageTaken() + (int) damage);
			}
			setBeenAttacked();

			if ((getDamageTaken() >= getMaxHP()) || (isDamegeSourcePlayer)) {
				if (!isDamegeSourcePlayer) {
					setDamageTaken(getMaxHP());
					destroyAircraft();
					this.timeSinceHit = 20;

					String cmd = getCommand().trim();
					if (cmd.startsWith("/"))
						cmd = cmd.substring(1);
					if (!cmd.isEmpty()) {
						MCH_DummyCommandSender.execCommand(cmd);
					}

					if (dmt.equalsIgnoreCase("inWall")) {
						explosionByCrash(0.0D);
						this.damageSinceDestroyed = getMaxHP();
					} else {
						MCH_Explosion.newExplosion(this.worldObj, null, entity, this.posX, this.posY, this.posZ, 2.0F,
								2.0F, true, true, true, true, 5);
					}

				} else {
					if ((getAcInfo() != null) && (getAcInfo().getItem() != null)) {
						if (isCreative) {
							if ((MCH_Config.DropItemInCreativeMode.prmBool) && (!isSneaking)) {
								dropItemWithOffset(getAcInfo().getItem(), 1, 0.0F);
							}
							if ((!MCH_Config.DropItemInCreativeMode.prmBool) && (isSneaking)) {
								dropItemWithOffset(getAcInfo().getItem(), 1, 0.0F);
							}
						} else {
							dropItemWithOffset(getAcInfo().getItem(), 1, 0.0F);
						}
					}
					setDead(true);
				}

			}

		} else if ((isDamegeSourcePlayer) && (isCreative)) {
			setDead(true);
		}

		if (playDamageSound) {
			W_WorldFunc.MOD_playSoundAtEntity(this, "helidmg", 1.0F, 0.9F + this.rand.nextFloat() * 0.1F);
		}

		return true;
	}

	public boolean isExploded() {
		return (isDestroyed()) && (this.damageSinceDestroyed > getMaxHP() / 10 + 1);
	}

	public void destruct() {
		if (getRiddenByEntity() != null) {
			getRiddenByEntity().mountEntity(null);
		}
		setDead(true);
	}

	public EntityItem entityDropItem(ItemStack is, float par2) {
		if (is.stackSize == 0) {
			return null;
		}

		setAcDataToItem(is);
		return super.entityDropItem(is, par2);
	}

	public void setAcDataToItem(ItemStack is) {
		if (!is.hasTagCompound()) {
			is.setTagCompound(new NBTTagCompound());
		}

		NBTTagCompound nbt = is.getTagCompound();

		nbt.setString("MCH_Command", getCommand());

		if (MCH_Config.ItemFuel.prmBool) {
			nbt.setInteger("MCH_Fuel", getFuel());
		}
		if (MCH_Config.ItemDamage.prmBool) {
			is.setItemDamage(getDamageTaken());
		}
	}

	public void getAcDataFromItem(ItemStack is) {
		if (!is.hasTagCompound()) {
			return;
		}
		NBTTagCompound nbt = is.getTagCompound();

		setCommandForce(nbt.getString("MCH_Command"));

		if (MCH_Config.ItemFuel.prmBool) {
			setFuel(nbt.getInteger("MCH_Fuel"));
		}
		if (MCH_Config.ItemDamage.prmBool) {
			setDamageTaken(is.getItemDamage());
		}
	}

	public boolean isUseableByPlayer(EntityPlayer player) {
		if (isUAV()) {
			return super.isUseableByPlayer(player);
		}
		if (!this.isDead) {
			if (getSeatIdByEntity(player) >= 0) {
				return player.getDistanceSqToEntity(this) <= 4096.0D;
			}

			return player.getDistanceSqToEntity(this) <= 64.0D;
		}

		return false;
	}

	public void applyEntityCollision(Entity par1Entity) {
	}

	public void addVelocity(double par1, double par3, double par5) {
	}

	public void setVelocity(double par1, double par3, double par5) {
		this.velocityX = (this.motionX = par1);
		this.velocityY = (this.motionY = par3);
		this.velocityZ = (this.motionZ = par5);
	}

	public void onFirstUpdate() {
		if (!this.worldObj.isRemote) {
			setCommonStatus(3, MCH_Config.InfinityAmmo.prmBool);
			setCommonStatus(4, MCH_Config.InfinityFuel.prmBool);
		}
	}

	public void onRidePilotFirstUpdate() {
		if (this.worldObj.isRemote) {
			if (W_Lib.isClientPlayer(getRiddenByEntity())) {
				updateClientSettings(0);
			}
		}

		Entity pilot = getRiddenByEntity();
		if (pilot != null) {
			pilot.rotationYaw = getLastRiderYaw();
			pilot.rotationPitch = getLastRiderPitch();
		}

		this.keepOnRideRotation = false;

		if (getAcInfo() != null) {
			switchFreeLookModeClient(getAcInfo().defaultFreelook);
		}
	}

	public double getCurrentThrottle() {
		return this.currentThrottle;
	}

	public void setCurrentThrottle(double throttle) {
		this.currentThrottle = throttle;
	}

	public void addCurrentThrottle(double throttle) {
		setCurrentThrottle(getCurrentThrottle() + throttle);
	}

	public double getPrevCurrentThrottle() {
		return this.prevCurrentThrottle;
	}

	public boolean canMouseRot() {
		return (!this.isDead) && (getRiddenByEntity() != null) && (!isDestroyed());
	}

	public boolean canUpdateYaw(Entity player) {
		if (getRidingEntity() != null)
			return false;
		if (getCountOnUpdate() < 30)
			return false;
		return MCH_Lib.getBlockIdY(this, 3, -2) == 0;
	}

	public boolean canUpdatePitch(Entity player) {
		if (getCountOnUpdate() < 30)
			return false;
		return MCH_Lib.getBlockIdY(this, 3, -2) == 0;
	}

	public boolean canUpdateRoll(Entity player) {
		if (getRidingEntity() != null)
			return false;
		if (getCountOnUpdate() < 30)
			return false;
		return MCH_Lib.getBlockIdY(this, 3, -2) == 0;
	}

	public boolean isOverridePlayerYaw() {
		return !isFreeLookMode();
	}

	public boolean isOverridePlayerPitch() {
		return !isFreeLookMode();
	}

	public double getAddRotationYawLimit() {
		return getAcInfo() != null ? 40.0D * getAcInfo().mobilityYaw : 40.0D;
	}

	public double getAddRotationPitchLimit() {
		return getAcInfo() != null ? 40.0D * getAcInfo().mobilityPitch : 40.0D;
	}

	public double getAddRotationRollLimit() {
		return getAcInfo() != null ? 40.0D * getAcInfo().mobilityRoll : 40.0D;
	}

	public float getYawFactor() {
		return 1.0F;
	}

	public float getPitchFactor() {
		return 1.0F;
	}

	public float getRollFactor() {
		return 1.0F;
	}

	public abstract void onUpdateAngles(float paramFloat);

	public float getControlRotYaw(float mouseX, float mouseY, float tick) {
		return 0.0F;
	}

	public float getControlRotPitch(float mouseX, float mouseY, float tick) {
		return 0.0F;
	}

	public float getControlRotRoll(float mouseX, float mouseY, float tick) {
		return 0.0F;
	}

	public void setAngles(Entity player, boolean fixRot, float fixYaw, float fixPitch, float deltaX, float deltaY,
			float x, float y, float partialTicks) {
		if (partialTicks < 0.03F)
			partialTicks = 0.4F;
		if (partialTicks > 0.9F)
			partialTicks = 0.6F;
		this.lowPassPartialTicks.put(partialTicks);
		partialTicks = this.lowPassPartialTicks.getAvg();
		
		float ac_pitch = getRotPitch();
		float ac_yaw = getRotYaw();
		float ac_roll = getRotRoll();

		if (isFreeLookMode()) {
			x = y = 0.0F;
		}

		float yaw = 0.0F;
		float pitch = 0.0F;
		float roll = 0.0F;

		if (canUpdateYaw(player)) {
			double limit = getAddRotationYawLimit();
			yaw = getControlRotYaw(x, y, partialTicks);
			if (yaw < -limit)
				yaw = (float) -limit;
			if (yaw > limit)
				yaw = (float) limit;
			yaw = (float) (yaw * getYawFactor() * 0.06D * partialTicks);
		}
		if (canUpdatePitch(player)) {
			double limit = getAddRotationPitchLimit();
			pitch = getControlRotPitch(x, y, partialTicks);
			if (pitch < -limit)
				pitch = (float) -limit;
			if (pitch > limit)
				pitch = (float) limit;
			pitch = (float) (-pitch * getPitchFactor() * 0.06D * partialTicks);
		}
		if (canUpdateRoll(player)) {
			double limit = getAddRotationRollLimit();
			roll = getControlRotRoll(x, y, partialTicks);
			if (roll < -limit)
				roll = (float) -limit;
			if (roll > limit)
				roll = (float) limit;
			roll = roll * getRollFactor() * 0.06F * partialTicks;
		}

		MCH_Math.FMatrix m_add = MCH_Math.newMatrix();

		MCH_Math.MatTurnZ(m_add, roll / 180.0F * 3.1415927F);
		MCH_Math.MatTurnX(m_add, pitch / 180.0F * 3.1415927F);
		MCH_Math.MatTurnY(m_add, yaw / 180.0F * 3.1415927F);

		MCH_Math.MatTurnZ(m_add, (float) (getRotRoll() / 180.0F * 3.141592653589793D));
		MCH_Math.MatTurnX(m_add, (float) (getRotPitch() / 180.0F * 3.141592653589793D));
		MCH_Math.MatTurnY(m_add, (float) (getRotYaw() / 180.0F * 3.141592653589793D));
		MCH_Math.FVector3D v = MCH_Math.MatrixToEuler(m_add);

		if (getAcInfo().limitRotation) {
			v.x = MCH_Lib.RNG(v.x, getAcInfo().minRotationPitch, getAcInfo().maxRotationPitch);
			v.z = MCH_Lib.RNG(v.z, getAcInfo().minRotationRoll, getAcInfo().maxRotationRoll);
		}

		if (v.z > 180.0F)
			v.z -= 360.0F;
		if (v.z < -180.0F) {
			v.z += 360.0F;
		}
		setRotYaw(v.y);
		setRotPitch(v.x);
		setRotRoll(v.z);

		onUpdateAngles(partialTicks);

		if (getAcInfo().limitRotation) {
			v.x = MCH_Lib.RNG(getRotPitch(), getAcInfo().minRotationPitch, getAcInfo().maxRotationPitch);
			v.z = MCH_Lib.RNG(getRotRoll(), getAcInfo().minRotationRoll, getAcInfo().maxRotationRoll);
			setRotPitch(v.x);
			setRotRoll(v.z);
		}

		float RV = 180.0F;
		if (MathHelper.abs(getRotPitch()) > 90.0F) {
			MCH_Lib.DbgLog(true, "MCH_EntityAircraft.setAngles Error:Pitch=%.1f",
					new Object[] { Float.valueOf(getRotPitch()) });
		}

		if (getRotRoll() > 180.0F) {
			setRotRoll(getRotRoll() - 360.0F);
		}
		if (getRotRoll() < -180.0F) {
			setRotRoll(getRotRoll() + 360.0F);
		}

		this.prevRotationRoll = getRotRoll();
		this.prevRotationPitch = getRotPitch();

		if (getRidingEntity() == null) {
			this.prevRotationYaw = getRotYaw();
		}

		if ((isOverridePlayerYaw()) || (fixRot)) {
			if (getRidingEntity() == null) {
				player.prevRotationYaw = (getRotYaw() + (fixRot ? fixYaw : 0.0F));
			} else {
				if (getRotYaw() - player.rotationYaw > 180.0F) {
					player.prevRotationYaw += 360.0F;
				}
				if (getRotYaw() - player.rotationYaw < -180.0F) {
					player.prevRotationYaw -= 360.0F;
				}
			}
			player.rotationYaw = (getRotYaw() + (fixRot ? fixYaw : 0.0F));
		} else {
			player.setAngles(deltaX, 0.0F);
		}

		if ((isOverridePlayerPitch()) || (fixRot)) {
			player.prevRotationPitch = (getRotPitch() + (fixRot ? fixPitch : 0.0F));
			player.rotationPitch = (getRotPitch() + (fixRot ? fixPitch : 0.0F));
		} else {
			player.setAngles(0.0F, deltaY);
		}

		if (((getRidingEntity() == null) && (ac_yaw != getRotYaw())) || (ac_pitch != getRotPitch())
				|| (ac_roll != getRotRoll())) {

			this.aircraftRotChanged = true;
		}
	}

	public boolean canSwitchSearchLight(Entity entity) {
		if (haveSearchLight()) {
			if (getSeatIdByEntity(entity) <= 1) {
				return true;
			}
		}
		return false;
	}

	public boolean isSearchLightON() {
		return getCommonStatus(6);
	}

	public void setSearchLight(boolean onoff) {
		setCommonStatus(6, onoff);
	}

	public boolean haveSearchLight() {
		return (getAcInfo() != null) && (getAcInfo().searchLights.size() > 0);
	}

	public float getSearchLightValue(Entity entity) {
		if ((haveSearchLight()) && (isSearchLightON())) {
			for (MCH_AircraftInfo.SearchLight sl : getAcInfo().searchLights) {
				Vec3 pos = getTransformedPosition(sl.pos);
				double dist = entity.getDistanceSq(pos.xCoord, pos.yCoord, pos.zCoord);
				if ((dist > 2.0D) && (dist < sl.height * sl.height + 20.0F)) {
					double cx = entity.posX - pos.xCoord;
					double cy = entity.posY - pos.yCoord;
					double cz = entity.posZ - pos.zCoord;
					double h = 0.0D;
					double v = 0.0D;

					if (!sl.fixDir) {
						Vec3 vv = MCH_Lib.RotVec3(0.0D, 0.0D, 1.0D, -this.lastSearchLightYaw + sl.yaw,
								-this.lastSearchLightPitch + sl.pitch, -getRotRoll());

						h = MCH_Lib.getPosAngle(vv.xCoord, vv.zCoord, cx, cz);
						v = Math.atan2(cy, Math.sqrt(cx * cx + cz * cz)) * 180.0D / 3.141592653589793D;
						v = Math.abs(v + this.lastSearchLightPitch + sl.pitch);
					} else {
						float stRot = 0.0F;
						if (sl.steering) {
							stRot = this.rotYawWheel * sl.stRot;
						}
						Vec3 vv = MCH_Lib.RotVec3(0.0D, 0.0D, 1.0D, -getRotYaw() + sl.yaw + stRot,
								-getRotPitch() + sl.pitch, -getRotRoll());

						h = MCH_Lib.getPosAngle(vv.xCoord, vv.zCoord, cx, cz);
						v = Math.atan2(cy, Math.sqrt(cx * cx + cz * cz)) * 180.0D / 3.141592653589793D;
						v = Math.abs(v + getRotPitch() + sl.pitch);
					}

					float angle = sl.angle * 3.0F;
					if ((h < angle) && (v < angle)) {
						float value = 0.0F;
						if (h + v < angle) {
							value = (float) (1440.0D * (1.0D - (h + v) / angle));
						}

						return value <= 240.0F ? value : 240.0F;
					}
				}
			}
		}

		return 0.0F;
	}

	public abstract void onUpdateAircraft();

	public void onUpdate() {
		if (getCountOnUpdate() < 2) {
			this.prevPosition.clear(Vec3.createVectorHelper(this.posX, this.posY, this.posZ));
		}

		this.prevCurrentThrottle = getCurrentThrottle();

		this.lastBBDamageFactor = 1.0F;

		updateControl();

		checkServerNoMove();

		onUpdate_RidingEntity();
		
		Iterator<UnmountReserve> itr = this.listUnmountReserve.iterator();
		while (itr.hasNext()) {
			UnmountReserve ur = (UnmountReserve) itr.next();
			if ((ur.entity != null) && (!ur.entity.isDead)) {
				ur.entity.setPosition(ur.posX, ur.posY, ur.posZ);
				ur.entity.fallDistance = this.fallDistance;
			}
			if (ur.cnt > 0) {
				ur.cnt -= 1;
			}
			if (ur.cnt == 0) {
				itr.remove();
			}
		}

		if ((isDestroyed()) && (getCountOnUpdate() % 20 == 0)) {
			for (int sid = 0; sid < getSeatNum() + 1; sid++) {
				Entity entity = getEntityBySeatId(sid);
				if (entity != null) {
					if ((sid != 0) || (!isUAV())) {

						if (MCH_Config.applyDamageVsEntity(entity, DamageSource.inFire, 1.0F) > 0.0F) {
							entity.setFire(5);
						}
					}
				}
			}
		}

		if ((this.aircraftRotChanged) || (this.aircraftRollRev)) {
			if ((this.worldObj.isRemote) && (getRiddenByEntity() != null)) {
				MCH_PacketIndRotation.send(this);
				this.aircraftRotChanged = false;
				this.aircraftRollRev = false;
			}
		}

		if (!this.worldObj.isRemote) {
			if ((int) this.prevRotationRoll != (int) getRotRoll()) {
				float roll = MathHelper.wrapAngleTo180_float(getRotRoll());
				getDataWatcher().updateObject(26, new Short((short) (int) roll));
			}
		}
		this.prevRotationRoll = getRotRoll();

		if (!this.worldObj.isRemote) {
			if ((isTargetDrone()) && (!isDestroyed())) {
				if ((getCountOnUpdate() > 20) && (!canUseFuel())) {
					setDamageTaken(getMaxHP());

					destroyAircraft();
					MCH_Explosion.newExplosion(this.worldObj, null, null, this.posX, this.posY, this.posZ, 2.0F, 2.0F,
							true, true, true, true, 5);
				}
			}
		}

		if ((this.worldObj.isRemote) && (getAcInfo() != null)) {
			if ((getHP() <= 0) && (getDespawnCount() <= 0)) {
				destroyAircraft();
			}
		}
		if ((!this.worldObj.isRemote) && (getDespawnCount() > 0)) {
			setDespawnCount(getDespawnCount() - 1);
			if (getDespawnCount() <= 1) {
				setDead(true);
			}
		}

		super.onUpdate();

		if (getParts() != null) {
			for (Entity e : getParts()) {
				if (e != null) {
					e.onUpdate();
				}
			}
		}
		updateNoCollisionEntities();

		updateUAV();
		supplyFuel();
		supplyAmmoToOtherAircraft();
		updateFuel();
		repairOtherAircraft();

		if (this.modeSwitchCooldown > 0) {
			this.modeSwitchCooldown -= 1;
		}
		if ((this.lastRiddenByEntity == null) && (getRiddenByEntity() != null)) {
			onRidePilotFirstUpdate();
		}

		if (this.countOnUpdate == 0) {
			onFirstUpdate();
		}
		this.countOnUpdate += 1;
		if (this.countOnUpdate >= 1000000) {
			this.countOnUpdate = 1;
		}

		if (this.worldObj.isRemote) {
			this.commonStatus = getDataWatcher().getWatchableObjectInt(23);
		}

		this.fallDistance = 0.0F;
		if (this.riddenByEntity != null) {
			this.riddenByEntity.fallDistance = 0.0F;
		}

		if (this.missileDetector != null) {
			this.missileDetector.update();
		}

		if (this.soundUpdater != null) {
			this.soundUpdater.update();
		}
		if ((getTowChainEntity() != null) && (getTowChainEntity().isDead)) {
			setTowChainEntity(null);
		}

		updateSupplyAmmo();

		autoRepair();

		int ft = getFlareTick();
		this.flareDv.update();
		if ((!this.worldObj.isRemote) && (getFlareTick() == 0) && (ft != 0)) {
			setCommonStatus(0, false);
		}

		Entity e = getRiddenByEntity();

		if ((e != null) && (!e.isDead) && (!isDestroyed())) {
			this.lastRiderYaw = e.rotationYaw;
			this.prevLastRiderYaw = e.prevRotationYaw;
			this.lastRiderPitch = e.rotationPitch;
			this.prevLastRiderPitch = e.prevRotationPitch;
		} else if ((getTowedChainEntity() != null) || (this.ridingEntity != null)) {
			this.lastRiderYaw = this.rotationYaw;
			this.prevLastRiderYaw = this.prevRotationYaw;
			this.lastRiderPitch = this.rotationPitch;
			this.prevLastRiderPitch = this.prevRotationPitch;
		}

		updatePartCameraRotate();

		updatePartWheel();
		updatePartCrawlerTrack();

		updatePartLightHatch();
		regenerationMob();

		if ((getRiddenByEntity() == null) && (this.lastRiddenByEntity != null)) {
			unmountEntity();
		}

		updateExtraBoundingBox();

		boolean prevOnGround = this.onGround;
		double prevMotionY = this.motionY;

		onUpdateAircraft();

		if (getAcInfo() != null) {
			updateParts(getPartStatus());
		}

		if (this.recoilCount > 0) {
			this.recoilCount -= 1;
		}
		if (!W_Entity.isEqual(MCH_MOD.proxy.getClientPlayer(), getRiddenByEntity())) {
			updateRecoil(1.0F);
		}

		if ((!this.worldObj.isRemote) && (isDestroyed()) && (!isExploded())) {
			if ((!prevOnGround) && (this.onGround) && (prevMotionY < -0.2D)) {
				explosionByCrash(prevMotionY);
				this.damageSinceDestroyed = getMaxHP();
			}
		}

		onUpdate_PartRotation();

		onUpdate_ParticleSmoke();

		updateSeatsPosition(this.posX, this.posY, this.posZ, false);
		updateHitBoxPosition();

		onUpdate_CollisionGroundDamage();

		onUpdate_UnmountCrew();

		onUpdate_Repelling();
		checkRideRack();

		if ((this.lastRidingEntity == null) && (getRidingEntity() != null)) {
			onRideEntity(getRidingEntity());
		}

		this.lastRiddenByEntity = getRiddenByEntity();
		this.lastRidingEntity = getRidingEntity();

		this.prevPosition.put(Vec3.createVectorHelper(this.posX, this.posY, this.posZ));
	}

	private void updateNoCollisionEntities() {
		if (this.worldObj.isRemote) {
			return;
		}
		if (getCountOnUpdate() % 10 != 0) {
			return;
		}

		for (int i = 0; i < 1 + getSeatNum(); i++) {
			Entity e = getEntityBySeatId(i);
			if (e != null) {
				this.noCollisionEntities.put(e, Integer.valueOf(8));
			}
		}

		if ((getTowChainEntity() != null) && (getTowChainEntity().towedEntity != null)) {
			this.noCollisionEntities.put(getTowChainEntity().towedEntity, Integer.valueOf(60));
		}

		if ((getTowedChainEntity() != null) && (getTowedChainEntity().towEntity != null)) {
			this.noCollisionEntities.put(getTowedChainEntity().towEntity, Integer.valueOf(60));
		}

		if ((this.ridingEntity instanceof MCH_EntitySeat)) {
			MCH_EntityAircraft ac = ((MCH_EntitySeat) this.ridingEntity).getParent();
			if (ac != null) {
				this.noCollisionEntities.put(ac, Integer.valueOf(60));
			}
		} else if (this.ridingEntity != null) {
			this.noCollisionEntities.put(this.ridingEntity, Integer.valueOf(60));
		}

		for (Entity key : this.noCollisionEntities.keySet()) {
			this.noCollisionEntities.put(key,
					Integer.valueOf(((Integer) this.noCollisionEntities.get(key)).intValue() - 1));
		}

		for (Iterator<Integer> key = this.noCollisionEntities.values().iterator(); key.hasNext();) {
			if (((Integer) key.next()).intValue() <= 0) {
				key.remove();
			}
		}
	}

	public void updateControl() {
		if (!this.worldObj.isRemote) {
			setCommonStatus(7, this.moveLeft);
			setCommonStatus(8, this.moveRight);
			setCommonStatus(9, this.throttleUp);
			setCommonStatus(10, this.throttleDown);
		} else if (MCH_MOD.proxy.getClientPlayer() != getRiddenByEntity()) {
			this.moveLeft = getCommonStatus(7);
			this.moveRight = getCommonStatus(8);
			this.throttleUp = getCommonStatus(9);
			this.throttleDown = getCommonStatus(10);
		}
	}

	public void updateRecoil(float partialTicks) {
		if ((this.recoilCount > 0) && (this.recoilCount >= 12)) {
			float pitch = MathHelper.cos((float) ((this.recoilYaw - getRotRoll()) * 3.141592653589793D / 180.0D));
			float roll = MathHelper.sin((float) ((this.recoilYaw - getRotRoll()) * 3.141592653589793D / 180.0D));
			float recoil = MathHelper.cos((float) (this.recoilCount * 6 * 3.141592653589793D / 180.0D))
					* this.recoilValue;

			setRotPitch(getRotPitch() + recoil * pitch * partialTicks);
			setRotRoll(getRotRoll() + recoil * roll * partialTicks);
		}
	}

	private void updatePartLightHatch() {
		this.prevRotLightHatch = this.rotLightHatch;
		if (isSearchLightON()) {
			this.rotLightHatch = ((float) (this.rotLightHatch + 0.5D));
		} else {
			this.rotLightHatch = ((float) (this.rotLightHatch - 0.5D));
		}
		if (this.rotLightHatch > 1.0F)
			this.rotLightHatch = 1.0F;
		if (this.rotLightHatch < 0.0F)
			this.rotLightHatch = 0.0F;
	}

	public void updateExtraBoundingBox() {
		for (MCH_BoundingBox bb : this.extraBoundingBox) {
			bb.updatePosition(this.posX, this.posY, this.posZ, getRotYaw(), getRotPitch(), getRotRoll());
		}
	}

	public void updatePartWheel() {
		if (!this.worldObj.isRemote)
			return;
		if (getAcInfo() == null) {
			return;
		}
		this.prevRotWheel = this.rotWheel;
		this.prevRotYawWheel = this.rotYawWheel;

		float LEN = 1.0F;
		float MIN = 0.0F;
		double throttle = getCurrentThrottle();
		double pivotTurnThrottle = getAcInfo().pivotTurnThrottle;
		if (pivotTurnThrottle <= 0.0D)
			pivotTurnThrottle = 1.0D;
		else {
			pivotTurnThrottle *= 0.10000000149011612D;
		}
		boolean localMoveLeft = this.moveLeft;
		boolean localMoveRight = this.moveRight;
		if ((getAcInfo().enableBack) && (this.throttleBack > 0.01D) && (throttle <= 0.0D)) {
			throttle = -this.throttleBack * 15.0F;
		}

		if ((localMoveLeft) && (!localMoveRight)) {
			this.rotYawWheel += 0.1F;
			if (this.rotYawWheel > 1.0F) {
				this.rotYawWheel = 1.0F;
			}
		} else if ((!localMoveLeft) && (localMoveRight)) {
			this.rotYawWheel -= 0.1F;
			if (this.rotYawWheel < -1.0F) {
				this.rotYawWheel = -1.0F;
			}
		} else {
			this.rotYawWheel *= 0.9F;
		}

		this.rotWheel = ((float) (this.rotWheel + throttle * getAcInfo().partWheelRot));
		if (this.rotWheel >= 360.0F) {
			this.rotWheel -= 360.0F;
			this.prevRotWheel -= 360.0F;
		} else if (this.rotWheel < 0.0F) {
			this.rotWheel += 360.0F;
			this.prevRotWheel += 360.0F;
		}
	}

	public void updatePartCrawlerTrack() {
		if (!this.worldObj.isRemote)
			return;
		if (getAcInfo() == null) {
			return;
		}
		this.prevRotTrackRoller[0] = this.rotTrackRoller[0];
		this.prevRotTrackRoller[1] = this.rotTrackRoller[1];

		this.prevRotCrawlerTrack[0] = this.rotCrawlerTrack[0];
		this.prevRotCrawlerTrack[1] = this.rotCrawlerTrack[1];

		float LEN = 1.0F;
		float MIN = 0.0F;
		double throttle = getCurrentThrottle();
		double pivotTurnThrottle = getAcInfo().pivotTurnThrottle;
		if (pivotTurnThrottle <= 0.0D)
			pivotTurnThrottle = 1.0D;
		else {
			pivotTurnThrottle *= 0.10000000149011612D;
		}
		boolean localMoveLeft = this.moveLeft;
		boolean localMoveRight = this.moveRight;
		int dir = 1;
		if ((getAcInfo().enableBack) && (this.throttleBack > 0.0F) && (throttle <= 0.0D)) {
			throttle = -this.throttleBack * 5.0F;
			if (localMoveLeft != localMoveRight) {
				boolean tmp = localMoveLeft;
				localMoveLeft = localMoveRight;
				localMoveRight = tmp;
				dir = -1;
			}
		}

		if ((localMoveLeft) && (!localMoveRight)) {
			throttle = 0.2D * dir;
			int tmp203_202 = 0;
			float[] tmp203_199 = this.throttleCrawlerTrack;
			tmp203_199[tmp203_202] = ((float) (tmp203_199[tmp203_202] + throttle));
			int tmp215_214 = 1;
			float[] tmp215_211 = this.throttleCrawlerTrack;
			tmp215_211[tmp215_214] = ((float) (tmp215_211[tmp215_214] - pivotTurnThrottle * throttle));
		} else if ((!localMoveLeft) && (localMoveRight)) {
			throttle = 0.2D * dir;
			int tmp251_250 = 0;
			float[] tmp251_247 = this.throttleCrawlerTrack;
			tmp251_247[tmp251_250] = ((float) (tmp251_247[tmp251_250] - pivotTurnThrottle * throttle));
			int tmp266_265 = 1;
			float[] tmp266_262 = this.throttleCrawlerTrack;
			tmp266_262[tmp266_265] = ((float) (tmp266_262[tmp266_265] + throttle));
		} else {
			if (throttle > 0.2D)
				throttle = 0.2D;
			if (throttle < -0.2D)
				throttle = -0.2D;
			int tmp305_304 = 0;
			float[] tmp305_301 = this.throttleCrawlerTrack;
			tmp305_301[tmp305_304] = ((float) (tmp305_301[tmp305_304] + throttle));
			int tmp317_316 = 1;
			float[] tmp317_313 = this.throttleCrawlerTrack;
			tmp317_313[tmp317_316] = ((float) (tmp317_313[tmp317_316] + throttle));
		}

		for (int i = 0; i < 2; i++) {
			if (this.throttleCrawlerTrack[i] < -0.72F) {
				this.throttleCrawlerTrack[i] = -0.72F;
			} else if (this.throttleCrawlerTrack[i] > 0.72F) {
				this.throttleCrawlerTrack[i] = 0.72F;
			}

			this.rotTrackRoller[i] += this.throttleCrawlerTrack[i] * getAcInfo().trackRollerRot;
			if (this.rotTrackRoller[i] >= 360.0F) {
				this.rotTrackRoller[i] -= 360.0F;
				this.prevRotTrackRoller[i] -= 360.0F;
			} else if (this.rotTrackRoller[i] < 0.0F) {
				this.rotTrackRoller[i] += 360.0F;
				this.prevRotTrackRoller[i] += 360.0F;
			}

			this.rotCrawlerTrack[i] -= this.throttleCrawlerTrack[i];
			while (this.rotCrawlerTrack[i] >= 1.0F) {
				this.rotCrawlerTrack[i] -= 1.0F;
				this.prevRotCrawlerTrack[i] -= 1.0F;
			}
			while (this.rotCrawlerTrack[i] < 0.0F)
				this.rotCrawlerTrack[i] += 1.0F;
			while (this.prevRotCrawlerTrack[i] < 0.0F) {
				this.prevRotCrawlerTrack[i] += 1.0F;
			}
			int tmp602_600 = i;
			float[] tmp602_597 = this.throttleCrawlerTrack;
			tmp602_597[tmp602_600] = ((float) (tmp602_597[tmp602_600] * 0.75D));
		}
	}

	public void checkServerNoMove() {
		if (!this.worldObj.isRemote) {
			double moti = this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ;
			if (moti < 1.0E-4D) {
				if (this.serverNoMoveCount < 20) {
					this.serverNoMoveCount += 1;
					if (this.serverNoMoveCount >= 20) {
						this.serverNoMoveCount = 0;
						if ((this.worldObj instanceof WorldServer)) {
							((WorldServer) this.worldObj).getEntityTracker().func_151247_a(this,
									new S12PacketEntityVelocity(getEntityId(), 0.0D, 0.0D, 0.0D));
						}

					}
				}
			} else {
				this.serverNoMoveCount = 0;
			}
		}
	}

	public boolean haveRotPart() {
		if ((this.worldObj.isRemote) && (getAcInfo() != null)) {
			if ((this.rotPartRotation.length > 0) && (this.rotPartRotation.length == getAcInfo().partRotPart.size())) {

				return true;
			}
		}
		return false;
	}

	public void onUpdate_PartRotation() {
		if (haveRotPart()) {
			for (int i = 0; i < this.rotPartRotation.length; i++) {
				this.prevRotPartRotation[i] = this.rotPartRotation[i];
				if (((!isDestroyed()) && (((MCH_AircraftInfo.RotPart) getAcInfo().partRotPart.get(i)).rotAlways))
						|| (getRiddenByEntity() != null)) {
					this.rotPartRotation[i] += ((MCH_AircraftInfo.RotPart) getAcInfo().partRotPart.get(i)).rotSpeed;
					if (this.rotPartRotation[i] < 0.0F)
						this.rotPartRotation[i] += 360.0F;
					if (this.rotPartRotation[i] >= 360.0F) {
						this.rotPartRotation[i] -= 360.0F;
					}
				}
			}
		}
	}

	public void onRideEntity(Entity ridingEntity) {
	}

	public int getAlt(double px, double py, double pz) {
		int i;
		for (i = 0; i < 256; i++) {
			if ((py - i <= 0.0D) || ((py - i < 256.0D)
					&& (0 != W_WorldFunc.getBlockId(this.worldObj, (int) px, (int) py - i, (int) pz)))) {
				break;
			}
		}

		return i;
	}

	public boolean canRepelling(Entity entity) {
		if (isRepelling()) {
			if (this.tickRepelling > 50) {
				return true;
			}
		}
		return false;
	}

	private void onUpdate_Repelling() {
		if ((getAcInfo() != null) && (getAcInfo().haveRepellingHook())) {
			if (isRepelling()) {
				int alt = getAlt(this.posX, this.posY, this.posZ);

				if ((this.ropesLength > -50.0F) && (this.ropesLength > -alt)) {
					this.ropesLength = ((float) (this.ropesLength
							- (this.worldObj.isRemote ? 0.30000001192092896D : 0.25D)));
				}
			} else {
				this.ropesLength = 0.0F;
			}
		}

		onUpdate_UnmountCrewRepelling();
	}

	private void onUpdate_UnmountCrewRepelling() {
		if (getAcInfo() == null) {
			return;
		}

		if (!isRepelling()) {
			this.tickRepelling = 0;
			return;
		}

		if (this.tickRepelling < 60) {
			this.tickRepelling += 1;
			return;
		}

		if (this.worldObj.isRemote) {
			return;
		}
		for (int ropeIdx = 0; ropeIdx < getAcInfo().repellingHooks.size(); ropeIdx++) {
			MCH_AircraftInfo.RepellingHook hook = (MCH_AircraftInfo.RepellingHook) getAcInfo().repellingHooks
					.get(ropeIdx);
			if (getCountOnUpdate() % hook.interval == 0) {

				for (int i = 1; i < getSeatNum(); i++) {
					MCH_EntitySeat seat = getSeat(i);
					if ((seat != null) && (seat.riddenByEntity != null)
							&& (!W_EntityPlayer.isPlayer(seat.riddenByEntity))
							&& (!(seat.riddenByEntity instanceof MCH_EntityGunner))) {
						if (!(getSeatInfo(i + 1) instanceof MCH_SeatRackInfo)) {

							Entity entity = seat.riddenByEntity;
							Vec3 dropPos = getTransformedPosition(hook.pos, (Vec3) this.prevPosition.oldest());
							seat.posX = dropPos.xCoord;
							seat.posY = (dropPos.yCoord - 2.0D);
							seat.posZ = dropPos.zCoord;
							entity.mountEntity(null);

							unmountEntityRepelling(entity, dropPos, ropeIdx);

							this.lastUsedRopeIndex = ropeIdx;
							break;
						}
					}
				}
			}
		}
	}

	public void unmountEntityRepelling(Entity entity, Vec3 dropPos, int ropeIdx) {
		entity.posX = dropPos.xCoord;
		entity.posY = (dropPos.yCoord - 2.0D);
		entity.posZ = dropPos.zCoord;

		MCH_EntityHide hideEntity = new MCH_EntityHide(this.worldObj, entity.posX, entity.posY, entity.posZ);

		hideEntity.setParent(this, entity, ropeIdx);
		hideEntity.motionX = (entity.motionX = 0.0D);
		hideEntity.motionY = (entity.motionY = 0.0D);
		hideEntity.motionZ = (entity.motionZ = 0.0D);
		hideEntity.fallDistance = (entity.fallDistance = 0.0F);

		this.worldObj.spawnEntityInWorld(hideEntity);
	}

	private void onUpdate_UnmountCrew() {
		if (getAcInfo() == null) {
			return;
		}

		if (this.isParachuting) {
			if (MCH_Lib.getBlockIdY(this, 3, -10) != 0) {
				stopUnmountCrew();
			} else if ((!haveHatch()) || (getHatchRotation() > 89.0F)) {
				if (getCountOnUpdate() % getAcInfo().mobDropOption.interval == 0) {
					if (!unmountCrew(true)) {

						stopUnmountCrew();
					}
				}
			}
		}
	}

	public void unmountAircraft() {
		Vec3 v = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
		if ((this.ridingEntity instanceof MCH_EntitySeat)) {
			MCH_EntityAircraft ac = ((MCH_EntitySeat) this.ridingEntity).getParent();
			MCH_SeatInfo seatInfo = ac.getSeatInfo(this);
			if ((seatInfo instanceof MCH_SeatRackInfo)) {
				v = ((MCH_SeatRackInfo) seatInfo).getEntryPos();
				v = ac.getTransformedPosition(v);
			}
		} else if ((this.ridingEntity instanceof EntityMinecartEmpty)) {
			this.dismountedUserCtrl = true;
		}
		setLocationAndAngles(v.xCoord, v.yCoord, v.zCoord, getRotYaw(), getRotPitch());
		mountEntity(null);
		setLocationAndAngles(v.xCoord, v.yCoord, v.zCoord, getRotYaw(), getRotPitch());
	}

	public boolean canUnmount(Entity entity) {
		if (getAcInfo() == null)
			return false;
		if (!getAcInfo().isEnableParachuting)
			return false;
		if (getSeatIdByEntity(entity) <= 1) {
			return false;
		}
		if ((haveHatch()) && (getHatchRotation() < 89.0F)) {
			return false;
		}
		return true;
	}

	public void unmount(Entity entity) {
		if (getAcInfo() == null) {
			return;
		}
		if ((canRepelling(entity)) && (getAcInfo().haveRepellingHook())) {
			MCH_EntitySeat seat = getSeatByEntity(entity);
			if (seat != null) {
				this.lastUsedRopeIndex = ((this.lastUsedRopeIndex + 1) % getAcInfo().repellingHooks.size());

				Vec3 dropPos = getTransformedPosition(
						((MCH_AircraftInfo.RepellingHook) getAcInfo().repellingHooks.get(this.lastUsedRopeIndex)).pos,
						(Vec3) this.prevPosition.oldest());

				dropPos = dropPos.addVector(0.0D, -2.0D, 0.0D);
				seat.posX = dropPos.xCoord;
				seat.posY = dropPos.yCoord;
				seat.posZ = dropPos.zCoord;
				entity.mountEntity(null);
				entity.posX = dropPos.xCoord;
				entity.posY = dropPos.yCoord;
				entity.posZ = dropPos.zCoord;
				unmountEntityRepelling(entity, dropPos, this.lastUsedRopeIndex);
			} else {
				MCH_Lib.Log(this, "Error:MCH_EntityAircraft.unmount seat=null : " + entity, new Object[0]);
			}
		} else if (canUnmount(entity)) {
			MCH_EntitySeat seat = getSeatByEntity(entity);

			if (seat != null) {
				Vec3 dropPos = getTransformedPosition(getAcInfo().mobDropOption.pos, (Vec3) this.prevPosition.oldest());

				seat.posX = dropPos.xCoord;
				seat.posY = dropPos.yCoord;
				seat.posZ = dropPos.zCoord;
				entity.mountEntity(null);
				entity.posX = dropPos.xCoord;
				entity.posY = dropPos.yCoord;
				entity.posZ = dropPos.zCoord;
				dropEntityParachute(entity);
			} else {
				MCH_Lib.Log(this, "Error:MCH_EntityAircraft.unmount seat=null : " + entity, new Object[0]);
			}
		}
	}

	public boolean canParachuting(Entity entity) {
		if ((getAcInfo() != null) && (getAcInfo().isEnableParachuting)) {
			if (getSeatIdByEntity(entity) > 1) {
				if (MCH_Lib.getBlockIdY(this, 3, -13) == 0) {
					if ((haveHatch()) && (getHatchRotation() > 89.0F)) {
						return getSeatIdByEntity(entity) > 1;
					}

					return getSeatIdByEntity(entity) > 1;
				}
			}
		}

		return false;
	}

	public void onUpdate_RidingEntity() {
		if ((!this.worldObj.isRemote) && (this.waitMountEntity == 0) && (getCountOnUpdate() > 20)) {
			if (canMountWithNearEmptyMinecart()) {
				mountWithNearEmptyMinecart();
			}
		}

		if (this.waitMountEntity > 0) {
			this.waitMountEntity -= 1;
		}
		if ((!this.worldObj.isRemote) && (getRidingEntity() != null)) {
			setRotRoll(getRotRoll() * 0.9F);
			setRotPitch(getRotPitch() * 0.95F);

			Entity re = getRidingEntity();
			float target = MathHelper.wrapAngleTo180_float(re.rotationYaw + 90.0F);
			if (target - this.rotationYaw > 180.0F)
				target -= 360.0F;
			if (target - this.rotationYaw < -180.0F)
				target += 360.0F;
			if (this.ticksExisted % 2 == 0) {
			}

			float dist = 50.0F * (float) re.getDistanceSq(re.prevPosX, re.prevPosY, re.prevPosZ);
			if (dist > 0.001D) {
				dist = MathHelper.sqrt_double(dist);
				float distYaw = MCH_Lib.RNG(target - this.rotationYaw, -dist, dist);
				this.rotationYaw += distYaw;
			}

			double bkPosX = this.posX;
			double bkPosY = this.posY;
			double bkPosZ = this.posZ;
			if (getRidingEntity().isDead) {
				mountEntity(null);
				this.waitMountEntity = 20;
			} else if (getCurrentThrottle() > 0.8D) {
				this.motionX = getRidingEntity().motionX;
				this.motionY = getRidingEntity().motionY;
				this.motionZ = getRidingEntity().motionZ;
				mountEntity(null);
				this.waitMountEntity = 20;
			}
			this.posX = bkPosX;
			this.posY = bkPosY;
			this.posZ = bkPosZ;
		}
	}

	public void explosionByCrash(double prevMotionY) {
		float exp = getAcInfo() != null ? getAcInfo().maxFuel / 400.0F : 2.0F;
		if (exp < 1.0F)
			exp = 1.0F;
		if (exp > 15.0F)
			exp = 15.0F;
		MCH_Lib.DbgLog(this.worldObj, "OnGroundAfterDestroyed:motionY=%.3f",
				new Object[] { Float.valueOf((float) prevMotionY) });
		MCH_Explosion.newExplosion(this.worldObj, null, null, this.posX, this.posY, this.posZ, exp,
				exp >= 2.0F ? exp * 0.5F : 1.0F, true, true, true, true, 5);
	}

	public void onUpdate_CollisionGroundDamage() {
		if (isDestroyed()) {
			return;
		}

		if (MCH_Lib.getBlockIdY(this, 3, -3) > 0) {
			if (!this.worldObj.isRemote) {
				float roll = MathHelper.abs(MathHelper.wrapAngleTo180_float(getRotRoll()));
				float pitch = MathHelper.abs(MathHelper.wrapAngleTo180_float(getRotPitch()));
				if ((roll > getGiveDamageRot()) || (pitch > getGiveDamageRot())) {
					float dmg = MathHelper.abs(roll) + MathHelper.abs(pitch);
					if (dmg < 90.0F) {
						dmg *= 0.4F * (float) getDistance(this.prevPosX, this.prevPosY, this.prevPosZ);
					} else {
						dmg *= 0.4F;
					}
					if ((dmg > 1.0F) && (this.rand.nextInt(4) == 0)) {
						attackEntityFrom(DamageSource.inWall, dmg);
					}
				}
			}
		}

		if (getCountOnUpdate() % 30 == 0) {
			if ((getAcInfo() == null) || (!getAcInfo().isFloat)) {
				if (MCH_Lib.isBlockInWater(this.worldObj, (int) (this.posX + 0.5D),
						(int) (this.posY + 1.5D + getAcInfo().submergedDamageHeight), (int) (this.posZ + 0.5D))) {

					int hp = getMaxHP() / 10;
					if (hp <= 0)
						hp = 1;
					attackEntityFrom(DamageSource.inWall, hp);
				}
			}
		}
	}

	public float getGiveDamageRot() {
		return 40.0F;
	}

	public void applyServerPositionAndRotation() {
		double rpinc = this.aircraftPosRotInc;
		double yaw = MathHelper.wrapAngleTo180_double(this.aircraftYaw - getRotYaw());
		double roll = MathHelper.wrapAngleTo180_double(getServerRoll() - getRotRoll());
		if ((!isDestroyed()) && ((!W_Lib.isClientPlayer(getRiddenByEntity())) || (getRidingEntity() != null))) {
			setRotYaw((float) (getRotYaw() + yaw / rpinc));
			setRotPitch((float) (getRotPitch() + (this.aircraftPitch - getRotPitch()) / rpinc));
			setRotRoll((float) (getRotRoll() + roll / rpinc));
		}
		setPosition(this.posX + (this.aircraftX - this.posX) / rpinc, this.posY + (this.aircraftY - this.posY) / rpinc,
				this.posZ + (this.aircraftZ - this.posZ) / rpinc);

		setRotation(getRotYaw(), getRotPitch());
		this.aircraftPosRotInc -= 1;
	}

	protected void autoRepair() {
		if (this.timeSinceHit > 0) {
			this.timeSinceHit -= 1;
		}
		if (getMaxHP() <= 0) {
			return;
		}
		if (!isDestroyed()) {

			if (getDamageTaken() > this.beforeDamageTaken) {
				this.repairCount = 600;

			} else if (this.repairCount > 0) {
				this.repairCount -= 1;
			} else {
				this.repairCount = 40;

				double hpp = getHP() / getMaxHP();
				if (hpp >= MCH_Config.AutoRepairHP.prmDouble) {
					repair(getMaxHP() / 100);
				}
			}
		}

		this.beforeDamageTaken = getDamageTaken();
	}

	public boolean repair(int tpd) {
		if (tpd < 1)
			tpd = 1;
		int damage = getDamageTaken();

		if (damage > 0) {
			if (!this.worldObj.isRemote) {
				setDamageTaken(damage - tpd);
			}
			return true;
		}

		return false;
	}

	public void repairOtherAircraft() {
		float range = getAcInfo() != null ? getAcInfo().repairOtherVehiclesRange : 0.0F;
		if (range <= 0.0F) {
			return;
		}
		if ((!this.worldObj.isRemote) && (getCountOnUpdate() % 20 == 0)) {
			List list = this.worldObj.getEntitiesWithinAABB(MCH_EntityAircraft.class,
					getBoundingBox().expand(range, range, range));

			for (int i = 0; i < list.size(); i++) {
				MCH_EntityAircraft ac = (MCH_EntityAircraft) list.get(i);

				if (!W_Entity.isEqual(this, ac)) {
					if (ac.getHP() < ac.getMaxHP()) {
						ac.setDamageTaken(ac.getDamageTaken() - getAcInfo().repairOtherVehiclesValue);
					}
				}
			}
		}
	}

	protected void regenerationMob() {
		if (isDestroyed()) {
			return;
		}
		if (this.worldObj.isRemote) {
			return;
		}
		if ((getAcInfo() != null) && (getAcInfo().regeneration) && (getRiddenByEntity() != null)) {
			MCH_EntitySeat[] st = getSeats();
			for (MCH_EntitySeat s : st) {
				if ((s != null) && (!s.isDead)) {
					Entity e = s.riddenByEntity;
					if ((W_Lib.isEntityLivingBase(e)) && (!e.isDead)) {
						PotionEffect pe = W_Entity.getActivePotionEffect(e, Potion.regeneration);
						if ((pe == null) || ((pe != null) && (pe.getDuration() < 500))) {
							W_Entity.addPotionEffect(e, new PotionEffect(Potion.regeneration.id, 250, 0, true));
						}
					}
				}
			}
		}
	}

	public double getWaterDepth() {
		byte b0 = 5;
		double d0 = 0.0D;

		for (int i = 0; i < b0; i++) {
			double d1 = this.boundingBox.minY + (this.boundingBox.maxY - this.boundingBox.minY) * (i + 0) / b0 - 0.125D;
			double d2 = this.boundingBox.minY + (this.boundingBox.maxY - this.boundingBox.minY) * (i + 1) / b0 - 0.125D;

			d1 += getAcInfo().floatOffset;
			d2 += getAcInfo().floatOffset;

			AxisAlignedBB axisalignedbb = W_AxisAlignedBB.getAABB(this.boundingBox.minX, d1, this.boundingBox.minZ,
					this.boundingBox.maxX, d2, this.boundingBox.maxZ);

			if (this.worldObj.isAABBInMaterial(axisalignedbb, Material.water)) {
				d0 += 1.0D / b0;
			}
		}
		return d0;
	}

	public int getCountOnUpdate() {
		return this.countOnUpdate;
	}

	public boolean canSupply() {
		if (!canFloatWater()) {
			return (MCH_Lib.getBlockIdY(this, 1, -3) != 0) && (!isInWater());
		}

		return MCH_Lib.getBlockIdY(this, 1, -3) != 0;
	}

	public void setFuel(int fuel) {
		if (!this.worldObj.isRemote) {
			if (fuel < 0)
				fuel = 0;
			if (fuel > getMaxFuel()) {
				fuel = getMaxFuel();
			}
			if (fuel != getFuel()) {
				getDataWatcher().updateObject(25, Integer.valueOf(fuel));
			}
		}
	}

	public int getFuel() {
		return getDataWatcher().getWatchableObjectInt(25);
	}

	public float getFuelP() {
		int m = getMaxFuel();
		if (m == 0) {
			return 0.0F;
		}

		return getFuel() / m;
	}

	public boolean canUseFuel(boolean checkOtherSeet) {
		return (getMaxFuel() <= 0) || (getFuel() > 1) || (isInfinityFuel(getRiddenByEntity(), checkOtherSeet));
	}

	public boolean canUseFuel() {
		return canUseFuel(false);
	}

	public int getMaxFuel() {
		return getAcInfo() != null ? getAcInfo().maxFuel : 0;
	}

	public void supplyFuel() {
		float range = getAcInfo() != null ? getAcInfo().fuelSupplyRange : 0.0F;
		if (range <= 0.0F) {
			return;
		}
		if ((!this.worldObj.isRemote) && (getCountOnUpdate() % 10 == 0)) {
			List list = this.worldObj.getEntitiesWithinAABB(MCH_EntityAircraft.class,
					getBoundingBox().expand(range, range, range));

			for (int i = 0; i < list.size(); i++) {
				MCH_EntityAircraft ac = (MCH_EntityAircraft) list.get(i);

				if (!W_Entity.isEqual(this, ac)) {

					if (((!this.onGround) || (ac.canSupply())) && (ac.getFuel() < ac.getMaxFuel())) {
						int fc = ac.getMaxFuel() - ac.getFuel();
						if (fc > 30)
							fc = 30;
						ac.setFuel(ac.getFuel() + fc);
					}

					ac.fuelSuppliedCount = 40;
				}
			}
		}
	}

	public void updateFuel() {
		if (getMaxFuel() == 0) {
			return;
		}
		if (this.fuelSuppliedCount > 0) {
			this.fuelSuppliedCount -= 1;
		}
		if ((!isDestroyed()) && (!this.worldObj.isRemote)) {

			if ((getCountOnUpdate() % 20 == 0) && (getFuel() > 1) && (getThrottle() > 0.0D)
					&& (this.fuelSuppliedCount <= 0)) {

				double t = getThrottle() * 1.4D;
				if (t > 1.0D)
					t = 1.0D;
				this.fuelConsumption += t * getAcInfo().fuelConsumption * getFuelConsumptionFactor();
				if (this.fuelConsumption > 1.0D) {
					int f = (int) this.fuelConsumption;
					this.fuelConsumption -= f;
					setFuel(getFuel() - f);
				}
			}

			int curFuel = getFuel();
			if ((canSupply()) && (getCountOnUpdate() % 10 == 0) && (curFuel < getMaxFuel())) {
				for (int i = 0; i < 3; i++) {
					if (curFuel < getMaxFuel()) {
						ItemStack fuel = getGuiInventory().getFuelSlotItemStack(i);
						if ((fuel != null) && ((fuel.getItem() instanceof MCH_ItemFuel))) {
							if (fuel.getItemDamage() < fuel.getMaxDamage()) {
								int fc = getMaxFuel() - curFuel;
								if (fc > 100)
									fc = 100;
								if (fuel.getItemDamage() > fuel.getMaxDamage() - fc) {
									fc = fuel.getMaxDamage() - fuel.getItemDamage();
								}
								fuel.setItemDamage(fuel.getItemDamage() + fc);
								curFuel += fc;
							}
						}
					}
				}
				if (getFuel() != curFuel) {
					MCH_Achievement.addStat(this.riddenByEntity, MCH_Achievement.supplyFuel, 1);
				}
				setFuel(curFuel);
			}
		}
	}

	public float getFuelConsumptionFactor() {
		return 1.0F;
	}

	public void updateSupplyAmmo() {
		if (!this.worldObj.isRemote) {
			boolean isReloading = false;
			if (((getRiddenByEntity() instanceof EntityPlayer)) && (!getRiddenByEntity().isDead)) {
				if ((((EntityPlayer) getRiddenByEntity()).openContainer instanceof MCH_AircraftGuiContainer)) {
					isReloading = true;
				}
			}
			setCommonStatus(2, isReloading);

			if ((!isDestroyed()) && (this.beforeSupplyAmmo == true) && (!isReloading)) {
				reloadAllWeapon();

				MCH_PacketNotifyAmmoNum.sendAllAmmoNum(this, null);
			}

			this.beforeSupplyAmmo = isReloading;
		}

		if (getCommonStatus(2)) {
			this.supplyAmmoWait = 20;
		}
		if (this.supplyAmmoWait > 0) {
			this.supplyAmmoWait -= 1;
		}
	}

	public void supplyAmmo(int weaponID) {
		if (this.worldObj.isRemote) {
			MCH_WeaponSet ws = getWeapon(weaponID);
			ws.supplyRestAllAmmo();
		} else {
			MCH_Achievement.addStat(this.riddenByEntity, MCH_Achievement.supplyAmmo, 1);

			if ((getRiddenByEntity() instanceof EntityPlayer)) {
				EntityPlayer player = (EntityPlayer) getRiddenByEntity();

				if (canPlayerSupplyAmmo(player, weaponID)) {
					MCH_WeaponSet ws = getWeapon(weaponID);

					for (MCH_WeaponInfo.RoundItem ri : ws.getInfo().roundItems) {
						int num = ri.num;
						for (int i = 0; i < player.inventory.mainInventory.length; i++) {
							ItemStack itemStack = player.inventory.mainInventory[i];
							if ((itemStack != null) && (itemStack.isItemEqual(ri.itemStack))) {
								if ((itemStack.getItem() == W_Item.getItemByName("water_bucket"))
										|| (itemStack.getItem() == W_Item.getItemByName("lava_bucket"))) {

									if (itemStack.stackSize == 1) {
										player.inventory.setInventorySlotContents(i,
												new ItemStack(W_Item.getItemByName("bucket"), 1));
										num--;
									}
								} else if (itemStack.stackSize > num) {
									itemStack.stackSize -= num;
									num = 0;
								} else {
									num -= itemStack.stackSize;
									itemStack.stackSize = 0;
									player.inventory.mainInventory[i] = null;
								}
							}
							if (num <= 0) {
								break;
							}
						}
					}

					ws.supplyRestAllAmmo();
				}
			}
		}
	}

	public void supplyAmmoToOtherAircraft() {
		float range = getAcInfo() != null ? getAcInfo().ammoSupplyRange : 0.0F;
		if (range <= 0.0F) {
			return;
		}
		if ((!this.worldObj.isRemote) && (getCountOnUpdate() % 40 == 0)) {
			List list = this.worldObj.getEntitiesWithinAABB(MCH_EntityAircraft.class,
					getBoundingBox().expand(range, range, range));

			for (int i = 0; i < list.size(); i++) {
				MCH_EntityAircraft ac = (MCH_EntityAircraft) list.get(i);

				if (!W_Entity.isEqual(this, ac)) {
					if (ac.canSupply()) {
						for (int wid = 0; wid < ac.getWeaponNum(); wid++) {
							MCH_WeaponSet ws = ac.getWeapon(wid);
							int num = ws.getRestAllAmmoNum() + ws.getAmmoNum();
							if (num < ws.getAllAmmoNum()) {
								int ammo = ws.getAllAmmoNum() / 10;
								if (ammo < 1)
									ammo = 1;
								ws.setRestAllAmmoNum(num + ammo);

								EntityPlayer player = ac.getEntityByWeaponId(wid);

								if (num != ws.getRestAllAmmoNum() + ws.getAmmoNum()) {
									if (ws.getAmmoNum() <= 0) {
										ws.reloadMag();
									}

									MCH_PacketNotifyAmmoNum.sendAmmoNum(ac, player, wid);
								}
							}
						}
					}
				}
			}
		}
	}

	public boolean canPlayerSupplyAmmo(EntityPlayer player, int weaponId) {
		if (MCH_Lib.getBlockIdY(this, 1, -3) == 0) {
			return false;
		}
		if (!canSupply()) {
			return false;
		}
		MCH_WeaponSet ws = getWeapon(weaponId);

		if (ws.getRestAllAmmoNum() + ws.getAmmoNum() >= ws.getAllAmmoNum()) {
			return false;
		}

		for (MCH_WeaponInfo.RoundItem ri : ws.getInfo().roundItems) {
			int num = ri.num;
			for (ItemStack itemStack : player.inventory.mainInventory) {
				if ((itemStack != null) && (itemStack.isItemEqual(ri.itemStack))) {
					num -= itemStack.stackSize;
				}
				if (num <= 0) {
					break;
				}
			}

			if (num > 0) {
				return false;
			}
		}

		return true;
	}

	public MCH_EntityAircraft setTextureName(String name) {
		if ((name != null) && (!name.isEmpty())) {

			getDataWatcher().updateObject(21, String.valueOf(name));
		}
		return this;
	}

	public String getTextureName() {
		return getDataWatcher().getWatchableObjectString(21);
	}

	public void switchNextTextureName() {
		if (getAcInfo() != null) {
			setTextureName(getAcInfo().getNextTextureName(getTextureName()));
		}
	}

	public void zoomCamera() {
		if (canZoom()) {
			float z = this.camera.getCameraZoom();

			if (z >= getZoomMax() - 0.01D) {
				z = 1.0F;
			} else {
				z *= 2.0F;
				if (z >= getZoomMax()) {
					z = getZoomMax();
				}
			}
			this.camera.setCameraZoom(z <= getZoomMax() + 0.01D ? z : 1.0F);
		}
	}

	public int getZoomMax() {
		return getAcInfo() != null ? getAcInfo().cameraZoom : 1;
	}

	public boolean canZoom() {
		return getZoomMax() > 1;
	}

	public boolean canSwitchCameraMode() {
		if (isDestroyed())
			return false;
		return (getAcInfo() != null) && (getAcInfo().isEnableNightVision);
	}

	public boolean canSwitchCameraMode(int seatID) {
		if (isDestroyed())
			return false;
		return (canSwitchCameraMode()) && (this.camera.isValidUid(seatID));
	}

	public int getCameraMode(EntityPlayer player) {
		return this.camera.getMode(getSeatIdByEntity(player));
	}

	public String getCameraModeName(EntityPlayer player) {
		return this.camera.getModeName(getSeatIdByEntity(player));
	}

	public void switchCameraMode(EntityPlayer player) {
		switchCameraMode(player, this.camera.getMode(getSeatIdByEntity(player)) + 1);
	}

	public void switchCameraMode(EntityPlayer player, int mode) {
		this.camera.setMode(getSeatIdByEntity(player), mode);
	}

	public void updateCameraViewers() {
		for (int i = 0; i < getSeatNum() + 1; i++) {
			this.camera.updateViewer(i, getEntityBySeatId(i));
		}
	}

	public void updateRadar(int radarSpeed) {
		if (isEntityRadarMounted()) {
			this.radarRotate += radarSpeed;

			if (this.radarRotate >= 360) {
				this.radarRotate = 0;
			}

			if (this.radarRotate == 0) {

				this.entityRadar.updateXZ(this, 64);
			}
		}
	}

	public int getRadarRotate() {
		return this.radarRotate;
	}

	public void initRadar() {
		this.entityRadar.clear();
		this.radarRotate = 0;
	}

	public ArrayList<MCH_Vector2> getRadarEntityList() {
		return this.entityRadar.getEntityList();
	}

	public ArrayList<MCH_Vector2> getRadarEnemyList() {
		return this.entityRadar.getEnemyList();
	}

	public void moveEntity(double par1, double par3, double par5) {
		if (getAcInfo() == null) {
			return;
		}
		this.worldObj.theProfiler.startSection("move");
		this.ySize *= 0.4F;
		double d3 = this.posX;
		double d4 = this.posY;
		double d5 = this.posZ;

		double d6 = par1;
		double d7 = par3;
		double d8 = par5;
		AxisAlignedBB axisalignedbb = this.boundingBox.copy();

		List list = getCollidingBoundingBoxes(this, this.boundingBox.addCoord(par1, par3, par5));

		for (int i = 0; i < list.size(); i++) {
			par3 = ((AxisAlignedBB) list.get(i)).calculateYOffset(this.boundingBox, par3);
		}

		this.boundingBox.offset(0.0D, par3, 0.0D);

		if ((!this.field_70135_K) && (d7 != par3)) {
			par5 = 0.0D;
			par3 = 0.0D;
			par1 = 0.0D;
		}

		boolean flag1 = (this.onGround) || ((d7 != par3) && (d7 < 0.0D));

		for (int j = 0; j < list.size(); j++) {
			par1 = ((AxisAlignedBB) list.get(j)).calculateXOffset(this.boundingBox, par1);
		}

		this.boundingBox.offset(par1, 0.0D, 0.0D);

		if ((!this.field_70135_K) && (d6 != par1)) {
			par5 = 0.0D;
			par3 = 0.0D;
			par1 = 0.0D;
		}

		for (int j = 0; j < list.size(); j++) {
			par5 = ((AxisAlignedBB) list.get(j)).calculateZOffset(this.boundingBox, par5);
		}

		this.boundingBox.offset(0.0D, 0.0D, par5);

		if ((!this.field_70135_K) && (d8 != par5)) {
			par5 = 0.0D;
			par3 = 0.0D;
			par1 = 0.0D;
		}

		if ((this.stepHeight > 0.0F) && (flag1) && (this.ySize < 0.05F) && ((d6 != par1) || (d8 != par5))) {
			double d12 = par1;
			double d10 = par3;
			double d11 = par5;
			par1 = d6;
			par3 = this.stepHeight;
			par5 = d8;
			AxisAlignedBB axisalignedbb1 = this.boundingBox.copy();
			this.boundingBox.setBB(axisalignedbb);
			list = getCollidingBoundingBoxes(this, this.boundingBox.addCoord(d6, par3, d8));

			for (int k = 0; k < list.size(); k++) {
				par3 = ((AxisAlignedBB) list.get(k)).calculateYOffset(this.boundingBox, par3);
			}

			this.boundingBox.offset(0.0D, par3, 0.0D);

			if ((!this.field_70135_K) && (d7 != par3)) {
				par5 = 0.0D;
				par3 = 0.0D;
				par1 = 0.0D;
			}

			for (int k = 0; k < list.size(); k++) {
				par1 = ((AxisAlignedBB) list.get(k)).calculateXOffset(this.boundingBox, par1);
			}

			this.boundingBox.offset(par1, 0.0D, 0.0D);

			if ((!this.field_70135_K) && (d6 != par1)) {
				par5 = 0.0D;
				par3 = 0.0D;
				par1 = 0.0D;
			}

			for (int k = 0; k < list.size(); k++) {
				par5 = ((AxisAlignedBB) list.get(k)).calculateZOffset(this.boundingBox, par5);
			}

			this.boundingBox.offset(0.0D, 0.0D, par5);

			if ((!this.field_70135_K) && (d8 != par5)) {
				par5 = 0.0D;
				par3 = 0.0D;
				par1 = 0.0D;
			}

			if ((!this.field_70135_K) && (d7 != par3)) {
				par5 = 0.0D;
				par3 = 0.0D;
				par1 = 0.0D;
			} else {
				par3 = -this.stepHeight;

				for (int k = 0; k < list.size(); k++) {
					par3 = ((AxisAlignedBB) list.get(k)).calculateYOffset(this.boundingBox, par3);
				}

				this.boundingBox.offset(0.0D, par3, 0.0D);
			}

			if (d12 * d12 + d11 * d11 >= par1 * par1 + par5 * par5) {
				par1 = d12;
				par3 = d10;
				par5 = d11;
				this.boundingBox.setBB(axisalignedbb1);
			}
		}

		this.worldObj.theProfiler.endSection();
		this.worldObj.theProfiler.startSection("rest");
		this.posX = ((this.boundingBox.minX + this.boundingBox.maxX) / 2.0D);
		this.posY = (this.boundingBox.minY + this.yOffset - this.ySize);
		this.posZ = ((this.boundingBox.minZ + this.boundingBox.maxZ) / 2.0D);
		this.isCollidedHorizontally = ((d6 != par1) || (d8 != par5));
		this.isCollidedVertically = (d7 != par3);
		this.onGround = ((d7 != par3) && (d7 < 0.0D));
		this.isCollided = ((this.isCollidedHorizontally) || (this.isCollidedVertically));
		updateFallState(par3, this.onGround);

		if (d6 != par1) {
			this.motionX = 0.0D;
		}

		if (d7 != par3) {
			this.motionY = 0.0D;
		}

		if (d8 != par5) {
			this.motionZ = 0.0D;
		}

		double d12 = this.posX - d3;
		double d10 = this.posY - d4;
		double d11 = this.posZ - d5;

		try {
			doBlockCollisions();
		} catch (Throwable throwable) {
			CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Checking entity tile collision");
			CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being checked for collision");
			addEntityCrashInfo(crashreportcategory);
			throw new ReportedException(crashreport);
		}

		this.worldObj.theProfiler.endSection();
	}

	public static List getCollidingBoundingBoxes(Entity par1Entity, AxisAlignedBB par2AxisAlignedBB) {
		ArrayList collidingBoundingBoxes = new ArrayList();
		collidingBoundingBoxes.clear();
		int i = MathHelper.floor_double(par2AxisAlignedBB.minX);
		int j = MathHelper.floor_double(par2AxisAlignedBB.maxX + 1.0D);
		int k = MathHelper.floor_double(par2AxisAlignedBB.minY);
		int l = MathHelper.floor_double(par2AxisAlignedBB.maxY + 1.0D);
		int i1 = MathHelper.floor_double(par2AxisAlignedBB.minZ);
		int j1 = MathHelper.floor_double(par2AxisAlignedBB.maxZ + 1.0D);

		for (int k1 = i; k1 < j; k1++) {
			for (int l1 = i1; l1 < j1; l1++) {
				if (par1Entity.worldObj.blockExists(k1, 64, l1)) {
					for (int i2 = k - 1; i2 < l; i2++) {
						Block block = W_WorldFunc.getBlock(par1Entity.worldObj, k1, i2, l1);

						if (block != null) {
							block.addCollisionBoxesToList(par1Entity.worldObj, k1, i2, l1, par2AxisAlignedBB,
									collidingBoundingBoxes, par1Entity);
						}
					}
				}
			}
		}

		double d0 = 0.25D;
		List list = par1Entity.worldObj.getEntitiesWithinAABBExcludingEntity(par1Entity,
				par2AxisAlignedBB.expand(d0, d0, d0));

		for (int j2 = 0; j2 < list.size(); j2++) {
			Entity entity = (Entity) list.get(j2);

			if ((!W_Lib.isEntityLivingBase(entity)) && (!(entity instanceof MCH_EntitySeat))
					&& (!(entity instanceof MCH_EntityHitBox))) {
				AxisAlignedBB axisalignedbb1 = entity.getBoundingBox();

				if ((axisalignedbb1 != null) && (axisalignedbb1.intersectsWith(par2AxisAlignedBB))) {
					collidingBoundingBoxes.add(axisalignedbb1);
				}

				axisalignedbb1 = par1Entity.getCollisionBox(entity);

				if ((axisalignedbb1 != null) && (axisalignedbb1.intersectsWith(par2AxisAlignedBB))) {
					collidingBoundingBoxes.add(axisalignedbb1);
				}
			}
		}
		return collidingBoundingBoxes;
	}

	protected void onUpdate_updateBlock() {
		if (!MCH_Config.Collision_DestroyBlock.prmBool) {
			return;
		}

		for (int l = 0; l < 4; l++) {
			int i1 = MathHelper.floor_double(this.posX + (l % 2 - 0.5D) * 0.8D);
			int j1 = MathHelper.floor_double(this.posZ + (l / 2 - 0.5D) * 0.8D);

			for (int k1 = 0; k1 < 2; k1++) {
				int l1 = MathHelper.floor_double(this.posY) + k1;
				Block block = W_WorldFunc.getBlock(this.worldObj, i1, l1, j1);
				if (!W_Block.isNull(block)) {
					if (block == W_Block.getSnowLayer()) {
						this.worldObj.setBlockToAir(i1, l1, j1);
					}
					if ((block == W_Blocks.waterlily) || (block == W_Blocks.cake)) {
						W_WorldFunc.destroyBlock(this.worldObj, i1, l1, j1, false);
					}
				}
			}
		}
	}

	public void onUpdate_ParticleSmoke() {
		if (!this.worldObj.isRemote)
			return;
		if (getCurrentThrottle() <= 0.10000000149011612D) {
			return;
		}

		float yaw = getRotYaw();
		float pitch = getRotPitch();
		float roll = getRotRoll();

		MCH_WeaponSet ws = getCurrentWeapon(getRiddenByEntity());

		if (!(ws.getFirstWeapon() instanceof MCH_WeaponSmoke)) {
			return;
		}
		for (int i = 0; i < ws.getWeaponNum(); i++) {
			MCH_WeaponBase wb = ws.getWeapon(i);
			if (wb != null) {
				MCH_WeaponInfo wi = wb.getInfo();
				if (wi != null) {
					Vec3 rot = MCH_Lib.RotVec3(0.0D, 0.0D, 1.0D, -yaw - 180.0F + wb.fixRotationYaw,
							pitch - wb.fixRotationPitch, roll);

					if (this.rand.nextFloat() <= getCurrentThrottle() * 1.5D) {
						Vec3 pos = MCH_Lib.RotVec3(wb.position, -yaw, -pitch, -roll);
						double x = this.posX + pos.xCoord + rot.xCoord;
						double y = this.posY + pos.yCoord + rot.yCoord;
						double z = this.posZ + pos.zCoord + rot.zCoord;

						for (int smk = 0; smk < wi.smokeNum; smk++) {
							float c = this.rand.nextFloat() * 0.05F;
							int maxAge = (int) (this.rand.nextDouble() * wi.smokeMaxAge);

							MCH_ParticleParam prm = new MCH_ParticleParam(this.worldObj, "smoke", x, y, z);
							prm.setMotion(rot.xCoord * wi.acceleration + (this.rand.nextDouble() - 0.5D) * 0.2D,
									rot.yCoord * wi.acceleration + (this.rand.nextDouble() - 0.5D) * 0.2D,
									rot.zCoord * wi.acceleration + (this.rand.nextDouble() - 0.5D) * 0.2D);

							prm.size = ((this.rand.nextInt(5) + 5.0F) * wi.smokeSize);
							prm.setColor(wi.color.a + this.rand.nextFloat() * 0.05F, wi.color.r + c, wi.color.g + c,
									wi.color.b + c);

							prm.age = maxAge;
							prm.toWhite = true;
							prm.diffusible = true;

							MCH_ParticlesUtil.spawnParticle(prm);
						}
					}
				}
			}
		}
	}

	protected void onUpdate_ParticleSandCloud(boolean seaOnly) {
		if ((seaOnly) && (!getAcInfo().enableSeaSurfaceParticle)) {
			return;
		}

		double particlePosY = (int) this.posY;
		boolean b = false;

		float scale = getAcInfo().particlesScale * 3.0F;
		if (seaOnly) {
			scale *= 2.0F;
		}

		double throttle = getCurrentThrottle();
		throttle *= 2.0D;
		if (throttle > 1.0D) {
			throttle = 1.0D;
		}
		int count = seaOnly ? (int) (scale * 7.0F) : 0;

		int rangeY = (int) (scale * 10.0F) + 1;
		int y;
		for (y = 0; (y < rangeY) && (!b); y++) {
			for (int x = -1; x <= 1; x++) {
				for (int z = -1; z <= 1; z++) {
					Block block = W_WorldFunc.getBlock(this.worldObj, (int) (this.posX + 0.5D) + x,
							(int) (this.posY + 0.5D) - y, (int) (this.posZ + 0.5D) + z);

					if ((!b) && (block != null) && (!Block.isEqualTo(block, Blocks.air))) {
						if ((seaOnly) && (W_Block.isEqual(block, W_Block.getWater()))) {
							count--;
						}
						if (count <= 0) {
							particlePosY = this.posY + 1.0D + scale / 5.0F - y;
							b = true;
							x += 100;
							break;
						}
					}
				}
			}
		}
		double pn = (rangeY - y + 1) / (5.0D * scale) / 2.0D;
		if ((b) && (getAcInfo().particlesScale > 0.01F)) {
			for (int k = 0; k < (int) (throttle * 6.0D * pn); k++) {
				float r = (float) (this.rand.nextDouble() * 3.141592653589793D * 2.0D);
				double dx = MathHelper.cos(r);
				double dz = MathHelper.sin(r);
				MCH_ParticleParam prm = new MCH_ParticleParam(this.worldObj, "smoke", this.posX + dx * scale * 3.0D,
						particlePosY + (this.rand.nextDouble() - 0.5D) * scale, this.posZ + dz * scale * 3.0D,
						scale * (dx * 0.3D), scale * -0.4D * 0.05D, scale * (dz * 0.3D), scale * 5.0F);

				prm.setColor(prm.a * 0.6F, prm.r, prm.g, prm.b);
				prm.age = ((int) (10.0F * scale));
				prm.motionYUpAge = (seaOnly ? 0.2F : 0.1F);
				MCH_ParticlesUtil.spawnParticle(prm);
			}
		}
	}

	protected boolean canTriggerWalking() {
		return false;
	}

	public AxisAlignedBB getCollisionBox(Entity par1Entity) {
		return par1Entity.boundingBox;
	}

	public AxisAlignedBB getBoundingBox() {
		return this.boundingBox;
	}

	public boolean canBePushed() {
		return false;
	}

	public double getMountedYOffset() {
		return 0.0D;
	}

	public float getShadowSize() {
		return 2.0F;
	}

	public boolean canBeCollidedWith() {
		return !this.isDead;
	}

	public boolean useFlare(int type) {
		if ((getAcInfo() == null) || (!getAcInfo().haveFlare())) {
			return false;
		}
		for (int i : getAcInfo().flare.types) {
			if (i == type) {
				setCommonStatus(0, true);
				if (this.flareDv.use(type)) {
					return true;
				}
			}
		}

		return false;
	}

	public int getCurrentFlareType() {
		if (!haveFlare())
			return 0;
		return getAcInfo().flare.types[this.currentFlareIndex];
	}

	public void nextFlareType() {
		if (haveFlare()) {
			this.currentFlareIndex = ((this.currentFlareIndex + 1) % getAcInfo().flare.types.length);
		}
	}

	public boolean canUseFlare() {
		if ((getAcInfo() == null) || (!getAcInfo().haveFlare()))
			return false;
		if (getCommonStatus(0))
			return false;
		return this.flareDv.tick == 0;
	}

	public boolean isFlarePreparation() {
		return this.flareDv.isInPreparation();
	}

	public boolean isFlareUsing() {
		return this.flareDv.isUsing();
	}

	public int getFlareTick() {
		return this.flareDv.tick;
	}

	public boolean haveFlare() {
		return (getAcInfo() != null) && (getAcInfo().haveFlare());
	}

	public boolean haveFlare(int seatID) {
		return (haveFlare()) && (seatID >= 0) && (seatID <= 1);
	}

	private static final MCH_EntitySeat[] seatsDummy = new MCH_EntitySeat[0];

	public MCH_EntitySeat[] getSeats() {
		return this.seats != null ? this.seats : seatsDummy;
	}

	public int getSeatIdByEntity(Entity entity) {
		if (entity == null)
			return -1;
		if (isEqual(getRiddenByEntity(), entity))
			return 0;
		for (int i = 0; i < getSeats().length; i++) {
			MCH_EntitySeat seat = getSeats()[i];
			if ((seat != null) && (isEqual(seat.riddenByEntity, entity))) {
				return i + 1;
			}
		}
		return -1;
	}

	public MCH_EntitySeat getSeatByEntity(Entity entity) {
		int idx = getSeatIdByEntity(entity);
		if (idx > 0) {
			return getSeat(idx - 1);
		}
		return null;
	}

	public Entity getEntityBySeatId(int id) {
		if (id == 0) {
			return getRiddenByEntity();
		}

		id--;
		if ((id < 0) || (id >= getSeats().length))
			return null;
		return this.seats[id] != null ? this.seats[id].riddenByEntity : null;
	}

	public EntityPlayer getEntityByWeaponId(int id) {
		if ((id >= 0) && (id < getWeaponNum())) {
			for (int i = 0; i < this.currentWeaponID.length; i++) {
				if (this.currentWeaponID[i] == id) {
					Entity e = getEntityBySeatId(i);
					if ((e instanceof EntityPlayer)) {
						return (EntityPlayer) e;
					}
				}
			}
		}

		return null;
	}

	public Entity getWeaponUserByWeaponName(String name) {
		if (getAcInfo() == null) {
			return null;
		}
		MCH_AircraftInfo.Weapon weapon = getAcInfo().getWeaponByName(name);
		Entity entity = null;
		if (weapon != null) {
			entity = getEntityBySeatId(getWeaponSeatID(null, weapon));
			if ((entity == null) && (weapon.canUsePilot)) {
				entity = getRiddenByEntity();
			}
		}
		return entity;
	}

	protected void newSeats(int seatsNum) {
		if (seatsNum >= 2) {
			if (this.seats != null) {
				for (int i = 0; i < this.seats.length; i++) {
					if (this.seats[i] != null) {
						this.seats[i].setDead();
						this.seats[i] = null;
					}
				}
			}
			this.seats = new MCH_EntitySeat[seatsNum - 1];
		}
	}

	public MCH_EntitySeat getSeat(int idx) {
		return idx < this.seats.length ? this.seats[idx] : null;
	}

	public void setSeat(int idx, MCH_EntitySeat seat) {
		if (idx < this.seats.length) {
			MCH_Lib.DbgLog(this.worldObj, "MCH_EntityAircraft.setSeat SeatID=" + idx + " / seat[]"
					+ (this.seats[idx] != null) + " / " + (seat.riddenByEntity != null), new Object[0]);
			if ((this.seats[idx] != null) && (this.seats[idx].riddenByEntity != null)) {
			}

			this.seats[idx] = seat;
		}
	}

	public boolean isValidSeatID(int seatID) {
		return (seatID >= 0) && (seatID < getSeatNum() + 1);
	}

	public void updateHitBoxPosition() {
	}

	public void updateSeatsPosition(double px, double py, double pz, boolean setPrevPos) {
		MCH_SeatInfo[] info = getSeatsInfo();

		if ((this.pilotSeat != null) && (!this.pilotSeat.isDead)) {
			this.pilotSeat.prevPosX = this.pilotSeat.posX;
			this.pilotSeat.prevPosY = this.pilotSeat.posY;
			this.pilotSeat.prevPosZ = this.pilotSeat.posZ;

			this.pilotSeat.setPosition(px, py, pz);
			if ((info != null) && (info.length > 0) && (info[0] != null)) {
				Vec3 v = getTransformedPosition(info[0].pos.xCoord, info[0].pos.yCoord, info[0].pos.zCoord, px, py, pz,
						info[0].rotSeat);

				this.pilotSeat.setPosition(v.xCoord, v.yCoord, v.zCoord);
			}
			this.pilotSeat.rotationPitch = getRotPitch();
			this.pilotSeat.rotationYaw = getRotYaw();
			if (setPrevPos) {
				this.pilotSeat.prevPosX = this.pilotSeat.posX;
				this.pilotSeat.prevPosY = this.pilotSeat.posY;
				this.pilotSeat.prevPosZ = this.pilotSeat.posZ;
			}
		}

		int i = 0;

		for (MCH_EntitySeat seat : this.seats) {
			i++;
			if ((seat != null) && (!seat.isDead)) {
				float offsetY = 0.0F;
				if (seat.riddenByEntity != null) {
					if (W_Lib.isClientPlayer(seat.riddenByEntity)) {
						offsetY = 1.0F;

					} else if (seat.riddenByEntity.height >= 1.0F) {
						offsetY = -seat.riddenByEntity.height + 1.0F;
					}
				}

				seat.prevPosX = seat.posX;
				seat.prevPosY = seat.posY;
				seat.prevPosZ = seat.posZ;
				MCH_SeatInfo si = i < info.length ? info[i] : info[0];
				Vec3 v = getTransformedPosition(si.pos.xCoord, si.pos.yCoord + offsetY, si.pos.zCoord, px, py, pz,
						si.rotSeat);

				seat.setPosition(v.xCoord, v.yCoord, v.zCoord);
				seat.rotationPitch = getRotPitch();
				seat.rotationYaw = getRotYaw();
				if (setPrevPos) {
					seat.prevPosX = seat.posX;
					seat.prevPosY = seat.posY;
					seat.prevPosZ = seat.posZ;
				}

				if ((si instanceof MCH_SeatRackInfo)) {
					seat.updateRotation(((MCH_SeatRackInfo) si).fixYaw + getRotYaw(), ((MCH_SeatRackInfo) si).fixPitch);
				}

				seat.updatePosition();
			}
		}
	}

	public int getClientPositionDelayCorrection() {
		return 7;
	}

	public void setPositionAndRotation2(double par1, double par3, double par5, float par7, float par8, int par9) {
		this.aircraftPosRotInc = (par9 + getClientPositionDelayCorrection());

		this.aircraftX = par1;
		this.aircraftY = par3;
		this.aircraftZ = par5;
		this.aircraftYaw = par7;
		this.aircraftPitch = par8;
		this.motionX = this.velocityX;
		this.motionY = this.velocityY;
		this.motionZ = this.velocityZ;
	}

	public void updateRiderPosition(double px, double py, double pz) {
		MCH_SeatInfo[] info = getSeatsInfo();

		if ((this.riddenByEntity != null) && (!this.riddenByEntity.isDead)) {
			float riddenEntityYOffset = this.riddenByEntity.yOffset;

			float offset = 0.0F;

			if ((this.riddenByEntity instanceof EntityPlayer)) {

				if (!W_Lib.isClientPlayer(this.riddenByEntity)) {
					offset -= 1.62F;
				}
			}

			Vec3 v;
			if ((info != null) && (info.length > 0)) {
				v = getTransformedPosition(info[0].pos.xCoord, info[0].pos.yCoord + riddenEntityYOffset - 0.5D,
						info[0].pos.zCoord, px, py, pz, info[0].rotSeat);

			} else {

				v = getTransformedPosition(0.0D, riddenEntityYOffset - 1.0F, 0.0D);
			}
			this.riddenByEntity.yOffset = 0.0F;
			this.riddenByEntity.setPosition(v.xCoord, v.yCoord, v.zCoord);
			this.riddenByEntity.yOffset = riddenEntityYOffset;
		}
	}

	public void updateRiderPosition() {
		updateRiderPosition(this.posX, this.posY, this.posZ);
	}

	public Vec3 calcOnTurretPos(Vec3 pos) {
		float ry = getLastRiderYaw();
		if (getRiddenByEntity() != null) {
			ry = getRiddenByEntity().rotationYaw;
		}

		Vec3 tpos = getAcInfo().turretPosition.addVector(0.0D, pos.yCoord, 0.0D);
		Vec3 v = pos.addVector(-tpos.xCoord, -tpos.yCoord, -tpos.zCoord);
		v = MCH_Lib.RotVec3(v, -ry, 0.0F, 0.0F);
		Vec3 vv = MCH_Lib.RotVec3(tpos, -getRotYaw(), -getRotPitch(), -getRotRoll());
		v.xCoord += vv.xCoord;
		v.yCoord += vv.yCoord;
		v.zCoord += vv.zCoord;

		return v;
	}

	public float getLastRiderYaw() {
		return this.lastRiderYaw;
	}

	public float getLastRiderPitch() {
		return this.lastRiderPitch;
	}

	@SideOnly(Side.CLIENT)
	public void setupAllRiderRenderPosition(float tick, EntityPlayer player) {
		double x = this.lastTickPosX + (this.posX - this.lastTickPosX) * tick;
		double y = this.lastTickPosY + (this.posY - this.lastTickPosY) * tick;
		double z = this.lastTickPosZ + (this.posZ - this.lastTickPosZ) * tick;
		updateRiderPosition(x, y, z);
		updateSeatsPosition(x, y, z, true);
		for (int i = 0; i < getSeatNum() + 1; i++) {
			Entity e = getEntityBySeatId(i);
			if (e != null) {
				e.lastTickPosX = e.posX;
				e.lastTickPosY = e.posY;
				e.lastTickPosZ = e.posZ;
			}
		}

		if ((getTVMissile() != null) && (W_Lib.isClientPlayer(getTVMissile().shootingEntity))) {
			Entity tv = getTVMissile();
			x = tv.prevPosX + (tv.posX - tv.prevPosX) * tick;
			y = tv.prevPosY + (tv.posY - tv.prevPosY) * tick;
			z = tv.prevPosZ + (tv.posZ - tv.prevPosZ) * tick;
			MCH_ViewEntityDummy.setCameraPosition(x, y, z);
		} else {
			MCH_AircraftInfo.CameraPosition cpi = getCameraPosInfo();
			if ((cpi != null) && (cpi.pos != null)) {
				MCH_SeatInfo seatInfo = getSeatInfo(player);
				Vec3 v;
				if ((seatInfo != null) && (seatInfo.rotSeat)) {
					v = calcOnTurretPos(cpi.pos);
				} else {
					v = MCH_Lib.RotVec3(cpi.pos, -getRotYaw(), -getRotPitch(), -getRotRoll());
				}
				MCH_ViewEntityDummy.setCameraPosition(x + v.xCoord, y + v.yCoord, z + v.zCoord);
				if (!cpi.fixRot) {
				}
			}
		}
	}

	public Vec3 getTurretPos(Vec3 pos, boolean turret) {
		if (turret) {
			float ry = getLastRiderYaw();
			if (getRiddenByEntity() != null) {
				ry = getRiddenByEntity().rotationYaw;
			}

			Vec3 tpos = getAcInfo().turretPosition.addVector(0.0D, pos.yCoord, 0.0D);
			Vec3 v = pos.addVector(-tpos.xCoord, -tpos.yCoord, -tpos.zCoord);
			v = MCH_Lib.RotVec3(v, -ry, 0.0F, 0.0F);
			Vec3 vv = MCH_Lib.RotVec3(tpos, -getRotYaw(), -getRotPitch(), -getRotRoll());
			v.xCoord += vv.xCoord;
			v.yCoord += vv.yCoord;
			v.zCoord += vv.zCoord;
			return v;
		}
		return Vec3.createVectorHelper(0.0D, 0.0D, 0.0D);
	}

	public Vec3 getTransformedPosition(Vec3 v) {
		return getTransformedPosition(v.xCoord, v.yCoord, v.zCoord);
	}

	public Vec3 getTransformedPosition(double x, double y, double z) {
		return getTransformedPosition(x, y, z, this.posX, this.posY, this.posZ);
	}

	public Vec3 getTransformedPosition(Vec3 v, Vec3 pos) {
		return getTransformedPosition(v.xCoord, v.yCoord, v.zCoord, pos.xCoord, pos.yCoord, pos.zCoord);
	}

	public Vec3 getTransformedPosition(Vec3 v, double px, double py, double pz) {
		return getTransformedPosition(v.xCoord, v.yCoord, v.zCoord, this.posX, this.posY, this.posZ);
	}

	public Vec3 getTransformedPosition(double x, double y, double z, double px, double py, double pz) {
		Vec3 v = MCH_Lib.RotVec3(x, y, z, -getRotYaw(), -getRotPitch(), -getRotRoll());
		return v.addVector(px, py, pz);
	}

	public Vec3 getTransformedPosition(double x, double y, double z, double px, double py, double pz, boolean rotSeat) {
		if ((rotSeat) && (getAcInfo() != null)) {
			MCH_AircraftInfo info = getAcInfo();
			Vec3 tv = MCH_Lib.RotVec3(x - info.turretPosition.xCoord, y - info.turretPosition.yCoord,
					z - info.turretPosition.zCoord, -getLastRiderYaw() + getRotYaw(), 0.0F, 0.0F);

			x = tv.xCoord + info.turretPosition.xCoord;
			y = tv.yCoord + info.turretPosition.xCoord;
			z = tv.zCoord + info.turretPosition.xCoord;
		}

		Vec3 v = MCH_Lib.RotVec3(x, y, z, -getRotYaw(), -getRotPitch(), -getRotRoll());

		return v.addVector(px, py, pz);
	}

	protected MCH_SeatInfo[] getSeatsInfo() {
		if (this.seatsInfo != null) {
			return this.seatsInfo;
		}

		newSeatsPos();

		return this.seatsInfo;
	}

	public MCH_SeatInfo getSeatInfo(int index) {
		MCH_SeatInfo[] seats = getSeatsInfo();
		if ((index >= 0) && (seats != null) && (index < seats.length)) {
			return seats[index];
		}
		return null;
	}

	public MCH_SeatInfo getSeatInfo(Entity entity) {
		return getSeatInfo(getSeatIdByEntity(entity));
	}

	protected void setSeatsInfo(MCH_SeatInfo[] v) {
		this.seatsInfo = v;
	}

	public int getSeatNum() {
		if (getAcInfo() == null)
			return 0;
		int s = getAcInfo().getNumSeatAndRack();

		return s >= 1 ? s - 1 : 1;
	}

	protected void newSeatsPos() {
		if (getAcInfo() != null) {
			MCH_SeatInfo[] v = new MCH_SeatInfo[getAcInfo().getNumSeatAndRack()];
			for (int i = 0; i < v.length; i++) {
				v[i] = ((MCH_SeatInfo) getAcInfo().seatList.get(i));
			}
			setSeatsInfo(v);
		}
	}

	public void createSeats(String uuid) {
		if (this.worldObj.isRemote)
			return;
		if (uuid.isEmpty()) {
			return;
		}
		setCommonUniqueId(uuid);

		this.seats = new MCH_EntitySeat[getSeatNum()];
		for (int i = 0; i < this.seats.length; i++) {
			this.seats[i] = new MCH_EntitySeat(this.worldObj, this.posX, this.posY, this.posZ);
			this.seats[i].parentUniqueID = getCommonUniqueId();
			this.seats[i].seatID = i;
			this.seats[i].setParent(this);
			this.worldObj.spawnEntityInWorld(this.seats[i]);
		}
	}

	public boolean interactFirstSeat(EntityPlayer player) {
		if (getSeats() == null)
			return false;
		int seatId = 1;
		for (MCH_EntitySeat seat : getSeats()) {
			if ((seat != null) && (seat.riddenByEntity == null) && (!isMountedEntity(player))
					&& (canRideSeatOrRack(seatId, player))) {
				if (this.worldObj.isRemote)
					break;
				player.mountEntity(seat);
				break;
			}

			seatId++;
		}
		return true;
	}

	public void onMountPlayerSeat(MCH_EntitySeat seat, Entity entity) {
		if (seat != null) {
			if ((((entity instanceof EntityPlayer)) || ((entity instanceof MCH_EntityGunner)) ? 1 : 0) != 0) {
			}
		} else {
			return;
		}
		if ((this.worldObj.isRemote) && (MCH_Lib.getClientPlayer() == entity)) {
			switchGunnerFreeLookMode(false);
		}

		initCurrentWeapon(entity);

		MCH_Lib.DbgLog(this.worldObj, "onMountEntitySeat:%d",
				new Object[] { Integer.valueOf(W_Entity.getEntityId(entity)) });

		Entity pilot = getRiddenByEntity();
		int sid = getSeatIdByEntity(entity);

		if ((sid == 1) && ((getAcInfo() == null) || (!getAcInfo().isEnableConcurrentGunnerMode))) {
			switchGunnerMode(false);
		}

		if (sid > 0) {
			this.isGunnerModeOtherSeat = true;
		}

		if ((pilot != null) && (getAcInfo() != null)) {
			int cwid = getCurrentWeaponID(pilot);
			MCH_AircraftInfo.Weapon w = getAcInfo().getWeaponById(cwid);
			if ((w != null) && (getWeaponSeatID(getWeaponInfoById(cwid), w) == sid)) {
				int next = getNextWeaponID(pilot, 1);
				MCH_Lib.DbgLog(this.worldObj, "onMountEntitySeat:%d:->%d",
						new Object[] { Integer.valueOf(W_Entity.getEntityId(pilot)), Integer.valueOf(next) });
				if (next >= 0) {
					switchWeapon(pilot, next);
				}
			}
		}
		if (this.worldObj.isRemote) {
			updateClientSettings(sid);
		}
	}

	public MCH_WeaponInfo getWeaponInfoById(int id) {
		if (id >= 0) {
			MCH_WeaponSet ws = getWeapon(id);
			if (ws != null) {
				return ws.getInfo();
			}
		}
		return null;
	}

	public abstract boolean canMountWithNearEmptyMinecart();

	protected void mountWithNearEmptyMinecart() {
		if (getRidingEntity() != null) {
			return;
		}
		int d = 2;
		if (this.dismountedUserCtrl) {
			d = 6;
		}

		List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(d, d, d));

		if ((list != null) && (!list.isEmpty())) {
			for (int i = 0; i < list.size(); i++) {
				Entity entity = (Entity) list.get(i);

				if ((entity instanceof EntityMinecartEmpty)) {
					if (this.dismountedUserCtrl) {
						return;
					}

					if ((entity.riddenByEntity == null) && (entity.canBePushed())) {
						this.waitMountEntity = 20;
						MCH_Lib.DbgLog(this.worldObj.isRemote,
								"MCH_EntityAircraft.mountWithNearEmptyMinecart:" + entity, new Object[0]);

						mountEntity(entity);
						return;
					}
				}
			}
		}

		this.dismountedUserCtrl = false;
	}

	public boolean isRidePlayer() {
		if ((getRiddenByEntity() instanceof EntityPlayer))
			return true;
		for (MCH_EntitySeat seat : getSeats()) {
			if ((seat != null) && ((seat.riddenByEntity instanceof EntityPlayer))) {
				return true;
			}
		}
		return false;
	}

	public void onUnmountPlayerSeat(MCH_EntitySeat seat, Entity entity) {
		MCH_Lib.DbgLog(this.worldObj, "onUnmountPlayerSeat:%d",
				new Object[] { Integer.valueOf(W_Entity.getEntityId(entity)) });
		int sid = getSeatIdByEntity(entity);
		this.camera.initCamera(sid, entity);
		MCH_SeatInfo seatInfo = getSeatInfo(seat.seatID + 1);
		if (seatInfo != null) {
			setUnmountPosition(entity, Vec3.createVectorHelper(seatInfo.pos.xCoord, 0.0D, seatInfo.pos.zCoord));
		}
		if (!isRidePlayer()) {
			switchGunnerMode(false);
			switchHoveringMode(false);
		}
	}

	public boolean isCreatedSeats() {
		return !getCommonUniqueId().isEmpty();
	}

	public void onUpdate_Seats() {
		boolean b = false;
		for (int i = 0; i < this.seats.length; i++) {
			if (this.seats[i] != null) {
				if (!this.seats[i].isDead) {
					this.seats[i].fallDistance = 0.0F;
				}

			} else {
				b = true;
			}
		}

		if (b) {
			if (this.seatSearchCount > 40) {
				if (this.worldObj.isRemote) {

					MCH_PacketSeatListRequest.requestSeatList(this);

				} else {

					searchSeat();
				}
				this.seatSearchCount = 0;
			}

			this.seatSearchCount += 1;
		}
	}

	public void searchSeat() {
		List list = this.worldObj.getEntitiesWithinAABB(MCH_EntitySeat.class,
				this.boundingBox.expand(60.0D, 60.0D, 60.0D));

		for (int i = 0; i < list.size(); i++) {
			MCH_EntitySeat seat = (MCH_EntitySeat) list.get(i);
			if ((!seat.isDead) && (seat.parentUniqueID.equals(getCommonUniqueId()))) {
				if ((seat.seatID >= 0) && (seat.seatID < getSeatNum()) && (this.seats[seat.seatID] == null)) {
					this.seats[seat.seatID] = seat;
					seat.setParent(this);
				}
			}
		}
	}

	public String getCommonUniqueId() {
		return this.commonUniqueId;
	}

	public void setCommonUniqueId(String uniqId) {
		this.commonUniqueId = uniqId;
	}

	public void setDead() {
		setDead(false);
	}

	public void setDead(boolean dropItems) {
		this.dropContentsWhenDead = dropItems;
		super.setDead();

		if (getRiddenByEntity() != null) {
			getRiddenByEntity().mountEntity(null);
		}
		getGuiInventory().setDead();
		for (MCH_EntitySeat s : this.seats)
			if (s != null)
				s.setDead();
		if (this.soundUpdater != null) {
			this.soundUpdater.update();
		}
		if (getTowChainEntity() != null) {
			getTowChainEntity().setDead();
			setTowChainEntity(null);
		}

		for (Entity e : getParts()) {
			if (e != null) {
				e.setDead();
			}
		}
		MCH_Lib.DbgLog(this.worldObj, "setDead:" + (getAcInfo() != null ? getAcInfo().name : "null"), new Object[0]);
	}

	public void unmountEntity() {
		if (!isRidePlayer()) {
			switchHoveringMode(false);
		}

		this.moveLeft = (this.moveRight = this.throttleDown = this.throttleUp = false);

		Entity rByEntity = null;
		if (this.riddenByEntity != null) {
			rByEntity = this.riddenByEntity;
			this.camera.initCamera(0, rByEntity);
			if (!this.worldObj.isRemote) {
				this.riddenByEntity.mountEntity(null);
			}
		} else if (this.lastRiddenByEntity != null) {
			rByEntity = this.lastRiddenByEntity;
			if ((rByEntity instanceof EntityPlayer)) {
				this.camera.initCamera(0, rByEntity);
			}
		}
		MCH_Lib.DbgLog(this.worldObj, "unmountEntity:" + rByEntity, new Object[0]);

		if (!isRidePlayer()) {
			switchGunnerMode(false);
		}
		setCommonStatus(1, false);

		if (!isUAV()) {
			setUnmountPosition(rByEntity, getSeatsInfo()[0].pos);
		} else if ((rByEntity != null) && ((rByEntity.ridingEntity instanceof MCH_EntityUavStation))) {
			rByEntity.mountEntity(null);
		}

		this.riddenByEntity = null;
		this.lastRiddenByEntity = null;

		if (this.cs_dismountAll) {
			unmountCrew(false);
		}
	}

	public Entity getRidingEntity() {
		return this.ridingEntity;
	}

	public void startUnmountCrew() {
		this.isParachuting = true;
		if (haveHatch()) {
			foldHatch(true, true);
		}
	}

	public void stopUnmountCrew() {
		this.isParachuting = false;
	}

	public void unmountCrew() {
		if (getAcInfo() == null) {
			return;
		}
		if (getAcInfo().haveRepellingHook()) {
			if (!isRepelling()) {
				if (MCH_Lib.getBlockIdY(this, 3, -4) > 0) {
					unmountCrew(false);
				} else if (canStartRepelling()) {
					startRepelling();
				}

			} else {
				stopRepelling();
			}
		} else if (this.isParachuting) {
			stopUnmountCrew();
		} else if ((getAcInfo().isEnableParachuting) && (MCH_Lib.getBlockIdY(this, 3, -10) == 0)) {
			startUnmountCrew();
		} else {
			unmountCrew(false);
		}
	}

	public boolean isRepelling() {
		return getCommonStatus(5);
	}

	public void setRepellingStat(boolean b) {
		setCommonStatus(5, b);
	}

	public Vec3 getRopePos(int ropeIndex) {
		if ((getAcInfo() != null) && (getAcInfo().haveRepellingHook())) {
			if (ropeIndex < getAcInfo().repellingHooks.size()) {
				return getTransformedPosition(
						((MCH_AircraftInfo.RepellingHook) getAcInfo().repellingHooks.get(ropeIndex)).pos);
			}
		}
		return Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
	}

	private void startRepelling() {
		MCH_Lib.DbgLog(this.worldObj, "MCH_EntityAircraft.startRepelling()", new Object[0]);
		setRepellingStat(true);
		this.throttleUp = false;
		this.throttleDown = false;
		this.moveLeft = false;
		this.moveRight = false;
		this.tickRepelling = 0;
	}

	private void stopRepelling() {
		MCH_Lib.DbgLog(this.worldObj, "MCH_EntityAircraft.stopRepelling()", new Object[0]);
		setRepellingStat(false);
	}

	public static float abs(float p_76135_0_) {
		return p_76135_0_ >= 0.0F ? p_76135_0_ : -p_76135_0_;
	}

	public static double abs(double p_76135_0_) {
		return p_76135_0_ >= 0.0D ? p_76135_0_ : -p_76135_0_;
	}

	public boolean canStartRepelling() {
		if (getAcInfo().haveRepellingHook()) {

			if (isHovering()) {
				if ((abs(getRotPitch()) < 3.0F) && (abs(getRotRoll()) < 3.0F)) {
					Vec3 v = ((Vec3) this.prevPosition.oldest()).addVector(-this.posX, -this.posY, -this.posZ);
					if (v.lengthVector() < 0.3D) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public boolean unmountCrew(boolean unmountParachute) {
		boolean ret = false;
		MCH_SeatInfo[] pos = getSeatsInfo();
		for (int i = 0; i < this.seats.length; i++) {
			if ((this.seats[i] != null) && (this.seats[i].riddenByEntity != null)) {
				Entity entity = this.seats[i].riddenByEntity;
				if ((!(entity instanceof EntityPlayer)) && (!(pos[(i + 1)] instanceof MCH_SeatRackInfo))) {

					if (unmountParachute) {
						if (getSeatIdByEntity(entity) > 1) {
							ret = true;

							Vec3 dropPos = getTransformedPosition(getAcInfo().mobDropOption.pos,
									(Vec3) this.prevPosition.oldest());

							this.seats[i].posX = dropPos.xCoord;
							this.seats[i].posY = dropPos.yCoord;
							this.seats[i].posZ = dropPos.zCoord;
							entity.mountEntity(null);
							entity.posX = dropPos.xCoord;
							entity.posY = dropPos.yCoord;
							entity.posZ = dropPos.zCoord;
							dropEntityParachute(entity);
							break;
						}
					} else {
						ret = true;
						Vec3 dropPos = pos[(i + 1)].pos;
						setUnmountPosition(this.seats[i], pos[(i + 1)].pos);
						entity.mountEntity(null);
						setUnmountPosition(entity, pos[(i + 1)].pos);
					}
				}
			}
		}
		return ret;
	}

	public void setUnmountPosition(Entity rByEntity, Vec3 pos) {
		if (rByEntity != null) {

			MCH_AircraftInfo info = getAcInfo();
			Vec3 v;
			if ((info != null) && (info.unmountPosition != null)) {
				v = getTransformedPosition(info.unmountPosition);
			} else {
				double x = pos.xCoord;
				x = x >= 0.0D ? x + 3.0D : x - 3.0D;
				v = getTransformedPosition(x, 2.0D, pos.zCoord);
			}
			rByEntity.setPosition(v.xCoord, v.yCoord, v.zCoord);
			this.listUnmountReserve.add(new UnmountReserve(rByEntity, v.xCoord, v.yCoord, v.zCoord));
		}
	}

	public boolean unmountEntityFromSeat(Entity entity) {
		if ((entity == null) || (this.seats == null) || (this.seats.length == 0)) {
			return false;
		}
		for (MCH_EntitySeat seat : this.seats) {
			if ((seat != null) && (seat.riddenByEntity != null) && (W_Entity.isEqual(seat.riddenByEntity, entity))) {
				entity.mountEntity(null);
			}
		}

		return false;
	}

	public void ejectSeat(Entity entity) {
		int sid = getSeatIdByEntity(entity);
		if ((sid < 0) || (sid > 1)) {
			return;
		}
		if (getGuiInventory().haveParachute()) {
			if (sid == 0) {
				getGuiInventory().consumeParachute();
				unmountEntity();
				ejectSeatSub(entity, 0);

				entity = getEntityBySeatId(1);
				if ((entity instanceof EntityPlayer)) {
					entity = null;
				}
			}

			if (getGuiInventory().haveParachute()) {
				if (entity != null) {
					getGuiInventory().consumeParachute();
					unmountEntityFromSeat(entity);
					ejectSeatSub(entity, 1);
				}
			}
		}
	}

	public void ejectSeatSub(Entity entity, int sid) {
		Vec3 pos = getSeatInfo(sid) != null ? getSeatInfo(sid).pos : null;
		if (pos != null) {
			Vec3 v = getTransformedPosition(pos.xCoord, pos.yCoord + 2.0D, pos.zCoord);
			entity.setPosition(v.xCoord, v.yCoord, v.zCoord);
		}

		Vec3 v = MCH_Lib.RotVec3(0.0D, 2.0D, 0.0D, -getRotYaw(), -getRotPitch(), -getRotRoll());
		entity.motionX = (this.motionX + v.xCoord + (this.rand.nextFloat() - 0.5D) * 0.1D);
		entity.motionY = (this.motionY + v.yCoord);
		entity.motionZ = (this.motionZ + v.zCoord + (this.rand.nextFloat() - 0.5D) * 0.1D);

		MCH_EntityParachute parachute = new MCH_EntityParachute(this.worldObj, entity.posX, entity.posY, entity.posZ);
		parachute.rotationYaw = entity.rotationYaw;

		parachute.motionX = entity.motionX;
		parachute.motionY = entity.motionY;
		parachute.motionZ = entity.motionZ;
		parachute.fallDistance = entity.fallDistance;

		parachute.user = entity;
		parachute.setType(2);

		this.worldObj.spawnEntityInWorld(parachute);

		if ((getAcInfo().haveCanopy()) && (isCanopyClose())) {
			openCanopy_EjectSeat();
		}

		W_WorldFunc.MOD_playSoundAtEntity(entity, "eject_seat", 5.0F, 1.0F);
	}

	public boolean canEjectSeat(Entity entity) {
		int sid = getSeatIdByEntity(entity);
		if ((sid == 0) && (isUAV()))
			return false;
		return (sid >= 0) && (sid < 2) && (getAcInfo() != null) && (getAcInfo().isEnableEjectionSeat);
	}

	public int getNumEjectionSeat() {
		return 0;
	}

	public int getMountedEntityNum() {
		int num = 0;
		if ((this.riddenByEntity != null) && (!this.riddenByEntity.isDead))
			num++;
		if ((this.seats != null) && (this.seats.length > 0)) {
			for (MCH_EntitySeat seat : this.seats) {
				if ((seat != null) && (seat.riddenByEntity != null) && (!seat.riddenByEntity.isDead)) {
					num++;
				}
			}
		}
		return num;
	}

	public void mountMobToSeats() {
		List list = this.worldObj.getEntitiesWithinAABB(W_Lib.getEntityLivingBaseClass(),
				this.boundingBox.expand(3.0D, 2.0D, 3.0D));

		for (int i = 0; i < list.size(); i++) {
			Entity entity = (Entity) list.get(i);
			if ((!(entity instanceof EntityPlayer)) && (entity.ridingEntity == null)) {
				int sid = 1;
				for (MCH_EntitySeat seat : getSeats()) {
					if ((seat != null) && (seat.riddenByEntity == null) && (!isMountedEntity(entity))
							&& (canRideSeatOrRack(sid, entity))) {

						if ((getSeatInfo(sid) instanceof MCH_SeatRackInfo)) {
							break;
						}

						entity.mountEntity(seat);
					}
					sid++;
				}
			}
		}
	}

	public void mountEntityToRack() {
		if (!MCH_Config.EnablePutRackInFlying.prmBool) {
			if (getCurrentThrottle() > 0.3D)
				return;
			Block block = MCH_Lib.getBlockY(this, 1, -3, true);
			if ((block == null) || (W_Block.isEqual(block, Blocks.air))) {
				return;
			}
		}
		int countRideEntity = 0;
		for (int sid = 0; sid < getSeatNum(); sid++) {
			MCH_EntitySeat seat = getSeat(sid);
			if (((getSeatInfo(1 + sid) instanceof MCH_SeatRackInfo)) && (seat != null)
					&& (seat.riddenByEntity == null)) {

				MCH_SeatRackInfo info = (MCH_SeatRackInfo) getSeatInfo(1 + sid);
				Vec3 v = MCH_Lib.RotVec3(info.getEntryPos().xCoord, info.getEntryPos().yCoord,
						info.getEntryPos().zCoord, -getRotYaw(), -getRotPitch(), -getRotRoll());

				v.xCoord += this.posX;
				v.yCoord += this.posY;
				v.zCoord += this.posZ;

				AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(v.xCoord, v.yCoord, v.zCoord, v.xCoord, v.yCoord,
						v.zCoord);

				float range = info.range;
				List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, bb.expand(range, range, range));

				for (int i = 0; i < list.size(); i++) {
					Entity entity = (Entity) list.get(i);

					if (canRideSeatOrRack(1 + sid, entity)) {

						if ((entity instanceof MCH_IEntityCanRideAircraft)) {
							if (((MCH_IEntityCanRideAircraft) entity).canRideAircraft(this, sid, info)) {
								MCH_Lib.DbgLog(this.worldObj, "MCH_EntityAircraft.mountEntityToRack:%d:%s",
										new Object[] { Integer.valueOf(sid), entity });
								entity.mountEntity(seat);
								countRideEntity++;
								break;
							}
						} else if (entity.ridingEntity == null) {
							NBTTagCompound nbt = entity.getEntityData();
							if ((nbt.hasKey("CanMountEntity")) && (nbt.getBoolean("CanMountEntity"))) {
								MCH_Lib.DbgLog(this.worldObj, "MCH_EntityAircraft.mountEntityToRack:%d:%s:%s",
										new Object[] { Integer.valueOf(sid), entity, entity.getClass() });
								entity.mountEntity(seat);
								countRideEntity++;
								break;
							}
						}
					}
				}
			}
		}
		if (countRideEntity > 0) {
			W_WorldFunc.DEF_playSoundEffect(this.worldObj, this.posX, this.posY, this.posZ, "random.click", 1.0F, 1.0F);
		}
	}

	public void unmountEntityFromRack() {
		for (int sid = getSeatNum() - 1; sid >= 0; sid--) {
			MCH_EntitySeat seat = getSeat(sid);
			if (((getSeatInfo(sid + 1) instanceof MCH_SeatRackInfo)) && (seat != null)
					&& (seat.riddenByEntity != null)) {

				MCH_SeatRackInfo info = (MCH_SeatRackInfo) getSeatInfo(sid + 1);
				Entity entity = seat.riddenByEntity;

				Vec3 pos = info.getEntryPos();
				if ((entity instanceof MCH_EntityAircraft)) {
					if (pos.zCoord >= getAcInfo().bbZ) {
						pos = pos.addVector(0.0D, 0.0D, 12.0D);
					} else {
						pos = pos.addVector(0.0D, 0.0D, -12.0D);
					}
				}
				Vec3 v = MCH_Lib.RotVec3(pos.xCoord, pos.yCoord, pos.zCoord, -getRotYaw(), -getRotPitch(),
						-getRotRoll());
				seat.posX = (entity.posX = this.posX + v.xCoord);
				seat.posY = (entity.posY = this.posY + v.yCoord);
				seat.posZ = (entity.posZ = this.posZ + v.zCoord);

				UnmountReserve ur = new UnmountReserve(entity, entity.posX, entity.posY, entity.posZ);
				ur.cnt = 8;
				this.listUnmountReserve.add(ur);

				entity.mountEntity(null);

				if (MCH_Lib.getBlockIdY(this, 3, -20) > 0) {
					MCH_Lib.DbgLog(this.worldObj, "MCH_EntityAircraft.unmountEntityFromRack:%d:%s",
							new Object[] { Integer.valueOf(sid), entity });
					break;
				}

				MCH_Lib.DbgLog(this.worldObj, "MCH_EntityAircraft.unmountEntityFromRack:%d Parachute:%s",
						new Object[] { Integer.valueOf(sid), entity });
				dropEntityParachute(entity);

				break;
			}
		}
	}

	public void dropEntityParachute(Entity entity) {
		entity.motionX = this.motionX;
		entity.motionY = this.motionY;
		entity.motionZ = this.motionZ;

		MCH_EntityParachute parachute = new MCH_EntityParachute(this.worldObj, entity.posX, entity.posY, entity.posZ);
		parachute.rotationYaw = entity.rotationYaw;

		parachute.motionX = entity.motionX;
		parachute.motionY = entity.motionY;
		parachute.motionZ = entity.motionZ;
		parachute.fallDistance = entity.fallDistance;

		parachute.user = entity;
		parachute.setType(3);

		this.worldObj.spawnEntityInWorld(parachute);
	}

	public void rideRack() {
		if (this.ridingEntity != null) {
			return;
		}

		AxisAlignedBB bb = getBoundingBox();
		List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, bb.expand(60.0D, 60.0D, 60.0D));

		for (int i = 0; i < list.size(); i++) {
			Entity entity = (Entity) list.get(i);
			if ((entity instanceof MCH_EntityAircraft)) {

				MCH_EntityAircraft ac = (MCH_EntityAircraft) entity;

				if (ac.getAcInfo() != null) {

					for (int sid = 0; sid < ac.getSeatNum(); sid++) {
						MCH_SeatInfo seatInfo = ac.getSeatInfo(1 + sid);
						if ((seatInfo instanceof MCH_SeatRackInfo)) {
							if (ac.canRideSeatOrRack(1 + sid, entity)) {

								MCH_SeatRackInfo info = (MCH_SeatRackInfo) seatInfo;
								MCH_EntitySeat seat = ac.getSeat(sid);
								if ((seat != null) && (seat.riddenByEntity == null)) {
									Vec3 v = ac.getTransformedPosition(info.getEntryPos());
									float r = info.range;
									if ((this.posX >= v.xCoord - r) && (this.posX <= v.xCoord + r)
											&& (this.posY >= v.yCoord - r) && (this.posY <= v.yCoord + r)
											&& (this.posZ >= v.zCoord - r) && (this.posZ <= v.zCoord + r)) {

										if (canRideAircraft(ac, sid, info)) {
											W_WorldFunc.DEF_playSoundEffect(this.worldObj, this.posX, this.posY,
													this.posZ, "random.click", 1.0F, 1.0F);

											mountEntity(seat);
											return;
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public boolean canPutToRack() {
		for (int i = 0; i < getSeatNum(); i++) {
			MCH_EntitySeat seat = getSeat(i);
			MCH_SeatInfo seatInfo = getSeatInfo(i + 1);
			if ((seat != null) && (seat.riddenByEntity == null) && ((seatInfo instanceof MCH_SeatRackInfo))) {
				return true;
			}
		}
		return false;
	}

	public boolean canDownFromRack() {
		for (int i = 0; i < getSeatNum(); i++) {
			MCH_EntitySeat seat = getSeat(i);
			MCH_SeatInfo seatInfo = getSeatInfo(i + 1);
			if ((seat != null) && (seat.riddenByEntity != null) && ((seatInfo instanceof MCH_SeatRackInfo))) {
				return true;
			}
		}
		return false;
	}

	public void checkRideRack() {
		if (getCountOnUpdate() % 10 != 0) {
			return;
		}

		this.canRideRackStatus = false;

		if (this.ridingEntity != null) {
			return;
		}

		AxisAlignedBB bb = getBoundingBox();
		List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, bb.expand(60.0D, 60.0D, 60.0D));

		for (int i = 0; i < list.size(); i++) {
			Entity entity = (Entity) list.get(i);
			if ((entity instanceof MCH_EntityAircraft)) {

				MCH_EntityAircraft ac = (MCH_EntityAircraft) entity;

				if (ac.getAcInfo() != null) {

					for (int sid = 0; sid < ac.getSeatNum(); sid++) {
						MCH_SeatInfo seatInfo = ac.getSeatInfo(1 + sid);
						if ((seatInfo instanceof MCH_SeatRackInfo)) {
							MCH_SeatRackInfo info = (MCH_SeatRackInfo) seatInfo;
							MCH_EntitySeat seat = ac.getSeat(sid);
							if ((seat != null) && (seat.riddenByEntity == null)) {
								Vec3 v = ac.getTransformedPosition(info.getEntryPos());
								float r = info.range;
								boolean rx = (this.posX >= v.xCoord - r) && (this.posX <= v.xCoord + r);
								boolean ry = (this.posY >= v.yCoord - r) && (this.posY <= v.yCoord + r);
								boolean rz = (this.posZ >= v.zCoord - r) && (this.posZ <= v.zCoord + r);
								if ((this.posX >= v.xCoord - r) && (this.posX <= v.xCoord + r)
										&& (this.posY >= v.yCoord - r) && (this.posY <= v.yCoord + r)
										&& (this.posZ >= v.zCoord - r) && (this.posZ <= v.zCoord + r)) {

									if (canRideAircraft(ac, sid, info)) {
										this.canRideRackStatus = true;
										return;
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public boolean canRideRack() {
		return (this.ridingEntity == null) && (this.canRideRackStatus);
	}

	public boolean canRideAircraft(MCH_EntityAircraft ac, int seatID, MCH_SeatRackInfo info) {
		if (getAcInfo() == null) {
			return false;
		}
		if (ac.ridingEntity != null)
			return false;
		if (this.ridingEntity != null) {
			return false;
		}
		boolean canRide = false;

		for (String s : info.names) {
			if ((s.equalsIgnoreCase(getAcInfo().name)) || (s.equalsIgnoreCase(getAcInfo().getKindName()))) {
				canRide = true;
				break;
			}
		}
		if (!canRide) {
			for (MCH_AircraftInfo.RideRack rr : getAcInfo().rideRacks) {
				int id = ac.getAcInfo().getNumSeat() - 1 + (rr.rackID - 1);
				if ((id == seatID) && (rr.name.equalsIgnoreCase(ac.getAcInfo().name))) {
					MCH_EntitySeat seat = ac.getSeat(ac.getAcInfo().getNumSeat() - 1 + rr.rackID - 1);
					if ((seat != null) && (seat.riddenByEntity == null)) {
						canRide = true;
						break;
					}
				}
			}
			if (!canRide) {
				return false;
			}
		}

		for (MCH_EntitySeat seat : getSeats()) {
			if ((seat != null) && ((seat.riddenByEntity instanceof MCH_IEntityCanRideAircraft))) {
				return false;
			}
		}
		return true;
	}

	public boolean isMountedEntity(Entity entity) {
		if (entity == null)
			return false;
		return isMountedEntity(W_Entity.getEntityId(entity));
	}

	public EntityPlayer getFirstMountPlayer() {
		if ((getRiddenByEntity() instanceof EntityPlayer)) {
			return (EntityPlayer) getRiddenByEntity();
		}

		for (MCH_EntitySeat seat : getSeats()) {
			if ((seat != null) && ((seat.riddenByEntity instanceof EntityPlayer))) {
				return (EntityPlayer) seat.riddenByEntity;
			}
		}

		return null;
	}

	public boolean isMountedSameTeamEntity(EntityLivingBase player) {
		if ((player == null) || (player.getTeam() == null)) {
			return false;
		}
		if ((this.riddenByEntity instanceof EntityLivingBase)) {
			if (player.isOnSameTeam((EntityLivingBase) this.riddenByEntity)) {
				return true;
			}
		}

		for (MCH_EntitySeat seat : getSeats()) {
			if ((seat != null) && ((seat.riddenByEntity instanceof EntityLivingBase))) {
				if (player.isOnSameTeam((EntityLivingBase) seat.riddenByEntity)) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean isMountedOtherTeamEntity(EntityLivingBase player) {
		if (player == null)
			return false;
		EntityLivingBase target = null;

		if ((this.riddenByEntity instanceof EntityLivingBase)) {
			target = (EntityLivingBase) this.riddenByEntity;
			if ((player.getTeam() != null) && (target.getTeam() != null) && (!player.isOnSameTeam(target))) {
				return true;
			}
		}

		for (MCH_EntitySeat seat : getSeats()) {
			if ((seat != null) && ((seat.riddenByEntity instanceof EntityLivingBase))) {
				target = (EntityLivingBase) seat.riddenByEntity;
				if ((player.getTeam() != null) && (target.getTeam() != null) && (!player.isOnSameTeam(target))) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean isMountedEntity(int entityId) {
		if (W_Entity.getEntityId(this.riddenByEntity) == entityId) {
			return true;
		}

		for (MCH_EntitySeat seat : getSeats()) {
			if ((seat != null) && (seat.riddenByEntity != null)
					&& (W_Entity.getEntityId(seat.riddenByEntity) == entityId)) {
				return true;
			}
		}

		return false;
	}

	public void onInteractFirst(EntityPlayer player) {
	}

	public boolean checkTeam(EntityPlayer player) {
		for (int i = 0; i < 1 + getSeatNum(); i++) {
			Entity entity = getEntityBySeatId(i);
			if (((entity instanceof EntityPlayer)) || ((entity instanceof MCH_EntityGunner))) {
				EntityLivingBase riddenEntity = (EntityLivingBase) entity;
				if ((riddenEntity.getTeam() != null) && (!riddenEntity.isOnSameTeam(player))) {
					return false;
				}
			}
		}
		return true;
	}

	private boolean switchSeat = false;

	public boolean interactFirst(EntityPlayer player, boolean ss) {
		this.switchSeat = ss;
		boolean ret = interactFirst(player);
		this.switchSeat = false;
		return ret;
	}

	public boolean interactFirst(EntityPlayer player) {
		if (isDestroyed()) {
			return false;
		}
		if (getAcInfo() == null) {
			return false;
		}
		if (!checkTeam(player)) {
			return false;
		}
		ItemStack itemStack = player.getCurrentEquippedItem();
		if ((itemStack != null) && ((itemStack.getItem() instanceof MCH_ItemWrench))) {

			if ((!this.worldObj.isRemote) && (player.isSneaking())) {
				switchNextTextureName();
			}
			return false;
		}
		if ((itemStack != null) && ((itemStack.getItem() instanceof MCH_ItemSpawnGunner))) {
			return false;
		}

		if (player.isSneaking()) {
			super.openInventory(player);
			return false;
		}

		if (!getAcInfo().canRide) {
			return false;
		}

		if ((this.riddenByEntity != null) || (isUAV())) {

			return interactFirstSeat(player);
		}

		if ((player.ridingEntity instanceof MCH_EntitySeat)) {
			return false;
		}

		if (!canRideSeatOrRack(0, player)) {
			return false;
		}

		if (!this.switchSeat) {
			if ((getAcInfo().haveCanopy()) && (isCanopyClose())) {
				openCanopy();
				return false;
			}

			if (getModeSwitchCooldown() > 0) {
				return false;
			}
		}

		closeCanopy();

		this.riddenByEntity = null;
		this.lastRiddenByEntity = null;

		initRadar();

		if (!this.worldObj.isRemote) {
			player.mountEntity(this);

			if (!this.keepOnRideRotation) {

				mountMobToSeats();
			}
		} else {
			updateClientSettings(0);
		}

		setCameraId(0);

		initPilotWeapon();

		this.lowPassPartialTicks.clear();

		if (getAcInfo().name.equalsIgnoreCase("uh-1c")) {
			MCH_Achievement.addStat(this.riddenByEntity, MCH_Achievement.rideValkyries, 1);
		}

		onInteractFirst(player);

		return true;
	}

	public boolean canRideSeatOrRack(int seatId, Entity entity) {
		if (getAcInfo() == null)
			return false;
		for (Integer[] a : getAcInfo().exclusionSeatList) {
			if (Arrays.asList(a).contains(Integer.valueOf(seatId))) {
				Integer[] arr$ = a;
				int len$ = arr$.length;
				for (int i$ = 0; i$ < len$; i$++) {
					int id = arr$[i$].intValue();

					if (getEntityBySeatId(id) != null) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public void updateClientSettings(int seatId) {
		this.cs_dismountAll = MCH_Config.DismountAll.prmBool;
		this.cs_heliAutoThrottleDown = MCH_Config.AutoThrottleDownHeli.prmBool;
		this.cs_planeAutoThrottleDown = MCH_Config.AutoThrottleDownPlane.prmBool;
		this.cs_tankAutoThrottleDown = MCH_Config.AutoThrottleDownTank.prmBool;
		this.camera.setShaderSupport(seatId, Boolean.valueOf(W_EntityRenderer.isShaderSupport()));
		MCH_PacketNotifyClientSetting.send();
	}

	public boolean canLockEntity(Entity entity) {
		return !isMountedEntity(entity);
	}

	public void switchNextSeat(Entity entity) {
		if (entity == null)
			return;
		if ((this.seats == null) || (this.seats.length <= 0))
			return;
		if (!isMountedEntity(entity)) {
			return;
		}

		boolean isFound = false;
		int sid = 1;
		for (MCH_EntitySeat seat : this.seats) {
			if (seat != null) {
				if ((getSeatInfo(sid) instanceof MCH_SeatRackInfo))
					break;
				if (W_Entity.isEqual(seat.riddenByEntity, entity)) {
					isFound = true;
				} else if ((isFound) && (seat.riddenByEntity == null)) {
					entity.mountEntity(seat);
					return;
				}
				sid++;
			}
		}

		sid = 1;
		for (MCH_EntitySeat seat : this.seats) {
			if ((seat != null) && (seat.riddenByEntity == null)) {
				if ((getSeatInfo(sid) instanceof MCH_SeatRackInfo))
					break;
				entity.mountEntity(seat);
				onMountPlayerSeat(seat, entity);
				return;
			}
			sid++;
		}
	}

	public void switchPrevSeat(Entity entity) {
		if (entity == null)
			return;
		if ((this.seats == null) || (this.seats.length <= 0))
			return;
		if (!isMountedEntity(entity)) {
			return;
		}

		boolean isFound = false;
		for (int i = this.seats.length - 1; i >= 0; i--) {
			MCH_EntitySeat seat = this.seats[i];
			if (seat != null) {
				if (W_Entity.isEqual(seat.riddenByEntity, entity)) {
					isFound = true;
				} else if ((isFound) && (seat.riddenByEntity == null)) {
					entity.mountEntity(seat);

					return;
				}
			}
		}

		for (int i = this.seats.length - 1; i >= 0; i--) {
			MCH_EntitySeat seat = this.seats[i];
			if ((!(getSeatInfo(i + 1) instanceof MCH_SeatRackInfo)) && (seat != null)
					&& (seat.riddenByEntity == null)) {
				entity.mountEntity(seat);

				return;
			}
		}
	}

	public Entity[] getParts() {
		return this.partEntities;
	}

	public float getSoundVolume() {
		return 1.0F;
	}

	public float getSoundPitch() {
		return 1.0F;
	}

	public abstract String getDefaultSoundName();

	public String getSoundName() {
		if (getAcInfo() == null)
			return "";
		return !getAcInfo().soundMove.isEmpty() ? getAcInfo().soundMove : getDefaultSoundName();
	}

	public boolean isSkipNormalRender() {
		return this.ridingEntity instanceof MCH_EntitySeat;
	}

	public boolean isRenderBullet(Entity entity, Entity rider) {
		if (isCameraView(rider)) {
			if ((W_Entity.isEqual(getTVMissile(), entity))
					&& (W_Entity.isEqual(getTVMissile().shootingEntity, rider))) {
				return false;
			}
		}
		return true;
	}

	public boolean isCameraView(Entity entity) {
		return (getIsGunnerMode(entity)) || (isUAV());
	}

	public void updateCamera(double x, double y, double z) {
		if (!this.worldObj.isRemote)
			return;
		if (getTVMissile() != null) {
			this.camera.setPosition(this.TVmissile.posX, this.TVmissile.posY, this.TVmissile.posZ);
			this.camera.setCameraZoom(1.0F);
			this.TVmissile.isSpawnParticle = (!isMissileCameraMode(this.TVmissile.shootingEntity));
		} else {
			setTVMissile(null);

			MCH_AircraftInfo.CameraPosition cpi = getCameraPosInfo();
			Vec3 cp = cpi != null ? cpi.pos : Vec3.createVectorHelper(0.0D, 0.0D, 0.0D);
			Vec3 v = MCH_Lib.RotVec3(cp, -getRotYaw(), -getRotPitch(), -getRotRoll());
			this.camera.setPosition(x + v.xCoord, y + v.yCoord, z + v.zCoord);
		}
	}

	public void updateCameraRotate(float yaw, float pitch) {
		this.camera.prevRotationYaw = this.camera.rotationYaw;
		this.camera.prevRotationPitch = this.camera.rotationPitch;
		this.camera.rotationYaw = yaw;
		this.camera.rotationPitch = pitch;
	}

	public void updatePartCameraRotate() {
		if (this.worldObj.isRemote) {
			Entity e = getEntityBySeatId(1);
			if (e == null) {
				e = getRiddenByEntity();
			}

			if (e != null) {
				this.camera.partRotationYaw = e.rotationYaw;

				float pitch = e.rotationPitch;
				this.camera.prevPartRotationYaw = this.camera.partRotationYaw;
				this.camera.prevPartRotationPitch = this.camera.partRotationPitch;

				this.camera.partRotationPitch = pitch;
			}
		}
	}

	public void setTVMissile(MCH_EntityTvMissile entity) {
		this.TVmissile = entity;
	}

	public MCH_EntityTvMissile getTVMissile() {
		return (this.TVmissile != null) && (!this.TVmissile.isDead) ? this.TVmissile : null;
	}

	public MCH_WeaponSet[] createWeapon(int seat_num) {
		this.currentWeaponID = new int[seat_num];
		for (int i = 0; i < this.currentWeaponID.length; i++) {
			this.currentWeaponID[i] = -1;
		}
		if ((getAcInfo() == null) || (getAcInfo().weaponSetList.size() <= 0) || (seat_num <= 0)) {
			return new MCH_WeaponSet[] { this.dummyWeapon };
		}

		MCH_WeaponSet[] weaponSetArray = new MCH_WeaponSet[getAcInfo().weaponSetList.size()];
		for (int i = 0; i < getAcInfo().weaponSetList.size(); i++) {
			MCH_AircraftInfo.WeaponSet ws = (MCH_AircraftInfo.WeaponSet) getAcInfo().weaponSetList.get(i);
			MCH_WeaponBase[] wb = new MCH_WeaponBase[ws.weapons.size()];
			for (int j = 0; j < ws.weapons.size(); j++) {
				wb[j] = MCH_WeaponCreator.createWeapon(this.worldObj, ws.type,
						((MCH_AircraftInfo.Weapon) ws.weapons.get(j)).pos,
						((MCH_AircraftInfo.Weapon) ws.weapons.get(j)).yaw,
						((MCH_AircraftInfo.Weapon) ws.weapons.get(j)).pitch, this,
						((MCH_AircraftInfo.Weapon) ws.weapons.get(j)).turret);

				wb[j].aircraft = this;
			}
			if ((wb.length > 0) && (wb[0] != null)) {
				float defYaw = ((MCH_AircraftInfo.Weapon) ws.weapons.get(0)).defaultYaw;
				weaponSetArray[i] = new MCH_WeaponSet(wb);
				weaponSetArray[i].prevRotationYaw = defYaw;
				weaponSetArray[i].rotationYaw = defYaw;
				weaponSetArray[i].defaultRotationYaw = defYaw;
			}
		}

		return weaponSetArray;
	}

	public void switchWeapon(Entity entity, int id) {
		int sid = getSeatIdByEntity(entity);
		if (!isValidSeatID(sid)) {
			return;
		}
		int beforeWeaponID = this.currentWeaponID[sid];

		if ((getWeaponNum() <= 0) || (this.currentWeaponID.length <= 0))
			return;
		if (id < 0) {
			this.currentWeaponID[sid] = -1;
		}
		if (id >= getWeaponNum()) {
			id = getWeaponNum() - 1;
		}
		MCH_Lib.DbgLog(this.worldObj, "switchWeapon:" + W_Entity.getEntityId(entity) + " -> " + id, new Object[0]);

		getCurrentWeapon(entity).reload();
		this.currentWeaponID[sid] = id;
		MCH_WeaponSet ws = getCurrentWeapon(entity);
		ws.onSwitchWeapon(this.worldObj.isRemote, isInfinityAmmo(entity));

		if (!this.worldObj.isRemote) {
			MCH_PacketNotifyWeaponID.send(this, sid, id, ws.getAmmoNum(), ws.getRestAllAmmoNum());
		}
	}

	public void updateWeaponID(int sid, int id) {
		if ((sid < 0) || (sid >= this.currentWeaponID.length)) {
			return;
		}
		if ((getWeaponNum() <= 0) || (this.currentWeaponID.length <= 0))
			return;
		if (id < 0) {
			this.currentWeaponID[sid] = -1;
		}
		if (id >= getWeaponNum()) {
			id = getWeaponNum() - 1;
		}
		MCH_Lib.DbgLog(this.worldObj, "switchWeapon:seatID=" + sid + ", WeaponID=" + id, new Object[0]);

		this.currentWeaponID[sid] = id;
	}

	public void updateWeaponRestAmmo(int id, int num) {
		if (id < getWeaponNum()) {
			getWeapon(id).setRestAllAmmoNum(num);
		}
	}

	public MCH_WeaponSet getWeaponByName(String name) {
		for (MCH_WeaponSet ws : this.weapons) {
			if (ws.isEqual(name))
				return ws;
		}
		return null;
	}

	public int getWeaponIdByName(String name) {
		int id = 0;
		for (MCH_WeaponSet ws : this.weapons) {
			if (ws.isEqual(name))
				return id;
			id++;
		}
		return -1;
	}

	public void reloadAllWeapon() {
		for (int i = 0; i < getWeaponNum(); i++) {
			getWeapon(i).reloadMag();
		}
	}

	public MCH_WeaponSet getFirstSeatWeapon() {
		if ((this.currentWeaponID != null) && (this.currentWeaponID.length > 0) && (this.currentWeaponID[0] >= 0)) {

			return getWeapon(this.currentWeaponID[0]);
		}
		return getWeapon(0);
	}

	public void initCurrentWeapon(Entity entity) {
		int sid = getSeatIdByEntity(entity);
		MCH_Lib.DbgLog(this.worldObj, "initCurrentWeapon:" + W_Entity.getEntityId(entity) + ":%d",
				new Object[] { Integer.valueOf(sid) });
		if ((sid < 0) || (sid >= this.currentWeaponID.length)) {
			return;
		}
		this.currentWeaponID[sid] = -1;

		if (((entity instanceof EntityPlayer)) || ((entity instanceof MCH_EntityGunner))) {
			this.currentWeaponID[sid] = getNextWeaponID(entity, 1);
			switchWeapon(entity, getCurrentWeaponID(entity));

			if (this.worldObj.isRemote) {
				MCH_PacketIndNotifyAmmoNum.send(this, -1);
			}
		}
	}

	public void initPilotWeapon() {
		this.currentWeaponID[0] = -1;
	}

	public MCH_WeaponSet getCurrentWeapon(Entity entity) {
		return getWeapon(getCurrentWeaponID(entity));
	}

	protected MCH_WeaponSet getWeapon(int id) {
		if ((id < 0) || (this.weapons.length <= 0) || (id >= this.weapons.length)) {
			return this.dummyWeapon;
		}
		return this.weapons[id];
	}

	public int getWeaponIDBySeatID(int sid) {
		if ((sid < 0) || (sid >= this.currentWeaponID.length)) {
			return -1;
		}
		return this.currentWeaponID[sid];
	}

	public double getLandInDistance(Entity user) {
		if ((this.lastCalcLandInDistanceCount != getCountOnUpdate()) && (getCountOnUpdate() % 5 == 0)) {
			this.lastCalcLandInDistanceCount = getCountOnUpdate();

			MCH_WeaponParam prm = new MCH_WeaponParam();
			prm.setPosition(this.posX, this.posY, this.posZ);
			prm.entity = this;
			prm.user = user;
			prm.isInfinity = isInfinityAmmo(prm.user);

			if (prm.user != null) {
				MCH_WeaponSet currentWs = getCurrentWeapon(prm.user);
				if (currentWs != null) {
					int sid = getSeatIdByEntity(prm.user);
					if (getAcInfo().getWeaponSetById(sid) != null) {
						prm.isTurret = ((MCH_AircraftInfo.Weapon) getAcInfo().getWeaponSetById(sid).weapons
								.get(0)).turret;
					}
					this.lastLandInDistance = currentWs.getLandInDistance(prm);
				}
			}
		}

		return this.lastLandInDistance;
	}

	public boolean useCurrentWeapon(Entity user) {
		MCH_WeaponParam prm = new MCH_WeaponParam();
		prm.setPosition(this.posX, this.posY, this.posZ);
		prm.entity = this;
		prm.user = user;
		return useCurrentWeapon(prm);
	}

	public boolean useCurrentWeapon(MCH_WeaponParam prm) {
		prm.isInfinity = isInfinityAmmo(prm.user);

		if (prm.user != null) {
			MCH_WeaponSet currentWs = getCurrentWeapon(prm.user);
			if ((currentWs != null) && (currentWs.canUse())) {
				int sid = getSeatIdByEntity(prm.user);
				if (getAcInfo().getWeaponSetById(sid) != null) {
					prm.isTurret = ((MCH_AircraftInfo.Weapon) getAcInfo().getWeaponSetById(sid).weapons.get(0)).turret;
				}
				int lastUsedIndex = currentWs.getCurrentWeaponIndex();
				if (currentWs.use(prm)) {
					for (MCH_WeaponSet ws : this.weapons) {
						if ((ws != currentWs) && (!ws.getInfo().group.isEmpty())
								&& (ws.getInfo().group.equals(currentWs.getInfo().group))) {
							ws.waitAndReloadByOther(prm.reload);
						}
					}

					if (!this.worldObj.isRemote) {
						int shift = 0;
						for (MCH_WeaponSet ws : this.weapons) {
							if (ws == currentWs)
								break;
							shift += ws.getWeaponNum();
						}
						shift += lastUsedIndex;
						this.useWeaponStat |= (shift < 32 ? 1 << shift : 0);
					}
					return true;
				}
			}
		}
		return false;
	}

	public void switchCurrentWeaponMode(Entity entity) {
		getCurrentWeapon(entity).switchMode();
	}

	public int getWeaponNum() {
		return this.weapons.length;
	}

	public int getCurrentWeaponID(Entity entity) {
		if ((!(entity instanceof EntityPlayer)) && (!(entity instanceof MCH_EntityGunner)))
			return -1;
		int id = getSeatIdByEntity(entity);
		return (id >= 0) && (id < this.currentWeaponID.length) ? this.currentWeaponID[id] : -1;
	}

	public int getNextWeaponID(Entity entity, int step) {
		if (getAcInfo() == null)
			return -1;
		int sid = getSeatIdByEntity(entity);
		if (sid < 0)
			return -1;
		int id = getCurrentWeaponID(entity);
		int i;
		for (i = 0; i < getWeaponNum(); i++) {
			if (step >= 0) {
				id = (id + 1) % getWeaponNum();
			} else {
				id = id > 0 ? id - 1 : getWeaponNum() - 1;
			}
			MCH_AircraftInfo.Weapon w = getAcInfo().getWeaponById(id);
			if (w != null) {
				MCH_WeaponInfo wi = getWeaponInfoById(id);
				int wpsid = getWeaponSeatID(wi, w);
				if (wpsid < getSeatNum() + 1 + 1) {

					if (wpsid == sid) {
						break;
					}

					if ((sid == 0) && (w.canUsePilot) && (!(getEntityBySeatId(wpsid) instanceof EntityPlayer))
							&& (!(getEntityBySeatId(wpsid) instanceof MCH_EntityGunner)))
						break;
				}
			}
		}
		if (i >= getWeaponNum()) {
			return -1;
		}
		MCH_Lib.DbgLog(this.worldObj, "getNextWeaponID:%d:->%d",
				new Object[] { Integer.valueOf(W_Entity.getEntityId(entity)), Integer.valueOf(id) });

		return id;
	}

	public int getWeaponSeatID(MCH_WeaponInfo wi, MCH_AircraftInfo.Weapon w) {
		if ((wi != null) && ((wi.target & 0xC3) == 0) && (wi.type.isEmpty())) {
			if ((MCH_MOD.proxy.isSinglePlayer()) || (MCH_Config.TestMode.prmBool)) {
				return 1000;
			}
		}
		return w.seatID;
	}

	public boolean isMissileCameraMode(Entity entity) {
		return (getTVMissile() != null) && (isCameraView(entity));
	}

	public boolean isPilotReloading() {
		return (getCommonStatus(2)) || (this.supplyAmmoWait > 0);
	}

	public int getUsedWeaponStat() {
		if (getAcInfo() == null)
			return 0;
		if (getAcInfo().getWeaponNum() <= 0) {
			return 0;
		}
		int stat = 0;
		int i = 0;
		for (MCH_WeaponSet w : this.weapons) {
			if (i >= 32)
				break;
			for (int wi = 0; wi < w.getWeaponNum(); wi++) {
				if (i >= 32)
					break;
				stat |= (w.isUsed(wi) ? 1 << i : 0);
				i++;
			}
		}
		return stat;
	}

	public boolean isWeaponNotCooldown(MCH_WeaponSet checkWs, int index) {
		if (getAcInfo() == null)
			return false;
		if (getAcInfo().getWeaponNum() <= 0)
			return false;
		int shift = 0;
		for (MCH_WeaponSet ws : this.weapons) {
			if (ws == checkWs) {
				break;
			}

			shift += ws.getWeaponNum();
		}
		shift += index;
		return (this.useWeaponStat & 1 << shift) != 0;
	}

	public void updateWeapons() {
		if (getAcInfo() == null)
			return;
		if (getAcInfo().getWeaponNum() <= 0) {
			return;
		}
		int prevUseWeaponStat = this.useWeaponStat;
		if (!this.worldObj.isRemote) {

			this.useWeaponStat |= getUsedWeaponStat();
			getDataWatcher().updateObject(24, new Integer(this.useWeaponStat));
			this.useWeaponStat = 0;

		} else {
			this.useWeaponStat = getDataWatcher().getWatchableObjectInt(24);
		}

		float yaw = MathHelper.wrapAngleTo180_float(getRotYaw());
		float pitch = MathHelper.wrapAngleTo180_float(getRotPitch());

		int id = 0;
		for (int wid = 0; wid < this.weapons.length; wid++) {
			MCH_WeaponSet w = this.weapons[wid];

			boolean isLongDelay = false;
			if (w.getFirstWeapon() != null) {
				isLongDelay = w.isLongDelayWeapon();
			}

			boolean isSelected = false;
			for (int swid : this.currentWeaponID) {
				if (swid == wid) {
					isSelected = true;
					break;
				}
			}

			boolean isWpnUsed = false;
			for (int index = 0; index < w.getWeaponNum(); index++) {
				boolean isPrevUsed = (id < 32) && ((prevUseWeaponStat & 1 << id) != 0);
				boolean isUsed = (id < 32) && ((this.useWeaponStat & 1 << id) != 0);
				if ((isLongDelay) && (isPrevUsed) && (isUsed)) {

					isUsed = false;
				}
				isWpnUsed |= isUsed;

				if ((!isPrevUsed) && (isUsed == true)) {
					float recoil = w.getInfo().recoil;
					if (recoil > 0.0F) {
						this.recoilCount = 30;
						this.recoilValue = recoil;
						this.recoilYaw = w.rotationYaw;
					}
				}

				if ((this.worldObj.isRemote) && (isUsed)) {
					Vec3 wrv = MCH_Lib.RotVec3(0.0D, 0.0D, -1.0D, -w.rotationYaw - yaw, -w.rotationPitch);
					Vec3 spv = w.getCurrentWeapon().getShotPos(this);
					spawnParticleMuzzleFlash(this.worldObj, w.getInfo(), this.posX + spv.xCoord, this.posY + spv.yCoord,
							this.posZ + spv.zCoord, wrv);
				}

				w.updateWeapon(this, isUsed, index);
				id++;
			}
			w.update(this, isSelected, isWpnUsed);

			MCH_AircraftInfo.Weapon wi = getAcInfo().getWeaponById(wid);
			if ((wi != null) && (!isDestroyed())) {
				Entity entity = getEntityBySeatId(getWeaponSeatID(getWeaponInfoById(wid), wi));
				if ((wi.canUsePilot) && (!(entity instanceof EntityPlayer))
						&& (!(entity instanceof MCH_EntityGunner))) {
					entity = getEntityBySeatId(0);
				}
				if (((entity instanceof EntityPlayer)) || ((entity instanceof MCH_EntityGunner))) {
					if (((int) wi.minYaw != 0) || ((int) wi.maxYaw != 0)) {
						float ty = wi.turret ? MathHelper.wrapAngleTo180_float(getLastRiderYaw()) - yaw : 0.0F;
						float ey = MathHelper.wrapAngleTo180_float(entity.rotationYaw - yaw - wi.defaultYaw - ty);
						if ((Math.abs((int) wi.minYaw) < 360) && (Math.abs((int) wi.maxYaw) < 360)) {
							float targetYaw = MCH_Lib.RNG(ey, wi.minYaw, wi.maxYaw);

							float wy = w.rotationYaw - wi.defaultYaw - ty;

							if (targetYaw < wy) {
								if (wy - targetYaw > 15.0F)
									wy -= 15.0F;
								else {
									wy = targetYaw;
								}
							} else if (targetYaw > wy) {
								if (targetYaw - wy > 15.0F)
									wy += 15.0F;
								else
									wy = targetYaw;
							}
							w.rotationYaw = (wy + wi.defaultYaw + ty);
						} else {
							w.rotationYaw = (ey + ty);
						}
					}

					float ep = MathHelper.wrapAngleTo180_float(entity.rotationPitch - pitch);
					w.rotationPitch = MCH_Lib.RNG(ep, wi.minPitch, wi.maxPitch);

					w.rotationTurretYaw = 0.0F;
				} else {
					w.rotationTurretYaw = (getLastRiderYaw() - getRotYaw());
					if ((getTowedChainEntity() != null) || (this.ridingEntity != null)) {
						w.rotationYaw = 0.0F;
					}
				}
			}
		}

		updateWeaponBay();

		if (this.hitStatus > 0)
			this.hitStatus -= 1;
	}

	public void updateWeaponsRotation() {
		if (getAcInfo() == null)
			return;
		if (getAcInfo().getWeaponNum() <= 0)
			return;
		if (isDestroyed()) {
			return;
		}
		float yaw = MathHelper.wrapAngleTo180_float(getRotYaw());
		float pitch = MathHelper.wrapAngleTo180_float(getRotPitch());

		for (int wid = 0; wid < this.weapons.length; wid++) {
			MCH_WeaponSet w = this.weapons[wid];

			MCH_AircraftInfo.Weapon wi = getAcInfo().getWeaponById(wid);
			if (wi != null) {
				Entity entity = getEntityBySeatId(getWeaponSeatID(getWeaponInfoById(wid), wi));
				if ((wi.canUsePilot) && (!(entity instanceof EntityPlayer))
						&& (!(entity instanceof MCH_EntityGunner))) {
					entity = getEntityBySeatId(0);
				}
				if (((entity instanceof EntityPlayer)) || ((entity instanceof MCH_EntityGunner))) {
					if (((int) wi.minYaw != 0) || ((int) wi.maxYaw != 0)) {
						float ty = wi.turret ? MathHelper.wrapAngleTo180_float(getLastRiderYaw()) - yaw : 0.0F;
						float ey = MathHelper.wrapAngleTo180_float(entity.rotationYaw - yaw - wi.defaultYaw - ty);
						if ((Math.abs((int) wi.minYaw) < 360) && (Math.abs((int) wi.maxYaw) < 360)) {
							float targetYaw = MCH_Lib.RNG(ey, wi.minYaw, wi.maxYaw);

							float wy = w.rotationYaw - wi.defaultYaw - ty;

							if (targetYaw < wy) {
								if (wy - targetYaw > 15.0F)
									wy -= 15.0F;
								else {
									wy = targetYaw;
								}
							} else if (targetYaw > wy) {
								if (targetYaw - wy > 15.0F)
									wy += 15.0F;
								else
									wy = targetYaw;
							}
							w.rotationYaw = (wy + wi.defaultYaw + ty);
						} else {
							w.rotationYaw = (ey + ty);
						}
					}

					float ep = MathHelper.wrapAngleTo180_float(entity.rotationPitch - pitch);
					w.rotationPitch = MCH_Lib.RNG(ep, wi.minPitch, wi.maxPitch);

					w.rotationTurretYaw = 0.0F;
				} else {
					w.rotationTurretYaw = (getLastRiderYaw() - getRotYaw());
				}
			}

			w.prevRotationYaw = w.rotationYaw;
		}
	}

	private void spawnParticleMuzzleFlash(World w, MCH_WeaponInfo wi, double px, double py, double pz, Vec3 wrv) {
		if (wi.listMuzzleFlashSmoke != null) {
			for (MCH_WeaponInfo.MuzzleFlash mf : wi.listMuzzleFlashSmoke) {
				double x = px + -wrv.xCoord * mf.dist;
				double y = py + -wrv.yCoord * mf.dist;
				double z = pz + -wrv.zCoord * mf.dist;

				MCH_ParticleParam p = new MCH_ParticleParam(w, "smoke", px, py, pz);
				p.size = mf.size;
				for (int i = 0; i < mf.num; i++) {
					p.a = (mf.a * 0.9F + w.rand.nextFloat() * 0.1F);

					float color = w.rand.nextFloat() * 0.1F;
					p.r = (color + mf.r * 0.9F);
					p.g = (color + mf.g * 0.9F);
					p.b = (color + mf.b * 0.9F);

					p.age = ((int) (mf.age + 0.1D * mf.age * w.rand.nextFloat()));
					p.posX = (x + (w.rand.nextDouble() - 0.5D) * mf.range);
					p.posY = (y + (w.rand.nextDouble() - 0.5D) * mf.range);
					p.posZ = (z + (w.rand.nextDouble() - 0.5D) * mf.range);

					p.motionX = (w.rand.nextDouble() * (p.posX < x ? -0.2D : 0.2D));
					p.motionY = (w.rand.nextDouble() * (p.posY < y ? -0.03D : 0.03D));
					p.motionZ = (w.rand.nextDouble() * (p.posZ < z ? -0.2D : 0.2D));

					MCH_ParticlesUtil.spawnParticle(p);
				}
			}
		}

		if (wi.listMuzzleFlash != null) {
			for (MCH_WeaponInfo.MuzzleFlash mf : wi.listMuzzleFlash) {
				float color = this.rand.nextFloat() * 0.1F + 0.9F;
				MCH_ParticlesUtil.spawnParticleExplode(this.worldObj, px + -wrv.xCoord * mf.dist,
						py + -wrv.yCoord * mf.dist, pz + -wrv.zCoord * mf.dist, mf.size, color * mf.r, color * mf.g,
						color * mf.b, mf.a, mf.age + w.rand.nextInt(3));
			}
		}
	}

	private void updateWeaponBay() {
		for (int i = 0; i < this.weaponBays.length; i++) {
			WeaponBay wb = this.weaponBays[i];
			MCH_AircraftInfo.WeaponBay info = (MCH_AircraftInfo.WeaponBay) getAcInfo().partWeaponBay.get(i);
			boolean isSelected = false;
			Integer[] arr$ = info.weaponIds;
			int len$ = arr$.length;
			for (int i$ = 0; i$ < len$; i$++) {
				int wid = arr$[i$].intValue();

				for (int sid = 0; sid < this.currentWeaponID.length; sid++) {
					if ((wid == this.currentWeaponID[sid]) && (getEntityBySeatId(sid) != null)) {
						isSelected = true;
					}
				}
			}
			wb.prevRot = wb.rot;
			if (isSelected) {
				if (wb.rot < 90.0F) {
					wb.rot += 3.0F;
				}
				if (wb.rot >= 90.0F) {
					wb.rot = 90.0F;
				}
			} else {
				if (wb.rot > 0.0F) {
					wb.rot -= 3.0F;
				}
				if (wb.rot <= 0.0F) {
					wb.rot = 0.0F;
				}
			}
		}
	}

	public int getHitStatus() {
		return this.hitStatus;
	}

	public int getMaxHitStatus() {
		return 15;
	}

	public void hitBullet() {
		this.hitStatus = getMaxHitStatus();
	}

	public void initRotationYaw(float yaw) {
		this.rotationYaw = yaw;
		this.prevRotationYaw = yaw;
		this.lastRiderYaw = yaw;
		this.lastSearchLightYaw = yaw;

		for (MCH_WeaponSet w : this.weapons) {
			w.rotationYaw = w.defaultRotationYaw;
			w.rotationPitch = 0.0F;
		}
	}

	public MCH_AircraftInfo getAcInfo() {
		return this.acInfo;
	}

	public abstract Item getItem();

	public void setAcInfo(MCH_AircraftInfo info) {
		this.acInfo = info;
		if (info != null) {
			this.partHatch = createHatch();
			this.partCanopy = createCanopy();
			this.partLandingGear = createLandingGear();

			this.weaponBays = createWeaponBays();

			this.rotPartRotation = new float[info.partRotPart.size()];
			this.prevRotPartRotation = new float[info.partRotPart.size()];

			this.extraBoundingBox = createExtraBoundingBox();

			this.partEntities = createParts();

			this.stepHeight = info.stepHeight;
		}
	}

	public MCH_BoundingBox[] createExtraBoundingBox() {
		MCH_BoundingBox[] ar = new MCH_BoundingBox[getAcInfo().extraBoundingBox.size()];

		int i = 0;
		for (MCH_BoundingBox bb : getAcInfo().extraBoundingBox) {
			ar[i] = bb.copy();
			i++;
		}

		return ar;
	}

	public Entity[] createParts() {
		Entity[] list = new Entity[1];
		list[0] = this.partEntities[0];

		return list;
	}

	public void updateUAV() {
		if (!isUAV()) {
			return;
		}

		if (this.worldObj.isRemote) {
			int eid = getDataWatcher().getWatchableObjectInt(22);
			if (eid > 0) {
				if (this.uavStation == null) {
					Entity uavEntity = this.worldObj.getEntityByID(eid);
					if ((uavEntity instanceof MCH_EntityUavStation)) {
						this.uavStation = ((MCH_EntityUavStation) uavEntity);
						this.uavStation.setControlAircract(this);
					}

				}

			} else if (this.uavStation != null) {
				this.uavStation.setControlAircract(null);
				this.uavStation = null;

			}

		} else if (this.uavStation != null) {
			double udx = this.posX - this.uavStation.posX;
			double udz = this.posZ - this.uavStation.posZ;
			if (udx * udx + udz * udz > 15129.0D) {
				this.uavStation.setControlAircract(null);
				setUavStation(null);
				attackEntityFrom(DamageSource.outOfWorld, getMaxHP() + 10);
			}
		}

		if ((this.uavStation != null) && (this.uavStation.isDead)) {
			this.uavStation = null;
		}
	}

	public void switchGunnerMode(boolean mode) {
		boolean debug_bk_mode = this.isGunnerMode;
		Entity pilot = getEntityBySeatId(0);

		if ((!mode) || (canSwitchGunnerMode())) {
			if ((this.isGunnerMode == true) && (!mode)) {
				setCurrentThrottle(this.beforeHoverThrottle);
				this.isGunnerMode = false;
				this.camera.setCameraZoom(1.0F);
				getCurrentWeapon(pilot).onSwitchWeapon(this.worldObj.isRemote, isInfinityAmmo(pilot));
			} else if ((!this.isGunnerMode) && (mode == true)) {
				this.beforeHoverThrottle = getCurrentThrottle();
				this.isGunnerMode = true;
				this.camera.setCameraZoom(1.0F);
				getCurrentWeapon(pilot).onSwitchWeapon(this.worldObj.isRemote, isInfinityAmmo(pilot));
			}
		}

		MCH_Lib.DbgLog(this.worldObj, "switchGunnerMode %s->%s",
				new Object[] { debug_bk_mode ? "ON" : "OFF", mode ? "ON" : "OFF" });
	}

	public boolean canSwitchGunnerMode() {
		if ((getAcInfo() == null) || (!getAcInfo().isEnableGunnerMode)) {
			return false;
		}

		if (!isCanopyClose()) {
			return false;
		}
		if (!getAcInfo().isEnableConcurrentGunnerMode) {

			if ((getEntityBySeatId(1) instanceof EntityPlayer))
				return false;
		}
		return !isHoveringMode();
	}

	public boolean canSwitchGunnerModeOtherSeat(EntityPlayer player) {
		int sid = getSeatIdByEntity(player);

		if (sid > 0) {
			MCH_SeatInfo info = getSeatInfo(sid);
			if (info != null) {
				return (info.gunner) && (info.switchgunner);
			}
		}
		return false;
	}

	public void switchGunnerModeOtherSeat(EntityPlayer player) {
		this.isGunnerModeOtherSeat = (!this.isGunnerModeOtherSeat);
	}

	public boolean isHoveringMode() {
		return this.isHoveringMode;
	}

	public void switchHoveringMode(boolean mode) {
		stopRepelling();

		if (canSwitchHoveringMode()) {
			if (isHoveringMode() != mode) {
				if (mode) {
					this.beforeHoverThrottle = getCurrentThrottle();
				} else {
					setCurrentThrottle(this.beforeHoverThrottle);
				}

				this.isHoveringMode = mode;
				if (this.riddenByEntity != null) {
					this.riddenByEntity.rotationPitch = 0.0F;
					this.riddenByEntity.prevRotationPitch = 0.0F;
				}
			}
		}
	}

	public boolean canSwitchHoveringMode() {
		if (getAcInfo() == null)
			return false;
		return !this.isGunnerMode;
	}

	public boolean isHovering() {
		return (this.isGunnerMode) || (isHoveringMode());
	}

	public boolean getIsGunnerMode(Entity entity) {
		if (getAcInfo() == null) {
			return false;
		}
		int id = getSeatIdByEntity(entity);
		if (id < 0) {
			return false;
		}

		if ((id == 0) && (getAcInfo().isEnableGunnerMode)) {
			return this.isGunnerMode;
		}

		MCH_SeatInfo[] st = getSeatsInfo();
		if (id < st.length) {
			if (st[id].gunner) {
				if ((this.worldObj.isRemote) && (st[id].switchgunner)) {
					return this.isGunnerModeOtherSeat;
				}

				return true;
			}
		}

		return false;
	}

	public boolean isPilot(Entity player) {
		return W_Entity.isEqual(getRiddenByEntity(), player);
	}

	public boolean canSwitchFreeLook() {
		return true;
	}

	public boolean isFreeLookMode() {
		return (getCommonStatus(1)) || (isRepelling());
	}

	public void switchFreeLookMode(boolean b) {
		setCommonStatus(1, b);
	}

	public void switchFreeLookModeClient(boolean b) {
		setCommonStatus(1, b, true);
	}

	public boolean canSwitchGunnerFreeLook(EntityPlayer player) {
		MCH_SeatInfo seatInfo = getSeatInfo(player);
		if ((seatInfo != null) && (seatInfo.fixRot) && (getIsGunnerMode(player))) {
			return true;
		}
		return false;
	}

	public boolean isGunnerLookMode(EntityPlayer player) {
		if (isPilot(player)) {
			return false;
		}
		return this.isGunnerFreeLookMode;
	}

	public void switchGunnerFreeLookMode(boolean b) {
		this.isGunnerFreeLookMode = b;
	}

	public void switchGunnerFreeLookMode() {
		switchGunnerFreeLookMode(!this.isGunnerFreeLookMode);
	}

	public void updateParts(int stat) {
		if (isDestroyed()) {
			return;
		}
		MCH_Parts[] parts = { this.partHatch, this.partCanopy, this.partLandingGear };

		for (MCH_Parts p : parts) {
			if (p != null) {
				p.updateStatusClient(stat);
				p.update();
			}
		}

		if ((!isDestroyed()) && (!this.worldObj.isRemote) && (this.partLandingGear != null)) {
			int blockId = 0;

			if ((!isLandingGearFolded()) && (this.partLandingGear.getFactor() <= 0.1F)) {
				blockId = MCH_Lib.getBlockIdY(this, 3, -20);

				if ((getCurrentThrottle() <= 0.800000011920929D) || (this.onGround) || (blockId != 0)) {

					if ((getAcInfo().isFloat)
							&& ((isInWater()) || (MCH_Lib.getBlockY(this, 3, -20, true) == W_Block.getWater()))) {
						this.partLandingGear.setStatusServer(true);
					}
				}
			} else if ((isLandingGearFolded() == true) && (this.partLandingGear.getFactor() >= 0.9F)) {

				blockId = MCH_Lib.getBlockIdY(this, 3, -10);
				if ((getCurrentThrottle() < getUnfoldLandingGearThrottle()) && (blockId != 0)) {
					boolean unfold = true;

					if (getAcInfo().isFloat) {
						blockId = MCH_Lib.getBlockIdY(this.worldObj, this.posX,
								this.posY + 1.0D + getAcInfo().floatOffset, this.posZ, 1, 65386, true);

						if (W_Block.isEqual(blockId, W_Block.getWater())) {
							unfold = false;
						}
					}

					if (unfold) {
						this.partLandingGear.setStatusServer(false);
					}
				} else if (getVtolMode() == 2) {

					if (blockId != 0) {
						this.partLandingGear.setStatusServer(false);
					}
				}
			}
		}
	}

	public float getUnfoldLandingGearThrottle() {
		return 0.8F;
	}

	private int getPartStatus() {
		return getDataWatcher().getWatchableObjectInt(31);
	}

	private void setPartStatus(int n) {
		getDataWatcher().updateObject(31, Integer.valueOf(n));
	}

	protected void initPartRotation(float yaw, float pitch) {
		this.lastRiderYaw = yaw;
		this.prevLastRiderYaw = yaw;
		this.camera.partRotationYaw = yaw;
		this.camera.prevPartRotationYaw = yaw;
		this.lastSearchLightYaw = yaw;
	}

	public int getLastPartStatusMask() {
		return 24;
	}

	public int getModeSwitchCooldown() {
		return this.modeSwitchCooldown;
	}

	public void setModeSwitchCooldown(int n) {
		this.modeSwitchCooldown = n;
	}

	protected WeaponBay[] createWeaponBays() {
		WeaponBay[] wbs = new WeaponBay[getAcInfo().partWeaponBay.size()];
		for (int i = 0; i < wbs.length; i++) {
			wbs[i] = new WeaponBay();
		}
		return wbs;
	}

	protected MCH_Parts createHatch() {
		MCH_Parts hatch = null;
		if (getAcInfo().haveHatch()) {
			hatch = new MCH_Parts(this, 4, 31, "Hatch");
			hatch.rotationMax = 90.0F;
			hatch.rotationInv = 1.5F;
			hatch.soundEndSwichOn.setPrm("plane_cc", 1.0F, 1.0F);
			hatch.soundEndSwichOff.setPrm("plane_cc", 1.0F, 1.0F);
			hatch.soundSwitching.setPrm("plane_cv", 1.0F, 0.5F);
		}
		return hatch;
	}

	public boolean haveHatch() {
		return this.partHatch != null;
	}

	public boolean canFoldHatch() {
		if ((this.partHatch == null) || (this.modeSwitchCooldown > 0))
			return false;
		return this.partHatch.isOFF();
	}

	public boolean canUnfoldHatch() {
		if ((this.partHatch == null) || (this.modeSwitchCooldown > 0))
			return false;
		return this.partHatch.isON();
	}

	public void foldHatch(boolean fold) {
		foldHatch(fold, false);
	}

	public void foldHatch(boolean fold, boolean force) {
		if (this.partHatch == null)
			return;
		if ((!force) && (this.modeSwitchCooldown > 0))
			return;
		this.partHatch.setStatusServer(fold);
		this.modeSwitchCooldown = 20;
		if (!fold) {
			stopUnmountCrew();
		}
	}

	public float getHatchRotation() {
		return this.partHatch != null ? this.partHatch.rotation : 0.0F;
	}

	public float getPrevHatchRotation() {
		return this.partHatch != null ? this.partHatch.prevRotation : 0.0F;
	}

	public void foldLandingGear() {
		if ((this.partLandingGear == null) || (getModeSwitchCooldown() > 0)) {
			return;
		}
		this.partLandingGear.setStatusServer(true);
		setModeSwitchCooldown(20);
	}

	public void unfoldLandingGear() {
		if ((this.partLandingGear == null) || (getModeSwitchCooldown() > 0))
			return;
		if (isLandingGearFolded()) {
			this.partLandingGear.setStatusServer(false);
			setModeSwitchCooldown(20);
		}
	}

	public boolean canFoldLandingGear() {
		if (getLandingGearRotation() >= 1.0F)
			return false;
		Block block = MCH_Lib.getBlockY(this, 3, -10, true);
		return (!isLandingGearFolded()) && (block == W_Blocks.air);
	}

	public boolean canUnfoldLandingGear() {
		if (getLandingGearRotation() < 89.0F)
			return false;
		return isLandingGearFolded();
	}

	public boolean isLandingGearFolded() {
		return this.partLandingGear != null ? this.partLandingGear.getStatus() : false;
	}

	protected MCH_Parts createLandingGear() {
		MCH_Parts lg = null;
		if (getAcInfo().haveLandingGear()) {
			lg = new MCH_Parts(this, 2, 31, "LandingGear");
			lg.rotationMax = 90.0F;
			lg.rotationInv = 2.5F;
			lg.soundStartSwichOn.setPrm("plane_cc", 1.0F, 0.5F);
			lg.soundEndSwichOn.setPrm("plane_cc", 1.0F, 0.5F);
			lg.soundStartSwichOff.setPrm("plane_cc", 1.0F, 0.5F);
			lg.soundEndSwichOff.setPrm("plane_cc", 1.0F, 0.5F);
			lg.soundSwitching.setPrm("plane_cv", 1.0F, 0.75F);
		}
		return lg;
	}

	public float getLandingGearRotation() {
		return this.partLandingGear != null ? this.partLandingGear.rotation : 0.0F;
	}

	public float getPrevLandingGearRotation() {
		return this.partLandingGear != null ? this.partLandingGear.prevRotation : 0.0F;
	}

	public int getVtolMode() {
		return 0;
	}

	public void openCanopy() {
		if ((this.partCanopy == null) || (getModeSwitchCooldown() > 0)) {
			return;
		}
		this.partCanopy.setStatusServer(true);
		setModeSwitchCooldown(20);
	}

	public void openCanopy_EjectSeat() {
		if (this.partCanopy == null) {
			return;
		}
		this.partCanopy.setStatusServer(true, false);
		setModeSwitchCooldown(40);
	}

	public void closeCanopy() {
		if ((this.partCanopy == null) || (getModeSwitchCooldown() > 0))
			return;
		if (getCanopyStat()) {
			this.partCanopy.setStatusServer(false);
			setModeSwitchCooldown(20);
		}
	}

	public boolean getCanopyStat() {
		return this.partCanopy != null ? this.partCanopy.getStatus() : false;
	}

	public boolean isCanopyClose() {
		if (this.partCanopy == null)
			return true;
		return (!getCanopyStat()) && (getCanopyRotation() <= 0.01F);
	}

	public float getCanopyRotation() {
		return this.partCanopy != null ? this.partCanopy.rotation : 0.0F;
	}

	public float getPrevCanopyRotation() {
		return this.partCanopy != null ? this.partCanopy.prevRotation : 0.0F;
	}

	protected MCH_Parts createCanopy() {
		MCH_Parts canopy = null;
		if (getAcInfo().haveCanopy()) {
			canopy = new MCH_Parts(this, 0, 31, "Canopy");
			canopy.rotationMax = 90.0F;
			canopy.rotationInv = 3.5F;
			canopy.soundEndSwichOn.setPrm("plane_cc", 1.0F, 1.0F);
			canopy.soundEndSwichOff.setPrm("plane_cc", 1.0F, 1.0F);
		}

		return canopy;
	}

	public boolean hasBrake() {
		return false;
	}

	public void setBrake(boolean b) {
		if (!this.worldObj.isRemote) {
			setCommonStatus(11, b);
		}
	}

	public boolean getBrake() {
		return getCommonStatus(11);
	}

	public void setGunnerStatus(boolean b) {
		if (!this.worldObj.isRemote) {
			setCommonStatus(12, b);
		}
	}

	public boolean getGunnerStatus() {
		return getCommonStatus(12);
	}

	public int getSizeInventory() {
		return getAcInfo() != null ? getAcInfo().inventorySize : 0;
	}

	public String getInvName() {
		if (getAcInfo() == null)
			return super.getInvName();
		String s = getAcInfo().displayName;
		return s.length() <= 32 ? s : s.substring(0, 31);
	}

	public boolean isInvNameLocalized() {
		return getAcInfo() != null;
	}

	public MCH_EntityChain getTowChainEntity() {
		return this.towChainEntity;
	}

	public void setTowChainEntity(MCH_EntityChain chainEntity) {
		this.towChainEntity = chainEntity;
	}

	public MCH_EntityChain getTowedChainEntity() {
		return this.towedChainEntity;
	}

	public void setTowedChainEntity(MCH_EntityChain towedChainEntity) {
		this.towedChainEntity = towedChainEntity;
	}
}
