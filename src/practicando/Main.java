package practicando;

import java.io.IOException;
import java.net.InetAddress;

public class Main {

	public static void main(String[] args) throws IOException {
		Thread tS = new Thread(() -> Server.f1()),
			   tC = new Thread(() -> Client.f1());
		
//		tS.start();
//		tC.start();
		
		Server s = new Server(1300);
		Client c = new Client(0, "localhost", 1300, "a.txt");
//		for(int i = 0; i < 2; i++) {
//			Client c = new Client(i, InetAddress.getLocalHost(), 1300, "a.txt");
//		}
	}
}