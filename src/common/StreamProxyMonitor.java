package common;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public class StreamProxyMonitor extends StreamProxy{
	private Semaphore mutexIn, mutexOut;
	
	public StreamProxyMonitor(Socket s, Boolean fromClient) throws IOException{
		super(s, fromClient);
		
		mutexIn = new Semaphore(1);
		mutexOut = new Semaphore(1);
	}
	
	@Override
	public Message read() {
		Message m = null;
		try {
			mutexIn.acquire();
			m = super.read();
			mutexIn.release();
		} catch (InterruptedException e) {
			notifyObservers("error");
		}
		return m;
	}
	
	@Override
	public void write(Message o) {
		try {
			mutexOut.acquire();
			super.write(o);
			mutexOut.release();
		} catch(InterruptedException e) {
			e.printStackTrace();
			notifyObservers("error");
		}
	}
}
