public class ShotEntity extends Entity{
	// adjust for varying sources
	// prolly using constructor
	private double movespeed = -300;
	private gameManager game;
	private boolean hit = false;

	public ShotEntity(gameManager g, double x, double y){
		super("sprites/shot.gif", x, y);
		this.game = g;
		dy = movespeed;
	}
	
	public void move(long delta){
		super.move(delta);
		//check for off the top
		if(y < -100){
			game.removeEntity(this);
		}
	}
	public void collidedWith(Entity other){
		if(hit){
			return;
		}
		if(other instanceof AlienEntity){
			game.removeEntity(this);
			game.removeEntity(other);
			game.notifyAlienKilled();
			hit = true;
		}
	}
}
