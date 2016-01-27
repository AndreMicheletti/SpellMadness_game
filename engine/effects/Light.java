package game.engine.effects;

import game.main.ResourceLoader;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class Light {
	
	public static String DEFAULT_LIGHT = "particles/light1.bmp";
	public static Image DEFAULT_BITMAP = null;
	
	private static int w = 0, h = 0;
	
	public static void preloadImage() {
		try {
			DEFAULT_BITMAP = ResourceLoader.getImage(DEFAULT_LIGHT);
			w = DEFAULT_BITMAP.getWidth();
			h = DEFAULT_BITMAP.getHeight();
		} catch (SlickException e) {
			System.out.println("ERRO AO PRECARREGAR A IMAGEM DE LUZ");
			e.printStackTrace();
		}
	}
	
	public static void drawLight(Graphics g, float x, float y) {
		g.setDrawMode(Graphics.MODE_ADD);
		
		g.drawImage(DEFAULT_BITMAP, x - (w / 2), y - (h / 2));
		
		g.setDrawMode(Graphics.MODE_NORMAL);
		g.flush();
	}
	
	public static void drawLight(Graphics g, float x, float y, Color c) {
		g.setDrawMode(Graphics.MODE_ADD);

		g.drawImage(DEFAULT_BITMAP, x - (w / 2), y - (h / 2), c);
		
		g.setDrawMode(Graphics.MODE_NORMAL);
		g.flush();
	}
	
	public static void drawLight(Graphics g, float x, float y, Color c, float scale) {
		g.setDrawMode(Graphics.MODE_ADD);
		
		Image bitmap = DEFAULT_BITMAP.copy();
		
		float ww = w * scale;
		float hh = h * scale;
		
		bitmap.draw(x - (ww / 2.0f), y - (hh / 2.0f), scale, c);
		
		g.setDrawMode(Graphics.MODE_NORMAL);
		g.flush();
	}
}
