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

import java.util.HashSet;
import java.util.Set;

import org.l2jmobius.commons.util.Rnd;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.item.ItemTemplate;
import org.l2jmobius.gameserver.model.item.enums.ItemProcessType;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.ExTryEnchantArtifactResult;
import org.l2jmobius.gameserver.network.serverpackets.InventoryUpdate;
import org.l2jmobius.gameserver.network.serverpackets.SystemMessage;

/**
 * @author Bonux
 */
public class RequestExTryEnchantArtifact extends ClientPacket
{
	private static final int[] ENCHANT_CHANCES =
	{
		100,
		70,
		70,
		50,
		40,
		40,
		40,
		30,
		30,
		20
	};
	
	private int _targetObjectId = 0;
	private int _count = 0;
	private final Set<Integer> _ingredients = new HashSet<>();
	
	@Override
	protected void readImpl()
	{
		_targetObjectId = readInt();
		_count = readInt();
		for (int i = 0; i < _count; i++)
		{
			_ingredients.add(readInt());
		}
	}
	
	@Override
	protected void runImpl()
	{
		if (_ingredients.contains(_targetObjectId))
		{
			return;
		}
		
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (player.hasBlockActions() || player.isInStoreMode() || player.isProcessingRequest() || player.isFishing() || player.isInTraingCamp() || (_count != _ingredients.size()))
		{
			player.sendPacket(ExTryEnchantArtifactResult.ERROR_PACKET);
			return;
		}
		
		final Item targetItem = player.getInventory().getItemByObjectId(_targetObjectId);
		if (targetItem == null)
		{
			player.sendPacket(ExTryEnchantArtifactResult.ERROR_PACKET);
			return;
		}
		
		final ItemTemplate item = targetItem.getTemplate();
		final int artifactSlot = item.getArtifactSlot();
		if (artifactSlot <= 0)
		{
			player.sendPacket(ExTryEnchantArtifactResult.ERROR_PACKET);
			return;
		}
		
		final int enchantLevel = targetItem.getEnchantLevel();
		int needCount = 0;
		if (enchantLevel <= 6)
		{
			needCount = 3;
		}
		else if (enchantLevel <= 9)
		{
			needCount = 2;
		}
		
		if ((needCount == 0) || (needCount != _ingredients.size()))
		{
			player.sendPacket(ExTryEnchantArtifactResult.ERROR_PACKET);
			return;
		}
		
		final int chance = ENCHANT_CHANCES[enchantLevel];
		if (chance == 0)
		{
			player.sendPacket(ExTryEnchantArtifactResult.ERROR_PACKET);
			return;
		}
		
		int ingredientEnchant = -1;
		if (enchantLevel <= 3)
		{
			ingredientEnchant = 0;
		}
		else if (enchantLevel <= 6)
		{
			ingredientEnchant = 1;
		}
		else if (enchantLevel <= 9)
		{
			ingredientEnchant = 3;
		}
		
		if (ingredientEnchant == -1)
		{
			player.sendPacket(ExTryEnchantArtifactResult.ERROR_PACKET);
			return;
		}
		
		for (int objectId : _ingredients)
		{
			final Item ingredient = player.getInventory().getItemByObjectId(objectId);
			if ((ingredient == null) || (ingredient.getEnchantLevel() < ingredientEnchant) || (ingredient.getEnchantLevel() > ingredientEnchant) || (ingredient.getTemplate().getArtifactSlot() != artifactSlot))
			{
				player.sendPacket(ExTryEnchantArtifactResult.ERROR_PACKET);
				return;
			}
			
			player.destroyItem(ItemProcessType.FEE, ingredient, 1, player, true);
		}
		
		if (Rnd.get(100) < chance)
		{
			targetItem.setEnchantLevel(enchantLevel + 1);
			final InventoryUpdate iu = new InventoryUpdate();
			iu.addModifiedItem(targetItem);
			player.sendInventoryUpdate(iu);
			player.sendPacket(new SystemMessage(SystemMessageId.ARTIFACT_UPGRADE_SUCCEEDED_AND_YOU_OBTAINED_S1).addItemName(targetItem.getId()));
			player.sendPacket(new ExTryEnchantArtifactResult(ExTryEnchantArtifactResult.SUCCESS, targetItem.getEnchantLevel()));
		}
		else
		{
			player.sendPacket(SystemMessageId.FAILED_TO_UPGRADE_ARTIFACT_THE_ITEM_S_UPGRADE_LEVEL_WILL_REMAIN_THE_SAME);
			player.sendPacket(new ExTryEnchantArtifactResult(ExTryEnchantArtifactResult.FAIL, targetItem.getEnchantLevel()));
		}
	}
}
