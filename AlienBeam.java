public class AlienBeam extends Entity{
        // adjust for varying sources
        // prolly using constructor
        private double movespeed = 300;
        private gameManager game;
        private boolean hit = false;
	public int damage = 10;
        public AlienBeam(gameManager g, double x, double y){
                super("sprites/shot.gif", x, y);
                this.game = g;
                dy = movespeed;
        }

        public void move(long delta){
                super.move(delta);
                //check for off the top
                if(y > 600){
                        game.removeEntity(this);
                }
        }
        public void collidedWith(Entity other){
                if(hit){
                        return;
                }
                if(other instanceof ShipEntity){
                       ShipEntity t = (ShipEntity)other;
			t.shields = t.shields - damage;
			System.out.println(t.shields);
			game.removeEntity(this);
                        hit = true;
                }
        }
}

