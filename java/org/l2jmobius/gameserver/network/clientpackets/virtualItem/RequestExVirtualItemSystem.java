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
package org.l2jmobius.gameserver.network.clientpackets.virtualItem;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.l2jmobius.gameserver.config.IllusoryEquipmentConfig;
import org.l2jmobius.gameserver.data.holders.VirtualItemHolder;
import org.l2jmobius.gameserver.data.xml.VirtualItemData;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.virtualitem.VirtualItemService;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.clientpackets.ClientPacket;
import org.l2jmobius.gameserver.network.serverpackets.virtualItem.ExVirtualItemSystem;
import org.l2jmobius.gameserver.network.serverpackets.virtualItem.ExVirtualItemSystemBaseInfo;
import org.l2jmobius.gameserver.network.serverpackets.virtualItem.ExVirtualItemSystemPointInfo;

/**
 * @author CostyKiller
 */
public class RequestExVirtualItemSystem extends ClientPacket
{
	private static final Logger LOGGER = Logger.getLogger(RequestExVirtualItemSystem.class.getName());
	
	private int _type;
	private int _selectIndexMain;
	private int _selectIndexSub;
	private long _selectSlot;
	// private int _indexMain;
	// private int _indexSub;
	// private long _slot;
	// private int _costVISPoint;
	// private int _itemClass;
	// private int _enchant;
	private final List<VirtualItemHolder> _updateVisItemInfo = new LinkedList<>();
	
	@Override
	protected void readImpl()
	{
		_type = readByte();
		_selectIndexMain = readInt();
		_selectIndexSub = readInt();
		_selectSlot = readInt();
		readShort(); // Always 26
		// _indexMain = readInt();
		// _indexSub = readInt();
		// _slot = readInt();
		// _costVISPoint = readInt();
		// _itemClass = readInt();
		// _enchant = readInt();
		
		// final VirtualItemHolder virtualItem = new VirtualItemHolder(_nIndexMain, _nSlot, _nIndexSub, _nItemClass, _nEnchant, _nCostVISPoint);
		// _updateVisItemInfo.add(virtualItem);
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (player.isInStoreMode() || player.isCrafting() || player.isProcessingRequest() || player.isProcessingTransaction())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_USE_THIS_SYSTEM_DURING_TRADING_PRIVATE_STORE_AND_WORKSHOP_SETUP);
			return;
		}
		
		if (player.isFishing())
		{
			// You can't mount, dismount, break and drop items while fishing.
			player.sendPacket(SystemMessageId.YOU_CANNOT_DO_THAT_WHILE_FISHING_2);
			return;
		}
		
		if (player.isFlying())
		{
			return;
		}
		
		if (!IllusoryEquipmentConfig.ILLUSORY_EQUIPMENT_ENABLED)
		{
			return;
		}
		
		boolean result = true;
		switch (_type)
		{
			case 1:
			{
				_updateVisItemInfo.addAll(VirtualItemService.getSelectedVirtualItems(player));
				break;
			}
			case 2:
			{
				VirtualItemService.resetSelections(player, true);
				break;
			}
			case 3:
			{
				final VirtualItemHolder holder = VirtualItemData.getInstance().getVirtualItem(_selectIndexMain, _selectIndexSub);
				if (holder == null)
				{
					if (IllusoryEquipmentConfig.ILLUSORY_EQUIPMENT_EVENT_DEBUG_ENABLED)
					{
						LOGGER.info("[IllusoryEquipment] Player " + player.getName() + " tried to select missing indexMain=" + _selectIndexMain + " indexSub=" + _selectIndexSub);
					}
					result = false;
					break;
				}
				
				final Map<Integer, Integer> selections = VirtualItemService.getSelections(player);
				final int previousIndexSub = selections.getOrDefault(_selectIndexMain, 0);
				final VirtualItemHolder previousHolder = previousIndexSub > 0 ? VirtualItemData.getInstance().getVirtualItem(_selectIndexMain, previousIndexSub) : null;
				selections.put(_selectIndexMain, _selectIndexSub);
				
				final int totalCost = VirtualItemService.calculateTotalCost(selections);
				final int acquiredPoints = player.getVariables().getInt(PlayerVariables.ILLUSORY_POINTS_ACQUIRED, 0);
				final int maxPoints = Math.min(acquiredPoints, IllusoryEquipmentConfig.ILLUSORY_EQUIPMENT_EVENT_POINTS_LIMIT);
				if (IllusoryEquipmentConfig.ILLUSORY_EQUIPMENT_EVENT_DEBUG_ENABLED)
				{
					LOGGER.info("[IllusoryEquipment] Player " + player.getName() + " applying indexMain=" + _selectIndexMain + " indexSub=" + _selectIndexSub + " cost=" + holder.getCostVISPoint() + " totalCost=" + totalCost + " maxPoints=" + maxPoints);
				}
				if (totalCost > maxPoints)
				{
					player.sendPacket(SystemMessageId.YOU_CANNOT_COMPLETE_THE_PURCHASE_AS_YOU_DO_NOT_HAVE_ENOUGH_ILLUSORY_EQUIPMENT_POINTS);
					if (IllusoryEquipmentConfig.ILLUSORY_EQUIPMENT_EVENT_DEBUG_ENABLED)
					{
						LOGGER.info("[IllusoryEquipment] Player " + player.getName() + " insufficient points: totalCost=" + totalCost + " maxPoints=" + maxPoints);
					}
					result = false;
					break;
				}
				
				result = VirtualItemService.applySelection(player, holder, previousHolder, true);
				if (!result)
				{
					if (IllusoryEquipmentConfig.ILLUSORY_EQUIPMENT_EVENT_DEBUG_ENABLED)
					{
						LOGGER.info("[IllusoryEquipment] Player " + player.getName() + " failed to apply indexMain=" + _selectIndexMain + " indexSub=" + _selectIndexSub);
					}
					player.sendMessage("Unable to apply illusory equipment selection.");
					break;
				}
				
				player.getVariables().set(PlayerVariables.ILLUSORY_POINTS_USED, totalCost);
				player.sendPacket(SystemMessageId.THE_SELECTED_ILLUSORY_EFFECT_IS_APPLIED);
				_updateVisItemInfo.addAll(VirtualItemService.getSelectedVirtualItems(player));
				break;
			}
		}
		
		player.sendPacket(new ExVirtualItemSystem(player, _type, _selectIndexMain, _selectIndexSub, _selectSlot, _updateVisItemInfo, result));
		final int usedPoints = player.getVariables().getInt(PlayerVariables.ILLUSORY_POINTS_USED, 0);
		final int acquiredPoints = player.getVariables().getInt(PlayerVariables.ILLUSORY_POINTS_ACQUIRED, 0);
		final int maxPoints = Math.min(acquiredPoints, IllusoryEquipmentConfig.ILLUSORY_EQUIPMENT_EVENT_POINTS_LIMIT);
		player.sendPacket(new ExVirtualItemSystemBaseInfo(player));
		player.sendPacket(new ExVirtualItemSystemPointInfo(player, Math.max(0, maxPoints - usedPoints)));
	}
}
