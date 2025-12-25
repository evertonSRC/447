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
package org.l2jmobius.gameserver.config;

import org.l2jmobius.commons.util.ConfigReader;

/**
 * This class loads all the illusory equipment related configurations.
 * @author Mobius
 */
public class IllusoryEquipmentConfig
{
	// File
	private static final String ILLUSORY_EQUIPMENT_CONFIG_FILE = "./config/IllusoryEquipment.ini";
	
	// Constants
	public static boolean ILLUSORY_EQUIPMENT_ENABLED;
	public static int ILLUSORY_EQUIPMENT_EVENT_DURATION;
	public static boolean ILLUSORY_EQUIPMENT_EVENT_DEBUG_ENABLED;
	public static int ILLUSORY_EQUIPMENT_EVENT_POINTS_LIMIT;
	
	public static void load()
	{
		final ConfigReader config = new ConfigReader(ILLUSORY_EQUIPMENT_CONFIG_FILE);
		ILLUSORY_EQUIPMENT_ENABLED = config.getBoolean("Enabled", false);
		ILLUSORY_EQUIPMENT_EVENT_DURATION = config.getInt("Duration", 5);
		ILLUSORY_EQUIPMENT_EVENT_DEBUG_ENABLED = config.getBoolean("Debug", false);
		ILLUSORY_EQUIPMENT_EVENT_POINTS_LIMIT = config.getInt("PointsLimit", 600);
	}
}
