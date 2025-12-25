/*
 * Copyright (c) 2013 L2jMobius
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
 * IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package ai.bosses.Baium;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.gameserver.ai.Intention;
import org.l2jmobius.gameserver.geoengine.GeoEngine;
import org.l2jmobius.gameserver.managers.GrandBossManager;
import org.l2jmobius.gameserver.managers.InstanceManager;
import org.l2jmobius.gameserver.managers.ZoneManager;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.StatSet;
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.actor.Attackable;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Playable;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.enums.player.MountType;
import org.l2jmobius.gameserver.model.actor.instance.GrandBoss;
import org.l2jmobius.gameserver.model.instancezone.Instance;
import org.l2jmobius.gameserver.model.script.Script;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.model.skill.SkillCaster;
import org.l2jmobius.gameserver.model.skill.enums.SkillFinishType;
import org.l2jmobius.gameserver.model.skill.holders.SkillHolder;
import org.l2jmobius.gameserver.model.variables.NpcVariables;
import org.l2jmobius.gameserver.model.zone.type.NoRestartZone;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.serverpackets.Earthquake;
import org.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2jmobius.gameserver.network.serverpackets.PlaySound;
import org.l2jmobius.gameserver.network.serverpackets.SocialAction;
import org.l2jmobius.gameserver.util.MathUtil;

/**
 * Baium AI<br>
 * This version of Baium is applied in Master Class Ch. 3 (October 26, 2022) linked to TimedHunting floor 9.<br>
 * Baium was removed from the game in the Shield of Kingdom version on August 6, 2024.
 * @author Notorion
 */
public class Baium extends Script
{
	private static final Logger LOGGER = Logger.getLogger(Baium.class.getName());
	
	// TimedHunting
	private static final int INSTANCE_ID = 1020; // Tower of Insolence
	
	// Raid Schedule Configuration
	private static final int RAID_DAY = Calendar.WEDNESDAY; // Day of week
	private static final int SPAWN_HOUR = 21; // Start time (0-23 format)
	private static final int CLOSE_HOUR = 23; // End time (Instance closes at this hour)
	
	// NPCs
	private static final int BAIUM = 29391;
	private static final int BAIUM_STONE = 29392;
	private static final int GALAXIA = 29393;
	private static final int TELE_CUBE = 31842;
	private static final int ANGEL_PIKEMAN = 29395;
	private static final int ANGEL_ARCHER = 29397;
	private static final int DARK_ANGEL = 29394;
	private static final int ARCHANGEL_SEALER_ID = 29396;
	
	// Baium skills
	private static final SkillHolder EMPERORS_WILL = new SkillHolder(34335, 1);
	private static final SkillHolder THUNDER_ROAR = new SkillHolder(34336, 1);
	private static final SkillHolder EXECUTION = new SkillHolder(34338, 1);
	
	private static final SkillHolder HEAL_OF_BAIUM = new SkillHolder(4135, 1);
	private static final SkillHolder ANTI_STRIDER = new SkillHolder(4258, 1);
	
	// Attack skills of the Raid Boss Galaxia
	private static final SkillHolder HOLY_STRIKE = new SkillHolder(34348, 1);
	private static final SkillHolder HOLY_SPEAR_RANGE = new SkillHolder(34349, 1);
	private static final SkillHolder HOLY_SPEAR_AOE = new SkillHolder(34350, 1);
	
	// Archangel_Sealer skill for the visual effect of sealing Baium
	private static final SkillHolder DIVINE_SEALING = new SkillHolder(34334, 1);
	
	// Visual effect skills of the protection barriers
	private static final SkillHolder BAIUM_BOSS_PROTECTION = new SkillHolder(29518, 1);
	private static final SkillHolder BAIUM_BARRIER = new SkillHolder(29515, 1);
	
	// Time of the protection barriers
	private static final int BAIUM_BARRIER_HIT_COUNT = 1000; // Hits on the 1st Shield (Initial Protection)
	private static final int BAIUM_BARRIER_HIT_COUNT_VULN = 2000; // Hits to break the 10-minute barrier
	private static final long BAIUM_BARRIER_TIMEOUT_MS = 600000; // Maximum duration of the barrier (10 min)
	private static final long BAIUM_BARRIER_VULN_MIN_MS = 50000; // Minimum Vulnerability (50 sec)
	private static final long BAIUM_BARRIER_VULN_MAX_MS = 75000; // Maximum Vulnerability (75 sec)
	
	// Time to seal Baium
	private static final long SEALER_CAST_TIME_MS = 20000;
	private static final long SEALER_RESPAWN_DELAY_MS = 5000;
	private static final int SEAL_ATTEMPTS = 30;
	
	// Zone
	private static final NoRestartZone ZONE = ZoneManager.getInstance().getZoneById(70051, NoRestartZone.class);
	
	// Status
	private static final int ALIVE = 0;
	private static final int WAITING = 1;
	private static final int IN_FIGHT = 2;
	private static final int DEAD = 3;
	
	// Locations
	private static final Location BAIUM_LOC = new Location(116033, 17447, 10107, 40188);
	private static final Location TELEPORT_CUBIC_LOC = new Location(115017, 15549, 10090);
	private static final Location[] TELEPORT_OUT_LOC =
	{
		new Location(108784, 16000, -4928),
		new Location(113824, 10448, -5164),
		new Location(115488, 22096, -5168),
	};
	
	private static final Location GALAXIA_SPAWN_LOC = new Location(114650, 16131, 10080, 40717);
	
	private static final Location[] ANGEL_PIKEMAN_SPAWNS =
	{
		new Location(114497, 16231, 10080, 40428),
		new Location(114376, 16365, 10080, 40428),
		new Location(114256, 16498, 10080, 40428),
		new Location(114135, 16632, 10080, 40428),
		new Location(114014, 16766, 10080, 40428),
		new Location(113894, 16899, 10080, 40428),
		new Location(113773, 17033, 10080, 40428),
		new Location(113653, 17166, 10080, 40428),
		new Location(113532, 17300, 10080, 40428),
		new Location(113411, 17434, 10080, 40428),
		new Location(113291, 17567, 10080, 40428),
		new Location(114737, 15965, 10080, 40428),
		new Location(114858, 15831, 10080, 40428),
		new Location(114978, 15698, 10080, 40428),
		new Location(115099, 15564, 10080, 40428),
		new Location(115220, 15430, 10080, 40428),
		new Location(115340, 15297, 10080, 40428),
		new Location(115461, 15163, 10080, 40428),
		new Location(115581, 15030, 10080, 40428),
		new Location(115702, 14896, 10080, 40428),
		new Location(115823, 14762, 10080, 40428),
		new Location(115943, 14629, 10080, 40428)
	};
	
	private static final Location[] ANGEL_ARCHER_SPAWNS =
	{
		new Location(114602, 16335, 10080, 40141),
		new Location(114485, 16472, 10080, 40141),
		new Location(114368, 16609, 10080, 40141),
		new Location(114251, 16746, 10080, 40141),
		new Location(114134, 16883, 10080, 40141),
		new Location(114017, 17020, 10080, 40141),
		new Location(113900, 17157, 10080, 40141),
		new Location(113783, 17293, 10080, 40141),
		new Location(113666, 17430, 10080, 40141),
		new Location(113549, 17567, 10080, 40141),
		new Location(113433, 17704, 10080, 40141),
		new Location(114834, 16063, 10080, 40141),
		new Location(114951, 15926, 10080, 40141),
		new Location(115068, 15789, 10080, 40141),
		new Location(115185, 15652, 10080, 40141),
		new Location(115302, 15515, 10080, 40141),
		new Location(115419, 15378, 10080, 40141),
		new Location(115536, 15241, 10080, 40141),
		new Location(115653, 15105, 10080, 40141),
		new Location(115770, 14968, 10080, 40141),
		new Location(115887, 14831, 10080, 40141),
		new Location(116003, 14694, 10080, 40141)
	};
	
	private static final Location[] DARK_ANGEL_SPAWNS =
	{
		new Location(114992, 17531, 10080, 40717),
		new Location(114253, 17533, 10080, 40717),
		new Location(113954, 17417, 10080, 40717),
		new Location(115936, 16480, 10080, 40717),
		new Location(115981, 15728, 10080, 40717),
		new Location(115910, 15434, 10064, 40717),
		new Location(114192, 14545, 10080, 40717),
		new Location(113268, 15714, 10080, 40717)
	};
	
	private static final Location[] ARCHANGEL_SEALER_SPAWNS =
	{
		new Location(114961, 17116, 10080, 0),
		new Location(113652, 15812, 10080, 0),
		new Location(114488, 14947, 10080, 0),
		new Location(115634, 16332, 10080, 0)
	};
	
	private GrandBoss _baium = null;
	private static long _lastAttack = 0;
	private static Player _standbyPlayer = null;
	
	private final Map<Npc, Integer> _baiumHits = new ConcurrentHashMap<>();
	private boolean _baiumVulnerable = false;
	private boolean _baiumHasBossProtection = false;
	private boolean _baiumHasTimedBarrier = false;
	private long _lastBarrierHitTime = 0;
	
	private boolean _darkAngelsSpawned = false;
	private boolean _angelsSpawned50 = false;
	private boolean _hp70Triggered = false;
	private boolean _hp50Triggered = false;
	private boolean _galaxiaFirstAttack = false;
	
	private boolean _sealingActive = false;
	private int _sealingWaveCount = 0;
	private final List<Integer> _liveSealerIndices = new CopyOnWriteArrayList<>();
	
	private Baium()
	{
		addInstanceCreatedId(INSTANCE_ID);
		addTalkId(TELE_CUBE, BAIUM_STONE);
		addStartNpc(TELE_CUBE, BAIUM_STONE);
		addAttackId(BAIUM, GALAXIA, ARCHANGEL_SEALER_ID);
		addSpawnId(GALAXIA, ANGEL_PIKEMAN, ANGEL_ARCHER, ARCHANGEL_SEALER_ID);
		addEnterZoneId(ZONE.getId());
		addKillId(BAIUM, ARCHANGEL_SEALER_ID);
		addSpellFinishedId(BAIUM);
		addCreatureSeeId(BAIUM);
		
		resetSealerIndices();
		
		initializeCronograma();
		
		ThreadPool.scheduleAtFixedRate(this::checkHardClose, 60000, 60000);
	}
	
	private void initializeCronograma()
	{
		if (isInsideAvailabilityWindow())
		{
			if (GrandBossManager.getInstance().getStatus(BAIUM) == DEAD)
			{
				StatSet info = GrandBossManager.getInstance().getStatSet(BAIUM);
				long respawnTime = info.getLong("respawn_time");
				if (System.currentTimeMillis() > respawnTime)
				{
					GrandBossManager.getInstance().setStatus(BAIUM, ALIVE);
					LOGGER.info("Baium AI: Server started within the active combat window. Status set to ALIVE.");
				}
			}
		}
		else
		{
			if (GrandBossManager.getInstance().getStatus(BAIUM) != DEAD)
			{
				GrandBossManager.getInstance().setStatus(BAIUM, DEAD);
				LOGGER.info("Baium AI: Outside of combat window. Status forced to DEAD.");
			}
			
			scheduleNextSpawn();
		}
	}
	
	private boolean isInsideAvailabilityWindow()
	{
		Calendar now = Calendar.getInstance();
		int day = now.get(Calendar.DAY_OF_WEEK);
		int hour = now.get(Calendar.HOUR_OF_DAY);
		return ((day == RAID_DAY) && (hour >= SPAWN_HOUR) && (hour < CLOSE_HOUR));
	}
	
	private void scheduleNextSpawn()
	{
		Calendar nextStart = Calendar.getInstance();
		nextStart.set(Calendar.DAY_OF_WEEK, RAID_DAY);
		nextStart.set(Calendar.HOUR_OF_DAY, SPAWN_HOUR);
		nextStart.set(Calendar.MINUTE, 0);
		nextStart.set(Calendar.SECOND, 0);
		
		if (Calendar.getInstance().after(nextStart))
		{
			nextStart.add(Calendar.WEEK_OF_YEAR, 1);
		}
		
		long delay = nextStart.getTimeInMillis() - System.currentTimeMillis();
		
		StatSet info = GrandBossManager.getInstance().getStatSet(BAIUM);
		info.set("respawn_time", nextStart.getTimeInMillis());
		GrandBossManager.getInstance().setStatSet(BAIUM, info);
		
		LOGGER.info("Baium AI: Next spawn scheduled for " + nextStart.getTime());
		
		startQuestTimer("SCHEDULED_UNLOCK", delay, null, null);
	}
	
	/**
	 * Checks if the Raid timeout has been reached. If the current time is greater than CLOSE_HOUR on the day of the RAID, and the Boss is still alive, forces the instance to close to prevent it from remaining open indefinitely.
	 */
	private void checkHardClose()
	{
		final Calendar now = Calendar.getInstance();
		if ((now.get(Calendar.DAY_OF_WEEK) == RAID_DAY) && (now.get(Calendar.HOUR_OF_DAY) >= CLOSE_HOUR))
		{
			if (GrandBossManager.getInstance().getStatus(BAIUM) != DEAD)
			{
				LOGGER.info("Baium AI: Hard Close triggered (Time limit reached). Clearing zone and closing entry.");
				
				if (_baium != null)
				{
					notifyEvent("CLEAR_ZONE", _baium, null);
				}
				else
				{
					ZONE.oustAllPlayers();
				}
				
				GrandBossManager.getInstance().setStatus(BAIUM, DEAD);
				scheduleNextSpawn();
			}
		}
	}
	
	@Override
	public void onInstanceCreated(Instance instance, Player player)
	{
		if (instance.getTemplateId() != INSTANCE_ID)
		{
			return;
		}
		
		final int status = GrandBossManager.getInstance().getStatus(BAIUM);
		final StatSet info = GrandBossManager.getInstance().getStatSet(BAIUM);
		
		// Check the status: ALIVE (entered the window) or IN_FIGHT (server restarted mid-fight)
		// The script should generate a Statue/Boss on instance 1020 (TimedHunting).
		switch (status)
		{
			case WAITING:
			{
				GrandBossManager.getInstance().setStatus(BAIUM, ALIVE);
			}
			case ALIVE:
			{
				addSpawn(BAIUM_STONE, BAIUM_LOC, false, 0, false, instance.getId());
				spawnStaticGuards(instance.getId());
				break;
			}
			case IN_FIGHT:
			{
				final double curr_hp = info.getDouble("currentHP");
				final double curr_mp = info.getDouble("currentMP");
				final int loc_x = info.getInt("loc_x");
				final int loc_y = info.getInt("loc_y");
				final int loc_z = info.getInt("loc_z");
				final int heading = info.getInt("heading");
				
				_baium = (GrandBoss) addSpawn(BAIUM, loc_x, loc_y, loc_z, heading, false, 0, false, instance.getId());
				_baium.setCurrentHpMp(curr_hp, curr_mp);
				_lastAttack = System.currentTimeMillis();
				addBoss(_baium);
				
				spawnStaticGuards(instance.getId());
				
				final Skill protectionSkill = BAIUM_BOSS_PROTECTION.getSkill();
				if (protectionSkill != null)
				{
					protectionSkill.applyEffects(_baium, _baium);
					_baium.setInvul(true);
					_baiumHasBossProtection = true;
				}
				
				if (curr_hp < (_baium.getMaxHp() * 0.70))
				{
					_hp70Triggered = true;
					_sealingWaveCount = info.getInt("SealingWaveCount", 0);
					startQuestTimer("SEAL_START_WAVE", 5000, _baium, null);
				}
				
				startQuestTimer("CHECK_ATTACK", 60000, _baium, null);
				break;
			}
		}
	}
	
	private void resetSealerIndices()
	{
		_liveSealerIndices.clear();
		for (int i = 0; i < ARCHANGEL_SEALER_SPAWNS.length; i++)
		{
			_liveSealerIndices.add(i);
		}
	}
	
	private void spawnStaticGuards(int instanceId)
	{
		addSpawn(GALAXIA, GALAXIA_SPAWN_LOC, false, 0, false, instanceId);
		
		for (Location loc : ANGEL_PIKEMAN_SPAWNS)
		{
			addSpawn(ANGEL_PIKEMAN, loc, false, 0, false, instanceId);
		}
		
		for (Location loc : ANGEL_ARCHER_SPAWNS)
		{
			addSpawn(ANGEL_ARCHER, loc, false, 0, false, instanceId);
		}
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		switch (event)
		{
			case "SCHEDULED_UNLOCK":
			{
				GrandBossManager.getInstance().setStatus(BAIUM, ALIVE);
				break;
			}
			case "wakeUp":
			{
				if (getStatus() == ALIVE)
				{
					setStatus(IN_FIGHT);
					final int instanceId = npc.getInstanceId();
					
					_baium = (GrandBoss) addSpawn(BAIUM, BAIUM_LOC, false, 0, false, instanceId);
					_baium.disableCoreAI(true);
					_baium.setRandomWalking(false);
					addBoss(_baium);
					_lastAttack = System.currentTimeMillis();
					Skill protectionSkill = BAIUM_BOSS_PROTECTION.getSkill();
					
					if (protectionSkill != null)
					{
						protectionSkill.applyEffects(_baium, _baium);
						_baium.setInvul(true);
						_baiumHasBossProtection = true;
						_baiumHasTimedBarrier = false;
					}
					else
					{
						_baium.setInvul(false);
						_baiumHasBossProtection = false;
						_baiumHasTimedBarrier = false;
					}
					
					_baiumVulnerable = false;
					_baiumHits.clear();
					_lastBarrierHitTime = 0;
					_darkAngelsSpawned = false;
					_angelsSpawned50 = false;
					_hp70Triggered = false;
					_hp50Triggered = false;
					_galaxiaFirstAttack = false;
					resetSealerIndices();
					
					startQuestTimer("WAKEUP_ACTION", 50, _baium, null);
					startQuestTimer("MANAGE_EARTHQUAKE", 2000, _baium, player);
					startQuestTimer("CHECK_ATTACK", 60000, _baium, null);
					npc.deleteMe();
				}
				break;
			}
			case "WAKEUP_ACTION":
			{
				if (npc != null)
				{
					ZONE.broadcastPacket(new SocialAction(_baium.getObjectId(), 2));
					
					// Baium awakening message - appears only once.
					ZONE.broadcastPacket(new ExShowScreenMessage(NpcStringId.SOMEBODY_HAS_BROKEN_BAIUM_S_SEAL_LISTEN_TO_ME_ANGELS_YOU_MUST_DESTROY_THE_ONE_RESPONSIBLE_FOR_THAT, ExShowScreenMessage.TOP_CENTER, 7000, true));
				}
				break;
			}
			case "MANAGE_EARTHQUAKE":
			{
				if (npc != null)
				{
					ZONE.broadcastPacket(new Earthquake(npc.getX(), npc.getY(), npc.getZ(), 40, 10));
					ZONE.broadcastPacket(new PlaySound("BS02_A"));
					startQuestTimer("SOCIAL_ACTION", 8000, npc, player);
				}
				break;
			}
			case "SOCIAL_ACTION":
			{
				if (npc != null)
				{
					ZONE.broadcastPacket(new SocialAction(npc.getObjectId(), 3));
					startQuestTimer("ENABLE_AI", 6000, npc, player);
				}
				break;
			}
			case "ENABLE_AI":
			{
				_baium.disableCoreAI(false);
				_baium.setRandomWalking(true);
				
				if ((player != null) && !player.isDead())
				{
					addAttackPlayerDesire(npc, player);
				}
				else if ((_standbyPlayer != null) && !_standbyPlayer.isDead())
				{
					addAttackPlayerDesire(npc, _standbyPlayer);
				}
				else
				{
					for (Player creature : World.getInstance().getVisibleObjectsInRange(npc, Player.class, 2000))
					{
						if (ZONE.isInsideZone(creature) && !creature.isDead())
						{
							addAttackPlayerDesire(npc, creature);
							break;
						}
					}
				}
				break;
			}
			case "SELECT_TARGET":
			{
				if ((npc == null) || npc.isDead() || npc.isCastingNow(SkillCaster::isAnyNormalType))
				{
					break;
				}
				
				final Attackable mob = npc.asAttackable();
				
				if (getStatus() == DEAD)
				{
					mob.deleteMe();
					break;
				}
				
				if ((_baium == null) || (getStatus() != IN_FIGHT))
				{
					startQuestTimer("SELECT_TARGET", 5000, npc, null);
					break;
				}
				
				if (mob.getCurrentHp() <= 1)
				{
					mob.asAttackable().clearAggroList();
					mob.setTarget(_baium);
					mob.getAI().setIntention(Intention.FOLLOW, _baium);
					startQuestTimer("SELECT_TARGET", 3000, npc, null);
					break;
				}
				
				final Creature mostHated = mob.getMostHated();
				
				if ((mostHated != null) && mostHated.isPlayer() && ZONE.isInsideZone(mostHated) && !mostHated.isDead() && GeoEngine.getInstance().canSeeTarget(mob, mostHated))
				{
					final double distance = mob.calculateDistance3D(mostHated);
					SkillHolder skillToCast = null;
					
					if (distance > 1200)
					{
						mob.getAI().setIntention(Intention.FOLLOW, mostHated);
					}
					else if (distance > 300)
					{
						skillToCast = HOLY_SPEAR_RANGE;
					}
					else
					{
						if (getRandom(100) < 30)
						{
							skillToCast = HOLY_SPEAR_AOE;
						}
						else
						{
							skillToCast = HOLY_STRIKE;
						}
					}
					
					if ((skillToCast != null) && SkillCaster.checkUseConditions(mob, skillToCast.getSkill()))
					{
						mob.setTarget(mostHated);
						mob.doCast(skillToCast.getSkill());
					}
					else if (distance <= 300)
					{
						mob.getAI().setIntention(Intention.ATTACK, mostHated);
					}
				}
				else
				{
					boolean found = false;
					for (Playable creature : World.getInstance().getVisibleObjectsInRange(mob, Playable.class, 1200))
					{
						if (creature.isPlayer() && ZONE.isInsideZone(creature) && !creature.isDead() && GeoEngine.getInstance().canSeeTarget(mob, creature))
						{
							mob.getAI().setIntention(Intention.ATTACK, creature);
							found = true;
							break;
						}
					}
					
					if (!found)
					{
						mob.getAI().setIntention(Intention.IDLE);
					}
				}
				
				startQuestTimer("SELECT_TARGET", 5000, npc, null);
				break;
			}
			case "SEAL_START_WAVE":
			{
				if ((_baium == null) || _baium.isDead())
				{
					break;
				}
				
				if (_liveSealerIndices.isEmpty())
				{
					break;
				}
				
				_sealingActive = true;
				_sealingWaveCount++;
				
				if (_sealingWaveCount > SEAL_ATTEMPTS)
				{
					ZONE.broadcastPacket(new ExShowScreenMessage(NpcStringId.getNpcStringId(1804028), ExShowScreenMessage.TOP_CENTER, 7000, true)); // The Sealed Angels have been able to imprison Baium.
					startQuestTimer("SEAL_FINAL_FAILURE", 7000, _baium, null);
					break;
				}
				
				if (_sealingWaveCount == 4)
				{
					ZONE.broadcastPacket(new ExShowScreenMessage(NpcStringId.getNpcStringId(1804030), ExShowScreenMessage.TOP_CENTER, 7000, true)); // I sense that Baium is trying to harness the Seal's energy. We must stop the intruders and defend the Sealed Angels.
				}
				else if ((_sealingWaveCount == 10) || (_sealingWaveCount == 20))
				{
					ZONE.broadcastPacket(new ExShowScreenMessage(NpcStringId.getNpcStringId(1804031), ExShowScreenMessage.TOP_CENTER, 7000, true)); // Pour some more power to the Seal to imprison Baium. Don't give up, the victory is close!
				}
				else
				{
					ZONE.broadcastPacket(new ExShowScreenMessage(NpcStringId.getNpcStringId(1804019), ExShowScreenMessage.TOP_CENTER, 5000, true)); // Hurry up, Archangels! Seal Baium!
				}
				
				int instanceId = _baium.getInstanceId();
				for (int index : _liveSealerIndices)
				{
					Location spawnLoc = ARCHANGEL_SEALER_SPAWNS[index];
					final Npc sealer = addSpawn(ARCHANGEL_SEALER_ID, spawnLoc, false, 0, false, instanceId);
					sealer.setScriptValue(index);
					startQuestTimer("SEALER_ACT", 2000, sealer, null);
				}
				
				Skill sealingSkill = DIVINE_SEALING.getSkill();
				if (sealingSkill != null)
				{
					sealingSkill.applyEffects(_baium, _baium);
				}
				
				saveSealingState(_baium);
				startQuestTimer("SEAL_END_CAST", SEALER_CAST_TIME_MS + 2000, _baium, null);
				break;
			}
			case "SEALER_ACT":
			{
				if ((npc == null) || npc.isDead() || (_baium == null) || _baium.isDead())
				{
					if (npc != null)
					{
						npc.deleteMe();
					}
					break;
				}
				
				Skill sealingSkill = DIVINE_SEALING.getSkill();
				if (sealingSkill == null)
				{
					npc.deleteMe();
					break;
				}
				
				sealingSkill.applyEffects(_baium, _baium);
				
				npc.setTarget(_baium);
				npc.setRunning();
				npc.setHeading(npc.calculateHeadingTo(_baium));
				npc.broadcastInfo();
				npc.doCast(sealingSkill);
				break;
			}
			case "SEAL_END_CAST":
			{
				if (_baium != null)
				{
					_baium.stopSkillEffects(SkillFinishType.REMOVED, DIVINE_SEALING.getSkillId());
					
					for (Npc sealer : World.getInstance().getVisibleObjects(_baium, Npc.class))
					{
						if (sealer.getId() == ARCHANGEL_SEALER_ID)
						{
							sealer.deleteMe();
						}
					}
					
					if (!_liveSealerIndices.isEmpty() && _sealingActive)
					{
						startQuestTimer("SEAL_START_WAVE", SEALER_RESPAWN_DELAY_MS, _baium, null);
					}
				}
				break;
			}
			case "SEAL_FINAL_FAILURE":
			{
				GrandBossManager.getInstance().setStatus(BAIUM, DEAD);
				Instance world = npc.getInstanceWorld();
				if (world != null)
				{
					notifyEvent("CLEAR_ZONE", npc, null);
				}
				break;
			}
			case "SEAL_MECHANIC_CANCEL":
			{
				_sealingActive = false;
				cancelSealingTimers(_baium);
				break;
			}
			case "CHECK_ATTACK":
			{
				if ((npc != null) && ((_lastAttack + 1800000) < System.currentTimeMillis()))
				{
					notifyEvent("CLEAR_ZONE", npc, null);
					addSpawn(BAIUM_STONE, BAIUM_LOC, false, 0, false, npc.getInstanceId());
					setStatus(ALIVE);
				}
				else if (npc != null)
				{
					if (((_lastAttack + 300000) < System.currentTimeMillis()) && (npc.getCurrentHp() < (npc.getMaxHp() * 0.75)))
					{
						npc.setTarget(npc);
						npc.doCast(HEAL_OF_BAIUM.getSkill());
					}
					
					startQuestTimer("CHECK_ATTACK", 60000, npc, null);
				}
				break;
			}
			case "CLEAR_STATUS":
			{
				setStatus(ALIVE);
				addSpawn(BAIUM_STONE, BAIUM_LOC, false, 0);
				
				_hp70Triggered = false;
				_hp50Triggered = false;
				_sealingActive = false;
				_sealingWaveCount = 0;
				resetSealerIndices();
				removeSealingState();
				break;
			}
			case "CLEAR_ZONE":
			{
				
				Instance world = (npc != null) ? npc.getInstanceWorld() : null;
				if (world == null)
				{
					
					for (Instance inst : InstanceManager.getInstance().getInstances())
					{
						if (inst.getTemplateId() == INSTANCE_ID)
						{
							world = inst;
							break;
						}
					}
				}
				
				if (world != null)
				{
					
					for (Player playerInInstance : world.getPlayers())
					{
						if ((playerInInstance != null) && playerInInstance.isOnline())
						{
							playerInInstance.setInstance(null);
							playerInInstance.teleToLocation(TELEPORT_OUT_LOC[getRandom(TELEPORT_OUT_LOC.length)], null);
						}
					}
					
					world.destroy();
				}
				
				if (_baium != null)
				{
					cancelQuestTimer("APPLY_BARRIER_2", _baium, null);
					cancelQuestTimer("BAIUM_BARRIER_TIMEOUT_10MIN", _baium, null);
					cancelQuestTimer("BAIUM_BARRIER_REAPPLY_TIMER", _baium, null);
					cancelSealingTimers(_baium);
				}
				
				_baiumVulnerable = false;
				_baiumHasBossProtection = false;
				_baiumHasTimedBarrier = false;
				_baiumHits.clear();
				_darkAngelsSpawned = false;
				_angelsSpawned50 = false;
				_hp50Triggered = false;
				_hp70Triggered = false;
				_galaxiaFirstAttack = false;
				resetSealerIndices();
				break;
			}
			case "RESPAWN_BAIUM":
			{
				if (getStatus() == DEAD)
				{
					StatSet info = GrandBossManager.getInstance().getStatSet(BAIUM);
					info.set("respawn_time", System.currentTimeMillis());
					GrandBossManager.getInstance().setStatSet(BAIUM, info);
					GrandBossManager.getInstance().setStatus(BAIUM, ALIVE);
				}
				break;
			}
			case "ABORT_FIGHT":
			{
				if (getStatus() == IN_FIGHT)
				{
					_baium = null;
					notifyEvent("CLEAR_ZONE", null, null);
					notifyEvent("CLEAR_STATUS", null, null);
					// player.sendMessage(getClass().getSimpleName() + ": Aborting fight!");
					
					if (_baium != null)
					{
						cancelQuestTimer("APPLY_BARRIER_2", _baium, null);
						cancelQuestTimer("BAIUM_BARRIER_TIMEOUT_10MIN", _baium, null);
						cancelQuestTimer("BAIUM_BARRIER_REAPPLY_TIMER", _baium, null);
					}
					
					_baiumVulnerable = false;
					_baiumHasBossProtection = false;
					_baiumHasTimedBarrier = false;
					_baiumHits.clear();
					_darkAngelsSpawned = false;
					_angelsSpawned50 = false;
				}
				
				cancelQuestTimers("CHECK_ATTACK");
				break;
			}
			
			case "APPLY_BARRIER_2":
			{
				if ((npc != null) && (_baium != null) && (npc.getObjectId() == _baium.getObjectId()))
				{
					if (BAIUM_BARRIER.getSkill() != null)
					{
						BAIUM_BARRIER.getSkill().applyEffects(npc, npc);
						_baiumHasTimedBarrier = true;
						_baium.setInvul(true);
						_baiumHits.put(npc, 0);
						_lastBarrierHitTime = 0;
						startQuestTimer("BAIUM_BARRIER_TIMEOUT_10MIN", BAIUM_BARRIER_TIMEOUT_MS, npc, null);
					}
				}
				break;
			}
			case "BAIUM_BARRIER_TIMEOUT_10MIN":
			{
				if ((npc != null) && _baiumHasTimedBarrier && !_baiumVulnerable)
				{
					if (BAIUM_BARRIER.getSkill() != null)
					{
						npc.stopSkillEffects(SkillFinishType.REMOVED, BAIUM_BARRIER.getSkillId());
					}
					
					if (BAIUM_BOSS_PROTECTION.getSkill() != null)
					{
						BAIUM_BOSS_PROTECTION.getSkill().applyEffects(npc, npc);
						_baiumHasBossProtection = true;
						_baiumHasTimedBarrier = false;
						_baiumHits.put(npc, 0);
						_lastBarrierHitTime = 0;
					}
				}
				break;
			}
			case "BAIUM_BARRIER_REAPPLY_TIMER":
			{
				if ((npc != null) && _baiumVulnerable)
				{
					_baiumVulnerable = false;
					npc.setInvul(true);
					
					if (BAIUM_BARRIER.getSkill() != null)
					{
						BAIUM_BARRIER.getSkill().applyEffects(npc, npc);
						_baiumHasBossProtection = false;
						_baiumHasTimedBarrier = true;
						_baiumHits.put(npc, 0);
						_lastBarrierHitTime = 0;
						startQuestTimer("BAIUM_BARRIER_TIMEOUT_10MIN", BAIUM_BARRIER_TIMEOUT_MS, npc, null);
					}
				}
				break;
			}
			case "REAPPLY_BARRIER_VISUAL":
			{
				if ((npc != null) && (npc.getId() == BAIUM) && !npc.isDead())
				{
					if (_baiumHasBossProtection)
					{
						Skill protectionSkill = BAIUM_BOSS_PROTECTION.getSkill();
						if ((protectionSkill != null) && (npc.getEffectList().getBuffInfoBySkillId(protectionSkill.getId()) == null))
						{
							protectionSkill.applyEffects(npc, npc);
						}
					}
					else if (_baiumHasTimedBarrier)
					{
						Skill barrierSkill = BAIUM_BARRIER.getSkill();
						if ((barrierSkill != null) && (npc.getEffectList().getBuffInfoBySkillId(barrierSkill.getId()) == null))
						{
							barrierSkill.applyEffects(npc, npc);
						}
					}
				}
				break;
			}
			case "MANAGE_SKILLS":
			{
				if (npc != null)
				{
					manageSkills(npc);
				}
				break;
			}
		}
		
		return super.onEvent(event, npc, player);
	}
	
	@Override
	public void onSpawn(Npc npc)
	{
		switch (npc.getId())
		{
			case GALAXIA:
			{
				startQuestTimer("SELECT_TARGET", 5000, npc, null);
				npc.setRandomWalking(false);
				npc.setRandomAnimation(false);
				npc.setUndying(true);
				break;
			}
			case ANGEL_PIKEMAN:
			case ANGEL_ARCHER:
			{
				npc.setRandomWalking(false);
				npc.setRandomAnimation(false);
				break;
			}
			case ARCHANGEL_SEALER_ID:
			{
				npc.setRandomWalking(false);
				npc.setRandomAnimation(false);
				npc.disableCoreAI(false);
				break;
			}
		}
	}
	
	@Override
	public void onAttack(Npc npc, Player attacker, int damage, boolean isSummon, Skill skill)
	{
		_lastAttack = System.currentTimeMillis();
		switch (npc.getId())
		{
			case ARCHANGEL_SEALER_ID:
			{
				if (npc.getTarget() != _baium)
				{
					if (npc.isCastingNow() || npc.isAttackingNow())
					{
						npc.abortCast();
						npc.abortAttack();
					}
					
					npc.asAttackable().clearAggroList();
					npc.setTarget(_baium);
					npc.getAI().setIntention(Intention.IDLE);
				}
				
				if (!npc.isCastingNow() && (_baium != null) && !_baium.isDead())
				{
					Skill sealingSkill = DIVINE_SEALING.getSkill();
					if (sealingSkill != null)
					{
						npc.doCast(sealingSkill);
					}
				}
				return;
			}
			case BAIUM:
			{
				if ((npc.getCurrentHp() < (npc.getMaxHp() * 0.70)) && !_hp70Triggered)
				{
					_hp70Triggered = true;
					_sealingWaveCount = 0;
					resetSealerIndices();
					startQuestTimer("SEAL_START_WAVE", 1000, npc, null);
				}
				
				if ((npc.getCurrentHp() < (npc.getMaxHp() * 0.50)) && !_hp50Triggered)
				{
					_hp50Triggered = true;
					ZONE.broadcastPacket(new ExShowScreenMessage(NpcStringId.getNpcStringId(1804027), ExShowScreenMessage.TOP_CENTER, 7000, true)); // Fools! Now you will feel my true power!
				}
				
				if ((npc.getCurrentHp() < (npc.getMaxHp() * 0.75)) && !_darkAngelsSpawned)
				{
					_darkAngelsSpawned = true;
					int instanceId = npc.getInstanceId();
					for (Location loc : DARK_ANGEL_SPAWNS)
					{
						addSpawn(DARK_ANGEL, loc, false, 0, false, instanceId);
					}
				}
				
				if ((npc.getCurrentHp() < (npc.getMaxHp() * 0.50)) && !_angelsSpawned50)
				{
					_angelsSpawned50 = true;
					int instanceId = npc.getInstanceId();
					for (Location loc : ANGEL_PIKEMAN_SPAWNS)
					{
						addSpawn(ANGEL_PIKEMAN, loc, false, 0, false, instanceId);
					}
					
					for (Location loc : ANGEL_ARCHER_SPAWNS)
					{
						addSpawn(ANGEL_ARCHER, loc, false, 0, false, instanceId);
					}
				}
				
				if (!_baiumVulnerable)
				{
					if ((System.currentTimeMillis() - _lastBarrierHitTime) > 60000)
					{
						_baiumHits.put(npc, 0);
					}
					
					_lastBarrierHitTime = System.currentTimeMillis();
					
					final int hits = _baiumHits.merge(npc, 1, Integer::sum);
					
					if (_baiumHasBossProtection)
					{
						if (hits >= BAIUM_BARRIER_HIT_COUNT)
						{
							_baium.setInvul(true);
							if (BAIUM_BOSS_PROTECTION.getSkill() != null)
							{
								_baium.stopSkillEffects(SkillFinishType.REMOVED, BAIUM_BOSS_PROTECTION.getSkillId());
							}
							
							_baiumHasBossProtection = false;
							_baiumHits.put(npc, 0);
							_lastBarrierHitTime = 0;
							startQuestTimer("APPLY_BARRIER_2", 1000, npc, null);
						}
					}
					else if (_baiumHasTimedBarrier)
					{
						if (hits >= BAIUM_BARRIER_HIT_COUNT_VULN)
						{
							if (BAIUM_BARRIER.getSkill() != null)
							{
								_baium.stopSkillEffects(SkillFinishType.REMOVED, BAIUM_BARRIER.getSkillId());
							}
							
							_baiumHasTimedBarrier = false;
							_baiumVulnerable = true;
							npc.setInvul(false);
							_baiumHits.put(npc, 0);
							_lastBarrierHitTime = 0;
							cancelQuestTimer("BAIUM_BARRIER_TIMEOUT_10MIN", npc, null);
							long vulnerableTime = getRandom(BAIUM_BARRIER_VULN_MIN_MS, BAIUM_BARRIER_VULN_MAX_MS);
							startQuestTimer("BAIUM_BARRIER_REAPPLY_TIMER", vulnerableTime, npc, null);
						}
					}
					return;
				}
				
				if ((attacker.getMountType() == MountType.STRIDER) && !attacker.isAffectedBySkill(ANTI_STRIDER.getSkillId()) && !npc.isSkillDisabled(ANTI_STRIDER.getSkill()))
				{
					npc.setTarget(attacker);
					npc.doCast(ANTI_STRIDER.getSkill());
				}
				
				if (skill == null)
				{
					refreshAiParams(attacker, npc, (damage * 1000));
				}
				else if (npc.getCurrentHp() < (npc.getMaxHp() * 0.25))
				{
					refreshAiParams(attacker, npc, ((damage / 3) * 100));
				}
				else if (npc.getCurrentHp() < (npc.getMaxHp() * 0.5))
				{
					refreshAiParams(attacker, npc, (damage * 20));
				}
				else if (npc.getCurrentHp() < (npc.getMaxHp() * 0.75))
				{
					refreshAiParams(attacker, npc, (damage * 10));
				}
				else
				{
					refreshAiParams(attacker, npc, ((damage / 3) * 20));
				}
				
				manageSkills(npc);
				break;
			}
			case GALAXIA:
			{
				if (!_galaxiaFirstAttack)
				{
					_galaxiaFirstAttack = true;
					ZONE.broadcastPacket(new ExShowScreenMessage(NpcStringId.INTRUDERS_MUST_BE_ELIMINATED, ExShowScreenMessage.TOP_CENTER, 5000, true));
				}
				
				if (npc.getCurrentHp() <= 1)
				{
					npc.asAttackable().clearAggroList();
					return;
				}
				
				final Attackable mob = npc.asAttackable();
				final Creature mostHated = mob.getMostHated();
				if ((getRandom(100) < 10) && SkillCaster.checkUseConditions(mob, HOLY_SPEAR_AOE.getSkill()))
				{
					if ((mostHated != null) && (npc.calculateDistance3D(mostHated) < 1000) && ZONE.isCharacterInZone(mostHated))
					{
						mob.setTarget(mostHated);
						mob.doCast(HOLY_SPEAR_AOE.getSkill());
					}
					else if (ZONE.isCharacterInZone(attacker))
					{
						mob.setTarget(attacker);
						mob.doCast(HOLY_SPEAR_AOE.getSkill());
					}
				}
				break;
			}
		}
	}
	
	@Override
	public void onKill(Npc npc, Player killer, boolean isSummon)
	{
		switch (npc.getId())
		{
			case ARCHANGEL_SEALER_ID:
			{
				int spawnIndex = npc.getScriptValue();
				if (_liveSealerIndices.contains(spawnIndex))
				{
					_liveSealerIndices.remove(Integer.valueOf(spawnIndex));
				}
				
				if (_liveSealerIndices.isEmpty() && _sealingActive)
				{
					notifyEvent("SEAL_MECHANIC_CANCEL", _baium, null);
					for (Npc sealer : World.getInstance().getVisibleObjects(npc, Npc.class))
					{
						if (sealer.getId() == ARCHANGEL_SEALER_ID)
						{
							sealer.deleteMe();
						}
					}
				}
				return;
			}
			case BAIUM:
			{
				if (ZONE.isCharacterInZone(killer))
				{
					setStatus(DEAD);
					addSpawn(TELE_CUBE, TELEPORT_CUBIC_LOC, false, 900000, false, npc.getInstanceId());
					ZONE.broadcastPacket(new PlaySound("BS01_D"));
					
					scheduleNextSpawn();
					
					startQuestTimer("CLEAR_ZONE", 900000, npc, null);
					cancelQuestTimer("CHECK_ATTACK", npc, null);
					cancelQuestTimer("APPLY_BARRIER_2", npc, null);
					cancelQuestTimer("BAIUM_BARRIER_TIMEOUT_10MIN", npc, null);
					cancelQuestTimer("BAIUM_BARRIER_REAPPLY_TIMER", npc, null);
					cancelSealingTimers(npc);
					_baiumVulnerable = false;
					_baiumHasBossProtection = false;
					_baiumHasTimedBarrier = false;
					_baiumHits.clear();
					_darkAngelsSpawned = false;
					_angelsSpawned50 = false;
					_hp70Triggered = false;
					_hp50Triggered = false;
					_sealingActive = false;
					resetSealerIndices();
				}
				break;
			}
		}
	}
	
	private void cancelSealingTimers(Npc npc)
	{
		cancelQuestTimer("SEAL_START_WAVE", npc, null);
		cancelQuestTimer("SEAL_END_CAST", npc, null);
		cancelQuestTimer("SEAL_REAPPEAR", npc, null);
		cancelQuestTimer("SEALER_ACT", npc, null);
		
		for (Npc sealer : World.getInstance().getVisibleObjects(npc, Npc.class))
		{
			if (sealer.getId() == ARCHANGEL_SEALER_ID)
			{
				cancelQuestTimer("SEAL_CAST_END", sealer, null);
				cancelQuestTimer("SEALER_ACT", sealer, null);
				sealer.deleteMe();
			}
		}
		
		if (_baium != null)
		{
			_baium.stopSkillEffects(SkillFinishType.REMOVED, DIVINE_SEALING.getSkillId());
		}
	}
	
	private void saveSealingState(GrandBoss baium)
	{
		final StatSet info = GrandBossManager.getInstance().getStatSet(BAIUM);
		info.set("SealingWaveCount", _sealingWaveCount);
		GrandBossManager.getInstance().setStatSet(BAIUM, info);
	}
	
	private void removeSealingState()
	{
		final StatSet info = GrandBossManager.getInstance().getStatSet(BAIUM);
		info.remove("SealingWaveCount");
		GrandBossManager.getInstance().setStatSet(BAIUM, info);
	}
	
	@Override
	public void onSpellFinished(Npc npc, Player player, Skill skill)
	{
		startQuestTimer("MANAGE_SKILLS", 1000, npc, null);
		if (!ZONE.isCharacterInZone(npc) && (_baium != null))
		{
			_baium.teleToLocation(BAIUM_LOC);
		}
	}
	
	@Override
	public void unload(boolean removeFromList)
	{
		if (_baium != null)
		{
			_baium.deleteMe();
		}
		
		super.unload(removeFromList);
	}
	
	private void refreshAiParams(Creature attacker, Npc npc, int damage)
	{
		refreshAiParams(attacker, npc, damage, damage);
	}
	
	private void refreshAiParams(Creature attacker, Npc npc, int damage, int aggro)
	{
		final int newAggroVal = damage + getRandom(3000);
		final int aggroVal = aggro + 1000;
		final NpcVariables vars = npc.getVariables();
		for (int i = 0; i < 3; i++)
		{
			if (attacker == vars.getObject("c_quest" + i, Creature.class))
			{
				if (vars.getInt("i_quest" + i) < aggroVal)
				{
					vars.set("i_quest" + i, newAggroVal);
				}
				return;
			}
		}
		
		final int index = MathUtil.getIndexOfMinValue(vars.getInt("i_quest0"), vars.getInt("i_quest1"), vars.getInt("i_quest2"));
		vars.set("i_quest" + index, newAggroVal);
		vars.set("c_quest" + index, attacker);
	}
	
	private int getStatus()
	{
		return GrandBossManager.getInstance().getStatus(BAIUM);
	}
	
	private void addBoss(GrandBoss grandboss)
	{
		GrandBossManager.getInstance().addBoss(grandboss);
	}
	
	private void setStatus(int status)
	{
		GrandBossManager.getInstance().setStatus(BAIUM, status);
	}
	
	private void manageSkills(Npc npc)
	{
		if (npc.isCastingNow(SkillCaster::isAnyNormalType) || npc.isCoreAIDisabled() || !npc.isInCombat())
		{
			return;
		}
		
		final NpcVariables vars = npc.getVariables();
		for (int i = 0; i < 3; i++)
		{
			final Creature attacker = vars.getObject("c_quest" + i, Creature.class);
			if ((attacker == null) || ((npc.calculateDistance3D(attacker) > 9000) || attacker.isDead()))
			{
				vars.set("i_quest" + i, 0);
			}
		}
		
		final int index = MathUtil.getIndexOfMaxValue(vars.getInt("i_quest0"), vars.getInt("i_quest1"), vars.getInt("i_quest2"));
		final Creature creature = vars.getObject("c_quest" + index, Creature.class);
		final int i2 = vars.getInt("i_quest" + index);
		if ((i2 > 0) && (getRandom(100) < 70))
		{
			vars.set("i_quest" + index, 500);
		}
		
		SkillHolder skillToCast = null;
		if ((creature != null) && !creature.isDead())
		{
			final int chance = getRandom(100);
			if (npc.getCurrentHp() > (npc.getMaxHp() * 0.50))
			{
				if (chance < 10)
				{
					skillToCast = EXECUTION;
				}
				else if (chance < 25)
				{
					skillToCast = THUNDER_ROAR;
				}
				else
				{
					skillToCast = EMPERORS_WILL;
				}
			}
			else
			{
				if (chance < 40)
				{
					skillToCast = EXECUTION;
				}
				else if (chance < 70)
				{
					skillToCast = THUNDER_ROAR;
				}
				else
				{
					skillToCast = EMPERORS_WILL;
				}
			}
		}
		
		if ((skillToCast != null) && SkillCaster.checkUseConditions(npc, skillToCast.getSkill()))
		{
			if (skillToCast.getSkillId() == EXECUTION.getSkillId())
			{
				npc.setTarget(npc);
			}
			else
			{
				npc.setTarget(creature);
			}
			
			npc.doCast(skillToCast.getSkill());
		}
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		return npc.getId() + ".html";
	}
	
	public static void main(String[] args)
	{
		new Baium();
	}
}