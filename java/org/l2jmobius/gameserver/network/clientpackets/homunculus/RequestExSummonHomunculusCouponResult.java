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
package org.l2jmobius.gameserver.network.clientpackets.homunculus;

import org.l2jmobius.commons.util.Rnd;
import org.l2jmobius.gameserver.data.xml.HomunculusCreationData;
import org.l2jmobius.gameserver.data.xml.HomunculusData;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.homunculus.Homunculus;
import org.l2jmobius.gameserver.model.homunculus.HomunculusCreationTemplate;
import org.l2jmobius.gameserver.model.homunculus.HomunculusTemplate;
import org.l2jmobius.gameserver.model.item.enums.ItemProcessType;
import org.l2jmobius.gameserver.model.item.holders.ItemHolder;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.network.PacketLogger;
import org.l2jmobius.gameserver.network.clientpackets.ClientPacket;
import org.l2jmobius.gameserver.network.serverpackets.homunculus.ExShowHomunculusBirthInfo;
import org.l2jmobius.gameserver.network.serverpackets.homunculus.ExShowHomunculusList;
import org.l2jmobius.gameserver.network.serverpackets.homunculus.ExSummonHomunculusCouponResult;

/**
 * @author NasSeKa, Manax
 */
public class RequestExSummonHomunculusCouponResult extends ClientPacket
{
	private int _itemId;
	
	@Override
	protected void readImpl()
	{
		_itemId = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (player.getHomunculusList().size() == player.getAvailableHomunculusSlotCount())
		{
			player.sendPacket(new ExSummonHomunculusCouponResult(0, 0));
			PacketLogger.info("Player " + player.getObjectId() + " " + player.getName() + ", trying create homunculus withouts avaible slots!");
			return;
		}
		
		final HomunculusCreationTemplate creationTemplate = HomunculusCreationData.getInstance().getTemplateByItemId(_itemId);
		if ((creationTemplate == null) || creationTemplate.getItemFee().isEmpty())
		{
			PacketLogger.info("Player " + player.getObjectId() + " " + player.getName() + ", trying create homunculus with not existing coupon!");
			return;
		}
		
		// Take items.
		for (ItemHolder itemHolder : creationTemplate.getItemFee())
		{
			final Item item = player.getInventory().getItemByItemId(itemHolder.getId());
			if ((item == null) || (item.getCount() < itemHolder.getCount()))
			{
				return;
			}
		}
		
		for (ItemHolder itemHolder : creationTemplate.getItemFee())
		{
			if (!player.destroyItemByItemId(ItemProcessType.FEE, itemHolder.getId(), itemHolder.getCount(), player, true))
			{
				PacketLogger.info("Player " + player.getObjectId() + " " + player.getName() + ", trying create homunculus without " + itemHolder + "!");
				return;
			}
		}
		
		double chance = Rnd.get(100.0);
		double current = 0;
		int homunculusId = 0;
		while (homunculusId == 0)
		{
			if (chance > creationTemplate.getMaxChance())
			{
				player.sendMessage("Homunculus is not created!");
				player.sendPacket(new ExSummonHomunculusCouponResult(0, 0));
				return;
			}
			
			for (Double[] homuHolder : creationTemplate.getCreationChance())
			{
				current += homuHolder[1];
				if (current >= chance)
				{
					homunculusId = homuHolder[0].intValue();
					break;
				}
			}
		}
		
		final HomunculusTemplate template = HomunculusData.getInstance().getTemplate(homunculusId);
		if (template == null)
		{
			PacketLogger.warning("Could not find Homunculus template " + homunculusId + ".");
			return;
		}
		
		final Homunculus homunculus = new Homunculus(template, player.getHomunculusList().size(), 1, 0, 0, 0, 0, 0, 0, false);
		if (player.getHomunculusList().add(homunculus))
		{
			player.sendPacket(new ExShowHomunculusBirthInfo(player));
			player.sendPacket(new ExShowHomunculusList(player));
			player.sendPacket(new ExSummonHomunculusCouponResult(1, homunculus.getSlot()));
		}
	}
}
