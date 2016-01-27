package game.engine.enums;

/**
 * Tipos de Spell
 * @author André Micheletti
 *
 */
public enum SpellType {
	
	FIRE, ICE, SHOCK, ARCANE, LIGHT, HEAL, WIND, PUSH;
	
	public boolean classCanUse(MageClass c) {
		switch (c) {
		case BLACK:
			return (this.equals(FIRE) | this.equals(ICE));
		case BLUE:
			return (this.equals(SHOCK) | this.equals(ARCANE));
		case WHITE:
			return (this.equals(LIGHT) | this.equals(HEAL));
		case RED:
			return (this.equals(WIND) | this.equals(PUSH));
		case NO_CLASS:
			return true;
		}
		return false;
	}
	
	/**
	 * Transforma o Enum em uma String
	 * @return a string que representa o enum
	 */
	public String toString() {
		if (this.equals(FIRE))
			return "fire";
		if (this.equals(ICE))
			return "ice";
		if (this.equals(SHOCK))
			return "shock";
		if (this.equals(ARCANE))
			return "arcane";
		if (this.equals(LIGHT))
			return "light";
		if (this.equals(HEAL))
			return "heal";
		if (this.equals(WIND))
			return "wind";
		if (this.equals(PUSH))
			return "push";
		
		return "fire";
	}

}
