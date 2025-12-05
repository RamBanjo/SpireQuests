package spireQuests.quests.ramchops.patch;

import basemod.abstracts.CustomSavable;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.ShopRoom;

public class ShopMoneyTracker{

    public static int moneySpentInShop = 0;

    @SpirePatch2(
            clz = AbstractPlayer.class,
            method = "loseGold",
            paramtypez = int.class)
    public static class SpendGoldPatch{
        @SpirePrefixPatch
        public static void LoseGoldPatch(AbstractPlayer __instance, int goldAmount){

            if (AbstractDungeon.getCurrRoom() instanceof ShopRoom) {
                moneySpentInShop += goldAmount;
            }

        }
    }
}
