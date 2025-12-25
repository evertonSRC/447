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

import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.data.SpawnTable;
import org.l2jmobius.gameserver.managers.ItemAuctionManager;
import org.l2jmobius.gameserver.model.Spawn;
import org.l2jmobius.gameserver.model.itemauction.ItemAuction;
import org.l2jmobius.gameserver.model.itemauction.ItemAuctionInstance;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * @author Index, Gaikotsu
 */
public class ExItemAuctionStatus extends ServerPacket
{
	private static final int AUCTION_MANAGER = 34328;
	
	private int _x = 0;
	private int _y = 0;
	private int _z = 0;
	private int _status = 0;
	
	public ExItemAuctionStatus()
	{
		final Spawn spawn = SpawnTable.getInstance().getAnySpawn(AUCTION_MANAGER);
		if (spawn != null)
		{
			_x = spawn.getX();
			_y = spawn.getY();
			_z = spawn.getZ();
			final ItemAuctionInstance manager = ItemAuctionManager.getInstance().getManagerInstance(AUCTION_MANAGER);
			if (manager != null)
			{
				final ItemAuction auction = manager.getCurrentAuction();
				if (auction != null)
				{
					_status = 1;
				}
			}
		}
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_ITEM_AUCTION_STATUS.writeId(this, buffer);
		buffer.writeInt(_x);
		buffer.writeInt(_y);
		buffer.writeInt(_z);
		buffer.writeInt(0);
		buffer.writeShort(_status);
	}
}
