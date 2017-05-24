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
	class ListeningThread extends Thread{

		private ServerSocket serverSocket;

		private Socket[] incomingSockets;

		public ListeningThread() throws IOException{
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
			//Prepare for paxos
			//CLI: replicate, stop, resume, total, print, merge
			if(splitreq[0].equals("replicate")) {

		    	LogObject logObject= createLogObject(splitreq[1]);
		    	System.out.println("FileName: " + logObject.fileName);
		    	
		    	for (HashMap.Entry entry : logObject.wordDict.entrySet()) {
    				System.out.println(entry.getKey() + ", " + entry.getValue());
				}

			}



			//PRM - PRM communication
			if(splitreq[0].equals("prepare")) {

			}
	    }

		@Override
		public void run(){
			incomingSockets = new Socket[PRM_IPList.length];
			while(true){
				try{
					//accepting the CLI
					System.out.println("Waiting for CLI on port " + 
               			serverSocket.getLocalPort() + "...");
            		Socket cliServer = serverSocket.accept();
            		cliServer.setSoTimeout(15000);
            		System.out.println("Just connected to " + cliServer.getRemoteSocketAddress());
           			

            		//accepting all nodes' PRM  
            		for(int i = 0; i < PRM_IPList.length; i++) {
            			incomingSockets[i] = serverSocket.accept();
            			incomingSockets[i].setSoTimeout(15000);
            		}

           			while(true){
           				DataInputStream in = new DataInputStream(cliServer.getInputStream());

           				String request = "";
           				if(in.available() > 0)
							 request = in.readUTF();
						
						System.out.println("Received request: " + request);
						processRequest(request);

						for(int i = 0; i < incomingSockets.length; i++){
							in = new DataInputStream(incomingSockets[i].getInputStream());
							String str = "";
							if(in.available() > 0)
								str = in.readUTF();
							System.out.println(str);
						}
						break;
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
	Socket[] prmSockets;

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
			Thread t = new ListeningThread();
			t.start();
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public void setUpClient(){
		prmSockets = new Socket[PRM_IPList.length];
		try{
    		Thread.sleep(3000);
		}catch(InterruptedException i){
			System.out.println("InterruptedException");
		}
    	System.out.println("I'm woke");
    	for(int i = 0; i < PRM_IPList.length; i++){
			String serverName = PRM_IPList[i];
			int port = 5001;
			System.out.println("Connecting to " + serverName + " on port " + port);
			try{
				prmSockets[i] = new Socket(serverName, port);
			}catch (UnknownHostException h){
				System.out.println("UnknownHostException");
				break;
			}catch(IOException e){
				e.printStackTrace();
				break;
			}
		} 
	}

	public static void main(String[] args){
		String configFile = args[0]; 
		Scanner in = null;
		try{
			in = new Scanner(new File(configFile));
		}catch(FileNotFoundException f){
			System.out.println("ERR");
		}
		
		int numPRMs = in.nextInt();
		String[] prmIP = new String[numPRMs];
		for(int i = 0; i < numPRMs; i++){
			prmIP[i] = in.next();
		}
		PRM p = new PRM(1, 1, "", null, prmIP);	
		p.setUpServer();

		p.setUpClient();

	}
}



