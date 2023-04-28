package server;

import java.io.IOException;
import java.net.Socket;
import java.util.Set;

import common.Message;
import common.MessageType;
import common.Observer;
import common.StreamProxyMonitor;

public class ClientListener extends Thread{
	
	private Socket socket;
	private Server server;
	private StreamProxyMonitor streamProxy;
	
	private String id;
	private String username;
	
	private Boolean listening;
	private Boolean bussyUploading;
	
	public ClientListener(String id, Socket socket, Server s, Observer log) throws IOException {
		this.id = id;
		this.socket = socket;
		this.server = s;
		streamProxy = new StreamProxyMonitor(socket, false);
		streamProxy.setLog(log);

		listening = true;
		bussyUploading = false;
	}
	
	public void updateFiles(Set<String> files) {
		Message m = new Message(id, "server", MessageType.ACTUALIZAR_FICHEROS, files);
		streamProxy.write(m);
	}
	
	public void prepareUpload(String file, Integer port) {
		bussyUploading = true;
		Message m = new Message(id, "server", MessageType.PETICION_EMISION_FICHERO, file, port);
		streamProxy.write(m);
	}
	
	public void addFile(Message m) {
		String fileName = m.getText();
		String clientId = m.getSrc();
		server.addFile(fileName, clientId);
		streamProxy.write(new Message(clientId, "server", m.nextType()));
	}

	public String getIp() {
		//Separamos la ip del puerto debido a que el metodo getRemoteSocketAddress nos informa del puerto tambien
		String ip = socket.getRemoteSocketAddress().toString().split(":")[0].split("/")[1];
		return ip;
	}
	
	public String getClientId() { return id; }
	public String getUserName() { return username; }
	public String getDestIp() { return socket.getRemoteSocketAddress().toString(); }
	public boolean free() { return !bussyUploading; }
	
	private void startConnection(Message m) throws InterruptedException, IOException {
		username = m.getSrc();
		streamProxy.write(new Message(id.toString(), m.getDest(), m.nextType()));
		server.connectionStablished(id.toString());
	}
	
	private void startDownload(Message m) {
		String file = m.getText();
		String clientInfoWhoShares = server.startTransfer(file);
		Message mResponse;

		if (clientInfoWhoShares == null)
			mResponse = new Message(m.getSrc(), m.getDest(), m.nextType(), "error");
		else {
			bussyUploading = true;
			mResponse = new Message(m.getSrc(), m.getDest(), m.nextType(), clientInfoWhoShares);
		}
		
		streamProxy.write(mResponse);
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
				case DESCARGA_FICHERO:
					startDownload(m);
				case FIN_DESCARGA_FICHERO:
					bussyUploading = false;
					break;
				case FIN_EMISION_FICHERO:
					bussyUploading = false;
					break;
				default:
					break;
				}
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
