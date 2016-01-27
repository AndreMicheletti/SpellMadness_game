package game.engine.object;

import org.newdawn.slick.Graphics;

import game.engine.scene.SceneGame;

public class ExplosiveObject extends MapObject {

	public ExplosiveObject(SceneGame scene) {
		super(scene);
	}
	
	protected void init() {
		disposed = false;
	}

	protected void updateObject() {
		
	}

	public void render(Graphics g) {
		
	}

}
