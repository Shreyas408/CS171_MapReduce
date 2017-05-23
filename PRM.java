import java.net.*;
import java.io.*;
import java.util.*;

public class PRM{
	class Request{
		int id; 
		Tuple BallotNum;
		Tuple AcceptNum;
		String fileName; 
	}	
	class LogObject{
		String fileName;
		HashMap<String, int> wordCount;
	}
	class ServerThread extends Thread{
		private ServerSocket serverSocket;

		public ServerThread(int port) throws IOException{
			serverSocket = new ServerSocket(port);
			serverSocket.setSoTimeout(10000);
		}

		@Override
		public void run(){
			while(true){
				try{
					System.out.println("Waiting for client on port " + 
               			serverSocket.getLocalPort() + "...");
            		Socket server = serverSocket.accept();
				}
				catch (SocketTimeoutException s){
					System.out.println("Socket timed out!");
					break;
				}
				catch (IOException e){
					e.printStackTrace();
					break;
				}
			}
		}
	}



	int PORT = 5001;
	String IP = 127.0.0.1; 

	int[] PRM_PortList;
	String[] PRM_IPList;
	int CLI_Port;
	String CLI_IP; 
	ArrayList<LogObject> log; 

	int reqNum = 0;
	int procID;

	public PRM(int procID, int CLI_Port, String CLI_IP, int[] PRM_PortList, String[] PRM_IPList){
		this.CLI_Port = CLI_Port;
		this.CLI_IP = CLI_IP;
		this.PRM_PortList = PRM_PortList;
		this.PRM_IPList = PRM_IPList; 
		this.procID = procID;
	}

	public static void main(String[] args){
		
	}

}

