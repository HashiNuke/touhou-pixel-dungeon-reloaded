package com.touhoupixel.touhoupixeldungeonreloaded.actors.mobs;

import com.touhoupixel.touhoupixeldungeonreloaded.Dungeon;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.Char;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.buffs.Buff;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.buffs.Haste;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.buffs.OneDefDamage;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.buffs.Triplespeed;
import com.touhoupixel.touhoupixeldungeonreloaded.items.potions.PotionOfHealing;
import com.touhoupixel.touhoupixeldungeonreloaded.sprites.FlandreSprite;
import com.touhoupixel.touhoupixeldungeonreloaded.sprites.HijiriSprite;
import com.touhoupixel.touhoupixeldungeonreloaded.sprites.SakiSprite;
import com.watabou.utils.Random;

public class Flandre extends Mob {

    {
        spriteClass = FlandreSprite.class;

        HP = HT = 175;
        defenseSkill = 42;
        EXP = 9;
        maxLvl = 50;

        loot = new PotionOfHealing();
        lootChance = 0.1f;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(20, 24);
    }

    @Override
    public int attackSkill(Char target) {
        return 47;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 2);
    }

    @Override
    public int attackProc(Char hero, int damage) {
        damage = super.attackProc(enemy, damage);
        if (enemy == Dungeon.hero && enemy.alignment != this.alignment) {
            if (Dungeon.hero.lvl != 99 && hero.HT > 3 && hero.HP > 3 && Random.Int(4) == 0) {
                hero.HT /= 2;
                hero.HP /= 2;
            }
        }
        return damage;
    }
}