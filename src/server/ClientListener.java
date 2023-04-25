package server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Map;
import java.util.Set;

import common.Message;
import common.MessageType;
import common.Observer;
import common.StreamProxy;
import common.StreamProxyMonitor;

public class ClientListener extends Thread{
	
	private Socket socket;
	private Server server;
	private StreamProxyMonitor streamProxy;
	
	private String id;
	private String username;
	
	private Boolean listening;
	
	public ClientListener(String id, Socket socket, Server s, Observer log) throws IOException {
		this.id = id;
		this.socket = socket;
		this.server = s;
		streamProxy = new StreamProxyMonitor(socket, false);
		streamProxy.setLog(log);

		listening = true;
	}
	
	public String getClientId() { return id; }
	public String getUserName() { return username; }
	
	private void startConnection(Message m) throws InterruptedException, IOException {
		username = m.getSrc();
		streamProxy.write(new Message(id.toString(), m.getDest(), m.nextType()));
		server.connectionStablished(id.toString());
	}
	
	public void filesActualization(Set<String> files) {
		Message m = new Message(id, "server", MessageType.ACTUALIZAR_FICHEROS, files);
		streamProxy.write(m);
	}
	
	public void addFile(Message m) {
		String fileName = m.getFileName();
		String clientId = m.getSrc();
		server.addFile(fileName, clientId);
		streamProxy.write(new Message(clientId, "server", m.nextType()));
	}
	
	private void closeConnection(Message m) {
		server.closeConnection(id);
		streamProxy.write(new Message(m.getSrc(), m.getDest(), m.nextType()));
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		while (listening) {
			try {
				Message m = streamProxy.read();
				switch(m.getType()) {
				case INICIAR_CONEXION:
					startConnection(m);
					break;
				case NUEVO_FICHERO_C:
					addFile(m);
					break;
				case FIN_CONEXION:
					listening = false;
					closeConnection(m);
					break;
				}
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
