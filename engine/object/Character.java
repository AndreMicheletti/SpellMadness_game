package game.engine.object;

import game.engine.Engine;
import game.engine.enums.DieCause;
import game.engine.enums.Direction;
import game.engine.enums.GravityState;
import game.engine.items.ItemSpell;
import game.engine.scene.SceneGame;
import game.main.ResourceLoader;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.*;

/**
 * Classe base para qualquer personagem do jogo
 * @author André Micheletti
 *
 */
public abstract class Character {
	
	protected Image bitmap = null;
	protected Image lastbitmap = null;
	protected SceneGame onScene = null;
	
	protected boolean canShoot = true;
	
	protected boolean dead = false;
	
	protected boolean canCustomAnimate = true;
	
	public Color drawFlash = null;
	
	protected int drawFlashCounter = 0;
	
	protected Animation toDrawAnimation = null;
	protected Animation customAnimation = null;
	
	protected boolean customAnimating = false;
	
	protected Image stand, move, fall, die, melee;
	
	protected Direction dir = Direction.RIGHT;
	
	protected int tilex = 0, tiley = 0;
	protected float x = 0, y = 0;
	protected float y_vel = 0, x_vel = 0;
	
	protected int width = 32, height = 32;
	
	protected GravityState gState = GravityState.ON_AIR;
	
	protected ItemSpell equippedSpell = Engine.spellDatabase[0];
	protected int spellRecover = 0;
	protected int respawnTimer = 0;
	
	protected int maxHp = Engine.DEFAULT_CHAR_HP;
	protected int hp = Engine.DEFAULT_CHAR_HP;
	
	public Character(SceneGame s) {
		onScene = s;
		init();
	}
	
	protected void init() {
		
	}
	
	public void update(int delta) {
		lastbitmap = bitmap;
		
		if (dead) {
			updateDie(delta);
			return;
		}
		updateVerticalMovement();
		updateHorizontalMovement();
		
		// Atualização de Custom Animation
		if (customAnimating) {
			customAnimation.update(delta);
		}
		
		// Atualiza a animação quando no chão
		if (gState == GravityState.ON_GROUND) {
			updateOnGroundAnimation();
		// Atualiza a animação quando no ar
		} else {
			updateOnAirAnimation();
		}
		// Atualiza a recuperação de spells
		spellRecover = Math.max(0, spellRecover - 1);
		// FIM Update
	}
	
	protected void setToGround() {
		y_vel = 0;
		toDrawAnimation.setCurrentFrame(0);
		gState = GravityState.ON_GROUND;
	}
	
	protected void setToAir() {
		gState = GravityState.ON_AIR;
	}
	
	protected void updateVerticalMovement() {		
		
		if (gState == GravityState.ON_AIR) {
			
			// Se y_vel for 0, começa a cair
			if (y_vel == 0) {
				y_vel = 1;
			} else {
				// Se não, fazer valer a gravidade
				y_vel += getMyGravityAcc();
			}
			
			float lasty = y;
			
			// Muda o valor de y, fazendo mover
			y += y_vel;
			
			refreshPoints();
			
			// Se estiver caindo
			if (y_vel > 0) {
				// Checa as colisões em baixo
				int[] belowTileA = onScene.containWithMap(getGroundLineLeft());
				int[] belowTileB = onScene.containWithMap(getGroundLineRight());
				
				if (belowTileA != null) {				
					int ty = belowTileA[1];
					setToGround(); y = ty - height;
				}
				if (belowTileB != null) {
					int ty = belowTileB[1];
					setToGround(); y = ty - height;
				}		
			} else if (y_vel < 0) {
				// Se estiver pulando, checa as colisões em cima
				y_vel += Engine.GRAVITY_ACC;
				
				int[] aboveTile = onScene.collidesWithMap(getHorizontalCollideTop());
				if (aboveTile != null || onScene.getTileId(tilex, tiley) != 0) {
					y = lasty;
					y_vel = 0;
				}
			}
			
			applyFriction();
			
		} else if (gState == GravityState.ON_GROUND) {
			
			if (onScene.collidesWithMap(getGroundLineLeft()) == null & onScene.collidesWithMap(getGroundLineRight()) == null) {
				setToAir();
			}				
		}
		
		refreshPoints();
	}	
	
	protected void updateHorizontalMovement() {
		float beginx = x;
		if (x_vel > 0) {
			dir = Direction.RIGHT;
			x += x_vel;
			refreshPoints();
			
			int[] colTile = onScene.collidesWithMap(getHorizontalCollideMid());
			if (colTile != null) {
				if (colTile[0] > getRealX()) {
					float dif = 0.1f + Math.abs(colTile[0] - getHorizontalCollideMid().getMaxX());
					x -= dif; x_vel = 0;
				}
			}
		} else if (x_vel < 0) {
			dir = Direction.LEFT;
			x += x_vel;
			refreshPoints();
			
			int[] colTile = onScene.collidesWithMap(getHorizontalCollideMid());
			if (colTile != null) {
				if (colTile[0] < getRealX()) {
					float dif = 0.1f + Math.abs((colTile[0] + 32) - getHorizontalCollideMid().getX());
					x += dif; x_vel = 0;					
				}
			}
		}
		
		if (onScene.getTileId(tilex, tiley) != 0) {
			x = beginx; x_vel = 0;
		}
		
		refreshPoints();
	}
	
	protected void applyFriction() {
		if (gState == GravityState.ON_GROUND) {
			if (x_vel > 0) {
				x_vel -= Engine.FRICTION;
				if (x_vel < 0) x_vel = 0;
			} else if (x_vel < 0) {
				x_vel += Engine.FRICTION;
				if (x_vel > 0) x_vel = 0;
			}
		} else {
			if (x_vel > 0) {
				x_vel -= Engine.ACCELERATION / 3;
				if (x_vel < 0) x_vel = 0;
			} else if (x_vel < 0) {
				x_vel += Engine.ACCELERATION / 3;
				if (x_vel > 0) x_vel = 0;
			}
		}
	}

	public void render(Graphics g) {
		int anim_duration = 1;
		if (customAnimating == true) {
			if (customAnimation != null) {
				if (customAnimation.isStopped()) {
					removeCustomAnimation();
				} else {
					if (dir == Direction.LEFT) {
						customAnimation.draw(getRealX() + 32, y, - width, height);			
					} else {
						customAnimation.draw(getRealX(), y);
					}
					return;
				}
			} else {
				removeCustomAnimation();
			}	
		}
		
		if (gState == GravityState.ON_AIR) {
			bitmap = fall;
		} else {
			anim_duration = 4;
			bitmap = (x_vel == 0 ? stand : move);
		}
		
		if (lastbitmap != bitmap) {
			toDrawAnimation = new Animation(new SpriteSheet(bitmap, 32, 32), anim_duration);
			toDrawAnimation.setSpeed(0.06f);
			toDrawAnimation.setAutoUpdate(true);
			lastbitmap = bitmap;
		}
		
		if (!dead) {
			if (dir == Direction.LEFT) {
				if (drawFlash != null)
					toDrawAnimation.drawFlash(getRealX() + 32, y, -width, height, drawFlash);
				else
					toDrawAnimation.draw(getRealX() + 32, y, -width, height);
			} else {
				if (drawFlash != null)
					toDrawAnimation.drawFlash(getRealX(), y, width, height, drawFlash);	
				else
					toDrawAnimation.draw(getRealX(), y, width, height);	
			}
		}
		drawFlash = null;
		
		if (Engine.SHOW_FPS == true) drawDebug(g);
		
	}
	
	protected void drawDebug(Graphics g) {
		g.setColor(Color.green);
		g.draw(getHorizontalCollideMid());
		g.draw(getHorizontalCollideTop());
		g.setColor(Color.yellow);
		g.draw(getGroundLineLeft());
		g.draw(getGroundLineRight());
		g.setColor(Color.blue);
		g.draw(getBoundRectangle());
	}
	
	protected void updateOnGroundAnimation() {
		if (x_vel != 0) {
			toDrawAnimation.setAutoUpdate(true);
			//if (gState == GravityState.ON_GROUND) toDrawAnimation.update(delta);
		} else {
			toDrawAnimation.setAutoUpdate(false);
			toDrawAnimation.setCurrentFrame(0);
		}
	}
	
	protected void shoot() {
		if (canShoot() == false) return;
		if (equippedSpell == null) return;
		if (spellRecover > 0) return;
		
		Spell s = new Spell(onScene, equippedSpell, this, Engine.getInput().getMouseX(), Engine.getInput().getMouseY());
		onScene.addSpell(s);
		spellRecover = equippedSpell.cooldown;
	}	

	protected void melee() {
		if (canShoot() == false) return;
		if (spellRecover > 0) return;
		if (customAnimating) return;
		
		//if (gState == GravityState.ON_GROUND) 
			Animation anim = new Animation(new SpriteSheet(melee, 32, 32), 4);
			customAnimate(anim, 0.08f);
		//}
		
		spellRecover = 20;
	}
	
	
	protected float getMyGravityAcc() {
		return Engine.GRAVITY_ACC;
	}
	
	protected void updateOnAirAnimation() {
		
	}
	
	protected void customAnimate(Animation anim, float speed) {
		customAnimation = anim;
		customAnimating = true;
		customAnimation.setAutoUpdate(false);
		customAnimation.setLooping(false);
		customAnimation.setSpeed(speed);
		customAnimation.start();
	}
	
	protected void removeCustomAnimation() {
		customAnimating = false;
		customAnimation = null;
	} 
	
	public void moveTo(int tx, int ty) {
		tilex = tx;
		tiley = ty;
		x = 32 * tx;
		y = 32 * ty;
	}	
	
	protected void refreshPoints() {
		tilex = Math.round(getCenterX()) / 32;
		tiley = Math.round(getCenterY()) / 32;
		if (tiley > 19) {
			die(DieCause.FALL);
		}
	}
	
	protected void updateDie(int delta) {
		if (dead == false) return;
		
		updateVerticalMovement();
		updateHorizontalMovement();
		
		// Custom animation Update
		if (customAnimating) {
			customAnimation.update(delta);
		}
		
		if (respawnTimer > 0) {
			respawnTimer -= 1;
		} else {
			respawnTimer = 0;
			respawn();
		}
	}
	
	protected void die(DieCause cause) {
		if (dead) return;
		hp = 0;
		try {
			String dieSe = Engine.SPELLDIE_SOUND;
			if (cause == DieCause.FALL) {
				dieSe = Engine.FALL_SOUND;
				y_vel = -5F;
			}
			
			if (cause == DieCause.TIME_OVER)
				dieSe = Engine.TIMEOVER_SOUND;
			
			Animation anim = new Animation(new SpriteSheet(die, 32, 32), 8);
			customAnimate(anim, 0.04f);
			
			ResourceLoader.getSound(dieSe).play(1f, Engine.master_volume);
			respawnTimer = getRespawnTime();
			dead = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void damage(int qnt) {
		hp -= qnt;
		if (hp <= 0) {
			hp = 0;
			die(DieCause.SPELL);
		}
	}	
	
	protected void respawn() {
		dead = false;
	}
	
	public Image getBitmap() {
		return bitmap;
	}
	
	public void setBitmap(String file) throws SlickException {
		bitmap = ResourceLoader.getImage(file);
	}
	
	protected Line getHorizontalCollideMid() {
		return new Line(getRealX(), y+(height/2)+4, getRealX()+width, y+(height/2)+4);
	}	
	protected Line getHorizontalCollideTop() {
		return new Line(getRealX(), y+9, getRealX()+width, y+9);
	}	
	
	protected Line getGroundLineLeft() {
		return new Line(getRealX()+10, y+height-4, getRealX()+10, y+height+6);		
	}
	protected Line getGroundLineRight() {
		return new Line(getRealX()+26, y+height-4, getRealX()+26, y+height+6);	
	}
	
	public float getCenterX() {
		return (x + 16);
	}
	
	public float getCenterY() {
		return (y + 16);
	}
	
	public int getTileX() {
		return tilex;
	}
	
	public int getTileY() {
		return tiley;
	}
	
	public float getRealX() {
		return x - onScene.screenX;
	}
	
	public float getXVel() {
		return x_vel;
	}
	
	public float getX() {
		return x;
	}
	
	public ItemSpell getEquippedSpell() {
		return equippedSpell;
	}
	
	protected Rectangle getBoundRectangle() {
		return new Rectangle(32*tilex - onScene.screenX, 32*tiley, 32, 32);
	}	
	
	public boolean canShoot() {
		return canShoot;
	}
	
	protected int getRespawnTime() {
		return Engine.DEFAULT_SPAWNTIME;
	}
	
	public int getHp() {
		return hp;
	}
	
	public int getMaxHp() {
		return maxHp;
	}
	
	public GravityState getGState() {
		return gState;
	}
	
	public Direction getDir() {
		return dir;
	}
	
	public float getYVel() {
		return y_vel;
	}

}
