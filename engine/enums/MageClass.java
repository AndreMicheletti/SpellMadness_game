package game.engine.enums;

/**
 * Classes do jogador
 * @author André Micheletti
 *
 */
public enum MageClass {
	
	BLACK,
	
	WHITE,
	
	BLUE,
	
	RED,
	
	NO_CLASS;
	
	public String toString() {
		if (this.equals(BLACK))
			return "black";
		if (this.equals(BLUE))
			return "blue";
		if (this.equals(WHITE))
			return "white";
		if (this.equals(RED))
			return "red";
		return "blue";
	}

}
