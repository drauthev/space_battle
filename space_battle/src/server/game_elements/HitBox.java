package server.game_elements;

public class HitBox{
	
	private int width, heigth;
	GameElement owner;
	
	// K�?‰RDÅ�JEL::: HA PARAM�?‰TERK�?‰NT �?�TADOK EGY OJJEKTUMOT, M�?�SOLAT K�?‰SZ�?œL, VAGY REFERENCIA?
	HitBox(int width, int heigth, GameElement owner){
		this.setWidth(width);
		this.setHeigth(heigth);
		this.owner = owner;
	}
		
	public boolean isCollision(GameElement otherElement){
		// TODO, hat�?©konyabb elj�?¡r�?¡st csin�?¡lni..
		int thisX = owner.getCoordX() - width/2;	// norm�?¡l�?¡s a bal felsÅ�? sarokba, onnan indul majd a ciklus
		int thisY = owner.getCoordY() + heigth/2;
		int otherX = otherElement.getCoordX() - width/2;
		int otherY = otherElement.getCoordY() + heigth/2;

		for(int i=thisX; i < thisX+width; i++){	// hat�?¡rokat MEGGONDOLNI
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