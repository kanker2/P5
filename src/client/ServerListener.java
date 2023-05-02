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
import java.util.concurrent.Semaphore;

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
	
	//Asigna un nuevo Id al cliente asociado a este ServerListener
	public void connectToServer() throws IOException, ClassNotFoundException, ProtocolError{
		String clientId = stablishConnection();
		client.setId(clientId);	
	}
	
	//Establece conexion con Server y recupera un nuevo Id de cliente
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

	//Peticion de descarga de fichero al servidor
	public void downloadFile(String file) {
		Message serverReq = new Message("server", client.getId(), MessageType.DESCARGA_FICHERO);
		serverReq.setText(file);
		streamProxy.write(serverReq);
	}
	
	//Notifica al servidor de que el cliente tiene un nuevo fichero para compartir
	public void newShareableFile(String name) {
		Message m = new Message("server", client.getId(), MessageType.NUEVO_FICHERO_C);
		m.setText(name);
		streamProxy.write(m);
	}
	
	public void uploadFinished() {
		Message m = new Message("server", client.getId(), MessageType.FIN_EMISION_FICHERO);
		streamProxy.write(m);
	}
	
	public void downloadFinished() {
		Message m = new Message("server", client.getId(), MessageType.FIN_DESCARGA_FICHERO);
		streamProxy.write(m);
	}
	
	//Actualiza los ficheros que el cliente en cuestion puede descargar
	private void updateDownloadableFiles(Message m) {
		Set<String> downloadableFiles = m.getFiles();
		client.updateDownloadableFiles(downloadableFiles);
		streamProxy.write(new Message(m.getSrc(), m.getDest(), m.nextType()));
	}
	
	private void startFileUpload(String filename, Integer port, String id, String ip) {
		Emisor e = new Emisor(filename, client.getPath(filename), port, this);
		e.start();
		while(!e.readyToConnect());
		Message m = new Message("server", client.getId(), MessageType.CONF_PETICION_EMISION_FICHERO);
		m.setId(id);
		m.setIp(ip);
		m.setPort(port);
		streamProxy.write(m);	
		
	}
	
	
	/*---------------------------------------------------------------*/
	//Metodos para la emision de ficheros entre clientes de forma p2p
	public void write(Message m, Socket s) {
		streamProxy.write(m, s);
	}
	
	public Message read(Socket s) {
		return streamProxy.read(s);
	}
	/*---------------------------------------------------------------*/

	
	public String getIp() { return socketToServer.getInetAddress().getHostAddress(); }
	public Integer getPort() { return socketToServer.getPort(); }
	public String getId() { return client.getId(); }
	
	public void setLog(Observer o) {
		streamProxy.setLog(o);
	}
	
	//ServerListener empieza a escuchar
	public void listen() {
		(new Thread(this)).start();
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
				case PETICION_EMISION_FICHERO:
					String file = m.getText();
					Integer port = m.getPort(); 
					String sendToClientId = m.getId();
					String ip = m.getIp();
					startFileUpload(file, port, sendToClientId,ip);
					break;
				case CONF_DESCARGA_FICHERO:
					String text = m.getText();
					//Si todo a ido bien y se puede descargar el archivo recibimos un string de la siguiente forma
					//[ip]:[port]
					//En caso contrario un string con el valor error
					if (text.equals("error"))
						client.failedDownload();
					else 
						client.startFileDownload(m.getIp(), m.getPort());
					break;
				default:
					break;
				}
			}
		}
	}
}