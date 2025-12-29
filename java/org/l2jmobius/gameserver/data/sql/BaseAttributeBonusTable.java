/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2jmobius.gameserver.data.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.l2jmobius.commons.database.DatabaseFactory;
import org.l2jmobius.gameserver.model.actor.holders.player.BaseAttributeBonusHolder;

public class BaseAttributeBonusTable
{
	private static final Logger LOGGER = Logger.getLogger(BaseAttributeBonusTable.class.getName());
	
	private static final String SELECT_BONUSES = "SELECT isDual, strBonus, conBonus, dexBonus, intBonus, witBonus, menBonus FROM character_base_attribute_bonus WHERE charId=?";
	private static final String REPLACE_BONUSES = "REPLACE INTO character_base_attribute_bonus (charId, isDual, strBonus, conBonus, dexBonus, intBonus, witBonus, menBonus) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
	
	public BaseAttributeBonusHolder[] loadBonuses(int charId)
	{
		final BaseAttributeBonusHolder main = new BaseAttributeBonusHolder();
		final BaseAttributeBonusHolder dual = new BaseAttributeBonusHolder();
		
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement(SELECT_BONUSES))
		{
			ps.setInt(1, charId);
			try (ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
				{
					final BaseAttributeBonusHolder holder = rs.getBoolean("isDual") ? dual : main;
					holder.setAll(rs.getInt("strBonus"), rs.getInt("conBonus"), rs.getInt("dexBonus"), rs.getInt("intBonus"), rs.getInt("witBonus"), rs.getInt("menBonus"));
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Could not restore base attribute bonuses for charId " + charId + ": " + e.getMessage(), e);
		}
		
		return new BaseAttributeBonusHolder[]
		{
			main,
			dual
		};
	}
	
	public void saveBonuses(int charId, boolean isDual, BaseAttributeBonusHolder bonus)
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement(REPLACE_BONUSES))
		{
			ps.setInt(1, charId);
			ps.setBoolean(2, isDual);
			ps.setInt(3, bonus.getStrBonus());
			ps.setInt(4, bonus.getConBonus());
			ps.setInt(5, bonus.getDexBonus());
			ps.setInt(6, bonus.getIntBonus());
			ps.setInt(7, bonus.getWitBonus());
			ps.setInt(8, bonus.getMenBonus());
			ps.execute();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Could not store base attribute bonuses for charId " + charId + ": " + e.getMessage(), e);
		}
	}
	
	public static BaseAttributeBonusTable getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final BaseAttributeBonusTable INSTANCE = new BaseAttributeBonusTable();
	}
}
