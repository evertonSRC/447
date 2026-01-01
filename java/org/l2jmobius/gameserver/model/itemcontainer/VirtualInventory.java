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
package org.l2jmobius.gameserver.model.itemcontainer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.l2jmobius.commons.database.DatabaseFactory;
import org.l2jmobius.gameserver.data.holders.VirtualItemHolder;
import org.l2jmobius.gameserver.data.xml.SkillData;
import org.l2jmobius.gameserver.data.xml.VirtualItemData;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.item.enums.BodyPart;
import org.l2jmobius.gameserver.model.item.enums.ItemSkillType;
import org.l2jmobius.gameserver.model.item.holders.ItemSkillHolder;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.model.skill.Skill;

/**
 * Stores and applies virtual equipped items for Illusory Equipment.
 * @author Mobius
 */
public class VirtualInventory
{
	private static final Logger LOGGER = Logger.getLogger(VirtualInventory.class.getName());
	private static final String RESTORE_QUERY = "SELECT slot, index_main, index_sub FROM character_virtual_equipment WHERE charId=?";
	private static final String STORE_QUERY = "REPLACE INTO character_virtual_equipment (charId, slot, index_main, index_sub) VALUES (?,?,?,?)";
	private static final String DELETE_QUERY = "DELETE FROM character_virtual_equipment WHERE charId=? AND slot=?";
	
	private final Player _owner;
	private final Map<Long, VirtualItemHolder> _equipped = new HashMap<>();
	private final Map<Long, Item> _equippedItems = new HashMap<>();
	private final Set<Integer> _skillIds = new HashSet<>();
	
	public VirtualInventory(Player owner)
	{
		_owner = owner;
	}
	
	public Player getOwner()
	{
		return _owner;
	}
	
	public Collection<Item> getEquippedItems()
	{
		return Collections.unmodifiableCollection(_equippedItems.values());
	}
	
	public Collection<VirtualItemHolder> getEquippedHolders()
	{
		if (_equipped.isEmpty())
		{
			return Collections.emptyList();
		}
		
		final List<VirtualItemHolder> holders = new ArrayList<>(_equipped.size());
		for (Map.Entry<Long, VirtualItemHolder> entry : _equipped.entrySet())
		{
			final VirtualItemHolder holder = entry.getValue();
			holders.add(new VirtualItemHolder(holder.getIndexMain(), entry.getKey(), holder.getIndexSub(), holder.getItemId(), holder.getEnchant(), holder.getCostVISPoint()));
		}
		
		return holders;
	}
	
	public VirtualItemHolder getEquippedHolder(long slot)
	{
		return _equipped.get(slot);
	}
	
	public Item getItemByBodyPart(BodyPart bodyPart)
	{
		for (Item item : _equippedItems.values())
		{
			final BodyPart itemBodyPart = item.getTemplate().getBodyPart();
			if (itemBodyPart == bodyPart)
			{
				return item;
			}
			
			if ((bodyPart == BodyPart.CHEST) && ((itemBodyPart == BodyPart.FULL_ARMOR) || (itemBodyPart == BodyPart.ALLDRESS)))
			{
				return item;
			}
		}
		return null;
	}
	
	public double getStats(org.l2jmobius.gameserver.model.stats.Stat stat)
	{
		double value = 0;
		for (Item item : _equippedItems.values())
		{
			value += item.getTemplate().getStats(stat, 0);
		}
		return value;
	}
	
	public void restore()
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement(RESTORE_QUERY))
		{
			ps.setInt(1, _owner.getObjectId());
			try (ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
				{
					final long slot = rs.getLong("slot");
					final int indexMain = rs.getInt("index_main");
					final int indexSub = rs.getInt("index_sub");
					final VirtualItemHolder holder = VirtualItemData.getInstance().getVirtualItem(indexMain, indexSub);
					if (holder == null)
					{
						LOGGER.warning(getClass().getSimpleName() + ": Missing virtual item for player " + _owner.getName() + " indexMain " + indexMain + " indexSub " + indexSub);
						continue;
					}
					
					equipInternal(slot, holder, false, false);
				}
			}
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Could not restore virtual equipment for player " + _owner.getName(), e);
		}
	}
	
	public void clear(boolean broadcast)
	{
		for (Long slot : new ArrayList<>(_equipped.keySet()))
		{
			unequip(slot, broadcast);
		}
	}
	
	public boolean equip(long slot, VirtualItemHolder holder, boolean broadcast)
	{
		return equipInternal(slot, holder, broadcast, true);
	}
	
	private boolean equipInternal(long slot, VirtualItemHolder holder, boolean broadcast, boolean persist)
	{
		final VirtualItemHolder existing = _equipped.get(slot);
		if ((existing != null) && (existing.getIndexMain() == holder.getIndexMain()) && (existing.getIndexSub() == holder.getIndexSub()))
		{
			return false;
		}
		
		if (existing != null)
		{
			unequipInternal(slot, broadcast, persist);
		}
		
		_equipped.put(slot, holder);
		
		if (holder.getSlot() == 0)
		{
			applyVirtualSkill(holder, true, broadcast);
		}
		else
		{
			final Item item = new Item(holder.getItemId());
			item.setEnchantLevel(holder.getEnchant());
			item.setOwnerId(_owner.getObjectId());
			item.setVirtual(true);
			_equippedItems.put(slot, item);
			applyItemSkills(item, true, broadcast);
		}
		
		if (persist)
		{
			storeSlot(slot, holder);
		}
		
		if (broadcast)
		{
			_owner.getStat().recalculateStats(true);
			_owner.broadcastInfo();
		}
		
		LOGGER.info(getClass().getSimpleName() + ": " + _owner.getName() + " equipped virtual item " + holder.getItemId() + " +" + holder.getEnchant() + " on slot " + slot);
		return true;
	}
	
	public void unequip(long slot, boolean broadcast)
	{
		unequipInternal(slot, broadcast, true);
	}
	
	private void unequipInternal(long slot, boolean broadcast, boolean persist)
	{
		final VirtualItemHolder holder = _equipped.remove(slot);
		if (holder == null)
		{
			return;
		}
		
		final Item item = _equippedItems.remove(slot);
		if (holder.getSlot() == 0)
		{
			applyVirtualSkill(holder, false, broadcast);
		}
		else if (item != null)
		{
			applyItemSkills(item, false, broadcast);
		}
		
		if (persist)
		{
			deleteSlot(slot);
		}
		
		if (broadcast)
		{
			_owner.getStat().recalculateStats(true);
			_owner.broadcastInfo();
		}
		
		LOGGER.info(getClass().getSimpleName() + ": " + _owner.getName() + " unequipped virtual item from slot " + slot);
	}
	
	private void applyVirtualSkill(VirtualItemHolder holder, boolean add, boolean broadcast)
	{
		final SkillData skillData = SkillData.getInstance();
		final int skillId = holder.getItemId();
		final int skillLevel = Math.max(holder.getEnchant(), 1);
		if (skillData.getMaxLevel(skillId) == 0)
		{
			LOGGER.warning(getClass().getSimpleName() + ": Missing skill " + skillId + " level " + skillLevel);
			return;
		}
		
		final Skill skill = skillData.getSkill(skillId, skillLevel);
		if (skill == null)
		{
			LOGGER.warning(getClass().getSimpleName() + ": Missing skill " + skillId + " level " + skillLevel);
			return;
		}
		
		if (add)
		{
			if (_owner.getSkillLevel(skill.getId()) < skill.getLevel())
			{
				_owner.addSkill(skill, false);
				_skillIds.add(skill.getId());
			}
		}
		else if (!isSkillProvidedByVirtualItems(skill.getId(), holder) && !isSkillProvidedByInventory(skill.getId()))
		{
			if (_skillIds.remove(skill.getId()))
			{
				_owner.removeSkill(skill, false, skill.isPassive());
			}
		}
		
		if (broadcast)
		{
			_owner.sendSkillList();
		}
	}
	
	private void applyItemSkills(Item item, boolean add, boolean broadcast)
	{
		if (!item.getTemplate().hasSkills())
		{
			return;
		}
		
		if (add)
		{
			addItemSkills(item, item.getTemplate().getSkills(ItemSkillType.NORMAL));
			addEnchantSkills(item, item.getTemplate().getSkills(ItemSkillType.ON_ENCHANT));
			item.getTemplate().forEachSkill(ItemSkillType.ON_EQUIP, holder -> holder.getSkill().activateSkill(_owner, _owner));
		}
		else
		{
			removeItemSkills(item, item.getTemplate().getSkills(ItemSkillType.NORMAL));
			removeItemSkills(item, item.getTemplate().getSkills(ItemSkillType.ON_ENCHANT));
			item.getTemplate().forEachSkill(ItemSkillType.ON_UNEQUIP, holder -> holder.getSkill().activateSkill(_owner, _owner));
		}
		
		if (broadcast)
		{
			_owner.sendSkillList();
		}
	}
	
	private void addItemSkills(Item item, List<ItemSkillHolder> skills)
	{
		if (skills == null)
		{
			return;
		}
		
		for (ItemSkillHolder holder : skills)
		{
			final Skill skill = holder.getSkill();
			if ((skill != null) && (_owner.getSkillLevel(skill.getId()) < skill.getLevel()))
			{
				_owner.addSkill(skill, false);
				_skillIds.add(skill.getId());
			}
		}
	}
	
	private void addEnchantSkills(Item item, List<ItemSkillHolder> skills)
	{
		if (skills == null)
		{
			return;
		}
		
		for (ItemSkillHolder holder : skills)
		{
			if (item.getEnchantLevel() < holder.getValue())
			{
				continue;
			}
			
			final Skill skill = holder.getSkill();
			if ((skill != null) && (_owner.getSkillLevel(skill.getId()) < skill.getLevel()))
			{
				_owner.addSkill(skill, false);
				_skillIds.add(skill.getId());
			}
		}
	}
	
	private void removeItemSkills(Item item, List<ItemSkillHolder> skills)
	{
		if (skills == null)
		{
			return;
		}
		
		for (ItemSkillHolder holder : skills)
		{
			if ((holder.getType() == ItemSkillType.ON_ENCHANT) && (item.getEnchantLevel() < holder.getValue()))
			{
				continue;
			}
			
			final Skill skill = holder.getSkill();
			if ((skill != null) && !isSkillProvidedByVirtualItems(skill.getId(), item) && !isSkillProvidedByInventory(skill.getId()))
			{
				if (_skillIds.remove(skill.getId()))
				{
					_owner.removeSkill(skill, false, skill.isPassive());
				}
			}
		}
	}
	
	private boolean isSkillProvidedByVirtualItems(int skillId, VirtualItemHolder excluded)
	{
		for (Map.Entry<Long, VirtualItemHolder> entry : _equipped.entrySet())
		{
			final VirtualItemHolder holder = entry.getValue();
			if ((excluded != null) && (holder.getIndexMain() == excluded.getIndexMain()) && (holder.getIndexSub() == excluded.getIndexSub()))
			{
				continue;
			}
			
			if ((holder.getSlot() == 0) && (holder.getItemId() == skillId))
			{
				return true;
			}
			
			if (holder.getSlot() != 0)
			{
				final Item item = _equippedItems.get(entry.getKey());
				if ((item != null) && isSkillProvidedByItem(item, skillId))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	private boolean isSkillProvidedByVirtualItems(int skillId, Item excludedItem)
	{
		for (Item item : _equippedItems.values())
		{
			if ((excludedItem != null) && (item == excludedItem))
			{
				continue;
			}
			
			if (isSkillProvidedByItem(item, skillId))
			{
				return true;
			}
		}
		
		return false;
	}
	
	private boolean isSkillProvidedByInventory(int skillId)
	{
		for (Item item : _owner.getInventory().getPaperdollItems())
		{
			if (isSkillProvidedByItem(item, skillId))
			{
				return true;
			}
		}
		
		return false;
	}
	
	private boolean isSkillProvidedByItem(Item item, int skillId)
	{
		final List<ItemSkillHolder> normalSkills = item.getTemplate().getSkills(ItemSkillType.NORMAL);
		if (containsSkill(normalSkills, skillId))
		{
			return true;
		}
		
		final List<ItemSkillHolder> enchantSkills = item.getTemplate().getSkills(ItemSkillType.ON_ENCHANT);
		return containsEnchantSkill(item, enchantSkills, skillId);
	}
	
	private boolean containsSkill(List<ItemSkillHolder> skills, int skillId)
	{
		if (skills == null)
		{
			return false;
		}
		
		for (ItemSkillHolder holder : skills)
		{
			if (holder.getSkillId() == skillId)
			{
				return true;
			}
		}
		
		return false;
	}
	
	private boolean containsEnchantSkill(Item item, List<ItemSkillHolder> skills, int skillId)
	{
		if (skills == null)
		{
			return false;
		}
		
		for (ItemSkillHolder holder : skills)
		{
			if ((item.getEnchantLevel() >= holder.getValue()) && (holder.getSkillId() == skillId))
			{
				return true;
			}
		}
		
		return false;
	}
	
	private void storeSlot(long slot, VirtualItemHolder holder)
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement(STORE_QUERY))
		{
			ps.setInt(1, _owner.getObjectId());
			ps.setLong(2, slot);
			ps.setInt(3, holder.getIndexMain());
			ps.setInt(4, holder.getIndexSub());
			ps.execute();
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Could not save virtual equipment for player " + _owner.getName(), e);
		}
	}
	
	private void deleteSlot(long slot)
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement(DELETE_QUERY))
		{
			ps.setInt(1, _owner.getObjectId());
			ps.setLong(2, slot);
			ps.execute();
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Could not delete virtual equipment for player " + _owner.getName(), e);
		}
	}
}
