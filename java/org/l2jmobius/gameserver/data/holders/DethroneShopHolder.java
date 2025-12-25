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
package org.l2jmobius.gameserver.data.holders;

/**
 * @author Liamxroy
 */
public class DethroneShopHolder
{
	private final int _id;
	private final int _category;
	private final int _minLevel;
	private final int _maxLevel;
	private final int[] _ingredientIds;
	private final long[] _ingredientQuantities;
	private final int[] _ingredientEnchants;
	private final int _productionId;
	private final long _count;
	private final float _chance;
	private final int _productionId2;
	private final long _count2;
	private final float _chance2;
	private final int _productionId3;
	private final long _count3;
	private final float _chance3;
	private final int _productionId4;
	private final long _count4;
	private final float _chance4;
	private final int _productionId5;
	private final long _count5;
	private final int _accountDailyLimit;
	private final int _accountBuyLimit;
	private final boolean _announce;
	
	public DethroneShopHolder(int id, int category, int minLevel, int maxLevel, int[] ingredientIds, long[] ingredientQuantities, int[] ingredientEnchants, int productionId, long count, float chance, int productionId2, long count2, float chance2, int productionId3, long count3, float chance3, int productionId4, long count4, float chance4, int productionId5, long count5, int accountDailyLimit, int accountBuyLimit, boolean announce)
	{
		_id = id;
		_category = category;
		_minLevel = minLevel;
		_maxLevel = maxLevel;
		_ingredientIds = ingredientIds;
		_ingredientQuantities = ingredientQuantities;
		_ingredientEnchants = ingredientEnchants;
		_productionId = productionId;
		_count = count;
		_chance = chance;
		_productionId2 = productionId2;
		_count2 = count2;
		_chance2 = chance2;
		_productionId3 = productionId3;
		_count3 = count3;
		_chance3 = chance3;
		_productionId4 = productionId4;
		_count4 = count4;
		_chance4 = chance4;
		_productionId5 = productionId5;
		_count5 = count5;
		_accountDailyLimit = accountDailyLimit;
		_accountBuyLimit = accountBuyLimit;
		_announce = announce;
	}
	
	public int getId()
	{
		return _id;
	}
	
	public int getCategory()
	{
		return _category;
	}
	
	public int getMinLevel()
	{
		return _minLevel;
	}
	
	public int getMaxLevel()
	{
		return _maxLevel;
	}
	
	public int[] getIngredientIds()
	{
		return _ingredientIds;
	}
	
	public long[] getIngredientQuantities()
	{
		return _ingredientQuantities;
	}
	
	public int[] getIngredientEnchants()
	{
		return _ingredientEnchants;
	}
	
	public int getProductionId()
	{
		return _productionId;
	}
	
	public long getCount()
	{
		return _count;
	}
	
	public float getChance()
	{
		return _chance;
	}
	
	public int getProductionId2()
	{
		return _productionId2;
	}
	
	public long getCount2()
	{
		return _count2;
	}
	
	public float getChance2()
	{
		return _chance2;
	}
	
	public int getProductionId3()
	{
		return _productionId3;
	}
	
	public long getCount3()
	{
		return _count3;
	}
	
	public float getChance3()
	{
		return _chance3;
	}
	
	public int getProductionId4()
	{
		return _productionId4;
	}
	
	public long getCount4()
	{
		return _count4;
	}
	
	public float getChance4()
	{
		return _chance4;
	}
	
	public int getProductionId5()
	{
		return _productionId5;
	}
	
	public long getCount5()
	{
		return _count5;
	}
	
	public int getAccountDailyLimit()
	{
		return _accountDailyLimit;
	}
	
	public int getAccountBuyLimit()
	{
		return _accountBuyLimit;
	}
	
	public boolean isAnnounce()
	{
		return _announce;
	}
}
