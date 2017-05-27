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

	//public CLI(int prmPort, String prmIP){
	//}
	//PRM communication
	OutputStream outToServer;
	DataOutputStream out;

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

	public void setupClient(){
		String serverName = "127.0.0.1";
      	int port = 5001;
      	try {
        	System.out.println("Connecting to " + serverName + " on port " + port);
        	prmClient = new Socket(serverName, port);
         
        	System.out.println("Just connected to " + prmClient.getRemoteSocketAddress());
        	outToServer = prmClient.getOutputStream();
        	out = new DataOutputStream(outToServer);
         
        	out.writeUTF("Hello from " + prmClient.getLocalSocketAddress());
        	InputStream inFromServer = prmClient.getInputStream();
        	DataInputStream in = new DataInputStream(inFromServer);
         
         	//System.out.println("Server says " + in.readUTF());
      	}catch(IOException e) {
         	e.printStackTrace();
      	}
	}

	public void closeClient(){
		try{
			prmClient.close();
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
				break;
			case REDUCE:
				System.out.println("Reduce");
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
		c.setupClient();
		while(true){
			try{
				c.readInput();
			}catch(Exception e){
				System.out.println("e");
			}
		}
	}
}
