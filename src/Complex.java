
public class Complex {
	private double r, i; // real and imaginary parts of the complex number

	public Complex(double real, double imaginary) {
		r = real;
		i = imaginary;
	}

	public double getReal() {
		return r;
	}

	public double getImaginary() {
		return i;
	}

	public void setReal(double real) {
		r = real;
	}

	public void setImaginary(double imaginary) {
		i = imaginary;
	}

	// Square the imaginary number
	public void square() {
		double tmp = r * r - i * i;
		i = 2.0 * r * i;
		r = tmp;
	}

	// Work out the modulus squared of the complex number
	public double modulusSquared() {
		return r * r + i * i;
	}
	
	public void squareModulus(){
		double tmp = r * r - i * i;
		i = 2.0 * Math.abs(r) * Math.abs(i);
		r = tmp;
	}

	// Add another complex number to the complex number
	public void add(Complex z) {
		r = r + z.getReal();
		i = i + z.getImaginary();
	}
}
