package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.Semaphore;

import common.Message;
import common.MessageType;

public class Server extends Thread{
	
	Map<String, Set<String>> filesToClientIds;
	Map<String, ClientListener> clientListeners;
	private SystemInfoMonitor infoMonitor;
	
	Semaphore idsMutex;
	Integer idGenerator;
	
	private ServerSocket ss;
	
	public Server(Integer port) throws IOException{
		filesToClientIds = new TreeMap<>();
		infoMonitor = new SystemInfoMonitor();
		
		clientListeners = new TreeMap<>();
		
		idGenerator = 0;
		idsMutex = new Semaphore(1);
		
		ss = new ServerSocket(port);
	}
	
	public String getNewId() throws InterruptedException {
		idsMutex.acquire();
		Integer tmp = idGenerator;
		idsMutex.release();
		return tmp.toString();
	}

	public void notifyFileActualization(String clientId) {
		infoMonitor.request_read();
		for(Map.Entry<String, ClientListener> entry : clientListeners.entrySet()) {
			Map<String, Object> args = new TreeMap<>();
			args.put("ficheros", filesToClientIds.keySet());
			if (entry.getKey() != clientId) {
				ClientListener cl = entry.getValue();
				cl.filesActualization(args);
			}
		}
		infoMonitor.release_read();
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
//		notifyFileActualization(clientId);
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				Socket s = ss.accept();
				ClientListener cl = new ClientListener(s, this);
				cl.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
