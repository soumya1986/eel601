package multicore;


import mpi.*;
public class HelloEclipseWorld {
	public static void main(String[] args) throws Exception {
		MPI.Init(args) ;
		int rank = MPI.	COMM_WORLD.Rank();
		int size = MPI.COMM_WORLD.Size();
		System.out.println("I am process <"+rank+"> of total <"+size+"> processes.");
		MPI.Finalize();
	}
}
