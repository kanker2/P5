package common;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import common.Message;
import common.Observable;
import common.Observer;

public class StreamProxy extends Observable{
	protected ObjectInputStream fin;
	protected ObjectOutputStream fout;
	private Message lastGoodMessage;
	
	public StreamProxy(Socket s) throws IOException {
		this(s, true);
	}
	
	public StreamProxy(Socket s, Boolean fromClient) throws IOException{
		if (fromClient) {
			fin = new ObjectInputStream(s.getInputStream());
			fout = new ObjectOutputStream(s.getOutputStream());
		}
		else {
			fout = new ObjectOutputStream(s.getOutputStream());
			fin = new ObjectInputStream(s.getInputStream());
		}
	}
	
	public Message lastCommunication() { return lastGoodMessage; }
	
	public void setLog(Observer o) {
		this.addObserver(o);
	}
	
	public Message read() {
		try {
			Message o = (Message) fin.readObject();
			notifyObservers(o);
			lastGoodMessage = o;
			return o;
		} catch(ClassNotFoundException | IOException e) {
			notifyObservers("error");
			return null;
		}
	}
	
	public void write(Message o){
		try {
			fout.writeObject(o);
			fout.flush();
			lastGoodMessage = o;
			notifyObservers(o);
		} catch(IOException e) {
			e.printStackTrace();
			notifyObservers("error");
		}
	}
}
