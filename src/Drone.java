// imports
import java.awt.Color;
import java.util.ArrayList;

// The drone class will control the artificial intelligence of the drone for the Drone vs Chicken program
// You will begin development with the go() member function.

class Drone extends Agent {
	private Chicken[] chickens;			// This array contains all the chickens
	private int numberOfChickens;		// This tells you how many chickens there are
	private Coop theCoop;				// This array contains theCoop
	private Drone[] drone;				// This array contains all drones
	private int numberOfDrones;			// This tells you how many drones there are

	private String tag;					// This is a String version of the drone's index number
	private int stepSize=2;				// This sets the drone speed to two
	private int rangex = 0;				// Store the max value of x
	private int rangey = 0;				// Stores the max value of y
	private int mine = 0;				// Current Chicken Targeted

	private int destx=0;				// Destination x
	private int desty=0;				// Destination y
	private double degrees = 0;			// Degree value for rotation;

	private int coopx=884;				// theCoop's start x position
	private int coopy=384;				// theCoop's start y position
	private int timeFlies = 0;			// slows coop movement
	private int rad = 70; 				// Original Coop Radius
	private double size = 1;			// Original Size of Coop

	private EZText callSign;			// This displays over the drone to identify the drone
	private EZLine laser;				// EZ Line for laser targeting effect
	private EZLine [] cWalk;			// EZ Line for tracking chicken destination
	
	private EZSound ChickenCooped = EZ.addSound("ChickenCooped.wav");
	private EZSound DroneChase = EZ.addSound("DroneChase.wav");
	private boolean pau = false;
	

	// arrayList to store the un-cooped chickens by their index number
	private static ArrayList<Integer> chicks = new ArrayList<Integer>();

	// Constructor for drone
	Drone(int i, int w, int h) {
		// This places all the drones at the starting point of (0,384)
		x = 0;					
		y = 384;
		
		// Dimension's of the EZ Environment
		rangex = w;
		rangey = h;
		// This initializes the graphics and drone identification
		picture = EZ.addImage("drone.png", x, y);
		tag=i+"";
		callSign=EZ.addText(x,y,tag,Color.BLACK,12);
		laser = EZ.addLine(x, y,coopx,coopy,Color.RED,2);
	}
	
	// Assign array of chickens to drone
	void introduceChickensToDrone(Chicken[] chk, int nchickens){
		chickens = chk;
		numberOfChickens = nchickens;
		mine = nchickens-1;
	}
	
	// Assign array of drones to drone
	void introduceDronesToDrone(Drone[] drn, int ndrones){
		drone = drn;
		numberOfDrones = ndrones;
	}
	
	// stores Coop info into theCoop object 
	void introduceCoopToDrone(Coop cp){
		theCoop = cp;
	}
	
	// Moves the Drone graphics
	void setImagePosition(int posx, int posy) {
		super.setImagePosition(posx, posy); 
		picture.rotateTo(degrees);
		callSign.translateTo(posx, posy);  // sets callSign to drone's location
		laser.setPoint1(posx+4, posy);  // sets EZ Line laser to drone's location for Point1; note slightly off center to avoid callSign
		
		// sets EZ Line laser Point2 position:
		if(!chickens[mine].isCooped())  // if not cooped
			laser.setPoint2(chickens[mine].getX(), chickens[mine].getY());  // sets to targeted chicken's location
		else
			laser.setPoint2(posx+4, posy);  // sets to Point1 position if no chicken targeted; effectively hiding line
	}
	
	// Here is where you begin to put the intelligence for your drones.
	// Call this repeatedly to keep the drone updated.
	void whereTheCoopAt(int i){
		int buffer=40;
		coopx = theCoop.getX();
		coopy = theCoop.getY();
		
		float ratio=(float)(chickens[i].getY()-coopy)/coopy;
		if(chickens[i].getY()==coopy){
			desty=coopy;
			if(chickens[i].getX()<coopx) destx-=buffer;
			if(chickens[i].getX()>coopx) destx+=buffer;
		} else {
			if(chickens[i].getY()<coopy) desty-=-(buffer*ratio);
			if(chickens[i].getY()>coopy) desty+=buffer*ratio;
		}
		if(chickens[i].getX()==coopx){
			destx=coopx;
			if(chickens[i].getY()<coopy) desty-=-(buffer*ratio);
			if(chickens[i].getY()>coopy) desty+=buffer*ratio;
		} else {
			if(chickens[i].getX()<coopx) destx-=buffer;
			if(chickens[i].getX()>coopx) destx+=buffer;
		}
	}
	
	// scans field of vision for chickens and stores chickens index number in chicks arrayList
	void droneVision(int num){
		if(num==0)
			for(int j=100;j<1300;j+=100)
				for(int i=0;i<numberOfChickens;i++)
					if(!chicks.contains(i))
						if (isNearBy(chickens[i],j))
							chicks.add(i);
		if(mine==numberOfChickens-1){
			if(mine==0){
				mine=chicks.get(0);
			}
			else {
				mine=chicks.get(chicks.size()-(chicks.size()-1));
				chicks.remove(chicks.size()-(chicks.size()-1));
			}

		}
	}
	
	// destination inputs for drones with no chicken to hunt; puts unused drones "out-of-the-way"
	void home(){
		// Have the drone return "home" when not in use;
		// At the same time, have drones move out of the way if a chicken comes near
		destx = 1000;
		desty = 20;
		
		for (int i = 0; i < numberOfChickens; i++) {
			if (isNearBy(chickens[i], 150)) {
				if(!(chickens[i].cooped)){
					destx = 975;
					desty = 384;
					break;
				} else {
					destx = 1000;
					desty = 20;
				}
			}
		}
	}

	// behavior when dealing with chickens that are off-screen
	void watchDog(){
		int inter = 110;
		int pos1 = 100;
		int pos2 = 160;
		
		int NowX = chickens[mine].getX();  // targeted chicken's current X position
		int NowY = chickens[mine].getY();  // targeted chicken's current Y position
		int WillX = chickens[mine].destx;  // targeted chicken's dest X position
		int WillY = chickens[mine].desty;  // targeted chicken's dest Y position
		
		at12(inter, pos1, pos2, NowX, NowY, WillX, WillY);
		at01(inter, pos1, pos2, NowX, NowY, WillX, WillY);
//		at02(inter, pos1, pos2, NowX, NowY, WillX, WillY);
		at03(inter, pos1, pos2, NowX, NowY, WillX, WillY);
//		at04(inter, pos1, pos2, NowX, NowY, WillX, WillY);
		at05(inter, pos1, pos2, NowX, NowY, WillX, WillY);
		at06(inter, pos1, pos2, NowX, NowY, WillX, WillY);
		at07(inter, pos1, pos2, NowX, NowY, WillX, WillY);
		at08(inter, pos1, pos2, NowX, NowY, WillX, WillY);
		at09(inter, pos1, pos2, NowX, NowY, WillX, WillY);
		at10(inter, pos1, pos2, NowX, NowY, WillX, WillY);
		at11(inter, pos1, pos2, NowX, NowY, WillX, WillY);
	}
	
	// calculates where the chicken x position where will return based on a constant y position
	int interceptX(int cX, int cY, int dX, int dY, int inter){
		int intercept = 0;
		
		try
		{
			intercept = ((dX-cX)/(dY-cY))*(inter-cY)+cX;
		}
		catch (ArithmeticException e)
		{
		}
		
		return intercept;
	}

	// calculates where the chicken y position where will return based on a constant x position
		int interceptY(int cX, int cY, int dX, int dY, int inter){
		int intercept = 0;

		try
		{
			intercept = ((dX-cX)/(dY-cY))*(inter-cX)+cY;
		}
		catch (ArithmeticException e)
		{
		}
		
		return intercept;
	}
	
	// test to see if chicken is a certain x or y range
	boolean testAt(int p, int low, int high){
		boolean test = false;

		if(p>=low && p<=high)
			test = true;
		
		return test;
	}
	
	// behavior when chicken is off top center
	void at12(int inter, int pos1, int pos2, int chickyNowX, int chickyNowY, int chickyWillX, int chickyWillY){
		int intercept = interceptX(chickyNowX,chickyNowY,chickyWillX, chickyWillY,inter);
		int segStart = rangex/3 + 1;
		int segEnd = rangex - rangex/3;
		
		if(chickyNowY<=inter && testAt(chickyNowX, segStart, segEnd)){
			desty = inter;
			if(chickyWillX<=inter)
				if(chickyNowX<=getX())
					destx = chickyNowX+pos2;
				else
					destx = chickyNowX-pos2;
			else
				if(intercept<=chickyNowX){
					destx = intercept+pos2;
					desty = 0;
				} else {
					destx = intercept-pos2;
					desty = 0;
				}
		}
	}

	// behavior when chicken is of top right
	void at01(int inter, int pos1, int pos2, int chickyNowX, int chickyNowY, int chickyWillX, int chickyWillY){
		int intercept = interceptX(chickyNowX,chickyNowY,chickyWillX, chickyWillY,inter);
		int segStart = (rangex +1) - (rangex/3);
		int segEnd = rangex;

		if(chickyNowY<=inter && testAt(chickyNowX, segStart, segEnd)){
			destx = pos2;
			desty = pos2;
		} else {
			if(intercept<=segEnd){
				desty = inter;
				if(chickyNowX==intercept)
					destx = chickyNowX;
			} else {
				destx = segStart;
				desty = inter;
				}
		}
	}

	// behavior when chicken is off right top corner
	void at02(int inter, int pos1, int pos2, int chickyNowX, int chickyNowY, int chickyWillX, int chickyWillY){
		int intercept = interceptY(chickyNowX,chickyNowY,chickyWillX, chickyWillY,inter);
		int segStart = 0;
		int segEnd = rangey/3;
		
		if(chickyNowY<=inter && testAt(chickyNowX, segStart, segEnd)){
			destx = rangex-pos2;
			desty = pos2;
			if(chickyWillX<=inter)
				if(chickyNowY<=getY())
					desty = chickyNowY+pos2;
				else
					desty = chickyNowY-pos2;
			else
				if(intercept>=segEnd){
					desty = rangey-inter;
					if(chickyNowX==intercept)
						destx = chickyNowX;
				} else {
					destx = segStart;
					desty = rangey;
					}
			}
	}

	// behavior when chicken is off right center
	void at03(int inter, int pos1, int pos2, int chickyNowX, int chickyNowY, int chickyWillX, int chickyWillY){
		int intercept = interceptY(chickyNowX,chickyNowY,chickyWillX, chickyWillY,inter);
		int segStart = rangey/3 + 1;
		int segEnd = rangey - rangey/3;
		
		if(chickyNowY<=inter && testAt(chickyNowX, segStart, segEnd)){
			destx = rangex-inter;
			if(chickyWillX<=inter)
				if(chickyNowY<=getY())
					desty = chickyNowY+pos2;
				else
					desty = chickyNowY-pos2;
			else
				if(intercept<=chickyNowY){
					destx = rangex;
					desty = intercept+pos2;
				} else {
					destx = rangex;
					desty = intercept-pos2;
				}
		}
	}

	// behavior when chicken is off right bottom corner
	void at04(int inter, int pos1, int pos2, int chickyNowX, int chickyNowY, int chickyWillX, int chickyWillY){
		int intercept = interceptY(chickyNowX,chickyNowY,chickyWillX, chickyWillY,inter);
		int segStart = (rangey +1) - (rangey/3);
		int segEnd = rangey;
		
		if(chickyNowY<=inter && testAt(chickyNowX, segStart, segEnd)){
			destx = rangex-pos2;
			desty = pos2;
			if(chickyWillX<=inter)
				if(chickyNowY<=getY())
					desty = chickyNowY+pos2;
				else
					desty = chickyNowY-pos2;
			else
				if(intercept>=segEnd){
					desty = rangey-inter;
					if(chickyNowX==intercept)
						destx = chickyNowX;
				} else {
					destx = segStart;
					desty = rangey;
					}
			}
	}
	
	// behavior when chicken is below right
	void at05(int inter, int pos1, int pos2, int chickyNowX, int chickyNowY, int chickyWillX, int chickyWillY){
		int intercept = interceptX(chickyNowX,chickyNowY,chickyWillX, chickyWillY,inter);
		int segStart = (rangex +1) - (rangex/3);
		int segEnd = rangex;

		if(chickyNowY<=inter && testAt(chickyNowX, segStart, segEnd)){
			destx = rangex-pos2;
			desty = rangey-pos2;
		} else {
			if(intercept<=segStart){
				desty = rangey-inter;
				if(chickyNowX==intercept)
					destx = chickyNowX;
			} else {
				destx = segStart;
				desty = rangey-inter;
				}
		}
	}
	
	// behavior when chicken is below center
	void at06(int inter, int pos1, int pos2, int chickyNowX, int chickyNowY, int chickyWillX, int chickyWillY){
		inter = rangey-inter;
		int intercept = interceptX(chickyNowX,chickyNowY,chickyWillX, chickyWillY,inter);
		int segStart = rangex/3 + 1;
		int segEnd = rangex - rangex/3;
		
		if(chickyNowY<=(rangey-inter) && testAt(chickyNowX, segStart, segEnd)){
			desty = inter;
			if(chickyWillX<+inter)
				if(chickyNowX<=getX())
					destx = chickyNowX+pos2;
				else
					destx = chickyNowX-pos2;
			else
				if(intercept<=chickyNowX){
					destx = intercept+pos2;
					desty = rangey;
				} else {
					destx = intercept-pos2;
					desty = rangey;
				}
		}
	}	

	// behavior when chicken is below left
	void at07(int inter, int pos1, int pos2, int chickyNowX, int chickyNowY, int chickyWillX, int chickyWillY){
		inter = rangey-inter;
		int intercept = interceptX(chickyNowX,chickyNowY,chickyWillX, chickyWillY,inter);
		int segStart = 0;
		int segEnd = rangex/3;

		if(chickyNowY<=(rangey-inter) && testAt(chickyNowX, segStart, segEnd)){
			destx = pos2;
			desty = rangey-pos2;
		} else {
			if(intercept<=segEnd){
				desty = inter;
				if(chickyNowX==intercept)
					destx = chickyNowX;
			} else {
				destx = segStart;
				desty = inter;
				}
		}
	}
	
	// behavior when chicken is left bottom corner
	void at08(int inter, int pos1, int pos2, int chickyNowX, int chickyNowY, int chickyWillX, int chickyWillY){
		int intercept = interceptY(chickyNowX,chickyNowY,chickyWillX, chickyWillY,inter);
		int segStart = 0;
		int segEnd = rangey/3;
		
		if(chickyNowY<=inter && testAt(chickyNowX, segStart, segEnd)){
			destx = pos2;
			desty = pos2;
			if(chickyWillX<=inter)
				if(chickyNowY<=getY())
					desty = chickyNowY+pos2;
				else
					desty = chickyNowY-pos2;
			else
				if(intercept>=segEnd){
					desty = rangey-inter;
					if(chickyNowX==intercept)
						destx = chickyNowX;
				} else {
					destx = segStart;
					desty = rangey;
					}
			}
	}

	// behavior when chicken is left center
	void at09(int inter, int pos1, int pos2, int chickyNowX, int chickyNowY, int chickyWillX, int chickyWillY){
		int intercept = interceptY(chickyNowX,chickyNowY,chickyWillX, chickyWillY,inter);
		int segStart = rangey/3 + 1;
		int segEnd = rangey - rangey/3;
		
		if(chickyNowY<=inter && testAt(chickyNowX, segStart, segEnd)){
			destx = inter;
			if(chickyWillX<=inter)
				if(chickyNowY<=getY())
					desty = chickyNowY+pos2;
				else
					desty = chickyNowY-pos2;
			else
				if(intercept<=chickyNowY){
					destx = 0;
					desty = intercept+pos2;
				} else {
					destx = 0;
					desty = intercept-pos2;
				}
		}
	}

	// behavior when chicken is left top corner
	void at10(int inter, int pos1, int pos2, int chickyNowX, int chickyNowY, int chickyWillX, int chickyWillY){
		int intercept = interceptY(chickyNowX,chickyNowY,chickyWillX, chickyWillY,inter);
		int segStart = (rangey +1) - (rangey/3);
		int segEnd = rangey;
		
		if(chickyNowY<=inter && testAt(chickyNowX, segStart, segEnd)){
			destx = pos2;
			desty = rangey-pos2;
			if(chickyWillX<=inter)
				if(chickyNowY<=getY())
					desty = chickyNowY+pos2;
				else
					desty = chickyNowY-pos2;
			else
				if(intercept>=segEnd){
					desty = rangey-inter;
					if(chickyNowX==intercept)
						destx = chickyNowX;
				} else {
					destx = segStart;
					desty = rangey;
					}
			}
	}
	
	// behavior when chicken is left top corner
	void at11(int inter, int pos1, int pos2, int chickyNowX, int chickyNowY, int chickyWillX, int chickyWillY){
		int intercept = interceptX(chickyNowX,chickyNowY,chickyWillX, chickyWillY, inter);
		int segStart = 0;
		int segEnd = rangex/3;

		if(chickyNowY<=inter && testAt(chickyNowX, segStart, segEnd)){
			destx = pos2;
			desty = pos2;
		} else {
			if(intercept<=segEnd){
				desty = inter;
				if(chickyNowX==intercept)
					destx = chickyNowX;
			} else {
				destx = segStart;
				desty = inter;
				}
		}
	}
	
	// sorts chicks arrayList based on distance of drone to chicken
	void proximity(){
		int closest = 0;  // holds closest value
		int range = 0;  // holds range value
		int tempIndex = 0;  // temporarily holds index value
		
		if(chicks.size()!=0){  // test to make sure arrayist has objects
			for(int x = 0; x < chicks.size(); x++) {  // swap loop
				closest = distance(getX(),getY(),chickens[chicks.get(0)].getX(),chickens[chicks.get(0)].getY());
				tempIndex = x; 
				for(int y = x; y < chicks.size(); y++) {  // scan across the array for-loop
					range = distance(getX(),getY(),chickens[chicks.get(y)].getX(),chickens[chicks.get(y)].getY());
					if (closest >= range) {  // if to compare; must be >= because it compares to itself, useful if there are repeat values
						closest = range; // resets closest value
						tempIndex = y;  // stores index number
					} // closes if
					int tempChick = chicks.get(tempIndex);  // holds chicken index
					chicks.set(tempIndex, chicks.get(x));  // reassigns 
					chicks.set(x, tempChick);  // held chicken value
				} // close swap for-loop
			}
		}
	}

	// targeting system for the drone, also serves as trigger for sounds
	void targeting(){
		int i = 0;
		if(chickens[mine].cooped){
			if(chicks.size()==0){
				home();
				if (pau==false){
					pau = true;
					ChickenCooped.play();
				}
			} else {
				if(!chickens[chicks.get(i)].cooped)  {
					ChickenCooped.play();
					DroneChase.play();
					mine=chicks.get(i);
					chicks.remove(i);							
					desty = y + (chickens[mine].getY() - getY());
					destx = x + (chickens[mine].getX() - getX());
					whereTheCoopAt(mine);
				}
			}
		} else {
			desty = y + (chickens[mine].getY() - getY());
			destx = x + (chickens[mine].getX() - getX());
			whereTheCoopAt(mine);
		} 
	}

	// re-targets drone if chicken moves off screen
	void reTargeting(){
		chicks.add(mine);  // returns the off-screen chicken to chicks arraylist
		
		if(!chickens[chicks.get(0)].cooped){
			mine=chicks.get(0);
			chicks.remove(0);
			if(borderGuard(chickens[mine].getX(),chickens[mine].getY())) {
				desty = y + (chickens[mine].getY() - getY());
				destx = x + (chickens[mine].getX() - getX());
				whereTheCoopAt(mine);
			} else {
				watchDog();  // behavior when the only chickens are off screen
			}
		}	
	}
		
	// implements movement commands based on destination inputs
	void move(){
		if(borderGuard(destx,desty)){
			degrees = bearing(destx, desty);
			if (y > desty) moveUp(stepSize);
			if (y < desty) moveDown(stepSize);
			if (x > destx) moveLeft(stepSize);
			if (x < destx) moveRight(stepSize);
		}
		setImagePosition(x, y);
	}
	
	// test to make sure the drone stays on screen
	boolean borderGuard(int destx, int desty){
		boolean border = false;
		
		if((destx >= 0 && destx <= 1024) && (desty >= 0 && desty <= 768))
				border = true;
		
		return border;
	}

	// test to make sure the drone stays on screen
	boolean edgeGuard(int destx, int desty){
		boolean edge = false;
		
		if((destx > 80 && destx < 944) && (desty > 80 && desty < 668))
				edge = true;
		
		return edge;
	}
	
	// moves the drone and determines behavior
	void go(){
		chickenUpdate();
		
		proximity();
		
		targeting();
		
		if(!borderGuard(destx,desty))
			reTargeting();
		
		move();
	}

	// deals with chickens that are indirectly/accidently cooped
	void chickenUpdate(){
		for(int i=0;i<chicks.size();i++)
			if(chickens[chicks.get(i)].isCooped())
				chicks.remove(i);  // removes from chicks arrayList
	}	
	
	// finds the degree that the drone is heading
	double bearing(int destx, int desty){
		double d = 0;
		int q = 1;
		int v = 0;
		int theX = 0;
		int theY = 0;
		int ax = getX();  // current position x
		int ay = getY();  // current position y

		if(destx<=ax)
			q = -1; // adjust for quadrant 1
		else
			q = 1;  // adjustment for quadrant 2

		if(desty<=ay){
			// for quadrants 1 & 2
			theX = ((destx - ax)*(destx - ax));  
			theY = ((desty - ay)*(desty - ay));
		} else {
			if(destx<=ax)
				v = 0; // adjust for quadrant 3
			else
				v = 90;  // adjustment for quadrant 4
			// for quadrants 3 & 4			
			theX = ((ax - destx)*(ax - destx));
			theY = ((ay - desty)*(ax - destx));
		} 
		
		d = Math.toDegrees(q*(Math.atan2(theX, theY)))+v;
		return d;
	}
	
	// creates the blue laser to track the chickens destination
	void chickenTracker(){
		cWalk = new EZLine [numberOfChickens];
		
		for(int i = 0; i < numberOfChickens; i++)
			cWalk [i] = EZ.addLine(0, 0, 0, 0, Color.BLUE, 1);
	}
	
	// updates blue laser tracks the chickens destination
	void trackChickens(int c, int chicX, int chicY, int chicDestx, int chicDesty) {
		try
		{
		if (!chickens[c].isCooped()){
			cWalk[c].setPoint1(chicX, chicY);  // sets EZ Line laser to drone's location for Point1; note slightly off center to avoid callSign
			cWalk[c].setPoint2(chicDestx, chicDesty);  // sets to targeted chicken's location
		} else {
			cWalk[c].setPoint2(chicX, chicY);  // sets to targeted chicken's location
		}
		}
		catch (NullPointerException e)
		{
		}
	}
	
	// moves the coop to the center of the screen and changes size of coop plus coop's radius
	void aCoup(int r, double s){
		theCoop.RADIUS = r;
		theCoop.picture.scaleTo(s);
		if((rangex/2) < theCoop.getX())
			if(timeFlies>25){
				theCoop.x -=1;
				timeFlies = 0;
			}	
		timeFlies++;
		theCoop.setImagePosition(theCoop.x, theCoop.y);
	}
	
	// drone hacks chicken destination values (i.e., mind control)
	void fowlPlay(int c, int x, int y){
		chickens[c].destx = x;
		chickens[c].desty = y;
	}
	
	// switch to control the kobiyashimaru hack scenarios
	void kobiyashimaru(int c, int d){
		switch(d){
		case 1:
			break;
		case 2:
			kobiyashimaru1(c);
		case 3: 
			kobiyashimaru2(c);
		case 4: 
			kobiyashimaru3(c);
		case 5:
			kobiyashimaru4(c);
		default:
			break;
		}
	}
	
	// hack scenario 1:  instant win
	void kobiyashimaru1(int c){
		rad = 7000;
		size = 20;
		aCoup(rad, size);
		fowlPlay(c, theCoop.getX(), theCoop.getY());
	}

	// hack scenario 2:  mind control plus slower coop resize and movement
	void kobiyashimaru2(int c){
		aCoup(rad, size);
		fowlPlay(c, theCoop.getX(), theCoop.getY());

		if(timeFlies>30){
			rad  += size;
			size += 0.01;
		}
	
	timeFlies++;
	}

	// hack scenario 3:  mind control and slowest coop movement
	void kobiyashimaru3(int c){
		aCoup(rad, size);
		fowlPlay(c, theCoop.getX(), theCoop.getY());
	}
	
	// hack scenario 4:  just mind control
	void kobiyashimaru4(int c){
		fowlPlay(c, theCoop.getX(), theCoop.getY());
	}
	
}
