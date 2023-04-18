package practicando;

import java.io.Serializable;

public class File implements Serializable{
	private String fileName;
	private String content;
	
	public File(String fileName) {
		this.fileName = fileName;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public void updateContent(String content) {
		this.content = content;
	}
	
	@Override
	public String toString() {
		return "FileName: \n"+fileName+"\nContent: \n"+content+"\n";
	}
}
