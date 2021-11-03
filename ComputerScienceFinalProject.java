/*
Lindsey Ferguson & Siovan Gecelovsky (handed in her project seperately)
ICS4U-01
Final Project- Operation Kirby
For our final project we decided to do a classic rendition of Kirby's Adventure.
Special features include:
	*kirby: walking god <(* *<)
	*controled by keyboard, no mouse inputs needed
	*side scoller game
	*ability to inahle enemies and gain power, or projectiles
	*with the player controlling Kirby. Kirby can walk left/right, fly, inhale enemies, throw the enemies back
 	*as ammo, and gain the powers from certain enemies (POPPY BRO). There is an intro screen,
 	*then the main levels, and a menu if you press the M KEY. The enemies each have different behaviours.
 	*there are 3 main enemies, 2 minor enemies that kirby can interact with 
*/
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.imageio.*;
import java.awt.image.*; 
import java.io.*; 
import java.util.ArrayList;
import java.io.File; 
import java.io.IOException; 
import java.util.Scanner; 
  
public class ComputerScienceFinalProject extends JFrame{
	Timer myTimer;   
	GamePanel game;
	boolean reset=false;
	ComputerScienceFinalProject current;

    public ComputerScienceFinalProject() {
    	super("KIRBY");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		myTimer = new Timer(15, new TickListener());	
		start();
		current = this;
		game = new GamePanel(this);
		add(game);
		pack();
		setResizable(false);
		setVisible(true);
    }
    
    public void start(){
    	myTimer.start();
    }
    
    class TickListener implements ActionListener{
		public void actionPerformed(ActionEvent evt){
			if(game!= null && game.ready){
				game.move(); //these allow for kirby and the enemy to move
				game.WaddleDeeMove();
				game.PoppyBroMove();
				game.ScarfyMove();
				game.GrizzoMove();
				game.SpikeMove();
				game.decoMove();
				game.repaint();
			}
			if(reset){
				reset = false;
				remove(game);
				game = new GamePanel(current);
				add(game);
				pack();
			}		
		}
	}

	public static void main(String[] args){//arguments
		ComputerScienceFinalProject frame = new ComputerScienceFinalProject();		
    }
}

class GamePanel extends JPanel{
	ComputerScienceFinalProject mainframe;
	private KirbyClass kirby;
    private TextClass PressEnter;//decoration for the intro
    private FireWorkClass Firework1;//these are for decoration, if you were to win the game
    private FireWorkClass Firework2;
    private ArrayList<WaddleDeeClass> WaddleDee = new ArrayList<WaddleDeeClass>();//all the enemies get added in to thier own Arraylist, this allows for us to easlier add and remove them
    private ArrayList<PoppyBroClass> PoppyBro = new ArrayList<PoppyBroClass>();
    private ArrayList<GrizzoClass> Grizzo = new ArrayList<GrizzoClass>();
    private ArrayList<ScarfyClass> Scarfy = new ArrayList<ScarfyClass>();
    private ArrayList<SpikyDudeClass> Spike = new ArrayList<SpikyDudeClass>();
    private static ArrayList<Bomb> allBombs = new ArrayList<Bomb>();//all the bomvs get stored in a list
	private static ArrayList<Boolean> bombAvail = new ArrayList<Boolean>();
	private static ArrayList<Double> gravity = new ArrayList<Double>();
    private ArrayList<shootingStarClass> shootingStar = new ArrayList<shootingStarClass>(); //when kirby inhales 
	public boolean ready=false,pause = false;//we use pause to allow users to access the menu  
	private static Bomb kb;
	private boolean kbAvail = false;//checking if kirby's bombs are available
	private double grav = 0;//gravity for kirby's bombs
	private int n;//for knowing where kirby's bombs get thrown
	private boolean []keys;
	public String page = "intro",lastLevel,type;//page keeps track of the dif pages and allows us to navigate through them, type is what we use to get poppybro powers for kirby, and last leevl is when kirby dies this send him back to the start of the level
	private Image back,bar,kirbyHP,intropg,levelintro,levelSelectorpg,star,menu,bosspage,winPg,tutorialPg,deathPg;//all the images we used for the pages and decoration
	private BufferedImage mask;//the mask allows us to stay above the ground
	private boolean kirbyMove =false, bomb = false;//kirbyMove 
	private int score=0,starX=250,starY=470,wait= 90,delay,delay2;
	public static int offset;//the offset allows for kirby to have the classic side scroller look
	private int[] allX,allY;//for kirby
	Font fontSys=null;

	public GamePanel(ComputerScienceFinalProject m){
		mainframe =m;
		enemyClear();//oopsie whoopsie :)
		keys = new boolean[KeyEvent.KEY_LAST+1];
		kirby = new KirbyClass(150,360,"walkingKirby",10);
		PressEnter = new TextClass(140,30,"startTXT",2);
		Firework1 = new FireWorkClass(70,225,"firework",16);
		Firework2 = new FireWorkClass(625,70,"firework",16);
		addKeyListener(new moveListener());
		loadPics();//loas all the pics 
		fontSys = new Font("Comic Sans MS",Font.PLAIN,32);//this is to write the score an amount of lives
	 	setPreferredSize(new Dimension(800,616));  
	 	allX = new int[3];
		allY = new int[3];
	} 
		
	public void Level1Enemies(){// each level has different enemies positions 
		WaddleDee.add(new WaddleDeeClass(680,485,"WaddleDeeWalk",10));
		WaddleDee.add(new WaddleDeeClass(1305,485,"WaddleDeeWalk",10));
		WaddleDee.add(new WaddleDeeClass(2255,415,"WaddleDeeWalk",10));
		WaddleDee.add(new WaddleDeeClass(2620,485,"WaddleDeeWalk",10));
		WaddleDee.add(new WaddleDeeClass(3500,415,"WaddleDeeWalk",10));
		Grizzo.add(new GrizzoClass(1400,445,"grizzo",4));
		Spike.add(new SpikyDudeClass(3375,260));
		Scarfy.add(new ScarfyClass(1305,150,"scarfy",5));
	}
	
	public void Level2Enemies(){//(same as Level1Enemies)// PoppyBro
		WaddleDee.add(new WaddleDeeClass(417,350,"WaddleDeeWalk",10));
		WaddleDee.add(new WaddleDeeClass(870,215,"WaddleDeeWalk",10));
		WaddleDee.add(new WaddleDeeClass(1496,215,"WaddleDeeWalk",10));
		Grizzo.add(new GrizzoClass(915,445,"grizzo",4));
		PoppyBro.add(new PoppyBroClass(3755, 425,"poppyBros",6));
		allBombs.add(new Bomb(0,0));//each poppybro has his own bomb set, so for each new poppybro they also have to get added into the list
	    bombAvail.add(false);	
	    gravity.add(0.0);
	    Scarfy.add(new ScarfyClass(2300,150,"scarfy",5));	 
	    Spike.add(new SpikyDudeClass(2242,260));
	    Spike.add(new SpikyDudeClass(2898,525));
	    Spike.add(new SpikyDudeClass(3170,525));   	
	}
	
	public void Level3Enemies(){//(same as Level2enemies)
		WaddleDee.add(new WaddleDeeClass(194,285,"WaddleDeeWalk",10));
		Scarfy.add(new ScarfyClass(230,150,"scarfy",5));
		PoppyBro.add(new PoppyBroClass(636,425,"poppyBros",6));
		allBombs.add(new Bomb(0,0));
	    bombAvail.add(false);
	    gravity.add(0.0);
	    Grizzo.add(new GrizzoClass(770,180,"grizzo",4));
	    WaddleDee.add(new WaddleDeeClass(1350,350,"WaddleDeeWalk",10));
	    WaddleDee.add(new WaddleDeeClass(1735,88,"WaddleDeeWalk",10));
	    WaddleDee.add(new WaddleDeeClass(2393,88,"WaddleDeeWalk",10));
	    PoppyBro.add(new PoppyBroClass(2000, 500,"poppyBros",6));
		allBombs.add(new Bomb(0,0));
	    bombAvail.add(false);
	    gravity.add(0.0);
	    Grizzo.add(new GrizzoClass(3115,120,"grizzo",4));
		PoppyBro.add(new PoppyBroClass(3397,445,"poppyBros",6));
		allBombs.add(new Bomb(0,0));
	    bombAvail.add(false);
	    gravity.add(0.0);
	    WaddleDee.add(new WaddleDeeClass(3730,350,"WaddleDeeWalk",10));
	    Spike.add(new SpikyDudeClass(1943,150));   	
	    Spike.add(new SpikyDudeClass(2231, 150));   		
	}
	   
    private void enemyClear(){//once kirby completes each level enemies get placed into different positions, so this clears them from the list after each level
    	WaddleDee.clear();
		Grizzo.clear();
		PoppyBro.clear();
		allBombs.clear();
		Scarfy.clear();
		Spike.clear();
    }

	public void loadPics(){//loads all the images we used (mostly background & deco)
		try{
	  		mask = ImageIO.read(new File("BackgroundAndMaks/mask0.png"));
	  	}
	  	catch (IOException e){
			System.out.println(e);
	    }
		back = new ImageIcon("pages/intro4.png").getImage();
		bar = new ImageIcon("bar.png").getImage();
		kirbyHP = new ImageIcon("kirbyHPbar.png").getImage();
		intropg =  new ImageIcon("pages/intro1.png").getImage();
		levelintro =  new ImageIcon("pages/intro3.png").getImage();
		star = new ImageIcon("star.png").getImage();
		menu =  new ImageIcon("pages/menu.png").getImage();
		winPg = new ImageIcon("pages/winner.png").getImage();
		tutorialPg = new ImageIcon("pages/tutorial.png").getImage();
		deathPg = new ImageIcon("pages/death pg.png").getImage();
	}

    public void addNotify() {
        super.addNotify();
        requestFocus();
        ready = true;
    }
    
    public void updateCoords(){//it breaks the kirby, up into his x&y movements separtly, and then checking that he can move there before applying the movemnts
		allX[0] = kirby.getX();
		allX[1] = kirby.getX() + (kirby.getImage().getWidth(null)/2);
		allX[2] = kirby.getX() + kirby.getImage().getWidth(null);
		allY[0] = kirby.getY();
		allY[1] = kirby.getY() + kirby.getImage().getHeight(null)/2;
		allY[2] = kirby.getY() + kirby.getImage().getHeight(null);
	}
    
    private boolean clear(int x,int y){//this allows us to "read" from our mask, anywhere that is the colour black kirby cant go through
   		Color WALL = new Color(0,0,0);
		if(x<0 || x>= mask.getWidth(null) || y<0 || y>= mask.getHeight(null)){
			return false;
		}
		Color c = new Color(mask.getRGB(x, y));
		return !c.equals(WALL);
	}
	
	private boolean door(int x,int y){//in our masks doors are red, once kirby gets to a door this checks to see
   		Color doorKnob = new Color(255,0,0);
		if(x<0 || x>= mask.getWidth(null) || y<0 || y>= mask.getHeight(null)){
			return false;
		}
		Color c = new Color(mask.getRGB(x, y));
		return  c.equals(doorKnob);
	}
	
	public boolean atWall(int x, int y){//in our mask we drew blue walls, this allows for the enemies to hit t he walls and "reverse" to keep them in a certain difference, this also wont affect kirby as he uses clear();
		Color WALL = new Color(0,0,255);
		if(x<0 || x>= mask.getWidth(null) || y<0 || y>= mask.getHeight(null)){
			return false;
		}
		Color c = new Color(mask.getRGB(x, y));
		return !c.equals(WALL);
	}

	public void kirbyHp(){//when an enemie hits kirby, he looses one of his Hp's, once hes Hp reaches zero (out of 6) he looses a life, and gets sent back to the start of that level
		kirby.setHP(kirby.getHP()-1);
		score+=500;
		kirby.setX(kirby.getX()-80);
		kirby.setY(kirby.getY()-20);
		if(kirby.getHP() == 0){
			kirby.setLives(kirby.getLives() - 1);
			kirby.setHP(6);
			offset = 0;
			kirby.setX(300);
			kirby.setY(475);
			enemyClear();
			if(lastLevel == "level 1"){
				Level1Enemies();
			}
			if(lastLevel == "level 2"){
				Level2Enemies();
			}
			if(lastLevel == "level 3"){
				Level2Enemies();
			}
		}
		if(kirby.getLives() ==0){//if he looses both lives the game is over :(
			page = "death";
		}
		if(kirby.getpowers() == true){//if kirby has powers and gets hit he then looses those powers :(
			kirby.setpowers(false);
		}
	}
	
	public void decoMove(){//this just makes our decorations look animated 
		PressEnter.move();	
		Firework1.move();
		Firework2.move();
	}

	public void WaddleDeeMove(){//this is where the movement and collisons for waddleDee happen
		if(pause == false){
			for(int i=0;i<WaddleDee.size();i++){
	    		if(WaddleDee.get(i).collidez(kirby, offset)){
					WaddleDee.remove(i);//if waddle collides with kirby he gets removed from the arraylist
					kirbyHp();
					break;
				}
				if(kb!=null){//if wadlde gets hit with a bomb thrown by kirby he will also get removed
	    			if(WaddleDee.get(i).bombCollide(kb, offset)){
	    				WaddleDee.remove(i);
	    				score+=200;
	    			}
	    		}
				if(!atWall(WaddleDee.get(i).getX()+15,WaddleDee.get(i).getY())){//this is where the atWall method comes in, this allows him to move until he hits a wall, when  he hits one his movements get reversed
		     		WaddleDee.get(i).reverse();
		     	}	
		     	if(kirbyMove = true){//they techically only move when kirbys not moving other wise they move at 2x the speed
		     		WaddleDee.get(i).move(1);
		     	}
		     	else{
		     		WaddleDee.get(i).move(0);
		     	}
			}
		}
	}
	
	public void ScarfyMove(){//Same as waddleDeeMove
		if(pause == false){
			for(int i=0;i<Scarfy.size();i++){
	    		if(Scarfy.get(i).collidez(kirby, offset)){
					Scarfy.remove(i);
					kirbyHp();
					break;
				}
				if(kb!=null){
	    			if(Scarfy.get(i).bombCollide(kb, offset)){
	    				Scarfy.remove(i);
	    				score+=200;
	    			}
	    		}
				if(!atWall(Scarfy.get(i).getX(),Scarfy.get(i).getY())){
		     		Scarfy.get(i).reverse();
		     	}	
		     	if(kirbyMove = true){
		     		Scarfy.get(i).move(2);
		     	}
		     	else{
		     		Scarfy.get(i).move(0);
		     	}
			}
		}
	}
	
	public void PoppyBroMove(){	
		if(pause == false){
			for(int i=0;i<PoppyBro.size();i++){
	    		if(PoppyBro.get(i).collidez(kirby, offset)){
					PoppyBro.remove(i);
					allBombs.set(i, null);
					bombAvail.set(i, false);
					kirbyHp();
					break;
				}
				if(kb!=null){
	    			if(PoppyBro.get(i).bombCollide(kb, offset)){
	    				PoppyBro.remove(i);
	    				score+=200;
	    			}
	    		}
				else{
					if(bombAvail.get(i) == false){
						 allBombs.set(i, new Bomb(PoppyBro.get(i).getX(),PoppyBro.get(i).getY()));
						 bombAvail.set(i, true);
					}
					bombThrow(allBombs.get(i), PoppyBro.get(i), bombAvail.get(i), i);
					if(!atWall(PoppyBro.get(i).getX(),PoppyBro.get(i).getY())){
			     		PoppyBro.get(i).reverse();
			     	}	
			     	if(kirbyMove = true){
			     		PoppyBro.get(i).move(1);
			     	}
			     	if(bombAvail.get(i) == true){
	    				if(allBombs.get(i).collidez(kirby, offset)){
				    		kirbyHp();
				    		bombExplode(allBombs.get(i), bombAvail.get(i), i);
				    		kirby.setX(kirby.getX()-50);
	    				}
			     	}
			     	else{
			     		PoppyBro.get(i).move(0);
			     	}
				}
			}
		}
	}
	
	public void SpikeMove(){//Same as waddleDeeMove, but cant be killed 
		if(pause == false){
			for(int i=0;i<Spike.size();i++){
				if(Spike.get(i).collidez(kirby, offset)){
					kirbyHp();
					kirby.setX(kirby.getX()-80);
					kirby.setY(kirby.getY()-20);
				}
			}
		}
	}		
	
	public void GrizzoMove(){//Same as waddleDeeMove
		if(pause == false){
			for(int i=0;i<Grizzo.size();i++){
	    		if(Grizzo.get(i).collidez(kirby, offset)){
					Grizzo.remove(i);
					kirbyHp();
					break;
				}
				if(kb!=null){
	    			if(Grizzo.get(i).bombCollide(kb, offset)){
	    				Grizzo.remove(i);
	    				score+=200;
	    			}
	    		}
				if(!atWall(Grizzo.get(i).getX(),Grizzo.get(i).getY())){
		     		Grizzo.get(i).reverse();
		     	}	
		     	if(kirbyMove = true){
		     		Grizzo.get(i).move(2);
		     	}
		     	else{
		     		Grizzo.get(i).move(0);
		     	}
			}
		}
	}
			
    private void bombThrow(Bomb b, PoppyBroClass p, Boolean bAvail, int i){//the gravity movement of the bombs
    	if(clear(allBombs.get(i).getX(), allBombs.get(i).getY()+31)){
    	   	gravity.set(i, gravity.get(i)+0.08);
    		allBombs.get(i).setY((int)(b.getY() + gravity.get(i)));
    		if(p.getDir()==p.RIGHT){
    			allBombs.get(i).setX(p.getX() + 80);
    		}
    		else{
    			allBombs.get(i).setX(p.getX() - 80);
    		}
    	}
    	else{//gravity get set to zero
    		gravity.set(i, 0.0);
    		bombExplode(allBombs.get(i), bAvail, i);//bomb explodes when it hits the ground
    	}
    }
    
    private void bombExplode(Bomb b, Boolean bAvail, int i){
    	allBombs.get(i).explode();//bomb explodes and then gets removed
		if(allBombs.get(i).getFrame() == 3){
			bombAvail.set(i, false);
			allBombs.set(i, null);
		}
    }
    
    private void kbombThrow(Bomb b, Boolean bAvail, int n){ //same as bomb throw but uses enter key as detonator
    	if(clear(b.getX(), b.getY()+31)){
    	   	grav+=0.08;
    		b.setY((int)(b.getY() + grav));
    		b.setX(b.getX() + n);
    	}
    	else{
    		grav=0.0;
    		kbombExplode(b, bAvail);
    	}
    }
    
    private void kbombExplode(Bomb b, Boolean bAvail){//same as bomb explode
    	b.explode();
		if(b.getFrame() == 3){
			kbAvail = false;
			kb = null;
		}
    }
    
    private void EnemySwallow(){//this is where each enemy would get swlalowed if the z key gets pressed (in the move method)
    	for(int i =0;i<WaddleDee.size();i++){
			if(WaddleDee.get(i).swallows(kirby,offset)){
				if(kirby.getDirH() == kirby.RIGHT){//since kirby can swallow from each direction
					WaddleDee.get(i).move(-2);//if kirby is facing right, the enemy will move twords the left to kirby
				}
				else{
					WaddleDee.get(i).move(2);//otherwise that means kirby is facing left so the enemy will move twords the right
				}
				if(WaddleDee.get(i).collidez(kirby, offset)){
					WaddleDee.remove(i);//after colliding while kirby is swallowed the enemy gets removed from the list
					kirby.setEnemySwallowed(true);//if the enemy is swallowed then kirby gets the option to either swallow them or shoot them out
					type = "waddleDee";//for powers you need to get thier type, if the enemy is poppy bro then kirby gets powers 
					score +=1500;
				}
			}
		}
		for(int i =0;i<Scarfy.size();i++){
			if(Scarfy.get(i).swallows(kirby,offset)){
				if(kirby.getDirH() == kirby.LEFT){
					Scarfy.get(i).move(-2);
				}
				else{
					Scarfy.get(i).move(2);
				}
				if(Scarfy.get(i).collidez(kirby, offset)){
					Scarfy.remove(i);
					kirby.setEnemySwallowed(true);
					type = "Scarfy";
					score +=1500;
				}
			}
		}
		for(int i =0;i<PoppyBro.size();i++){
			if(PoppyBro.get(i).swallows(kirby,offset)){
				if(kirby.getDirH() == kirby.RIGHT){
					PoppyBro.get(i).move(-2);
				}
				else{
					PoppyBro.get(i).move(2);
				}
				if(PoppyBro.get(i).collidez(kirby, offset)){
					PoppyBro.remove(i);
					kirby.setEnemySwallowed(true);
					type = "poppyBro";
					score+=1500;
					allBombs.set(i, null);//since poppybro has bombs, once hes removed from the array list, the bombs have to be to 
					bombAvail.set(i, false);
				}
			}
		}
		for(int i =0;i<Grizzo.size();i++){
			if(Grizzo.get(i).swallows(kirby,offset)){
				if(kirby.getDirH() == kirby.LEFT){
					Grizzo.get(i).move(-4);
				}
				else{
					Grizzo.get(i).move(4);
				}
				if(Grizzo.get(i).collidez(kirby, offset)){
					Grizzo.remove(i);
					type = "grizzo";
					kirby.setEnemySwallowed(true);
					score+=1500;
				}
			}
		}
    }
    
    private void levelReset(){//just used to rest a level or advance to the next level
    	offset = 0;//the offset gets set to zero again as they are set in the begining 
		kirby.setX(300);//kirbys cooords get reset aswell 
		kirby.setY(475);
		enemyClear();
    }
    
    private void DoorPages(){//once kirby is standing infront of a door and the user presses the up arrow key (in move method) this checks the level tyhe user is on, clear the enemies and set sthe next level
    	if(page == "level 1 Entrance"){
			page = "level 1";
			lastLevel = "level 1";//this is for resetting is the user looses a life
			back = new ImageIcon("BackgroundAndMaks/Level1part1.png").getImage();//the new mask and background are set
			try {
	    		mask = ImageIO.read(new File("BackgroundAndMaks/mask1.png"));
			} 
			catch (IOException e) {
				System.out.println(e);
			}
			levelReset();
			Level1Enemies();
		}
		else if(page == "level 1"){
			page = "level 2";
			lastLevel = "level 2";
			back = new ImageIcon("BackgroundAndMaks/level1part2.png").getImage();
			try {
	    		mask = ImageIO.read(new File("BackgroundAndMaks/mask2.png"));
			} 
			catch (IOException e) {
				System.out.println(e);
			}
			levelReset();
			Level2Enemies();
		}
		else if(page == "level 2"){
			page = "level 3";
			lastLevel = "level 3";
			back = new ImageIcon("BackgroundAndMaks/level1part3.png").getImage();
			try {
	    		mask = ImageIO.read(new File("BackgroundAndMaks/mask3.png"));
			} 
			catch (IOException e) {
				System.out.println(e);
			}
			levelReset();
			Level3Enemies();
		}
		else if(page == "level 3"){
			page = "win";
		}
    }
	
	public void move(){//this is where all of kirbys main keystrokes are
		if(pause == false){//pause is when the menu is brought up, it allows the users to move the starselect (starselect method) with out moving kirby
			if(kirby.getpowers() == true){//if kirby has powers he can throw bombs
	    		if(keys[KeyEvent.VK_ENTER]){//if enter is pressed kirby throws a bomb
	    			if(kbAvail == false){
	    				kbAvail = true;
	    				if(kirby.getDirH()== kirby.RIGHT){
	    					kb = new Bomb(allX[2]-offset, kirby.getY());//bomb placemnt
	    					n = 3;
	    				}
	    				else{
	    					kb = new Bomb(allX[0]-offset, kirby.getY());
	    					n = -3;
	    				}
	    			}
	    		}
	    		if(kb!=null){//if theres bombs throw them
	    			kbombThrow(kb, kbAvail, n);
	    		}
	    	}
			if(keys[KeyEvent.VK_RIGHT]){
				kirby.right(0);//if kirby wants to move to the right, it checks that he can move there and then he moves there
				updateCoords();
				if(!clear(allX[2]-offset, allY[1]) && clear(allX[2]-offset, allY[0])){//checking withe the mask and then applying the movements 
					for(int i=0;i<3;i++){///////the beginning, middle, end coordinates that make up kirby (think: tic tac toe board)
	    				kirby.setY(kirby.getY()-1);//kirby is divided into 9 segments, using leftx, middlex, rightx, and topy, middley, bottomy (so we can check the right part of kirby given how he moves)
	    				kirby.setX(kirby.getX()+1);
	    				updateCoords();
	    		 	}
	    		}
	    		else if(kirby.getX()<300 || offset<-3340){
		    		for(int i=0;i<3;i++){
		    			if(clear(allX[2]-offset, allY[0]) && clear(allX[2]-offset, allY[1])){
		    				kirby.setX(kirby.getX()+1);
		    				updateCoords();
		    			}
		    		}
	    		}
	    		else{
	    			for(int i=0;i<3;i++){
		    			if(clear(allX[2]-offset, allY[0]) && clear(allX[2]-offset, allY[1])){
		    				offset-=1;
		    				updateCoords();
		    			}
		    		}	
	    		}
			}
			else if(keys[KeyEvent.VK_LEFT] && clear(kirby.getX()-offset,kirby.getY())){
				kirby.left(0);
    			updateCoords();
	    		if(!clear(allX[0]-offset, allY[1]) && clear(allX[0]-offset, allY[0])){
	    			for(int i=0;i<3;i++){
	    				kirby.setY(kirby.getY()-1);
	    				kirby.setX(kirby.getX()-1);
	    				updateCoords();
		    		 }
		    	}
	    		else if(kirby.getX()>10 || offset >0){//this allows kirby to move back once hes crossed an area 
		    		for(int i=0;i<3;i++){
		    			if(clear(allX[0]-offset, allY[0]) && clear(allX[0]-offset, allY[1])){
		    				kirby.setX(kirby.getX()-1);
		    			}
		    		}
	    		}
	    		else{
	    			for(int i=0;i<3;i++){
		    			if(clear(allX[0]-offset, allY[0]) && clear(allX[0]-offset, allY[1])){
		    				offset+=1;
		    			}
		    		}
	    		}
			}
			if(keys[KeyEvent.VK_M]){//if the m key is pressed this will "pause" the game my making kirby and the enemies not being able to move
				pause =true; 
				if(pause == true){
					page = "menu";
				}
			}
			if(keys[KeyEvent.VK_Z] && !clear(kirby.getX()-offset,kirby.getY() +64)){//if the z key is pressed kirby can swallow an enemy 
				kirby.swallow();
				EnemySwallow();
			}
			
			if(keys[KeyEvent.VK_UP]){
				if(door(allX[1]-offset, allY[1])){
					DoorPages();
				}
			kirby.up(0);
    		updateCoords();
    		for(int i=0;i<10;i++){
    			if(clear(allX[0]-offset, allY[0]) && clear(allX[1]-offset, allY[0])){
    				kirby.setY(kirby.getY()-1);
    				updateCoords();
    			}
    		}
    	}
    	else if(!keys[KeyEvent.VK_UP] && clear(allX[0]-offset, allY[2])){
    		kirby.up(0);
			if(clear(allX[0]-offset, allY[2]) && clear(allX[2]-offset, allY[2])){
				kirby.setY(kirby.getY()+5);
				updateCoords();
			}
    	}
    	else if(!keys[KeyEvent.VK_UP] && !clear(allX[0]-offset, allY[2])){
    		kirby.resetDirV();
    	}
    }  	
		if(keys[KeyEvent.VK_DOWN]){//if the key is pressed down and kirby has inahled an enemy then if that enemy has a power kirby will gain it
			if(kirby.getEnemySwallowed() == true){
				if(type == "poppyBro"){//each enemy has a type 
					kirby.setpowers(true);
				}
				else{
					kirby.setEnemySwallowed(false);//if the enemy dosent have a power then kirby gains nothing
				}
			}
		}
	}
	
	private void pauseMvmt(){//when the user is in teh menu, this allows them to move the star sekector (this method was made to stop at each option as before it would just slide through and looked ugly)
    	if(keys[KeyEvent.VK_DOWN]){
			if(starY==470 || starY ==520){
				starY+= 50;
				keys[KeyEvent.VK_DOWN] = false;
			}
		}
    	else if(keys[KeyEvent.VK_UP]){
			if(starY==570|| starY ==520){
				starY-= 50;
				keys[KeyEvent.VK_UP] = false;
			}
		}
    }
	
	private void starSelect(){//when the menu is brought up by pressing the m key, it brings up a selection 
		Rectangle star = new Rectangle(starX,starY,30,30);
		Rectangle backToGame = new Rectangle(250,465,200, 40);	
		Rectangle mainMenu = new Rectangle(250,515,200, 40);
		Rectangle tutorial = new Rectangle(250,565,200, 40);	
		if(keys[KeyEvent.VK_ENTER]){
			if(page == "menu"){
				if(star.intersects(backToGame)||backToGame.intersects(star)){ 
					page = lastLevel;
					pause= false;
				}
				else if(star.intersects(mainMenu)||mainMenu.intersects(star)){ 
					mainframe.reset = true;
				}
		
				else if(star.intersects(tutorial)||tutorial.intersects(star)){ 
					page = "tutorial";
				}
			}
		}	
	}

	class moveListener implements KeyListener{
	    public void keyTyped(KeyEvent e) {
	    	if(keys[KeyEvent.VK_ENTER]){
				if(page == "intro"){
					page = "level intro";
				}
			}
		}
	    public void keyPressed(KeyEvent e) {
	        keys[e.getKeyCode()] = true;    
	    }    
	    public void keyReleased(KeyEvent e) {
	        keys[e.getKeyCode()] = false;
	        kirby.setSwallow(false);
	    }
    }
  
    private void paintScore(Graphics g){//this shows kirbys score, this also allows kirby score to keep 8 placeholders (mostly deco)
    	g.setColor(new Color(0,0,0));  
    	g.setFont(fontSys);
    	if(score <=9 && score >=0){
    		g.drawString("0000000"+score,230,90);
    	}
    	else if(score <= 99 && score >= 19){
    		g.drawString("000000"+score,230,90);
    	}
    	else if (score <= 999 && score >= 99){
    		g.drawString("00000"+score,230,90);
    	}
    	else if (score <= 999 && score >= 99){
    		g.drawString("00000"+score,230,90);
    	}
    	else if (score <= 9999 && score >= 999){
    		g.drawString("0000"+score,230,90);
    	}
    	else if (score <= 99999 && score >= 9999){
    		g.drawString("000"+score,230,90);
    	}
    	else if (score <= 999999 && score >= 99999){
    		g.drawString("00"+score,230,90);
    	}
    	else if (score <= 9999999 && score >= 999999){
    		g.drawString("0"+score,230,90);
    	}
    	g.drawString("0"+kirby.getLives(),690,70);//also paints kirbys Hp's and lives 
    	for(int i = 0;i<kirby.getHP();i++){
	        g.drawImage(kirbyHP,232+(i*27),25, null);  
	    }
    }
    
    private void shootStarEnemyCollide(){//this sees if the starshoot collides with one of the enemies (in shootstar method)
    		for(int k = 0;k<WaddleDee.size();k++){
			for (int i=0;i<shootingStar.size();i++){
				if((shootingStar.get(i)).collidez(WaddleDee.get(i))){
					score+= 1000;
					WaddleDee.remove(i);
					shootingStar.remove(i);
				}
			}
		}
		for(int k = 0;k<Grizzo.size();k++){
			for (int i=0;i<shootingStar.size();i++){
				if((shootingStar.get(i)).collidez(Grizzo.get(i))){
					score+= 1000;
					Grizzo.remove(i);
					shootingStar.remove(i);
				}
			}
		}
		for(int k = 0;k<Scarfy.size();k++){
			for (int i=0;i<shootingStar.size();i++){
				if((shootingStar.get(i)).collidez(Scarfy.get(i))){
					shootingStar.remove(i);
					score+= 1000;
					Scarfy.remove(i);
				}
			}
		}
		for(int k = 0;k<PoppyBro.size();k++){
			for (int i=0;i<shootingStar.size();i++){
				if((shootingStar.get(i)).collidez(PoppyBro.get(i))){
					shootingStar.remove(i);
					score+= 1000;
					PoppyBro.remove(i);//have to remove the bombs as well when poppybro get removed
					allBombs.set(i, null);
					bombAvail.set(i, false);
				}
			}
		}
    }
    
    private void shootStar(Graphics g){//once kirby has inahled an enemy, if the user presses the space bar he will shoott out a star 
    	if(kirby.getEnemySwallowed() == true){	//the enemy has to be swallowed first
	    	if(keys[KeyEvent.VK_SPACE]){
				shootingStar.add(new shootingStarClass(kirby.getX()-offset+15,kirby.getY()+10));//this adds the star to the arraylist 
				kirby.setEnemySwallowed(false);//after the star is shot than the enemy is no longer swallowed
		    }
    	} 
	    for(int i=0;i<shootingStar.size();i++){//this draws and moves the star
	    	shootingStar.get(i).draw(g, offset);
    		if(kirbyMove = true){//they techically only move when kirbys not moving other wise they move at 2x the speed
	     		shootingStar.get(i).move(kirby,4);//if kirby is moving than the offset will make it look like its moving
	     	}
	     	else{
	     		shootingStar.get(i).move(kirby,0);//otherwise the star will actually move
	     	}
   		}
		shootStarEnemyCollide();//sees if they collide
		for (int i =0;i<shootingStar.size();i++){//if the shooting star isnt clear it get removed (aka hits a wall)
   			if(!clear(shootingStar.get(i).getX()+15,shootingStar.get(i).getY())){
		   		shootingStar.remove(i);
   			}
		}	
    }
    
    private void paintEnemys(Graphics g){//this just paint all the enemies and bombs
    	for(int i=0;i<WaddleDee.size();i++){
	    	WaddleDee.get(i).draw(g, offset);
	    }
	    for(int i=0;i<Spike.size();i++){
	    	Spike.get(i).draw(g, offset);
	    }
	    for(int i=0;i<Grizzo.size();i++){
	    	Grizzo.get(i).draw(g, offset);
	    }
	    for(int i=0;i<Scarfy.size();i++){
	    	Scarfy.get(i).draw(g, offset);
	    }
	    for(int i=0;i<PoppyBro.size();i++){
	    	PoppyBro.get(i).draw(g, offset);
	    	if(bombAvail.get(i) == true){
	    		allBombs.get(i).draw(g,offset);
	    	}
	    }
	    if(kb!=null){
	    		kb.draw(g, offset);
	    	}

    }
    
    public void paintComponent(Graphics g){
    	g.setColor(new Color(255,255,255));//this is to fill in all the gaps in the mask
	    g.fillRect(0,0,800,616);
    	if (page == "intro"){
    		g.drawImage(intropg,0,0,null);
    		PressEnter.draw(g);
		}
		if(page == "death"){//if the user has lost both lives the game is over, and they get sent back to the main menu after they press the enter key
    		g.drawImage(deathPg,0,0,null);
			if(keys[KeyEvent.VK_ENTER]){
				mainframe.reset = true;
			}
		}
     	if (page == "menu"){    	
     		pauseMvmt();
	    	g.drawImage(menu,0,0,null);
	    	g.drawImage(star,starX,starY,null);//this is that star picture
	    	starSelect();//this allows the user to choose what option they want on the menu (refer to starSelect method)
    	}
    	if (page == "tutorial"){
    		g.drawImage(tutorialPg,0,0,null);
			if(keys[KeyEvent.VK_BACK_SPACE]){//if the user is on the tutorial page inorer for them to get bak to the main menu they just have to press backspace
				page = "menu";
			}
    	}
    	else if (page == "level intro"){
    		delay += 1;
    		g.drawImage(levelintro,0,0,null);
			if(delay % wait == 0){//this page is timed for 90ish seconds so after that it automatically goes to the next page
				page = "level 1 Entrance";
			}
    	}
    	else if (page == "level 1 Entrance"){
    		g.drawImage(mask,0,0,null);
    		g.drawImage(back,0,0,null);
    		kirby.draw(g);	
    	}
    	else if (page == "level 1"||page == "level 2"||page == "level 3"){//since all 3 level have the same basic layout
	    	g.drawImage(mask,offset,0,null);
	    	g.drawImage(back,offset,0,null);
	    	g.drawImage(bar,-2,0,null);  
    		paintScore(g);
	       	paintEnemys(g);
	       	shootStar(g);
	        kirby.draw(g);
    	}
    	else if (page == "win"){//after completeing level 3 the user technically wins
    		g.drawImage(winPg,0,0,null);
    		Firework1.draw(g);//the fireworks are for decoration
    		Firework2.draw(g);
    		kirby.dance();
    		kirby.draw(g);
    	}
    }    
}

class KirbyClass{
	private int x,y,hp=6,lives =2;
	private boolean swallow,enemySwallowed= false, powers = false,dance=false;
	private Image[] walkPics,jumpPics,flyPics,swallowPics,PowersInhalePics,swallowedPics,PowersFlyPics,PowersWalkPics,PowersSwallowedPics,dancePics;
	private int dirH= RIGHT,dirV,frameWalk, delayWalk,frameSwallow, delaySwallow, framePowerWalk, delayPowerWalk, frameFly, delayFly,frameEnemySwallowed,delayEnemySwallowed,frameDance, delayDance;
	public static final int LEFT = 0, RIGHT = 1,UP =2,FLY =3, WAIT = 5;

	public KirbyClass(int x, int y, String name, int n){
		this.x=x;
		this.y=y;
		walkPics = new Image[n];		
		for(int i = 0; i<n; i++){
			walkPics[i] = new ImageIcon("kirbyPics/walkingKirby/walkingKirby"+i+".png").getImage();
		}
		PowersWalkPics = new Image[8];		
		for(int i = 0; i<8; i++){
			PowersWalkPics[i] = new ImageIcon("kirbyPics/walkingPowers/walkingKirby"+i+".png").getImage();
		}
		dancePics = new Image[22];		
		for(int i = 0; i<22; i++){
			dancePics[i] = new ImageIcon("kirbyPics/dance/kdance"+(i+1)+".png").getImage();
		}
		PowersFlyPics = new Image[2];		
		for(int i = 0; i<2; i++){
			PowersFlyPics[i] = new ImageIcon("kirbyPics/flyPowers/kfly"+i+".png").getImage();
		}
		PowersInhalePics = new Image[2];		
		for(int i = 0; i<2; i++){
			PowersInhalePics[i] = new ImageIcon("kirbyPics/kirbyinhalePowers/kswallow"+(i+1)+".png").getImage();
		}
		flyPics = new Image[2];		
		for(int i = 0; i<2; i++){
			flyPics[i] = new ImageIcon("kirbyPics/fly/kfly"+i+".png").getImage();
		}
		swallowPics = new Image[2];		
		for(int i = 0; i<2; i++){
			swallowPics[i] = new ImageIcon("kirbyPics/kirbyinhale/kswallow"+(i+1)+".png").getImage();
		}
		swallowedPics = new Image[2];		
		for(int i = 0; i<2; i++){
			swallowedPics[i] = new ImageIcon("kirbyPics/swallowed/swallowed"+(i)+".png").getImage();
		}	
		PowersSwallowedPics = new Image[2];		
		for(int i = 0; i<2; i++){
			PowersSwallowedPics[i] = new ImageIcon("kirbyPics/Powersswallowed/swallowed"+(i)+".png").getImage();
		}
	}

	public void right(int dx){
		dirH = RIGHT;
		delayWalk += 1;
		delayPowerWalk +=1;
		if(delayWalk % WAIT == 0){
			frameWalk = (frameWalk + 1) % walkPics.length;
		}
		if(delayPowerWalk % WAIT == 0){
			framePowerWalk = (framePowerWalk + 1) % PowersWalkPics.length;
		}
	}
	
	public void left(int dx){
		dirH = LEFT;
		delayWalk += 1;
		delayPowerWalk +=1;
		if(delayWalk % WAIT == 0){
			frameWalk = (frameWalk + 1) % walkPics.length;
		}
		if(delayPowerWalk % WAIT == 0){
			framePowerWalk = (framePowerWalk + 1) % PowersWalkPics.length;
		}
	}

	public void swallow(){
		swallow =true;
		delaySwallow+=1;
		if(delaySwallow%WAIT==0){
			frameSwallow= (frameSwallow+1)% swallowPics.length;
		}
	}
	
	public void enemySwallowed(){
		enemySwallowed =true;
		delayEnemySwallowed+=1;
		if(delayEnemySwallowed%WAIT==0){
			frameEnemySwallowed= (frameEnemySwallowed+1)% swallowedPics.length;
		}
	}

	public void up(int dy){
		dirV = FLY;
		delayFly+= 1;
		if(delayFly % WAIT == 0){
			frameFly = (frameFly + 1) % flyPics.length;
		}
	}
	
	public void dance(){
		dance = true;
		delayDance+= 1;
		if(frameDance!=21){
			if(delayDance % 7 == 0){
				frameDance = (frameDance + 1) % dancePics.length;
			}
		}
	}
	public void setDirV(int n){dirV = n;}
	public int getDirV(){return dirV;}
	public int getDirH(){return dirH;}
	public void resetDirV(){dirV = 0;}
	public int getX(){return x;}
	public boolean getpowers(){return powers;}
	public void setpowers(boolean gp){powers = gp;}
	public int getHP(){return hp;}
	public void setHP(int b){hp = b;}
	public boolean getSwallow(){return swallow;}
	public void setSwallow(boolean hl){swallow = hl;}
	public boolean getEnemySwallowed(){return enemySwallowed;}
	public void setEnemySwallowed(boolean es){enemySwallowed = es;}
	public void setX(int xo){x = xo;}
	public int getY(){return y;}
	public int getLives(){return lives;}
	public void setLives(int li){lives = li;}
	public void setY(int yo){y = yo;}
	public Image getImage(){return walkPics[frameWalk];}
	
	private void directionDraw(Graphics g, Image[] pics, int frame){
		if(dirH == LEFT){
			int w = pics[frame].getWidth(null);
			int h = pics[frame].getHeight(null);
			g.drawImage(pics[frame], x+w, y, -w, h, null);
		}
		else{
			g.drawImage(pics[frame], x, y, null);
		}
	}

	public void draw(Graphics g){
		if(dance == true){
			g.drawImage(dancePics[frameDance], x, y, null);
		}
		else if (powers == true){
			if(dirV == FLY){
				directionDraw(g, PowersFlyPics, frameFly);	
			}
			else if(swallow==true){
				directionDraw(g, PowersInhalePics, frameSwallow);
			}
			else if(enemySwallowed==true){
				directionDraw(g, PowersSwallowedPics, frameEnemySwallowed);
			}
			else if(dirH==RIGHT){
				g.drawImage(PowersWalkPics[framePowerWalk], x, y-10, null);
			}
			else if (dirH==LEFT){
				int w = PowersWalkPics[framePowerWalk].getWidth(null);
				int h = PowersWalkPics[framePowerWalk].getHeight(null);
				g.drawImage(PowersWalkPics[framePowerWalk], x + w, y-10, -w, h, null);
			}
		}
		else if(swallow==true){
			directionDraw(g, swallowPics, frameSwallow);	
		}
		else if(enemySwallowed==true){
			directionDraw(g, swallowedPics, frameEnemySwallowed);
		}
		else if(dirV == FLY){
			directionDraw(g, flyPics, frameFly);
		}
		else if(dirH==RIGHT){
			g.drawImage(walkPics[frameWalk], x, y, null);
		}
		else if (dirH==LEFT){
			int w = walkPics[frameWalk].getWidth(null);
			int h = walkPics[frameWalk].getHeight(null);
			g.drawImage(walkPics[frameWalk], x + w, y, -w, h, null);
		}
	}
}

class shootingStarClass{
	private int x,y,dir =RIGHT;
	private Image shootingStar;
	public static final int LEFT = 0, RIGHT = 1;

	public shootingStarClass(int x, int y){
		this.x=x;
		this.y=y;
		shootingStar =  new ImageIcon("star.png").getImage();
	}
	
	public void move(KirbyClass kirby, int dx){
		dir = kirby.getDirH();
		if(dir == RIGHT){
			x +=dx;
		}
		else{
			x-=dx;
		}
	}
	public boolean collidez(WaddleDeeClass W){
		Rectangle fRect = new Rectangle(W.getX(), W.getY(), 50, 50);
		Rectangle cRect = new Rectangle(x, y, shootingStar.getWidth(null), shootingStar.getHeight(null));	
		if(cRect.intersects(fRect) || fRect.intersects(cRect)){ 
			return true;
		}
		return false;	
	}
	public boolean collidez(GrizzoClass G){
		Rectangle fRect = new Rectangle(G.getX(), G.getY(), 100, 100);
		Rectangle cRect = new Rectangle(x, y, shootingStar.getWidth(null), shootingStar.getHeight(null));	
		if(cRect.intersects(fRect) || fRect.intersects(cRect)){ 
			return true;
		}
		return false;	
	}
	public boolean collidez(PoppyBroClass P){
		Rectangle fRect = new Rectangle(P.getX(), P.getY(), 100, 100);
		Rectangle cRect = new Rectangle(x, y, shootingStar.getWidth(null), shootingStar.getHeight(null));	
		if(cRect.intersects(fRect) || fRect.intersects(cRect)){ 
			return true;
		}
		return false;	
	}
	public boolean collidez(ScarfyClass S){
		Rectangle fRect = new Rectangle(S.getX(), S.getY(), 100, 100);
		Rectangle cRect = new Rectangle(x, y, shootingStar.getWidth(null), shootingStar.getHeight(null));	
		if(cRect.intersects(fRect) || fRect.intersects(cRect)){ 
			return true;
		}
		return false;	
	}
	
	public int getX(){return x;}
	public void setX(int xo){x = xo;}
	public int getDir(){return dir;}
	public void setY(int yo){y = yo;}
	public int getY(){return y;}
	
	public void draw(Graphics g, int off){
		if(dir==RIGHT){
			g.drawImage(shootingStar, x+off, y, null);
		}
		else if (dir==LEFT){
			int w = shootingStar.getWidth(null);
			int h = shootingStar.getHeight(null);
			g.drawImage(shootingStar, x+off + w, y, -w, h, null);
		}
	}
}

class WaddleDeeClass{
	private int x,y;
	private Image[]pics;
	private int dir, frame, delay;
	public static final int LEFT = 0, RIGHT = 1, WAIT = 5;
		
	public WaddleDeeClass(int x, int y, String name, int n){
		this.x=x;
		this.y=y;
		dir = RIGHT;
		pics = new Image[n];		
		for(int i = 0; i<n; i++){
			pics[i] = new ImageIcon(name+"/"+name+i+".png").getImage();
		}
	}
	
	public void move(int dx){
		if(dir == RIGHT){
			x +=dx;
		}
		else{
			x-=dx;
		}
		delay += 1;
		if(delay % WAIT == 0){
			frame = (frame + 1) % pics.length;
		}
	}

	public boolean collidez(KirbyClass kirby,int off){
		int kirbyWidth = kirby.getImage().getWidth(null);
		int kirbyHeight = kirby.getImage().getHeight(null);
		Rectangle fRect = new Rectangle(kirby.getX(), kirby.getY(), kirbyWidth, kirbyHeight);
		Rectangle cRect = new Rectangle(x+off, y,  pics[frame].getWidth(null),  pics[frame].getHeight(null));	
		if(cRect.intersects(fRect) || fRect.intersects(cRect)){ 
			return true;
		}
		return false;	
	}
	
	public boolean bombCollide(Bomb b, int offset){
		int bx = b.getX();
		int by = b.getY();
		Rectangle kRect = new Rectangle(bx+offset, by, 25, 25);
		Rectangle eRect = new Rectangle(x+offset, y, 50, 50);
		if(eRect.intersects(kRect) || kRect.intersects(eRect)){
			return true;
		}
		return false;
	}

	public boolean swallows(KirbyClass kirby,int off){
		int kx;
		if(kirby.getDirH() == kirby.RIGHT){
			kx = kirby.getX();
		}
		else{
			kx = kirby.getX()-500;
		}
		int ky = kirby.getY();
		Rectangle fRect = new Rectangle(kx, ky, 500, 55);
		Rectangle cRect = new Rectangle(x+off, y,50, 50);	
		if(cRect.intersects(fRect) || fRect.intersects(cRect)){ 
			return true;
		}
		return false;
	}
	
	public void reverse(){
		if(dir ==RIGHT){
			dir = LEFT;
		}
		else if(dir == LEFT){
			dir = RIGHT;
		}
	}
	public int getX(){return x;}
	public void setX(int xo){x = xo;}
	public int getDir(){return dir;}
	public void setY(int yo){y = yo;}
	public int getY(){return y;}
	public void draw(Graphics g,int off){
		if(dir == RIGHT){
			g.drawImage(pics[frame], x+off, y, null);
		}
		else{
			int w = pics[frame].getWidth(null);
			int h = pics[frame].getHeight(null);
			g.drawImage(pics[frame], x+off + w, y, -w, h, null);
		}
	}
}

class PoppyBroClass{
	private int x,y;
	private Image[]pics;
	private int dir, frame, delay;
	public static final int LEFT = 0, RIGHT = 1, WAIT = 15;
		
	public PoppyBroClass(int x, int y, String name, int n){
		this.x=x;
		this.y=y;
		dir = RIGHT;
		pics = new Image[n];		
		for(int i = 0; i<n; i++){
			pics[i] = new ImageIcon(name+"/"+name+i+".png").getImage();
		}
	}
	
	public void move(int dx){
		if(dir == RIGHT){
			x +=dx;
		}
		else{
			x-=dx;
		}
		delay += 1;
		if(delay % WAIT == 0){
			frame = (frame + 1) % pics.length;
		}
	}

	public boolean inView(KirbyClass kirby, int offset){
		int ex;
		if(dir == RIGHT){
			ex = x;
		}
		else{
			ex = x - 250;
		}
		int kx = kirby.getX();
		int ky = kirby.getY();
		Rectangle eRect = new Rectangle(ex+offset, y, 250, 55);
		Rectangle kRect = new Rectangle(kx, ky, 50, 50);
		if(eRect.intersects(kRect) || kRect.intersects(eRect)){
			return true;
		}
		return false;	
	}


	public boolean collidez(KirbyClass kirby,int off){	
		int kirbyWidth = kirby.getImage().getWidth(null);
		int kirbyHeight = kirby.getImage().getHeight(null);
		Rectangle fRect = new Rectangle(kirby.getX(), kirby.getY(), kirbyWidth, kirbyHeight);
		Rectangle cRect = new Rectangle(x+off, y,  pics[frame].getWidth(null),  pics[frame].getHeight(null));	
		if(cRect.intersects(fRect) || fRect.intersects(cRect)){ 
			return true;
		}
		return false;
	}
	
	public boolean bombCollide(Bomb b, int offset){
		int bx = b.getX();
		int by = b.getY();
		Rectangle kRect = new Rectangle(bx+offset, by, 25, 25);
		Rectangle eRect = new Rectangle(x+offset, y, 50, 50);
		if(eRect.intersects(kRect) || kRect.intersects(eRect)){
			return true;
		}
		return false;
	}
	
	public boolean swallows(KirbyClass kirby,int off){
		int kx;
		if(kirby.getDirH() == kirby.RIGHT){
			kx = kirby.getX();
		}
		else{
			kx = kirby.getX()-500;
		}
		int ky = kirby.getY();
		Rectangle fRect = new Rectangle(kx, ky, 500, 55);
		Rectangle cRect = new Rectangle(x+off, y,50, 50);	
		if(cRect.intersects(fRect) || fRect.intersects(cRect)){ 
			return true;
		}
		return false;
	}
	
	public void reverse(){
		if(dir ==RIGHT){
			dir = LEFT;
		}
		else if(dir == LEFT){
			dir = RIGHT;
		}
	}
	public int getX(){return x;}
	public void setX(int xo){x = xo;}
	public void setY(int yo){y = yo;}
	public int getY(){return y;}
	public int getDir(){return dir;}
	public void draw(Graphics g,int off){
		if(dir == RIGHT){
			g.drawImage(pics[frame], x+off, y, null);
		}
		else{
			int w = pics[frame].getWidth(null);
			int h = pics[frame].getHeight(null);
			g.drawImage(pics[frame], x+off + w, y, -w, h, null);
		}
	}
}

class GrizzoClass{
	private int x,y;
	private Image[]pics;
	private int dir, frame, delay;
	public static final int LEFT = 0, RIGHT = 1, WAIT = 7; 
		
	public GrizzoClass(int x, int y, String name, int n){
		this.x=x;
		this.y=y;
		dir = RIGHT;
		pics = new Image[n];		
		for(int i = 0; i<n; i++){
			pics[i] = new ImageIcon(name+"/"+name+i+".png").getImage();
		}
	}
	
	public void move(int dx){
		if(dir == RIGHT){
			x +=dx;
		}
		else{
			x-=dx;
		}
		delay += 1;
		if(delay % WAIT == 0){
			frame = (frame + 1) % pics.length;
		}
	}

	public boolean collidez(KirbyClass kirby,int off){
		int kirbyWidth = kirby.getImage().getWidth(null);
		int kirbyHeight = kirby.getImage().getHeight(null);
		Rectangle fRect = new Rectangle(kirby.getX(), kirby.getY(), kirbyWidth, kirbyHeight);
		Rectangle cRect = new Rectangle(x+off, y,  pics[frame].getWidth(null),  pics[frame].getHeight(null));	
		if(cRect.intersects(fRect) || fRect.intersects(cRect)){ 
			return true;
		}
		return false;
	}
	
	public boolean bombCollide(Bomb b, int offset){
		int bx = b.getX();
		int by = b.getY();
		Rectangle kRect = new Rectangle(bx+offset, by, 25, 25);
		Rectangle eRect = new Rectangle(x+offset, y, 50, 50);
		if(eRect.intersects(kRect) || kRect.intersects(eRect)){
			return true;
		}
		return false;
	}
	
	public boolean swallows(KirbyClass kirby,int off){
		int kx;
		if(kirby.getDirH() == kirby.RIGHT){
			kx = kirby.getX();
		}
		else{
			kx = kirby.getX()-500;
		}
		int ky = kirby.getY();
		Rectangle fRect = new Rectangle(kx, ky, 500, 55);
		Rectangle cRect = new Rectangle(x+off, y,50, 50);
		if(cRect.intersects(fRect) || fRect.intersects(cRect)){ 	
			return true;
		}
		return false;
	}
	
	public void reverse(){
		if(dir ==RIGHT){
			dir = LEFT;
		}
		else if(dir == LEFT){
			dir = RIGHT;
		}
	}
	public int getX(){return x;}
	public void setX(int xo){x = xo;}
	public void setY(int yo){y = yo;}
	public int getY(){return y;}
	public void draw(Graphics g,int off){
		if(dir == RIGHT){
			g.drawImage(pics[frame], x+off, y, null);
		}
		else{
			int w = pics[frame].getWidth(null);
			int h = pics[frame].getHeight(null);
			g.drawImage(pics[frame], x+off + w, y, -w, h, null);
		}
	}
}

class ScarfyClass{
	private int x,y;
	private Image[]pics;
	private int dir, frame, delay;
	public static final int LEFT = 0, RIGHT = 1, WAIT = 15;
		
	public ScarfyClass(int x, int y, String name, int n){
		this.x=x;
		this.y=y;
		dir = RIGHT;
		pics = new Image[n];		
		for(int i = 0; i<n; i++){
			pics[i] = new ImageIcon(name+"/"+name+i+".png").getImage();
		}
	}
	
	public void move(int dx){
		if(dir == RIGHT){
			x +=dx;
		}
		else{
			x-=dx;
		}
		delay += 1;
		if(delay % WAIT == 0){
			frame = (frame + 1) % pics.length;
		}
	}
	
	public boolean bombCollide(Bomb b, int offset){
		int bx = b.getX();
		int by = b.getY();
		Rectangle kRect = new Rectangle(bx+offset, by, 25, 25);
		Rectangle eRect = new Rectangle(x+offset, y, 50, 50);
		if(eRect.intersects(kRect) || kRect.intersects(eRect)){
			return true;
		}
		return false;
	}
	
	public boolean collidez(KirbyClass kirby,int off){
		int kirbyWidth = kirby.getImage().getWidth(null);
		int kirbyHeight = kirby.getImage().getHeight(null);
		Rectangle fRect = new Rectangle(kirby.getX(), kirby.getY(), kirbyWidth, kirbyHeight);
		Rectangle cRect = new Rectangle(x+off, y,  pics[frame].getWidth(null),  pics[frame].getHeight(null));	
		if(cRect.intersects(fRect) || fRect.intersects(cRect)){ 
			return true;
		}
		return false;
	}
	
	public boolean swallows(KirbyClass kirby,int off){
		int kx;
		if(kirby.getDirH() == kirby.RIGHT){
			kx = kirby.getX();
		}
		else{
			kx = kirby.getX()-500;
		}
		int ky = kirby.getY();
		Rectangle fRect = new Rectangle(kx, ky, 500, 55);
		Rectangle cRect = new Rectangle(x+off, y,50, 50);	
		if(cRect.intersects(fRect) || fRect.intersects(cRect)){ 
			return true;
		}
		return false;
	}
	
	public void reverse(){
		if(dir ==RIGHT){
			dir = LEFT;
		}
		else if(dir == LEFT){
			dir = RIGHT;
		}
	}
	public int getX(){return x;}
	public void setX(int xo){x = xo;}
	public void setY(int yo){y = yo;}
	public int getY(){return y;}
	public void draw(Graphics g,int off){
		if(dir == RIGHT){
			g.drawImage(pics[frame], x+off, y, null);
		}
		else{
			int w = pics[frame].getWidth(null);
			int h = pics[frame].getHeight(null);
			g.drawImage(pics[frame], x+off + w, y, -w, h, null);
		}
	}
}

class TextClass{ //this is the flashing text on the intro page, all it does if flash, telling th euser to press enter to advance to the next page
	private int x,y;
	private Image[]pics;
	private int frame, delay;
	public static final int WAIT = 40;
		
	public TextClass(int x, int y, String name, int n){
		this.x=x;
		this.y=y;
		pics = new Image[n];		
		for(int i = 0; i<n; i++){
			pics[i] = new ImageIcon(name+"/"+name+i+".png").getImage();
		}
	}
	public void move(){
		delay += 1;
		if(delay % WAIT == 0){
			frame = (frame + 1) % pics.length;
		}
	}
	public void draw(Graphics g){
		g.drawImage(pics[frame], x, y, null);
	}
}

class Bomb{
	private int x, y;
	private Image bombPic;
	private Image[]bombEx;
	private int frameEx, delayEx;
	private boolean explode = false;
	public static final int WAIT = 10;
	public Bomb(int x, int y){
		this.x = x;
		this.y = y;
		bombPic = new ImageIcon("bomb/bomb0.png").getImage();
		bombEx = new Image[5];
		for(int i = 0; i<5; i++){
			bombEx[i] = new ImageIcon("bomb/bomb"+i+".png").getImage();
		}
	}
	
	public void explode(){
		explode = true;
		delayEx += 1;
		if(delayEx % WAIT == 0){
			frameEx = (frameEx + 1) % bombEx.length;
		}
	}
	
	public boolean collidez(KirbyClass kirby, int offset){
		int kx = kirby.getX();
		int ky = kirby.getY();
		Rectangle kRect = new Rectangle(kx, ky, 64, 55);
		Rectangle eRect = new Rectangle(x+offset, y, 50, 50);
		if(eRect.intersects(kRect) || kRect.intersects(eRect)){
			return true;
		}
		return false;
	}
	
	public void draw(Graphics g, int offset){
		if(!explode){
		g.drawImage(bombPic, x+offset, y, bombPic.getWidth(null),bombPic.getHeight(null), null);
		}
		else{
			g.drawImage(bombEx[frameEx], x + offset, y, bombPic.getWidth(null),bombPic.getHeight(null), null);
		}
	}
	
	public int getX(){return x;}
	public void setX(int dx){x = dx;}
	public int getY(){return y;}
	public void setY(int dy){y = dy;}
	public int getFrame(){return frameEx;}
}

class FireWorkClass{//this is a ecoration for the win page
	private int x,y;
	private Image[]pics;
	private int frame, delay;
	public static final int WAIT = 12;
		
	public FireWorkClass(int x, int y, String name, int n){
		this.x=x;
		this.y=y;
		pics = new Image[n];		
		for(int i = 0; i<n; i++){
			pics[i] = new ImageIcon(name+"/"+name+(i+1)+".png").getImage();
		}
	}
	public void move(){
		delay += 1;
		if(delay % WAIT == 0){
			frame = (frame + 1) % pics.length;
		}
	}
	public void draw(Graphics g){
		g.drawImage(pics[frame], x, y, null);
	}
}

class SpikyDudeClass{
	private int x,y;
	private Image pic;
	private int dir, frame, delay;
	public static final int LEFT = 0, RIGHT = 1, WAIT = 5;
		
	public SpikyDudeClass(int x, int y){
		this.x=x;
		this.y=y;
		dir = RIGHT;
		pic = new ImageIcon("spikydude.png").getImage();
	}
	
	public boolean collidez(KirbyClass kirby,int off){
		int kirbyWidth = kirby.getImage().getWidth(null);
		int kirbyHeight = kirby.getImage().getHeight(null);
		Rectangle fRect = new Rectangle(kirby.getX(), kirby.getY(), kirbyWidth, kirbyHeight);
		Rectangle cRect = new Rectangle(x+off, y,  pic.getWidth(null),  pic.getHeight(null));	
		if(cRect.intersects(fRect) || fRect.intersects(cRect)){ 
			return true;
		}
		return false;	
	}

	public int getX(){return x;}
	public void setX(int xo){x = xo;}
	public int getDir(){return dir;}
	public void setY(int yo){y = yo;}
	public int getY(){return y;}
	
	public void draw(Graphics g,int off){
		if(dir == RIGHT){
			g.drawImage(pic, x+off, y, null);
		}
		else{
			int w = pic.getWidth(null);
			int h = pic.getHeight(null);
			g.drawImage(pic, x+off + w, y, -w, h, null);
		}
	}
}