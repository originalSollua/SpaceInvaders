// Space Invaders 
// taking a stab at a java based game
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Toolkit;
// this is the game manager. it will be responcible for maintaining the window.
// a new game will be constructed so that it builds the playing space on invocation of
// a new gameManager object.
//
// win conditions, death conditions, and monster slayage conditions defined here
// game loop defined here
// add later: pass parameters to the construction of the gameManager so that you can adjust 
// qualities of the game

// eventually, a loader class will be invoked to construct a gameManager. 
// the manager will in turn produce a level

public class gameManager extends Canvas{
	// here we will define many game specific variables
	
	// for accelerated page flipping
	private BufferStrategy strategy;
	// is the game running?
	private boolean gameRunning = true;
	// the list of all entities in the game
	// in version 2, this will be uipdated during the game loop to add more enimies
	private ArrayList entities = new ArrayList();
	// the list of things that are destroyed or no ,onger relevent;
	private ArrayList removeThese = new ArrayList();
	// the player data
	private Entity ship;
	private double moveSpeed = 300;
	private long lastFire = 0;
	private long fireRate = 500;
	// number of things to slay
	private int foeCount;
	
	private String message = "";
	private boolean waitingOnKeyPress = true;
	// control trackers?
	private boolean leftKeyPressed = false;
	private boolean rightKeyPressed = false;
	private boolean fireKeyPressed = false;
	// this becomes true if we need to update game state this loop
	private boolean logicThisLoop = false;
	
	// end private variables
	
	// begin construction
	public gameManager(){
		// create frame
		JFrame container = new JFrame("Space Invaders");
		// resoultion
		JPanel panel = (JPanel)container.getContentPane();
		panel.setPreferredSize(new Dimension(800, 600));
		panel.setLayout(null);
		
		setBounds(0,0,800,600);
		panel.add(this);
		
		
		// will most likely need a second pannel for UI in later versions
		
		// we wont repaint the whole thing at once
		// accelerated mode
		setIgnoreRepaint(true);
		// visiblify the wondow
		container.pack();
		container.setResizable(false);
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		container.setLocation((int)(d.width/2 - container.getSize().getWidth()/2),
					(int)(d.height/2 - container.getSize().getHeight()/2));

		container.setVisible(true);
		
		// enable wiondow closing
		container.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e){
				System.exit(0);
				}
				});
		// add key input system here, to respond to key press
		addKeyListener(new KeyInputHandler());
		// redirect key event focus
		requestFocus();
		
		// buffer strat so that awt can accelerate graphics
		createBufferStrategy(2);
		strategy = getBufferStrategy();
		
		// method call to set up all the active entities at the start
		initEntities();
	}
		// fresh game start
	private void startGame(){
		// clear old data
		entities.clear();
		initEntities();			
		// earase old keypresses
		leftKeyPressed = false;
		rightKeyPressed = false;
		fireKeyPressed = false;
	
	}
	// initialize starting entities
	private void initEntities(){
		// palyer entity
		// last param is starting point
		ship = new ShipEntity(this, "sprites/ship.gif", 370, 550);
		entities.add(ship);
			
		// now make starting enimies
		foeCount = 0;
		for(int i = 0; i < 5; i++){
			for(int j = 0; j < 12; j++){
				Entity foe = new AlienEntity(this, "sprites/alien.gif", 100+(j*50),(50)+i*30);
				entities.add(foe);
				foeCount++;
			}
		}
	}
		
	// notification that logic needs updating, in response to soem event
	public void updateLogic(){
		logicThisLoop = true;
	}
		
	public void removeEntity(Entity entity){
		removeThese.add(entity);
	}
	public void notifyDeath(){
		message = "dead. press any key to continue";
		waitingOnKeyPress = true;
	}
		
	public void notifyWin(){
		message = "Flawless victory";
		waitingOnKeyPress = true;
	}
		
	public void notifyAlienKilled(){
		foeCount--;
		if(foeCount <=0){
			notifyWin();
		}
		// remaining monsters more dificult
		for(int i = 0; i < entities.size(); i++){
			Entity entity = (Entity)entities.get(i);
			if(entity instanceof AlienEntity){
				entity.setHorizontalMovement(entity.getHorizontalMovement()*1.02);
			}
		}
	}
		
	// attempt to have the palyer fire a shot. modify this to be based off weapon type later
	public void tryToFire(){
		// check that fier CD is exhausted
		if(System.currentTimeMillis() - lastFire < fireRate){
			return;
		}
		lastFire = System.currentTimeMillis();
		ShotEntity shot = new ShotEntity(this, "sprites/shot.gif", ship.getX()+10, ship.getY()-30);
		entities.add(shot);
	}
		
	// primary game loop
	public void gameLoop(){
		long lastLoopTime = System.currentTimeMillis();
		
		while(gameRunning){
			long delta = System.currentTimeMillis() - lastLoopTime;
			lastLoopTime = System.currentTimeMillis();
				
			// blank screen
			Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
			// change to reflect the type of background?
			g.setColor(Color.black);
			g.fillRect(0,0,800,600);
				
			// move the things
			if(!waitingOnKeyPress){
				for(int i = 0; i < entities.size(); i++){
					Entity entity = (Entity)entities.get(i);
					entity.move(delta);
				}
			}
			// cycle through drawing entities
			for(int i = 0; i < entities.size(); i++){
				Entity entity = (Entity)entities.get(i);
				// incorporate the drawing method into the entity class
				entity.draw(g);
			}
			
			// collision check
			// brute force implementation
			// ponder a method that can do better
			// IE, create a list of entities that can potentially collide, then compare only those
			//	also check shots fired first?
			//	have to notify both entities that they weere collided
			for(int i = 0; i < entities.size(); i ++){
				for(int j = i+1; j < entities.size(); j++){
					Entity me = (Entity)entities.get(i);
					Entity him = (Entity)entities.get(j);
						
					if(me.collidesWith(him)){
						me.collidedWith(him);
						him.collidedWith(me);
					}
				}
			}
			// clear up anything that got collided
			// this is why we chucked all the the removes into an arraylist first
			if(!entities.isEmpty()){
				entities.removeAll(removeThese);
			}
			removeThese.clear();
			
			//logic check
			
			if(logicThisLoop){
				for(int i = 0; i < entities.size(); i++){
					Entity entity = (Entity)entities.get(i);
					entity.doLogic();
				}
				logicThisLoop = false;
			}
			// while waiting on input
				
			if(waitingOnKeyPress){
				g.setColor(Color.white);
				g.drawString(message,400-g.getFontMetrics().stringWidth(message)/2, 250);
				g.drawString("Press Any Key", 
					(400-g.getFontMetrics().stringWidth("Press Any Key")/2), 300);
			}
				
			// end of drawing, clean thing up
			g.dispose();
			strategy.show();
			
			//player movement
			ship.setHorizontalMovement(0);
			if(leftKeyPressed && !rightKeyPressed){
				ship.setHorizontalMovement(-moveSpeed);
			}
			else if(rightKeyPressed && !leftKeyPressed){
				ship.setHorizontalMovement(moveSpeed);
			}
			
			// attempt to fire
			if(fireKeyPressed){
				tryToFire();
			}
				
			// wait to control processor load
			// slopy implementation
			// look up better ways to do this
			try{
				Thread.sleep(10);
			}
			catch(Exception e){}
		}
	}
		
	private class KeyInputHandler extends KeyAdapter{
		private int keyPressCount = 1;
		public void keyPressed(KeyEvent e){	
			if (waitingOnKeyPress){
				return;
			}
			// this is where we are defineing the controls
			// as more are added, or changed, this is where that eventuallu
			// must be reflected
			if(e.getKeyCode() == KeyEvent.VK_LEFT){
				leftKeyPressed = true;
			}
			if(e.getKeyCode() == KeyEvent.VK_RIGHT){
				rightKeyPressed = true;
			}
			if(e.getKeyCode() == KeyEvent.VK_SPACE){
				fireKeyPressed = true;		
			}
		}
			
		// key released
		public void keyReleased(KeyEvent e){
			if(waitingOnKeyPress){
				return;
			}
				
			// as above, this is where the unpressed controls are delt with
			// one entry for every control
			if(e.getKeyCode() == KeyEvent.VK_LEFT){
				leftKeyPressed = false;
			}
			if(e.getKeyCode() == KeyEvent.VK_RIGHT){
				rightKeyPressed = false;
			}
			if(e.getKeyCode() == KeyEvent.VK_SPACE){
				fireKeyPressed = false;
			}
				
		}
			
		// key tapped
		public void keyTyped(KeyEvent e){
			if(waitingOnKeyPress){
				if(keyPressCount == 1){
					waitingOnKeyPress = false;
					startGame();
					keyPressCount = 0;
				}
				else{
					keyPressCount++;
				}
			}
			if(e.getKeyChar() == 27){
				System.exit(0);
			}
		}
	}
		
	// entry point of the game
	// modify this later so that we can run a level and ship loader
	// save state loader as well
	public static void main(String[]args){
		gameManager g = new gameManager();
		// use args here to specify how to build the level later
		// also pass in ship detaisl
		g.gameLoop();
	}
}	
