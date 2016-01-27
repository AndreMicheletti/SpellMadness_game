package game.engine.object;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Line;

import game.engine.Engine;
import game.engine.enums.Direction;
import game.engine.enums.GravityState;
import game.engine.enums.MageClass;
import game.engine.scene.SceneGame;
import static game.main.ResourceLoader.*;

/**
 * Classe que renderiza e controla o jogador
 * @author André Micheletti
 *
 */
public class Player extends Character {

	public boolean inputEnabled = true;
	private boolean wallSliding = false;
	
	private Image slide;
	private Image roll;
	
	private int jumpMax = 1;
	
	public MageClass mClass;

	private int rollRecover = 0;
	private int jumpRecover = 0;
	private int jumpCount = 0;

	public Player(SceneGame s, MageClass clas) {
		super(s);
		mClass = clas;
		initialize();
	}
	
	protected void initialize() {
		String folder = "player/" + mClass.toString() + "/";
		try {
			stand = getImage(folder + "stand.png");
			
			fall = getImage(folder + "fall.png");
			
			move = getImage(folder + "moving.png");
			
			slide = getImage(folder + "slide.png");
			
			roll = getImage(folder + "roll.png");

			die = getImage(folder + "die.png");

			melee = getImage(folder + "melee.png");	
			
			equippedSpell = Engine.spellDatabase[0];
			
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	
	public void update(int delta) {
		super.update(delta);
		if (dead) return;
		
		// Update WallSliding State
		if (wallSliding) {
			jumpCount = 0;
			y_vel = Math.min(y_vel, 0.75f);
		}
		// Update Jump Recover
		jumpRecover = Math.max(0, jumpRecover - 1);
		// Update Roll Recover
		rollRecover = Math.max(0, rollRecover - 1);
		// Update Inputs
		if (inputEnabled) updateInputs();
	}

	private void updateInputs() {
		if (Engine.getInput().isKeyPressed(Input.KEY_ESCAPE)) {
			onScene.pauseGame();
		}
		if (Engine.getInput().isKeyPressed(Input.KEY_W)) {
			jump();
		}
		if (Engine.getInput().isKeyPressed(Input.KEY_SPACE)) {
			roll();
		}
		if (Engine.getInput().isKeyPressed(Input.KEY_1)) {
			equippedSpell = Engine.spellDatabase[0];
		}
		if (Engine.getInput().isKeyPressed(Input.KEY_2)) {
			equippedSpell = Engine.spellDatabase[1];
		}
		if (Engine.getInput().isKeyPressed(Input.KEY_3)) {
			equippedSpell = Engine.spellDatabase[2];
		}
		if (Engine.getInput().isKeyPressed(Input.KEY_4)) {
			equippedSpell = Engine.spellDatabase[3];
		}
		if (canShoot()) {
			if (Engine.getInput().isMousePressed(0)) {
				shoot();
			}
			if (Engine.getInput().isMousePressed(1)) {
				damage(10);
				melee();
			}
		}
		if (customAnimating) return;
		boolean moving = false;
		if (Engine.getInput().isKeyDown(Input.KEY_D)) {
			left();
			moving = true;
		}
		if (Engine.getInput().isKeyDown(Input.KEY_A)) {
			right();
			moving = true;
		}
		if (!moving) {
			applyFriction();
			wallSliding = false;
		}
	}

	private void right() {
		x_vel -= Engine.ACCELERATION;
		if (x_vel < -4) x_vel = -4;
	}

	private void left() {
		x_vel += Engine.ACCELERATION;
		if (x_vel > 4) x_vel = 4;
	}

	private void jump() {
		//if (gState == GravityState.ON_GROUND) {
		if (jumpRecover == 0 && jumpCount < jumpMax) {
			gState = GravityState.ON_AIR;
			y_vel = -7.5f;
			jumpRecover = Engine.JUMP_RECOVER;
			jumpCount += 1;
			if (wallSliding) {
				if (Engine.getInput().isKeyDown(Input.KEY_D)) {
					x_vel -= 3.2f;
				}
				if (Engine.getInput().isKeyDown(Input.KEY_A)) {
					x_vel += 3.2f;
				}
			} 
		}
	}
	
	protected void setToGround() {
		super.setToGround();
		jumpCount = 0;
	}
	
	protected void updateOnAirAnimation() {
		if (jumpCount == jumpMax) {
			toDrawAnimation.setAutoUpdate(true);
		} else {
			toDrawAnimation.setAutoUpdate(false);
			toDrawAnimation.setCurrentFrame(0);
		}
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
					x -= dif; x_vel = 0; wallSliding = true;
				}
			} else {
				wallSliding = false;
			}
		} else if (x_vel < 0) {
			dir = Direction.LEFT;
			x += x_vel;
			refreshPoints();
			
			int[] colTile = onScene.collidesWithMap(getHorizontalCollideMid());
			if (colTile != null) {
				if (colTile[0] < getRealX()) {
					float dif = 0.1f + Math.abs((colTile[0] + 32) - getHorizontalCollideMid().getX());
					x += dif; x_vel = 0; wallSliding = true;				
				}
			} else {
				wallSliding = false;
			}
		}
		
		refreshPoints();
		
		if (gState == GravityState.ON_GROUND)
			wallSliding = false;
		
		if (onScene.getTileId(tilex, tiley) != 0) {
			x = beginx; x_vel = 0;
		}
		
		refreshPoints();
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
			if (wallSliding == true) {
				bitmap = slide;
			} else {
				bitmap = fall;
			}			
		} else {	
			anim_duration = 4;
			bitmap = (x_vel == 0 ? stand : move);
		}
		
		if (lastbitmap != bitmap) {
			toDrawAnimation = new Animation(new SpriteSheet(bitmap, 32, 32), anim_duration);
			toDrawAnimation.setSpeed(0.045f);
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

	private void roll() {
		if (customAnimating) return;
		if (rollRecover != 0) return;
		if (wallSliding) return;
		
		float sp = 5.2f;
		
		if (Engine.getInput().isKeyDown(Input.KEY_D)) {
			x_vel = sp;
			dir = Direction.RIGHT;
		} else if (Engine.getInput().isKeyDown(Input.KEY_A)) {
			x_vel = -sp;
			dir = Direction.LEFT;
		} else {
			if (dir == Direction.LEFT)
				x_vel = -sp;
			else
				x_vel = sp;
		}
		
		Animation anim = new Animation(new SpriteSheet(roll, 32, 32), 8);
		customAnimate(anim, 0.2f);
		rollRecover = Engine.ROLL_RECOVER;
	}
	
	protected void respawn() {
		int px = Integer.parseInt(onScene.sceneMap.getMapProperty("playerSpawn", "3,3").split(",")[0]);
		int py = Integer.parseInt(onScene.sceneMap.getMapProperty("playerSpawn", "3,3").split(",")[1]);

		moveTo(px, py);
		y_vel = 0; x_vel = 0;
		hp = maxHp;
		dead = false;
		
	}
	
	public boolean isWallSliding() {
		return wallSliding;
	}
	
	protected boolean isMoving() {
		return (x_vel != 0);
	}
	
	protected Line getHorizontalCollideMid() {
		return new Line(getRealX(), y+(height/2)+4, getRealX()+width, y+(height/2)+4);
	}
	
	protected Line getHorizontalCollideTop() {
		return new Line(getRealX()+2, y+9, getRealX()+width-2, y+9);
	}
	
	protected Line getGroundLineLeft() {
		return new Line(getRealX()+10, y+height-4, getRealX()+10, y+height+6);		
	}
	protected Line getGroundLineRight() {
		return new Line(getRealX()+26, y+height-4, getRealX()+26, y+height+6);	
	}

}
