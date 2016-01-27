package game.engine.items;

import org.newdawn.slick.Color;

/**
 * Classe que determina os parâmetros de uma spell
 * @author André Micheletti
 *
 */
public class ItemSpell {

	public String spellBitmap = "";
	
	public int damage = 0;
	public int cooldown = 0;
	
	public float projectileSpeed = 0;
	public float projectileRange = 0;
	
	public int particleSpawn = 1;
	public int particleLife = 30;
	
	public String splashSprite = "";
	public int splashFrames = 0;
	public float splashSpeed = 0;
	public int splashAmount = 0;
	
	public String shootSe = "";
	public String impactSe = "";
	
	public Color lightColor = new Color(1f,1f,1f,0.8f);
	
	public ItemSpell(String bitmap, int dmg, int cd, float speed, float range,
			int particleSpawnTime, int particleLifeTime, String splash, int frames, float sSpeed, int sAmount,
			String shootSE, String impactSE, Color l_color) {
		spellBitmap = bitmap;
		damage = dmg;
		cooldown = cd;
		projectileSpeed = speed;
		projectileRange = range;
		particleSpawn = particleSpawnTime;
		particleLife = particleLifeTime;
		splashFrames = frames;
		splashSprite = splash;
		splashSpeed = sSpeed;		
		shootSe = shootSE;
		splashAmount = sAmount;
		impactSe = impactSE;
		lightColor = l_color;
	}
	
}
