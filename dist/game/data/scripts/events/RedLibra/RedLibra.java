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
package events.RedLibra;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.l2jmobius.commons.database.DatabaseFactory;
import org.l2jmobius.gameserver.data.enums.CategoryType;
import org.l2jmobius.gameserver.data.xml.CategoryData;
import org.l2jmobius.gameserver.data.xml.ClassListData;
import org.l2jmobius.gameserver.data.xml.ExperienceData;
import org.l2jmobius.gameserver.data.xml.SkillTreeData;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.enums.creature.Race;
import org.l2jmobius.gameserver.model.actor.enums.player.PlayerClass;
import org.l2jmobius.gameserver.model.actor.enums.player.SubclassInfoType;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.ListenerRegisterType;
import org.l2jmobius.gameserver.model.events.annotations.Id;
import org.l2jmobius.gameserver.model.events.annotations.RegisterEvent;
import org.l2jmobius.gameserver.model.events.annotations.RegisterType;
import org.l2jmobius.gameserver.model.events.holders.OnDailyReset;
import org.l2jmobius.gameserver.model.events.holders.actor.npc.OnNpcMenuSelect;
import org.l2jmobius.gameserver.model.item.enums.ItemProcessType;
import org.l2jmobius.gameserver.model.script.LongTimeEvent;
import org.l2jmobius.gameserver.model.skill.SkillCaster;
import org.l2jmobius.gameserver.model.skill.holders.SkillHolder;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.AcquireSkillList;
import org.l2jmobius.gameserver.network.serverpackets.ExSubjobInfo;
import org.l2jmobius.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * Red Libra<br>
 * @URL https://4ga.me/3OsQCDf
 * @author Mobius, Tanatos
 */
public class RedLibra extends LongTimeEvent
{
	// NPCs
	private static final int RED = 34210;
	private static final int GREEN = 34211;
	private static final int BLACK = 34212;
	private static final int PINK = 34213;
	private static final int BLUE = 34214;
	
	// Items
	private static final int STONE_OF_DESTINY = 17722;
	private static final int CHAOS_POMANDER = 37375;
	
	// Skills
	private static final SkillHolder GREEN_BUFF = new SkillHolder(32266, 1);
	private static final SkillHolder RED_BUFF = new SkillHolder(32264, 1);
	
	// Misc
	private static final String STONE_OF_DESTINY_VAR = "STONE_OF_DESTINY_RECEIVED";
	private static final String GREEN_BUFF_VAR = "GREEN_BUFF_RECEIVED";
	private static final int PLAYER_MIN_LEVEL = 105;
	private static final List<PlayerClass> dualClassList = new ArrayList<>();
	static
	{
		dualClassList.addAll(Arrays.asList(PlayerClass.AEORE_CARDINAL, PlayerClass.AEORE_EVA_SAINT, PlayerClass.AEORE_SHILLIEN_SAINT));
		dualClassList.addAll(Arrays.asList(PlayerClass.FEOH_ARCHMAGE, PlayerClass.FEOH_SOULTAKER, PlayerClass.FEOH_MYSTIC_MUSE, PlayerClass.FEOH_STORM_SCREAMER, PlayerClass.FEOH_SOUL_HOUND));
		dualClassList.addAll(Arrays.asList(PlayerClass.ISS_HIEROPHANT, PlayerClass.ISS_SWORD_MUSE, PlayerClass.ISS_SPECTRAL_DANCER, PlayerClass.ISS_DOOMCRYER));
		dualClassList.addAll(Arrays.asList(PlayerClass.OTHELL_ADVENTURER, PlayerClass.OTHELL_WIND_RIDER, PlayerClass.OTHELL_GHOST_HUNTER, PlayerClass.OTHELL_FORTUNE_SEEKER));
		dualClassList.addAll(Arrays.asList(PlayerClass.SIGEL_PHOENIX_KNIGHT, PlayerClass.SIGEL_HELL_KNIGHT, PlayerClass.SIGEL_EVA_TEMPLAR, PlayerClass.SIGEL_SHILLIEN_TEMPLAR));
		dualClassList.addAll(Arrays.asList(PlayerClass.TYRR_DUELIST, PlayerClass.TYRR_DREADNOUGHT, PlayerClass.TYRR_TITAN, PlayerClass.TYRR_GRAND_KHAVATARI, PlayerClass.TYRR_DOOMBRINGER));
		dualClassList.addAll(Arrays.asList(PlayerClass.WYNN_ARCANA_LORD, PlayerClass.WYNN_ELEMENTAL_MASTER, PlayerClass.WYNN_SPECTRAL_MASTER));
		dualClassList.addAll(Arrays.asList(PlayerClass.YUL_SAGITTARIUS, PlayerClass.YUL_MOONLIGHT_SENTINEL, PlayerClass.YUL_GHOST_SENTINEL, PlayerClass.YUL_TRICKSTER));
	}
	private static final int REAWAKEN_PRICE = 300000000;
	private static final int RED_BUFF_PRICE = 100000000;
	private static final Location ERATON_LOC = new Location(147342, 23750, -1984);
	
	private RedLibra()
	{
		addStartNpc(RED, GREEN, BLACK, PINK, BLUE);
		addFirstTalkId(RED, GREEN, BLACK, PINK, BLUE);
		addTalkId(RED, GREEN, BLACK, PINK, BLUE);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		String htmltext = null;
		switch (event)
		{
			case "34210-1.htm":
			case "34210-2.htm":
			case "34210-3.htm":
			case "34210-4.htm":
			case "34210-5.htm":
			case "34211-1.htm":
			case "34211-2.htm":
			case "34211-3.htm":
			case "34211-4.htm":
			case "34211-5.htm":
			case "34212-1.htm":
			case "34212-2.htm":
			case "34212-3.htm":
			case "34212-4.htm":
			case "34212-5.htm":
			case "34212-6.htm":
			case "34212-7.htm":
			case "34212-8.htm":
			case "34212-9.htm":
			case "34212-10.htm":
			case "34214-1.htm":
			case "34214-2.htm":
			case "34214-3.htm":
			case "34214-4.htm":
			case "34214-5.htm":
			case "34214-6.htm":
			{
				htmltext = event;
				break;
			}
			case "freeStone":
			{
				if (npc.getId() != RED)
				{
					break;
				}
				
				if (player.getAccountVariables().getBoolean(STONE_OF_DESTINY_VAR, false))
				{
					player.sendMessage("This account has already received a gift. The gift can only be given once per account");
					htmltext = "34210-stoneGiven.htm";
					break;
				}
				
				player.getAccountVariables().set(STONE_OF_DESTINY_VAR, true);
				player.getAccountVariables().storeMe();
				giveItems(player, STONE_OF_DESTINY, 1);
				htmltext = "34210-stoneGiven.htm";
				break;
			}
			case "teleport":
			{
				if (npc.getId() != RED)
				{
					break;
				}
				
				player.teleToLocation(ERATON_LOC);
				break;
			}
			case "redBuff":
			{
				if (player.getAdena() < RED_BUFF_PRICE)
				{
					htmltext = "34210-noAdena.htm";
					break;
				}
				
				SkillCaster.triggerCast(player, player, RED_BUFF.getSkill());
				player.reduceAdena(ItemProcessType.FEE, RED_BUFF_PRICE, npc, true);
				htmltext = "34210-buffGiven.htm";
				break;
			}
			case "reawakenDualclass":
			{
				if (!player.hasDualClass() || (player.getLevel() < 85) || !player.isDualClassActive() || !player.isAwakenedClass())
				{
					htmltext = "34211-noDual.htm";
				}
				else if (player.getAdena() < REAWAKEN_PRICE)
				{
					htmltext = "34211-noAdena.htm";
				}
				else if (player.isTransformed() || player.hasSummon())
				{
					htmltext = "34211-noTransform.htm";
				}
				else if (!player.isInventoryUnder80(true) || (player.getWeightPenalty() >= 2))
				{
					player.sendPacket(SystemMessageId.NOT_ENOUGH_SPACE_IN_THE_INVENTORY);
					player.sendMessage("Not enough space in the inventory. Unable to process this request until your inventory's weight and slot count are less than 80 percent of capacity.");
				}
				else
				{
					htmltext = "34211-reawaken.htm";
				}
				break;
			}
			case "reawakenConfirm":
			{
				if (!player.hasDualClass() || (player.getLevel() < 85) || !player.isDualClassActive() || !player.isAwakenedClass())
				{
					htmltext = "34211-noDual.htm";
				}
				else if (player.getAdena() < REAWAKEN_PRICE)
				{
					htmltext = "34211-noAdena.htm";
				}
				else if (player.isTransformed() || player.hasSummon())
				{
					htmltext = "34211-noTransform.htm";
				}
				else if (!player.isInventoryUnder80(true) || (player.getWeightPenalty() >= 2))
				{
					player.sendPacket(SystemMessageId.NOT_ENOUGH_SPACE_IN_THE_INVENTORY);
					player.sendMessage("Not enough space in the inventory. Unable to process this request until your inventory's weight and slot count are less than 80 percent of capacity.");
				}
				else
				{
					htmltext = "34211-reawakenList.htm";
				}
				break;
			}
			case "reawaken_SIXTH_SIGEL_GROUP":
			case "reawaken_SIXTH_TIR_GROUP":
			case "reawaken_SIXTH_OTHEL_GROUP":
			case "reawaken_SIXTH_YR_GROUP":
			case "reawaken_SIXTH_FEOH_GROUP":
			case "reawaken_SIXTH_IS_GROUP":
			case "reawaken_SIXTH_WYNN_GROUP":
			case "reawaken_SIXTH_EOLH_GROUP":
			{
				final CategoryType cType = CategoryType.valueOf(event.replace("reawaken_", ""));
				if (cType == null)
				{
					LOGGER.warning(getClass().getSimpleName() + ": Cannot parse CategoryType, event: " + event);
				}
				
				final StringBuilder sb = new StringBuilder();
				final NpcHtmlMessage html = getNpcHtmlMessage(player, npc, "34211-reawakenClassList.htm");
				for (PlayerClass dualClasses : getDualClasses(player, cType))
				{
					if (dualClasses != null)
					{
						sb.append("<button value=\"" + ClassListData.getInstance().getClass(dualClasses.getId()).getClassName() + "\" action=\"bypass -h menu_select?ask=1&reply=" + dualClasses.getId() + "\" width=\"200\" height=\"31\" back=\"L2UI_CT1.HtmlWnd_DF_Awake_Down\" fore=\"L2UI_CT1.HtmlWnd_DF_Awake\"><br>");
					}
				}
				
				html.replace("%dualclassList%", sb.toString());
				player.sendPacket(html);
				break;
			}
			case "greenBuff":
			{
				if (npc.getId() != GREEN)
				{
					break;
				}
				
				if (player.getLevel() < PLAYER_MIN_LEVEL)
				{
					htmltext = "34211-noLevel.htm";
					break;
				}
				
				if (player.getAccountVariables().getBoolean(GREEN_BUFF_VAR, false))
				{
					player.sendMessage("This account has already received a gift. The gift can only be given once per account");
					htmltext = "34211-buffGiven.htm";
					break;
				}
				
				player.getAccountVariables().set(GREEN_BUFF_VAR, true);
				player.getAccountVariables().storeMe();
				SkillCaster.triggerCast(player, player, GREEN_BUFF.getSkill());
				htmltext = "34211-buffGiven.htm";
				break;
			}
		}
		
		return htmltext;
	}
	
	@RegisterEvent(EventType.ON_NPC_MENU_SELECT)
	@RegisterType(ListenerRegisterType.NPC)
	@Id(GREEN)
	public void onNpcMenuSelect(OnNpcMenuSelect event)
	{
		final Player player = event.getTalker();
		final Npc npc = event.getNpc();
		final int ask = event.getAsk();
		
		switch (ask)
		{
			case 1:
			{
				final int classId = event.getReply();
				if (!player.hasDualClass() || (player.getLevel() < 85) || !player.isDualClassActive() || !player.isAwakenedClass() || (player.getAdena() < REAWAKEN_PRICE) || player.isTransformed() || player.hasSummon() || !player.isInventoryUnder80(true) || (player.getWeightPenalty() >= 2))
				{
					break;
				}
				
				if (!getDualClasses(player, null).contains(PlayerClass.getPlayerClass(classId)))
				{
					break;
				}
				
				player.reduceAdena(ItemProcessType.FEE, REAWAKEN_PRICE, npc, true);
				final int level = player.getLevel();
				
				final int classIndex = player.getClassIndex();
				if (player.modifySubClass(classIndex, classId, true))
				{
					player.abortCast();
					player.stopAllEffectsExceptThoseThatLastThroughDeath();
					player.stopAllEffects();
					player.stopCubics();
					player.setActiveClass(classIndex);
					player.sendPacket(new ExSubjobInfo(player, SubclassInfoType.CLASS_CHANGED));
					player.sendPacket(getNpcHtmlMessage(player, npc, "34211-reawakenSuccess.htm"));
					SkillTreeData.getInstance().cleanSkillUponChangeClass(player);
					player.restoreDualSkills();
					player.sendPacket(new AcquireSkillList(player));
					player.sendSkillList();
					takeItems(player, CHAOS_POMANDER, -1);
					giveItems(player, CHAOS_POMANDER, 2);
				}
				
				addExpAndSp(player, (ExperienceData.getInstance().getExpForLevel(level) + 1) - player.getExp(), 0);
				break;
			}
		}
	}
	
	public List<PlayerClass> getAvailableDualclasses(Player player)
	{
		final List<PlayerClass> dualClasses = new ArrayList<>();
		for (PlayerClass ClassId : PlayerClass.values())
		{
			if ((ClassId.getRace() != Race.ERTHEIA) && CategoryData.getInstance().isInCategory(CategoryType.SIXTH_CLASS_GROUP, ClassId.getId()) && (ClassId.getId() != player.getPlayerClass().getId()))
			{
				dualClasses.add(ClassId);
			}
		}
		
		return dualClasses;
	}
	
	private List<PlayerClass> getDualClasses(Player player, CategoryType cType)
	{
		final List<PlayerClass> tempList = new ArrayList<>();
		final int baseClassId = player.getBaseClass();
		final int dualClassId = player.getPlayerClass().getId();
		for (PlayerClass temp : dualClassList)
		{
			if ((temp.getId() != baseClassId) && (temp.getId() != dualClassId) && ((cType == null) || CategoryData.getInstance().isInCategory(cType, temp.getId())))
			{
				tempList.add(temp);
			}
		}
		
		return tempList;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		return npc.getId() + "-1.htm";
	}
	
	private NpcHtmlMessage getNpcHtmlMessage(Player player, Npc npc, String fileName)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
		final String text = getHtm(player, fileName);
		if (text == null)
		{
			LOGGER.info("Cannot find HTML file for " + RedLibra.class.getSimpleName() + " AI: " + fileName);
			return null;
		}
		
		html.setHtml(text);
		return html;
	}
	
	@RegisterEvent(EventType.ON_DAILY_RESET)
	@RegisterType(ListenerRegisterType.GLOBAL)
	public void onDailyReset(OnDailyReset event)
	{
		if (!isEventPeriod())
		{
			return;
		}
		
		// Update data for offline players.
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement("DELETE FROM account_gsdata WHERE var = ? AND account_name NOT IN (SELECT account_name FROM characters WHERE online = 1)"))
		{
			ps.setString(1, GREEN_BUFF_VAR);
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.warning(getClass().getSimpleName() + ": Could not reset variables: " + e.getMessage());
		}
		
		// Update data for online players.
		for (Player player : World.getInstance().getPlayers())
		{
			player.getAccountVariables().remove(GREEN_BUFF_VAR);
		}
		
		LOGGER.info(getClass().getSimpleName() + " has been reset.");
	}
	
	public static void main(String[] args)
	{
		new RedLibra();
	}
}
