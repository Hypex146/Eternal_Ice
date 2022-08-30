package plugin.utilities;

import java.util.logging.Level;

import plugin.EternalIce;

public class EILogger {
	private EternalIce main_plugin_;
	private LogLevel log_level_;
	
	public EILogger(EternalIce main_plugin, LogLevel log_level) {
		main_plugin_ = main_plugin;
		log_level_ = log_level;
	}
	
	public EILogger(EternalIce main_plugin) {
		main_plugin_ = main_plugin;
		log_level_ = LogLevel.STANDART;
	}
	
	public void setLogLevel(LogLevel log_level) {
		log_level_ = log_level;
	}
	
	public LogLevel getLogLevel() {
		return log_level_;
	}
	
	public boolean log(LogLevel log_level, Level level, String message) {
		if (log_level_.compareTo(log_level) < 0) { return false; }
		main_plugin_.getLogger().log(level, message);
		return true;
	}

}
