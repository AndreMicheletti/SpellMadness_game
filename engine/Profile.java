package game.engine;

import game.engine.enums.MageClass;
import static game.main.DataManager.*;

/**
 * A classe que controla o Profile do jogador
 * @author André Micheletti
 *
 */
public class Profile {
	
	public static String profileFile = "player/profile.spm";
	
	public static String profileName = "Mad";
	public static MageClass mageClass = MageClass.BLUE;
	
	public static String getProfileToSave() {
		String result = "";
		result += "name:" + profileName + ";";	
		result += "class:" + mageClass.toString() + ";";		
		return result;
	}
	
	public static String getDefaultProfileToSave() {
		String result = "";
		result += "name:Mad;";
		result += "class:blue;";		
		return result;
	}
	
	public static void loadProfile() {
		if (existe(profileFile)) {
			System.out.println("READING PROFILE FILE");
			String[] contents = lerArq(profileFile).split(";");
			for (String line : contents) {
				if (line.startsWith("name:")) profileName = line.replaceAll("name:", "");
				if (line.startsWith("class:")) {
					String classname = line.replaceAll("class:", "");
					switch (classname) {
					case "blue":
						 mageClass = MageClass.BLUE;
					case "black":
						 mageClass = MageClass.BLACK;
					case "white":
						 mageClass = MageClass.WHITE;
					case "red":
						 mageClass = MageClass.RED;
					default:
						 mageClass = MageClass.BLUE;
					}
				}
			}
		} else {
			System.out.println("CREATING NEW PROFILE FILE");
			gravarArq(profileFile, getDefaultProfileToSave());
		}
	}
	
	public static boolean hasSaveFile() {
		return (listarArqs("saves/").size() > 0);
	}

}
