import java.net.*;
import java.io.*;
import java.util.*;

class UpdateRequest extends Request{
	public ArrayList<LogObject> logobjects;

	//Prepare
	/*
    public Request(Tuple ballotNum, Tuple acceptNum, LogObject logobj) {
		reqType = "prepare";
		this.ballotNum = ballotNum;
		this.acceptNum = acceptNum;
		logobject = logobj;
    }*/

    //ACK
    public UpdateRequest(String reqType, ArrayList<LogObject> lo) {
    	super(reqType, null, null, null);
    	logobjects = lo; 
    }
}
