package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Collection;

import common.Message;
import common.MessageType;

public class ServerListener extends Thread{
	
	private Client client;
	private Socket serverSocket;
	private ObjectInputStream fin;
	private ObjectOutputStream fout;
	
	
	public ServerListener(Client c, String ip, int port) throws IOException{
		client = c;
		serverSocket = new Socket(ip, port);
		fin = new ObjectInputStream(serverSocket.getInputStream());
		fout = new ObjectOutputStream(serverSocket.getOutputStream());
	}
	
	public void listFiles() throws IOException, ClassNotFoundException{
		fout.writeObject(new Message("server", client.getId(), MessageType.PEDIR_LISTA_ARCHIVOS));
		Message confirmation = (Message) fin.readObject();
		Collection<String> listFiles = (Collection<String>) fin.readObject();
	}
}
