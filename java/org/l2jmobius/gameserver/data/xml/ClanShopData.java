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
package org.l2jmobius.gameserver.data.xml;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.w3c.dom.Document;

import org.l2jmobius.commons.util.IXmlReader;
import org.l2jmobius.gameserver.data.holders.ClanShopProductHolder;
import org.l2jmobius.gameserver.model.StatSet;
import org.l2jmobius.gameserver.model.item.ItemTemplate;

/**
 * @author Mobius
 */
public class ClanShopData implements IXmlReader
{
	private static final Logger LOGGER = Logger.getLogger(ClanShopData.class.getName());
	
	private final List<ClanShopProductHolder> _clanShopProducts = new ArrayList<>();
	
	protected ClanShopData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		_clanShopProducts.clear();
		parseDatapackFile("config/ClanShop.xml");
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _clanShopProducts.size() + " clan shop products.");
	}
	
	@Override
	public void parseDocument(Document document, File file)
	{
		forEach(document, "list", listNode -> forEach(listNode, "clan", productNode ->
		{
			// Parse attributes into StatSet.
			final StatSet set = new StatSet(parseAttributes(productNode));
			
			// Extract clan shop product attributes.
			final int clanLevel = set.getInt("level");
			final int itemId = set.getInt("item");
			final int count = set.getInt("count");
			final long adena = set.getLong("adena");
			final int fame = set.getInt("fame");
			
			// Retrieve the item template.
			final ItemTemplate item = ItemData.getInstance().getTemplate(itemId);
			if (item == null)
			{
				LOGGER.info(getClass().getSimpleName() + ": Could not create clan shop item " + itemId + ", it does not exist.");
			}
			else // Add the ClanShopProductHolder to the list.
			{
				_clanShopProducts.add(new ClanShopProductHolder(clanLevel, item, count, adena, fame));
			}
		}));
	}
	
	public ClanShopProductHolder getProduct(int itemId)
	{
		for (ClanShopProductHolder product : _clanShopProducts)
		{
			if (product.getTradeItem().getItem().getId() == itemId)
			{
				return product;
			}
		}
		
		return null;
	}
	
	public List<ClanShopProductHolder> getProducts()
	{
		return _clanShopProducts;
	}
	
	public static ClanShopData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ClanShopData INSTANCE = new ClanShopData();
	}
}
