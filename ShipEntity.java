public class ShipEntity extends Entity{
	// game is here so that the entity knows data about the state
	// of the game, i think
	private gameManager game;
	public int shields = 100;
	
	public ShipEntity(gameManager g, String ref, int x, int y){	
		super(ref, (double)x,(double)y);
		this.game =g;
	}
	public void move(long delta){
		// dont fall off the edge
		if(dx < 0 && x <10){
			return;
		}
		//other side, add code for vertical later
		if (dx > 0 && x > 750){
			return;
		}
		super.move(delta);
	}
	
	// collidedWith
	// what does ths ship do if collision
	
	public void collidedWith(Entity other){
		if(other instanceof AlienEntity){
			//dead
			// make aliens shoot, so need to expand this to include shot
			game.notifyDeath();
		}
		if(other instanceof AlienBeam){
			if(shields <= 0){
				game.notifyDeath();
			}
		}
	}
}
