package game.engine.effects;

import java.util.Random;

import game.engine.scene.SceneGame;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class LightFX {
	
	public int tileX, tileY;
	
	public float offsetX = 0, offsetY = 0, scale = 1f, true_scale;
	
	public LightType type = LightType.FIRE;
	
	private SceneGame onScene = null;
	
	private boolean disposed = false, nextUpd = false;
	
	
	public LightFX(SceneGame scene, int tilex, int tiley, float ofx, float ofy, float s, String light_type) {
		onScene = scene;
		tileX = tilex;
		tileY = tiley;
		offsetX = ofx;
		offsetY = ofy;
		
		switch (light_type.toLowerCase()) {
		case "fire":
			type = LightType.FIRE;
			break;
		default:
			type = LightType.FIRE;
			break;				
		}
		
		scale = s;
	}
	
	public void update() {
		if (isDisposed()) return;
	}
	
	public void render(Graphics g) {
		if (isDisposed()) return;
		if (nextUpd) {
			true_scale = scale - (new Random().nextFloat() * 0.5f);
			nextUpd = false;
		} else
			nextUpd = true;
		
		Light.drawLight(g, (tileX * 32) + offsetX - onScene.screenX, (tileY * 32) + offsetY, type.getColor(), true_scale);
		
	}
	
	public void dispose() {
		disposed = true;
	}
	
	public boolean isDisposed() {
		return disposed;
	}
	
	public enum LightType {
		
		FIRE;
		
		public Color getColor() {
			switch (this) {
			case FIRE:
				return new Color(210, 98, 4, 170);
			}
			return new Color(1f, 1f, 1f, 0.5f);
		}
	}

}
