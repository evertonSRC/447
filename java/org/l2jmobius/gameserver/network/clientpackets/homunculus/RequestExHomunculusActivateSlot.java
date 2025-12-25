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

import java.util.List;
import java.util.logging.Logger;

import org.l2jmobius.gameserver.data.xml.HomunculusSlotData;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.homunculus.HomunculusSlotTemplate;
import org.l2jmobius.gameserver.model.item.enums.ItemProcessType;
import org.l2jmobius.gameserver.model.item.holders.ItemHolder;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.network.PacketLogger;
import org.l2jmobius.gameserver.network.clientpackets.ClientPacket;
import org.l2jmobius.gameserver.network.serverpackets.homunculus.ExActivateHomunculusResult;
import org.l2jmobius.gameserver.network.serverpackets.homunculus.ExHomunculusPointInfo;
import org.l2jmobius.gameserver.network.serverpackets.homunculus.ExShowHomunculusList;

/**
 * @author Manax
 */
public class RequestExHomunculusActivateSlot extends ClientPacket
{
	private int _slot;
	
	@Override
	protected void readImpl()
	{
		_slot = readInt();
		// _activate = readByte() == 1; // enabled?
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		final int size = player.getHomunculusList().size();
		final HomunculusSlotTemplate template = HomunculusSlotData.getInstance().getTemplate(_slot);
		if ((size != 0) && ((player.getHomunculusList().get(_slot) != null) || (_slot == player.getAvailableHomunculusSlotCount())))
		{
			PacketLogger.info(getClass().getSimpleName() + " player " + player.getName() + " " + player.getObjectId() + " trying unlock already unlocked slot!");
			player.sendPacket(new ExActivateHomunculusResult(false));
			return;
		}
		
		if (!template.getSlotEnabled())
		{
			Logger.getLogger(getClass().getSimpleName() + " player " + player.getName() + " " + player.getObjectId() + " trying unlock disabled slot!");
			player.sendPacket(new ExActivateHomunculusResult(false));
			return;
		}
		
		final List<ItemHolder> fee = template.getPrice();
		for (ItemHolder feeHolder : fee)
		{
			if ((player.getInventory().getItemByItemId(feeHolder.getId()) == null) || ((player.getInventory().getItemByItemId(feeHolder.getId()) != null) && (player.getInventory().getItemByItemId(feeHolder.getId()).getCount() < feeHolder.getCount())))
			{
				player.sendPacket(new ExActivateHomunculusResult(false));
				return;
			}
		}
		
		for (ItemHolder feeHolder : fee)
		{
			if (!player.destroyItemByItemId(ItemProcessType.FEE, feeHolder.getId(), feeHolder.getCount(), player, true))
			{
				Logger.getLogger(getClass().getSimpleName() + " player " + player.getName() + " " + player.getObjectId() + " trying unlock slot without items!");
				player.sendPacket(new ExActivateHomunculusResult(false));
				return;
			}
		}
		
		player.broadcastUserInfo();
		player.getVariables().set(PlayerVariables.HOMUNCULUS_OPENED_SLOT_COUNT, _slot);
		player.sendPacket(new ExHomunculusPointInfo(player));
		player.sendPacket(new ExShowHomunculusList(player));
		player.sendPacket(new ExActivateHomunculusResult(true));
	}
}
