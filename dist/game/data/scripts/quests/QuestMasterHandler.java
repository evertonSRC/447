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
package quests;

import java.util.logging.Level;
import java.util.logging.Logger;

import quests.Q10001_NewPath.Q10001_NewPath;
import quests.Q10002_TrainingBeginsNow.Q10002_TrainingBeginsNow;
import quests.Q10003_StrengthOfSpirit.Q10003_StrengthOfSpirit;
import quests.Q10004_MysteriousPowersInfluence.Q10004_MysteriousPowersInfluence;
import quests.Q10005_RealBattle.Q10005_RealBattle;
import quests.Q10006_CurseOfUndying.Q10006_CurseOfUndying;
import quests.Q10007_PathOfDestinyBeginning.Q10007_PathOfDestinyBeginning;
import quests.Q10008_AntidoteIngredients.Q10008_AntidoteIngredients;
import quests.Q10009_Resurrected.Q10009_Resurrected;
import quests.Q10010_DeathlyMischief.Q10010_DeathlyMischief;
import quests.Q10011_DiscoveryOfWindSpiritRealmObjects.Q10011_DiscoveryOfWindSpiritRealmObjects;
import quests.Q10012_InSearchOfAClue.Q10012_InSearchOfAClue;
import quests.Q10013_DeathOfTelesha.Q10013_DeathOfTelesha;
import quests.Q10014_EnigmaticEncounter.Q10014_EnigmaticEncounter;
import quests.Q10015_PathOfDestinyProving.Q10015_PathOfDestinyProving;
import quests.Q10016_ChangedSpirits.Q10016_ChangedSpirits;
import quests.Q10017_WhyAreTheRatelHere.Q10017_WhyAreTheRatelHere;
import quests.Q10018_ViolentGrowlers.Q10018_ViolentGrowlers;
import quests.Q10019_CommunicationBreakdown.Q10019_CommunicationBreakdown;
import quests.Q10020_AttackOfTheEnragedForest.Q10020_AttackOfTheEnragedForest;
import quests.Q10021_EssenceOfTheProphecy.Q10021_EssenceOfTheProphecy;
import quests.Q10022_HelpersIdentity.Q10022_HelpersIdentity;
import quests.Q10023_ProphesiedOne.Q10023_ProphesiedOne;
import quests.Q10024_PathOfDestinyConviction.Q10024_PathOfDestinyConviction;
import quests.Q10025_CheckOutTheSituation.Q10025_CheckOutTheSituation;
import quests.Q10026_SuspiciousMovements.Q10026_SuspiciousMovements;
import quests.Q10027_SomeonesTraces.Q10027_SomeonesTraces;
import quests.Q10028_KetraOrcs.Q10028_KetraOrcs;
import quests.Q10029_TheyMustBeUpToSomething.Q10029_TheyMustBeUpToSomething;
import quests.Q10030_PrayingForSafety.Q10030_PrayingForSafety;
import quests.Q10031_ProphecyMachineRestoration.Q10031_ProphecyMachineRestoration;
import quests.Q10032_ToGereth.Q10032_ToGereth;
import quests.Q10033_ProphecyInterpretation.Q10033_ProphecyInterpretation;
import quests.Q10034_ChamberOfProphecies.Q10034_ChamberOfProphecies;
import quests.Q10035_ConcentratedMagicalEnergy.Q10035_ConcentratedMagicalEnergy;
import quests.Q10036_PathODestinyOvercome.Q10036_PathODestinyOvercome;
import quests.Q10101_NewHope.Q10101_NewHope;
import quests.Q10102_TrainingBeginsNow.Q10102_TrainingBeginsNow;
import quests.Q10103_StrengthOfSpirit.Q10103_StrengthOfSpirit;
import quests.Q10104_MysteriousPowersInfluence.Q10104_MysteriousPowersInfluence;
import quests.Q10105_RealBattle.Q10105_RealBattle;
import quests.Q10106_CurseOfUndying.Q10106_CurseOfUndying;
import quests.Q10107_SplitDestiny.Q10107_SplitDestiny;
import quests.Q10108_AntidoteIngredients.Q10108_AntidoteIngredients;
import quests.Q10109_Resurrected.Q10109_Resurrected;
import quests.Q10110_DeathlyMischief.Q10110_DeathlyMischief;
import quests.Q10111_DiscoveryOfWindSpiritRealmObjects.Q10111_DiscoveryOfWindSpiritRealmObjects;
import quests.Q10112_InSearchOfAClue.Q10112_InSearchOfAClue;
import quests.Q10113_DeathOfTelesha.Q10113_DeathOfTelesha;
import quests.Q10114_EnigmaticEncounter.Q10114_EnigmaticEncounter;
import quests.Q10115_WindsOfFateEncounters.Q10115_WindsOfFateEncounters;
import quests.Q10116_ChangedSpirits.Q10116_ChangedSpirits;
import quests.Q10117_WhyAreTheRatelHere.Q10117_WhyAreTheRatelHere;
import quests.Q10118_ViolentGrowlers.Q10118_ViolentGrowlers;
import quests.Q10119_CommunicationBreakdown.Q10119_CommunicationBreakdown;
import quests.Q10120_AttackOfTheEnragedForest.Q10120_AttackOfTheEnragedForest;
import quests.Q10121_EssenceOfTheProphecy.Q10121_EssenceOfTheProphecy;
import quests.Q10122_HelpersIdentity.Q10122_HelpersIdentity;
import quests.Q10123_ProphesiedOne.Q10123_ProphesiedOne;
import quests.Q10124_PathOfDestinyConviction.Q10124_PathOfDestinyConviction;
import quests.Q10125_CheckOutTheSituation.Q10125_CheckOutTheSituation;
import quests.Q10126_SuspiciousMovements.Q10126_SuspiciousMovements;
import quests.Q10127_SomeonesTraces.Q10127_SomeonesTraces;
import quests.Q10128_KetraOrcs.Q10128_KetraOrcs;
import quests.Q10129_TheyMustBeUpToSomething.Q10129_TheyMustBeUpToSomething;
import quests.Q10130_PrayingForSafety.Q10130_PrayingForSafety;
import quests.Q10131_ProphecyMachineRestoration.Q10131_ProphecyMachineRestoration;
import quests.Q10132_ToGereth.Q10132_ToGereth;
import quests.Q10133_ProphecyInterpretation.Q10133_ProphecyInterpretation;
import quests.Q10134_ChamberOfProphecies.Q10134_ChamberOfProphecies;
import quests.Q10135_ConcentratedMagicalEnergy.Q10135_ConcentratedMagicalEnergy;
import quests.Q10136_WindOfDestinyChoice.Q10136_WindOfDestinyChoice;
import quests.Q10201_NewLife.Q10201_NewLife;
import quests.Q10202_TrainingBeginsNow.Q10202_TrainingBeginsNow;
import quests.Q10203_StrengthOfSpirit.Q10203_StrengthOfSpirit;
import quests.Q10204_MysteriousPowersInfluence.Q10204_MysteriousPowersInfluence;
import quests.Q10205_RealBattle.Q10205_RealBattle;
import quests.Q10206_CurseOfUndying.Q10206_CurseOfUndying;
import quests.Q10207_PathOfDestinyBeginning.Q10207_PathOfDestinyBeginning;
import quests.Q10208_AntidoteIngredients.Q10208_AntidoteIngredients;
import quests.Q10209_Resurrected.Q10209_Resurrected;
import quests.Q10210_DeathlyMischief.Q10210_DeathlyMischief;
import quests.Q10211_DiscoveryOfWindSpiritRealmObjects.Q10211_DiscoveryOfWindSpiritRealmObjects;
import quests.Q10212_InSearchOfAClue.Q10212_InSearchOfAClue;
import quests.Q10213_DeathOfTelesha.Q10213_DeathOfTelesha;
import quests.Q10214_EnigmaticEncounter.Q10214_EnigmaticEncounter;
import quests.Q10215_PathOfDestinyProving.Q10215_PathOfDestinyProving;
import quests.Q10216_ChangedSpirits.Q10216_ChangedSpirits;
import quests.Q10217_WhyAreTheRatelHere.Q10217_WhyAreTheRatelHere;
import quests.Q10218_ViolentGrowlers.Q10218_ViolentGrowlers;
import quests.Q10219_CommunicationBreakdown.Q10219_CommunicationBreakdown;
import quests.Q10220_AttackOfTheEnragedForest.Q10220_AttackOfTheEnragedForest;
import quests.Q10221_EssenceOfTheProphecy.Q10221_EssenceOfTheProphecy;
import quests.Q10222_HelpersIdentity.Q10222_HelpersIdentity;
import quests.Q10223_ProphesiedOne.Q10223_ProphesiedOne;
import quests.Q10224_PathOfDestinyConviction.Q10224_PathOfDestinyConviction;
import quests.Q10225_CheckOutTheSituation.Q10225_CheckOutTheSituation;
import quests.Q10226_SuspiciousMovements.Q10226_SuspiciousMovements;
import quests.Q10227_SomeonesTraces.Q10227_SomeonesTraces;
import quests.Q10228_KetraOrcs.Q10228_KetraOrcs;
import quests.Q10229_TheyMustBeUpToSomething.Q10229_TheyMustBeUpToSomething;
import quests.Q10230_PrayingForSafety.Q10230_PrayingForSafety;
import quests.Q10231_ProphecyMachineRestoration.Q10231_ProphecyMachineRestoration;
import quests.Q10232_ToGereth.Q10232_ToGereth;
import quests.Q10233_ProphecyInterpretation.Q10233_ProphecyInterpretation;
import quests.Q10234_ChamberOfProphecies.Q10234_ChamberOfProphecies;
import quests.Q10235_ConcentratedMagicalEnergy.Q10235_ConcentratedMagicalEnergy;
import quests.Q10236_PathODestinyOvercome.Q10236_PathODestinyOvercome;
import quests.Q10301_ResearchersPath.Q10301_ResearchersPath;
import quests.Q10302_TrainingBeginsNow.Q10302_TrainingBeginsNow;
import quests.Q10303_StrengthOfSpirit.Q10303_StrengthOfSpirit;
import quests.Q10304_MysteriousPowersInfluence.Q10304_MysteriousPowersInfluence;
import quests.Q10305_RealBattle.Q10305_RealBattle;
import quests.Q10306_CurseOfUndying.Q10306_CurseOfUndying;
import quests.Q10307_PathOfDestinyBeginning.Q10307_PathOfDestinyBeginning;
import quests.Q10308_AntidoteIngredients.Q10308_AntidoteIngredients;
import quests.Q10309_Resurrected.Q10309_Resurrected;
import quests.Q10310_DeathlyMischief.Q10310_DeathlyMischief;
import quests.Q10311_DiscoveryOfWindSpiritRealmObjects.Q10311_DiscoveryOfWindSpiritRealmObjects;
import quests.Q10312_InSearchOfAClue.Q10312_InSearchOfAClue;
import quests.Q10313_DeathOfTelesha.Q10313_DeathOfTelesha;
import quests.Q10314_EnigmaticEncounter.Q10314_EnigmaticEncounter;
import quests.Q10315_PathOfDestinyProving.Q10315_PathOfDestinyProving;
import quests.Q10316_ChangedSpirits.Q10316_ChangedSpirits;
import quests.Q10317_WhyAreTheRatelHere.Q10317_WhyAreTheRatelHere;
import quests.Q10318_ViolentGrowlers.Q10318_ViolentGrowlers;
import quests.Q10319_CommunicationBreakdown.Q10319_CommunicationBreakdown;
import quests.Q10320_AttackOfTheEnragedForest.Q10320_AttackOfTheEnragedForest;
import quests.Q10321_EssenceOfTheProphecy.Q10321_EssenceOfTheProphecy;
import quests.Q10322_HelpersIdentity.Q10322_HelpersIdentity;
import quests.Q10323_ProphesiedOne.Q10323_ProphesiedOne;
import quests.Q10324_PathOfDestinyConviction.Q10324_PathOfDestinyConviction;
import quests.Q10325_CheckOutTheSituation.Q10325_CheckOutTheSituation;
import quests.Q10326_SuspiciousMovements.Q10326_SuspiciousMovements;
import quests.Q10327_SomeonesTraces.Q10327_SomeonesTraces;
import quests.Q10328_KetraOrcs.Q10328_KetraOrcs;
import quests.Q10329_TheyMustBeUpToSomething.Q10329_TheyMustBeUpToSomething;
import quests.Q10330_PrayingForSafety.Q10330_PrayingForSafety;
import quests.Q10331_ProphecyMachineRestoration.Q10331_ProphecyMachineRestoration;
import quests.Q10332_ToGereth.Q10332_ToGereth;
import quests.Q10333_ProphecyInterpretation.Q10333_ProphecyInterpretation;
import quests.Q10334_ChamberOfProphecies.Q10334_ChamberOfProphecies;
import quests.Q10335_ConcentratedMagicalEnergy.Q10335_ConcentratedMagicalEnergy;
import quests.Q10336_PathODestinyOvercome.Q10336_PathODestinyOvercome;
import quests.Q10500_AdenAdventure.Q10500_AdenAdventure;
import quests.Q10501_AdenAdventure.Q10501_AdenAdventure;
import quests.Q10502_AStepToYourDestiny1.Q10502_AStepToYourDestiny1;
import quests.Q10503_AStepToYourDestiny2.Q10503_AStepToYourDestiny2;
import quests.Q10504_AStepToYourDestiny3.Q10504_AStepToYourDestiny3;
import quests.Q10505_AStepToYourDestiny1.Q10505_AStepToYourDestiny1;
import quests.Q10506_AStepToYourDestiny2.Q10506_AStepToYourDestiny2;
import quests.Q10507_AStepToYourDestiny3.Q10507_AStepToYourDestiny3;
import quests.Q10508_AStepToYourDestiny3.Q10508_AStepToYourDestiny3;
import quests.Q10509_NobleMaterial1.Q10509_NobleMaterial1;
import quests.Q10510_NobleMaterial1.Q10510_NobleMaterial1;
import quests.Q10511_NobleMaterial2.Q10511_NobleMaterial2;
import quests.Q10512_NobleMaterial2.Q10512_NobleMaterial2;
import quests.Q10513_NobleMaterial3.Q10513_NobleMaterial3;
import quests.Q10514_NobleMaterial4.Q10514_NobleMaterial4;
import quests.Q10515_NobleMaterial4.Q10515_NobleMaterial4;
import quests.Q10516_NobleMaterial4.Q10516_NobleMaterial4;
import quests.Q10517_NobleMaterial4.Q10517_NobleMaterial4;
import quests.Q10518_ExaltedOneWhoFacesTheLimit.Q10518_ExaltedOneWhoFacesTheLimit;
import quests.Q10519_LevelUpTo101.Q10519_LevelUpTo101;
import quests.Q10520_ExaltedOneWhoOvercomesTheLimit.Q10520_ExaltedOneWhoOvercomesTheLimit;
import quests.Q10521_LevelUpTo102.Q10521_LevelUpTo102;
import quests.Q10522_ExaltedOneWhoShattersTheLimit.Q10522_ExaltedOneWhoShattersTheLimit;
import quests.Q10523_LevelUpTo103.Q10523_LevelUpTo103;
import quests.Q10524_ExaltedReachingAnotherLevel.Q10524_ExaltedReachingAnotherLevel;
import quests.Q10525_LevelUpTo105.Q10525_LevelUpTo105;
import quests.Q10526_ExaltedGuideToPower.Q10526_ExaltedGuideToPower;
import quests.Q10527_LevelUpTo107.Q10527_LevelUpTo107;
import quests.Q10528_ExaltedObtainingNewPower.Q10528_ExaltedObtainingNewPower;
import quests.Q10529_LevelUpTo110.Q10529_LevelUpTo110;
import quests.Q10530_ExaltedPowerHarmony.Q10530_ExaltedPowerHarmony;
import quests.Q10531_LevelUpTo115.Q10531_LevelUpTo115;
import quests.Q20001_BrothersBoundInChains.Q20001_BrothersBoundInChains;
import quests.Q20281_ReconnaissanceInLangkLizardmenBarracks.Q20281_ReconnaissanceInLangkLizardmenBarracks;
import quests.Q20282_FreePrisonersInLangkLizardmenTemple1.Q20282_FreePrisonersInLangkLizardmenTemple1;
import quests.Q20283_FreePrisonersInLangkLizardmenTemple2.Q20283_FreePrisonersInLangkLizardmenTemple2;
import quests.Q21001_TombRaiders.Q21001_TombRaiders;
import quests.Q21002_JourneyToTheConquestWorld.Q21002_JourneyToTheConquestWorld;
import quests.Q21003_HuntingTime1.Q21003_HuntingTime1;
import quests.Q21004_HuntingTime2.Q21004_HuntingTime2;
import quests.Q21005_HuntingTime3.Q21005_HuntingTime3;
import quests.Q21006_HuntingTime4.Q21006_HuntingTime4;
import quests.Q21007_ChasingTheLight.Q21007_ChasingTheLight;
import quests.Q21008_WhereFlowersBlossom.Q21008_WhereFlowersBlossom;
import quests.Q21009_SunOfDarknessPrimalFire.Q21009_SunOfDarknessPrimalFire;
import quests.Q21010_FlameHunting1.Q21010_FlameHunting1;
import quests.Q21011_FlameHunting2.Q21011_FlameHunting2;
import quests.Q21012_FlameHunting3.Q21012_FlameHunting3;
import quests.Q21013_FlameHunting4.Q21013_FlameHunting4;
import quests.Q21014_InTheSearchOfTheFireSource1.Q21014_InTheSearchOfTheFireSource1;
import quests.Q21015_InTheSearchOfTheFireSource2.Q21015_InTheSearchOfTheFireSource2;
import quests.Q21016_FireSmell1.Q21016_FireSmell1;
import quests.Q21017_FireSmell2.Q21017_FireSmell2;
import quests.Q21018_FireSmell3.Q21018_FireSmell3;

/**
 * @author NosBit, Mobius
 */
public class QuestMasterHandler
{
	private static final Logger LOGGER = Logger.getLogger(QuestMasterHandler.class.getName());
	
	private static final Class<?>[] QUESTS =
	{
		Q10001_NewPath.class,
		Q10002_TrainingBeginsNow.class,
		Q10003_StrengthOfSpirit.class,
		Q10004_MysteriousPowersInfluence.class,
		Q10005_RealBattle.class,
		Q10006_CurseOfUndying.class,
		Q10007_PathOfDestinyBeginning.class,
		Q10008_AntidoteIngredients.class,
		Q10009_Resurrected.class,
		Q10010_DeathlyMischief.class,
		Q10011_DiscoveryOfWindSpiritRealmObjects.class,
		Q10012_InSearchOfAClue.class,
		Q10013_DeathOfTelesha.class,
		Q10014_EnigmaticEncounter.class,
		Q10015_PathOfDestinyProving.class,
		Q10016_ChangedSpirits.class,
		Q10017_WhyAreTheRatelHere.class,
		Q10018_ViolentGrowlers.class,
		Q10019_CommunicationBreakdown.class,
		Q10020_AttackOfTheEnragedForest.class,
		Q10021_EssenceOfTheProphecy.class,
		Q10022_HelpersIdentity.class,
		Q10023_ProphesiedOne.class,
		Q10024_PathOfDestinyConviction.class,
		Q10025_CheckOutTheSituation.class,
		Q10026_SuspiciousMovements.class,
		Q10027_SomeonesTraces.class,
		Q10028_KetraOrcs.class,
		Q10029_TheyMustBeUpToSomething.class,
		Q10030_PrayingForSafety.class,
		Q10031_ProphecyMachineRestoration.class,
		Q10032_ToGereth.class,
		Q10033_ProphecyInterpretation.class,
		Q10034_ChamberOfProphecies.class,
		Q10035_ConcentratedMagicalEnergy.class,
		Q10036_PathODestinyOvercome.class,
		Q10101_NewHope.class,
		Q10102_TrainingBeginsNow.class,
		Q10103_StrengthOfSpirit.class,
		Q10104_MysteriousPowersInfluence.class,
		Q10105_RealBattle.class,
		Q10106_CurseOfUndying.class,
		Q10107_SplitDestiny.class,
		Q10108_AntidoteIngredients.class,
		Q10109_Resurrected.class,
		Q10110_DeathlyMischief.class,
		Q10111_DiscoveryOfWindSpiritRealmObjects.class,
		Q10112_InSearchOfAClue.class,
		Q10113_DeathOfTelesha.class,
		Q10114_EnigmaticEncounter.class,
		Q10115_WindsOfFateEncounters.class,
		Q10116_ChangedSpirits.class,
		Q10117_WhyAreTheRatelHere.class,
		Q10118_ViolentGrowlers.class,
		Q10119_CommunicationBreakdown.class,
		Q10120_AttackOfTheEnragedForest.class,
		Q10121_EssenceOfTheProphecy.class,
		Q10122_HelpersIdentity.class,
		Q10123_ProphesiedOne.class,
		Q10124_PathOfDestinyConviction.class,
		Q10125_CheckOutTheSituation.class,
		Q10126_SuspiciousMovements.class,
		Q10127_SomeonesTraces.class,
		Q10128_KetraOrcs.class,
		Q10129_TheyMustBeUpToSomething.class,
		Q10130_PrayingForSafety.class,
		Q10131_ProphecyMachineRestoration.class,
		Q10132_ToGereth.class,
		Q10133_ProphecyInterpretation.class,
		Q10134_ChamberOfProphecies.class,
		Q10135_ConcentratedMagicalEnergy.class,
		Q10136_WindOfDestinyChoice.class,
		Q10201_NewLife.class,
		Q10202_TrainingBeginsNow.class,
		Q10203_StrengthOfSpirit.class,
		Q10204_MysteriousPowersInfluence.class,
		Q10205_RealBattle.class,
		Q10206_CurseOfUndying.class,
		Q10207_PathOfDestinyBeginning.class,
		Q10208_AntidoteIngredients.class,
		Q10209_Resurrected.class,
		Q10210_DeathlyMischief.class,
		Q10211_DiscoveryOfWindSpiritRealmObjects.class,
		Q10212_InSearchOfAClue.class,
		Q10213_DeathOfTelesha.class,
		Q10214_EnigmaticEncounter.class,
		Q10215_PathOfDestinyProving.class,
		Q10216_ChangedSpirits.class,
		Q10217_WhyAreTheRatelHere.class,
		Q10218_ViolentGrowlers.class,
		Q10219_CommunicationBreakdown.class,
		Q10220_AttackOfTheEnragedForest.class,
		Q10221_EssenceOfTheProphecy.class,
		Q10222_HelpersIdentity.class,
		Q10223_ProphesiedOne.class,
		Q10224_PathOfDestinyConviction.class,
		Q10225_CheckOutTheSituation.class,
		Q10226_SuspiciousMovements.class,
		Q10227_SomeonesTraces.class,
		Q10228_KetraOrcs.class,
		Q10229_TheyMustBeUpToSomething.class,
		Q10230_PrayingForSafety.class,
		Q10231_ProphecyMachineRestoration.class,
		Q10232_ToGereth.class,
		Q10233_ProphecyInterpretation.class,
		Q10234_ChamberOfProphecies.class,
		Q10235_ConcentratedMagicalEnergy.class,
		Q10236_PathODestinyOvercome.class,
		Q10301_ResearchersPath.class,
		Q10302_TrainingBeginsNow.class,
		Q10303_StrengthOfSpirit.class,
		Q10304_MysteriousPowersInfluence.class,
		Q10305_RealBattle.class,
		Q10306_CurseOfUndying.class,
		Q10307_PathOfDestinyBeginning.class,
		Q10308_AntidoteIngredients.class,
		Q10309_Resurrected.class,
		Q10310_DeathlyMischief.class,
		Q10311_DiscoveryOfWindSpiritRealmObjects.class,
		Q10312_InSearchOfAClue.class,
		Q10313_DeathOfTelesha.class,
		Q10314_EnigmaticEncounter.class,
		Q10315_PathOfDestinyProving.class,
		Q10316_ChangedSpirits.class,
		Q10317_WhyAreTheRatelHere.class,
		Q10318_ViolentGrowlers.class,
		Q10319_CommunicationBreakdown.class,
		Q10320_AttackOfTheEnragedForest.class,
		Q10321_EssenceOfTheProphecy.class,
		Q10322_HelpersIdentity.class,
		Q10323_ProphesiedOne.class,
		Q10324_PathOfDestinyConviction.class,
		Q10325_CheckOutTheSituation.class,
		Q10326_SuspiciousMovements.class,
		Q10327_SomeonesTraces.class,
		Q10328_KetraOrcs.class,
		Q10329_TheyMustBeUpToSomething.class,
		Q10330_PrayingForSafety.class,
		Q10331_ProphecyMachineRestoration.class,
		Q10332_ToGereth.class,
		Q10333_ProphecyInterpretation.class,
		Q10334_ChamberOfProphecies.class,
		Q10335_ConcentratedMagicalEnergy.class,
		Q10336_PathODestinyOvercome.class,
		Q10500_AdenAdventure.class,
		Q10501_AdenAdventure.class,
		Q10502_AStepToYourDestiny1.class,
		Q10503_AStepToYourDestiny2.class,
		Q10504_AStepToYourDestiny3.class,
		Q10505_AStepToYourDestiny1.class,
		Q10506_AStepToYourDestiny2.class,
		Q10507_AStepToYourDestiny3.class,
		Q10508_AStepToYourDestiny3.class,
		Q10509_NobleMaterial1.class,
		Q10510_NobleMaterial1.class,
		Q10511_NobleMaterial2.class,
		Q10512_NobleMaterial2.class,
		Q10513_NobleMaterial3.class,
		Q10514_NobleMaterial4.class,
		Q10515_NobleMaterial4.class,
		Q10516_NobleMaterial4.class,
		Q10517_NobleMaterial4.class,
		Q10518_ExaltedOneWhoFacesTheLimit.class,
		Q10519_LevelUpTo101.class,
		Q10520_ExaltedOneWhoOvercomesTheLimit.class,
		Q10521_LevelUpTo102.class,
		Q10522_ExaltedOneWhoShattersTheLimit.class,
		Q10523_LevelUpTo103.class,
		Q10524_ExaltedReachingAnotherLevel.class,
		Q10525_LevelUpTo105.class,
		Q10526_ExaltedGuideToPower.class,
		Q10527_LevelUpTo107.class,
		Q10528_ExaltedObtainingNewPower.class,
		Q10529_LevelUpTo110.class,
		Q10530_ExaltedPowerHarmony.class,
		Q10531_LevelUpTo115.class,
		Q20001_BrothersBoundInChains.class,
		Q20281_ReconnaissanceInLangkLizardmenBarracks.class,
		Q20282_FreePrisonersInLangkLizardmenTemple1.class,
		Q20283_FreePrisonersInLangkLizardmenTemple2.class,
		Q21001_TombRaiders.class,
		Q21002_JourneyToTheConquestWorld.class,
		Q21003_HuntingTime1.class,
		Q21004_HuntingTime2.class,
		Q21005_HuntingTime3.class,
		Q21006_HuntingTime4.class,
		Q21007_ChasingTheLight.class,
		Q21008_WhereFlowersBlossom.class,
		Q21009_SunOfDarknessPrimalFire.class,
		Q21010_FlameHunting1.class,
		Q21011_FlameHunting2.class,
		Q21012_FlameHunting3.class,
		Q21013_FlameHunting4.class,
		Q21014_InTheSearchOfTheFireSource1.class,
		Q21015_InTheSearchOfTheFireSource2.class,
		Q21016_FireSmell1.class,
		Q21017_FireSmell2.class,
		Q21018_FireSmell3.class,
	};
	
	public static void main(String[] args)
	{
		for (Class<?> quest : QUESTS)
		{
			try
			{
				quest.getDeclaredConstructor().newInstance();
			}
			catch (Exception e)
			{
				LOGGER.log(Level.SEVERE, QuestMasterHandler.class.getSimpleName() + ": Failed loading " + quest.getSimpleName() + ":", e);
			}
		}
	}
}
