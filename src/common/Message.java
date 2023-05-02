package common;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import client.FileShared;

public class Message implements Serializable{
	private static final long serialVersionUID = 1L;
	private MessageType type;
	private String dest, src;
	private String fileName;
	private String ip; private Integer port;
	private Set<String> files;
	private FileShared file;
	private String id;
	private String addres;
	
	public Message(String dest, String src, MessageType type) {
		this.type = type;
		this.src = src;
		this.dest = dest;
	}
	
	public void setType(MessageType f) { type = f; }
	public void setDest(String f) { dest = f; }
	public void setSrc(String f) { src = f; }
	public void setText(String f) { fileName = f; }
	public void setIp(String f) { ip = f; }
	public void setPort(Integer f) { port = f; }
	public void setFiles(Set<String> f) { files = f; }
	public void setFile(FileShared f) { file = f; }
	public void setId(String f) { id = f; }
	public void setAddres(String f) { addres = f; }
	
	public MessageType getType() { return type; }
	public String getDest() { return dest; }
	public String getSrc() { return src; }
	public String getText() { return fileName; }
	public String getIp() { return ip; }
	public Integer getPort() { return port; }
	public Set<String> getFiles() { return files; }
	public FileShared getFile() { return file; }
	public String getId() {return id;}
	public String getAddres() {return this.addres;}

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
