package plugin.listeners;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
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
		Player prey = event.getEntity();
		Player killer = prey.getKiller();
		if (killer == null) {
			main_plugin_.getScoreManager().takePenaltyFromPlayer(prey);
		} else {
			main_plugin_.getScoreManager().takeRewardForPlayer(killer, prey);
		}
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		LivingEntity mob = event.getEntity();
		if (mob instanceof Player) { return; }
		Player player = mob.getKiller();
		main_plugin_.getScoreManager().takeRewardForMob(player, mob);
	}
	
}
