package common;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Semaphore;

import common.manageConcurrency.LockTicket;

//Clase que encapsula StreamProxy y garantiza la concurrencia, se emplea en la clase ClientListener
//Dado que se puede dar concurrencia a la hora de subir un fichero en el lado del cliente
//Ya que esta accion implica que el hilo que recibe el mensaje en ClientListener tiene que notificar
//Al resto de clientes de que la lista de ficheros se a actualizado

public class StreamProxyMonitor extends StreamProxy{
	private LockTicket mutexIn, mutexOut;
	
	public StreamProxyMonitor(Socket s, Boolean fromClient) throws IOException{
		super(s, fromClient);
		
		mutexIn = new LockTicket();
		mutexOut = new LockTicket();
	}
	
	@Override
	public Message read() {
		Message m = null;
		mutexIn.lock();
		m = super.read();
		mutexIn.unlock();
		return m;
	}
	
	@Override
	public void write(Message o) {
		mutexOut.lock();
		super.write(o);
		mutexOut.unlock();
	}
}
