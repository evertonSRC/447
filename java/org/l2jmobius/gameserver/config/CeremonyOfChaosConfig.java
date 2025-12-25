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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.l2jmobius.commons.util.ConfigReader;
import org.l2jmobius.gameserver.model.item.holders.ItemHolder;

/**
 * This class loads all the Ceremony of Chaos related configurations.
 * @author Mobius
 */
public class CeremonyOfChaosConfig
{
	private static final Logger LOGGER = Logger.getLogger(CeremonyOfChaosConfig.class.getName());
	
	// File
	private static final String CEREMONY_OF_CHAOS_CONFIG_FILE = "./config/CeremonyOfChaos.ini";
	
	// Constants
	public static List<Integer> COC_COMPETITION_DAYS;
	public static int COC_START_TIME;
	public static int COC_MIN_PLAYERS;
	public static int COC_MAX_PLAYERS;
	public static int COC_MAX_ARENAS;
	public static List<ItemHolder> COC_WINNER_REWARDS;
	public static List<ItemHolder> COC_BEST_KILLER_REWARDS;
	public static List<ItemHolder> COC_MONTHLY_WINNER_REWARDS;
	public static int COC_MONTHLY_WINNER_FAME;
	public static int COC_MONTHLY_WINNER_CLAN_REPUTATION;
	
	public static void load()
	{
		final ConfigReader config = new ConfigReader(CEREMONY_OF_CHAOS_CONFIG_FILE);
		COC_COMPETITION_DAYS = new ArrayList<>();
		for (String s : config.getString("CoCCompetitionDays", "3,4,5").split(","))
		{
			COC_COMPETITION_DAYS.add(Integer.parseInt(s));
		}
		COC_START_TIME = config.getInt("CoCStartTime", 18);
		COC_MIN_PLAYERS = config.getInt("CoCMinPlayers", 2);
		COC_MAX_PLAYERS = config.getInt("CoCMaxPlayers", 18);
		COC_MAX_ARENAS = config.getInt("CoCMaxArenas", 5);
		COC_WINNER_REWARDS = parseItemsList(config.getString("CoCWinnerRewards", "45584,5;36333,1"));
		COC_BEST_KILLER_REWARDS = parseItemsList(config.getString("CoCBestKillerRewards", "35982,1"));
		COC_MONTHLY_WINNER_REWARDS = parseItemsList(config.getString("CoCMonthlyWinnerRewards", "35565,1;35564,1"));
		COC_MONTHLY_WINNER_FAME = config.getInt("CoCMonthlyWinnerFame", 5000);
		COC_MONTHLY_WINNER_CLAN_REPUTATION = config.getInt("CoCMonthlyWinnerClanReputation", 150000);
	}
	
	/**
	 * Parse a config value from its string representation to a two-dimensional int array.<br>
	 * The format of the value to be parsed should be as follows: "item1Id,item1Amount;item2Id,item2Amount;...itemNId,itemNAmount".
	 * @param line the value of the parameter to parse
	 * @return the parsed list or {@code null} if nothing was parsed
	 */
	private static List<ItemHolder> parseItemsList(String line)
	{
		if (line.isEmpty())
		{
			return Collections.emptyList();
		}
		
		final String[] propertySplit = line.split(";");
		if (line.equalsIgnoreCase("none") || (propertySplit.length == 0))
		{
			return Collections.emptyList();
		}
		
		String[] valueSplit;
		final List<ItemHolder> result = new ArrayList<>(propertySplit.length);
		for (String value : propertySplit)
		{
			valueSplit = value.split(",");
			if (valueSplit.length != 2)
			{
				LOGGER.warning("parseItemsList[CeremonyOfChaosConfig.load()]: invalid entry -> " + valueSplit[0] + ", should be itemId,itemNumber. Skipping to the next entry in the list.");
				continue;
			}
			
			int itemId = -1;
			try
			{
				itemId = Integer.parseInt(valueSplit[0]);
			}
			catch (NumberFormatException e)
			{
				LOGGER.warning("parseItemsList[CeremonyOfChaosConfig.load()]: invalid itemId -> " + valueSplit[0] + ", value must be an integer. Skipping to the next entry in the list.");
				continue;
			}
			
			int count = -1;
			try
			{
				count = Integer.parseInt(valueSplit[1]);
			}
			catch (NumberFormatException e)
			{
				LOGGER.warning("parseItemsList[CeremonyOfChaosConfig.load()]: invalid item number -> " + valueSplit[1] + ", value must be an integer. Skipping to the next entry in the list.");
				continue;
			}
			
			if ((itemId > 0) && (count > 0))
			{
				result.add(new ItemHolder(itemId, count));
			}
		}
		
		return result;
	}
}
