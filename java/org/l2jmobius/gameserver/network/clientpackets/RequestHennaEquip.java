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
package org.l2jmobius.gameserver.network.clientpackets;

import org.l2jmobius.gameserver.config.GeneralConfig;
import org.l2jmobius.gameserver.config.custom.PremiumSystemConfig;
import org.l2jmobius.gameserver.data.xml.HennaData;
import org.l2jmobius.gameserver.managers.PunishmentManager;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.item.Henna;
import org.l2jmobius.gameserver.model.item.enums.ItemProcessType;
import org.l2jmobius.gameserver.network.PacketLogger;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.ActionFailed;
import org.l2jmobius.gameserver.network.serverpackets.HennaEquipList;
import org.l2jmobius.gameserver.network.serverpackets.InventoryUpdate;

/**
 * @author Mobius
 */
public class RequestHennaEquip extends ClientPacket
{
	private int _symbolId;
	
	@Override
	protected void readImpl()
	{
		_symbolId = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (!getClient().getFloodProtectors().canPerformTransaction())
		{
			return;
		}
		
		final Henna henna = HennaData.getInstance().getHenna(_symbolId);
		if (henna == null)
		{
			PacketLogger.warning("Invalid Henna Id: " + _symbolId + " from " + player);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (henna.isPremium())
		{
			if ((PremiumSystemConfig.PREMIUM_HENNA_SLOT_ENABLED_FOR_ALL || player.hasPremiumStatus()) && PremiumSystemConfig.PREMIUM_HENNA_SLOT_ENABLED && (player.getPlayerClass().level() > 1))
			{
				if (player.getHenna(4) != null)
				{
					player.sendPacket(SystemMessageId.YOU_HAVE_NO_FREE_TATTOO_SLOTS);
					player.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
			}
			else
			{
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
		}
		else if (player.getHennaEmptySlots() == 0)
		{
			player.sendPacket(SystemMessageId.YOU_HAVE_NO_FREE_TATTOO_SLOTS);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final long count = player.getInventory().getInventoryItemCount(henna.getDyeItemId(), -1);
		if (henna.isAllowedClass(player.getPlayerClass()) && (count >= henna.getWearCount()) && (player.getAdena() >= henna.getWearFee()) && player.addHenna(henna))
		{
			player.destroyItemByItemId(ItemProcessType.FEE, henna.getDyeItemId(), henna.getWearCount(), player, true);
			player.getInventory().reduceAdena(ItemProcessType.FEE, henna.getWearFee(), player, player.getLastFolkNPC());
			final InventoryUpdate iu = new InventoryUpdate();
			iu.addModifiedItem(player.getInventory().getAdenaInstance());
			player.sendInventoryUpdate(iu);
			player.sendPacket(new HennaEquipList(player));
			player.updateSymbolSealSkills();
			player.sendPacket(SystemMessageId.A_TATTOO_IS_ADDED);
		}
		else
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_MAKE_A_TATTOO);
			if (!player.isGM() && !henna.isAllowedClass(player.getPlayerClass()))
			{
				PunishmentManager.handleIllegalPlayerAction(player, "Exploit attempt: Character " + player.getName() + " of account " + player.getAccountName() + " tryed to add a forbidden henna.", GeneralConfig.DEFAULT_PUNISH);
			}
			
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
	}
}
