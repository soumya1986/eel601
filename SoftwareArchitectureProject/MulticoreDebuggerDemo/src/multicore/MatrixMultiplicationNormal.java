package multicore;

import java.util.Date;
import java.util.Random;

public class MatrixMultiplicationNormal {
	
	private static int numOfRowsOrCols = 2048;
	static Random rand = new Random();
	
	public static void main(String args[]){
		
		long lStartTime = new Date().getTime();
		
		
		int[][] matrix_A = create2DMatrix();
		int[][] matrix_B = create2DMatrix();
		
		int[][] matrix_C = multiply_matrix(matrix_A, matrix_B);
		
		long lEndTime = new Date().getTime();
    	long difference = lEndTime - lStartTime;
    	System.out.println("Elapsed milliseconds: " + difference);
		//display2DMatrix(matrix_C);
	}
	
	private static void display2DMatrix(int[][] matrix){
		System.out.println("\n");
		for(int i = 0; i < matrix.length; i++) {
            for(int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
	}
	
	private static int[][] create2DMatrix(){
		int[][] matrix = new int[numOfRowsOrCols][numOfRowsOrCols];
		
		for (int row = 0; row < numOfRowsOrCols; row++) {
            for (int col = 0; col < numOfRowsOrCols; col++) {
            	matrix[row][col] = rand.nextInt(10);
            }
        }
		
		//display2DMatrix(matrix);
		return matrix;
	}
	
	private static int[][] multiply_matrix(int a[][], int b[][]){
		int[][] c = new int[numOfRowsOrCols][numOfRowsOrCols];
		for(int i=0;i<numOfRowsOrCols;i++)
		{
			for(int j=0;j<numOfRowsOrCols;j++)
			{
				for(int k=0;k<numOfRowsOrCols;k++)
				{
					c[i][j]=c[i][j]+(a[i][k]*b[k][j]);
				}
			}
		}
		
		return c;
		
	}

}
