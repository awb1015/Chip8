//Chip8 has 2-byte Op-codes which works well for a char
import java.util.Stack;

public class CPU {
	
	//fields
	private char[] memory;
	private char[] V;
	private short opcode;
	//Index register
	private short I;
	//Program counter
	private short pc;
	//Array to store state of pixels in display
	private char[] gfx;
	//We also need a stack to recall the location from before the instruction set calls for a jump
	private Stack<Short> stack; 
	private char delay_timer;
	private char sound_timer;
	private boolean drawFlag;
	
	//Constructor
	public CPU(){
		//Initialize registers and memory
		memory = new char[4096];
		V = new char[16];
		//Program counter starts at 0x200, below this value is font set
		pc = 0x200;
		opcode = 0;
		I = 0;
		stack = new Stack<Short>();
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
		
		case 0x1000: //1NNN This Jumps to the address at NNN
			//Unlike 0x2NNN there's no need to store the program count to the stack
			pc = (short)(opcode & 0x0FFF);
		break;
		
		case 0x2000: //2NNN This calls the subroutine at NNN
			//Store our current state in the stack
			stack.push(pc);
			pc = (short) (opcode & 0x0FFF);
		break;
		
		case 0x3000: //3XNN This skips the next code block if V[X] equals NN
			if((opcode & 0x00FF) == V[(opcode & 0x0F00)>>8]){
				pc += 4;
			}
			else{
				pc +=2;
			}
		break;
		
		case 0x4000: //4XNN This skips the next instruction if V[x]!=NN
			if((opcode & 0x00FF) != V[(opcode & 0x0F00)>>8]){
				pc += 4;
			}
			else{
				pc += 2;
			}
		break;
		
		case 0x5000: //5XY0 Skips the next instruction if Vx = Vy
			if(V[((opcode & 0x0F00)>>8)] == V[((opcode & 0x00F0) >> 4)]){
				pc+=4;
			}
			else{
				pc+=2;
			}
		break;
		
		case 0x6000: //6XNN Sets Vx to NN
			V[((opcode & 0x0F00) >> 8)] =(char) (opcode & 0x00FF);
			pc+=2;
		break;
		
		case 0x7000: //7XNN Adds NN to Vx
			V[((opcode & 0x0F00)>>8)] = (char)((V[((opcode & 0x0F00)>>8)]) + (char)(opcode & 0x00FF));		
		break;
		
		//Now a fair amount of cases for our 8XXX series
		case 0x8000:
			//Now compare last four bits
			switch(opcode & 0x000F)
			{
				case 0x0000: //8XY0 Sets Vx to value of Vy
					//Assignment here
				break;
				
				case 0x0001: // 8XY1 Sets Vx to Vx or Vy
					//Bit-wise op here
				break;
				
				case 0x0002: //8XY2 Sets Vx to Vx and Vy
					//Bit-wise op here
				break;
				
				case 0x0003: //8XY3 Sets Vx to Vx XOR Vy
					//Bit-wise op here
				break;
				
				case 0x0004: //8XY4 Adds Vx to Vy if there is a carry set VF=1
					//Addition and if statement in here
				break;
				
				case 0x0005: //8XY5 Vy is subtracted from Vx. Set VF=1 when there is a borrow
					//Substraction and if statement in here
				break;
				
				case 0x0006: //8XY6 bit shift Vx by on. Vf is set to least significant bit of Vx before shift
					//bit shift here
				break;
				
				case 0x0007: //8XY7 Vx = Vy - Vx Set Vf to 0 when there is a borrow
					//Substraction and if statement in here
				break;
				
				case 0x000E: //8XYE Shifts Vx left by one. Vf is set to most significant bit prior to shift
					//Bitwise and VF set here
				break;
					
				default:
					System.out.println("Unknown opcode at 0x8000" + opcode);
			
			}
		
		case 0x9000: //9XY0 Skips the next instruction if Vx!=Vy
			if(V[((opcode & 0x0F00)>>8)] != V[((opcode & 0x00F0) >> 4)]){
				pc+=4;
			}
			else{
				pc+=2;
			}
		break;
		
		case 0xA000: //ANNN Sets I to the address NNN
			I = (short) (opcode & 0x0FFF);
			//Increase program counter to next pair of 2 bits
			pc += 2;
		break;
		
		case 0xB000: //BNNN Jumps to address NNN +V0
			//This is WRONG
			pc = (opcode & 0x0FFF) + V[0];			
		break;
		
		case 0x0000:
			//Now we need to compare the last four bits
			switch (opcode & 0x000F)
			{
				//This case below has a last 4 bits of 0 and first four of zero
				case 0x0000: // 0x00E0: Clear Screen
					//Something here to clear the screen
					drawFlag = true;
				break;
				
				case 0x000E: // 0x00EE : Returns from subroutine
					//We need to restore address value from the stack
					//Presumably no need to increase pc by two as this has been done before it was pushed to the stack
					pc = stack.pop();
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
	
	public boolean getDrawFlag(){
		
		//Simple return statement to allow for things to play nicely
		return drawFlag;
	}
	
	//Set our keys here
	public void setKeys(){
	
	}
	
}
