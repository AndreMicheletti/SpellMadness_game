package game.engine.scene;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

import game.engine.Engine;
import game.engine.Profile;
import game.engine.effects.ParticleFX;
import game.engine.ui.UIButton;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Sound;

import static game.main.ResourceLoader.*;

/**
 * Scene Title - A scene que controla a Tela de Título
 * @author André Micheletti
 *
 */
public class SceneTitle extends SceneBase {
	
	public static int FADEIN_TIME = 120;
	public static int FADEIN_TIME2 = 60;
	public static int FADEOUT_TIME = 40;
	
	private Image layer1, layer2, layer3;

	private Image fire, ice, sparks;
	
	private UIButton play;
	private UIButton options;
	private UIButton profile;
	private UIButton exit;
	
	private int fadeInTimer = FADEIN_TIME;
	private int fadeInTimer2 = FADEIN_TIME2;
	
	private NScene nextScene = NScene.PLAY;
	
	private boolean fadeOut = false;
	private int fadeOutTimer = 0;
	
	private Sound titleBGM = null;
	
	private int backgroundScroll = 0;
	
	private int particleTimer = 0;
	public int screenX = 0;
	
	private ArrayList<ParticleFX> particles = new ArrayList<ParticleFX>();
	
	public SceneTitle() {
		String folder = "scene/";
		terminated = false; initialized = false;
		try {
			
			mouseBitmap = getImage(Engine.MOUSE_BITMAP);
			layer1 = getImage(folder + "title_layer1.png");
			layer2 = getImage(folder + "title_layer2.png");
			layer3 = getImage(folder + "title_layer3.png");

			sparks = getImage(folder + "title_spark.png");
			fire = getImage(folder + "title_fire.png");
			ice = getImage(folder + "title_ice.png");
			
			titleBGM = getSound("title.wav");	
			
			play = new UIButton(folder + "btn_fire.png", folder + "btn_fire_over.png", 113, 298, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					play();
				}
			});
			options = new UIButton(folder + "btn_ice.png", folder + "btn_ice_over.png", 106, 424, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					options();
				}
			});
			profile = new UIButton(folder + "btn_spark.png", folder + "btn_spark_over.png", 600, 298, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					profile();
				}
			});
			exit = new UIButton(folder + "btn_rock.png", folder + "btn_rock_over.png", 597, 429, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					exit();
				}
			});
			
		} catch (Exception e) {
			System.out.println("Error when loading SceneTitle");
			e.printStackTrace();
		} finally {
			init();
		}
	}
	
	/**
	 * Método de Inicialização
	 */
	public void init() {
		initialized = true;
		System.out.println("LOADING PROFILE");
		Profile.loadProfile();
	}
	
	/**
	 * Método de atualização logica
	 * @param delta delta de tempo desde a ultima
	 */
	public void update(int delta) {
		if (terminated) return;	

		this.play.update();
		this.exit.update();
		this.options.update();
		this.profile.update();
		
		if (fadeInTimer2 <= 30) {
			updateParticles();
		}
	}
	
	private void updateParticles() {
		if (particleTimer <= 0) {
			particleTimer = 10;
			createFireParticles();
			createIceParticles();
			createSparkParticles();
		} else {
			particleTimer -= 1;
		}
			
		// Atualiza as particulas da tela
		ArrayList<ParticleFX> trash = new ArrayList<ParticleFX>();
		for (ParticleFX a : particles) {
			if (a.isDisposed()) {
				trash.add(a);
			} else {
				a.update();
			}
		}
		for (ParticleFX item : trash) { particles.remove(item); }
	}
	
	private void createFireParticles() {
		int ax = 90 + (new Random().nextInt(17) - 8);
		int ay = 250 + (new Random().nextInt(17) - 8);
		int life = 40 + (new Random().nextInt(31) - 15);
		for (int i = 0; i < 2; i ++) {
			float x_vel = 1f - (new Random().nextFloat()) * 2f;
			float y_vel = (new Random().nextFloat()) * -2f;
			ParticleFX p = new ParticleFX(fire, ax, ay, x_vel, y_vel, life, false);
			//p.drawMode = Graphics.MODE_ADD;
			particles.add(p);
		}
	}
	
	private void createIceParticles() {
		int ax = 85 + (new Random().nextInt(17) - 8);
		int ay = 380 + (new Random().nextInt(17) - 8);
		int life = 100 + (new Random().nextInt(31) - 15);
		float x_vel = 0.8f - (new Random().nextFloat()) * 2f;
		float y_vel = (new Random().nextFloat()) * 2f;
		ParticleFX p = new ParticleFX(ice, ax, ay, x_vel, y_vel, life, true);
		//p.drawMode = Graphics.MODE_ADD;
		particles.add(p);
	}
	
	private void createSparkParticles() {
		int ax = 560 + (new Random().nextInt(17) - 8);
		int ay = 280 + (new Random().nextInt(17) - 8);
		int life = 65 + (new Random().nextInt(31) - 15);
		float x_vel = 0.8f - (new Random().nextFloat()) * 2f;
		float y_vel = 0.8f - (new Random().nextFloat()) * 2f;
		ParticleFX p = new ParticleFX(sparks, ax, ay, x_vel, y_vel, life, true);
		//p.drawMode = Graphics.MODE_ADD;
		particles.add(p);
	}
	
	/**
	 * Método de renderização
	 * @param g
	 */
	public void render(Graphics g) {
		if (terminated) return;
		
		Color color = new Color(1f,1f,1f,1f);
		Color color2 = new Color(1f,1f,1f,1f);
		
		backgroundScroll += 1;
		if (backgroundScroll >= 800) {
			backgroundScroll = 0;
		}
		
		g.drawImage(layer2, backgroundScroll, 0);
		g.drawImage(layer2, backgroundScroll - 800, 0);
		
		if (fadeInTimer > 0) {
			float dif = (float) fadeInTimer / FADEIN_TIME;
			
			color = new Color(1f,1f,1f,1f - dif);
			fadeInTimer -= 1;
			
			color2 = new Color(1f,1f,1f,0f);
			
			if (fadeInTimer == 0) {
				if (!titleBGM.playing()) {
					titleBGM.loop(1f, Engine.master_volume);
					play.enable();
					options.enable();
					exit.enable();
					profile.enable();
				}
			}
		} else {			
			if (fadeInTimer2 > 0) {
				float dif = (float) fadeInTimer2 / FADEIN_TIME2;
				color2 = new Color(1f,1f,1f,1f - dif);
				fadeInTimer2 -= 1;
			}
		}		
		
		g.drawImage(layer1, 0, 0, color);
		
		for (ParticleFX a : particles)
				a.render(g);
		
		g.drawImage(layer3, 0, 0, color2);
		
		play.renderCustom(g, color2);
		options.renderCustom(g, color2);
		profile.renderCustom(g, color2);
		exit.renderCustom(g, color2);
		
		if (fadeOut) {
			
			fadeOutTimer += 1;
			if (fadeOutTimer >= FADEOUT_TIME)  {
				terminated = true;
			}
			titleBGM.stop();
			
			float dif = ((float) fadeOutTimer / (float) FADEOUT_TIME);
			g.setColor(new Color(0f,0f,0f,dif));
			g.fillRect(0, 0, Engine.GAME_WIDTH, Engine.GAME_HEIGHT);
		}
		
		renderMouse(g);
		
	}
	
	private void play() {
		System.out.println("PLAY!");
		terminate();
	}
	
	private void options() {
		System.out.println("OPTIONS!");
	}
	
	private void profile() {
		System.out.println("PROFILE!");
	}
	
	private void exit() { 
		System.out.println("EXIT!");
		terminate();
		nextScene = NScene.EXIT;
	}
	
	public SceneBase nextScene() {
		switch (nextScene) {
		case PLAY:
			return new SceneGameMode();
		case PROFILE:
			return new SceneProfile();
		case OPTIONS:
			return new SceneOptions();
		case EXIT:
			return null;
		default:
			return new SceneGame("teste2.tmx");
		}
	}

	/**
	 * Método de  Finalização da Scene
	 */
	public void terminate() {
		fadeOut = true;
	}
	
	private enum NScene {
		PLAY, OPTIONS, PROFILE, EXIT
	}

}
