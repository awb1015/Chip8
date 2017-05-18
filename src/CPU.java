//Chip8 has 2-byte Op-codes which works well for a char


public class CPU {
	//fields
	char[] memory;
	char[] V;
	short opcode;
	//Index register
	short I;
	//Program counter
	short pc;
	//Array to store state of pixels in display
	char[] gfx;
	//We also need a stack to recall the location from before the instruction set calls for a jump
	short[] stack;
	//And a pointer to remember our stack position
	short sp;
	char delay_timer;
	char sound_timer;
	
	//Constructor
	public CPU(){
		//Initialize registers and memory
		memory = new char[4096];
		V = new char[16];
		//Program counter starts at 0x200
		pc = 0x200;
		opcode = 0;
		I = 0;
		sp = 0;
		//Initialize our stack and stack pointer
		stack = new short[16];
		sp = 0;
	}
	
	//Emulate cycle
	public void emulateCycle(){
		//Fetch Opcode (not sure this is OK with type cast)
		opcode = (short) (memory[pc] << 8 | memory[pc+1]);
		
		//Decode Opcode
		
		//Execute Opcode
		
		//Update Timers
		
	}
	
	public boolean drawFlag(){
		
		//Simple return statement to allow for things to play nicely
		return true;
	}
	
	//Set our keys here
	public void setKeys(){
	
	}
	
}
