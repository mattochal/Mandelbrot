import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MultithreadManager {
	private static ExecutorService fixedPool = Executors.newFixedThreadPool(4);

	// ConcurrentHashMap stores an integer corresponding to the order of the
	// calculated frame
	// Stores the integer of the next image
	public static ConcurrentHashMap<Integer, BufferedImage> imageList = new ConcurrentHashMap<Integer, BufferedImage>();

	public void setDimensions(int width, int height) {

	}

	public static void go(double sminX, double smaxX, double sminY, double smaxY, double eminX, double emaxX, double eminY, double emaxY) throws InterruptedException, ExecutionException{
		// loop until minX == maxX or minY == maxY
		double zoom = 0.8; // zoom
		int iter = 3;
		
		double focusX = (eminX+emaxX)/2;
		double focusY = (eminY+emaxY)/2;
		
		double pminX = sminX = -2;
		double pmaxX = smaxX = 2;
		double pminY = sminY = -2;
		double pmaxY = emaxY = 2;
		eminX = -1.3235589134172723;
		emaxX = -1.3235589134172552;
		eminY = -0.057566894633859110;
		emaxY = -0.057566894633846800;
		
		int off = 0;
		for (int i =0 ; i < iter; i++){
			sminX = eminX - (eminX - sminX)*zoom;
			smaxX = emaxX - (emaxX - smaxX)*zoom;
			sminY = eminY - (eminY - sminY)*zoom;
			smaxY = emaxY - (emaxY - smaxY)*zoom;
			FractalCallable callable = new FractalCallable(sminX,smaxX,sminY,smaxY,100+i);
			Future<BufferedImage> future = fixedPool.submit(callable);
			BufferedImage img = future.get();
			imageList.put(off, img);
			System.out.println((off) + " Image created");
			off += 1;
			ImageSetCallable imageset = new ImageSetCallable(img, off);
			Future<HashMap<Integer,BufferedImage>> futureImages = fixedPool.submit(imageset);
			for (int a =0; a<10; a++){
				MultithreadManager.imageList.put(off+a,  futureImages.get().get(a));
				System.out.println((off+a) + " Image created");
			}
			off+=10;
			pminX = sminX;
			pmaxX = smaxX;
			pminY = sminY;
			pmaxY = emaxY;
		}
	}
}