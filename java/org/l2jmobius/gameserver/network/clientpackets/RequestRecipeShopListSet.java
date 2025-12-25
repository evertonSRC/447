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

import static org.l2jmobius.gameserver.model.itemcontainer.Inventory.MAX_ADENA;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.l2jmobius.gameserver.config.GeneralConfig;
import org.l2jmobius.gameserver.config.PlayerConfig;
import org.l2jmobius.gameserver.data.holders.RecipeHolder;
import org.l2jmobius.gameserver.data.xml.ItemData;
import org.l2jmobius.gameserver.data.xml.RecipeData;
import org.l2jmobius.gameserver.managers.PunishmentManager;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.enums.player.PrivateStoreType;
import org.l2jmobius.gameserver.model.zone.ZoneId;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.ActionFailed;
import org.l2jmobius.gameserver.network.serverpackets.RecipeShopMsg;
import org.l2jmobius.gameserver.taskmanagers.AttackStanceTaskManager;
import org.l2jmobius.gameserver.util.Broadcast;

/**
 * RequestRecipeShopListSet client packet class.
 */
public class RequestRecipeShopListSet extends ClientPacket
{
	private static final int BATCH_LENGTH = 12;
	
	private Map<Integer, Long> _manufactureRecipes = null;
	
	@Override
	protected void readImpl()
	{
		final int count = readInt();
		if ((count <= 0) || (count > PlayerConfig.MAX_ITEM_IN_PACKET) || ((count * BATCH_LENGTH) != remaining()))
		{
			return;
		}
		
		_manufactureRecipes = new HashMap<>(count);
		for (int i = 0; i < count; i++)
		{
			final int id = readInt();
			final long cost = readLong();
			if (cost < 0)
			{
				_manufactureRecipes = null;
				return;
			}
			
			_manufactureRecipes.put(id, cost);
		}
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (_manufactureRecipes == null)
		{
			player.sendPacket(SystemMessageId.ITEMS_IN_A_PRIVATE_STORE_OR_A_PRIVATE_WORKSHOP_CANNOT_BE_EQUIPPED);
			player.setPrivateStoreType(PrivateStoreType.NONE);
			player.broadcastUserInfo();
			return;
		}
		
		if (player.isCastingNow())
		{
			player.sendPacket(SystemMessageId.A_PRIVATE_STORE_MAY_NOT_BE_OPENED_WHILE_USING_A_SKILL);
			return;
		}
		
		if (player.isCrafting())
		{
			player.sendPacket(SystemMessageId.THE_ITEM_CREATION_IS_IN_PROGRESS_PLEASE_WAIT);
			return;
		}
		
		if (AttackStanceTaskManager.getInstance().hasAttackStanceTask(player) || player.isInDuel())
		{
			player.sendPacket(SystemMessageId.WHILE_YOU_ARE_ENGAGED_IN_COMBAT_YOU_CANNOT_OPERATE_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (player.isInsideZone(ZoneId.NO_STORE))
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_OPEN_A_PRIVATE_WORKSHOP_HERE);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		for (Entry<Integer, Long> item : _manufactureRecipes.entrySet())
		{
			final int recipeId = item.getKey();
			final long recipeCost = item.getValue();
			final RecipeHolder recipe = RecipeData.getInstance().getRecipe(recipeId);
			if (recipe == null)
			{
				player.sendPacket(SystemMessageId.THE_RECIPE_IS_INCORRECT);
				return;
			}
			
			if (ItemData.getInstance().getTemplate(recipe.getItemId()).isQuestItem())
			{
				player.sendPacket(SystemMessageId.QUEST_RECIPES_CAN_NOT_BE_REGISTERED);
				return;
			}
			
			if (!player.hasRecipeList(recipe.getId()))
			{
				PunishmentManager.handleIllegalPlayerAction(player, "Warning!! " + player + " of account " + player.getAccountName() + " tried to set recipe which he does not have.", GeneralConfig.DEFAULT_PUNISH);
				return;
			}
			
			if (recipeCost > MAX_ADENA)
			{
				PunishmentManager.handleIllegalPlayerAction(player, "Warning!! " + player + " of account " + player.getAccountName() + " tried to set price of " + recipeCost + " adena in Private Manufacture.", GeneralConfig.DEFAULT_PUNISH);
				return;
			}
		}
		
		player.setManufactureItems(_manufactureRecipes);
		
		player.setStoreName(!player.hasManufactureShop() ? "" : player.getStoreName());
		player.setPrivateStoreType(PrivateStoreType.MANUFACTURE);
		player.sitDown();
		player.broadcastUserInfo();
		Broadcast.toSelfAndKnownPlayers(player, new RecipeShopMsg(player));
	}
}
