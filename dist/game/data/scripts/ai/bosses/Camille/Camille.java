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
package ai.bosses.Camille;

import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.instance.Door;
import org.l2jmobius.gameserver.model.instancezone.Instance;
import org.l2jmobius.gameserver.model.script.InstanceScript;
import org.l2jmobius.gameserver.network.enums.Movie;

/**
 * Camille instance zone.
 * @author Sero
 * @URL https://www.youtube.com/watch?v=jpv9S_xQVrA
 */
public class Camille extends InstanceScript
{
	// NPCs
	private static final int CAMILLE = 26236;
	private static final int MAMUT = 26243;
	private static final int ISBURG = 26244;
	private static final int TRANSMISSION_UNIT = 34324;
	private static final int ERDA = 34319;
	
	// Locations
	private static final Location ENTER_LOCATION = new Location(-245768, 147832, 4662);
	private static final Location CAMILLE_LOCATION = new Location(-245752, 150392, 11845);
	
	// Misc
	private static final int TEMPLATE_ID = 266;
	
	public Camille()
	{
		super(TEMPLATE_ID);
		addStartNpc(ERDA);
		addTalkId(ERDA, TRANSMISSION_UNIT);
		addFirstTalkId(TRANSMISSION_UNIT);
		addKillId(CAMILLE, MAMUT, ISBURG);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		switch (event)
		{
			case "enterInstance":
			{
				enterInstance(player, npc, TEMPLATE_ID);
				final Instance world = player.getInstanceWorld();
				if (isInInstance(world))
				{
					world.getPlayers().forEach(p -> p.teleToLocation(ENTER_LOCATION));
					world.getDoors().forEach(Door::closeMe);
				}
				break;
			}
			case "teleup":
			{
				final Instance world = npc.getInstanceWorld();
				if (isInInstance(world) && (npc.getId() == TRANSMISSION_UNIT))
				{
					world.getPlayers().forEach(p -> p.teleToLocation(CAMILLE_LOCATION));
					world.getDoors().forEach(Door::closeMe);
				}
				break;
			}
		}
		
		return super.onEvent(event, npc, player);
	}
	
	@Override
	public void onKill(Npc npc, Player player, boolean isSummon)
	{
		final Instance world = npc.getInstanceWorld();
		if (isInInstance(world))
		{
			switch (npc.getId())
			{
				case MAMUT:
				{
					world.openCloseDoor(world.getTemplateParameters().getInt("firstDoorId"), true);
					world.openCloseDoor(world.getTemplateParameters().getInt("secondDoorId"), true);
					world.setReenterTime();
					break;
				}
				case ISBURG:
				{
					world.spawnGroup("teleport");
					world.setReenterTime();
					break;
				}
				case CAMILLE:
				{
					playMovie(world, Movie.SC_CAMILLE_ENDING);
					world.finishInstance();
					break;
				}
			}
		}
	}
	
	public static void main(String[] args)
	{
		new Camille();
	}
}
