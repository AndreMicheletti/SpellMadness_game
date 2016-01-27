package game.main;

/**
 * Validador de Sistema Operacional
 * @author André Micheletti
 *
 */
public class OSValidator {
	
	/**
	 * Pega a string que identifica o OS
	 */
	private static String OS = System.getProperty("os.name").toLowerCase();
	 
	/**
	 * Checa se o OS é Windows
	 * @return true se windows, false caso contrário
	 */
	public static boolean isWindows() { 
		return (OS.indexOf("win") >= 0); 
	}
 
	/**
	 * Checa se o OS é Mac
	 * @return true se mac, false caso contrário
	 */
	public static boolean isMac() { 
		return (OS.indexOf("mac") >= 0); 
	}
	
	/**
	 * Checa se o OS é Unix
	 * @return true se unix, false caso contrário
	 */
	public static boolean isUnix() { 
		return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 ); 
	}
	
	/**
	 * Checa se o OS é Solaris
	 * @return true se solaris, false caso contrário
	 */
	public static boolean isSolaris() { 
		return (OS.indexOf("sunos") >= 0); 
	}

}
