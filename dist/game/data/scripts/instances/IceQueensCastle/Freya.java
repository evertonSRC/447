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
package instances.IceQueensCastle;

import java.util.List;

import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.groups.Party;
import org.l2jmobius.gameserver.model.instancezone.Instance;
import org.l2jmobius.gameserver.model.item.holders.ItemHolder;
import org.l2jmobius.gameserver.model.script.InstanceScript;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.model.skill.SkillCaster;
import org.l2jmobius.gameserver.model.skill.holders.SkillHolder;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.enums.Movie;
import org.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;

/**
 * @author Tanatos
 * @URL https://www.youtube.com/watch?v=3l9JuxM5lk0&t
 * @URL https://l2central.info/main/locations/instance_zones/party/ice_queen_castle/
 */
public class Freya extends InstanceScript
{
	// NPCs
	private static final int BENUSTA = 34542;
	private static final int FREYA_THRONE = 26511;
	private static final int FREYA_WAITING = 26512;
	private static final int FREYA_STAND = 26513;
	private static final int GLAKIAS = 26514;
	private static final int WINTER_CRYSTAL = 26518;
	private static final int ICE_WALL = 26519;
	private static final int HUGE_ICICLE = 26520;
	
	// Skills
	private static final SkillHolder ICE_BOLT = new SkillHolder(34437, 1); // Queen's Ice Bolt
	private static final SkillHolder FREEZING_BOLT = new SkillHolder(34437, 2); // Queen's Freezing Bolt
	private static final SkillHolder ICE_STORM = new SkillHolder(34438, 1); // Queen's Ice Storm
	private static final SkillHolder FREEZING_STORM = new SkillHolder(34438, 2); // Queen's Freezing Storm
	private static final SkillHolder ICE_HURRICANE = new SkillHolder(34439, 1); // Queen's Ice Hurricane
	private static final SkillHolder FREEZING_HURRICANE = new SkillHolder(34439, 2); // Queen's Freezing Hurricane
	private static final SkillHolder FREEZING_BLIZARD = new SkillHolder(34440, 1); // Queen's Freezing Blizzard
	private static final SkillHolder FREEZING_QUEEN_WAVE = new SkillHolder(34441, 2); // Queen's Freezing Wave
	private static final SkillHolder ETERNAL_BLIZZARD = new SkillHolder(34442, 1); // Eternal Blizzard
	private static final SkillHolder FREEZING_STRIKE = new SkillHolder(34443, 1); // Freezing Strike
	private static final SkillHolder FREEZING_SMASH = new SkillHolder(34444, 1); // Freezing Smash
	private static final SkillHolder FREEZING_WAVE = new SkillHolder(34445, 1); // Freezing Wave
	private static final SkillHolder FINAL_FREEZING = new SkillHolder(34447, 1); // Final Freezing Burst
	private static final SkillHolder FREEZING_CHAIN = new SkillHolder(34449, 1); // Freezing Chain
	private static final SkillHolder FREEZING_MASS_CHAIN = new SkillHolder(34449, 2); // Mass Freezing Chain
	private static final SkillHolder FROSTY_COMMAND = new SkillHolder(34451, 1); // Frosty Command
	private static final SkillHolder ICE_BOLTS = new SkillHolder(34452, 1); // Summon Ice Bolts
	private static final SkillHolder FINAL_FREEZING_DISPLAY = new SkillHolder(6276, 1); // Final Freezing Burst Display
	// Items
	private static final ItemHolder JEWELRY_BOX = new ItemHolder(82497, 1);
	private static final ItemHolder WARM_ENERGY = new ItemHolder(82491, 1);
	private static final ItemHolder COOL_ENERGY = new ItemHolder(82492, 1);
	private static final ItemHolder FREYAS_CLOAK = new ItemHolder(82493, 1);
	private static final ItemHolder FREYAS_NECKLACE = new ItemHolder(82494, 1);
	private static final ItemHolder FREYAS_STAFF = new ItemHolder(82495, 1);
	private static final ItemHolder FREYAS_CROWN = new ItemHolder(82496, 1);
	
	// Misc
	private static final int TEMPLATE_ID = 323;
	
	public Freya()
	{
		super(TEMPLATE_ID);
		addStartNpc(BENUSTA);
		addAttackId(FREYA_THRONE, FREYA_STAND, GLAKIAS);
		addKillId(FREYA_STAND);
		addSpellFinishedId(HUGE_ICICLE, WINTER_CRYSTAL, FREYA_STAND);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		switch (event)
		{
			case "enterInstance":
			{
				if (player.isInParty())
				{
					final Party party = player.getParty();
					if (!party.isLeader(player))
					{
						player.sendPacket(SystemMessageId.ONLY_A_PARTY_LEADER_CAN_MAKE_THE_REQUEST_TO_ENTER);
						return null;
					}
					
					if (player.isInCommandChannel())
					{
						player.sendPacket(SystemMessageId.YOU_CANNOT_ENTER_AS_YOU_DON_T_MEET_THE_REQUIREMENTS);
						return null;
					}
					
					final List<Player> members = party.getMembers();
					for (Player member : members)
					{
						if (!member.isInsideRadius3D(npc, 1000))
						{
							player.sendMessage("Player " + member.getName() + " must come closer.");
							return null;
						}
					}
					
					for (Player member : members)
					{
						enterInstance(member, npc, TEMPLATE_ID);
					}
				}
				else if (player.isGM())
				{
					enterInstance(player, npc, TEMPLATE_ID);
				}
				else
				{
					player.sendPacket(SystemMessageId.YOU_ARE_NOT_IN_A_PARTY_SO_YOU_CANNOT_ENTER);
				}
				
				if (player.getInstanceWorld() != null)
				{
					startQuestTimer("movie_opening", 5000, null, player);
				}
				break;
			}
			case "movie_opening":
			{
				final Instance world = player.getInstanceWorld();
				if (!isInInstance(world))
				{
					return null;
				}
				
				world.setStatus(1);
				playMovie(world, Movie.SC_BOSS_FREYA_OPENING);
				startQuestTimer("check_status", 53500, null, player);
				break;
			}
			case "movie_freya_waiting":
			{
				final Instance world = player.getInstanceWorld();
				if (!isInInstance(world))
				{
					return null;
				}
				
				playMovie(world, Movie.SC_BOSS_FREYA_PHASECH_A);
				startQuestTimer("movie_glakias", 13100, null, player);
				break;
			}
			case "movie_glakias":
			{
				final Instance world = player.getInstanceWorld();
				if (!isInInstance(world))
				{
					return null;
				}
				
				world.setStatus(2);
				final Npc freyaThrone = world.getNpc(FREYA_THRONE);
				if (freyaThrone != null)
				{
					freyaThrone.deleteMe();
				}
				
				playMovie(world, Movie.SC_ICE_HEAVYKNIGHT_SPAWN);
				startQuestTimer("check_status", 10000, null, player);
				break;
			}
			case "movie_last_freya":
			{
				final Instance world = player.getInstanceWorld();
				if (!isInInstance(world))
				{
					return null;
				}
				
				world.setStatus(3);
				final Npc glakias = world.getNpc(GLAKIAS);
				if (glakias != null)
				{
					glakias.deleteMe();
				}
				
				final Npc freyaWaiting = world.getNpc(FREYA_WAITING);
				if (freyaWaiting != null)
				{
					freyaWaiting.deleteMe();
				}
				
				world.getAliveNpcs(HUGE_ICICLE).forEach(Creature::deleteMe);
				playMovie(world, Movie.SC_BOSS_FREYA_PHASECH_B);
				startQuestTimer("check_status", 21500, null, player);
				break;
			}
			case "movie_ending":
			{
				final Instance world = player.getInstanceWorld();
				if (!isInInstance(world))
				{
					return null;
				}
				
				playMovie(world, Movie.SC_BOSS_FREYA_ENDING_A);
				break;
			}
			case "freya_rage":
			{
				final Instance world = player.getInstanceWorld();
				if (!isInInstance(world))
				{
					return null;
				}
				
				if (world.getStatus() == 1)
				{
					for (int i = 0; i < 3; i++)
					{
						addSpawn(ICE_WALL, player.getX() + getRandom(-400, 400), player.getY() + getRandom(-400, 400), player.getZ() + 20, 0, false, 10000, false, world.getId());
					}
					
					addSpawn(ICE_WALL, player.getX(), player.getY(), player.getZ() + 20, 0, false, 10000, false, world.getId());
					startQuestTimer("ice_hurricane_cast", 1000, null, player);
				}
				break;
			}
			case "glakias_rage":
			{
				final Instance world = player.getInstanceWorld();
				if (!isInInstance(world))
				{
					return null;
				}
				
				if (world.getStatus() == 2)
				{
					showOnScreenMsg(world, NpcStringId.I_WILL_FREEZE_YOUR_HEART, ExShowScreenMessage.TOP_CENTER, 10000, true);
					world.spawnGroup("ICICLES_1");
					world.spawnGroup("ICICLES_2");
					world.spawnGroup("ICICLES_3");
					world.spawnGroup("ICICLES_4");
					startQuestTimer("ice_bolts_1", 1000, null, player);
					startQuestTimer("ice_bolts_2", 1000, null, player);
					startQuestTimer("ice_bolts_3", 7000, null, player);
					startQuestTimer("ice_bolts_4", 7000, null, player);
				}
				break;
			}
			case "ice_bolts_1":
			{
				final Instance world = player.getInstanceWorld();
				if (!isInInstance(world))
				{
					return null;
				}
				
				if (world.getStatus() == 2)
				{
					world.getNpcsOfGroup("ICICLES_1").forEach(icicle ->
					{
						if (icicle != null)
						{
							icicle.setDisplayEffect(1);
							try
							{
								Thread.sleep(700);
							}
							catch (Exception e)
							{
							}
							
							icicle.setDisplayEffect(2);
							icicle.doCast(ICE_BOLTS.getSkill());
						}
					});
				}
				break;
			}
			case "ice_bolts_2":
			{
				final Instance world = player.getInstanceWorld();
				if (!isInInstance(world))
				{
					return null;
				}
				
				if (world.getStatus() == 2)
				{
					world.getNpcsOfGroup("ICICLES_2").forEach(icicle ->
					{
						if (icicle != null)
						{
							icicle.setDisplayEffect(1);
							try
							{
								Thread.sleep(810);
							}
							catch (Exception e)
							{
							}
							
							icicle.setDisplayEffect(2);
							icicle.doCast(ICE_BOLTS.getSkill());
						}
					});
				}
				break;
			}
			case "ice_bolts_3":
			{
				final Instance world = player.getInstanceWorld();
				if (!isInInstance(world))
				{
					return null;
				}
				
				if (world.getStatus() == 2)
				{
					world.getNpcsOfGroup("ICICLES_3").forEach(icicle ->
					{
						if (icicle != null)
						{
							icicle.setDisplayEffect(1);
							try
							{
								Thread.sleep(500);
							}
							catch (Exception e)
							{
							}
							
							icicle.setDisplayEffect(2);
							icicle.doCast(ICE_BOLTS.getSkill());
						}
					});
				}
				break;
			}
			case "ice_bolts_4":
			{
				final Instance world = player.getInstanceWorld();
				if (!isInInstance(world))
				{
					return null;
				}
				
				if (world.getStatus() == 2)
				{
					world.getNpcsOfGroup("ICICLES_4").forEach(icicle ->
					{
						if (icicle != null)
						{
							icicle.setDisplayEffect(1);
							try
							{
								Thread.sleep(578);
							}
							catch (Exception e)
							{
							}
							
							icicle.setDisplayEffect(2);
							icicle.doCast(ICE_BOLTS.getSkill());
						}
					});
				}
				break;
			}
			case "frosty_command":
			{
				final Instance world = player.getInstanceWorld();
				if (!isInInstance(world))
				{
					return null;
				}
				
				if (world.getStatus() == 2)
				{
					final boolean commandCasted = world.getParameters().getBoolean("COMMAND_CASTED", false);
					if (!commandCasted)
					{
						final Npc glakias = world.getNpc(GLAKIAS);
						glakias.doCast(FROSTY_COMMAND.getSkill());
						world.getParameters().set("COMMAND_CASTED", true);
					}
					else
					{
						startQuestTimer("frosty_command", 1000, null, player);
					}
				}
				break;
			}
			case "freezing_hurricane":
			{
				final Instance world = player.getInstanceWorld();
				if (!isInInstance(world))
				{
					return null;
				}
				
				if (world.getStatus() < 4)
				{
					for (int i = 0; i < 5; i++)
					{
						addSpawn(ICE_WALL, player.getX() + getRandom(-800, 800), player.getY() + getRandom(-800, 800), player.getZ() + 20, 0, false, 10000, false, world.getId());
					}
					
					addSpawn(ICE_WALL, player.getX(), player.getY(), player.getZ() + 20, 0, false, 10000, false, world.getId());
					startQuestTimer("freezing_hurricane_cast", 1000, null, player);
				}
				break;
			}
			case "freezing_blizzard":
			{
				final Instance world = player.getInstanceWorld();
				if (!isInInstance(world))
				{
					return null;
				}
				
				if (world.getStatus() < 4)
				{
					for (int i = 0; i < 10; i++)
					{
						addSpawn(ICE_WALL, player.getX() + getRandom(-800, 800), player.getY() + getRandom(-800, 800), player.getZ() + 20, 0, false, 10000, false, world.getId());
					}
					
					addSpawn(ICE_WALL, player.getX(), player.getY(), player.getZ() + 20, 0, false, 10000, false, world.getId());
					startQuestTimer("freezing_blizzard_cast", 1000, null, player);
				}
				break;
			}
			case "ice_hurricane_cast":
			{
				final Instance world = player.getInstanceWorld();
				if (!isInInstance(world))
				{
					return null;
				}
				
				if (world.getStatus() == 1)
				{
					world.getAliveNpcs(ICE_WALL).forEach(wall ->
					{
						if (wall != null)
						{
							wall.doCast(ICE_HURRICANE.getSkill());
							
						}
					});
					startQuestTimer("freya_rage", 12000, null, player);
				}
				break;
			}
			case "freezing_hurricane_cast":
			{
				final Instance world = player.getInstanceWorld();
				if (!isInInstance(world))
				{
					return null;
				}
				
				if (world.getStatus() == 3)
				{
					world.getAliveNpcs(ICE_WALL).forEach(wall ->
					{
						if (wall != null)
						{
							wall.doCast(FREEZING_HURRICANE.getSkill());
							
						}
					});
					startQuestTimer("freezing_hurricane", 20000, null, player);
				}
				break;
			}
			case "freezing_blizzard_cast":
			{
				final Instance world = player.getInstanceWorld();
				if (!isInInstance(world))
				{
					return null;
				}
				
				if (world.getStatus() == 3)
				{
					world.getAliveNpcs(ICE_WALL).forEach(wall ->
					{
						if (wall != null)
						{
							wall.doCast(FREEZING_BLIZARD.getSkill());
							
						}
					});
					startQuestTimer("freezing_blizzard", 30000, null, player);
				}
				break;
			}
			case "eternal_blizzard":
			{
				final Instance world = player.getInstanceWorld();
				if (!isInInstance(world))
				{
					return null;
				}
				
				if ((world.getStatus() == 3) && (world.getAliveNpcCount(WINTER_CRYSTAL) > 0))
				{
					final Npc winterCrystal = world.getNpc(WINTER_CRYSTAL);
					winterCrystal.doCast(ETERNAL_BLIZZARD.getSkill());
				}
				break;
			}
			case "final_freezing_burst":
			{
				final Instance world = player.getInstanceWorld();
				if (!isInInstance(world))
				{
					return null;
				}
				
				if (world.getStatus() == 3)
				{
					showOnScreenMsg(world, NpcStringId.REST_IN_PEACE_FOREVER, ExShowScreenMessage.TOP_CENTER, 10000, true);
					final Npc freya = world.getNpc(FREYA_STAND);
					freya.doCast(FINAL_FREEZING.getSkill());
				}
				break;
			}
			case "summon_winter_crystal":
			{
				final Instance world = player.getInstanceWorld();
				if (!isInInstance(world))
				{
					return null;
				}
				
				if (world.getStatus() == 3)
				{
					world.spawnGroup("WINTER_CRYSTAL");
					startQuestTimer("eternal_blizzard", 1000, null, player);
				}
				break;
			}
			case "check_status":
			{
				final Instance world = player.getInstanceWorld();
				if (!isInInstance(world))
				{
					return null;
				}
				
				switch (world.getStatus())
				{
					case 1:
					{
						final boolean freyaSpawned = world.getParameters().getBoolean("FREYA_SPAWNED", false);
						if (!freyaSpawned)
						{
							world.spawnGroup("FREYA_STAGE_1");
							showOnScreenMsg(world, NpcStringId.YOU_WILL_PAY_FOR_BREAKING_THE_SILENCE_OF_THE_CASTLE_HIDDEN_IN_THE_EVERLASTING_ICE, ExShowScreenMessage.TOP_CENTER, 10000, true);
							world.getParameters().set("FREYA_SPAWNED", true);
						}
						break;
					}
					case 2:
					{
						final boolean glakiasSpawned = world.getParameters().getBoolean("GLAKIAS_SPAWNED", false);
						if (!glakiasSpawned)
						{
							world.spawnGroup("GLAKIAS");
							world.spawnGroup("FREYA_STAGE_2");
							world.spawnGroup("KANNAS_KNIGHTS");
							showOnScreenMsg(world, NpcStringId.GLAKIAS_LOYALTY_TO_THE_ICE_QUEEN_WILL_INCREASE_IN_5_MINUTES, ExShowScreenMessage.TOP_CENTER, 10000, true);
							startQuestTimer("frosty_command", 300000, null, player);
							world.getParameters().set("GLAKIAS_SPAWNED", true);
							world.getParameters().set("FREYA_SPAWNED", false);
						}
						break;
					}
					case 3:
					{
						
						final boolean freyaSpawned = world.getParameters().getBoolean("FREYA_SPAWNED", false);
						if (!freyaSpawned)
						{
							world.spawnGroup("FREYA_STAGE_3");
							world.getParameters().set("FREYA_SPAWNED", true);
						}
						break;
					}
				}
				break;
			}
		}
		
		return super.onEvent(event, npc, player);
	}
	
	@Override
	public void onAttack(Npc npc, Player attacker, int damage, boolean isSummon, Skill skill)
	{
		final Instance world = npc.getInstanceWorld();
		if (isInInstance(world))
		{
			switch (npc.getId())
			{
				case FREYA_THRONE:
				{
					final Creature mostHated = npc.asAttackable().getMostHated();
					final boolean freya90 = world.getParameters().getBoolean("FREYA_90", false);
					final boolean freya80 = world.getParameters().getBoolean("FREYA_80", false);
					if ((npc.getCurrentHp() <= (npc.getMaxHp() * 0.9)) && !freya90)
					{
						showOnScreenMsg(world, NpcStringId.FEEL_THE_POWER_OF_MY_RAGE, ExShowScreenMessage.TOP_CENTER, 10000, true);
						startQuestTimer("freya_rage", 100, null, attacker);
						world.getParameters().set("FREYA_90", true);
					}
					else if ((npc.getCurrentHp() <= (npc.getMaxHp() * 0.8)) && !freya80)
					{
						startQuestTimer("movie_freya_waiting", 100, null, attacker);
						world.getParameters().set("FREYA_80", true);
					}
					else
					{
						if (getRandom(100) < 40)
						{
							if (SkillCaster.checkUseConditions(npc, ICE_BOLT.getSkill()))
							{
								npc.setTarget(mostHated);
								npc.doCast(ICE_BOLT.getSkill());
							}
						}
						else
						{
							if (SkillCaster.checkUseConditions(npc, ICE_STORM.getSkill()))
							{
								npc.doCast(ICE_STORM.getSkill());
							}
						}
					}
					break;
				}
				
				case GLAKIAS:
				{
					final Creature mostHated = npc.asAttackable().getMostHated();
					final boolean glakias50 = world.getParameters().getBoolean("GLAKIAS_50", false);
					final boolean glakias5 = world.getParameters().getBoolean("GLAKIAS_5", false);
					if ((npc.getCurrentHp() <= (npc.getMaxHp() * 0.5)) && !glakias50)
					{
						startQuestTimer("glakias_rage", 100, null, attacker);
						world.getParameters().set("GLAKIAS_50", true);
					}
					else if ((npc.getCurrentHp() <= (npc.getMaxHp() * 0.05)) && !glakias5)
					{
						startQuestTimer("movie_last_freya", 100, null, attacker);
						world.getParameters().set("GLAKIAS_5", true);
					}
					else
					{
						if (getRandom(100) < 20)
						{
							if (SkillCaster.checkUseConditions(npc, FREEZING_STRIKE.getSkill()))
							{
								npc.setTarget(mostHated);
								npc.doCast(FREEZING_STRIKE.getSkill());
							}
						}
						else if ((getRandom(100) < 40) && (getRandom(100) > 20))
						{
							if (SkillCaster.checkUseConditions(npc, FREEZING_SMASH.getSkill()))
							{
								npc.doCast(FREEZING_SMASH.getSkill());
							}
						}
						else if ((getRandom(100) < 60) && (getRandom(100) > 40))
						{
							if (SkillCaster.checkUseConditions(npc, FREEZING_WAVE.getSkill()))
							{
								npc.doCast(FREEZING_WAVE.getSkill());
							}
						}
						else if ((getRandom(100) < 80) && (getRandom(100) > 60))
						{
							if (SkillCaster.checkUseConditions(npc, FREEZING_CHAIN.getSkill()))
							{
								npc.setTarget(mostHated);
								npc.doCast(FREEZING_CHAIN.getSkill());
							}
						}
						else
						{
							if (SkillCaster.checkUseConditions(npc, FREEZING_MASS_CHAIN.getSkill()))
							{
								npc.setTarget(mostHated);
								npc.doCast(FREEZING_MASS_CHAIN.getSkill());
							}
							
						}
					}
					break;
				}
				case FREYA_STAND:
				{
					final Creature mostHated = npc.asAttackable().getMostHated();
					final boolean freya70 = world.getParameters().getBoolean("FREYA_70", false);
					final boolean freya50 = world.getParameters().getBoolean("FREYA_50", false);
					final boolean freya5 = world.getParameters().getBoolean("FREYA_5", false);
					if ((npc.getCurrentHp() <= (npc.getMaxHp() * 0.9)) && !freya70)
					{
						startQuestTimer("freezing_hurricane", 1000, null, attacker);
						startQuestTimer("freezing_blizzard", 5000, null, attacker);
						world.getParameters().set("FREYA_70", true);
					}
					else if ((npc.getCurrentHp() <= (npc.getMaxHp() * 0.5)) && !freya50)
					{
						startQuestTimer("summon_winter_crystal", 100, null, attacker);
						showOnScreenMsg(world, NpcStringId.THE_ICE_QUEEN_S_POWER_WILL_BRING_YOU_THE_UNBEARABLE_SUFFERING, ExShowScreenMessage.TOP_CENTER, 10000, true);
						world.getParameters().set("FREYA_50", true);
					}
					else if ((npc.getCurrentHp() <= (npc.getMaxHp() * 0.05)) && !freya5)
					{
						startQuestTimer("summon_winter_crystal", 100, null, attacker);
						showOnScreenMsg(world, NpcStringId.ENOUGH_FACE_YOU_DEATH_AND_HER_NAME_IS_THE_ICE_QUEEN, ExShowScreenMessage.TOP_CENTER, 10000, true);
						world.getParameters().set("FREYA_5", true);
					}
					else if (npc.getCurrentHp() <= (npc.getMaxHp() * 0.01))
					{
						startQuestTimer("final_freezing_burst", 100, null, attacker);
					}
					else
					{
						if (getRandom(100) < 33)
						{
							if (SkillCaster.checkUseConditions(npc, FREEZING_BOLT.getSkill()))
							{
								npc.setTarget(mostHated);
								npc.doCast(FREEZING_BOLT.getSkill());
							}
						}
						else if ((getRandom(100) < 66) && (getRandom(100) > 33))
						{
							if (SkillCaster.checkUseConditions(npc, FREEZING_STORM.getSkill()))
							{
								npc.doCast(FREEZING_STORM.getSkill());
							}
						}
						else
						{
							if (SkillCaster.checkUseConditions(npc, FREEZING_QUEEN_WAVE.getSkill()))
							{
								npc.doCast(FREEZING_QUEEN_WAVE.getSkill());
							}
						}
					}
					break;
				}
			}
		}
	}
	
	@Override
	public void onSpellFinished(Npc npc, Player player, Skill skill)
	{
		final Instance world = npc.getInstanceWorld();
		if (world != null)
		{
			switch (npc.getId())
			{
				case HUGE_ICICLE:
				{
					if ((skill.getId() == ICE_BOLTS.getSkillId()) && (world.getStatus() == 2))
					{
						npc.setDisplayEffect(3);
					}
					break;
				}
				case WINTER_CRYSTAL:
				{
					if ((skill.getId() == ETERNAL_BLIZZARD.getSkillId()) && (world.getStatus() == 3))
					{
						npc.setDisplayEffect(3);
						world.getNpc(WINTER_CRYSTAL).doDie(npc);
						world.getNpc(FREYA_STAND).setCurrentHp(world.getNpc(FREYA_STAND).getMaxHp() * 0.70);
						for (Player member : world.getPlayers())
						{
							member.doDie(member);
						}
					}
					break;
				}
				case FREYA_STAND:
				{
					if ((skill.getId() == FINAL_FREEZING.getSkillId()) && (world.getStatus() == 3))
					{
						world.getNpc(FREYA_STAND).setCurrentHp(world.getNpc(FREYA_STAND).getMaxHp() * 0.20);
						npc.doCast(FINAL_FREEZING_DISPLAY.getSkill());
						for (Player member : world.getPlayers())
						{
							member.doDie(member);
						}
					}
					break;
				}
			}
		}
	}
	
	@Override
	public void onKill(Npc npc, Player killer, boolean isSummon)
	{
		final Instance world = npc.getInstanceWorld();
		if (isInInstance(world))
		{
			if (npc.getId() == FREYA_STAND)
			{
				world.setStatus(4);
				if (world.getAliveNpcCount(WINTER_CRYSTAL) > 0)
				{
					final Npc winterCrystal = world.getNpc(WINTER_CRYSTAL);
					if (winterCrystal != null)
					{
						winterCrystal.deleteMe();
					}
				}
				
				startQuestTimer("movie_ending", 100, null, killer);
				
				for (Player member : world.getPlayers())
				{
					if ((member.getParty() != null) || (member.isGM()))
					{
						giveItems(member, JEWELRY_BOX);
					}
				}
				
				for (Player member : world.getPlayers())
				{
					if ((member.getParty() != null) || (member.isGM()))
					{
						switch (getRandom(7))
						{
							case 0:
							{
								break;
							}
							case 1:
							{
								giveItems(member, WARM_ENERGY);
								break;
							}
							case 2:
							{
								giveItems(member, COOL_ENERGY);
								break;
							}
							case 3:
							{
								giveItems(member, FREYAS_CLOAK);
								break;
							}
							case 4:
							{
								giveItems(member, FREYAS_NECKLACE);
								break;
							}
							case 5:
							{
								giveItems(member, FREYAS_STAFF);
								break;
							}
							case 6:
							{
								giveItems(member, FREYAS_CROWN);
								break;
							}
						}
					}
				}
				
				world.finishInstance(2);
			}
		}
	}
	
	public static void main(String[] args)
	{
		new Freya();
	}
}
