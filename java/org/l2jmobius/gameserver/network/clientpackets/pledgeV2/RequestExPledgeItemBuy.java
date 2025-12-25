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
package org.l2jmobius.gameserver.network.clientpackets.pledgeV2;

import org.l2jmobius.gameserver.data.holders.ClanShopProductHolder;
import org.l2jmobius.gameserver.data.xml.ClanShopData;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.clan.Clan;
import org.l2jmobius.gameserver.model.item.enums.ItemProcessType;
import org.l2jmobius.gameserver.network.clientpackets.ClientPacket;
import org.l2jmobius.gameserver.network.serverpackets.pledgeV2.ExPledgeItemBuy;

/**
 * @author Mobius
 */
public class RequestExPledgeItemBuy extends ClientPacket
{
	private int _itemId;
	private int _count;
	
	@Override
	protected void readImpl()
	{
		_itemId = readInt();
		_count = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		final Clan clan = player.getClan();
		if (clan == null)
		{
			player.sendPacket(new ExPledgeItemBuy(1));
			return;
		}
		
		if ((_count < 1) || (_count > 10000))
		{
			player.sendPacket(new ExPledgeItemBuy(1));
			return;
		}
		
		final ClanShopProductHolder product = ClanShopData.getInstance().getProduct(_itemId);
		if (product == null)
		{
			player.sendPacket(new ExPledgeItemBuy(1));
			return;
		}
		
		if (clan.getLevel() < product.getClanLevel())
		{
			player.sendPacket(new ExPledgeItemBuy(2));
			return;
		}
		
		final long slots = product.getTradeItem().getItem().isStackable() ? 1 : product.getTradeItem().getCount() * _count;
		final long weight = product.getTradeItem().getItem().getWeight() * product.getTradeItem().getCount() * _count;
		if (!player.getInventory().validateWeight(weight) || !player.getInventory().validateCapacity(slots))
		{
			player.sendPacket(new ExPledgeItemBuy(3));
			return;
		}
		
		if ((player.getAdena() < (product.getAdena() * _count)) || (player.getFame() < (product.getFame() * _count)))
		{
			player.sendPacket(new ExPledgeItemBuy(3));
			return;
		}
		
		if (product.getAdena() > 0)
		{
			player.reduceAdena(ItemProcessType.FEE, product.getAdena() * _count, player, true);
		}
		
		if (product.getFame() > 0)
		{
			player.setFame(player.getFame() - (product.getFame() * _count));
			player.broadcastUserInfo();
		}
		
		player.addItem(ItemProcessType.BUY, _itemId, product.getCount() * _count, player, true);
		player.sendPacket(new ExPledgeItemBuy(0));
	}
}
