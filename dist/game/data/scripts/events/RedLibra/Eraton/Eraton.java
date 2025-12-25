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
package events.RedLibra.Eraton;

import java.util.List;

import org.l2jmobius.gameserver.config.PlayerConfig;
import org.l2jmobius.gameserver.data.xml.ClassListData;
import org.l2jmobius.gameserver.data.xml.SkillData;
import org.l2jmobius.gameserver.data.xml.SkillTreeData;
import org.l2jmobius.gameserver.model.SkillLearn;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.enums.player.PlayerClass;
import org.l2jmobius.gameserver.model.actor.enums.player.SubclassInfoType;
import org.l2jmobius.gameserver.model.actor.holders.player.Shortcut;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.ListenerRegisterType;
import org.l2jmobius.gameserver.model.events.annotations.Id;
import org.l2jmobius.gameserver.model.events.annotations.RegisterEvent;
import org.l2jmobius.gameserver.model.events.annotations.RegisterType;
import org.l2jmobius.gameserver.model.events.holders.actor.npc.OnNpcMenuSelect;
import org.l2jmobius.gameserver.model.item.enums.ItemProcessType;
import org.l2jmobius.gameserver.model.item.holders.ItemHolder;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.model.olympiad.Hero;
import org.l2jmobius.gameserver.model.olympiad.Olympiad;
import org.l2jmobius.gameserver.model.script.Script;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.network.Disconnection;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.ExSubjobInfo;
import org.l2jmobius.gameserver.network.serverpackets.ExUserInfoInvenWeight;
import org.l2jmobius.gameserver.network.serverpackets.LeaveWorld;
import org.l2jmobius.gameserver.network.serverpackets.NpcHtmlMessage;
import org.l2jmobius.gameserver.network.serverpackets.SocialAction;
import org.l2jmobius.gameserver.taskmanagers.AutoUseTaskManager;

/**
 * Red Libra<br>
 * Step 1.<br>
 * Contact Red to move to Eraton in the Aden Temple where the class change is taking place. (Must have the Stone of Destiny and the Main Class cloak.)<br>
 * Step 2.<br>
 * Contact Eraton in the Aden Temple to learn the details of the class change. Once everything is ready, select the desired class and confirm your choice.<br>
 * Step 3.<br>
 * Congratulations! The main character class has been changed. The process is accompanied by a distinctive animation with a character jumping up.
 * @author Index, Gaikotsu
 */
public class Eraton extends Script
{
	// NPC
	private static final int ERATON = 34584;
	
	// Items
	private static final ItemHolder STONE_OF_DESTINY = new ItemHolder(17722, 1);
	private static final ItemHolder CHAOS_POMANDER = new ItemHolder(37374, 2);
	
	// Misc
	private static final String ITEM_NAME_PATTERN = "&#" + STONE_OF_DESTINY.getId() + ";";
	
	private Eraton()
	{
		addStartNpc(ERATON);
		addFirstTalkId(ERATON);
		addTalkId(ERATON);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		String htmltext = null;
		switch (event)
		{
			case ("back"):
			{
				htmltext = getHtm(player, "34584.html").replace("%required_item%", ITEM_NAME_PATTERN);
				break;
			}
			case ("ERATON_HELP"):
			{
				// TODO: NEED TO BE FOUND!!
				htmltext = getHtm(player, "34584-9.html").replace("%required_item%", ITEM_NAME_PATTERN).replace("%required_item_count%", String.valueOf(STONE_OF_DESTINY.getCount()));
				break;
			}
			case ("ERATON_LIST"):
			{
				final StringBuilder sb = new StringBuilder();
				sb.append("<button align=\"LEFT\" icon=\"NORMAL\" action=\"bypass -h Quest Eraton ERATON_" + "SIGEL" + "\">Sigel Knight</button>");
				sb.append("<button align=\"LEFT\" icon=\"NORMAL\" action=\"bypass -h Quest Eraton ERATON_" + "TYRR" + "\">Tyrr Warrior</button>");
				sb.append("<button align=\"LEFT\" icon=\"NORMAL\" action=\"bypass -h Quest Eraton ERATON_" + "OTHELL" + "\">Othell Rogue</button>");
				sb.append("<button align=\"LEFT\" icon=\"NORMAL\" action=\"bypass -h Quest Eraton ERATON_" + "YUL" + "\">Yul Archer</button>");
				sb.append("<button align=\"LEFT\" icon=\"NORMAL\" action=\"bypass -h Quest Eraton ERATON_" + "FEOH" + "\">Feoh Wizard</button>");
				sb.append("<button align=\"LEFT\" icon=\"NORMAL\" action=\"bypass -h Quest Eraton ERATON_" + "ISS" + "\">Iss Enchanter</button>");
				sb.append("<button align=\"LEFT\" icon=\"NORMAL\" action=\"bypass -h Quest Eraton ERATON_" + "WYNN" + "\">Wynn Summoner</button>");
				sb.append("<button align=\"LEFT\" icon=\"NORMAL\" action=\"bypass -h Quest Eraton ERATON_" + "AEORE" + "\">Aeore Healer</button>");
				sb.append("<button align=\"LEFT\" icon=\"NORMAL\" action=\"bypass -h Quest Eraton ERATON_" + "SHINEMAKER" + "\">Shine Maker</button>");
				htmltext = getHtm(player, "34584-1.html").replace("%CLASS_NAMES%", sb.toString());
				break;
			}
			case ("ERATON_SIGEL"):
			{
				final StringBuilder sb = new StringBuilder();
				sb.append("<button align=\"LEFT\" icon=\"NORMAL\" action=\"bypass -h Quest Eraton ERATON_" + PlayerClass.SIGEL_PHOENIX_KNIGHT + "\">Sigel Phoenix Knight</button>");
				sb.append("<button align=\"LEFT\" icon=\"NORMAL\" action=\"bypass -h Quest Eraton ERATON_" + PlayerClass.SIGEL_HELL_KNIGHT + "\">Sigel Hell Knight</button>");
				sb.append("<button align=\"LEFT\" icon=\"NORMAL\" action=\"bypass -h Quest Eraton ERATON_" + PlayerClass.SIGEL_EVA_TEMPLAR + "\">Sigel Eva Templar</button>");
				sb.append("<button align=\"LEFT\" icon=\"NORMAL\" action=\"bypass -h Quest Eraton ERATON_" + PlayerClass.SIGEL_SHILLIEN_TEMPLAR + "\">Sigel Shillien Templar</button>");
				
				// Death Knights became able to use the Stone of Destiny item with the Death Knight Reborn Content Update released on August 1st 2023.
				sb.append("<button align=\"LEFT\" icon=\"NORMAL\" action=\"bypass -h Quest Eraton ERATON_" + PlayerClass.SIGEL_DEATH_KNIGHT + "\">Sigel Death Knight</button>");
				htmltext = getHtm(player, "34584-2.html").replace("%CLASS_LIST%", sb.toString());
				break;
			}
			case ("ERATON_TYRR"):
			{
				final StringBuilder sb = new StringBuilder();
				sb.append("<button align=\"LEFT\" icon=\"NORMAL\" action=\"bypass -h Quest Eraton ERATON_" + PlayerClass.TYRR_DUELIST + "\">Tyrr Duelist</button>");
				sb.append("<button align=\"LEFT\" icon=\"NORMAL\" action=\"bypass -h Quest Eraton ERATON_" + PlayerClass.TYRR_DREADNOUGHT + "\">Tyrr Drearnought</button>");
				sb.append("<button align=\"LEFT\" icon=\"NORMAL\" action=\"bypass -h Quest Eraton ERATON_" + PlayerClass.TYRR_TITAN + "\">Tyrr Titan</button>");
				sb.append("<button align=\"LEFT\" icon=\"NORMAL\" action=\"bypass -h Quest Eraton ERATON_" + PlayerClass.TYRR_GRAND_KHAVATARI + "\">Tyrr Grand Khavatari</button>");
				sb.append("<button align=\"LEFT\" icon=\"NORMAL\" action=\"bypass -h Quest Eraton ERATON_" + PlayerClass.TYRR_DOOMBRINGER + "\">Tyrr Doombringer</button>");
				sb.append("<button align=\"LEFT\" icon=\"NORMAL\" action=\"bypass -h Quest Eraton ERATON_" + PlayerClass.TYRR_MAESTRO + "\">Tyrr Maestro</button>");
				sb.append("<button align=\"LEFT\" icon=\"NORMAL\" action=\"bypass -h Quest Eraton ERATON_" + PlayerClass.EVISCERATOR + "\">Eviscerator</button>");
				
				htmltext = getHtm(player, "34584-2.html").replace("%CLASS_LIST%", sb.toString());
				break;
			}
			case ("ERATON_OTHELL"):
			{
				final StringBuilder sb = new StringBuilder();
				sb.append("<button align=\"LEFT\" icon=\"NORMAL\" action=\"bypass -h Quest Eraton ERATON_" + PlayerClass.OTHELL_ADVENTURER + "\">Othell Adventurer</button>");
				sb.append("<button align=\"LEFT\" icon=\"NORMAL\" action=\"bypass -h Quest Eraton ERATON_" + PlayerClass.OTHELL_WIND_RIDER + "\">Othell Wind Rider</button>");
				sb.append("<button align=\"LEFT\" icon=\"NORMAL\" action=\"bypass -h Quest Eraton ERATON_" + PlayerClass.OTHELL_GHOST_HUNTER + "\">Othell Ghost Hunter</button>");
				sb.append("<button align=\"LEFT\" icon=\"NORMAL\" action=\"bypass -h Quest Eraton ERATON_" + PlayerClass.OTHELL_FORTUNE_SEEKER + "\">Othell Fortune Seeker</button>");
				
				htmltext = getHtm(player, "34584-2.html").replace("%CLASS_LIST%", sb.toString());
				break;
			}
			case ("ERATON_YUL"):
			{
				final StringBuilder sb = new StringBuilder();
				sb.append("<button align=\"LEFT\" icon=\"NORMAL\" action=\"bypass -h Quest Eraton ERATON_" + PlayerClass.YUL_SAGITTARIUS + "\">Yul Sagittarius</button>");
				sb.append("<button align=\"LEFT\" icon=\"NORMAL\" action=\"bypass -h Quest Eraton ERATON_" + PlayerClass.YUL_MOONLIGHT_SENTINEL + "\">Yul Moonlight Sentinel</button>");
				sb.append("<button align=\"LEFT\" icon=\"NORMAL\" action=\"bypass -h Quest Eraton ERATON_" + PlayerClass.YUL_GHOST_SENTINEL + "\">Yul Ghost Sentinel</button>");
				sb.append("<button align=\"LEFT\" icon=\"NORMAL\" action=\"bypass -h Quest Eraton ERATON_" + PlayerClass.YUL_TRICKSTER + "\">Yul Trickster</button>");
				htmltext = getHtm(player, "34584-2.html").replace("%CLASS_LIST%", sb.toString());
				break;
			}
			case ("ERATON_FEOH"):
			{
				final StringBuilder sb = new StringBuilder();
				sb.append("<button align=\"LEFT\" icon=\"NORMAL\" action=\"bypass -h Quest Eraton ERATON_" + PlayerClass.FEOH_ARCHMAGE + "\">Feoh Archmage</button>");
				sb.append("<button align=\"LEFT\" icon=\"NORMAL\" action=\"bypass -h Quest Eraton ERATON_" + PlayerClass.FEOH_SOULTAKER + "\">Feoh Soultaker</button>");
				sb.append("<button align=\"LEFT\" icon=\"NORMAL\" action=\"bypass -h Quest Eraton ERATON_" + PlayerClass.FEOH_MYSTIC_MUSE + "\">Feoh Mystic Muse</button>");
				sb.append("<button align=\"LEFT\" icon=\"NORMAL\" action=\"bypass -h Quest Eraton ERATON_" + PlayerClass.FEOH_STORM_SCREAMER + "\">Feoh Storm Screamer</button>");
				sb.append("<button align=\"LEFT\" icon=\"NORMAL\" action=\"bypass -h Quest Eraton ERATON_" + PlayerClass.FEOH_SOUL_HOUND + "\">Feoh SoulHound</button>");
				sb.append("<button align=\"LEFT\" icon=\"NORMAL\" action=\"bypass -h Quest Eraton ERATON_" + PlayerClass.SAYHA_SEER + "\">Sayha's Seer</button>");
				
				htmltext = getHtm(player, "34584-2.html").replace("%CLASS_LIST%", sb.toString());
				break;
			}
			case ("ERATON_ISS"):
			{
				final StringBuilder sb = new StringBuilder();
				sb.append("<button align=\"LEFT\" icon=\"NORMAL\" action=\"bypass -h Quest Eraton ERATON_" + PlayerClass.ISS_HIEROPHANT + "\">Iss Hierophant</button>");
				sb.append("<button align=\"LEFT\" icon=\"NORMAL\" action=\"bypass -h Quest Eraton ERATON_" + PlayerClass.ISS_SWORD_MUSE + "\">Iss Sword Muse</button>");
				sb.append("<button align=\"LEFT\" icon=\"NORMAL\" action=\"bypass -h Quest Eraton ERATON_" + PlayerClass.ISS_SPECTRAL_DANCER + "\">Iss Spectral Dancer</button>");
				sb.append("<button align=\"LEFT\" icon=\"NORMAL\" action=\"bypass -h Quest Eraton ERATON_" + PlayerClass.ISS_DOOMCRYER + "\">Iss Doomcryer</button>");
				sb.append("<button align=\"LEFT\" icon=\"NORMAL\" action=\"bypass -h Quest Eraton ERATON_" + PlayerClass.ISS_DOMINATOR + "\">Iss Dominator</button>");
				
				htmltext = getHtm(player, "34584-2.html").replace("%CLASS_LIST%", sb.toString());
				break;
			}
			case ("ERATON_WYNN"):
			{
				final StringBuilder sb = new StringBuilder();
				sb.append("<button align=\"LEFT\" icon=\"NORMAL\" action=\"bypass -h Quest Eraton ERATON_" + PlayerClass.WYNN_ARCANA_LORD + "\">Wynn Arcana Lord</button>");
				sb.append("<button align=\"LEFT\" icon=\"NORMAL\" action=\"bypass -h Quest Eraton ERATON_" + PlayerClass.WYNN_ELEMENTAL_MASTER + "\">Wynn Elemental Master</button>");
				sb.append("<button align=\"LEFT\" icon=\"NORMAL\" action=\"bypass -h Quest Eraton ERATON_" + PlayerClass.WYNN_SPECTRAL_MASTER + "\">Wynn Spectral Master</button>");
				htmltext = getHtm(player, "34584-2.html").replace("%CLASS_LIST%", sb.toString());
				break;
			}
			case ("ERATON_AEORE"):
			{
				final StringBuilder sb = new StringBuilder();
				sb.append("<button align=\"LEFT\" icon=\"NORMAL\" action=\"bypass -h Quest Eraton ERATON_" + PlayerClass.AEORE_CARDINAL + "\">Aeore Cardinal</button>");
				sb.append("<button align=\"LEFT\" icon=\"NORMAL\" action=\"bypass -h Quest Eraton ERATON_" + PlayerClass.AEORE_EVA_SAINT + "\">Aeore Eva Saint</button>");
				sb.append("<button align=\"LEFT\" icon=\"NORMAL\" action=\"bypass -h Quest Eraton ERATON_" + PlayerClass.AEORE_SHILLIEN_SAINT + "\">Aeore Shillien Saint</button>");
				htmltext = getHtm(player, "34584-2.html").replace("%CLASS_LIST%", sb.toString());
				break;
			}
			case ("ERATON_SHINEMAKER"):
			{
				final StringBuilder sb = new StringBuilder();
				sb.append("<button align=\"LEFT\" icon=\"NORMAL\" action=\"bypass -h Quest Eraton ERATON_" + PlayerClass.SHINE_MAKER + "\">Shine Maker</button>");
				htmltext = getHtm(player, "34584-2.html").replace("%CLASS_LIST%", sb.toString());
				break;
			}
			default:
			{
				final PlayerClass playerClass = PlayerClass.valueOf(event.replace("ERATON_", ""));
				if (playerClass != null)
				{
					final StringBuilder sb = new StringBuilder();
					sb.append("<Button ALIGN=LEFT ICON=NORMAL action=\"bypass -h menu_select?ask=1&reply=" + playerClass.getId() + "\">" + "Select " + ClassListData.getInstance().getClass(playerClass.getId()).getClassName() + "</Button>");
					htmltext = getHtm(player, "34584-3.html").replace("%CONFIRM_BUTTON%", sb.toString());
				}
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		String htmltext;
		htmltext = getHtm(player, "34584.html").replace("%required_item%", ITEM_NAME_PATTERN);
		return htmltext;
	}
	
	@RegisterEvent(EventType.ON_NPC_MENU_SELECT)
	@RegisterType(ListenerRegisterType.NPC)
	@Id(ERATON)
	public void onNpcMenuSelect(OnNpcMenuSelect event)
	{
		final Player player = event.getTalker();
		final int ask = event.getAsk();
		switch (ask)
		{
			case 1:
			{
				final int classId = event.getReply();
				if (!player.isAwakenedClass())
				{
					player.sendPacket(new NpcHtmlMessage(getHtm(player, "34584-7.html")));
					return;
				}
				
				if (!hasQuestItems(player, STONE_OF_DESTINY.getId()))
				{
					player.sendPacket(new NpcHtmlMessage(getHtm(player, "34584-4.html").replace("%required_item%", ITEM_NAME_PATTERN).replace("%required_item_count%", String.valueOf(STONE_OF_DESTINY.getCount()))));
					return;
				}
				
				if ((player.getDualClass() != null) && (player.getDualClass().getId() == classId))
				{
					player.sendPacket(new NpcHtmlMessage(getHtm(player, "34584-6.html").replace("%s1%", "Main").replace("%s2%", "Dual")));
					return;
				}
				
				if ((player.getClass() != null) && (player.getBaseClass() == classId))
				{
					player.sendPacket(new NpcHtmlMessage(getHtm(player, "34584-6.html").replace("%s1%", "Main").replace("%s2%", "Current")));
					return;
				}
				
				if (player.isTransformed() || player.hasSummon() || player.isDualClassActive())
				{
					player.sendPacket(new NpcHtmlMessage(getHtm(player, "34584-5.html")));
					return;
				}
				
				if (player.isHero() || player.isTrueHero() || Hero.getInstance().isUnclaimedHero(player.getObjectId()))
				{
					player.sendPacket(SystemMessageId.YOU_CANNOT_AWAKEN_WHEN_YOU_ARE_A_HERO_OR_ON_THE_WAIT_LIST_FOR_HERO_STATUS);
					return;
				}
				
				// TODO: SET 1000 points for Olympiad after change main class.
				
				takeItem(player, STONE_OF_DESTINY);
				
				player.abortCast();
				player.stopCubics();
				player.stopAllEffects();
				player.getEffectList().stopAllToggles();
				player.getEffectList().stopAllEffectsWithoutExclusions(false, false); // ReplaceSkillBySkill should stop here.
				if (PlayerConfig.ERATON_RETAINED_SKILLS.isEmpty())
				{
					player.removeAllSkills();
				}
				else
				{
					for (Skill skill : player.getAllSkills())
					{
						if (!PlayerConfig.ERATON_RETAINED_SKILLS.contains(skill.getId()))
						{
							player.removeSkill(skill);
						}
					}
				}
				
				player.getEffectList().stopAllEffectsWithoutExclusions(false, false); // After removal of skills.
				
				// Stop auto use.
				for (Shortcut shortcut : player.getAllShortcuts())
				{
					if (!shortcut.isAutoUse())
					{
						continue;
					}
					
					player.removeAutoShortcut(shortcut.getSlot(), shortcut.getPage());
					
					if (player.getAutoUseSettings().isAutoSkill(shortcut.getId()))
					{
						final Skill knownSkill = player.getKnownSkill(shortcut.getId());
						if (knownSkill != null)
						{
							if (knownSkill.hasNegativeEffect())
							{
								AutoUseTaskManager.getInstance().removeAutoSkill(player, shortcut.getId());
							}
							else
							{
								AutoUseTaskManager.getInstance().removeAutoBuff(player, shortcut.getId());
							}
						}
					}
					else
					{
						final Item knownItem = player.getInventory().getItemByObjectId(shortcut.getId());
						if (knownItem != null)
						{
							if (knownItem.isPotion())
							{
								AutoUseTaskManager.getInstance().setAutoPotionItem(player, knownItem.getId());
							}
							else
							{
								AutoUseTaskManager.getInstance().removeAutoSupplyItem(player, knownItem.getId());
							}
						}
					}
				}
				
				if ((classId == 216)) // Death Knight
				{
					player.getAppearance().setMale();
					player.setDeathKnight(true);
					player.setOriginalClass(PlayerClass.SIGEL_DEATH_KNIGHT);
					player.setPlayerClass(classId);
				}
				else if (classId == 174) // Iss Dominator
				{
					player.setOriginalClass(PlayerClass.ISS_DOMINATOR);
					player.setPlayerClass(classId);
				}
				else if (classId == 156) // Tyrr Maestro
				{
					player.setOriginalClass(PlayerClass.TYRR_MAESTRO);
					player.setPlayerClass(classId);
				}
				else if (classId == 188) // Eviscerator
				{
					player.getAppearance().setFemale();
					player.setOriginalClass(PlayerClass.EVISCERATOR);
					player.setPlayerClass(classId);
				}
				else if (classId == 189) // Sayha's Seer
				{
					player.getAppearance().setFemale();
					player.setOriginalClass(PlayerClass.SAYHA_SEER);
					player.setPlayerClass(classId);
				}
				else if (classId == 235) // Shine Maker
				{
					player.getAppearance().setFemale();
					player.setOriginalClass(PlayerClass.SHINE_MAKER);
					player.setPlayerClass(classId);
				}
				else
				{
					if (player.getPlayerClass() == PlayerClass.SIGEL_DEATH_KNIGHT)
					{
						player.setOriginalClass(PlayerClass.SIGEL_PHOENIX_KNIGHT);
					}
					
					if (player.getPlayerClass() == PlayerClass.SHINE_MAKER)
					{
						player.setOriginalClass(PlayerClass.MAESTRO);
					}
					
					if (player.getOriginalClass() == null)
					{
						player.setOriginalClass(player.getPlayerClass());
					}
					
					player.setPlayerClass(classId);
				}
				
				player.setBaseClass(player.getActiveClass());
				player.setAbilityPointsUsed(0, true);
				player.restoreAbilitySkills();
				player.storeMe();
				SkillTreeData.getInstance().cleanSkillUponChangeClass(player);
				for (SkillLearn skill : SkillTreeData.getInstance().getRaceSkillTree(player.getRace()))
				{
					player.addSkill(SkillData.getInstance().getSkill(skill.getSkillId(), skill.getSkillLevel()), true);
				}
				
				final List<Integer> removedSkillIds = PlayerConfig.HARDIN_REMOVED_SKILLS.get(classId);
				if (removedSkillIds != null)
				{
					for (int skillId : removedSkillIds)
					{
						final Skill skill = player.getKnownSkill(skillId);
						if (skill != null)
						{
							player.removeSkill(skill);
						}
					}
				}
				
				// Disarm unusable equipment.
				player.disarmUnusableEquipment();
				
				if (player.getWarehouse().getItemByItemId(CHAOS_POMANDER.getId()) != null)
				{
					final long warehouseCount = (player.getWarehouse().getItemByItemId(CHAOS_POMANDER.getId())).getCount();
					if (warehouseCount > 0)
					{
						player.getWarehouse().destroyItemByItemId(ItemProcessType.FEE, CHAOS_POMANDER.getId(), warehouseCount, player, null);
					}
				}
				
				if (hasAtLeastOneQuestItem(player, CHAOS_POMANDER.getId()))
				{
					takeItems(player, CHAOS_POMANDER.getId(), -1);
				}
				
				// Remove olympiad nobless.
				Olympiad.removeNobleStats(player.getObjectId());
				
				// Set new PlayerClass.
				player.restoreDualSkills();
				player.store(false);
				player.broadcastUserInfo();
				player.sendSkillList();
				player.sendPacket(new ExSubjobInfo(player, SubclassInfoType.CLASS_CHANGED));
				player.sendPacket(new ExUserInfoInvenWeight(player));
				giveItems(player, CHAOS_POMANDER);
				player.sendPacket(new SocialAction(player.getObjectId(), 20));
				Disconnection.of(player).storeAndDeleteWith(LeaveWorld.STATIC_PACKET);
				break;
			}
		}
	}
	
	public static void main(String[] args)
	{
		new Eraton();
	}
}
