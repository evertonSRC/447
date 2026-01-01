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
package org.l2jmobius.gameserver.data.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.l2jmobius.commons.database.DatabaseFactory;

/**
 * @author Mobius
 */
public class VirtualPointsDAO
{
	private static final Logger LOGGER = Logger.getLogger(VirtualPointsDAO.class.getName());
	
	private static final String SELECT_QUERY = "SELECT points FROM character_virtual_points WHERE charId = ?";
	private static final String UPSERT_QUERY = "REPLACE INTO character_virtual_points (charId, points) VALUES (?, ?)";
	
	public int getPoints(int objectId)
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement(SELECT_QUERY))
		{
			statement.setInt(1, objectId);
			try (ResultSet rset = statement.executeQuery())
			{
				if (rset.next())
				{
					return rset.getInt("points");
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Could not load virtual points for: " + objectId, e);
		}
		
		return 0;
	}
	
	public void setPoints(int objectId, int points)
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement(UPSERT_QUERY))
		{
			statement.setInt(1, objectId);
			statement.setInt(2, points);
			statement.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Could not save virtual points for: " + objectId, e);
		}
	}
	
	public int addPoints(int objectId, int points)
	{
		int newPoints = points;
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement selectStatement = con.prepareStatement(SELECT_QUERY);
			PreparedStatement upsertStatement = con.prepareStatement(UPSERT_QUERY))
		{
			selectStatement.setInt(1, objectId);
			try (ResultSet rset = selectStatement.executeQuery())
			{
				if (rset.next())
				{
					newPoints = Math.max(0, rset.getInt("points") + points);
				}
				else
				{
					newPoints = Math.max(0, points);
				}
			}
			
			upsertStatement.setInt(1, objectId);
			upsertStatement.setInt(2, newPoints);
			upsertStatement.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Could not add virtual points for: " + objectId, e);
		}
		
		return newPoints;
	}
	
	public static VirtualPointsDAO getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final VirtualPointsDAO INSTANCE = new VirtualPointsDAO();
	}
}
