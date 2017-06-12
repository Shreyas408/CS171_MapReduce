import java.net.*;
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class CLI{
	private static  Pattern map        = Pattern.compile("map \\S+");
	private static  Pattern reduce     = Pattern.compile("reduce \\S+[ \\S+]+");
	private static  Pattern replicate  = Pattern.compile("replicate \\S+");
	private static  Pattern stop       = Pattern.compile("stop");
	private static  Pattern resume     = Pattern.compile("resume"); 
	private static  Pattern total      = Pattern.compile("total \\S+[ \\S+]+");
	private static  Pattern print      = Pattern.compile("print");
	private static  Pattern merge      = Pattern.compile("merge \\S+ \\S+");

	public enum INPUT {
	    MAP, REDUCE, REPLICATE, STOP, RESUME, TOTAL, PRINT, MERGE, INVALID
	}
	
	private Map m1;
	private Map m2;

	int PORT = 5000;
	String IP = "127.0.0.1";

	int prmPort;
	String prmIP;

	Socket prmClient;
    Socket map1Client;
    Socket map2Client;
    Socket reducerClient; 
    
	//public CLI(int prmPort, String prmIP){
	//}
	//PRM communication
	OutputStream outToServer;
    DataOutputStream out;

    OutputStream map1Server;
    DataOutputStream map1out;

    OutputStream map2Server;
    DataOutputStream map2out;

	OutputStream reducerServer;
    DataOutputStream reducerout;    

	class ServerThread extends Thread{
		private ServerSocket serverSocket;

		public ServerThread() throws IOException{
			serverSocket = new ServerSocket(PORT);
			serverSocket.setSoTimeout(15000);
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
            
            			System.out.println(in.readUTF());
            		}
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

	public void setupClient(){
		String serverName = "127.0.0.1";
      	int port = 5001;
      	try {
	    //For prm
	    System.out.println("Connecting to " + serverName + " on port " + port);
        	prmClient = new Socket(serverName, port);
         
        	System.out.println("Just connected to " + prmClient.getRemoteSocketAddress());
        	outToServer = prmClient.getOutputStream();
        	out = new DataOutputStream(outToServer);
         
        	out.writeUTF("Hello from " + prmClient.getLocalSocketAddress());
        	InputStream inFromServer = prmClient.getInputStream();
        	DataInputStream in = new DataInputStream(inFromServer);

		//For map
		// map1Client = new Socket(serverName, mapport);
		// System.out.println("Just connected to " + map1Client.getRemoteSocketAddress());
		// map1Server = map1Client.getOutputStream();
		// map1out = new DataOutputStream(map1Server);

         	//System.out.println("Server says " + in.readUTF());
      	}catch(IOException e) {
         	e.printStackTrace();
      	}
	}

	public void setUpMapClient(){
		int port1 = 5002;
		int port2 = 5003;
		String serverName = "127.0.0.1";
		try{
			map1Client = new Socket(serverName, port1);
			System.out.println("Just connected to " + map1Client.getRemoteSocketAddress());
			map1Server = map1Client.getOutputStream();
			map1out = new DataOutputStream(map1Server);

			map2Client = new Socket(serverName, port2);
			System.out.println("Just connected to " + map2Client.getRemoteSocketAddress());
			map2Server = map2Client.getOutputStream();
			map2out = new DataOutputStream(map2Server);

		}catch(IOException e){
			System.out.println("ErR"); 
		}

	}

	public void setUpReducerClient(){
		int port = 5004;
		String serverName = "127.0.0.1";
		try{
			reducerClient = new Socket(serverName, port);
			System.out.println("Just connected to " + reducerClient.getRemoteSocketAddress());
			reducerServer = reducerClient.getOutputStream();
			reducerout = new DataOutputStream(reducerServer);

		}catch(IOException e){
			System.out.println("ErR"); 
		}

	}

	public void closeClients(){
		try{
			prmClient.close();
    		map1Client.close();
		    map2Client.close();
		    reducerClient.close();

		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public void readInput() throws Exception{
		Scanner in = new Scanner(System.in);
		String line = in.nextLine();
	
		switch (getCmd(line)){
			case MAP:
				System.out.println("Map");
				String[] splitline = line.trim().split("\\s+");
				File mapfile = new File(splitline[1]);
				Scanner filein = new Scanner(mapfile);
				int chars = 0;
				while(filein.hasNextLine()) {
				    String fline = filein.nextLine();
				    chars += fline.length();
				}
				//System.out.println("Number of chars: " + chars);
				//read char and move offset until space
				int offset = chars/2;
				BufferedReader reader = new BufferedReader(new FileReader(mapfile));
				reader.skip(offset);
				char curChar = (char)reader.read();
				while(curChar != ' ') { //keep moving until you hit a space
				    //System.out.println(curChar);
				    offset += 1;
				    reader.skip(1);
				    curChar = (char)reader.read();
				}
				offset+=1;
				System.out.println("offset value: " + offset);
				//map1 gets 0 offset-1
				//map2 gets offset (chars-(offset-1))
				map1out.writeUTF("map " + splitline[1] + " 0 " + (offset));
				map2out.writeUTF("map " + splitline[1] + " " + offset + " " + (chars-(offset+1)));
				
				break;
			case REDUCE:
				System.out.println("Reduce");
				reducerout.writeUTF(line);
				break;
			case REPLICATE:
				System.out.println("Replicate");
				out.writeUTF(line);
				break;
			case STOP:
				System.out.println("Stop");
				out.writeUTF(line);
				break;
			case RESUME:
				System.out.println("Resume");
				out.writeUTF(line);
				break;
			case TOTAL:
				System.out.println("Total");
				out.writeUTF(line);
				break;
			case PRINT:
				System.out.println("Print");
				out.writeUTF(line);
				break;
			case MERGE:
				System.out.println("Merge");
				out.writeUTF(line);
				break;
			default:
				System.out.println("Invalid Input");
				break;
		}
		
	}

	private INPUT getCmd(String str){
		if(map.matcher(str).matches()){
			return INPUT.MAP;
		}
		else if(reduce.matcher(str).matches()){
			return INPUT.REDUCE;
		}
		else if(replicate.matcher(str).matches()){
			return INPUT.REPLICATE;
		}
		else if(stop.matcher(str).matches()){
			return INPUT.STOP;
		}
		else if(resume.matcher(str).matches()){
			return INPUT.RESUME;
		}
		else if(total.matcher(str).matches()){
			return INPUT.TOTAL;
		}
		else if(print.matcher(str).matches()){
			return INPUT.PRINT;
		}
		else if(merge.matcher(str).matches()){
			return INPUT.MERGE;
		}
		else{
			return INPUT.INVALID;
		}
	}

	public static void main(String[] args){
		CLI c = new CLI();
		//c.setupClient();
		try{
			c.setUpMapClient();
			c.setUpReducerClient(); 
			while(true){
				c.readInput();
			}
		}catch(Exception e){
			System.out.println("e");
		}

		c.closeClients();
	}
}
