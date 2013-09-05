package multicore;

import java.io.Serializable;

public class Matrix implements Serializable{
	
	int[][] array2D;
	
	public Matrix(int[][] array2D){
		this.array2D = array2D;
	}

	public int[][] getArray2D() {
		return array2D;
	}

	public void setArray2D(int[][] array2d) {
		array2D = array2d;
	}
	
	

}
