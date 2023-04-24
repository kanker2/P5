package practicando;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	
	ServerSocket ss;
	ServerSocketManager ssm;
	
	public Server(int port) throws IOException{
		ss = new ServerSocket(port);
		ssm = new ServerSocketManager(this);
		ssm.start();
	}

	public ServerSocket getSS() {
		return ss;
	}
	
	public String getContent(String fileName) throws IOException, FileNotFoundException{
		FileInputStream fileIn = new FileInputStream(fileName);
		String content = (new String(fileIn.readAllBytes()));
		fileIn.close();
		return content;
	}
	
	public static void f1()
	{
		try {
			ServerSocket ss = new ServerSocket(99);
			Socket s1 = ss.accept();
			System.out.println(s1);
			
			ObjectInputStream ois = new ObjectInputStream(s1.getInputStream());
			System.out.println("Server recibe: " + ((Integer) ois.readObject()));
			
			PrintWriter foutC = new PrintWriter(s1.getOutputStream());
			
			foutC.write("adios\n");
			foutC.flush();
		}
		catch (Exception e)
		{
			System.err.println("Error f1 Server");
		}
	}
}
