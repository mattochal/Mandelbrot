import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class CanvasPanel extends JPanel {

	private static ExecutorService fixedPool = Executors.newFixedThreadPool(4);

	// ConcurrentHashMap stores an integer corresponding to the order of the
	// calculated frame
	// Stores the integer of the next image
	public static ConcurrentHashMap<Integer, BufferedImage> imageList = new ConcurrentHashMap<Integer, BufferedImage>();

	public void setDimensions(int width, int height) {

	}

	public void go(double sminX, double smaxX, double sminY, double smaxY, double eminX, double emaxX, double eminY, double emaxY) throws InterruptedException, ExecutionException{
		// loop until minX == maxX or minY == maxY
		double zoom = 0.8; // zoom
		int iter = 5;
		
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
		
		for (Integer a : MultithreadManager.imageList.keySet()){
			System.out.println("painting image: " + a.intValue());
			setImg(MultithreadManager.imageList.get(a));
			repaint();
			Thread.sleep(30);
		}
		
	}
	
	private BufferedImage img;
	private boolean b = true;

	public CanvasPanel(){
		img = new BufferedImage(CallableExample.WIDTH_canvas, CallableExample.HEIGHT_canvas,
				BufferedImage.TYPE_INT_RGB);
	}
	
	private synchronized BufferedImage getImg(){
		return img;
	}
	
	private synchronized void setImg(BufferedImage img){
		this.img = img;
	}
	
	boolean draw = false;
	
	@Override
	public void paint(Graphics g) {
			g.drawImage(getImg(), 0, 0, this);
	}
	
	public void zoom() throws Exception {
		int i = 0;
		
		/*MultithreadManager.go(-2, 2, -1.6, 1.6, 0.5, 1.5, -0.4, -1.2);
		//draw = true;
		for (Integer a : MultithreadManager.imageList.keySet()){
			System.out.println("painting image: " + a.intValue());
			setImg(MultithreadManager.imageList.get(a));
			repaint();
			Thread.sleep(10);
		}
		
		repaint();
		/*while (true){//!MultithreadManager.imageList.isEmpty()){
			System.out.println("There are images: " + MultithreadManager.imageList.mappingCount());
			Thread.sleep(1000);
			img = MultithreadManager.imageList.get(i);
			System.out.println(img.getHeight());
			
			i++;
			/*frame.setTitle(String.valueOf(i));
			while (!MultithreadManager.imageList.containsKey(i)){
				Thread.sleep(10);
			}
			img = MultithreadManager.imageList.get(i);
			repaint();
			Thread.sleep(50);
			i++;*/
		
	}
	
	static JFrame frame = new JFrame();
	
	public static void main(String[] arg) throws Exception{
		CanvasPanel panel =  new CanvasPanel();
		SwingUtilities.invokeLater(new Runnable(){

			@Override
			public void run() {
				frame.setVisible(true);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setPreferredSize(new Dimension(CallableExample2.WIDTH_canvas,CallableExample2.HEIGHT_canvas));
				frame.setMinimumSize(new Dimension(CallableExample2.WIDTH_canvas,CallableExample2.HEIGHT_canvas));
				frame.setPreferredSize(new Dimension(CallableExample2.WIDTH_canvas,CallableExample2.HEIGHT_canvas));
				
				frame.setContentPane(panel);
				panel.setLayout(null);
				frame.revalidate();
				
			}
		});
		panel.go(0, 0, 0, 0, 0, 0, 0, 0);
		
	}
}