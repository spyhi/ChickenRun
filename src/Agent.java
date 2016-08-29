/*
 * Parent class for Chicken, Drone and Coop.
 * Agent class contains member variables and member functions that are shared by the 3 classes.
 */
public class Agent {
	int x;
	int y;
	EZImage picture;
	
	// Get the X location of agent
	int getX(){
		return x;
	}
	
	// Get the Y location of agent
	int getY(){
		return y;
	}
	
	void setPosition(int posx, int posy) {
		x = posx;
		y = posy;
		setImagePosition(x,y);
	}
	void setImagePosition(int posx, int posy) {
		picture.translateTo(posx,posy);
	}
	
	void moveLeft(int step){
		x=x-step;
		setImagePosition(x,y);
	}
	void moveRight(int step){
		x=x+step;
		setImagePosition(x,y);
	}
	void moveUp(int step){
		y=y-step;
		setImagePosition(x,y);
	}
	void moveDown(int step) {
		y=y+step;
		setImagePosition(x,y);
	}
	
	// Calculate distance between two points
	int distance(int x1, int y1, int x2, int y2){	
		return (int) Math.sqrt(((x1-x2)*(x1-x2))+((y1-y2)*(y1-y2)));
	}
	
	// Is there another agent (chicken or drone or coop) near this current agent?
	boolean isNearBy(Agent anAgent, int dist){
		if (distance(anAgent.getX(),anAgent.getY(), getX(), getY()) < dist)
			return true;
		else return false;
	}
	// Not used
	boolean isInside(int posx, int posy){
			return picture.isPointInElement(posx,posy);
	}
}
