package client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import common.Message;

public class Receptor extends Thread {
	private String ip;
	private Integer port;
	private Client c;
	private ServerListener serverListener;
	private boolean connected = false;

	public Receptor(String ip, Integer port, Client c, ServerListener serverListener) {
		this.ip = ip;
		this.port = port;
		this.c = c;
		this.serverListener = serverListener;
	}

	@Override
	public void run() {

		while (!connected) {
			try {
				Socket s = new Socket(ip, port); // Establece conexion con emisor
				System.out.println("Receptor: conexion establecida");
				this.connected = true;
				Message m = serverListener.read(s); // Recibe fichero del emisor
				System.out.println("Receptor: fichero recibido");
				File f = m.getFile();

				m = new Message(m.getSrc(), c.getId(), m.nextType()); // Responde con mensaje CONF_LISTA_EMISION_FICHERO
				serverListener.write(m, s);
				System.out.println("Receptor: conf Lista emision fichero");
				
				s.close(); // Cierre conexion

				c.succesDownload(f);

			} catch (IOException e) {
				System.out.println("No se pudo conectar, reintentando en 3 segundos!");
				try {
					Thread.sleep(3000);
				} catch (InterruptedException ie) {
					// Manejar la interrupción del hilo
				}
				e.printStackTrace();
			}
		}

	}
}
