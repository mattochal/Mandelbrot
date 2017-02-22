import java.awt.image.BufferedImage;
import java.util.concurrent.Callable;

public class FractalCallable implements Callable<BufferedImage> {
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

		public BufferedImage call() {
			int width = WIDTH_canvas;
			int height = HEIGHT_canvas;
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
			BufferedImage img = new BufferedImage(pixels.length,pixels[0].length,BufferedImage.TYPE_INT_RGB);
			for (int i = 0; i < pixels.length; i++){
				for (int j = 0; j < pixels[0].length; j++){
					img.setRGB(i, j, (int)(pixels[i][j]*20000));
				}
			}
			return img;
		}
	}