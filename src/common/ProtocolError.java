package common;

public class ProtocolError extends Exception {
	public ProtocolError(Message m) {
		super("ERROR in PROTOCOL\nmessage: " + m.getType() + "src[" + m.getSrc() + "]; dest[" + m.getDest() + "]\n");
	}
}
