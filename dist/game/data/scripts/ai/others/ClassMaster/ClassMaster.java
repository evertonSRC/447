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
package ai.others.ClassMaster;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import org.l2jmobius.commons.util.IXmlReader;
import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.gameserver.config.PlayerConfig;
import org.l2jmobius.gameserver.data.enums.CategoryType;
import org.l2jmobius.gameserver.data.xml.CategoryData;
import org.l2jmobius.gameserver.data.xml.ClassListData;
import org.l2jmobius.gameserver.data.xml.ExperienceData;
import org.l2jmobius.gameserver.data.xml.ItemData;
import org.l2jmobius.gameserver.data.xml.SkillData;
import org.l2jmobius.gameserver.data.xml.SkillTreeData;
import org.l2jmobius.gameserver.model.SkillLearn;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.enums.creature.Race;
import org.l2jmobius.gameserver.model.actor.enums.player.PlayerClass;
import org.l2jmobius.gameserver.model.actor.enums.player.SubclassInfoType;
import org.l2jmobius.gameserver.model.actor.holders.player.SubClassHolder;
import org.l2jmobius.gameserver.model.clan.Clan;
import org.l2jmobius.gameserver.model.events.EventDispatcher;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.ListenerRegisterType;
import org.l2jmobius.gameserver.model.events.annotations.RegisterEvent;
import org.l2jmobius.gameserver.model.events.annotations.RegisterType;
import org.l2jmobius.gameserver.model.events.holders.actor.player.OnPlayerBecomeNoblesse;
import org.l2jmobius.gameserver.model.events.holders.actor.player.OnPlayerBypass;
import org.l2jmobius.gameserver.model.events.holders.actor.player.OnPlayerLevelChanged;
import org.l2jmobius.gameserver.model.events.holders.actor.player.OnPlayerLogin;
import org.l2jmobius.gameserver.model.events.holders.actor.player.OnPlayerPressTutorialMark;
import org.l2jmobius.gameserver.model.events.holders.actor.player.OnPlayerProfessionChange;
import org.l2jmobius.gameserver.model.item.enums.ItemProcessType;
import org.l2jmobius.gameserver.model.item.holders.ItemHolder;
import org.l2jmobius.gameserver.model.script.Script;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.model.spawns.SpawnTemplate;
import org.l2jmobius.gameserver.model.zone.ZoneId;
import org.l2jmobius.gameserver.network.serverpackets.AcquireSkillList;
import org.l2jmobius.gameserver.network.serverpackets.ExSubjobInfo;
import org.l2jmobius.gameserver.network.serverpackets.NpcHtmlMessage;
import org.l2jmobius.gameserver.network.serverpackets.PlaySound;
import org.l2jmobius.gameserver.network.serverpackets.TutorialCloseHtml;
import org.l2jmobius.gameserver.network.serverpackets.TutorialShowQuestionMark;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.util.FormatUtil;

/**
 * Class Master AI.
 * @author Nik, Mobius
 */
public class ClassMaster extends Script implements IXmlReader
{
	private static final Logger LOGGER = Logger.getLogger(ClassMaster.class.getName());
	
	// NPCs
	private static final List<Integer> CLASS_MASTERS = new ArrayList<>();
	static
	{
		CLASS_MASTERS.add(31756); // Mr. Cat
		CLASS_MASTERS.add(31757); // Queen of Hearts
	}
	
	// Misc
	private boolean _isEnabled;
	private boolean _spawnClassMasters;
	private boolean _showPopupWindow;
	private final List<ClassChangeData> _classChangeData = new LinkedList<>();
	private static final int DUAL_CLASS_MIN_LEVEL = 1;
	private static final int DUAL_CLASS_CREATE_ASK = 1001;
	private static final int DUAL_CLASS_CHANGE_ASK = 1002;
	private static final String DUAL_CERTIFICATE_COUNT_VAR = "DUAL_CERTIFICATE_COUNT";
	private static final int[] DUAL_SKILL_LEVELS = {85, 90, 95, 99, 101, 103, 105, 107, 109, 110};
	
	// Dual class rewards/items
	private static final int CHAOS_POMANDER_DUAL_CLASS = 37375;
	private static final int PAULINAS_RGRADE_EQUIPMENT_SET = 46919;
	private static final int DUAL_CERTIFICATE = 36078;
	private static final int DUAL_CERTIFICATE_ENHANCED = 81731;
	private static final Map<CategoryType, Integer> POWER_ITEMS = new EnumMap<>(CategoryType.class);
	static
	{
		POWER_ITEMS.put(CategoryType.SIXTH_SIGEL_GROUP, 32264); // Abelius Power
		POWER_ITEMS.put(CategoryType.SIXTH_TIR_GROUP, 32265); // Sapyros Power
		POWER_ITEMS.put(CategoryType.SIXTH_OTHEL_GROUP, 32266); // Ashagen Power
		POWER_ITEMS.put(CategoryType.SIXTH_YR_GROUP, 32267); // Cranigg Power
		POWER_ITEMS.put(CategoryType.SIXTH_FEOH_GROUP, 32268); // Soltkreig Power
		POWER_ITEMS.put(CategoryType.SIXTH_WYNN_GROUP, 32269); // Naviarope Power
		POWER_ITEMS.put(CategoryType.SIXTH_IS_GROUP, 32270); // Leister Power
		POWER_ITEMS.put(CategoryType.SIXTH_EOLH_GROUP, 32271); // Laksis Power
	}
	
	private static final List<PlayerClass> DUAL_CLASS_LIST = new ArrayList<>();
	static
	{
		DUAL_CLASS_LIST.addAll(Arrays.asList(PlayerClass.SIGEL_PHOENIX_KNIGHT, PlayerClass.SIGEL_HELL_KNIGHT, PlayerClass.SIGEL_EVA_TEMPLAR, PlayerClass.SIGEL_SHILLIEN_TEMPLAR));
		DUAL_CLASS_LIST.addAll(Arrays.asList(PlayerClass.TYRR_DUELIST, PlayerClass.TYRR_DREADNOUGHT, PlayerClass.TYRR_TITAN, PlayerClass.TYRR_GRAND_KHAVATARI, PlayerClass.TYRR_DOOMBRINGER));
		DUAL_CLASS_LIST.addAll(Arrays.asList(PlayerClass.OTHELL_ADVENTURER, PlayerClass.OTHELL_WIND_RIDER, PlayerClass.OTHELL_GHOST_HUNTER, PlayerClass.OTHELL_FORTUNE_SEEKER));
		DUAL_CLASS_LIST.addAll(Arrays.asList(PlayerClass.YUL_SAGITTARIUS, PlayerClass.YUL_MOONLIGHT_SENTINEL, PlayerClass.YUL_GHOST_SENTINEL, PlayerClass.YUL_TRICKSTER));
		DUAL_CLASS_LIST.addAll(Arrays.asList(PlayerClass.FEOH_ARCHMAGE, PlayerClass.FEOH_SOULTAKER, PlayerClass.FEOH_MYSTIC_MUSE, PlayerClass.FEOH_STORM_SCREAMER, PlayerClass.FEOH_SOUL_HOUND));
		DUAL_CLASS_LIST.addAll(Arrays.asList(PlayerClass.ISS_HIEROPHANT, PlayerClass.ISS_SWORD_MUSE, PlayerClass.ISS_SPECTRAL_DANCER, PlayerClass.ISS_DOOMCRYER));
		DUAL_CLASS_LIST.addAll(Arrays.asList(PlayerClass.WYNN_ARCANA_LORD, PlayerClass.WYNN_ELEMENTAL_MASTER, PlayerClass.WYNN_SPECTRAL_MASTER));
		DUAL_CLASS_LIST.addAll(Arrays.asList(PlayerClass.AEORE_CARDINAL, PlayerClass.AEORE_EVA_SAINT, PlayerClass.AEORE_SHILLIEN_SAINT));
	}
	
	public ClassMaster()
	{
		load();
		addStartNpc(CLASS_MASTERS);
		addTalkId(CLASS_MASTERS);
		addFirstTalkId(CLASS_MASTERS);
	}
	
	@Override
	public void load()
	{
		_classChangeData.clear();
		parseDatapackFile("config/ClassMaster.xml");
		
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _classChangeData.size() + " class change options.");
	}
	
	@Override
	public boolean isValidating()
	{
		return false;
	}
	
	@Override
	public void parseDocument(Document document, File file)
	{
		NamedNodeMap attrs;
		for (Node n = document.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if ("list".equals(n.getNodeName()))
			{
				for (Node cm = n.getFirstChild(); cm != null; cm = cm.getNextSibling())
				{
					attrs = cm.getAttributes();
					if ("classMaster".equals(cm.getNodeName()))
					{
						_isEnabled = parseBoolean(attrs, "classChangeEnabled", false);
						if (!_isEnabled)
						{
							return;
						}
						
						_spawnClassMasters = parseBoolean(attrs, "spawnClassMasters", true);
						_showPopupWindow = parseBoolean(attrs, "showPopupWindow", false);
						for (Node c = cm.getFirstChild(); c != null; c = c.getNextSibling())
						{
							attrs = c.getAttributes();
							if ("classChangeOption".equals(c.getNodeName()))
							{
								final List<CategoryType> appliedCategories = new LinkedList<>();
								final List<ItemHolder> requiredItems = new LinkedList<>();
								final List<ItemHolder> rewardedItems = new LinkedList<>();
								boolean setNoble = false;
								boolean setHero = false;
								final String optionName = parseString(attrs, "name", "");
								for (Node b = c.getFirstChild(); b != null; b = b.getNextSibling())
								{
									attrs = b.getAttributes();
									if ("appliesTo".equals(b.getNodeName()))
									{
										for (Node r = b.getFirstChild(); r != null; r = r.getNextSibling())
										{
											attrs = r.getAttributes();
											if ("category".equals(r.getNodeName()))
											{
												final CategoryType category = CategoryType.findByName(r.getTextContent().trim());
												if (category == null)
												{
													LOGGER.severe(getClass().getSimpleName() + ": Incorrect category type: " + r.getNodeValue());
													continue;
												}
												
												appliedCategories.add(category);
											}
										}
									}
									
									if ("rewards".equals(b.getNodeName()))
									{
										for (Node r = b.getFirstChild(); r != null; r = r.getNextSibling())
										{
											attrs = r.getAttributes();
											if ("item".equals(r.getNodeName()))
											{
												final int itemId = parseInteger(attrs, "id");
												final int count = parseInteger(attrs, "count", 1);
												rewardedItems.add(new ItemHolder(itemId, count));
											}
											else if ("setNoble".equals(r.getNodeName()))
											{
												setNoble = true;
											}
											else if ("setHero".equals(r.getNodeName()))
											{
												setHero = true;
											}
										}
									}
									else if ("conditions".equals(b.getNodeName()))
									{
										for (Node r = b.getFirstChild(); r != null; r = r.getNextSibling())
										{
											attrs = r.getAttributes();
											if ("item".equals(r.getNodeName()))
											{
												final int itemId = parseInteger(attrs, "id");
												final int count = parseInteger(attrs, "count", 1);
												requiredItems.add(new ItemHolder(itemId, count));
											}
										}
									}
								}
								
								if (appliedCategories.isEmpty())
								{
									LOGGER.warning(getClass().getSimpleName() + ": Class change option: " + optionName + " has no categories to be applied on. Skipping!");
									continue;
								}
								
								final ClassChangeData classChangeData = new ClassChangeData(optionName, appliedCategories);
								classChangeData.setItemsRequired(requiredItems);
								classChangeData.setItemsRewarded(rewardedItems);
								classChangeData.setRewardHero(setHero);
								classChangeData.setRewardNoblesse(setNoble);
								
								_classChangeData.add(classChangeData);
							}
						}
					}
				}
			}
		}
	}
	
	@Override
	public void onSpawnActivate(SpawnTemplate template)
	{
		if (_spawnClassMasters)
		{
			template.spawnAllIncludingNotDefault(null);
		}
	}
	
	@Override
	public void onSpawnDeactivate(SpawnTemplate template)
	{
		template.despawnAll();
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		if (!_isEnabled)
		{
			return null;
		}
		
		final String htmltext = getHtm(player, "test_server_helper001.html");
		if (htmltext == null)
		{
			return null;
		}
		
		final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
		html.setHtml(htmltext.replace("%dualclassButtons%", buildDualClassButtons(player)));
		player.sendPacket(html);
		return null;
	}
	
	@Override
	public String onEvent(String eventValue, Npc npc, Player player)
	{
		if (!_isEnabled)
		{
			return null;
		}
		
		String htmltext = null;
		String event = eventValue;
		final StringTokenizer st = new StringTokenizer(event);
		event = st.nextToken();
		switch (event)
		{
			case "buyitems":
			{
				htmltext = npc.getId() == CLASS_MASTERS.get(0) ? "test_server_helper001a.html" : "test_server_helper001b.html";
				break;
			}
			case "dualclass_choose":
			{
				if (!canUseDualClassFeatures(player, true))
				{
					return null;
				}
				if (player.hasDualClass())
				{
					player.sendMessage("You already have a dual class.");
					return null;
				}
				
				showDualClassGroupList(player, npc, "Which class would you like as your Dual Class?", "dualclass_choose_");
				return null;
			}
			case "dualclass_change":
			{
				if (!canUseDualClassFeatures(player, false))
				{
					return null;
				}
				if (!player.hasDualClass())
				{
					player.sendMessage("You do not have a dual class yet.");
					return null;
				}
				
				showDualClassGroupList(player, npc, "Choose a new Dual Class. Fee: " + FormatUtil.formatAdena(PlayerConfig.FEE_DELETE_DUALCLASS_SKILLS) + " Adena.", "dualclass_change_");
				return null;
			}
			case "setnoble":
			{
				if (player.getNobleLevel() > 0)
				{
					htmltext = "test_server_helper025b.html";
				}
				else if (player.getLevel() < 75)
				{
					htmltext = "test_server_helper025a.html";
				}
				else
				{
					player.setNobleLevel(1);
					checkNobleListener(player);
					player.broadcastUserInfo();
					
					// TODO: SetOneTimeQuestFlag(talker, 10385, 1);
					htmltext = "test_server_helper025.html";
				}
				break;
			}
			case "firstclass":
			{
				htmltext = getFirstOccupationChangeHtml(player);
				break;
			}
			case "secondclass":
			{
				htmltext = getSecondOccupationChangeHtml(player);
				break;
			}
			case "thirdclass":
			{
				if (player.isInCategory(CategoryType.THIRD_CLASS_GROUP) && (player.getLevel() > 75))
				{
					if (changeToNextClass(player))
					{
						player.sendPacket(new PlaySound("ItemSound.quest_fanfare_2"));
						player.broadcastUserInfo();
						htmltext = "test_server_helper021.html";
					}
				}
				else if (player.isInCategory(CategoryType.FOURTH_CLASS_GROUP))
				{
					htmltext = "test_server_helper011.html";
				}
				else if (player.isInCategory(CategoryType.SIXTH_CLASS_GROUP))
				{
					htmltext = "test_server_helper011a.html";
				}
				else
				{
					htmltext = "test_server_helper024.html";
				}
				break;
			}
			case "directthirdclass":
			{
				htmltext = getDirectThirdOccupationChangeHtml(player);
				break;
			}
			case "awaken":
			{
				if (player.isInCategory(CategoryType.FOURTH_CLASS_GROUP) && (player.getLevel() > 84))
				{
					if (changeToNextClass(player))
					{
						player.sendPacket(new PlaySound("ItemSound.quest_fanfare_2"));
						player.broadcastUserInfo();
						player.store(false); // Save player cause if server crashes before this char is saved, he will lose class and the money payed for class change.
						htmltext = "test_server_helper021.html";
					}
				}
				else if (player.isInCategory(CategoryType.SIXTH_CLASS_GROUP))
				{
					htmltext = "test_server_helper011a.html";
				}
				else
				{
					htmltext = "test_server_helper011b.html";
				}
				break;
			}
			case "setclass":
			{
				if (!st.hasMoreTokens())
				{
					return null;
				}
				
				final int classId = Integer.parseInt(st.nextToken());
				boolean canChange = false;
				if (player.isInCategory(CategoryType.FIRST_CLASS_GROUP)
					&& (CategoryData.getInstance().isInCategory(CategoryType.FOURTH_CLASS_GROUP, classId)
						|| CategoryData.getInstance().isInCategory(CategoryType.SIXTH_CLASS_GROUP, classId)))
				{
					canChange = true;
				}
				else if ((player.isInCategory(CategoryType.SECOND_CLASS_GROUP) || player.isInCategory(CategoryType.FIRST_CLASS_GROUP)) && (player.getLevel() >= 40)) // In retail you can skip first occupation
				{
					canChange = CategoryData.getInstance().isInCategory(CategoryType.THIRD_CLASS_GROUP, classId) || (player.isInCategory(CategoryType.FIRST_CLASS_GROUP) && CategoryData.getInstance().isInCategory(CategoryType.SECOND_CLASS_GROUP, classId));
				}
				else if (player.isInCategory(CategoryType.FIRST_CLASS_GROUP) && (player.getLevel() >= 20))
				{
					canChange = CategoryData.getInstance().isInCategory(CategoryType.SECOND_CLASS_GROUP, classId);
				}
				else if (player.isInCategory(CategoryType.THIRD_CLASS_GROUP) && (player.getLevel() >= 76))
				{
					canChange = CategoryData.getInstance().isInCategory(CategoryType.FOURTH_CLASS_GROUP, classId);
				}
				else if (player.isInCategory(CategoryType.FOURTH_CLASS_GROUP) && (player.getLevel() >= 85)) // 9
				{
					canChange = CategoryData.getInstance().isInCategory(CategoryType.SIXTH_CLASS_GROUP, classId); // 11
				}
				
				if (canChange)
				{
					int classDataIndex = -1;
					if (st.hasMoreTokens())
					{
						classDataIndex = Integer.parseInt(st.nextToken());
					}
					
					if (checkIfClassChangeHasOptions(player) && (classDataIndex == -1))
					{
						htmltext = getHtm(player, "cc_options.html");
						htmltext = htmltext.replace("%name%", ClassListData.getInstance().getClass(classId).getClassName()); // getEscapedClientCode());
						htmltext = htmltext.replace("%options%", getClassChangeOptions(player, classId));
						return htmltext;
					}
					
					final ClassChangeData data = getClassChangeData(classDataIndex);
					if (data != null)
					{
						// Required items.
						if (!data.getItemsRequired().isEmpty())
						{
							for (ItemHolder ri : data.getItemsRequired())
							{
								if (player.getInventory().getInventoryItemCount(ri.getId(), -1) < ri.getCount())
								{
									player.sendMessage("You do not have enough items.");
									return null; // No class change if payment failed.
								}
							}
							
							for (ItemHolder ri : data.getItemsRequired())
							{
								player.destroyItemByItemId(ItemProcessType.FEE, ri.getId(), ri.getCount(), npc, true);
							}
						}
						
						// Give possible rewards.
						if (!data.getItemsRewarded().isEmpty())
						{
							for (ItemHolder ri : data.getItemsRewarded())
							{
								giveItems(player, ri);
							}
						}
						
						// Give possible nobless status reward.
						if (data.isRewardNoblesse())
						{
							player.setNobleLevel(1);
							checkNobleListener(player);
						}
						
						// Give possible hero status reward.
						if (data.isRewardHero())
						{
							player.setHero(true);
						}
					}
					
					player.setPlayerClass(classId);
					if (player.isSubClassActive())
					{
						player.getSubClasses().get(player.getClassIndex()).setPlayerClass(player.getActiveClass());
					}
					else
					{
						player.setBaseClass(player.getActiveClass());
					}
					
					if (player.isInCategory(CategoryType.SIXTH_CLASS_GROUP))
					{
						SkillTreeData.getInstance().cleanSkillUponChangeClass(player);
						for (SkillLearn skill : SkillTreeData.getInstance().getRaceSkillTree(player.getRace()))
						{
							player.addSkill(SkillData.getInstance().getSkill(skill.getSkillId(), skill.getSkillLevel()), true);
						}
					}
					
					if (PlayerConfig.AUTO_LEARN_SKILLS)
					{
						player.giveAvailableSkills(PlayerConfig.AUTO_LEARN_FS_SKILLS, PlayerConfig.AUTO_LEARN_FP_SKILLS, true, PlayerConfig.AUTO_LEARN_SKILLS_WITHOUT_ITEMS);
					}
					
					player.store(false); // Save player cause if server crashes before this char is saved, he will lose class and the money payed for class change.
					player.broadcastUserInfo();
					player.sendSkillList();
					player.sendPacket(new PlaySound("ItemSound.quest_fanfare_2"));
					return "test_server_helper021.html";
				}
				break;
			}
			case "clanlevel":
			{
				htmltext = player.isClanLeader() ? "test_server_helper022.html" : "pl014.html";
				break;
			}
			case "learnskills":
			{
				// Retail class master only lets you learn all third class skills.
				if (player.isInCategory(CategoryType.SIXTH_CLASS_GROUP))
				{
					htmltext = "test_server_helper001_failed.html";
				}
				else
				{
					player.giveAvailableSkills(true, true, true, true);
				}
				break;
			}
			case "clanlevelup":
			{
				final Clan clan = player.getClan();
				if ((clan == null) || !player.isClanLeader())
				{
					return null;
				}
				
				if (clan.getLevel() >= 10)
				{
					htmltext = "test_server_helper022a.html";
				}
				else
				{
					clan.changeLevel(clan.getLevel() + 1);
					clan.broadcastClanStatus();
				}
				break;
			}
			case "test_server_helper001.html":
			{
				if (CLASS_MASTERS.contains(npc.getId()))
				{
					final String mainHtml = getHtm(player, event);
					if (mainHtml != null)
					{
						htmltext = mainHtml.replace("%dualclassButtons%", buildDualClassButtons(player));
					}
				}
				break;
			}
			default:
			{
				if (event.startsWith("dualclass_pick"))
				{
					if (!canUseDualClassFeatures(player, true) || player.hasDualClass())
					{
						return null;
					}
					if (!st.hasMoreTokens())
					{
						return null;
					}
					
					final int classId = Integer.parseInt(st.nextToken());
					handleDualClassCreate(player, npc, classId);
					return null;
				}
				else if (event.startsWith("dualclass_change_pick"))
				{
					if (!canUseDualClassFeatures(player, false) || !player.hasDualClass())
					{
						return null;
					}
					if (!st.hasMoreTokens())
					{
						return null;
					}
					
					final int classId = Integer.parseInt(st.nextToken());
					handleDualClassChange(player, npc, classId);
					return null;
				}
				else if (event.startsWith("dualclass_choose_"))
				{
					if (!canUseDualClassFeatures(player, true) || player.hasDualClass())
					{
						return null;
					}
					
					showDualClassClassList(player, npc, event.replace("dualclass_choose_", ""), "Select your Dual Class.", "dualclass_pick");
					return null;
				}
				else if (event.startsWith("dualclass_change_"))
				{
					if (!canUseDualClassFeatures(player, false) || !player.hasDualClass())
					{
						return null;
					}
					
					showDualClassClassList(player, npc, event.replace("dualclass_change_", ""), "Select the new Dual Class.", "dualclass_change_pick");
					return null;
				}
				break;
			}
		}
		
		return htmltext;
	}
	
	private boolean canUseDualClassFeatures(Player player, boolean creating)
	{
		if (player.isChangingClass())
		{
			player.sendMessage("You are already changing class.");
			return false;
		}
		
		if (!isMainClassAwakened(player))
		{
			player.sendMessage("You can only manage a Dual Class after your main class is awakened.");
			LOGGER.info("Dual class access denied for " + player.getName() + ": main class not awakened.");
			return false;
		}
		
		if (player.getLevel() < DUAL_CLASS_MIN_LEVEL)
		{
			player.sendMessage("You must be level " + DUAL_CLASS_MIN_LEVEL + " or higher to use Dual Class.");
			LOGGER.info("Dual class access denied for " + player.getName() + ": level " + player.getLevel() + " < " + DUAL_CLASS_MIN_LEVEL + ".");
			return false;
		}
		
		if (player.isAlikeDead() || player.isDead())
		{
			player.sendMessage("You cannot use this while dead.");
			return false;
		}
		
		if (player.isInCombat() || player.isInDuel() || player.isInOlympiadMode() || player.inObserverMode())
		{
			player.sendMessage("You cannot change classes while in combat or special modes.");
			return false;
		}
		
		if (player.isTeleporting())
		{
			player.sendMessage("You cannot change classes while teleporting.");
			return false;
		}
		
		if ((player.getPvpFlag() > 0) || player.isInsideZone(ZoneId.PVP) || player.isInsideZone(ZoneId.SIEGE))
		{
			player.sendMessage("You cannot change classes while in PvP.");
			return false;
		}
		
		if (player.isOnEvent())
		{
			player.sendMessage("You cannot change classes while participating in an event.");
			return false;
		}
		
		if (player.isTransformed() || player.isMounted())
		{
			player.sendMessage("You cannot change classes while transformed or mounted.");
			return false;
		}
		
		if (player.hasSummon())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_CREATE_OR_CHANGE_A_DUAL_CLASS_WHILE_A_SERVITOR_OR_A_PET_IS_SUMMONED);
			return false;
		}
		
		if (player.isInStoreMode() || player.isCrafting() || (player.getActiveTradeList() != null) || player.isProcessingRequest())
		{
			player.sendMessage("You cannot change classes while trading or operating a private store.");
			return false;
		}
		
		if (player.isInInstance())
		{
			player.sendMessage("You cannot change classes inside an instance.");
			return false;
		}
		
		if (!player.isInventoryUnder80(true) || (player.getWeightPenalty() >= 2))
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_CREATE_OR_CHANGE_A_DUAL_CLASS_WHILE_YOU_HAVE_OVERWEIGHT);
			return false;
		}
		
		return true;
	}
	
	private boolean isMainClassAwakened(Player player)
	{
		return CategoryData.getInstance().isInCategory(CategoryType.SIXTH_CLASS_GROUP, player.getBaseClass());
	}
	
	private String buildDualClassButtons(Player player)
	{
		if (!isMainClassAwakened(player))
		{
			return "";
		}
		
		if (player.hasDualClass())
		{
			return "<Button ALIGN=LEFT ICON=\"NORMAL\" action=\"bypass -h Quest ClassMaster dualclass_change\">\"Alterar Dual Class.\"</Button>";
		}
		
		return "<Button ALIGN=LEFT ICON=\"NORMAL\" action=\"bypass -h Quest ClassMaster dualclass_choose\">\"Escolher Dual Class.\"</Button>";
	}
	
	private void showDualClassGroupList(Player player, Npc npc, String title, String actionPrefix)
	{
		final String htmltext = getHtm(player, "dualclass_select_group.html");
		if (htmltext == null)
		{
			return;
		}

		final String html = htmltext.replace("%title%", title).replace("%actionPrefix%", actionPrefix);
		if (npc == null)
		{
			showResult(player, html);
			return;
		}
		
		final NpcHtmlMessage htmlMessage = new NpcHtmlMessage(npc.getObjectId());
		htmlMessage.setHtml(html);
		player.sendPacket(htmlMessage);
	}
	
	private void showDualClassClassList(Player player, Npc npc, String categoryName, String title, String action)
	{
		final CategoryType cType = CategoryType.findByName(categoryName);
		if (cType == null)
		{
			player.sendMessage("Invalid dual class category.");
			return;
		}
		
		final String htmltext = getHtm(player, "dualclass_select_class.html");
		if (htmltext == null)
		{
			return;
		}
		
		final StringBuilder sb = new StringBuilder();
		for (PlayerClass dualClass : getDualClasses(player, cType))
		{
			if (dualClass != null)
			{
				sb.append("<button value=\"" + ClassListData.getInstance().getClass(dualClass.getId()).getClassName() + "\" action=\"bypass -h Quest ClassMaster " + action + " " + dualClass.getId() + "\" width=\"200\" height=\"31\" back=\"L2UI_CT1.HtmlWnd_DF_Awake_Down\" fore=\"L2UI_CT1.HtmlWnd_DF_Awake\"><br>");
			}
		}
		
		final String html = htmltext.replace("%title%", title).replace("%dualclassList%", sb.toString());
		if (npc == null)
		{
			showResult(player, html);
			return;
		}
		
		final NpcHtmlMessage htmlMessage = new NpcHtmlMessage(npc.getObjectId());
		htmlMessage.setHtml(html);
		player.sendPacket(htmlMessage);
	}
	
	private List<PlayerClass> getDualClasses(Player player, CategoryType cType)
	{
		final List<PlayerClass> tempList = new ArrayList<>();
		final int baseClassId = player.getBaseClass();
		final int activeClassId = player.getPlayerClass().getId();
		final int dualClassId = player.getDualClass() != null ? player.getDualClass().getId() : -1;
		for (PlayerClass temp : DUAL_CLASS_LIST)
		{
			if ((temp.getId() != baseClassId) && (temp.getId() != activeClassId) && (temp.getId() != dualClassId) && ((cType == null) || CategoryData.getInstance().isInCategory(cType, temp.getId())))
			{
				tempList.add(temp);
			}
		}
		
		return tempList;
	}
	
	private void handleDualClassCreate(Player player, Npc npc, int classId)
	{
		if (!getDualClasses(player, null).contains(PlayerClass.getPlayerClass(classId)))
		{
			player.sendMessage("Invalid dual class selection.");
			LOGGER.warning("Dual class create denied for " + player.getName() + ": invalid class " + classId + ".");
			return;
		}
		
		if (player.addSubClass(classId, 1, true))
		{
			player.abortCast();
			player.stopAllEffectsExceptThoseThatLastThroughDeath();
			player.stopAllEffects();
			player.stopCubics();
			player.setActiveClass(1);
			player.sendPacket(new ExSubjobInfo(player, SubclassInfoType.CLASS_CHANGED));
			SkillTreeData.getInstance().cleanSkillUponChangeClass(player);
			player.restoreDualSkills();
			player.sendPacket(new AcquireSkillList(player));
			player.sendSkillList();
			player.broadcastUserInfo();
			
			// Fix Death Knight model animation.
			if (player.isDeathKnight())
			{
				player.transform(101, false);
				ThreadPool.schedule(() -> player.stopTransformation(false), 50);
			}
			
			// Item rewards
			player.addItem(ItemProcessType.REWARD, CHAOS_POMANDER_DUAL_CLASS, 2, player, true);
			player.addItem(ItemProcessType.REWARD, PAULINAS_RGRADE_EQUIPMENT_SET, 1, player, true);
			giveItems(player, getPowerItemId(player), 1);
			LOGGER.info("Player " + player.getName() + " chose dual class " + classId + " via ClassMaster.");
		}
	}
	
	private void handleDualClassChange(Player player, Npc npc, int classId)
	{
		final SubClassHolder dualClass = player.getDualClass();
		if (dualClass == null)
		{
			player.sendMessage("You do not have a dual class yet.");
			return;
		}
		
		if (dualClass.getId() == classId)
		{
			return;
		}
		
		if (!getDualClasses(player, null).contains(PlayerClass.getPlayerClass(classId)))
		{
			player.sendMessage("Invalid dual class selection.");
			LOGGER.warning("Dual class change denied for " + player.getName() + ": invalid class " + classId + ".");
			return;
		}
		
		if (player.getAdena() < PlayerConfig.FEE_DELETE_DUALCLASS_SKILLS)
		{
			player.sendMessage("You need " + FormatUtil.formatAdena(PlayerConfig.FEE_DELETE_DUALCLASS_SKILLS) + " Adena to change your Dual Class.");
			return;
		}
		
		player.reduceAdena(ItemProcessType.FEE, PlayerConfig.FEE_DELETE_DUALCLASS_SKILLS, npc, true);
		LOGGER.info("Player " + player.getName() + " paid " + PlayerConfig.FEE_DELETE_DUALCLASS_SKILLS + " Adena to change dual class.");
		clearDualClassSkills(player, npc);
		
		final long dualClassExp = dualClass.getExp();
		final int classIndex = dualClass.getClassIndex();
		if (player.modifySubClass(classIndex, classId, true))
		{
			player.abortCast();
			player.stopAllEffectsExceptThoseThatLastThroughDeath();
			player.stopAllEffects();
			player.stopCubics();
			player.setActiveClass(classIndex);
			player.sendPacket(new ExSubjobInfo(player, SubclassInfoType.CLASS_CHANGED));
			SkillTreeData.getInstance().cleanSkillUponChangeClass(player);
			player.restoreDualSkills();
			player.sendPacket(new AcquireSkillList(player));
			player.sendSkillList();
			takeItems(player, CHAOS_POMANDER_DUAL_CLASS, -1);
			giveItems(player, CHAOS_POMANDER_DUAL_CLASS, 2);
			LOGGER.info("Player " + player.getName() + " changed dual class to " + classId + " via ClassMaster.");
			addExpAndSp(player, dualClassExp - player.getExp(), 0);
		}
	}
	
	private void clearDualClassSkills(Player player, Npc npc)
	{
		takeItems(player, DUAL_CERTIFICATE, -1);
		takeItems(player, DUAL_CERTIFICATE_ENHANCED, -1);
		player.getWarehouse().destroyItemByItemId(ItemProcessType.QUEST, DUAL_CERTIFICATE, -1, player, npc);
		player.getWarehouse().destroyItemByItemId(ItemProcessType.QUEST, DUAL_CERTIFICATE_ENHANCED, -1, player, npc);
		player.getVariables().remove(DUAL_CERTIFICATE_COUNT_VAR);
		
		final PlayerVariables vars = player.getVariables();
		for (int lv : DUAL_SKILL_LEVELS)
		{
			vars.remove("DualSkill-" + lv);
		}
		
		takeSkills(player, "DualSkillList");
	}
	
	private void takeSkills(Player player, String type)
	{
		final PlayerVariables vars = player.getVariables();
		final String list = vars.getString(type, "");
		if (!list.isEmpty())
		{
			final String[] skills = list.split(";");
			for (String skill : skills)
			{
				final String[] str = skill.split("-");
				final Skill sk = SkillData.getInstance().getSkill(Integer.parseInt(str[0]), Integer.parseInt(str[1]));
				player.removeSkill(sk);
			}
			
			vars.remove(type);
			player.sendSkillList();
		}
	}
	
	private int getPowerItemId(Player player)
	{
		return POWER_ITEMS.entrySet().stream().filter(e -> player.isInCategory(e.getKey())).mapToInt(Entry::getValue).findFirst().orElse(0);
	}

	private String getDirectThirdOccupationChangeHtml(Player player)
	{
		String htmltext = null;
		if (player.isInCategory(CategoryType.FIRST_CLASS_GROUP))
		{
			switch (player.getPlayerClass())
			{
				case FIGHTER:
				{
					htmltext = "direct_third_human_fighter.html";
					break;
				}
				case MAGE:
				{
					htmltext = "direct_third_human_mystic.html";
					break;
				}
				case ELVEN_FIGHTER:
				{
					htmltext = "direct_third_elven_fighter.html";
					break;
				}
				case ELVEN_MAGE:
				{
					htmltext = "direct_third_elven_mystic.html";
					break;
				}
				case DARK_FIGHTER:
				{
					htmltext = "direct_third_dark_fighter.html";
					break;
				}
				case DARK_MAGE:
				{
					htmltext = "direct_third_dark_mystic.html";
					break;
				}
				case ORC_FIGHTER:
				{
					htmltext = "direct_third_orc_fighter.html";
					break;
				}
				case ORC_MAGE:
				{
					htmltext = "direct_third_orc_mystic.html";
					break;
				}
				case DWARVEN_FIGHTER:
				{
					htmltext = "direct_third_dwarven_fighter.html";
					break;
				}
				case MALE_SOLDIER:
				{
					htmltext = "direct_third_kamael_male.html";
					break;
				}
				case FEMALE_SOLDIER:
				{
					htmltext = "direct_third_kamael_female.html";
					break;
				}
				case ERTHEIA_FIGHTER:
				{
					htmltext = "direct_third_ertheia_fighter.html";
					break;
				}
				case ERTHEIA_WIZARD:
				{
					htmltext = "direct_third_ertheia_wizard.html";
					break;
				}
				case DEATH_SOLDIER:
				{
					htmltext = "direct_third_death_soldier.html";
					break;
				}
				case DWARF_MAKER:
				{
					htmltext = "direct_third_dwarf_maker.html";
					break;
				}
			}
		}
		else if (player.isInCategory(CategoryType.SECOND_CLASS_GROUP))
		{
			htmltext = "direct_third_invalid.html";
		}
		else if (player.isInCategory(CategoryType.THIRD_CLASS_GROUP))
		{
			htmltext = "test_server_helper010.html";
		}
		else if (player.isInCategory(CategoryType.FOURTH_CLASS_GROUP))
		{
			htmltext = "test_server_helper011.html";
		}
		else if (player.isInCategory(CategoryType.SIXTH_CLASS_GROUP))
		{
			htmltext = "test_server_helper011a.html";
		}

		return htmltext;
	}
	
	private String getFirstOccupationChangeHtml(Player player)
	{
		String htmltext = null;
		if (player.isInCategory(CategoryType.FIRST_CLASS_GROUP))
		{
			if (player.getRace() == Race.ERTHEIA)
			{
				htmltext = "test_server_helper027a.html";
			}
			else if (player.getLevel() < 20)
			{
				htmltext = "test_server_helper027.html";
			}
			else
			{
				switch (player.getPlayerClass())
				{
					case FIGHTER:
					{
						htmltext = "test_server_helper026a.html";
						break;
					}
					case MAGE:
					{
						htmltext = "test_server_helper026b.html";
						break;
					}
					case ELVEN_FIGHTER:
					{
						htmltext = "test_server_helper026c.html";
						break;
					}
					case ELVEN_MAGE:
					{
						htmltext = "test_server_helper026d.html";
						break;
					}
					case DARK_FIGHTER:
					{
						htmltext = "test_server_helper026e.html";
						break;
					}
					case DARK_MAGE:
					{
						htmltext = "test_server_helper026f.html";
						break;
					}
					case ORC_FIGHTER:
					{
						htmltext = "test_server_helper026g.html";
						break;
					}
					case ORC_MAGE:
					{
						htmltext = "test_server_helper026h.html";
						break;
					}
					case DWARVEN_FIGHTER:
					{
						htmltext = "test_server_helper026i.html";
						break;
					}
					case MALE_SOLDIER:
					{
						htmltext = "test_server_helper026j.html";
						break;
					}
					case FEMALE_SOLDIER:
					{
						htmltext = "test_server_helper026k.html";
						break;
					}
					case DEATH_SOLDIER:
					{
						htmltext = "test_server_helper026l.html";
						break;
					}
					case DWARF_MAKER:
					{
						htmltext = "test_server_helper026m.html";
						break;
					}
				}
			}
		}
		else if (player.isInCategory(CategoryType.SECOND_CLASS_GROUP))
		{
			htmltext = "test_server_helper028.html";
		}
		else if (player.isInCategory(CategoryType.THIRD_CLASS_GROUP))
		{
			htmltext = "test_server_helper010.html";
		}
		else if (player.isInCategory(CategoryType.FOURTH_CLASS_GROUP))
		{
			htmltext = "test_server_helper011.html";
		}
		else if (player.isInCategory(CategoryType.SIXTH_CLASS_GROUP))
		{
			htmltext = "test_server_helper011a.html";
		}
		
		return htmltext;
	}
	
	private String getSecondOccupationChangeHtml(Player player)
	{
		String htmltext = null;
		if (player.isInCategory(CategoryType.SECOND_CLASS_GROUP) || player.isInCategory(CategoryType.FIRST_CLASS_GROUP))
		{
			if (player.getLevel() < 40)
			{
				htmltext = "test_server_helper023.html";
			}
			else
			{
				switch (player.getPlayerClass())
				{
					case FIGHTER:
					{
						htmltext = "test_server_helper012.html";
						break;
					}
					case WARRIOR:
					{
						htmltext = "test_server_helper012a.html";
						break;
					}
					case KNIGHT:
					{
						htmltext = "test_server_helper012b.html";
						break;
					}
					case ROGUE:
					{
						htmltext = "test_server_helper012c.html";
						break;
					}
					case DEATH_SOLDIER:
					{
						htmltext = "test_server_helper012d.html";
						break;
					}
					case DEATH_WARRIOR:
					{
						htmltext = "test_server_helper012e.html";
						break;
					}
					case MAGE:
					{
						htmltext = "test_server_helper013.html";
						break;
					}
					case WIZARD:
					{
						htmltext = "test_server_helper013a.html";
						break;
					}
					case CLERIC:
					{
						htmltext = "test_server_helper013b.html";
						break;
					}
					case ELVEN_FIGHTER:
					{
						htmltext = "test_server_helper014.html";
						break;
					}
					case ELVEN_KNIGHT:
					{
						htmltext = "test_server_helper014a.html";
						break;
					}
					case ELVEN_SCOUT:
					{
						htmltext = "test_server_helper014b.html";
						break;
					}
					case ELVEN_MAGE:
					{
						htmltext = "test_server_helper015.html";
						break;
					}
					case ELVEN_WIZARD:
					{
						htmltext = "test_server_helper015a.html";
						break;
					}
					case ORACLE:
					{
						htmltext = "test_server_helper015b.html";
						break;
					}
					case DARK_FIGHTER:
					{
						htmltext = "test_server_helper016.html";
						break;
					}
					case PALUS_KNIGHT:
					{
						htmltext = "test_server_helper016a.html";
						break;
					}
					case ASSASSIN:
					{
						htmltext = "test_server_helper016b.html";
						break;
					}
					case DARK_MAGE:
					{
						htmltext = "test_server_helper017.html";
						break;
					}
					case DARK_WIZARD:
					{
						htmltext = "test_server_helper017a.html";
						break;
					}
					case SHILLIEN_ORACLE:
					{
						htmltext = "test_server_helper017b.html";
						break;
					}
					case ORC_FIGHTER:
					{
						htmltext = "test_server_helper018.html";
						break;
					}
					case ORC_RAIDER:
					{
						htmltext = "test_server_helper018a.html";
						break;
					}
					case ORC_MONK:
					{
						htmltext = "test_server_helper018b.html";
						break;
					}
					case ORC_MAGE:
					case ORC_SHAMAN:
					{
						htmltext = "test_server_helper019.html";
						break;
					}
					case DWARVEN_FIGHTER:
					{
						htmltext = "test_server_helper020.html";
						break;
					}
					case ARTISAN:
					{
						htmltext = "test_server_helper020b.html";
						break;
					}
					case SCAVENGER:
					{
						htmltext = "test_server_helper020a.html";
						break;
					}
					case MALE_SOLDIER:
					case TROOPER:
					{
						htmltext = "test_server_helper020c.html";
						break;
					}
					case FEMALE_SOLDIER:
					case WARDER:
					{
						htmltext = "test_server_helper020d.html";
						break;
					}
					case ERTHEIA_FIGHTER:
					{
						htmltext = "test_server_helper020e.html";
						break;
					}
					case ERTHEIA_WIZARD:
					{
						htmltext = "test_server_helper020f.html";
						break;
					}
					case EARTH_MAKER:
					{
						htmltext = "test_server_helper020g.html";
						break;
					}
				}
			}
		}
		else if (player.isInCategory(CategoryType.THIRD_CLASS_GROUP))
		{
			htmltext = "test_server_helper010.html";
		}
		else if (player.isInCategory(CategoryType.FOURTH_CLASS_GROUP))
		{
			htmltext = "test_server_helper011.html";
		}
		else if (player.isInCategory(CategoryType.SIXTH_CLASS_GROUP))
		{
			htmltext = "test_server_helper011a.html";
		}
		else
		{
			htmltext = "test_server_helper029.html";
		}
		
		return htmltext;
	}
	
	private boolean changeToNextClass(Player player)
	{
		PlayerClass newClass = null;
		if (player.getPlayerClass() == PlayerClass.FEMALE_SOUL_HOUND)
		{
			newClass = PlayerClass.FEOH_SOUL_HOUND; // Fix for Female Soulhounds
		}
		else
		{
			newClass = Arrays.stream(PlayerClass.values()).filter(cid -> player.getPlayerClass() == cid.getParent()).findAny().orElse(null);
		}
		
		if (newClass == null)
		{
			LOGGER.warning(getClass().getSimpleName() + ": No new classId found for " + player);
			return false;
		}
		else if (newClass == player.getPlayerClass())
		{
			LOGGER.warning(getClass().getSimpleName() + ": New classId found for " + player + " is exactly the same as the one he currently is!");
			return false;
		}
		else if (checkIfClassChangeHasOptions(player))
		{
			String html = getHtm(player, "cc_options.html");
			html = html.replace("%name%", ClassListData.getInstance().getClass(newClass.getId()).getClassName()); // getEscapedClientCode());
			html = html.replace("%options%", getClassChangeOptions(player, newClass.getId()));
			showResult(player, html);
			return false;
		}
		else
		{
			ClassChangeData data = null;
			for (ClassChangeData ccd : _classChangeData)
			{
				if (ccd.isInCategory(player))
				{
					data = ccd;
					break;
				}
			}
			
			if (data != null)
			{
				// Required items.
				if (!data.getItemsRequired().isEmpty())
				{
					for (ItemHolder ri : data.getItemsRequired())
					{
						if (player.getInventory().getInventoryItemCount(ri.getId(), -1) < ri.getCount())
						{
							player.sendMessage("You do not have enough items.");
							return false; // No class change if payment failed.
						}
					}
					
					for (ItemHolder ri : data.getItemsRequired())
					{
						player.destroyItemByItemId(ItemProcessType.FEE, ri.getId(), ri.getCount(), player, true);
					}
				}
				
				// Give possible rewards.
				if (!data.getItemsRewarded().isEmpty())
				{
					for (ItemHolder ri : data.getItemsRewarded())
					{
						giveItems(player, ri);
					}
				}
				
				// Give possible nobless status reward.
				if (data.isRewardNoblesse())
				{
					player.setNobleLevel(1);
					checkNobleListener(player);
				}
				
				// Give possible hero status reward.
				if (data.isRewardHero())
				{
					player.setHero(true);
				}
			}
			
			player.setPlayerClass(newClass.getId());
			if (player.isSubClassActive())
			{
				player.getSubClasses().get(player.getClassIndex()).setPlayerClass(player.getActiveClass());
			}
			else
			{
				player.setBaseClass(player.getActiveClass());
			}
			
			if (player.isInCategory(CategoryType.SIXTH_CLASS_GROUP))
			{
				SkillTreeData.getInstance().cleanSkillUponChangeClass(player);
				for (SkillLearn skill : SkillTreeData.getInstance().getRaceSkillTree(player.getRace()))
				{
					player.addSkill(SkillData.getInstance().getSkill(skill.getSkillId(), skill.getSkillLevel()), true);
				}
			}
			
			if (PlayerConfig.AUTO_LEARN_SKILLS)
			{
				player.giveAvailableSkills(PlayerConfig.AUTO_LEARN_FS_SKILLS, PlayerConfig.AUTO_LEARN_FP_SKILLS, true, PlayerConfig.AUTO_LEARN_SKILLS_WITHOUT_ITEMS);
			}
			
			player.store(false); // Save player cause if server crashes before this char is saved, he will lose class and the money payed for class change.
			player.broadcastUserInfo();
			player.sendSkillList();
			return true;
		}
	}
	
	private void showPopupWindow(Player player)
	{
		if (!_showPopupWindow)
		{
			return;
		}
		
		// @formatter:off
		if ((player.isInCategory(CategoryType.FIRST_CLASS_GROUP) && (player.getLevel() >= 20)) ||
			((player.isInCategory(CategoryType.SECOND_CLASS_GROUP) || player.isInCategory(CategoryType.FIRST_CLASS_GROUP)) && (player.getLevel() >= 40)) ||
			(player.isInCategory(CategoryType.THIRD_CLASS_GROUP) && (player.getLevel() >= 76)) ||
			(player.isInCategory(CategoryType.FOURTH_CLASS_GROUP) && (player.getLevel() >= 85)))
		// @formatter:on
		{
			player.sendPacket(new TutorialShowQuestionMark(102, 0));
		}
	}
	
	@RegisterEvent(EventType.ON_PLAYER_PRESS_TUTORIAL_MARK)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onPlayerPressTutorialMark(OnPlayerPressTutorialMark event)
	{
		final Player player = event.getPlayer();
		if (!_showPopupWindow || (event.getMarkId() != 102))
		{
			return;
		}
		
		String html = null;
		if ((player.isInCategory(CategoryType.SECOND_CLASS_GROUP) || player.isInCategory(CategoryType.FIRST_CLASS_GROUP)) && (player.getLevel() >= 40)) // In retail you can skip first occupation
		{
			html = getHtm(player, getSecondOccupationChangeHtml(player));
		}
		else if (player.isInCategory(CategoryType.FIRST_CLASS_GROUP) && (player.getLevel() >= 20))
		{
			html = getHtm(player, getFirstOccupationChangeHtml(player));
		}
		else if (player.isInCategory(CategoryType.THIRD_CLASS_GROUP) && (player.getLevel() >= 76))
		{
			html = getHtm(player, "qm_thirdclass.html");
		}
		else if (player.isInCategory(CategoryType.FOURTH_CLASS_GROUP) && (player.getLevel() >= 85)) // 9
		{
			html = getHtm(player, "qm_awaken.html");
		}
		
		if (html != null)
		{
			showResult(event.getPlayer(), html);
			// player.sendPacket(new TutorialShowHtml(html));
		}
	}
	
	@RegisterEvent(EventType.ON_PLAYER_BYPASS)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onPlayerBypass(OnPlayerBypass event)
	{
		if (event.getCommand().startsWith("Quest ClassMaster "))
		{
			final String html = onEvent(event.getCommand().substring(18), null, event.getPlayer());
			event.getPlayer().sendPacket(TutorialCloseHtml.STATIC_PACKET);
			showResult(event.getPlayer(), html);
		}
	}
	
	@RegisterEvent(EventType.ON_PLAYER_PROFESSION_CHANGE)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onPlayerProfessionChange(OnPlayerProfessionChange event)
	{
		showPopupWindow(event.getPlayer());
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LEVEL_CHANGED)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onPlayerLevelChanged(OnPlayerLevelChanged event)
	{
		showPopupWindow(event.getPlayer());
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LOGIN)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onPlayerLogin(OnPlayerLogin event)
	{
		showPopupWindow(event.getPlayer());
	}
	
	private String getClassChangeOptions(Player player, int selectedClassId)
	{
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < _classChangeData.size(); i++)
		{
			final ClassChangeData option = getClassChangeData(i);
			if ((option == null) || option.getCategories().stream().noneMatch(player::isInCategory))
			{
				continue;
			}
			
			sb.append("<tr><td><img src=L2UI_CT1.ChatBalloon_DF_TopCenter width=276 height=1 /></td></tr>");
			sb.append("<tr><td><table bgcolor=3f3f3f width=100%>");
			sb.append("<tr><td align=center><a action=\"bypass -h Quest ClassMaster setclass " + selectedClassId + " " + i + "\">" + option.getName() + ":</a></td></tr>");
			sb.append("<tr><td><table width=276>");
			sb.append("<tr><td>Requirements:</td></tr>");
			if (option.getItemsRequired().isEmpty())
			{
				sb.append("<tr><td><font color=LEVEL>Free</font></td></tr>");
			}
			else
			{
				option.getItemsRequired().forEach(ih -> sb.append("<tr><td><font color=\"LEVEL\">" + ih.getCount() + "</font></td><td>" + ItemData.getInstance().getTemplate(ih.getId()).getName() + "</td><td width=30></td></tr>"));
			}
			sb.append("<tr><td>Rewards:</td></tr>");
			if (option.getItemsRewarded().isEmpty())
			{
				if (option.isRewardNoblesse())
				{
					sb.append("<tr><td><font color=\"LEVEL\">Noblesse status.</font></td></tr>");
				}
				
				if (option.isRewardHero())
				{
					sb.append("<tr><td><font color=\"LEVEL\">Hero status.</font></td></tr>");
				}
				
				if (!option.isRewardNoblesse() && !option.isRewardHero())
				{
					sb.append("<tr><td><font color=LEVEL>none</font></td></tr>");
				}
			}
			else
			{
				option.getItemsRewarded().forEach(ih -> sb.append("<tr><td><font color=\"LEVEL\">" + ih.getCount() + "</font></td><td>" + ItemData.getInstance().getTemplate(ih.getId()).getName() + "</td><td width=30></td></tr>"));
				if (option.isRewardNoblesse())
				{
					sb.append("<tr><td><font color=\"LEVEL\">Noblesse status.</font></td></tr>");
				}
				
				if (option.isRewardHero())
				{
					sb.append("<tr><td><font color=\"LEVEL\">Hero status.</font></td></tr>");
				}
			}
			
			sb.append("</table></td></tr>");
			sb.append("</table></td></tr>");
			sb.append("<tr><td><img src=L2UI_CT1.ChatBalloon_DF_TopCenter width=276 height=1 /></td></tr>");
		}
		
		return sb.toString();
	}
	
	private static class ClassChangeData
	{
		private final String _name;
		private final List<CategoryType> _appliedCategories;
		private boolean _rewardNoblesse;
		private boolean _rewardHero;
		private List<ItemHolder> _itemsRequired;
		private List<ItemHolder> _itemsRewarded;
		
		public ClassChangeData(String name, List<CategoryType> appliedCategories)
		{
			_name = name;
			_appliedCategories = appliedCategories != null ? appliedCategories : Collections.emptyList();
		}
		
		public String getName()
		{
			return _name;
		}
		
		public List<CategoryType> getCategories()
		{
			return _appliedCategories != null ? _appliedCategories : Collections.emptyList();
		}
		
		public boolean isInCategory(Player player)
		{
			if (_appliedCategories != null)
			{
				for (CategoryType category : _appliedCategories)
				{
					if (player.isInCategory(category))
					{
						return true;
					}
				}
			}
			
			return false;
		}
		
		public boolean isRewardNoblesse()
		{
			return _rewardNoblesse;
		}
		
		public void setRewardNoblesse(boolean rewardNoblesse)
		{
			_rewardNoblesse = rewardNoblesse;
		}
		
		public boolean isRewardHero()
		{
			return _rewardHero;
		}
		
		public void setRewardHero(boolean rewardHero)
		{
			_rewardHero = rewardHero;
		}
		
		void setItemsRequired(List<ItemHolder> itemsRequired)
		{
			_itemsRequired = itemsRequired;
		}
		
		public List<ItemHolder> getItemsRequired()
		{
			return _itemsRequired != null ? _itemsRequired : Collections.emptyList();
		}
		
		void setItemsRewarded(List<ItemHolder> itemsRewarded)
		{
			_itemsRewarded = itemsRewarded;
		}
		
		public List<ItemHolder> getItemsRewarded()
		{
			return _itemsRewarded != null ? _itemsRewarded : Collections.emptyList();
		}
	}
	
	private boolean checkIfClassChangeHasOptions(Player player)
	{
		boolean showOptions = false;
		
		// Check if there are requirements
		for (ClassChangeData ccd : _classChangeData)
		{
			if (!ccd.getItemsRequired().isEmpty() && ccd.isInCategory(player))
			{
				showOptions = true;
				break;
			}
		}
		
		if (!showOptions)
		{
			// Check if there is more than 1 reward to chose.
			int count = 0;
			for (ClassChangeData ccd : _classChangeData)
			{
				if (!ccd.getItemsRewarded().isEmpty() && ccd.isInCategory(player))
				{
					count++;
				}
			}
			
			if (count > 1)
			{
				showOptions = true;
			}
		}
		
		return showOptions;
	}
	
	private ClassChangeData getClassChangeData(int index)
	{
		if ((index >= 0) && (index < _classChangeData.size()))
		{
			return _classChangeData.get(index);
		}
		
		return null;
	}
	
	private void checkNobleListener(Player player)
	{
		// Notify to scripts.
		if (EventDispatcher.getInstance().hasListener(EventType.ON_PLAYER_BECOME_NOBLESSE))
		{
			EventDispatcher.getInstance().notifyEventAsync(new OnPlayerBecomeNoblesse(player));
		}
	}
	
	public static void main(String[] args)
	{
		new ClassMaster();
	}
}
