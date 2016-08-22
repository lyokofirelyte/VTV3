package com.github.lyokofirelyte.VTV3;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import lombok.Getter;

public enum VTV {

	SENDER(0, Player.class),
	MESSAGE(1, String.class), 
	EVENT(2, Object.class),
	
	// Entity Create Portal
	ENTITY_TYPE(3, String.class),
	PORTAL_TYPE(4, String.class),
	
	BLOCK(5, Block.class),
	EXP(6, Integer.class);

	VTV(int id, Class<?> type){
		this.id = id;
		this.type = type;
	}
	
	@Getter
	private int id;
	
	@Getter
	private Class<?> type;
	
	public String s(){
		return toString();
	}
}