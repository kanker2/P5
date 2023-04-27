package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.Semaphore;

import common.Observable;
import common.Observer;

public class Server extends Observable implements Runnable{
	
	private Map<String, Set<String>> filesToClientIds;
	private Map<String, ClientListener> clientListeners;
	private SystemInfoMonitor infoMonitor;
	
	private Observer log;
	
	Semaphore idsMutex;
	Integer idGenerator;
	
	private ServerSocket ss;
	
	public Server(Integer port) throws IOException{
		filesToClientIds = new TreeMap<>();
		clientListeners = new TreeMap<>();
		
		infoMonitor = new SystemInfoMonitor();
		
		
		idGenerator = 0;
		idsMutex = new Semaphore(1);
		
		ss = new ServerSocket(port);
	}
	
	public String getNewId() throws InterruptedException {
		idsMutex.acquire();
		Integer tmp = idGenerator;
		idGenerator++;
		idsMutex.release();
		return tmp.toString();
	}
	
	public void addFile(String fName, String clientId) {
		infoMonitor.request_write();
		Set<String> clientsWithFile = filesToClientIds.get(fName);
		if (clientsWithFile != null)
			clientsWithFile.add(clientId);
		else {
			clientsWithFile = new TreeSet<>();
			clientsWithFile.add(clientId);
			filesToClientIds.put(fName, clientsWithFile);
		}
		infoMonitor.release_write();
		notifyObservers("add_file");
		notifyNewFilesToClients();
	}
	
	public void notifyNewFilesToClient(String id) {
		infoMonitor.request_read();
		Set<String> files = new HashSet<>(filesToClientIds.keySet());
		ClientListener cl = clientListeners.get(id);
		infoMonitor.release_read();
		cl.updateFiles(files);
	}
	
	public void notifyNewFilesToClients() {
		infoMonitor.request_read();
		Set<String> files = filesToClientIds.keySet();
		for(Map.Entry<String, ClientListener> entry : clientListeners.entrySet()) {
			ClientListener cl = entry.getValue();
			cl.updateFiles(files);
		}
		infoMonitor.release_read();
	}
	
	public void connectionStablished(String id) {
		notifyObservers("new_connection");
		notifyNewFilesToClient(id);
	}
	
	public void closeConnection(String id) {
		infoMonitor.request_write();
		clientListeners.remove(id);
		removeClientFiles(id);
		infoMonitor.release_write();
		notifyObservers("removed_connection");
		notifyNewFilesToClients();
	}
	
	private void createNewClientListener(Socket s) throws IOException, InterruptedException {
		String id = getNewId();
		ClientListener cl = new ClientListener(id, s, this, log);
		
		infoMonitor.request_write();
		clientListeners.put(id, cl);
		infoMonitor.release_write();
		
		cl.start();
	}
	
	private void removeClientFiles(String id) {
		Set<String> removableFiles = new TreeSet<>();
		for(Map.Entry<String, Set<String>> entry : filesToClientIds.entrySet()) {
			Set<String> clientsWithFile = entry.getValue();
			clientsWithFile.remove(id);
			if (clientsWithFile.isEmpty())
				removableFiles.add(entry.getKey());
		}
		
		for(String removableFile : removableFiles)
			filesToClientIds.remove(removableFile);
	}
	
	public Map<String, String> getUsers(){
		Map<String, String> users = new HashMap<String, String>();
		infoMonitor.request_read();
		for(Map.Entry<String, ClientListener> entry : clientListeners.entrySet()) 
			users.put(entry.getKey(), entry.getValue().getUserName());
		infoMonitor.release_read();
		return users;
	}
	
	public Set<String> getDownloadableFiles(){
		infoMonitor.request_read();
		Set<String> downloadableFiles = filesToClientIds.keySet();
		infoMonitor.release_read();
		return new HashSet<>(downloadableFiles);
	}
	
	public void setLog(Observer o) {
		log = o;
	}

	@Override
	public void run() {
		while(true) {
			try {
				Socket s = ss.accept();
				
				createNewClientListener(s);
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}