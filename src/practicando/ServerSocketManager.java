package practicando;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;

public class ServerSocketManager extends Thread{
	private Server s;
	
	public ServerSocketManager(Server s) {
		this.s = s;
	}
	
	public void run() {
		while (true) {
			try {
				Socket socket = s.getSS().accept();
				(new Thread(() -> {
					try {
						BufferedReader finC = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						String fileName = finC.readLine();
						File f = new File(fileName);

						f.updateContent(s.getContent(f.getFileName()));
						
						this.sleep(new Random().nextInt(1000));
						
						ObjectOutputStream foutC = new ObjectOutputStream(socket.getOutputStream());
						foutC.writeObject(f);
						foutC.flush();
					}
					catch (IOException e) {
						System.err.println("Error consiguiendo el contenido");
						e.printStackTrace();
					}
					catch (InterruptedException e) { e.printStackTrace(); }
				})).start();
			}
			catch (IOException e) {
				System.err.println("ERROR leyendo socket en ServerSocketManager");
				e.printStackTrace();
			}
		}
	}
}
