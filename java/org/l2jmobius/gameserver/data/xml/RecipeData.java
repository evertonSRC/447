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
package org.l2jmobius.gameserver.data.xml;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import org.l2jmobius.commons.util.IXmlReader;
import org.l2jmobius.gameserver.data.holders.RecipeHolder;
import org.l2jmobius.gameserver.model.StatSet;
import org.l2jmobius.gameserver.model.item.holders.ItemChanceHolder;
import org.l2jmobius.gameserver.model.item.holders.ItemHolder;
import org.l2jmobius.gameserver.network.enums.StatusUpdateType;

/**
 * @author Nik
 */
public class RecipeData implements IXmlReader
{
	private static final Logger LOGGER = Logger.getLogger(RecipeData.class.getName());
	
	private final Map<Integer, RecipeHolder> _recipes = new HashMap<>();
	
	protected RecipeData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		_recipes.clear();
		parseDatapackFile("data/Recipes.xml");
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _recipes.size() + " recipes.");
	}
	
	@Override
	public void parseDocument(Document document, File file)
	{
		StatSet set;
		Node att;
		NamedNodeMap attrs;
		for (Node n = document.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if ("list".equalsIgnoreCase(n.getNodeName()))
			{
				for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
				{
					if ("recipe".equalsIgnoreCase(d.getNodeName()))
					{
						attrs = d.getAttributes();
						set = new StatSet();
						for (int i = 0; i < attrs.getLength(); i++)
						{
							att = attrs.item(i);
							set.set(att.getNodeName(), att.getNodeValue());
						}
						
						final int recipeId = set.getInt("id");
						List<ItemHolder> materials = Collections.emptyList();
						List<ItemChanceHolder> productGroup = Collections.emptyList();
						List<ItemHolder> npcFee = Collections.emptyList();
						final Map<StatusUpdateType, Double> statUse = new HashMap<>();
						
						for (Node c = d.getFirstChild(); c != null; c = c.getNextSibling())
						{
							if ("materials".equalsIgnoreCase(c.getNodeName()))
							{
								materials = getItemList(c);
							}
							else if ("product".equalsIgnoreCase(c.getNodeName()))
							{
								productGroup = getItemList(c).stream().map(ItemChanceHolder.class::cast).collect(Collectors.toList());
							}
							else if ("npcFee".equalsIgnoreCase(c.getNodeName()))
							{
								npcFee = getItemList(c);
							}
							else if ("statUse".equalsIgnoreCase(c.getNodeName()))
							{
								for (Node b = c.getFirstChild(); b != null; b = b.getNextSibling())
								{
									if ("stat".equalsIgnoreCase(b.getNodeName()))
									{
										final StatusUpdateType stat = StatusUpdateType.valueOf(b.getAttributes().getNamedItem("name").getNodeValue());
										final double value = Double.parseDouble(b.getAttributes().getNamedItem("val").getNodeValue());
										statUse.put(stat, value);
									}
								}
							}
						}
						
						_recipes.put(recipeId, new RecipeHolder(set, materials, productGroup, npcFee, statUse));
					}
				}
			}
		}
	}
	
	private List<ItemHolder> getItemList(Node c)
	{
		final List<ItemHolder> items = new ArrayList<>();
		for (Node b = c.getFirstChild(); b != null; b = b.getNextSibling())
		{
			if ("item".equalsIgnoreCase(b.getNodeName()))
			{
				final int itemId = Integer.parseInt(b.getAttributes().getNamedItem("id").getNodeValue());
				final long itemCount = Long.parseLong(b.getAttributes().getNamedItem("count").getNodeValue());
				
				if (b.getAttributes().getNamedItem("chance") != null)
				{
					final double chance = Double.parseDouble(b.getAttributes().getNamedItem("chance").getNodeValue());
					items.add(new ItemChanceHolder(itemId, chance, itemCount));
				}
				else
				{
					items.add(new ItemHolder(itemId, itemCount));
				}
			}
		}
		
		return items;
	}
	
	/**
	 * Retrieves the recipe associated with a specific recipe item ID.
	 * @param itemId the ID of the recipe item to search for
	 * @return the {@code RecipeHolder} containing the recipe details for the specified recipe item ID, or {@code null} if there is no recipe data associated with this item ID
	 */
	public RecipeHolder getRecipeByRecipeItemId(int itemId)
	{
		return _recipes.values().stream().filter(r -> r.getItemId() == itemId).findAny().orElse(null);
	}
	
	/**
	 * Retrieves the recipe associated with a specific recipe ID.
	 * @param recipeId the ID of the recipe (not the recipe item ID) to search for
	 * @return the {@code RecipeHolder} containing all necessary information for crafting the recipe, or {@code null} if there is no data associated with this recipe ID
	 */
	public RecipeHolder getRecipe(int recipeId)
	{
		return _recipes.get(recipeId);
	}
	
	public static RecipeData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final RecipeData INSTANCE = new RecipeData();
	}
}
