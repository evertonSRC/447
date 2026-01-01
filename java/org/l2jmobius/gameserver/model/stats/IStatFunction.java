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
package org.l2jmobius.gameserver.model.stats;

import java.util.OptionalDouble;

import org.l2jmobius.gameserver.config.OlympiadConfig;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.instance.Pet;
import org.l2jmobius.gameserver.model.actor.transform.Transform;
import org.l2jmobius.gameserver.data.xml.ItemData;
import org.l2jmobius.gameserver.model.item.ItemTemplate;
import org.l2jmobius.gameserver.model.item.enums.BodyPart;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.model.item.type.CrystalType;
import org.l2jmobius.gameserver.model.item.type.WeaponType;
import org.l2jmobius.gameserver.model.itemcontainer.Inventory;
import org.l2jmobius.gameserver.model.actor.holders.player.VirtualEquippedItem;

/**
 * @author UnAfraid
 */
@FunctionalInterface
public interface IStatFunction
{
	default void throwIfPresent(OptionalDouble base)
	{
		if (base.isPresent())
		{
			throw new IllegalArgumentException("base should not be set for " + getClass().getSimpleName());
		}
	}
	
	default double calcEnchantBodyPart(Creature creature, BodyPart... bodyParts)
	{
		double value = 0;
		final Player player = creature.isPlayer() ? creature.asPlayer() : null;
		for (BodyPart bodyPart : bodyParts)
		{
			final Item item = creature.getInventory().getPaperdollItemByBodyPart(bodyPart);
			if ((item != null) && (item.getEnchantLevel() >= 4) && ((item.getTemplate().getCrystalTypePlus() == CrystalType.R) || (item.getTemplate().getCrystalTypePlus() == CrystalType.L)))
			{
				value += calcEnchantBodyPartBonus(item.getEnchantLevel(), item.getTemplate().isBlessed());
			}
			
			if (player != null)
			{
				for (VirtualEquippedItem virtualItem : player.getVirtualEquipmentItems())
				{
					final ItemTemplate template = ItemData.getInstance().getTemplate(virtualItem.getItemId());
					if ((template == null) || (template.getBodyPart() != bodyPart))
					{
						continue;
					}
					
					final int enchantLevel = player.getVirtualItemEnchantLevel(template, virtualItem.getEnchant());
					if ((enchantLevel >= 4) && ((template.getCrystalTypePlus() == CrystalType.R) || (template.getCrystalTypePlus() == CrystalType.L)))
					{
						value += calcEnchantBodyPartBonus(enchantLevel, template.isBlessed());
					}
				}
			}
		}
		
		return value;
	}
	
	default double calcEnchantBodyPartBonus(int enchantLevel, boolean isBlessed)
	{
		return 0;
	}
	
	default double calcWeaponBaseValue(Creature creature, Stat stat)
	{
		final double baseTemplateValue = creature.getTemplate().getBaseValue(stat, 0);
		final Transform transform = creature.getTransformation();
		double baseValue = transform == null ? baseTemplateValue : transform.getStats(creature, stat, baseTemplateValue);
		if (creature.isPet())
		{
			final Pet pet = creature.asPet();
			final Item weapon = pet.getActiveWeaponInstance();
			final double baseVal = stat == Stat.PHYSICAL_ATTACK ? pet.getPetLevelData().getPetPAtk() : stat == Stat.MAGIC_ATTACK ? pet.getPetLevelData().getPetMAtk() : baseTemplateValue;
			baseValue = baseVal + (weapon != null ? weapon.getTemplate().getStats(stat, baseVal) : 0);
		}
		else if (creature.isPlayer() && ((transform == null) || transform.canUseWeaponStats()))
		{
			final Item weapon = creature.getActiveWeaponInstance();
			baseValue = (weapon != null ? weapon.getTemplate().getStats(stat, baseTemplateValue) : baseTemplateValue);
		}
		
		if (creature.isPlayer())
		{
			baseValue += creature.asPlayer().getVirtualItemStats(stat);
		}
		
		return baseValue;
	}
	
	default double calcWeaponPlusBaseValue(Creature creature, Stat stat)
	{
		final double baseTemplateValue = creature.getTemplate().getBaseValue(stat, 0);
		final Transform transform = creature.getTransformation();
		double baseValue = (transform != null) && !transform.isStance() ? transform.getStats(creature, stat, baseTemplateValue) : baseTemplateValue;
		if (creature.isPlayable())
		{
			final Inventory inv = creature.getInventory();
			if (inv != null)
			{
				baseValue += inv.getPaperdollCache().getStats(stat);
				if (creature.isPlayer())
				{
					baseValue += creature.asPlayer().getVirtualItemStats(stat);
				}
			}
		}
		
		return baseValue;
	}
	
	default double calcEnchantedItemBonus(Creature creature, Stat stat)
	{
		if (!creature.isPlayer())
		{
			return 0;
		}
		
		double value = 0;
		final Player player = creature.asPlayer();
		for (Item equippedItem : creature.getInventory().getPaperdollItems(Item::isEnchanted))
		{
			final ItemTemplate item = equippedItem.getTemplate();
			final BodyPart bodyPart = item.getBodyPart();
			if ((bodyPart == BodyPart.HAIR) || //
				(bodyPart == BodyPart.HAIR2) || //
				(bodyPart == BodyPart.HAIRALL))
			{
				// TODO: Item after enchant shows pDef, but scroll says mDef increase.
				if ((stat != Stat.PHYSICAL_DEFENCE) && (stat != Stat.MAGICAL_DEFENCE))
				{
					continue;
				}
			}
			else if (item.getStats(stat, 0) <= 0)
			{
				continue;
			}
			
			final double blessedBonus = item.isBlessed() ? 1.5 : 1;
			int enchant = equippedItem.getEnchantLevel();
			
			if (creature.asPlayer().isInOlympiadMode())
			{
				if (item.isWeapon())
				{
					if ((OlympiadConfig.OLYMPIAD_WEAPON_ENCHANT_LIMIT >= 0) && (enchant > OlympiadConfig.OLYMPIAD_WEAPON_ENCHANT_LIMIT))
					{
						enchant = OlympiadConfig.OLYMPIAD_WEAPON_ENCHANT_LIMIT;
					}
				}
				else
				{
					if ((OlympiadConfig.OLYMPIAD_ARMOR_ENCHANT_LIMIT >= 0) && (enchant > OlympiadConfig.OLYMPIAD_ARMOR_ENCHANT_LIMIT))
					{
						enchant = OlympiadConfig.OLYMPIAD_ARMOR_ENCHANT_LIMIT;
					}
				}
			}
			
			if ((stat == Stat.MAGICAL_DEFENCE) || (stat == Stat.PHYSICAL_DEFENCE))
			{
				value += calcEnchantDefBonus(equippedItem, blessedBonus, enchant);
			}
			else if (stat == Stat.MAGIC_ATTACK)
			{
				value += calcEnchantMatkBonus(equippedItem, blessedBonus, enchant);
			}
			else if ((stat == Stat.PHYSICAL_ATTACK) && equippedItem.isWeapon())
			{
				value += calcEnchantedPAtkBonus(equippedItem, blessedBonus, enchant);
			}
		}
		
		for (VirtualEquippedItem virtualItem : player.getVirtualEquipmentItems())
		{
			final ItemTemplate template = ItemData.getInstance().getTemplate(virtualItem.getItemId());
			if (template == null)
			{
				continue;
			}
			
			final BodyPart bodyPart = template.getBodyPart();
			if ((bodyPart == BodyPart.HAIR) || //
				(bodyPart == BodyPart.HAIR2) || //
				(bodyPart == BodyPart.HAIRALL))
			{
				if ((stat != Stat.PHYSICAL_DEFENCE) && (stat != Stat.MAGICAL_DEFENCE))
				{
					continue;
				}
			}
			else if (template.getStats(stat, 0) <= 0)
			{
				continue;
			}
			
			final int enchant = player.getVirtualItemEnchantLevel(template, virtualItem.getEnchant());
			if (enchant <= 0)
			{
				continue;
			}
			
			final double blessedBonus = template.isBlessed() ? 1.5 : 1;
			if ((stat == Stat.MAGICAL_DEFENCE) || (stat == Stat.PHYSICAL_DEFENCE))
			{
				value += calcEnchantDefBonus(template, blessedBonus, enchant);
			}
			else if (stat == Stat.MAGIC_ATTACK)
			{
				value += calcEnchantMatkBonus(template, blessedBonus, enchant);
			}
			else if ((stat == Stat.PHYSICAL_ATTACK) && template.isWeapon())
			{
				value += calcEnchantedPAtkBonus(template, blessedBonus, enchant);
			}
		}
		
		return value;
	}
	
	/**
	 * @param item
	 * @param blessedBonus
	 * @param enchant
	 * @return
	 */
	static double calcEnchantDefBonus(Item item, double blessedBonus, int enchant)
	{
		switch (item.getTemplate().getCrystalTypePlus())
		{
			case L:
			case R:
			{
				return ((2 * blessedBonus * enchant) + (6 * blessedBonus * Math.max(0, enchant - 3)));
			}
			default:
			{
				return enchant + (3 * Math.max(0, enchant - 3));
			}
		}
	}

	static double calcEnchantDefBonus(ItemTemplate item, double blessedBonus, int enchant)
	{
		switch (item.getCrystalTypePlus())
		{
			case L:
			case R:
			{
				return ((2 * blessedBonus * enchant) + (6 * blessedBonus * Math.max(0, enchant - 3)));
			}
			default:
			{
				return enchant + (3 * Math.max(0, enchant - 3));
			}
		}
	}
	
	/**
	 * @param item
	 * @param blessedBonus
	 * @param enchant
	 * @return
	 */
	static double calcEnchantMatkBonus(Item item, double blessedBonus, int enchant)
	{
		switch (item.getTemplate().getCrystalTypePlus())
		{
			case L:
			case R:
			{
				return ((5 * blessedBonus * enchant) + (10 * blessedBonus * Math.max(0, enchant - 3)));
			}
			case S:
			{
				// M. Atk. increases by 4 for all weapons.
				// Starting at +4, M. Atk. bonus double.
				return (4 * enchant) + (8 * Math.max(0, enchant - 3));
			}
			case A:
			case B:
			case C:
			{
				// M. Atk. increases by 3 for all weapons.
				// Starting at +4, M. Atk. bonus double.
				return (3 * enchant) + (6 * Math.max(0, enchant - 3));
			}
			default:
			{
				// M. Atk. increases by 2 for all weapons. Starting at +4, M. Atk. bonus double.
				// Starting at +4, M. Atk. bonus double.
				return (2 * enchant) + (4 * Math.max(0, enchant - 3));
			}
		}
	}

	static double calcEnchantMatkBonus(ItemTemplate item, double blessedBonus, int enchant)
	{
		switch (item.getCrystalTypePlus())
		{
			case L:
			case R:
			{
				return ((5 * blessedBonus * enchant) + (10 * blessedBonus * Math.max(0, enchant - 3)));
			}
			case S:
			{
				return (4 * enchant) + (8 * Math.max(0, enchant - 3));
			}
			case A:
			case B:
			case C:
			{
				return (3 * enchant) + (6 * Math.max(0, enchant - 3));
			}
			default:
			{
				return (2 * enchant) + (4 * Math.max(0, enchant - 3));
			}
		}
	}
	
	/**
	 * @param item
	 * @param blessedBonus
	 * @param enchant
	 * @return
	 */
	static double calcEnchantedPAtkBonus(Item item, double blessedBonus, int enchant)
	{
		switch (item.getTemplate().getCrystalTypePlus())
		{
			case L:
			case R:
			{
				if ((item.getWeaponItem().getBodyPart() == BodyPart.LR_HAND) && (item.getWeaponItem().getItemType() != WeaponType.POLE))
				{
					if (item.getWeaponItem().getItemType().isRanged())
					{
						return (12 * blessedBonus * enchant) + (24 * blessedBonus * Math.max(0, enchant - 3));
					}
					
					return (7 * blessedBonus * enchant) + (14 * blessedBonus * Math.max(0, enchant - 3));
				}
				
				return (6 * blessedBonus * enchant) + (12 * blessedBonus * Math.max(0, enchant - 3));
			}
			case S:
			{
				if ((item.getWeaponItem().getBodyPart() == BodyPart.LR_HAND) && (item.getWeaponItem().getItemType() != WeaponType.POLE))
				{
					if (item.getWeaponItem().getItemType().isRanged())
					{
						// P. Atk. increases by 10 for bows.
						// Starting at +4, P. Atk. bonus double.
						return (10 * enchant) + (20 * Math.max(0, enchant - 3));
					}
					
					// P. Atk. increases by 6 for two-handed swords, two-handed blunts, dualswords, and two-handed combat weapons.
					// Starting at +4, P. Atk. bonus double.
					return (6 * enchant) + (12 * Math.max(0, enchant - 3));
				}
				
				// P. Atk. increases by 5 for one-handed swords, one-handed blunts, daggers, spears, and other weapons.
				// Starting at +4, P. Atk. bonus double.
				return (5 * enchant) + (10 * Math.max(0, enchant - 3));
			}
			case A:
			{
				if ((item.getWeaponItem().getBodyPart() == BodyPart.LR_HAND) && (item.getWeaponItem().getItemType() != WeaponType.POLE))
				{
					if (item.getWeaponItem().getItemType().isRanged())
					{
						// P. Atk. increases by 8 for bows.
						// Starting at +4, P. Atk. bonus double.
						return (8 * enchant) + (16 * Math.max(0, enchant - 3));
					}
					
					// P. Atk. increases by 5 for two-handed swords, two-handed blunts, dualswords, and two-handed combat weapons.
					// Starting at +4, P. Atk. bonus double.
					return (5 * enchant) + (10 * Math.max(0, enchant - 3));
				}
				
				// P. Atk. increases by 4 for one-handed swords, one-handed blunts, daggers, spears, and other weapons.
				// Starting at +4, P. Atk. bonus double.
				return (4 * enchant) + (8 * Math.max(0, enchant - 3));
			}
			case B:
			case C:
			{
				if ((item.getWeaponItem().getBodyPart() == BodyPart.LR_HAND) && (item.getWeaponItem().getItemType() != WeaponType.POLE))
				{
					if (item.getWeaponItem().getItemType().isRanged())
					{
						// P. Atk. increases by 6 for bows.
						// Starting at +4, P. Atk. bonus double.
						return (6 * enchant) + (12 * Math.max(0, enchant - 3));
					}
					
					// P. Atk. increases by 4 for two-handed swords, two-handed blunts, dualswords, and two-handed combat weapons.
					// Starting at +4, P. Atk. bonus double.
					return (4 * enchant) + (8 * Math.max(0, enchant - 3));
				}
				
				// P. Atk. increases by 3 for one-handed swords, one-handed blunts, daggers, spears, and other weapons.
				// Starting at +4, P. Atk. bonus double.
				return (3 * enchant) + (6 * Math.max(0, enchant - 3));
			}
			default:
			{
				if (item.getWeaponItem().getItemType().isRanged())
				{
					// Bows increase by 4.
					// Starting at +4, P. Atk. bonus double.
					return (4 * enchant) + (8 * Math.max(0, enchant - 3));
				}
				
				// P. Atk. increases by 2 for all weapons with the exception of bows.
				// Starting at +4, P. Atk. bonus double.
				return (2 * enchant) + (4 * Math.max(0, enchant - 3));
			}
		}
	}

	static double calcEnchantedPAtkBonus(ItemTemplate item, double blessedBonus, int enchant)
	{
		final WeaponType weaponType = (WeaponType) item.getItemType();
		switch (item.getCrystalTypePlus())
		{
			case L:
			case R:
			{
				if ((item.getBodyPart() == BodyPart.LR_HAND) && (weaponType != WeaponType.POLE))
				{
					if (weaponType.isRanged())
					{
						return (12 * blessedBonus * enchant) + (24 * blessedBonus * Math.max(0, enchant - 3));
					}
					
					return (7 * blessedBonus * enchant) + (14 * blessedBonus * Math.max(0, enchant - 3));
				}
				
				return (6 * blessedBonus * enchant) + (12 * blessedBonus * Math.max(0, enchant - 3));
			}
			case S:
			{
				if ((item.getBodyPart() == BodyPart.LR_HAND) && (weaponType != WeaponType.POLE))
				{
					if (weaponType.isRanged())
					{
						return (10 * enchant) + (20 * Math.max(0, enchant - 3));
					}
					
					return (6 * enchant) + (12 * Math.max(0, enchant - 3));
				}
				
				return (5 * enchant) + (10 * Math.max(0, enchant - 3));
			}
			case A:
			{
				if ((item.getBodyPart() == BodyPart.LR_HAND) && (weaponType != WeaponType.POLE))
				{
					if (weaponType.isRanged())
					{
						return (8 * enchant) + (16 * Math.max(0, enchant - 3));
					}
					
					return (5 * enchant) + (10 * Math.max(0, enchant - 3));
				}
				
				return (4 * enchant) + (8 * Math.max(0, enchant - 3));
			}
			case B:
			{
				if ((item.getBodyPart() == BodyPart.LR_HAND) && (weaponType != WeaponType.POLE))
				{
					if (weaponType.isRanged())
					{
						return (6 * enchant) + (12 * Math.max(0, enchant - 3));
					}
					
					return (4 * enchant) + (8 * Math.max(0, enchant - 3));
				}
				
				return (3 * enchant) + (6 * Math.max(0, enchant - 3));
			}
			case C:
			{
				if ((item.getBodyPart() == BodyPart.LR_HAND) && (weaponType != WeaponType.POLE))
				{
					if (weaponType.isRanged())
					{
						return (5 * enchant) + (10 * Math.max(0, enchant - 3));
					}
					
					return (3 * enchant) + (6 * Math.max(0, enchant - 3));
				}
				
				return (2 * enchant) + (4 * Math.max(0, enchant - 3));
			}
			default:
			{
				if ((item.getBodyPart() == BodyPart.LR_HAND) && (weaponType != WeaponType.POLE))
				{
					if (weaponType.isRanged())
					{
						return (4 * enchant) + (8 * Math.max(0, enchant - 3));
					}
					
					return (2 * enchant) + (4 * Math.max(0, enchant - 3));
				}
				
				return (1 * enchant) + (2 * Math.max(0, enchant - 3));
			}
		}
	}
	
	default double validateValue(Creature creature, double value, double minValue, double maxValue)
	{
		if (value > maxValue) // && !creature.isGM()
		{
			return maxValue;
		}
		
		return Math.max(minValue, value);
	}
	
	double calc(Creature creature, OptionalDouble base, Stat stat);
}
