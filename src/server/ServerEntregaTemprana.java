package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.Semaphore;

import common.Observable;
import common.Observer;

public class ServerEntregaTemprana extends Observable implements Runnable{
	
	private Map<String, Set<String>> filesToClients;
	private Map<String, ClientListener> clientListeners;
	private SystemInfoMonitor filesToClientsMonitor, clientListenersMonitor;
	
	private Observer log;
	
	Semaphore idsMutex;
	Integer idGenerator;
	
	private ServerSocket ss;
	
	public ServerEntregaTemprana(Integer port) throws IOException{
		filesToClients = new TreeMap<>();
		clientListeners = new TreeMap<>();
		
		filesToClientsMonitor = new SystemInfoMonitor();
		clientListenersMonitor = new SystemInfoMonitor();
		
		
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
		filesToClientsMonitor.request_read();
		Set<String> clientsWithFile;
		if (filesToClients.containsKey(fName))
			clientsWithFile = new HashSet<>();
		else
			clientsWithFile = new HashSet<>(filesToClients.get(fName));
		filesToClientsMonitor.release_read();
		
		clientsWithFile.add(clientId);
		
		filesToClientsMonitor.request_write();
		filesToClients.put(fName, clientsWithFile);
		filesToClientsMonitor.release_write();
		
		notifyObservers("add_file");
		notifyNewFilesToClients();
	}
	
	public void notifyNewFilesToClient(String id) {
		filesToClientsMonitor.request_read();
		Set<String> files = new HashSet<>(filesToClients.keySet());
		filesToClientsMonitor.release_read();

		clientListenersMonitor.request_read();
		ClientListener cl = clientListeners.get(id);
		clientListenersMonitor.release_read();
		cl.updateFiles(files);
	}
	
	public void notifyNewFilesToClients() {
		filesToClientsMonitor.request_read();
		Set<String> files = new HashSet<>(filesToClients.keySet());
		filesToClientsMonitor.release_read();
		
		clientListenersMonitor.request_read();
		Collection<ClientListener> cls = new HashSet<>(clientListeners.values());
		clientListenersMonitor.release_read();
		
		for(ClientListener cl : cls) 
			cl.updateFiles(files);
	}
	
	public void connectionStablished(String id) {
		notifyObservers("new_connection");
		notifyNewFilesToClient(id);
	}
	
	public void closeConnection(String id) {
		clientListenersMonitor.request_write();
		clientListeners.remove(id);
		clientListenersMonitor.release_write();
		
		removeClientFiles(id);

		notifyObservers("removed_connection");
		notifyNewFilesToClients();
	}
	
	private void createNewClientListener(Socket s) throws IOException, InterruptedException {
		String id = getNewId();
		ClientListener cl = new ClientListener(id, s, this, log);
		
		clientListenersMonitor.request_write();
		clientListeners.put(id, cl);
		clientListenersMonitor.release_write();
		
		cl.start();
	}
	
	private void removeClientFiles(String id) {
		Set<String> removableFiles = new TreeSet<>();
		
		filesToClientsMonitor.request_read();
		Set<Entry<String, Set<String>>> entrySet = filesToClients.entrySet();
		filesToClientsMonitor.release_read();

		for(Map.Entry<String, Set<String>> entry : entrySet) {
			Set<String> clientsWithFile = entry.getValue();
			clientsWithFile.remove(id);
			if (clientsWithFile.isEmpty())
				removableFiles.add(entry.getKey());
		}
		
		filesToClientsMonitor.request_write();
		for(String removableFile : removableFiles)
			filesToClients.remove(removableFile);
		filesToClientsMonitor.release_write();
	}
	
	public Map<String, String> getUsers(){
		clientListenersMonitor.request_read();
		Set<Entry<String, ClientListener>> entrySet = new HashSet<>(clientListeners.entrySet());
		clientListenersMonitor.release_read();
		
		Map<String, String> users = new HashMap<String, String>();
		for(Map.Entry<String, ClientListener> entry : entrySet) 
			users.put(entry.getKey(), entry.getValue().getUserName());
		
		return users;
	}
	
	public Set<String> getDownloadableFiles(){
		filesToClientsMonitor.request_read();
		Set<String> downloadableFiles = new HashSet<>(filesToClients.keySet());
		filesToClientsMonitor.release_read();
		
		return downloadableFiles;
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