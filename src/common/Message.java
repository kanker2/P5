package common;

import java.io.Serializable;
import java.util.Map;

public class Message implements Serializable{
	private MessageType type;
	private String dest, src;
	private Map<String, Object> content;
	
	public Message(String dest, String src, MessageType type, Map<String, Object> content) {
		this.type = type;
		this.src = src;
		this.dest = dest;
		this.content = content;
	}

	public Message(String dest, String src, MessageType type) {
		this(dest,src,type,null);
	}
	
	public MessageType getType() { return type; }
	public String getDest() { return dest; }
	public String getSrc() { return src; }
	public Object getcontent(String key) { return content.get(key); }

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
