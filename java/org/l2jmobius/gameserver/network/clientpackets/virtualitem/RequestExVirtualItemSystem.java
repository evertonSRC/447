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
package org.l2jmobius.gameserver.network.clientpackets.virtualitem;

import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.item.virtual.VirtualSlot;
import org.l2jmobius.gameserver.network.clientpackets.ClientPacket;
import org.l2jmobius.gameserver.network.serverpackets.virtualitem.ExVirtualItemSystem;
import org.l2jmobius.gameserver.network.serverpackets.virtualitem.ExVirtualItemSystemPointInfo;

/**
 * @author Mobius
 */
public class RequestExVirtualItemSystem extends ClientPacket
{
	private static final int ACTION_REFRESH = 0;
	private static final int ACTION_EQUIP = 1;
	private static final int ACTION_UNEQUIP = 2;
	
	private int _action = ACTION_REFRESH;
	private int _indexMain;
	private int _indexSub;
	private int _slotId;
	
	@Override
	protected void readImpl()
	{
		final int available = remaining();
		if (available >= 12)
		{
			_action = readInt();
			_indexMain = readInt();
			_indexSub = readInt();
		}
		else if (available == 8)
		{
			_action = ACTION_EQUIP;
			_indexMain = readInt();
			_indexSub = readInt();
		}
		else if (available == 4)
		{
			_action = ACTION_UNEQUIP;
			_slotId = readInt();
		}
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		switch (_action)
		{
			case ACTION_EQUIP:
			{
				player.equipVirtualFromCatalog(_indexMain, _indexSub);
				break;
			}
			case ACTION_UNEQUIP:
			{
				final VirtualSlot slot = resolveSlot(_slotId);
				if (slot != null)
				{
					player.unequipVirtual(slot);
				}
				break;
			}
		}
		
		player.sendPacket(new ExVirtualItemSystem(player));
		player.sendPacket(new ExVirtualItemSystemPointInfo(player.getVirtualPoints()));
	}
	
	private VirtualSlot resolveSlot(int slotId)
	{
		VirtualSlot slot = VirtualSlot.fromClientSlot(slotId);
		if ((slot == null) && (slotId >= 1000))
		{
			slot = VirtualSlot.fromClientSlot(slotId - 1000);
		}
		return slot;
	}
}
