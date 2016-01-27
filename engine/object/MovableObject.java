package game.engine.object;

import org.newdawn.slick.Graphics;

import game.engine.scene.SceneGame;

public class MovableObject extends MapObject {

	public MovableObject(SceneGame scene) {
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
