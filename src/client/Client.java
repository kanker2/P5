package client;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Client {
	private String id;
	private String username;
	private ServerListener serverListener;
	private Set<String> availableFiles;
	/*
	c1 quiere a2
		pide a oyenteServidor a2
		oyenteServidor pide a servidor a2
		servidor buscar socket correspondiente sc
		servidor manda sc a oyenteServidor
		oyenteServidor crea instancia receptor con la info de sc y de c1
		receptor crea canales de input y output con sc
		receptor recibe archivo
		receptor envia archivo a c1 (c1.recibirArchivo(archivoLeido))
		
	1 listarArchivos
		1 notificar al oyenteServidor
		2 nos devuelve la info
		3 ya veremos que hacemos con eso
	2 descargarArchivo
		1 notificas al oyenteServidor y le envias el archivo que quieres
	3 terminarSesion
		1 se notifica al oyenteServidor
		
	4 (opcional) cargarArchivo
	*/
	
	public Client (String username, String ip, int port) throws IOException{
		this.username = username;
		availableFiles = new HashSet<>();
		serverListener = new ServerListener(this, ip, port);
	}
	
	public String getId() { return id; }
	public String getUsername() { return username; }
	
	public void listFiles() {
		new Thread(() -> {
			try {
				serverListener.listFiles();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
	
	public void receiveFiles() {
		/*Updateamos info*/
	}
	
	public void downloadFile(String file) {
		
	}
	
	public void byebye() {
		
	}
}