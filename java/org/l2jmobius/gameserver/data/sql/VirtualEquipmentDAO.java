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
import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.l2jmobius.commons.database.DatabaseFactory;
import org.l2jmobius.gameserver.model.actor.enums.player.VirtualSlot;
import org.l2jmobius.gameserver.model.actor.holders.player.VirtualEquippedItem;

/**
 * @author Mobius
 */
public class VirtualEquipmentDAO
{
	private static final Logger LOGGER = Logger.getLogger(VirtualEquipmentDAO.class.getName());
	
	private static final String SELECT_QUERY = "SELECT slot, itemId, enchant, indexMain, indexSub FROM character_virtual_equipment WHERE charId = ?";
	private static final String DELETE_QUERY = "DELETE FROM character_virtual_equipment WHERE charId = ? AND slot = ?";
	private static final String DELETE_ALL_QUERY = "DELETE FROM character_virtual_equipment WHERE charId = ?";
	private static final String UPSERT_QUERY = "REPLACE INTO character_virtual_equipment (charId, slot, itemId, enchant, indexMain, indexSub) VALUES (?, ?, ?, ?, ?, ?)";
	
	public Map<VirtualSlot, VirtualEquippedItem> load(int objectId)
	{
		final Map<VirtualSlot, VirtualEquippedItem> equipment = new EnumMap<>(VirtualSlot.class);
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement(SELECT_QUERY))
		{
			statement.setInt(1, objectId);
			try (ResultSet rset = statement.executeQuery())
			{
				while (rset.next())
				{
					final int slotId = rset.getInt("slot");
					final VirtualSlot slot = VirtualSlot.fromId(slotId);
					if (slot == null)
					{
						LOGGER.warning(getClass().getSimpleName() + ": Unknown virtual slot id " + slotId + " for: " + objectId);
						continue;
					}
					
					equipment.put(slot, new VirtualEquippedItem(rset.getInt("itemId"), rset.getInt("enchant"), rset.getInt("indexMain"), rset.getInt("indexSub")));
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Could not load virtual equipment for: " + objectId, e);
		}
		
		return equipment;
	}
	
	public void saveSlot(int objectId, VirtualSlot slot, VirtualEquippedItem item)
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement(UPSERT_QUERY))
		{
			statement.setInt(1, objectId);
			statement.setInt(2, slot.getId());
			statement.setInt(3, item.getItemId());
			statement.setInt(4, item.getEnchant());
			statement.setInt(5, item.getIndexMain());
			statement.setInt(6, item.getIndexSub());
			statement.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Could not save virtual equipment for: " + objectId, e);
		}
	}
	
	public void deleteSlot(int objectId, VirtualSlot slot)
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement(DELETE_QUERY))
		{
			statement.setInt(1, objectId);
			statement.setInt(2, slot.getId());
			statement.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Could not delete virtual equipment for: " + objectId, e);
		}
	}
	
	public void saveAll(int objectId, Map<VirtualSlot, VirtualEquippedItem> equipment)
	{
		try (Connection con = DatabaseFactory.getConnection())
		{
			try (PreparedStatement deleteStatement = con.prepareStatement(DELETE_ALL_QUERY))
			{
				deleteStatement.setInt(1, objectId);
				deleteStatement.executeUpdate();
			}
			
			if (equipment.isEmpty())
			{
				return;
			}
			
			try (PreparedStatement statement = con.prepareStatement(UPSERT_QUERY))
			{
				for (Map.Entry<VirtualSlot, VirtualEquippedItem> entry : equipment.entrySet())
				{
					final VirtualSlot slot = entry.getKey();
					final VirtualEquippedItem item = entry.getValue();
					statement.setInt(1, objectId);
					statement.setInt(2, slot.getId());
					statement.setInt(3, item.getItemId());
					statement.setInt(4, item.getEnchant());
					statement.setInt(5, item.getIndexMain());
					statement.setInt(6, item.getIndexSub());
					statement.addBatch();
				}
				statement.executeBatch();
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Could not save virtual equipment for: " + objectId, e);
		}
	}
	
	public static VirtualEquipmentDAO getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final VirtualEquipmentDAO INSTANCE = new VirtualEquipmentDAO();
	}
}
