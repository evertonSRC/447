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
package instances.AshenShadowCamp;

import java.util.List;

import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.groups.Party;
import org.l2jmobius.gameserver.model.instancezone.Instance;
import org.l2jmobius.gameserver.model.item.holders.ItemHolder;
import org.l2jmobius.gameserver.model.script.InstanceScript;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.enums.ChatType;
import org.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2jmobius.gameserver.util.ArrayUtil;

/**
 * @author Mobius, Liamxroy, Tanatos
 */
public class AshenShadowCamp extends InstanceScript
{
	// NPCs
	private static final int BENUSTA = 34542;
	private static final int INNER_BENUSTA = 34651;
	private static final int EKZITA = 34573;
	
	// Monsters
	private static final int SIGNALMAN_105 = 23651;
	private static final int SIGNALMAN_110 = 24811;
	private static final int SIGNALMAN_115 = 24770;
	private static final int SIGNALMAN_120 = 24719;
	private static final int[] REVOLUTIONARIES_105 =
	{
		23640, // Unit 1 Elite Soldier
		23641, // Unit 2 Elite Soldier
		23642, // Unit 3 Elite Soldier
		23643, // Unit 4 Elite Soldier
		23644, // Unit 5 Elite Soldier
		23645, // Unit 6 Elite Soldier
		23646, // Unit 7 Elite Soldier
		23647, // Unit 8 Elite Soldier
		23648, // Dark Crusader (isSummon)
		23649, // Banshee Queen (isSummon)
		SIGNALMAN_105, // Unit Signalman
		23652, // Unit Guard
	};
	private static final int[] REVOLUTIONARIES_110 =
	{
		24800, // Knight Agar
		24801, // Warrior Ule
		24802, // Rogue Fiord
		24803, // Archer Torn
		24804, // Mage Kenaz
		24805, // Enchanter Nied
		24806, // Summoner Inke
		24807, // Healer Zera
		24808, // Soul Specter (isSummon)
		24809, // Banshee Queen (isSummon)
		SIGNALMAN_110, // Unit Signalman
		24812, // Unit Guard
	};
	private static final int[] REVOLUTIONARIES_115 =
	{
		24760, // Knight Sabra
		24761, // Warrior Akna
		24762, // Rogue Daivan
		24763, // Archer Keoba
		24764, // Mage Maona
		24765, // Enchanter Pio
		24766, // Summoner Kai
		24767, // Healer Luin
		24768, // Soul Specter (isSummon)
		24769, // Banshee Queen (isSummon)
		SIGNALMAN_115, // Unit Signalman
		24771, // Unit Guard
	};
	private static final int[] REVOLUTIONARIES_120 =
	{
		24709, // Knight Zorin
		24710, // Warrior Anot
		24711, // Rogue Strech
		24712, // Archer Kilin
		24713, // Mage Allun
		24714, // Enchanter Skara
		24715, // Summoner Bran
		24716, // Healer Keraim
		24717, // Soul Specter (isSummon)
		24718, // Banshee Queen (isSummon)
		SIGNALMAN_120, // Unit Signalman
		24720, // Unit Guard
	};
	private static final int[] COMMANDERS_105 =
	{
		23653, // Unit Commander 1
		23654, // Unit Commander 2
		23655, // Unit Commander 2
		23656, // Unit Commander 2
		23657, // Unit Commander 3
		23658, // Unit Commander 4
		23659, // Unit Commander 4
		23660, // Unit Commander 5
		23661, // Unit Commander 6
		23662, // Unit Commander 7
		23663, // Unit Commander 8
		23664, // Unit Commander 8
	};
	private static final int[] COMMANDERS_110 =
	{
		24813, // Knight Agar
		24814, // Warrior Ule
		24815, // Warrior Ule
		24816, // Warrior Ule
		24817, // Rogue Fiord
		24818, // Archer Torn
		24819, // Archer Torn
		24820, // Mage Kenaz
		24821, // Enchanter Nied
		24822, // Summoner Inke
		24823, // Healer Zera
		24824, // Healer Zera
	};
	private static final int[] COMMANDERS_115 =
	{
		24772, // Knight Sabra
		24773, // Warrior Akna
		24774, // Warrior Akna
		24775, // Warrior Akna
		24776, // Rogue Daivan
		24777, // Archer Keoba
		24778, // Archer Keoba
		24779, // Mage Maona
		24780, // Enchanter Pio
		24781, // Summoner Kai
		24782, // Healer Luin
		24783, // Healer Luin
	};
	private static final int[] COMMANDERS_120 =
	{
		24721, // Knight Zorin
		24722, // Warrior Anot
		24723, // Warrior Anot
		24724, // Warrior Anot
		24725, // Rogue Strech
		24726, // Archer Kilin
		24727, // Archer Kilin
		24728, // Mage Allun
		24729, // Enchanter Skara
		24730, // Summoner Bran
		24731, // Healer Keraim
		24732, // Healer Keraim
	};
	private static final int[] RAIDBOSSES_105 =
	{
		23665, // Unit Commander 1
		23666, // Unit Commander 2
		23667, // Unit Commander 2
		23668, // Unit Commander 2
		23669, // Unit Commander 3
		23670, // Unit Commander 4
		23671, // Unit Commander 4
		23672, // Unit Commander 5
		23673, // Unit Commander 6
		23674, // Unit Commander 7
		23675, // Unit Commander 8
		23676, // Unit Commander 8
	};
	private static final int[] RAIDBOSSES_110 =
	{
		24825, // Knight Agar
		24826, // Warrior Ule
		24827, // Warrior Ule
		24828, // Warrior Ule
		24829, // Rogue Fiord
		24830, // Archer Torn
		24831, // Archer Torn
		24832, // Mage Kenaz
		24833, // Enchanter Nied
		24834, // Summoner Inke
		24835, // Healer Zera
		24836, // Healer Zera
	};
	private static final int[] RAIDBOSSES_115 =
	{
		24784, // Knight Sabra
		24785, // Warrior Akna
		24786, // Warrior Akna
		24787, // Warrior Akna
		24788, // Rogue Daivan
		24789, // Archer Keoba
		24790, // Archer Keoba
		24791, // Mage Maona
		24792, // Enchanter Pio
		24793, // Summoner Kai
		24794, // Healer Luin
		24795, // Healer Luin
	};
	private static final int[] RAIDBOSSES_120 =
	{
		24733, // Knight Zorin
		24734, // Warrior Anot
		24735, // Warrior Anot
		24736, // Warrior Anot
		24737, // Rogue Strech
		24738, // Archer Kilin
		24739, // Archer Kilin
		24740, // Mage Allun
		24741, // Enchanter Skara
		24742, // Summoner Bran
		24743, // Healer Keraim
		24744, // Healer Keraim
	};
	
	// Items
	private static final ItemHolder BENUSTAS_REWARD_BOX_105 = new ItemHolder(82453, 1);
	private static final ItemHolder BENUSTAS_REWARD_BOX_110 = new ItemHolder(82454, 1);
	private static final ItemHolder BENUSTAS_REWARD_BOX_115 = new ItemHolder(82455, 1);
	private static final ItemHolder BENUSTAS_REWARD_BOX_120 = new ItemHolder(82925, 1);
	private static final ItemHolder BENUSTAS_SHINING_REWARD_BOX = new ItemHolder(82456, 1);
	
	// Locations
	private static final Location COMMANDER_LOCATION = new Location(-81911, 154244, -3177);
	private static final Location RAIDBOSS_LOCATION = new Location(-83028, 150866, -3128);
	
	// Misc
	private static final int TEMPLATE_ID = 319;
	
	public AshenShadowCamp()
	{
		super(TEMPLATE_ID);
		addStartNpc(BENUSTA);
		addFirstTalkId(INNER_BENUSTA, EKZITA);
		addSpawnId(REVOLUTIONARIES_105);
		addSpawnId(REVOLUTIONARIES_110);
		addSpawnId(REVOLUTIONARIES_115);
		addSpawnId(REVOLUTIONARIES_120);
		addSpawnId(COMMANDERS_105);
		addSpawnId(COMMANDERS_110);
		addSpawnId(COMMANDERS_115);
		addSpawnId(COMMANDERS_120);
		addSpawnId(RAIDBOSSES_105);
		addSpawnId(RAIDBOSSES_110);
		addSpawnId(RAIDBOSSES_115);
		addSpawnId(RAIDBOSSES_120);
		addKillId(SIGNALMAN_105, SIGNALMAN_110, SIGNALMAN_115, SIGNALMAN_120);
		addKillId(COMMANDERS_105);
		addKillId(COMMANDERS_110);
		addKillId(COMMANDERS_115);
		addKillId(COMMANDERS_120);
		addKillId(RAIDBOSSES_105);
		addKillId(RAIDBOSSES_110);
		addKillId(RAIDBOSSES_115);
		addKillId(RAIDBOSSES_120);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		switch (event)
		{
			case "34651-1.html":
			case "34651-2.html":
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
				break;
			}
			case "exitInstance":
			{
				final Instance world = player.getInstanceWorld();
				if (!isInInstance(world))
				{
					return null;
				}
				
				world.ejectPlayer(player);
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
								world.spawnGroup("WAVE1_105");
								break;
							}
							case "110":
							{
								world.spawnGroup("WAVE1_110");
								break;
							}
							case "115":
							{
								world.spawnGroup("WAVE1_115");
								break;
							}
							case "120":
							{
								world.spawnGroup("WAVE1_120");
								break;
							}
						}
						
						world.setReenterTime();
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
			if (npc.getId() == INNER_BENUSTA)
			{
				if (player.isInParty())
				{
					final Party party = player.getParty();
					if (!party.isLeader(player))
					{
						return "34651-1.html";
					}
					
					return "34651.html";
				}
				else if (player.isGM())
				{
					return "34651.html";
				}
			}
			else if (npc.getId() == EKZITA)
			{
				return "34573.html";
			}
		}
		
		return null;
	}
	
	@Override
	public void onKill(Npc npc, Player player, boolean isSummon)
	{
		final Instance world = npc.getInstanceWorld();
		if (isInInstance(world))
		{
			final Party party = world.getFirstPlayer().getParty();
			final Player randomPlayer = party != null ? party.getRandomPlayer() : null;
			final String instanceLv = world.getParameters().getString("INSTANCE_LEVEL", null);
			
			// Handle Signalman kills - prevent multiple spawns by checking and setting flags.
			if ((npc.getId() == SIGNALMAN_105) || (npc.getId() == SIGNALMAN_110) || (npc.getId() == SIGNALMAN_115) || (npc.getId() == SIGNALMAN_120))
			{
				// Check if we're in wave 1 (status 2) and haven't spawned commander yet.
				if (world.isStatus(2) && !world.getParameters().getBoolean("COMMANDER_SPAWNED", false))
				{
					world.getParameters().set("COMMANDER_SPAWNED", true);
					world.setStatus(3);
					
					switch (instanceLv)
					{
						case "105":
						{
							addSpawn(getRandomEntry(COMMANDERS_105), COMMANDER_LOCATION, false, 0, false, world.getId());
							break;
						}
						case "110":
						{
							addSpawn(getRandomEntry(COMMANDERS_110), COMMANDER_LOCATION, false, 0, false, world.getId());
							break;
						}
						case "115":
						{
							addSpawn(getRandomEntry(COMMANDERS_115), COMMANDER_LOCATION, false, 0, false, world.getId());
							break;
						}
						case "120":
						{
							addSpawn(getRandomEntry(COMMANDERS_120), COMMANDER_LOCATION, false, 0, false, world.getId());
							break;
						}
					}
				}
				// Check if we're in wave 2 (status 4) and haven't spawned raidboss yet.
				else if (world.isStatus(4) && !world.getParameters().getBoolean("RAIDBOSS_SPAWNED", false))
				{
					world.getParameters().set("RAIDBOSS_SPAWNED", true);
					
					switch (instanceLv)
					{
						case "105":
						{
							addSpawn(getRandomEntry(RAIDBOSSES_105), RAIDBOSS_LOCATION, false, 0, false, world.getId());
							break;
						}
						case "110":
						{
							addSpawn(getRandomEntry(RAIDBOSSES_110), RAIDBOSS_LOCATION, false, 0, false, world.getId());
							break;
						}
						case "115":
						{
							addSpawn(getRandomEntry(RAIDBOSSES_115), RAIDBOSS_LOCATION, false, 0, false, world.getId());
							break;
						}
						case "120":
						{
							addSpawn(getRandomEntry(RAIDBOSSES_120), RAIDBOSS_LOCATION, false, 0, false, world.getId());
							break;
						}
					}
				}
			}
			// Handle Commander kills - spawn wave 2.
			else if ((ArrayUtil.contains(COMMANDERS_105, npc.getId())) || (ArrayUtil.contains(COMMANDERS_110, npc.getId())) || (ArrayUtil.contains(COMMANDERS_115, npc.getId())) || (ArrayUtil.contains(COMMANDERS_120, npc.getId())))
			{
				world.setStatus(4);
				switch (instanceLv)
				{
					case "105":
					{
						world.spawnGroup("WAVE2_105");
						break;
					}
					case "110":
					{
						world.spawnGroup("WAVE2_110");
						break;
					}
					case "115":
					{
						world.spawnGroup("WAVE2_115");
						break;
					}
					case "120":
					{
						world.spawnGroup("WAVE2_120");
						break;
					}
				}
			}
			// Handle Raidboss kills - finish instance.
			else if ((ArrayUtil.contains(RAIDBOSSES_105, npc.getId())) || (ArrayUtil.contains(RAIDBOSSES_110, npc.getId())) || (ArrayUtil.contains(RAIDBOSSES_115, npc.getId())) || (ArrayUtil.contains(RAIDBOSSES_120, npc.getId())))
			{
				switch (instanceLv)
				{
					case "105":
					{
						for (Player member : world.getPlayers())
						{
							if ((member.getParty() != null) || (member.isGM()))
							{
								giveItems(member, BENUSTAS_REWARD_BOX_105);
							}
						}
						
						if ((randomPlayer != null) && (getRandom(100) < 80) && (world.getPlayersCount() == world.getParameters().getInt("INITIAL_PARTY_MEMBERS", 0)))
						{
							if (randomPlayer.getParty() != null)
							{
								giveItems(randomPlayer, BENUSTAS_SHINING_REWARD_BOX);
							}
						}
						break;
					}
					case "110":
					{
						for (Player member : world.getPlayers())
						{
							if ((member.getParty() != null) || (member.isGM()))
							{
								giveItems(member, BENUSTAS_REWARD_BOX_110);
							}
						}
						
						if ((randomPlayer != null) && (getRandom(100) < 80) && (world.getPlayersCount() == world.getParameters().getInt("INITIAL_PARTY_MEMBERS", 0)))
						{
							if (randomPlayer.getParty() != null)
							{
								giveItems(randomPlayer, BENUSTAS_SHINING_REWARD_BOX);
							}
						}
						break;
					}
					case "115":
					{
						for (Player member : world.getPlayers())
						{
							if ((member.getParty() != null) || (member.isGM()))
							{
								giveItems(member, BENUSTAS_REWARD_BOX_115);
							}
						}
						
						if ((randomPlayer != null) && (getRandom(100) < 80) && (world.getPlayersCount() == world.getParameters().getInt("INITIAL_PARTY_MEMBERS", 0)))
						{
							if (randomPlayer.getParty() != null)
							{
								giveItems(randomPlayer, BENUSTAS_SHINING_REWARD_BOX);
							}
						}
						break;
					}
					case "120":
					{
						for (Player member : world.getPlayers())
						{
							if ((member.getParty() != null) || (member.isGM()))
							{
								giveItems(member, BENUSTAS_REWARD_BOX_120);
							}
						}
						
						if ((randomPlayer != null) && (getRandom(100) < 80) && (world.getPlayersCount() == world.getParameters().getInt("INITIAL_PARTY_MEMBERS", 0)))
						{
							if (randomPlayer.getParty() != null)
							{
								giveItems(randomPlayer, BENUSTAS_SHINING_REWARD_BOX);
							}
						}
						break;
					}
				}
				
				world.spawnGroup("EKZITA");
				world.getNpc(EKZITA).broadcastSay(ChatType.NPC_GENERAL, NpcStringId.I_WILL_HELP_YOU_ESCAPE_FROM_HERE);
				world.finishInstance();
			}
		}
	}
	
	@Override
	public void onInstanceCreated(Instance instance, Player player)
	{
		instance.getParameters().set("INITIAL_PARTY_MEMBERS", player.getParty() != null ? player.getParty().getMemberCount() : 1);
	}
	
	@Override
	public void onSpawn(Npc npc)
	{
		npc.setRandomWalking(false);
	}
	
	public static void main(String[] args)
	{
		new AshenShadowCamp();
	}
}
