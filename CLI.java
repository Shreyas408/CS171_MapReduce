import java.net.*;
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class CLI{
	private static final Pattern map        = Pattern.compile("map \\S+");
	private static final Pattern reduce     = Pattern.compile("reduce \\S+ \\S+[ \\S+]+");
	private static final Pattern replicate  = Pattern.compile("replicate \\S+");
	private static final Pattern stop       = Pattern.compile("stop");
	private static final Pattern resume     = Pattern.compile("resume"); 
	private static final Pattern total      = Pattern.compile("total \\S+ \\S+[ \\S+]+");
	private static final Pattern print      = Pattern.compile("print");
	private static final Pattern merge      = Pattern.compile("merge \\S+ \\S+");

	private enum INPUT {
	    MAP, REDUCE, REPLICATE, STOP, RESUME, TOTAL, PRINT, MERGE, INVALID
	}
	
	private Map m1;
	private Map m2;

	int PORT = 5000;
	String IP = "127.0.0.1";

	int prmPort;
	String prmIP;

	public CLI(int prmPort, String prmIP){
		myPRM = p;
	}

	public void setupClient(){
		String serverName = "PRM";
      	int port = Integer.parseInt(args[1]);
      	try {
        	System.out.println("Connecting to " + serverName + " on port " + port);
        	Socket client = new Socket(serverName, port);
         
        	System.out.println("Just connected to " + client.getRemoteSocketAddress());
        	OutputStream outToServer = client.getOutputStream();
        	DataOutputStream out = new DataOutputStream(outToServer);
         
        	out.writeUTF("Hello from " + client.getLocalSocketAddress());
        	InputStream inFromServer = client.getInputStream();
        	DataInputStream in = new DataInputStream(inFromServer);
         
         	System.out.println("Server says " + in.readUTF());
         	client.close();
      	}catch(IOException e) {
         	e.printStackTrace();
      	}
	}

	public void readInput(){
		Scanner in = new Scanner(System.in);
		String line = in.nextLine();

	
		switch getCmd(line){
			case MAP:
				break;
			case REDUCE:
				break;
			case REPLICATE:
				break;
			case STOP:
				break;
			case RESUME:
				break;
			case TOTAL:
				break;
			case PRINT:
				break;
			case MERGE:
				break;
			case INVALID:
				break;
		}

		// // map filename
		// Pattern mapPattern = Pattern.compile("map \\S+");
		// Matcher mapMatcher = mapPattern.matcher(line);
		// //if(mapMatcher.matches()){
		// 	//System.out.println("MAp Matches");
		// //}

		// // reduce filename1 filename2 filename3...
		// Pattern reducePattern = Pattern.compile("reduce \\S+ \\S+[ \\S+]+");
		// Matcher reduceMatcher = reducePattern.matcher(line);
		// //if(reduceMatcher.matches())
		// 	//System.out.println("Matches!");

		// // replicate filename
		// Pattern replicatePattern = Pattern.compile("replicate \\S+");
		// Matcher replicateMatcher = replicatePattern.matcher(line);

		// // stop

		// // resume
		
		// // total pos1 pos2 ...
		// Pattern totalPattern = Pattern.compile("total \\S+ \\S+[ \\S+]+");
		// Matcher totalMatcher = totalPattern.matcher(line);

		// // print 
		
		// // merge pos1 pos2
		// Pattern mergePattern = Pattern.compile("merge \\S+ \\S+");
		// Matcher mergeMatcher = mergePattern.matcher(line);

		// //Map m = new Map();
		// //m.run();
		
	}

	private INPUT getCmd(String str){
		if(map.matcher(str).matches()){
			return MAP;
		}
		else if(reudce.matcher(str).matches()){
			return REDUCE;
		}
		else if(replicate.matcher(str).matches()){
			return REPLICATE;
		}
		else if(stop.matcher(str).matches()){
			return STOP;
		}
		else if(resume.matcher(str).matches()){
			return RESUME;
		}
		else if(total.matcher(str).matches()){
			return TOTAL;
		}
		else if(print.matcher(str).matches()){
			return PRINT;
		}
		else if(merge.matcher(str).matches()){
			return MERGE;
		}
		else{
			return INVALID;
		}
	}
	public static void main(String[] args){
		
	}
}