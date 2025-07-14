package dev.akyawen.util;

import org.tempura.console.util.Ansi;

public class CustomLogger {
    private void log(int color, String level, String message) {
    	switch (color) {
		case 1:
			System.out.println(Ansi.GREEN + "[" + level + "] " + message + Ansi.SANE);
			break;
		case 2:
			System.out.println(Ansi.YELLOW + "[" + level + "] " + message + Ansi.SANE);
			break;
		case 3:
			System.out.println(Ansi.RED + "[" + level + "] " + message + Ansi.SANE);
			break;
		}
    }

    public void info(String message) {
        log(1, "INFO", message);
    }

    public void warn(String message) {
        log(2, "WARN", message);
    }

    public void error(String message) {
        log(3, "ERROR", message);
    }
}
