package server.game_elements;

public class HitBox{
	
	private int width, heigth;
	GameElement owner;
	
	// KÃ?â€°RDÃ…ï¿½JEL::: HA PARAMÃ?â€°TERKÃ?â€°NT Ã?ï¿½TADOK EGY OJJEKTUMOT, MÃ?ï¿½SOLAT KÃ?â€°SZÃ?Å“L, VAGY REFERENCIA?
	HitBox(int width, int heigth, GameElement owner){
		this.setWidth(width);
		this.setHeigth(heigth);
		this.owner = owner;
	}
		
	public boolean isCollision(GameElement otherElement){
		// TODO, hatÃ?Â©konyabb eljÃ?Â¡rÃ?Â¡st csinÃ?Â¡lni..
		int thisX = owner.getCoordX() - width/2;	// normÃ?Â¡lÃ?Â¡s a bal felsÃ…â€? sarokba, onnan indul majd a ciklus
		int thisY = owner.getCoordY() + heigth/2;
		int otherX = otherElement.getCoordX() - width/2;
		int otherY = otherElement.getCoordY() + heigth/2;

		for(int i=thisX; i < thisX+width; i++){	// hatÃ?Â¡rokat MEGGONDOLNI
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