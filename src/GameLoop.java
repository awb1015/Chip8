
public class GameLoop {
	//Declare our CPU object
	static CPU myChip8;
	static Display myDisplay;

	public static void main(String[] argc){
		//Set up Graphics
		myDisplay = new Display();
		//Set up Input

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
			myChip8.setKeys();
		}
	}

	public static void drawGraphics(){
		//Attempt at a real display
		myDisplay.updateDisplay(myChip8.getGraphicsMatrix());		
		//And also simple printing for now
		for(int i=0; i<2048; i++){
			if(i%64==0){
				System.out.println();
			}
			if(myChip8.getPixel(i)!='\u0000'){
				System.out.print("X");
			}
			else{
				System.out.print(" ");
			}
			
		}
		System.out.println("graphics updated?");
		try        
		{
		    Thread.sleep(100);
		} 
		catch(InterruptedException ex) 
		{
		    Thread.currentThread().interrupt();
		}
		myChip8.setDrawFlag(false);
	}
	

}
