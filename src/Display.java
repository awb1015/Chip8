import java.awt.image.BufferedImage;
import javax.swing.JFrame;

public class Display {
	private static int width;
	private static int height;
	private int type;
	private static BufferedImage image;
	
	//Constructor
	public Display(){
		//Set Display sizes
		int width = 64;
		int height = 32;
		int type = BufferedImage.TYPE_INT_ARGB;

		BufferedImage image = new BufferedImage(width, height, type);

		int color = 255; // RGBA value, each component in a byte

		for(int x = 0; x < width; x++) {
		    for(int y = 0; y < height; y++) {
		        image.setRGB(x, y, color);
		    }
		}
		JFrame frame = new JFrame();
		frame.getContentPane().add(image);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setSize(width, height);
	    frame.setVisible(true);
	}
	
	public void updateDisplay(char[] gfx){
		for(int x = 0; x < width; x++) {
		    for(int y = 0; y < height; y++) {
		        if(gfx[(x*(y+1))]!= 0){
		        	//Then set pixel to white
		        	image.setRGB(255, 255, 255);
		        }
		        else{
		        	//Then the pixel is black
		        	image.setRGB(0, 0, 0);
		        }
		    }
		}
	}

}
