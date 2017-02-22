/*import java.util.concurrent.Callable;

public class CallableFactalTask implements Callable<double[][]>{

	private FractalAxisPlane axisPlane;
	private int orderID, width, height;
	
	public CallableFactalTask(int orderID, FractalAxisPlane axisPlane, int width, int height){
		this.orderID = orderID;
		this.axisPlane = axisPlane;
		this.height = height;
		this.width = width;
	}
	
	public int getOrderID(){
		return orderID;
	}

	@Override
	public double[][] call() throws Exception {
		double[][] pixels = axisPlane.getPixelValues(width, height);
		return pixels;
	}	
}
*/