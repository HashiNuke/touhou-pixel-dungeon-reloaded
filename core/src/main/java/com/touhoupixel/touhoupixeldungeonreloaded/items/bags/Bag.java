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

package com.touhoupixel.touhoupixeldungeonreloaded.items.bags;

import com.touhoupixel.touhoupixeldungeonreloaded.Badges;
import com.touhoupixel.touhoupixeldungeonreloaded.Dungeon;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.Char;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.buffs.LostInventory;
import com.touhoupixel.touhoupixeldungeonreloaded.actors.hero.Hero;
import com.touhoupixel.touhoupixeldungeonreloaded.items.Item;
import com.touhoupixel.touhoupixeldungeonreloaded.scenes.GameScene;
import com.touhoupixel.touhoupixeldungeonreloaded.windows.WndQuickBag;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

import java.util.ArrayList;
import java.util.Iterator;

public class Bag extends Item implements Iterable<Item> {

	public static final String AC_OPEN	= "OPEN";
	
	{
		image = 11;
		
		defaultAction = AC_OPEN;

		unique = true;
	}

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.remove(AC_DROP);
		actions.remove(AC_THROW);
		return actions;
	}
	
	public Char owner;
	
	public ArrayList<Item> items = new ArrayList<>();

	public int capacity(){
		return 20; // default container size
	}
	
	@Override
	public void execute( Hero hero, String action ) {

		super.execute( hero, action );

		if (action.equals( AC_OPEN ) && !items.isEmpty()) {
			
			GameScene.show( new WndQuickBag( this ) );
			
		}
	}
	
	@Override
	public boolean collect( Bag container ) {

		grabItems(container);

		if (super.collect( container )) {
			
			owner = container.owner;
			
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onDetach( ) {
		this.owner = null;
		for (Item item : items)
			Dungeon.quickslot.clearItem(item);
		updateQuickslot();
	}

	public void grabItems(){
		if (owner != null && owner instanceof Hero && this != ((Hero) owner).belongings.backpack) {
			grabItems(((Hero) owner).belongings.backpack);
		}
	}

	public void grabItems( Bag container ){
		for (Item item : container.items.toArray( new Item[0] )) {
			if (canHold( item )) {
				int slot = Dungeon.quickslot.getSlot(item);
				item.detachAll(container);
				if (!item.collect(this)) {
					item.collect(container);
				}
				if (slot != -1) {
					Dungeon.quickslot.setSlot(slot, item);
				}
			}
		}
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}
	
	@Override
	public boolean isIdentified() {
		return true;
	}
	
	public void clear() {
		items.clear();
	}
	
	public void resurrect() {
		for (Item item : items.toArray(new Item[0])){
			if (!item.unique) items.remove(item);
		}
	}
	
	private static final String ITEMS	= "inventory";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( ITEMS, items );
	}

	//temp variable so that bags can load contents even with lost inventory debuff
	private boolean loading;

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );

		loading = true;
		for (Bundlable item : bundle.getCollection( ITEMS )) {
			if (item != null) ((Item)item).collect( this );
		}
		loading = false;
	}
	
	public boolean contains( Item item ) {
		for (Item i : items) {
			if (i == item) {
				return true;
			} else if (i instanceof Bag && ((Bag)i).contains( item )) {
				return true;
			}
		}
		return false;
	}

	public boolean canHold( Item item ){
		if (!loading && owner != null && owner.buff(LostInventory.class) != null
			&& !item.keptThoughLostInvent){
			return false;
		}

		if (items.contains(item) || item instanceof Bag || items.size() < capacity()){
			return true;
		} else if (item.stackable) {
			for (Item i : items) {
				if (item.isSimilar( i )) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public Iterator<Item> iterator() {
		return new ItemIterator();
	}
	
	private class ItemIterator implements Iterator<Item> {

		private int index = 0;
		private Iterator<Item> nested = null;
		
		@Override
		public boolean hasNext() {
			if (nested != null) {
				return nested.hasNext() || index < items.size();
			} else {
				return index < items.size();
			}
		}

		@Override
		public Item next() {
			if (nested != null && nested.hasNext()) {
				
				return nested.next();
				
			} else {
				
				nested = null;
				
				Item item = items.get( index++ );
				if (item instanceof Bag) {
					nested = ((Bag)item).iterator();
				}
				
				return item;
			}
		}

		@Override
		public void remove() {
			if (nested != null) {
				nested.remove();
			} else {
				items.remove( index );
			}
		}
	}
}
