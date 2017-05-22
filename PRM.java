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
		HashMap<>
	}
	int PORT;
	String IP; 
	int[] PORTLIST;
	String[] IPLIST; 

	int reqNum;
	int procID

	public PRM(int port, int[] ports, String[] ips){
		PORTLIST = ports;
		IPLIST = ips;
		PORT = port;
	}

}