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
package org.l2jmobius.gameserver.network.serverpackets.virtualitem;

import java.util.EnumMap;
import java.util.Map;

import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.item.virtual.VirtualEquippedItem;
import org.l2jmobius.gameserver.model.item.virtual.VirtualSlot;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Mobius
 */
public class ExVirtualItemSystem extends ServerPacket
{
	private final Map<VirtualSlot, VirtualEquippedItem> _equipment;
	
	public ExVirtualItemSystem(Player player)
	{
		_equipment = new EnumMap<>(VirtualSlot.class);
		_equipment.putAll(player.getVirtualEquipment());
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_VIRTUALITEM_SYSTEM.writeId(this, buffer);
		buffer.writeInt(VirtualSlot.values().length);
		for (VirtualSlot slot : VirtualSlot.values())
		{
			final VirtualEquippedItem equippedItem = _equipment.get(slot);
			buffer.writeInt(slot.getVirtualSlotId());
			buffer.writeInt(equippedItem != null ? equippedItem.getItemId() : 0);
			buffer.writeInt(equippedItem != null ? equippedItem.getEnchantLevel() : 0);
		}
	}
}
