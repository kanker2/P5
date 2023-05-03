package client;

import java.io.IOException;
import java.net.Socket;

import common.Message;
import common.StreamProxy;

public class Receptor extends Thread {
	private String ip;
	private Integer port;
	private Client c;
	private ServerListener serverListener;

	public Receptor(String ip, Integer port, Client c, ServerListener serverListener) {
		this.ip = ip;
		this.port = port;
		this.c = c;
		this.serverListener = serverListener;
	}

	@Override
	public void run() {

			try {
				Socket s = new Socket(ip, port); // Establece conexion con emisor
				
				Message m = serverListener.read(s); // Recibe fichero del emisor
				FileShared f = m.getFile();

				m = new Message(m.getSrc(), c.getId(), m.nextType()); // Responde con mensaje CONF_LISTA_EMISION_FICHERO
				serverListener.write(m, s);
				
				s.close(); // Cierre conexion

				c.succesDownload(f);

			} catch (IOException e) {
				e.printStackTrace();
			}

	}
}
