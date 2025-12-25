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
package org.l2jmobius.gameserver.model.olympiad;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.l2jmobius.commons.database.DatabaseFactory;
import org.l2jmobius.gameserver.data.sql.CharInfoTable;
import org.l2jmobius.gameserver.model.actor.Player;

/**
 * @author dontknowdontcare
 */
public class OlympiadFightHistory
{
	private static final Logger LOGGER = Logger.getLogger(OlympiadFightHistory.class.getName());
	
	private static final int MAX_FIGHT_HISTORY_COUNT = 3;
	private final Player _player;
	
	private List<OlympiadFight> _fights = new ArrayList<>();
	
	public OlympiadFightHistory(Player player)
	{
		_player = player;
		if ((MAX_FIGHT_HISTORY_COUNT > 0) && (_player != null))
		{
			loadRecentFights(_player.getObjectId());
		}
	}
	
	public List<OlympiadFight> getFights()
	{
		return _fights;
	}
	
	public int getWinnerFormat(Participant parOne, Participant parTwo, int winner)
	{
		if (parOne == null)
		{
			LOGGER.warning("OlympiadFightHistory - getWinnerFormat error parOne is null, owner = " + _player.getName());
			return 2;
		}
		
		if (_player.getObjectId() == parOne.getObjectId())
		{
			if (winner == 1)
			{
				return 1;
			}
			else if (winner == 2)
			{
				return 0;
			}
			else
			{
				return 2;
			}
		}
		
		if (winner == 2)
		{
			return 1;
		}
		else if (winner == 1)
		{
			return 0;
		}
		else
		{
			return 2;
		}
	}
	
	public void addOlympiadFight(Participant opponent, int winner)
	{
		if (opponent != null)
		{
			final OlympiadFight fight = new OlympiadFight(opponent.getName(), opponent.getLevel(), opponent.getBaseClass(), winner);
			final List<OlympiadFight> newFights = new ArrayList<>();
			newFights.add(fight);
			if ((MAX_FIGHT_HISTORY_COUNT > 1) && !_fights.isEmpty())
			{
				for (int i = 0; i < (MAX_FIGHT_HISTORY_COUNT - 1); i++)
				{
					if (_fights.size() < (i + 1))
					{
						break;
					}
					
					OlympiadFight curFight = _fights.get(i);
					newFights.add(curFight);
				}
			}
			
			_fights = newFights;
		}
	}
	
	private void loadRecentFights(int charId)
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT * FROM olympiad_fights WHERE (charOneId=? OR charTwoId=?) ORDER BY start DESC LIMIT " + MAX_FIGHT_HISTORY_COUNT))
		{
			ps.setInt(1, charId);
			ps.setInt(2, charId);
			try (ResultSet rset = ps.executeQuery())
			{
				int charOneId;
				int charOneClass;
				int charTwoId;
				int charTwoClass;
				int winner;
				int opponentCharId;
				int opponentLevel;
				int opponentClassId;
				int winInfo;
				while (rset.next())
				{
					charOneId = rset.getInt("charOneId");
					charOneClass = rset.getInt("charOneClass");
					charTwoId = rset.getInt("charTwoId");
					charTwoClass = rset.getInt("charTwoClass");
					winner = rset.getInt("winner");
					opponentLevel = charOneId == charId ? rset.getInt("charTwoLevel") : rset.getInt("charOneLevel");
					opponentCharId = charOneId == charId ? charTwoId : charOneId;
					opponentClassId = charOneId == charId ? charTwoClass : charOneClass;
					
					final String name = CharInfoTable.getInstance().getNameById(opponentCharId);
					if ((name != null))
					{
						// winInfo: 0 = win, 1 = loss, 2 = draw
						if (charOneId == charId)
						{
							if (winner == 1)
							{
								winInfo = 1;
							}
							else if (winner == 2)
							{
								winInfo = 0;
							}
							else
							{
								winInfo = 2;
							}
						}
						else
						{
							if (winner == 2)
							{
								winInfo = 1;
							}
							else if (winner == 1)
							{
								winInfo = 0;
							}
							else
							{
								winInfo = 2;
							}
						}
						
						_fights.add(new OlympiadFight(name, opponentLevel, opponentClassId, winInfo));
					}
					else
					{
						LOGGER.severe("OlympiadFightHistory could not load opponent name for char id = " + opponentCharId);
					}
				}
			}
		}
		catch (SQLException e)
		{
			LOGGER.warning("OlympiadFightHistory: could not load fights history for CharId: " + charId + ": " + e);
		}
	}
}
