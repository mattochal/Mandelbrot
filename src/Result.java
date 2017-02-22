class Result {
		int order;
		double[][] pixels;
		public Result(int order, double[][] pixels){
			this.order = order;
			this.pixels = pixels;
		}
		public double[][] getPixels(){
			return this.pixels;
		}
		public int getOrder(){
			return order;
		}
}
