package game.main;

import java.util.ArrayList;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.tiled.TiledMap;

/**
 * Classe que carrega os resources no jogo
 * @author André Micheletti
 *
 */
public class ResourceLoader {
	
	/**
	 * Pasta principal de resources
	 */
	private static final String MAIN_FOLDER = "res/";
	
	/**
	 * Pasta para os graficos
	 */
	private static final String GFX_FOLDER = "graphic/";
	
	/**
	 * Pasta para os audios
	 */
	private static final String SFX_FOLDER = "sound/";
	
	/**
	 * Pasta para os mapas
	 */
	private static final String MAPS_FOLDER = "maps/";
	
	/**
	 * Graficos ja carregados
	 */
	private static ArrayList<Image> loadedGfx = new ArrayList<Image>();
	private static ArrayList<String> loaded_1 = new ArrayList<String>();
	
	/**
	 * Audios ja carregados
	 */
	private static ArrayList<Sound> loadedSfx = new ArrayList<Sound>();
	private static ArrayList<String> loaded_2 = new ArrayList<String>();
	
	/**
	 * Carrega um grafico e o guarda
	 * @param filename o nome do arquivo (com extensão)
	 * @return o resource pronto para uso
	 * @throws SlickException
	 */
	public static Image getImage(String filename) throws SlickException {
		if (loaded_1.contains(filename)) {
			return loadedGfx.get(loaded_1.indexOf(filename));
		} else {
			Image img = new Image(MAIN_FOLDER + GFX_FOLDER + filename);
			loaded_1.add(filename);
			loadedGfx.add(img);
			return img;
		}
	}
	
	/**
	 * Carrega um audio e o guarda
	 * @param filename o nome do arquivo (com extensão)
	 * @return o resource pronto para uso
	 * @throws SlickException
	 */
	public static Sound getSound(String filename) throws SlickException {
		if (loaded_2.contains(filename)) {
			return loadedSfx.get(loaded_2.indexOf(filename));
		} else {
			Sound snd = new Sound(MAIN_FOLDER + SFX_FOLDER + filename);
			loaded_2.add(filename);
			loadedSfx.add(snd);
			return snd;
		}
	}
	
	/**
	 * Carrega um mapa para uso
	 * @param filename o nome do arquivo (com extensão)
	 * @return o mapa pronto para uso
	 * @throws SlickException
	 */
	public static TiledMap getMap(String filename) throws SlickException {
		return new TiledMap(MAIN_FOLDER + MAPS_FOLDER + filename);
	}

}
