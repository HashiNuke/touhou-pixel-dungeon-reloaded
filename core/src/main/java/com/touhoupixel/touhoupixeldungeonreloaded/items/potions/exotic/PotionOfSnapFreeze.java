/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2022 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.touhoupixel.touhoupixeldungeonreloaded.items.potions.exotic;

import com.touhoupixel.touhoupixeldungeonreloaded.Assets;
import com.touhoupixel.touhoupixeldungeonreloaded.Challenges;
import com.touhoupixel.touhoupixeldungeonreloaded.Dungeon;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.Actor;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.Char;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.blobs.Freezing;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.buffs.Buff;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.buffs.Roots;
import com.touhoupixel.touhoupixeldungeonreloaded.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

public class PotionOfSnapFreeze extends ExoticPotion {
	
	{
		icon = ItemSpriteSheet.Icons.POTION_SNAPFREEZ;
	}
	
	@Override
	public void shatter(int cell) {
		
		if (Dungeon.level.heroFOV[cell]) {
			if (!Dungeon.isChallenged(Challenges.UNIDENTIFIED_OBJECT)) {
				identify();
			}
			
			splash( cell );
			Sample.INSTANCE.play( Assets.Sounds.SHATTER );
		}
		
		for (int offset : PathFinder.NEIGHBOURS9){
			if (!Dungeon.level.solid[cell+offset]) {
				
				Freezing.affect( cell + offset );
				
				Char ch = Actor.findChar( cell + offset);
				if (ch != null){
					Buff.affect(ch, Roots.class, Roots.DURATION*2f);
				}
				
			}
		}
	}
}
