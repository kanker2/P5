package practicando;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientSocketListener extends Thread{
	
	Client c;
	int id;
	
	public ClientSocketListener(Client c, int id)
	{
		this.c = c;
		this.id = id;
	}
	
	private void printErrMessage() {
		System.err.println("Error at SocketListener " + id + " of " + c.getId() + " Client");
	}
	
	@Override
	public void run() {
		try {
			Socket s = new Socket(c.getIP(), c.getPort());
			PrintWriter foutC = new PrintWriter(s.getOutputStream());
//			System.out.println(new ObjectOutputStream(s.getOutputStream()));
			foutC.println(c.getFileName());
			foutC.flush();

			ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
			File fileAsked = (File) ois.readObject();
			c.updateFile(fileAsked);
		}
		catch(IOException e) {
			printErrMessage();
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			printErrMessage();
			e.printStackTrace();
		}
	}
}
