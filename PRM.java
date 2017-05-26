import java.net.*;
import java.io.*;
import java.util.*;


public class PRM{

	public static int  PRM_PORT = 5001;

    public static int procID;
    public int ballotCounter;
    public int acceptCounter;
    
	class Request{
		int id; 
		Tuple BallotNum;
		Tuple AcceptNum;
		LogObject logobject; 

	    public Request(int ballotCount, int acceptProcID, int acceptCounter , LogObject logobj) {
			id = procID;
			BallotNum = new Tuple(id, ballotCount);
			AcceptNum = new Tuple(acceptProcID, acceptCounter);
			logobject = logobj;
	    }
	    /*
	    public Request createPaxosRequest(LogObject logobj) {
		Request newReq = new Request(ballotCounter, procID, acceptCounter, logobj);
		return newReq;
		}*/
		public String toString(){
			String result = id + " " + BallotNum.toString() + " " + AcceptNum.toString();
			return result;
		}

	}
	class LogObject{
		String fileName;
		HashMap<String, Integer> wordDict = new HashMap<String, Integer>();
	}
	class ListeningThread extends Thread{

		private ServerSocket serverSocket;

		private Socket[] incomingSockets;

		public ListeningThread() throws IOException{
			//serverSocket = new ServerSocket(PRM_PORT);
			//serverSocket.setSoTimeout(15000);
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
	    
	    public void processCLIRequest(String request) {
			//split on whitespace into array splitreq
			String[] splitreq = request.trim().split("\\s+");
			//Prepare for paxos
			//CLI: replicate, stop, resume, total, print, merge
			if(splitreq[0].equals("replicate")) {
			    
		    	LogObject logObject= createLogObject(splitreq[1]);

			//testing proper creation
			/*
			System.out.println("FileName: " + logObject.fileName);
		    	
		    	for (HashMap.Entry entry : logObject.wordDict.entrySet()) {
    				System.out.println(entry.getKey() + ", " + entry.getValue());
				}

				}*/
				Request newRequest = new Request(ballotCounter, procID, acceptCounter, logObject);

			//send paxos prepare
				for(int i = 0; i < prmOutSockets.length; i++){
					try{
						//ObjectOutputStream oos = new ObjectOutputStream(prmOutSockets[i].getOutputStream());
						outStreams[i].writeObject(newRequest); 
						System.out.println("Delivering Prepare Request");
					}catch(IOException e){
						e.printStackTrace();
						break;
					}
				}
			//newRequest.createPaxosRequest(logObject);
			}
			
	    }

	    public void processPaxosRequest(String request) {
		//TODO:
	    	System.out.println(request);
			return;
	    }

		@Override
		public void run(){
			try{
				//setUpServer();

        		Thread.sleep(5000);
       			while(true){

       				String request = "1";
       				if(cliInputStream.available() > 0)
						 request = cliInputStream.readUTF();
					
					System.out.println("Received request: " + request);
					if(!request.equals("1")) {
						processCLIRequest(request);
					}

					for(int i = 0; i < incomingSockets.length; i++){
						//in = new DataInputStream(incomingSockets[i].getInputStream());
						//DataInputStream dataIn = new DataInputStream(incomingSockets[i].getInputStream());

						//request = "2";
						System.out.println("objectIn.available(): " + inStreams[i].available());
						if(inStreams[i].available() > 0){
							try{
								System.out.println("reading object...");
								Object o = inStreams[i].readObject();
								System.out.println("object read?");
								Request r = null;
								if(o instanceof Request){
									r = (Request)o;
									System.out.println("Received r");
								}
							}catch(ClassNotFoundException c){
								System.out.println("ClassNotFoundException");
							}
						}
						System.out.println("I'm in the LOOP");
					}
				}
			}
			catch (SocketTimeoutException s){
				System.out.println("Socket timed out!");
				//break;
			}
			catch (IOException e){
				e.printStackTrace();
				//break;
			}
			catch (InterruptedException ie){
				System.out.println("InterruptedException");
				//break;
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

	ServerSocket serverSocket; //used for CLI
	DataInputStream cliInputStream;


	Socket[] prmOutSockets;
	ObjectOutputStream[] outStreams; 
	

	Socket[] incomingSockets;
	ObjectInputStream[] inStreams;

	int reqNum = 0;

	public PRM(int procID, int CLI_Port, String CLI_IP, int[] PRM_PortList, String[] PRM_IPList){
		this.CLI_Port = CLI_Port;
		this.CLI_IP = CLI_IP;
		this.PRM_PortList = PRM_PortList;
		this.PRM_IPList = PRM_IPList; 
		this.procID = procID;
	}

	public void setUpServer(){
		try{
			serverSocket = new ServerSocket(PRM_PORT);
			incomingSockets = new Socket[PRM_IPList.length];
			//accepting the CLI
			System.out.println("Waiting for CLI on port " + 
   			serverSocket.getLocalPort() + "...");
    		Socket cliServer = serverSocket.accept();
      		//cliServer.setSoTimeout(15000);
    		System.out.println("Just connected to " + cliServer.getRemoteSocketAddress());
   			
			cliInputStream = new DataInputStream(cliServer.getInputStream());

    		//accepting all nodes' PRM  
    		System.out.println(PRM_IPList.length + " is the length");
    		for(int i = 0; i < PRM_IPList.length; i++) {
    			incomingSockets[i] = serverSocket.accept();
    			System.out.println("incomingSockets[" + i + "] accepted");
    			//incomingSockets[i].setSoTimeout(15000);
    		}
    	}
    	catch (SocketTimeoutException s){
			System.out.println("Socket timed out!");
		}
		catch (IOException e){
			e.printStackTrace();
		}
		
	}

	public void setUpClient(){
		prmOutSockets = new Socket[PRM_IPList.length];
		outStreams = new ObjectOutputStream[PRM_IPList.length];
    	//System.out.println("I'm woke");
    	
    	for(int i = 0; i < PRM_IPList.length; i++){
			String serverName = PRM_IPList[i];
			int port = 5001;
			System.out.println("Connecting to " + serverName + " on port " + port);
			try{
				prmOutSockets[i] = new Socket(serverName, port);
				System.out.println("Connected to prmOutSockets[" + i + "]");
				outStreams[i] = new ObjectOutputStream(prmOutSockets[i].getOutputStream());
				System.out.println("Connected to outStreams[" + i + "]");
			}catch (UnknownHostException h){
				System.out.println("UnknownHostException");
				break;
			}catch(IOException e){
				e.printStackTrace();
				break;
			}
		} 

		//SetUpInputStream::
	}

	public void setUpInputStream(){
		try{
			inStreams = new ObjectInputStream[PRM_IPList.length];
			for(int i = 0; i < inStreams.length; i++){
				inStreams[i] = new ObjectInputStream(incomingSockets[i].getInputStream());
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public void init(){
		Thread t = new Thread() {
			@Override 
			public void run(){
				setUpServer();
			}
		};
		t.start();


		setUpClient();

		
		try{
    		Thread.sleep(3000);
		}catch(InterruptedException i){
			System.out.println("InterruptedException");
		}

		setUpInputStream(); //ObjectInputStream objectIn = new ObjectInputStream(incomingSockets[i].getInputStream());


		try{
			t = new ListeningThread();
			t.start();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}

	public static void main(String[] args){
	    procID = Integer.parseInt(args[0]); 
	    String configFile = args[1];
		
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



