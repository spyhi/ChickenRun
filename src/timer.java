// imports
import java.awt.Color;  // needed for EZ Text

//timer version 2 (stopwatch version per Prof request)
public class timer { // opens class
	  
	  // member variables
	  private long start = 0;  // stores initial start time
	  private long stop = 0;  // stores time value when stopwatch is stopped
	  private boolean run = false;  // stop on/off, default off
	  private int finaly = EZ.getWindowHeight()/2;  //Y location for final text
	  
	  EZText board=EZ.addText(512, 30, "", Color.YELLOW, 25);  // displays time
	  EZText scoreCoop = EZ.addText(900, 30, "", Color.YELLOW,25);  // displays number of cooped chickens 
	  EZText scoreFree = EZ.addText(905, 60, "", Color.YELLOW,25);  // displays number of uncooped chickens
	  EZText dronesWin = EZ.addText(512, EZ.getWindowHeight()/3, "DRONES WIN!", Color.WHITE, 100); //Win Text
	  	  
	  //the timer constructor
	  public timer(){
		run = false;
	  }
	  
	  //starts the stopwatch function
	  public void startTimer() {
		run = true;  // turns stopwatch on
	    start = System.currentTimeMillis(); 
	  }

	  //stops the stopwatch (will reset when started again)
	  public void stopTimer() {
	  if (run)
		  stop = System.currentTimeMillis();  // stores last time value
	    run = false;  // turns stopwatch off
	    board.color=Color.WHITE;  // turns display font color white when off
	    scoreCoop.hide();
	    scoreFree.hide();
	    int count = 0;
	    for (int i=30; i <= finaly; i++){ //Move final time to center
	    	board.translateTo(512, i);
	    	board.fontSize = i/7;
	    }
	    dronesWin.show(); // Displays final message
	  }
  	  
	  //java records time in milliseconds
	  public long getMSeconds() {
		  long runTime;  // stores run time
		  
		  if (run)  // while on
			  runTime = (System.currentTimeMillis() - start);  // gets current time
		  else  // while off 
			  runTime = (stop - start);  // time after time stop
		  
		  return runTime;  // returns the stop watch time whether on/off
	  }
	  
	  // inserts a 0 in front of value less than 10
	  public String update0(int value){
		  String temp = ""; 
		  
		  if(value<10)
			  temp = "0" + value;
		  else
			  temp = "" + value;
		  
		  return temp;		
	  }

	  // inserts a 0 in front of value less than 10
	  public String update00(int value){
		  String temp = ""; 
		  
		  if(value<10)
			  temp = "00" + value;
		  else if(value<100)
			  temp = "0" + value;
		  else
			  temp = "" + value;
		  
		  return temp;		
	  }
	  
	  // changes the word "minutes" to "minute" if only 1 minute has elapsed
	  public String updateMin(int min){
		  String temp = ""; 
		  
		  if(min==1)
			  temp = "minute";  // when minute equals 1
		  else
			  temp = "minutes";  // all other minute values
		  
		  return temp;		
	  }
	  
	  //converts recorded time into minutes and seconds
	  public String getTimeElapsed() {
		  int minutes;
		  int seconds;
		  long runTime = getMSeconds();  // calls function to get time elapsed value
		  String timeElapsed="";
	    
	      minutes = ((int) runTime/1000)/60;
	      seconds = ((int) runTime/1000) - (minutes*60);
	      timeElapsed = minutes + " " + updateMin(minutes) + " " + update0(seconds) + " seconds";

	      return timeElapsed;
	  }
 
	  // runs the updated timer and score info into the EZ Text Objects
	  public void displayDashBoard(int chickens, int coop) {
		  	board.msg = getTimeElapsed();  // feeds up-to-date time info into display board
			scoreCoop.msg = "Cooped: " + update00(coop);
			scoreFree.msg = "    Free: " + update00(chickens-coop);
		
			board.pullToFront();
			scoreCoop.pullToFront();
			scoreFree.pullToFront();
			
			// stops timer when all chickens are cooped
			if(chickens==coop) 
				stopTimer();
	  }
	  
	  // Print Line test for timer
	  public void printTimer() {
		  System.out.println(getMSeconds());
	  }
	  
}  // closes class
