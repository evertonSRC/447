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
package ai.bosses.Valakas;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.l2jmobius.gameserver.ai.Intention;
import org.l2jmobius.gameserver.data.xml.SkillData;
import org.l2jmobius.gameserver.managers.GrandBossManager;
import org.l2jmobius.gameserver.managers.ZoneManager;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.StatSet;
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Playable;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.enums.player.MountType;
import org.l2jmobius.gameserver.model.actor.instance.GrandBoss;
import org.l2jmobius.gameserver.model.script.Script;
import org.l2jmobius.gameserver.model.skill.BuffInfo;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.model.skill.holders.SkillHolder;
import org.l2jmobius.gameserver.model.zone.ZoneType;
import org.l2jmobius.gameserver.model.zone.type.NoRestartZone;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2jmobius.gameserver.network.serverpackets.MagicSkillUse;
import org.l2jmobius.gameserver.network.serverpackets.PlaySound;
import org.l2jmobius.gameserver.network.serverpackets.SocialAction;
import org.l2jmobius.gameserver.network.serverpackets.SpecialCamera;
import org.l2jmobius.gameserver.util.LocationUtil;

/**
 * Valakas AI.
 * @author Notorion
 */
public class Valakas extends Script
{
	// NPC
	private static final int VALAKAS = 29028; // Valakas Lv.118
	private static final int LAVASAURUS = 29029;
	
	// Configs
	private static final int VALAKAS_WAIT_TIME = 20; // // Minutes until Valakas appears (Lair)
	
	private static final int VALAKAS_SPAWN_INTERVAL = 192; // Hours - 8 days
	private static final int VALAKAS_SPAWN_RANDOM = 144; // Hours - 6 days
	
	// Skills Standard
	private static final int VALAKAS_REGENERATION = 4691;
	private static final SkillHolder VALAKAS_LAVA_SKIN = new SkillHolder(4680, 1); // Lava Skin
	private static final int VALAKAS_FEAR_ID = 4689; // Valakas: Fear Lv.3
	
	// New Mechanics Skills
	private static final SkillHolder VALAKAS_BERSERK_BUFF = new SkillHolder(5865, 1); // Valakas: Berserk Lv.1
	private static final SkillHolder DOOM_OF_BODY = new SkillHolder(5862, 1); // Doom of Body Lv.3
	private static final SkillHolder DOOM_OF_SOUL = new SkillHolder(5863, 1); // Doom of Soul Lv.3
	
	// Combat Skills
	// Stages 1 & 2 (100% - 60%)
	private static final SkillHolder[] VALAKAS_REGULAR_SKILLS =
	{
		new SkillHolder(4681, 1), // Valakas Trample
		new SkillHolder(4682, 1), // Valakas Trample
		new SkillHolder(4683, 1), // Valakas Dragon Breath
		new SkillHolder(4689, 1), // Valakas Fear
	};
	
	// Stages 3, 4 & 5 (60% - 0%) – Adds Meteor Storm (4690) to the rotation
	private static final SkillHolder[] VALAKAS_METEOR_SKILLS =
	{
		new SkillHolder(4681, 1), // Valakas Trample
		new SkillHolder(4682, 1), // Valakas Trample
		new SkillHolder(4683, 1), // Valakas Dragon Breath
		new SkillHolder(4689, 1), // Valakas Fear
		new SkillHolder(4690, 1), // Valakas Meteor Storm
	};
	
	private static final SkillHolder[] VALAKAS_AOE_SKILLS =
	{
		new SkillHolder(4683, 1), // Valakas Dragon Breath
		new SkillHolder(4684, 1), // Valakas Dragon Breath
		new SkillHolder(4685, 1), // Valakas Tail Stomp
		new SkillHolder(4686, 1), // Valakas Tail Stomp
		new SkillHolder(4688, 1), // Valakas Stun
		new SkillHolder(4689, 1), // Valakas Fear
		new SkillHolder(4690, 1), // Valakas Meteor Storm
	};
	
	// Locations
	private static final Location[] TELEPORT_CUBE_LOCATIONS =
	{
		new Location(214880, -116144, -1644),
		new Location(213696, -116592, -1644),
		new Location(212112, -116688, -1644),
		new Location(211184, -115472, -1664),
		new Location(210336, -114592, -1644),
		new Location(211360, -113904, -1644),
		new Location(213152, -112352, -1644),
		new Location(214032, -113232, -1644),
		new Location(214752, -114592, -1644),
		new Location(209824, -115568, -1421),
		new Location(210528, -112192, -1403),
		new Location(213120, -111136, -1408),
		new Location(215184, -111504, -1392),
		new Location(215456, -117328, -1392),
		new Location(213200, -118160, -1424)
	};
	
	private static final Location VALAKAS_HIDDEN_LOC = new Location(220963, -104895, -1620);
	private static final Location ATTACKER_REMOVE = new Location(150037, -57255, -2976);
	private static final Location VALAKAS_LAIR = new Location(212852, -114842, -1632, 63000); // Heading 63000 ensures Valakas faces the camera correctly at spawn
	private static final Location VALAKAS_REGENERATION_LOC = new Location(-105200, -253104, -15264);
	
	// Valakas status.
	private static final byte DORMANT = 0; // Valakas is spawned; no player has entered yet. Entry is open.
	private static final byte WAITING = 1; // A player entered; a 30-minute window opens for others. Entry remains open.
	private static final byte FIGHTING = 2; // Valakas is in combat. Entry is closed.
	private static final byte DEAD = 3; // Valakas is defeated. Entry is closed.
	
	// Messages
	private static final NpcStringId VALAKAS_CHALLENGE_MSG = NpcStringId.getNpcStringId(1000519); // [$s1, you arrogant fool! You dare challenge me, the Ruler of Flames?]
	private static final NpcStringId VALAKAS_FURY_MSG = NpcStringId.getNpcStringId(1801075); // Because the cowardly counterattacks continued, Valakas' fury has reached its maximum!
	
	// Misc
	private static final ZoneType BOSS_ZONE = ZoneManager.getInstance().getZoneById(12010);
	private static final NoRestartZone GROUND_ZONE = ZoneManager.getInstance().getZoneById(13010, NoRestartZone.class);
	
	private GrandBoss _valakas = null;
	private Playable _actualVictim; // Current target Valakas is focusing.
	private long _timeTracker = 0; // Timestamp of the last damage received by Valakas (inactivity watchdog).
	private int _lastSkillId = 0; // Tracks last skill to reduce consecutive Fear casts.
	
	// State flags & logic (Reworked v2019).
	private boolean _hp30Triggered = false;
	
	// Phase flags for Lava Skin activation thresholds.
	private boolean _phase80Triggered = false;
	private boolean _phase60Triggered = false;
	private boolean _phase40Triggered = false;
	private boolean _phase20Triggered = false;
	
	// Berserk & Doom logic (Reworked v2019).
	private final List<Player> _doomVictims = new CopyOnWriteArrayList<>();
	private boolean _isBerserkActive = false;
	private long _berserkStartTime = 0;
	
	private boolean _berserkPending = false;
	
	public Valakas()
	{
		addSpawnId(VALAKAS);
		addAttackId(VALAKAS);
		addKillId(VALAKAS);
		
		final StatSet info = GrandBossManager.getInstance().getStatSet(VALAKAS);
		final int status = GrandBossManager.getInstance().getStatus(VALAKAS);
		switch (status)
		{
			case DEAD:
			{
				final long temp = info.getLong("respawn_time") - System.currentTimeMillis();
				if (temp > 0)
				{
					startQuestTimer("valakas_unlock", temp, null, null);
				}
				else
				{
					// Respawn time expired while the server was offline; spawn hidden and set to dormant.
					_valakas = (GrandBoss) addSpawn(VALAKAS, VALAKAS_REGENERATION_LOC, false, 0);
					_valakas.teleToLocation(VALAKAS_HIDDEN_LOC);
					GrandBossManager.getInstance().setStatus(VALAKAS, DORMANT);
					GrandBossManager.getInstance().addBoss(_valakas);
					_valakas.setInvul(true);
					_valakas.setRunning();
					_valakas.getAI().setIntention(Intention.IDLE);
				}
				break;
			}
			case DORMANT:
			{
				_valakas = (GrandBoss) addSpawn(VALAKAS, VALAKAS_REGENERATION_LOC, false, 0);
				_valakas.teleToLocation(VALAKAS_HIDDEN_LOC);
				GrandBossManager.getInstance().addBoss(_valakas);
				_valakas.setInvul(true);
				_valakas.setRunning();
				_valakas.getAI().setIntention(Intention.IDLE);
				break;
			}
			case WAITING:
			{
				_valakas = (GrandBoss) addSpawn(VALAKAS, VALAKAS_REGENERATION_LOC, false, 0);
				_valakas.teleToLocation(VALAKAS_HIDDEN_LOC);
				GrandBossManager.getInstance().addBoss(_valakas);
				_valakas.setInvul(true);
				_valakas.setRunning();
				_valakas.getAI().setIntention(Intention.IDLE);
				startQuestTimer("beginning", VALAKAS_WAIT_TIME * 60000, _valakas, null);
				break;
			}
			case FIGHTING:
			{
				final int loc_x = info.getInt("loc_x");
				final int loc_y = info.getInt("loc_y");
				final int loc_z = info.getInt("loc_z");
				final int heading = info.getInt("heading");
				final double hp = info.getDouble("currentHP");
				final double mp = info.getDouble("currentMP");
				_valakas = (GrandBoss) addSpawn(VALAKAS, loc_x, loc_y, loc_z, heading, false, 0);
				GrandBossManager.getInstance().addBoss(_valakas);
				_valakas.setCurrentHpMp(hp, mp);
				_valakas.setRunning();
				
				// Start timers.
				_timeTracker = System.currentTimeMillis();
				startQuestTimer("regen_task", 60000, _valakas, null, true);
				startQuestTimer("skill_task", 2000, _valakas, null, true);
				
				// Reworked v2019: Recovery logic when reloading during combat.
				double hpPercent = _valakas.getCurrentHpPercent();
				if (hpPercent <= 30)
				{
					_hp30Triggered = true;
					startQuestTimer("SPAWN_LAVASAURUS_WAVE", 10000, _valakas, null);
					startQuestTimer("BERSERK_CYCLE", 10000, _valakas, null);
				}
				else
				{
					// Normal cycle if HP > 30% (starts 5 minutes later).
					startQuestTimer("BERSERK_CYCLE", 300000, _valakas, null);
				}
				
				if (hpPercent <= 80)
				{
					_phase80Triggered = true;
				}
				
				if (hpPercent <= 60)
				{
					_phase60Triggered = true;
				}
				
				if (hpPercent <= 40)
				{
					_phase40Triggered = true;
				}
				
				if (hpPercent <= 20)
				{
					_phase20Triggered = true;
				}
				break;
			}
			default:
			{
				_valakas.teleToLocation(VALAKAS_HIDDEN_LOC);
				_valakas.setInvul(true);
				_valakas.getAI().setIntention(Intention.IDLE);
				break;
			}
		}
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		switch (event)
		{
			case "valakas_unlock":
			{
				GrandBossManager.getInstance().setStatus(VALAKAS, DORMANT);
				_valakas = (GrandBoss) addSpawn(VALAKAS, VALAKAS_REGENERATION_LOC, false, 0);
				_valakas.teleToLocation(VALAKAS_HIDDEN_LOC);
				GrandBossManager.getInstance().addBoss(_valakas);
				_valakas.setInvul(true);
				_valakas.setRunning();
				_valakas.getAI().setIntention(Intention.IDLE);
				break;
			}
			case "beginning":
			{
				// Store the current timestamp (used for inactivity reset).
				_timeTracker = System.currentTimeMillis();
				
				// Prevents rotated/invisible boss issues during the intro sequence.
				npc.teleToLocation(VALAKAS_LAIR);
				
				startQuestTimer("broadcast_spawn", 100, npc, null);
				// Launch the cinematic, and tasks (regen + skill).
				startQuestTimer("spawn_1", 1700, npc, null);
				startQuestTimer("spawn_2", 3200, npc, null);
				startQuestTimer("spawn_3", 6500, npc, null);
				startQuestTimer("spawn_4", 9400, npc, null);
				startQuestTimer("spawn_5", 12100, npc, null);
				startQuestTimer("spawn_6", 12430, npc, null);
				startQuestTimer("spawn_7", 15430, npc, null);
				startQuestTimer("spawn_8", 16830, npc, null);
				startQuestTimer("spawn_9", 23530, npc, null);
				startQuestTimer("spawn_10", 26000, npc, null);
				startQuestTimer("challenge_message", 28500, npc, null);
				break;
			}
			case "regen_task":
			{
				// Inactivity check – 15 minutes without damage while fighting.
				if ((GrandBossManager.getInstance().getStatus(VALAKAS) == FIGHTING) && ((_timeTracker + 900000) < System.currentTimeMillis()))
				{
					npc.getAI().setIntention(Intention.IDLE);
					npc.teleToLocation(VALAKAS_REGENERATION_LOC);
					GrandBossManager.getInstance().setStatus(VALAKAS, DORMANT);
					npc.setCurrentHpMp(npc.getMaxHp(), npc.getMaxMp());
					
					// Reworked 2025: Clean up Lavasaurus minions on reset.
					World.getInstance().forEachVisibleObject(npc, Npc.class, minion ->
					{
						if ((minion != null) && (minion.getId() == LAVASAURUS) && !minion.isDead())
						{
							minion.deleteMe();
						}
					});
					
					// Oust all players from the zone.
					BOSS_ZONE.oustAllPlayers();
					
					// Cancel all active tasks.
					cancelQuestTimer("regen_task", npc, null);
					cancelQuestTimer("skill_task", npc, null);
					
					// Cancel new mechanics timers.
					cancelQuestTimer("SPAWN_LAVASAURUS_WAVE", npc, null);
					cancelQuestTimer("BERSERK_CYCLE", npc, null);
					cancelQuestTimer("DOOM_MECHANIC_END", npc, null);
					cancelQuestTimer("DOOM_TICK", npc, null);
					cancelQuestTimer("DOOM_SOUL_DELAYED", npc, null);
					return null;
				}
				
				// Check if "Valakas Regeneration" is already active.
				final BuffInfo info = npc.getEffectList().getBuffInfoBySkillId(VALAKAS_REGENERATION);
				final int level = info != null ? info.getSkill().getLevel() : 0;
				
				// HP < 25% → apply Regeneration Lv. 4.
				if ((npc.getCurrentHp() < (npc.getMaxHp() / 4)) && (level != 4))
				{
					npc.setTarget(npc);
					npc.doCast(SkillData.getInstance().getSkill(VALAKAS_REGENERATION, 4));
				}
				// HP < 50% → apply Regeneration Lv. 3.
				else if ((npc.getCurrentHp() < ((npc.getMaxHp() * 2) / 4.0)) && (level != 3))
				{
					npc.setTarget(npc);
					npc.doCast(SkillData.getInstance().getSkill(VALAKAS_REGENERATION, 3));
				}
				// HP < 75% → apply Regeneration Lv. 2.
				else if ((npc.getCurrentHp() < ((npc.getMaxHp() * 3) / 4.0)) && (level != 2))
				{
					npc.setTarget(npc);
					npc.doCast(SkillData.getInstance().getSkill(VALAKAS_REGENERATION, 2));
				}
				else if (level != 1)
				{
					npc.setTarget(npc);
					npc.doCast(SkillData.getInstance().getSkill(VALAKAS_REGENERATION, 1));
				}
				startQuestTimer("regen_task", 60000, npc, null, true);
				break;
			}
			case "broadcast_spawn":
			{
				// Prevent Valakas from acting during the cinematic intro.
				npc.setInvul(true); // Invulnerable.
				npc.disableAllSkills(); // No skills.
				npc.setTargetable(false); // Cannot be targeted.
				npc.setImmobilized(true); // No movement or physical attacks.
				
				for (Player plyr : BOSS_ZONE.getPlayersInside())
				{
					// Ensure players see the cinematic; if skipped, Valakas appears correctly (avoids old invisible attack bugs).
					plyr.sendPacket(new SpecialCamera(npc, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0));
					plyr.sendPacket(new PlaySound(1, "BS03_A", 0, 0, 0, 0, 0));
					plyr.sendPacket(new SocialAction(npc.getObjectId(), 3));
				}
				break;
			}
			case "spawn_1":
			{
				BOSS_ZONE.broadcastPacket(new SpecialCamera(npc, 1800, 180, -1, 1500, 15000, 10000, 0, 0, 1, 0, 0));
				break;
			}
			case "spawn_2":
			{
				BOSS_ZONE.broadcastPacket(new SpecialCamera(npc, 1300, 180, -5, 3000, 15000, 10000, 0, -5, 1, 0, 0));
				break;
			}
			case "spawn_3":
			{
				BOSS_ZONE.broadcastPacket(new SpecialCamera(npc, 500, 180, -8, 600, 15000, 10000, 0, 60, 1, 0, 0));
				break;
			}
			case "spawn_4":
			{
				BOSS_ZONE.broadcastPacket(new SpecialCamera(npc, 800, 180, -8, 2700, 15000, 10000, 0, 30, 1, 0, 0));
				break;
			}
			case "spawn_5":
			{
				BOSS_ZONE.broadcastPacket(new SpecialCamera(npc, 200, 250, 70, 0, 15000, 10000, 30, 80, 1, 0, 0));
				break;
			}
			case "spawn_6":
			{
				BOSS_ZONE.broadcastPacket(new SpecialCamera(npc, 1100, 250, 70, 2500, 15000, 10000, 30, 80, 1, 0, 0));
				break;
			}
			case "spawn_7":
			{
				BOSS_ZONE.broadcastPacket(new SpecialCamera(npc, 700, 150, 30, 0, 15000, 10000, -10, 60, 1, 0, 0));
				break;
			}
			case "spawn_8":
			{
				BOSS_ZONE.broadcastPacket(new SpecialCamera(npc, 1200, 150, 20, 2900, 15000, 10000, -10, 30, 1, 0, 0));
				break;
			}
			case "spawn_9":
			{
				BOSS_ZONE.broadcastPacket(new SpecialCamera(npc, 750, 170, -10, 3400, 15000, 4000, 10, -15, 1, 0, 0));
				break;
			}
			case "spawn_10":
			{
				GrandBossManager.getInstance().setStatus(VALAKAS, FIGHTING);
				
				// Revert cinematic restrictions and enable combat.
				npc.setInvul(false);
				npc.setImmobilized(false);
				npc.enableAllSkills();
				npc.setTargetable(true);
				npc.getAI().setIntention(Intention.ACTIVE);
				
				// Force visual update in case the cinematic was skipped.
				npc.broadcastInfo();
				_hp30Triggered = false;
				_phase80Triggered = false;
				_phase60Triggered = false;
				_phase40Triggered = false;
				_phase20Triggered = false;
				_isBerserkActive = false;
				_doomVictims.clear();
				_lastSkillId = 0;
				_berserkPending = false;
				startQuestTimer("start_ai_tasks", 2000, npc, null);
				break;
			}
			case "start_ai_tasks":
			{
				if (GrandBossManager.getInstance().getStatus(VALAKAS) == FIGHTING)
				{
					startQuestTimer("regen_task", 60000, npc, null, true);
					startQuestTimer("skill_task", 2000, npc, null, true);
					startQuestTimer("BERSERK_CYCLE", 300000, npc, null);
				}
				break;
			}
			case "challenge_message":
			{
				if (GrandBossManager.getInstance().getStatus(VALAKAS) == FIGHTING)
				{
					final Playable target = getRandomTarget(npc);
					if (target != null)
					{
						BOSS_ZONE.broadcastPacket(new ExShowScreenMessage(VALAKAS_CHALLENGE_MSG, ExShowScreenMessage.TOP_CENTER, 10000, true, target.getName()));
					}
				}
				break;
			}
			case "BERSERK_CYCLE":
			{
				if (GrandBossManager.getInstance().getStatus(VALAKAS) != FIGHTING)
				{
					return null;
				}
				// Flag as pending so Berserk is prioritized in the next skill selection.
				_berserkPending = true;
				BOSS_ZONE.broadcastPacket(new ExShowScreenMessage(VALAKAS_FURY_MSG, ExShowScreenMessage.TOP_CENTER, 10000, true));
				
				// Mark Berserk start time to drive Doom progression.
				_isBerserkActive = true;
				_berserkStartTime = System.currentTimeMillis();
				
				// Activate Doom only if HP ≤ 30%.
				if (npc.getCurrentHpPercent() <= 30)
				{
					_doomVictims.clear();
					
					// Start Doom ticks.
					startQuestTimer("DOOM_TICK", 3000, npc, null);
					
					// End mechanic after 1m45s.
					startQuestTimer("DOOM_MECHANIC_END", 105000, npc, null);
				}
				
				// Reschedule next Berserk cycle in 5 minutes.
				startQuestTimer("BERSERK_CYCLE", 300000, npc, null);
				break;
			}
			case "DOOM_TICK":
			{
				if (!_isBerserkActive || (GrandBossManager.getInstance().getStatus(VALAKAS) != FIGHTING))
				{
					return null;
				}
				
				// Progressive targeting: 5 → 3 → 2 → 1 victims as time passes.
				long elapsed = System.currentTimeMillis() - _berserkStartTime;
				int maxVictims = 1;
				if (elapsed < 15000)
				{
					maxVictims = 5;
				}
				else if (elapsed < 45000)
				{
					maxVictims = 3;
				}
				else if (elapsed < 75000)
				{
					maxVictims = 2;
				}
				else
				{
					maxVictims = 1;
				}
				
				// Remove invalid/out-of-zone targets.
				_doomVictims.removeIf(victim -> (victim == null) || victim.isDead() || !victim.isOnline() || !BOSS_ZONE.isInsideZone(victim) || (npc.calculateDistance3D(victim) > 2000));
				while (_doomVictims.size() > maxVictims)
				{
					_doomVictims.remove(0);
				}
				
				// Select new victims to apply Doom debuffs.
				if (_doomVictims.size() < maxVictims)
				{
					List<Player> candidates = new ArrayList<>();
					for (Player p : World.getInstance().getVisibleObjectsInRange(npc, Player.class, 2000))
					{
						if ((p != null) && !p.isDead() && BOSS_ZONE.isInsideZone(p) && !_doomVictims.contains(p))
						{
							candidates.add(p);
						}
					}
					while ((_doomVictims.size() < maxVictims) && !candidates.isEmpty())
					{
						_doomVictims.add(candidates.remove(getRandom(candidates.size())));
					}
				}
				
				// Apply Doom of Body immediately.
				for (Player victim : _doomVictims)
				{
					if (DOOM_OF_BODY.getSkill() != null)
					{
						DOOM_OF_BODY.getSkill().applyEffects(npc, victim);
						npc.broadcastPacket(new MagicSkillUse(npc, victim, DOOM_OF_BODY.getSkillId(), 1, 0, 0));
					}
				}
				
				// Doom of Soul: 50% immediate, 50% delayed by 2s.
				if (getRandom(2) == 0)
				{
					for (Player victim : _doomVictims)
					{
						if (DOOM_OF_SOUL.getSkill() != null)
						{
							DOOM_OF_SOUL.getSkill().applyEffects(npc, victim);
							npc.broadcastPacket(new MagicSkillUse(npc, victim, DOOM_OF_SOUL.getSkillId(), 1, 0, 0));
						}
					}
				}
				else
				{
					startQuestTimer("DOOM_SOUL_DELAYED", 2000, npc, null);
				}
				startQuestTimer("DOOM_TICK", 3000, npc, null);
				break;
			}
			case "DOOM_SOUL_DELAYED":
			{
				if (!_isBerserkActive || (GrandBossManager.getInstance().getStatus(VALAKAS) != FIGHTING))
				{
					return null;
				}
				
				// Apply delayed Doom of Soul to current victims.
				if (DOOM_OF_SOUL.getSkill() != null)
				{
					for (Player victim : _doomVictims)
					{
						if ((victim != null) && !victim.isDead() && BOSS_ZONE.isInsideZone(victim))
						{
							DOOM_OF_SOUL.getSkill().applyEffects(npc, victim);
							npc.broadcastPacket(new MagicSkillUse(npc, victim, DOOM_OF_SOUL.getSkillId(), 1, 0, 0));
						}
					}
				}
				break;
			}
			case "DOOM_MECHANIC_END":
			{
				cancelQuestTimer("DOOM_TICK", npc, null);
				cancelQuestTimer("DOOM_SOUL_DELAYED", npc, null);
				_doomVictims.clear();
				break;
			}
			case "SPAWN_LAVASAURUS_WAVE":
			{
				if (GrandBossManager.getInstance().getStatus(VALAKAS) != FIGHTING)
				{
					return null;
				}
				
				final int totalMobs = 21;
				final int spacing = 100;
				final int cols = 6;
				final int rows = (int) Math.ceil((double) totalMobs / cols);
				final int startX = npc.getX() - ((cols * spacing) / 2);
				final int startY = npc.getY() - ((rows * spacing) / 2);
				final int z = npc.getZ();
				for (int i = 0; i < totalMobs; i++)
				{
					int r = i / cols;
					int c = i % cols;
					final int x = startX + (c * spacing);
					final int y = startY + (r * spacing);
					addSpawn(LAVASAURUS, x, y, z, npc.getHeading(), false, 0);
				}
				startQuestTimer("SPAWN_LAVASAURUS_WAVE", 180000, npc, null);
				break;
			}
			case "die_1":
			{
				BOSS_ZONE.broadcastPacket(new SpecialCamera(npc, 2000, 130, -1, 0, 15000, 10000, 0, 0, 1, 1, 0));
				break;
			}
			case "die_2":
			{
				BOSS_ZONE.broadcastPacket(new SpecialCamera(npc, 1100, 210, -5, 3000, 15000, 10000, -13, 0, 1, 1, 0));
				break;
			}
			case "die_3":
			{
				BOSS_ZONE.broadcastPacket(new SpecialCamera(npc, 1300, 200, -8, 3000, 15000, 10000, 0, 15, 1, 1, 0));
				break;
			}
			case "die_4":
			{
				BOSS_ZONE.broadcastPacket(new SpecialCamera(npc, 1000, 190, 0, 500, 15000, 10000, 0, 10, 1, 1, 0));
				break;
			}
			case "die_5":
			{
				BOSS_ZONE.broadcastPacket(new SpecialCamera(npc, 1700, 120, 0, 2500, 15000, 10000, 12, 40, 1, 1, 0));
				break;
			}
			case "die_6":
			{
				BOSS_ZONE.broadcastPacket(new SpecialCamera(npc, 1700, 20, 0, 700, 15000, 10000, 10, 10, 1, 1, 0));
				break;
			}
			case "die_7":
			{
				BOSS_ZONE.broadcastPacket(new SpecialCamera(npc, 1700, 10, 0, 1000, 15000, 10000, 20, 70, 1, 1, 0));
				break;
			}
			case "die_8":
			{
				BOSS_ZONE.broadcastPacket(new SpecialCamera(npc, 1700, 10, 0, 300, 15000, 250, 20, -20, 1, 1, 0));
				for (Location loc : TELEPORT_CUBE_LOCATIONS)
				{
					addSpawn(31759, loc, false, 900000); // 15 minutes cube duration.
				}
				startQuestTimer("remove_players", 900000, null, null); // 15 minutes to expel players.
				break;
			}
			case "skill_task":
			{
				callSkillAI(npc);
				break;
			}
			case "remove_players":
			{
				BOSS_ZONE.oustAllPlayers();
				break;
			}
		}
		
		return super.onEvent(event, npc, player);
	}
	
	@Override
	public void onSpawn(Npc npc)
	{
		npc.asAttackable().setCanReturnToSpawnPoint(false);
		npc.setRandomWalking(false);
	}
	
	@Override
	public void onAttack(Npc npc, Player attacker, int damage, boolean isSummon)
	{
		if (npc.isInvul())
		{
			attacker.doDie(attacker);
			return;
		}
		
		if (npc.isHpBlocked())
		{
			return;
		}
		
		if (GrandBossManager.getInstance().getStatus(VALAKAS) != FIGHTING)
		{
			attacker.teleToLocation(ATTACKER_REMOVE);
			return;
		}
		
		// 30% HP trigger (Lavasaurus spawn only).
		if ((npc.getCurrentHpPercent() <= 30) && !_hp30Triggered)
		{
			_hp30Triggered = true;
			startQuestTimer("SPAWN_LAVASAURUS_WAVE", 500, npc, null);
			cancelQuestTimer("BERSERK_CYCLE", npc, null);
			startQuestTimer("BERSERK_CYCLE", 500, npc, null);
		}
		
		// Lava Skin phase logic.
		final double hpPercent = npc.getCurrentHpPercent();
		
		// Stage 2 start (≤ 80% HP).
		if ((hpPercent <= 80) && !_phase80Triggered)
		{
			_phase80Triggered = true;
			VALAKAS_LAVA_SKIN.getSkill().applyEffects(npc, npc);
		}
		
		// Stage 3 start (≤ 60% HP).
		if ((hpPercent <= 60) && !_phase60Triggered)
		{
			_phase60Triggered = true;
			VALAKAS_LAVA_SKIN.getSkill().applyEffects(npc, npc);
		}
		
		// Stage 4 start (≤ 40% HP).
		if ((hpPercent <= 40) && !_phase40Triggered)
		{
			_phase40Triggered = true;
			VALAKAS_LAVA_SKIN.getSkill().applyEffects(npc, npc);
		}
		
		// Stage 5 start (≤ 20% HP).
		if ((hpPercent <= 20) && !_phase20Triggered)
		{
			_phase20Triggered = true;
			VALAKAS_LAVA_SKIN.getSkill().applyEffects(npc, npc);
		}
		
		if ((attacker.getMountType() == MountType.STRIDER) && !attacker.isAffectedBySkill(4258))
		{
			npc.setTarget(attacker);
			npc.doCast(SkillData.getInstance().getSkill(4258, 1));
		}
		
		_timeTracker = System.currentTimeMillis();
	}
	
	@Override
	public void onKill(Npc npc, Player killer, boolean isSummon)
	{
		cancelQuestTimer("regen_task", npc, null);
		cancelQuestTimer("skill_task", npc, null);
		cancelQuestTimer("SPAWN_LAVASAURUS_WAVE", npc, null);
		cancelQuestTimer("BERSERK_CYCLE", npc, null);
		cancelQuestTimer("DOOM_MECHANIC_END", npc, null);
		cancelQuestTimer("DOOM_TICK", npc, null);
		cancelQuestTimer("DOOM_SOUL_DELAYED", npc, null);
		_doomVictims.clear();
		World.getInstance().forEachVisibleObject(npc, Npc.class, minion ->
		{
			if ((minion != null) && (minion.getId() == LAVASAURUS) && !minion.isDead())
			{
				minion.deleteMe();
			}
		});
		
		BOSS_ZONE.broadcastPacket(new PlaySound(1, "B03_D", 0, 0, 0, 0, 0));
		BOSS_ZONE.broadcastPacket(new SpecialCamera(npc, 1200, 20, -10, 0, 10000, 13000, 0, 0, 0, 0, 0));
		
		startQuestTimer("die_1", 300, npc, null); // 300
		startQuestTimer("die_2", 600, npc, null); // 300
		startQuestTimer("die_3", 3800, npc, null); // 3200
		startQuestTimer("die_4", 8200, npc, null); // 4400
		startQuestTimer("die_5", 8700, npc, null); // 500
		startQuestTimer("die_6", 13300, npc, null); // 4600
		startQuestTimer("die_7", 14000, npc, null); // 700
		startQuestTimer("die_8", 16500, npc, null); // 2500
		
		GrandBossManager.getInstance().setStatus(VALAKAS, DEAD);
		
		final long baseIntervalMillis = VALAKAS_SPAWN_INTERVAL * 3600000L;
		final long randomRangeMillis = VALAKAS_SPAWN_RANDOM * 3600000L;
		final long respawnTime = baseIntervalMillis + getRandom(-randomRangeMillis, randomRangeMillis);
		startQuestTimer("valakas_unlock", respawnTime, null, null);
		
		final StatSet info = GrandBossManager.getInstance().getStatSet(VALAKAS);
		info.set("respawn_time", System.currentTimeMillis() + respawnTime);
		GrandBossManager.getInstance().setStatSet(VALAKAS, info);
		
	}
	
	@Override
	public void onSpellFinished(Npc npc, Player player, Skill skill)
	{
		startQuestTimer("skill_task", 2000, npc, null);
		_lastSkillId = skill.getId();
		if (!GROUND_ZONE.isCharacterInZone(npc) && (_valakas != null))
		{
			_valakas.teleToLocation(VALAKAS_LAIR);
		}
	}
	
	private void callSkillAI(Npc npc)
	{
		if (npc.isInvul() || npc.isCastingNow())
		{
			return;
		}
		
		if (_berserkPending && (VALAKAS_BERSERK_BUFF.getSkill() != null))
		{
			npc.setTarget(npc);
			npc.doCast(VALAKAS_BERSERK_BUFF.getSkill());
			_berserkPending = false;
			return;
		}
		
		if ((_actualVictim == null) || _actualVictim.isDead() || !(npc.isInSurroundingRegion(_actualVictim)) || _actualVictim.isAffectedBySkill(VALAKAS_FEAR_ID) || (getRandom(10) == 0))
		{
			_actualVictim = getRandomTarget(npc);
		}
		
		final Skill skill = getRandomSkill(npc).getSkill();
		if (LocationUtil.checkIfInRange((skill.getCastRange() < 600) ? 600 : skill.getCastRange(), npc, _actualVictim, true))
		{
			npc.getAI().setIntention(Intention.IDLE);
			npc.setTarget(_actualVictim);
			npc.doCast(skill);
		}
		else
		{
			npc.getAI().setIntention(Intention.FOLLOW, _actualVictim);
			npc.setTarget(_actualVictim);
		}
	}
	
	private SkillHolder getRandomSkill(Npc npc)
	{
		final int hpRatio = (int) ((npc.getCurrentHp() / npc.getMaxHp()) * 100);
		
		SkillHolder skillHolder;
		int attempts = 0;
		do
		{
			if (World.getInstance().getVisibleObjectsInRange(npc, Player.class, 1200).size() >= 20)
			{
				skillHolder = getRandomEntry(VALAKAS_AOE_SKILLS);
			}
			else if (hpRatio > 60)
			{
				skillHolder = getRandomEntry(VALAKAS_REGULAR_SKILLS);
			}
			else
			{
				skillHolder = getRandomEntry(VALAKAS_METEOR_SKILLS);
			}
			attempts++;
		}
		while ((skillHolder.getSkillId() == VALAKAS_FEAR_ID) && (_lastSkillId == VALAKAS_FEAR_ID) && (attempts < 5));
		
		return skillHolder;
	}
	
	// Smart AI: prefers targets not under the Fear debuff; falls back to any valid target.
	private Playable getRandomTarget(Npc npc)
	{
		final List<Playable> result = new ArrayList<>();
		final List<Playable> nonFearedResult = new ArrayList<>();
		World.getInstance().forEachVisibleObject(npc, Playable.class, obj ->
		{
			if ((obj != null) && !obj.isDead() && !obj.isInvisible() && !obj.isPet() && obj.isPlayable())
			{
				result.add(obj);
				if (!obj.isAffectedBySkill(VALAKAS_FEAR_ID))
				{
					nonFearedResult.add(obj);
				}
			}
		});
		
		// Targeting logic: prefer non-feared players; fallback to any valid target.
		if (!nonFearedResult.isEmpty())
		{
			return getRandomEntry(nonFearedResult);
		}
		
		return getRandomEntry(result);
	}
	
	public static void main(String[] args)
	{
		new Valakas();
	}
}