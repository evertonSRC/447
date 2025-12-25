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
package org.l2jmobius.gameserver.config;

import org.l2jmobius.commons.util.ConfigReader;

/**
 * This class loads all the grand boss related configurations.
 * @author Mobius
 */
public class GrandBossConfig
{
	// File
	private static final String GRANDBOSS_CONFIG_FILE = "./config/GrandBoss.ini";
	
	// Constants
	public static int VALAKAS_WAIT_TIME;
	public static int VALAKAS_SPAWN_INTERVAL;
	public static int VALAKAS_SPAWN_RANDOM;
	public static boolean VALAKAS_RECOGNIZE_HERO;
	public static int BAIUM_SPAWN_INTERVAL;
	public static int BAIUM_SPAWN_RANDOM;
	public static boolean BAIUM_RECOGNIZE_HERO;
	public static int CORE_SPAWN_INTERVAL;
	public static int CORE_SPAWN_RANDOM;
	public static int ORFEN_SPAWN_INTERVAL;
	public static int ORFEN_SPAWN_RANDOM;
	public static int QUEEN_ANT_SPAWN_INTERVAL;
	public static int QUEEN_ANT_SPAWN_RANDOM;
	public static int BELETH_WAIT_TIME;
	public static int BELETH_SPAWN_INTERVAL;
	public static int BELETH_SPAWN_RANDOM;
	public static int BELETH_MIN_PLAYERS;
	public static int BELETH_MAX_PLAYERS;
	public static int KELBIM_WAIT_TIME;
	public static int KELBIM_SPAWN_INTERVAL;
	public static int KELBIM_SPAWN_RANDOM;
	public static int KELBIM_MIN_PLAYERS;
	public static int KELBIM_MAX_PLAYERS;
	public static int ANAKIM_MIN_PLAYERS;
	public static int ANAKIM_MAX_PLAYERS;
	public static int ANAKIM_MIN_PLAYER_LEVEL;
	public static int LILITH_MIN_PLAYERS;
	public static int LILITH_MAX_PLAYERS;
	public static int LILITH_MIN_PLAYER_LEVEL;
	public static int TRASKEN_SPAWN_INTERVAL;
	public static int TRASKEN_SPAWN_RANDOM;
	public static int TRASKEN_MIN_PLAYERS;
	public static int TRASKEN_MAX_PLAYERS;
	public static int TRASKEN_MIN_PLAYER_LEVEL;
	public static int LINDVIOR_SPAWN_INTERVAL;
	public static int LINDVIOR_SPAWN_RANDOM;
	public static int LINDVIOR_MIN_PLAYERS;
	public static int LINDVIOR_MAX_PLAYERS;
	public static int LINDVIOR_MIN_PLAYER_LEVEL;
	public static int HELIOS_WAIT_TIME;
	public static int HELIOS_SPAWN_INTERVAL;
	public static int HELIOS_SPAWN_RANDOM;
	public static int HELIOS_MIN_PLAYER;
	public static int HELIOS_MIN_PLAYER_LEVEL;
	public static int RAMONA_SPAWN_INTERVAL;
	public static int RAMONA_SPAWN_RANDOM;
	public static int RAMONA_MIN_PLAYER;
	public static int ETINA_SPAWN_INTERVAL;
	public static int ETINA_SPAWN_RANDOM;
	public static int ETINA_MIN_PLAYERS;
	public static int ETINA_MAX_PLAYERS;
	public static int ETINA_MIN_PLAYER_LEVEL;
	public static int FAFURION_WAIT_TIME;
	public static int FAFURION_SPAWN_INTERVAL;
	public static int FAFURION_SPAWN_RANDOM;
	public static int FAFURION_MIN_PLAYERS;
	public static int FAFURION_MAX_PLAYERS;
	public static int FAFURION_MIN_PLAYER_LEVEL;
	
	public static void load()
	{
		final ConfigReader config = new ConfigReader(GRANDBOSS_CONFIG_FILE);
		VALAKAS_WAIT_TIME = config.getInt("ValakasWaitTime", 30);
		VALAKAS_SPAWN_INTERVAL = config.getInt("IntervalOfValakasSpawn", 264);
		VALAKAS_SPAWN_RANDOM = config.getInt("RandomOfValakasSpawn", 72);
		VALAKAS_RECOGNIZE_HERO = config.getBoolean("ValakasRecognizeHero", true);
		BAIUM_SPAWN_INTERVAL = config.getInt("IntervalOfBaiumSpawn", 168);
		BAIUM_SPAWN_RANDOM = config.getInt("RandomOfBaiumSpawn", 48);
		BAIUM_RECOGNIZE_HERO = config.getBoolean("BaiumRecognizeHero", true);
		CORE_SPAWN_INTERVAL = config.getInt("IntervalOfCoreSpawn", 60);
		CORE_SPAWN_RANDOM = config.getInt("RandomOfCoreSpawn", 24);
		ORFEN_SPAWN_INTERVAL = config.getInt("IntervalOfOrfenSpawn", 48);
		ORFEN_SPAWN_RANDOM = config.getInt("RandomOfOrfenSpawn", 20);
		QUEEN_ANT_SPAWN_INTERVAL = config.getInt("IntervalOfQueenAntSpawn", 36);
		QUEEN_ANT_SPAWN_RANDOM = config.getInt("RandomOfQueenAntSpawn", 17);
		BELETH_WAIT_TIME = config.getInt("BelethWaitTime", 5);
		BELETH_SPAWN_INTERVAL = config.getInt("IntervalOfBelethSpawn", 192);
		BELETH_SPAWN_RANDOM = config.getInt("RandomOfBelethSpawn", 148);
		BELETH_MIN_PLAYERS = config.getInt("BelethMinPlayers", 49);
		BELETH_MAX_PLAYERS = config.getInt("BelethMaxPlayers", 350);
		KELBIM_WAIT_TIME = config.getInt("KelbimWaitTime", 5);
		KELBIM_SPAWN_INTERVAL = config.getInt("IntervalOfKelbimSpawn", 192);
		KELBIM_SPAWN_RANDOM = config.getInt("RandomOfKelbimSpawn", 148);
		KELBIM_MIN_PLAYERS = config.getInt("KelbimMinPlayers", 49);
		KELBIM_MAX_PLAYERS = config.getInt("KelbimMaxPlayers", 350);
		ANAKIM_MIN_PLAYERS = config.getInt("AnakimMinPlayers", 98);
		ANAKIM_MAX_PLAYERS = config.getInt("AnakimMaxPlayers", 120);
		ANAKIM_MIN_PLAYER_LEVEL = config.getInt("AnakimMinPlayerLvl", 105);
		LILITH_MIN_PLAYERS = config.getInt("LilithMinPlayers", 98);
		LILITH_MAX_PLAYERS = config.getInt("LilithMaxPlayers", 120);
		LILITH_MIN_PLAYER_LEVEL = config.getInt("LilithMinPlayerLvl", 105);
		TRASKEN_SPAWN_INTERVAL = config.getInt("IntervalOfTraskenSpawn", 264);
		TRASKEN_SPAWN_RANDOM = config.getInt("RandomOfTraskenSpawn", 72);
		TRASKEN_MIN_PLAYERS = config.getInt("TraskenMinPlayers", 49);
		TRASKEN_MAX_PLAYERS = config.getInt("TraskenMaxPlayers", 112);
		TRASKEN_MIN_PLAYER_LEVEL = config.getInt("TraskenMinPlayerLvl", 85);
		LINDVIOR_SPAWN_INTERVAL = config.getInt("IntervalOfLindviorSpawn", 264);
		LINDVIOR_SPAWN_RANDOM = config.getInt("RandomOfLindviorSpawn", 72);
		LINDVIOR_MIN_PLAYERS = config.getInt("LindviorMinPlayers", 49);
		LINDVIOR_MAX_PLAYERS = config.getInt("LindviorMaxPlayers", 112);
		LINDVIOR_MIN_PLAYER_LEVEL = config.getInt("LindviorMinPlayerLvl", 99);
		HELIOS_WAIT_TIME = config.getInt("HeliosWaitTime", 10);
		HELIOS_SPAWN_INTERVAL = config.getInt("IntervalOfHeliosSpawn", 264);
		HELIOS_SPAWN_RANDOM = config.getInt("RandomOfHeliosSpawn", 72);
		HELIOS_MIN_PLAYER = config.getInt("HeliosMinPlayers", 70);
		HELIOS_MIN_PLAYER_LEVEL = config.getInt("HeliosMinPlayerLvl", 102);
		RAMONA_SPAWN_INTERVAL = config.getInt("IntervalOfRamonaSpawn", 72);
		RAMONA_SPAWN_RANDOM = config.getInt("RandomOfRamonaSpawn", 48);
		RAMONA_MIN_PLAYER = config.getInt("RamonaMinPlayers", 7);
		ETINA_SPAWN_INTERVAL = config.getInt("IntervalOfEtinaSpawn", 264);
		ETINA_SPAWN_RANDOM = config.getInt("RandomOfEtinaSpawn", 72);
		ETINA_MIN_PLAYERS = config.getInt("EtinaMinPlayers", 49);
		ETINA_MAX_PLAYERS = config.getInt("EtinaMaxPlayers", 112);
		ETINA_MIN_PLAYER_LEVEL = config.getInt("EtinaMinPlayerLvl", 103);
		FAFURION_WAIT_TIME = config.getInt("FafurionWaitTime", 10);
		FAFURION_SPAWN_INTERVAL = config.getInt("IntervalOfFafurionSpawn", 264);
		FAFURION_SPAWN_RANDOM = config.getInt("RandomOfFafurionSpawn", 72);
		FAFURION_MIN_PLAYERS = config.getInt("FafurionMinPlayers", 49);
		FAFURION_MAX_PLAYERS = config.getInt("FafurionMaxPlayers", 200);
		FAFURION_MIN_PLAYER_LEVEL = config.getInt("FafurionMinPlayerLvl", 105);
	}
}
