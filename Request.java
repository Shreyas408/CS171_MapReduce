import java.net.*;
import java.io.*;
import java.util.*;

class Request implements Serializable{
	public Tuple ballotNum = new Tuple(0,0);
	public Tuple acceptNum = new Tuple(0,0);
	public LogObject logobject = null; 
	public String reqType; 

	//Prepare
	/*
    public Request(Tuple ballotNum, Tuple acceptNum, LogObject logobj) {
		reqType = "prepare";
		this.ballotNum = ballotNum;
		this.acceptNum = acceptNum;
		logobject = logobj;
    }*/

    //ACK
    public Request(String reqType, Tuple preparerBallot, Tuple currentAcceptBallot, LogObject currentAcceptVal) {
    	this.reqType = reqType;
    	ballotNum = preparerBallot;
    	acceptNum = currentAcceptBallot;
    	logobject = currentAcceptVal;
    }

    /*
    public Request createPaxosRequest(LogObject logobj) {
	Request newReq = new Request(ballotCounter, procID, acceptCounter, logobj);
	return newReq;
	}*/
	public String toString(){
		//String result = id + " " + BallotNum.toString() + " " + AcceptNum.toString();
		return "result";
	}

}
