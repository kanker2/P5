package common;

public class ProtocolError extends Exception {
	public ProtocolError(Message m) {
		super("ERROR for " + m);
	}
}
