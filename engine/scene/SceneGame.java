package game.engine.scene;

import java.util.ArrayList;
import java.util.Random;

import game.engine.Engine;
import game.engine.effects.AnimationFX;
import game.engine.effects.Light;
import game.engine.effects.LightFX;
import game.engine.effects.ParticleFX;
import game.engine.enums.MageClass;
import game.engine.object.Player;
import game.engine.object.Spell;
import game.main.ResourceLoader;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.tiled.TiledMap;

/**
 * Scene Game - A scene que controla o jogo
 * @author André Micheletti
 *
 */
public class SceneGame extends SceneBase {
	
	public TiledMap sceneMap = null;
	public Player player = null;

	public int screenX = 0;
	
	protected ArrayList<Shape> mapCollision = new ArrayList<Shape>();
	protected ArrayList<Spell> spells = new ArrayList<Spell>();
	protected ArrayList<AnimationFX> animations = new ArrayList<AnimationFX>();
	protected ArrayList<ParticleFX> mouseParticles = new ArrayList<ParticleFX>();
	protected ArrayList<LightFX> lightEffects = new ArrayList<LightFX>();
	
	protected int blockLayer = 0;
	protected Image background = null, parallax = null, parallax_2;
	
	protected float light_level = 1f;
	
	protected Sound bgm = null;
	protected Image cursorImage = null;
	
	protected HUD hud = null;
	protected ScenePause pause = null;
	
	/**
	 * Não utilizar estre construtor
	 */
	public SceneGame() {
		
	}
	
	/**
	 * Construtor Padrão
	 * @param map Arquivo do mapa com extensão
	 */
	public SceneGame(String map) {
		terminated = false; initialized = false;
		try {
			
			sceneMap = ResourceLoader.getMap(map);	
			getMapCollision();
			
			background = ResourceLoader.getImage("background/" + sceneMap.getMapProperty("background", "clouds.jpg"));
			
			String p = sceneMap.getMapProperty("parallax", "");
			if (!p.equals("")) parallax = ResourceLoader.getImage("background/" + p);
			
			p = sceneMap.getMapProperty("parallax_2", "");
			if (!p.equals("")) parallax_2 = ResourceLoader.getImage("background/" + p);	
			
			light_level = Float.parseFloat(sceneMap.getMapProperty("light_level", "1f"));
			
			blockLayer = sceneMap.getLayerIndex("Blocks");
			
			cursorImage = ResourceLoader.getImage("mouse_icon.png");
			
			bgm = ResourceLoader.getSound("music/" + sceneMap.getMapProperty("music", "music1.wav"));
			
			init();
			
		} catch (SlickException e) {
			System.out.println("Erro ao carregar mapa " + map + " na criação de SceneGame\n\n");
			e.printStackTrace();
		}
	}

	/**
	 * Método de Inicialização
	 */
	public void init() {
		
		Light.preloadImage();
		
		int px = Integer.parseInt(sceneMap.getMapProperty("playerSpawn", "3,3").split(",")[0]);
		int py = Integer.parseInt(sceneMap.getMapProperty("playerSpawn", "3,3").split(",")[1]);
		
		player = new Player(this, MageClass.NO_CLASS);
		player.moveTo(px, py);
		
		hud = new HUD(this);
		
		if (!bgm.playing())
			bgm.loop(1f, Engine.master_volume);
		
		createMouseParticle(4);
		fadeIn(60);
		initialized = true;
	}

	/**
	 * Loads the map collision of the map
	 */
	protected void getMapCollision() {
		int w = sceneMap.getWidth();
		int h = sceneMap.getHeight();
		
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				
				if (sceneMap.getTileId(x, y, 0) != 0) {
					mapCollision.add(new Rectangle(32 * x, 32 * y, 32, 32));
				}
				
				// Procura por efeitos de luz na camada de blocos
				if (!sceneMap.getTileProperty(sceneMap.getTileId(x, y, 0), "light_type", "").equals("")) {
					String light_type = sceneMap.getTileProperty(sceneMap.getTileId(x, y, 0), "light_type", "");
					float ofx = Float.parseFloat(sceneMap.getTileProperty(sceneMap.getTileId(x, y, 0), "offset_x", "0.0f"));
					float ofy = Float.parseFloat(sceneMap.getTileProperty(sceneMap.getTileId(x, y, 0), "offset_y", "0.0f"));
					float scale = Float.parseFloat(sceneMap.getTileProperty(sceneMap.getTileId(x, y, 0), "scale", "1f"));
					lightEffects.add(new LightFX(this, x, y, ofx, ofy, scale, light_type));
				}
				if (sceneMap.getLayerCount() > 1) {
					// Procura por efeitos de luz na camada extra
					if (!sceneMap.getTileProperty(sceneMap.getTileId(x, y, 1), "light_type", "").equals("")) {
						String light_type = sceneMap.getTileProperty(sceneMap.getTileId(x, y, 1), "light_type", "");
						float ofx = Float.parseFloat(sceneMap.getTileProperty(sceneMap.getTileId(x, y, 1), "offset_x", "0.0f"));
						float ofy = Float.parseFloat(sceneMap.getTileProperty(sceneMap.getTileId(x, y, 1), "offset_y", "0.0f"));
						float scale = Float.parseFloat(sceneMap.getTileProperty(sceneMap.getTileId(x, y, 1), "scale", "1f"));
						lightEffects.add(new LightFX(this, x, y, ofx, ofy, scale, light_type));
					}
				}
				
			}
		}
	}
	
	/**
	 * Método de atualização logica
	 * @param delta delta de tempo desde a ultima
	 */
	public void update(int delta) {
		
		// Atualiza o pause do jogo
		if (isPaused()) {
			if (pause.initialized) pause.update(delta);
			if (pause.terminated == true) {
				pause = null; bgm.play();
			} else
				return;
		}
		
		// Atualiza o Jogador
		player.update(delta);
		
		// Atualiza a HUD
		hud.update(delta);
		
		// Atualiza as Spells no mapa
		updateSpells();
		
		// Atualiza as animações no mapa
		updateAnimationsFX(delta);
		
		// Atualiza as partículas do mouse
		ArrayList<ParticleFX> trash3 = new ArrayList<ParticleFX>();
		for (ParticleFX a : mouseParticles) {
			if (a.isDisposed()) {
				trash3.add(a);
			} else {
				a.update();
			}
		}
		for (ParticleFX item : trash3) { mouseParticles.remove(item); createMouseParticle(1); }
		
		updateCamera();
		
	}
	
	protected void updateAnimationsFX(int delta) {
		ArrayList<AnimationFX> trash2 = new ArrayList<AnimationFX>();
		for (AnimationFX a : animations) {
			if (a.isDisposed()) {
				trash2.add(a);
			} else {
				a.update(delta);
			}
		}
		for (AnimationFX item : trash2) { animations.remove(item); }
	}
	
	protected void updateSpells() {
		ArrayList<Spell> trash = new ArrayList<Spell>();
		for (Spell s : spells) {
			if (s.isDisposed()) {
				if (s.particles.size() != 0) {
					s.updateParticles();
				} else {
					trash.add(s);
				}
			} else {
				s.update();
			}
		}
		for (Spell item : trash) { spells.remove(item); }
	}
	
	protected void updateCamera() {
		
		float playerSpeed = Math.abs(player.getXVel());
		if (playerSpeed == 0) playerSpeed = 4;
		
		float dif = (player.getX() + 16 - screenX);
		if ((350 < dif && dif < 465) == false) playerSpeed = 12;
		
		if (player.getRealX() + 16 < 360) {
			screenX -= playerSpeed;
		} else if (player.getRealX() + 16 > 440) {
			screenX += playerSpeed;
		}
		
		int max_x = sceneMap.getWidth() * 32 - 800;
		
		screenX = Math.max(0, screenX);
		if (screenX > max_x) screenX = max_x;
		
	}

	/**
	 * Método de renderização
	 * @param g
	 */
	public void render(Graphics g) {
		
		// Renderiza os elementos do jogo
		renderGameElements(g);
		
		// Renderiza os elementos de debug
		if (Engine.SHOW_FPS) {
			drawDebug(g);
		}
		
		// Renderiza elementos da tela
		renderScreen(g);
		
		// Renderiza o pause
		if (isPaused())
			pause.render(g);
		else 
			// Renderiza o Mouse
			renderMouse(g);
		
	}

	protected void renderGameElements(Graphics g) {	
		// Renderiza o fundo
		g.drawImage(background, 0, 0);
		
		// Renderiza o parallax
		if (parallax != null) g.drawImage(parallax, screenX * -0.5f - 200, 0);
		
		// Renderiza a camada Extra
		if (sceneMap.getLayerCount() > 1) sceneMap.render(-screenX, 0, 1);
		
		// Renderiza o jogador
		player.render(g);
		
		// Renderiza o Mapa
		sceneMap.render(-screenX, 0, blockLayer);
		
		// Renderiza os efeitos de luz
		renderLightLevel(g);

		// Renderiza e atualiza as Spells
		ArrayList<Spell> trash = new ArrayList<Spell>();
		for (Spell s : spells) {
			if (s.isDisposed()) {
				if (s.particles.size() != 0) s.renderParticles(g);
				else trash.add(s);
			} else {
				s.render(g);
			}
		}
		for (Spell item : trash) spells.remove(item);
		
		// Renderiza as animaçõs
		for (AnimationFX a : animations) a.render(g);
		
		// Renderiza o parallax frontal
		if (parallax_2 != null) g.drawImage(parallax_2, screenX * -0.75f, 0);
		
		// Renderiza a HUD
		hud.render(g);
	}

	protected void renderLightLevel(Graphics g) {
		// Renderiza o nivel de luminosidade do mapa
		if (light_level != 1f & light_level != 0.0f) {
			g.setColor(getLightLevelColor());
			g.fill(Engine.getScreenRectangle());
		}		
		
		// Renderiza os efeitos de luz no mapa
		for (LightFX effect : lightEffects) effect.render(g);
	}
	
	public Color getLightLevelColor() {
		return new Color(0f,0f,0f,1f - light_level);
	}

	protected void drawDebug(Graphics g) {
		g.setColor(Color.white);
		for (Shape r : mapCollision) {
			g.draw(new Rectangle(r.getX() - screenX, r.getY(), r.getWidth(), r.getHeight()));
		}
		
		g.setColor(Color.cyan);
		g.draw(getCameraBounds());
		
		g.setColor(Color.white);
		g.drawString("TILE X = " + player.getTileX(), 100, 40);
		g.drawString("TILE Y = " + player.getTileY(), 100, 60);
		g.drawString("SCREEN X = " + screenX, 100, 80);
		g.drawString("G_STATE = " + player.getGState(), 100, 100);
		g.drawString("WALL_SLIDING = " + player.isWallSliding(), 100, 120);
		g.drawString("EQUIPPED_SPELL = " + player.getEquippedSpell().spellBitmap, 100, 140);
		g.drawString("DIRECTION = " + player.getDir(), 600, 40);
		g.drawString("X_VELOCITY = " + player.getXVel(), 600, 60);
		g.drawString("Y_VELOCITY = " + player.getYVel(), 600, 80);
		g.drawString("SPELL ON MAP = " + spells.size(), 600, 100);
		g.drawString("ANIMATION ON MAP = " + animations.size(), 600, 120);
		int total_p = 0;
		for (Spell p : spells)	total_p += p.particles.size();
		for (AnimationFX a : animations) total_p += a.particles.size();
		g.drawString("TOTAL PARTICLES = " + total_p, 600, 140);
		g.drawString("F1 to close Debug", 100, 20);
		

	}
	
	public void addSpell(Spell s) {
		spells.add(s);
	}
	
	protected Rectangle getCameraBounds() {
		return new Rectangle(360, 0, 80, 640);
	}
	
	protected void createMouseParticle(int qnt) {
		if (player.getEquippedSpell() == null) return;
		try {
			Image a = ResourceLoader.getImage("particles/" + player.getEquippedSpell().spellBitmap + "_particle.png");
			
			int ax = Engine.getInput().getMouseX() + screenX;
			int ay = Engine.getInput().getMouseY();
			int life = 30 + (new Random().nextInt(31) - 15);
			for (int i = 0; i < qnt; i ++) {
				float x_vel = 0.5f - (new Random().nextFloat()) * 1.5f;
				float y_vel = 0.5f - (new Random().nextFloat()) * 1.5f;
				mouseParticles.add(new ParticleFX(a, ax, ay, x_vel, y_vel, life, true, this));
			}
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	
	protected void renderMouse(Graphics g) {
		g.drawImage(cursorImage , Engine.getInput().getMouseX(), Engine.getInput().getMouseY());
		for (ParticleFX p : mouseParticles) {
			p.render(g);
		}
	}
	
	/**
	 * Método de  Finalização da Scene
	 */
	public void terminate() {
		fadeOutAndTerminate(60);
	}
	
	public void addAnimationFX(AnimationFX anim) {
		animations.add(anim);
	}
	
	/**
	 * Retorna o tipo de tile determinado pelas coord.
	 * @param x a coord x do tile
	 * @param y a coord y do tile
	 * @return o Id do tipo do tile encontrado
	 */
	public int getTileId(int x, int y) {
		try {
			return sceneMap.getTileId(x, y, blockLayer);
		} catch (java.lang.ArrayIndexOutOfBoundsException ex) {
			//System.out.println("Trying to move out of the Map ~ SceneGame");
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * Checa se determinada figura colide com o mapa
	 * @param yours a figura
	 * @return true se colidiu, false caso contrário
	 */
	public int[] collidesWithMap(Shape yours) {
		for (Shape r : mapCollision) {
			Shape newR = new Rectangle(r.getX() - screenX, r.getY(), r.getWidth(), r.getHeight());
			if (newR.intersects(yours)) {
				return new int[] { Math.round(newR.getX()), Math.round(newR.getY()) };
			}
		}
		return null;
	}
	/**
	 * Checa se determinada figura contem com o mapa
	 * @param yours a figura
	 * @return true se colidiu, false caso contrário
	 */
	public int[] containWithMap(Shape yours) {
		for (Shape r : mapCollision) {
			Shape newR = new Rectangle(r.getX() - screenX, r.getY(), r.getWidth(), r.getHeight());
			if (newR.contains(yours)) {
				return new int[] { Math.round(newR.getX()), Math.round(newR.getY()) };
			}
		}
		return null;
	}
	
	public void fadeIn(int time) {
		fadeinTime = time;
		fadeinTimer = time;
	}
	
	public void fadeOut(int time) {
		fadeoutTime = time;
		fadeoutTimer = time;
	}
	
	public void pauseGame() {
		if (isPaused()) return;
		
		bgm.stop();
		pause = new ScenePause(this);
	}
	
	public boolean isPaused() {
		return (pause != null);
	}
	
	/**
	 * Classe que controla a cena de pause
	 * @author André Micheletti
	 *
	 */
	public class ScenePause extends SceneBase {
		
		private SceneGame onScene = null;
		
		private Image layer1 = null;
		
		public ScenePause(SceneGame me) {
			onScene = me; initialized = false; terminated = false;
			try {
				
				layer1 = ResourceLoader.getImage("scene/pause_layer1.png");
				
				mouseBitmap = ResourceLoader.getImage(Engine.MOUSE_BITMAP);
				
				init();
				
			} catch (SlickException e) {
				terminated = true;
				e.printStackTrace();
			}
		}		
		
		/**
		 * Método de Inicialização
		 */
		public void init() {
			initialized = true;
		}
		
		/**
		 * Método de atualização logica
		 * @param delta delta de tempo desde a ultima
		 */
		public void update(int delta) {
			super.update(delta);
			if (Engine.getInput().isKeyPressed(Input.KEY_ESCAPE)) {
				terminate();
			}
		}
		
		/**
		 * Método de renderização
		 * @param g
		 */
		public void render(Graphics g) {
			g.drawImage(layer1, 0, 0);
			this.renderScreen(g);
			this.renderMouse(g);
		}
		
		/**
		 * Determina a próxima scene
		 * @return a proxima scene
		 */
		public SceneBase nextScene() {
			return onScene;
		}
		
		/**
		 * Método de  Finalização da Scene
		 */
		public void terminate() {
			terminated = true;
		}
	}

	@SuppressWarnings("unused")
	/**
	 * Classe que controla a HUD do jogo
	 * @author André Micheletti
	 *
	 */ protected class HUD {
		
		private boolean showing = true;
		private SceneGame onScene = null;
		
		private Image bar_base;
		private Image hp_bar, hp_bar_remove;
		
		public HUD(SceneGame scene) {
			String hud = "hud/";
			try {
				
				this.onScene = scene;
				showing = true;
				
				bar_base = ResourceLoader.getImage(hud + "bar_base.png");
				hp_bar = ResourceLoader.getImage(hud + "hp_bar.png");
				hp_bar_remove = ResourceLoader.getImage(hud + "hp_bar_remove.png");
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public void update(int delta) {
			
		}
		
		public void render(Graphics g) {
			if (!showing) return;
			
			int wd = 100 * getPlayer().getHp() / getPlayer().getMaxHp();
			
			g.drawImage(bar_base, Engine.HUD_HPX, Engine.HUD_HPY);
			
			hp_bar.draw(Engine.HUD_HPX + 3, Engine.HUD_HPY + 3, wd, 10);
			
		}
		
		public void hide() {
			showing = false;
		}
		
		public void show() {
			showing = true;
		}
		
		public boolean isVisible() {
			return showing;
		}
		
		private Player getPlayer() {
			return onScene.player;
		}
		
	}
	
}
