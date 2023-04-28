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
		
		file = new File(filename, path);
		(new Thread(() -> file.uploadFileContent() )).start();
	}
	
	@Override
	public void run() {
		try {
			ServerSocket ss = new ServerSocket(port);
			Socket s = ss.accept();
			System.out.println(port);
			
			while (!file.loaded());
			
			Message m = new Message("cliente", serverListener.getId(), MessageType.LISTA_EMISION_FICHERO, file);
			serverListener.write(m, s);
			m = serverListener.read(s);

			serverListener.uploadFinished();
			
			s.close();
			ss.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
