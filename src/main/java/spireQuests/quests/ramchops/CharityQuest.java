package spireQuests.quests.ramchops;

import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.relics.Ectoplasm;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;
import javassist.CtBehavior;
import spireQuests.Anniv8Mod;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestManager;
import spireQuests.quests.QuestReward;
import spireQuests.quests.ramchops.monsters.EvilSentry;
import spireQuests.quests.ramchops.trackers.ClericFundsTracker;
import spireQuests.quests.ramchops.trackers.ClericRewardTracker;

import java.util.ArrayList;
import java.util.List;

import static spireQuests.Anniv8Mod.makeID;
import static spireQuests.util.Wiz.adp;

public class CharityQuest extends AbstractQuest implements CustomSavable<Integer>{

    private static final String ID = makeID(CharityQuest.class.getSimpleName());
    private static String questWarning;
    private static String clericMsgTitle;
    private static String clericMsgBody;

    int maxHPGain = 0;

    public CharityQuest() {
        super(QuestType.SHORT, QuestDifficulty.EASY);

        useDefaultReward = false;

        new ClericFundsTracker().add(this);
        new ClericRewardTracker().add(this);
        new TriggerTracker<Integer>(QuestTriggers.GAIN_MONEY, 1){
            @Override
            public boolean isComplete() {
                return super.isComplete() || adp().hasRelic(Ectoplasm.ID);
            }
        }.add(this);

        needHoverTip = true;
        isAutoComplete = true;

        questWarning = questStrings.EXTRA_TEXT[0];
        clericMsgTitle = questStrings.EXTRA_TEXT[1];
        clericMsgBody = questStrings.EXTRA_TEXT[2];
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onComplete() {
        questRewards.clear();

        if (maxHPGain <= 0) maxHPGain = 0;
        addReward(new QuestReward.MaxHPReward(maxHPGain));
    }

    @Override
    public boolean canSpawn() {
        return AbstractDungeon.actNum >= 1 && AbstractDungeon.actNum <= 2 && !adp().hasRelic(Ectoplasm.ID);
    }

    @Override
    public boolean complete() {
        if(questConditionsAreFulfilled() && maxHPGain == 0){
            if(trackers.size() > 1 && trackers.get(1) instanceof ClericRewardTracker){
                maxHPGain = ((ClericRewardTracker)trackers.get(1)).localCount;
                if (maxHPGain == 0){
                    maxHPGain = -1;
                }
            }else{
                Anniv8Mod.logger.warn("Failed to detect ClericRewardTracker in the list of trackers. Please tell Ram to fix the code.");
            }
        }

        return super.complete();
    }

    @Override
    public Integer onSave() {
        return maxHPGain;
    }

    @Override
    public void onLoad(Integer integer) {
        maxHPGain = integer;
    }

    @Override
    public void makeTooltips(List<PowerTip> tipList) {
        super.makeTooltips(tipList);

        Ectoplasm ecto = new Ectoplasm();

        tipList.add(new PowerTip(ecto.name, ecto.description));
    }

    @SpirePatch2(
            clz = RewardItem.class,
            method = "applyGoldBonus",
            paramtypez = boolean.class)
    public static class goldWarningPatch{
        @SpirePostfixPatch
        public static void goldWarning(RewardItem __instance, boolean theft){
            // if this quest exists
            CharityQuest q = (CharityQuest) QuestManager.quests().stream()
                    .filter(quest -> ID.equals(quest.id) && !quest.isCompleted() && !quest.isFailed())
                    .findAny()
                    .orElse(null);
            if(q != null) {

                ArrayList<PowerTip> ptList = new ArrayList<>();

                ptList.add(new PowerTip(clericMsgTitle, clericMsgBody));
                TipHelper.queuePowerTips(360.0F * Settings.scale, (float) InputHelper.mY, ptList);
                __instance.text += questWarning;
            }
        }
    }

    @SpirePatch2(
            clz = RewardItem.class,
            method = "render",
            paramtypez = SpriteBatch.class)
    public static class goldTooltipPatch {
        @SpireInsertPatch(
                locator = Locator.class)
        public static void goldTooltip(RewardItem __instance, SpriteBatch sb){
            // if this quest exists
            CharityQuest q = (CharityQuest) QuestManager.quests().stream()
                    .filter(quest -> ID.equals(quest.id) && !quest.isCompleted() && !quest.isFailed())
                    .findAny()
                    .orElse(null);
            if(q != null) {

                if (__instance.hb.hovered && (__instance.type == RewardItem.RewardType.GOLD || __instance.type == RewardItem.RewardType.STOLEN_GOLD)){
                    ArrayList<PowerTip> ptList = new ArrayList<>();
                    ptList.add(new PowerTip(clericMsgTitle, clericMsgBody));
                    TipHelper.queuePowerTips(360.0F * Settings.scale, (float) InputHelper.mY, ptList);
                }


            }
        }
    }

    private static class Locator extends SpireInsertLocator{

        @Override
        public int[] Locate(CtBehavior ctBehavior) throws Exception {

            Matcher finalMatcher = new Matcher.MethodCallMatcher(Hitbox.class, "render");

            ArrayList<Matcher> linkPrereq = new ArrayList<>();
            linkPrereq.add(new Matcher.MethodCallMatcher(RewardItem.class, "renderRelicLink"));

            return LineFinder.findInOrder(ctBehavior, linkPrereq, finalMatcher);
        }
    }
}
