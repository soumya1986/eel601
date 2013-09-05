package multicore;


import java.util.Date;
import java.util.Random;

import mpi.*;

public class MatrixMultiplicationOnRing {
	
	private static int numOfRowsOrCols = 1024;
	private static int totalNumberOfElements;
	
	private static int[][] matrix_B = new int[numOfRowsOrCols][numOfRowsOrCols];
	
	static Random rand = new Random();
	
	static int rank = 0; //The current process.
    static int size = 0; //Total number of processes
    static int peer = 0; 
    static int tag = 100;
    
    static int myNumberOfElements; 
    static int factor;
    static int myElementsOfMatrixA_1D[];
    static int myElementsOfMatrixB_1D[];
    static int myElementsOfMatrixA_2D[][];
    static int myElementsOfMatrixB_2D[][];
    static int resultMatrix_2D[][];
    static int receivedMatrix_1D[];
    
    static boolean initializedMatrixA = false;
    static boolean initializedMatrixB = false;
    static boolean initializedResultMatrix = false;
    
	
	public static void main(String args[]){
		
		MPI.Init(args); 
		rank = MPI.COMM_WORLD.Rank() ; //The current process.
        size = MPI.COMM_WORLD.Size() ; //Total number of processes
        
        
        
        totalNumberOfElements = numOfRowsOrCols*numOfRowsOrCols;
        factor = numOfRowsOrCols/size; // number of rows or cols to hold by each processor 
        myNumberOfElements = numOfRowsOrCols*factor; // number of elements to hold from each matrix
        
        //System.out.println(numOfRowsOrCols+";"+size+";"+myNumberOfElements);
        
        myElementsOfMatrixA_1D = new int[myNumberOfElements]; // each processor will hold a part of the matrix A
        myElementsOfMatrixB_1D = new int[myNumberOfElements]; // each processor will hold a part of the matrix B
        
        myElementsOfMatrixA_2D = new int[factor][numOfRowsOrCols]; // each processor will hold a part of the matrix A
        myElementsOfMatrixB_2D = new int[numOfRowsOrCols][factor]; // each processor will hold a part of the matrix B
        
        receivedMatrix_1D = new int[totalNumberOfElements];
        
        /*
        if(rank == 100){
        // create and display the matrix
		int[][] array2d = create2DMatrix();
		display2DMatrix(array2d);
		int[] array1d = convertTo1Dfrom2D(array2d);
		displayArray(array1d);
		int[][] array2d_1 = convertTo2Dfrom1D(array1d);
		display2DMatrix(array2d_1);
		
        }
        */
        
        long lStartTime = new Date().getTime();
    	
        distributeFirstMatrix();
        /*
        for(int k=0;k<size;k++){
        	distributeSecondMatrix(k);
        }
        */
        
        long lEndTime = new Date().getTime();
    	long difference = lEndTime - lStartTime;
    	System.out.println("Elapsed milliseconds: " + difference);
	}
	
	private static void distributeFirstMatrix(){
		if(rank == 0) { 
        	 
			 int[][] matrix_A = create2DMatrix();
			 display2DMatrix(matrix_A);
			 
			 
			 int[] matrix_A_1D = convertTo1Dfrom2D_RowMajor(matrix_A);
			 
			 getMyElementsOfMatrix(matrix_A_1D, rank);
			 
			 peer = 1;
             MPI.COMM_WORLD.Ssend(matrix_A_1D, 0, totalNumberOfElements, MPI.INT, peer, tag) ; 
             
             peer = size -1 ; 
             MPI.COMM_WORLD.Ssend(matrix_A_1D, 0, totalNumberOfElements, MPI.INT, peer, tag) ; 
             
         }
         
         else if(rank == (size -1)) { 
        	 
        	 Status status;
        	 
        	 int[] matrix_A_1D = new int[totalNumberOfElements]; 
        	 peer = 0;
        	 status = MPI.COMM_WORLD.Recv(matrix_A_1D, 0, totalNumberOfElements, MPI.INT, peer, tag); 
        	 
        	 getMyElementsOfMatrix(matrix_A_1D, rank);
        	 
             peer = (size -2);
             MPI.COMM_WORLD.Ssend(matrix_A_1D, 0, totalNumberOfElements, MPI.INT, peer, tag) ; 
         }
         
         else if(rank == (size/2)) { 
        	 
        	 Status status;
        	 
        	 int[] matrix_A_1D = new int[totalNumberOfElements]; 
        	 peer = size/2-1;
        	 status = MPI.COMM_WORLD.Recv(matrix_A_1D, 0, totalNumberOfElements, MPI.INT, peer, tag); 
        	 
        	 getMyElementsOfMatrix(matrix_A_1D, rank);
        	 
         }
         
         else if(rank == (size/2 + 1)) { 
        	 
        	 Status status;
        	 
        	 int[] matrix_A_1D = new int[totalNumberOfElements]; 
        	 peer = size/2+2;
        	 status = MPI.COMM_WORLD.Recv(matrix_A_1D, 0, totalNumberOfElements, MPI.INT, peer, tag); 
        	 
        	 getMyElementsOfMatrix(matrix_A_1D, rank);
        	 
         }
         
         else if((rank >= 1) && (rank <= (size/2 -1))) { 
        	 
        	 Status status;
        	 
        	 int[] matrix_A_1D = new int[totalNumberOfElements]; 
        	 peer = rank - 1;
        	 status = MPI.COMM_WORLD.Recv(matrix_A_1D, 0, totalNumberOfElements, MPI.INT, peer, tag); 
        	 
        	 getMyElementsOfMatrix(matrix_A_1D, rank);
        	 
             peer = (rank + 1);
             MPI.COMM_WORLD.Ssend(matrix_A_1D, 0, totalNumberOfElements, MPI.INT, peer, tag) ; 
             
         }
         
         else{
        	 
        	 Status status;
        	 
        	 int[] matrix_A_1D = new int[totalNumberOfElements]; 
        	 peer = rank + 1;
        	 status = MPI.COMM_WORLD.Recv(matrix_A_1D, 0, totalNumberOfElements, MPI.INT, peer, tag); 
        	 
        	 getMyElementsOfMatrix(matrix_A_1D, rank);
        	              
             peer = (rank - 1);
             MPI.COMM_WORLD.Ssend(matrix_A_1D, 0, totalNumberOfElements, MPI.INT, peer, tag) ; 
        	 
         }
 
	}
	
	private static void distributeSecondMatrix(int index){
		if(rank == 0) { 
			
			initializeMatrixB();
			
			initializeResultMatrix();
			 
			 int[] matrix_B_1D = convertTo1Dfrom2D_ColumnMajor(matrix_B);
			 
			 //System.out.println("************ "+matrix_B_1D.length);
			 int[] matrix_B_1D_sub = new int[myNumberOfElements];
			 
			 // populate with columns of second matrix
			 for(int i=0;i<myNumberOfElements;i++){
				 matrix_B_1D_sub[i] = matrix_B_1D[i + index*myNumberOfElements];
			 }
			 
			 
			 peer = 1;
             MPI.COMM_WORLD.Ssend(matrix_B_1D_sub, 0, myNumberOfElements, MPI.INT, peer, tag) ; 
             
             peer = size -1 ; 
             MPI.COMM_WORLD.Ssend(matrix_B_1D_sub, 0, myNumberOfElements, MPI.INT, peer, tag) ; 
             
             int[] resultMatrix_1D = popupate(matrix_B_1D_sub, index);
             
             MPI.COMM_WORLD.Recv(receivedMatrix_1D, 0, totalNumberOfElements, MPI.INT, peer, peer); 
             resultMatrix_1D = add(resultMatrix_1D, receivedMatrix_1D);
             
             peer = 1;
             MPI.COMM_WORLD.Recv(receivedMatrix_1D, 0, totalNumberOfElements, MPI.INT, peer, peer); 
             
             resultMatrix_1D = add(resultMatrix_1D, receivedMatrix_1D);
             
             display2DMatrix(convertTo2Dfrom1D_RowMajor(resultMatrix_1D, numOfRowsOrCols, numOfRowsOrCols));
             
         }
         
         else if(rank == (size -1)) { 
        	 
        	 Status status;
        	 
        	 initializeResultMatrix();
        	 
        	 int[] matrix_B_1D_sub = new int[totalNumberOfElements]; 
        	 peer = 0;
        	 status = MPI.COMM_WORLD.Recv(matrix_B_1D_sub, 0, totalNumberOfElements, MPI.INT, peer, tag); 
        	 
             peer = (size -2);
             MPI.COMM_WORLD.Ssend(matrix_B_1D_sub, 0, totalNumberOfElements, MPI.INT, peer, tag) ; 
             
             int[] resultMatrix_1D = popupate(matrix_B_1D_sub, index);
             
             MPI.COMM_WORLD.Recv(receivedMatrix_1D, 0, totalNumberOfElements, MPI.INT, peer, peer); 
             resultMatrix_1D = add(resultMatrix_1D, receivedMatrix_1D);
             
             peer = 0;
             MPI.COMM_WORLD.Ssend(resultMatrix_1D, 0, totalNumberOfElements, MPI.INT, peer, rank) ; 
             
             
         }
         
         else if(rank == (size/2)) { 
        	 
        	 Status status;
        	 
        	 initializeResultMatrix();
        	 
        	 int[] matrix_B_1D_sub = new int[totalNumberOfElements]; 
        	 
        	 peer = size/2-1;
        	 status = MPI.COMM_WORLD.Recv(matrix_B_1D_sub, 0, totalNumberOfElements, MPI.INT, peer, tag); 
        	 
        	 int[] resultMatrix_1D = popupate(matrix_B_1D_sub, index);      
        	 
        	 MPI.COMM_WORLD.Ssend(resultMatrix_1D, 0, totalNumberOfElements, MPI.INT, peer, rank) ; 
        	 
         }
         
         else if(rank == (size/2 + 1)) { 
        	 
        	 initializeResultMatrix();
        	 
        	 Status status;
        	 
        	 int[] matrix_B_1D_sub = new int[totalNumberOfElements]; 
        	 peer = size/2+2;
        	 status = MPI.COMM_WORLD.Recv(matrix_B_1D_sub, 0, totalNumberOfElements, MPI.INT, peer, tag); 
        	 
        	 int[] resultMatrix_1D = popupate(matrix_B_1D_sub, index);
        	 
        	 MPI.COMM_WORLD.Ssend(resultMatrix_1D, 0, totalNumberOfElements, MPI.INT, peer, rank) ; 
        	 
         }
         
         else if((rank >= 1) && (rank <= (size/2 -1))) { 
        	 
        	 initializeResultMatrix();
        	 
        	 Status status;
        	 
        	 int[] matrix_B_1D_sub = new int[totalNumberOfElements]; 
        	 peer = rank - 1;
        	 status = MPI.COMM_WORLD.Recv(matrix_B_1D_sub, 0, totalNumberOfElements, MPI.INT, peer, tag); 
        	 
             peer = (rank + 1);
             MPI.COMM_WORLD.Ssend(matrix_B_1D_sub, 0, totalNumberOfElements, MPI.INT, peer, tag) ; 
             
             int[] resultMatrix_1D = popupate(matrix_B_1D_sub, index);
             
             MPI.COMM_WORLD.Recv(receivedMatrix_1D, 0, totalNumberOfElements, MPI.INT, peer, peer); 
             resultMatrix_1D = add(resultMatrix_1D, receivedMatrix_1D);
             
             peer = rank - 1;
             MPI.COMM_WORLD.Ssend(resultMatrix_1D, 0, totalNumberOfElements, MPI.INT, peer, rank) ; 
         }
         
         else{
        	 
        	 Status status;
        	 
        	 initializeResultMatrix();
        	 
        	 int[] matrix_B_1D_sub = new int[totalNumberOfElements]; 
        	 peer = rank + 1;
        	 status = MPI.COMM_WORLD.Recv(matrix_B_1D_sub, 0, totalNumberOfElements, MPI.INT, peer, tag); 
        	 
             peer = (rank - 1);
             MPI.COMM_WORLD.Ssend(matrix_B_1D_sub, 0, totalNumberOfElements, MPI.INT, peer, tag) ; 
             
             int[] resultMatrix_1D = popupate(matrix_B_1D_sub, index);
             
             MPI.COMM_WORLD.Recv(receivedMatrix_1D, 0, totalNumberOfElements, MPI.INT, peer, peer); 
             resultMatrix_1D = add(resultMatrix_1D, receivedMatrix_1D);
             
             peer = rank + 1;
             MPI.COMM_WORLD.Ssend(resultMatrix_1D, 0, totalNumberOfElements, MPI.INT, peer, rank) ; 
        	 
         }
 
	}
	
	private static void getMyElementsOfMatrix(int[] matrix_1D, int rank){
		for(int i=0 ; i<myNumberOfElements; i++){
			 myElementsOfMatrixA_1D[i] = matrix_1D[i + rank*myNumberOfElements];
		 }
		
		//System.out.print("Rank "+rank+": ");
		for(int j=0 ; j<myNumberOfElements; j++){
			//System.out.print(myElementsOfMatrixA_1D[j] + " , ");
		}
		
		//System.out.println();
	}
	
	private static void getMyElementsOfMatrixB(int[] matrix_1D){
		for(int i=0 ; i<myNumberOfElements; i++){
			 myElementsOfMatrixB_1D[i] = matrix_1D[i];
		 }
		
		//System.out.print("Rank "+rank+": ");
		for(int j=0 ; j<myNumberOfElements; j++){
			//System.out.println(myElementsOfMatrixB_1D[j] + " , ");
		}
		
		//System.out.println();
	}
	
	private static void displayArray(int[] array1d) {
		
		for(int i=0;i<(Math.pow(numOfRowsOrCols, 2));i++){
			System.out.print(array1d[i]+" , ");
		}
		System.out.println("\n");
		
	}

	private static int vectorMultiplication(int[] numArr1, int[] numArr2){
		int sum = 0;
		for(int i=0;i<numOfRowsOrCols;i++){
			sum = sum + numArr1[i]*numArr2[i];
		}
		return sum;
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
	
	private static void display2DMatrix(int[][] matrix){
		
		/*
		System.out.println("\n");
		
		for(int i = 0; i < matrix.length; i++) {
				 
            for(int j = 0; j < matrix[i].length; j++) {
                     
                System.out.print(matrix[i][j] + " ");
                
            }
            System.out.println();
        }
		*/
		
	}
	
	private static int[][] convertTo2Dfrom1D_RowMajor(int[] array1d, int noOfRows, int noOfCols){
		int[][] array2d = new int[noOfRows][noOfCols];
		for(int i=0, k=0; i<noOfRows; i++){
			for(int j=0; j<noOfCols; j++){
				array2d[i][j] = array1d[k++];
			}
		}
		//display2DMatrix(array2d);
		return array2d;
	}
	
	private static int[][] convertTo2Dfrom1D_ColumnMajor(int[] array1d, int noOfRows, int noOfCols){
		//System.out.println(array1d.length+","+noOfRows+","+noOfCols);
		int[][] array2d = new int[noOfRows][noOfCols];
		for(int i=0, k=0; i<noOfCols; i++){
			for(int j=0; j<noOfRows; j++){
				array2d[j][i] = array1d[k++];
			}
		}
		//display2DMatrix(array2d);
		return array2d;
	}
	
	private static int[] convertTo1Dfrom2D_RowMajor(int[][] array2d){
		int[] array1d = new int[(int) Math.pow(numOfRowsOrCols, 2)];
		for(int i=0, k=0; i<numOfRowsOrCols; i++){
			for(int j=0; j<numOfRowsOrCols; j++){
				array1d[k++] = array2d[i][j];
			}
		}
		return array1d;
	}
	
	private static int[] convertTo1Dfrom2D_ColumnMajor(int[][] array2d){
		int[] array1d = new int[(int) Math.pow(numOfRowsOrCols, 2)];
		for(int i=0, k=0; i<numOfRowsOrCols; i++){
			for(int j=0; j<numOfRowsOrCols; j++){
				
				array1d[k] = array2d[j][i];
				k++;
				
			}
		}
		return array1d;
	}
	
	private static int[][] multiply_matrix(int a[][], int b[][]){
		int[][] c = new int[factor][factor];
		for(int i=0;i<factor;i++)
		{
			for(int j=0;j<factor;j++)
			{
				for(int k=0;k<numOfRowsOrCols;k++)
				{
					c[i][j]=c[i][j]+(a[i][k]*b[k][j]);
				}
			}
		}
		
		return c;
		
	}
	
	private static void initializeResultMatrix(){
		if(!initializedResultMatrix){
			resultMatrix_2D = new int[numOfRowsOrCols][numOfRowsOrCols];
			initializedResultMatrix = true;
		}
	}
	
	private static void initializeMatrixB(){
		if(!initializedMatrixB){
			 matrix_B = create2DMatrix();
			 initializedMatrixB = true;
			 display2DMatrix(matrix_B);
		}
	}
	
	private static int[] popupate(int[] matrix_B_1D_sub, int index){
		getMyElementsOfMatrixB(matrix_B_1D_sub);
        myElementsOfMatrixA_2D = convertTo2Dfrom1D_RowMajor(myElementsOfMatrixA_1D, factor, numOfRowsOrCols);
		 myElementsOfMatrixB_2D = convertTo2Dfrom1D_ColumnMajor(myElementsOfMatrixB_1D, numOfRowsOrCols, factor);
		 int[][] temp_result = multiply_matrix(myElementsOfMatrixA_2D, myElementsOfMatrixB_2D);
		 for(int k=0;k<factor;k++){
			 for(int l=0;l<factor;l++){
				 resultMatrix_2D[rank*factor+k][index*factor+l] = temp_result[k][l];
			 }
		 }
		 if(index == (size-1)){
			 //display2DMatrix(resultMatrix_2D);
		 }
		 return convertTo1Dfrom2D_RowMajor(resultMatrix_2D);
		 
	}
	
	private static int[] add(int[] matrixA, int[] matrixB){
		int[] matrixC = new int[matrixA.length];
		for(int i=0;i<matrixA.length;i++){
			matrixC[i] = matrixA[i]+matrixB[i];
		}
		
		return matrixC;
	}

}


