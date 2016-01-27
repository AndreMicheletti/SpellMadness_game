package game.engine.scene;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import game.engine.Engine;
import game.engine.Profile;
import game.engine.enums.GameMode;
import game.engine.ui.UIButton;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import static game.main.ResourceLoader.*;

/**
 * Classe que define a Scene de escolha de modo de jogo
 * @author André Micheletti
 *
 */
public class SceneGameMode extends SceneBase {
	
	private String FOLDER = "scene/";
	
	private Image background = null;
	
	private NScene nextScene = NScene.CAMPAIGN;
	
	private UIButton campaign_start, campaign_continue, arcade;
	
	public SceneGameMode() {
		try {

			mouseBitmap = getImage(Engine.MOUSE_BITMAP);
			background = getImage(FOLDER + "gamemode_layer1.png");
			
			campaign_start = new UIButton(FOLDER + "btn_campaign.png", FOLDER + "btn_campaign_over.png", 75, 210, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					startCampaign();
				}
			});
			campaign_continue = new UIButton(FOLDER + "btn_continue.png", FOLDER + "btn_continue_over.png", 420, 210, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					continueCampaign();
				}
			});
			arcade = new UIButton(FOLDER + "btn_arcade.png", FOLDER + "btn_arcade_over.png", 245, 380, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					startArcade();
				}
			});
			
			
		} catch (SlickException ex) {
			ex.printStackTrace();
		} finally {
			init();
		}
	}
	
	/**
	 * Método de Inicialização
	 */
	public void init() {
		terminated = false;
		initialized = true;
		this.campaign_start.enable();
		if (Profile.hasSaveFile()) {
			System.out.println("HAS SAVE FILE!");
			this.campaign_continue.enable();
		} else {
			System.out.println("HAS NOT SAVE FILE!");
			this.campaign_continue.disable();
		}
		this.arcade.enable();
		fadeIn(60);
	}
	
	/**
	 * Método de atualização logica
	 * @param delta delta de tempo desde a ultima
	 */
	public void update(int delta) {
		if (Engine.getInput().isKeyPressed(Input.KEY_ENTER)) {
			terminate();
		}
		this.campaign_start.update();
		this.campaign_continue.update();
		this.arcade.update();
	}
	
	/**
	 * Método de renderização
	 * @param g
	 */
	public void render(Graphics g) {
		background.draw(0, 0);

		this.campaign_start.render(g);
		this.campaign_continue.render(g);
		this.arcade.render(g);	
		
		renderScreen(g);
		renderMouse(g);
	}
	
	private void startCampaign() {
		System.out.println("STARTING CAMPAIGN ");
		nextScene = NScene.CAMPAIGN;
		terminate();
	}
	
	private void continueCampaign() {
		System.out.println("LOADING CAMPAIGN ");
		nextScene = NScene.LOAD;
	}
	
	private void startArcade() {
		System.out.println("STARTING ARCADE ");
		nextScene = NScene.ARCADE;
		terminate();
	}
	
	/**
	 * Determina a próxima scene
	 * @return a proxima scene
	 */
	public SceneBase nextScene() {
		switch (nextScene) {
		case CAMPAIGN:
			return new SceneGameCampaign("teste2.tmx");
		case LOAD:
			return new SceneGameCampaign("teste2.tmx");
		case ARCADE:
			return new SceneGameArcade("arcade_maps/shooting/shooting_1.tmx", GameMode.ARCADE_SHOOTING);			
		default:
			return new SceneGameCampaign("teste2.tmx");
		}
	}
	
	/**
	 * Método de  Finalização da Scene
	 */
	public void terminate() {
		fadeOutAndTerminate(60);
	}

	/**
	 * Enumerable que guarda a escolha feita
	 * @author André Micheletti
	 *
	 */
	private enum NScene {
		CAMPAIGN, LOAD, ARCADE
	}

}
