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
package org.l2jmobius.gameserver.network.clientpackets.alchemy;

import org.l2jmobius.commons.util.Rnd;
import org.l2jmobius.gameserver.data.xml.AlchemyData;
import org.l2jmobius.gameserver.data.xml.ItemData;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.enums.creature.Race;
import org.l2jmobius.gameserver.model.alchemy.AlchemyCraftData;
import org.l2jmobius.gameserver.model.events.EventDispatcher;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.holders.item.OnItemCombination;
import org.l2jmobius.gameserver.model.item.ItemTemplate;
import org.l2jmobius.gameserver.model.item.enums.ItemProcessType;
import org.l2jmobius.gameserver.model.item.holders.ItemHolder;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.model.itemcontainer.PlayerInventory;
import org.l2jmobius.gameserver.network.PacketLogger;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.clientpackets.ClientPacket;
import org.l2jmobius.gameserver.network.serverpackets.alchemy.ExAlchemyConversion;
import org.l2jmobius.gameserver.taskmanagers.AttackStanceTaskManager;

/**
 * @author Sdw
 */
public class RequestAlchemyConversion extends ClientPacket
{
	private int _craftTimes;
	private int _skillId;
	private int _skillLevel;
	// private final Set<ItemHolder> _ingredients = new HashSet<>();
	
	@Override
	protected void readImpl()
	{
		_craftTimes = readInt();
		readShort();
		_skillId = readInt();
		_skillLevel = readInt();
		// final int ingredientsSize = readInt();
		// for (int i = 0; i < ingredientsSize; i++)
		// {
		// _ingredients.add(new ItemHolder(readInt(), readLong()));
		// }
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if ((player == null) || (player.getRace() != Race.ERTHEIA))
		{
			return;
		}
		
		if (_craftTimes < 1)
		{
			player.sendPacket(new ExAlchemyConversion(0, 0));
			return;
		}
		
		if (AttackStanceTaskManager.getInstance().hasAttackStanceTask(player))
		{
			player.sendPacket(new ExAlchemyConversion(0, 0));
			player.sendPacket(SystemMessageId.YOU_CANNOT_USE_ALCHEMY_WHILE_IN_BATTLE);
			return;
		}
		else if (player.isInStoreMode())
		{
			player.sendPacket(new ExAlchemyConversion(0, 0));
			player.sendPacket(SystemMessageId.YOU_CANNOT_USE_ALCHEMY_WHILE_TRADING_OR_USING_A_PRIVATE_STORE_OR_SHOP);
			return;
		}
		else if (player.isDead())
		{
			player.sendPacket(new ExAlchemyConversion(0, 0));
			player.sendPacket(SystemMessageId.YOU_CANNOT_USE_ALCHEMY_WHILE_DEAD);
			return;
		}
		else if (player.isMovementDisabled())
		{
			player.sendPacket(new ExAlchemyConversion(0, 0));
			player.sendPacket(SystemMessageId.YOU_CANNOT_USE_ALCHEMY_WHILE_IMMOBILE);
			return;
		}
		
		final AlchemyCraftData data = AlchemyData.getInstance().getCraftData(_skillId, _skillLevel);
		if (data == null)
		{
			player.sendPacket(new ExAlchemyConversion(0, 0));
			PacketLogger.warning("Missing AlchemyData for skillId: " + _skillId + ", skillLevel: " + _skillLevel);
			return;
		}
		
		// if (!_ingredients.equals(data.getIngredients()))
		// {
		// PacketLogger.warning("Client ingredients are not same as server ingredients for alchemy conversion player: "+ +"", player);
		// return;
		// }
		
		// Chance based on grade.
		final float baseChance;
		
		// Custom XML chance value.
		final float customChance = data.getChance();
		
		switch (data.getGrade())
		{
			case 1: // Elementary
			{
				baseChance = customChance > 0 ? customChance : 100;
				break;
			}
			case 2: // Intermediate
			{
				baseChance = customChance > 0 ? customChance : 80;
				break;
			}
			case 3: // Advanced
			{
				baseChance = customChance > 0 ? customChance : 60;
				break;
			}
			default: // Master
			{
				baseChance = customChance > 0 ? customChance : 50;
				break;
			}
		}
		
		// Calculate success and failure count.
		final ItemTemplate successItemTemplate = ItemData.getInstance().getTemplate(data.getProductionSuccess().getId());
		final ItemTemplate failureItemTemplate = ItemData.getInstance().getTemplate(data.getProductionFailure().getId());
		int totalWeight = 0;
		int totalslots = (successItemTemplate.isStackable() ? 1 : 0) + (failureItemTemplate.isStackable() ? 1 : 0);
		int successCount = 0;
		int failureCount = 0;
		for (int i = 0; i < _craftTimes; i++)
		{
			if (Rnd.get(100) < baseChance)
			{
				successCount++;
				totalWeight += successItemTemplate.getWeight();
				totalslots += successItemTemplate.isStackable() ? 0 : 1;
			}
			else
			{
				failureCount++;
				totalWeight += failureItemTemplate.getWeight();
				totalslots += failureItemTemplate.isStackable() ? 0 : 1;
			}
		}
		
		// Check if player has enough ingredients.
		for (ItemHolder ingredient : data.getIngredients())
		{
			final long count = ingredient.getCount() * _craftTimes;
			if (count < 0)
			{
				player.sendPacket(new ExAlchemyConversion(0, 0));
				return;
			}
			
			if (player.getInventory().getInventoryItemCount(ingredient.getId(), -1) < count)
			{
				player.sendPacket(new ExAlchemyConversion(0, 0));
				player.sendPacket(SystemMessageId.NOT_ENOUGH_MATERIALS_2);
				return;
			}
		}
		
		// Weight and capacity check.
		final PlayerInventory inventory = player.getInventory();
		if (!inventory.validateWeight(totalWeight) || ((totalslots > 0) && !inventory.validateCapacity(totalslots)))
		{
			player.sendPacket(new ExAlchemyConversion(0, 0));
			player.sendPacket(SystemMessageId.NOT_ENOUGH_INVENTORY_SPACE_FREE_UP_SOME_SPACE_AND_TRY_AGAIN);
			return;
		}
		
		// Destroy ingredients.
		for (ItemHolder ingredient : data.getIngredients())
		{
			final Item item = player.getInventory().getItemByItemId(ingredient.getId());
			player.getInventory().destroyItem(ItemProcessType.FEE, item, ingredient.getCount() * _craftTimes, player, null);
		}
		
		// Add success items.
		if (successCount > 0)
		{
			player.getInventory().addItem(ItemProcessType.REWARD, data.getProductionSuccess().getId(), data.getProductionSuccess().getCount() * successCount, player, null);
		}
		
		// Add failed items.
		if (failureCount > 0)
		{
			player.getInventory().addItem(ItemProcessType.REWARD, data.getProductionFailure().getId(), data.getProductionFailure().getCount() * failureCount, player, null);
		}
		
		// Notify to scripts.
		if (EventDispatcher.getInstance().hasListener(EventType.ON_ITEM_COMBINATION))
		{
			for (ItemHolder ingredient : data.getIngredients())
			{
				final Item item = player.getInventory().getItemByItemId(ingredient.getId());
				EventDispatcher.getInstance().notifyEventAsync(new OnItemCombination(player, item));
			}
		}
		
		player.sendPacket(new ExAlchemyConversion(successCount, failureCount));
	}
}
