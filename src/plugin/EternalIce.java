package plugin;

import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import utilities.EIConfigurator;
import utilities.EILogger;
import utilities.LogLevel;

public class EternalIce extends JavaPlugin {
	private final String plugin_name_ = "Eternal_Ice";
	private boolean enable_;
	private FileConfiguration config_;
	private EIConfigurator configurator_;
	private EILogger logger_;
	
	public EternalIce() {
		enable_ = true;
		config_ = null;
		configurator_=  new EIConfigurator(this);
		logger_ = new EILogger(this, LogLevel.Standart);
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
	
	public void loadConfig() {
		logger_.log(LogLevel.Debug, Level.INFO, "Подгружаем конфиг");
		config_ = getConfig();
		enable_ = configurator_.getBoolean(config_, "enable", true);
		LogLevel log_level = LogLevel.toEnum(configurator_.getString(config_, 
				"log_level", LogLevel.Standart.toString()));
		if (log_level == null) {
			logger_.log(LogLevel.Debug, Level.WARNING, "Неверно выставлено поле \"log_level\" в конфиге");
			configurator_.setString(config_, "log_level", LogLevel.Standart.toString());
			logger_.log(LogLevel.Debug, Level.WARNING, "Поле \"log_level\" установлено по умолчанию");
			log_level = LogLevel.Standart;
		}
		logger_.setLogLevel(log_level);
		logger_.log(LogLevel.Debug, Level.INFO, "Сохраняем конфиг");
		saveConfig();
	}
	
	public boolean checkEnableStatus() {
		logger_.log(LogLevel.Debug, Level.INFO, "Проверяем не отключён ли плагин в конфиге");
		if (enable_ == true) { 
			logger_.log(LogLevel.Debug, Level.INFO, "Плагин не отключён в конфиге");
			return true; 
			}
		logger_.log(LogLevel.Debug, Level.INFO, "Плагин отключён в конфиге");
		getServer().getPluginManager().disablePlugin(this);
		logger_.log(LogLevel.Standart, Level.INFO, "Плагин был отключён");
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
		logger_.log(LogLevel.Standart, Level.INFO, greeting);
	}
	
	@Override
	public void onEnable() {
		loadConfig();
		if (!checkEnableStatus()) { return; }
		printGreetingInConsole();
	}
	
	

}
