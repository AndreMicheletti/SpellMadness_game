package game.engine.effects;

import game.engine.object.Spell;

import java.util.Random;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;

/**
 * Classe que renderiza partículas usadas nas spells
 * @author André Micheletti
 *
 */
public class Particle {
	
	private Spell origin = null;
	private Image bitmap = null;
	
	private int timerMax = 30, timer = 0;
	
	private float x, y;
	private float x_vel, y_vel;
	
	public int drawMode = Graphics.MODE_NORMAL;
	public float rotation = 0;
	
	private boolean disposed = false;
	
	public Particle(Spell or, Image bit, boolean rotate) {
		origin = or;
		Random r = new Random();
		x = origin.getX() + (r.nextInt(3) - 1);
		y = origin.getY() + (r.nextInt(3) - 1);
		x_vel = (r.nextFloat() * 2) - 1f;
		y_vel = (r.nextFloat() * 2) - 1f;
		bitmap = bit.copy();
		
		timerMax = origin.getItem().particleLife;
		
		if (rotate)
			rotation = (new Random().nextInt(361));
		
		bitmap.setCenterOfRotation(bitmap.getWidth() / 2, bitmap.getHeight() / 2);
		bitmap.setRotation(rotation);
	}
	
	public void update() {
		if (isDisposed()) return;
		
		x += x_vel;
		y += y_vel;
		updateCollisions();
		
		if (timer >= timerMax) {
			dispose();
		} else {
			timer += 1;
		}		
	}
	
	private void updateCollisions() {
		if (isDisposed()) return;
		
		int[] tile = origin.onScene.collidesWithMap(getBounds());
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
		return new Rectangle(getRealX(), y, 8, 8);
	}
	
	public float getRealX() {
		return x- origin.onScene.screenX;
	}
	
	public float getRealY() {
		return y;
	}

	public void render(Graphics g) {
		if (isDisposed()) return;
		
		g.setDrawMode(drawMode);
		
		float dif = (float) timer / timerMax;
		Color myAlphaColor = new Color(1f,1f,1f,1f - dif);
		
		g.drawImage(bitmap, getRealX(), y, myAlphaColor);
		
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

}
