import java.net.*;
import java.io.*;
import java.util.*;


class LogObject implements Serializable{
	String fileName;
	HashMap<String, Integer> wordDict = new HashMap<String, Integer>();
}