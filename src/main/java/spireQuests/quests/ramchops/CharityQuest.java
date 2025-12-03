package spireQuests.quests.ramchops;

import basemod.abstracts.CustomSavable;
import basemod.helpers.CardPowerTip;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.Ectoplasm;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import spireQuests.Anniv8Mod;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;
import spireQuests.quests.ramchops.cards.SelloutAdvertisementCard;
import spireQuests.quests.ramchops.trackers.AdsPlayedQuestTracker;
import spireQuests.quests.ramchops.trackers.ClericFundsTracker;
import spireQuests.quests.ramchops.trackers.ClericRewardTracker;
import spireQuests.quests.ramchops.trackers.SelloutCombatTracker;

import java.util.List;

import static spireQuests.util.Wiz.adp;

public class CharityQuest extends AbstractQuest implements CustomSavable<Integer>{

    int maxHPGain = 0;

    public CharityQuest() {
        super(QuestType.SHORT, QuestDifficulty.EASY);

        useDefaultReward = false;

        new ClericFundsTracker().add(this);
        new ClericRewardTracker().add(this);
        new TriggerTracker<>(QuestTriggers.GAIN_MONEY, 1).add(this);
        addReward(new QuestReward.MaxHPReward(0));
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onComplete() {
        questRewards.clear();
        addReward(new QuestReward.MaxHPReward(maxHPGain));
    }

    @Override
    public boolean canSpawn() {
        return AbstractDungeon.actNum >= 1 && AbstractDungeon.actNum <= 2 && !adp().hasRelic(Ectoplasm.ID);
    }

    @Override
    public boolean complete() {

        if(!this.isCompleted()){
            Object o = trackers.get(1);

            if(o instanceof ClericRewardTracker){
                maxHPGain = ((ClericRewardTracker) o).localCount;
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
}

