import java.util.Random;

// The chicken class controls the artificial intelligence of the chicken for the Drone vs Chicken program
// The class extends the Agent class
public class Chicken extends Agent {
	
	static int CHICKENVISION = 100;		// How far in the distance the chicken can see
	static int CHICKENEXCITEDNESS = 3;	// How far it runs away when it sees a drone
	static int STEPSIZE = 1;
	
	int destx, desty;					// Target destination of chicken
	
	boolean cooped = false;				// Whether the chicken is cooped or not
	
	// Variables to allow chicken to know where the drones are
	int numberOfDrones = 0;
	Drone[] drones;
	
	
	// Constructor for chicken
	Chicken() {
		Random randomGenerator = new Random();
		x = randomGenerator.nextInt(800);
		y = randomGenerator.nextInt(768);
		
		destx = randomGenerator.nextInt(800);
		desty = randomGenerator.nextInt(768);
		
		picture = EZ.addImage("chicken.png", x, y);
	}
	
	void introduceDronesToChicken(Drone[] drn, int ndrones){
		drones = drn;
		numberOfDrones = ndrones;
	}
	
	void setRandomDirection(){
		Random randomGenerator = new Random();
		destx = randomGenerator.nextInt(800);
		desty = randomGenerator.nextInt(768);			
	}
	
	void setCooped(boolean coop){
		cooped = coop;
	}
	
	boolean isCooped(){
		return cooped;
	}
	
	// Main intelligence of the chicken
	// Call this repeatedly, once per main loop cycle to keep the chicken going.
	void go(){
		
		// If the chicken is cooped already then do nothing.
		if (isCooped()) return;
		
		Random randomGenerator = new Random();
		
		// Move the chicken to its destination
		if (x > destx) moveLeft(STEPSIZE);
		if (x < destx) moveRight(STEPSIZE);
		if (y > desty) moveUp(STEPSIZE);
		if (y < desty) moveDown(STEPSIZE);
		
		// If reached destination set a new destination
		if ((x == destx) && (y == desty)) {
			setRandomDirection();
		}

		// Have the chicken look at each drone and figure out if it is nearby.
		// If it is then set a destination to move away from it.
		for (int i = 0; i < numberOfDrones; i++) {
			if (isNearBy(drones[i], CHICKENVISION)) {
				destx = x - (drones[i].getX() - getX())* CHICKENEXCITEDNESS;// * randomGenerator.nextInt(5);
				desty = y - (drones[i].getY() - getY())* CHICKENEXCITEDNESS;// * randomGenerator.nextInt(5);
			}
		}
	}
	
	void setImagePosition(int posx, int posy) {
		if (!isCooped()){
			super.setImagePosition(posx, posy);
		}
	}

}
