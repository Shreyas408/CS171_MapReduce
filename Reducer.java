import java.net.*;
import java.io.*;
import java.util.*;

public class Reducer{
	
	ServerSocket serverSocket; 
	DataInputStream inStream;
	int port = 5004; 


	public void setUpServer() throws Exception{
		serverSocket = new ServerSocket(port);
		Socket cliServer = serverSocket.accept();
		inStream = new DataInputStream(cliServer.getInputStream());
	}
	public void listen() throws Exception{
		while(true){
		    //			System.out.println("Reading data...");
			//if(inStream.available() > 0){
			String request = inStream.readUTF();
			//}
			System.out.println(request);

			String[] splitline = request.trim().split("\\s+");
			HashMap<String, Integer> dictionary = new HashMap<String, Integer>(); 

			String filename = (splitline[1].split("_"))[0];

			for(int i = 1; i < splitline.length; i++){
				File mFile = new File(splitline[i]);
				Scanner filein = new Scanner(mFile);
				while(filein.hasNext()){
					String str = filein.next();
					str = str.substring(1, str.length()-1);
					String[] splitstr = str.split(",");
					
					String key = splitstr[0];
					int value = Integer.parseInt(splitstr[1]);
					if(dictionary.containsKey(key)) {
						dictionary.put(key, dictionary.get(key) + value);
					}
					else {
						dictionary.put(key, value);
					}//splitstr[0] // Word
					//splitstr[1] // occurance
				}
			}

			writeDictToFile(dictionary, filename + "_reduced");

			//File mFile = new File(splitline[1]);
			//Scanner filein = new Scanner(mFile);
		}
	}

	public void writeDictToFile(HashMap<String, Integer> dict, String filename){
		BufferedWriter bw = null;
		FileWriter fw = null;

		try {

			String content = "";
			for(String currentKey : dict.keySet()) {
				content += "<" + currentKey + "," + dict.get(currentKey) + "> ";
			}
			content.trim();



			fw = new FileWriter(filename);
			bw = new BufferedWriter(fw);
			bw.write(content);


		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			try {

				if (bw != null)
					bw.close();

				if (fw != null)
					fw.close();

			} catch (IOException ex) {

				ex.printStackTrace();

			}

		}


	}

	public static void main(String[] args){
		Reducer r = new Reducer(); 
		try{
			r.setUpServer();
			r.listen();
		}
		catch(FileNotFoundException f){
			System.out.println("File Not Found");
		}catch(Exception e){
			e.printStackTrace();

		}
	}
}
