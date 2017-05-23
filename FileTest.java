import java.net.*;
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class FileTest{
	public static void main(String[] args){
		String filename = "test1_reduced";

		File file = new File(filename);	
		Scanner in = null;
		try{
			in = new Scanner(file);
		}catch(FileNotFoundException f){
			System.out.println("File Not Found");
		}

		LogObject myLogObject = new LogObject();
		myLogObject.filename = filename;

		while(in.hasNext()){
			String str = in.next();
			str = str.substring(1,str.length()-1);
			//System.out.println(str);
			String[] arr = str.split(",");
			//System.out.println(arr[0] + " " + arr[1]);
			myLogObject.wordDict.put(arr[0], Integer.parseInt(arr[1]));
		}

		//LogObject myobject = new LogObject();
		//myobject.fileName = filename;

	}
}