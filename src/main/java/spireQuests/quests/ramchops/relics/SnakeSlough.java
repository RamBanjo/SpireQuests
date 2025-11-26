package spireQuests.quests.ramchops.relics;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.animations.FastShakeAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.tempCards.Insight;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.LoseStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.combat.ShockWaveEffect;
import spireQuests.abstracts.AbstractSQRelic;

import static spireQuests.Anniv8Mod.makeID;
import static spireQuests.util.Wiz.adp;
import static spireQuests.util.Wiz.applyToSelf;


public class SnakeSlough extends AbstractSQRelic {

    public static String ID = makeID(SnakeSlough.class.getSimpleName());

    public SnakeSlough() {
        super(ID, "ramchops", RelicTier.SPECIAL, LandingSound.FLAT);
    }

    private int DEX_GAIN_AT_START = 1;

    @Override
    public void atBattleStart() {
        counter = 0;
        applyToSelf(new DexterityPower(adp(), DEX_GAIN_AT_START));
    }

    @Override
    public void atTurnStart() {
        counter += 1;

        if (counter % 3 == 0){
            this.flash();
            this.counter = 0;

            int dexCount = 0;
            if (adp().getPower(DexterityPower.POWER_ID) != null){
                dexCount = adp().getPower(DexterityPower.POWER_ID).amount;
            }

            applyToSelf(new StrengthPower(adp(), dexCount));
            applyToSelf(new LoseStrengthPower(adp(), dexCount));
        }
    }

    @Override
    public void onVictory() {
        counter = -1;
    }

    @Override
    public String getUpdatedDescription() {
        return String.format(DESCRIPTIONS[0], DEX_GAIN_AT_START);
    }
}
