/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2jmobius.gameserver.network.serverpackets.alchemy;

import java.util.ArrayList;
import java.util.List;

import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.enums.TryMixCubeType;
import org.l2jmobius.gameserver.network.holders.AlchemyResult;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Sdw
 */
public class ExTryMixCube extends ServerPacket
{
	private final TryMixCubeType _type;
	private final List<AlchemyResult> _items = new ArrayList<>();
	
	public ExTryMixCube(TryMixCubeType type)
	{
		_type = type;
	}
	
	public void addItem(AlchemyResult item)
	{
		_items.add(item);
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_TRY_MIX_CUBE.writeId(this, buffer);
		buffer.writeByte(_type.ordinal());
		buffer.writeInt(_items.size());
		for (AlchemyResult holder : _items)
		{
			buffer.writeByte(holder.getType().ordinal());
			buffer.writeInt(holder.getId());
			buffer.writeLong(holder.getCount());
		}
	}
}
