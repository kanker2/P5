package client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import common.Message;
import common.MessageType;
import common.StreamProxy;

public class Emisor extends Thread{
	private String path;
	private Integer port;
	private ServerListener serverListener;
	private FileShared file;
	
	private boolean readyToConnect;
	
	public Emisor(String filename, String path, Integer port, ServerListener serverListener) {
		this.port = port;
		this.serverListener = serverListener;
		readyToConnect = false;
		
		//Lanza un nuevo hilo para cargar el fichero en cuestion en File
		file = new FileShared(filename, path);
		(new Thread(() -> file.uploadFileContent() )).start(); 
	}
	
	public boolean isFileLoaded() {
		return this.file.loaded();
	}
	
	public boolean readyToConnect () {
		return readyToConnect;
	}
	
	@Override
	public void run() {
		try {
			ServerSocket ss = new ServerSocket(port);
			readyToConnect = true;
			Socket s = ss.accept();
			System.out.println("Emisor: conectado en puerto " + port);
			
			while (!file.loaded());
			System.out.println("Emisor: Fichero cargado!");
			
			Message m = new Message("cliente", serverListener.getId(), MessageType.LISTA_EMISION_FICHERO);
			m.setFile(file);
			serverListener.write(m, s);
			System.out.println("Emisor: Lista emision fichero");
			m = serverListener.read(s);
			System.out.println("Emisor: Conf Lista emision fichero");

			serverListener.uploadFinished();
			
			s.close();
			ss.close();
			readyToConnect = false;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
