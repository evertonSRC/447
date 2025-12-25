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
package org.l2jmobius.gameserver.network.serverpackets.dethroneability;

import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.config.ConquestConfig;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.item.enums.ItemProcessType;
import org.l2jmobius.gameserver.model.item.holders.ItemHolder;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author CostyKiller
 */
public class ExAbilityOfFireInit extends ServerPacket
{
	private final Player _player;
	private final int _type;
	private int _success = 0;
	
	public ExAbilityOfFireInit(Player player, int type)
	{
		_player = player;
		_type = type;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_ENHANCED_ABILITY_OF_FIRE_INIT.writeId(this, buffer);
		buffer.writeByte(_type);
		
		if (_type == 0) // Fire Source
		{
			// No reset button here
		}
		else if (_type == 1) // Life Source
		{
			if (checkItems(_player))
			{
				for (ItemHolder requiredItems : ConquestConfig.CONQUEST_ABILITY_UPGRADES_RESET_REQUIRED_ITEMS)
				{
					_player.destroyItemByItemId(ItemProcessType.FEE, requiredItems.getId(), requiredItems.getCount(), _player, true);
					_success = 1;
				}
				
				buffer.writeByte(_success); // int Result
				if (_success == 1)
				{
					buffer.writeInt(_player.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_LIFE_SOURCE_UPGRADES, 500)); // int ExpUpCount
					_player.getVariables().set(PlayerVariables.CONQUEST_ABILITY_LIFE_SOURCE_UPGRADES, 500);
					_player.getVariables().set(PlayerVariables.CONQUEST_ABILITY_LIFE_SOURCE_RESET, 0);
					_player.getVariables().storeMe();
				}
				else
				{
					buffer.writeInt(0);
				}
			}
		}
		else if (_type == 2) // Flame Spark
		{
			if (checkItems(_player))
			{
				for (ItemHolder requiredItems : ConquestConfig.CONQUEST_ABILITY_UPGRADES_RESET_REQUIRED_ITEMS)
				{
					_player.destroyItemByItemId(ItemProcessType.FEE, requiredItems.getId(), requiredItems.getCount(), _player, true);
					_success = 1;
				}
				
				buffer.writeByte(_success); // int Result
				if (_success == 1)
				{
					buffer.writeInt(_player.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_FLAME_SPARK_UPGRADES, 60)); // int ExpUpCount
					_player.getVariables().set(PlayerVariables.CONQUEST_ABILITY_FLAME_SPARK_UPGRADES, 60);
					_player.getVariables().set(PlayerVariables.CONQUEST_ABILITY_FLAME_SPARK_RESET, 0);
					_player.getVariables().storeMe();
				}
				else
				{
					buffer.writeInt(0);
				}
			}
		}
		else if (_type == 3) // Fire Totem
		{
			if (checkItems(_player))
			{
				for (ItemHolder requiredItems : ConquestConfig.CONQUEST_ABILITY_UPGRADES_RESET_REQUIRED_ITEMS)
				{
					_player.destroyItemByItemId(ItemProcessType.FEE, requiredItems.getId(), requiredItems.getCount(), _player, true);
					_success = 1;
				}
				
				buffer.writeByte(_success); // int Result
				if (_success == 1)
				{
					buffer.writeInt(_player.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_FIRE_TOTEM_UPGRADES, 100)); // int ExpUpCount
					_player.getVariables().set(PlayerVariables.CONQUEST_ABILITY_FIRE_TOTEM_UPGRADES, 100);
					_player.getVariables().set(PlayerVariables.CONQUEST_ABILITY_FIRE_TOTEM_RESET, 0);
					_player.getVariables().storeMe();
				}
				else
				{
					buffer.writeInt(0);
				}
			}
		}
		else if (_type == 4) // Battle Soul
		{
			if (checkItems(_player))
			{
				for (ItemHolder requiredItems : ConquestConfig.CONQUEST_ABILITY_UPGRADES_RESET_REQUIRED_ITEMS)
				{
					_player.destroyItemByItemId(ItemProcessType.FEE, requiredItems.getId(), requiredItems.getCount(), _player, true);
					_success = 1;
				}
				
				buffer.writeByte(_success); // int Result
				if (_success == 1)
				{
					buffer.writeInt(_player.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_BATTLE_SOUL_UPGRADES, 100)); // int ExpUpCount
					_player.getVariables().set(PlayerVariables.CONQUEST_ABILITY_BATTLE_SOUL_UPGRADES, 100);
					_player.getVariables().set(PlayerVariables.CONQUEST_ABILITY_BATTLE_SOUL_RESET, 0);
					_player.getVariables().storeMe();
				}
				else
				{
					buffer.writeInt(0);
				}
			}
		}
	}
	
	public boolean checkItems(Player player)
	{
		for (ItemHolder requiredItems : ConquestConfig.CONQUEST_ABILITY_UPGRADES_RESET_REQUIRED_ITEMS)
		{
			if (player.getInventory().getItemByItemId(requiredItems.getId()).getCount() >= requiredItems.getCount())
			{
				return true;
			}
		}
		
		return false;
	}
}
