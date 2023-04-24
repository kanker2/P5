package common;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Message implements Serializable{
	private static final long serialVersionUID = 1L;
	private MessageType type;
	private String dest, src;
	private String fileName;
	private String ip; private Integer port;
	private Set<String> files;
	
	public Message(String dest, String src, MessageType type, String fileName) {
		this(dest,src,type);
		this.fileName = fileName;
	}
	
	public Message(String dest, String src, MessageType type, String ip, Integer port) {
		this(dest,src,type);
		this.ip = ip;
		this.port = port;
	}
	
	public Message(String dest, String src, MessageType type, Set<String> files) {
		this(dest,src,type);
		this.files = new HashSet<>(files);
	}
	
	public Message(String dest, String src, MessageType type) {
		this.type = type;
		this.src = src;
		this.dest = dest;
	}
	
	public MessageType getType() { return type; }
	public String getDest() { return dest; }
	public String getSrc() { return src; }
	public String getFileName() { return fileName; }
	public String getIp() { return ip; }
	public Integer getPort() { return port; }
	public Set<String> getFiles() { return files; }

	public MessageType nextType() {
		switch(type) {
		case INICIAR_CONEXION:
			return MessageType.CONF_INICIAR_CONEXION;
		case ACTUALIZAR_FICHEROS:
			return MessageType.CONF_ACTUALIZAR_FICHEROS;
		case NUEVO_FICHERO_C:
			return MessageType.CONF_NUEVO_FICHERO_C;
		case FIN_CONEXION:
			return MessageType.CONF_FIN_CONEXION;
		case NUEVO_FICHERO_S:
			return MessageType.CONF_NUEVO_FICHERO_S;
		case DESCARGA_FICHERO:
			return MessageType.CONF_DESCARGA_FICHERO;
		case CONF_DESCARGA_FICHERO:
			return MessageType.LISTA_EMISION_FICHERO;
		case LISTA_EMISION_FICHERO:
			return MessageType.CONF_LISTA_EMISION_FICHERO;
		case PETICION_EMISION_FICHERO:
			return MessageType.CONF_PETICION_EMISION_FICHERO;
		default:
			return MessageType.ERROR;
		}
	}
	
	@Override
	public String toString() {
		return src + " -> " + dest + ": " + type;
	}
}
