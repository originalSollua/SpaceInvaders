import java.awt.Graphics;
import java.awt.Rectangle;

public abstract class Entity{
	// data about the entity
	public double x;
	public double y;
	private Sprite sprite;
	//velocity
	public double dx;
	public double dy;
	// area for colision dectection
	private Rectangle me = new Rectangle();
	// other colision square, for doing science
	private Rectangle him = new Rectangle();
	
	// construct
	public Entity(String ref, double x, double y){
		this.sprite = SpriteStore.get().getSprite(ref);
		this.x = x;
		this.y = y;
	}
	
	// move method
	public void move(long delta){
		x+=(delta*dx) / 1000;
		y+=(delta*dy) / 1000;
	}
	
	// change horizontal speed modifyer
	public void setHorizontalMovement(double dx){
		this.dx = dx;
	}
	// veritcal
	public void setVerticalMovement(double dy){
		this.dy = dy;
	}
	//getters
	
	public double getHorizontalMovement(){
		return dx;
	}
	public double getVerticalMovement(){
		return dy;
	}
	
	public void draw(Graphics g){
		sprite.draw(g, (int)x, (int)y);
	}
	
	//do logic. does things that apparently are entity based and not implemented
	// maybe dodge, modify health, that sort of thing
	public void doLogic(){
	}
	
	public double getX(){
		return x;
	}
	public double getY(){
		return y;
	}
	// get the width of a sprite from its entity object.
	public double getWidth(){
		return sprite.getWidth();
	}
	
	//IMPORTANT COLLISION DETECTION HERE
	public boolean collidesWith(Entity other){
		me.setBounds((int)x, (int)y, sprite.getWidth(), sprite.getHight());
		him.setBounds((int)other.x, (int)other.y, other.sprite.getWidth(),other.sprite.getHight());
		
		return me.intersects(him);
	}
	// since each entity does something unique on collison
	// use abstract
	public abstract void collidedWith(Entity other);
}
