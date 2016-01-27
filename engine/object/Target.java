package game.engine.object;

import java.util.ArrayList;

import game.engine.Engine;
import game.engine.scene.SceneGame;
import game.main.ResourceLoader;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Rectangle;

/**
 * Classe que controla os alvos do jogo
 * @author André Micheletti
 *
 */
public class Target {
	
	public static String DEFAULT_BITMAP = "objects/target.png";
	
	public static String DEFAULT_SOUND = "target.wav";
	
	public int tileX = 0, tileY = 0;
	
	private Image bitmap = null;
	
	private Sound se = null;
	
	private SceneGame onScene = null;
	
	private boolean disposed = false;
	
	public Target(SceneGame scene, int x, int y) {
		try {
			
			onScene = scene;
			
			bitmap = ResourceLoader.getImage(DEFAULT_BITMAP);
			
			se = ResourceLoader.getSound(DEFAULT_SOUND);
			
			tileX = x; tileY = y;
			
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	
	public void update(int delta) {
		if (isDisposed()) return;
		
	}
	
	public void updateCollisionWithSpells(ArrayList<Spell> spells) {
		for (Spell p : spells) {
			if (p.getBounds().intersects(getBouds())) {
				p.collide();
				onDestroy();
				break;
			}
		}
	}
	
	public void onDestroy() {
		
	}
	
	public void playSound() {
		se.play();
	}
	
	public void render(Graphics g) {
		if (isDisposed()) return;
		
		g.drawImage(bitmap, tileX * 32 - onScene.screenX, tileY * 32);
		
		if (Engine.SHOW_FPS) {
			drawDebug(g);
		}
	}
	
	public void drawDebug(Graphics g) {
		g.setColor(Color.pink);
		g.draw(getBouds());
	}
	
	public Rectangle getBouds() {
		return new Rectangle(tileX * 32 - onScene.screenX, tileY * 32, 32, 32);
	}
	
	public void dispose() {
		disposed = true;
	}
	
	public boolean isDisposed() {
		return disposed;
	}

}
