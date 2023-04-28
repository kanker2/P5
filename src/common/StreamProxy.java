package common;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

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
			lastGoodMessage = o;
			notifyObservers(o);
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
	
	public Message read(Socket s) {
		Message m = null;
		try {
			ObjectInputStream fin = new ObjectInputStream(s.getInputStream());
			m = (Message) fin.readObject(); 
			notifyObservers(m);
			fin.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return m;
	}
	
	public void write(Message m, Socket s) {
		try {
			ObjectOutputStream fout = new ObjectOutputStream(s.getOutputStream());
			fout.writeObject(m);
			fout.flush();
			notifyObservers(m);
			fout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
