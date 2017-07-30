import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GameLoop {
	//Declare our CPU object
	static CPU myChip8;
	static Display myDisplay;
	static int[] keybuffer;
	static int[] keyID;


	public static void main(String[] argc){
		//Set up Graphics
		myDisplay = new Display();
		//Set up Input
		keybuffer = new int[16];
		keyID = new int[256];
		initialiseInput();
		//Initialize system

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

			//Check for inputs
			myChip8.setKeys(keybuffer);
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
		myDisplay.updateDisplay(myChip8.getGraphicsMatrix());		
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
	
	public static void initialiseInput(){
		for(int i=0; i<keyID.length; i++){
			keyID[i] = -1;
		}
		keyID['1'] = 1;
		keyID['2'] = 2;
		keyID['3'] = 3;		
		keyID['Q'] = 4;
		keyID['W'] = 5;
		keyID['E'] = 6;
		keyID['A'] = 7;
		keyID['S'] = 8;
		keyID['D'] = 9;
		keyID['Z'] = 0xA;
		keyID['X'] = 0;
		keyID['C'] = 0xB;
		keyID['4'] = 0xC;
		keyID['R'] = 0xD;
		keyID['F'] = 0xE;
		keyID['V'] = 0xF;
	}

}
