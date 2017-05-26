import java.net.*;
import java.io.*;
import java.util.*;



public class PRM{

	public static int  PRM_PORT = 5001;

    public static int procID;
    public int ballotCounter;
    
    class PRMListener extends Thread{
    	ObjectInputStream inStream;
    	Socket inSock;

    	public PRMListener(Socket ic, ObjectInputStream ois){
    		inStream = ois;
    		inSock = ic;
    	}

    	@Override
    	public void run(){
    		while(true){
	    		try{
					System.out.println("reading object...");
					Object o = inStream.readObject();
					System.out.println("object read?");
					Request r = null;
					if(o instanceof Request){
						r = (Request)o;
						System.out.println("Received r");
						processPaxosRequest(inSock.getInetAddress().toString(), r);
					}
				}catch(ClassNotFoundException c){
					System.out.println("ClassNotFoundException");
				}catch(IOException e){
					e.printStackTrace();
				}
			}
    	}
    }

	class CLIListener extends Thread{
		@Override
		public void run(){
			try{

        		Thread.sleep(5000);
       			while(true){

       				String request = "1";
       				if(cliInputStream.available() > 0)
						 request = cliInputStream.readUTF();
					
					//System.out.println("Received request: " + request);
					if(!request.equals("1")) {
						processCLIRequest(request);
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


	String IP = "127.0.0.1"; 

	int[] PRM_PortList;
	String[] PRM_IPList;
	int CLI_Port;
	String CLI_IP;  

	ServerSocket serverSocket; //used for CLI
	DataInputStream cliInputStream;


	Socket[] prmOutSockets; //
	ObjectOutputStream[] outStreams; //
	

	Socket[] incomingSockets; //
	ObjectInputStream[] inStreams;

	//Queue<String> queue = new ConcurrentLinkedQueue<String>();

	//Request handling
	int ackCounter = 0;
	ArrayList<Request> requestList = new ArrayList<Request>();

	Tuple ballotNum = new Tuple(0,0);
	Tuple acceptNum = new Tuple(0,0);
	LogObject currentLogObject = null;

	ArrayList<LogObject> log = new ArrayList<LogObject>();

	int acceptCounter = 0;


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
    			//Not hitting this part 7:58
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

		try{
		    Thread.sleep(3000);
		}catch(InterruptedException i){
		    System.out.println("ERR");
		}
		setUpClient();

		
		try{
    		Thread.sleep(3000);
		}catch(InterruptedException i){
			System.out.println("InterruptedException");
		}

		setUpInputStream(); //ObjectInputStream objectIn = new ObjectInputStream(incomingSockets[i].getInputStream());


		t = new CLIListener();
		t.start();
		for(int i = 0; i < inStreams.length; i++){
			t = new PRMListener( incomingSockets[i], inStreams[i]);
			t.start();
		}

		//printIps();
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

			Request newRequest = new Request(procID, ++ballotCounter, acceptNum, logObject);

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
		}
		else if(splitreq[0].equals("stop")) {
			
		}
		else if(splitreq[0].equals("print")) {
			for(int i = 0; i < log.size(); i++) {
				System.out.println(log.get(i).fileName);
			}
			System.out.println("Done printing");
		}
		else if(splitreq[0].equals("merge")) {
			//merge two hashmaps

		}
		
    }

    public void processPaxosRequest(String ip, Request request) throws IOException{
	//TODO:
    	System.out.println("Request type: " + request.reqType);
    	if(request.reqType.equals("prepare")) {
    		//ack if ballot is bigger than mine
			ballotNum = ballotNum.compare(request.ballotNum);

			//update request to send back
			Request ackReq = new Request("ack", request.ballotNum, acceptNum, currentLogObject);
			for(int i = 0; i < prmOutSockets.length; i++) {
				if(ip.equals(prmOutSockets[i].getInetAddress().toString())) {
					outStreams[i].writeObject(ackReq);
				}
			}
		}
    	else if(request.reqType.equals("ack")) {
    		incrementAck(request);

    		//looking for full consensus
    		if(ackCounter >= prmOutSockets.length) {
    			Tuple b = new Tuple(0,0);
    			LogObject myVal = currentLogObject;
    			for(int i = 0; i < ackCounter; i++) {
    				if(b.isLessThan(requestList.get(i).ballotNum) && 
    					requestList.get(i).logobject != null) {
    					b = requestList.get(i).ballotNum;
    					myVal = requestList.get(i).logobject;
    				}
    			}
    			Request acceptReq = new Request("accept", ballotNum, null, myVal);
    			for(int i = 0; i < prmOutSockets.length; i++) {
    				outStreams[i].writeObject(acceptReq);
    			}
    		}
    	}
    	else {


    		// if(acceptNum.isLessThan(request.ballotNum)) {
    		// 	incrementAccept(false);
    		// }

    		System.out.println("Upon receiving Accept: " + acceptNum.toString());
    		System.out.println("Wiht request ballon num: " + request.ballotNum.toString());

    		if(acceptNum.isLessThan(request.ballotNum)){
 
     			//check to make sure we're only sending the first time
				Request acceptReq = new Request("accept", request.ballotNum, null, request.logobject);
				for(int i = 0; i < prmOutSockets.length; i++) {
					outStreams[i].writeObject(acceptReq);
				}
				incrementAccept(true); //original acceptor's accept 
    			   	
    			acceptNum = request.ballotNum;
    			ballotNum = request.ballotNum;
    			currentLogObject = request.logobject;

    		}
    		else if(acceptNum.isEqualTo(request.ballotNum)){
    			incrementAccept(false);
    		}

    		if(acceptCounter >= prmOutSockets.length) {
    			//decide on this log object
    			log.add(currentLogObject);
    			return;
    		}
    	}
		return;
    }

    public synchronized void incrementAck(Request request) {
    	ackCounter++;
    	requestList.add(request);
    }

    public synchronized void incrementAccept(boolean reset) {
    	
    	if(reset){
    		acceptCounter = 1;
    	}
    	else{
    		acceptCounter++;
    	}

    }

    public void printIps(){
    	for(int i = 0; i < prmOutSockets.length; i++){
    		System.out.println("prmOutSockets[" + i + "] = " + prmOutSockets[i].getInetAddress().toString());
    		System.out.println("incomingSockets[" + i + "] = " + incomingSockets[i].getInetAddress().toString());
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

		p.init();

	}
}