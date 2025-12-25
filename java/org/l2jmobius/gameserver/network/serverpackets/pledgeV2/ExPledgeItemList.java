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
package org.l2jmobius.gameserver.network.serverpackets.pledgeV2;

import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.data.holders.ClanShopProductHolder;
import org.l2jmobius.gameserver.data.xml.ClanShopData;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.clan.Clan;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.AbstractItemPacket;

/**
 * @author Mobius
 */
public class ExPledgeItemList extends AbstractItemPacket
{
	private final Player _player;
	
	public ExPledgeItemList(Player player)
	{
		_player = player;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		final Clan clan = _player.getClan();
		if (clan == null)
		{
			return;
		}
		
		ServerPackets.EX_PLEDGE_ITEM_LIST.writeId(this, buffer);
		buffer.writeShort(ClanShopData.getInstance().getProducts().size()); // Product count.
		for (ClanShopProductHolder product : ClanShopData.getInstance().getProducts())
		{
			writeItem(product.getTradeItem(), buffer);
			buffer.writeByte(clan.getLevel() < product.getClanLevel() ? 0 : 2); // 0 locked, 1 need activation, 2 available
			buffer.writeLong(product.getAdena()); // Purchase price: adena
			buffer.writeInt(product.getFame()); // Purchase price: fame
			buffer.writeByte(product.getClanLevel()); // Required pledge level
			buffer.writeByte(0); // Required pledge mastery
			buffer.writeLong(0); // Activation price: adena
			buffer.writeInt(0); // Activation price: reputation
			buffer.writeInt(0); // Time to deactivation
			buffer.writeInt(0); // Time to restock
			buffer.writeShort(0); // Current stock
			buffer.writeShort(0); // Total stock
		}
	}
}
