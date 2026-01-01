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

import org.l2jmobius.gameserver.data.xml.ItemData;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.holders.player.VirtualEquippedItem;
import org.l2jmobius.gameserver.model.actor.enums.creature.AttributeType;
import org.l2jmobius.gameserver.model.item.enchant.attribute.AttributeHolder;
import org.l2jmobius.gameserver.model.item.ItemTemplate;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.model.itemcontainer.Inventory;
import org.l2jmobius.gameserver.model.stats.IStatFunction;
import org.l2jmobius.gameserver.model.stats.Stat;

/**
 * @author UnAfraid
 */
public class AttributeFinalizer implements IStatFunction
{
	private final AttributeType _type;
	private final boolean _isWeapon;
	
	public AttributeFinalizer(AttributeType type, boolean isWeapon)
	{
		_type = type;
		_isWeapon = isWeapon;
	}
	
	@Override
	public double calc(Creature creature, OptionalDouble base, Stat stat)
	{
		throwIfPresent(base);
		
		double baseValue = creature.getTemplate().getBaseValue(stat, 0);
		if (creature.isPlayable())
		{
			if (_isWeapon)
			{
				final Item weapon = creature.getActiveWeaponInstance();
				if (weapon != null)
				{
					final AttributeHolder weaponInstanceHolder = weapon.getAttribute(_type);
					if (weaponInstanceHolder != null)
					{
						baseValue += weaponInstanceHolder.getValue();
					}
					
					final AttributeHolder weaponHolder = weapon.getTemplate().getAttribute(_type);
					if (weaponHolder != null)
					{
						baseValue += weaponHolder.getValue();
					}
				}
				
				if (creature.isPlayer())
				{
					final Player player = creature.asPlayer();
					for (VirtualEquippedItem virtualItem : player.getVirtualEquipmentItems())
					{
						final ItemTemplate template = ItemData.getInstance().getTemplate(virtualItem.getItemId());
						if ((template == null) || !template.isWeapon())
						{
							continue;
						}
						
						final AttributeHolder weaponHolder = template.getAttribute(_type);
						if (weaponHolder != null)
						{
							baseValue += weaponHolder.getValue();
						}
					}
				}
			}
			else
			{
				final Inventory inventory = creature.getInventory();
				if (inventory != null)
				{
					for (Item item : inventory.getPaperdollItems(Item::isArmor))
					{
						final AttributeHolder weaponInstanceHolder = item.getAttribute(_type);
						if (weaponInstanceHolder != null)
						{
							baseValue += weaponInstanceHolder.getValue();
						}
						
						final AttributeHolder weaponHolder = item.getTemplate().getAttribute(_type);
						if (weaponHolder != null)
						{
							baseValue += weaponHolder.getValue();
						}
					}
				}
				
				if (creature.isPlayer())
				{
					final Player player = creature.asPlayer();
					for (VirtualEquippedItem virtualItem : player.getVirtualEquipmentItems())
					{
						final ItemTemplate template = ItemData.getInstance().getTemplate(virtualItem.getItemId());
						if ((template == null) || !template.isArmor())
						{
							continue;
						}
						
						final AttributeHolder armorHolder = template.getAttribute(_type);
						if (armorHolder != null)
						{
							baseValue += armorHolder.getValue();
						}
					}
				}
			}
		}
		
		return Stat.defaultValue(creature, stat, baseValue);
	}
}
