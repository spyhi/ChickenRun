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
