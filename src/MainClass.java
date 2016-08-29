import java.awt.Color;


// Main program class for Drone vs Chicken program

// This program includes multiple builds for resolving the chicken problem: One normal, and four hacks
// Choice 1 is the regular version without exploiting public apis
// Choice 2 is an insta-win condition exploiting publicly accessible member functions and variables
// Choice 3 is the same as before only slower
// Choice 4 the coop moves, but does not resize
// Choice 5, the chickens are "hypnotized" and move to the coop on their own. This is the theoretical
// max time under the "normal" conditions.


public class MainClass {  // open class
	// member variables
	static int NUMBER_OF_CHICKENS = 10;
	static int NUMBER_OF_DRONES = 5;
	static int HEIGHT = 768;
	static int WIDTH = 1024;
	static int scenario = 0;
	static int choices = 5;

	static Chicken [] chickens;
	static Coop theCoop;
	static Drone [] drones;
	static timer time;
	static Repeat choose = new Repeat();
	
	// initializes the EZ graphics system, sets background, and calls the timer constructor
	static void readySet(){
		// Setup EZ graphics system.
		EZ.initialize(1024, 768);
		
		// Draw the road.
		EZ.addImage("field.jpg", 1024 / 2, 768 / 2);
		
		EZSound intro = EZ.addSound("gitemboys.wav");
		EZSound themeSong = EZ.addSound("ChickenTheme.wav");
		intro.play();
		themeSong.loop();

	}
	
	// calls the constructor for the chickens, the coop, and the drones; introduces everyone to each other;
	static void introducePlayers(){
		// Make chickens
		chickens = new Chicken[NUMBER_OF_CHICKENS];
		for (int i = 0; i < NUMBER_OF_CHICKENS; i++)
			chickens[i] = new Chicken();
		
		// Make coop
		theCoop = new Coop();
		
		// Make drones
		drones = new Drone[NUMBER_OF_DRONES];
		for (int i = 0; i < NUMBER_OF_DRONES; i++)
			drones[i] = new Drone(i, WIDTH, HEIGHT);
		
		// Make the drones aware of the chickens
		for (int i = 0; i < NUMBER_OF_DRONES; i++){
			drones[i].introduceChickensToDrone(chickens, NUMBER_OF_CHICKENS);
			drones[i].introduceDronesToDrone(drones, NUMBER_OF_DRONES);
			drones[i].introduceCoopToDrone(theCoop);
			drones[i].droneVision(i);  // scans field for chicken location and stores in chicks ArrayList
		}
		
		drones[0].chickenTracker();
		
		// Make the chickens aware of the drones
		for (int i=0; i < NUMBER_OF_CHICKENS; i++){
			chickens[i].introduceDronesToChicken(drones,NUMBER_OF_DRONES);
		}	
		
		// Make the coop aware of the chickens
		theCoop.introduceChickensToCoop(chickens,NUMBER_OF_CHICKENS);
	}
	
	// starts the timer and the program
	static void action(){
		time = new timer();  //call timer constructor
		time.startTimer();  // starts timer
		time.dronesWin.hide();
		
		while(true){  // open infinite while-loop

			// displays stopwatch and score board
			time.displayDashBoard(NUMBER_OF_CHICKENS, theCoop.numberOfChickensInCoop());

			// Control the drones
			for (int i = 0; i < NUMBER_OF_DRONES; i++)
				drones[i].go();
		
			// Control the chickens
			for (int i=0; i < NUMBER_OF_CHICKENS; i++){
				chickens[i].go();
				//drones[0].trackChickens(i, chickens[i].getX(), chickens[i].getY(), chickens[i].destx,chickens[i].desty);
				drones[0].kobiyashimaru(i,scenario);
			}
			
			EZ.refreshScreen();  // refresh EZ Graphics
		}  // closes infinite while-loop
	}

	static int switcher(){
		EZText intro=EZ.addText(500, 300, "Choose Your Adventure!", Color.yellow, 40);
		EZText introOptions=EZ.addText(500, 360, ("Press keys 1 - "+choices), Color.yellow, 40);
		int s=0;
		boolean go=false;
		while(!go){
			for(int i=0;i<choices+1;i++){
				if(EZInteraction.wasKeyPressed(choose.key(i))) {
					go=true;
					s=choose.keyI(i);
				}
			}
		}
		intro.hide();
		introOptions.hide();
		return s;
		}
	
	public static void main(String[] args) {  // open main
		while(true){
		// initialize EZ Graphics, set background, ready timer
		readySet();
		
		// calls the Chickens, the Coop, the Drones and introduces them to each other
		introducePlayers();
		
		scenario = switcher();
		
		// runs the program and starts the timer
		action();
		}
	}  // close main
	
}  // close class
