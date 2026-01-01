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
package org.l2jmobius.gameserver.network.serverpackets.virtualItem;

import java.util.List;

import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.config.IllusoryEquipmentConfig;
import org.l2jmobius.gameserver.data.holders.VirtualItemEntry;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author CostyKiller
 */
public class ExVirtualItemSystem extends ServerPacket
{
	private final Player _player;
	private final int _type;
	private final boolean _result;
	private final int _selectIndexMain;
	private final int _selectIndexSub;
	private final long _selectSlot;
	private final List<VirtualItemEntry> _updateVisItemInfo;
	
	public ExVirtualItemSystem(Player player, int type, int selectIndexMain, int selectIndexSub, long selectSlot, List<VirtualItemEntry> updateVisItemInfo, boolean result)
	{
		_player = player;
		_type = type;
		_result = result;
		_selectIndexMain = selectIndexMain;
		_selectIndexSub = selectIndexSub;
		_selectSlot = selectSlot;
		_updateVisItemInfo = updateVisItemInfo;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_VIRTUALITEM_SYSTEM.writeId(this, buffer);
		final int availablePoints = _player.getVirtualPoints();
		final int usedPoints = 0;
		
		if (_type == 1)// Read existing virtual items.
		{
			buffer.writeByte(_type); // var int cType;
			buffer.writeByte(_result); // var int cResult;
			buffer.writeInt(IllusoryEquipmentConfig.ILLUSORY_EQUIPMENT_EVENT_DURATION * 2592000); // Event ending time (2592000 = 30 days in milis)
			buffer.writeInt(availablePoints); // var int nTotalGetVISPoint;
			buffer.writeInt(usedPoints); // var int nTotalUsedVISPoint;
			buffer.writeInt(_selectIndexMain); // var int nSelectIndexMain;
			buffer.writeInt(_selectIndexSub); // var int nSelectIndexSub;
			buffer.writeLong(_selectSlot); // var int nSelectSlot;
			
			writeVirtualItemList(buffer, _updateVisItemInfo);
		}
		else if (_type == 2) // Reset all.
		{
			buffer.writeByte(_type); // var int cType;
			buffer.writeByte(_result); // var int cResult;
			buffer.writeInt(IllusoryEquipmentConfig.ILLUSORY_EQUIPMENT_EVENT_DURATION * 2592000); // Event ending time (2592000 = 30 days in milis)
			buffer.writeInt(availablePoints); // var int nTotalGetVISPoint;
			buffer.writeInt(usedPoints); // var int nTotalUsedVISPoint;
			buffer.writeInt(IllusoryEquipmentConfig.ILLUSORY_EQUIPMENT_EVENT_POINTS_LIMIT); // max available points default 600
			
			writeVirtualItemList(buffer, _updateVisItemInfo);
		}
		else if (_type == 3) // Update virtual items
		{
			buffer.writeByte(_type); // var int cType;
			buffer.writeByte(_result); // var int cResult;
			buffer.writeInt(IllusoryEquipmentConfig.ILLUSORY_EQUIPMENT_EVENT_DURATION * 2592000); // Event ending time (2592000 = 30 days in milis)
			buffer.writeInt(availablePoints); // var int nTotalGetVISPoint;
			buffer.writeInt(usedPoints); // var int nTotalUsedVISPoint;
			buffer.writeInt(_selectIndexMain); // var int nSelectIndexMain;
			buffer.writeInt(_selectIndexSub); // var int nSelectIndexSub;
			buffer.writeLong(_selectSlot); // var int nSelectSlot;
			
			writeVirtualItemList(buffer, _updateVisItemInfo);
		}
		
		_player.sendPacket(new ExVirtualItemSystemBaseInfo(_player));
		_player.sendPacket(new ExVirtualItemSystemPointInfo(_player, availablePoints));
	}

	private static void writeVirtualItemList(WritableBuffer buffer, List<VirtualItemEntry> entries)
	{
		buffer.writeInt(entries.size()); // equipment array size
		for (VirtualItemEntry entry : entries)
		{
			buffer.writeInt(entry.getIndexMain()); // var int nIndexMain;
			buffer.writeInt(entry.getIndexSub()); // var int nIndexSub;
			final long slotIdClient = entry.getSlotIdClient() > 0 ? entry.getSlotIdClient() : entry.getVirtualSlotId();
			buffer.writeLong(slotIdClient); // var int nSlot;
			writeVirtualItemStats(buffer, entry);
		}
	}

	private static void writeVirtualItemStats(WritableBuffer buffer, VirtualItemEntry entry)
	{
		if (entry == null)
		{
			buffer.writeInt(0); // var int _nCostVISPoint;
			buffer.writeInt(0); // var int nItemClass;
			buffer.writeInt(0); // var int nEnchant;
			return;
		}
		
		buffer.writeInt(entry.getCostVISPoint()); // var int _nCostVISPoint;
		buffer.writeInt(entry.getItemId()); // var int nItemClass;
		buffer.writeInt(entry.getEnchant()); // var int nEnchant;
	}
}
