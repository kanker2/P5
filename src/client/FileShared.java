package client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;

public class FileShared implements Serializable{
	private String filename;
	private String path;
	private ArrayList<String> lines;
	private boolean loaded;
	
	public FileShared(String filename, String path) {
		this.loaded = false;		
		this.filename = filename;
		this.path = path;
		this.lines = new ArrayList<>();
	}

	public void setPath(String path) { this.path = path; }
	
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
	
	public void writeFile() throws IOException {
		File f = new File(path);
		f.createNewFile();

		PrintWriter pw = new PrintWriter(f);
		for (String line : lines) {
			pw.println(line);
			pw.flush();
		}
	}
	
	public String toString() {
		
		StringBuilder s = new StringBuilder();
		for (String line : lines) {
			s.append(line);
		}
		return s.toString();
	}
}
