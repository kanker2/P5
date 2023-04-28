package client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class File implements Serializable{
	private String filename;
	private String path;
	private ArrayList<String> lines;
	private boolean loaded;
	
	public File(String filename, String path) {
		loaded = false;
		
		this.filename = filename;
		this.path = path;
		lines = new ArrayList<>();
	}

	public String getFileName() { return filename; }
	public ArrayList<String> getFileContent(){ return lines; }
	public boolean loaded () { return loaded; }
	
	public void uploadFileContent() {
		try {
			BufferedReader fin = new BufferedReader(new FileReader(new java.io.File(path)));
			String line = fin.readLine();
			while (line != null) {
				lines.add(line);
				line = fin.readLine();
			}
		} catch ( IOException e) {
			e.printStackTrace();
		}
		
		loaded = true;
	}
}
