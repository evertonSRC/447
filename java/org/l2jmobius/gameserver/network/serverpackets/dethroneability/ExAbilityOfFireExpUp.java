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

import java.text.NumberFormat;
import java.util.Locale;

import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.commons.util.Rnd;
import org.l2jmobius.gameserver.config.ConquestConfig;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.item.enums.ItemProcessType;
import org.l2jmobius.gameserver.model.item.holders.ItemHolder;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;
import org.l2jmobius.gameserver.network.serverpackets.SystemMessage;

/**
 * @author CostyKiller
 */
public class ExAbilityOfFireExpUp extends ServerPacket
{
	private final Player _player;
	private final int _type;
	private int _success = 0;
	
	public ExAbilityOfFireExpUp(Player player, int type)
	{
		_player = player;
		_type = type;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_ENHANCED_ABILITY_OF_FIRE_EXP_UP.writeId(this, buffer);
		buffer.writeByte(_type);
		if (_type == 0) // Fire Source
		{
			// _success = 0
		}
		else if (_type == 1) // Life Source
		{
			_player.setCurrentHp(_player.getCurrentHp() - ConquestConfig.CONQUEST_ABILITY_LIFE_SOURCE_REQUIRED_HP_POINTS);
			
			// Java 18
			// final NumberFormat formatter = NumberFormat.getInstance(new Locale("en", "US"));
			// Java 19
			final NumberFormat formatter = NumberFormat.getInstance(Locale.of("en", "US"));
			_player.sendMessage("Your HP has decreased by " + formatter.format(ConquestConfig.CONQUEST_ABILITY_LIFE_SOURCE_REQUIRED_HP_POINTS) + ".");
			
			for (ItemHolder requiredItems : ConquestConfig.CONQUEST_ABILITY_LIFE_SOURCE_REQUIRED_ITEMS)
			{
				_player.destroyItemByItemId(ItemProcessType.FEE, requiredItems.getId(), requiredItems.getCount(), _player, true);
			}
			
			_player.getVariables().set(PlayerVariables.CONQUEST_ABILITY_LIFE_SOURCE_UPGRADES, (_player.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_LIFE_SOURCE_UPGRADES, 500) - 1));
			_player.getVariables().storeMe();
			
			if (Rnd.get(100) < ConquestConfig.CONQUEST_ABILITY_LIFE_SOURCE_UPGRADE_CHANCE)
			{
				_player.getVariables().set(PlayerVariables.CONQUEST_ABILITY_LIFE_SOURCE_EXP, (_player.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_LIFE_SOURCE_EXP, 0) + ConquestConfig.CONQUEST_ABILITY_LIFE_SOURCE_EXP_AMOUNT));
				_player.getVariables().storeMe();
				_success = 1;
			}
			
			buffer.writeByte(_success); // int Result
			buffer.writeInt(_player.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_LIFE_SOURCE_EXP, 0)); // int EXP
			buffer.writeInt(_player.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_LIFE_SOURCE_UPGRADES, 500)); // int ExpUpCount
			if (_success == 1)
			{
				// Rewards Part
				if (Rnd.get(100) < ConquestConfig.CONQUEST_ABILITY_LIFE_SOURCE_REWARD_CHANCE)
				{
					for (ItemHolder rewards : ConquestConfig.CONQUEST_ABILITY_LIFE_SOURCE_REWARDS)
					{
						_player.addItem(ItemProcessType.REWARD, rewards.getId(), rewards.getCount(), _player, true);
					}
					
					buffer.writeInt(ConquestConfig.CONQUEST_ABILITY_LIFE_SOURCE_REWARDS.size()); // array<_ItemInfo> rewards
					for (ItemHolder rewards : ConquestConfig.CONQUEST_ABILITY_LIFE_SOURCE_REWARDS)
					{
						buffer.writeInt(rewards.getId());
						buffer.writeLong(rewards.getCount());
					}
				}
				else
				{
					buffer.writeInt(0); // array<_ItemInfo> rewards
				}
			}
			else
			{
				buffer.writeInt(0); // array<_ItemInfo> rewards
			}
		}
		else if (_type == 2) // Flame Spark
		{
			for (ItemHolder requiredItems : ConquestConfig.CONQUEST_ABILITY_FLAME_SPARK_REQUIRED_ITEMS)
			{
				_player.destroyItemByItemId(ItemProcessType.FEE, requiredItems.getId(), requiredItems.getCount(), _player, true);
			}
			
			_player.getVariables().set(PlayerVariables.CONQUEST_ABILITY_FLAME_SPARK_UPGRADES, (_player.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_FLAME_SPARK_UPGRADES, 60) - 1));
			_player.getVariables().storeMe();
			
			if (Rnd.get(100) < ConquestConfig.CONQUEST_ABILITY_FLAME_SPARK_UPGRADE_CHANCE)
			{
				_player.getVariables().set(PlayerVariables.CONQUEST_ABILITY_FLAME_SPARK_EXP, (_player.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_FLAME_SPARK_EXP, 0) + ConquestConfig.CONQUEST_ABILITY_FLAME_SPARK_EXP_AMOUNT));
				_player.getVariables().storeMe();
				_success = 1;
			}
			
			buffer.writeByte(_success); // int Result
			buffer.writeInt(_player.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_FLAME_SPARK_EXP, 0)); // int EXP
			buffer.writeInt(_player.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_FLAME_SPARK_UPGRADES, 60)); // int ExpUpCount
			if (_success == 1)
			{
				// Rewards Part
				if (Rnd.get(100) < ConquestConfig.CONQUEST_ABILITY_LIFE_SOURCE_REWARD_CHANCE)
				{
					for (ItemHolder rewards : ConquestConfig.CONQUEST_ABILITY_FLAME_SPARK_REWARDS)
					{
						_player.addItem(ItemProcessType.REWARD, rewards.getId(), rewards.getCount(), _player, true);
					}
					
					buffer.writeInt(ConquestConfig.CONQUEST_ABILITY_FLAME_SPARK_REWARDS.size()); // array<_ItemInfo> rewards
					for (ItemHolder rewards : ConquestConfig.CONQUEST_ABILITY_FLAME_SPARK_REWARDS)
					{
						buffer.writeInt(rewards.getId());
						buffer.writeLong(rewards.getCount());
					}
				}
				else
				{
					buffer.writeInt(0); // array<_ItemInfo> rewards
				}
			}
			else
			{
				buffer.writeInt(0); // array<_ItemInfo> rewards
			}
		}
		else if (_type == 3) // Fire Totem
		{
			for (ItemHolder requiredItems : ConquestConfig.CONQUEST_ABILITY_FIRE_TOTEM_REQUIRED_ITEMS)
			{
				_player.destroyItemByItemId(ItemProcessType.FEE, requiredItems.getId(), requiredItems.getCount(), _player, true);
			}
			
			_player.getVariables().set(PlayerVariables.CONQUEST_ABILITY_FIRE_TOTEM_UPGRADES, (_player.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_FIRE_TOTEM_UPGRADES, 100) - 1));
			_player.getVariables().storeMe();
			
			if (Rnd.get(100) < ConquestConfig.CONQUEST_ABILITY_FIRE_TOTEM_UPGRADE_CHANCE)
			{
				_player.getVariables().set(PlayerVariables.CONQUEST_ABILITY_FIRE_TOTEM_EXP, (_player.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_FIRE_TOTEM_EXP, 0) + ConquestConfig.CONQUEST_ABILITY_FIRE_TOTEM_EXP_AMOUNT));
				_player.getVariables().storeMe();
				_success = 1;
			}
			
			buffer.writeByte(_success); // int Result
			buffer.writeInt(_player.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_FIRE_TOTEM_EXP, 0)); // int EXP
			buffer.writeInt(_player.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_FIRE_TOTEM_UPGRADES, 100)); // int ExpUpCount
			if (_success == 1)
			{
				// Rewards Part
				if (Rnd.get(100) < ConquestConfig.CONQUEST_ABILITY_FIRE_TOTEM_REWARD_CHANCE)
				{
					for (ItemHolder rewards : ConquestConfig.CONQUEST_ABILITY_FIRE_TOTEM_REWARDS)
					{
						_player.addItem(ItemProcessType.REWARD, rewards.getId(), rewards.getCount(), _player, true);
					}
					
					buffer.writeInt(ConquestConfig.CONQUEST_ABILITY_FIRE_TOTEM_REWARDS.size()); // array<_ItemInfo> rewards
					for (ItemHolder rewards : ConquestConfig.CONQUEST_ABILITY_FIRE_TOTEM_REWARDS)
					{
						buffer.writeInt(rewards.getId());
						buffer.writeLong(rewards.getCount());
					}
				}
				else
				{
					buffer.writeInt(0);
				}
			}
			else
			{
				buffer.writeInt(0); // array<_ItemInfo> rewards
			}
		}
		else if (_type == 4) // Battle Soul
		{
			_player.setSp(_player.getSp() - ConquestConfig.CONQUEST_ABILITY_BATTLE_SOUL_REQUIRED_SP_POINTS);
			SystemMessage sm = new SystemMessage(SystemMessageId.YOUR_SP_HAS_DECREASED_BY_S1);
			sm.addLong(ConquestConfig.CONQUEST_ABILITY_BATTLE_SOUL_REQUIRED_SP_POINTS);
			_player.sendPacket(sm);
			_player.broadcastUserInfo();
			for (ItemHolder requiredItems : ConquestConfig.CONQUEST_ABILITY_BATTLE_SOUL_REQUIRED_ITEMS)
			{
				_player.destroyItemByItemId(ItemProcessType.FEE, requiredItems.getId(), requiredItems.getCount(), _player, true);
			}
			
			_player.getVariables().set(PlayerVariables.CONQUEST_ABILITY_BATTLE_SOUL_UPGRADES, (_player.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_BATTLE_SOUL_UPGRADES, 100) - 1));
			_player.getVariables().storeMe();
			
			if (Rnd.get(100) < ConquestConfig.CONQUEST_ABILITY_BATTLE_SOUL_UPGRADE_CHANCE)
			{
				_player.getVariables().set(PlayerVariables.CONQUEST_ABILITY_BATTLE_SOUL_EXP, (_player.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_BATTLE_SOUL_EXP, 0) + ConquestConfig.CONQUEST_ABILITY_BATTLE_SOUL_EXP_AMOUNT));
				_player.getVariables().storeMe();
				_success = 1;
			}
			
			buffer.writeByte(_success); // int Result
			buffer.writeInt(_player.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_BATTLE_SOUL_EXP, 0)); // int EXP
			buffer.writeInt(_player.getVariables().getInt(PlayerVariables.CONQUEST_ABILITY_BATTLE_SOUL_UPGRADES, 100)); // int ExpUpCount
			if (_success == 1)
			{
				// Rewards Part
				if (Rnd.get(100) < ConquestConfig.CONQUEST_ABILITY_BATTLE_SOUL_REWARD_CHANCE)
				{
					for (ItemHolder rewards : ConquestConfig.CONQUEST_ABILITY_BATTLE_SOUL_REWARDS)
					{
						_player.addItem(ItemProcessType.REWARD, rewards.getId(), rewards.getCount(), _player, true);
					}
					
					buffer.writeInt(ConquestConfig.CONQUEST_ABILITY_BATTLE_SOUL_REWARDS.size()); // array<_ItemInfo> rewards
					for (ItemHolder rewards : ConquestConfig.CONQUEST_ABILITY_BATTLE_SOUL_REWARDS)
					{
						buffer.writeInt(rewards.getId());
						buffer.writeLong(rewards.getCount());
					}
				}
				else
				{
					buffer.writeInt(0);
				}
			}
			else
			{
				buffer.writeInt(0); // array<_ItemInfo> rewards
			}
		}
	}
}
