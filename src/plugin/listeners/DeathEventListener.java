package plugin.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import plugin.EternalIce;

public class DeathEventListener implements Listener {
	private EternalIce main_plugin_;
	// configuration
	// constants
	
	public DeathEventListener(EternalIce main_plugin) {
		main_plugin_ = main_plugin;
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		main_plugin_.getScoreManager().processPlayerDeathEvent(event);
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		main_plugin_.getScoreManager().processEntityDeathEvent(event);
	}
	
}
