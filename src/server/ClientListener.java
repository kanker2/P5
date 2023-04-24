package server;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;

import common.Message;
import common.MessageType;
import common.Observer;
import common.StreamProxy;
import common.StreamProxyMonitor;

public class ClientListener extends Thread{
	
	private Socket socket;
	private Server server;
	private StreamProxy streamProxy;
	
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
		
//		fout = new ObjectOutputStream(socket.getOutputStream());
//		fin = new ObjectInputStream(socket.getInputStream());
	}
	
	public String getClientId() { return id; }
	public String getUserName() { return username; }
	
	private void startConnection(Message m) throws InterruptedException, IOException {
		username = m.getSrc();
		streamProxy.write(new Message(id.toString(), m.getDest(), m.nextType()));
		server.connectionStablished();
//		fout.writeObject(new Message(id.toString(), m.getDest(), m.nextType()));
//		fout.flush();
	}
	
	public void filesActualization(Map<String, Object> args) {
		Message m = new Message(id, "server", MessageType.ACTUALIZAR_FICHEROS, args);
		streamProxy.write(m);
//		fout.writeObject(m);
	}
	
	public void addFile(Message m) {
		Map<String, Object> args = m.getArgs();
		String fileName = (String) args.get("nombre_fichero");
		String clientId = m.getSrc();
		server.addFile(fileName, clientId);
		streamProxy.write(new Message(clientId, "server", m.nextType()));
//		try {
//			fout.writeObject();
//		} catch(IOException e) {
//			e.printStackTrace();
//		}
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
//				Message m = (Message) fin.readObject();
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
				
				
//			} catch (ClassNotFoundException | IOException | InterruptedException e) {
//				e.printStackTrace();
//			}
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
