package client;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import common.Observable;
import common.Observer;
import common.ProtocolError;
import main.Main;

public class Client extends Observable {
	private String id;
	private String username;
	private ServerListener serverListener;
	private Map<String, String> shareableFiles; //Nombre fichero, localizacion fichero
	private Set<String> downloadableFiles;
	
	public Client (String username, String ip, int port) throws ClassNotFoundException, IOException, ProtocolError {
		this.username = username;
		shareableFiles = new TreeMap<>();
		serverListener = new ServerListener(this, ip, port);
		(new File(Main.DEFAULT_DOWNLOADS_DIR)).mkdir();
	}

	//Cliente se conecta por primera vez
	public void connectToServer() throws IOException, ClassNotFoundException, ProtocolError{
		serverListener.connectToServer(); //Establece comunicacion con server para adquirir un id de usuario
		serverListener.listen();		  //El hilo empieza a escuchar
	}
	
	//Cliente cierra sesion
	public void closeConnection() {
		serverListener.closeConnection();
		notifyObservers("close_connection");
	}
	
	//Cliente desea descargar un fichero del servidor
	public void downloadFile(String file) {
		serverListener.downloadFile(file);
	}
	
	//Cliente ayande nuevo fichero para compartir
	public void newShareableFile(String name, String path) {
		shareableFiles.put(name, path);
		notifyObservers("owned_files");	
		serverListener.newShareableFile(name);
	}
	
	//Actualiza lista de ficheros disponibles para descargar
	public void updateDownloadableFiles(Set<String> downloadableFiles) {
		this.downloadableFiles = downloadableFiles;
		notifyObservers("downloadable_files");
	}
	
	//Comienza descarga de un fichero en un nuevo hilo
	public void startFileDownload(String ip, Integer port) {
		Receptor r = new Receptor(ip, port, this, serverListener);
		r.start();
	}
	
	//Notifica fallo de descarga
	public void failedDownload() {
		notifyObservers("failed_download");
	}
	
	//Notifica descarga exitosa
	public void succesDownload(FileShared f) {
		serverListener.downloadFinished();
		notifyObservers("success_download:" + f.getFileName());
		saveFile(f);
	}
	
	public void listFiles() {
		
	}
	
	public String getId() { return id; }
	public String getUsername() { return username; }
	public Map<String, String> getShareableFiles() { return shareableFiles; }
	public Set<String> getDownloadableFiles() {return downloadableFiles; }
	public String getIp() { return serverListener.getIp(); }
	public Integer getPort() { return serverListener.getPort(); }
	public String getPath(String filename) { return shareableFiles.get(filename); }
	
	public void setId(String id) { 
		this.id = id;
		notifyObservers("id_set");
	}
	
	public void setLog(Observer o) {
		serverListener.setLog(o);
	}
	
	private void saveFile(FileShared f) {
		String path = Main.DEFAULT_DOWNLOADS_DIR + "/" + f.getFileName();
		
		f.setPath(path);
		try {
			f.writeFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		newShareableFile(f.getFileName(), path);
	}
}