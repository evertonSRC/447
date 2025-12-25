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

import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.data.holders.RecipeHolder;
import org.l2jmobius.gameserver.data.xml.RecipeData;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.stat.PlayerStat;
import org.l2jmobius.gameserver.model.stats.Stat;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.PacketLogger;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * @author Mobius
 */
public class RecipeItemMakeInfo extends ServerPacket
{
	private final int _id;
	private final Player _player;
	private final Boolean _success;
	private final long _offeringMaximumAdena;
	private final double _craftRate;
	private final double _craftCritical;
	
	public RecipeItemMakeInfo(int id, Player player, boolean success, long offeringMaximumAdena)
	{
		_id = id;
		_player = player;
		_success = success;
		_offeringMaximumAdena = offeringMaximumAdena;
		final PlayerStat stat = _player.getStat();
		_craftRate = stat.getValue(Stat.CRAFT_RATE, 0);
		_craftCritical = stat.getValue(Stat.CRAFTING_CRITICAL, 0);
	}
	
	public RecipeItemMakeInfo(int id, Player player, boolean success)
	{
		_id = id;
		_player = player;
		_success = success;
		_offeringMaximumAdena = 0;
		final PlayerStat stat = _player.getStat();
		_craftRate = stat.getValue(Stat.CRAFT_RATE, 0);
		_craftCritical = stat.getValue(Stat.CRAFTING_CRITICAL, 0);
	}
	
	public RecipeItemMakeInfo(int id, Player player, long offeringMaximumAdena)
	{
		_id = id;
		_player = player;
		_success = null;
		_offeringMaximumAdena = offeringMaximumAdena;
		final PlayerStat stat = _player.getStat();
		_craftRate = stat.getValue(Stat.CRAFT_RATE, 0);
		_craftCritical = stat.getValue(Stat.CRAFTING_CRITICAL, 0);
	}
	
	public RecipeItemMakeInfo(int id, Player player)
	{
		_id = id;
		_player = player;
		_success = null;
		_offeringMaximumAdena = 0;
		final PlayerStat stat = _player.getStat();
		_craftRate = stat.getValue(Stat.CRAFT_RATE, 0);
		_craftCritical = stat.getValue(Stat.CRAFTING_CRITICAL, 0);
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		final RecipeHolder recipe = RecipeData.getInstance().getRecipe(_id);
		if (recipe == null)
		{
			PacketLogger.info("Character: " + _player + ": Requested unexisting recipe with id = " + _id);
			return;
		}
		
		ServerPackets.RECIPE_ITEM_MAKE_INFO.writeId(this, buffer);
		buffer.writeInt(_id);
		buffer.writeInt(!recipe.isDwarvenRecipe()); // 0 = Dwarven - 1 = Common
		buffer.writeInt((int) _player.getCurrentMp());
		buffer.writeInt(_player.getMaxMp());
		buffer.writeInt(_success == null ? -1 : (_success ? 1 : 0)); // item creation none/success/failed
		buffer.writeByte(_offeringMaximumAdena > 0); // Show offering window.
		buffer.writeLong(_offeringMaximumAdena); // Adena worth of items for maximum offering.
		buffer.writeDouble(Math.min(_craftRate, 100.0));
		buffer.writeByte(_craftCritical > 0);
		buffer.writeDouble(Math.min(_craftCritical, 100.0));
		buffer.writeByte(0); // find me
	}
}
