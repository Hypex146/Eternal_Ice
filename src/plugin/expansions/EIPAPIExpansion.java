package plugin.expansions;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import plugin.EternalIce;
import plugin.utilities.EIConfigurator;

public class EIPAPIExpansion extends PlaceholderExpansion {
	private EternalIce main_plugin_;
	// configuration
	private Map<String, String> placeholders_;
	// constants
	
	public EIPAPIExpansion(EternalIce main_plugin) {
		main_plugin_ = main_plugin;
		placeholders_ = new HashMap<String, String>();
	}
	
	public void updateParams() {
		FileConfiguration cfg = main_plugin_.getConfig();
		EIConfigurator ei_cfg = main_plugin_.getEIConfigurator();
		ConfigurationSection placeholders_section = ei_cfg.getConfigurationSection(cfg, "Placeholders_settings");
		updatePlaceholders(placeholders_section);
	}
	
	private void updatePlaceholders(ConfigurationSection placeholders_section) {
		EIConfigurator ei_cfg = main_plugin_.getEIConfigurator();
		placeholders_.clear();
		String level = ei_cfg.getString(placeholders_section, "level", "level");
		String mana = ei_cfg.getString(placeholders_section, "mana", "mana");
		String cooldown_right = ei_cfg.getString(placeholders_section, "cooldown_right", "cooldown_right");
		String cooldown_left = ei_cfg.getString(placeholders_section, "cooldown_left", "cooldown_left");
		String score = ei_cfg.getString(placeholders_section, "score", "score");
		placeholders_.put("level", level);
		placeholders_.put("mana", mana);
		placeholders_.put("cooldown", cooldown_right);
		placeholders_.put("cooldown", cooldown_left);
		placeholders_.put("score", score);
	}

    @Override
    public String getAuthor() {
        return "Hypex";
    }
    
    @Override
    public String getIdentifier() {
        return "ei";
    }

    @Override
    public String getVersion() {
        return "0.0.1";
    }
    
    @Override
    public boolean persist() {
        return true;
    }
    
    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (params.equals(placeholders_.getOrDefault("level", null))) {
        	if (player instanceof Player) {
        		return String.valueOf(main_plugin_.getLevelManager().getLevel((Player)player));
        	}
        	return "null";
        } else if (params.equals(placeholders_.getOrDefault("mana", null))) {
        	if (player instanceof Player) {
        		return String.valueOf(main_plugin_.getManaManager().getMana((Player)player));
        	}
        	return "null";
        } else if (params.equals(placeholders_.getOrDefault("cooldown_right", null))) {
        	if (player instanceof Player) {
        		return String.valueOf(main_plugin_.getAbilityManager().getCooldown((Player)player, true));
        	}
        	return "null";
        } else if (params.equals(placeholders_.getOrDefault("cooldown_left", null))) {
        	if (player instanceof Player) {
        		return String.valueOf(main_plugin_.getAbilityManager().getCooldown((Player)player, false));
        	}
        	return "null";
        } else if (params.equals(placeholders_.getOrDefault("score", null))) {
        	if (player instanceof Player) {
        		return String.valueOf(main_plugin_.getScoreManager().getScore((Player)player));
        	}
        	return "null";
        }
        return null; // Placeholder is unknown by the Expansion
    }

}
