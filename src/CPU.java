
import java.util.Stack;
import java.io.*;

public class CPU {

	//fields
	private char[] memory;
	private char[] V;
	private char opcode;
	//Index register
	private char I;
	//Program counter
	private char pc;
	//Array to store state of pixels in display
	private byte[] gfx;
	//We also need a stack to recall the location from before the instruction set calls for a jump
	private Stack<Character> stack; 
	private int delay_timer;
	private int sound_timer;
	private boolean drawFlag;
	private byte[] key;

	//Constructor
	public CPU(){
		//Initialize registers and memory
		memory = new char[4096];
		V = new char[16];
		//Program counter starts at 0x200, below this value is font set
		pc = 0x200;
		opcode = 0;
		I = 0x0;
		delay_timer = 0;
		sound_timer = 0;
		drawFlag = false;
		stack = new Stack<Character>();
		//Load font set to memory
		for(int i=0; i<80; i++){
			memory[0x50 & i]= (char)(FontSet.getFontSetEntry(i) & 0xFF);
		}
		gfx = new byte[64*32];
		key = new byte[16];
		//Hacky way to load our rom for now
		loadinMem();
	}

	//Emulate cycle
	public void emulateCycle(){
		//Fetch Opcode (not sure this is OK with type cast)
		opcode = (char) (memory[pc] << 8 | memory[pc+1]);
		System.out.println("The opcode is " +Integer.toHexString(opcode));

		//Decode Opcode & Execute Opcode
		//Just check the first four bits for our cases
		switch(opcode & 0xF000){

		case 0x1000: //1NNN This Jumps to the address at NNN
			//Unlike 0x2NNN there's no need to store the program count to the stack
			pc = (char)(opcode & 0x0FFF);
			break;

		case 0x2000: //2NNN This calls the subroutine at NNN
			//Store our current state in the stack
			stack.push(pc);
			pc = (char) (opcode & 0x0FFF);
			break;

		case 0x3000: //3XNN This skips the next code block if V[X] equals NN
			if((int)(opcode & 0x00FF) == V[(int)(opcode & 0x0F00)>>8]){
				pc += 4;
			}
			else{
				pc +=2;
			}
			break;

		case 0x4000: //4XNN This skips the next instruction if V[x]!=NN
			if((int)(opcode & 0x00FF) != V[(int)(opcode & 0x0F00)>>8]){
				pc += 4;
			}
			else{
				pc += 2;
			}
			break;

		case 0x5000: //5XY0 Skips the next instruction if Vx = Vy
			if(V[(int)((opcode & 0x0F00)>>8)] == V[(int)((opcode & 0x00F0) >> 4)]){
				pc+=4;
			}
			else{
				pc+=2;
			}
			break;

		case 0x6000: //6XNN Sets Vx to NN
			V[(int)((opcode & 0x0F00) >> 8)] = (char)(opcode & 0x00FF);
			pc+=2;
			break;

		case 0x7000: //7XNN Adds NN to Vx
			int _x = (opcode & 0x0F00)>>8;
			int nn = opcode & 0x00FF;
			V[_x] = (char)((V[_x]+nn)& 0xFF);
			pc +=2;
			break;

			//Now a fair amount of cases for our 8XXX series
		case 0x8000:
			//Now compare last four bits
			switch(opcode & 0x000F)
			{
			case 0x0000: //8XY0 Sets Vx to value of Vy
				V[(int)((opcode & 0x0F00)>>8)] = V[(int)((opcode & 0x00F0)>>4)];
				pc +=2;
				break;

			case 0x0001: // 8XY1 Sets Vx to Vx or Vy
				V[(int)((opcode & 0x0F00)>>8)] = (char) (((V[(int)((opcode & 0x0F00)>>8)])|(V[(int)((opcode & 0x00F0)>>4)])) & 0xFF);
				pc +=2;
				break;

			case 0x0002: //8XY2 Sets Vx to Vx and Vy
				V[((opcode & 0x0F00)>>8)] = (char) ((V[(int)((opcode & 0x0F00)>>8)]) & (V[(int)((opcode & 0x00F0)>>4)]));
				pc +=2;
				break;

			case 0x0003: //8XY3 Sets Vx to Vx XOR Vy
				V[(int)((opcode & 0x0F00)>>8)] = (char) (((V[(int)((opcode & 0x0F00)>>8)]) ^ (V[(int)((opcode & 0x00F0)>>4)])) & 0xFF);
				pc +=2;
				break;

			case 0x0004: //8XY4 Adds Vx to Vy if there is a carry set VF=1
				if(V[(int)((opcode & 0x00F0)>>4)]>(0xFF - V[(int)((opcode & 0x0F00)>>8)])){
					//Then we need to carry
					V[0xF] = 1;
				}
				else{
					V[0xF] = 0;
				}
				V[(int)((opcode & 0x0F00)>>8)] = (char) ((V[(int)((opcode & 0x0F00)>>8)] + V[(int)((opcode & 0x00F0)>>4)]) & 0xFF);
				pc +=2;
				break;

			case 0x0005: //8XY5 Vy is subtracted from Vx. Set VF=0 when there is a borrow
				if(V[(int)(opcode & 0x00F0) >> 4] > V[(int)(opcode & 0x0F00) >> 8]){
					V[0xF] = 0;
				}
				else{
					V[0xF] = 1;
				}
				V[(int)((opcode & 0x0F00)>>8)] = (char) ((V[(int)((opcode & 0x0F00)>>8)] - V[(int)((opcode & 0x00F0)>>4)]) & 0xFF);
				pc +=2;
				break;

			case 0x0006: //8XY6 bit shift Vx by one. Vf is set to least significant bit of Vx before shift
				V[0xF] = (char) ((V[(int)((opcode & 0x0F00)>>8)]) & 0x1);
				V[(int)((opcode & 0x0F00)>>8)] = (char) ((V[(int)((opcode & 0x0F00)>>8)])>>1);
				pc+=2;
				break;

			case 0x0007: //8XY7 Vx = Vy - Vx Set Vf to 0 when there is a borrow
				if(V[(int)(opcode & 0x0F00) >> 8] > V[(int)(opcode & 0x00F0) >> 4]){
					V[0xF] = 0;
				}
				else{
					V[0xF] = 1;
				}
				V[(int)((opcode & 0x0F00)>>8)] =  (char) ((V[(int)((opcode & 0x00F0)>>4)] - V[(int)((opcode & 0x0F00)>>8)]) & 0xFF);
				pc+=2;
				break;

			case 0x000E: //8XYE Shifts Vx left by one. Vf is set to most significant bit prior to shift
				V[0xF] = (char) (((V[(int)((opcode & 0x0F00)>>8)]) & 0x80)>>8);
				V[(int)((opcode & 0x0F00)>>8)] = (char) ((V[(int)((opcode & 0x0F00)>>8)])<<1);
				pc+=2;
				break;

			default:
				System.out.println("Unknown opcode at 0x8000" + opcode);

			}
			break;

		case 0x9000: //9XY0 Skips the next instruction if Vx!=Vy
			if(V[(int)((opcode & 0x0F00)>>8)] != V[(int)((opcode & 0x00F0) >> 4)]){
				pc+=4;
			}
			else{
				pc+=2;
			}
			break;

		case 0xA000: //ANNN Sets I to the address NNN
			I = (char) (opcode & 0x0FFF);
			pc += 2;
			break;

		case 0xB000: //BNNN Jumps to address NNN +V0
			pc = (char)(((int)opcode & 0x0FFF)+ ((int)(V[0] & 0xFF)));
			break;

		case 0xC000: //CXNN Sets Vx to be result of bitwise OR between NN and Random Num
			V[(int)((opcode & 0x0F00)>>8)] = (char) (((opcode & 0x00FF) | (0+ (int)(Math.random() * ((255-0) + 1))))); 
			pc+= 2;
			break;

		case 0xD000: //DXYN Draws a sprite at VX, VY that is 8 pixels wide and N pixels high
			int x = V[(opcode & 0x0F00) >> 8];
			int y = V[(opcode & 0x00F0) >> 4];
			int height = opcode & 0x000F;
			int pixel;

			V[0xF] = 0;
			for (int yline = 0; yline < height; yline++)
			{
				pixel = memory[I + yline];
				for(int xline = 0; xline < 8; xline++)
				{
					if((pixel & (0x80 >> xline)) != 0)
					{
						if(gfx[(x + xline + ((y + yline) * 64))] == 1){
							V[0xF] = 1;
						}                              
						gfx[x + xline + ((y + yline) * 64)] ^= 1;
					}
				}
			}
			drawFlag = true;
			pc += 2;
			break;

		case 0xE000: //Two 0xE000 opcodes here, time for a deeper level of case
			switch(opcode & 0x000F){


			case 0x000E: //EX9E Skips the next instruction if the key at Vx is pressed
				int x_ = (opcode & 0x0F00)>>8;
				int keypress = V[x_];
				if(key[keypress]==1){
					pc+=4;
				}
				else{
					pc+=2;
				}
				break;

			case 0x0001: //EXA1 Skips the next instruction if the key stored at Vx isn't pressed
				if(key[(int)V[(int)(opcode & 0x0F00) >> 8]] == 0){
					pc+=4;
				}
				else{
					pc+=2;
				}
				break;


			default:
				System.out.println("Unknown Opcode [0xE000] : 0x" + opcode);

			}
			break;

		case 0xF000:
			switch (opcode & 0x00FF)
			{
			case 0x0007: //FX07 Sets Vx to the Value of the delay timer
				V[(int)((opcode & 0x0F00)>>8)] = (char) delay_timer;
				pc+=2;
				break;

			case 0x000A: //FX0A A key press is awaited and then stored at Vx. All instruction halted until key press
				boolean keyPress = false;
				for(int i=0; i<key.length; i++){
					if(key[i]!=0){
						V[(int)(opcode & 0x0F00) >> 8] = (char) i;
						keyPress = true;
					}
				}
				//If there's no key press we return without iterating the program counter
				if(!keyPress){
					return;
				}
				pc+=2;				
				break;

			case 0x0015: //FX15 Set delay timer to Vx
				delay_timer = V[(int)((opcode & 0x0F00)>>8)];
				pc+=2;
				break;

			case 0x0018: //FX18 Set the sound timer to Vx
				sound_timer = V[(int)((opcode & 0x0F00)>>8)];
				pc+=2;
				break;

			case 0x001E: //FX1E Add Vx to I
				I = (char) (I + V[(int)((opcode & 0x0F00)>>8)]);
				pc+=2;
				break;

			case 0x0029: //FX29 Set I to the location of the sprite for the character in Vx
				I = (char) ((((int)(V[(int)(opcode & 0x0F00) >> 8]))*5) + 0x050);
				pc+=2;
				break;

			case 0x0033: //FX33 Decimal representation of VX, place the hundreds digit in memory at location in I, the tens digit at location I+1, and the ones digit at location I+2
				memory[I]     = (char) (V[(int)(opcode & 0x0F00) >> 8] / 100);
				memory[I + 1] = (char) ((V[(int)(opcode & 0x0F00) >> 8] / 10) % 10);
				memory[I + 2] = (char) ((V[(int)(opcode & 0x0F00) >> 8] % 100) % 10);
				pc+=2;
				break;

			case 0x0055: //FX55 Stores V[0] to V[x] in memory starting at address i
				for(int i=0; i<=((int)((opcode & 0x0F00)>>8)); i++){
					memory[I + i] = V[i];
				}
				I = (char) (I + ((opcode & 0x0F00)>>8) + 1);
				pc+=2;
				break;

			case 0x0065: //FX65 Fills V[0] to V[x] with values from memory begining at I
				for(int i=0; i<= ((int)((opcode & 0x0F00)>>8)); i++){
					V[i] = memory[I +i];
				}
				I = (char) (I + ((int)((opcode & 0x0F00)>>8))+ 1);
				pc+=2;
				break;

			default:
				System.out.println("Unknown Opcode [0xF000" + opcode);

			}
			break;

		case 0x0000:
			//Now we need to compare the last four bits
			switch (opcode & 0x000F)
			{

			case 0x0000: // 0x00E0: Clear Screen
				for(int i=0; i<gfx.length; i++){
					gfx[i] = 0;
				}
				drawFlag = true;
				pc+=2;
				break;


			case 0x000E: // 0x00EE : Returns from subroutine
				//We need to restore address value from the stack
				pc = stack.pop();
				pc+=2;
				break;

			default:
				System.out.println("Unknown Opcode [0x0000]: 0x" + opcode);

			}
			break;

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

	public void setDrawFlag(boolean drawFlag){
		drawFlag = this.drawFlag;
	}

	//Set our keys here
	public void setKeys(int[] keybuffer){
		//update keyarray
		for(int i=0; i<key.length; i++){
			key[i] = (byte) keybuffer[i];
		}

	}

	public byte getPixel(int i){
		return gfx[i];
	}

	public byte[] getGraphicsMatrix(){
		return gfx;
	}

	public void loadinMem(){
		//Fixed location and hacky loader
		DataInputStream input = null;
		try {
			input = new DataInputStream(new FileInputStream("c:\\c8roms\\INVADERS"));

			int offset = 0;
			while(input.available() > 0) {
				memory[0x200 + offset] = (char)(input.readByte() & 0xFF);
				offset++;
			}
			System.out.println("Rom Loaded");

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		} finally {
			if(input != null) {
				try { input.close(); } catch (IOException ex) {}
			}
		}
	}

}
