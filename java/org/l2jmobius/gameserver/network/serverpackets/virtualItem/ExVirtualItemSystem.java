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
import org.l2jmobius.gameserver.data.holders.VirtualItemHolder;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
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
	private final int _selectIndexMain;
	private final int _selectIndexSub;
	private final long _selectSlot;
	private final List<VirtualItemHolder> _updateVisItemInfo;
	private final boolean _result;
	
	public ExVirtualItemSystem(Player player, int type, int selectIndexMain, int selectIndexSub, long selectSlot, List<VirtualItemHolder> updateVisItemInfo, boolean result)
	{
		_player = player;
		_type = type;
		_selectIndexMain = selectIndexMain;
		_selectIndexSub = selectIndexSub;
		_selectSlot = selectSlot;
		_updateVisItemInfo = updateVisItemInfo;
		_result = result;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_VIRTUALITEM_SYSTEM.writeId(this, buffer);
		final int illusoryPointsAcquired = _player.getVariables().getInt(PlayerVariables.ILLUSORY_POINTS_ACQUIRED, 0);
		final int illusoryPointsUsed = _player.getVariables().getInt(PlayerVariables.ILLUSORY_POINTS_USED, 0);
		
		if (_type == 1)// XXX Read existing virtual items.
		{
			buffer.writeByte(_type); // var int cType;
			buffer.writeByte(_result); // var int cResult;
			buffer.writeInt(IllusoryEquipmentConfig.ILLUSORY_EQUIPMENT_EVENT_DURATION * 2592000); // Event ending time (2592000 = 30 days in milis)
			buffer.writeInt(illusoryPointsAcquired); // var int nTotalGetVISPoint;
			buffer.writeInt(illusoryPointsUsed); // var int nTotalUsedVISPoint;
			buffer.writeInt(_selectIndexMain); // var int nSelectIndexMain;
			buffer.writeInt(_selectIndexSub); // var int nSelectIndexSub;
			buffer.writeLong(_selectSlot); // var int nSelectSlot;
			writeVirtualItems(buffer);
		}
		else if (_type == 2) // XXX Reset all.
		{
			buffer.writeByte(_type); // var int cType;
			buffer.writeByte(_result); // var int cResult;
			buffer.writeInt(IllusoryEquipmentConfig.ILLUSORY_EQUIPMENT_EVENT_DURATION * 2592000); // Event ending time (2592000 = 30 days in milis)
			buffer.writeInt(illusoryPointsAcquired); // var int nTotalGetVISPoint;
			buffer.writeInt(illusoryPointsUsed); // var int nTotalUsedVISPoint;
			buffer.writeInt(IllusoryEquipmentConfig.ILLUSORY_EQUIPMENT_EVENT_POINTS_LIMIT); // max available points default 600
			writeVirtualItems(buffer);
		}
		
		else if (_type == 3) // XXX Update virtual items
		{
			buffer.writeByte(_type); // var int cType;
			buffer.writeByte(_result); // var int cResult;
			buffer.writeInt(IllusoryEquipmentConfig.ILLUSORY_EQUIPMENT_EVENT_DURATION * 2592000); // Event ending time (2592000 = 30 days in milis)
			buffer.writeInt(illusoryPointsAcquired); // var int nTotalGetVISPoint;
			buffer.writeInt(illusoryPointsUsed); // var int nTotalUsedVISPoint;
			buffer.writeInt(_selectIndexMain); // var int nSelectIndexMain;
			buffer.writeInt(_selectIndexSub); // var int nSelectIndexSub;
			buffer.writeLong(_selectSlot); // var int nSelectSlot;
			writeVirtualItems(buffer);
		}
	}
	
	private void writeVirtualItems(WritableBuffer buffer)
	{
		buffer.writeInt(_updateVisItemInfo.size()); // equipment array size
		for (VirtualItemHolder virtualItem : _updateVisItemInfo)
		{
			buffer.writeInt(virtualItem.getIndexMain()); // var int nIndexMain;
			buffer.writeInt(virtualItem.getIndexSub()); // var int nIndexSub;
			buffer.writeLong(virtualItem.getSlot()); // var int nSlot;
			buffer.writeInt(virtualItem.getCostVISPoint()); // var int nCostVISPoint;
			buffer.writeInt(virtualItem.getItemId()); // var int nItemClass;
			buffer.writeInt(virtualItem.getEnchant()); // var int nEnchant;
		}
	}
}
