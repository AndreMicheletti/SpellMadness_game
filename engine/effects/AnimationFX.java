package game.engine.effects;

import java.util.ArrayList;
import java.util.Random;

import game.engine.scene.SceneGame;
import game.main.ResourceLoader;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

/**
 * Classe que cria uma animação em frames com particulas
 * @author André Micheletti
 *
 */
public class AnimationFX {
	
	public static int WIDTH = 32, HEIGHT = 32;
	
	public Animation animation = null;
	
	private Image particleBit = null;
	private boolean disposed = false;
	
	private SceneGame onScene = null;

	public ArrayList<ParticleFX> particles = new ArrayList<ParticleFX>();
	
	private float rx, ry;
	
	public AnimationFX(Animation anim, String particleName, float x, float y,
			int particleNumber, int particleLife, float animSpeed, SceneGame scene) {
		try {
			
			particleBit = ResourceLoader.getImage(particleName);
			
			rx = x; ry = y;
			
			onScene = scene;
			
			createParticles(particleNumber, particleLife);
			
			this.animation = anim;
			animation.setLooping(false);
			animation.setAutoUpdate(true);
			animation.setCurrentFrame(0);
			animation.setSpeed(animSpeed);
			animation.start();
			
		} catch (Exception e ){
			e.printStackTrace();
			dispose();
		}
	}
	
	private void createParticles(int n, int life) {
		float sx = rx;
		float sy = ry;
		for (int i = 0; i < n; i ++) {
			float x_vel = 1.2f - new Random().nextFloat() * 2;
			float y_vel = 1.2f - new Random().nextFloat() * 2;
			particles.add(new ParticleFX(particleBit, sx, sy, x_vel, y_vel, life, true, onScene));
		}
	}

	public void update(int delta) {
		if (isDisposed()) return;
		
		updateParticles();
		if (particles.size() == 0) {
			dispose();
		}
		
	}

	public void render(Graphics g) {
		if (isDisposed()) return;
		
		if (!animation.isStopped()) {
			animation.draw(rx - onScene.screenX - 16, ry - 16);
		}
		renderParticles(g);
	}
	
	public void updateParticles() {
		ArrayList<ParticleFX> trash = new ArrayList<ParticleFX>();
		for (ParticleFX p : particles) {
			p.update();
			if (p.isDisposed()) trash.add(p);				
		}
		for (ParticleFX p : trash) {
			particles.remove(p);
		}
	}
	
	public void renderParticles(Graphics g) {
		for (ParticleFX p : particles) {
			p.render(g);
		}	
	}
	
	public void dispose() {
		if (isDisposed()) return;
		disposed = true;
	}
	
	public boolean isDisposed() {
		return disposed;
	}

}
