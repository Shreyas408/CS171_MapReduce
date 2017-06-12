import java.net.*;
import java.io.*;
import java.util.*;

public class Map{

	int port;
	int id;
	ServerSocket serverSocket; 
	DataInputStream inStream;


	public Map(int id, int port){
		this.port = port; 
		this.id = id; 
	}

	public void listen() throws Exception{
		while(true){
			System.out.println("Reading data...");
			//if(inStream.available() > 0){
			String request = inStream.readUTF();
			//}
			System.out.println(request);

			String[] splitline = request.trim().split("\\s+");
			File mFile = new File(splitline[1]);
			Scanner filein = new Scanner(mFile);

			int chars = 0;
			String word = "";
			BufferedReader reader = new BufferedReader(new FileReader(mFile));
			HashMap<String, Integer> dictionary = new HashMap<String, Integer>();

			reader.skip(Integer.parseInt(splitline[2]));
			while(chars <= Integer.parseInt(splitline[3])) { //size = splitline[3]
				char curChar = (char)reader.read();
				if(curChar != ' '){
					word += curChar;
					//System.out.println(word);
				}

				else {
					if(word.equals("")) {
						//do nothing on empty string
					}
					//add this word into my map
					else if(dictionary.containsKey(word)) {
						dictionary.put(word, dictionary.get(word) + 1);
						word = "";
					}
					else {
						dictionary.put(word, 1);
						word = "";
					}
				}
				chars++;
			}
			if(word.equals("")) {
				//do nothing on empty string
			}
			//add this word into my map
			else if(dictionary.containsKey(word)) {
				dictionary.put(word, dictionary.get(word) + 1);
				word = "";
			}
			else {
				dictionary.put(word, 1);
				word = "";
			}
			writeDictToFile(dictionary, splitline[1] + "_I_" + id);


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

	public void setUpServer() throws Exception{
		serverSocket = new ServerSocket(port);
		Socket cliServer = serverSocket.accept();
		inStream = new DataInputStream(cliServer.getInputStream());
	}

	public static void main(String[] args){
		//java Map port#
		try{

			Map m = new Map(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
			m.setUpServer();
			m.listen();
		}catch(FileNotFoundException f){
			System.out.println("File Not Found");
		}catch(Exception e){
			e.printStackTrace();

		}

	}
}