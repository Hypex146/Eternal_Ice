package plugin.managers;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitScheduler;

import plugin.EternalIce;
import plugin.utilities.EIConfigurator;

public class ManaManager {
	private EternalIce main_plugin_;
	private int mana_refill_task_id_;
	// configuration
	private int add_mana_frequency_;
	// const
	private final NamespacedKey mana_key_;
	
	public ManaManager(EternalIce main_plugin) {
		main_plugin_ = main_plugin;
		mana_key_ = new NamespacedKey(main_plugin_, "mana");
	}
	
	public void updateParams() {
		FileConfiguration cfg = main_plugin_.getConfig();
		EIConfigurator ei_cfg = main_plugin_.getEIConfigurator();
		ConfigurationSection mana_section = ei_cfg.getConfigurationSection(cfg, "Mana_settings");
		add_mana_frequency_ = ei_cfg.getInt(mana_section, "add_mana_frequency", 20);
		updateManaRefillTask();
	}
	
	private void updateManaRefillTask() {
		BukkitScheduler scheduler =  main_plugin_.getServer().getScheduler();
		if (scheduler.isCurrentlyRunning(mana_refill_task_id_) 
				|| scheduler.isQueued(mana_refill_task_id_)) {
			scheduler.cancelTask(mana_refill_task_id_);
		}
		regManaRefill();
	}
	
	public int getMana(Player player) {
		PersistentDataContainer container = player.getPersistentDataContainer();
		if (container == null) { return -1; }
		return container.getOrDefault(mana_key_, PersistentDataType.INTEGER, 0);
	}
	
	public boolean setMana(Player player, int mana) {
		PersistentDataContainer container = player.getPersistentDataContainer();
		if (container == null) { return false; }
		container.set(mana_key_, PersistentDataType.INTEGER, mana);
		return true;
	}
	
	public boolean addMana(Player player, int mana, boolean can_overfill) {
		int current_mana = getMana(player);
		int new_mana = mana + current_mana;
		if (!setMana(player, new_mana)) { return false; }
		if (can_overfill) { return true; }
		ArrayList<HashMap<String, Object>> level_table = main_plugin_.getLevelManager().getLevelTable();
		int level = main_plugin_.getLevelManager().getLevel(player);
		int max_mana = (int) level_table.get(level - 1).get("mana_reserve");
		if (max_mana < new_mana) {
			return setMana(player, max_mana);
		}
		return true;
	}
	
	public boolean addMana(Player player, int mana) {
		return setMana(player, mana + getMana(player));
	}
	
	public boolean addManaTag(Player player) {
		PersistentDataContainer container = player.getPersistentDataContainer();
		if (container == null) { return false; }
		if (container.has(mana_key_, PersistentDataType.INTEGER)) {
			return false;
		}
		container.set(mana_key_, PersistentDataType.INTEGER, 0);
		return true;
	}
	
	private float getManaBarFill(Player player) {
		int current_mana = getMana(player);
		ArrayList<HashMap<String, Object>> level_table = main_plugin_.getLevelManager().getLevelTable();
		int level = main_plugin_.getLevelManager().getLevel(player);
		int max_mana = (int) level_table.get(level - 1).get("mana_reserve");
		return ((float)current_mana / (float)max_mana);
	}
	
	private void refillMana() { // FIXME
		for (World world : main_plugin_.getWorlds()) {
			for (Player player : world.getPlayers()) {
				ArrayList<HashMap<String, Object>> level_table = main_plugin_.getLevelManager().getLevelTable();
				int level = main_plugin_.getLevelManager().getLevel(player);
				int add_mana_value = (int) level_table.get(level - 1).get("add_mana_value");
				addMana(player, add_mana_value, false);
				player.setLevel(getMana(player));
				player.setExp(getManaBarFill(player));
			}
		}
	}
	
	public void regManaRefill() {
		Runnable task = new Runnable() {
			@Override
			public void run() {
				refillMana();
			}
		};
		mana_refill_task_id_ = main_plugin_.getServer().getScheduler().scheduleSyncRepeatingTask(main_plugin_, 
				task, add_mana_frequency_, add_mana_frequency_);
	}

}
