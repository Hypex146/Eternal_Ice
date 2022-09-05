package plugin.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import plugin.EternalIce;

public class PlayerJoinEventListener implements Listener {
	private EternalIce main_plugin_;
	// configuration
	// constants
	
	public PlayerJoinEventListener(EternalIce main_plugin) {
		main_plugin_ = main_plugin;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		main_plugin_.getLevelManager().addLevelTag(event.getPlayer());
		main_plugin_.getManaManager().addManaTag(event.getPlayer());
	}
	

}
