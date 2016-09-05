# ChickenRun
##*Two simple bots fight each other: How fast can the drones get the chickens back in the coops?*
*Made with Jesus Gonzalez and Michael Kurihara*

The final project for ICS 111 at the University of Hawaii at Manoa with Jason Leigh. The task was to make "drone" bots that would herd chicken bots with random movement properties to the coop as quickly as possible. The bots were then run in class to determine which one was the fastest.

##Analyzing the Problem
To build our drones, we were allowed to analyze the chicken code to determine the best approach. While the movement properties were random, we discovered that chickens had a predictable reaction to the drones:

```java
// Have the chicken look at each drone and figure out if it is nearby.
// If it is then set a destination to move away from it.
for (int i = 0; i < numberOfDrones; i++) {
	if (isNearBy(drones[i], CHICKENVISION)) {
		destx = x - (drones[i].getX() - getX())* CHICKENEXCITEDNESS;// * randomGenerator.nextInt(5);
		desty = y - (drones[i].getY() - getY())* CHICKENEXCITEDNESS;// * randomGenerator.nextInt(5);
	}
}
```
Basically, if a drone is within the chicken's limit of vision (CHICKENVISION), it should set a destination in the opposite direction of the drone for a variable set by CHICKENEXCITEDNESS. We were able to exploit this to build a drone AI which would scare the chickens in a straight line to the coop.
```java
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
```
#Other Key Ideas
One problem we ran into is that drones would often swarm to a single chicken, causing unpredicable behavior and often causing the chicken to escape all the drones, which would simply start the process over again, so we implemented a checkout system, where each drone would choose a "target chicken" by removing it from a shared queue, preventing drones from chasing the same chicken.
```java
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
```
And in order to optimize the time slightly, we implemented a function that would reorganize the queue by proximity each round so the drone would always select, check out, and herd the closest chicken to the coop.
```java
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
```
There was much more code to handle corner cases, since the chicken bots were allowed to wander off-screen and the drones eventually ran out of chickens to chase, but these features were the heart of our little AI. 

And while this was our "in the spirit of competition" solution, there is also a lot of code devoted to "hacking" the chickens and coop, since they were left with default privacy and we were able to manipulate their values directly from the drone class, which was technically allowed in the rules, since we were told to only modify the drone code. In the end, our AI won fair and square, but our teacher said he would have allowed the hacks, too.

[See a video of the drone AI in action!](https://youtu.be/bpXoYn5SS7I)

*Note: The graphics were made with EZ, which was developed at the University of Hawaii at Manoa by Dylan Kobayashi to help students get into working with UIs and making "interesting" apps earlier in their college careers. **All rights are reserved for EZ.java**
