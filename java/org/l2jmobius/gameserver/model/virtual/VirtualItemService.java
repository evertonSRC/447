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
package org.l2jmobius.gameserver.model.virtual;

import java.util.logging.Logger;

import org.l2jmobius.gameserver.data.holders.VirtualItemEntry;
import org.l2jmobius.gameserver.data.xml.VirtualItemData;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.enums.player.VirtualSlot;
import org.l2jmobius.gameserver.model.actor.holders.player.VirtualEquippedItem;

/**
 * @author Mobius
 */
public final class VirtualItemService
{
	private static final Logger LOGGER = Logger.getLogger(VirtualItemService.class.getName());
	
	private VirtualItemService()
	{
	}
	
	public static VirtualItemResult equipVirtualItem(Player player, int indexMain, int indexSub, long slotRequested)
	{
		if (player == null)
		{
			return VirtualItemResult.failure("Player inválido.");
		}
		
		final VirtualItemEntry entry = VirtualItemData.getInstance().getVirtualItemEntry(indexMain, indexSub);
		if (entry == null)
		{
			return VirtualItemResult.failure("Virtual item não encontrado para indexMain=" + indexMain + " indexSub=" + indexSub + ".");
		}
		
		final VirtualSlot resolvedSlot = VirtualSlot.resolve(slotRequested, entry);
		if (resolvedSlot == null)
		{
			return VirtualItemResult.failure("Slot virtual inválido.");
		}
		
		final VirtualSlot expectedSlot = VirtualSlot.resolve(0, entry);
		if ((slotRequested > 0) && (expectedSlot != null) && (expectedSlot != resolvedSlot))
		{
			return VirtualItemResult.failure("Slot incompatível. Esperado: " + expectedSlot.getAlias() + ".");
		}
		
		if (player.getVirtualEquipment().isSlotOccupied(resolvedSlot))
		{
			return VirtualItemResult.failure("Slot " + resolvedSlot.getAlias() + " já ocupado. Desequipe primeiro.");
		}
		
		final int cost = entry.getCostVISPoint();
		if (cost > 0)
		{
			final int availablePoints = player.getVirtualPoints();
			if (availablePoints < cost)
			{
				LOGGER.warning("Virtual item equip denied for player " + player.getName() + " (" + player.getObjectId() + ") with points=" + availablePoints + " cost=" + cost + " entry=" + indexMain + ":" + indexSub + ".");
				return VirtualItemResult.failure("Pontos insuficientes. Necessário: " + cost + ", disponível: " + availablePoints + ".");
			}
		}
		
		final VirtualEquippedItem item = new VirtualEquippedItem(entry.getItemId(), entry.getEnchant(), entry.getIndexMain(), entry.getIndexSub());
		player.setVirtualEquipment(resolvedSlot, item);
		if (cost > 0)
		{
			player.addVirtualPoints(-cost);
		}
		return VirtualItemResult.success("Virtual item equipado em " + resolvedSlot.getAlias() + ".", resolvedSlot, item);
	}
	
	public static VirtualItemResult unequipVirtualItem(Player player, VirtualSlot slot)
	{
		if (player == null)
		{
			return VirtualItemResult.failure("Player inválido.");
		}
		
		if (slot == null)
		{
			return VirtualItemResult.failure("Slot virtual inválido.");
		}
		
		if (!player.getVirtualEquipment().isSlotOccupied(slot))
		{
			return VirtualItemResult.failure("Slot " + slot.getAlias() + " está vazio.");
		}
		
		player.removeVirtualEquipment(slot);
		return VirtualItemResult.success("Virtual item removido do slot " + slot.getAlias() + ".", slot, null);
	}
	
	public static class VirtualItemResult
	{
		private final boolean _success;
		private final String _message;
		private final VirtualSlot _slot;
		private final VirtualEquippedItem _item;
		
		private VirtualItemResult(boolean success, String message, VirtualSlot slot, VirtualEquippedItem item)
		{
			_success = success;
			_message = message;
			_slot = slot;
			_item = item;
		}
		
		public boolean isSuccess()
		{
			return _success;
		}
		
		public String getMessage()
		{
			return _message;
		}
		
		public VirtualSlot getSlot()
		{
			return _slot;
		}
		
		public VirtualEquippedItem getItem()
		{
			return _item;
		}
		
		private static VirtualItemResult success(String message, VirtualSlot slot, VirtualEquippedItem item)
		{
			return new VirtualItemResult(true, message, slot, item);
		}
		
		private static VirtualItemResult failure(String message)
		{
			return new VirtualItemResult(false, message, null, null);
		}
	}
}
