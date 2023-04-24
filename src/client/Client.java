package client;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import common.Observable;
import common.Observer;
import common.ProtocolError;

public class Client extends Observable {
	private String id;
	private String username;
	private ServerListener serverListener;
	private Map<String, String> shareableFiles; //Nombre fichero, localizacion fichero
	private Set<String> downloadableFiles;
	
	public Client (String username, String ip, int port) throws ClassNotFoundException, IOException, ProtocolError {
		this.username = username;
		shareableFiles = new TreeMap<>();
		serverListener = new ServerListener(this, ip, port);
	}
	
	public String getId() { return id; }
	public String getUsername() { return username; }
	public Map<String, String> getShareableFiles() { return shareableFiles; }
	public Set<String> getDownloadableFiles() {return downloadableFiles; }
	public String getIp() { return serverListener.getIp(); }
	public Integer getPort() { return serverListener.getPort(); }
	
	public void setId(String id) { 
		this.id = id;
		notifyObservers("id_set");
	}

	public void connectToServer() throws IOException, ClassNotFoundException, ProtocolError{
		serverListener.connectToServer();
		(new Thread(serverListener)).start();
	}
	
	public void newShareableFile(String name, String path) {
		shareableFiles.put(name, path);
		notifyObservers("owned_files");
		
		serverListener.newShareableFile(name);
	}
	
	public void updateDownloadableFiles(Set<String> downloadableFiles) {
		this.downloadableFiles = downloadableFiles;
		notifyObservers("downloadable_files");
	}
	
	public void listFiles() {
		
	}
	
	public void downloadFile(String file) {
		
	}
	
	public void closeConnection() {
		serverListener.closeConnection();
		notifyObservers("close_connection");
	}
	
	public void setLog(Observer o) {
		serverListener.setLog(o);
	}
}