package game.engine.scene;

import java.util.ArrayList;
import java.util.Random;

import game.engine.Engine;
import game.engine.effects.AnimationFX;
import game.engine.enums.GameMode;
import game.engine.object.Spell;
import game.engine.object.Target;
import game.main.DataManager;
import game.main.ResourceLoader;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;

/**
 * Scene Game Arcade - A scene que controla o jogo (modo arcade)
 * @author André Micheletti
 *
 */
public class SceneGameArcade extends SceneGame {
	
	public GameMode gameMode = null;

	private ArrayList<Integer[]> targetsSpawn = new ArrayList<Integer[]>();
	
	private ArrayList<Target> targets = new ArrayList<Target>();
	
	private int maxTargetsOnMap = 1, maxTargetsOnScreen = 1, timeLimit = 120;
	private int timer = 0, spawnDelay = 720, spawnTimer = 0;
	private Integer[] lastRandom = null;
	@SuppressWarnings("unused")
	private int targetsDestroyed = 0, targetsLost = 0, targetsCreated = 0;
	
	/**
	 * Construtor Padrão
	 * @param map Arquivo do mapa com extensão
	 */
	public SceneGameArcade(String map, GameMode mode) {
		terminated = false; initialized = false;
		gameMode = mode;
		try {
			
			sceneMap = ResourceLoader.getMap(map);	
			getMapCollision();
			
			loadArcadeConfig(map.replaceFirst(".tmx", ".spm"));
			
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
		super.init();
		//System.out.println("\nEncrypt: " +  DataManager.encryptString("max_target:1;max_target_on_map:20;spawn:6,6;spawn:6,9;spawn:15,4;spawn:23,4;19,4;spawn:19,8;spawn:16,11;spawn:22,11;spawn:9,13;spawn:4,15;spawn:36,5;spawn:27,5;spawn:28,11;spawn:36,13;time_limit:3600;spawn_delay:540;"));
	}

	
	/**
	 * Método de atualização logica
	 * @param delta delta de tempo desde a ultima
	 */
	private void loadArcadeConfig(String file) {
		file = "res/maps/" + file;
		String content = DataManager.lerArq2(file);
		for (String line : content.split(";")) {
			if (line.startsWith("spawn:")) {
				int x = Integer.parseInt(line.replaceAll("spawn:", "").split(",")[0]);
				int y = Integer.parseInt(line.replaceAll("spawn:", "").split(",")[1]);
				targetsSpawn.add(new Integer[] {x, y});
			}
			if (line.startsWith("max_target:")) {
				maxTargetsOnScreen = Integer.parseInt(line.replaceAll("max_target:", ""));
			}
			if (line.startsWith("max_target_on_map:")) {
				maxTargetsOnMap = Integer.parseInt(line.replaceAll("max_target_on_map:", ""));
			}
			if (line.startsWith("time_limit:")) {
				timeLimit = Integer.parseInt(line.replaceAll("time_limit:", ""));
			}
			if (line.startsWith("spawn_delay:")) {
				spawnDelay = Integer.parseInt(line.replaceAll("spawn_delay:", ""));
			}
		}
	}

	
	/**
	 * Método de atualização logica
	 * @param delta delta de tempo desde a ultima
	 */
	public void update(int delta) {
		super.update(delta);
		
		updateTargets();
		
		if (targetsCreated == maxTargetsOnMap) {
			if (targetsDestroyed == targetsCreated) {
				// WIN ~ END
				targets.clear();
			}
		} else {			
			updateTimer();
			
			updateSpawn();
		}
		
	}
	
	private void updateSpawn() {
		if (spawnTimer > 0) {
			spawnTimer -= 1;
		} else {
			if (targets.size() >= maxTargetsOnScreen) {
				removeLastTarget();
			}
			spawnTarget();
		}
	}
	
	private void spawnTarget() {
		
		Integer[] random;
		do {
			random = targetsSpawn.get(new Random().nextInt(targetsSpawn.size()));
		} while (lastRandom == random);
		
		lastRandom = random;
		
		Target t = new Target(this, random[0], random[1]) {
			public void onDestroy() {
				targetsDestroyed += 1;
				spawnTimer = 0;
				this.playSound();
				this.dispose();
			}
		};
		targets.add(t);
		targetsCreated += 1;
		
		spawnTimer = spawnDelay;		
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
	
	private void updateTargets() {
		ArrayList<Target> lixo = new ArrayList<Target>();
		for (Target t : targets) {
			t.updateCollisionWithSpells(spells);
			if (t.isDisposed()) lixo.add(t);
		}
		for (Target item : lixo) targets.remove(item);
	}
	
	private void removeLastTarget() {
		targetsLost += 1;
		targets.get(0).dispose();
		targets.remove(targets.get(0));
	}
	
	public void clearTargets() {
		targets.clear();
	}
	
	private void updateTimer() {
		timer += 1;
		if (timer > timeLimit) {
			// "GAME OVER" aqui
		}
	}
	
	/**
	 * Renderiza informaçoes de arcade
	 * @param g
	 */
	private void renderHeadsUp(Graphics g) {
		String str = "";
		
		float total_seconds = timer / Engine.TARGET_FRAME_RATE;
		int minutes = (int) (total_seconds / 60);
		int seconds = (int) (total_seconds - (60 * minutes));
		float milis = timer - (60.0f * seconds);
		
		str += (minutes > 9 ? minutes + ":" : "0" + minutes + ":");
		str += (seconds > 9 ? seconds + "."  : "0" + seconds + "." );
		str += (milis > 9.0f ? milis : "0" + milis).toString().replace(".0", "");
		
		g.setColor(Color.white);
		g.drawString("Time: " + str, 20, 20);
		
		g.drawString("Targets: " + targetsDestroyed + "/" + targetsCreated, 180, 20);
		
	}
	
	/**
	 * Renderiza os elementos da tela
	 * @param g
	 */
	protected void renderScreen(Graphics g) {
		super.renderScreen(g);
		renderHeadsUp(g);
	}

	/**
	 * Renderiza os elementod do jogo
	 * @param g
	 */
	protected void renderGameElements(Graphics g) {	
		// Renderiza o fundo
		g.drawImage(background, 0, 0);
		
		// Renderiza o parallax
		if (parallax != null) g.drawImage(parallax, screenX * -0.5f - 200, 0);
		
		// Renderiza a camada Extra
		if (sceneMap.getLayerCount() > 1) sceneMap.render(-screenX, 0, 1);
		
		// Renderiza o jogador
		player.render(g);
		
		// Renderiza os Targets
		for (Target t : targets) t.render(g);
		
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
	
	/**
	 * Checa se determinada figura colide com o mapa
	 * @param yours a figura
	 * @return true se colidiu, false caso contrário
	 */
	public int[] collidesWithMap(Shape yours) {
		for (Shape r : mapCollision) {
			Shape newR = new Rectangle(r.getX() - screenX, r.getY(), r.getWidth(), r.getHeight());
			if (newR.intersects(yours))
				return new int[] { Math.round(newR.getX()), Math.round(newR.getY()) };
		}
		for (Target t : targets) {
			Shape newR = t.getBouds();
			if (newR.intersects(yours))
				return new int[] { Math.round(newR.getX()), Math.round(newR.getY()) };
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
			if (newR.contains(yours)) 
				return new int[] { Math.round(newR.getX()), Math.round(newR.getY()) };
		}
		for (Target t : targets) {
			Shape newR = t.getBouds();
			if (newR.contains(yours))
				return new int[] { Math.round(newR.getX()), Math.round(newR.getY()) };
		}
		return null;
	}
}