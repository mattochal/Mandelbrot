import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class CallableExample {

	public static final int WIDTH_canvas = 720, HEIGHT_canvas = 648;
	public static final int WIDTH_toolbox = 300, HEIGHT_toolbox = HEIGHT_canvas;

	public static class FractalCallable implements Callable<double[][]> {
		public static final int WIDTH_canvas = 720, HEIGHT_canvas = 648;
		public static final int WIDTH_toolbox = 300, HEIGHT_toolbox = HEIGHT_canvas;
		
		private double minX,  maxX,  minY,  maxY;
		private int maxit = 500;

		public FractalCallable(double minX, double maxX, double minY, double maxY, int max) {
			this.maxX = maxX;
			this.minX = minX;
			this.maxY = maxY;
			this.minY = minY;
			maxit = max;
		}
		
		public double getWidth() {
			return maxX - minX;
		}

		public double getHeight() {
			return maxY - minY;
		}

		public double[][] call() {
			int width = CallableExample.WIDTH_canvas;
			int height = CallableExample.HEIGHT_canvas;
			Complex z, c;
			double[][] pixels = new double[width][height];
			int iter;// iterations

			double pixelWidth = getWidth() / width;
			double pixelHeight = getHeight() / height;

			for (int j = 0; j < height; j++) {
				for (int i = 0; i < width; i++) {

					z = new Complex(i * pixelWidth + minX, j * pixelHeight + minY);
					c = new Complex(i * pixelWidth + minX, j * pixelHeight + minY);
					iter = maxit;
					while (z.modulusSquared() < 4 && iter > 0) {
						z.square();
						z.add(c);
						iter--;
					}

					pixels[i][j] = iter * 1.0 / maxit;
				}
			}
			return pixels;
		}
	}
	
	static List<Future<double[][]>> set = new ArrayList<Future<double[][]>>();

	public static void main(String args[]) throws Exception {
		ExecutorService pool = Executors.newFixedThreadPool(4);
		double sminX = -2;
		double smaxX = 2;
		double sminY = -2;
		double smaxY = 2;
		double eminX = -1.3235589134172723;
		double emaxX = -1.3235589134172552;
		double eminY = -0.057566894633859110;
		double emaxY = -0.057566894633846800;
		
		double zoom = 0.9; // zoom
		int iter = 1;
		for (int i =0 ; i < iter; i++){
			sminX = eminX - (eminX - sminX)*zoom;
			smaxX = emaxX - (emaxX - smaxX)*zoom;
			sminY = eminY - (eminY - sminY)*zoom;
			smaxY = emaxY - (emaxY - smaxY)*zoom;
			Callable<double[][]> callable = new FractalCallable(sminX,smaxX,sminY,smaxY,100+i);
			Future<double[][]> future = pool.submit(callable);
			CallableExample.set.add(future);
		}
		
		int count = 0;
		JFrame frame = new JFrame(String.valueOf(count));
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(CallableExample.WIDTH_canvas,CallableExample.HEIGHT_canvas));
		frame.setMinimumSize(new Dimension(CallableExample.WIDTH_canvas,CallableExample.HEIGHT_canvas));
		frame.setPreferredSize(new Dimension(CallableExample.WIDTH_canvas,CallableExample.HEIGHT_canvas));
		
		frame.setTitle(String.valueOf(count));
		CanvasPanel panel =  new CanvasPanel(CallableExample.set.get(0).get());
		frame.setContentPane(panel);
		panel.setLayout(null);
		frame.revalidate();
		for (int j = 1; j > 0; j--) {
			panel.zoom(1);
		}
		//Thread.sleep(100);
		/*
		for (Future<double[][]> f : CallableExample.set){
			count++;
			frame.setTitle(String.valueOf(count));
			panel =  new CanvasPanel(f.get());
			frame.setContentPane(panel);
			panel.setLayout(null);
			frame.revalidate();
			Thread.sleep(100);
		}*/
		
	}
}