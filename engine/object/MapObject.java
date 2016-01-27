package game.engine.object;

import game.engine.scene.SceneGame;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;

public abstract class MapObject {
	
	protected int tilex = 0, tiley = 0;
	protected float x = 0, y = 0;
	protected float y_vel = 0, x_vel = 0;
	
	protected int width = 32, height = 32;
	
	protected Image bitmap = null;
	
	protected SceneGame onScene = null;
	
	protected boolean disposed = false;
	
	public MapObject(SceneGame scene) {
		onScene = scene;
		init();
	}
	
	protected void init() {
		disposed = false;
	}
	
	public void update() {
		if (isDisposed()) return;
		updateObject();
	}
	
	protected abstract void updateObject();
	
	public abstract void render(Graphics g);
	
	public Rectangle getBouds() {
		return new Rectangle(tilex * 32 - onScene.screenX, tiley * 32, 32, 32);
	}
	
	public void dispose() {
		disposed = true;
	}
	
	public boolean isDisposed() {
		return disposed;
	}
	
	public float getYVel() {
		return y_vel;
	}
	public float getXVel() {
		return y_vel;
	}
	public float getX() {
		return x;
	}
	public float getY() {
		return y;
	}
	public float getTileX() {
		return tilex;
	}
	public float getTileY() {
		return tiley;
	}
	public Image getBitmap() {
		return bitmap;
	}

}
