package plugin.utilities;

public enum LogLevel {
	MINIMAL,
	STANDART,
	DEBUG;
	
	public static LogLevel toEnum(String string) {
		switch (string) {
			case "MINIMAL":
				return LogLevel.MINIMAL;
			case "STANDART":
				return LogLevel.STANDART;
			case "DEBUG":
				return LogLevel.DEBUG;
		}
		return null;
	}
	
}
