import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GameLoop {
	//Declare our CPU object
	static CPU myChip8;
	static Display myDisplay;

	public static void main(String[] argc){
		//Set up Graphics and input
		myDisplay = new Display();
		//Initialize CPU Object
		myChip8 = new CPU();
		//Load Game
		emulationLoop();
	}

	private static void emulationLoop(){
		while(true){
			myChip8.emulateCycle();

			//Check for draw flag
			if(myChip8.getDrawFlag()){
				drawGraphics();
			}
			//Check for inputs and update
			myChip8.setKeys(myDisplay.getKeyBuffer());
			//Wait to prevent too fast a refresh rate
			try        
			{
			    Thread.sleep(16);
			} 
			catch(InterruptedException ex) 
			{
				//This should never get thrown
			    Thread.currentThread().interrupt();
			}
		}
	}

	public static void drawGraphics(){
		//Attempt at a real display
	
		//And also simple printing for now
		byte [] gfx = myChip8.getGraphicsMatrix();
		for(int i=0; i<gfx.length; i++){
			if(gfx[i]==0){
				System.out.print(" ");
			}
			else{
				System.out.print("X");
			}
			if(i%64==0){
				System.out.println("");
			}
		}
		myChip8.setDrawFlag(false);
	}
	

}
