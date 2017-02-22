import java.awt.Image;
import java.awt.image.BufferedImage;

public class ImageEnlarger {
	
	public static Image getFrame(BufferedImage img){
		int width = img.getWidth();
		int height = img.getHeight();
		return img.getScaledInstance(width+1,height+1, Image.SCALE_SMOOTH);
	}
}
