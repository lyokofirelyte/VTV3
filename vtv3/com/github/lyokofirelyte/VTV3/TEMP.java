package com.github.lyokofirelyte.VTV3;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

// IGNORE THIS IS JUST FOR ME TO CODE QUICKER!
public class TEMP {

	public String parseItem(String message, Object... objs){
		
		for (Object o : objs){
			if (o instanceof Player){
				Player player = (Player) o;
				r(message,
					"<playername>", player.getName(),
					"<playeruuid>", player.getUniqueId().toString(),
					"<playerdisplayname>", player.getDisplayName()
				);
			} else if (o instanceof Block){
				Block b = (Block) o;
				r(message,
					"<blocktype>", b.getType().name()
				);
			}
		}
		
		return message;
	}
	
	private String r(String orig, String... a){
		for (int i = 0; i < a.length; i += 2){
			orig = orig.replace(a[i], a[i+1]);
		}
		return orig;
	}
}