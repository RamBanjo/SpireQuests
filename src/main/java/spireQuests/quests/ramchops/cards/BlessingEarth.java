package spireQuests.quests.ramchops.cards;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.BufferPower;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.vfx.combat.MiracleEffect;
import spireQuests.abstracts.AbstractSQCard;

import static spireQuests.Anniv8Mod.makeID;
import static spireQuests.util.Wiz.applyToSelf;
import static spireQuests.util.Wiz.atb;

public class BlessingEarth extends AbstractSQCard {
    public final static String ID = makeID("BlessingEarth");
    private Random rng;

    public BlessingEarth() {
        super(ID, "ramchops",0, CardType.SKILL, CardRarity.SPECIAL, CardTarget.SELF);
        this.exhaust = true;
        this.selfRetain = true;
        this.baseMagicNumber = this.magicNumber = 1;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        atb(new VFXAction(new MiracleEffect(Color.LIME, Color.GREEN, "HEAL_1")));
        AbstractDungeon.player.increaseMaxHp(magicNumber, false);
    }

    @Override
    public void upp() {
        upgradeMagicNumber(1);
    }
}

