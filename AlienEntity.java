public class AlienEntity extends Entity{
	private double movespeed = 75;
	private gameManager game;
	
	public AlienEntity(gameManager g, String ref, int x, int y){
		super(ref,(double)x, (double)y);
		this.game = g;
		dx = -movespeed;
	}
	
	public void move(long delta){
		if(dx<0 && x < 10){
			game.updateLogic();
		}
		if(dx > 0 && x > 750){
			game.updateLogic();
		}
		super.move(delta);
	}
		
	public void doLogic(){
		dx = -dx;
		y += 10;
		if(y >570){
			game.notifyDeath();
		}
	}
	
	public void collidedWith(Entity other){
		// handle it
		// we check collisons else where, so this is redundant for now
		// if aliens hitting somehting maters specifically, do it here
	}
}
