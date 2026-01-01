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
package org.l2jmobius.gameserver.model.stats.finalizers;

import java.util.OptionalDouble;

import org.l2jmobius.gameserver.config.PlayerConfig;
import org.l2jmobius.gameserver.data.xml.ItemData;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.holders.player.VirtualEquippedItem;
import org.l2jmobius.gameserver.model.item.ItemTemplate;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.model.itemcontainer.Inventory;
import org.l2jmobius.gameserver.model.stats.IStatFunction;
import org.l2jmobius.gameserver.model.stats.Stat;

/**
 * @author Mobius
 */
public class MaxStaminaFinalizer implements IStatFunction
{
	@Override
	public double calc(Creature creature, OptionalDouble base, Stat stat)
	{
		throwIfPresent(base);
		
		double baseValue = creature.getTemplate().getBaseValue(stat, 0);
		if (creature.isPlayer())
		{
			final Player player = creature.asPlayer();
			if (player != null)
			{
				baseValue = PlayerConfig.BASE_STAMINA + (PlayerConfig.STAMINA_PER_LEVEL * player.getLevel());
			}
		}
		
		return defaultValue(creature, stat, baseValue);
	}
	
	private static double defaultValue(Creature creature, Stat stat, double baseValue)
	{
		final double mul = creature.getStat().getMul(stat);
		final double add = creature.getStat().getAdd(stat);
		double addItem = 0;
		
		final Inventory inv = creature.getInventory();
		if (inv != null)
		{
			for (Item item : inv.getPaperdollItems())
			{
				addItem += item.getTemplate().getStats(stat, 0);
			}
		}
		
		if (creature.isPlayer())
		{
			final Player player = creature.asPlayer();
			for (VirtualEquippedItem virtualItem : player.getVirtualEquipmentItems())
			{
				final ItemTemplate template = ItemData.getInstance().getTemplate(virtualItem.getItemId());
				if (template != null)
				{
					addItem += template.getStats(stat, 0);
				}
			}
		}
		
		return (mul * baseValue) + add + addItem + creature.getStat().getMoveTypeValue(stat, creature.getMoveType());
	}
}
