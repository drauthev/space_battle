package space_battle.server.game_elements;

/**
 * Hitbox of {@link NPC} classes and the {@link Player} class. 
 * @author daniel.szeifert
 * @version 1.0
 * @since 2014-05-17
 */
public class HitBox{
	
	private int width, heigth;
	GameElement owner;
	
	/**
	 * Constructor
	 * @param width Latitude on the X-axis
	 * @param heigth Latitude on the Y-axis
	 * @param owner Reference to the class owning the hitbox.
	 */
	HitBox(int width, int heigth, GameElement owner){
		this.setWidth(width);
		this.setHeigth(heigth);
		this.owner = owner;
	}
	
	/**
	 * Checks if there is an overlay between two game element's coordinates.
	 * @param otherElement reference to the other game element, with which the collision is checked.
	 * @return true, if there is an overlay of the two objects. false otherwise
	 */
	public boolean isCollision(GameElement otherElement){
		// TODO: hatekonyabb eljaras..
		int thisX = (int) (owner.getCoordX() - width/2);	// normalas a bal felso sarokba, onnan indul majd a ciklus
		int thisY = (int) (owner.getCoordY() + heigth/2);
		int otherX = (int) (otherElement.getCoordX() - width/2);
		int otherY = (int) (otherElement.getCoordY() + heigth/2);

		for(int i=thisX; i < thisX+width; i++){	// hatarokat meggondolni
			for(int j=otherX; j<otherX+width; j++){
				if(i==j){
					for(int k=thisY; k > thisY-heigth; k--){
						for(int l=otherY; l > otherY-heigth; l--){
							if(k==l) return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	// Getters, setters
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeigth() {
		return heigth;
	}

	public void setHeigth(int heigth) {
		this.heigth = heigth;
	}

	
}