import java.net.*;
import java.io.*;
import java.util.*;


public class PRM{

	public static int  PRM_PORT = 5001;
	
	class Request{
		int id; 
		Tuple BallotNum;
		Tuple AcceptNum;
		LogObject logobject; 
	}	
	class LogObject{
		String fileName;
		HashMap<String, Integer> wordDict;
	}
	class ServerThread extends Thread{
		private ServerSocket serverSocket;

		public ServerThread() throws IOException{
			serverSocket = new ServerSocket(PRM_PORT);
			serverSocket.setSoTimeout(15000);
		}

	    public LogObject createLogObject(String filename) {
		System.out.println("create logobject function");
		File file = new File(filename);
		LogObject myobject = new LogObject();
		myobject.fileName = filename;

		return myobject;
	    }
	    
	    public void processRequest(String request) {
		String[] splitreq = request.trim().split("\\s+");

		if(splitreq[0] == "replicate") {
		    createLogObject(splitreq[1]);
		}
	    }

		@Override
		public void run(){
			while(true){
				try{
					System.out.println("Waiting for client on port " + 
               			serverSocket.getLocalPort() + "...");
            		Socket server = serverSocket.accept();
            		System.out.println("Just connected to " + server.getRemoteSocketAddress());
           			while(true){
           				DataInputStream in = new DataInputStream(server.getInputStream());

					String request = in.readUTF();
					System.out.println("Received request: " + request);
					processRequest(request);
				}
            		//DataOutputStream out = new DataOutputStream(server.getOutputStream());
            		//out.writeUTF("Thank you for connecting to " + server.getLocalSocketAddress()
               		   //+ "\nGoodbye!");
            		//server.close();
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
	String IP = "127.0.0.1"; 

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

	public void setUpServer(){
		try{
			Thread t = new ServerThread();
			t.start();
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public static void main(String[] args){
		PRM p = new PRM(1, 1, "", null, null);	
		p.setUpServer();
	}
}



