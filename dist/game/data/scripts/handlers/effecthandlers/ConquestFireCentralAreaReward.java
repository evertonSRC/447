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
package handlers.effecthandlers;

import org.l2jmobius.commons.util.Rnd;
import org.l2jmobius.gameserver.config.ConquestConfig;
import org.l2jmobius.gameserver.managers.ZoneManager;
import org.l2jmobius.gameserver.model.StatSet;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.effects.AbstractEffect;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.model.script.Quest;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.model.zone.ZoneType;
import org.l2jmobius.gameserver.network.SystemMessageId;

/**
 * @author CostyKiller
 */
public class ConquestFireCentralAreaReward extends AbstractEffect
{
	private static final ZoneType FIRE_SOURCE_CENTRAL_AREA_ZONE = ZoneManager.getInstance().getZoneByName("central_area_fire_source");
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	public ConquestFireCentralAreaReward(StatSet params)
	{
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item)
	{
		if (!effected.isPlayable())
		{
			return;
		}
		
		if (effected.isPlayer() && FIRE_SOURCE_CENTRAL_AREA_ZONE.isInsideZone(effected))
		{
			final Player player = effected.asPlayer();
			if (Rnd.get(100) < ConquestConfig.CONQUEST_ABILITY_FIRE_SOURCE_UPGRADE_CHANCE)
			{
				player.getVariables().set(PlayerVariables.CONQUEST_ABILITY_FIRE_SOURCE_EXP, player.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_FIRE_SOURCE_EXP, 0) + ConquestConfig.CONQUEST_ABILITY_FIRE_SOURCE_EXP_AMOUNT);
				player.sendPacket(SystemMessageId.YOU_HAVE_RECEIVED_FIRE_SOURCE_POINTS);
				Quest.showOnScreenMsg(player, "You have received Fire Source points.", 5000);
			}
		}
	}
}
