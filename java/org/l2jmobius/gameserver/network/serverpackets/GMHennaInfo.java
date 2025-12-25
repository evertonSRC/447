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
package org.l2jmobius.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.item.Henna;
import org.l2jmobius.gameserver.model.stats.BaseStat;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * This server packet sends the player's henna information using the Game Master's UI.
 * @author Mobius
 */
public class GMHennaInfo extends ServerPacket
{
	private final Player _player;
	private final List<Henna> _hennas = new ArrayList<>();
	
	public GMHennaInfo(Player player)
	{
		_player = player;
		for (int i = 1; i < 4; i++)
		{
			if (player.getHenna(i) != null)
			{
				_hennas.add(player.getHenna(i));
			}
		}
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.GMHENNA_INFO.writeId(this, buffer);
		buffer.writeShort(_player.getHennaValue(BaseStat.INT)); // equip INT
		buffer.writeShort(_player.getHennaValue(BaseStat.STR)); // equip STR
		buffer.writeShort(_player.getHennaValue(BaseStat.CON)); // equip CON
		buffer.writeShort(_player.getHennaValue(BaseStat.MEN)); // equip MEN
		buffer.writeShort(_player.getHennaValue(BaseStat.DEX)); // equip DEX
		buffer.writeShort(_player.getHennaValue(BaseStat.WIT)); // equip WIT
		buffer.writeShort(_player.getHennaValue(BaseStat.LUC)); // equip LUC
		buffer.writeShort(_player.getHennaValue(BaseStat.CHA)); // equip CHA
		buffer.writeInt(3); // Slots
		buffer.writeInt(_hennas.size()); // Size
		for (Henna henna : _hennas)
		{
			buffer.writeInt(henna.getDyeId());
			buffer.writeInt(1);
		}
		
		if (_player.getHenna(4) != null)
		{
			buffer.writeInt(_player.getHenna(4).getDyeId());
			buffer.writeInt(_player.getHenna(4).getDuration()); // Premium Slot Dye Time Left
			buffer.writeInt(_player.getHenna(4).isAllowedClass(_player.getPlayerClass()));
		}
		else
		{
			buffer.writeInt(0); // Premium Slot Dye ID
			buffer.writeInt(0); // Premium Slot Dye Time Left
			buffer.writeInt(0); // Premium Slot Dye ID isValid
		}
	}
}
