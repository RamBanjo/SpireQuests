package spireQuests.quests.ramchops.trackers;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.OptionalTriggerTracker;
import spireQuests.quests.Trigger;


public class ClericRewardTracker extends OptionalTriggerTracker<Integer> {

    public ClericRewardTracker(){
        super(ClericFundsTracker.CLERIC_FUND_CHANGE, 0, true);
    }

    @Override
    public void trigger(Integer param) {

        int DIVISOR = 15;
        localCount = Math.floorDiv(param, DIVISOR);
    }
}
