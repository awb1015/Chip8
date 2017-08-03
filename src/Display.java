import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Display extends JFrame implements KeyListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static int width;
	private static int height;
	private int type;
	private static BufferedImage image;
	private static JLabel label;
	private HashMap<Character, Integer> keymap;
	private static int[] keyBuffer;
	
	//Constructor
	public Display(){
		//Set Display sizes
		int width = 640;
		int height = 320;
		int type = BufferedImage.TYPE_INT_ARGB;
		//Initialise and populate keymap
		keymap = new HashMap<>();		
		fillkeymap();
		keyBuffer = new int[16];
		//Now need a key listener as well

		BufferedImage image = new BufferedImage(width, height, type);
		Icon icon = new ImageIcon(image);
		JLabel label = new JLabel(icon);


		int color = 255; // RGBA value, each component in a byte

		for(int x = 0; x < width; x++) {
		    for(int y = 0; y < height; y++) {
		        image.setRGB(x, y, color);
		    }
		}
		JFrame frame = new JFrame();
		frame.getContentPane().add(label);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setSize(width, height);
	    frame.setVisible(true);
	    frame.setTitle("Chip 8 Emulator");
		frame.addKeyListener(this);
		frame.setFocusable(true);
	    repaint();
	    pack();
	}
	
	public void updateDisplay(byte[] gfx){
		for(int i = 0; i < width; i++) {
		    for(int j = 0; j < height; j++) {
		        if(gfx[(i*(j+1))]!= 0){
		        	//Then set pixel to white
		        	image.setRGB(i, j, 255);
		        }
		        else{
		        	//Then the pixel is black
		        	image.setRGB(i, j, 0);
		        }
		        //Now draw rectangles 10x

		    }
		}
		repaint();
		
	}
	
	private void fillkeymap(){
		keymap.put('1', 1);
		keymap.put('2', 2);
		keymap.put('3', 3);
		keymap.put('q', 4);
		keymap.put('w', 5);
		keymap.put('e', 6);
		keymap.put('r', 7);
		keymap.put('s', 8);
		keymap.put('d', 9);
		keymap.put('z', 0xA);
		keymap.put('x', 0);
		keymap.put('c', 0xB);
		keymap.put('4', 0xC);
		keymap.put('r', 0xD);
		keymap.put('f', 0xE);
		keymap.put('v', 0xF);
	}
	
	public void keyPressed(KeyEvent e){
		//If the input is valid we now add to the keybuffer
		if(keymap.get(e.getKeyChar()) != null) {
			keyBuffer[keymap.get(e.getKeyChar())] = 1;
		}
	}
	
	public void keyReleased(KeyEvent e) {
		//Similar to before, we update the keybuffer
		if(keymap.get(e.getKeyChar()) != null) {
			keyBuffer[keymap.get(e.getKeyChar())] = 0;
		}
	}
	
	public void keyTyped(KeyEvent e) {	
	}
	
	public int[] getKeyBuffer(){
		return keyBuffer;
	}

}
