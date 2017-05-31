
public class GameLoop {
	//Declare our CPU object
	static CPU myChip8; 

	public static void main(String[] argc){
		//Set up Graphics

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
		//Simple printing for now
		for(int i=0; i<2048; i++){
			if(i%64==0){
				System.out.println();
			}
			if(myChip8.getPixel(i)!=0){
				System.out.print("X");
			}
			else{
				System.out.print(" ");
			}
		}
	}
	

}
