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
package org.l2jmobius.gameserver.network.clientpackets.dethrone;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.l2jmobius.commons.database.DatabaseFactory;
import org.l2jmobius.commons.util.StringUtil;
import org.l2jmobius.gameserver.config.PlayerConfig;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.network.PacketLogger;
import org.l2jmobius.gameserver.network.clientpackets.ClientPacket;
import org.l2jmobius.gameserver.network.serverpackets.dethrone.ExDethroneCheckName;

/**
 * @author Negrito8
 */
public class RequestExDethroneCheckName extends ClientPacket
{
	private static final int CHANGE_NAME = 1; // A fee charged with your name. (ok message ?)
	private static final int ALREADY_EXISTS = -1; // The name already exists
	private static final int INVALID_NAME = -2; // Incorrect name. please try again
	private static final int MAXIMUM_NAME_LENGTH = -3; // Enter character title (Up to 16 characters)
	
	private String _conquestName;
	
	@Override
	protected void readImpl()
	{
		_conquestName = readString();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		int result;
		
		// For some reason, name is starting with a char non-alphanumeric. (SizedString?)
		if (!StringUtil.isAlphaNumeric(_conquestName.substring(1)) || checkRestrictedNames(_conquestName.substring(1)))
		{
			result = INVALID_NAME;
		}
		
		else if (getIdByName(_conquestName) > 0)
		{
			result = ALREADY_EXISTS;
		}
		else if (_conquestName.length() > 16)
		{
			result = MAXIMUM_NAME_LENGTH;
		}
		else
		{
			result = CHANGE_NAME;
		}
		
		player.sendPacket(new ExDethroneCheckName(result));
	}
	
	private int getIdByName(String name)
	{
		int id = -1;
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement pst = con.prepareStatement("SELECT charId FROM character_variables WHERE var = ? and val = ?"))
		{
			pst.setString(1, PlayerVariables.CONQUEST_NAME);
			pst.setString(2, name);
			try (ResultSet resultSet = pst.executeQuery())
			{
				while (resultSet.next())
				{
					id = resultSet.getInt("charId");
				}
			}
		}
		catch (Exception e)
		{
			PacketLogger.warning(getClass().getSimpleName() + ": Could not check existing name " + e.getMessage());
		}
		
		if (id > 0)
		{
			return id;
		}
		
		// Value not found.
		return -1;
	}
	
	private boolean checkRestrictedNames(String name)
	{
		if (PlayerConfig.FORBIDDEN_NAMES.length > 0)
		{
			for (String forbidden : PlayerConfig.FORBIDDEN_NAMES)
			{
				if (name.toLowerCase().contains(forbidden.toLowerCase()))
				{
					return true;
				}
			}
		}
		
		return false;
	}
}
