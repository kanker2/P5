package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import common.Message;
import common.MessageType;
import common.ProtocolError;

public class ServerListener extends Thread{
	
	private Client client;
	private boolean listening;
	private Socket socketToServer;
	private ObjectInputStream fin;
	
	public ServerListener(Client c, String ip, int port) throws IOException, ClassNotFoundException, ProtocolError{
		client = c;
		listening = true;
		socketToServer = new Socket(ip, port);
		fin = new ObjectInputStream(socketToServer.getInputStream());
		ObjectOutputStream fout = new ObjectOutputStream(socketToServer.getOutputStream());
		String clientId = stablishConnection(fin, fout);
		client.setId(clientId);
	}

	private String stablishConnection(ObjectInputStream fin, ObjectOutputStream fout) throws IOException, ClassNotFoundException, ProtocolError{
		Message hiServer = new Message("server", "new client", MessageType.INICIAR_CONEXION);
		fout.writeObject(hiServer);
		Message serverResponse = (Message) fin.readObject();
		if (serverResponse.getType() != hiServer.nextType())
			throw new ProtocolError(hiServer);
		String clientId = serverResponse.getDest();
		return clientId;
	}
	
	@Override
	public void run() {
		while (listening) {
			
		}
	}
}
