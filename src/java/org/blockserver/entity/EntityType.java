package org.blockserver.entity;

import org.blockserver.player.Player;

public enum EntityType {
	PLAYER(63, Player.class); // TODO more entities
	/*FALLING_BLOCK(66, FallingBlock.class); I'll implement this later, due to I have to fully
	implement first a block appearing when completely when the entity falls completely. */
	private int id;
	private Class<? extends Entity> c;
	private EntityType(int id, Class c){
		this.id = id;
		this.c = c;
	}

	public int getID() {
	    return id;
    }

	public Class<?> getEntityClass(){
	    return c;
	}
}