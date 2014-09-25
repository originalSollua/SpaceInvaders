public class missle extends Entity{
        // adjust for varying sources
        // prolly using constructor
        private double movespeed = -150;
        private gameManager game;
        private boolean hit = false;
	Entity chase;

        public missle(gameManager g, double x, double y, Entity c){
                super("sprites/missle.png", x, y);
                this.game = g;
                dy = movespeed;
		this.chase = c;
        }

        public void move(long delta){
	//	System.out.println(this.x+ " " + this.y);
		if(chase !=null){
			this.x += (delta*(chase.x-this.x)/7)/1000;
			this.y += ((delta*dy))/1200;
		
		}
		else{
                	super.move(delta);
                	//check for off the top
		}
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

