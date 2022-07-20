package utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import plugin.EternalIce;


public class EIConfigurator {
	private EternalIce main_plugin_;
	
	public EIConfigurator(EternalIce main_plugin) {
		main_plugin_ = main_plugin;
	}
	
	public FileConfiguration getCustomConfig(String pathToFile) {
		File configFile = new File(main_plugin_.getDataFolder()+"/"+pathToFile);
		File folderFile = configFile.getParentFile();
		if (!folderFile.exists()) {
			folderFile.mkdirs();
			main_plugin_.getEILogger().log(LogLevel.Debug, Level.INFO, "Создана папка: " + folderFile.getAbsolutePath());
		}
		if (!configFile.exists()) {
			try {
				configFile.createNewFile();
				main_plugin_.getEILogger().log(LogLevel.Debug, Level.INFO, "Создан файл: " + configFile.getAbsolutePath());
			} catch (IOException e) {
				main_plugin_.getEILogger().log(LogLevel.Minimal, Level.SEVERE, "Не удалось создать файл (IOException): "
						+configFile.getAbsolutePath());
				e.printStackTrace();
				return null;
			}
		}
		YamlConfiguration customConfig = new YamlConfiguration();
		try {
			customConfig.load(configFile);
			main_plugin_.getEILogger().log(LogLevel.Debug, Level.INFO, "Считан файл: "+configFile.getAbsolutePath());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			main_plugin_.getEILogger().log(LogLevel.Minimal, Level.SEVERE, "Не удалось считать файл (FileNotFoundException): "
					+configFile.getAbsolutePath());
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			main_plugin_.getEILogger().log(LogLevel.Minimal, Level.SEVERE, "Не удалось считать файл (IOException): "
					+configFile.getAbsolutePath());
			e.printStackTrace();
			return null;
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
			main_plugin_.getEILogger().log(LogLevel.Minimal, Level.SEVERE, "Не удалось считать файл (InvalidConfigurationException): "
					+configFile.getAbsolutePath());
			e.printStackTrace();
			return null;
		}
		return customConfig;
	}
	
	public int saveCustomConfig(String pathToFile, FileConfiguration configToSave) {
		File configFile = new File(main_plugin_.getDataFolder()+"/"+pathToFile);
		try {
			configToSave.save(configFile);
			main_plugin_.getEILogger().log(LogLevel.Debug, Level.INFO, "Сохранён файл: "+configFile.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
			main_plugin_.getEILogger().log(LogLevel.Minimal, Level.SEVERE, 
					"Не удалось сохранить файл: (IOException) "+configFile.getAbsolutePath());
			e.printStackTrace();
			return -1;
		}
		return 0;
	}
	
	public boolean getBoolean(FileConfiguration config, String field, boolean defaultValue) {
		if (!config.isBoolean(field)) {
			main_plugin_.getEILogger().log(LogLevel.Debug, Level.WARNING, 
					"Неверно выставлено поле \"" + field + "\" в конфиге");
			config.set(field, defaultValue);
			main_plugin_.getEILogger().log(LogLevel.Debug, Level.WARNING, 
					"Поле \"" + field + "\" установлено по умолчанию");
		}
		return config.getBoolean(field);
	}
	
	public void setBoolean(FileConfiguration config, String field, boolean value) {
		config.set(field, value);
	}
	
	public int getInt(FileConfiguration config, String field, int defaultValue) {
		if (!config.isInt(field)) {
			main_plugin_.getEILogger().log(LogLevel.Debug, Level.WARNING, 
					"Неверно выставлено поле \"" + field + "\" в конфиге");
			config.set(field, defaultValue);
			main_plugin_.getEILogger().log(LogLevel.Debug, Level.WARNING, 
					"Поле \"" + field + "\" установлено по умолчанию");
		}
		return config.getInt(field);
	}
	
	public void setInt(FileConfiguration config, String field, int value) {
		config.set(field, value);
	}
	
	public String getString(FileConfiguration config, String field, String defaultValue) {
		if (!config.isString(field)) {
			main_plugin_.getEILogger().log(LogLevel.Debug, Level.WARNING, 
					"Неверно выставлено поле \"" + field + "\" в конфиге");
			config.set(field, defaultValue);
			main_plugin_.getEILogger().log(LogLevel.Debug, Level.WARNING, 
					"Поле \"" + field + "\" установлено по умолчанию");
		}
		return config.getString(field);
	}
	
	public void setString(FileConfiguration config, String field, String value) {
		config.set(field, value);
	}
	
	public double getDouble(FileConfiguration config, String field, double defaultValue) {
		if (!config.isDouble(field)) {
			main_plugin_.getEILogger().log(LogLevel.Debug, Level.WARNING, 
					"Неверно выставлено поле \"" + field + "\" в конфиге");
			config.set(field, defaultValue);
			main_plugin_.getEILogger().log(LogLevel.Debug, Level.WARNING, 
					"Поле \"" + field + "\" установлено по умолчанию");
		}
		return config.getDouble(field);
	}
	
	public void setDouble(FileConfiguration config, String field, double value) {
		config.set(field, value);
	}
	
	public List<String> getStringList(FileConfiguration config, String field){
		List<String> stringList;
		stringList = config.getStringList(field);
		if (stringList.size()>0) {
			return stringList;
		}
		main_plugin_.getEILogger().log(LogLevel.Debug, Level.WARNING, 
				"Неверно выставлено поле \"" + field + "\" в конфиге");
		config.set(field, stringList);
		main_plugin_.getEILogger().log(LogLevel.Debug, Level.WARNING, 
				"Поле \"" + field + "\" установлено по умолчанию");
		return stringList;
	}
	
}
