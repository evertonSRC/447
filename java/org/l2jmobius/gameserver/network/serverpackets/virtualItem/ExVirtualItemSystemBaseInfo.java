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

import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.config.IllusoryEquipmentConfig;
import org.l2jmobius.gameserver.managers.GlobalVariablesManager;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author CostyKiller
 */
public class ExVirtualItemSystemBaseInfo extends ServerPacket
{
	private final Player _player;
	private final int _illusoryPointsAcquired;
	private final int _illusoryPointsUsed;
	private final long _virtualItemEventStart = GlobalVariablesManager.getInstance().getLong("VIRTUAL_ITEM_EVENT_START", 0); // Event starting time
	private final long _virtualItemEventEnd;
	
	public ExVirtualItemSystemBaseInfo(Player player)
	{
		_player = player;
		_illusoryPointsAcquired = _player.getVirtualPoints();
		_illusoryPointsUsed = 0;
		
		// Event ending time ((start time + (2592000000L * config interval)) - current time).
		_virtualItemEventEnd = (((_virtualItemEventStart + (2592000000L * IllusoryEquipmentConfig.ILLUSORY_EQUIPMENT_EVENT_DURATION)) - System.currentTimeMillis()) / 1000);
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_VIRTUALITEM_SYSTEM_BASE_INFO.writeId(this, buffer);
		buffer.writeInt((int) _virtualItemEventEnd);
		buffer.writeInt(_illusoryPointsAcquired); // Total Illusory Points acquired
		buffer.writeInt(_illusoryPointsUsed); // Total Illusory Points used
		buffer.writeInt(IllusoryEquipmentConfig.ILLUSORY_EQUIPMENT_EVENT_POINTS_LIMIT); // max available points default 600
	}
}
