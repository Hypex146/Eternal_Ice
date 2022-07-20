package utilities;

public enum LogLevel {
	Minimal,
	Standart,
	Debug;
	
	public static LogLevel toEnum(String string) {
		switch (string) {
			case "Minimal":
				return LogLevel.Minimal;
			case "Standart":
				return LogLevel.Standart;
			case "Debug":
				return LogLevel.Debug;
		}
		return null;
	}
	
}
