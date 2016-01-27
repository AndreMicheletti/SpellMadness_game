package game.engine.ui;

import game.engine.Engine;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;

/**
 * Classe principal para qualquer elemento da User Interface
 * @author André Micheletti
 *
 */
public class UIObject {
	
	protected float x, y;
	protected int width, height;
	
	protected Image bitmap, mouseOver;
	
	protected boolean enabled = false;
	
	protected void create() {
		enabled = true;
	}
	
	public void update() {
		
	}
	
	public void render(Graphics g) {		
		if (isMouseOver() & enabled) {
			g.drawImage(mouseOver, x, y);
		} else {
			g.drawImage(bitmap, x, y);
		}
		
		if (Engine.SHOW_FPS) renderDebug(g);
	}
	
	public void renderDebug(Graphics g) {		

		Rectangle r = new Rectangle(this.x, this.y, this.width, this.height);
		g.setColor(Color.white);
		g.draw(r);
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void enable() {
		enabled = true;
	}
	
	public void disable() {
		enabled = false;
	}
	
	public boolean isMouseOver() {
		float mx = Engine.getInput().getMouseX();
		float my = Engine.getInput().getMouseY();
		Rectangle r = new Rectangle(this.x, this.y, this.width, this.height);
		return r.contains(mx, my);
		//return (getMouseX() > x & getMouseY() > y & getMouseX() <= x + width & getMouseY() <= y + height);
	}

}
