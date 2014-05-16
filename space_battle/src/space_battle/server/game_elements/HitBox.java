package space_battle.server.game_elements;

public class HitBox{
	
	private int width, heigth;
	GameElement owner;
	
	
	HitBox(int width, int heigth, GameElement owner){
		this.setWidth(width);
		this.setHeigth(heigth);
		this.owner = owner;
	}
		
	public boolean isCollision(GameElement otherElement){
		// TODO, hatekonyabb eljaras..
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