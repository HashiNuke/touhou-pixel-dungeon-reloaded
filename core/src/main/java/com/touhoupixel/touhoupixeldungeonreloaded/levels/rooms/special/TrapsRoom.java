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

package com.touhoupixel.touhoupixeldungeonreloaded.levels.rooms.special;

import com.touhoupixel.touhoupixeldungeonreloaded.Challenges;
import com.touhoupixel.touhoupixeldungeonreloaded.Dungeon;
import com.touhoupixel.touhoupixeldungeonreloaded.items.Generator;
import com.touhoupixel.touhoupixeldungeonreloaded.items.Heap;
import com.touhoupixel.touhoupixeldungeonreloaded.items.Item;
import com.touhoupixel.touhoupixeldungeonreloaded.items.potions.PotionOfLevitation;
import com.touhoupixel.touhoupixeldungeonreloaded.levels.Level;
import com.touhoupixel.touhoupixeldungeonreloaded.levels.Terrain;
import com.touhoupixel.touhoupixeldungeonreloaded.levels.painters.Painter;
import com.touhoupixel.touhoupixeldungeonreloaded.levels.traps.AntiHealTrap;
import com.touhoupixel.touhoupixeldungeonreloaded.levels.traps.DespairTrap;
import com.touhoupixel.touhoupixeldungeonreloaded.levels.traps.DisintegrationTrap;
import com.touhoupixel.touhoupixeldungeonreloaded.levels.traps.ExConfusionTrap;
import com.touhoupixel.touhoupixeldungeonreloaded.levels.traps.ExplosiveTrap;
import com.touhoupixel.touhoupixeldungeonreloaded.levels.traps.FlashingTrap;
import com.touhoupixel.touhoupixeldungeonreloaded.levels.traps.FlockTrap;
import com.touhoupixel.touhoupixeldungeonreloaded.levels.traps.GrimTrap;
import com.touhoupixel.touhoupixeldungeonreloaded.levels.traps.GrippingTrap;
import com.touhoupixel.touhoupixeldungeonreloaded.levels.traps.PoisonDartTrap;
import com.touhoupixel.touhoupixeldungeonreloaded.levels.traps.TeleportationTrap;
import com.touhoupixel.touhoupixeldungeonreloaded.levels.traps.Trap;
import com.touhoupixel.touhoupixeldungeonreloaded.levels.traps.WarpingTrap;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

public class TrapsRoom extends SpecialRoom {

	public void paint( Level level ) {
		 
		Painter.fill( level, this, Terrain.WALL );

		Class<? extends Trap> trapClass;
		switch (Random.Int(4)){
			case 0:
				trapClass = null;
				break;
			default:
				trapClass = Random.oneOf(levelTraps[Dungeon.depth/5]);
				break;
		}

		if (trapClass == null){
			Painter.fill(level, this, 1, Terrain.CHASM);
		} else {
			Painter.fill(level, this, 1, Terrain.TRAP);
		}
		
		Door door = entrance();
		door.set( Door.Type.REGULAR );
		
		int lastRow = level.map[left + 1 + (top + 1) * level.width()] == Terrain.CHASM ? Terrain.CHASM : Terrain.EMPTY;

		int x = -1;
		int y = -1;
		if (door.x == left) {
			x = right - 1;
			y = top + height() / 2;
			Painter.fill( level, x, top + 1, 1, height() - 2 , lastRow );
		} else if (door.x == right) {
			x = left + 1;
			y = top + height() / 2;
			Painter.fill( level, x, top + 1, 1, height() - 2 , lastRow );
		} else if (door.y == top) {
			x = left + width() / 2;
			y = bottom - 1;
			Painter.fill( level, left + 1, y, width() - 2, 1 , lastRow );
		} else if (door.y == bottom) {
			x = left + width() / 2;
			y = top + 1;
			Painter.fill( level, left + 1, y, width() - 2, 1 , lastRow );
		}

		for(Point p : getPoints()) {
			int cell = level.pointToCell(p);
			if (level.map[cell] == Terrain.TRAP){
				level.setTrap(Reflection.newInstance(trapClass).reveal(), cell);
			}
		}
		
		int pos = x + y * level.width();
		if (Random.Int( 3 ) == 0) {
			if (lastRow == Terrain.CHASM) {
				Painter.set( level, pos, Terrain.EMPTY );
			}
			level.drop( prize( level ), pos ).type = Heap.Type.CHEST;
		} else {
			Painter.set( level, pos, Terrain.PEDESTAL );
			level.drop( prize( level ), pos ).type = Heap.Type.CHEST;
		}
		
		level.addItemToSpawn( new PotionOfLevitation() );
	}
	
	private static Item prize( Level level ) {

		Item prize;

		//67% chance for prize item
		if (Random.Int(3) != 0){
			prize = level.findPrizeItem();
			if (prize != null)
				return prize;
		}
		
		//1 floor set higher in probability, never cursed
		do {
			if (Random.Int(2) == 0) {
				prize = Generator.randomWeapon((Dungeon.depth / 5) + 1);
			} else {
				prize = Generator.randomArmor((Dungeon.depth / 5) + 1);
			}
		} while (prize.cursed || Challenges.isItemBlocked(prize));
		prize.cursedKnown = true;

		//33% chance for an extra update.
		if (Random.Int(3) == 0){
			prize.upgrade();
		}
		
		return prize;
	}

	@SuppressWarnings("unchecked")
	private static Class<?extends Trap>[][] levelTraps = new Class[][]{
			{AntiHealTrap.class, DespairTrap.class, ExConfusionTrap.class, DisintegrationTrap.class, PoisonDartTrap.class, GrimTrap.class},
			{AntiHealTrap.class, DespairTrap.class, ExConfusionTrap.class, DisintegrationTrap.class, PoisonDartTrap.class, GrimTrap.class},
			{AntiHealTrap.class, DespairTrap.class, ExConfusionTrap.class, DisintegrationTrap.class, PoisonDartTrap.class, GrimTrap.class},
			{AntiHealTrap.class, DespairTrap.class, ExConfusionTrap.class, DisintegrationTrap.class, PoisonDartTrap.class, GrimTrap.class},
			{AntiHealTrap.class, DespairTrap.class, ExConfusionTrap.class, DisintegrationTrap.class, PoisonDartTrap.class, GrimTrap.class},
			{AntiHealTrap.class, DespairTrap.class, ExConfusionTrap.class, DisintegrationTrap.class, PoisonDartTrap.class, GrimTrap.class},
			{AntiHealTrap.class, DespairTrap.class, ExConfusionTrap.class, DisintegrationTrap.class, PoisonDartTrap.class, GrimTrap.class},
			{AntiHealTrap.class, DespairTrap.class, ExConfusionTrap.class, DisintegrationTrap.class, PoisonDartTrap.class, GrimTrap.class},
			{AntiHealTrap.class, DespairTrap.class, ExConfusionTrap.class, DisintegrationTrap.class, PoisonDartTrap.class, GrimTrap.class},
			{AntiHealTrap.class, DespairTrap.class, ExConfusionTrap.class, DisintegrationTrap.class, PoisonDartTrap.class, GrimTrap.class},
			{AntiHealTrap.class, DespairTrap.class, ExConfusionTrap.class, DisintegrationTrap.class, PoisonDartTrap.class, GrimTrap.class},
			{AntiHealTrap.class, DespairTrap.class, ExConfusionTrap.class, DisintegrationTrap.class, PoisonDartTrap.class, GrimTrap.class},
			{AntiHealTrap.class, DespairTrap.class, ExConfusionTrap.class, DisintegrationTrap.class, PoisonDartTrap.class, GrimTrap.class},
			{AntiHealTrap.class, DespairTrap.class, ExConfusionTrap.class, DisintegrationTrap.class, PoisonDartTrap.class, GrimTrap.class},
			{AntiHealTrap.class, DespairTrap.class, ExConfusionTrap.class, DisintegrationTrap.class, PoisonDartTrap.class, GrimTrap.class},
			{AntiHealTrap.class, DespairTrap.class, ExConfusionTrap.class, DisintegrationTrap.class, PoisonDartTrap.class, GrimTrap.class},
			{AntiHealTrap.class, DespairTrap.class, ExConfusionTrap.class, DisintegrationTrap.class, PoisonDartTrap.class, GrimTrap.class},
			{AntiHealTrap.class, DespairTrap.class, ExConfusionTrap.class, DisintegrationTrap.class, PoisonDartTrap.class, GrimTrap.class},
			{AntiHealTrap.class, DespairTrap.class, ExConfusionTrap.class, DisintegrationTrap.class, PoisonDartTrap.class, GrimTrap.class},
			{AntiHealTrap.class, DespairTrap.class, ExConfusionTrap.class, DisintegrationTrap.class, PoisonDartTrap.class, GrimTrap.class},
	};
}
