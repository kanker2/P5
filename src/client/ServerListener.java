package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import common.Message;
import common.MessageType;
import common.Observer;
import common.ProtocolError;
import common.StreamProxy;

public class ServerListener implements Runnable{
	
	private Client client;
	private boolean listening;
	private Socket socketToServer;
	private StreamProxy streamProxy;
	
	public ServerListener(Client c, String ip, int port) throws IOException, ClassNotFoundException, ProtocolError{
		client = c;
		listening = true;
		socketToServer = new Socket(ip, port);
		streamProxy = new StreamProxy(socketToServer);
	}

	public String getIp() { return socketToServer.getInetAddress().getHostAddress(); }
	public Integer getPort() { return socketToServer.getPort(); }
	
	public void newShareableFile(String name) {
		Message m = new Message("server", client.getId(), MessageType.NUEVO_FICHERO_C, name);
		streamProxy.write(m);
	}
	
	public void connectToServer() throws IOException, ClassNotFoundException, ProtocolError{
		String clientId = stablishConnection();
		client.setId(clientId);
	}
	
	private String stablishConnection() throws IOException, ClassNotFoundException, ProtocolError{
		Message hiServer = new Message("server", client.getUsername(), MessageType.INICIAR_CONEXION);
		streamProxy.write(hiServer);
		Message serverResponse = (Message) streamProxy.read();
		if (serverResponse == null || serverResponse.getType() != hiServer.nextType())
			throw new ProtocolError(hiServer);
		String clientId = serverResponse.getDest();
		return clientId;
	}
	
	public void closeConnection() {
		streamProxy.write(new Message("server", client.getId(), MessageType.FIN_CONEXION));
	}
	
	private void updateDownloadableFiles(Message m) {
		Set<String> downloadableFiles = m.getFiles();
		client.updateDownloadableFiles(downloadableFiles);
		streamProxy.write(new Message(m.getSrc(), m.getDest(), m.nextType()));
	}
	
	public void setLog(Observer o) {
		streamProxy.setLog(o);
	}
	
	@Override
	public void run() {
		while (listening) {
			Message m = streamProxy.read();
			if (m != null) {
				switch(m.getType()) {
				case CONF_NUEVO_FICHERO_C:
					break;
				case CONF_FIN_CONEXION:
					listening = false;
					try {
						socketToServer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;
				case ACTUALIZAR_FICHEROS:
					updateDownloadableFiles(m);
					break;
				default:
					break;
				}
			}
		}
	}
}