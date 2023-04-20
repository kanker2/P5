package common;

import java.io.Serializable;

public class Message implements Serializable{
	private MessageType type;
	private String dest, src;
	
	public Message(String dest, String src, MessageType type) {
		this.type = type;
		this.src = src;
		this.dest = dest;
	}
	
	public MessageType getType() { return type; }
	public String getDest() { return dest; }
	public String getSrc() { return src; }
	
	public MessageType nextType() {
		return null;
	}
}
