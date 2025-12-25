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
package ai.areas.Conquest;

import java.util.HashSet;
import java.util.Set;

import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.gameserver.data.xml.SpawnData;
import org.l2jmobius.gameserver.managers.ZoneManager;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.script.Script;
import org.l2jmobius.gameserver.model.spawns.SpawnGroup;
import org.l2jmobius.gameserver.model.spawns.SpawnTemplate;
import org.l2jmobius.gameserver.model.zone.type.ConquestZone;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.enums.ChatType;
import org.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;

/**
 * Conquest Area Special Mobs AI.
 * @URL https://l2central.info/main/locations/activity/conquest/
 * @author CostyKiller
 */
public final class ConquestAreasSpecialMobs extends Script
{
	// Special Mobs
	// Soul Flower
	private static final int SOUL_FLOWER_ASA_1 = 19818; // Soul Flower Asa Region 1
	private static final int SOUL_FLOWER_ASA_2 = 19819; // Soul Flower Asa Region 2
	private static final int SOUL_FLOWER_ASA_3 = 19820; // Soul Flower Asa Region 3
	private static final int SOUL_FLOWER_ANIMA_1 = 19821; // Soul Flower Anima Region 1
	private static final int SOUL_FLOWER_ANIMA_2 = 19822; // Soul Flower Anima Region 2
	private static final int SOUL_FLOWER_ANIMA_3 = 19823; // Soul Flower Anima Region 3
	private static final int SOUL_FLOWER_NOX_1 = 19824; // Soul Flower Nox Region 1
	private static final int SOUL_FLOWER_NOX_2 = 19825; // Soul Flower Nox Region 2
	private static final int SOUL_FLOWER_NOX_3 = 19826; // Soul Flower Nox Region 3
	private static final int SOUL_FLOWER_VITA = 19840; // Soul Flower Vita
	private static final int SOUL_FLOWER_IGNIS = 19841; // Soul Flower Ignis
	private static final Set<Integer> SOUL_FLOWERS = new HashSet<>();
	static
	{
		SOUL_FLOWERS.add(SOUL_FLOWER_ASA_1);
		SOUL_FLOWERS.add(SOUL_FLOWER_ASA_2);
		SOUL_FLOWERS.add(SOUL_FLOWER_ASA_3);
		SOUL_FLOWERS.add(SOUL_FLOWER_ANIMA_1);
		SOUL_FLOWERS.add(SOUL_FLOWER_ANIMA_2);
		SOUL_FLOWERS.add(SOUL_FLOWER_ANIMA_3);
		SOUL_FLOWERS.add(SOUL_FLOWER_NOX_1);
		SOUL_FLOWERS.add(SOUL_FLOWER_NOX_2);
		SOUL_FLOWERS.add(SOUL_FLOWER_NOX_3);
		SOUL_FLOWERS.add(SOUL_FLOWER_VITA);
		SOUL_FLOWERS.add(SOUL_FLOWER_IGNIS);
	}
	
	// Soul Tree
	private static final int SOUL_TREE_ASA = 19827; // Soul Tree Asa
	private static final int SOUL_TREE_ANIMA = 19829; // Soul Tree Anima
	private static final int SOUL_TREE_NOX = 19828; // Soul Tree Nox
	private static final int SOUL_TREE_VITA = 19842; // Soul Tree Vita
	private static final int SOUL_TREE_IGNIS = 19843; // Soul Tree Ignis
	private static final Set<Integer> SOUL_TREE_MINIONS = new HashSet<>();
	static
	{
		SOUL_TREE_MINIONS.add(27776);
		SOUL_TREE_MINIONS.add(27777);
		SOUL_TREE_MINIONS.add(27778);
		SOUL_TREE_MINIONS.add(27779);
		SOUL_TREE_MINIONS.add(27780);
		SOUL_TREE_MINIONS.add(27781);
		SOUL_TREE_MINIONS.add(27782);
		SOUL_TREE_MINIONS.add(27783);
		SOUL_TREE_MINIONS.add(27784);
		SOUL_TREE_MINIONS.add(27826);
		SOUL_TREE_MINIONS.add(27827);
		SOUL_TREE_MINIONS.add(27828);
		SOUL_TREE_MINIONS.add(27829);
		SOUL_TREE_MINIONS.add(27830);
		SOUL_TREE_MINIONS.add(27831);
	}
	
	// Water Zone Avengers
	private static final int DARIL_WATER_SEO = 27703; // Seo
	private static final int DARIL_WATER_CRAIGO = 27704; // Craigo
	private static final int DARIL_PHRAN_SEO = 27709; // Seo Phan
	private static final int DARIL_PHRAN_CRAIGO = 27710; // Craigo Phran
	
	// Fire Zone Avengers
	private static final int CATSHI_AGEL_AVENGER = 27807; // Catshi Agel Avenger
	private static final int CATSHI_GOF_AVENGER = 27800; // Catshi Gof Avenger
	
	// Trigger Mobs Water Zones
	// Water Zone Avengers
	private static final int TRIGGER_DARIL_WATER_DARIL = 27701; // Daril
	private static final int TRIGGER_DARIL_WATER_ATRON = 27702; // Atron
	private static final int TRIGGER_DARIL_PHRAN_DARIL = 27707; // Daril Phran
	private static final int TRIGGER_DARIL_PHRAN_ATRON = 27708; // Atron Phran
	private static final Set<Integer> TRIGGER_MOBS_ASA_1 = new HashSet<>(); // Asa Region 1
	static
	{
		TRIGGER_MOBS_ASA_1.add(27713);
		TRIGGER_MOBS_ASA_1.add(27714);
		TRIGGER_MOBS_ASA_1.add(27715);
		TRIGGER_MOBS_ASA_1.add(27716);
		TRIGGER_MOBS_ASA_1.add(27717);
		TRIGGER_MOBS_ASA_1.add(27718);
	}
	private static final Set<Integer> TRIGGER_MOBS_ASA_2 = new HashSet<>(); // Asa Region 2
	static
	{
		TRIGGER_MOBS_ASA_2.add(27719);
		TRIGGER_MOBS_ASA_2.add(27720);
		TRIGGER_MOBS_ASA_2.add(27721);
		TRIGGER_MOBS_ASA_2.add(27722);
		TRIGGER_MOBS_ASA_2.add(27723);
		TRIGGER_MOBS_ASA_2.add(27724);
		TRIGGER_MOBS_ASA_2.add(27725);
	}
	private static final Set<Integer> TRIGGER_MOBS_ASA_3 = new HashSet<>(); // Asa Region 3
	static
	{
		TRIGGER_MOBS_ASA_3.add(27726);
		TRIGGER_MOBS_ASA_3.add(27727);
		TRIGGER_MOBS_ASA_3.add(27728);
		TRIGGER_MOBS_ASA_3.add(27729);
		TRIGGER_MOBS_ASA_3.add(27730);
		TRIGGER_MOBS_ASA_3.add(27731);
		TRIGGER_MOBS_ASA_3.add(27732);
		TRIGGER_MOBS_ASA_3.add(27733);
	}
	private static final Set<Integer> TRIGGER_MOBS_ANIMA_1 = new HashSet<>(); // Anima Region 1
	static
	{
		TRIGGER_MOBS_ANIMA_1.add(27755);
		TRIGGER_MOBS_ANIMA_1.add(27756);
		TRIGGER_MOBS_ANIMA_1.add(27757);
		TRIGGER_MOBS_ANIMA_1.add(27758);
		TRIGGER_MOBS_ANIMA_1.add(27759);
		TRIGGER_MOBS_ANIMA_1.add(27760);
	}
	private static final Set<Integer> TRIGGER_MOBS_ANIMA_2 = new HashSet<>(); // Anima Region 2
	static
	{
		TRIGGER_MOBS_ANIMA_2.add(27761);
		TRIGGER_MOBS_ANIMA_2.add(27762);
		TRIGGER_MOBS_ANIMA_2.add(27763);
		TRIGGER_MOBS_ANIMA_2.add(27764);
		TRIGGER_MOBS_ANIMA_2.add(27765);
		TRIGGER_MOBS_ANIMA_2.add(27766);
		TRIGGER_MOBS_ANIMA_2.add(27767);
	}
	private static final Set<Integer> TRIGGER_MOBS_ANIMA_3 = new HashSet<>(); // Anima Region 3
	static
	{
		TRIGGER_MOBS_ANIMA_3.add(27768);
		TRIGGER_MOBS_ANIMA_3.add(27769);
		TRIGGER_MOBS_ANIMA_3.add(27770);
		TRIGGER_MOBS_ANIMA_3.add(27771);
		TRIGGER_MOBS_ANIMA_3.add(27772);
		TRIGGER_MOBS_ANIMA_3.add(27773);
		TRIGGER_MOBS_ANIMA_3.add(27774);
		TRIGGER_MOBS_ANIMA_3.add(27775);
	}
	private static final Set<Integer> TRIGGER_MOBS_NOX_1 = new HashSet<>(); // Nox Region 1
	static
	{
		TRIGGER_MOBS_NOX_1.add(27734);
		TRIGGER_MOBS_NOX_1.add(27735);
		TRIGGER_MOBS_NOX_1.add(27736);
		TRIGGER_MOBS_NOX_1.add(27737);
		TRIGGER_MOBS_NOX_1.add(27738);
		TRIGGER_MOBS_NOX_1.add(27739);
	}
	private static final Set<Integer> TRIGGER_MOBS_NOX_2 = new HashSet<>(); // Nox Region 2
	static
	{
		TRIGGER_MOBS_NOX_2.add(27740);
		TRIGGER_MOBS_NOX_2.add(27741);
		TRIGGER_MOBS_NOX_2.add(27742);
		TRIGGER_MOBS_NOX_2.add(27743);
		TRIGGER_MOBS_NOX_2.add(27745);
		TRIGGER_MOBS_NOX_2.add(27746);
	}
	private static final Set<Integer> TRIGGER_MOBS_NOX_3 = new HashSet<>(); // Nox Region 3
	static
	{
		TRIGGER_MOBS_NOX_3.add(27747);
		TRIGGER_MOBS_NOX_3.add(27748);
		TRIGGER_MOBS_NOX_3.add(27749);
		TRIGGER_MOBS_NOX_3.add(27750);
		TRIGGER_MOBS_NOX_3.add(27751);
		TRIGGER_MOBS_NOX_3.add(27752);
		TRIGGER_MOBS_NOX_3.add(27753);
		TRIGGER_MOBS_NOX_3.add(27754);
	}
	
	// Trigger Mobs Fire Zones
	private static final Set<Integer> TRIGGER_MOBS_FIERY = new HashSet<>(); // Fiery Field
	static
	{
		TRIGGER_MOBS_FIERY.add(27803);
		TRIGGER_MOBS_FIERY.add(27804);
		TRIGGER_MOBS_FIERY.add(27805);
	}
	private static final Set<Integer> TRIGGER_MOBS_FLAMES = new HashSet<>(); // Garden of Flames
	static
	{
		TRIGGER_MOBS_FLAMES.add(27797);
		TRIGGER_MOBS_FLAMES.add(27798);
		TRIGGER_MOBS_FLAMES.add(27799);
	}
	private static final Set<Integer> TRIGGER_MOBS_VITA = new HashSet<>(); // Vita
	static
	{
		TRIGGER_MOBS_VITA.add(27808);
		TRIGGER_MOBS_VITA.add(27810);
		TRIGGER_MOBS_VITA.add(27811);
		TRIGGER_MOBS_VITA.add(27812);
		TRIGGER_MOBS_VITA.add(27813);
		TRIGGER_MOBS_VITA.add(27814);
		TRIGGER_MOBS_VITA.add(27815);
		TRIGGER_MOBS_VITA.add(27816);
	}
	private static final Set<Integer> TRIGGER_MOBS_IGNIS = new HashSet<>(); // Ignis
	static
	{
		TRIGGER_MOBS_IGNIS.add(27817);
		TRIGGER_MOBS_IGNIS.add(27819);
		TRIGGER_MOBS_IGNIS.add(27820);
		TRIGGER_MOBS_IGNIS.add(27821);
		TRIGGER_MOBS_IGNIS.add(27822);
		TRIGGER_MOBS_IGNIS.add(27823);
		TRIGGER_MOBS_IGNIS.add(27824);
		TRIGGER_MOBS_IGNIS.add(27825);
	}
	
	// Spawns
	private static final SpawnTemplate ASA_SOUL_TREE_MINIONS = SpawnData.getInstance().getSpawnByName("AsaSoulTree");
	private static final SpawnTemplate ANIMA_SOUL_TREE_MINIONS = SpawnData.getInstance().getSpawnByName("AnimaSoulTree");
	private static final SpawnTemplate NOX_SOUL_TREE_MINIONS = SpawnData.getInstance().getSpawnByName("NoxSoulTree");
	private static final SpawnTemplate VITA_SOUL_TREE_MINIONS = SpawnData.getInstance().getSpawnByName("VitaSoulTree");
	private static final SpawnTemplate IGNIS_SOUL_TREE_MINIONS = SpawnData.getInstance().getSpawnByName("IgnisSoulTree");
	
	// Locations
	private static final Location ASA_LOC = new Location(-10584, -200411, -3484, 29820);
	private static final Location ANIMA_LOC = new Location(-28519, -214377, -3223, 63873);
	private static final Location NOX_LOC = new Location(-2319, -213340, -3628, 31924);
	private static final Location VITA_LOC = new Location(10823, -222987, -3734, 5448);
	private static final Location IGNIS_LOC = new Location(27094, -208170, -5294, 14018);
	
	// Misc
	private static final int FLOWER_SPAWN_CHANCE = 3;
	private static final int TREE_SPAWN_CHANCE = 3;
	private static final int AVENGER_SPAWN_CHANCE = 15;
	private static final ConquestZone CONQUEST_ZONE = ZoneManager.getInstance().getZoneByName("conquest", ConquestZone.class);
	private static volatile boolean _asaCanSpawn = true;
	private static volatile boolean _animaCanSpawn = true;
	private static volatile boolean _noxCanSpawn = true;
	private static volatile boolean _vitaCanSpawn = true;
	private static volatile boolean _ignisCanSpawn = true;
	
	private ConquestAreasSpecialMobs()
	{
		addKillId(TRIGGER_DARIL_WATER_DARIL, TRIGGER_DARIL_WATER_ATRON, TRIGGER_DARIL_PHRAN_DARIL, TRIGGER_DARIL_PHRAN_ATRON);
		addKillId(TRIGGER_MOBS_ASA_1);
		addKillId(TRIGGER_MOBS_ASA_2);
		addKillId(TRIGGER_MOBS_ASA_3);
		addKillId(TRIGGER_MOBS_ANIMA_1);
		addKillId(TRIGGER_MOBS_ANIMA_2);
		addKillId(TRIGGER_MOBS_ANIMA_3);
		addKillId(TRIGGER_MOBS_NOX_1);
		addKillId(TRIGGER_MOBS_NOX_2);
		addKillId(TRIGGER_MOBS_NOX_3);
		addKillId(TRIGGER_MOBS_FIERY);
		addKillId(TRIGGER_MOBS_FLAMES);
		addKillId(TRIGGER_MOBS_VITA);
		addKillId(TRIGGER_MOBS_IGNIS);
		addSpawnId(SOUL_FLOWERS);
		addSpawnId(SOUL_TREE_MINIONS);
		addSpawnId(SOUL_TREE_ASA, SOUL_TREE_ANIMA, SOUL_TREE_NOX, SOUL_TREE_VITA, SOUL_TREE_IGNIS);
		addSpawnId(DARIL_WATER_SEO, DARIL_WATER_CRAIGO, DARIL_PHRAN_SEO, DARIL_PHRAN_CRAIGO, CATSHI_AGEL_AVENGER, CATSHI_GOF_AVENGER);
	}
	
	@Override
	public void onKill(Npc npc, Player killer, boolean isSummon)
	{
		switch (npc.getId())
		{
			case TRIGGER_DARIL_WATER_DARIL:
			{
				if (getRandom(100) < AVENGER_SPAWN_CHANCE)
				{
					addSpawn(DARIL_WATER_SEO, npc, true, 300000, true);
				}
				break;
			}
			case TRIGGER_DARIL_WATER_ATRON:
			{
				if (getRandom(100) < AVENGER_SPAWN_CHANCE)
				{
					addSpawn(DARIL_WATER_CRAIGO, npc, true, 300000, true);
				}
				break;
			}
			case TRIGGER_DARIL_PHRAN_DARIL:
			{
				if (getRandom(100) < AVENGER_SPAWN_CHANCE)
				{
					addSpawn(DARIL_PHRAN_SEO, npc, true, 300000, true);
				}
				break;
			}
			case TRIGGER_DARIL_PHRAN_ATRON:
			{
				if (getRandom(100) < AVENGER_SPAWN_CHANCE)
				{
					addSpawn(DARIL_PHRAN_CRAIGO, npc, true, 300000, true);
				}
				break;
			}
			case 27713: // TRIGGER_MOBS_ASA_1
			case 27714: // TRIGGER_MOBS_ASA_1
			case 27715: // TRIGGER_MOBS_ASA_1
			case 27716: // TRIGGER_MOBS_ASA_1
			case 27717: // TRIGGER_MOBS_ASA_1
			case 27718: // TRIGGER_MOBS_ASA_1
			{
				if (getRandom(100) < FLOWER_SPAWN_CHANCE)
				{
					addSpawn(SOUL_FLOWER_ASA_1, npc, true, 300000, true);
				}
				
				spawnAsaSoulTree();
				break;
			}
			case 27719: // TRIGGER_MOBS_ASA_2
			case 27720: // TRIGGER_MOBS_ASA_2
			case 27721: // TRIGGER_MOBS_ASA_2
			case 27722: // TRIGGER_MOBS_ASA_2
			case 27723: // TRIGGER_MOBS_ASA_2
			case 27724: // TRIGGER_MOBS_ASA_2
			case 27725: // TRIGGER_MOBS_ASA_2
			{
				if (getRandom(100) < FLOWER_SPAWN_CHANCE)
				{
					addSpawn(SOUL_FLOWER_ASA_2, npc, true, 300000, true);
				}
				
				spawnAsaSoulTree();
				break;
			}
			case 27726: // TRIGGER_MOBS_ASA_3
			case 27727: // TRIGGER_MOBS_ASA_3
			case 27728: // TRIGGER_MOBS_ASA_3
			case 27729: // TRIGGER_MOBS_ASA_3
			case 27730: // TRIGGER_MOBS_ASA_3
			case 27731: // TRIGGER_MOBS_ASA_3
			case 27732: // TRIGGER_MOBS_ASA_3
			case 27733: // TRIGGER_MOBS_ASA_3
			{
				if (getRandom(100) < FLOWER_SPAWN_CHANCE)
				{
					addSpawn(SOUL_FLOWER_ASA_3, npc, true, 300000, true);
				}
				
				spawnAsaSoulTree();
				break;
			}
			case 27755: // TRIGGER_MOBS_ANIMA_1
			case 27756: // TRIGGER_MOBS_ANIMA_1
			case 27757: // TRIGGER_MOBS_ANIMA_1
			case 27758: // TRIGGER_MOBS_ANIMA_1
			case 27759: // TRIGGER_MOBS_ANIMA_1
			case 27760: // TRIGGER_MOBS_ANIMA_1
			{
				if (getRandom(100) < FLOWER_SPAWN_CHANCE)
				{
					addSpawn(SOUL_FLOWER_ANIMA_1, npc, true, 300000, true);
				}
				
				spawnAnimaSoulTree();
				break;
			}
			case 27761: // TRIGGER_MOBS_ANIMA_2
			case 27762: // TRIGGER_MOBS_ANIMA_2
			case 27763: // TRIGGER_MOBS_ANIMA_2
			case 27764: // TRIGGER_MOBS_ANIMA_2
			case 27765: // TRIGGER_MOBS_ANIMA_2
			case 27766: // TRIGGER_MOBS_ANIMA_2
			case 27767: // TRIGGER_MOBS_ANIMA_2
			{
				if (getRandom(100) < FLOWER_SPAWN_CHANCE)
				{
					addSpawn(SOUL_FLOWER_ANIMA_2, npc, true, 300000, true);
				}
				
				spawnAnimaSoulTree();
				break;
			}
			case 27768: // TRIGGER_MOBS_ANIMA_3
			case 27769: // TRIGGER_MOBS_ANIMA_3
			case 27770: // TRIGGER_MOBS_ANIMA_3
			case 27771: // TRIGGER_MOBS_ANIMA_3
			case 27772: // TRIGGER_MOBS_ANIMA_3
			case 27773: // TRIGGER_MOBS_ANIMA_3
			case 27774: // TRIGGER_MOBS_ANIMA_3
			case 27775: // TRIGGER_MOBS_ANIMA_3
			{
				if (getRandom(100) < FLOWER_SPAWN_CHANCE)
				{
					addSpawn(SOUL_FLOWER_ANIMA_3, npc, true, 300000, true);
				}
				
				spawnAnimaSoulTree();
				break;
			}
			case 27734: // TRIGGER_MOBS_NOX_1
			case 27735: // TRIGGER_MOBS_NOX_1
			case 27736: // TRIGGER_MOBS_NOX_1
			case 27737: // TRIGGER_MOBS_NOX_1
			case 27738: // TRIGGER_MOBS_NOX_1
			case 27739: // TRIGGER_MOBS_NOX_1
			{
				if (getRandom(100) < FLOWER_SPAWN_CHANCE)
				{
					addSpawn(SOUL_FLOWER_NOX_1, npc, true, 300000, true);
				}
				
				spawnNoxSoulTree();
				break;
			}
			case 27740: // TRIGGER_MOBS_NOX_2
			case 27741: // TRIGGER_MOBS_NOX_2
			case 27742: // TRIGGER_MOBS_NOX_2
			case 27743: // TRIGGER_MOBS_NOX_2
			case 27745: // TRIGGER_MOBS_NOX_2
			case 27746: // TRIGGER_MOBS_NOX_2
			{
				if (getRandom(100) < FLOWER_SPAWN_CHANCE)
				{
					addSpawn(SOUL_FLOWER_NOX_2, npc, true, 300000, true);
				}
				
				spawnNoxSoulTree();
				break;
			}
			case 27747: // TRIGGER_MOBS_NOX_3
			case 27748: // TRIGGER_MOBS_NOX_3
			case 27749: // TRIGGER_MOBS_NOX_3
			case 27750: // TRIGGER_MOBS_NOX_3
			case 27751: // TRIGGER_MOBS_NOX_3
			case 27752: // TRIGGER_MOBS_NOX_3
			case 27753: // TRIGGER_MOBS_NOX_3
			case 27754: // TRIGGER_MOBS_NOX_3
			{
				if (getRandom(100) < FLOWER_SPAWN_CHANCE)
				{
					addSpawn(SOUL_FLOWER_NOX_3, npc, true, 300000, true);
				}
				
				spawnNoxSoulTree();
				break;
			}
			case 27803: // TRIGGER_MOBS_FIERY
			case 27804: // TRIGGER_MOBS_FIERY
			case 27805: // TRIGGER_MOBS_FIERY
			{
				if (getRandom(100) < AVENGER_SPAWN_CHANCE)
				{
					addSpawn(CATSHI_AGEL_AVENGER, npc, true, 300000, true);
				}
				break;
			}
			case 27797: // TRIGGER_MOBS_FLAMES
			case 27798: // TRIGGER_MOBS_FLAMES
			case 27799: // TRIGGER_MOBS_FLAMES
			{
				if (getRandom(100) < AVENGER_SPAWN_CHANCE)
				{
					addSpawn(CATSHI_GOF_AVENGER, npc, true, 300000, true);
				}
				break;
			}
			case 27808: // TRIGGER_MOBS_VITA
			case 27810: // TRIGGER_MOBS_VITA
			case 27811: // TRIGGER_MOBS_VITA
			case 27812: // TRIGGER_MOBS_VITA
			case 27813: // TRIGGER_MOBS_VITA
			case 27814: // TRIGGER_MOBS_VITA
			case 27815: // TRIGGER_MOBS_VITA
			case 27816: // TRIGGER_MOBS_VITA
			{
				if (getRandom(100) < FLOWER_SPAWN_CHANCE)
				{
					addSpawn(SOUL_FLOWER_VITA, npc, true, 300000, true);
				}
				
				spawnVitaSoulTree();
				break;
			}
			case 27817: // TRIGGER_MOBS_IGNIS
			case 27819: // TRIGGER_MOBS_IGNIS
			case 27820: // TRIGGER_MOBS_IGNIS
			case 27821: // TRIGGER_MOBS_IGNIS
			case 27822: // TRIGGER_MOBS_IGNIS
			case 27823: // TRIGGER_MOBS_IGNIS
			case 27824: // TRIGGER_MOBS_IGNIS
			case 27825: // TRIGGER_MOBS_IGNIS
			{
				if (getRandom(100) < FLOWER_SPAWN_CHANCE)
				{
					addSpawn(SOUL_FLOWER_IGNIS, npc, true, 300000, true);
				}
				
				spawnIgnisSoulTree();
				break;
			}
		}
	}
	
	private static void spawnAsaSoulTree()
	{
		if (_asaCanSpawn && (getRandom(100) < TREE_SPAWN_CHANCE))
		{
			_asaCanSpawn = false;
			ThreadPool.schedule(() ->
			{
				_asaCanSpawn = true;
				ASA_SOUL_TREE_MINIONS.getGroups().forEach(SpawnGroup::despawnAll);
				if (World.getInstance().getNpc(SOUL_TREE_ASA) != null)
				{
					CONQUEST_ZONE.broadcastPacket(new ExShowScreenMessage(NpcStringId.THE_SOUL_TREE_HAS_DISAPPEARED_FROM_ASA, ExShowScreenMessage.TOP_CENTER, 10000, true));
				}
			}, 1800000);
			addSpawn(SOUL_TREE_ASA, ASA_LOC, false, 1800000, false, 0);
			ASA_SOUL_TREE_MINIONS.getGroups().forEach(SpawnGroup::spawnAll);
			CONQUEST_ZONE.broadcastPacket(new ExShowScreenMessage(NpcStringId.THE_SOUL_TREE_APPEARS_IN_ASA, ExShowScreenMessage.TOP_CENTER, 10000, true));
		}
	}
	
	private static void spawnAnimaSoulTree()
	{
		if (_animaCanSpawn && (getRandom(100) < TREE_SPAWN_CHANCE))
		{
			_animaCanSpawn = false;
			ThreadPool.schedule(() ->
			{
				_animaCanSpawn = true;
				ANIMA_SOUL_TREE_MINIONS.getGroups().forEach(SpawnGroup::despawnAll);
				if (World.getInstance().getNpc(SOUL_TREE_ANIMA) != null)
				{
					CONQUEST_ZONE.broadcastPacket(new ExShowScreenMessage(NpcStringId.THE_SOUL_TREE_HAS_DISAPPEARED_FROM_ANIMA, ExShowScreenMessage.TOP_CENTER, 10000, true));
				}
			}, 1800000);
			addSpawn(SOUL_TREE_ANIMA, ANIMA_LOC, false, 1800000, false, 0);
			ANIMA_SOUL_TREE_MINIONS.getGroups().forEach(SpawnGroup::spawnAll);
			CONQUEST_ZONE.broadcastPacket(new ExShowScreenMessage(NpcStringId.THE_SOUL_TREE_APPEARS_IN_ANIMA, ExShowScreenMessage.TOP_CENTER, 10000, true));
		}
	}
	
	private static void spawnNoxSoulTree()
	{
		if (_noxCanSpawn && (getRandom(100) < TREE_SPAWN_CHANCE))
		{
			_noxCanSpawn = false;
			ThreadPool.schedule(() ->
			{
				_noxCanSpawn = true;
				NOX_SOUL_TREE_MINIONS.getGroups().forEach(SpawnGroup::despawnAll);
				if (World.getInstance().getNpc(SOUL_TREE_NOX) != null)
				{
					CONQUEST_ZONE.broadcastPacket(new ExShowScreenMessage(NpcStringId.THE_SOUL_TREE_HAS_DISAPPEARED_FROM_NOX, ExShowScreenMessage.TOP_CENTER, 10000, true));
				}
			}, 1800000);
			addSpawn(SOUL_TREE_NOX, NOX_LOC, false, 1800000, false, 0);
			NOX_SOUL_TREE_MINIONS.getGroups().forEach(SpawnGroup::spawnAll);
			CONQUEST_ZONE.broadcastPacket(new ExShowScreenMessage(NpcStringId.THE_SOUL_TREE_APPEARS_IN_NOX, ExShowScreenMessage.TOP_CENTER, 10000, true));
		}
	}
	
	private static void spawnVitaSoulTree()
	{
		if (_vitaCanSpawn && (getRandom(100) < TREE_SPAWN_CHANCE))
		{
			_vitaCanSpawn = false;
			ThreadPool.schedule(() ->
			{
				_vitaCanSpawn = true;
				VITA_SOUL_TREE_MINIONS.getGroups().forEach(SpawnGroup::despawnAll);
				if (World.getInstance().getNpc(SOUL_TREE_VITA) != null)
				{
					CONQUEST_ZONE.broadcastPacket(new ExShowScreenMessage(NpcStringId.THE_SOUL_TREE_HAS_DISAPPEARED_FROM_VITA, ExShowScreenMessage.TOP_CENTER, 10000, true));
				}
			}, 1800000);
			addSpawn(SOUL_TREE_VITA, VITA_LOC, false, 1800000, false, 0);
			VITA_SOUL_TREE_MINIONS.getGroups().forEach(SpawnGroup::spawnAll);
			CONQUEST_ZONE.broadcastPacket(new ExShowScreenMessage(NpcStringId.THE_SOUL_TREE_APPEARS_IN_VITA, ExShowScreenMessage.TOP_CENTER, 10000, true));
		}
	}
	
	private static void spawnIgnisSoulTree()
	{
		if (_ignisCanSpawn && (getRandom(100) < TREE_SPAWN_CHANCE))
		{
			_ignisCanSpawn = false;
			ThreadPool.schedule(() ->
			{
				_ignisCanSpawn = true;
				IGNIS_SOUL_TREE_MINIONS.getGroups().forEach(SpawnGroup::despawnAll);
				if (World.getInstance().getNpc(SOUL_TREE_IGNIS) != null)
				{
					CONQUEST_ZONE.broadcastPacket(new ExShowScreenMessage(NpcStringId.THE_SOUL_TREE_HAS_DISAPPEARED_FROM_IGNIS, ExShowScreenMessage.TOP_CENTER, 10000, true));
				}
			}, 1800000);
			addSpawn(SOUL_TREE_IGNIS, IGNIS_LOC, false, 1800000, false, 0);
			IGNIS_SOUL_TREE_MINIONS.getGroups().forEach(SpawnGroup::spawnAll);
			CONQUEST_ZONE.broadcastPacket(new ExShowScreenMessage(NpcStringId.THE_SOUL_TREE_APPEARS_IN_IGNIS, ExShowScreenMessage.TOP_CENTER, 10000, true));
		}
	}
	
	@Override
	public void onSpawn(Npc npc)
	{
		npc.setRandomWalking(false);
		switch (npc.getId())
		{
			case DARIL_WATER_SEO:
			case DARIL_PHRAN_SEO:
			{
				npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.MEOW_WHO_IS_BULLYING_MY_FRIENDS);
				break;
			}
			case DARIL_WATER_CRAIGO:
			case DARIL_PHRAN_CRAIGO:
			{
				npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.HEY_WHO_DARES_TO_INSULT_ATRON);
				break;
			}
			case CATSHI_AGEL_AVENGER:
			case CATSHI_GOF_AVENGER:
			{
				npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.DON_T_TORMENT_OUR_CATSHI);
				break;
			}
		}
		
		if (SOUL_FLOWERS.contains(npc.getId()))
		{
			npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.SONG_OF_MOUNTAIN_SOULS);
		}
	}
	
	public static void main(String[] args)
	{
		new ConquestAreasSpecialMobs();
	}
}
