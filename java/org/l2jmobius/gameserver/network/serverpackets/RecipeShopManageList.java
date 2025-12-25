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
package org.l2jmobius.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.data.holders.RecipeHolder;
import org.l2jmobius.gameserver.data.xml.RecipeData;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;

public class RecipeShopManageList extends ServerPacket
{
	private final Player _seller;
	private final boolean _isDwarven;
	private final Collection<RecipeHolder> _recipes;
	private List<Entry<Integer, Long>> _manufacture;
	
	public RecipeShopManageList(Player seller, boolean isDwarven)
	{
		_seller = seller;
		_isDwarven = isDwarven;
		_recipes = (isDwarven && (_seller.getCreateItemLevel() > 0)) ? _seller.getDwarvenRecipeBook() : _seller.getCommonRecipeBook();
		if (_seller.hasManufactureShop())
		{
			_manufacture = new ArrayList<>();
			for (Entry<Integer, Long> item : _seller.getManufactureItems().entrySet())
			{
				final RecipeHolder recipe = RecipeData.getInstance().getRecipe(item.getKey());
				if (((recipe != null) && (recipe.isDwarvenRecipe() == _isDwarven)) && seller.hasRecipeList(recipe.getId()))
				{
					_manufacture.add(item);
				}
			}
		}
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.RECIPE_SHOP_MANAGE_LIST.writeId(this, buffer);
		buffer.writeInt(_seller.getObjectId());
		buffer.writeInt((int) _seller.getAdena());
		buffer.writeInt(!_isDwarven);
		if ((_recipes == null) || _recipes.isEmpty())
		{
			buffer.writeInt(0);
		}
		else
		{
			buffer.writeInt(_recipes.size()); // number of items in recipe book
			int count = 1;
			for (RecipeHolder recipe : _recipes)
			{
				buffer.writeInt(recipe.getId());
				buffer.writeInt(count++);
			}
		}
		
		if ((_manufacture == null) || _manufacture.isEmpty())
		{
			buffer.writeInt(0);
		}
		else
		{
			buffer.writeInt(_manufacture.size());
			for (Entry<Integer, Long> item : _manufacture)
			{
				buffer.writeInt(item.getKey());
				buffer.writeInt(0); // CanCraft?
				buffer.writeLong(item.getValue());
			}
		}
	}
}
