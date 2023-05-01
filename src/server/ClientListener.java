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
	private Boolean busyUploading;
	
	public ClientListener(String id, Socket socket, Server s, Observer log) throws IOException {
		this.id = id;
		this.socket = socket;
		this.server = s;
		streamProxy = new StreamProxyMonitor(socket, false);
		streamProxy.setLog(log);

		listening = true;
		busyUploading = false;
	}
	
	public void updateFiles(Set<String> files) {
		Message m = new Message(id, "server", MessageType.ACTUALIZAR_FICHEROS, files);
		streamProxy.write(m);
	}
	
	public void prepareUpload(String file, Integer port, String id, String addres) {
		busyUploading = true;
		Message m = new Message(this.id, "server", MessageType.PETICION_EMISION_FICHERO, file, port, Integer.parseInt(id), addres);
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
	public boolean free() { return !busyUploading; }
	
	private void startConnection(Message m) throws InterruptedException, IOException {
		username = m.getSrc();
		streamProxy.write(new Message(id.toString(), m.getDest(), m.nextType()));
		server.connectionStablished(id.toString());
	}
	
	//Busca en el Servidor que clientes puede compartir dicho fichero
	private void startDownload(Message m) {
		String file = m.getText();
		//Devuelve info del cliente que puede compartir el fichero en formato ip:port
		if (server.startTransfer(file, m.getSrc())) {
			busyUploading = true;
		}; 

//		String clientInfoWhoShares; //		
//		Message mResponse;
//
//		if (clientInfoWhoShares == null)
//			mResponse = new Message(m.getSrc(), m.getDest(), m.nextType(), "error");
//		else {
//			busyUploading = true;
//			mResponse = new Message(m.getSrc(), m.getDest(), m.nextType(), clientInfoWhoShares); //CONF_DESCARGA_FICHERO
//		}
//		
//		streamProxy.write(mResponse);
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
	
	public StreamProxyMonitor getStream() {
		return this.streamProxy;
	}
	
	//Envia de vuelta la confirmacion de descarga de fichero al cliente que habia pedido la descarga
	private void sendToClientListener(Message m) {
		ClientListener cl = server.getClientListener(m.getId().toString());
		Message msg = new Message(cl.getClientId(), "servidor", MessageType.CONF_DESCARGA_FICHERO, m.getAddres());
		cl.getStream().write(msg);
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
					busyUploading = false;
					break;
				case FIN_EMISION_FICHERO:
					busyUploading = false;
					break;
				case CONF_PETICION_EMISION_FICHERO:
					sendToClientListener(m);
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
