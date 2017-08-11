import java.awt.Color;
import javax.swing.JPanel;


public class Graphics extends JPanel{
	
	public void paintGraphics(Graphics g){
		super();
	}
	
	public void paintComponent(Graphics g){
		g.setColor (Color.RED);
        g.fillRect (100, 100, 100, 100);
	  }

}
