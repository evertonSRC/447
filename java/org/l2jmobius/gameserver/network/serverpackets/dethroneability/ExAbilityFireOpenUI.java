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
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Liamxroy, CostyKiller, Mobius
 */
public class ExAbilityFireOpenUI extends ServerPacket
{
	private final int _setEffectLevel;
	private final int _fireSourceLevel;
	private final int _fireSourceExp;
	private final int _lifeSourceLevel;
	private final int _lifeSourceExp;
	private final int _lifeSourceResetCounter;
	private final int _lifeSourceUpgrades;
	private final int _flameSparkLevel;
	private final int _flameSparkExp;
	private final int _flameSparkResetCounter;
	private final int _flameSparkUpgrades;
	private final int _fireTotemLevel;
	private final int _fireTotemExp;
	private final int _fireTotemResetCounter;
	private final int _fireTotemUpgrades;
	private final int _battleSoulLevel;
	private final int _battleSoulExp;
	private final int _battleSoulResetCounter;
	private final int _battleSoulUpgrades;
	
	public ExAbilityFireOpenUI(Player player)
	{
		final PlayerVariables variables = player.getVariables();
		_setEffectLevel = checkAbilitySetLevels(variables);
		_fireSourceLevel = variables.getInt(PlayerVariables.CONQUEST_ABILITY_FIRE_SOURCE_LEVEL, 0);
		_fireSourceExp = variables.getInt(PlayerVariables.CONQUEST_ABILITY_FIRE_SOURCE_EXP, 0);
		_lifeSourceLevel = variables.getInt(PlayerVariables.CONQUEST_ABILITY_LIFE_SOURCE_LEVEL, 0);
		_lifeSourceExp = variables.getInt(PlayerVariables.CONQUEST_ABILITY_LIFE_SOURCE_EXP, 0);
		_lifeSourceResetCounter = variables.getInt(PlayerVariables.CONQUEST_ABILITY_LIFE_SOURCE_RESET, 1);
		_lifeSourceUpgrades = variables.getInt(PlayerVariables.CONQUEST_ABILITY_LIFE_SOURCE_UPGRADES, 500);
		_flameSparkLevel = variables.getInt(PlayerVariables.CONQUEST_ABILITY_FLAME_SPARK_LEVEL, 0);
		_flameSparkExp = variables.getInt(PlayerVariables.CONQUEST_ABILITY_FLAME_SPARK_EXP, 0);
		_flameSparkResetCounter = variables.getInt(PlayerVariables.CONQUEST_ABILITY_FLAME_SPARK_RESET, 1);
		_flameSparkUpgrades = variables.getInt(PlayerVariables.CONQUEST_ABILITY_FLAME_SPARK_UPGRADES, 60);
		_fireTotemLevel = variables.getInt(PlayerVariables.CONQUEST_ABILITY_FIRE_TOTEM_LEVEL, 0);
		_fireTotemExp = variables.getInt(PlayerVariables.CONQUEST_ABILITY_FIRE_TOTEM_EXP, 0);
		_fireTotemResetCounter = variables.getInt(PlayerVariables.CONQUEST_ABILITY_FIRE_TOTEM_RESET, 1);
		_fireTotemUpgrades = variables.getInt(PlayerVariables.CONQUEST_ABILITY_FIRE_TOTEM_UPGRADES, 100);
		_battleSoulLevel = variables.getInt(PlayerVariables.CONQUEST_ABILITY_BATTLE_SOUL_LEVEL, 0);
		_battleSoulExp = variables.getInt(PlayerVariables.CONQUEST_ABILITY_BATTLE_SOUL_EXP, 0);
		_battleSoulResetCounter = variables.getInt(PlayerVariables.CONQUEST_ABILITY_BATTLE_SOUL_RESET, 1);
		_battleSoulUpgrades = variables.getInt(PlayerVariables.CONQUEST_ABILITY_BATTLE_SOUL_UPGRADES, 100);
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_ENHANCED_ABILITY_OF_FIRE_OPEN_UI.writeId(this, buffer);
		
		buffer.writeInt(_setEffectLevel);
		
		// Fire Source.
		buffer.writeInt(_fireSourceLevel);
		buffer.writeInt(_fireSourceExp);
		buffer.writeInt(0); // Allow to reset counter.
		buffer.writeInt(0); // Upgrades number.
		
		// Life Source.
		buffer.writeInt(_lifeSourceLevel);
		buffer.writeInt(_lifeSourceExp);
		buffer.writeInt(_lifeSourceResetCounter);
		buffer.writeInt(_lifeSourceUpgrades);
		
		// Flame Spark.
		buffer.writeInt(_flameSparkLevel);
		buffer.writeInt(_flameSparkExp);
		buffer.writeInt(_flameSparkResetCounter);
		buffer.writeInt(_flameSparkUpgrades);
		
		// Fire Totem.
		buffer.writeInt(_fireTotemLevel);
		buffer.writeInt(_fireTotemExp);
		buffer.writeInt(_fireTotemResetCounter);
		buffer.writeInt(_fireTotemUpgrades);
		
		// Battle Soul.
		buffer.writeInt(_battleSoulLevel);
		buffer.writeInt(_battleSoulExp);
		buffer.writeInt(_battleSoulResetCounter);
		buffer.writeInt(_battleSoulUpgrades);
	}
	
	private int checkAbilitySetLevels(PlayerVariables variables)
	{
		if ((variables.getInt(PlayerVariables.CONQUEST_ABILITY_FIRE_SOURCE_LEVEL, 0) == 10) //
			&& (variables.getInt(PlayerVariables.CONQUEST_ABILITY_LIFE_SOURCE_LEVEL, 0) == 10) //
			&& (variables.getInt(PlayerVariables.CONQUEST_ABILITY_FLAME_SPARK_LEVEL, 0) == 10) //
			&& (variables.getInt(PlayerVariables.CONQUEST_ABILITY_FIRE_TOTEM_LEVEL, 0) == 10) //
			&& (variables.getInt(PlayerVariables.CONQUEST_ABILITY_BATTLE_SOUL_LEVEL, 0) == 10))
		{
			return 10;
		}
		else if ((variables.getInt(PlayerVariables.CONQUEST_ABILITY_FIRE_SOURCE_LEVEL, 0) >= 6) //
			&& (variables.getInt(PlayerVariables.CONQUEST_ABILITY_LIFE_SOURCE_LEVEL, 0) >= 6) //
			&& (variables.getInt(PlayerVariables.CONQUEST_ABILITY_FLAME_SPARK_LEVEL, 0) >= 6) //
			&& (variables.getInt(PlayerVariables.CONQUEST_ABILITY_FIRE_TOTEM_LEVEL, 0) >= 6) //
			&& (variables.getInt(PlayerVariables.CONQUEST_ABILITY_BATTLE_SOUL_LEVEL, 0) >= 6))
		{
			return 6;
		}
		else if ((variables.getInt(PlayerVariables.CONQUEST_ABILITY_FIRE_SOURCE_LEVEL, 0) >= 3) //
			&& (variables.getInt(PlayerVariables.CONQUEST_ABILITY_LIFE_SOURCE_LEVEL, 0) >= 3) //
			&& (variables.getInt(PlayerVariables.CONQUEST_ABILITY_FLAME_SPARK_LEVEL, 0) >= 3) //
			&& (variables.getInt(PlayerVariables.CONQUEST_ABILITY_FIRE_TOTEM_LEVEL, 0) >= 3) //
			&& (variables.getInt(PlayerVariables.CONQUEST_ABILITY_BATTLE_SOUL_LEVEL, 0) >= 3))
		{
			return 3;
		}
		else
		{
			return 0;
		}
	}
}
