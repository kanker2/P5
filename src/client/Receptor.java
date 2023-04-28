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
	
	public Receptor (String ip, Integer port, Client c, ServerListener serverListener) {
		this.ip = ip;
		this.port = port;
		this.c = c;
		this.serverListener = serverListener;
	}
	
	@Override
	public void run() {
		try {
			Socket s = new Socket(ip, port);
			Message m = serverListener.read(s);
			
			File f = m.getFile();
			
			m = new Message(m.getSrc(), c.getId(), m.nextType());
			serverListener.write(m, s);
			
			s.close();
			
			c.succesDownload(f);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
