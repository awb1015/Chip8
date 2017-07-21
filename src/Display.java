import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Display {
	private static int width;
	private static int height;
	private int type;
	private static BufferedImage image;
	private static JLabel label;
	
	//Constructor
	public Display(){
		//Set Display sizes
		int width = 640;
		int height = 320;
		int type = BufferedImage.TYPE_INT_ARGB;

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
	}
	
	public void updateDisplay(byte[] gfx){
		for(int i = 0; i < width; i++) {
		    for(int j = 0; j < height; j++) {
		        if(gfx[(i*(j+1))]!= 0){
		        	//Then set pixel to white
		        	image.setRGB(255, 255, 255);
		        }
		        else{
		        	//Then the pixel is black
		        	image.setRGB(0, 0, 0);
		        }
		        //Now draw rectangles 10x
		    }
		}
		
	}

}
