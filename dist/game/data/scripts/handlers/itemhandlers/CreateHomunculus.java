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
package handlers.itemhandlers;

import java.util.ArrayList;
import java.util.List;

import org.l2jmobius.commons.util.Rnd;
import org.l2jmobius.gameserver.config.PlayerConfig;
import org.l2jmobius.gameserver.data.xml.HomunculusCreationData;
import org.l2jmobius.gameserver.data.xml.HomunculusData;
import org.l2jmobius.gameserver.handler.IItemHandler;
import org.l2jmobius.gameserver.model.actor.Playable;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.homunculus.Homunculus;
import org.l2jmobius.gameserver.model.homunculus.HomunculusTemplate;
import org.l2jmobius.gameserver.model.item.enums.ItemProcessType;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.homunculus.ExHomunculusSummonResult;
import org.l2jmobius.gameserver.network.serverpackets.homunculus.ExShowHomunculusBirthInfo;
import org.l2jmobius.gameserver.network.serverpackets.homunculus.ExShowHomunculusCouponUi;
import org.l2jmobius.gameserver.network.serverpackets.homunculus.ExShowHomunculusList;

/**
 * @author Liamxroy, Mobius
 */
public class CreateHomunculus implements IItemHandler
{
	@Override
	public boolean onItemUse(Playable playable, Item item, boolean forceUse)
	{
		if (!playable.isPlayer())
		{
			playable.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_THIS_ITEM);
			return false;
		}
		
		final Player player = playable.asPlayer();
		final int size = player.getHomunculusList().size();
		if (size >= PlayerConfig.MAX_HOMUNCULUS_COUNT)
		{
			player.sendMessage("There's no available slot for Homunculus!");
			return false;
		}
		
		double chance = 0.00;
		switch (item.getId())
		{
			case 81808: // Common Homunculus' Hourglass
			{
				chance = 7.00;
				break;
			}
			case 81809: // High-grade Homunculus' Hourglass
			{
				chance = 2.99;
				break;
			}
			case 81810: // Top-grade Homunculus' Hourglass
			{
				chance = 0.01;
				break;
			}
		}
		
		final List<Integer> ids = new ArrayList<>();
		for (Double[] itemChances : HomunculusCreationData.getInstance().getDefaultTemplate().getCreationChance())
		{
			if (itemChances[1] == chance)
			{
				ids.add(itemChances[0].intValue());
			}
			
			if ((item.getId() == 81810 /* Top-grade Homunculus' Hourglass */) && (itemChances[1] != 7.00))
			{
				ids.add(itemChances[0].intValue());
			}
		}
		
		final int homunculusId = ids.get(Rnd.get(ids.size()));
		final HomunculusTemplate template = HomunculusData.getInstance().getTemplate(homunculusId);
		if (template == null)
		{
			LOGGER.warning("CreateHomunculus: Could not find Homunculus template " + homunculusId + "!");
			return false;
		}
		
		player.destroyItem(ItemProcessType.FEE, item, player, true);
		
		final Homunculus homunculus = new Homunculus(template, player.getHomunculusList().size(), 1, 0, 0, 0, 0, 0, 0, false);
		if (player.getHomunculusList().add(homunculus))
		{
			player.getVariables().set(PlayerVariables.HOMUNCULUS_CREATION_TIME, 0);
			player.getVariables().set(PlayerVariables.HOMUNCULUS_HP_POINTS, 0);
			player.getVariables().set(PlayerVariables.HOMUNCULUS_SP_POINTS, 0);
			player.getVariables().set(PlayerVariables.HOMUNCULUS_VP_POINTS, 0);
			player.sendPacket(new ExShowHomunculusBirthInfo(player));
			player.sendPacket(new ExHomunculusSummonResult(1));
			player.sendPacket(SystemMessageId.A_HOMUNCULUS_SLOT_IS_UNLOCKED);
			player.calculateHomunculusSlots();
		}
		
		player.sendPacket(new ExShowHomunculusCouponUi());
		player.broadcastUserInfo();
		player.sendPacket(new ExShowHomunculusList(player));
		return true;
	}
}
