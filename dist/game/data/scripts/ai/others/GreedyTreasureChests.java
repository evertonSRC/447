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
package ai.others;

import org.l2jmobius.gameserver.managers.GlobalVariablesManager;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.script.Script;

/**
 * Greedy Treasure Chests AI.
 * @URL https://dev.l2central.info/main/articles/355.html
 * @author CostyKiller, NasSeKa
 */
public class GreedyTreasureChests extends Script
{
	// NPCs
	private static final int CHEST_LV110 = 8710;
	private static final int CHEST_LV120 = 8711;
	// @formatter:off
	private static final int[] MONSTERS = 
	{
		// Silent Valley
		24506, 24507, 24508, 24509, 24510,
		// Ivory Tower Crater
		24421, 24422, 24423, 24424, 24425, 24426,
		// Tanor Canyon
		20936, 20937, 20938, 20939, 20940, 20941, 20942, 20943, 24587,
		// Alligator Island
		24373, 24376, 24377,	
		// Field of Silence
		24517, 24520, 24521, 24522, 24523,
		// Forest of Mirrors
		24461, 24462, 24463, 24464, 24465, 24466,
		// Varka Silenos Barracks
		24636, 24637, 24638, 24639, 24640, 				  
		// Ketra Orc Outpost
		24631, 24632, 24633, 24634, 24635,
		// Field of Whispers
		24304, 24305, 24306, 24307, 24308,
		// Isle of Prayer
		24445, 24446, 24447, 24448, 24449, 24450, 24451,
		// Breka's Stronghold
		24415, 24416, 24417, 24418, 24419, 24420,
		// Sel Mahum Training Grounds
		24492, 24493, 24494, 24495,
		// Plains of Lizardman
		24496, 24497, 24498, 24499,
		// Fields of Massacre
		24486, 24487, 24488, 24489, 24490, 24491,
		// Sea Of Spores
		24621, 24622, 24623, 24624,
		// Dragon Valley
		24481, 24482,
		// Fafurion Temple
		24318, 24322, 24323, 24325, 24329,
		// Wastelands
		24500, 24501, 24502, 24503, 24504, 24505,
		// Beast Farm
		24651, 24652, 24653, 24654, 24655, 24656, 24657, 24658, 24659,
		// Valley of Saints
		24876, 24877, 24878, 24879, 24880,
		// Hot Spirits
		24881, 24882, 24883, 24884, 24885, 24886,
		// Argos Wall 116 lvl (not 122)
		24606, 24607, 24608, 24609, 24610, 24611,
		// Neutral Zone (108)
		24641, 24642, 24643, 24644,
	};
	// @formatter:on
	
	// Misc
	private static final int TREASURE_CHEST_CHANCE = 15; // 15% chance to spawn
	private static final int RND_OFFSET = 10800000; // 3 hours = 10.800.000 milliseconds
	private static final long RESPAWN_DELAY = 43200000; // 12 hours = 43.200.000 milliseconds
	
	private GreedyTreasureChests()
	{
		super();
		addStartNpc(CHEST_LV110);
		addStartNpc(CHEST_LV120);
		addTalkId(CHEST_LV110);
		addTalkId(CHEST_LV120);
		addKillId(CHEST_LV110);
		addKillId(CHEST_LV120);
		addKillId(MONSTERS);
	}
	
	@Override
	public void onKill(Npc npc, Player killer, boolean isSummon)
	{
		if (getRandom(100) < TREASURE_CHEST_CHANCE)
		{
			final long currentTime = System.currentTimeMillis();
			switch (npc.getId())
			{
				// Silent Valley
				case 24506:
				case 24507:
				case 24508:
				case 24509:
				case 24510:
				{
					if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_SV_1", 0))
					{
						addSpawn(CHEST_LV110, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_SV_1", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_SV_2", 0))
					{
						addSpawn(CHEST_LV110, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_SV_2", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					break;
				}
				// Ivory Tower Crater
				case 24421:
				case 24422:
				case 24423:
				case 24424:
				case 24425:
				case 24426:
				{
					if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_IT_1", 0))
					{
						addSpawn(CHEST_LV110, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_IT_1", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_IT_2", 0))
					{
						addSpawn(CHEST_LV110, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_IT_2", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					break;
				}
				// Tanor Canyon
				case 20936:
				case 20937:
				case 20938:
				case 20939:
				case 20940:
				case 20941:
				case 20942:
				case 20943:
				case 24587:
				{
					if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_TC_1", 0))
					{
						addSpawn(CHEST_LV110, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_TC_1", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_TC_2", 0))
					{
						addSpawn(CHEST_LV110, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_TC_2", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					break;
				}
				// Alligator Island
				case 24373:
				case 24376:
				case 24377:
				{
					if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_AI_1", 0))
					{
						addSpawn(CHEST_LV110, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_AI_1", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_AI_2", 0))
					{
						addSpawn(CHEST_LV110, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_AI_2", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					break;
				}
				// Field of Silence
				case 24517:
				case 24520:
				case 24521:
				case 24522:
				case 24523:
				{
					if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_FS_1", 0))
					{
						addSpawn(CHEST_LV110, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_FS_1", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_FS_2", 0))
					{
						addSpawn(CHEST_LV110, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_FS_2", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					break;
				}
				// Forest of Mirrors
				case 24461:
				case 24462:
				case 24463:
				case 24464:
				case 24465:
				case 24466:
				{
					if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_FM_1", 0))
					{
						addSpawn(CHEST_LV110, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_FM_1", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_FM_2", 0))
					{
						addSpawn(CHEST_LV110, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_FM_2", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_FM_3", 0))
					{
						addSpawn(CHEST_LV110, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_FM_3", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_FM_4", 0))
					{
						addSpawn(CHEST_LV110, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_FM_4", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					break;
				}
				// Varka Silenos Barracks
				case 24636:
				case 24637:
				case 24638:
				case 24639:
				case 24640:
				{
					if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_VS_1", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_VS_1", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_VS_2", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_VS_2", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					break;
				}
				// Ketra Orc Outpost
				case 24631:
				case 24632:
				case 24633:
				case 24634:
				case 24635:
				{
					if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_KO_1", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_KO_1", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_KO_2", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_KO_2", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					break;
				}
				// Field of Whispers
				case 24304:
				case 24305:
				case 24306:
				case 24307:
				case 24308:
				{
					if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_FW_1", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_FW_1", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_FW_2", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_FW_2", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					break;
				}
				// Isle of Prayer
				case 24445:
				case 24446:
				case 24447:
				case 24448:
				case 24449:
				case 24450:
				case 24451:
				{
					if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_IP_1", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_IP_1", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_IP_2", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_IP_2", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					break;
				}
				// Breka's Stronghold
				case 24415:
				case 24416:
				case 24417:
				case 24418:
				case 24419:
				case 24420:
				{
					if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_BS_1", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_BS_1", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_BS_2", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_BS_2", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					break;
				}
				// Sel Mahum Training Grounds
				case 24492:
				case 24493:
				case 24494:
				case 24495:
				{
					if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_SM_1", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_SM_1", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_SM_2", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_SM_2", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					break;
				}
				// Plains of Lizardman
				case 24496:
				case 24497:
				case 24498:
				case 24499:
				{
					if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_PL_1", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_PL_1", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_PL_2", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_PL_2", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					break;
				}
				// Fields of Massacre
				case 24486:
				case 24487:
				case 24488:
				case 24489:
				case 24490:
				case 24491:
				{
					if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_FOM_1", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_FOM_1", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_FOM_2", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_FOM_2", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					break;
				}
				// Sea Of Spores
				case 24621:
				case 24622:
				case 24623:
				case 24624:
				{
					if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_SS_1", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_SS_1", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_SS_2", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_SS_2", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					break;
				}
				// Dragon Valley
				case 24481:
				case 24482:
				{
					if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_DV_1", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_DV_1", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_DV_2", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_DV_2", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					break;
				}
				// Fafurion Temple
				case 24318:
				case 24322:
				case 24323:
				case 24325:
				case 24329:
				{
					if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_FT_1", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_FT_1", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_FT_2", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_FT_2", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					break;
				}
				// Wastelands
				case 24500:
				case 24501:
				case 24502:
				case 24503:
				case 24504:
				case 24505:
				{
					if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_WS_1", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_WS_1", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_WS_2", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_WS_2", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_WS_3", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_WS_3", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					break;
				}
				// Beast Farm
				case 24651:
				case 24652:
				case 24653:
				case 24654:
				case 24655:
				case 24656:
				case 24657:
				case 24658:
				case 24659:
				{
					if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_BF_1", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_BF_1", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_BF_2", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_BF_2", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_BF_3", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_BF_3", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					break;
				}
				// Valley of Saints
				case 24876:
				case 24877:
				case 24878:
				case 24879:
				case 24880:
				{
					if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_VoS_1", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_VoS_1", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_VoS_2", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_VoS_2", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_VoS_3", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_VoS_3", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_VoS_4", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_VoS_4", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					break;
				}
				// Hot Spirits
				case 24881:
				case 24882:
				case 24883:
				case 24884:
				case 24885:
				case 24886:
				{
					if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_HS_1", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_HS_1", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_HS_2", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_HS_2", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_HS_3", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_HS_3", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_HS_4", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_HS_4", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					break;
				}
				// Argos Wall 116 lvl (not 122)
				case 24606:
				case 24607:
				case 24608:
				case 24609:
				case 24610:
				case 24611:
				{
					if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_AW_1", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_AW_1", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_AW_2", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_AW_2", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_AW_3", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_AW_3", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_AW_4", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_AW_4", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					break;
				}
				// Neutral Zone (108)
				case 24641:
				case 24642:
				case 24643:
				case 24644:
				{
					if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_NZ_1", 0))
					{
						addSpawn(CHEST_LV110, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_NZ_1", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_NZ_2", 0))
					{
						addSpawn(CHEST_LV110, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_NZ_2", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					break;
				}
			}
		}
	}
	
	public static void main(String[] args)
	{
		new GreedyTreasureChests();
	}
}
