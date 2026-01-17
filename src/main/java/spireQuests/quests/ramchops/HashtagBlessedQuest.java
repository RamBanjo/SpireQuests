package spireQuests.quests.ramchops;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import spireQuests.Anniv8Mod;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestManager;
import spireQuests.quests.QuestReward;
import spireQuests.quests.ramchops.monsters.EvilSentry;
import spireQuests.quests.ramchops.relics.MahjongRelic;
import spireQuests.util.Wiz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static spireQuests.Anniv8Mod.makeID;

public class HashtagBlessedQuest extends AbstractQuest {

    private static final String ID = makeID(HashtagBlessedQuest.class.getSimpleName());

    public HashtagBlessedQuest(){
        super(QuestType.LONG, QuestDifficulty.NORMAL);

        new TriggerTracker<>(QuestTriggers.VICTORY, 1).triggerCondition(
                (x) -> AbstractDungeon.getCurrRoom() instanceof MonsterRoomBoss && AbstractDungeon.actNum == 2
        ).add(this);

        addReward(new QuestReward.RelicReward(new MahjongRelic()));

        this.isAutoComplete = true;
    }

    @Override
    public boolean canSpawn() {
        return AbstractDungeon.actNum == 1;
    }


    @SpirePatch2(
            clz = MonsterRoom.class,
            method = "onPlayerEntry"
    )
    public static class BlessingPatch{
        @SpirePostfixPatch
        public static void SelectRandomEnemyForBlessing(MonsterRoom __instance){
            // if this quest exists
            HashtagBlessedQuest q = (HashtagBlessedQuest) QuestManager.quests().stream()
                    .filter(quest -> ID.equals(quest.id) && !quest.isCompleted() && !quest.isFailed())
                    .findAny()
                    .orElse(null);
            if(q != null) {
                Anniv8Mod.logger.info("Choosing random enemy to bless...");
                AbstractMonster blessTarget = getRandomNonMinionEnemy();

                if (blessTarget == null){
                    Anniv8Mod.logger.warn("Can't bless anyone because everyone is a minion.");
                }else{
                    giveBlessing(blessTarget);
                }

            }
        }
    }

    enum MonsterBlessing{
        RITUAL,
        STRENGTH,
        PAINSTAB,
        SPIKES,
        HEARTBEAT,
        METALLICIZE,
        TIMEWARP,
        PLATED,
        BUFFER,
        HIDE,
        REGEN,
        INTANGIBLE
    }

    public static void giveBlessing(AbstractMonster m){
        List<MonsterBlessing> blessings = Collections.unmodifiableList(Arrays.asList(MonsterBlessing.values()));
        int count = blessings.size();
        MonsterBlessing chosenBlessing = blessings.get(AbstractQuest.rng.random(count -1));

        switch (chosenBlessing){
            case RITUAL:
                Wiz.applyToEnemy(m, new RitualPower(m, 1, false));
                break;
            case STRENGTH:
                Wiz.applyToEnemy(m, new StrengthPower(m, 3));
                break;
            case PAINSTAB:
                Wiz.applyToEnemy(m, new PainfulStabsPower(m));
                break;
            case SPIKES:
                Wiz.applyToEnemy(m, new ThornsPower(m, 2));
                break;
            case HEARTBEAT:
                Wiz.applyToEnemy(m, new BeatOfDeathPower(m, 1));
                break;
            case METALLICIZE:
                Wiz.applyToEnemy(m, new MetallicizePower(m, 3));
                break;
            case TIMEWARP:
                Wiz.applyToEnemy(m, new TimeWarpPower(m));
                break;
            case PLATED:
                Wiz.applyToEnemy(m, new PlatedArmorPower(m, 5));
                break;
            case BUFFER:
                Wiz.applyToEnemy(m, new BufferPower(m, 2));
                break;
            case HIDE:
                Wiz.applyToEnemy(m, new SharpHidePower(m, 5));
                break;
            case REGEN:
                Wiz.applyToEnemy(m, new RegenerateMonsterPower(m, 2));
                break;
            case INTANGIBLE:
                Wiz.applyToEnemy(m, new IntangiblePower(m, 1));
                break;
        }
    }

    public static AbstractMonster getRandomNonMinionEnemy(){
        Object[] noMinionsList = AbstractDungeon.getMonsters().monsters.stream().filter(
                mon -> !mon.hasPower(MinionPower.POWER_ID)
        ).toArray();

        if (noMinionsList.length == 0){
            return null;
        }

        return (AbstractMonster) noMinionsList[AbstractQuest.rng.random(noMinionsList.length - 1)];
    }
}
