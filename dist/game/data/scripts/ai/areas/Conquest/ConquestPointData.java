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
package ai.areas.Conquest;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import org.l2jmobius.commons.util.IXmlReader;
import org.l2jmobius.gameserver.config.ConquestConfig;

/**
 * Point data parser.
 * @author Zoey76, CostyKiller
 */
public class ConquestPointData implements IXmlReader
{
	private final Map<Integer, int[]> _pointsInfo = new HashMap<>();
	private final Map<Integer, int[]> _pvpPointsInfo = new HashMap<>();
	
	public ConquestPointData()
	{
		if (ConquestConfig.CONQUEST_SYSTEM_ENABLED)
		{
			load();
		}
	}
	
	@Override
	public void load()
	{
		_pointsInfo.clear();
		_pvpPointsInfo.clear();
		parseDatapackFile("data/scripts/ai/areas/Conquest/conquestPoints.xml");
		printSection("Conquest");
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _pointsInfo.size() + " conquest points reward data.");
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _pvpPointsInfo.size() + " conquest pvp points reward data.");
	}
	
	private void printSection(String section)
	{
		String s = "=[ " + section + " ]";
		while (s.length() < 61)
		{
			s = "-" + s;
		}
		
		LOGGER.info(s);
	}
	
	@Override
	public void parseDocument(Document document, File file)
	{
		for (Node n = document.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if ("list".equals(n.getNodeName()))
			{
				for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
				{
					parsePoint(d);
				}
			}
		}
	}
	
	/**
	 * Parses the point.
	 * @param d the node to parse
	 */
	private void parsePoint(Node d)
	{
		if ("npc".equals(d.getNodeName()))
		{
			final NamedNodeMap attrs = d.getAttributes();
			Node att = attrs.getNamedItem("id");
			if (att == null)
			{
				LOGGER.severe(getClass().getSimpleName() + ": Missing NPC ID, skipping record!");
				return;
			}
			
			final int npcId = Integer.parseInt(att.getNodeValue());
			att = attrs.getNamedItem("personalPoints");
			if (att == null)
			{
				LOGGER.severe("[Conquest Points Info] Missing personal points info for NPC ID " + npcId + ", skipping record");
				return;
			}
			
			att = attrs.getNamedItem("serverPoints");
			if (att == null)
			{
				LOGGER.severe("[Conquest Points Info] Missing server points info for NPC ID " + npcId + ", skipping record");
				return;
			}
			
			final int personalPoints = Integer.parseInt(att.getNodeValue());
			final int serverPoints = Integer.parseInt(att.getNodeValue());
			att = attrs.getNamedItem("zonePoints");
			final int zonePoints = (att == null) ? 0 : Integer.parseInt(att.getNodeValue());
			att = attrs.getNamedItem("zoneId");
			final int zoneId = (att == null) ? 0 : Integer.parseInt(att.getNodeValue());
			_pointsInfo.put(npcId, new int[]
			{
				personalPoints,
				serverPoints,
				zonePoints,
				zoneId,
			});
		}
		
		if ("player".equals(d.getNodeName()))
		{
			final NamedNodeMap attrs = d.getAttributes();
			Node att = attrs.getNamedItem("level");
			if (att == null)
			{
				LOGGER.severe(getClass().getSimpleName() + ": Missing Player level, skipping record!");
				return;
			}
			
			final int level = Integer.parseInt(att.getNodeValue());
			att = attrs.getNamedItem("personalPoints");
			if (att == null)
			{
				LOGGER.severe("[Conquest Points Info] Missing personal points info for PLAYER LEVEL " + level + ", skipping record");
				return;
			}
			
			att = attrs.getNamedItem("serverPoints");
			if (att == null)
			{
				LOGGER.severe("[Conquest Points Info] Missing server points info for PLAYER LEVEL " + level + ", skipping record");
				return;
			}
			
			final int personalPoints = Integer.parseInt(att.getNodeValue());
			final int serverPoints = Integer.parseInt(att.getNodeValue());
			final int bloodyCoins = Integer.parseInt(att.getNodeValue());
			_pvpPointsInfo.put(level, new int[]
			{
				personalPoints,
				serverPoints,
				bloodyCoins,
			});
		}
	}
	
	/**
	 * Gets all the points data.
	 * @return the points data
	 */
	public Map<Integer, int[]> getPointsInfo()
	{
		return _pointsInfo;
	}
	
	/**
	 * Gets all the pvp points data.
	 * @return the pvp points data
	 */
	public Map<Integer, int[]> getPvpPointsInfo()
	{
		return _pvpPointsInfo;
	}
	
	/**
	 * Gets the personal points amount for an specific NPC ID.
	 * @param npcId the NPC ID
	 * @return the personal points amount for an specific NPC ID
	 */
	public int getPersonalPointsAmount(int npcId)
	{
		return _pointsInfo.get(npcId)[0];
	}
	
	/**
	 * Gets the server points amount for the given NPC ID.
	 * @param npcId the NPC ID
	 * @return the server points amount for the given NPC ID
	 */
	public int getServerPointsAmount(int npcId)
	{
		return _pointsInfo.get(npcId)[1];
	}
	
	/**
	 * Gets the zone points amount for the given NPC ID.
	 * @param npcId the NPC ID
	 * @return the zone points for the given NPC ID
	 */
	public int getZonePointsAmount(int npcId)
	{
		return _pointsInfo.get(npcId)[2];
	}
	
	/**
	 * Gets the zone id for the given NPC ID.
	 * @param npcId the NPC ID
	 * @return the zone id for the given NPC ID
	 */
	public int getZoneId(int npcId)
	{
		return _pointsInfo.get(npcId)[3];
	}
	
	/**
	 * Gets the personal points amount for an specific PLAYER LEVEL.
	 * @param level the player level
	 * @return the personal points amount for an specific player level
	 */
	public int getPvpPersonalPointsAmount(int level)
	{
		return _pvpPointsInfo.get(level)[0];
	}
	
	/**
	 * Gets the server points amount for the given PLAYER LEVEL.
	 * @param level the player level
	 * @return the server points amount for an specific player level
	 */
	public int getPvpServerPointsAmount(int level)
	{
		return _pvpPointsInfo.get(level)[1];
	}
	
	/**
	 * Gets the coins amount for PLAYER LEVEL
	 * @param level the player level
	 * @return the bloody coins amount for an specific player level
	 */
	public int getCoinsAmount(int level)
	{
		return _pvpPointsInfo.get(level)[2];
	}
	
	public static ConquestPointData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ConquestPointData INSTANCE = new ConquestPointData();
	}
}
