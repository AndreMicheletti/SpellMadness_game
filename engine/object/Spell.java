package game.engine.object;

import java.util.ArrayList;

import game.engine.Engine;
import game.engine.effects.AnimationFX;
import game.engine.effects.Light;
import game.engine.effects.Particle;
import game.engine.items.ItemSpell;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;

import game.engine.scene.SceneGame;
import game.main.ResourceLoader;

import static game.main.ResourceLoader.*;

/**
 * Classe que renderiza uma spell dentro do jogo
 * @author André Micheletti
 *
 */
public class Spell {
	
	public static int WIDTH = 16, HEIGHT = 16;
	
	private Character spellCaster = null;
	
	private boolean disposed = false;
	private Image bitmap = null;
	private Image bitmapParticle = null;
	
	public ArrayList<Particle> particles = new ArrayList<Particle>();
	
	private float x_vel, y_vel;
	private float x, y;
	
	private int rangeCovered = 0;
	
	private float scale = 2f;
	
	private ItemSpell spell = null;	
	public SceneGame onScene = null;
	
	private int particleSpawn = 1;
	private int particleTimer = 0;
	
	private Sound shootSound = null;
	
	public Spell(SceneGame scene, ItemSpell thisSpell, Character caster, float mouseX, float mouseY) {
		if (caster == null) { disposed = true; return; }
		if (thisSpell == null) { disposed = true; return; }
		try {
			
			onScene = scene;
			
			spell = thisSpell;
			particleSpawn = spell.particleSpawn;
			spellCaster = caster;
			
			x = caster.getCenterX();
			y = caster.getCenterY();
			
			calculateVelocities(spellCaster.getRealX(), spellCaster.getCenterY(), mouseX, mouseY);
			
			bitmap = getImage("particles/" + spell.spellBitmap + ".png");
			bitmapParticle = getImage("particles/" + spell.spellBitmap + "_particle.png");
			
			init();
			
		} catch (SlickException e) {
			e.printStackTrace();
			disposed = true;
		}
	}

	/**
	 * Inicializa a Spell
	 */
	public void init() {
		spawnParticle();
		try {
			shootSound = ResourceLoader.getSound(spell.shootSe);
			shootSound.playAt(1f, Engine.master_volume, x, y, 1f);
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Atualização Lógica da Spell
	 */
	public void update() {
		if (isDisposed()) return;
		x += x_vel;
		y += y_vel;
		if (particleTimer >= particleSpawn) {
			spawnParticle();
			particleTimer = 0;
		} else {
			particleTimer += 1;
		}		
		updateParticles();
		updateCollisions();
		rangeCovered += 1;
		if (rangeCovered >= spell.projectileRange) {
			dispose();
		}
	}
	
	/**
	 * Método de renderização da Spell
	 * @param g graficos da tela
	 */
	public void render(Graphics g) {
		if (isDisposed()) return;
		
		renderParticles(g);
		g.drawImage(bitmap, getRealX(), y);
		
		if (Engine.SHOW_FPS) {
			g.setColor(Color.red);
			g.draw(getBounds());
		}
	}
	
	public void updateParticles() {
		ArrayList<Particle> trash = new ArrayList<Particle>();
		for (Particle p : particles) {
			p.update();
			if (p.isDisposed()) trash.add(p);				
		}
		for (Particle p : trash) {
			particles.remove(p);
		}
	}
	
	public void renderParticles(Graphics g) {
		for (Particle p : particles) p.render(g);
		if (isDisposed()) {
			scale =  Math.max(scale - 0.085f , 0);
		}
		Light.drawLight(g, getRealX() + 5, y, spell.lightColor, scale);
	}
	
	private void updateCollisions() {
		if (isDisposed()) return;
		if (onScene.collidesWithMap(getBounds()) != null) collide();
	}
	
	public void collide() {
		try {
			shootSound.stop();
			Sound sound = ResourceLoader.getSound(spell.impactSe);
			sound.playAt(1f, Engine.master_volume, x, y, 1f);
			Animation a = new Animation(new SpriteSheet(getImage("animations/" + spell.splashSprite + ".png"), 32, 32), spell.splashFrames);
			onScene.addAnimationFX(
					new AnimationFX(a, "animations/" + spell.splashSprite + "_particle.png",
							x + 8, y + 8, spell.splashAmount, 40, spell.splashSpeed, onScene));
		} catch (SlickException e) {
			e.printStackTrace();
		}
		dispose();
		scale = 4f;
	}
	
	private void calculateVelocities(float px, float py, float mx, float my) {
		
		double xDistance = mx - px;
		double yDistance = my - py;
		
		double angle = -Math.toRadians(Math.toDegrees(Math.atan2(yDistance, xDistance)));;
		
        x_vel = (float)(spell.projectileSpeed * Math.cos(angle));
        y_vel = (float)(-spell.projectileSpeed * Math.sin(angle));
	}
	
	private void spawnParticle() {
		particles.add(new Particle(this, bitmapParticle, true));
	}
	
	public float getRealX() {
		return x - onScene.screenX;
	}	
	
	public float getX() {
		return x;
	}	
	
	public float getY() {
		return y;
	}
	
	public Rectangle getBounds() {
		return new Rectangle(getRealX()+3, y+3, WIDTH-6, HEIGHT-6);
	}
	
	public void dispose() {
		if (isDisposed()) return;
		disposed = true;
	}
	
	public boolean isDisposed() {
		return disposed;
	}
	
	public ItemSpell getItem() {
		return spell;
	}

}
