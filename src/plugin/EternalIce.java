package plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;

import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import plugin.commands.AbilityCommand;
import plugin.commands.LevelCommand;
import plugin.commands.PluginCommand;
import plugin.commands.ScoreCommand;
import plugin.commands.TestCommand;
import plugin.expansions.EIPAPIExpansion;
import plugin.listeners.DeathEventListener;
import plugin.listeners.PlayerClickEventListener;
import plugin.listeners.PlayerJoinEventListener;
import plugin.managers.AbilityManager;
import plugin.managers.LevelManager;
import plugin.managers.ManaManager;
import plugin.managers.ScoreManager;
import plugin.utilities.EIConfigurator;
import plugin.utilities.EILogger;
import plugin.utilities.LogLevel;

public class EternalIce extends JavaPlugin {
	private FileConfiguration config_;
	private EIConfigurator configurator_;
	private EILogger logger_;
	private ScoreManager score_manager_;
	private LevelManager level_manager_;
	private ManaManager mana_manager_;
	private AbilityManager ability_manager_;
	private EIPAPIExpansion papi_expansion_;
	// configuration
	private boolean enable_;
	private boolean enable_greeting_;
	private Collection<World> worlds_;
	// constants
	private final String plugin_name_ = "Eternal_Ice";
	
	public EternalIce() {
		configurator_=  new EIConfigurator(this);
		logger_ = new EILogger(this, LogLevel.STANDART); // TODO CHECK?
		score_manager_ = new ScoreManager(this);
		level_manager_ = new LevelManager(this);
		mana_manager_ = new ManaManager(this);
		ability_manager_ = new AbilityManager(this);
		papi_expansion_ = new EIPAPIExpansion(this);
		worlds_ = new ArrayList<World>();
	}
	
	public String getPluginName() {
		return plugin_name_;
	}
	
	public boolean isEnable() {
		return enable_;
	}
	
	public EIConfigurator getEIConfigurator() {
		return configurator_;
	}
	
	public EILogger getEILogger() {
		return logger_;
	}
	
	public ScoreManager getScoreManager() {
		return score_manager_;
	}
	
	public LevelManager getLevelManager() {
		return level_manager_;
	}
	
	public ManaManager getManaManager() {
		return mana_manager_;
	}
	
	public AbilityManager getAbilityManager() {
		return ability_manager_;
	}
	
	public Collection<World> getWorlds() {
		return worlds_;
	}
	
	public void loadConfig() {
		logger_.log(LogLevel.DEBUG, Level.INFO, "Подгружаем конфиг");
		saveDefaultConfig();
		reloadConfig();
		updateParams();
		score_manager_.updateParams();
		level_manager_.updateParams();
		mana_manager_.updateParams();
		ability_manager_.updateParams();
		papi_expansion_.updateParams();
		logger_.log(LogLevel.DEBUG, Level.INFO, "Сохраняем конфиг");
		saveConfig();
	}
	
	public void updateParams() {
		config_ = getConfig();
		ConfigurationSection main_section = configurator_.getConfigurationSection(config_, "Main_settings");
		LogLevel log_level = LogLevel.toEnum(configurator_.getString(main_section, 
				"log_level", LogLevel.DEBUG.toString()));
		if (log_level == null) {
			configurator_.setString(main_section, "log_level", LogLevel.STANDART.toString());
			log_level = LogLevel.STANDART;
		}
		logger_.setLogLevel(log_level);
		enable_ = configurator_.getBoolean(main_section, "enable", true);
		enable_greeting_ = configurator_.getBoolean(main_section, "enable_greeting", true);
		updateWorlds();
	}
	
	private void updateWorlds() {
		worlds_.clear();
		ConfigurationSection main_section = configurator_.getConfigurationSection(config_, "Main_settings");
		Collection<String> world_names = configurator_.getStringList(main_section, "worlds");
		for (String world_name : world_names) {
			World world = getServer().getWorld(world_name);
			if (world == null) {
				logger_.log(LogLevel.STANDART, Level.WARNING, "Мир " + world_name + " не найден!");
				continue;
			}
			worlds_.add(world);
		}
	}
	
	public boolean checkEnableStatus() {
		logger_.log(LogLevel.DEBUG, Level.INFO, "Проверяем не отключён ли плагин в конфиге");
		if (enable_ == true) { 
			logger_.log(LogLevel.DEBUG, Level.INFO, "Плагин не отключён в конфиге");
			return true; 
			}
		logger_.log(LogLevel.DEBUG, Level.INFO, "Плагин отключён в конфиге");
		getServer().getPluginManager().disablePlugin(this);
		logger_.log(LogLevel.STANDART, Level.INFO, "Плагин был отключён");
		return false;
	}
	
	private void printGreetingInConsole() {
		String greeting = "\n===========================\n"
				+ "|   |     ___   ___        \n"
				+ "|   |\\   /|  |  |     \\  / \n"
				+ "|===| \\ / |__|  |__    \\/  \n"
				+ "|   |  |  |     |      /\\  \n"
				+ "|   |  |  |     |__   /  \\ \n"
				+ "===========================";
		logger_.log(LogLevel.STANDART, Level.INFO, greeting);
	}
	
	private boolean checkDepends() {
	    if(getServer().getPluginManager().getPlugin("PlaceholderAPI") == null) {
	    	logger_.log(LogLevel.STANDART, Level.SEVERE, "Плагин отключён из-за отсутствия PAPI!");
	        getServer().getPluginManager().disablePlugin(this);
	        return false;
	    }
	    return true;
	}
	
	@Override
	public void onEnable() {
		loadConfig();
		if (!checkEnableStatus()) { return; }
		if (!checkDepends()) { return; }
		if (enable_greeting_) { printGreetingInConsole(); }
		papi_expansion_.register();
		getCommand("eternal_ice").setExecutor(new PluginCommand(this));
		getCommand("eternal_ice_level").setExecutor(new LevelCommand(this));
		getCommand("eternal_ice_score").setExecutor(new ScoreCommand(this));
		getCommand("eternal_ice_ability").setExecutor(new AbilityCommand(this));
		getCommand("test").setExecutor(new TestCommand(this));
		getServer().getPluginManager().registerEvents(new DeathEventListener(this), this);
		getServer().getPluginManager().registerEvents(new PlayerClickEventListener(this), this);
		getServer().getPluginManager().registerEvents(new PlayerJoinEventListener(this), this);
	}

}
