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
		HashMap<String, Integer> wordDict = new HashMap<String, Integer>();
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
			Scanner in = null;
			try{
				in = new Scanner(file);
			}catch(FileNotFoundException f){
				System.out.println("File Not Found");
			}

			LogObject myLogObject = new LogObject();
			myLogObject.fileName = filename;

			while(in.hasNext()){
				String str = in.next();
				str = str.substring(1,str.length()-1);
				String[] arr = str.split(",");
				myLogObject.wordDict.put(arr[0], Integer.parseInt(arr[1]));
			}

			return myLogObject;
	    }
	    
	    public void processRequest(String request) {
			String[] splitreq = request.trim().split("\\s+");
			System.out.println("File Name: " +splitreq[1]);

			if(splitreq[0].equals("replicate")) {

		    	LogObject logObject= createLogObject(splitreq[1]);
		    	System.out.println("FileName: " + logObject.fileName);
		    	
		    	for (HashMap.Entry entry : logObject.wordDict.entrySet()) {
    				System.out.println(entry.getKey() + ", " + entry.getValue());
				}

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



