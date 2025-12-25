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
import org.l2jmobius.gameserver.model.homunculus.HomunculusTemplate;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.network.PacketLogger;
import org.l2jmobius.gameserver.network.clientpackets.ClientPacket;
import org.l2jmobius.gameserver.network.serverpackets.homunculus.ExHomunculusSummonResult;
import org.l2jmobius.gameserver.network.serverpackets.homunculus.ExShowHomunculusBirthInfo;
import org.l2jmobius.gameserver.network.serverpackets.homunculus.ExShowHomunculusList;

/**
 * @author Mobius
 */
public class RequestExHomunculusSummon extends ClientPacket
{
	@Override
	protected void readImpl()
	{
		// readByte();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		final int hpPoints = player.getVariables().getInt(PlayerVariables.HOMUNCULUS_HP_POINTS, 0);
		final int spPoints = player.getVariables().getInt(PlayerVariables.HOMUNCULUS_SP_POINTS, 0);
		final int vpPoints = player.getVariables().getInt(PlayerVariables.HOMUNCULUS_VP_POINTS, 0);
		final int homunculusCreateTime = (int) (player.getVariables().getLong(PlayerVariables.HOMUNCULUS_CREATION_TIME, 0) / 1000);
		
		if ((homunculusCreateTime > 0) && ((System.currentTimeMillis() / 1000) >= homunculusCreateTime) && (hpPoints == HomunculusCreationData.getInstance().getDefaultTemplate().getHPFeeCount()) && (spPoints == HomunculusCreationData.getInstance().getDefaultTemplate().getSPFeeCount()) && (vpPoints == HomunculusCreationData.getInstance().getDefaultTemplate().getVPFeeCount()))
		{
			double chance = Rnd.get(100.0);
			double current = 0;
			int homunculusId = 0;
			while (homunculusId == 0)
			{
				if (chance > HomunculusCreationData.getInstance().getDefaultTemplate().getMaxChance())
				{
					player.sendMessage("Homunculus is not created!");
					player.sendPacket(new ExHomunculusSummonResult(0));
					return;
				}
				
				for (Double[] homuHolder : HomunculusCreationData.getInstance().getDefaultTemplate().getCreationChance())
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
				PacketLogger.warning("Counld not find Homunculus template " + homunculusId + ".");
				return;
			}
			
			final Homunculus homunculus = new Homunculus(template, player.getHomunculusList().size(), 1, 0, 0, 0, 0, 0, 0, false);
			if (player.getHomunculusList().add(homunculus))
			{
				player.getVariables().set(PlayerVariables.HOMUNCULUS_CREATION_TIME, 0);
				player.getVariables().set(PlayerVariables.HOMUNCULUS_HP_POINTS, 0);
				player.getVariables().set(PlayerVariables.HOMUNCULUS_SP_POINTS, 0);
				player.getVariables().set(PlayerVariables.HOMUNCULUS_VP_POINTS, 0);
				player.sendPacket(new ExShowHomunculusBirthInfo(player));
				player.sendPacket(new ExHomunculusSummonResult(1));
				player.sendPacket(new ExShowHomunculusList(player));
			}
		}
	}
}
