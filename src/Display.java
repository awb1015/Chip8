import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;

public class Display extends JFrame implements KeyListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static int width;
	private static int height;
	private int type;
	private HashMap<Character, Integer> keymap;
	private static int[] keyBuffer;
	public Graphics basicgfx = new Graphics();
	
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
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setSize(width, height);
	    frame.setVisible(true);
	    frame.setTitle("Chip 8 Emulator");
		frame.addKeyListener(this);
		frame.setFocusable(true);
		basicgfx = new Graphics();
		frame.setContentPane(basicgfx);
		repaint();
	    pack();
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
