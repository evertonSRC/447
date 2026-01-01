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
package org.l2jmobius.gameserver.model.virtualitem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.l2jmobius.gameserver.config.IllusoryEquipmentConfig;
import org.l2jmobius.gameserver.data.holders.VirtualItemHolder;
import org.l2jmobius.gameserver.data.xml.ItemData;
import org.l2jmobius.gameserver.data.xml.SkillData;
import org.l2jmobius.gameserver.data.xml.VirtualItemData;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.item.enums.BodyPart;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.network.serverpackets.InventoryUpdate;

/**
 * Handles persistence and application of Illusory Equipment selections.
 * @author Mobius
 */
public class VirtualItemService
{
	private static final Logger LOGGER = Logger.getLogger(VirtualItemService.class.getName());
	private static final String SELECTION_PREFIX = "ILLUSORY_EQUIPMENT_SELECTION_";
	
	public static Map<Integer, Integer> getSelections(Player player)
	{
		final Map<Integer, Integer> selections = new HashMap<>();
		for (int mainIndex : VirtualItemData.getInstance().getMainIndexes())
		{
			final int subIndex = player.getVariables().getInt(SELECTION_PREFIX + mainIndex, 0);
			if (subIndex > 0)
			{
				selections.put(mainIndex, subIndex);
			}
		}
		return selections;
	}
	
	public static List<VirtualItemHolder> getSelectedVirtualItems(Player player)
	{
		final List<VirtualItemHolder> items = new ArrayList<>();
		for (Map.Entry<Integer, Integer> entry : getSelections(player).entrySet())
		{
			final VirtualItemHolder holder = VirtualItemData.getInstance().getVirtualItem(entry.getKey(), entry.getValue());
			if (holder != null)
			{
				items.add(holder);
			}
		}
		return items;
	}
	
	public static int calculateTotalCost(Map<Integer, Integer> selections)
	{
		int totalCost = 0;
		for (Map.Entry<Integer, Integer> entry : selections.entrySet())
		{
			final VirtualItemHolder holder = VirtualItemData.getInstance().getVirtualItem(entry.getKey(), entry.getValue());
			if (holder != null)
			{
				totalCost += holder.getCostVISPoint();
			}
		}
		return totalCost;
	}
	
	public static void updateUsedPoints(Player player)
	{
		final int usedPoints = calculateTotalCost(getSelections(player));
		player.getVariables().set(PlayerVariables.ILLUSORY_POINTS_USED, usedPoints);
	}
	
	public static boolean applySelection(Player player, VirtualItemHolder holder, VirtualItemHolder oldHolder, boolean sendUpdates)
	{
		if ((holder == null) || (player == null))
		{
			return false;
		}
		
		if ((oldHolder != null) && (oldHolder.getItemId() == holder.getItemId()) && (oldHolder.getEnchant() == holder.getEnchant()) && (oldHolder.getSlot() == holder.getSlot()))
		{
			player.getVariables().set(SELECTION_PREFIX + holder.getIndexMain(), holder.getIndexSub());
			return true;
		}
		
		if (!canApply(player, holder))
		{
			return false;
		}
		
		final boolean applied = applyEffect(player, holder, sendUpdates);
		if (!applied)
		{
			if (IllusoryEquipmentConfig.ILLUSORY_EQUIPMENT_EVENT_DEBUG_ENABLED)
			{
				LOGGER.info(getLogPrefix(player) + "Failed to apply virtual item indexMain=" + holder.getIndexMain() + " indexSub=" + holder.getIndexSub());
			}
			return false;
		}
		
		if (oldHolder != null)
		{
			final boolean oldIsSkill = isSkill(oldHolder);
			final boolean newIsSkill = isSkill(holder);
			final boolean sameItemId = oldHolder.getItemId() == holder.getItemId();
			if ((oldIsSkill || newIsSkill) && !sameItemId)
			{
				removeEffect(player, oldHolder, sendUpdates);
			}
			else if (!oldIsSkill && !newIsSkill && (oldHolder.getSlot() != holder.getSlot()))
			{
				removeEffect(player, oldHolder, sendUpdates);
			}
		}
		
		player.getVariables().set(SELECTION_PREFIX + holder.getIndexMain(), holder.getIndexSub());
		return true;
	}
	
	public static void resetSelections(Player player, boolean sendUpdates)
	{
		for (VirtualItemHolder holder : getSelectedVirtualItems(player))
		{
			removeEffect(player, holder, sendUpdates);
		}
		
		for (int mainIndex : VirtualItemData.getInstance().getMainIndexes())
		{
			player.getVariables().remove(SELECTION_PREFIX + mainIndex);
		}
		
		player.getVariables().set(PlayerVariables.ILLUSORY_POINTS_USED, 0);
	}
	
	public static void reapplySelections(Player player)
	{
		for (VirtualItemHolder holder : getSelectedVirtualItems(player))
		{
			if (!applyEffect(player, holder, false) && IllusoryEquipmentConfig.ILLUSORY_EQUIPMENT_EVENT_DEBUG_ENABLED)
			{
				LOGGER.info(getLogPrefix(player) + "Failed to reapply indexMain=" + holder.getIndexMain() + " indexSub=" + holder.getIndexSub());
			}
		}
		
		updateUsedPoints(player);
	}
	
	private static boolean applyEffect(Player player, VirtualItemHolder holder, boolean sendUpdates)
	{
		final Skill skill = SkillData.getInstance().getSkill(holder.getItemId(), holder.getEnchant());
		if (isSkill(holder))
		{
			player.addSkill(skill, true);
			if (sendUpdates)
			{
				player.sendSkillList();
			}
			return true;
		}
		
		final Item baseItem = getEquippedItemForSlot(player, holder.getSlot());
		if (baseItem == null)
		{
			return false;
		}
		
		baseItem.setVisualId(holder.getItemId());
		if (sendUpdates)
		{
			final InventoryUpdate iu = new InventoryUpdate();
			iu.addModifiedItem(baseItem);
			player.sendInventoryUpdate(iu);
			player.broadcastInfo();
		}
		return true;
	}
	
	private static void removeEffect(Player player, VirtualItemHolder holder, boolean sendUpdates)
	{
		if (isSkill(holder))
		{
			final Skill skill = SkillData.getInstance().getSkill(holder.getItemId(), holder.getEnchant());
			player.removeSkill(skill, false, true);
			if (sendUpdates)
			{
				player.sendSkillList();
			}
			return;
		}
		
		final Item baseItem = getEquippedItemForSlot(player, holder.getSlot());
		if (baseItem == null)
		{
			return;
		}
		
		baseItem.setVisualId(0);
		if (sendUpdates)
		{
			final InventoryUpdate iu = new InventoryUpdate();
			iu.addModifiedItem(baseItem);
			player.sendInventoryUpdate(iu);
			player.broadcastInfo();
		}
	}
	
	private static Item getEquippedItemForSlot(Player player, long slotMask)
	{
		final BodyPart bodyPart = getBodyPart(slotMask);
		if (bodyPart == null)
		{
			return null;
		}
		return player.getInventory().getPaperdollItemByBodyPart(bodyPart);
	}
	
	private static boolean canApply(Player player, VirtualItemHolder holder)
	{
		if (isSkill(holder))
		{
			return SkillData.getInstance().getSkill(holder.getItemId(), holder.getEnchant()) != null;
		}
		
		if (ItemData.getInstance().getTemplate(holder.getItemId()) == null)
		{
			return false;
		}
		
		return getEquippedItemForSlot(player, holder.getSlot()) != null;
	}
	
	private static boolean isSkill(VirtualItemHolder holder)
	{
		return (SkillData.getInstance().getSkill(holder.getItemId(), holder.getEnchant()) != null) && (ItemData.getInstance().getTemplate(holder.getItemId()) == null);
	}
	
	private static BodyPart getBodyPart(long slotMask)
	{
		for (BodyPart part : BodyPart.values())
		{
			if (part.getMask() == slotMask)
			{
				return part;
			}
		}
		return null;
	}
	
	private static String getLogPrefix(Player player)
	{
		return "[IllusoryEquipment] playerId=" + player.getObjectId() + " ";
	}
}
