package game.engine.ui;

import static game.main.ResourceLoader.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import game.engine.Engine;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

/**
 * Classe que define um botão de User Interface
 * @author André Micheletti
 *
 */
public class UIButton extends UIObject {
	
	private ActionListener btnAction = null;
	
	public UIButton(String bit, String over, float x, float y, ActionListener action) {
		try {
			
			bitmap = getImage(bit);
			
			mouseOver = getImage(over);
			
			this.x = x;
			this.y = y;
			
			this.width = bitmap.getWidth();
			this.height = bitmap.getHeight();
			
			this.btnAction = action;
			
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	
	public void update() {
		if (enabled == false) return;
		if (btnAction == null) return;
		if (isMouseOver())
			if (Engine.getInput().isMousePressed(0))
				btnAction.actionPerformed(new ActionEvent(this, 1, "action"));
	}
	
	public void renderCustom(Graphics g, Color c) {		
		if (isMouseOver() & enabled) {
			g.drawImage(mouseOver, x, y, c);
		} else {
			g.drawImage(bitmap, x, y, c);
		}
		
		if (Engine.SHOW_FPS) {
			renderDebug(g);
		}
	}

}
