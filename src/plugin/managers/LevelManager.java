package plugin.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import plugin.EternalIce;
import plugin.utilities.EIConfigurator;

public class LevelManager {
	private EternalIce main_plugin_;
	// configuration
	private int max_level_;
	private List<Map<String, Object>> level_params_; // TODO make Collection and Map CHECK?
	// constants
	private final NamespacedKey level_key_;
	
	public LevelManager(EternalIce main_plugin) {
		main_plugin_ = main_plugin;
		level_key_ = new NamespacedKey(main_plugin_, "level");
		level_params_ = new ArrayList<Map<String, Object>>();
	}
	
	public void updateParams() {
		FileConfiguration cfg = main_plugin_.getConfig();
		EIConfigurator ei_cfg = main_plugin_.getEIConfigurator();
		ConfigurationSection level_section = ei_cfg.getConfigurationSection(cfg, "Levels_settings");
		max_level_ = ei_cfg.getInt(level_section, "max_level", 1); //TODO another way to know max level
		updateLevelInfo(level_section);
	}
	
	private void updateLevelInfo(ConfigurationSection level_section) {
		EIConfigurator ei_cfg = main_plugin_.getEIConfigurator();
		level_params_.clear();
		for (int i = 0; i < max_level_; i++) {
			int level = i + 1;
			String field_name = "Level_" + level;
			ConfigurationSection current_level_section = 
					ei_cfg.getConfigurationSection(level_section, field_name);
			Integer cost = ei_cfg.getInt(current_level_section, "cost", 1);
			Integer mana_reserve = ei_cfg.getInt(current_level_section, "mana_reserve", 1);
			Integer add_mana_value = ei_cfg.getInt(current_level_section, "add_mana_value", 1);
			Map<String, Object> level_info = new HashMap<String, Object>();
			level_info.put("cost", cost);
			level_info.put("mana_reserve", mana_reserve);
			level_info.put("add_mana_value", add_mana_value);
			level_params_.add(level_info);
		}
	}
	
	public List<Map<String, Object>> getLevelTable() {
		return level_params_;
	}
	
	public boolean levelUp(Player player) {
		PersistentDataContainer container = player.getPersistentDataContainer();
		if (container == null) { return false; }
		int current_level = container.getOrDefault(level_key_, 
				PersistentDataType.INTEGER, 1);
		if (current_level >= max_level_) { return false; }
		int level_cost = (int) level_params_.get(current_level).get("cost");
		if (!main_plugin_.getScoreManager().takeScoreFromPlayer(player, 
				level_cost)) { return false; }
		container.set(level_key_, PersistentDataType.INTEGER, current_level + 1);
		return true;
	}
	
	public int getLevel(Player player) {
		PersistentDataContainer container = player.getPersistentDataContainer();
		if (container == null) { return -1; }
		return container.getOrDefault(level_key_, PersistentDataType.INTEGER, 1);
	}
	
	public boolean setLevel(Player player, int level) {
		if (level > max_level_ || level < 1) { return false; }
		PersistentDataContainer container = player.getPersistentDataContainer();
		if (container == null) { return false; }
		container.set(level_key_, PersistentDataType.INTEGER, level);
		return true;
	}
	
	public int getMaxLevel() {
		return max_level_;
	}
	
	public boolean addLevelTag(Player player) {
		PersistentDataContainer container = player.getPersistentDataContainer();
		if (container == null) { return false; }
		if (container.has(level_key_, PersistentDataType.INTEGER)) {
			return false;
		}
		container.set(level_key_, PersistentDataType.INTEGER, 1);
		return true;
	}
	
}
