package server;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;

import common.Message;
import common.MessageType;

public class ClientListener extends Thread{
	private Server server;
	private ObjectInputStream fin;
	private ObjectOutputStream fout;
	private String id;
	private String username;
	
	public ClientListener(Socket socket, Server s) throws IOException {
		this.server = s;
		fout = new ObjectOutputStream(socket.getOutputStream());
		fin = new ObjectInputStream(socket.getInputStream());
	}
	
	private void startConnection(Message m) throws InterruptedException, IOException {
		id = server.getNewId();
		username = m.getSrc();
		fout.writeObject(new Message(id.toString(), m.getDest(), m.nextType()));
		fout.flush();
	}
	
	public void filesActualization(Map<String, Object> args) {
//		Message m = new Message(id.toString(), "server", MessageType.ACTUALIZAR_FICHEROS, args);
//		fout.writeObject(m);
	}
	
	public void addFile(Message m) {
		String fileName = (String) m.getcontent("nombre_fichero");
		String clientId = m.getSrc();
		server.addFile(fileName, clientId);
		try {
			fout.writeObject(new Message(clientId, "server", m.nextType()));
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				Message m = (Message) fin.readObject();
				switch(m.getType()) {
				case INICIAR_CONEXION:
					startConnection(m);
					break;
				case NUEVO_FICHERO_C:
					addFile(m);
					break;
				}
				
				
			} catch (ClassNotFoundException | IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
