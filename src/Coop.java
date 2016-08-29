// This class holds the information about the Coop and also keeps track of how many
// chicken are in the coop for the Drone vs Chicken program.

public class Coop extends Agent {
	
	Chicken[] chickens;		// Chickens the coop needs to keep track of
	int numberOfChickens;
	
	static int RADIUS = 70;	// Radius in which a chicken is considered "inside" the coop
	
	// Constructor - create the coop image and put it on the screen
	Coop(){
		x = 1024 - RADIUS*2;
		y = 768/2;
		
		picture = EZ.addImage("coop.png", x, y);
		picture.translateTo(x,y);
	}
	
	// Give the coop the array of chicken it needs to keep track of.
	void introduceChickensToCoop(Chicken[] chk, int num){
		chickens = chk;
		numberOfChickens=num;
	}
	
	// Count number of chickens that have been cooped.
	int numberOfChickensInCoop(){
		int count = 0;
		for (int i = 0; i < numberOfChickens; i++){
			if (isNearBy(chickens[i], RADIUS)) {
				chickens[i].setCooped(true);
			}
			if (chickens[i].isCooped()) count++;
		}
		return count;
	}
}
