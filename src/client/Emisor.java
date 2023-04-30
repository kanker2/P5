package client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import common.Message;
import common.MessageType;

public class Emisor extends Thread{
	private String path;
	private Integer port;
	private ServerListener serverListener;
	private File file;
	
	public Emisor(String filename, String path, Integer port, ServerListener serverListener) {
		this.port = port;
		this.serverListener = serverListener;
		
		//Lanza un nuevo hilo para cargar el fichero en cuestion en File
		file = new File(filename, path);
		(new Thread(() -> file.uploadFileContent() )).start(); 
	}
	
	@Override
	public void run() {
		try {
			ServerSocket ss = new ServerSocket(port);
			Socket s = ss.accept();
			System.out.println("Emisor: conectado en purto " + port);
			
			while (!file.loaded());
			System.out.println("Emisor: Fichero cargado!");
			
			Message m = new Message("cliente", serverListener.getId(), MessageType.LISTA_EMISION_FICHERO, file);
			serverListener.write(m, s);
			System.out.println("Emisor: Lista emison fichero");
			m = serverListener.read(s);
			System.out.println("Emisor: Conf Lista emison fichero");

			serverListener.uploadFinished();
			
			s.close();
			ss.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
