package game.engine.effects;

import game.engine.scene.SceneGame;

import java.util.Random;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;

/**
 * Classe que renderiza partículas para serem usadas em qualquer scene
 * @author André Micheletti
 *
 */
public class ParticleFX {
	
	private Image bitmap = null;
	
	private int timerMax = 30, timer = 0;
	
	private float x, y;
	private float x_vel, y_vel;
	
	public int drawMode = Graphics.MODE_NORMAL;
	public float rotation = 0;
	
	private SceneGame onScene = null;
	private boolean disposed = false;
	
	public ParticleFX(Image bit, float ax, float ay, float x_vel, float y_vel, int life, boolean rotate, SceneGame scene) {
		
		Random r = new Random();
		this.x = 1 + ax + (r.nextFloat() - 0.5f);
		this.y = 1 + ay + (r.nextFloat() - 0.5f);
		this.x_vel = x_vel;
		this.y_vel = y_vel;
		bitmap = bit.copy();
		
		timerMax = life;
		onScene = scene;
		
		if (rotate)
			rotation = (new Random().nextInt(361));
		
		bitmap.setCenterOfRotation(bitmap.getWidth() / 2, bitmap.getHeight() / 2);
		bitmap.setRotation(rotation);
	}
	
	public ParticleFX(Image bit, float ax, float ay, float x_vel, float y_vel, int life, boolean rotate ) {
		
		Random r = new Random();
		this.x = 1 + ax + (r.nextFloat() - 0.5f);
		this.y = 1 + ay + (r.nextFloat() - 0.5f);
		this.x_vel = x_vel;
		this.y_vel = y_vel;
		bitmap = bit.copy();
		
		timerMax = life;
		
		if (rotate)
			rotation = (new Random().nextInt(361));
		
		bitmap.setCenterOfRotation(bitmap.getWidth() / 2, bitmap.getHeight() / 2);
		bitmap.setRotation(rotation);
	}

	public void update() {
		if (isDisposed()) return;
		
		x += x_vel;
		y += y_vel;
		
		if (onScene != null)
			updateCollisions();
		
		if (timer >= timerMax) {
			dispose();
		} else {
			timer += 1;
		}		
	}
	
	private void updateCollisions() {
		if (isDisposed()) return;
		
		int[] tile = onScene.containWithMap(getBounds());
		if (tile != null) {
			if (tile[0] != getRealX()) {
				x_vel *= -1;				
			}
			if (tile[1] != y) {
				y_vel *= -1;				
			}
		}
	}

	public Rectangle getBounds() {
		if (onScene != null) 
			return new Rectangle(getRealX(), y, 8, 8);
		else
			return new Rectangle(x, y, 8, 8);
	}
	
	private float getRealX() {
		return x- onScene.screenX;
	}

	public void render(Graphics g) {
		if (isDisposed()) return;

		float dif = (float) timer / timerMax;
		Color myAlphaColor = new Color(1f,1f,1f,1f - dif);
		
		g.setDrawMode(drawMode);
		
		if (onScene != null) 
			g.drawImage(bitmap, getRealX(), y, myAlphaColor);
		else
			g.drawImage(bitmap, x, y, myAlphaColor);

		g.setDrawMode(Graphics.MODE_NORMAL);
		g.flush();
	}
	
	public void dispose() {
		if (isDisposed()) return;
		disposed = true;
	}
	
	public boolean isDisposed() {
		return disposed;
	}

	public void renderCustom(Graphics g, Color color) {
		if (isDisposed()) return;
		
		g.setDrawMode(drawMode);
		
		if (onScene != null) 
			g.drawImage(bitmap, getRealX(), y, color);
		else
			g.drawImage(bitmap, x, y, color);

		g.setDrawMode(Graphics.MODE_NORMAL);
		g.flush();
	}

}
