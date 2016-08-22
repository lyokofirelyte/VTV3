package com.github.lyokofirelyte.VTV3;

import lombok.Getter;

public enum VTEvent {

	ENTITY_CREATE_PORTAL(VTV.ENTITY_TYPE, VTV.PORTAL_TYPE),
	ENTITY_DAMAGE(),
	ENTITY_DEATH(),
	ENTITY_EXPLODE(),
	ENTITY_SPAWN(),
	ITEM_DESPAWN(),
	ITEM_SPAWN(),
	LIGHTNING_STRIKE(),
	NOTEBLOCK_PLAY(),
	POTION_SPLASH(),
	PROJECTILE_HIT(),
	VEHICLE_ENTER(),
	VEHICLE_EXIT(),
	PLAYER_BREAK_BLOCK(VTV.SENDER, VTV.BLOCK, VTV.EXP),
	PLAYER_CHAT(VTV.SENDER, VTV.MESSAGE),
	PLAYER_CLICK_AIR(),
	PLAYER_CLICK_BLOCK(),
	PLAYER_CLICK_INVENTORY(),
	PLAYER_COMMAND(),
	PLAYER_DEATH(),
	PLAYER_DROP_ITEM(),
	PLAYER_EMPTY_BUCKET(),
	PLAYER_ENCHANT(),
	PLAYER_ENTER_BED(),
	PLAYER_EXIT_BED(),
	PLAYER_FILL_BUCKET(),
	PLAYER_FLIGHT(),
	PLAYER_HOLD_ITEM(),
	PLAYER_JOIN(),
	PLAYER_KICK(),
	PLAYER_PLACE_BLOCK(),
	PLAYER_QUIT(),
	PLAYER_RESPAWN(),
	PLAYER_SNEAK(),
	PLAYER_SPRINT(),
	PLAYER_TRIGGER_PRESSURE_PLATE(),
	PLAYER_WORLD_CHANGE(),
	COMMAND_NOT_FOUND(),
	CONSOLE_COMMAND(),
	SERVER_LIST_PING(),
	SYSTEM_AUTO_SAVE(),
	SYSTEM_DISABLE(),
	SYSTEM_ENABLE(),
	SYSTEM_TIMER();
	
	VTEvent(VTV... vtvs){
		this.vtvs = vtvs;
	}
	
	@Getter
	private VTV[] vtvs;
	
	public Class<?>[] toClasses(){
		Class<?>[] clazz = new Class<?>[size()];
		for (int i = 0; i < size(); i++){
			clazz[i] = vtvs[i].getType();
		}
		return clazz;
	}
	
	public int size(){
		return vtvs.length;
	}
	
	public int totalSize(){
		return VTV.values().length;
	}
}