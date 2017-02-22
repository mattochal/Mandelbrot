import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

public class ImageSetCallable implements Callable<HashMap<Integer,BufferedImage>>{
	BufferedImage original;
	int iter;
	
	public ImageSetCallable(BufferedImage original, int iter){
		this.original = original;
		this.iter = iter;
	}
	
	public HashMap<Integer,BufferedImage> call() {
		HashMap<Integer,BufferedImage> imageSet = new HashMap<Integer,BufferedImage>();
		int no = 10;
		final int FACTOR  = 100;
		
		for (int i =0; i<no;i++){
		    int scaleX = (int) (original.getWidth() * 1.0 * (FACTOR+i)/FACTOR);
		    int scaleY = (int) (original.getHeight() * 1.0 * (FACTOR+i)/FACTOR);
		    Image image = original.getScaledInstance(scaleX, scaleY, Image.SCALE_SMOOTH);
		    BufferedImage buffered = new BufferedImage(scaleX, scaleY, BufferedImage.TYPE_INT_RGB);
		    buffered.getGraphics().drawImage(image, 0, 0 , null);
		    //buffered = buffered.getSubimage(0, 0, original.getWidth(), original.getHeight());
		    imageSet.put(i, buffered);
		}
		return imageSet;
	}
}
