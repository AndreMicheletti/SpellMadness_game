package game.engine.scene;

import game.engine.Engine;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

/**
 * Classe principal de Scenes
 * @author André Micheletti
 *
 */
public abstract class SceneBase {
	
	public boolean terminated = false;
	public boolean initialized = false;
	
	protected Image mouseBitmap = null;
	
	protected boolean fadingOutToTerminate = false;
	
	protected int fadeinTimer = 0, fadeoutTimer = 0;	
	protected int fadeinTime, fadeoutTime;
	
	public SceneBase() {
		
	}
	
	/**
	 * Método de Inicialização
	 */
	public void init() {
		
	}
	
	/**
	 * Método de atualização logica
	 * @param delta delta de tempo desde a ultima
	 */
	public void update(int delta) {
		
	}
	
	/**
	 * Método de renderização
	 * @param g
	 */
	public void render(Graphics g) {
		
	}
	
	/**
	 * Renderiza os elementos da tela
	 * @param g
	 */
	protected void renderScreen(Graphics g) {
		// Renderiza o Fade Out
		if (fadeoutTimer > 0) {			
			fadeoutTimer -= 1;			
			float dif = ((float) fadeoutTimer / (float) fadeoutTime);
			g.setColor(new Color(0f,0f,0f,1f - dif));
			g.fillRect(0, 0, Engine.GAME_WIDTH, Engine.GAME_HEIGHT);			
			if (fadeoutTimer == 0) {
				fadeoutTime = 0;
				if (fadingOutToTerminate) terminated = true;
			}
		}
		
		// Renderiza o Fade In
		if (fadeinTimer > 0) {			
			fadeinTimer -= 1;			
			float dif = ((float) fadeinTimer / (float) fadeinTime);
			g.setColor(new Color(0f,0f,0f,dif));
			g.fillRect(0, 0, Engine.GAME_WIDTH, Engine.GAME_HEIGHT);			
			if (fadeinTimer == 0) fadeinTime = 0;
		}		
	}
	
	/**
	 * Método de renderização do Mouse
	 * @param g
	 */
	protected void renderMouse(Graphics g) {
		if (mouseBitmap == null) { System.out.println("No mouse bitmap loaded for this scene !"); return; }
		mouseBitmap.draw(Engine.getInput().getMouseX(), Engine.getInput().getMouseY()); 
	}
	
	/**
	 * Determina a próxima scene
	 * @return a proxima scene
	 */
	public SceneBase nextScene() {
		return null;
	}
	
	/**
	 * Método de  Finalização da Scene
	 */
	public void terminate() {
		
	}
	
	/**
	 * Realiza um efeito de FadeIn na tela com determinada duração
	 * @param time a duração do efeito em frames
	 */
	public void fadeIn(int time) {
		fadeinTime = time;
		fadeinTimer = time;
	}

	
	/**
	 * Realiza um efeito de FadeOut na tela com determinada duração
	 * @param time a duração do efeito em frames
	 */
	public void fadeOut(int time) {
		fadeoutTime = time;
		fadeoutTimer = time;
	}
	
	/**
	 * Realiza um efeito de FadeOut na tela com determinada duração
	 * @param time a duração do efeito em frames
	 */
	public void fadeOutAndTerminate(int time) {
		fadeoutTime = time;
		fadeoutTimer = time;
		fadingOutToTerminate = true;
	}

}
