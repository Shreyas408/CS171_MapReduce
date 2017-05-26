import java.net.*;
import java.io.*;
import java.util.*;

class Request{
	int id; 
	Tuple BallotNum;
	Tuple AcceptNum;
	LogObject logobject; 

    public Request(int procID, int ballotCount, int acceptProcID, int acceptCounter , LogObject logobj) {
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

