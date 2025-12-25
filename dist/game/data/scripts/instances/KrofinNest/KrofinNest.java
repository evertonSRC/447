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
package instances.KrofinNest;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.groups.Party;
import org.l2jmobius.gameserver.model.instancezone.Instance;
import org.l2jmobius.gameserver.model.item.holders.ItemHolder;
import org.l2jmobius.gameserver.model.script.InstanceScript;
import org.l2jmobius.gameserver.model.skill.holders.SkillHolder;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.enums.ChatType;
import org.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2jmobius.gameserver.network.serverpackets.OnEventTrigger;
import org.l2jmobius.gameserver.util.ArrayUtil;

/**
 * @author CostyKiller, NasSeKa, Tanatos
 * @URL https://www.youtube.com/watch?v=0qM7YZ27T-Q
 * @URL https://l2central.info/main/locations/instance_zones/party/krofin_nest/?lang=ru
 */
public class KrofinNest extends InstanceScript
{
	// NPCs
	private static final int BENUSTA = 34542;
	private static final int INNER_BENUSTA = 34654;
	
	// Bosses
	private static final int KROSHA_105 = 26500;
	private static final int KROSHA_110 = 26467;
	private static final int KROSHA_115 = 26487;
	private static final int KROSHA_120 = 26525;
	private static final int[] KROSHA =
	{
		KROSHA_105,
		KROSHA_110,
		KROSHA_115,
		KROSHA_120
	};
	
	// Monsters
	private static final int[] FIRST_AREA_105 =
	{
		26503,
		26504,
		26507,
		26508
	};
	private static final int[] FIRST_AREA_110 =
	{
		26476,
		26477,
		26478,
		26479,
	};
	private static final int[] FIRST_AREA_115 =
	{
		26496,
		26497,
		26498,
		26499
	};
	private static final int[] FIRST_AREA_120 =
	{
		26531,
		26532,
		26533,
		26534
	};
	private static final int[] SECOND_AREA_105 =
	{
		26505
	};
	private static final int[] SECOND_AREA_110 =
	{
		26474
	};
	private static final int[] SECOND_AREA_115 =
	{
		26490
	};
	private static final int[] SECOND_AREA_120 =
	{
		26530
	};
	private static final int[] THIRD_AREA_105 =
	{
		26506,
		26509
	};
	private static final int[] THIRD_AREA_110 =
	{
		26470,
		26471
	};
	private static final int[] THIRD_AREA_115 =
	{
		26494,
		26495
	};
	private static final int[] THIRD_AREA_120 =
	{
		26528,
		26529
	};
	private static final int KROTANION_105 = 26501;
	private static final int KROPION_105 = 26502;
	private static final int KROTANION_110 = 26468;
	private static final int KROPION_110 = 26469;
	private static final int KROTANION_115 = 26488;
	private static final int KROPION_115 = 26489;
	private static final int KROTANION_120 = 26526;
	private static final int KROPION_120 = 26527;
	private static final int[] FOURTH_AREA_105 =
	{
		KROTANION_105,
		KROPION_105
	};
	private static final int[] FOURTH_AREA_110 =
	{
		KROTANION_110,
		KROPION_110
	};
	private static final int[] FOURTH_AREA_115 =
	{
		KROTANION_115,
		KROPION_115
	};
	private static final int[] FOURTH_AREA_120 =
	{
		KROTANION_120,
		KROPION_120
	};
	private static final int[] KROTANION_MINION =
	{
		KROTANION_105,
		KROTANION_110,
		KROTANION_115,
		KROTANION_120
	};
	private static final int[] KROPION_MINION =
	{
		KROPION_105,
		KROPION_110,
		KROPION_115,
		KROPION_120
	};
	private static final int DUMMY = 19276;
	
	// Skills
	private static final SkillHolder WATER_VORTEX = new SkillHolder(32651, 1);
	private static final SkillHolder SKILL_CENTER = new SkillHolder(34048, 1);
	private static final SkillHolder SKILL_OUTSIDE = new SkillHolder(34050, 1);
	private static final SkillHolder LIMIT_BARRIER = new SkillHolder(34040, 1);
	private static final SkillHolder KROPION_CURSE = new SkillHolder(34051, 1);
	private static final SkillHolder KROTANION_CURSE = new SkillHolder(34052, 1);
	private static final SkillHolder KROSHA_WAVE = new SkillHolder(34037, 1);
	private static final SkillHolder KROSHA_STAMP = new SkillHolder(34038, 1);
	private static final SkillHolder KROSHA_BEAM = new SkillHolder(34039, 1);
	
	// Items
	private static final ItemHolder BENUSTAS_REWARD_BOX_105 = new ItemHolder(82453, 1);
	private static final ItemHolder BENUSTAS_REWARD_BOX_110 = new ItemHolder(82454, 1);
	private static final ItemHolder BENUSTAS_REWARD_BOX_115 = new ItemHolder(82455, 1);
	private static final ItemHolder BENUSTAS_REWARD_BOX_120 = new ItemHolder(82925, 1);
	private static final ItemHolder BENUSTAS_SHINING_REWARD_BOX = new ItemHolder(82456, 1);
	
	// Misc
	private static final int TEMPLATE_ID = 322;
	private static final int DOOR1 = 23220101;
	private static final int DOOR2 = 24250002;
	private static final int DOOR3 = 24250004;
	private static final int DOOR4 = 24250006;
	private static final Map<Npc, Integer> KROSHA_HITS = new ConcurrentHashMap<>();
	private static final int HIT_COUNT = 100; // Guessed
	
	// Effect triggers
	private static final int DOOR1_CLOSED = 23227500;
	private static final int DOOR1_OPENING = 23227502;
	private static final int DOOR2_CLOSED = 24257710;
	private static final int DOOR2_OPENING = 24257712;
	private static final int DOOR3_CLOSED = 24257720;
	private static final int DOOR3_OPENING = 24257722;
	private static final int DOOR4_CLOSED = 24257730;
	private static final int DOOR4_OPENING = 24257732;
	private static final int BOSS_WATERFALL = 24257780;
	
	public KrofinNest()
	{
		super(TEMPLATE_ID);
		addStartNpc(BENUSTA);
		addFirstTalkId(INNER_BENUSTA);
		addAttackId(KROSHA_105, KROSHA_110, KROSHA_115, KROSHA_120);
		addKillId(KROSHA_105, KROSHA_110, KROSHA_115, KROSHA_120, KROTANION_105, KROPION_105, KROTANION_110, KROPION_110, KROTANION_115, KROPION_115, KROTANION_120, KROPION_120);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		switch (event)
		{
			case "34654-1.html":
			case "34654-2.html":
			{
				return event;
			}
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
					startQuestTimer("check_status", 10000, null, player);
					startQuestTimer("door_closed_effects", 1000, null, player);
				}
				break;
			}
			case "benusta_texts":
			{
				final Instance world = player.getInstanceWorld();
				if (!isInInstance(world))
				{
					return null;
				}
				
				final int benustaText = world.getParameters().getInt("BENUSTA_TEXT", 0);
				if (world.getAliveNpcCount(INNER_BENUSTA) > 0)
				{
					switch (benustaText)
					{
						case 0:
						{
							showOnScreenMsg(world, NpcStringId.THE_PARTY_LEADER_SHOULD_TALK_TO_ME_AND_SELECT_THE_DIFFICULTY_LEVEL, ExShowScreenMessage.TOP_CENTER, 5000, true);
							world.getNpc(INNER_BENUSTA).broadcastSay(ChatType.NPC_GENERAL, NpcStringId.THE_PARTY_LEADER_SHOULD_TALK_TO_ME_AND_SELECT_THE_DIFFICULTY_LEVEL);
							startQuestTimer("benusta_texts", 4000, null, player);
							world.getParameters().set("BENUSTA_TEXT", 1);
							break;
						}
						case 1:
						{
							showOnScreenMsg(world, NpcStringId.TAKE_INTO_ACCOUNT_THE_ABILITIES_OF_YOUR_PARTY_MEMBERS_WHEN_SELECTING_THE_DIFFICULTY, ExShowScreenMessage.TOP_CENTER, 5000, true);
							world.getNpc(INNER_BENUSTA).broadcastSay(ChatType.NPC_GENERAL, NpcStringId.TAKE_INTO_ACCOUNT_THE_ABILITIES_OF_YOUR_PARTY_MEMBERS_WHEN_SELECTING_THE_DIFFICULTY);
							startQuestTimer("benusta_texts", 4000, null, player);
							world.getParameters().set("BENUSTA_TEXT", 2);
							break;
						}
						case 2:
						{
							showOnScreenMsg(world, NpcStringId.THE_SELECTED_DIFFICULTY_CANNOT_BE_CHANGED_SO_CHOOSE_WISELY, ExShowScreenMessage.TOP_CENTER, 5000, true);
							world.getNpc(INNER_BENUSTA).broadcastSay(ChatType.NPC_GENERAL, NpcStringId.THE_SELECTED_DIFFICULTY_CANNOT_BE_CHANGED_SO_CHOOSE_WISELY);
							world.getParameters().set("BENUSTA_TEXT", 0);
							break;
						}
					}
				}
				break;
			}
			case "easyInstance":
			{
				final Instance world = player.getInstanceWorld();
				if (!isInInstance(world))
				{
					return null;
				}
				
				world.getParameters().set("INSTANCE_LEVEL", "105");
				showOnScreenMsg(world, NpcStringId.YOU_VE_CHOSEN_LV_105_GET_READY_FOR_THE_BATTLE, ExShowScreenMessage.TOP_CENTER, 8000, true);
				world.getNpc(INNER_BENUSTA).deleteMe();
				world.setStatus(1);
				startQuestTimer("door_closed_effects", 100, null, player);
				break;
			}
			case "mediumInstance":
			{
				final Instance world = player.getInstanceWorld();
				if (!isInInstance(world))
				{
					return null;
				}
				
				world.getParameters().set("INSTANCE_LEVEL", "110");
				showOnScreenMsg(world, NpcStringId.YOU_VE_CHOSEN_LV_110_GET_READY_FOR_THE_BATTLE, ExShowScreenMessage.TOP_CENTER, 8000, true);
				world.getNpc(INNER_BENUSTA).deleteMe();
				world.setStatus(1);
				startQuestTimer("door_closed_effects", 100, null, player);
				break;
			}
			case "hardInstance":
			{
				final Instance world = player.getInstanceWorld();
				if (!isInInstance(world))
				{
					return null;
				}
				
				world.getParameters().set("INSTANCE_LEVEL", "115");
				showOnScreenMsg(world, NpcStringId.YOU_VE_CHOSEN_LV_115_GET_READY_FOR_THE_BATTLE, ExShowScreenMessage.TOP_CENTER, 8000, true);
				world.getNpc(INNER_BENUSTA).deleteMe();
				world.setStatus(1);
				startQuestTimer("door_closed_effects", 100, null, player);
				break;
			}
			case "extremeInstance":
			{
				final Instance world = player.getInstanceWorld();
				if (!isInInstance(world))
				{
					return null;
				}
				
				world.getParameters().set("INSTANCE_LEVEL", "120");
				showOnScreenMsg(world, NpcStringId.YOU_VE_CHOSEN_LV_120_GET_READY_FOR_THE_BATTLE, ExShowScreenMessage.TOP_CENTER, 8000, true);
				world.getNpc(INNER_BENUSTA).deleteMe();
				world.setStatus(1);
				startQuestTimer("door_closed_effects", 100, null, player);
				break;
			}
			case "door_closed_effects":
			{
				final Instance world = player.getInstanceWorld();
				if (!isInInstance(world))
				{
					return null;
				}
				
				switch (world.getStatus())
				{
					case 1:
					case 2:
					{
						world.broadcastPacket(new OnEventTrigger(DOOR1_CLOSED, true));
						break;
					}
					case 3:
					{
						world.broadcastPacket(new OnEventTrigger(DOOR2_CLOSED, true));
						break;
					}
					case 5:
					{
						world.broadcastPacket(new OnEventTrigger(DOOR3_CLOSED, true));
						break;
					}
					case 7:
					{
						world.broadcastPacket(new OnEventTrigger(DOOR4_CLOSED, true));
						break;
					}
				}
				break;
			}
			case "door_opening_effects":
			{
				final Instance world = player.getInstanceWorld();
				if (!isInInstance(world))
				{
					return null;
				}
				
				switch (world.getStatus())
				{
					case 3:
					{
						world.broadcastPacket(new OnEventTrigger(DOOR1_OPENING, true));
						world.getDoor(DOOR1).openMe();
						break;
					}
					case 5:
					{
						world.broadcastPacket(new OnEventTrigger(DOOR2_OPENING, true));
						world.getDoor(DOOR2).openMe();
						break;
					}
					case 7:
					{
						world.broadcastPacket(new OnEventTrigger(DOOR3_OPENING, true));
						world.getDoor(DOOR3).openMe();
						break;
					}
					case 9:
					{
						world.broadcastPacket(new OnEventTrigger(DOOR4_OPENING, true));
						world.getDoor(DOOR4).openMe();
						break;
					}
				}
				break;
			}
			case "door_effects_off":
			{
				final Instance world = player.getInstanceWorld();
				if (!isInInstance(world))
				{
					return null;
				}
				
				switch (world.getStatus())
				{
					case 3:
					{
						world.broadcastPacket(new OnEventTrigger(DOOR1_CLOSED, false));
						showOnScreenMsg(world, NpcStringId.THE_WATER_ENERGY_IS_NO_LONGER_ACTIVE_THE_WAY_IS_CLEAR, ExShowScreenMessage.TOP_CENTER, 5000, true);
						break;
					}
					case 5:
					{
						world.broadcastPacket(new OnEventTrigger(DOOR2_CLOSED, false));
						showOnScreenMsg(world, NpcStringId.THE_WATER_ENERGY_IS_NO_LONGER_ACTIVE_THE_WAY_IS_CLEAR, ExShowScreenMessage.TOP_CENTER, 5000, true);
						break;
					}
					case 7:
					{
						world.broadcastPacket(new OnEventTrigger(DOOR3_CLOSED, false));
						showOnScreenMsg(world, NpcStringId.THE_WATER_ENERGY_IS_NO_LONGER_ACTIVE_THE_WAY_IS_CLEAR, ExShowScreenMessage.TOP_CENTER, 5000, true);
						break;
					}
					case 9:
					{
						world.broadcastPacket(new OnEventTrigger(DOOR4_CLOSED, false));
						showOnScreenMsg(world, NpcStringId.THE_WATER_ENERGY_IS_NO_LONGER_ACTIVE_THE_WAY_IS_CLEAR, ExShowScreenMessage.TOP_CENTER, 5000, true);
						break;
					}
				}
				break;
			}
			case "first_room_skill":
			{
				final Instance world = player.getInstanceWorld();
				if (!isInInstance(world))
				{
					return null;
				}
				
				world.getNpcsOfGroup("KROSHA_VORTEX").forEach(vortex ->
				{
					if (vortex != null)
					{
						ThreadPool.schedule(() -> addSkillCastDesire(vortex, vortex, WATER_VORTEX, 23), getRandom(300));
					}
				});
				startQuestTimer("first_room_skill", 6000, null, player);
				break;
			}
			case "third_room_skill":
			{
				final Instance world = player.getInstanceWorld();
				if (!isInInstance(world))
				{
					return null;
				}
				
				final Npc dummy = world.getNpc(DUMMY);
				if ((dummy == null) || !isInInstance(world))
				{
					return null;
				}
				
				if (world.getStatus() < 7)
				{
					if (getRandom(100) < 50)
					{
						addSkillCastDesire(dummy, dummy, SKILL_CENTER, 23);
						dummy.broadcastPacket(new ExShowScreenMessage(NpcStringId.THE_CENTER_EMANATES_DREADFUL_ENERGY, ExShowScreenMessage.TOP_CENTER, 5000, true));
					}
					else
					{
						addSkillCastDesire(dummy, dummy, SKILL_OUTSIDE, 23);
						dummy.broadcastPacket(new ExShowScreenMessage(NpcStringId.A_POWERFUL_AND_DANGEROUS_ENERGY_CAN_BE_FELT_OUTSIDE, ExShowScreenMessage.TOP_CENTER, 5000, true));
					}
					
					startQuestTimer("third_room_skill", 15000, null, player);
				}
				break;
			}
			case "krosha_skills_start":
			{
				final Instance world = player.getInstanceWorld();
				if (!isInInstance(world))
				{
					return null;
				}
				
				world.getNpcs(KROSHA).forEach(krosha ->
				{
					if (krosha != null)
					{
						int kroshnaSkillUsed = world.getParameters().getInt("KROSHA_SKILL_USED", 0);
						krosha.abortCast();
						if (kroshnaSkillUsed == 0)
						{
							addSkillCastDesire(krosha, krosha, KROSHA_WAVE, 23);
							world.getParameters().set("KROSHA_SKILL_USED", 1);
							startQuestTimer("krosha_skills_start", 4500, krosha, player);
							
						}
						else if (kroshnaSkillUsed == 1)
						{
							addSkillCastDesire(krosha, krosha, KROSHA_STAMP, 23);
							world.getParameters().set("KROSHA_SKILL_USED", 2);
							startQuestTimer("krosha_skills_start", 4500, krosha, player);
							
						}
						else if (kroshnaSkillUsed == 2)
						{
							addSkillCastDesire(krosha, krosha, KROSHA_BEAM, 23);
							world.getParameters().set("KROSHA_SKILL_USED", 0);
							startQuestTimer("krosha_skills_start", 25000, krosha, player);
						}
					}
				});
				break;
			}
			case "krosha_barrier_start":
			{
				final Instance world = player.getInstanceWorld();
				if (!isInInstance(world))
				{
					return null;
				}
				
				world.getNpcs(KROSHA).forEach(krosha ->
				{
					if (krosha != null)
					{
						LIMIT_BARRIER.getSkill().applyEffects(krosha, krosha);
						krosha.setInvul(true);
					}
				});
				showOnScreenMsg(world, NpcStringId.IF_THE_WATER_BARRIER_IS_NOT_DESTROYED_IN_30_SEC_QUEEN_KROSHA_WILL_BE_FULLY_HEALED, ExShowScreenMessage.TOP_CENTER, 5000, true);
				startQuestTimer("krosha_restore_hp", 10000, null, player);
				break;
			}
			case "krosha_restore_hp":
			{
				final Instance world = player.getInstanceWorld();
				if (!isInInstance(world))
				{
					return null;
				}
				
				final boolean seconds20 = world.getParameters().getBoolean("SECONDS_20", false);
				final boolean seconds10 = world.getParameters().getBoolean("SECONDS_10", false);
				final boolean kroshnaUDDestroyed = world.getParameters().getBoolean("KROSHA_BARRIER_DESTROYED", false);
				if (!kroshnaUDDestroyed)
				{
					if (!seconds20)
					{
						showOnScreenMsg(world, NpcStringId.TWENTY_SECONDS_ARE_REMAINING_2, ExShowScreenMessage.TOP_CENTER, 5000, true);
						world.getParameters().set("SECONDS_20", true);
						startQuestTimer("krosha_restore_hp", 10000, null, player);
					}
					else if (!seconds10)
					{
						showOnScreenMsg(world, NpcStringId.TEN_SECONDS_ARE_REMAINING_2, ExShowScreenMessage.TOP_CENTER, 5000, true);
						world.getParameters().set("SECONDS_10", true);
						startQuestTimer("krosha_restore_hp", 10000, null, player);
					}
					else
					{
						world.getNpcs(KROSHA).forEach(krosha ->
						{
							if (krosha != null)
							{
								showOnScreenMsg(world, NpcStringId.WATER_BARRIER_WASN_T_DESTROYED_SO_QUEEN_KROSHA_IS_FULLY_HEALED, ExShowScreenMessage.TOP_CENTER, 7000, true);
								krosha.setCurrentHp(krosha.getMaxHp());
								world.getParameters().set("KROSHA_BARRIER", false);
								world.getParameters().set("SECONDS_20", false);
								world.getParameters().set("SECONDS_10", false);
								startQuestTimer("krosha_destroy_barrier", 1000, null, player);
							}
						});
					}
				}
				else
				{
					showOnScreenMsg(world, NpcStringId.WATER_BARRIER_IS_DESTROYED, ExShowScreenMessage.TOP_CENTER, 7000, true);
					startQuestTimer("krosha_destroy_barrier", 1000, null, player);
				}
				break;
			}
			case "krosha_destroy_barrier":
			{
				final Instance world = player.getInstanceWorld();
				if (!isInInstance(world))
				{
					return null;
				}
				
				world.getNpcs(KROSHA).forEach(krosha ->
				{
					if (krosha != null)
					{
						krosha.stopSkillEffects(LIMIT_BARRIER.getSkill());
						krosha.setInvul(false);
					}
				});
				break;
			}
			case "check_status":
			{
				final Instance world = player.getInstanceWorld();
				if (!isInInstance(world))
				{
					return null;
				}
				
				final String instanceLv = world.getParameters().getString("INSTANCE_LEVEL", null);
				switch (world.getStatus())
				{
					case 0:
					{
						if (world.getAliveNpcCount(INNER_BENUSTA) == 0)
						{
							world.spawnGroup("BENUSTA");
						}
						
						startQuestTimer("benusta_texts", 5000, null, player);
						startQuestTimer("check_status", 5000, null, player);
						break;
					}
					case 1:
					{
						world.setStatus(2);
						switch (instanceLv)
						{
							case "105":
							{
								world.spawnGroup("FIRST_AREA_105");
								world.getNpcsOfGroup("FIRST_AREA_105").forEach(mob ->
								{
									if (mob != null)
									{
										mob.setRandomWalking(false);
									}
								});
								break;
							}
							case "110":
							{
								world.spawnGroup("FIRST_AREA_110");
								world.getNpcsOfGroup("FIRST_AREA_110").forEach(mob ->
								{
									if (mob != null)
									{
										mob.setRandomWalking(false);
									}
								});
								break;
							}
							case "115":
							{
								world.spawnGroup("FIRST_AREA_115");
								world.getNpcsOfGroup("FIRST_AREA_115").forEach(mob ->
								{
									if (mob != null)
									{
										mob.setRandomWalking(false);
									}
								});
								break;
							}
							case "120":
							{
								world.spawnGroup("FIRST_AREA_120");
								world.getNpcsOfGroup("FIRST_AREA_120").forEach(mob ->
								{
									if (mob != null)
									{
										mob.setRandomWalking(false);
									}
								});
								break;
							}
						}
						
						world.spawnGroup("KROSHA_VORTEX");
						startQuestTimer("first_room_skill", 1000, null, player);
						startQuestTimer("check_status", 5000, null, player);
						world.setReenterTime();
						break;
					}
					case 2:
					{
						if ((world.getAliveNpcCount(FIRST_AREA_105) == 0) && (world.getAliveNpcCount(FIRST_AREA_110) == 0) && (world.getAliveNpcCount(FIRST_AREA_115) == 0) && (world.getAliveNpcCount(FIRST_AREA_120) == 0))
						{
							world.setStatus(3);
							startQuestTimer("door_closed_effects", 10000, null, player);
							startQuestTimer("door_opening_effects", 5000, null, player);
							startQuestTimer("door_effects_off", 8000, null, player);
							startQuestTimer("check_status", 20000, null, player);
						}
						else
						{
							startQuestTimer("check_status", 1000, null, player);
						}
						break;
					}
					case 3:
					{
						final boolean firstWave = world.getParameters().getBoolean("FIRST_WAVE", false);
						final boolean secondWave = world.getParameters().getBoolean("SECOND_WAVE", false);
						final boolean thirdWave = world.getParameters().getBoolean("THRID_WAVE", false);
						final boolean fourthWave = world.getParameters().getBoolean("FOURTH_WAVE", false);
						switch (instanceLv)
						{
							case "105":
							{
								if (!firstWave)
								{
									world.spawnGroup("SECOND_AREA_105");
									world.getParameters().set("FIRST_WAVE", true);
									showOnScreenMsg(world, NpcStringId.NOW_YOU_WILL_SEE_THE_REAL_POWER_PREPARE_YOURSELF, ExShowScreenMessage.TOP_CENTER, 7000, true);
								}
								else if (!secondWave)
								{
									world.spawnGroup("SECOND_AREA_105_2");
									world.getParameters().set("SECOND_WAVE", true);
								}
								else if (!thirdWave)
								{
									world.spawnGroup("SECOND_AREA_105_3");
									world.getParameters().set("THRID_WAVE", true);
								}
								else if (!fourthWave)
								{
									world.spawnGroup("SECOND_AREA_105_4");
									world.getParameters().set("FOURTH_WAVE", true);
									world.setStatus(4);
								}
								break;
							}
							case "110":
							{
								if (!firstWave)
								{
									world.spawnGroup("SECOND_AREA_110");
									world.getParameters().set("FIRST_WAVE", true);
									showOnScreenMsg(world, NpcStringId.NOW_YOU_WILL_SEE_THE_REAL_POWER_PREPARE_YOURSELF, ExShowScreenMessage.TOP_CENTER, 7000, true);
								}
								else if (!secondWave)
								{
									world.spawnGroup("SECOND_AREA_110_2");
									world.getParameters().set("SECOND_WAVE", true);
								}
								else if (!thirdWave)
								{
									world.spawnGroup("SECOND_AREA_110_3");
									world.getParameters().set("THRID_WAVE", true);
								}
								else if (!fourthWave)
								{
									world.spawnGroup("SECOND_AREA_110_4");
									world.getParameters().set("FOURTH_WAVE", true);
									world.setStatus(4);
								}
								break;
							}
							case "115":
							{
								if (!firstWave)
								{
									world.spawnGroup("SECOND_AREA_115");
									world.getParameters().set("FIRST_WAVE", true);
									showOnScreenMsg(world, NpcStringId.NOW_YOU_WILL_SEE_THE_REAL_POWER_PREPARE_YOURSELF, ExShowScreenMessage.TOP_CENTER, 7000, true);
								}
								else if (!secondWave)
								{
									world.spawnGroup("SECOND_AREA_115_2");
									world.getParameters().set("SECOND_WAVE", true);
								}
								else if (!thirdWave)
								{
									world.spawnGroup("SECOND_AREA_115_3");
									world.getParameters().set("THRID_WAVE", true);
								}
								else if (!fourthWave)
								{
									world.spawnGroup("SECOND_AREA_115_4");
									world.getParameters().set("FOURTH_WAVE", true);
									world.setStatus(4);
								}
								break;
							}
							case "120":
							{
								if (!firstWave)
								{
									world.spawnGroup("SECOND_AREA_120");
									world.getParameters().set("FIRST_WAVE", true);
									showOnScreenMsg(world, NpcStringId.NOW_YOU_WILL_SEE_THE_REAL_POWER_PREPARE_YOURSELF, ExShowScreenMessage.TOP_CENTER, 7000, true);
								}
								else if (!secondWave)
								{
									world.spawnGroup("SECOND_AREA_120_2");
									world.getParameters().set("SECOND_WAVE", true);
								}
								else if (!thirdWave)
								{
									world.spawnGroup("SECOND_AREA_120_3");
									world.getParameters().set("THRID_WAVE", true);
								}
								else if (!fourthWave)
								{
									world.spawnGroup("SECOND_AREA_120_4");
									world.getParameters().set("FOURTH_WAVE", true);
									world.setStatus(4);
								}
								break;
							}
						}
						
						startQuestTimer("check_status", 10000, null, player);
						break;
					}
					case 4:
					{
						if ((world.getAliveNpcCount(SECOND_AREA_105) == 0) && (world.getAliveNpcCount(SECOND_AREA_110) == 0) && (world.getAliveNpcCount(SECOND_AREA_115) == 0) && (world.getAliveNpcCount(SECOND_AREA_120) == 0))
						{
							world.setStatus(5);
							startQuestTimer("door_closed_effects", 5000, null, player);
							startQuestTimer("door_opening_effects", 5000, null, player);
							startQuestTimer("door_effects_off", 8000, null, player);
							world.getParameters().set("FIRST_WAVE", false);
							world.getParameters().set("SECOND_WAVE", false);
							world.getParameters().set("THRID_WAVE", false);
							world.getParameters().set("FOURTH_WAVE", false);
							startQuestTimer("check_status", 20000, null, player);
						}
						else
						{
							startQuestTimer("check_status", 1000, null, player);
						}
						break;
					}
					case 5:
					{
						final boolean firstWave = world.getParameters().getBoolean("FIRST_WAVE", false);
						final boolean secondWave = world.getParameters().getBoolean("SECOND_WAVE", false);
						final boolean thirdWave = world.getParameters().getBoolean("THRID_WAVE", false);
						final boolean fourthWave = world.getParameters().getBoolean("FOURTH_WAVE", false);
						switch (instanceLv)
						{
							case "105":
							{
								if (!firstWave)
								{
									world.spawnGroup("THIRD_AREA_105");
									world.getParameters().set("FIRST_WAVE", true);
									showOnScreenMsg(world, NpcStringId.EXCELLENT_BUT_THAT_S_IT, ExShowScreenMessage.TOP_CENTER, 7000, true);
									startQuestTimer("third_room_skill", 5000, null, player);
								}
								else if (!secondWave)
								{
									world.spawnGroup("THIRD_AREA_105_2");
									world.getParameters().set("SECOND_WAVE", true);
								}
								else if (!thirdWave)
								{
									world.spawnGroup("THIRD_AREA_105_3");
									world.getParameters().set("THRID_WAVE", true);
								}
								else if (!fourthWave)
								{
									world.spawnGroup("THIRD_AREA_105_4");
									world.getParameters().set("FOURTH_WAVE", true);
									world.setStatus(6);
								}
								break;
							}
							case "110":
							{
								if (!firstWave)
								{
									world.spawnGroup("THIRD_AREA_110");
									world.getParameters().set("FIRST_WAVE", true);
									showOnScreenMsg(world, NpcStringId.EXCELLENT_BUT_THAT_S_IT, ExShowScreenMessage.TOP_CENTER, 7000, true);
									startQuestTimer("third_room_skill", 5000, null, player);
								}
								else if (!secondWave)
								{
									world.spawnGroup("THIRD_AREA_110_2");
									world.getParameters().set("SECOND_WAVE", true);
								}
								else if (!thirdWave)
								{
									world.spawnGroup("THIRD_AREA_110_3");
									world.getParameters().set("THRID_WAVE", true);
								}
								else if (!fourthWave)
								{
									world.spawnGroup("THIRD_AREA_110_4");
									world.getParameters().set("FOURTH_WAVE", true);
									world.setStatus(6);
								}
								break;
							}
							case "115":
							{
								if (!firstWave)
								{
									world.spawnGroup("THIRD_AREA_115");
									world.getParameters().set("FIRST_WAVE", true);
									showOnScreenMsg(world, NpcStringId.EXCELLENT_BUT_THAT_S_IT, ExShowScreenMessage.TOP_CENTER, 7000, true);
									startQuestTimer("third_room_skill", 5000, null, player);
								}
								else if (!secondWave)
								{
									world.spawnGroup("THIRD_AREA_115_2");
									world.getParameters().set("SECOND_WAVE", true);
								}
								else if (!thirdWave)
								{
									world.spawnGroup("THIRD_AREA_115_3");
									world.getParameters().set("THRID_WAVE", true);
								}
								else if (!fourthWave)
								{
									world.spawnGroup("THIRD_AREA_115_4");
									world.getParameters().set("FOURTH_WAVE", true);
									world.setStatus(6);
								}
								break;
							}
							case "120":
							{
								if (!firstWave)
								{
									world.spawnGroup("THIRD_AREA_120");
									world.getParameters().set("FIRST_WAVE", true);
									showOnScreenMsg(world, NpcStringId.EXCELLENT_BUT_THAT_S_IT, ExShowScreenMessage.TOP_CENTER, 7000, true);
									startQuestTimer("third_room_skill", 5000, null, player);
								}
								else if (!secondWave)
								{
									world.spawnGroup("THIRD_AREA_120_2");
									world.getParameters().set("SECOND_WAVE", true);
								}
								else if (!thirdWave)
								{
									world.spawnGroup("THIRD_AREA_120_3");
									world.getParameters().set("THRID_WAVE", true);
								}
								else if (!fourthWave)
								{
									world.spawnGroup("THIRD_AREA_120_4");
									world.getParameters().set("FOURTH_WAVE", true);
									world.setStatus(6);
								}
								break;
							}
						}
						
						world.spawnGroup("THIRD_AREA_DUMMY");
						startQuestTimer("check_status", 10000, null, player);
						break;
					}
					case 6:
					{
						if ((world.getAliveNpcCount(THIRD_AREA_105) == 0) && (world.getAliveNpcCount(THIRD_AREA_110) == 0) && (world.getAliveNpcCount(THIRD_AREA_115) == 0) && (world.getAliveNpcCount(THIRD_AREA_120) == 0))
						{
							world.setStatus(7);
							startQuestTimer("door_closed_effects", 5000, null, player);
							startQuestTimer("door_opening_effects", 5000, null, player);
							startQuestTimer("door_effects_off", 8000, null, player);
							startQuestTimer("check_status", 20000, null, player);
						}
						else
						{
							startQuestTimer("check_status", 1000, null, player);
						}
						break;
					}
					case 7:
					{
						world.setStatus(8);
						switch (instanceLv)
						{
							case "105":
							{
								world.spawnGroup("FOURTH_AREA_105");
								world.getNpcsOfGroup("FOURTH_AREA_105").forEach(mob ->
								{
									if (mob != null)
									{
										mob.setRandomWalking(false);
									}
								});
								break;
							}
							case "110":
							{
								world.spawnGroup("FOURTH_AREA_110");
								world.getNpcsOfGroup("FOURTH_AREA_110").forEach(mob ->
								{
									if (mob != null)
									{
										mob.setRandomWalking(false);
									}
								});
								break;
							}
							case "115":
							{
								world.spawnGroup("FOURTH_AREA_115");
								world.getNpcsOfGroup("FOURTH_AREA_115").forEach(mob ->
								{
									if (mob != null)
									{
										mob.setRandomWalking(false);
									}
								});
								break;
							}
							case "120":
							{
								world.spawnGroup("FOURTH_AREA_120");
								world.getNpcsOfGroup("FOURTH_AREA_120").forEach(mob ->
								{
									if (mob != null)
									{
										mob.setRandomWalking(false);
									}
								});
								break;
							}
						}
						
						showOnScreenMsg(world, NpcStringId.WE_ARE_TWO_IN_ONE_WE_WON_T_LOSE_TO_YOU_2, ExShowScreenMessage.TOP_CENTER, 7000, true);
						startQuestTimer("check_status", 1000, null, player);
						break;
					}
					case 8:
					{
						if ((world.getAliveNpcCount(FOURTH_AREA_105) == 0) && (world.getAliveNpcCount(FOURTH_AREA_110) == 0) && (world.getAliveNpcCount(FOURTH_AREA_115) == 0) && (world.getAliveNpcCount(FOURTH_AREA_120) == 0))
						{
							world.setStatus(9);
							startQuestTimer("door_opening_effects", 5000, null, player);
							startQuestTimer("door_effects_off", 8000, null, player);
							switch (instanceLv)
							{
								case "105":
								{
									world.spawnGroup("KROSHA_105");
									break;
								}
								case "110":
								{
									world.spawnGroup("KROSHA_110");
									break;
								}
								case "115":
								{
									world.spawnGroup("KROSHA_115");
									break;
								}
								case "120":
								{
									world.spawnGroup("KROSHA_120");
									break;
								}
							}
							
							world.broadcastPacket(new OnEventTrigger(BOSS_WATERFALL, true));
						}
						
						startQuestTimer("check_status", 1000, null, player);
						break;
					}
				}
				break;
			}
		}
		
		return null;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		final Instance world = player.getInstanceWorld();
		if (isInInstance(world))
		{
			if (player.isInParty())
			{
				final Party party = player.getParty();
				if (!party.isLeader(player))
				{
					return "34654-1.html";
				}
				
				return "34654.html";
			}
			else if (player.isGM())
			{
				return "34654.html";
			}
		}
		
		return null;
	}
	
	@Override
	public void onAttack(Npc npc, Player attacker, int damage, boolean isSummon)
	{
		final Instance world = attacker.getInstanceWorld();
		if (isInInstance(world))
		{
			final boolean kroshnaUD = world.getParameters().getBoolean("KROSHA_BARRIER", false);
			final boolean kroshnaUDDestroyed = world.getParameters().getBoolean("KROSHA_BARRIER_DESTROYED", false);
			final boolean kroshnaSkills = world.getParameters().getBoolean("KROSHA_SKILLS", false);
			if (world.getStatus() == 9)
			{
				if (!kroshnaSkills)
				{
					startQuestTimer("krosha_skills_start", 25000, npc, attacker);
					world.getParameters().set("KROSHA_SKILLS", true);
				}
				
				if ((npc.getCurrentHp() <= (npc.getMaxHp() * 0.5)) && !kroshnaUD)
				{
					startQuestTimer("krosha_barrier_start", 100, null, attacker);
					world.getParameters().set("KROSHA_BARRIER", true);
				}
				
				if (kroshnaUD && !kroshnaUDDestroyed)
				{
					final int hits = KROSHA_HITS.getOrDefault(npc, 0);
					KROSHA_HITS.put(npc, hits + 1);
					
					if (hits > HIT_COUNT)
					{
						world.getParameters().set("KROSHA_BARRIER_DESTROYED", true);
					}
				}
			}
		}
	}
	
	@Override
	public void onKill(Npc npc, Player killer, boolean isPet)
	{
		final Instance world = npc.getInstanceWorld();
		if (isInInstance(world))
		{
			final Party party = world.getFirstPlayer().getParty();
			final Player randomPlayer = party != null ? party.getRandomPlayer() : null;
			final boolean minionKilled = world.getParameters().getBoolean("MINION_KILLED", false);
			if (world.getStatus() == 8)
			{
				if (ArrayUtil.contains(KROTANION_MINION, npc.getId()) && !minionKilled)
				{
					showOnScreenMsg(world, NpcStringId.IN_ANGER_KROPION_PROTECTS_HIMSELF_WITH_A_CURSE, ExShowScreenMessage.TOP_CENTER, 7000, true);
					world.getParameters().set("MINION_KILLED", true);
					world.getPlayers().forEach(player ->
					{
						if (player != null)
						{
							KROPION_CURSE.getSkill().applyEffects(player, player);
						}
					});
				}
				else if (ArrayUtil.contains(KROPION_MINION, npc.getId()) && !minionKilled)
				{
					showOnScreenMsg(world, NpcStringId.IN_ANGER_KROTANION_PUTS_A_CURSE_ON_THOSE_AROUND_HIM, ExShowScreenMessage.TOP_CENTER, 7000, true);
					world.getParameters().set("MINION_KILLED", true);
					world.getPlayers().forEach(player ->
					{
						if (player != null)
						{
							KROTANION_CURSE.getSkill().applyEffects(player, player);
						}
					});
				}
			}
			else if (world.getStatus() == 9)
			{
				switch (npc.getId())
				{
					case KROSHA_105:
					{
						for (Player member : world.getPlayers())
						{
							if ((member.getParty() != null) || member.isGM())
							{
								giveItems(member, BENUSTAS_REWARD_BOX_105);
							}
						}
						
						if ((randomPlayer != null) && (getRandom(100) < 80) && (world.getPlayersCount() == world.getParameters().getInt("INITIAL_PARTY_MEMBERS", 0)) && (randomPlayer.getParty() != null))
						{
							giveItems(randomPlayer, BENUSTAS_SHINING_REWARD_BOX);
						}
						
						world.finishInstance();
						break;
					}
					case KROSHA_110:
					{
						for (Player member : world.getPlayers())
						{
							if ((member.getParty() != null) || member.isGM())
							{
								giveItems(member, BENUSTAS_REWARD_BOX_110);
							}
						}
						
						if ((randomPlayer != null) && (getRandom(100) < 80) && (world.getPlayersCount() == world.getParameters().getInt("INITIAL_PARTY_MEMBERS", 0)) && (randomPlayer.getParty() != null))
						{
							giveItems(randomPlayer, BENUSTAS_SHINING_REWARD_BOX);
						}
						
						world.finishInstance();
						break;
					}
					case KROSHA_115:
					{
						for (Player member : world.getPlayers())
						{
							if ((member.getParty() != null) || member.isGM())
							{
								giveItems(member, BENUSTAS_REWARD_BOX_115);
							}
						}
						
						if ((randomPlayer != null) && (getRandom(100) < 80) && (world.getPlayersCount() == world.getParameters().getInt("INITIAL_PARTY_MEMBERS", 0)) && (randomPlayer.getParty() != null))
						{
							giveItems(randomPlayer, BENUSTAS_SHINING_REWARD_BOX);
						}
						
						world.finishInstance();
						break;
					}
					case KROSHA_120:
					{
						for (Player member : world.getPlayers())
						{
							if ((member.getParty() != null) || member.isGM())
							{
								giveItems(member, BENUSTAS_REWARD_BOX_120);
							}
						}
						
						if ((randomPlayer != null) && (getRandom(100) < 80) && (world.getPlayersCount() == world.getParameters().getInt("INITIAL_PARTY_MEMBERS", 0)) && (randomPlayer.getParty() != null))
						{
							giveItems(randomPlayer, BENUSTAS_SHINING_REWARD_BOX);
						}
						
						world.finishInstance();
						break;
					}
				}
			}
		}
	}
	
	@Override
	public void onInstanceCreated(Instance instance, Player player)
	{
		instance.getParameters().set("INITIAL_PARTY_MEMBERS", player.getParty() != null ? player.getParty().getMemberCount() : 1);
	}
	
	public static void main(String[] args)
	{
		new KrofinNest();
	}
}
