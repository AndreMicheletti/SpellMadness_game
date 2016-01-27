package game.engine;

import game.engine.items.ItemSpell;
import game.engine.scene.SceneBase;
import game.engine.scene.SceneTitle;
import game.main.DataManager;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

import static game.main.OSValidator.*;

/**
 * A classe principal do Jogo
 * STARTED = 28-06-14
 * DEV PHASE = 28-06-14 ~ ?
 * @author André Micheletti
 * 
 * DEBUG INFO :
 *  - max spells on map same time : 13
 *  - max total particles on map  : 320
 * 
 * Créditos para:
 *  - André Micheletti
 *  - Brian Matzon ~ Sound Manager <brian@matzon.dk>
 *  - Slick2D
 *  - LWJGL
 */
public final class Engine extends BasicGame {
	
	// Constantes
	/** Título do Jogo */
	public static String GAME_TITLE = "Spell Madness - Dev";
	
	/** Largura da Tela */	
	public static int GAME_WIDTH  = 800;
	/** Altura da Tela */	
	public static int GAME_HEIGHT = 640;

	/** Aceleração da gravidade */	
	public static float GRAVITY_ACC = 0.1982f; // NICE = 0.1882f
	/** Resistencia do Ar */
	//public static float AIR_RESISTANCE = 0.150f;
	
	/** Aceleração */
	public static float ACCELERATION = 0.175f;
	
	/** Fricção */
	public static float FRICTION = 0.195f;
	
	/** Recover após Rolamento */
	public static int ROLL_RECOVER = 60;
	/** Recover após Pulo */
	public static int JUMP_RECOVER = 20;
	
	public static String FALL_SOUND = "fall.wav";
	public static String TIMEOVER_SOUND = "fall.wav";
	public static String SPELLDIE_SOUND = "die.wav";
	
	public static int DEFAULT_SPAWNTIME = 120;
	public static int DEFAULT_CHAR_HP = 100;
	
	public static int HUD_HPX = 20;
	public static int HUD_HPY = 600;
	
	public static String TITLE_BITMAP = "title/SpellMadnessTitle.png";
	public static String MOUSE_BITMAP = "mouse_icon.png";
	
	public static boolean use_encryption = true;
	
	/** Container do Jogo */
	public static AppGameContainer GAME_CONTAINER = null;
	/** Classe Principal */
	public static Engine GAME = null;
	
	/** Volume Principal */
	public static float master_volume = 1f;
	
	/** Intervalo máximo de atualização logica */
	public static int MAX_LOGIC_UPD_INTERVAL = 60;
	/** Frame Rate a ser usado */
	public static int TARGET_FRAME_RATE = 60;
	
	/** Utilização de Sincronização Vertical */
	public static boolean VERTICAL_SYNC = true;
	/** Atualizar container mesmo quando não for o foco */
	public static boolean ALWAYS_RENDER = true;
	/** Desenhar o FPS na tela */
	public static boolean SHOW_FPS = false;
	
	// Váriaveis de classe
	private static SceneBase scene = null;

	public Engine() {
		super(GAME_TITLE);
	}

	/**
	 * Método que inicia o jogo, tudo deve ser pré-carregado aqui
	 * @throws SlickException
	 */
	public void init(GameContainer container) throws SlickException {
		DataManager.initialize();
		callScene(getFirstScene());
	}

	/**
	 * Atualiza o Jogo - Atualização Logica
	 * @throws SlickException
	 */
	public void update(GameContainer container, int delta) throws SlickException {
		updateMainInputs();
		
		if (scene != null) {
			
			if (scene.terminated == true) {
				if (scene.nextScene() != null)
					callScene(scene.nextScene());
				else
					GAME_CONTAINER.exit();
			} else {
				if (scene.initialized)
					scene.update(delta);
			}
		}
	}
	
	/**
	 * Renderiza o jogo - Atualização Gráfica
	 * @throws SlickException
	 */
	public void render(GameContainer container, Graphics g) throws SlickException {
		if (scene != null) {
			
			if (scene.initialized)
				scene.render(g);
			
		}
	}
	
	/**
	 * Atualiza os inputs principais do jogo
	 * @throws SlickException 
	 */
	private void updateMainInputs() throws SlickException {
		if (getInput().isKeyPressed(Input.KEY_F1)) {
			SHOW_FPS = !SHOW_FPS;
			Engine.GAME_CONTAINER.setShowFPS(SHOW_FPS);
		}
	}
	
	public static void callScene(SceneBase newS) {
		scene = newS;
	}
	
	/**
	 * Método de input principal
	 * @return retorna o input a ser usado pelo jogo
	 */
	public static Input getInput() {
		return GAME_CONTAINER.getInput();
	}
	
	/**
	 * Retorna a instancia do jogo
	 * @return a instancia do jogo
	 */
	public static Engine getGame() {
		return GAME;
	}

	/**
	 * Método principal de processo
	 * @param args argumentos do processo
	 */
	public static void main(String[] args) {
		
		String osFolder = "";
		
		if (isWindows()) {
			osFolder = "windows";
		} else if (isMac()) {
			osFolder = "macosx";
		} else if (isSolaris()) {
			osFolder = "solaris";
		} else if (isUnix()) {
			osFolder = "linux";
		} else {
			System.out.println("Not a valid OS");
			System.exit(-1);
		}
		System.out.println("Loading libraries for " + osFolder);
		System.setProperty("org.lwjgl.librarypath", new java.io.File("lib/native/" + osFolder).getAbsolutePath());
		
		try {			
			
			Engine.GAME = new Engine();
			
			Engine.GAME_CONTAINER = new AppGameContainer(Engine.GAME);
			
			Engine.GAME_CONTAINER.setDisplayMode(Engine.GAME_WIDTH, Engine.GAME_HEIGHT, false);
			Engine.GAME_CONTAINER.setAlwaysRender(Engine.ALWAYS_RENDER);
			Engine.GAME_CONTAINER.setVSync(Engine.VERTICAL_SYNC);
			Engine.GAME_CONTAINER.setShowFPS(Engine.SHOW_FPS);
			Engine.GAME_CONTAINER.setMaximumLogicUpdateInterval(MAX_LOGIC_UPD_INTERVAL);		
			Engine.GAME_CONTAINER.setTargetFrameRate(TARGET_FRAME_RATE);

			Engine.GAME_CONTAINER.setMouseGrabbed(true);
			Mouse.setGrabbed(true);
			
			Engine.GAME_CONTAINER.start();
			Mouse.setCursorPosition(0, 0);
			
		} catch (Exception e) {
			if (e.getClass() == SlickException.class) {
				System.out.println("Erro no Slick\n\n");
			}
			e.printStackTrace();
		}
	}
	
	public static Rectangle getScreenRectangle() {
		return new Rectangle(0, 0, GAME_WIDTH, GAME_HEIGHT);
	}
	
	public static SceneBase getFirstScene() {
		//return new SceneGameMode();
		return new SceneTitle();
	}
	
	/**
	 * ~ Spell Databasse ~
	 * 
	 * spell partice,
	 * damage, cooldown,
	 * speed, range,
	 * particleSpawnTime, particleLife,
	 * animation, animationFrames, animationSpeed, animation amount of particles,
	 * shoot sound, impact sound,
	 * light color
	 * 
	 */
	public static ItemSpell[] spellDatabase = {
		
		new ItemSpell(
				"spell_base",
				10, 50,
				10.5f, 70,
				0, 30,
				"sparks", 8, 0.2f, 30,
				"shoot_base.wav", "impact_base.wav",
				new Color(164, 30, 176, 170)),
				
		new ItemSpell(
				"spell_fire1",
				10, 60,
				12.5f, 70,
				0, 30,
				"fire", 8, 0.2f,30,
				"shoot_fire1.wav", "impact_fire1.wav",
				new Color(210, 98, 4, 170)),
				
		new ItemSpell(
				"spell_ice1",
				10, 60,
				9.5f, 75,
				2, 60,
				"ice", 8, 0.2f, 30,
				"shoot_ice1.wav", "impact_ice1.wav",
				new Color(0, 150, 150, 70)),
				
		new ItemSpell(
				"spell_lightning1",
				10, 60,
				16.5f, 70,
				0, 15,
				"lightning", 8, 0.2f, 3,
				"shoot_lightning1.wav", "impact_lightning1.wav",
				new Color(14, 14, 214, 170))
	};

}
