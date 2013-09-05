package multicore;

import mpi.*;

public class SendFromMaster {
	
	
    static int rank = 0; //The current process.
    static int size = 0; //Total number of processes
    static int peer = 0; 

    
    static int len = 2 ;
    static int tag = 100 ; 
    
    static int msg1 = 0 , msg2 = 0 ;
	
	public static void main(String[] args){
		
		MPI.Init(args); 
		rank = MPI.COMM_WORLD.Rank() ; //The current process.
	    size = MPI.COMM_WORLD.Size() ; //Total number of processes
		
		 
		         /*
		         if(rank == 0) { 
		        	 
		        	 int buffer1 [] = new int[10]; 
		 
		             buffer1[0] = dataToBeSent ; 
		             peer = 1 ; 
		             MPI.COMM_WORLD.Send(buffer1, 0, len, MPI.INT, peer, tag) ; 
		             System.out.println("process <"+rank+"> sent a msg to " +"process <"+peer+">") ; 
		             
		             Status status = MPI.COMM_WORLD.Recv(buffer1, 0, buffer1.length, MPI.INT, peer, tag); 
		             System.out.println("process <"+rank+"> recv'ed a msg "+ "data <"+buffer1[0]
		            		 +">"+" data <"+buffer1[1]
		            		 +">"+"source <"+status.source+"> "
		            		 +" tag <"+status.tag   +"> "
		            		 +" count  <"+status.count +">\n") ; 
		 
		         } else if(rank == 1) { 
		 
		        	 int buffer2 [] = new int[10]; 
		             peer = 0 ; 
		             Status status = MPI.COMM_WORLD.Recv(buffer2, 0, buffer2.length, MPI.INT, peer, tag); 
		             System.out.println("process <"+rank+"> recv'ed a msg "+ "data <"+buffer2[0]
		            		 +">"+" source <"+status.source+"> "
		            		 +" tag <"+status.tag   +"> "
		            		 +" count  <"+status.count +">\n") ; 
		             
		             buffer2[1] = buffer2[0] + 1;
		             MPI.COMM_WORLD.Send(buffer2, 0, len, MPI.INT, peer, tag) ; 
		             System.out.println("process <"+rank+"> sent a msg to " +"process <"+peer+">") ; 
		         } 
		         */
		         
	    		send1();
	    		
	    		System.out.println("\n-----------------------\n");
	    		
	    		for( int i=0;i<10;i++)
	    			send2(i);
	    		
	    		System.out.println("\n-----------------------\n");
	    		
	    		viewResult();
		         
		         MPI.COMM_WORLD.Barrier();
		         MPI.Finalize(); 
		 
		     }  
	
			private static void printMsg(int rank, int msg, int peer, String state, String preposition){
				System.out.println("process <"+rank+"> "+state+" a msg <"+msg+"> "+ preposition + " process <"+peer+">");
			}
			
			private static void send1(){
				if(rank == 0) { 
		        	 
		        	 int buffer0 [] = new int[10]; 
		             buffer0[0] = 500 ; 
		             
		             msg1 = buffer0[0];
		             
		             peer = 1 ; 
		             buffer0[0] = buffer0[0]+1;
		             MPI.COMM_WORLD.Ssend(buffer0, 0, len, MPI.INT, peer, tag) ; 
		             printMsg(rank, buffer0[0], peer, "sent", "to");
		             
		             peer = size -1 ; 
		             buffer0[0] = buffer0[0]+1;
		             MPI.COMM_WORLD.Ssend(buffer0, 0, len, MPI.INT, peer, tag) ; 
		             printMsg(rank, buffer0[0], peer, "sent", "to");
		             
		         }
		         
		         else if(rank == (size -1)) { 
		        	 
		        	 Status status;
		        	 
		        	 int buffer1 [] = new int[10]; 
		        	 peer = 0;
		        	 status = MPI.COMM_WORLD.Recv(buffer1, 0, buffer1.length, MPI.INT, peer, tag); 
		        	 printMsg(rank, buffer1[0], status.source, "rcvd", "from");
		        	 
		        	 msg1 = buffer1[0];
		             
		             peer = (size -2);
		             buffer1[0] = buffer1[0]+1;
		             MPI.COMM_WORLD.Ssend(buffer1, 0, len, MPI.INT, peer, tag) ; 
		             printMsg(rank, buffer1[0], peer, "sent", "to");
		         }
		         
		         else if(rank == (size/2)) { 
		        	 
		        	 Status status;
		        	 
		        	 int buffer2 [] = new int[10]; 
		        	 peer = size/2-1;
		        	 status = MPI.COMM_WORLD.Recv(buffer2, 0, buffer2.length, MPI.INT, peer, tag); 
		        	 printMsg(rank, buffer2[0], status.source, "rcvd", "from");
		        	 
		        	 msg1 = buffer2[0];
		         }
		         
		         else if(rank == (size/2 + 1)) { 
		        	 
		        	 Status status;
		        	 
		        	 int buffer3 [] = new int[10]; 
		        	 peer = size/2+2;
		        	 status = MPI.COMM_WORLD.Recv(buffer3, 0, buffer3.length, MPI.INT, peer, tag); 
		        	 printMsg(rank, buffer3[0], status.source, "rcvd", "from");
		        	 
		        	 msg1 = buffer3[0];
		         }
		         
		         else if((rank >= 1) && (rank <= (size/2 -1))) { 
		        	 
		        	 Status status;
		        	 
		        	 int buffer4 [] = new int[10]; 
		        	 peer = rank - 1;
		        	 status = MPI.COMM_WORLD.Recv(buffer4, 0, buffer4.length, MPI.INT, peer, tag); 
		        	 printMsg(rank, buffer4[0], status.source, "rcvd", "from");
		        	 
		        	 msg1 = buffer4[0];
		             
		             peer = (rank + 1);
		             buffer4[0] = buffer4[0]+1;
		             MPI.COMM_WORLD.Ssend(buffer4, 0, len, MPI.INT, peer, tag) ; 
		             printMsg(rank, buffer4[0], peer, "sent", "to");
		         }
		         
		         else{
		        	 
		        	 Status status;
		        	 
		        	 int buffer5 [] = new int[10]; 
		        	 peer = rank + 1;
		        	 status = MPI.COMM_WORLD.Recv(buffer5, 0, buffer5.length, MPI.INT, peer, tag); 
		        	 printMsg(rank, buffer5[0], status.source, "rcvd", "from");
		        	 
		        	 msg1 = buffer5[0];
		             
		             peer = (rank - 1);
		             buffer5[0] = buffer5[0]+1;
		             MPI.COMM_WORLD.Ssend(buffer5, 0, len, MPI.INT, peer, tag) ; 
		             printMsg(rank, buffer5[0], peer, "sent", "to");
		        	 
		         }
		 
			}
			
			private static void send2(int val){
				if(rank == 0) { 
		        	 
		        	 int buffer0 [] = new int[10]; 
		             buffer0[0] = val ; 
		             
		             msg2 = buffer0[0];
		             
		             peer = 1 ; 
		             buffer0[0] = buffer0[0]+1;
		             MPI.COMM_WORLD.Ssend(buffer0, 0, len, MPI.INT, peer, tag) ; 
		             printMsg(rank, buffer0[0], peer, "sent", "to");
		             
		             peer = size -1 ; 
		             buffer0[0] = buffer0[0]+1;
		             MPI.COMM_WORLD.Ssend(buffer0, 0, len, MPI.INT, peer, tag) ; 
		             printMsg(rank, buffer0[0], peer, "sent", "to");
		             
		             System.out.println("******************"+" "+rank+" , "+(msg1 + msg2));
		             
		         }
		         
		         else if(rank == (size -1)) { 
		        	 
		        	 Status status;
		        	 
		        	 int buffer1 [] = new int[10]; 
		        	 peer = 0;
		        	 status = MPI.COMM_WORLD.Recv(buffer1, 0, buffer1.length, MPI.INT, peer, tag); 
		        	 printMsg(rank, buffer1[0], status.source, "rcvd", "from");
		        	 
		        	 msg2 = buffer1[0];
		             
		             peer = (size -2);
		             buffer1[0] = buffer1[0]+1;
		             MPI.COMM_WORLD.Ssend(buffer1, 0, len, MPI.INT, peer, tag) ; 
		             printMsg(rank, buffer1[0], peer, "sent", "to");
		             
		             System.out.println("******************"+" "+rank+" , "+(msg1 + msg2));
		         }
		         
		         else if(rank == (size/2)) { 
		        	 
		        	 Status status;
		        	 
		        	 int buffer2 [] = new int[10]; 
		        	 peer = size/2-1;
		        	 status = MPI.COMM_WORLD.Recv(buffer2, 0, buffer2.length, MPI.INT, peer, tag); 
		        	 printMsg(rank, buffer2[0], status.source, "rcvd", "from");
		        	 
		        	 msg2 = buffer2[0];
		        	 
		        	 System.out.println("******************"+" "+rank+" , "+(msg1 + msg2));
		         }
		         
		         else if(rank == (size/2 + 1)) { 
		        	 
		        	 Status status;
		        	 
		        	 int buffer3 [] = new int[10]; 
		        	 peer = size/2+2;
		        	 status = MPI.COMM_WORLD.Recv(buffer3, 0, buffer3.length, MPI.INT, peer, tag); 
		        	 printMsg(rank, buffer3[0], status.source, "rcvd", "from");
		        	 
		        	 msg2 = buffer3[0];
		        	 
		        	 System.out.println("******************"+" "+rank+" , "+(msg1 + msg2));
		         }
		         
		         else if((rank >= 1) && (rank <= (size/2 -1))) { 
		        	 
		        	 Status status;
		        	 
		        	 int buffer4 [] = new int[10]; 
		        	 peer = rank - 1;
		        	 status = MPI.COMM_WORLD.Recv(buffer4, 0, buffer4.length, MPI.INT, peer, tag); 
		        	 printMsg(rank, buffer4[0], status.source, "rcvd", "from");
		        	 
		        	 msg2 = buffer4[0];
		             
		             peer = (rank + 1);
		             buffer4[0] = buffer4[0]+1;
		             MPI.COMM_WORLD.Ssend(buffer4, 0, len, MPI.INT, peer, tag) ; 
		             printMsg(rank, buffer4[0], peer, "sent", "to");
		             
		             System.out.println("******************"+" "+rank+" , "+(msg1 + msg2));
		         }
		         
		         else{
		        	 
		        	 Status status;
		        	 
		        	 int buffer5 [] = new int[10]; 
		        	 peer = rank + 1;
		        	 status = MPI.COMM_WORLD.Recv(buffer5, 0, buffer5.length, MPI.INT, peer, tag); 
		        	 printMsg(rank, buffer5[0], status.source, "rcvd", "from");
		        	 
		        	 msg2 = buffer5[0];
		             
		             peer = (rank - 1);
		             buffer5[0] = buffer5[0]+1;
		             MPI.COMM_WORLD.Ssend(buffer5, 0, len, MPI.INT, peer, tag) ; 
		             printMsg(rank, buffer5[0], peer, "sent", "to");
		             
		             System.out.println("******************"+" "+rank+" , "+(msg1 + msg2));
		        	 
		         }
		 
			}
			
			public static void viewResult(){
				
				
				
			}
			
	}


