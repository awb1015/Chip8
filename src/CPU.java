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
	//Perhaps it will be better to emulate a stack with a stack rather than an array of shorts... to be determined
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
		//Program counter starts at 0x200, below this value is font set
		pc = 0x200;
		opcode = 0;
		I = 0;
		sp = 0;
		//Initialize our stack and stack pointer
		stack = new short[16];
		sp = 0;
		
		//Load font set to memory
		for(int i=0; i<80; i++){
			memory[i]= FontSet.getFontSetEntry(i);
		}
	}
	
	//Emulate cycle
	public void emulateCycle(){
		//Fetch Opcode (not sure this is OK with type cast)
		opcode = (short) (memory[pc] << 8 | memory[pc+1]);
		
		//Decode Opcode & Execute Opcode
		//At the moment this is set-up as a case structure perhaps a hashtable is better?
		//Just check the first four bits for our cases
		switch(opcode & 0xF000){
		
		case 0x2000: //2NNN This calls the subroutine at NNN
			//Store our current state in the stack
			stack[sp] = pc;
			++sp;
			pc = (short) (opcode & 0x0FFF);
		break;
		
		case 0xA000: //ANNN Sets I to the address NNN
			I = (short) (opcode & 0x0FFF);
			//Increase program counter to next pair of 2 bits
			pc += 2;
		break;
		
		case 0x0000:
			//Now we need to compare the last four bits
			switch (opcode & 0x000F)
			{
				//This case below has a last 4 bits of 0 and first four of zero
				case 0x0000: // 0x00E0: Clear Screen
					//Something here to clear the screen
				break;
				
				case 0x000E: // 0x00EE : Returns from subroutine
					//Execute opcode
					//We need to restore address value from the stack then reduce the stack pointer
					//Then set the program counter to the correct value
				break;
				
				default:
					System.out.println("Unknown Opcode [0x0000]: 0x" + opcode);
			}
		
						
		default:
			System.out.println("Unknown OpCode at 0x" + opcode);
		
		}
		
		//Update Timers
		if(delay_timer > 0){
			--delay_timer;
		}
		
		if(sound_timer > 0){
			if(sound_timer == 1){
				//Or some real sound one day
				System.out.println("BEEP");
			}
			--sound_timer;
		}
		
	}
	
	public boolean drawFlag(){
		
		//Simple return statement to allow for things to play nicely
		return true;
	}
	
	//Set our keys here
	public void setKeys(){
	
	}
	
}
