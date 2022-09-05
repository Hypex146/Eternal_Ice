package plugin.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import plugin.EternalIce;

public class PlayerClickEventListener implements Listener {
	private EternalIce main_plugin_;
	// configuration
	// constants
	
	public PlayerClickEventListener(EternalIce main_plugin) {
		main_plugin_ = main_plugin;
	}
	
	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		main_plugin_.getAbilityManager().processClickEvent(event);
	}

}
