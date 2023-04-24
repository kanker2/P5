package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import common.Message;
import common.Observable;
import common.Observer;

public class StreamProxy extends Observable{
	private ObjectInputStream fin;
	private ObjectOutputStream fout;
	private Message lastGoodMessage;
	
	public StreamProxy(Socket s) throws IOException {
		fin = new ObjectInputStream(s.getInputStream());
		fout = new ObjectOutputStream(s.getOutputStream());
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
			notifyObservers(o);
			fout.writeObject(o);
			fout.flush();
			lastGoodMessage = o;
		} catch(IOException e) {
			notifyObservers("error");
		}
	}
}
