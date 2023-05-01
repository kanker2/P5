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
import java.util.TreeSet;
import java.util.concurrent.Semaphore;

import common.Observable;
import common.Observer;
import common.manageConcurrency.ConcurrentHashMap;
import common.manageConcurrency.MonitorAccesManager;
import common.manageConcurrency.SemaphoreAccesManager;
import main.Main;

public class Server extends Observable implements Runnable{
	
	private Map<String, Set<String>> filesToClients; 		//Nombre de fichero, Conjunto de clientes que lo tiene
	private Map<String, ClientListener> clientListeners;	//Id de cliente, el clientListener asociado
	private Observer log;
	
	private Semaphore idsMutex;
	private Integer idGenerator;
	
	private ServerSocket ss;
	
	public Server(Integer port) throws IOException{
		filesToClients = new ConcurrentHashMap<>(new SemaphoreAccesManager());
		clientListeners = new ConcurrentHashMap<>(new SemaphoreAccesManager());	
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
	
	//Actualiza la tabla de ficheros y clientes que lo tiene
	public void addFile(String fName, String clientId) {
		Set<String> clientsWithFile;
		if (!filesToClients.containsKey(fName))
			clientsWithFile = new HashSet<>();
		else
			clientsWithFile = new HashSet<>(filesToClients.get(fName));
		
		clientsWithFile.add(clientId);
		
		filesToClients.put(fName, clientsWithFile);
		
		notifyObservers("add_file");
		notifyNewFilesToClients();
	}
	

	public void notifyNewFilesToClient(String id) {
		Set<String> files = new HashSet<>(filesToClients.keySet());
		ClientListener cl = clientListeners.get(id);
		cl.updateFiles(files);
	}
	
	public void notifyNewFilesToClients() {
		Set<String> files = new HashSet<>(filesToClients.keySet());		
		Collection<ClientListener> cls = new HashSet<>(clientListeners.values());
		
		for(ClientListener cl : cls) 
			cl.updateFiles(files);
	}
	
	public void connectionStablished(String id) {
		notifyObservers("new_connection");
		notifyNewFilesToClient(id);
	}
	
	public void closeConnection(String id) {
		clientListeners.remove(id);
		
		if (removeClientFiles(id))
			notifyNewFilesToClients();

		notifyObservers("removed_connection");
	}
	
	//Server busca un cliente que pueda compartir dicho fichero
	public boolean startTransfer(String file, String id) {
		ClientListener clWhoShares = null;
		HashSet<String> idsWhoCanShare = new HashSet<String>(filesToClients.get(file));

		//Buscamos al cliente que vaya a transmitir el archivo 
		for (String clientId : idsWhoCanShare) {
			clWhoShares = clientListeners.get(clientId);
			if (clWhoShares.free()) 
				break;
			else
				clWhoShares = null;
		}
		if (clWhoShares == null)
			return false;
		
		Integer portUsed = Main.FILE_SHARING_PORT;
		//Servidor manda PETICION_EMISION_FICHERO al cliente que puede compartir el fichero
		String addres = clWhoShares.getIp() + ":" + portUsed;
		clWhoShares.prepareUpload(file, portUsed, id, addres); 
	
		return true;
	}
	
	private void createNewClientListener(Socket s) throws IOException, InterruptedException {
		String id = getNewId();
		ClientListener cl = new ClientListener(id, s, this, log);
		
		clientListeners.put(id, cl);
		
		cl.start();
	}
	
	//Returns true if any file was removed from the table
	private boolean removeClientFiles(String id) {
		Set<String> removableFiles = new TreeSet<>();
		
		Set<Entry<String, Set<String>>> entrySet = new HashSet<>(filesToClients.entrySet());

		for(Map.Entry<String, Set<String>> entry : entrySet) {
			Set<String> clientsWithFile = entry.getValue();
			clientsWithFile.remove(id);
			if (clientsWithFile.isEmpty())
				removableFiles.add(entry.getKey());
		}
		
		for(String removableFile : removableFiles)
			filesToClients.remove(removableFile);
		return !removableFiles.isEmpty();
	}
	
	public Map<String, String> getUsers(){
		Set<Entry<String, ClientListener>> entrySet = new HashSet<>(clientListeners.entrySet());
		
		Map<String, String> users = new HashMap<String, String>();
		for(Map.Entry<String, ClientListener> entry : entrySet) 
			users.put(entry.getKey(), entry.getValue().getUserName());
		
		return users;
	}
	
	public Set<String> getDownloadableFiles(){
		Set<String> downloadableFiles = new HashSet<>(filesToClients.keySet());	
		return downloadableFiles;
	}
	
	public void setLog(Observer o) {
		log = o;
	}
	
	public ClientListener getClientListener(String id) {
		return clientListeners.get(id);
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