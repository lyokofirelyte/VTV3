package com.github.lyokofirelyte.VTV3;

import java.lang.reflect.Method;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import lombok.SneakyThrows;

public class VTEvents implements Listener {
	
	private VTV3 main;
	
	public VTEvents(VTV3 i){
		main = i;
	}
	
	public boolean execOn(String methodName, Class<?>[] clazzez, Object... args){
		for (Method m : main.getCompiledClass().getClass().getMethods()){
			if (m.getName().equals(methodName)){
				return main.exec(m.getName(), clazzez, args);
			}
		}
		return false;
	}
	
	public Class<?>[] mkclass(Class<?>... clazzez){
		return clazzez;
	}

	@EventHandler @SneakyThrows
	public void onCommand(PlayerCommandPreprocessEvent e){
		if (e.getMessage().equals("/vtr")){
			e.getPlayer().sendMessage(main.getParser().vtStyle("Reloading the triggers..."));
			main.comp();
			e.getPlayer().sendMessage(main.getParser().vtStyle("Completed successfully!"));
			e.setCancelled(true);
		} else {
			e.setCancelled(execOn("command_" + e.getMessage().split(" ")[0].replace("/", "") + "vtc", 
				mkclass(Player.class, String.class), 
				e.getPlayer(), e.getMessage()
			));
		}
	}
	
	@EventHandler @SneakyThrows
	public void onBlockBreak(BlockBreakEvent e){
		boolean cancel = execOn("event_player_break_blockvte", 
			VTEvent.PLAYER_BREAK_BLOCK.toClasses(),
			e.getPlayer(), e.getBlock(), new Integer(e.getExpToDrop())
		);
		e.setCancelled(cancel ? cancel : e.isCancelled());
	}
}